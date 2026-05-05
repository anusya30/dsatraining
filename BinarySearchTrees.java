// ================================================================
//   BINARY SEARCH TREES — Complete Deep Dive in Java
// ================================================================
//   Compile:  javac BinarySearchTrees.java
//   Run:      java BinarySearchTrees
// ================================================================
//
//   TOPICS:
//   1. BST Fundamentals
//   2. BST Properties
//   3. Search, Insert, Delete
//   4. Complexity Analysis
//   5. Balanced vs Skewed Trees
//   6. Real-World Applications
//   7. Interview-Level Problems
// ================================================================

import java.util.*;

public class BinarySearchTrees {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    static void sub(String title) {
        System.out.println("\n  ── " + title + " ──");
    }


    // ============================================================
    // NODE DEFINITION
    // ============================================================
    static class Node {
        int  val;
        Node left, right;
        int  height; // used by AVL tree

        Node(int val) {
            this.val    = val;
            this.height = 1;
        }
    }


    // ============================================================
    // CORE BST — Full Implementation with all operations
    // ============================================================
    static class BST {
        Node root;

        // ──────────────────────────────────────────────────────
        // INSERT — O(log n) average, O(n) worst
        //
        // Process:
        //   1. Start at root
        //   2. If val < current → go left
        //   3. If val > current → go right
        //   4. If null found   → insert here
        //   5. Duplicates ignored (BST property: strict inequality)
        //
        // Trace for insert(45) into BST with root 50:
        //   50 → 45 < 50 → go left
        //   30 → 45 > 30 → go right
        //   40 → 45 > 40 → go right
        //   null → insert Node(45) here ✓
        // ──────────────────────────────────────────────────────
        Node insert(Node node, int val) {
            if (node == null) return new Node(val);     // base: insert here
            if      (val < node.val) node.left  = insert(node.left,  val);
            else if (val > node.val) node.right = insert(node.right, val);
            // val == node.val → duplicate, ignore
            return node;
        }

        void insert(int val) { root = insert(root, val); }

        // ──────────────────────────────────────────────────────
        // SEARCH — O(log n) average, O(n) worst
        //
        // BST property guarantees: if target < node → it CAN ONLY
        // be in the left subtree. No need to search right. This is
        // what makes BST search O(log n) instead of O(n) like a
        // plain binary tree.
        //
        // Trace for search(60) in BST with root 50:
        //   50: 60 > 50 → go right
        //   70: 60 < 70 → go left
        //   60: found! ✓   (only 3 comparisons for 7-node tree)
        // ──────────────────────────────────────────────────────
        Node search(Node node, int val) {
            if (node == null || node.val == val) return node;
            if (val < node.val) return search(node.left,  val);
            else                return search(node.right, val);
        }

        Node search(int val) { return search(root, val); }

        // ──────────────────────────────────────────────────────
        // DELETE — O(log n) average, O(n) worst
        //
        // Three cases — each must preserve BST property:
        //
        // CASE 1: Node is a LEAF → simply remove it
        //   Before: ...→ [30] → [20*]   *=target
        //   After:  ...→ [30] → null
        //
        // CASE 2: Node has ONE CHILD → replace with that child
        //   Before: [50] → [30] → [20*] → [15]
        //   After:  [50] → [30] → [15]
        //
        // CASE 3: Node has TWO CHILDREN → replace val with
        //   in-order successor (smallest in right subtree),
        //   then delete that successor
        //   Before: [50] → [30(L=20,R=40*)] target=30
        //   InSuccessor: min(right subtree of 30) = 40
        //   Copy 40 into 30's position, delete original 40
        //   After: [50] → [40(L=20,R=null)]
        // ──────────────────────────────────────────────────────
        Node delete(Node node, int val) {
            if (node == null) return null;

            if      (val < node.val) node.left  = delete(node.left,  val);
            else if (val > node.val) node.right = delete(node.right, val);
            else {
                // Found the node to delete
                if (node.left  == null) return node.right; // Case 1 & 2: no left
                if (node.right == null) return node.left;  // Case 2: no right

                // Case 3: two children
                Node successor = findMin(node.right);   // in-order successor
                node.val   = successor.val;              // copy successor value
                node.right = delete(node.right, successor.val); // delete successor
            }
            return node;
        }

        void delete(int val) { root = delete(root, val); }

        // ──────────────────────────────────────────────────────
        // FIND MIN & MAX — O(log n) average
        // Min = leftmost node (keep going left until null)
        // Max = rightmost node (keep going right until null)
        // ──────────────────────────────────────────────────────
        Node findMin(Node node) {
            if (node == null) return null;
            while (node.left != null) node = node.left;
            return node;
        }

        Node findMax(Node node) {
            if (node == null) return null;
            while (node.right != null) node = node.right;
            return node;
        }

        int min() { return findMin(root).val; }
        int max() { return findMax(root).val; }

        // IN-ORDER → always returns sorted ascending sequence
        void inOrder(Node node, List<Integer> result) {
            if (node == null) return;
            inOrder(node.left,  result);
            result.add(node.val);
            inOrder(node.right, result);
        }

        List<Integer> inOrder() {
            List<Integer> r = new ArrayList<>();
            inOrder(root, r);
            return r;
        }

        // HEIGHT — O(n)
        int height(Node node) {
            if (node == null) return 0;
            return 1 + Math.max(height(node.left), height(node.right));
        }

        int height() { return height(root); }

        // COUNT NODES — O(n)
        int size(Node node) {
            if (node == null) return 0;
            return 1 + size(node.left) + size(node.right);
        }

        int size() { return size(root); }

        // FLOOR — largest value <= key — O(log n)
        // If key == node.val → exact match
        // If key < node.val  → floor must be in left subtree
        // If key > node.val  → floor could be node or in right subtree
        Node floor(Node node, int key) {
            if (node == null) return null;
            if (key == node.val) return node;
            if (key <  node.val) return floor(node.left, key);
            Node rightFloor = floor(node.right, key);
            return (rightFloor != null) ? rightFloor : node;
        }

        // CEILING — smallest value >= key — O(log n)
        Node ceiling(Node node, int key) {
            if (node == null) return null;
            if (key == node.val) return node;
            if (key >  node.val) return ceiling(node.right, key);
            Node leftCeiling = ceiling(node.left, key);
            return (leftCeiling != null) ? leftCeiling : node;
        }

        // KTH SMALLEST ELEMENT — O(n)
        // In-order traversal: kth element visited = kth smallest
        int[] kthSmallest(Node node, int k, int[] counter) {
            if (node == null) return null;
            int[] left = kthSmallest(node.left, k, counter);
            if (left != null) return left;
            counter[0]++;
            if (counter[0] == k) return new int[]{node.val};
            return kthSmallest(node.right, k, counter);
        }

        int kthSmallest(int k) {
            int[] counter = {0};
            int[] result  = kthSmallest(root, k, counter);
            return result != null ? result[0] : -1;
        }

        // COUNT NODES IN RANGE [lo, hi] — O(log n + k)
        int countInRange(Node node, int lo, int hi) {
            if (node == null) return 0;
            if (node.val < lo) return countInRange(node.right, lo, hi); // too small
            if (node.val > hi) return countInRange(node.left,  lo, hi); // too large
            return 1 + countInRange(node.left, lo, hi)
                     + countInRange(node.right, lo, hi);
        }
    }


    // ============================================================
    // AVL TREE — Self-Balancing BST
    // ============================================================
    //
    //   AVL Property: For every node,
    //   |height(left) - height(right)| <= 1  (balance factor -1, 0, or +1)
    //
    //   After every insert/delete, rebalance using ROTATIONS if needed.
    //
    //   FOUR ROTATION CASES:
    //   1. Left-Left (LL)    → Right rotation
    //   2. Right-Right (RR)  → Left rotation
    //   3. Left-Right (LR)   → Left rotation on left child, then Right rotation
    //   4. Right-Left (RL)   → Right rotation on right child, then Left rotation
    //
    //   ROTATIONS preserve BST property AND restore balance.
    //   All BST operations remain O(log n) GUARANTEED.
    // ============================================================
    static class AVL {
        Node root;

        int height(Node n)      { return n == null ? 0 : n.height; }
        int balanceFactor(Node n) {
            return n == null ? 0 : height(n.left) - height(n.right);
        }
        void updateHeight(Node n) {
            if (n != null)
                n.height = 1 + Math.max(height(n.left), height(n.right));
        }

        //       y                     x
        //      / \     rightRotate    / \
        //     x   T3   ─────────→  T1   y
        //    / \                        / \
        //  T1  T2                     T2  T3
        Node rightRotate(Node y) {
            Node x  = y.left;
            Node T2 = x.right;
            x.right = y;
            y.left  = T2;
            updateHeight(y);
            updateHeight(x);
            return x; // x is new root of this subtree
        }

        //     x                         y
        //    / \      leftRotate        / \
        //  T1   y     ─────────→      x   T3
        //      / \                   / \
        //    T2  T3                T1  T2
        Node leftRotate(Node x) {
            Node y  = x.right;
            Node T2 = y.left;
            y.left  = x;
            x.right = T2;
            updateHeight(x);
            updateHeight(y);
            return y; // y is new root of this subtree
        }

        Node balance(Node node) {
            updateHeight(node);
            int bf = balanceFactor(node);

            if (bf > 1) { // Left-heavy
                if (balanceFactor(node.left) < 0)
                    node.left = leftRotate(node.left);   // LR case
                return rightRotate(node);                // LL case
            }
            if (bf < -1) { // Right-heavy
                if (balanceFactor(node.right) > 0)
                    node.right = rightRotate(node.right); // RL case
                return leftRotate(node);                 // RR case
            }
            return node; // already balanced
        }

        Node insert(Node node, int val) {
            if (node == null) return new Node(val);
            if      (val < node.val) node.left  = insert(node.left,  val);
            else if (val > node.val) node.right = insert(node.right, val);
            else return node; // duplicate
            return balance(node);  // ← rebalance after insert
        }

        void insert(int val) { root = insert(root, val); }

        int height() {
            return root == null ? 0 : root.height;
        }

        void inOrder(Node node, List<Integer> res) {
            if (node == null) return;
            inOrder(node.left,  res);
            res.add(node.val);
            inOrder(node.right, res);
        }

        List<Integer> inOrder() {
            List<Integer> r = new ArrayList<>();
            inOrder(root, r);
            return r;
        }
    }


    // ============================================================
    // INTERVIEW PROBLEMS
    // ============================================================

    // ── PROBLEM 1: VALIDATE BST — O(n) time, O(h) space
    // Pass valid range [min, max] — node must be strictly inside.
    // Left subtree: range becomes (min, node.val)
    // Right subtree: range becomes (node.val, max)
    // Common mistake: only check immediate children (misses grandchildren!)
    static boolean isValidBST(Node node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return isValidBST(node.left,  min,      node.val)
            && isValidBST(node.right, node.val, max);
    }

    static boolean isValidBST(Node root) {
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    // ── PROBLEM 2: KTH SMALLEST ELEMENT — O(n) time, O(h) space
    // In-order traversal = sorted. Count until kth element.
    // Augmented BST (with subtree size) can do O(log n).
    static int kthSmallest(Node root, int k) {
        Deque<Node> stack = new ArrayDeque<>();
        Node curr = root;
        int count = 0;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) { stack.push(curr); curr = curr.left; }
            curr = stack.pop();
            if (++count == k) return curr.val;
            curr = curr.right;
        }
        return -1;
    }

    // ── PROBLEM 3: LOWEST COMMON ANCESTOR (BST) — O(log n) time
    // BST property: if both p and q < root → LCA in left subtree
    //               if both p and q > root → LCA in right subtree
    //               otherwise → root IS the LCA (split point)
    static Node lcaBST(Node root, int p, int q) {
        if (root == null) return null;
        if (p < root.val && q < root.val) return lcaBST(root.left,  p, q);
        if (p > root.val && q > root.val) return lcaBST(root.right, p, q);
        return root; // split point = LCA
    }

    // ── PROBLEM 4: CONVERT SORTED ARRAY TO BALANCED BST — O(n)
    // Always pick middle element as root → guarantees balance.
    // Left half → left subtree, right half → right subtree.
    static Node sortedArrayToBST(int[] nums, int lo, int hi) {
        if (lo > hi) return null;
        int mid  = lo + (hi - lo) / 2;      // middle = root
        Node node = new Node(nums[mid]);
        node.left  = sortedArrayToBST(nums, lo,    mid - 1);
        node.right = sortedArrayToBST(nums, mid + 1, hi);
        return node;
    }

    // ── PROBLEM 5: BST TO GREATER SUM TREE (GST) — O(n)
    // Each node's value = sum of all values >= it (including itself).
    // Approach: REVERSE in-order (Right→Root→Left) + running sum.
    static int runningSum = 0;

    static void toGST(Node node) {
        if (node == null) return;
        toGST(node.right);        // process larger values first
        runningSum += node.val;
        node.val    = runningSum; // replace with running sum
        toGST(node.left);
    }

    // ── PROBLEM 6: INORDER SUCCESSOR IN BST — O(log n)
    // In-order successor = next larger node.
    // Case A: if node has right subtree → leftmost of right subtree
    // Case B: no right subtree → last ancestor where we went LEFT
    static Node inorderSuccessor(Node root, Node target) {
        Node successor = null;
        while (root != null) {
            if (target.val < root.val) {
                successor = root;    // potential successor
                root = root.left;
            } else {
                root = root.right;   // go right, successor not here
            }
        }
        return successor;
    }

    // ── PROBLEM 7: TWO SUM IN BST — O(n) time, O(n) space
    // Collect in-order (sorted), then use two-pointer on sorted array.
    static boolean findTarget(Node root, int k) {
        List<Integer> sorted = new ArrayList<>();
        inOrderList(root, sorted);
        int lo = 0, hi = sorted.size() - 1;
        while (lo < hi) {
            int sum = sorted.get(lo) + sorted.get(hi);
            if      (sum == k) return true;
            else if (sum < k)  lo++;
            else               hi--;
        }
        return false;
    }

    static void inOrderList(Node node, List<Integer> list) {
        if (node == null) return;
        inOrderList(node.left,  list);
        list.add(node.val);
        inOrderList(node.right, list);
    }

    // ── PROBLEM 8: RECOVER BST (two swapped nodes) — O(n) time
    // In BST in-order = sorted. Two swapped nodes cause anomalies.
    // First anomaly:  first[prev > curr] → prev is first swapped
    // Second anomaly: second[prev > curr] → curr is second swapped
    // Swap values of first and second to fix.
    static Node first, second, prevNode;

    static void findSwapped(Node node) {
        if (node == null) return;
        findSwapped(node.left);
        if (prevNode != null && prevNode.val > node.val) {
            if (first == null) first = prevNode;  // first anomaly
            second = node;                         // second anomaly (update each time)
        }
        prevNode = node;
        findSwapped(node.right);
    }

    static void recoverBST(Node root) {
        first = second = prevNode = null;
        findSwapped(root);
        if (first != null && second != null) {
            int tmp     = first.val;
            first.val   = second.val;
            second.val  = tmp;
        }
    }

    // ── PROBLEM 9: BALANCE A BST — O(n)
    // Step 1: In-order traversal → sorted array
    // Step 2: Build balanced BST from sorted array (same as Problem 4)
    static Node balanceBST(Node root) {
        List<Integer> sorted = new ArrayList<>();
        inOrderList(root, sorted);
        int[] arr = sorted.stream().mapToInt(i -> i).toArray();
        return sortedArrayToBST(arr, 0, arr.length - 1);
    }

    // ── PROBLEM 10: MERGE TWO BSTs — O(m+n)
    // Step 1: In-order each BST → two sorted arrays
    // Step 2: Merge two sorted arrays → one sorted array
    // Step 3: Build balanced BST from merged sorted array
    static Node mergeBSTs(Node root1, Node root2) {
        List<Integer> list1 = new ArrayList<>(), list2 = new ArrayList<>();
        inOrderList(root1, list1);
        inOrderList(root2, list2);
        List<Integer> merged = mergeSortedLists(list1, list2);
        int[] arr = merged.stream().mapToInt(i -> i).toArray();
        return sortedArrayToBST(arr, 0, arr.length - 1);
    }

    static List<Integer> mergeSortedLists(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            if (a.get(i) <= b.get(j)) result.add(a.get(i++));
            else                       result.add(b.get(j++));
        }
        while (i < a.size()) result.add(a.get(i++));
        while (j < b.size()) result.add(b.get(j++));
        return result;
    }


    // ============================================================
    // TREE VISUALIZATION
    // ============================================================
    static void printTree(Node root, String prefix, boolean isLeft) {
        if (root == null) return;
        System.out.println("  " + prefix + (isLeft ? "├── " : "└── ") + root.val);
        printTree(root.left,  prefix + (isLeft ? "│   " : "    "), true);
        printTree(root.right, prefix + (isLeft ? "│   " : "    "), false);
    }

    static void visualize(Node root, String label) {
        System.out.println("\n  " + label + ":");
        if (root == null) { System.out.println("  (empty)"); return; }
        System.out.println("  └── " + root.val);
        printTree(root.left,  "      ", true);
        printTree(root.right, "      ", false);
    }

    static BST buildDemoBST() {
        BST bst = new BST();
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45})
            bst.insert(v);
        return bst;
    }


    // ============================================================
    //   MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   BINARY SEARCH TREES — Complete Deep Dive in Java       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");


        // ═══════════════════════════════════════════════════════
        // TOPIC 1 — BST FUNDAMENTALS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 1 — BST Fundamentals");

        sub("What is a BST?");
        System.out.println("  A Binary Search Tree is a Binary Tree with ONE extra rule:");
        System.out.println("  For EVERY node n:");
        System.out.println("    ALL values in LEFT subtree  < n.val");
        System.out.println("    ALL values in RIGHT subtree > n.val");
        System.out.println();
        System.out.println("  This ordering property makes search O(log n) on balanced trees.");
        System.out.println("  It reduces search space by HALF at every step.");

        sub("Why BST over a sorted array?");
        System.out.println("  Sorted Array:  O(log n) search  BUT O(n) insert/delete (shifting)");
        System.out.println("  BST:           O(log n) search  AND O(log n) insert/delete");
        System.out.println("  Linked List:   O(n) everything — no structure");
        System.out.println("  BST is the sweet spot: fast search + dynamic insertions.");

        sub("BST Visual");
        System.out.println("           50");
        System.out.println("          /  \\");
        System.out.println("         30   70");
        System.out.println("        /  \\  /  \\");
        System.out.println("       20  40 60  80");
        System.out.println("      /  \\   \\");
        System.out.println("     10  25  35");
        System.out.println();
        System.out.println("  Verify BST property at node 30:");
        System.out.println("  All of left subtree {10,20,25}: all < 30 ✓");
        System.out.println("  All of right subtree {35,40}:   all > 30 ✓");
        System.out.println("  AND all < 50 (parent) ✓ — property holds globally");

        sub("BST vs Plain Binary Tree");
        System.out.println("  Plain Binary Tree: no ordering. Search must check EVERY node O(n).");
        System.out.println("  BST: ordering eliminates HALF the tree at each step → O(log n).");
        System.out.println("  Trade-off: BST must maintain ordering on every insert/delete.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 2 — BST PROPERTIES
        // ═══════════════════════════════════════════════════════
        section("TOPIC 2 — BST Properties");

        BST demo = buildDemoBST();
        visualize(demo.root, "Demo BST");

        sub("Property 1: In-order traversal = Sorted ascending");
        List<Integer> sorted = demo.inOrder();
        System.out.println("  In-order: " + sorted);
        System.out.println("  Perfectly sorted! This is the defining property of BSTs.");
        System.out.println("  Use: print sorted, validate BST, kth smallest element.");

        sub("Property 2: Min is leftmost, Max is rightmost");
        System.out.println("  Min (leftmost) = " + demo.min());
        System.out.println("  Max (rightmost) = " + demo.max());
        System.out.println("  Reach min: 50→30→20→10. Path = height hops → O(log n).");

        sub("Property 3: Unique structural representation per key set");
        System.out.println("  SAME keys, DIFFERENT insertion orders → DIFFERENT tree shapes.");
        BST bst1 = new BST();
        BST bst2 = new BST();
        int[] sameKeys = {50, 30, 70};
        int[] diffOrder = {30, 50, 70};
        for (int v : sameKeys)   bst1.insert(v);
        for (int v : diffOrder)  bst2.insert(v);
        System.out.println("  Insert order [50,30,70] in-order: " + bst1.inOrder() +
                "  height=" + bst1.height());
        System.out.println("  Insert order [30,50,70] in-order: " + bst2.inOrder() +
                "  height=" + bst2.height());
        System.out.println("  Same keys, same in-order! But tree shapes differ.");
        System.out.println("  [30,50,70] is a right-skewed tree (height 3 vs 2).");

        sub("Property 4: Floor and Ceiling");
        BST fc = buildDemoBST();
        int[] queries = {15, 33, 42, 55, 75};
        System.out.println("  Floor = largest value <= key");
        System.out.println("  Ceiling = smallest value >= key");
        System.out.printf("  %-8s %-8s %-8s%n", "Key", "Floor", "Ceiling");
        System.out.println("  ──────────────────────────");
        for (int q : queries) {
            Node floorNode   = fc.floor(fc.root, q);
            Node ceilNode    = fc.ceiling(fc.root, q);
            System.out.printf("  %-8d %-8s %-8s%n", q,
                    floorNode   != null ? floorNode.val  + "" : "none",
                    ceilNode    != null ? ceilNode.val   + "" : "none");
        }
        System.out.println("  Used in: range queries, nearest-neighbor search");

        sub("Property 5: Subtree as BST");
        System.out.println("  Every subtree of a BST is itself a valid BST.");
        System.out.println("  This recursive structure is why recursive algorithms work so well.");

        sub("Property 6: Size and count queries");
        System.out.printf("  Size: %d nodes%n", demo.size());
        System.out.printf("  Count in [20,50]: %d nodes%n", demo.countInRange(demo.root, 20, 50));
        System.out.printf("  Count in [35,70]: %d nodes%n", demo.countInRange(demo.root, 35, 70));


        // ═══════════════════════════════════════════════════════
        // TOPIC 3 — SEARCH, INSERT, DELETE
        // ═══════════════════════════════════════════════════════
        section("TOPIC 3 — Search, Insert, Delete");

        BST ops = buildDemoBST();
        visualize(ops.root, "BST before operations");

        // ── SEARCH
        sub("SEARCH — O(log n) average");
        System.out.println("  Algorithm: compare target with current node.");
        System.out.println("  If equal → found. If less → go left. If more → go right.");
        System.out.println();
        int[] searchTargets = {25, 45, 99};
        for (int t : searchTargets) {
            Node result = ops.search(t);
            System.out.printf("  search(%2d): %s%n", t,
                    result != null ? "FOUND  ✓" : "NOT FOUND ✗");
        }
        System.out.println();
        System.out.println("  Trace for search(25):");
        System.out.println("    50: 25 < 50 → go left");
        System.out.println("    30: 25 < 30 → go left");
        System.out.println("    20: 25 > 20 → go right");
        System.out.println("    25: 25 == 25 → FOUND! (4 comparisons, not 11)");

        // ── INSERT
        sub("INSERT — O(log n) average");
        System.out.println("  Algorithm: same as search, but insert at the null position found.");
        System.out.println();
        System.out.println("  Before insert(45): " + ops.inOrder());
        ops.insert(45);
        System.out.println("  After  insert(45): " + ops.inOrder());
        System.out.println();
        System.out.println("  Trace for insert(45):");
        System.out.println("    50: 45 < 50 → go left");
        System.out.println("    30: 45 > 30 → go right");
        System.out.println("    40: 45 > 40 → go right");
        System.out.println("    45: found null at 40.right → insert Node(45) here ✓");
        visualize(ops.root, "After insert(45)");

        // ── DELETE
        sub("DELETE — O(log n) average — Three Cases");

        // Case 1: leaf
        System.out.println("  CASE 1 — Delete LEAF node (no children):");
        BST c1 = buildDemoBST();
        System.out.println("  Delete 10 (leaf):");
        System.out.println("  Before: " + c1.inOrder());
        c1.delete(10);
        System.out.println("  After:  " + c1.inOrder());
        System.out.println("  Action: just remove, set parent.left = null");

        // Case 2: one child
        System.out.println();
        System.out.println("  CASE 2 — Delete node with ONE CHILD:");
        BST c2 = buildDemoBST();
        System.out.println("  Delete 25 (has no children — leaf)");
        System.out.println("  Before: " + c2.inOrder());
        c2.delete(25);
        System.out.println("  After:  " + c2.inOrder());
        System.out.println("  Now delete 20 (has only left child=10):");
        c2.delete(20);
        System.out.println("  After:  " + c2.inOrder());
        System.out.println("  Action: replace deleted node with its single child");

        // Case 3: two children
        System.out.println();
        System.out.println("  CASE 3 — Delete node with TWO CHILDREN:");
        BST c3 = buildDemoBST();
        System.out.println("  Delete 30 (has left=20, right=40):");
        System.out.println("  Before: " + c3.inOrder());
        System.out.println("  In-order successor of 30 = min(right subtree) = 35");
        System.out.println("  Copy 35 into 30's position. Delete original 35.");
        c3.delete(30);
        System.out.println("  After:  " + c3.inOrder());
        visualize(c3.root, "After delete(30) — Case 3");

        sub("DELETE ROOT — special case of Case 3");
        BST rootDel = buildDemoBST();
        System.out.println("  Before delete(50) [root]: " + rootDel.inOrder());
        System.out.println("  In-order successor of 50 = min(right subtree) = 60");
        System.out.println("  New root val = 60. Delete original 60 from right subtree.");
        rootDel.delete(50);
        System.out.println("  After:  " + rootDel.inOrder());
        System.out.println("  New root: " + rootDel.root.val);


        // ═══════════════════════════════════════════════════════
        // TOPIC 4 — COMPLEXITY ANALYSIS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 4 — Complexity Analysis");

        sub("Operation complexity table");
        System.out.println("  ┌────────────────────┬──────────────┬──────────────┬──────────────┐");
        System.out.println("  │  Operation         │  Best Case   │  Average     │  Worst Case  │");
        System.out.println("  ├────────────────────┼──────────────┼──────────────┼──────────────┤");
        System.out.println("  │  Search            │  O(1)*       │  O(log n)    │  O(n)        │");
        System.out.println("  │  Insert            │  O(1)*       │  O(log n)    │  O(n)        │");
        System.out.println("  │  Delete            │  O(log n)    │  O(log n)    │  O(n)        │");
        System.out.println("  │  Min / Max         │  O(1)*       │  O(log n)    │  O(n)        │");
        System.out.println("  │  In-order traverse │  O(n)        │  O(n)        │  O(n)        │");
        System.out.println("  │  Floor / Ceiling   │  O(1)*       │  O(log n)    │  O(n)        │");
        System.out.println("  │  Kth Smallest      │  O(1)*       │  O(log n)**  │  O(n)        │");
        System.out.println("  │  Count in range    │  O(1)*       │  O(log n+k)  │  O(n)        │");
        System.out.println("  ├────────────────────┼──────────────┼──────────────┼──────────────┤");
        System.out.println("  │  Space (tree)      │  O(n)        │  O(n)        │  O(n)        │");
        System.out.println("  │  Space (ops stack) │  O(1)*       │  O(log n)    │  O(n)        │");
        System.out.println("  └────────────────────┴──────────────┴──────────────┴──────────────┘");
        System.out.println("  * when element is root or tree has 1 element");
        System.out.println("  ** O(log n) with augmented BST storing subtree sizes");

        sub("WHY O(log n) for balanced BST");
        System.out.println("  At each comparison, we eliminate HALF the remaining nodes.");
        System.out.println("  After 1 step: n/2 nodes remain");
        System.out.println("  After 2 steps: n/4 nodes remain");
        System.out.println("  After k steps: n/2^k nodes remain");
        System.out.println("  We stop when 1 node remains: n/2^k = 1 → k = log₂(n)");
        System.out.println();
        System.out.println("  n=1,000,000: only ~20 comparisons needed!");
        System.out.println("  Same logic as BINARY SEARCH on a sorted array.");

        sub("When worst case O(n) happens — skewed tree");
        BST skewed = new BST();
        for (int v : new int[]{10, 20, 30, 40, 50}) skewed.insert(v);
        System.out.println("  Inserting sorted values: 10, 20, 30, 40, 50");
        System.out.println("  Creates right-skewed tree:");
        visualize(skewed.root, "Skewed BST (degenerates to linked list)");
        System.out.println("  height=" + skewed.height() +
                " for n=" + skewed.size() + " nodes → O(n) operations!");
        System.out.println("  Search(50): must visit ALL 5 nodes — same as linear search.");

        sub("Comparison: BST vs other structures");
        System.out.println("  ┌────────────────────┬──────────┬──────────┬──────────┬──────────┐");
        System.out.println("  │  Operation         │  Array   │  Linked  │  BST     │  BST     │");
        System.out.println("  │                    │  Sorted  │  List    │  (avg)   │  (worst) │");
        System.out.println("  ├────────────────────┼──────────┼──────────┼──────────┼──────────┤");
        System.out.println("  │  Search            │  O(log n)│  O(n)    │  O(log n)│  O(n)    │");
        System.out.println("  │  Insert            │  O(n)    │  O(1)    │  O(log n)│  O(n)    │");
        System.out.println("  │  Delete            │  O(n)    │  O(n)    │  O(log n)│  O(n)    │");
        System.out.println("  │  Min/Max           │  O(1)    │  O(n)    │  O(log n)│  O(n)    │");
        System.out.println("  │  Sorted iteration  │  O(n)    │  O(n)    │  O(n)    │  O(n)    │");
        System.out.println("  └────────────────────┴──────────┴──────────┴──────────┴──────────┘");
        System.out.println("  BST wins over sorted array for dynamic insert/delete.");
        System.out.println("  Balance is CRITICAL for the O(log n) guarantee.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 5 — BALANCED vs SKEWED TREES
        // ═══════════════════════════════════════════════════════
        section("TOPIC 5 — Balanced vs Skewed Trees");

        sub("The Balance Problem");
        System.out.println("  Insert 1,2,3,4,5,6,7 in SORTED order into a plain BST:");
        BST sorted_insert = new BST();
        for (int v : new int[]{1,2,3,4,5,6,7}) sorted_insert.insert(v);
        System.out.println("  Result: fully right-skewed chain!");
        System.out.println("  Height = 7 (n). Search = O(n). Defeats the purpose of BST.");
        System.out.println();
        System.out.println("  Insert SAME 7 numbers in optimal order into BST:");
        BST optimal_insert = new BST();
        for (int v : new int[]{4,2,6,1,3,5,7}) optimal_insert.insert(v);
        System.out.println("  Height = " + optimal_insert.height() + " (log n). Search = O(log n).");

        sub("Height comparison: balanced vs skewed");
        System.out.println("  n=7 nodes:");
        System.out.printf("  Balanced height: %d  (log₂(7) ≈ 3)%n", optimal_insert.height());
        System.out.printf("  Skewed   height: %d  (n=7)%n", sorted_insert.height());
        System.out.println();
        System.out.println("  n=1,000,000 nodes:");
        System.out.println("  Balanced height: ~20  (log₂(1M) ≈ 20)");
        System.out.println("  Skewed   height: 1,000,000");
        System.out.println("  → 50,000× performance difference!");

        sub("AVL Tree — Self-Balancing BST");
        System.out.println("  AVL Property: |height(left) - height(right)| <= 1 at EVERY node");
        System.out.println("  Balance Factor = height(left) - height(right)");
        System.out.println("  Valid balance factors: -1, 0, +1");
        System.out.println("  If BF goes to +2 or -2 → rotate to restore balance");
        System.out.println();
        System.out.println("  Inserting 1,2,3,4,5,6,7 into AVL (vs plain BST):");

        AVL avl = new AVL();
        for (int v : new int[]{1,2,3,4,5,6,7}) avl.insert(v);

        System.out.println("  AVL in-order: " + avl.inOrder() + " (still sorted!)");
        System.out.println("  AVL height:   " + avl.height()  + " (log n, not n!)");
        System.out.println("  Plain BST height: " + sorted_insert.height());
        System.out.println("  Rotations kept it balanced despite sorted input.");

        sub("Rotation Types — keeping balance");
        System.out.println("  LL Case (Left-Left): right-rotate at unbalanced node");
        System.out.println("    Before:     30         After:    20");
        System.out.println("               /                   /  \\");
        System.out.println("             20         →        10   30");
        System.out.println("            /");
        System.out.println("           10");
        System.out.println();
        System.out.println("  RR Case (Right-Right): left-rotate at unbalanced node");
        System.out.println("    Before:  10           After:    20");
        System.out.println("               \\                   /  \\");
        System.out.println("               20       →        10   30");
        System.out.println("                  \\");
        System.out.println("                  30");
        System.out.println();
        System.out.println("  LR Case: left-rotate left child, then right-rotate");
        System.out.println("  RL Case: right-rotate right child, then left-rotate");

        sub("Balanced BSTs in Java standard library");
        System.out.println("  Java TreeMap  → Red-Black Tree (guaranteed O(log n))");
        System.out.println("  Java TreeSet  → Red-Black Tree (guaranteed O(log n))");
        System.out.println("  Red-Black vs AVL: RB is less strictly balanced but");
        System.out.println("  requires fewer rotations on insert/delete → faster writes.");

        sub("When to use which");
        System.out.println("  Plain BST:    learning, random data, simple implementation");
        System.out.println("  AVL Tree:     read-heavy workloads (stricter balance = faster search)");
        System.out.println("  Red-Black:    write-heavy workloads (fewer rotations = faster insert)");
        System.out.println("  Java TreeMap: production code (Red-Black, battle-tested)");


        // ═══════════════════════════════════════════════════════
        // TOPIC 6 — REAL-WORLD APPLICATIONS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 6 — Real-World Applications");

        sub("1. Database Indexes — Range Queries");
        System.out.println("  SQL: SELECT * FROM employees WHERE salary BETWEEN 40000 AND 80000");
        System.out.println();
        System.out.println("  B-Tree index (generalized BST) in MySQL/PostgreSQL:");
        System.out.println("  Step 1: Find 40000 via BST search     → O(log n)");
        System.out.println("  Step 2: In-order from 40000 to 80000  → O(k)");
        System.out.println("  Total:  O(log n + k) — k = number of results");
        System.out.println();
        System.out.println("  Compare to sequential scan without index: O(n) per query");
        System.out.println("  For 10M rows with 1000 results: B-Tree = ~25 seeks vs 10M reads");

        sub("2. Java TreeMap — Ordered Map Operations");
        TreeMap<Integer, String> salaryMap = new TreeMap<>();
        salaryMap.put(45000, "Alice");
        salaryMap.put(72000, "Navaneeth");
        salaryMap.put(38000, "Priya");
        salaryMap.put(90000, "Ravi");
        salaryMap.put(55000, "Ananya");

        System.out.println("  Employee salaries (TreeMap — always sorted):");
        salaryMap.forEach((sal, name) ->
                System.out.printf("    ₹%-8d → %s%n", sal, name));
        System.out.println();
        System.out.println("  firstKey(): ₹" + salaryMap.firstKey() + " (min salary)");
        System.out.println("  lastKey():  ₹" + salaryMap.lastKey()  + " (max salary)");
        System.out.println("  floorKey(60000):   ₹" + salaryMap.floorKey(60000));
        System.out.println("  ceilingKey(60000): ₹" + salaryMap.ceilingKey(60000));
        System.out.println("  subMap(40000,75000): " +
                salaryMap.subMap(40000, 75000));

        sub("3. Auto-complete / Prefix Search (BST-based Dictionary)");
        BST dict = new BST();
        // Using hash codes as proxy for string ordering
        String[] words = {"apple","application","apply","apt","banana","band","bandana"};
        TreeSet<String> wordTree = new TreeSet<>(Arrays.asList(words));
        System.out.println("  Words in TreeSet (Red-Black BST): " + wordTree);
        System.out.println("  headSet(\"b\"): " + wordTree.headSet("b") +
                " (words < 'b')");
        System.out.println("  tailSet(\"b\"): " + wordTree.tailSet("b") +
                " (words >= 'b')");
        System.out.println("  subSet(\"app\",\"apt\"): " + wordTree.subSet("app","apt"));

        sub("4. Event Scheduling System");
        System.out.println("  Events sorted by timestamp — find next event: O(log n)");
        TreeMap<Long, String> events = new TreeMap<>();
        events.put(1700000100L, "Meeting with Team");
        events.put(1700003600L, "Client Call");
        events.put(1700007200L, "Code Review");
        events.put(1700010800L, "Deploy Release");

        long now = 1700002000L;
        System.out.println("  Current time: " + now);
        System.out.println("  Next event:   " + events.ceilingEntry(now));
        System.out.println("  Prev event:   " + events.floorEntry(now));
        System.out.println("  All upcoming: " + events.tailMap(now));

        sub("5. BST as Priority Queue Alternative");
        System.out.println("  BST supports: O(log n) insert + O(log n) min/max extraction");
        System.out.println("  Binary Heap:  O(log n) insert + O(log n) extract-min");
        System.out.println("  BST advantage: also supports O(log n) arbitrary delete");
        System.out.println("  and O(log n) predecessor/successor — Heap can't do this.");
        System.out.println("  Used in: process schedulers (Linux CFS uses Red-Black Tree)");

        sub("6. Symbol Table in Compilers");
        System.out.println("  Compiler maintains a symbol table for variable/function names.");
        System.out.println("  BST: insert identifier → O(log n)");
        System.out.println("       lookup identifier → O(log n)");
        System.out.println("       sorted output (for debugging) → O(n) in-order");
        System.out.println("  Most compilers use hash table for O(1) but some use BST");
        System.out.println("  for ordered iteration (e.g., generating symbol listings).");


        // ═══════════════════════════════════════════════════════
        // TOPIC 7 — INTERVIEW-LEVEL PROBLEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 7 — Interview-Level Problems");

        sub("Problem 1: Validate BST");
        Node valid = buildDemoBST().root;

        Node invalid = new Node(5);
        invalid.left        = new Node(1);
        invalid.right       = new Node(4);
        invalid.right.left  = new Node(3);
        invalid.right.right = new Node(6);

        Node tricky = new Node(10);
        tricky.left         = new Node(5);
        tricky.right        = new Node(15);
        tricky.left.right   = new Node(12); // 12 > 10! invalid

        System.out.println("  Valid BST:   " + isValidBST(valid)   + " ✓");
        System.out.println("  Invalid BST: " + isValidBST(invalid) + " ✓ (5→4 wrong)");
        System.out.println("  Tricky BST:  " + isValidBST(tricky)  +
                " ✓ (5→12 > root 10, invalid)");
        System.out.println("  Technique: pass range [min,max] down, not just check children.");
        System.out.println("  O(n) time, O(h) space");

        sub("Problem 2: Kth Smallest Element");
        BST kthBST = buildDemoBST();
        System.out.println("  BST in-order (sorted): " + kthBST.inOrder());
        for (int k : new int[]{1, 3, 5, 7}) {
            System.out.printf("  %dth smallest: %d%n", k, kthSmallest(kthBST.root, k));
        }
        System.out.println("  Technique: iterative in-order with counter. O(n) time, O(h) space");

        sub("Problem 3: Lowest Common Ancestor");
        BST lcaTree = buildDemoBST();
        int[][] pairs = {{10,25},{20,40},{10,80},{25,45}};
        for (int[] pair : pairs) {
            Node lca = lcaBST(lcaTree.root, pair[0], pair[1]);
            System.out.printf("  LCA(%2d, %2d) = %d%n", pair[0], pair[1],
                    lca != null ? lca.val : -1);
        }
        System.out.println("  Technique: BST ordering — both < root? go left. Both > root? go right.");
        System.out.println("  Split point = LCA. O(log n) time, O(h) space");

        sub("Problem 4: Sorted Array → Balanced BST");
        int[] arr = {1, 2, 3, 4, 5, 6, 7};
        Node balanced = sortedArrayToBST(arr, 0, arr.length - 1);
        BST balBST = new BST(); balBST.root = balanced;
        System.out.println("  Input sorted array: " + Arrays.toString(arr));
        System.out.println("  Balanced BST in-order: " + balBST.inOrder());
        System.out.println("  Height: " + balBST.height() + "  (optimal log n)");
        visualize(balanced, "Balanced BST from sorted array");
        System.out.println("  Technique: always pick MIDDLE as root → guarantees balance.");
        System.out.println("  Recursively build left and right halves. O(n) time, O(n) space");

        sub("Problem 5: BST to Greater Sum Tree");
        int[] gstArr = {4, 2, 6, 1, 3, 5, 7};
        BST gstBST = new BST();
        for (int v : gstArr) gstBST.insert(v);
        System.out.println("  Before GST: " + gstBST.inOrder());
        runningSum = 0;
        toGST(gstBST.root);
        System.out.println("  After  GST: " + gstBST.inOrder());
        System.out.println("  Node 4 becomes 4+5+6+7=22, node 6 becomes 6+7=13, etc.");
        System.out.println("  Technique: reverse in-order (R→Root→L) + running sum.");
        System.out.println("  O(n) time, O(h) space");

        sub("Problem 6: In-order Successor");
        BST succBST = buildDemoBST();
        int[] succTests = {25, 40, 50, 80};
        for (int t : succTests) {
            Node target = succBST.search(t);
            Node succ   = inorderSuccessor(succBST.root, target);
            System.out.printf("  Successor of %2d = %s%n", t,
                    succ != null ? succ.val + "" : "none (largest element)");
        }
        System.out.println("  Technique: track last 'go left' ancestor. O(log n) time, O(1) space");

        sub("Problem 7: Two Sum in BST");
        BST tsBST = buildDemoBST();
        int[] targets = {30, 45, 130, 155};
        for (int t : targets) {
            System.out.printf("  findTarget(%-3d): %s%n", t, findTarget(tsBST.root, t));
        }
        System.out.println("  Technique: in-order → sorted array → two pointers.");
        System.out.println("  O(n) time, O(n) space");

        sub("Problem 8: Recover BST (two nodes swapped)");
        BST recoverBST = new BST();
        recoverBST.root         = new Node(3);
        recoverBST.root.left    = new Node(1);
        recoverBST.root.right   = new Node(4);
        recoverBST.root.right.left = new Node(2); // 2 and 3 are swapped!
        System.out.println("  Broken BST in-order: " + recoverBST.inOrder() +
                " (2 and 3 are swapped)");
        first = second = prevNode = null;
        recoverBST(recoverBST.root);
        System.out.println("  Fixed BST in-order:  " + recoverBST.inOrder());
        System.out.println("  Technique: in-order finds anomalies. Swap first & second. O(n)/O(h)");

        sub("Problem 9: Balance a BST");
        BST unbal = new BST();
        for (int v : new int[]{1,2,3,4,5}) unbal.insert(v); // right-skewed
        System.out.println("  Skewed BST in-order: " + unbal.inOrder() +
                "  height=" + unbal.height());
        Node rebalanced = balanceBST(unbal.root);
        BST rebalBST = new BST(); rebalBST.root = rebalanced;
        System.out.println("  Balanced BST in-order: " + rebalBST.inOrder() +
                "  height=" + rebalBST.height());
        System.out.println("  Technique: in-order → sorted array → rebuild. O(n)/O(n)");

        sub("Problem 10: Merge Two BSTs");
        BST bst_a = new BST();
        BST bst_b = new BST();
        for (int v : new int[]{2,1,4})   bst_a.insert(v);
        for (int v : new int[]{3,7,5,8}) bst_b.insert(v);
        System.out.println("  BST A: " + bst_a.inOrder());
        System.out.println("  BST B: " + bst_b.inOrder());
        Node merged = mergeBSTs(bst_a.root, bst_b.root);
        BST mergedBST = new BST(); mergedBST.root = merged;
        System.out.println("  Merged: " + mergedBST.inOrder());
        System.out.println("  Technique: in-order each → merge sorted arrays → rebuild. O(m+n)");

        // GRAND SUMMARY
        section("GRAND SUMMARY");
        System.out.println();
        System.out.println("  BST = Binary Tree where left < root < right at EVERY node.");
        System.out.println("  In-order traversal ALWAYS gives sorted output.");
        System.out.println();
        System.out.println("  OPERATIONS (balanced tree):");
        System.out.println("  Search / Insert / Delete → O(log n) average");
        System.out.println("  Min / Max / Floor / Ceiling → O(log n)");
        System.out.println("  In-order / All nodes → O(n)");
        System.out.println();
        System.out.println("  GOLDEN RULES:");
        System.out.println("  1. Insert sorted data → use AVL/Red-Black, not plain BST");
        System.out.println("  2. Validate BST with range [min,max], not local child check");
        System.out.println("  3. In-order = sorted → foundation for kth smallest, recovery");
        System.out.println("  4. LCA in BST: use ordering property O(log n) not O(n)");
        System.out.println("  5. Build balanced BST from sorted array: always pick middle");
        System.out.println("  6. Java TreeMap/TreeSet = Red-Black Tree = O(log n) guaranteed");
        System.out.println("  7. Reverse in-order (R→Root→L) = descending sort");
    }
}
