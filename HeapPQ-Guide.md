# Heap & Priority Queue — Complete Deep Dive in Java

---

## How to Run

```bash
javac HeapPQ.java
java HeapPQ
```

> Requires Java 17+ (uses `record` for Employee). For Java 8–16, replace `record Employee(...)` with a regular class.

---

## File Structure

```
HeapPQ.java
│
├── MinHeap                      → Array-based Min Heap from scratch
│   ├── insert() / heapifyUp()   → O(log n)
│   ├── extractMin() / heapifyDown() → O(log n)
│   ├── peek()                   → O(1)
│   ├── buildHeap()              → O(n)
│   ├── decreaseKey()            → O(log n)
│   └── delete()                 → O(log n)
│
├── MaxHeap                      → Mirror of MinHeap, flipped comparisons
│
├── heapSort()                   → O(n log n), in-place
│
├── MedianFinder                 → Two-heap median maintenance
│
├── KthLargestStream             → Online kth largest
│
├── kthLargest()                 → Problem 1
├── kthSmallest()                → Problem 2
├── topKFrequent()               → Problem 3
├── mergeKSorted()               → Problem 4 (Merge K Sorted)
├── taskScheduler()              → Problem 5
├── slidingWindowMedian()        → Problem 6
├── reorganizeString()           → Problem 8
├── minCostConnectRopes()        → Problem 9
│
└── main()                       → Runs all 7 topics
```

---

## Topic 1 — Heap Fundamentals

### What is a Heap?

A **Heap** is a **Complete Binary Tree** that satisfies the **Heap Property**.

- **Complete Binary Tree:** every level is fully filled except possibly the last level, which is filled **left to right** with no gaps.
- **Min Heap Property:** every parent ≤ both of its children → root is always the **minimum**.
- **Max Heap Property:** every parent ≥ both of its children → root is always the **maximum**.

```
Valid Min Heap:          Invalid (not complete):    Invalid (heap property broken):
        1                        1                           1
      /   \                    /   \                       /   \
     3     5                  2     3                     5     3
    / \   / \                  \                         / \
   7   9 8   6                  4  ← gap on left!       7   9
```

### The Array Representation — No Pointers Needed!

A complete binary tree maps **perfectly** to an array. This is the key insight that makes heaps memory-efficient:

```
Tree:              Array index:
        1    →  0
       / \
      3   5   →  1, 2
     / \ / \
    7  9 8  6  →  3, 4, 5, 6

For element at index i:
  Parent:       (i - 1) / 2
  Left child:   2 * i + 1
  Right child:  2 * i + 2
```

**Verification:**
```
Index 3 (val=7): parent = (3-1)/2 = 1 → arr[1]=3 ✓ (3 ≤ 7, min-heap property holds)
Index 5 (val=8): parent = (5-1)/2 = 2 → arr[2]=5 ✓ (5 ≤ 8, min-heap property holds)
```

### Why Array Storage is Superior

| | Array Heap | Node-based |
|--|-----------|------------|
| Memory per element | 4 bytes (int) | ~24 bytes (data + 2 ptrs) |
| CPU cache | Sequential access ✅ | Pointer chasing ❌ |
| Overhead | None | Object headers |
| Implementation | Simple index math | Complex pointers |

### Heap vs BST — Know When to Use Which

| Property | Heap | BST |
|----------|------|-----|
| Min/Max access | **O(1)** | O(log n) |
| Arbitrary search | O(n) | **O(log n)** |
| Sorted iteration | Not meaningful | **O(n) in-order** |
| Storage | Array, compact | Node objects |
| Best for | **Repeated min/max** | **Sorted data, range queries** |

> **Rule:** Use a Heap when you repeatedly need the minimum or maximum. Use a BST when you need sorted order or arbitrary key lookup.

---

## Topic 2 — Min Heap vs Max Heap

### Min Heap — Root is Always Minimum

```
Insert [5, 3, 8, 1, 9, 2, 7] one by one:

Final Min Heap:
        1
       / \
      3   2
     / \ / \
    5  9 8  7

Array: [1, 3, 2, 5, 9, 8, 7]
Drain order: 1, 2, 3, 5, 7, 8, 9  ← ascending!
```

### Max Heap — Root is Always Maximum

```
Same insertions into Max Heap:
        9
       / \
      5   8
     / \ / \
    3  1 2  7

Array: [9, 5, 8, 3, 1, 2, 7]
Drain order: 9, 8, 7, 5, 3, 2, 1  ← descending!
```

### The Counterintuitive K-th Element Trick

This confuses almost everyone at first:

```
kth LARGEST  →  MIN heap of size k
  Why? Heap holds the k largest seen so far.
       Root = smallest of those k = kth largest overall.
       If new element > root: it belongs in top-k → push, pop old root.

kth SMALLEST →  MAX heap of size k
  Why? Heap holds the k smallest seen so far.
       Root = largest of those k = kth smallest overall.
       If new element < root: it belongs in bottom-k → push, pop old root.
```

---

## Topic 3 — Heap Operations & Complexity

### Insert (Heapify Up) — O(log n)

```java
void insert(int val) {
    arr[size] = val;       // 1. Add at end (maintain complete tree)
    size++;
    heapifyUp(size - 1);   // 2. Restore heap property
}

void heapifyUp(int i) {
    while (i > 0 && arr[parent(i)] > arr[i]) {  // parent too large
        swap(i, parent(i));
        i = parent(i);                            // move up
    }
}
```

**Trace: insert(2) into [1, 3, 5, 7, 9, 8, 6]:**
```
Add at index 7: [1, 3, 5, 7, 9, 8, 6, 2]
parent(7) = 3 → arr[3]=7 > 2 → swap: [1, 3, 5, 2, 9, 8, 6, 7]
parent(3) = 1 → arr[1]=3 > 2 → swap: [1, 2, 5, 3, 9, 8, 6, 7]
parent(1) = 0 → arr[0]=1 ≤ 2 → STOP ✓
```

**Why O(log n)?** In the worst case, the new element bubbles all the way from a leaf to the root — a path of length h = log n (since the tree is complete).

### Extract Min (Heapify Down) — O(log n)

```java
int extractMin() {
    int min = arr[0];          // 1. Save root (the minimum)
    arr[0] = arr[size - 1];    // 2. Move LAST element to root
    size--;
    heapifyDown(0);            // 3. Restore heap property
    return min;
}

void heapifyDown(int i) {
    int smallest = i;
    if (hasLeft(i)  && arr[leftChild(i)]  < arr[smallest]) smallest = leftChild(i);
    if (hasRight(i) && arr[rightChild(i)] < arr[smallest]) smallest = rightChild(i);
    if (smallest != i) {
        swap(i, smallest);
        heapifyDown(smallest);  // continue down
    }
}
```

**Why move LAST element to root?** We need to maintain the complete tree property. Only the last element can be removed without creating a gap. Then we restore the heap order by sinking it down.

**Trace: extractMin() on [1, 2, 5, 3, 9, 8, 6]:**
```
Save 1. Move last (6) to root: [6, 2, 5, 3, 9, 8]
6 > min(2,5)=2 → swap with left: [2, 6, 5, 3, 9, 8]
6 > min(3,9)=3 → swap with left: [2, 3, 5, 6, 9, 8]
6 has no children that are smaller → STOP
Return 1 ✓
```

### Build Heap — O(n) — The Non-Obvious Result

**Naive approach:** insert n elements one by one → O(n log n)

**Smart approach:** start with any array, heapify down from the **last non-leaf** to the root:

```java
void buildHeap(int[] input) {
    System.arraycopy(input, 0, arr, 0, input.length);
    size = input.length;
    for (int i = size / 2 - 1; i >= 0; i--) {
        heapifyDown(i);  // heapify every internal node, bottom-up
    }
}
```

**Why O(n) and not O(n log n)?**

Most nodes are near the bottom and do very little work:

```
n/2  nodes are leaves    → 0 swaps each   (height 0)
n/4  nodes at height 1   → ≤ 1 swap each
n/8  nodes at height 2   → ≤ 2 swaps each
...
1    node at height log n → ≤ log n swaps

Total work = n/2·0 + n/4·1 + n/8·2 + n/16·3 + ...
           = n · Σ(k/2^k) for k=0..log n
           = n · 2    (geometric series sum = 2)
           = O(n)
```

### Heap Sort — O(n log n), In-Place

```
Phase 1: buildMaxHeap(arr)                  → O(n)
Phase 2: for end = n-1 down to 1:
           swap(arr[0], arr[end])            // max goes to correct position
           heapifyDown(arr, end, 0)          // restore heap for remaining

After each swap, the sorted portion grows from the right.
```

```
Input:  [4, 2, 7, 1, 5]
Build max heap: [7, 5, 4, 1, 2]

Swap 7↔2: [2,5,4,1 | 7] → heapify → [5,2,4,1 | 7]
Swap 5↔1: [1,2,4 | 5,7] → heapify → [4,2,1 | 5,7]
Swap 4↔1: [1,2 | 4,5,7] → heapify → [2,1 | 4,5,7]
Swap 2↔1: [1 | 2,4,5,7]

Result: [1,2,4,5,7] ✓
```

**Comparison with other sorting algorithms:**

| Algorithm | Time | Space | Stable? |
|-----------|------|-------|---------|
| Heap Sort | O(n log n) | **O(1)** | ❌ No |
| Merge Sort | O(n log n) | O(n) | ✅ Yes |
| Quick Sort | O(n log n) avg | O(log n) | ❌ No |
| Tim Sort (Java Arrays.sort) | O(n log n) | O(n) | ✅ Yes |

Heap Sort's advantage: guaranteed O(n log n) **and** O(1) in-place (no extra memory).

---

## Topic 4 — PriorityQueue in Java

### Basics

```java
// Min Heap (default — natural ordering)
PriorityQueue<Integer> minPQ = new PriorityQueue<>();

// Max Heap
PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Collections.reverseOrder());

// Custom comparator (objects sorted by a field)
PriorityQueue<int[]> custom = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
```

### Key Methods — Two Versions of Each

| Operation | Null-safe (preferred) | Exception-throwing |
|-----------|----------------------|-------------------|
| Insert | `offer(e)` | `add(e)` |
| Remove min | `poll()` | `remove()` |
| View min | `peek()` | `element()` |

Always prefer `offer/poll/peek` — they return `null` instead of throwing exceptions on empty queue.

### Critical: Iteration ≠ Sorted Order

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(5); pq.offer(1); pq.offer(3);

// WRONG: iterating prints heap array order, NOT sorted
for (int x : pq) System.out.print(x + " ");  // prints: 1 5 3 (heap internal array)

// CORRECT: poll gives sorted order
while (!pq.isEmpty()) System.out.print(pq.poll() + " ");  // prints: 1 3 5
```

The internal array satisfies the heap property, not sorted order. Only `poll()` extracts elements in guaranteed sorted order.

### Common Patterns

```java
// Pattern: PQ of int[] pairs — sort by first element
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
pq.offer(new int[]{3, 100});  // [priority, value]
pq.offer(new int[]{1, 200});
pq.poll();  // returns [1, 200] (min priority first)

// Pattern: PQ with multiple fields
PriorityQueue<int[]> twoField = new PriorityQueue<>((a, b) -> {
    if (a[0] != b[0]) return a[0] - b[0];  // sort by first field
    return a[1] - b[1];                      // break ties by second field
});
```

---

## Topic 5 — Real-World Applications

### 1. OS Process Scheduling

Every modern operating system uses a priority queue (backed by a heap) for CPU scheduling:

```
Max Priority Queue:
  Process A, priority 5
  Process B, priority 8  ← runs first
  Process C, priority 3

CPU always picks the highest-priority runnable process.
Linux CFS scheduler uses a Red-Black Tree instead of a heap
(supports O(log n) arbitrary deletion, unlike a heap).
```

### 2. Dijkstra's Shortest Path — Min Heap

The classic single-source shortest path algorithm:

```java
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
pq.offer(new int[]{0, source});  // [distance, node]

while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int dist = curr[0], node = curr[1];
    if (visited[node]) continue;
    visited[node] = true;

    for (int[] neighbor : graph[node]) {
        int newDist = dist + neighbor[1];
        if (newDist < distances[neighbor[0]]) {
            distances[neighbor[0]] = newDist;
            pq.offer(new int[]{newDist, neighbor[0]});
        }
    }
}
```

**Time:** O((V + E) log V) — each vertex and edge processed once, log V for heap operations.

### 3. Huffman Encoding — Min Heap

Huffman coding assigns shorter bit sequences to more frequent characters:

```
Frequencies: a=5, b=3, c=1, d=1, e=1

Min Heap: [(1,c), (1,d), (1,e), (3,b), (5,a)]

Step 1: merge (1,c) + (1,d) = (2,cd)  → heap: [(1,e),(2,cd),(3,b),(5,a)]
Step 2: merge (1,e) + (2,cd) = (3,ecd) → heap: [(3,b),(3,ecd),(5,a)]
Step 3: merge (3,b) + (3,ecd) = (6,becd) → heap: [(5,a),(6,becd)]
Step 4: merge (5,a) + (6,becd) = (11, root)

Result codes: a=0, b=10, c=110, d=1110, e=1111
(frequent chars get shorter codes → compression)
```

### 4. Median Maintenance

Maintain running median with O(log n) insert and O(1) query:

```
Two heaps partition the stream:
  maxHeap: lower half (root = max of lower half)
  minHeap: upper half (root = min of upper half)

Invariant 1: maxHeap.size() == minHeap.size() OR maxHeap.size() == minHeap.size() + 1
Invariant 2: maxHeap.peek() ≤ minHeap.peek()

Median:
  Even count: (maxHeap.peek() + minHeap.peek()) / 2.0
  Odd count:  maxHeap.peek()  (lower half has 1 extra)

Stream: 1, 7, 3, 5, 2
After 1: max=[1]    min=[]     median=1.0
After 7: max=[1]    min=[7]    median=4.0
After 3: max=[3,1]  min=[7]    median=3.0
After 5: max=[3,1]  min=[5,7]  median=4.0
After 2: max=[3,2,1] min=[5,7] median=3.0
```

---

## Topic 6 — Heap-Based Problem Patterns

### Pattern 1 — Kth Element (Heap of Size K)

```
kth LARGEST:
  Init: empty min heap, size limit = k
  For each num:
    push(num)
    if size > k: pop min  (remove the smallest — not in top k)
  Root = kth largest

kth SMALLEST:
  Same but max heap: pop max (remove largest — not in bottom k)
  Root = kth smallest
```

**Time: O(n log k)** — n elements processed, each heap op is O(log k).
**Space: O(k)** — only k elements in heap at any time.

### Pattern 2 — Top K Frequent

```
Step 1: Count frequencies with HashMap     → O(n)
Step 2: Min heap of size k by frequency    → O(n log k)
        For each unique element:
          push(element)
          if heap.size() > k: pop (removes least frequent)
Step 3: Drain heap → top k elements        → O(k log k)

Total: O(n log k) time, O(n) space
```

### Pattern 3 — Merge K Sorted Sequences

```
Init min heap with first element from each list: k elements
While heap not empty:
  Pop minimum (val, listIdx, elemIdx)
  Add to result
  If listIdx has a next element: push it

Each element is pushed and popped exactly once.
Time: O(n log k) where n = total elements, k = number of lists
Space: O(k) for the heap
```

### Pattern 4 — Two Heaps for Median

This pattern generalizes to any problem needing a **balanced partition** of a stream:

```
addNum(x):
  1. Push x to maxHeap (lower half)
  2. If maxHeap.peek() > minHeap.peek(): move max of maxH to minH (order fix)
  3. Rebalance sizes: |maxH.size() - minH.size()| ≤ 1

findMedian():
  Equal sizes → (maxH.peek() + minH.peek()) / 2.0
  maxH larger → maxH.peek()
```

### Pattern 5 — Greedy + Heap

When a greedy algorithm needs repeated access to the current minimum or maximum:

```
Task Scheduler: always execute most frequent remaining task
  → Max Heap by frequency

Minimum cost to connect ropes: always merge two shortest
  → Min Heap

Huffman encoding: always merge two least frequent
  → Min Heap

Meeting rooms: check if any room has ended
  → Min Heap of end times
```

### Pattern 6 — Sliding Window with Heap (Lazy Deletion)

For sliding window problems where elements expire, maintain a heap but **lazily mark deletions**:

```
When an element exits the window:
  Add it to a "deleted" set/counter (don't remove from heap yet)

When peeking at heap root:
  If root is in deleted set → pop it (it's stale)
  Otherwise → it's the true min/max

This avoids O(n) arbitrary deletion; lazy deletion is O(log n)
```

### Pattern Cheat Sheet

| Problem Signal | Which Heap | Size Constraint |
|---------------|------------|-----------------|
| "kth largest" | Min heap | k |
| "kth smallest" | Max heap | k |
| "top k frequent" | Min heap (by freq) | k |
| "merge k sorted" | Min heap | k |
| "running median" | Two heaps | N/A |
| "repeated minimum" | Min heap | N/A |
| "meeting rooms" | Min heap (end times) | N/A |
| "sliding window median" | Two heaps + lazy delete | window size |

---

## Topic 7 — Interview-Level Problems

### Problem 1: Kth Largest Element — O(n log k)

```java
int kthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) minHeap.poll();  // remove smallest
    }
    return minHeap.peek();  // root = kth largest
}
```

**Trace for nums=[3,2,1,5,6,4], k=2:**
```
Process 3: heap=[3]
Process 2: heap=[2,3]
Process 1: heap=[1,2,3] → size>2 → pop 1 → heap=[2,3]
Process 5: heap=[2,3,5] → size>2 → pop 2 → heap=[3,5]
Process 6: heap=[3,5,6] → size>2 → pop 3 → heap=[5,6]
Process 4: heap=[4,5,6] → size>2 → pop 4 → heap=[5,6]
Return heap.peek() = 5  ✓ (2nd largest)
```

---

### Problem 2: Find Median from Data Stream — O(log n) add, O(1) median

```java
class MedianFinder {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(reverseOrder()); // lower half
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();                // upper half

    void addNum(int num) {
        maxHeap.offer(num);

        // Step 1: fix ordering (max of lower ≤ min of upper)
        if (!minHeap.isEmpty() && maxHeap.peek() > minHeap.peek())
            minHeap.offer(maxHeap.poll());

        // Step 2: fix sizes (maxHeap can have at most 1 extra)
        if (maxHeap.size() > minHeap.size() + 1) minHeap.offer(maxHeap.poll());
        else if (minHeap.size() > maxHeap.size()) maxHeap.offer(minHeap.poll());
    }

    double findMedian() {
        if (maxHeap.size() == minHeap.size())
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        return maxHeap.peek();
    }
}
```

**Why two heaps?**
```
Stream so far: [1, 3, 5, 7, 9]

maxHeap (lower): [5, 3, 1]  root=5
minHeap (upper): [7, 9]     root=7

Median = 5 (odd count → maxHeap root)
```

The ordering invariant `maxHeap.peek() ≤ minHeap.peek()` ensures the two heaps represent a true partition of the data at the median.

---

### Problem 3: Merge K Sorted Lists — O(n log k)

```java
PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
// Seed: first element from each list
for (int i = 0; i < lists.length; i++)
    if (lists[i].length > 0)
        heap.offer(new int[]{lists[i][0], i, 0});  // [value, listIdx, elemIdx]

while (!heap.isEmpty()) {
    int[] top = heap.poll();
    result[idx++] = top[0];
    int li = top[1], ei = top[2];
    if (ei + 1 < lists[li].length)
        heap.offer(new int[]{lists[li][ei+1], li, ei+1});  // next from same list
}
```

**Why O(n log k)?** The heap always contains exactly k elements (one per list). Each of the n total elements is pushed and popped once, and each heap operation costs O(log k).

---

### Problem 4: Task Scheduler — O(n log n)

```
Strategy: always execute the most frequent task in each time slot.
A cycle = n+1 slots (task + cooldown).
Use a max heap to pick the most frequent each cycle.

Example: tasks=[A,A,A,B,B,B], n=2
Heap: [3(A), 3(B)]

Cycle 1 (slots 1-3): Execute A (freq→2), Execute B (freq→2), idle
  time=3, heap=[2(A),2(B)]

Cycle 2 (slots 4-6): Execute A (freq→1), Execute B (freq→1), idle
  time=6, heap=[1(A),1(B)]

Cycle 3 (slots 7-8): Execute A (freq→0), Execute B (freq→0)
  time=8, done!

Answer: 8
```

---

### Problem 5: Reorganize String — O(n log n)

```
"aab": a=2, b=1
Max heap: [(2,'a'), (1,'b')]

Step 1: pop a(2), pop b(1) → "ab", push a(1)
        heap: [(1,'a')]
Step 2: only a(1) left → append 'a' → "aba"
Result: "aba" ✓

"aaab": a=3, b=1
Step 1: pop a(3), pop b(1) → "ab", push a(2)
Step 2: only a(2) left → append 'a' → "aba"
        a(1) remains but heap size < 2 → impossible to place without adjacency
Result: "" (impossible)
```

**Condition for impossibility:** if any character's frequency > `(n+1)/2`, it's impossible to reorganize.

---

### Problem 6: Minimum Cost to Connect Ropes — O(n log n)

```
Greedy insight: to minimize total cost, always join the two shortest ropes.

Ropes: [4, 3, 2, 6]

Step 1: merge 2+3=5, cost=5.  Ropes: [4, 5, 6]
Step 2: merge 4+5=9, cost=9.  Ropes: [6, 9]
Step 3: merge 6+9=15, cost=15. Ropes: [15]

Total cost: 5+9+15=29

Why greedy works: short ropes merged early are added to longer combined ropes fewer times.
Merging long ropes early means that extra length gets counted in every subsequent merge.
```

This is essentially **Huffman Encoding** — the same greedy minimum-merge algorithm.

---

### Problem 7: Meeting Rooms II — O(n log n)

```
Sort meetings by start time.
Min heap tracks end times of active meetings.

If new meeting starts AFTER the earliest ending room: reuse that room.
Otherwise: allocate a new room.

Meetings: [0,30], [5,10], [15,20]

Sort: [0,30], [5,10], [15,20]
Process [0,30]:  heap=[30]          (1 room)
Process [5,10]:  5 < 30, new room → heap=[10,30]  (2 rooms)
Process [15,20]: 15 > 10, reuse → poll 10, push 20 → heap=[20,30]  (still 2 rooms)

Answer: heap.size() = 2 rooms needed ✓
```

---

## Interview Complexity Cheat Sheet

| Problem | Time | Space | Key Pattern |
|---------|------|-------|-------------|
| Kth largest | O(n log k) | O(k) | Min heap size k |
| Kth smallest | O(n log k) | O(k) | Max heap size k |
| Top k frequent | O(n log k) | O(n) | Freq map + min heap |
| Merge k sorted | O(n log k) | O(k) | Min heap one per list |
| Find median stream | O(log n) add | O(n) | Two heaps balanced |
| Task scheduler | O(n log n) | O(1) | Max heap + cycle |
| Sliding window median | O(n log k) | O(k) | Two heaps + lazy delete |
| Kth largest stream | O(log k) per add | O(k) | Min heap size k |
| Reorganize string | O(n log n) | O(n) | Max heap by freq |
| Connect ropes | O(n log n) | O(n) | Min heap greedy |
| Meeting rooms II | O(n log n) | O(n) | Min heap end times |
| Heap sort | O(n log n) | O(1) | In-place max heap |
| Build heap | **O(n)** | O(1) | heapifyDown bottom-up |

---

## Common Mistakes to Avoid

```java
// ❌ 1. kth LARGEST → confusing which heap to use
PriorityQueue<Integer> maxH = new PriorityQueue<>(Collections.reverseOrder());
// For kth LARGEST, you want a MAX heap?? NO!
// ✅ kth LARGEST needs a MIN heap of size k:
PriorityQueue<Integer> minH = new PriorityQueue<>(k);

// ❌ 2. Iterating PQ expecting sorted order
for (int x : pq) System.out.print(x + " ");  // NOT sorted!
// ✅ Use poll() for sorted output:
while (!pq.isEmpty()) System.out.print(pq.poll() + " ");

// ❌ 3. Median finder: adding to wrong heap first
minHeap.offer(num);  // adding to upper half first breaks the invariant
// ✅ Always add to maxHeap first, then rebalance:
maxHeap.offer(num);
// ... then fix ordering and sizes

// ❌ 4. Task scheduler: incorrect cycle counting
for (int i = 0; i < n; i++) ...  // n iterations, not n+1!
// ✅ Cycle = n+1 slots (1 task + n cooldown slots)
for (int i = 0; i <= n; i++) ...

// ❌ 5. Assuming PQ.remove(obj) is fast
pq.remove(specificObject);  // O(n) linear scan to find it!
// For O(log n) arbitrary deletion: use lazy deletion with a HashSet
// or maintain a TreeSet instead

// ❌ 6. Using == instead of compareTo for custom objects in PQ
Comparator<int[]> bad  = (a, b) -> a[0] > b[0] ? 1 : -1;  // skips equal case
Comparator<int[]> good = (a, b) -> Integer.compare(a[0], b[0]); // handles all cases

// ❌ 7. buildHeap starts at n/2 not n/2 - 1
for (int i = n/2; i >= 0; i--)  // processes one extra leaf (wasted work)
// ✅ Last non-leaf index = n/2 - 1:
for (int i = n/2 - 1; i >= 0; i--)
```

---

## The 7 Golden Rules

```
1. kth LARGEST  → Min heap of size k  (counterintuitive — remember it!)
2. kth SMALLEST → Max heap of size k  (counterintuitive — remember it!)
3. Running median → Two heaps: maxHeap (lower) + minHeap (upper), keep balanced
4. Merge k sorted → Min heap seeded with one element per list, O(n log k)
5. buildHeap is O(n), NOT O(n log n) — use it when converting array to heap
6. Java PriorityQueue: MIN by default. Use Collections.reverseOrder() for MAX
7. Iterating PriorityQueue ≠ sorted. Only poll() extracts in sorted order
```
