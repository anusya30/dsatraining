# Trees — Complete Deep Dive in Java

---

## How to Run

```bash
javac Trees.java
java Trees
```

> Requires Java 8 or higher. Check with `java -version`.

---

## File Structure

```
Trees.java
│
├── TreeNode                     → val, left, right
│
├── BST                          → Binary Search Tree
│   ├── insert()                 → O(log n) avg
│   ├── search()                 → O(log n) avg
│   ├── delete()                 → O(log n) avg, 3 cases
│   └── findMin()                → O(log n)
│
├── preOrder / inOrder / postOrder → Recursive DFS O(n)
├── levelOrder()                   → BFS with queue O(n)
│
├── preOrderIterative()            → Stack: push right then left
├── inOrderIterative()             → Classic curr + stack pattern
├── postOrderIterative()           → Two-stack trick
│
├── height() / size()              → Tree properties O(n)
├── isBalanced()                   → O(n) with -1 sentinel trick
│
├── maxDepth()                     → Problem 1
├── diameter()                     → Problem 2
├── isSymmetric()                  → Problem 3
├── lcaBinaryTree() / lcaBST()     → Problem 4
├── hasPathSum() / pathSumAll()    → Problem 5
├── isValidBST()                   → Problem 6
├── rightSideView()                → Problem 7
├── serialize() / deserialize()    → Problem 8
├── buildTree()                    → Problem 9
├── zigzagLevelOrder()             → Problem 10
│
└── main()                         → Runs all 7 topics
```

---

## Topic 1 — Tree Fundamentals & Terminology

### What is a Tree?

A **Tree** is a hierarchical, non-linear data structure. Unlike arrays and linked lists which are sequential, trees **branch** — each node can connect to multiple children.

**Formal definition:** A tree with `n` nodes has exactly `n-1` edges. There is exactly **one path** between any two nodes. No cycles. No backward connections between levels.

### Visual Terminology Guide

```
             [A]         ← ROOT — top node, no parent
            /   \
          [B]   [C]      ← INTERNAL NODES — have children
         / \     \
       [D] [E]   [F]     ← LEAVES — no children

Edge:    The line connecting A to B (or any parent to child)
Subtree: [B] together with [D] and [E] forms B's subtree
```

| Term | Definition | Example above |
|------|-----------|---------------|
| Root | Top node, no parent | A |
| Parent | Node with children below | A is parent of B,C |
| Child | Direct descendant | B,C are children of A |
| Siblings | Same parent | B and C are siblings |
| Leaf | No children | D, E, F |
| Internal node | Has at least one child | A, B, C |
| Height | Longest root-to-leaf path | 3 (A→B→D) |
| Depth of node | Path length from root | depth(D)=2, depth(A)=0 |
| Level | All nodes at same depth | Level 0={A}, Level 1={B,C} |
| Degree | Number of children | degree(A)=2, degree(D)=0 |
| Subtree | Node + all descendants | B's subtree = {B,D,E} |

### Types of Binary Trees

```
Perfect:              Complete:            Skewed:
    [1]                   [1]               [1]
   /   \                 /   \               \
 [2]   [3]            [2]   [3]             [2]
 / \   / \            / \   /                \
[4][5][6][7]        [4][5][6]                [3]
                                              \
All levels full     Last level          Degenerated to
n = 2^h - 1         left-filled         linked list!
```

| Type | Property | Height | Use Case |
|------|----------|--------|----------|
| Perfect | All levels full | log n | Theoretical analysis |
| Complete | Last level left-filled | log n | Binary Heap |
| Balanced | \|left_h - right_h\| ≤ 1 | log n | AVL, Red-Black |
| Skewed | One child per node | n | Worst case BST |

### Why Trees Over Arrays/Lists?

```
Arrays:       O(1) access, O(n) insert, requires sorted for O(log n) search
Linked List:  O(n) everything — no structure
BST:          O(log n) search+insert+delete when balanced
Hierarchy:    File systems, DOM, org charts are naturally trees
```

---

## Topic 2 — Binary Tree Structure

### Node in Memory

```java
class TreeNode {
    int      val;    // 4 bytes
    TreeNode left;   // 8 bytes (reference)
    TreeNode right;  // 8 bytes (reference)
}
// Total: ~24 bytes per node (+ 16-byte object header in JVM)
```

### BST Property

At **every** node in a BST:
```
ALL values in left subtree  < node.val
ALL values in right subtree > node.val
```

This must hold **for every node**, not just the root. The common mistake is checking only the immediate children:

```
         10
        /  \
       5   15
      / \
     3   12     ← 12 > 10! Violates BST property even though 12 > 5
```

This tree looks locally valid (12 > 5) but is globally invalid (12 > 10, so it can't be in the left subtree of 10).

### BST Insert — How It Works

```
Insert 45 into:       50
                     /  \
                   30    70
                  /  \   / \
                 20  40 60  80

45 < 50 → go left
45 > 30 → go right
45 > 40 → go right
null found → insert here!

Result:
                     50
                    /  \
                  30    70
                 /  \   / \
                20  40 60  80
                      \
                      45
```

### BST Delete — Three Cases

**Case 1: Leaf node** — simply remove it.

**Case 2: One child** — replace the node with its single child.

**Case 3: Two children** — replace node's value with its **in-order successor** (smallest value in right subtree), then delete the successor.

```
Delete 30 from BST (Case 3 — two children):

Before:          After:
   50               50
  /  \             /  \
 30   70    →     40   70
/  \  / \        /    / \
20 40 60 80      20   60 80

In-order successor of 30 = 40 (min of right subtree)
Copy 40 into 30's position, delete original 40
```

---

## Topic 3 — Traversals (DFS & BFS)

### The Four Traversals

```
Tree:
        1
       / \
      2   3
     / \   \
    4   5   6
```

| Traversal | Order | Result | Key Use |
|-----------|-------|--------|---------|
| Pre-order | Root→Left→Right | `1,2,4,5,3,6` | Copy/clone tree |
| In-order | Left→Root→Right | `4,2,5,1,3,6` | BST → sorted output |
| Post-order | Left→Right→Root | `4,5,2,6,3,1` | Delete tree, folder size |
| Level-order | Level by level | `[1],[2,3],[4,5,6]` | BFS, shortest path |

### Pre-order (Root First)

```java
void preOrder(TreeNode node) {
    if (node == null) return;
    visit(node);          // ROOT first
    preOrder(node.left);
    preOrder(node.right);
}
```

**Call stack trace for tree above:**
```
preOrder(1) → visit 1
  preOrder(2) → visit 2
    preOrder(4) → visit 4
      preOrder(null) → return
      preOrder(null) → return
    preOrder(5) → visit 5
      ...
  preOrder(3) → visit 3
    ...
Result: 1, 2, 4, 5, 3, 6
```

**Why it's called "pre":** the node is visited **before** its children. The root's value is always the first in pre-order.

### In-order (Root in Middle)

```java
void inOrder(TreeNode node) {
    if (node == null) return;
    inOrder(node.left);
    visit(node);          // ROOT in middle
    inOrder(node.right);
}
```

**Critical property: in-order traversal of a BST always gives sorted ascending output.** This is used for:
- Printing all BST values sorted
- Verifying a tree is a valid BST
- Finding the kth smallest element

### Post-order (Root Last)

```java
void postOrder(TreeNode node) {
    if (node == null) return;
    postOrder(node.left);
    postOrder(node.right);
    visit(node);          // ROOT last
}
```

**Why post-order for deletion/size calculation:**

```
Folder sizes (post-order):
  calculate size of /home/navaneeth/projects  ← children first
  calculate size of /home/navaneeth/documents ← children first
  calculate size of /home/navaneeth/          ← then parent = sum of children
  calculate size of /home/                    ← then grandparent
```

Each parent's size depends on its children's sizes → children must be processed first → **post-order**.

### Level-Order (BFS)

```java
Queue<TreeNode> queue = new LinkedList<>();
queue.offer(root);

while (!queue.isEmpty()) {
    int levelSize = queue.size();        // key: snapshot current level
    for (int i = 0; i < levelSize; i++) {
        TreeNode node = queue.poll();
        // process node
        if (node.left  != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
}
```

The `levelSize` snapshot is the critical pattern that separates **which nodes belong to which level**.

---

## Topic 4 — Time & Space Complexity

### All Traversals — O(n) Time

Every traversal visits each of the `n` nodes exactly **once**. One visit = O(1) work. Total = O(n).

### Space: It Depends on the Tree Shape

```
Balanced tree (h = log n):
  DFS recursion stack depth = h = log n → O(log n) space

Skewed tree (h = n):
  DFS recursion stack depth = h = n   → O(n) space → risk of StackOverflowError!

BFS queue:
  Max queue size = max width of tree
  Perfect tree max width = n/2 (last level) → O(n) space
  Skewed tree max width = 1              → O(1) space
```

### BST Operations Summary

| Operation | Balanced | Skewed | Fix |
|-----------|----------|--------|-----|
| Search | O(log n) | O(n) | Use AVL or Red-Black |
| Insert | O(log n) | O(n) | Use AVL or Red-Black |
| Delete | O(log n) | O(n) | Use AVL or Red-Black |
| Min/Max | O(log n) | O(n) | Use AVL or Red-Black |

**Java's `TreeMap` and `TreeSet` use Red-Black Trees** — always O(log n) regardless of insertion order.

### Height and Node Count

```
Perfect tree:   h = ⌊log₂(n)⌋     n = 2^(h+1) - 1
Balanced tree:  h = O(log n)
Skewed tree:    h = n - 1

For n = 1,000 nodes:
  Balanced: h ≈ 10 operations
  Skewed:   h = 999 operations
  → Balance gives 100× speedup
```

### Recursion Stack vs. Heap Stack

```java
// Recursive DFS — uses JVM call stack (limited ~10,000 frames)
void dfs(TreeNode node) {
    if (node == null) return;
    dfs(node.left);   // each call = one stack frame
    dfs(node.right);
}

// Iterative DFS — uses explicit stack on heap (unlimited)
void dfsIterative(TreeNode root) {
    Deque<TreeNode> stack = new ArrayDeque<>();  // heap — no limit
    stack.push(root);
    while (!stack.isEmpty()) { ... }
}
```

---

## Topic 5 — Recursive vs Iterative Approaches

### Iterative Pre-order — Why Push Right Before Left?

```java
stack.push(root);
while (!stack.isEmpty()) {
    TreeNode node = stack.pop();
    result.add(node.val);
    if (node.right != null) stack.push(node.right);  // push RIGHT first
    if (node.left  != null) stack.push(node.left);   // push LEFT last
}
```

Stack is LIFO. If we push right first, left is on top → left is popped and processed first. This matches the pre-order "left before right" property.

### Iterative In-order — The `curr` Pointer Pattern

```java
TreeNode curr = root;
while (curr != null || !stack.isEmpty()) {
    while (curr != null) {    // go as far LEFT as possible
        stack.push(curr);
        curr = curr.left;
    }
    curr = stack.pop();       // visit the leftmost unvisited
    result.add(curr.val);
    curr = curr.right;        // move to right subtree
}
```

**Trace for tree `1(2(4,5),3(null,6))`:**
```
curr=1: push 1, curr=2
curr=2: push 2, curr=4
curr=4: push 4, curr=null
curr=null: pop 4, visit 4, curr=null (4.right=null)
curr=null: pop 2, visit 2, curr=5 (2.right=5)
curr=5: push 5, curr=null
curr=null: pop 5, visit 5, curr=null
curr=null: pop 1, visit 1, curr=3 (1.right=3)
...
Result: 4, 2, 5, 1, 3, 6  ✓ (sorted for BST)
```

### Iterative Post-order — Two-Stack Trick

```java
// Stack 1 produces: root, right, left (reverse of post-order)
// Stack 2 reverses: left, right, root (= post-order)
stack1.push(root);
while (!stack1.isEmpty()) {
    TreeNode node = stack1.pop();
    stack2.push(node);
    if (node.left  != null) stack1.push(node.left);   // push left (right popped first)
    if (node.right != null) stack1.push(node.right);  // push right (popped into stack2 first)
}
// stack2 now has post-order in reverse
while (!stack2.isEmpty()) result.add(stack2.pop().val);
```

### When to Choose Which

| Scenario | Choice | Reason |
|----------|--------|--------|
| Interview, clean code | **Recursive** | Mirrors the problem structure, less code |
| Very deep/skewed tree | **Iterative** | No stack overflow risk |
| Production code | **Iterative** | Predictable memory usage |
| Asked "without recursion" | **Iterative** | Explicit requirement |
| Pre-order / Post-order | Both work well | Iterative versions are straightforward |
| In-order | **Recursive** preferred | Iterative in-order is non-trivial to reason about |

---

## Topic 6 — Real-World Applications

### 1. File System

Every OS stores files in a tree:
```
/ (root)
├── home/
│   └── navaneeth/
│       ├── projects/
│       └── documents/
└── usr/
    └── local/
```

- `ls -R`: **pre-order** traversal (parent before children)
- `du` (disk usage): **post-order** traversal (sum children sizes before parent)
- `find . -name "*.java"`: DFS with pruning

### 2. HTML DOM

```
html
├── head
│   └── title
└── body
    ├── div
    │   ├── p
    │   └── span
    └── footer
```

- `document.getElementById("id")`: BFS or DFS search O(n)
- CSS selector matching `body > div > p`: path traversal O(depth)
- React reconciliation (Virtual DOM diffing): compares two trees O(n)

### 3. Expression Trees

Compilers parse `(3 + 4) * (5 - 2)` into:

```
        *
       / \
      +   -
     / \ / \
    3  4 5  2
```

- **In-order** → `3 + 4 * 5 - 2` (infix — ambiguous without parens!)
- **Pre-order** → `* + 3 4 - 5 2` (prefix/Polish notation)
- **Post-order** → `3 4 + 5 2 - *` (postfix/RPN — evaluates with a stack)

### 4. Database Indexes

MySQL InnoDB uses **B-Trees** (generalized BST where nodes hold multiple keys):

```
Equality query: SELECT * WHERE id = 50
→ B-Tree search: O(log n) — binary search within each node

Range query: SELECT * WHERE salary BETWEEN 40000 AND 80000
→ Find lower bound: O(log n)
→ In-order scan from there: O(k) where k = results
→ Total: O(log n + k)
```

Hash indexes are faster for equality (O(1)) but can't do range queries.

### 5. Trie (Prefix Tree)

```
Insert: "app", "apple", "application"

root → [a] → [p] → [p*]                  (* = end of word "app")
                 → [l] → [e*]             (end of "apple")
                       → [i] → [c] → ... → [n*]  (end of "application")
```

- Search prefix: O(k) where k = prefix length — independent of total words stored
- Used in: Google autocomplete, IDE code completion, spell checkers, IP routing tables

### 6. Java TreeMap/TreeSet — Red-Black Tree

```java
TreeMap<String, Integer> map = new TreeMap<>();
// Keys always sorted
// All operations: O(log n) guaranteed (self-balancing Red-Black Tree)

map.firstKey()           // minimum key
map.lastKey()            // maximum key
map.floorKey("cherry")   // largest key ≤ "cherry"
map.ceilingKey("cherry") // smallest key ≥ "cherry"
map.subMap("a", "d")     // keys in ["a","d") range
```

Use `TreeMap` over `HashMap` when you need **sorted iteration** or **range queries**.

---

## Topic 7 — Interview-Level Problems

### Problem 1: Max Depth — O(n) time, O(h) space

```java
int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

The recursion naturally computes depth bottom-up. At each node: depth = 1 (for this node) + max depth of either subtree.

---

### Problem 2: Diameter of Binary Tree — O(n) time, O(h) space

**Diameter** = longest path between any two nodes. The path **may not pass through the root**.

```
    1
   / \
  2   3
 / \
4   5

Diameter = 4→2→1→3 = 3 edges (not through root of left subtree)
But also: 4→2→5 = 2 edges
Max = 3
```

**Key insight:** At each node, the longest path through it = `leftHeight + rightHeight`. Track global maximum.

```java
int diameterHelper(TreeNode node) {
    if (node == null) return 0;
    int left  = diameterHelper(node.left);
    int right = diameterHelper(node.right);
    maxDiameter = Math.max(maxDiameter, left + right);  // path through this node
    return 1 + Math.max(left, right);   // height returned to parent
}
```

**Common mistake:** computing `height(root.left) + height(root.right)` at the root only. This misses diameters that don't pass through the root. You must check at **every node**.

---

### Problem 3: Symmetric Tree — O(n) time, O(h) space

**Mirror comparison:** left.left must equal right.right (outer pair), AND left.right must equal right.left (inner pair).

```java
boolean isMirror(TreeNode left, TreeNode right) {
    if (left == null && right == null) return true;   // both null = symmetric
    if (left == null || right == null) return false;  // one null = asymmetric
    return left.val == right.val
        && isMirror(left.left,  right.right)   // outer pair
        && isMirror(left.right, right.left);   // inner pair
}
```

```
Symmetric:           Asymmetric:
    1                    1
   / \                  / \
  2   2                2   2
 / \ / \                \   \
3  4 4  3               3   3
```

---

### Problem 4: Lowest Common Ancestor — O(log n) BST, O(n) general BT

**For BST:** use ordering property.
```java
if (p < root.val && q < root.val) return lcaBST(root.left,  p, q);  // both left
if (p > root.val && q > root.val) return lcaBST(root.right, p, q);  // both right
return root;  // p and q on different sides = split point = LCA
```

**For general Binary Tree:** post-order traversal.
```java
TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left  = lca(root.left,  p, q);
    TreeNode right = lca(root.right, p, q);
    if (left != null && right != null) return root;  // p on left, q on right
    return left != null ? left : right;               // both on same side
}
```

**Trace:** if both p and q are in the left subtree, the recursive call returns their LCA. The right returns null. So we return the left result (their true LCA) to the parent.

---

### Problem 5: Path Sum — O(n) time, O(h) space

**Single path check:** subtract root value from target at each step. At a leaf, check if remaining equals zero.

```java
boolean hasPathSum(TreeNode root, int target) {
    if (root == null) return false;
    if (root.left == null && root.right == null) return root.val == target;
    return hasPathSum(root.left,  target - root.val)
        || hasPathSum(root.right, target - root.val);
}
```

**All paths (backtracking):** add node to path, recurse, **remove it before returning** (backtrack).

```java
void dfs(TreeNode node, int remain, List<Integer> path, List<List<Integer>> result) {
    path.add(node.val);
    if (isLeaf(node) && remain == node.val) result.add(new ArrayList<>(path));
    dfs(node.left,  remain - node.val, path, result);
    dfs(node.right, remain - node.val, path, result);
    path.remove(path.size() - 1);  // ← BACKTRACK
}
```

The backtrack step is essential — without it, `path` accumulates all visited nodes instead of just the current root-to-leaf path.

---

### Problem 6: Validate BST — O(n) time, O(h) space

**Wrong approach:** only check `node.left.val < node.val < node.right.val`. This misses cross-subtree violations.

**Correct approach:** pass valid range `[min, max]` down to each node.

```java
boolean isValidBST(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return isValidBST(node.left,  min,      node.val)  // left: must be < node.val
        && isValidBST(node.right, node.val, max);       // right: must be > node.val
}
// Call: isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE)
```

Using `Long.MIN_VALUE / Long.MAX_VALUE` handles edge cases where node values equal `Integer.MIN_VALUE` or `Integer.MAX_VALUE`.

---

### Problem 7: Right Side View — O(n) time, O(n) space

BFS level-order, take the **last node** at each level:

```java
if (i == levelSize - 1) result.add(node.val);  // last at this level = visible from right
```

```
    1          → see 1
   / \
  2   3        → see 3 (rightmost at level 1)
 / \   \
4   5   6      → see 6 (rightmost at level 2)

Right side view: [1, 3, 6]
```

---

### Problem 8: Serialize & Deserialize — O(n) time and space

**Serialize (pre-order with nulls):**
```
Tree:         1
             / \
            2   3
           / \
          4   5

Serialized: "1,2,4,null,null,5,null,null,3,null,null"
```

**Deserialize:** read next token from serialized string. If "null", return null. Otherwise create node, recursively build left then right.

Using a global index (or Queue of tokens) to track current position.

---

### Problem 9: Build Tree from Pre-order + In-order — O(n)

```
Pre-order: [3, 9, 20, 15, 7]   (first element = ROOT)
In-order:  [9, 3, 15, 20, 7]   (root splits left/right subtrees)

pre[0] = 3 = ROOT
Find 3 in in-order: index 1
Left subtree in-order:  [9]         (indices 0..0)
Right subtree in-order: [15, 20, 7] (indices 2..4)
Left subtree pre-order: [9]         (pre indices 1..1)
Right subtree pre-order:[20, 15, 7] (pre indices 2..4)

Recurse!
```

Use a **HashMap** to look up in-order index in O(1) instead of linear scan → overall O(n) vs O(n²).

---

### Problem 10: Zigzag Level Order — O(n) time, O(n) space

```java
boolean leftToRight = true;
for each level:
    int[] level = new int[levelSize];
    for (int i = 0; i < levelSize; i++) {
        int idx = leftToRight ? i : levelSize - 1 - i;
        level[idx] = node.val;
    }
    leftToRight = !leftToRight;
```

By pre-allocating a fixed-size array and computing the target index based on direction, each node is placed in O(1) without needing a second pass.

---

## Interview Complexity Cheat Sheet

| Problem | Time | Space | Key Technique |
|---------|------|-------|---------------|
| Max depth | O(n) | O(h) | Recursion: 1 + max(L,R) |
| Diameter | O(n) | O(h) | leftH + rightH at every node |
| Is symmetric | O(n) | O(h) | Mirror comparison |
| LCA (BST) | O(log n) | O(h) | BST ordering |
| LCA (BT) | O(n) | O(h) | Post-order returns |
| Path sum | O(n) | O(h) | Subtract + leaf check |
| All paths | O(n) | O(h) | DFS + backtrack |
| Validate BST | O(n) | O(h) | Range [min,max] propagation |
| Right side view | O(n) | O(n) | BFS last at each level |
| Serialize/Deserialize | O(n) | O(n) | Pre-order + null markers |
| Build from traversals | O(n) | O(n) | HashMap + pre[0]=root |
| Zigzag level order | O(n) | O(n) | BFS + direction toggle |

---

## Common Mistakes to Avoid

```java
// ❌ 1. BST validation: only checking immediate children
if (node.left.val < node.val && node.right.val > node.val)
    return true; // WRONG — misses grandchildren violations!
// ✅ Pass range down:
isValidBST(node, Long.MIN_VALUE, Long.MAX_VALUE);

// ❌ 2. Diameter: only computing at root
int diameter = height(root.left) + height(root.right); // WRONG
// ✅ Check at EVERY node:
int diameter = diameterHelper(root); // updates global max at each node

// ❌ 3. Path sum: forgetting to check for LEAF specifically
if (remain == 0) return true;  // WRONG — triggers at non-leaf nodes too!
// ✅ Check leaf:
if (root.left == null && root.right == null) return remain == root.val;

// ❌ 4. Building tree: linear scan in in-order array → O(n²)
for (int i = 0; i < inorder.length; i++)
    if (inorder[i] == root.val) rootIdx = i;  // O(n) per node!
// ✅ Pre-build HashMap → O(1) lookup:
Map<Integer, Integer> map = new HashMap<>();
for (int i = 0; i < inorder.length; i++) map.put(inorder[i], i);

// ❌ 5. Forgetting to backtrack in path problems
path.add(node.val);
dfs(node.left, ...);
dfs(node.right, ...);
// Missing: path.remove(path.size() - 1);  ← path grows without bound!

// ❌ 6. Height of null: should return 0 (or -1 for edges vs nodes)
int height(TreeNode node) {
    if (node == null) return -1;  // counting edges
    if (node == null) return 0;   // counting nodes ← more common
    return 1 + Math.max(height(node.left), height(node.right));
}
// Be consistent: pick one convention and stick to it
```

---

## The 7 Golden Rules

```
1. DFS = Stack or Recursion.   BFS = Queue. Never mix them up.
2. BST in-order → always sorted. Use for kth element, range queries.
3. Validate BST with range [min,max] propagation, NOT local child check.
4. LCA: post-order returns first node where both p and q are found.
5. Diameter = max(leftH + rightH) tracked at EVERY node, not just root.
6. Path problems: always backtrack (remove from path) after visiting a subtree.
7. Balance matters: skewed BST gives O(n) operations. Use TreeMap for guaranteed O(log n).
```
