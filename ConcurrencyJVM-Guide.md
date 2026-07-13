# Concurrency & Multithreading in Java
> Performance Tuning & JVM Internals — Advanced Java Engineering
> Covers: Thread Fundamentals · Synchronization · Executor Framework · Concurrent Collections · JVM Memory Model · Garbage Collection · Performance Tuning · Real-World Case Studies

---

## Table of Contents
1. [Thread Fundamentals](#1-thread-fundamentals)
2. [Synchronization & Locks](#2-synchronization--locks)
3. [Executor Framework](#3-executor-framework)
4. [Concurrent Collections](#4-concurrent-collections)
5. [JVM Memory Model](#5-jvm-memory-model)
6. [Garbage Collection](#6-garbage-collection)
7. [Performance Tuning](#7-performance-tuning)
8. [Real-World Case Studies](#8-real-world-case-studies)

---

## 1. Thread Fundamentals

### What Is a Thread?
A thread is the **smallest unit of execution** within a process. Multiple threads share the same process memory (heap, method area) but each has its own stack, program counter, and local variables.

```
Process Memory Layout:
┌─────────────────────────────────────────────┐
│                  HEAP                       │
│    (shared: objects, static fields)         │
├──────────────┬──────────────┬───────────────┤
│  Thread-1    │  Thread-2    │  Thread-3     │
│  Stack       │  Stack       │  Stack        │
│  (local vars)│  (local vars)│  (local vars) │
│  PC Register │  PC Register │  PC Register  │
└──────────────┴──────────────┴───────────────┘

Thread-safe: only local variables (stack)
Not thread-safe by default: heap objects (shared)
```

### Thread Lifecycle
```
NEW → RUNNABLE → RUNNING → BLOCKED/WAITING/TIMED_WAITING → TERMINATED

NEW:           Thread object created, start() not yet called
RUNNABLE:      Ready to run, waiting for CPU (includes actually running on CPU)
BLOCKED:       Waiting to acquire a monitor lock
WAITING:       Waiting indefinitely (wait(), join(), LockSupport.park())
TIMED_WAITING: Waiting with timeout (sleep(), wait(n), join(n))
TERMINATED:    run() method has completed
```

### Creating Threads — Three Ways

```java
// Way 1: Extend Thread
public class DownloadThread extends Thread {
    private final String url;
    private final String destination;

    public DownloadThread(String url, String destination) {
        super("download-" + url.hashCode());   // Named threads are easier to debug!
        this.url = url;
        this.destination = destination;
        setDaemon(false);   // Default: user thread (JVM waits for completion)
    }

    @Override
    public void run() {
        System.out.printf("[%s] Starting download: %s%n", getName(), url);
        // ... download logic
    }
}
DownloadThread t = new DownloadThread("https://example.com/file.zip", "/tmp/");
t.start();   // Don't call run()! That executes synchronously.

// Way 2: Implement Runnable (preferred — separates task from thread mechanics)
public class DataProcessor implements Runnable {
    private final List<Integer> data;

    public DataProcessor(List<Integer> data) { this.data = data; }

    @Override
    public void run() {
        data.stream().mapToInt(Integer::intValue).sum();
    }
}
Thread t2 = new Thread(new DataProcessor(myData), "data-processor-1");
t2.start();

// Way 3: Lambda (most concise for simple tasks)
Thread t3 = new Thread(() -> {
    System.out.println("[" + Thread.currentThread().getName() + "] Running!");
}, "lambda-thread");
t3.start();

// Way 4: Callable (returns a value — use with Future)
Callable<Integer> sumTask = () -> {
    return IntStream.rangeClosed(1, 1_000_000).sum();
};
```

### Thread States in Practice

```java
public class ThreadStateDemo {
    public static void main(String[] args) throws Exception {
        Object lock = new Object();

        Thread blocker = new Thread(() -> {
            synchronized (lock) {
                try { Thread.sleep(5000); } catch (InterruptedException e) {}
            }
        }, "lock-holder");

        Thread blocked = new Thread(() -> {
            synchronized (lock) { }  // Will be BLOCKED waiting for lock
        }, "lock-waiter");

        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                try { lock.wait(); } catch (InterruptedException e) {}
            }
        }, "condition-waiter");

        blocker.start();
        Thread.sleep(100);  // Ensure blocker holds lock

        blocked.start();
        waiter.start();
        Thread.sleep(100);

        System.out.println("blocker:  " + blocker.getState());  // TIMED_WAITING
        System.out.println("blocked:  " + blocked.getState());  // BLOCKED
        System.out.println("waiter:   " + waiter.getState());   // WAITING
    }
}
```

### Thread Priority, Daemon Threads, Interruption

```java
// Thread priority (1-10, hints to scheduler — not guaranteed)
Thread highPriority = new Thread(task);
highPriority.setPriority(Thread.MAX_PRIORITY);  // 10

// Daemon threads — JVM exits when only daemon threads remain
Thread daemon = new Thread(() -> {
    while (true) { cleanupExpiredCache(); sleep(60_000); }
}, "cache-cleanup-daemon");
daemon.setDaemon(true);   // Must set BEFORE start()
daemon.start();

// Correct interruption handling
public class InterruptibleTask implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                processNextItem();
                Thread.sleep(100);   // Throws InterruptedException if interrupted
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();   // RESTORE interrupt flag!
                System.out.println("Task interrupted, cleaning up...");
                break;  // Exit cleanly
            }
        }
    }
}

// Calling code
thread.interrupt();   // Signal the thread to stop
thread.join(5000);    // Wait up to 5 seconds for it to finish
```

### Thread Local Storage

```java
// ThreadLocal: each thread has its own independent copy
public class UserContext {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<String> requestId  =
        ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    public static void setUser(User user) { currentUser.set(user); }
    public static User getUser()          { return currentUser.get(); }
    public static String getRequestId()   { return requestId.get(); }

    public static void clear() {
        currentUser.remove();   // CRITICAL: Always remove in web apps to prevent memory leaks
        requestId.remove();     // Threads are reused in pools — old values persist!
    }
}

// In servlet filter / Spring interceptor:
try {
    UserContext.setUser(authenticatedUser);
    chain.doFilter(request, response);   // Downstream code can call UserContext.getUser()
} finally {
    UserContext.clear();   // ALWAYS clean up in finally block
}
```

---

## 2. Synchronization & Locks

### The Problem: Race Conditions

```java
// BROKEN: Counter with race condition
public class UnsafeCounter {
    private int count = 0;

    public void increment() {
        count++;   // NOT atomic! Compiled to: read count, add 1, write count
                   // Two threads can interleave → lost update!
    }

    public int get() { return count; }
}

// Without sync, running 1000 increments on 2 threads:
// Expected: 2000
// Actual: 1756, 1834, 1912... (varies every run)
```

### synchronized — The Fundamental Tool

```java
// Synchronized method — locks on 'this'
public class SafeCounter {
    private int count = 0;

    public synchronized void increment() { count++; }
    public synchronized void decrement() { count--; }
    public synchronized int get()        { return count; }
}

// Synchronized block — finer-grained, lock on specific object
public class BankAccount {
    private final Object balanceLock = new Object();   // Private dedicated lock
    private double balance;

    public void deposit(double amount) {
        synchronized (balanceLock) {
            if (amount <= 0) throw new IllegalArgumentException();
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        synchronized (balanceLock) {
            if (amount > balance) return false;
            balance -= amount;
            return true;
        }
    }
}

// Locking on class for static methods
public class SharedConfig {
    private static int maxConnections = 10;

    public static synchronized int getMaxConnections() { return maxConnections; }
    public static synchronized void setMaxConnections(int n) { maxConnections = n; }
}
```

### Volatile — Visibility Without Atomicity

```java
// volatile guarantees:
//   1. VISIBILITY: writes are immediately visible to other threads
//   2. ORDERING: prevents reordering across the volatile write/read
//   NOT guarantees: atomicity (use AtomicXxx for that)

public class ConfigReloader {
    private volatile boolean running = true;    // Without volatile: threads may see stale value
    private volatile Config config;              // Safe publication of reference

    public void stop() {
        running = false;    // Visible to all threads immediately
    }

    public void run() {
        while (running) {   // Each iteration reads fresh value from main memory
            process(config);
        }
    }
}

// Classic double-checked locking — needs volatile on Java 5+
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {                // First check (no lock)
            synchronized (Singleton.class) {
                if (instance == null) {         // Second check (with lock)
                    instance = new Singleton(); // volatile ensures others see fully constructed object
                }
            }
        }
        return instance;
    }
}
```

### java.util.concurrent.locks — Advanced Locking

```java
// ReentrantLock — more flexible than synchronized
public class BoundedBuffer<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private final Object[] buffer;
    private int count, putIndex, takeIndex;

    public BoundedBuffer(int capacity) { buffer = new Object[capacity]; }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (count == buffer.length) notFull.await();   // Wait if full
            buffer[putIndex] = item;
            putIndex = (putIndex + 1) % buffer.length;
            count++;
            notEmpty.signal();   // Signal one waiter that buffer is not empty
        } finally {
            lock.unlock();   // ALWAYS in finally block!
        }
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) notEmpty.await();   // Wait if empty
            T item = (T) buffer[takeIndex];
            takeIndex = (takeIndex + 1) % buffer.length;
            count--;
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}

// ReadWriteLock — allows concurrent reads, exclusive writes
public class CachingMap<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock  = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public V get(K key) {
        readLock.lock();     // Multiple threads can read simultaneously
        try {
            return cache.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, V value) {
        writeLock.lock();    // Exclusive write: no other reads or writes
        try {
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }
}

// StampedLock — optimistic reads (Java 8+, faster than ReadWriteLock)
public class Point {
    private final StampedLock lock = new StampedLock();
    private double x, y;

    public double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead();   // Try to read without locking
        double cx = x, cy = y;
        if (!lock.validate(stamp)) {             // Check if write occurred during read
            stamp = lock.readLock();             // Fall back to read lock
            try { cx = x; cy = y; }
            finally { lock.unlockRead(stamp); }
        }
        return Math.hypot(cx, cy);
    }

    public void move(double dx, double dy) {
        long stamp = lock.writeLock();
        try { x += dx; y += dy; }
        finally { lock.unlockWrite(stamp); }
    }
}
```

### Deadlock, Livelock, Starvation

```java
// DEADLOCK: Thread A holds Lock1, waits for Lock2
//           Thread B holds Lock2, waits for Lock1
// Prevention: Always acquire locks in the same order

// DEADLOCK example (DO NOT DO THIS):
class Transfer {
    static void transferDeadlock(Account from, Account to, double amount) {
        synchronized (from) {
            synchronized (to) {   // If another thread does reverse, DEADLOCK!
                from.debit(amount);
                to.credit(amount);
            }
        }
    }
}

// FIX: Consistent lock ordering using System.identityHashCode
class SafeTransfer {
    static void transfer(Account from, Account to, double amount) {
        Account first  = System.identityHashCode(from) <= System.identityHashCode(to) ? from : to;
        Account second = first == from ? to : from;
        synchronized (first) {
            synchronized (second) {
                from.debit(amount);
                to.credit(amount);
            }
        }
    }
}
```

### Atomic Classes — Lock-Free Concurrency

```java
// AtomicInteger/Long/Reference — use CPU CAS (Compare-And-Swap) instructions
public class AtomicCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    public int increment()          { return count.incrementAndGet(); }
    public int decrement()          { return count.decrementAndGet(); }
    public int add(int delta)       { return count.addAndGet(delta); }
    public int get()                { return count.get(); }

    // CAS operation — fundamental building block
    public boolean compareAndSet(int expected, int update) {
        return count.compareAndSet(expected, update);
    }
}

// AtomicReference for complex state
public class AtomicStack<T> {
    private static class Node<T> {
        final T item; final Node<T> next;
        Node(T item, Node<T> next) { this.item = item; this.next = next; }
    }

    private final AtomicReference<Node<T>> head = new AtomicReference<>();

    public void push(T item) {
        Node<T> newHead, oldHead;
        do {
            oldHead = head.get();
            newHead = new Node<>(item, oldHead);
        } while (!head.compareAndSet(oldHead, newHead));   // Retry if another thread modified head
    }

    public T pop() {
        Node<T> oldHead, newHead;
        do {
            oldHead = head.get();
            if (oldHead == null) return null;
            newHead = oldHead.next;
        } while (!head.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }
}

// LongAdder — faster than AtomicLong under high contention
// Uses multiple cells (one per CPU core), sums them on read
public class HighThroughputCounter {
    private final LongAdder counter = new LongAdder();
    public void increment() { counter.increment(); }
    public long get()       { return counter.sum(); }
    // 10x faster than AtomicLong under high thread contention
}
```

---

## 3. Executor Framework

### Why Executors?
```
Thread creation is expensive:
  - Stack memory allocation (~512KB default)
  - OS kernel thread creation
  - JVM bookkeeping

Thread pools reuse threads:
  - Create once, use many times
  - Bounded: prevents OOM from unbounded thread creation
  - Queueing: smooths bursts of work
  - Metrics: monitor queue depth, active threads
```

### Thread Pool Types

```java
// Fixed thread pool — bounded concurrency, good for CPU-bound tasks
ExecutorService fixed = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()   // Match CPU cores
);

// Cached thread pool — grows/shrinks dynamically, good for I/O-bound tasks
// WARNING: unbounded! Can create thousands of threads under load
ExecutorService cached = Executors.newCachedThreadPool();

// Single thread executor — sequential, ordered processing
ExecutorService single = Executors.newSingleThreadExecutor();

// Scheduled executor — for periodic/delayed tasks
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
scheduler.scheduleAtFixedRate(() -> cleanupExpiredSessions(), 0, 5, TimeUnit.MINUTES);

// ALWAYS prefer custom ThreadPoolExecutor for production:
ExecutorService executor = new ThreadPoolExecutor(
    4,                              // corePoolSize: minimum threads
    16,                             // maximumPoolSize: peak threads
    60L, TimeUnit.SECONDS,          // keepAliveTime: idle threads exit after this
    new LinkedBlockingQueue<>(1000),// workQueue: buffer for pending tasks
    new ThreadFactory() {           // custom thread factory for naming + daemon
        AtomicInteger n = new AtomicInteger(0);
        @Override public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "order-processor-" + n.incrementAndGet());
            t.setDaemon(false);
            return t;
        }
    },
    new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection policy: caller runs the task
    // Alternatives: AbortPolicy (default, throws), DiscardPolicy, DiscardOldestPolicy
);
```

### Future, CompletableFuture

```java
// Future — basic async result
ExecutorService exec = Executors.newFixedThreadPool(4);
Future<Long> sumFuture = exec.submit(() -> {
    return LongStream.rangeClosed(1L, 1_000_000_000L).sum();
});

// Do other work while computation runs...
System.out.println("Computing in background...");

try {
    Long result = sumFuture.get(30, TimeUnit.SECONDS);   // Blocks until done or timeout
    System.out.println("Sum: " + result);
} catch (TimeoutException e) {
    sumFuture.cancel(true);   // Interrupt the computation
    System.out.println("Computation timed out");
}

// CompletableFuture — non-blocking, composable async pipelines
public CompletableFuture<OrderConfirmation> processOrderAsync(Order order) {
    return CompletableFuture
        // Step 1: Validate asynchronously
        .supplyAsync(() -> validateOrder(order), validationExecutor)
        // Step 2: When validation done, charge payment
        .thenComposeAsync(validOrder -> chargePaymentAsync(validOrder), paymentExecutor)
        // Step 3: When payment done, reserve inventory
        .thenComposeAsync(paymentResult -> reserveInventoryAsync(paymentResult), inventoryExecutor)
        // Step 4: Transform result
        .thenApply(inventoryResult -> buildConfirmation(order, inventoryResult))
        // Step 5: Side effect — send confirmation email
        .thenApply(confirmation -> {
            emailExecutor.submit(() -> sendConfirmationEmail(confirmation));
            return confirmation;
        })
        // Step 6: Handle errors
        .exceptionally(ex -> {
            log.error("Order processing failed for {}: {}", order.getId(), ex.getMessage());
            compensate(order);
            return OrderConfirmation.failed(order.getId(), ex.getMessage());
        });
}

// Parallel fan-out — fetch from multiple services simultaneously
public UserDashboard buildDashboard(String userId) throws Exception {
    CompletableFuture<List<Order>>     orders      = fetchOrdersAsync(userId);
    CompletableFuture<UserProfile>     profile     = fetchProfileAsync(userId);
    CompletableFuture<List<Product>>   recommended = fetchRecommendationsAsync(userId);
    CompletableFuture<WalletBalance>   wallet      = fetchWalletAsync(userId);

    // Wait for ALL to complete (fail-fast on any failure)
    CompletableFuture.allOf(orders, profile, recommended, wallet).get(5, TimeUnit.SECONDS);

    return new UserDashboard(
        profile.get(),
        orders.get(),
        recommended.get(),
        wallet.get()
    );
    // Total latency = max(individual latencies) instead of sum!
}

// anyOf — return first completed (race condition)
CompletableFuture<String> primaryDC   = fetchFromPrimaryDC(key);
CompletableFuture<String> secondaryDC = fetchFromSecondaryDC(key);
Object result = CompletableFuture.anyOf(primaryDC, secondaryDC).get();
```

### Fork/Join Framework

```java
// ForkJoinPool — designed for divide-and-conquer recursive tasks
// Uses work-stealing: idle threads steal tasks from busy threads' queues
public class ParallelMergeSort extends RecursiveAction {
    private final int[] array;
    private final int start, end;
    private static final int THRESHOLD = 1000;

    public ParallelMergeSort(int[] array, int start, int end) {
        this.array = array; this.start = start; this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start <= THRESHOLD) {
            Arrays.sort(array, start, end);   // Sequential for small arrays
            return;
        }
        int mid = (start + end) / 2;
        ParallelMergeSort left  = new ParallelMergeSort(array, start, mid);
        ParallelMergeSort right = new ParallelMergeSort(array, mid, end);
        invokeAll(left, right);   // Fork both, join both
        merge(array, start, mid, end);
    }
}

ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
pool.invoke(new ParallelMergeSort(array, 0, array.length));

// Parallel streams use the common ForkJoinPool internally
long sum = LongStream.rangeClosed(1L, 1_000_000_000L)
    .parallel()   // Splits range across CPU cores
    .sum();
```

---

## 4. Concurrent Collections

### Why Not Just synchronize Existing Collections?

```java
// BROKEN: synchronized ArrayList is not enough
List<String> list = Collections.synchronizedList(new ArrayList<>());
// Still broken! Compound operations are not atomic:
if (!list.contains(item)) {   // Check and add: another thread can insert between these two lines
    list.add(item);
}

// BROKEN: synchronized HashMap iteration
Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());
// Iteration requires manual synchronization:
synchronized (map) {
    for (Map.Entry<String, Integer> e : map.entrySet()) {
        // ...
    }
}
```

### ConcurrentHashMap — The Workhorse

```java
ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<>();

// Thread-safe atomic operations
wordCount.putIfAbsent("hello", 0);
wordCount.computeIfAbsent("world", k -> 0);
wordCount.merge("hello", 1, Integer::sum);   // Atomic increment
wordCount.compute("world", (k, v) -> v == null ? 1 : v + 1);

// Atomic check-and-act (no external synchronization needed)
wordCount.putIfAbsent("key", expensiveDefault());

// Parallel operations (Java 8+)
long totalWords = wordCount.mappingCount();
int sumOfCounts = wordCount.reduceValues(1L, Integer::sum);  // Parallel reduce

// Java 8 segment locking: 16 segments by default (vs HashMap which locks entire table)
// Java 9+: even more fine-grained locking per bucket

// Common mistake — this is NOT atomic:
if (wordCount.get("key") == null) {
    wordCount.put("key", value);  // Use putIfAbsent instead!
}
```

### CopyOnWriteArrayList and ArrayBlockingQueue

```java
// CopyOnWriteArrayList — perfect for read-heavy, write-rare scenarios
// Every write creates a COPY of the backing array
CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList<>();

// Iteration is always safe (reads snapshot, never throws ConcurrentModificationException)
for (EventListener listener : listeners) {
    listener.onEvent(event);   // Safe even if other threads add/remove
}

listeners.add(newListener);     // Creates new copy of array — O(n) write
listeners.remove(oldListener);  // Also O(n) — only use when reads >> writes

// BlockingQueue — producer-consumer with blocking
BlockingQueue<Task> taskQueue = new ArrayBlockingQueue<>(1000);

// Producer
public void submitTask(Task task) throws InterruptedException {
    taskQueue.put(task);         // Blocks if queue is full
    // Or: taskQueue.offer(task, 100, MILLISECONDS) — with timeout
}

// Consumer
public void consumeTasks() {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            Task task = taskQueue.take();   // Blocks if queue is empty
            processTask(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}

// LinkedBlockingQueue vs ArrayBlockingQueue:
// ArrayBlockingQueue: bounded (backpressure), array-backed, single lock
// LinkedBlockingQueue: optionally bounded, linked nodes, separate head/tail locks
// PriorityBlockingQueue: ordered by priority
// DelayQueue: elements available after delay (scheduled tasks)
```

### ConcurrentLinkedQueue and Skip Lists

```java
// ConcurrentLinkedQueue — non-blocking (lock-free) queue
// Uses CAS operations — no locks, very high throughput
ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
queue.offer("item");            // Never blocks
String head = queue.poll();     // Returns null if empty (never blocks)
int size = queue.size();        // O(n) — avoid calling frequently

// ConcurrentSkipListMap — sorted concurrent map
// Alternative to TreeMap for concurrent access
// O(log n) get, put, remove — all concurrent
ConcurrentSkipListMap<String, Integer> sortedMap = new ConcurrentSkipListMap<>();
sortedMap.put("banana", 2);
sortedMap.put("apple", 1);
sortedMap.put("cherry", 3);
// Always sorted: apple=1, banana=2, cherry=3
String firstKey = sortedMap.firstKey();    // "apple"
Map<String, Integer> subset = sortedMap.subMap("apple", "cherry");
```

### CountDownLatch, CyclicBarrier, Semaphore, Phaser

```java
// CountDownLatch — wait for N events (one-time use)
CountDownLatch servicesReady = new CountDownLatch(3);

// Three services start up in parallel, each counts down when ready
executor.submit(() -> { startUserService();    servicesReady.countDown(); });
executor.submit(() -> { startOrderService();   servicesReady.countDown(); });
executor.submit(() -> { startPaymentService(); servicesReady.countDown(); });

servicesReady.await(30, TimeUnit.SECONDS);   // Main thread waits for all 3
System.out.println("All services ready, opening traffic gates");

// CyclicBarrier — all threads wait at a point, then all proceed (reusable!)
CyclicBarrier barrier = new CyclicBarrier(4, () ->
    System.out.println("All workers synchronized at checkpoint!"));

for (int i = 0; i < 4; i++) {
    executor.submit(() -> {
        processChunk();
        barrier.await();   // Wait for all 4 workers
        mergeResults();    // All proceed together
        barrier.await();   // Sync again before next round
    });
}

// Semaphore — control access to limited resources
Semaphore dbConnections = new Semaphore(10);   // Max 10 concurrent DB connections

try {
    dbConnections.acquire();    // Block until permit available
    try {
        executeQuery();
    } finally {
        dbConnections.release(); // ALWAYS release in finally!
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}

// Phaser — flexible generalization of CountDownLatch + CyclicBarrier
Phaser phaser = new Phaser(1);  // 1 = main thread
for (int i = 0; i < 5; i++) {
    phaser.register();           // Register each worker
    final int workerId = i;
    executor.submit(() -> {
        System.out.println("Worker " + workerId + " processing phase " + phaser.getPhase());
        phaser.arriveAndAwaitAdvance();  // Wait for all workers
        // Automatically advances to next phase when all arrive
    });
}
phaser.arriveAndDeregister();   // Main thread deregisters
```

---

## 5. JVM Memory Model

### JVM Memory Layout
```
┌────────────────────────────────────────────────────────────────┐
│                         HEAP                                   │
│  ┌──────────────────────────────┐  ┌─────────────────────────┐│
│  │    Young Generation          │  │     Old Generation      ││
│  │  ┌────────┐ ┌─────┐ ┌─────┐│  │  (Tenured Space)        ││
│  │  │  Eden  │ │ S0  │ │ S1  ││  │  Long-lived objects      ││
│  │  │ (new   │ │(from│ │(to) ││  │  Promoted from young gen ││
│  │  │ objects│ │)    │ │     ││  │                          ││
│  │  └────────┘ └─────┘ └─────┘│  └─────────────────────────┘│
│  └──────────────────────────────┘                             │
├────────────────────────────────────────────────────────────────┤
│                    NON-HEAP / METASPACE                        │
│  Class metadata, method bytecodes, constant pool              │
│  (Replaced PermGen in Java 8 — uses native memory)            │
├────────────────────────────────────────────────────────────────┤
│                     PER-THREAD AREAS                           │
│  Stack (stack frames, local vars, operand stack) × N threads  │
│  PC Register × N threads                                      │
│  Native Method Stack × N threads                              │
└────────────────────────────────────────────────────────────────┘
```

### Java Memory Model (JMM) — Happens-Before

```
The JMM defines when writes by one thread are visible to reads by another.

Happens-Before Relationships:
  1. Program order:     Within a thread, each statement HB the next
  2. Monitor lock:      unlock() HB any subsequent lock() on same monitor
  3. volatile write:    volatile write HB any subsequent volatile read of same var
  4. Thread start:      thread.start() HB first action in that thread
  5. Thread join:       Last action in thread HB thread.join() returning
  6. Transitivity:      A HB B and B HB C → A HB C

Without HB relationship:
  - Compiler may reorder instructions
  - CPU may reorder memory accesses
  - Values may be cached in registers or CPU caches (not flushed to main memory)
```

```java
// Without synchronization — data race!
class Holder {
    private int n;
    public Holder(int n) { this.n = n; }
    public void assertSanity() {
        if (n != n) throw new AssertionError("This is impossible!");
        // But it CAN happen without proper publication!
    }
}

// Safe publication idioms:
// 1. Via static initializer
public static final Holder holder = new Holder(42);  // Safe

// 2. Via volatile
private volatile Holder holder;
holder = new Holder(42);  // Volatile write ensures visibility

// 3. Via final field (most important!)
public class SafePoint {
    public final int x, y;   // final fields safely published after construction
    public SafePoint(int x, int y) { this.x = x; this.y = y; }
}

// 4. Via thread-safe container
private final AtomicReference<Holder> holderRef = new AtomicReference<>();
holderRef.set(new Holder(42));  // CAS ensures visibility
```

### Object References and Escape Analysis

```java
// Stack allocation (JVM optimization): short-lived objects may be allocated on stack
// Escape analysis: JVM determines if object can "escape" to heap
// If not: allocate on stack, eliminate synchronization overhead

public long computeSum(int n) {
    Point p = new Point(0, 0);  // May be stack-allocated if it doesn't escape!
    for (int i = 0; i < n; i++) {
        p.x += i;
    }
    return p.x;   // p never escapes this method
}
```

---

## 6. Garbage Collection

### GC Fundamentals

```
GC determines which objects are live (reachable from GC roots) and collects the rest.

GC Roots:
  - Local variables in active stack frames
  - Static variables
  - Active threads
  - JNI references

Reachability:
  Strong reference:  obj = new Object()         → never collected while referenced
  Soft reference:    new SoftReference<>(obj)   → collected only under memory pressure
  Weak reference:    new WeakReference<>(obj)   → collected at next GC
  Phantom reference: new PhantomReference<>(obj)→ collected, but notified via queue

GC Algorithms:
  Mark: Start from GC roots, mark all reachable objects
  Sweep: Collect all unmarked objects
  Compact: Move live objects together to eliminate fragmentation
```

### GC Collectors

```
Serial GC (-XX:+UseSerialGC):
  Single-threaded, stop-the-world
  Good for: Single-core, small heaps, simple apps

Parallel GC (-XX:+UseParallelGC):
  Multi-threaded young gen, single-thread old gen (Java 8 default)
  Good for: Throughput-focused batch processing

G1 GC (-XX:+UseG1GC):
  Heap divided into equal-sized regions (1-32MB each)
  Concurrent marking, predictable pause times
  Good for: Large heaps (>4GB), latency-sensitive apps (Java 9+ default)
  Target: -XX:MaxGCPauseMillis=200 (soft goal)

ZGC (-XX:+UseZGC):
  Concurrent, region-based, colored pointers
  Pause times < 10ms even on TB heaps
  Good for: Latency-critical, very large heaps (Java 15+ production-ready)

Shenandoah GC:
  Concurrent compaction (vs ZGC's relocation)
  Similar goals to ZGC, different algorithm
```

### Object Lifecycle and GC Tuning

```
Young GC (Minor GC):
  1. New objects allocated in Eden
  2. Eden fills up → Minor GC triggered
  3. Live objects in Eden + S0 → copy to S1, age++
  4. Objects with age >= tenuring threshold → promoted to Old Gen
  5. Eden + S0 cleared

Old GC (Major/Full GC):
  1. Old Gen fills up → Major GC
  2. Usually stop-the-world (except G1/ZGC concurrent phases)
  3. Much longer pause — avoid if possible!

Key tuning parameters:
  -Xms4g -Xmx4g          # Initial and max heap (set equal for predictability)
  -XX:NewRatio=3          # Old:Young ratio (3:1 = 75% old, 25% young)
  -XX:SurvivorRatio=8     # Eden:Survivor ratio (8:1:1 within young gen)
  -XX:MaxTenuringThreshold=15  # Age before promotion to old gen
  -XX:+UseG1GC            # Use G1 garbage collector
  -XX:MaxGCPauseMillis=200  # G1 soft pause target
  -XX:G1HeapRegionSize=16m  # G1 region size
```

```java
// Reference types for memory-sensitive caching
public class MemorySensitiveCache<K, V> {
    // SoftReference: JVM will collect when memory is low
    private final Map<K, SoftReference<V>> cache = new HashMap<>();

    public V get(K key) {
        SoftReference<V> ref = cache.get(key);
        if (ref == null) return null;
        V value = ref.get();   // null if GC collected it
        if (value == null) cache.remove(key);  // Clean up stale entry
        return value;
    }

    public void put(K key, V value) {
        cache.put(key, new SoftReference<>(value));
    }
}

// WeakHashMap — entries collected when key has no strong reference
WeakHashMap<Object, String> weakMap = new WeakHashMap<>();
Object key = new Object();
weakMap.put(key, "value");
key = null;     // Key has no more strong references
System.gc();    // After GC, entry may be removed from weakMap automatically
// Used for: canonicalization maps, caches keyed by domain objects

// PhantomReference — post-mortem cleanup
ReferenceQueue<Resource> queue = new ReferenceQueue<>();
PhantomReference<Resource> phantom = new PhantomReference<>(resource, queue);
resource = null;   // Allow GC
// After GC, phantom reference is enqueued in 'queue'
// Can poll queue to do cleanup (e.g., release native resources)
// This is what java.lang.ref.Cleaner uses (Java 9+)
```

---

## 7. Performance Tuning

### JVM Startup and JIT Compilation

```
JVM Execution Tiers:
  Tier 0: Interpreter (immediate, no compilation, slow)
  Tier 1: C1 Simple (quick compile, basic optimizations)
  Tier 2: C1 Limited Profiling (gather profiling data)
  Tier 3: C1 Full Profiling (more detailed profiling)
  Tier 4: C2 Full Optimization (aggressive JIT, uses profile data)

Key JIT optimizations:
  Inlining:           Small methods inlined at call site (eliminates call overhead)
  Escape Analysis:    Stack-allocate non-escaping objects
  Loop Unrolling:     Unroll loops to reduce branch overhead
  Dead Code Elim:     Remove unreachable/unused code
  Constant Folding:   Compute constant expressions at compile time
  Devirtualization:   Direct call instead of virtual dispatch when type known

JVM flags for JIT insight:
  -XX:+PrintCompilation           # Print methods being JIT-compiled
  -XX:+UnlockDiagnosticVMOptions
  -XX:+PrintInlining              # Show inlining decisions
  -XX:CompileThreshold=10000      # Calls before JIT compilation (default)
```

### Profiling and Benchmarking

```java
// NEVER use System.currentTimeMillis() for microbenchmarks!
// Use JMH (Java Microbenchmark Harness) instead

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
@State(Scope.Benchmark)
public class StringBenchmark {

    @Param({"10", "100", "1000"})
    public int size;

    private List<String> words;

    @Setup
    public void setup() {
        words = IntStream.range(0, size)
            .mapToObj(i -> "word" + i)
            .collect(Collectors.toList());
    }

    @Benchmark
    public String concatenation() {
        String result = "";
        for (String w : words) result += w;   // Creates new String each iteration!
        return result;
    }

    @Benchmark
    public String stringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (String w : words) sb.append(w);
        return sb.toString();
    }

    @Benchmark
    public String streamJoin() {
        return String.join("", words);
    }
}
// Run: java -jar benchmarks.jar StringBenchmark
```

### Memory Optimization Techniques

```java
// 1. Object pooling — reuse expensive objects
public class ByteBufferPool {
    private final int bufferSize;
    private final BlockingQueue<ByteBuffer> pool;

    public ByteBufferPool(int bufferSize, int poolSize) {
        this.bufferSize = bufferSize;
        this.pool = new LinkedBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.offer(ByteBuffer.allocateDirect(bufferSize));  // Off-heap: not GC'd!
        }
    }

    public ByteBuffer acquire() throws InterruptedException {
        return pool.take();
    }

    public void release(ByteBuffer buffer) {
        buffer.clear();   // Reset position/limit before reuse
        pool.offer(buffer);
    }
}

// 2. Primitive arrays instead of boxed collections (75% less memory!)
// int[] uses 4 bytes per element
// Integer[] uses 4 bytes reference + 16 bytes object header + 4 bytes value = 24 bytes

// 3. Off-heap memory for large data structures
ByteBuffer offHeap = ByteBuffer.allocateDirect(1024 * 1024 * 1024);  // 1GB off-heap
// Not GC'd, no GC pressure, direct memory access
// Used by: Netty, Chronicle Map, Ignite

// 4. Flyweight pattern — share immutable objects
public class ColorRegistry {
    private static final Map<String, Color> COLORS = new ConcurrentHashMap<>();

    public static Color of(String hex) {
        return COLORS.computeIfAbsent(hex, Color::fromHex);  // Share color instances
    }
}

// 5. String interning — share identical string literals
String s1 = new String("hello");
String s2 = new String("hello");
s1 == s2        // false — different objects
s1.intern() == s2.intern()  // true — same interned object
// But: intern() can cause memory leaks in PermGen/Metaspace if used on dynamic strings
```

### Identifying and Fixing Bottlenecks

```java
// 1. CPU profiling — find hot methods
// Tools: JProfiler, YourKit, async-profiler (low overhead), JFR (built-in Java 11+)

// 2. Memory profiling — find memory leaks
// Symptoms: heap grows, GC more frequent, eventually OOM
// Tools: Eclipse MAT, JProfiler, heap dumps (jmap -dump:live,format=b,file=heap.hprof PID)

// Common memory leaks:
// - Static collections that grow unbounded
// - ThreadLocal values not removed in web/pool contexts
// - Inner class holding implicit reference to outer class
// - Listeners/callbacks not deregistered
// - Resource streams not closed

// 3. Lock contention — find synchronization bottlenecks
// Tools: jstack (thread dump), async-profiler with --lock mode
// Fix: Use ConcurrentHashMap instead of synchronized HashMap
//      Use ReadWriteLock when reads >> writes
//      Use lock striping
//      Reduce lock scope (hold for shortest possible time)

// 4. I/O bottlenecks
// Use NIO (non-blocking I/O) for high-concurrency servers
// Use connection pooling
// Batch database operations

// Lock striping example — reduce contention
public class StripedCounter {
    private final long[] counts;
    private final int stripes;

    public StripedCounter(int stripes) {
        this.stripes = stripes;
        this.counts = new long[stripes];
    }

    public void increment() {
        // Different threads go to different stripes (based on thread ID)
        int stripe = (int)(Thread.currentThread().getId() % stripes);
        synchronized (this) { counts[stripe]++; }
        // Better: use AtomicLongArray, or simply use LongAdder!
    }

    public long sum() {
        long total = 0;
        for (long count : counts) total += count;
        return total;
    }
}
```

### JVM Flags for Production

```bash
# Production JVM configuration example (for 8GB heap)
java \
  # Heap sizing — set equal to minimize GC overhead from resizing
  -Xms8g -Xmx8g \
  # Use G1 GC — good balance of throughput and latency
  -XX:+UseG1GC \
  # Soft pause target (G1 will try to stay under this)
  -XX:MaxGCPauseMillis=200 \
  # GC logging (Java 11+ syntax)
  -Xlog:gc*:gc.log:time,level,tags:filecount=10,filesize=50m \
  # OOM heap dump for post-mortem analysis
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/app/heap-dump.hprof \
  # Exit JVM on OOM instead of struggling
  -XX:+ExitOnOutOfMemoryError \
  # Print command line flags on startup
  -XX:+PrintCommandLineFlags \
  # Disable slow reflection access warnings
  --add-opens java.base/java.util=ALL-UNNAMED \
  # Application
  -jar application.jar
```

---

## 8. Real-World Case Studies

### Case 1: High-Throughput Order Processing System

```
Problem: Process 50,000 orders/minute with < 100ms p99 latency
         Single-threaded: 500ms per order → system falls behind

Solution Architecture:
  1. ThreadPoolExecutor with bounded queue (backpressure)
  2. CompletableFuture pipeline (validation → payment → inventory)
  3. ConcurrentHashMap for in-flight order deduplication
  4. LongAdder metrics (zero contention)
  5. Semaphore to limit concurrent payment API calls

Result: 50K orders/minute, p99 = 87ms
```

### Case 2: Cache with Background Refresh

```
Pattern: Serve stale while refreshing (prevents thundering herd)
  1. On cache hit: return value, check if expired
  2. If near expiry: one thread triggers async refresh, others get stale value
  3. On cache miss: one thread fetches, others wait (ConcurrentHashMap.computeIfAbsent)
  4. Never serve expired data to all threads at once

Key: computeIfAbsent is atomic — prevents multiple threads fetching same key
```

### Case 3: Reactive Event Processing Pipeline

```
Requirements: Process 1M events/second from IoT sensors
  Each event: validate → enrich → transform → persist → notify

Bottleneck: Enrichment requires DB lookup (10ms each)
Fix: Async pipeline with proper thread pool sizing:
  - Validation: CPU-bound → ForkJoinPool (matches CPU cores)
  - Enrichment: I/O-bound → cached/large pool (10x CPU cores)
  - Transform:  CPU-bound → ForkJoinPool
  - Persist:    I/O-bound → dedicated DB thread pool
  - Notify:     Fire-and-forget → single-threaded notification queue
```

---

## Summary

### Threading Checklist

```
Visibility:
  □ Is shared mutable state protected by synchronization?
  □ Are volatile fields used for simple flags/references?
  □ Are final fields used where possible (safe publication)?

Atomicity:
  □ Are compound operations (check-then-act, read-modify-write) atomic?
  □ Using AtomicXxx for simple numeric counters?
  □ Using ConcurrentHashMap.compute() for atomic map updates?

Ordering:
  □ Is lock acquisition order consistent? (prevent deadlock)
  □ Lock scope minimized? (hold as briefly as possible)
  □ Are thread pools properly sized (CPU-bound vs I/O-bound)?

Resources:
  □ ExecutorService shut down in try-finally?
  □ ThreadLocal.remove() called in finally in pooled environments?
  □ Blocking operations wrapped with timeout?
  □ CompletableFuture.exceptionally() or handle() for error handling?
```

### GC + Performance Checklist

```
Memory:
  □ No unbounded static collections?
  □ Streams/connections closed in try-with-resources?
  □ ThreadLocal values cleaned up after request?
  □ Heap dump enabled for OOM: -XX:+HeapDumpOnOutOfMemoryError?

GC:
  □ Appropriate GC chosen for workload?
  □ -Xms == -Xmx to avoid heap resizing?
  □ GC logging enabled for analysis?
  □ Young gen sized appropriately (not too small → too many promotions)?

Performance:
  □ String concatenation in loops replaced with StringBuilder?
  □ Autoboxing in hot paths eliminated?
  □ Using primitive arrays where possible?
  □ Profiling done with real workload before optimizing?
  □ "Measure, don't guess" — profile before optimizing!
```

### The Core Insight

```
Concurrency is not about making things faster.
It is about making things CORRECT first, then fast.

"Write code for correctness first.
 Then profile to find actual bottlenecks.
 Then optimize those specific bottlenecks.
 Repeat."

Most important lessons:
  1. Shared mutable state is the root of all threading evil
  2. Prefer immutability — it's trivially thread-safe
  3. Prefer higher-level abstractions (CompletableFuture, BlockingQueue)
     over raw locks and synchronized
  4. Profile before optimizing — intuition is often wrong
  5. Test concurrency with stress tests (not unit tests)
     under realistic concurrency levels
```
