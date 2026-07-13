import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.stream.*;
import java.lang.ref.*;

/**
 * ============================================================
 * CONCURRENCY & MULTITHREADING + JVM INTERNALS — Complete Demo
 * ============================================================
 * Topics:
 *  1. Thread Fundamentals          (create, lifecycle, states, priority,
 *                                   daemon, interruption, ThreadLocal,
 *                                   thread groups, UncaughtExceptionHandler)
 *  2. Synchronization & Locks      (race condition demo, synchronized,
 *                                   volatile, ReentrantLock, ReadWriteLock,
 *                                   StampedLock, AtomicXxx, LongAdder,
 *                                   deadlock demo and prevention)
 *  3. Executor Framework           (all pool types, custom ThreadPoolExecutor,
 *                                   Future/CompletableFuture pipelines,
 *                                   allOf/anyOf, ForkJoin, parallel streams,
 *                                   rejection policies)
 *  4. Concurrent Collections       (ConcurrentHashMap atomic ops, COWL,
 *                                   BlockingQueue producer-consumer,
 *                                   ConcurrentSkipListMap, CountDownLatch,
 *                                   CyclicBarrier, Semaphore, Phaser)
 *  5. JVM Memory Model             (visibility demo, volatile correction,
 *                                   safe publication, happens-before,
 *                                   memory layout simulation)
 *  6. Garbage Collection           (reference types: soft/weak/phantom,
 *                                   GC simulation, WeakHashMap demo,
 *                                   memory-sensitive cache, object pooling)
 *  7. Performance Tuning           (string concat benchmark, lock contention,
 *                                   lock striping, LongAdder vs AtomicLong,
 *                                   object pool, false sharing detection,
 *                                   escape analysis hints)
 *  8. Real-World Case Studies      (order processing pipeline, thundering herd
 *                                   prevention, event processing, rate limiter,
 *                                   reactive cache with async refresh)
 *
 * Compile : javac ConcurrencyJVM.java
 * Run     : java ConcurrencyJVM
 * ============================================================
 */
public class ConcurrencyJVM {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) throws Exception {
        printBanner("CONCURRENCY & JVM INTERNALS — COMPLETE DEMO");

        section1_ThreadFundamentals();
        section2_SynchronizationAndLocks();
        section3_ExecutorFramework();
        section4_ConcurrentCollections();
        section5_JVMMemoryModel();
        section6_GarbageCollection();
        section7_PerformanceTuning();
        section8_RealWorldCaseStudies();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — THREAD FUNDAMENTALS
    // =========================================================
    static void section1_ThreadFundamentals() throws Exception {
        printSection("1. THREAD FUNDAMENTALS");

        // 1a. Thread creation — 3 ways
        System.out.println("--- 1a. Three Ways to Create Threads ---");
        Thread t1 = new Thread(new RunnableTask("Task-A"), "extend-thread");
        Thread t2 = new Thread(() -> System.out.println("  [Lambda] Running on: " + Thread.currentThread().getName()), "lambda-thread");
        CallableTask callable = new CallableTask(42);
        t1.start(); t2.start(); t1.join(); t2.join();

        // 1b. Thread states
        System.out.println("\n--- 1b. Thread State Lifecycle ---");
        Thread newThread = new Thread(() -> {});
        System.out.println("  Before start(): " + newThread.getState());  // NEW
        newThread.start();
        System.out.println("  After start():  " + newThread.getState());  // RUNNABLE or TERMINATED
        newThread.join();
        System.out.println("  After join():   " + newThread.getState());  // TERMINATED

        Object lock = new Object();
        Thread holder = new Thread(() -> { synchronized(lock) { try { Thread.sleep(500); } catch(Exception e) {} } }, "lock-holder");
        Thread waiter = new Thread(() -> { synchronized(lock) { } }, "lock-waiter");
        holder.start(); Thread.sleep(50);
        waiter.start(); Thread.sleep(50);
        System.out.println("  Holder state:  " + holder.getState()); // TIMED_WAITING
        System.out.println("  Waiter state:  " + waiter.getState()); // BLOCKED
        holder.join(); waiter.join();

        // 1c. Daemon threads
        System.out.println("\n--- 1c. Daemon Threads ---");
        Thread daemon = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("  [Daemon] heartbeat " + (i+1));
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }, "heartbeat-daemon");
        daemon.setDaemon(true);
        System.out.println("  Is daemon: " + daemon.isDaemon());
        daemon.start();

        // 1d. Thread interruption (correct pattern)
        System.out.println("\n--- 1d. Correct Interruption Pattern ---");
        Thread interruptible = new Thread(() -> {
            int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                count++;
                if (count % 100_000 == 0) System.out.println("  [Interruptible] iteration " + count/100_000);
                try {
                    if (count > 200_000) Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore flag!
                    System.out.println("  [Interruptible] Interrupted cleanly at count=" + count);
                    break;
                }
            }
        }, "interruptible-worker");
        interruptible.start();
        Thread.sleep(100);
        interruptible.interrupt();
        interruptible.join(1000);

        // 1e. ThreadLocal
        System.out.println("\n--- 1e. ThreadLocal — Per-Thread Context ---");
        ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> "REQ-" + Thread.currentThread().getId());
        CountDownLatch latch = new CountDownLatch(3);
        for (int i = 1; i <= 3; i++) {
            int id = i;
            new Thread(() -> {
                System.out.printf("  Thread-%d sees requestId: %s%n", id, requestId.get());
                requestId.remove(); // Always clean up!
                latch.countDown();
            }, "context-thread-" + i).start();
        }
        latch.await();

        // 1f. UncaughtExceptionHandler
        System.out.println("\n--- 1f. Uncaught Exception Handler ---");
        Thread faultyThread = new Thread(() -> {
            throw new RuntimeException("Simulated thread crash!");
        }, "faulty-thread");
        faultyThread.setUncaughtExceptionHandler((t, e) ->
            System.out.println("  [UEH] Caught in thread '" + t.getName() + "': " + e.getMessage()));
        faultyThread.start();
        faultyThread.join();
    }

    static class RunnableTask implements Runnable {
        String name; RunnableTask(String n){name=n;}
        @Override public void run() { System.out.println("  [Runnable] " + name + " on: " + Thread.currentThread().getName()); }
    }
    static class CallableTask implements Callable<Integer> {
        int value; CallableTask(int v){value=v;}
        @Override public Integer call() { return value * 2; }
    }

    // =========================================================
    // SECTION 2 — SYNCHRONIZATION & LOCKS
    // =========================================================
    static void section2_SynchronizationAndLocks() throws Exception {
        printSection("2. SYNCHRONIZATION & LOCKS");

        // 2a. Race condition demo
        System.out.println("--- 2a. Race Condition vs Thread-Safe Counter ---");
        UnsafeCounter unsafe = new UnsafeCounter();
        SafeCounter   safe   = new SafeCounter();
        AtomicCounter atomic = new AtomicCounter();
        int THREADS = 10, OPS = 1000;

        ExecutorService exec = Executors.newFixedThreadPool(THREADS);
        CountDownLatch unsafeLatch = new CountDownLatch(THREADS);
        CountDownLatch safeLatch   = new CountDownLatch(THREADS);
        CountDownLatch atomicLatch = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            exec.submit(() -> { for(int j=0;j<OPS;j++) unsafe.increment(); unsafeLatch.countDown(); });
            exec.submit(() -> { for(int j=0;j<OPS;j++) safe.increment();   safeLatch.countDown(); });
            exec.submit(() -> { for(int j=0;j<OPS;j++) atomic.increment(); atomicLatch.countDown(); });
        }
        unsafeLatch.await(); safeLatch.await(); atomicLatch.await();
        int expected = THREADS * OPS;
        System.out.printf("  Expected:  %,d%n", expected);
        System.out.printf("  Unsafe:    %,d %s (race condition!)%n", unsafe.get(), unsafe.get()==expected?"✓":"✗ WRONG");
        System.out.printf("  Synchronized: %,d %s%n", safe.get(),   safe.get()==expected?"✓":"✗");
        System.out.printf("  Atomic:    %,d %s%n", atomic.get(), atomic.get()==expected?"✓":"✗");
        exec.shutdown(); exec.awaitTermination(5, TimeUnit.SECONDS);

        // 2b. Volatile — visibility
        System.out.println("\n--- 2b. Volatile for Visibility ---");
        VolatileFlag flag = new VolatileFlag();
        Thread worker = new Thread(() -> {
            System.out.println("  [Worker] Waiting for signal...");
            while (!flag.isReady()) {} // Sees updated value thanks to volatile
            System.out.println("  [Worker] Got signal, proceeding!");
        });
        worker.start();
        Thread.sleep(100);
        flag.setReady(true);
        System.out.println("  [Main] Signal sent");
        worker.join(1000);

        // 2c. ReentrantLock with Conditions
        System.out.println("\n--- 2c. ReentrantLock + Condition (Bounded Buffer) ---");
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(3);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        pool.submit(() -> { for(int i=1;i<=5;i++) { try{buffer.put(i);System.out.println("  [Producer] Put: "+i);}catch(InterruptedException e){Thread.currentThread().interrupt();} } });
        pool.submit(() -> { for(int i=0;i<5;i++) { try{Thread.sleep(50);int v=buffer.take();System.out.println("  [Consumer] Got: "+v);}catch(InterruptedException e){Thread.currentThread().interrupt();} } });
        pool.shutdown(); pool.awaitTermination(5, TimeUnit.SECONDS);

        // 2d. ReadWriteLock
        System.out.println("\n--- 2d. ReadWriteLock — Concurrent Reads ---");
        CachingReadWriteMap<String,String> rwMap = new CachingReadWriteMap<>();
        rwMap.put("key1","value1"); rwMap.put("key2","value2");
        ExecutorService rwPool = Executors.newFixedThreadPool(6);
        CountDownLatch rwLatch = new CountDownLatch(6);
        for(int i=0;i<5;i++){final int id=i;rwPool.submit(()->{System.out.println("  [Reader-"+id+"] "+rwMap.get("key1"));rwLatch.countDown();});}
        rwPool.submit(()->{rwMap.put("key1","updatedValue");System.out.println("  [Writer] Updated key1");rwLatch.countDown();});
        rwLatch.await(); rwPool.shutdown();

        // 2e. StampedLock (optimistic reads)
        System.out.println("\n--- 2e. StampedLock (Optimistic Read) ---");
        StampedPoint point = new StampedPoint(3.0, 4.0);
        System.out.println("  Distance from origin: " + String.format("%.2f", point.distanceFromOrigin()));
        point.move(1.0, 0.0);
        System.out.println("  After move(1,0): " + String.format("%.2f", point.distanceFromOrigin()));

        // 2f. Deadlock prevention
        System.out.println("\n--- 2f. Deadlock Prevention (Ordered Lock Acquisition) ---");
        BankAccountLock accA = new BankAccountLock("ACC-A", 1000);
        BankAccountLock accB = new BankAccountLock("ACC-B", 500);
        ExecutorService dlPool = Executors.newFixedThreadPool(2);
        CountDownLatch dlLatch = new CountDownLatch(2);
        dlPool.submit(()->{SafeTransfer.transfer(accA,accB,200);System.out.println("  Transfer A→B(200): A="+accA.getBalance()+" B="+accB.getBalance());dlLatch.countDown();});
        dlPool.submit(()->{SafeTransfer.transfer(accB,accA,100);System.out.println("  Transfer B→A(100): A="+accA.getBalance()+" B="+accB.getBalance());dlLatch.countDown();});
        dlLatch.await(); dlPool.shutdown();

        // 2g. LongAdder vs AtomicLong
        System.out.println("\n--- 2g. LongAdder vs AtomicLong Throughput ---");
        compareCounters(8, 1_000_000);
    }

    static class UnsafeCounter { int count=0; void increment(){count++;} int get(){return count;} }
    static class SafeCounter { int count=0; synchronized void increment(){count++;} synchronized int get(){return count;} }
    static class AtomicCounter { AtomicInteger count=new AtomicInteger(); void increment(){count.incrementAndGet();} int get(){return count.get();} }
    static class VolatileFlag { private volatile boolean ready=false; boolean isReady(){return ready;} void setReady(boolean r){ready=r;} }
    static class BoundedBuffer<T> {
        ReentrantLock lock=new ReentrantLock(); Condition notFull,notEmpty;
        Object[] buf; int count,putIdx,takeIdx;
        BoundedBuffer(int cap){buf=new Object[cap];notFull=lock.newCondition();notEmpty=lock.newCondition();}
        void put(T item) throws InterruptedException {lock.lock();try{while(count==buf.length) notFull.await();buf[putIdx]=item;putIdx=(putIdx+1)%buf.length;count++;notEmpty.signal();}finally{lock.unlock();}}
        @SuppressWarnings("unchecked") T take() throws InterruptedException {lock.lock();try{while(count==0) notEmpty.await();T item=(T)buf[takeIdx];takeIdx=(takeIdx+1)%buf.length;count--;notFull.signal();return item;}finally{lock.unlock();}}
    }
    static class CachingReadWriteMap<K,V> {
        Map<K,V> map=new HashMap<>(); ReadWriteLock rwl=new ReentrantReadWriteLock();
        V get(K k){rwl.readLock().lock();try{return map.get(k);}finally{rwl.readLock().unlock();}}
        void put(K k,V v){rwl.writeLock().lock();try{map.put(k,v);}finally{rwl.writeLock().unlock();}}
    }
    static class StampedPoint {
        StampedLock sl=new StampedLock(); double x,y;
        StampedPoint(double x,double y){this.x=x;this.y=y;}
        double distanceFromOrigin(){long stamp=sl.tryOptimisticRead();double cx=x,cy=y;if(!sl.validate(stamp)){stamp=sl.readLock();try{cx=x;cy=y;}finally{sl.unlockRead(stamp);}}return Math.hypot(cx,cy);}
        void move(double dx,double dy){long stamp=sl.writeLock();try{x+=dx;y+=dy;}finally{sl.unlockWrite(stamp);}}
    }
    static class BankAccountLock {
        String id; int balance;
        BankAccountLock(String i,int b){id=i;balance=b;}
        synchronized void debit(int a){balance-=a;} synchronized void credit(int a){balance+=a;}
        synchronized int getBalance(){return balance;}
    }
    static class SafeTransfer {
        static void transfer(BankAccountLock from,BankAccountLock to,int amount){
            BankAccountLock first=System.identityHashCode(from)<=System.identityHashCode(to)?from:to;
            BankAccountLock second=first==from?to:from;
            synchronized(first){synchronized(second){from.debit(amount);to.credit(amount);}}
        }
    }
    static void compareCounters(int threads, int ops) throws Exception {
        AtomicLong atomicLong = new AtomicLong();
        LongAdder  longAdder  = new LongAdder();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch al = new CountDownLatch(threads), la = new CountDownLatch(threads);
        long t0 = System.nanoTime();
        for(int i=0;i<threads;i++) pool.submit(()->{for(int j=0;j<ops/threads;j++) atomicLong.incrementAndGet();al.countDown();});
        al.await(); long atomicTime = System.nanoTime()-t0;
        t0 = System.nanoTime();
        for(int i=0;i<threads;i++) pool.submit(()->{for(int j=0;j<ops/threads;j++) longAdder.increment();la.countDown();});
        la.await(); long adderTime = System.nanoTime()-t0;
        pool.shutdown();
        System.out.printf("  AtomicLong (%d threads, %,d ops): %,d ms%n",threads,ops,atomicTime/1_000_000);
        System.out.printf("  LongAdder  (%d threads, %,d ops): %,d ms  (%.1fx faster)%n",threads,ops,adderTime/1_000_000,(double)atomicTime/Math.max(adderTime,1));
    }

    // =========================================================
    // SECTION 3 — EXECUTOR FRAMEWORK
    // =========================================================
    static void section3_ExecutorFramework() throws Exception {
        printSection("3. EXECUTOR FRAMEWORK");

        // 3a. Thread pool types
        System.out.println("--- 3a. Thread Pool Types ---");
        ExecutorService fixed  = Executors.newFixedThreadPool(4);
        ExecutorService single = Executors.newSingleThreadExecutor();

        CountDownLatch fixedLatch = new CountDownLatch(4);
        for(int i=1;i<=4;i++){final int id=i;fixed.submit(()->{System.out.println("  [Fixed-Pool] Task-"+id+" on "+Thread.currentThread().getName());fixedLatch.countDown();});}
        fixedLatch.await();

        CountDownLatch singleLatch = new CountDownLatch(3);
        for(int i=1;i<=3;i++){final int id=i;single.submit(()->{System.out.println("  [Single-Pool] Task-"+id+" ordered on "+Thread.currentThread().getName());singleLatch.countDown();});}
        singleLatch.await();
        fixed.shutdown(); single.shutdown();

        // 3b. Custom ThreadPoolExecutor with rejection policy
        System.out.println("\n--- 3b. Custom ThreadPoolExecutor ---");
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(3);
        ExecutorService custom = new ThreadPoolExecutor(2, 4, 30L, TimeUnit.SECONDS, queue,
            r -> { Thread t=new Thread(r,"custom-worker-"+System.nanoTime()%1000);t.setDaemon(false);return t; },
            (r,e)->System.out.println("  [REJECTED] Task rejected — queue full!"));
        CountDownLatch customLatch = new CountDownLatch(5);
        for(int i=1;i<=8;i++){final int id=i;try{custom.submit(()->{System.out.println("  [Custom] Processing task-"+id);try{Thread.sleep(50);}catch(Exception ex){}customLatch.countDown();});}catch(Exception e){System.out.println("  [Custom] Could not submit task-"+id);}}
        customLatch.await(3, TimeUnit.SECONDS); custom.shutdown();

        // 3c. CompletableFuture pipeline
        System.out.println("\n--- 3c. CompletableFuture Pipeline ---");
        ExecutorService pipeline = Executors.newFixedThreadPool(4);
        long t0 = System.nanoTime();
        CompletableFuture<String> result = CompletableFuture
            .supplyAsync(() -> { simulate(20,"Validate order"); return "ORD-001"; }, pipeline)
            .thenApplyAsync(orderId -> { simulate(30,"Charge payment"); return orderId+":PAID"; }, pipeline)
            .thenApplyAsync(paid -> { simulate(15,"Reserve inventory"); return paid+":RESERVED"; }, pipeline)
            .thenApply(reserved -> "CONFIRMED: "+reserved)
            .exceptionally(ex -> "FAILED: "+ex.getMessage());
        System.out.println("  Pipeline result: " + result.get(5, TimeUnit.SECONDS));
        System.out.printf("  Pipeline time: %dms%n", (System.nanoTime()-t0)/1_000_000);

        // 3d. allOf fan-out (parallel service calls)
        System.out.println("\n--- 3d. CompletableFuture.allOf (Parallel Fan-Out) ---");
        t0 = System.nanoTime();
        CompletableFuture<String> profile  = CompletableFuture.supplyAsync(()->{simulate(50,"Fetch profile"); return "Alice";}, pipeline);
        CompletableFuture<Integer> orders  = CompletableFuture.supplyAsync(()->{simulate(80,"Fetch orders"); return 12;}, pipeline);
        CompletableFuture<Double>  wallet  = CompletableFuture.supplyAsync(()->{simulate(60,"Fetch wallet"); return 250.50;}, pipeline);
        CompletableFuture.allOf(profile,orders,wallet).get(5, TimeUnit.SECONDS);
        System.out.printf("  Dashboard built in %dms (max of 50,80,60 = 80ms in theory)%n",(System.nanoTime()-t0)/1_000_000);
        System.out.printf("  Profile:%s Orders:%d Wallet:$%.2f%n", profile.get(), orders.get(), wallet.get());
        pipeline.shutdown();

        // 3e. ScheduledExecutorService
        System.out.println("\n--- 3e. Scheduled Tasks ---");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        AtomicInteger ticks = new AtomicInteger(0);
        ScheduledFuture<?> periodic = scheduler.scheduleAtFixedRate(()->{
            int t=ticks.incrementAndGet();
            System.out.println("  [Scheduler] tick #"+t+" at "+System.currentTimeMillis()%10000);
        }, 0, 100, TimeUnit.MILLISECONDS);
        Thread.sleep(350);
        periodic.cancel(false);
        scheduler.shutdown();

        // 3f. ForkJoin
        System.out.println("\n--- 3f. ForkJoin: Parallel Array Sum ---");
        long[] data = LongStream.rangeClosed(1, 1_000_000).toArray();
        ForkJoinPool fjPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        t0 = System.nanoTime();
        long seqSum = 0; for(long v:data) seqSum+=v;
        long seqTime = System.nanoTime()-t0;
        t0 = System.nanoTime();
        long parSum = fjPool.invoke(new ParallelSum(data, 0, data.length));
        long parTime = System.nanoTime()-t0;
        System.out.printf("  Sequential: sum=%,d time=%,dns%n", seqSum, seqTime);
        System.out.printf("  Parallel:   sum=%,d time=%,dns  speedup=%.2fx%n", parSum, parTime, (double)seqTime/Math.max(parTime,1));
        fjPool.shutdown();
    }

    static void simulate(int ms, String task) { try{Thread.sleep(ms);}catch(Exception e){} System.out.println("  ["+Thread.currentThread().getName()+"] "+task+" done ("+ms+"ms)"); }

    static class ParallelSum extends RecursiveTask<Long> {
        long[] arr; int start,end; static final int THRESHOLD=50_000;
        ParallelSum(long[] a,int s,int e){arr=a;start=s;end=e;}
        @Override protected Long compute(){
            if(end-start<=THRESHOLD){long sum=0;for(int i=start;i<end;i++) sum+=arr[i];return sum;}
            int mid=(start+end)/2;
            ParallelSum left=new ParallelSum(arr,start,mid);
            ParallelSum right=new ParallelSum(arr,mid,end);
            left.fork();return right.compute()+left.join();}
    }

    // =========================================================
    // SECTION 4 — CONCURRENT COLLECTIONS
    // =========================================================
    static void section4_ConcurrentCollections() throws Exception {
        printSection("4. CONCURRENT COLLECTIONS");

        // 4a. ConcurrentHashMap atomic ops
        System.out.println("--- 4a. ConcurrentHashMap Atomic Operations ---");
        ConcurrentHashMap<String,Integer> wc = new ConcurrentHashMap<>();
        String[] words = "the quick brown fox jumps over the lazy dog the fox".split(" ");
        Arrays.stream(words).parallel().forEach(w -> wc.merge(w, 1, Integer::sum));
        System.out.println("  Word counts: " + new TreeMap<>(wc));
        System.out.println("  computeIfAbsent (expensive default): " + wc.computeIfAbsent("cat", k -> expensiveDefault()));
        System.out.println("  putIfAbsent 'dog': " + wc.putIfAbsent("dog", 99) + " (existing value preserved)");

        // 4b. CopyOnWriteArrayList
        System.out.println("\n--- 4b. CopyOnWriteArrayList (Safe Iteration) ---");
        CopyOnWriteArrayList<String> listeners = new CopyOnWriteArrayList<>();
        listeners.add("ListenerA"); listeners.add("ListenerB"); listeners.add("ListenerC");
        ExecutorService exec = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);
        exec.submit(()->{for(String l:listeners){System.out.println("  [Reader] "+l);try{Thread.sleep(10);}catch(Exception e){}}latch.countDown();});
        exec.submit(()->{Thread t=Thread.currentThread();try{Thread.sleep(5);}catch(Exception e){}listeners.add("ListenerD");System.out.println("  [Writer] Added ListenerD safely");latch.countDown();});
        exec.submit(()->{try{Thread.sleep(15);}catch(Exception e){}listeners.remove("ListenerA");System.out.println("  [Writer] Removed ListenerA");latch.countDown();});
        latch.await(); exec.shutdown();
        System.out.println("  Final listeners: " + listeners);

        // 4c. BlockingQueue producer-consumer
        System.out.println("\n--- 4c. BlockingQueue Producer-Consumer ---");
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(5);
        AtomicInteger produced = new AtomicInteger(), consumed = new AtomicInteger();
        CountDownLatch prodLatch = new CountDownLatch(10);
        ExecutorService pcPool = Executors.newFixedThreadPool(4);
        // 2 producers
        for(int p=1;p<=2;p++){final int pid=p;pcPool.submit(()->{for(int i=0;i<5;i++){String item="P"+pid+"-item"+i;try{queue.put(item);System.out.println("  [Producer-"+pid+"] Produced: "+item);produced.incrementAndGet();}catch(InterruptedException e){Thread.currentThread().interrupt();}}});}
        // 2 consumers
        for(int c=1;c<=2;c++){final int cid=c;pcPool.submit(()->{for(int i=0;i<5;i++){try{String item=queue.poll(2,TimeUnit.SECONDS);if(item!=null){System.out.println("  [Consumer-"+cid+"] Consumed: "+item);consumed.incrementAndGet();prodLatch.countDown();}}catch(InterruptedException e){Thread.currentThread().interrupt();}}});}
        prodLatch.await(5, TimeUnit.SECONDS);
        System.out.printf("  Produced: %d  Consumed: %d%n", produced.get(), consumed.get());
        pcPool.shutdown();

        // 4d. CountDownLatch — startup gate
        System.out.println("\n--- 4d. CountDownLatch — Service Startup Gate ---");
        CountDownLatch startGate = new CountDownLatch(3);
        ExecutorService startPool = Executors.newFixedThreadPool(3);
        String[] services = {"UserService","OrderService","PaymentService"};
        for(String svc:services){startPool.submit(()->{try{Thread.sleep(new Random().nextInt(200));}catch(Exception e){}System.out.println("  ["+svc+"] Ready ✓");startGate.countDown();});}
        System.out.println("  Waiting for all services...");
        startGate.await(5, TimeUnit.SECONDS);
        System.out.println("  All services ready — opening traffic gates!");
        startPool.shutdown();

        // 4e. CyclicBarrier — phases
        System.out.println("\n--- 4e. CyclicBarrier — Phased Computation ---");
        int WORKERS = 3;
        CyclicBarrier barrier = new CyclicBarrier(WORKERS, () -> System.out.println("  [Barrier] All workers synced, advancing phase!"));
        ExecutorService barrierPool = Executors.newFixedThreadPool(WORKERS);
        for(int w=1;w<=WORKERS;w++){final int wid=w;barrierPool.submit(()->{
            for(int phase=1;phase<=2;phase++){
                System.out.println("  [Worker-"+wid+"] Phase "+phase+" computing...");
                try{Thread.sleep(50*wid);barrier.await();}catch(Exception e){Thread.currentThread().interrupt();}
            }System.out.println("  [Worker-"+wid+"] Done all phases!");});}
        barrierPool.shutdown(); barrierPool.awaitTermination(5,TimeUnit.SECONDS);

        // 4f. Semaphore — resource limiting
        System.out.println("\n--- 4f. Semaphore — Limited Resource Access ---");
        Semaphore dbPool = new Semaphore(3); // Max 3 concurrent DB connections
        CountDownLatch semLatch = new CountDownLatch(7);
        ExecutorService semPool = Executors.newFixedThreadPool(7);
        for(int i=1;i<=7;i++){final int id=i;semPool.submit(()->{
            try{boolean acquired=dbPool.tryAcquire(500,TimeUnit.MILLISECONDS);
                if(acquired){System.out.println("  [Thread-"+id+"] Got DB connection (available: "+dbPool.availablePermits()+")");Thread.sleep(100);dbPool.release();System.out.println("  [Thread-"+id+"] Released connection");}
                else System.out.println("  [Thread-"+id+"] TIMEOUT — no connection available");}
            catch(Exception e){Thread.currentThread().interrupt();}finally{semLatch.countDown();}});}
        semLatch.await(); semPool.shutdown();
    }

    static int expensiveDefault(){try{Thread.sleep(10);}catch(Exception e){}return 0;}

    // =========================================================
    // SECTION 5 — JVM MEMORY MODEL
    // =========================================================
    static void section5_JVMMemoryModel() throws Exception {
        printSection("5. JVM MEMORY MODEL");

        // 5a. Visibility problem demo (volatile fix)
        System.out.println("--- 5a. Visibility: volatile vs non-volatile ---");
        System.out.println("  Non-volatile flag might cause infinite loop (depends on JIT):");
        NonVolatileStop nonVolatile = new NonVolatileStop();
        Thread nv = new Thread(nonVolatile, "non-volatile-thread");
        nv.start(); Thread.sleep(100); nonVolatile.stop();
        nv.join(500);
        System.out.println("  Non-volatile thread stopped: " + nonVolatile.count);

        System.out.println("  Volatile flag — guaranteed visibility:");
        VolatileStop vs = new VolatileStop();
        Thread vt = new Thread(vs, "volatile-thread");
        vt.start(); Thread.sleep(100); vs.stop();
        vt.join(500);
        System.out.println("  Volatile thread stopped: " + vs.count);

        // 5b. Happens-Before via synchronized
        System.out.println("\n--- 5b. Happens-Before via synchronized ---");
        SharedData shared = new SharedData();
        Thread writer = new Thread(() -> { shared.write(42, "hello"); System.out.println("  [Writer] Written: 42, hello"); });
        Thread reader = new Thread(() -> { try{Thread.sleep(100);}catch(Exception e){} System.out.println("  [Reader] Read: " + shared.readInt() + ", " + shared.readStr()); });
        writer.start(); writer.join();
        reader.start(); reader.join();

        // 5c. Safe publication patterns
        System.out.println("\n--- 5c. Safe Object Publication ---");
        System.out.println("  1. Via final field (most reliable):");
        FinalHolder fh = new FinalHolder(100);
        System.out.println("    FinalHolder.value = " + fh.value + " (safely published)");
        System.out.println("  2. Via volatile reference:");
        VolatilePublisher pub = new VolatilePublisher();
        pub.publish(new int[]{1,2,3,4,5});
        System.out.println("    Published array sum = " + Arrays.stream(pub.getData()).sum());
        System.out.println("  3. Via AtomicReference:");
        AtomicReference<String[]> atomicRef = new AtomicReference<>();
        atomicRef.set(new String[]{"a","b","c"});
        System.out.println("    AtomicRef[0] = " + atomicRef.get()[0]);

        // 5d. Memory layout simulation
        System.out.println("\n--- 5d. Memory Consumption Estimation ---");
        System.out.println("  Object header:     16 bytes (mark word 8 + klass pointer 8)");
        System.out.println("  int field:          4 bytes");
        System.out.println("  long field:         8 bytes");
        System.out.println("  reference field:    4 bytes (compressed oops on heap <= 32GB)");
        System.out.println("  boolean field:      1 byte (but padded to 8-byte boundary)");
        System.out.println();
        Runtime rt = Runtime.getRuntime(); rt.gc();
        long before = rt.totalMemory() - rt.freeMemory();
        List<byte[]> arrays = new ArrayList<>();
        for(int i=0;i<100;i++) arrays.add(new byte[1024*1024]); // 100MB
        long after = rt.totalMemory() - rt.freeMemory();
        System.out.printf("  Allocated ~100 x 1MB arrays, heap grew by: %dMB%n", (after-before)/1_000_000);
        arrays.clear(); rt.gc();
    }

    static class NonVolatileStop implements Runnable {
        boolean stop=false; long count=0;
        void stop(){stop=true;}
        @Override public void run(){while(!stop){count++;}System.out.println("  [NonVolatile] stopped after "+count+" iterations");}
    }
    static class VolatileStop implements Runnable {
        volatile boolean stop=false; long count=0;
        void stop(){stop=true;}
        @Override public void run(){while(!stop){count++;}System.out.println("  [Volatile] stopped after "+count+" iterations");}
    }
    static class SharedData {
        private int intVal; private String strVal;
        synchronized void write(int i,String s){intVal=i;strVal=s;}
        synchronized int readInt(){return intVal;}
        synchronized String readStr(){return strVal;}
    }
    static final class FinalHolder {final int value; FinalHolder(int v){value=v;}}
    static class VolatilePublisher {private volatile int[] data;void publish(int[] d){data=d;}int[] getData(){return data;}}

    // =========================================================
    // SECTION 6 — GARBAGE COLLECTION
    // =========================================================
    static void section6_GarbageCollection() throws Exception {
        printSection("6. GARBAGE COLLECTION");

        // 6a. Reference types
        System.out.println("--- 6a. Reference Types (Strong, Soft, Weak, Phantom) ---");
        // Strong reference
        Object strong = new Object();
        System.out.println("  Strong reference: " + (strong != null ? "alive" : "gone"));

        // Weak reference
        Object weakObj = new Object();
        WeakReference<Object> weak = new WeakReference<>(weakObj);
        System.out.println("  Weak before nulling: " + (weak.get() != null ? "alive" : "gone"));
        weakObj = null;
        System.gc(); Thread.sleep(100);
        System.out.println("  Weak after GC:       " + (weak.get() != null ? "alive" : "gone (collected)"));

        // Soft reference
        Object softObj = new byte[1024]; // Small, so probably won't be collected unless OOM
        SoftReference<Object> soft = new SoftReference<>(softObj);
        softObj = null;
        System.out.println("  Soft after nulling (still alive under normal memory): " + (soft.get() != null ? "alive" : "gone"));

        // 6b. WeakHashMap — auto-expiring map
        System.out.println("\n--- 6b. WeakHashMap (Auto-Expiring Entries) ---");
        WeakHashMap<Object, String> weakMap = new WeakHashMap<>();
        Object k1 = new Object(); Object k2 = new Object();
        weakMap.put(k1, "value-1"); weakMap.put(k2, "value-2");
        System.out.println("  WeakMap size before null: " + weakMap.size());
        k1 = null; // Remove strong reference to k1
        System.gc(); Thread.sleep(200);
        System.out.println("  WeakMap size after GC (k1 key collected): " + weakMap.size());

        // 6c. Memory-sensitive cache with SoftReference
        System.out.println("\n--- 6c. SoftReference Cache ---");
        SoftRefCache<String, byte[]> cache = new SoftRefCache<>();
        cache.put("large-data", new byte[1024 * 100]); // 100KB
        cache.put("thumbnail",  new byte[1024 * 5]);   // 5KB
        System.out.println("  Cache hit 'large-data': " + (cache.get("large-data") != null ? "hit ✓" : "miss (GC'd)"));
        System.out.println("  Cache hit 'thumbnail':  " + (cache.get("thumbnail")  != null ? "hit ✓" : "miss (GC'd)"));
        System.out.println("  Cache miss 'unknown':   " + (cache.get("unknown")    != null ? "hit" : "miss ✓"));

        // 6d. Object pooling to reduce GC pressure
        System.out.println("\n--- 6d. Object Pool (Reduce GC Pressure) ---");
        SimpleObjectPool<StringBuilder> sbPool = new SimpleObjectPool<>(10, () -> new StringBuilder(), sb -> sb.setLength(0));
        long t0 = System.nanoTime();
        for(int i=0;i<1000;i++){
            StringBuilder sb = sbPool.borrow();
            sb.append("Hello ").append(i);
            String result = sb.toString();
            sbPool.returnToPool(sb);
        }
        System.out.printf("  Pooled StringBuilder: %,dns for 1000 ops%n", System.nanoTime()-t0);
        t0 = System.nanoTime();
        for(int i=0;i<1000;i++){
            StringBuilder sb = new StringBuilder();
            sb.append("Hello ").append(i);
            String result = sb.toString();
            // sb becomes garbage here
        }
        System.out.printf("  New StringBuilder:    %,dns for 1000 ops%n", System.nanoTime()-t0);

        // 6e. GC stats
        System.out.println("\n--- 6e. GC Statistics ---");
        java.lang.management.MemoryMXBean memBean = java.lang.management.ManagementFactory.getMemoryMXBean();
        System.out.printf("  Heap used:      %,d KB%n", memBean.getHeapMemoryUsage().getUsed()/1024);
        System.out.printf("  Heap committed: %,d KB%n", memBean.getHeapMemoryUsage().getCommitted()/1024);
        System.out.printf("  Heap max:       %,d KB%n", memBean.getHeapMemoryUsage().getMax()/1024);
        java.lang.management.ManagementFactory.getGarbageCollectorMXBeans().forEach(gc ->
            System.out.printf("  GC [%s]: count=%d time=%dms%n", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime()));
    }

    static class SoftRefCache<K,V> {
        Map<K,SoftReference<V>> cache=new HashMap<>();
        void put(K k,V v){cache.put(k,new SoftReference<>(v));}
        V get(K k){SoftReference<V> ref=cache.get(k);if(ref==null) return null;V v=ref.get();if(v==null) cache.remove(k);return v;}
    }
    static class SimpleObjectPool<T> {
        BlockingQueue<T> pool=new LinkedBlockingQueue<>(); Supplier<T> factory; Consumer<T> reset;
        SimpleObjectPool(int size,Supplier<T> factory,Consumer<T> reset){this.factory=factory;this.reset=reset;for(int i=0;i<size;i++) pool.offer(factory.get());}
        T borrow(){T obj=pool.poll();return obj!=null?obj:factory.get();}
        void returnToPool(T obj){reset.accept(obj);pool.offer(obj);}
    }

    // =========================================================
    // SECTION 7 — PERFORMANCE TUNING
    // =========================================================
    static void section7_PerformanceTuning() throws Exception {
        printSection("7. PERFORMANCE TUNING");

        // 7a. String concatenation benchmark
        System.out.println("--- 7a. String Concatenation Performance ---");
        int SIZE = 1000;
        String[] words = IntStream.range(0,SIZE).mapToObj(i->"word"+i).toArray(String[]::new);
        long t0;

        t0 = System.nanoTime();
        String concat = "";
        for(String w:words) concat+=w;
        System.out.printf("  %-20s: %,dns (result len=%d)%n","+=concat",System.nanoTime()-t0,concat.length());

        t0 = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for(String w:words) sb.append(w);
        String sbResult = sb.toString();
        System.out.printf("  %-20s: %,dns (result len=%d)%n","StringBuilder",System.nanoTime()-t0,sbResult.length());

        t0 = System.nanoTime();
        String joined = String.join("", words);
        System.out.printf("  %-20s: %,dns (result len=%d)%n","String.join()",System.nanoTime()-t0,joined.length());

        t0 = System.nanoTime();
        String streamed = Arrays.stream(words).collect(Collectors.joining());
        System.out.printf("  %-20s: %,dns (result len=%d)%n","Collectors.joining()",System.nanoTime()-t0,streamed.length());

        // 7b. Lock contention measurement
        System.out.println("\n--- 7b. Lock Contention: synchronized vs ReentrantLock vs LongAdder ---");
        int THREADS = 8, OPS = 500_000;
        benchmarkCounter("synchronized", THREADS, OPS, new SyncCounter());
        benchmarkCounter("ReentrantLock", THREADS, OPS, new RLCounter());
        benchmarkCounter("AtomicInteger", THREADS, OPS, new AtomicIntCounter());
        benchmarkCounter("LongAdder    ", THREADS, OPS, new LongAdderCounter());

        // 7c. Autoboxing impact
        System.out.println("\n--- 7c. Autoboxing Performance Impact ---");
        t0 = System.nanoTime();
        List<Integer> boxed = new ArrayList<>();
        for(int i=0;i<100_000;i++) boxed.add(i); // Autoboxing int → Integer each time
        long sum1 = 0; for(int v:boxed) sum1+=v;
        System.out.printf("  Boxed   (ArrayList<Integer>): %,dns  sum=%,d%n",System.nanoTime()-t0,sum1);
        t0 = System.nanoTime();
        int[] prim = new int[100_000];
        for(int i=0;i<100_000;i++) prim[i]=i;
        long sum2 = 0; for(int v:prim) sum2+=v;
        System.out.printf("  Primitive (int[]):            %,dns  sum=%,d%n",System.nanoTime()-t0,sum2);

        // 7d. False sharing (cache line contention)
        System.out.println("\n--- 7d. False Sharing Demo ---");
        FalseSharingDemo.run();

        // 7e. Thread pool sizing guidance
        System.out.println("\n--- 7e. Thread Pool Sizing Guidance ---");
        int cpus = Runtime.getRuntime().availableProcessors();
        System.out.printf("  Available CPUs: %d%n", cpus);
        System.out.printf("  CPU-bound tasks:    pool size = %d (= CPU count)%n", cpus);
        System.out.printf("  I/O-bound tasks:    pool size = %d (= CPU * (1 + wait/compute))%n", cpus * 10);
        System.out.printf("  Mixed tasks:        pool size = %d to %d%n", cpus*2, cpus*5);
        System.out.println("  Rule of thumb: measure under realistic load, not theoretical!");
    }

    interface Counter { void increment(); long get(); }
    static class SyncCounter implements Counter { long c=0; public synchronized void increment(){c++;} public synchronized long get(){return c;} }
    static class RLCounter implements Counter { long c=0; ReentrantLock l=new ReentrantLock(); public void increment(){l.lock();try{c++;}finally{l.unlock();}} public long get(){return c;} }
    static class AtomicIntCounter implements Counter { AtomicLong c=new AtomicLong(); public void increment(){c.incrementAndGet();} public long get(){return c.get();} }
    static class LongAdderCounter implements Counter { LongAdder a=new LongAdder(); public void increment(){a.increment();} public long get(){return a.sum();} }
    static void benchmarkCounter(String name, int threads, int ops, Counter counter) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        long t0 = System.nanoTime();
        for(int i=0;i<threads;i++) pool.submit(()->{for(int j=0;j<ops/threads;j++) counter.increment();latch.countDown();});
        latch.await(); long elapsed = System.nanoTime()-t0;
        System.out.printf("  %-15s %d threads %,d ops: %,dms result=%,d%n",name,threads,ops,elapsed/1_000_000,counter.get());
        pool.shutdown();
    }
    static class FalseSharingDemo {
        // Padded to avoid false sharing
        static final long[] padded = new long[128]; // Two counters far apart in memory
        static final long[] contiguous = new long[2]; // Two counters adjacent (false sharing)
        static void run() throws Exception {
            int THREADS=2, OPS=50_000_000;
            // Contiguous — likely false sharing
            long t0=System.nanoTime();
            ExecutorService p=Executors.newFixedThreadPool(2);
            CountDownLatch l=new CountDownLatch(2);
            p.submit(()->{for(int i=0;i<OPS;i++) contiguous[0]++;l.countDown();});
            p.submit(()->{for(int i=0;i<OPS;i++) contiguous[1]++;l.countDown();});
            l.await(); long contiguousTime=System.nanoTime()-t0;
            // Padded — no false sharing
            t0=System.nanoTime();
            CountDownLatch l2=new CountDownLatch(2);
            p.submit(()->{for(int i=0;i<OPS;i++) padded[0]++;l2.countDown();});
            p.submit(()->{for(int i=0;i<OPS;i++) padded[64]++;l2.countDown();});
            l2.await(); long paddedTime=System.nanoTime()-t0;
            p.shutdown();
            System.out.printf("  Contiguous counters (false sharing): %,dms%n",contiguousTime/1_000_000);
            System.out.printf("  Padded counters (no false sharing):  %,dms%n",paddedTime/1_000_000);
            System.out.println("  (Padded should be faster due to no cache line invalidation)");
        }
    }

    // =========================================================
    // SECTION 8 — REAL-WORLD CASE STUDIES
    // =========================================================
    static void section8_RealWorldCaseStudies() throws Exception {
        printSection("8. REAL-WORLD CASE STUDIES");

        // 8a. High-throughput order processor
        System.out.println("--- 8a. High-Throughput Order Processor ---");
        OrderProcessor processor = new OrderProcessor(4, 1000);
        long t0 = System.nanoTime();
        CountDownLatch orderLatch = new CountDownLatch(100);
        for(int i=1;i<=100;i++){final int id=i;processor.submit(new OrderTask("ORD-"+id,id*1.5),()->orderLatch.countDown());}
        orderLatch.await(10, TimeUnit.SECONDS);
        System.out.printf("  Processed 100 orders in %dms%n",(System.nanoTime()-t0)/1_000_000);
        System.out.printf("  Throughput: %.0f orders/sec%n", 100.0/((System.nanoTime()-t0)/1_000_000_000.0));
        System.out.printf("  Total revenue: $%.2f%n", processor.getTotalRevenue());
        processor.shutdown();

        // 8b. Thundering herd prevention (cache stampede)
        System.out.println("\n--- 8b. Thundering Herd Prevention ---");
        AntiStampedeCache cache = new AntiStampedeCache();
        ExecutorService thunderPool = Executors.newFixedThreadPool(20);
        AtomicInteger dbCalls = new AtomicInteger();
        CountDownLatch thunderLatch = new CountDownLatch(20);
        // Simulate 20 threads all trying to fetch same key simultaneously
        for(int i=0;i<20;i++) thunderPool.submit(()->{
            String val = cache.get("product:123", ()->{ dbCalls.incrementAndGet(); return "Product{name=Laptop,price=999}"; });
            thunderLatch.countDown();});
        thunderLatch.await();
        System.out.println("  20 concurrent requests for same key → DB calls made: " + dbCalls.get() + " (should be 1)");
        thunderPool.shutdown();

        // 8c. Rate limiter (sliding window with concurrent safety)
        System.out.println("\n--- 8c. Concurrent Rate Limiter ---");
        ConcurrentRateLimiter rl = new ConcurrentRateLimiter(10, 1000); // 10 per second
        ExecutorService rlPool = Executors.newFixedThreadPool(20);
        AtomicInteger allowed = new AtomicInteger(), blocked = new AtomicInteger();
        CountDownLatch rlLatch = new CountDownLatch(20);
        for(int i=0;i<20;i++) rlPool.submit(()->{
            if(rl.tryAcquire("user:alice")) allowed.incrementAndGet();
            else blocked.incrementAndGet();
            rlLatch.countDown();});
        rlLatch.await();
        System.out.printf("  20 requests (limit=10/sec): allowed=%d blocked=%d%n", allowed.get(), blocked.get());
        rlPool.shutdown();

        // 8d. Async cache with background refresh
        System.out.println("\n--- 8d. Async Cache with Background Refresh ---");
        AsyncRefreshCache<String,String> asyncCache = new AsyncRefreshCache<>(2, TimeUnit.SECONDS);
        asyncCache.put("config", "v1");
        System.out.println("  Initial value: " + asyncCache.get("config", k -> "v2-from-db"));
        Thread.sleep(2100); // Wait for TTL to expire
        System.out.println("  After TTL expiry (serves stale, refreshes async): " + asyncCache.get("config", k -> "v2-refreshed"));
        Thread.sleep(200);
        System.out.println("  After async refresh: " + asyncCache.get("config", k -> "v3"));

        // 8e. Event processing pipeline
        System.out.println("\n--- 8e. Event Processing Pipeline (IoT Sensor) ---");
        EventPipeline pipeline = new EventPipeline(4);
        for(int i=1;i<=10;i++) pipeline.process(new SensorEvent("SENSOR-"+i,i*10.5));
        pipeline.shutdown();
        System.out.printf("  Processed: %d events  Avg latency: %,dns%n",
                pipeline.getProcessed(), pipeline.getAvgLatency());
    }

    // --- Case Study Classes ---
    static class OrderTask { String orderId; double amount; OrderTask(String id,double a){orderId=id;amount=a;} }
    static class OrderProcessor {
        ExecutorService pool; AtomicLong revenue=new AtomicLong(); LongAdder processed=new LongAdder();
        OrderProcessor(int threads,int queueSize){pool=new ThreadPoolExecutor(threads,threads,60,TimeUnit.SECONDS,new LinkedBlockingQueue<>(queueSize),r->{Thread t=new Thread(r,"order-worker-"+System.nanoTime()%1000);return t;},new ThreadPoolExecutor.CallerRunsPolicy());}
        void submit(OrderTask task, Runnable callback){pool.submit(()->{
            long t0=System.nanoTime(); validate(task); charge(task); notifyKafka(task);
            revenue.addAndGet((long)(task.amount*100));processed.increment();callback.run();});}
        void validate(OrderTask t){} void charge(OrderTask t){} void notifyKafka(OrderTask t){}
        double getTotalRevenue(){return revenue.get()/100.0;}
        void shutdown(){pool.shutdown();try{pool.awaitTermination(10,TimeUnit.SECONDS);}catch(Exception e){}}
    }
    static class AntiStampedeCache {
        ConcurrentHashMap<String,String> cache=new ConcurrentHashMap<>();
        String get(String key,Supplier<String> loader){
            return cache.computeIfAbsent(key,k->{System.out.println("  [Cache] MISS — Loading from DB: "+k);return loader.get();});
        }
    }
    static class ConcurrentRateLimiter {
        int limit; long windowMs; ConcurrentHashMap<String,Deque<Long>> windows=new ConcurrentHashMap<>();
        ConcurrentRateLimiter(int l,long w){limit=l;windowMs=w;}
        synchronized boolean tryAcquire(String key){
            long now=System.currentTimeMillis();
            Deque<Long> ts=windows.computeIfAbsent(key,k->new ArrayDeque<>());
            while(!ts.isEmpty()&&now-ts.peek()>=windowMs) ts.poll();
            if(ts.size()<limit){ts.offer(now);return true;}return false;}
    }
    static class AsyncRefreshCache<K,V> {
        ConcurrentHashMap<K,V> cache=new ConcurrentHashMap<>(); ConcurrentHashMap<K,Long> times=new ConcurrentHashMap<>();
        long ttlMs; ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor(r->{Thread t=new Thread(r,"cache-refresher");t.setDaemon(true);return t;});
        AsyncRefreshCache(long ttl,TimeUnit unit){ttlMs=unit.toMillis(ttl);}
        void put(K k,V v){cache.put(k,v);times.put(k,System.currentTimeMillis());}
        V get(K k,Function<K,V> loader){
            V current=cache.get(k);
            Long lastLoad=times.get(k);
            if(current!=null&&lastLoad!=null&&System.currentTimeMillis()-lastLoad<ttlMs) return current;
            if(current!=null){// Stale but has value — serve stale, refresh async
                scheduler.submit(()->{V fresh=loader.apply(k);cache.put(k,fresh);times.put(k,System.currentTimeMillis());System.out.println("  [AsyncCache] Background refreshed: "+k+"="+fresh);});
                return current;}
            // No value — load synchronously
            V fresh=loader.apply(k);cache.put(k,fresh);times.put(k,System.currentTimeMillis());return fresh;}
    }
    static class SensorEvent { String sensorId; double value; long ts=System.nanoTime(); SensorEvent(String id,double v){sensorId=id;value=v;} }
    static class EventPipeline {
        ExecutorService pool; AtomicLong totalLatency=new AtomicLong(); LongAdder count=new LongAdder();
        EventPipeline(int threads){pool=Executors.newFixedThreadPool(threads);}
        void process(SensorEvent e){pool.submit(()->{
            long start=System.nanoTime();
            validate(e); enrich(e); persist(e);
            totalLatency.addAndGet(System.nanoTime()-start); count.increment();});}
        void validate(SensorEvent e){} void enrich(SensorEvent e){} void persist(SensorEvent e){}
        long getProcessed(){return count.sum();} long getAvgLatency(){return count.sum()>0?totalLatency.get()/count.sum():0;}
        void shutdown(){pool.shutdown();try{pool.awaitTermination(5,TimeUnit.SECONDS);}catch(Exception ex){}}
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    static void printBanner(String t){System.out.println("\n"+"=".repeat(70)+"\n  "+t+"\n"+"=".repeat(70));}
    static void printSection(String t){System.out.println("\n"+"-".repeat(70)+"\n  SECTION "+t+"\n"+"-".repeat(70));}
}
