# Binary Search Trees — Complete Deep Dive in Java

---

## How to Run

```bash
javac BinarySearchTrees.java
java BinarySearchTrees
```

> Requires Java 8 or higher. Check with `java -version`.

---

## File Structure

```
BinarySearchTrees.java
│
├── Node                         → val, left, right, height
│
├── BST                          → Full BST implementation
│   ├── insert()                 → O(log n) avg
│   ├── search()                 → O(log n) avg
│   ├── delete()                 → O(log n) avg, 3 cases
│   ├── findMin() / findMax()    → O(log n)
│   ├── inOrder()                → O(n), always sorted
│   ├── height() / size()        → O(n)
│   ├── floor() / ceiling()      → O(log n)
│   ├── kthSmallest()            → O(n)
│   └── countInRange()           → O(log n + k)
│
├── AVL                          → Self-balancing BST
│   ├── rightRotate() / leftRotate()
│   ├── balance()                → rebalance after every insert
│   └── insert()                 → O(log n), always balanced
│
├── isValidBST()                 → Problem 1
├── kthSmallest()                → Problem 2 (iterative)
├── lcaBST()                     → Problem 3
├── sortedArrayToBST()           → Problem 4
├── toGST()                      → Problem 5
├── inorderSuccessor()           → Problem 6
├── findTarget()                 → Problem 7 (two sum)
├── recoverBST()                 → Problem 8
├── balanceBST()                 → Problem 9
├── mergeBSTs()                  → Problem 10
│
└── main()                       → Runs all 7 topics
```

---

## Topic 1 — BST Fundamentals

### What is a BST?

A **Binary Search Tree** is a Binary Tree with one additional rule — the **ordering property**:

> For **every** node `n` in the tree:
> - All values in the **left** subtree are **strictly less than** `n.val`
> - All values in the **right** subtree are **strictly greater than** `n.val`

This must hold not just for the root and its direct children, but for **every single node** in the tree.

```
Valid BST:             Invalid BST:
      50                     10
     /  \                   /  \
    30   70                5   15
   /  \  / \              / \
  20  40 60  80          3  12   ← 12 > 10! Violates global ordering
                                   (12 can't be in left subtree of 10)
```

### Why BST?

| Operation | Sorted Array | Linked List | BST (balanced) |
|-----------|-------------|------------|----------------|
| Search | O(log n) | O(n) | **O(log n)** |
| Insert | **O(n)** (shift) | O(1) | **O(log n)** |
| Delete | **O(n)** (shift) | O(n) | **O(log n)** |
| Min/Max | O(1) | O(n) | **O(log n)** |

BST is the **sweet spot**: fast search like a sorted array, fast insert/delete like a linked list.

### How the Ordering Property Enables O(log n) Search

```
Search for 60 in BST:
      50 ← compare: 60 > 50 → go RIGHT (eliminate entire left half)
        \
        70 ← compare: 60 < 70 → go LEFT (eliminate entire right half)
        /
       60 ← FOUND! ✓

Only 3 comparisons for 7 nodes.
Each comparison eliminates ~half the remaining tree.
```

---

## Topic 2 — BST Properties

### Property 1: In-order Traversal = Sorted Ascending

This is the most important property of a BST and the foundation of many algorithms:

```
BST:          50
             /  \
           30    70
          /  \   / \
         20  40 60  80

In-order (L→Root→R): 20, 30, 40, 50, 60, 70, 80  ← always sorted!
```

**Uses:**
- Print all values in sorted order
- Validate that a tree is a valid BST
- Find the kth smallest element
- Convert BST to sorted array

### Property 2: Min is Leftmost, Max is Rightmost

```java
Node findMin(Node node) {
    while (node.left != null) node = node.left;
    return node;
}
```

The minimum is reached by going left at every step until you hit null. This is because every left child is smaller than its parent, recursively.

**For BST above:** Min = 20 (leftmost), Max = 80 (rightmost). Path to min: 50→30→20.

### Property 3: Same Keys, Different Shapes

Insertion order determines tree shape. Same keys can produce radically different BSTs:

```
Insert [50, 30, 70]:        Insert [30, 50, 70]:
      50                          30
     /  \                           \
    30   70                         50
h=2, balanced                          \
                                        70
                                   h=3, right-skewed
```

Both have identical in-order: [30, 50, 70]. But the skewed tree has O(n) operations instead of O(log n). **Insertion order matters enormously.**

### Property 4: Floor and Ceiling

- **Floor(key)**: largest value ≤ key
- **Ceiling(key)**: smallest value ≥ key

```java
Node floor(Node node, int key) {
    if (node == null) return null;
    if (key == node.val) return node;          // exact match
    if (key <  node.val) return floor(node.left, key); // must be in left
    Node rightFloor = floor(node.right, key);  // try right subtree
    return rightFloor != null ? rightFloor : node; // right or current
}
```

**Example for BST {10,20,30,40,50,60,70,80}:**

| Key | Floor | Ceiling |
|-----|-------|---------|
| 15 | 10 | 20 |
| 33 | 30 | 40 |
| 50 | 50 | 50 (exact match) |
| 75 | 70 | 80 |

**Real-world use:** find closest available seat, nearest price point, IP routing.

### Property 5: Count in Range — O(log n + k)

```java
int countInRange(Node node, int lo, int hi) {
    if (node == null) return 0;
    if (node.val < lo) return countInRange(node.right, lo, hi); // skip left
    if (node.val > hi) return countInRange(node.left,  lo, hi); // skip right
    return 1 + countInRange(node.left, lo, hi) + countInRange(node.right, lo, hi);
}
```

BST ordering lets us prune entire subtrees — only O(log n) nodes are visited to locate boundaries, plus O(k) to count matches.

---

## Topic 3 — Search, Insert, Delete

### Search — O(log n) Average

```java
Node search(Node node, int val) {
    if (node == null || node.val == val) return node;  // base cases
    if (val < node.val) return search(node.left,  val); // eliminate right half
    else                return search(node.right, val); // eliminate left half
}
```

**Why it works:** At each node, the BST property guarantees the target can only be in one subtree. We never search both sides.

**Trace for search(25) in {10,20,25,30,40,50}:**
```
50: 25 < 50 → go left
30: 25 < 30 → go left
20: 25 > 20 → go right
25: found! ✓   (4 steps, not 6)
```

---

### Insert — O(log n) Average

```java
Node insert(Node node, int val) {
    if (node == null) return new Node(val);       // insert at null spot
    if      (val < node.val) node.left  = insert(node.left,  val);
    else if (val > node.val) node.right = insert(node.right, val);
    // duplicate: do nothing
    return node;
}
```

Insert uses the same path as search, placing the new node exactly where search would "fall off" into null.

**Trace for insert(45) into BST with root 50:**
```
50: 45 < 50 → go left
30: 45 > 30 → go right
40: 45 > 40 → go right
null → create Node(45), set as 40.right  ✓
```

---

### Delete — O(log n) Average — Three Cases

**Case 1: Leaf node** — simply set parent's pointer to null.

```
Delete 10:          Delete 10:
    20                  20
   /                   /
  10        →       null
```

**Case 2: One child** — replace the node with its single child.

```
Delete 20 (has only left child 10):
    50                  50
   /                   /
  30          →       30
 /                   /
20                  10
/
10
```

**Case 3: Two children** — the critical case. Replace with **in-order successor** (the smallest value in the right subtree), then delete that successor.

```
Delete 30 (has left=20, right=40):

      50                    50
     /  \                  /  \
    30   70    →          35   70
   /  \  / \             /  \  / \
  20  40 60  80         20  40 60  80
      /                    /
     35                   (35 deleted from here)

Step 1: Find in-order successor of 30 = min(right subtree) = 35
Step 2: Copy 35 into 30's position
Step 3: Delete original 35 from right subtree (it's a leaf or has one child)
```

**Why in-order successor?** It's the smallest value larger than the deleted node. Placing it at the deleted node's position maintains the BST property: all left values < 35 < all right values.

**Why not in-order predecessor?** Either works. Some implementations use the predecessor (largest in left subtree). Both are valid.

---

## Topic 4 — Complexity Analysis

### Why O(log n) for Balanced BST

```
At each step, we eliminate approximately HALF the remaining nodes:

After 1 step:  n/2   nodes remain
After 2 steps: n/4   nodes remain
After 3 steps: n/8   nodes remain
After k steps: n/2^k nodes remain

Stop when 1 node: n/2^k = 1  →  k = log₂(n)
```

This is identical to binary search on a sorted array. The tree structure provides the same halving behavior — but dynamically, without needing the array to be contiguous.

### Full Complexity Table

| Operation | Best | Average | Worst | Notes |
|-----------|------|---------|-------|-------|
| Search | O(1) | O(log n) | O(n) | Worst = skewed tree |
| Insert | O(1) | O(log n) | O(n) | Worst = skewed tree |
| Delete | O(log n) | O(log n) | O(n) | Worst = skewed tree |
| Min / Max | O(1) | O(log n) | O(n) | Leftmost/rightmost |
| Floor / Ceiling | O(1) | O(log n) | O(n) | BST path |
| In-order traverse | O(n) | O(n) | O(n) | All n nodes visited |
| Kth Smallest | O(k) | O(log n)* | O(n) | *with augmented BST |
| Count in range | O(1) | O(log n + k) | O(n) | k = matches |
| **Space (tree)** | **O(n)** | **O(n)** | **O(n)** | n nodes stored |
| **Space (recursion)** | **O(1)** | **O(log n)** | **O(n)** | Call stack depth |

### The Skewed Tree Problem

```
Insert sorted values 1, 2, 3, 4, 5:

1
 \
  2
   \
    3
     \
      4
       \
        5

Height = n = 5. Search(5) visits ALL nodes = O(n).
```

Every BST operation degrades to O(n) on a skewed tree — **same as linear search**. The entire point of using a BST is lost.

**Prevention:**
1. Use AVL Tree (guaranteed balance after every operation)
2. Use Red-Black Tree (looser balance, fewer rotations — used by Java's TreeMap)
3. Shuffle input before inserting (random order prevents worst case with ~99.9% probability)

---

## Topic 5 — Balanced vs Skewed Trees

### The Height Problem

```
For n = 1,000,000 nodes:
  Balanced tree: height ≈ log₂(1,000,000) ≈ 20
  Skewed tree:   height = 1,000,000

Operations on balanced:  20 steps
Operations on skewed: 1,000,000 steps
→ 50,000× performance difference
```

### AVL Tree — Strict Balance

**AVL property:** At every node, `|height(left) - height(right)| ≤ 1`.

**Balance factor:** `BF = height(left) - height(right)`. Valid: -1, 0, +1.

After every insert/delete, if any node's BF becomes +2 or -2, we **rotate** to restore balance.

#### Right Rotation (LL Case)

```
Before (BF at 30 = +2):    After:
       30                        20
      /                         /  \
     20             →          10   30
    /
   10

rightRotate(30):
  x = 30.left = 20
  T2 = x.right = null
  x.right = 30
  30.left = T2 (null)
  return x (20 is new root)
```

#### Left Rotation (RR Case)

```
Before (BF at 10 = -2):    After:
   10                            20
     \                          /  \
     20             →          10   30
       \
       30

leftRotate(10):
  y = 10.right = 20
  T2 = y.left = null
  y.left = 10
  10.right = T2 (null)
  return y (20 is new root)
```

#### LR and RL Cases

LR Case: Left child is right-heavy.
```
    30                30               20
   /     left(10)    /   right(30)    /  \
  10    ────────→   20  ──────────→  10  30
    \              /
    20            10
```

RL Case: Right child is left-heavy.
```
  10               10               20
    \   right(30)    \  left(10)   /  \
    30  ──────────→  20 ────────→ 10  30
   /                  \
  20                  30
```

**Key insight after rotations:** The BST property is preserved. Values that were in the correct relative position remain correct — rotations only change the tree's **shape**, not the **ordering**.

### Red-Black Tree (Java's TreeMap)

A Red-Black Tree has **looser balance requirements** than AVL:

```
Properties:
1. Every node is RED or BLACK
2. Root is BLACK
3. Every leaf (null) is BLACK
4. RED node → both children are BLACK
5. All paths from any node to leaf-nulls have the same number of BLACK nodes
```

This guarantees height ≤ 2 log₂(n+1) — not as tight as AVL (log n), but close enough for O(log n) operations.

**AVL vs Red-Black:**

| Property | AVL | Red-Black |
|----------|-----|-----------|
| Balance | Stricter | Looser |
| Search | Slightly faster | Slightly slower |
| Insert/Delete rotations | More | Fewer |
| Best for | Read-heavy | Write-heavy |
| Used in | - | **Java TreeMap/TreeSet** |

---

## Topic 6 — Real-World Applications

### 1. Database B-Tree Indexes

MySQL InnoDB uses B-Trees (generalized BST where nodes hold multiple keys):

```sql
SELECT * FROM employees WHERE salary BETWEEN 40000 AND 80000
```

```
B-Tree execution:
1. Search for salary=40000     → O(log n) — traverse tree
2. In-order from 40000 to 80000 → O(k)    — collect results
Total: O(log n + k)

Without index (sequential scan): O(n) = scan all 10M rows
With B-Tree index (1000 results): O(20 + 1000) = O(1020) steps
Speedup: ~10,000×
```

### 2. Java TreeMap — Ordered Operations

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(45000, "Alice");   // O(log n)
map.put(72000, "Bob");
map.put(38000, "Carol");

// Operations impossible with HashMap:
map.firstKey()              // 38000 (minimum)
map.lastKey()               // 72000 (maximum)
map.floorKey(50000)         // 45000 (largest ≤ 50000)
map.ceilingKey(50000)       // 72000 (smallest ≥ 50000)
map.subMap(40000, 75000)    // entries with keys in [40000, 75000)
map.headMap(50000)          // entries with keys < 50000
map.tailMap(50000)          // entries with keys ≥ 50000
```

All of these are O(log n) — impossible in O(log n) with a hash map.

### 3. Event Scheduling System

```java
TreeMap<Long, Event> schedule = new TreeMap<>();
schedule.put(timestamp1, event1);
schedule.put(timestamp2, event2);

// Find next event after NOW:
Map.Entry<Long, Event> next = schedule.ceilingEntry(System.currentTimeMillis());

// Find all events in next hour:
long now  = System.currentTimeMillis();
long hour = now + 3_600_000;
SortedMap<Long, Event> upcoming = schedule.subMap(now, hour);
```

### 4. Linux CFS Scheduler — Red-Black Tree

The Linux Completely Fair Scheduler uses a Red-Black Tree to manage runnable processes:

```
Key: virtual runtime (vruntime) of each process
Operation: pick next process = leftmost node (min vruntime) — O(log n)
After process runs: update vruntime, reinsert — O(log n)
Add new process: insert — O(log n)
Remove finished: delete — O(log n)
```

Using a sorted array or linked list would be O(n) for insertion. The BST gives O(log n) for all operations — critical when scheduling thousands of processes per second.

### 5. Symbol Tables in Compilers

```
When compiler sees: int x = 5; float y = 3.14; String name = "Navaneeth";
It inserts each identifier into a symbol table (BST or hash table):

  BST symbol table:
       name
      /    \
    int     y
      \
       x

lookup("y") → O(log n)
Insert new identifier → O(log n)
Iterate sorted (for symbol listings) → O(n) in-order
```

---

## Topic 7 — Interview-Level Problems

### Problem 1: Validate BST — O(n) time, O(h) space

**Wrong approach:**
```java
// ❌ Only checks immediate children — misses grandchild violations
boolean isValid(Node node) {
    if (node == null) return true;
    if (node.left  != null && node.left.val  >= node.val) return false;
    if (node.right != null && node.right.val <= node.val) return false;
    return isValid(node.left) && isValid(node.right);
}
```

**Counter-example to wrong approach:**
```
     10
    /  \
   5   15
  / \
 3  12     ← 12 > 10! This tree IS invalid but wrong approach says valid
```

**Correct approach:** pass valid range `(min, max)` to each node.

```java
boolean isValidBST(Node node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return isValidBST(node.left,  min,      node.val)
        && isValidBST(node.right, node.val, max);
}
// Initial call: isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE)
```

For node 12 in the example: `isValidBST(12, Long.MIN_VALUE, 10)` — 12 >= 10 → false! Caught.

**Why `Long` instead of `Integer`?** If a node's value is `Integer.MAX_VALUE`, comparing with `Integer.MAX_VALUE` would falsely exclude it. Using `Long` gives headroom.

---

### Problem 2: Kth Smallest Element — O(n) time, O(h) space

**Key insight:** In-order traversal gives sorted order. Count until kth element.

```java
// Iterative in-order (avoids recursion limit)
int kthSmallest(Node root, int k) {
    Deque<Node> stack = new ArrayDeque<>();
    Node curr = root;
    int count = 0;

    while (curr != null || !stack.isEmpty()) {
        while (curr != null) { stack.push(curr); curr = curr.left; }
        curr = stack.pop();
        if (++count == k) return curr.val;   // k-th node visited
        curr = curr.right;
    }
    return -1;
}
```

**Optimized O(log n):** Augment each BST node with `subtreeSize`. Then at each node: if `leftSize + 1 == k` → current is the answer. If `k <= leftSize` → go left. Else → go right with `k -= leftSize + 1`. This avoids visiting all nodes.

---

### Problem 3: LCA in BST — O(log n) time, O(h) space

The BST property makes LCA much faster than in a general binary tree:

```java
Node lcaBST(Node root, int p, int q) {
    if (p < root.val && q < root.val) return lcaBST(root.left,  p, q); // both left
    if (p > root.val && q > root.val) return lcaBST(root.right, p, q); // both right
    return root;  // root is the SPLIT POINT = LCA
}
```

**Intuition:** The LCA is the first node where p and q are on different sides (or one IS the node). If both are less than root, their LCA must be in the left subtree. If both are greater, it's in the right subtree. Otherwise, root splits them.

```
LCA(20, 40) in BST {10,20,25,30,35,40,50,60,70,80}:
  Root=50: both 20 and 40 < 50 → go left
  Root=30: 20 < 30 but 40 > 30 → SPLIT POINT → LCA = 30  ✓
```

This is O(log n) vs O(n) for general binary trees.

---

### Problem 4: Sorted Array to Balanced BST — O(n) time, O(n) space

**Key insight:** always pick the **middle element** as root. This guarantees the resulting tree has minimum possible height (perfectly balanced).

```java
Node sortedArrayToBST(int[] nums, int lo, int hi) {
    if (lo > hi) return null;
    int mid = lo + (hi - lo) / 2;
    Node node = new Node(nums[mid]);         // middle = root
    node.left  = sortedArrayToBST(nums, lo,    mid - 1);  // left half
    node.right = sortedArrayToBST(nums, mid + 1, hi);     // right half
    return node;
}
```

**For [1,2,3,4,5,6,7]:**
```
mid=3 (val=4) → root
Left  [1,2,3]: mid=1 (val=2) → left child of 4
Right [5,6,7]: mid=5 (val=6) → right child of 4
...

Result:
      4
     / \
    2   6
   / \ / \
  1  3 5  7    height=3 = log₂(7) ✓
```

---

### Problem 5: BST to Greater Sum Tree (GST) — O(n) time, O(h) space

**Problem:** Each node's value becomes the sum of all values ≥ it (including itself).

**Key insight:** Process nodes in **reverse in-order** (Right → Root → Left) = descending order. Maintain a running sum.

```java
int runningSum = 0;
void toGST(Node node) {
    if (node == null) return;
    toGST(node.right);          // larger values first
    runningSum += node.val;     // add current to sum
    node.val = runningSum;      // replace with cumulative sum
    toGST(node.left);           // smaller values later
}
```

```
BST: {1,2,3,4,5,6,7}
Reverse in-order: 7, 6, 5, 4, 3, 2, 1

Process 7: sum=7,  7→7
Process 6: sum=13, 6→13
Process 5: sum=18, 5→18
Process 4: sum=22, 4→22
Process 3: sum=25, 3→25
Process 2: sum=27, 2→27
Process 1: sum=28, 1→28

GST result: {28,27,25,22,18,13,7}
```

---

### Problem 6: In-order Successor — O(log n) time, O(1) space

**In-order successor** = the node with the next larger value.

**Case A:** Node has a right subtree → successor = leftmost node in right subtree.

**Case B:** No right subtree → successor = last ancestor where we went LEFT.

```java
Node inorderSuccessor(Node root, Node target) {
    Node successor = null;
    while (root != null) {
        if (target.val < root.val) {
            successor = root;    // this could be the successor
            root = root.left;    // search for a closer one
        } else {
            root = root.right;   // target is not in left subtree of root
        }
    }
    return successor;
}
```

```
Successor of 25 in BST {20,25,30,35,40,50}:
  50: 25 < 50 → successor=50, go left
  30: 25 < 30 → successor=30, go left
  20: 25 > 20 → go right
  25: 25 == 25 → go right (none) → stop
Successor = 30  ✓ (last time we went left)
```

---

### Problem 7: Two Sum in BST — O(n) time, O(n) space

```java
boolean findTarget(Node root, int k) {
    List<Integer> sorted = new ArrayList<>();
    inOrder(root, sorted);           // O(n) — get sorted list
    int lo = 0, hi = sorted.size() - 1;
    while (lo < hi) {
        int sum = sorted.get(lo) + sorted.get(hi);
        if      (sum == k) return true;
        else if (sum < k)  lo++;
        else               hi--;
    }
    return false;
}
```

**Alternative O(n) space with iterators:** maintain two BST iterators — one doing in-order (ascending), one doing reverse in-order (descending). Advance them like two pointers without materializing the full sorted array.

---

### Problem 8: Recover BST — O(n) time, O(h) space

Two nodes were accidentally swapped, breaking the BST property. Find and swap them back.

**Key insight:** In-order traversal of a broken BST shows exactly two "inversions" (places where prev > curr).

```
Broken BST in-order: [1, 3, 2, 4]  (3 and 2 are swapped)
                          ↑   ↑
                    first=3  second=2

First anomaly:  prev=3 > curr=2 → first = 3 (the culprit)
Second anomaly: same pair        → second = 2

Fix: swap first.val and second.val → [1, 2, 3, 4]  ✓
```

There can be two anomalies (nodes are far apart) or just one (nodes are adjacent in in-order).

---

### Problem 9: Balance a BST — O(n) time, O(n) space

```java
Node balanceBST(Node root) {
    List<Integer> sorted = new ArrayList<>();
    inOrder(root, sorted);                         // O(n) sorted list
    return sortedArrayToBST(sorted, 0, sorted.length - 1);  // O(n) rebuild
}
```

---

### Problem 10: Merge Two BSTs — O(m + n) time, O(m + n) space

```java
Node mergeBSTs(Node root1, Node root2) {
    List<Integer> list1 = inOrder(root1);          // O(m) sorted
    List<Integer> list2 = inOrder(root2);          // O(n) sorted
    List<Integer> merged = mergeSorted(list1, list2); // O(m+n) merge
    return sortedArrayToBST(merged, 0, merged.size()-1); // O(m+n) rebuild
}
```

This produces a **balanced** merged BST. Naive approach (insert all of BST2 into BST1) gives O(n log(m+n)) and may be unbalanced.

---

## Interview Complexity Cheat Sheet

| Problem | Time | Space | Key Technique |
|---------|------|-------|---------------|
| Validate BST | O(n) | O(h) | Range [min,max] propagation |
| Kth smallest | O(n) | O(h) | In-order counter |
| Kth smallest (optimal) | O(log n) | O(h) | Augmented BST subtree sizes |
| LCA in BST | O(log n) | O(h) | BST ordering split |
| Sorted array → BST | O(n) | O(n) | Pick middle as root |
| BST to GST | O(n) | O(h) | Reverse in-order + running sum |
| In-order successor | O(log n) | O(1) | Track last-go-left ancestor |
| Two sum in BST | O(n) | O(n) | In-order + two pointers |
| Recover BST | O(n) | O(h) | Find in-order anomalies |
| Balance BST | O(n) | O(n) | In-order → array → rebuild |
| Merge two BSTs | O(m+n) | O(m+n) | In-order each → merge → rebuild |

---

## Common Mistakes to Avoid

```java
// ❌ 1. BST validation: checking only direct children
if (node.left.val < node.val)  // ignores grandchild violations!
// ✅ Pass range down:
isValidBST(node, Long.MIN_VALUE, Long.MAX_VALUE)

// ❌ 2. Treating null height as -1 inconsistently
int h = height(null);   // returns -1 in one place, 0 in another → wrong balance calc
// ✅ Pick ONE convention: 0 for null (counting nodes) or -1 (counting edges)

// ❌ 3. Delete: forgetting the in-order successor case
if (node.left == null || node.right == null)
    return node.left != null ? node.left : node.right;
// Missing: what if BOTH children exist?
// ✅ Handle all 3 cases explicitly

// ❌ 4. Inserting sorted data into plain BST
for (int i = 1; i <= 1000000; i++) bst.insert(i);  // O(n²) total — skewed tree!
// ✅ Shuffle first OR use TreeMap (Red-Black Tree)

// ❌ 5. Floor/ceiling: off-by-one
if (key < node.val) return floor(node.left, key);  // correct — floor must be < node.val
if (key > node.val) return floor(node.left, key);  // ❌ wrong direction!
// ✅ key > node.val → try right subtree, fall back to node

// ❌ 6. LCA: using general binary tree O(n) algorithm for BST
// BST LCA is O(log n) using ordering — always use the specialized version
lcaBST(root, p, q)    // ✅ O(log n) — exploit BST property
lcaGeneral(root, p, q) // ❌ O(n) — doesn't use BST property
```

---

## The 7 Golden Rules

```
1. Always insert random data — sorted insertion degrades BST to O(n) linked list
2. Validate BST with range [min,max] propagation, NOT local child comparison
3. In-order ALWAYS gives sorted output — foundation for kth smallest, recovery, two sum
4. LCA in BST: O(log n) using ordering (not O(n) like general binary tree)
5. Build balanced BST from sorted array: always pick the MIDDLE element as root
6. Reverse in-order (R→Root→L) = descending sort — key for GST and reverse problems
7. Use Java TreeMap/TreeSet in production — Red-Black Tree guarantees O(log n) always
```
