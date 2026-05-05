# Queues — Complete Deep Dive in Java

---

## How to Run

```bash
javac Queues.java
java Queues
```

> Requires Java 8 or higher. Check with `java -version`.

---

## File Structure

```
Queues.java
│
├── ArrayQueue                   → Queue backed by fixed int[]
│   ├── enqueue()                → O(1)
│   ├── dequeue()                → O(1)
│   └── peek()                   → O(1)
│
├── LinkedQueue                  → Queue backed by linked nodes
│   ├── enqueue()                → O(1)  — add to tail
│   └── dequeue()                → O(1)  — remove from head
│
├── CircularQueue                → Ring buffer with modulo wrap-around
│   ├── enqueue()                → O(1)
│   └── dequeue()                → O(1)
│
├── bfs()                        → Standard BFS traversal
├── bfsLevels()                  → BFS level-by-level
├── slidingWindowMax()           → Deque-based O(n) solution
│
├── QueueUsingTwoStacks          → Interview: Queue from 2 Stacks
├── StackUsingTwoQueues          → Interview: Stack from 2 Queues
├── firstNonRepeating()          → Interview: Stream problem
├── rottenOranges()              → Interview: Multi-source BFS
├── generateBinaryNumbers()      → Interview: BFS tree pattern
├── taskScheduler()              → Interview: PriorityQueue
│
└── main()                       → Runs all 7 topics
```

---

## Topic 1 — Queue Fundamentals (FIFO)

### What is a Queue?

A **Queue** is a linear data structure that follows **FIFO — First In, First Out**.

The perfect real-world analogy: a **ticket counter line**:
- People join at the **back** → `enqueue / offer`
- People are served from the **front** → `dequeue / poll`
- The first person to join is the first person served

```
enqueue(10): REAR → [ 10 ] ← FRONT
enqueue(20): REAR → [ 10 | 20 ] ← FRONT
enqueue(30): REAR → [ 10 | 20 | 30 ] ← FRONT
dequeue()  : returns 10 → REAR → [ 20 | 30 ] ← FRONT
dequeue()  : returns 20 → REAR → [ 30 ] ← FRONT
peek()     : returns 30 (not removed)
```

### Core Operations

| Operation | Java Method | Description | Complexity |
|-----------|-------------|-------------|------------|
| Enqueue | `offer(x)` | Add to rear | **O(1)** |
| Dequeue | `poll()` | Remove from front | **O(1)** |
| Peek | `peek()` | View front, no remove | **O(1)** |
| Is Empty | `isEmpty()` | Check if empty | **O(1)** |
| Size | `size()` | Number of elements | **O(1)** |

### `offer` vs `add`, `poll` vs `remove`, `peek` vs `element`

Java provides two versions of each operation:

| Null-safe (preferred) | Exception-throwing | When full/empty |
|----------------------|-------------------|-----------------|
| `offer(x)` | `add(x)` | `offer` returns false; `add` throws |
| `poll()` | `remove()` | `poll` returns null; `remove` throws |
| `peek()` | `element()` | `peek` returns null; `element` throws |

**Always prefer the null-safe versions** (`offer`, `poll`, `peek`) to avoid unexpected exceptions.

### Queue vs Stack

```
Same input order: 1, 2, 3

Queue dequeue: 1, 2, 3  ← SAME ORDER     (fair, sequential)
Stack pop:     3, 2, 1  ← REVERSED ORDER (newest first)
```

Queue = **fair** processing. Stack = **reverse** processing.

### Java Queue Interface Hierarchy

```
Iterable
  └── Collection
        └── Queue              ← interface: offer, poll, peek
              ├── LinkedList   ← general-purpose, allows null
              ├── ArrayDeque   ← fast, recommended, no null
              ├── PriorityQueue← min-heap, ordered dequeue
              └── Deque        ← double-ended queue interface
                    └── ArrayDeque
```

---

## Topic 2 — Implementation Approaches

### Approach 1 — Array-based Queue

```java
class ArrayQueue {
    int[] arr;
    int front = 0, rear = -1, size = 0;

    void enqueue(int value) {
        arr[++rear] = value;  // advance rear, insert
        size++;
    }

    int dequeue() {
        int val = arr[front]; // read from front
        front++;              // advance front
        size--;
        return val;
    }
}
```

**Memory layout after enqueue(10,20,30,40):**
```
index:  [0]  [1]  [2]  [3]  [4]  [5]  [6]  [7]
value:  [10] [20] [30] [40] [  ] [  ] [  ] [  ]
         ↑                    ↑
       front=0              rear=3
```

**The "false overflow" problem:** after two dequeues, `front=2`. Indices 0 and 1 are permanently wasted. If you enqueue 6 more items, the array says "full" even though 2 slots are available at the front. **Solution: Circular Queue.**

---

### Approach 2 — LinkedList-based Queue

```java
class LinkedQueue {
    Node head;  // front — dequeue here
    Node tail;  // rear  — enqueue here

    void enqueue(int value) {
        Node newNode = new Node(value);
        if (tail != null) tail.next = newNode;
        tail = newNode;
        if (head == null) head = tail;
    }

    int dequeue() {
        int val = head.data;
        head = head.next;
        if (head == null) tail = null;  // emptied
        return val;
    }
}
```

**Memory layout:**
```
head(front) → [10|→] → [20|→] → [30|→] → [40|null] ← tail(rear)
```

**Why head = FRONT, tail = REAR?**
- Enqueue at tail: `tail.next = newNode; tail = newNode` → O(1)
- Dequeue from head: `head = head.next` → O(1)

If we reversed this (enqueue at head, dequeue at tail), dequeue would need to traverse the whole list to find the second-to-last node → O(n).

**Advantage:** No capacity limit, no wasted space.
**Limitation:** ~16 bytes per node vs 4 bytes in array. Poor cache locality.

---

### Approach 3 — Java Built-in: ArrayDeque (Recommended)

```java
Queue<Integer> queue = new ArrayDeque<>();

queue.offer(10);   // enqueue
queue.poll();      // dequeue
queue.peek();      // front element
queue.isEmpty();
queue.size();
```

`ArrayDeque` is backed by a resizable circular array internally. It automatically handles wrap-around and resizing. **Use this in 99% of code.**

---

### Approach 4 — PriorityQueue (Heap-based)

```java
// Min-heap (default): smallest element dequeues first
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(30); pq.offer(10); pq.offer(50);
pq.poll();  // returns 10 (not 30 — insertion order irrelevant!)

// Max-heap: largest element dequeues first
PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Collections.reverseOrder());
```

| Operation | Time |
|-----------|------|
| `offer(x)` | **O(log n)** — heap insertion (bubble up) |
| `poll()` | **O(log n)** — heap removal (bubble down) |
| `peek()` | **O(1)** — min/max always at root |
| `size()` | **O(1)** |

**PriorityQueue does NOT maintain FIFO order** — it dequeues based on priority (natural ordering or custom Comparator). This is a Min-Heap, not a sorted list.

---

## Topic 3 — Circular Queue (Ring Buffer)

### The Problem with Linear Array Queue

```
After enqueue(10,20,30,40) and two dequeues:

index:  [0]  [1]  [2]  [3]  [4]  [5]  [6]  [7]
value:  [ _] [ _] [30] [40] [  ] [  ] [  ] [  ]
                   ↑         ↑
                 front=2   rear=3

Slots 0 and 1 can NEVER be reused!
```

### The Circular Queue Solution

Use **modulo arithmetic** to wrap around:

```java
rear  = (rear  + 1) % capacity;  // wraps: 7→0, 6→7, etc.
front = (front + 1) % capacity;
```

Think of it as a **ring**:

```
capacity = 5

        index 0
      /         \
   index 4     index 1
      \         /
        index 3 — index 2

front and rear "chase" each other clockwise around the ring.
```

```java
void enqueue(int value) {
    if (isFull()) return;
    arr[rear] = value;
    rear = (rear + 1) % capacity;  // ← wrap around
    size++;
}

int dequeue() {
    int val = arr[front];
    front = (front + 1) % capacity;  // ← wrap around
    size--;
    return val;
}

boolean isFull()  { return size == capacity; }
boolean isEmpty() { return size == 0; }
```

**After two dequeues and two new enqueues (circular):**
```
Slot 0 and 1 are now reused!
index:  [50] [60] [30] [40] [  ]
         ↑              ↑
        rear=2        front=2... wait, both wrapped around
```

### Complexity

| Operation | Time | Space |
|-----------|------|-------|
| `enqueue` | O(1) | O(1) |
| `dequeue` | O(1) | O(1) |
| `peek` | O(1) | O(1) |
| **Overall** | — | **O(capacity)** — fixed, no waste |

**Real-world uses:** OS kernel ring buffers, audio/video streaming buffers, network packet buffers, producer-consumer pipelines, IoT sensor data streams.

---

## Topic 4 — Deque (Double-Ended Queue)

### What is a Deque?

A **Deque** (pronounced "deck") supports insert and remove from **both ends** — front and rear. It's the most flexible linear data structure.

```
FRONT ← [A] ⇄ [B] ⇄ [C] ⇄ [D] → REAR

addFirst("X")   → [X][A][B][C][D]     O(1)
addLast("Y")    → [X][A][B][C][D][Y]  O(1)
removeFirst()   → returns X            O(1)
removeLast()    → returns Y            O(1)
peekFirst()     → returns current front O(1)
peekLast()      → returns current rear  O(1)
```

### Deque as Stack AND Queue

```java
Deque<Integer> deque = new ArrayDeque<>();

// Used as STACK (LIFO):
deque.push(1);     // addFirst()    — add to front
deque.pop();       // removeFirst() — remove from front

// Used as QUEUE (FIFO):
deque.offerLast(1);  // add to rear
deque.pollFirst();   // remove from front
```

This is why `ArrayDeque` is recommended for both stacks and queues — it's one class that handles both patterns.

### Deque vs Stack vs Queue

| | Stack | Queue | Deque |
|--|-------|-------|-------|
| Add | one end (top) | one end (rear) | **both ends** |
| Remove | one end (top) | one end (front) | **both ends** |
| Use for | LIFO | FIFO | **both + sliding window** |

### Complexity

All Deque operations: **O(1) amortized** (occasional resize for ArrayDeque).

---

## Topic 5 — Real-World Use Cases

### 1. Print Spooler — OS Print Queue

Every operating system manages print jobs in a FIFO queue:

```
Job 1 submitted → Job 2 submitted → Job 3 submitted
                                     ↑ rear

Printer finishes → Job 1 dequeued
                   Job 2 is now at front
```

FIFO guarantees **fairness** — no job is starved. No document gets skipped because it was submitted later.

### 2. CPU Task Scheduling — Round Robin

```
Processes in ready queue: [P1] [P2] [P3]
Time slice = 2ms

Cycle 1: dequeue P1, execute 2ms, if not done → enqueue back
Cycle 2: dequeue P2, execute 2ms, if not done → enqueue back
Cycle 3: dequeue P3, execute 2ms, ...
```

The circular nature of a queue perfectly models round-robin scheduling — each process waits its turn and gets back in line after its time slice.

### 3. Message Queues — Async Systems

```
Producer Service          Message Queue          Consumer Service
  (sends messages)  →→→ [MSG1][MSG2][MSG3] →→→  (processes messages)
                         decoupled, buffered
```

When the consumer is busy, messages wait in the queue — no messages are lost, no producer is blocked. Used in: **Kafka, RabbitMQ, AWS SQS, Azure Service Bus**.

### 4. BFS Web Crawler

```java
Queue<String> frontier = new ArrayDeque<>();
Set<String> visited = new HashSet<>();
frontier.offer("https://starturl.com");

while (!frontier.isEmpty()) {
    String url = frontier.poll();
    if (visited.contains(url)) continue;
    visited.add(url);
    fetch(url);
    for (String link : extractLinks(url)) frontier.offer(link);
}
```

BFS ensures that pages at "depth 1" (directly linked from start) are all crawled before pages at "depth 2". This gives a natural breadth-first coverage of the web.

### 5. Ticket Booking — Fair Queue

A queue-based booking system guarantees that users who joined earlier always get served first — no jumping the queue, no starvation, predictable wait time.

---

## Topic 6 — BFS & Sliding Window

### BFS — Breadth-First Search

BFS explores a graph **level by level** using a queue. The queue ensures we finish all nodes at distance `d` before processing any node at distance `d+1`.

```java
Queue<Integer> queue = new LinkedList<>();
boolean[] visited = new boolean[n];

visited[start] = true;
queue.offer(start);

while (!queue.isEmpty()) {
    int node = queue.poll();         // process this node
    for (int neighbour : graph.get(node)) {
        if (!visited[neighbour]) {
            visited[neighbour] = true;
            queue.offer(neighbour);  // enqueue for later processing
        }
    }
}
```

**Why does BFS find the shortest path?**

Nodes are processed in order of their distance from the source. When a node is first dequeued, the path used to reach it is guaranteed to be the shortest — because all shorter paths were already processed in earlier queue levels.

```
Graph: 0 — 1 — 3
       |   |
       2   4
       |
       5

BFS from 0:
Level 0: process [0]         → enqueue 1, 2
Level 1: process [1, 2]      → enqueue 3, 4, 5
Level 2: process [3, 4, 5]   → no new nodes

Shortest distances: 0→1=1, 0→2=1, 0→3=2, 0→4=2, 0→5=2
```

**Level-by-Level BFS Pattern:**
```java
while (!queue.isEmpty()) {
    int levelSize = queue.size();       // how many nodes at this level
    for (int i = 0; i < levelSize; i++) {
        int node = queue.poll();
        // process node at current level
        // enqueue children for next level
    }
}
```

This `levelSize` snapshot trick is critical for problems that need to know which "round" of BFS you're on (minimum steps, minimum time, etc.).

| Metric | Value |
|--------|-------|
| Time Complexity | O(V + E) |
| Space Complexity | O(V) |
| Finds shortest path? | ✅ Yes (unweighted) |

---

### Sliding Window Maximum — Deque O(n)

**Problem:** Given an array and window size `k`, find the maximum in each sliding window.

**Brute force:** for each of `n-k+1` windows, scan `k` elements → **O(n × k)**.

**Deque approach:** maintain a **monotonic decreasing deque of indices**.

```java
Deque<Integer> dq = new ArrayDeque<>();  // stores indices

for (int i = 0; i < n; i++) {
    // 1. Remove indices outside current window from FRONT
    while (!dq.isEmpty() && dq.peekFirst() < i - k + 1)
        dq.pollFirst();

    // 2. Remove indices with values ≤ arr[i] from REAR
    //    (they can never be max while arr[i] is in the window)
    while (!dq.isEmpty() && arr[dq.peekLast()] <= arr[i])
        dq.pollLast();

    dq.offerLast(i);  // 3. Add current index

    if (i >= k - 1)
        result[i - k + 1] = arr[dq.peekFirst()];  // 4. Front = max
}
```

**Trace for `arr=[3,1,2,5,4,3], k=3`:**

```
i=0: arr[0]=3  dq=[0]           (no window yet)
i=1: arr[1]=1  1<3, dq=[0,1]    (no window yet)
i=2: arr[2]=2  2>1 pop 1; 2<3, dq=[0,2]  → window[0,2]: max=arr[0]=3
i=3: arr[3]=5  5>2 pop 2; 5>3 pop 0, dq=[3]  → window[1,3]: max=arr[3]=5
i=4: arr[4]=4  4<5, dq=[3,4]    → window[2,4]: max=arr[3]=5
i=5: arr[5]=3  3 is in window, 3<4, dq=[3,4,5] → window[3,5]: max=arr[3]=5

Result: [3, 5, 5, 5]  ✓
```

**Why it's O(n):** every element is added to the deque exactly once and removed at most once. Total operations = 2n → O(n).

**Why Deque and not just a stack or queue?**
- Need `pollFront` to expire old indices (outside window)
- Need `pollRear` to maintain decreasing order (remove useless smaller indices)
- Only a Deque supports both ends in O(1)

---

## Topic 7 — Interview-Level Problems

### Problem 1: Queue Using Two Stacks

**Problem:** Implement a FIFO queue using only stack operations.

```java
class QueueUsingTwoStacks {
    Deque<Integer> inStack  = new ArrayDeque<>();
    Deque<Integer> outStack = new ArrayDeque<>();

    void enqueue(int val) {
        inStack.push(val);  // always push to inStack
    }

    int dequeue() {
        if (outStack.isEmpty()) {
            // Pour ALL inStack → outStack (reverses order → FIFO restored!)
            while (!inStack.isEmpty()) outStack.push(inStack.pop());
        }
        return outStack.pop();
    }
}
```

**Why it works:**

```
enqueue 1,2,3: inStack=[3,2,1](top=3), outStack=[]

dequeue():
  outStack empty → pour: inStack=[], outStack=[1,2,3](top=1)
  pop outStack → returns 1  ← FIFO correct!

dequeue():
  outStack=[2,3], pop → returns 2  ← FIFO correct! (no pour needed)

enqueue(4): inStack=[4], outStack=[3]
dequeue():
  outStack=[3], pop → returns 3  ← FIFO correct!
```

**Amortized O(1):** Each element is pushed once to inStack, transferred once, popped once from outStack = 3 operations total per element = **O(1) amortized**.

---

### Problem 2: Stack Using Two Queues

**Problem:** Implement LIFO stack using only queue operations.

```java
void push(int val) {
    q2.offer(val);                          // new element in q2
    while (!q1.isEmpty()) q2.offer(q1.poll()); // move q1 → q2
    Queue<Integer> temp = q1; q1 = q2; q2 = temp; // swap
}

int pop()  { return q1.poll(); }
```

**Trace:**
```
push(1): q1=[1]
push(2): q2=[2], pour q1→q2: q2=[2,1], swap: q1=[2,1]
push(3): q2=[3], pour q1→q2: q2=[3,2,1], swap: q1=[3,2,1]

pop() → poll q1 → returns 3  ← LIFO correct!
pop() → poll q1 → returns 2  ← LIFO correct!
```

**Complexity:** `push` O(n) — has to pour n elements. `pop` O(1).

---

### Problem 3: First Non-Repeating in Stream

**Problem:** After each character arrives in a stream, return the first character that has appeared exactly once so far.

```java
int[]  freq  = new int[26];
Queue<Character> queue = new LinkedList<>();

for (char c : stream) {
    freq[c - 'a']++;
    queue.offer(c);
    // Remove front while it's now repeated (freq > 1)
    while (!queue.isEmpty() && freq[queue.peek() - 'a'] > 1)
        queue.poll();
    // Front of queue = first non-repeating (or queue empty = '#')
}
```

**Trace for `"aabcbb"`:**
```
'a': freq[a]=1, q=[a]      → first non-repeating: 'a'
'a': freq[a]=2, q=[a,a]    → drain front (a repeated) → q=[]    → '#'
'b': freq[b]=1, q=[b]      → first non-repeating: 'b'
'c': freq[c]=1, q=[b,c]    → first non-repeating: 'b'
'b': freq[b]=2, drain b    → q=[c]  → first non-repeating: 'c'
'b': freq[b]=3, no new drain → q=[c] → 'c'
```

**Complexity:** O(n) time, O(1) space (queue has at most 26 distinct chars at any time).

---

### Problem 4: Rotten Oranges — Multi-Source BFS

**Problem:** Grid with fresh (1) and rotten (2) oranges. Each minute, rotten spreads to 4-adjacent cells. Return minimum minutes to rot all, or -1.

**Key insight:** Start BFS from **all rotten oranges simultaneously** — this is Multi-Source BFS.

```java
// Enqueue ALL rotten oranges first
for (int r = 0; r < rows; r++)
    for (int c = 0; c < cols; c++)
        if (grid[r][c] == 2) queue.offer(new int[]{r, c});

// BFS level by level (each level = 1 minute)
while (!queue.isEmpty()) {
    int levelSize = queue.size();
    minutes++;
    for (int i = 0; i < levelSize; i++) {
        // spread to 4 neighbours
    }
}
```

**Why multi-source?** If we started from one rotten orange, we'd miss the spreading that happens simultaneously from all rotten oranges. Starting all at level 0 models the simultaneous spread correctly.

**Complexity:** O(m × n) time and space.

---

### Problem 5: Generate Binary Numbers 1 to N

**Pattern:** BFS tree — each dequeued string is a node that generates two children.

```
Dequeue "1"   → enqueue "10", "11"
Dequeue "10"  → enqueue "100", "101"
Dequeue "11"  → enqueue "110", "111"
...

Queue acts as a level-order traversal of an implicit binary tree:
         1
       /   \
      10   11
     / \   / \
   100 101 110 111
```

**Output for N=7:** `["1","10","11","100","101","110","111"]`

**Complexity:** O(n) time, O(n) space.

---

### Problem 6: Task Scheduler

**Problem:** Given tasks with a cooldown `n` between same tasks, find minimum CPU time.

**Strategy:** Always execute the most frequent remaining task to minimize idle time.

```java
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
// Each "cycle" = n+1 slots (one task + cooldown)
while (!maxHeap.isEmpty()) {
    List<Integer> cooldown = new ArrayList<>();
    for (int i = 0; i < n+1 && !maxHeap.isEmpty(); i++) {
        cooldown.add(maxHeap.poll() - 1);  // execute task
        time++;
    }
    maxHeap.addAll(cooldown.stream().filter(f -> f > 0).collect(...));
    if (!maxHeap.isEmpty()) time += idleSlots;  // fill with idle
}
```

**Complexity:** O(n log n) — n = number of tasks, each heap operation is O(log n).

---

## Interview Complexity Cheat Sheet

| Problem | Time | Space | Key Technique |
|---------|------|-------|---------------|
| Queue from 2 Stacks | O(1) amortized | O(n) | Pour on demand |
| Stack from 2 Queues | O(n) push | O(n) | Rotate queue on push |
| First non-repeating | O(n) | O(1) | Queue + freq[26] |
| Rotten oranges | O(m×n) | O(m×n) | Multi-source BFS |
| Generate binary 1..N | O(n) | O(n) | BFS tree pattern |
| Task scheduler | O(n log n) | O(n) | MaxHeap greedy |
| Sliding window max | O(n) | O(k) | Monotonic Deque |
| BFS shortest path | O(V+E) | O(V) | Level-by-level BFS |

---

## Common Mistakes to Avoid

```java
// ❌ 1. poll()/peek() without checking isEmpty()
int val = queue.poll();   // returns null if empty → NullPointerException!
if (!queue.isEmpty()) queue.poll();  // ✅ safe

// ❌ 2. Using LinkedList instead of ArrayDeque
Queue<Integer> q = new LinkedList<>();  // works, but slower (pointer overhead)
Queue<Integer> q = new ArrayDeque<>();  // ✅ faster, cache-friendly

// ❌ 3. Wrong BFS: not marking visited BEFORE enqueuing
queue.offer(neighbour);        // ❌ same node enqueued multiple times!
visited[neighbour] = true;     //    (causes duplicate processing)
queue.offer(neighbour);

// Correct:
visited[neighbour] = true;     // ✅ mark BEFORE enqueuing
queue.offer(neighbour);

// ❌ 4. Not resetting levelSize in BFS level-by-level
while (!queue.isEmpty()) {
    int node = queue.poll();   // ❌ processes all nodes at once, can't track levels
}
// ✅ snapshot the level size:
int levelSize = queue.size();
for (int i = 0; i < levelSize; i++) { ... }

// ❌ 5. Circular queue: wrong full/empty check
if (rear == front) // ❌ ambiguous — could mean full OR empty!
if (size == 0)     // ✅ use a separate size counter for clarity

// ❌ 6. Sliding window: forgetting to check front is within window
while (!dq.isEmpty() && arr[dq.peekLast()] <= arr[i])
    dq.pollLast();   // ✅ remove smaller from rear
// BUT also need:
while (!dq.isEmpty() && dq.peekFirst() < i - k + 1)
    dq.pollFirst();  // ✅ expire out-of-window from front
```

---

## The 5 Golden Rules

```
1. Use ArrayDeque — faster than LinkedList for all queue/stack operations
2. BFS always uses a Queue — guarantees shortest path in unweighted graphs
3. Sliding window max → Deque gives O(n) vs O(nk) brute force
4. Circular queue → fixes the wasted-space flaw of linear array queue
5. offer/poll/peek — prefer over add/remove/element (null-safe, no exceptions)
```
