// ================================================================
//   TREES — Complete Deep Dive in Java
// ================================================================
//   Compile:  javac Trees.java
//   Run:      java Trees
// ================================================================
//
//   TOPICS:
//   1. Tree Fundamentals & Terminology
//   2. Binary Tree Structure
//   3. Traversals (DFS & BFS)
//   4. Time & Space Complexity
//   5. Recursive vs Iterative Approaches
//   6. Real-World Applications
//   7. Interview-Level Problems
// ================================================================

import java.util.*;

public class Trees {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    static void sub(String title) {
        System.out.println("\n  ── " + title + " ──");
    }


    // ============================================================
    // TREE NODE — The building block
    // ============================================================
    static class TreeNode {
        int      val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val   = val;
            this.left  = null;
            this.right = null;
        }
    }


    // ============================================================
    // BINARY SEARCH TREE — Ordered Binary Tree
    // ============================================================
    //
    //   BST Property (maintained at every node):
    //   left.val  < node.val
    //   right.val > node.val
    //
    //   This property makes search O(log n) on balanced trees.
    //
    //        50
    //       /  \
    //      30   70
    //     /  \  / \
    //    20  40 60  80
    //
    //   search(60): 60 < 50? No → go right → 60 < 70? Yes → go left → found!
    //   Only 3 comparisons for 7 nodes. O(log n)
    // ============================================================
    static class BST {
        TreeNode root;

        // INSERT — O(log n) average, O(n) worst (skewed tree)
        TreeNode insert(TreeNode node, int val) {
            if (node == null) return new TreeNode(val);
            if (val < node.val) node.left  = insert(node.left,  val);
            else if (val > node.val) node.right = insert(node.right, val);
            return node; // val == node.val: duplicate, ignore
        }

        void insert(int val) { root = insert(root, val); }

        // SEARCH — O(log n) average
        boolean search(TreeNode node, int val) {
            if (node == null) return false;
            if (val == node.val) return true;
            if (val < node.val) return search(node.left,  val);
            else                return search(node.right, val);
        }

        boolean search(int val) { return search(root, val); }

        // DELETE — O(log n) average
        // Case 1: leaf node         → just remove
        // Case 2: one child         → replace node with its child
        // Case 3: two children      → replace with in-order successor
        //         (smallest in right subtree), then delete successor
        TreeNode delete(TreeNode node, int val) {
            if (node == null) return null;
            if (val < node.val)      node.left  = delete(node.left,  val);
            else if (val > node.val) node.right = delete(node.right, val);
            else {
                if (node.left  == null) return node.right; // case 1 & 2
                if (node.right == null) return node.left;  // case 2
                // Case 3: find in-order successor (min of right subtree)
                TreeNode successor = findMin(node.right);
                node.val   = successor.val;                // copy successor's val
                node.right = delete(node.right, successor.val); // delete successor
            }
            return node;
        }

        void delete(int val) { root = delete(root, val); }

        TreeNode findMin(TreeNode node) {
            while (node.left != null) node = node.left;
            return node;
        }
    }


    // ============================================================
    // DFS TRAVERSALS — Recursive
    // ============================================================
    //
    //   Three orderings, all O(n) time, O(h) space (h = height):
    //
    //   PRE-ORDER:  Root → Left → Right   (top-down)
    //   IN-ORDER:   Left → Root → Right   (sorted order for BST!)
    //   POST-ORDER: Left → Right → Root   (bottom-up)
    //
    //   For this tree:
    //          1
    //         / \
    //        2   3
    //       / \   \
    //      4   5   6
    //
    //   Pre-order:  1, 2, 4, 5, 3, 6   (root first)
    //   In-order:   4, 2, 5, 1, 3, 6   (sorted for BST)
    //   Post-order: 4, 5, 2, 6, 3, 1   (root last)
    //   BFS/Level:  1, 2, 3, 4, 5, 6   (level by level)
    // ============================================================

    // PRE-ORDER — Root, Left, Right
    static void preOrder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.val);           // VISIT root FIRST
        preOrder(node.left,  result);
        preOrder(node.right, result);
    }

    // IN-ORDER — Left, Root, Right
    static void inOrder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        inOrder(node.left,  result);
        result.add(node.val);           // VISIT root IN MIDDLE
        inOrder(node.right, result);
    }

    // POST-ORDER — Left, Right, Root
    static void postOrder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        postOrder(node.left,  result);
        postOrder(node.right, result);
        result.add(node.val);           // VISIT root LAST
    }

    // BFS / LEVEL-ORDER — uses Queue
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();           // snapshot this level
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(level);
        }
        return result;
    }


    // ============================================================
    // ITERATIVE TRAVERSALS — Using explicit Stack/Queue
    // ============================================================

    // ITERATIVE PRE-ORDER — Stack: push right first, then left
    // (so left is processed first — LIFO)
    static List<Integer> preOrderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);                  // visit
            if (node.right != null) stack.push(node.right); // push RIGHT first
            if (node.left  != null) stack.push(node.left);  // push LEFT last (processed first)
        }
        return result;
    }

    // ITERATIVE IN-ORDER — Classic two-pointer stack pattern
    // Go as far LEFT as possible, then visit, then go right
    static List<Integer> inOrderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {        // go left as far as possible
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();           // visit leftmost
            result.add(curr.val);
            curr = curr.right;            // move to right subtree
        }
        return result;
    }

    // ITERATIVE POST-ORDER — Two-stack trick
    // Stack1 simulates reverse post-order (root, right, left)
    // Stack2 reverses that to give left, right, root
    static List<Integer> postOrderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Deque<TreeNode> stack1 = new ArrayDeque<>();
        Deque<TreeNode> stack2 = new ArrayDeque<>();
        stack1.push(root);

        while (!stack1.isEmpty()) {
            TreeNode node = stack1.pop();
            stack2.push(node);                          // store in reverse
            if (node.left  != null) stack1.push(node.left);
            if (node.right != null) stack1.push(node.right);
        }
        while (!stack2.isEmpty()) result.add(stack2.pop().val);
        return result;
    }


    // ============================================================
    // TREE PROPERTIES — Utility methods
    // ============================================================

    // HEIGHT — longest path from root to any leaf — O(n)
    static int height(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    // SIZE — total number of nodes — O(n)
    static int size(TreeNode node) {
        if (node == null) return 0;
        return 1 + size(node.left) + size(node.right);
    }

    // IS BALANCED — height difference between left and right <= 1 — O(n)
    static int checkBalance(TreeNode node) {
        if (node == null) return 0;
        int left  = checkBalance(node.left);
        int right = checkBalance(node.right);
        if (left == -1 || right == -1) return -1;   // already unbalanced
        if (Math.abs(left - right) > 1) return -1;  // current node unbalanced
        return 1 + Math.max(left, right);
    }

    static boolean isBalanced(TreeNode root) {
        return checkBalance(root) != -1;
    }


    // ============================================================
    // INTERVIEW PROBLEMS
    // ============================================================

    // ── PROBLEM 1: MAX DEPTH — O(n) time, O(h) space
    // Max depth = height of tree = longest root-to-leaf path
    static int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    // ── PROBLEM 2: DIAMETER OF BINARY TREE — O(n) time, O(h) space
    // Diameter = longest path between ANY two nodes (may not pass through root)
    // At each node: diameter through it = leftHeight + rightHeight
    // Track global max
    static int[] diameterResult = {0}; // use array to pass by reference in recursion

    static int diameterHelper(TreeNode node) {
        if (node == null) return 0;
        int left  = diameterHelper(node.left);
        int right = diameterHelper(node.right);
        diameterResult[0] = Math.max(diameterResult[0], left + right); // path through this node
        return 1 + Math.max(left, right); // height up to this node
    }

    static int diameter(TreeNode root) {
        diameterResult[0] = 0;
        diameterHelper(root);
        return diameterResult[0];
    }

    // ── PROBLEM 3: IS SYMMETRIC — O(n) time, O(h) space
    // A tree is symmetric if left subtree mirrors right subtree.
    // Compare: left.left with right.right AND left.right with right.left
    static boolean isMirror(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        return left.val == right.val
                && isMirror(left.left,  right.right)  // outer pair
                && isMirror(left.right, right.left);  // inner pair
    }

    static boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return isMirror(root.left, root.right);
    }

    // ── PROBLEM 4: LOWEST COMMON ANCESTOR — O(n) time, O(h) space
    // LCA of p and q = deepest node that has both p and q as descendants.
    // For BST: use BST property for O(log n)
    // For general BT: post-order traversal
    static TreeNode lcaBinaryTree(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left  = lcaBinaryTree(root.left,  p, q);
        TreeNode right = lcaBinaryTree(root.right, p, q);
        if (left != null && right != null) return root; // p and q on different sides
        return left != null ? left : right;             // both on same side
    }

    // LCA for BST — O(log n) using BST property
    static TreeNode lcaBST(TreeNode root, int p, int q) {
        if (p < root.val && q < root.val) return lcaBST(root.left,  p, q); // both left
        if (p > root.val && q > root.val) return lcaBST(root.right, p, q); // both right
        return root; // split point = LCA
    }

    // ── PROBLEM 5: PATH SUM — O(n) time, O(h) space
    // Does a root-to-leaf path with the given sum exist?
    static boolean hasPathSum(TreeNode root, int target) {
        if (root == null) return false;
        if (root.left == null && root.right == null) // leaf node
            return root.val == target;
        return hasPathSum(root.left,  target - root.val)
            || hasPathSum(root.right, target - root.val);
    }

    // ALL ROOT-TO-LEAF PATHS with target sum
    static List<List<Integer>> pathSumAll(TreeNode root, int target) {
        List<List<Integer>> result = new ArrayList<>();
        pathDFS(root, target, new ArrayList<>(), result);
        return result;
    }

    static void pathDFS(TreeNode node, int remain,
                        List<Integer> path, List<List<Integer>> result) {
        if (node == null) return;
        path.add(node.val);
        if (node.left == null && node.right == null && remain == node.val) {
            result.add(new ArrayList<>(path)); // found valid path
        }
        pathDFS(node.left,  remain - node.val, path, result);
        pathDFS(node.right, remain - node.val, path, result);
        path.remove(path.size() - 1); // BACKTRACK — remove last element
    }

    // ── PROBLEM 6: VALIDATE BST — O(n) time, O(h) space
    // Naive: check left.val < root.val < right.val at each node — WRONG!
    // Counter-example: a node deep in left subtree could be > root
    // Correct: pass valid range [min, max] and verify node is within
    static boolean isValidBST(TreeNode node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return isValidBST(node.left,  min,      node.val) // left range: (min, node.val)
            && isValidBST(node.right, node.val, max);     // right range: (node.val, max)
    }

    static boolean isValidBST(TreeNode root) {
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    // ── PROBLEM 7: RIGHT SIDE VIEW — O(n) time, O(n) space
    // What you see looking at the tree from the RIGHT side.
    // = last node at each level in BFS
    static List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (i == levelSize - 1) result.add(node.val); // LAST node at level
                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return result;
    }

    // ── PROBLEM 8: SERIALIZE & DESERIALIZE — O(n) time and space
    // Convert tree to string and back — used for storage/transmission
    // Format: "1,2,4,null,null,5,null,null,3,null,6,null,null"
    static String serialize(TreeNode root) {
        if (root == null) return "null";
        return root.val + "," + serialize(root.left) + "," + serialize(root.right);
    }

    static int[] idx = {0};
    static TreeNode deserialize(String[] nodes) {
        if (nodes[idx[0]].equals("null")) { idx[0]++; return null; }
        TreeNode node = new TreeNode(Integer.parseInt(nodes[idx[0]++]));
        node.left  = deserialize(nodes);
        node.right = deserialize(nodes);
        return node;
    }

    // ── PROBLEM 9: BUILD TREE FROM TRAVERSALS — O(n) time, O(n) space
    // Given pre-order and in-order traversals, reconstruct the tree.
    // Pre-order[0] = ROOT. Find root in in-order → splits left and right.
    static TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> inMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) inMap.put(inorder[i], i);
        return buildHelper(preorder, 0, preorder.length - 1,
                0, inorder.length - 1, inMap);
    }

    static TreeNode buildHelper(int[] pre, int preL, int preR,
                                int inL, int inR, Map<Integer, Integer> inMap) {
        if (preL > preR || inL > inR) return null;
        TreeNode root    = new TreeNode(pre[preL]);     // pre[preL] is always root
        int inRoot       = inMap.get(root.val);         // find root in in-order
        int leftTreeSize = inRoot - inL;                // size of left subtree
        root.left  = buildHelper(pre, preL + 1, preL + leftTreeSize,
                inL, inRoot - 1, inMap);
        root.right = buildHelper(pre, preL + leftTreeSize + 1, preR,
                inRoot + 1, inR, inMap);
        return root;
    }

    // ── PROBLEM 10: ZIGZAG LEVEL ORDER — O(n) time, O(n) space
    // Alternate between left-to-right and right-to-left at each level
    static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            int[] level   = new int[levelSize];
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                int idx = leftToRight ? i : levelSize - 1 - i; // direction
                level[idx] = node.val;
                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            List<Integer> levelList = new ArrayList<>();
            for (int v : level) levelList.add(v);
            result.add(levelList);
            leftToRight = !leftToRight; // toggle direction
        }
        return result;
    }


    // ============================================================
    // TREE BUILDER HELPERS
    // ============================================================

    // Build the demo tree:
    //          1
    //         / \
    //        2   3
    //       / \   \
    //      4   5   6
    static TreeNode buildDemoTree() {
        TreeNode root = new TreeNode(1);
        root.left           = new TreeNode(2);
        root.right          = new TreeNode(3);
        root.left.left      = new TreeNode(4);
        root.left.right     = new TreeNode(5);
        root.right.right    = new TreeNode(6);
        return root;
    }

    // Build a BST:
    //        50
    //       /  \
    //      30   70
    //     / \   / \
    //    20  40 60  80
    static TreeNode buildBST() {
        BST bst = new BST();
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80}) bst.insert(v);
        return bst.root;
    }

    // Print tree visually
    static void printTree(TreeNode root, String prefix, boolean isLeft) {
        if (root == null) return;
        System.out.println("  " + prefix + (isLeft ? "├── " : "└── ") + root.val);
        printTree(root.left,  prefix + (isLeft ? "│   " : "    "), true);
        printTree(root.right, prefix + (isLeft ? "│   " : "    "), false);
    }

    static void visualize(TreeNode root, String label) {
        System.out.println("  " + label + ":");
        if (root == null) { System.out.println("  (empty)"); return; }
        System.out.println("  └── " + root.val);
        printTree(root.left,  "    ", true);
        printTree(root.right, "    ", false);
    }


    // ============================================================
    //   MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         TREES — Complete Deep Dive in Java               ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");


        // ═══════════════════════════════════════════════════════
        // TOPIC 1 — TREE FUNDAMENTALS & TERMINOLOGY
        // ═══════════════════════════════════════════════════════
        section("TOPIC 1 — Tree Fundamentals & Terminology");

        sub("What is a Tree?");
        System.out.println("  A Tree is a hierarchical, non-linear data structure.");
        System.out.println("  Unlike arrays/linked lists (linear), trees branch.");
        System.out.println("  Every tree is a collection of NODES connected by EDGES.");
        System.out.println("  There is exactly ONE path between any two nodes.");
        System.out.println("  No cycles. No bidirectional connections between levels.");

        sub("Key Terminology — Visualized");
        System.out.println("             [A]         ← ROOT (no parent)");
        System.out.println("            /   \\");
        System.out.println("          [B]   [C]       ← CHILDREN of A / SIBLINGS");
        System.out.println("         / \\     \\");
        System.out.println("       [D] [E]   [F]      ← D,E,F are LEAVES (no children)");
        System.out.println();
        System.out.println("  ROOT:         A — top node, has no parent");
        System.out.println("  PARENT:       A is parent of B and C");
        System.out.println("  CHILD:        B and C are children of A");
        System.out.println("  SIBLINGS:     B and C share the same parent A");
        System.out.println("  LEAF:         D, E, F — nodes with no children");
        System.out.println("  INTERNAL NODE:A, B, C — nodes with at least one child");
        System.out.println("  EDGE:         connection between parent and child");
        System.out.println("  HEIGHT:       longest path from root to any leaf = 3 (A→B→D)");
        System.out.println("  DEPTH:        path length from root to a node");
        System.out.println("                depth(A)=0, depth(B)=1, depth(D)=2");
        System.out.println("  LEVEL:        all nodes at same depth (level 0 = root)");
        System.out.println("  SUBTREE:      any node + all its descendants");
        System.out.println("  DEGREE:       number of children. A's degree=2, D's degree=0");

        sub("Types of Trees");
        System.out.println("  Binary Tree        → each node has AT MOST 2 children");
        System.out.println("  Binary Search Tree → binary tree with ordering property");
        System.out.println("  Complete Binary Tree→ all levels full except last (left-filled)");
        System.out.println("  Perfect Binary Tree → all internal nodes have 2 children");
        System.out.println("  Balanced Tree      → height difference ≤ 1 at every node");
        System.out.println("  AVL Tree           → self-balancing BST");
        System.out.println("  Red-Black Tree     → self-balancing (used in Java TreeMap)");
        System.out.println("  Trie (Prefix Tree) → character-by-character string storage");
        System.out.println("  Heap               → complete binary tree with heap property");
        System.out.println("  N-ary Tree         → each node can have N children");

        sub("Why Trees?");
        System.out.println("  Arrays:      O(log n) search ONLY if sorted; O(n) insert");
        System.out.println("  Linked List: O(n) search always");
        System.out.println("  BST:         O(log n) search, insert, delete (when balanced)");
        System.out.println("  Hierarchy:   File systems, HTML DOM, org charts — naturally trees");


        // ═══════════════════════════════════════════════════════
        // TOPIC 2 — BINARY TREE STRUCTURE
        // ═══════════════════════════════════════════════════════
        section("TOPIC 2 — Binary Tree Structure");

        sub("Node structure in memory");
        System.out.println("  class TreeNode {");
        System.out.println("      int      val;    // 4 bytes — the data");
        System.out.println("      TreeNode left;   // 8 bytes — reference to left child");
        System.out.println("      TreeNode right;  // 8 bytes — reference to right child");
        System.out.println("  }                    // ~24 bytes per node (+ object header)");
        System.out.println();
        System.out.println("  Compare to LinkedList node: data + 1 pointer = 16 bytes");
        System.out.println("  TreeNode: data + 2 pointers = 24 bytes");

        sub("Building our demo tree");
        TreeNode demo = buildDemoTree();
        visualize(demo, "Demo Binary Tree");
        System.out.println();
        System.out.println("  Height: " + height(demo));
        System.out.println("  Size  : " + size(demo));
        System.out.println("  Balanced: " + isBalanced(demo));

        sub("Binary Search Tree");
        TreeNode bst = buildBST();
        visualize(bst, "Binary Search Tree");
        System.out.println();
        System.out.println("  BST Property: left < node < right (at EVERY node)");
        System.out.println();

        BST bstOps = new BST();
        for (int v : new int[]{50,30,70,20,40,60,80}) bstOps.insert(v);
        System.out.println("  search(60) = " + bstOps.search(60) + "   50→right→70→left→60 ✓");
        System.out.println("  search(45) = " + bstOps.search(45) + "  not in tree");
        bstOps.insert(45);
        System.out.println("  After insert(45), search(45) = " + bstOps.search(45));

        sub("Perfect vs Complete vs Skewed");
        System.out.println("  Perfect:   all levels full. n nodes = 2^h - 1");
        System.out.println("      [1]");
        System.out.println("     /   \\");
        System.out.println("   [2]   [3]");
        System.out.println("   / \\ / \\");
        System.out.println("  [4][5][6][7]   height=3, nodes=7=2^3-1");
        System.out.println();
        System.out.println("  Complete:  all levels full except last (filled left to right)");
        System.out.println("  Used by: Binary Heaps (stored efficiently in array)");
        System.out.println();
        System.out.println("  Skewed:    every node has only one child (degenerates to list)");
        System.out.println("  [1]→[2]→[3]→[4]   height=n=4. BST operations become O(n)!");
        System.out.println("  Fix: AVL Trees or Red-Black Trees (self-balancing)");


        // ═══════════════════════════════════════════════════════
        // TOPIC 3 — TRAVERSALS (DFS & BFS)
        // ═══════════════════════════════════════════════════════
        section("TOPIC 3 — Traversals (DFS & BFS)");

        TreeNode t = buildDemoTree();
        visualize(t, "Tree being traversed");
        System.out.println();

        sub("DFS — Depth-First Search (3 orders)");
        System.out.println("  DFS goes DEEP before going WIDE.");
        System.out.println("  Uses RECURSION (implicit call stack) or explicit Stack.");
        System.out.println();

        List<Integer> pre  = new ArrayList<>();
        List<Integer> in   = new ArrayList<>();
        List<Integer> post = new ArrayList<>();
        preOrder (t, pre);
        inOrder  (t, in);
        postOrder(t, post);

        System.out.println("  PRE-ORDER  (Root, Left, Right): " + pre);
        System.out.println("  Pattern: ROOT first → good for COPYING/CLONING a tree");
        System.out.println("  Use:     prefix expressions, directory listing (parent first)");
        System.out.println();
        System.out.println("  IN-ORDER   (Left, Root, Right):  " + in);
        System.out.println("  Pattern: sorted output for BST");
        System.out.println("  Use:     sorting, expression trees, kth smallest in BST");
        System.out.println();
        System.out.println("  POST-ORDER (Left, Right, Root):  " + post);
        System.out.println("  Pattern: ROOT last → good for DELETING/freeing a tree");
        System.out.println("  Use:     postfix expressions, calculating folder sizes");

        sub("BFS — Breadth-First Search (Level Order)");
        System.out.println("  BFS goes WIDE before going DEEP.");
        System.out.println("  Uses a QUEUE. Processes each level completely before next.");
        System.out.println();
        List<List<Integer>> levels = levelOrder(t);
        System.out.println("  Level-order traversal (level by level):");
        for (int lvl = 0; lvl < levels.size(); lvl++) {
            System.out.println("  Level " + lvl + ": " + levels.get(lvl));
        }
        System.out.println();
        System.out.println("  Use: shortest path (unweighted), level-wise processing,");
        System.out.println("       right side view, minimum depth, cousin nodes");

        sub("BST In-Order = Sorted Output");
        TreeNode bstNode = buildBST();
        List<Integer> bstIn = new ArrayList<>();
        inOrder(bstNode, bstIn);
        System.out.println("  BST in-order: " + bstIn + "  ← always ascending! ✓");
        System.out.println("  This is why in-order traversal is used for BST validation.");

        sub("Traversal Cheat Sheet");
        System.out.println("  ┌──────────────┬─────────────────────┬─────────────────────────┐");
        System.out.println("  │  Traversal   │  Order              │  Best Use               │");
        System.out.println("  ├──────────────┼─────────────────────┼─────────────────────────┤");
        System.out.println("  │  Pre-order   │  Root→Left→Right    │  Copy tree, prefix expr │");
        System.out.println("  │  In-order    │  Left→Root→Right    │  BST sort, kth element  │");
        System.out.println("  │  Post-order  │  Left→Right→Root    │  Delete tree, postfix   │");
        System.out.println("  │  Level-order │  Level by level     │  Shortest path, BFS     │");
        System.out.println("  └──────────────┴─────────────────────┴─────────────────────────┘");


        // ═══════════════════════════════════════════════════════
        // TOPIC 4 — TIME & SPACE COMPLEXITY
        // ═══════════════════════════════════════════════════════
        section("TOPIC 4 — Time & Space Complexity");

        sub("Traversal Complexity");
        System.out.println("  ALL traversals visit EVERY node exactly ONCE.");
        System.out.println("  Time: O(n) for all traversals (n = number of nodes)");
        System.out.println();
        System.out.println("  Space complexity depends on the CALL STACK depth:");
        System.out.println("  Balanced tree: h = log n → O(log n) space");
        System.out.println("  Skewed tree:   h = n     → O(n) space (worst case)");
        System.out.println("  BFS uses a QUEUE: O(w) where w = max width of tree");
        System.out.println("  Perfect tree max width = n/2 → O(n) space for BFS");

        sub("BST Operation Complexity");
        System.out.println("  ┌─────────────┬──────────────┬──────────────┬────────────────────┐");
        System.out.println("  │  Operation  │  Average     │  Worst Case  │  Worst Case Reason │");
        System.out.println("  ├─────────────┼──────────────┼──────────────┼────────────────────┤");
        System.out.println("  │  search     │  O(log n)    │  O(n)        │  Skewed tree       │");
        System.out.println("  │  insert     │  O(log n)    │  O(n)        │  Skewed tree       │");
        System.out.println("  │  delete     │  O(log n)    │  O(n)        │  Skewed tree       │");
        System.out.println("  │  min/max    │  O(log n)    │  O(n)        │  Skewed tree       │");
        System.out.println("  │  in-order   │  O(n)        │  O(n)        │  Visit all nodes   │");
        System.out.println("  └─────────────┴──────────────┴──────────────┴────────────────────┘");
        System.out.println("  Balanced BST (AVL/Red-Black): GUARANTEED O(log n) for all ops");
        System.out.println("  Java TreeMap uses Red-Black Tree → always O(log n)");

        sub("Height vs Node Count relationship");
        System.out.println("  Perfect tree:  h = log₂(n+1)  → n = 2^h - 1");
        System.out.println("  Balanced tree: h = O(log n)");
        System.out.println("  Skewed tree:   h = n");
        System.out.println();
        System.out.println("  For n=1000 nodes:");
        System.out.println("  Balanced height: ~10  → O(10) operations");
        System.out.println("  Skewed   height: 1000 → O(1000) operations");
        System.out.println("  Balance matters: 100× performance difference!");

        sub("Space complexity: Recursion stack");
        System.out.println("  Every recursive DFS call uses one stack frame.");
        System.out.println("  Max frames = height of tree = O(h)");
        System.out.println("  Balanced: O(log n) frames — fine");
        System.out.println("  Skewed:   O(n) frames — StackOverflowError risk!");
        System.out.println("  Solution: iterative traversal with explicit stack on heap");


        // ═══════════════════════════════════════════════════════
        // TOPIC 5 — RECURSIVE vs ITERATIVE
        // ═══════════════════════════════════════════════════════
        section("TOPIC 5 — Recursive vs Iterative Approaches");

        TreeNode ri = buildDemoTree();
        visualize(ri, "Tree for Recursive vs Iterative comparison");
        System.out.println();

        sub("Pre-order: Recursive vs Iterative");
        List<Integer> rPre = new ArrayList<>();
        preOrder(ri, rPre);
        List<Integer> iPre = preOrderIterative(ri);
        System.out.println("  Recursive  pre-order: " + rPre);
        System.out.println("  Iterative  pre-order: " + iPre);
        System.out.println("  Match: " + rPre.equals(iPre) + " ✓");
        System.out.println();
        System.out.println("  Iterative: push RIGHT first, then LEFT (LIFO → left processed first)");

        sub("In-order: Recursive vs Iterative");
        List<Integer> rIn = new ArrayList<>();
        inOrder(ri, rIn);
        List<Integer> iIn = inOrderIterative(ri);
        System.out.println("  Recursive  in-order:  " + rIn);
        System.out.println("  Iterative  in-order:  " + iIn);
        System.out.println("  Match: " + rIn.equals(iIn) + " ✓");
        System.out.println();
        System.out.println("  Iterative: 'go as far left as possible' loop");
        System.out.println("  Uses curr pointer + stack. Trickiest iterative traversal.");

        sub("Post-order: Recursive vs Iterative");
        List<Integer> rPost = new ArrayList<>();
        postOrder(ri, rPost);
        List<Integer> iPost = postOrderIterative(ri);
        System.out.println("  Recursive  post-order:" + rPost);
        System.out.println("  Iterative  post-order:" + iPost);
        System.out.println("  Match: " + rPost.equals(iPost) + " ✓");
        System.out.println();
        System.out.println("  Iterative: two-stack trick — stack2 reverses root-right-left");
        System.out.println("  to produce left-right-root (post-order)");

        sub("When to use Recursive vs Iterative");
        System.out.println("  RECURSIVE:  cleaner code, matches problem structure naturally");
        System.out.println("              risk: StackOverflowError for very deep/skewed trees");
        System.out.println("              best for: most interview problems (n ≤ 10^4)");
        System.out.println();
        System.out.println("  ITERATIVE:  no stack overflow risk, explicit control");
        System.out.println("              more code, harder to write/read");
        System.out.println("              best for: production code, very large trees,");
        System.out.println("              when asked 'without recursion' in interview");

        sub("Complexity comparison");
        System.out.println("  Both recursive and iterative: O(n) time, O(h) space");
        System.out.println("  Recursive stack:  JVM call stack (limited ~10K depth)");
        System.out.println("  Iterative stack:  Heap-allocated (effectively unlimited)");
        System.out.println("  In practice, use recursive unless tree could be very deep.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 6 — REAL-WORLD APPLICATIONS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 6 — Real-World Applications");

        sub("1. File System (OS Directory Tree)");
        System.out.println("  /");
        System.out.println("  ├── home/");
        System.out.println("  │   └── navaneeth/");
        System.out.println("  │       ├── projects/");
        System.out.println("  │       └── documents/");
        System.out.println("  └── usr/");
        System.out.println("      └── local/");
        System.out.println("  Tree traversal used in: ls -R, find, du (disk usage)");
        System.out.println("  Post-order: compute folder size = sum of all children sizes");
        System.out.println("  Pre-order:  print directory listing (parent before children)");

        sub("2. HTML DOM Tree (Browser Rendering)");
        System.out.println("  <html>");
        System.out.println("    <head>  <body>");
        System.out.println("             <div>");
        System.out.println("               <p>  <span>");
        System.out.println("  CSS selector: body > div > p — tree path traversal O(depth)");
        System.out.println("  JavaScript: document.getElementById() — BFS or DFS search");
        System.out.println("  React Virtual DOM: tree diffing algorithm (reconciliation)");

        sub("3. Expression Trees (Compilers)");
        System.out.println("  Expression: (3 + 4) * (5 - 2)");
        System.out.println();
        System.out.println("            [*]");
        System.out.println("           /   \\");
        System.out.println("         [+]   [-]");
        System.out.println("        /  \\  /  \\");
        System.out.println("       3    4 5    2");
        System.out.println();
        System.out.println("  In-order:   3 + 4 * 5 - 2  (infix — what we write)");
        System.out.println("  Pre-order:  * + 3 4 - 5 2  (prefix / Polish)");
        System.out.println("  Post-order: 3 4 + 5 2 - *  (postfix / RPN — easiest to eval)");

        sub("4. Binary Search — Database Indexes");
        System.out.println("  Database B-Tree index for: SELECT * WHERE salary > 50000");
        System.out.println("  B-Tree is a generalized BST where each node can hold");
        System.out.println("  multiple keys and have multiple children.");
        System.out.println("  Range queries: in-order traversal from start node → O(log n + k)");
        System.out.println("  Used in: MySQL InnoDB, PostgreSQL, SQLite");

        sub("5. Auto-complete / Spell Check (Trie)");
        System.out.println("  Trie (prefix tree): each node = one character");
        System.out.println("  Insert 'apple', 'app', 'application':");
        System.out.println("  root → a → p → p → (end)");
        System.out.println("                 \\→ l → e → (end)");
        System.out.println("                     \\→ i → c → a → t → i → o → n → (end)");
        System.out.println("  Search prefix 'app': O(length of prefix) = O(k)");
        System.out.println("  Used in: Google search suggestions, IDE autocomplete, spellcheck");

        sub("6. Priority Queue (Binary Heap)");
        System.out.println("  Min-Heap: parent.val <= children.val (min always at root)");
        System.out.println("  Stored as ARRAY (complete binary tree property):");
        System.out.println("  parent(i) = (i-1)/2,  left(i) = 2i+1,  right(i) = 2i+2");
        System.out.println("  Java PriorityQueue is a min-heap. insert/remove: O(log n)");
        System.out.println("  Used in: Dijkstra's algorithm, task scheduling, event simulation");

        sub("7. Java TreeMap / TreeSet — Red-Black Tree");
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("banana", 2); treeMap.put("apple", 5);
        treeMap.put("cherry", 1); treeMap.put("date", 8);
        System.out.println("  TreeMap (Red-Black Tree) — always O(log n), keys sorted:");
        treeMap.forEach((k,v) -> System.out.println("    " + k + " → " + v));
        System.out.println("  firstKey(): " + treeMap.firstKey() +
                "  lastKey(): " + treeMap.lastKey());
        System.out.println("  floorKey(\"c\"): " + treeMap.floorKey("c") +
                "  ceilingKey(\"c\"): " + treeMap.ceilingKey("c"));


        // ═══════════════════════════════════════════════════════
        // TOPIC 7 — INTERVIEW-LEVEL PROBLEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 7 — Interview-Level Problems");

        TreeNode interview = buildDemoTree();

        sub("Problem 1: Max Depth");
        System.out.println("  Tree: " + levelOrder(interview));
        System.out.println("  maxDepth = " + maxDepth(interview));
        System.out.println("  Technique: recursion. 1 + max(left, right). O(n)/O(h)");

        sub("Problem 2: Diameter of Binary Tree");
        diameterResult[0] = 0;
        System.out.println("  diameter = " + diameter(interview));
        System.out.println("  Longest path: 4→2→1→3→6 = 4 edges");
        System.out.println("  Technique: at each node, leftHeight+rightHeight = path length");
        System.out.println("  Track global max. O(n)/O(h)");

        sub("Problem 3: Is Symmetric");
        TreeNode sym = new TreeNode(1);
        sym.left = new TreeNode(2); sym.right = new TreeNode(2);
        sym.left.left = new TreeNode(3); sym.left.right = new TreeNode(4);
        sym.right.left = new TreeNode(4); sym.right.right = new TreeNode(3);

        TreeNode asym = new TreeNode(1);
        asym.left = new TreeNode(2); asym.right = new TreeNode(2);
        asym.left.right = new TreeNode(3); asym.right.right = new TreeNode(3);

        System.out.println("  Symmetric tree:    " + isSymmetric(sym) + " ✓");
        System.out.println("  Asymmetric tree:   " + isSymmetric(asym) + " ✓");
        System.out.println("  Technique: compare left.left with right.right (outer)");
        System.out.println("             and left.right with right.left (inner). O(n)/O(h)");

        sub("Problem 4: Lowest Common Ancestor");
        TreeNode lcaTree = buildBST();
        BST lcaBstHelper = new BST();
        for (int v : new int[]{50,30,70,20,40,60,80}) lcaBstHelper.insert(v);
        int p=20, q=40;
        TreeNode lca = lcaBST(lcaBstHelper.root, p, q);
        System.out.println("  BST: " + new ArrayList<>(Arrays.asList(50,30,70,20,40,60,80)));
        System.out.printf("  LCA(%d, %d) = %d  (both in left subtree of 30)%n", p, q, lca.val);
        System.out.printf("  LCA(%d, %d) = %d  (split at 50)%n", 20, 60,
                lcaBST(lcaBstHelper.root, 20, 60).val);
        System.out.println("  BST technique: if both < root → go left; both > root → go right");
        System.out.println("  Split point = LCA. O(log n)/O(h)");

        sub("Problem 5: Path Sum");
        TreeNode pathTree = buildDemoTree();
        // tree: 1→2→4 = 7, 1→2→5 = 8, 1→3→6 = 10
        System.out.println("  Tree levels: " + levelOrder(pathTree));
        System.out.println("  hasPathSum(7):  " + hasPathSum(pathTree, 7)  +
                "  (1→2→4=7)");
        System.out.println("  hasPathSum(10): " + hasPathSum(pathTree, 10) +
                " (1→3→6=10)");
        System.out.println("  hasPathSum(15): " + hasPathSum(pathTree, 15) +
                " (no such path)");
        System.out.println("  Technique: subtract root.val from target, recurse. O(n)/O(h)");
        System.out.println("  All paths: " + pathSumAll(pathTree, 8) + "  (1+2+5=8)");

        sub("Problem 6: Validate BST");
        TreeNode validBST   = buildBST();
        TreeNode invalidBST = new TreeNode(5);
        invalidBST.left     = new TreeNode(1);
        invalidBST.right    = new TreeNode(4);
        invalidBST.right.left  = new TreeNode(3);
        invalidBST.right.right = new TreeNode(6);
        System.out.println("  Valid BST:   " + isValidBST(validBST));
        System.out.println("  Invalid BST: " + isValidBST(invalidBST) +
                "  (5→4 violates left subtree must be < root)");
        System.out.println("  Technique: pass valid range [min, max] to each node.");
        System.out.println("  Naive (left<root<right) misses cross-subtree violations. O(n)/O(h)");

        sub("Problem 7: Right Side View");
        System.out.println("  Tree levels: " + levelOrder(interview));
        System.out.println("  Right side view: " + rightSideView(interview));
        System.out.println("  Technique: BFS, take last node of each level. O(n)/O(n)");

        sub("Problem 8: Serialize & Deserialize");
        TreeNode original   = buildDemoTree();
        String   serialized = serialize(original);
        System.out.println("  Serialized: \"" + serialized + "\"");
        idx[0] = 0;
        TreeNode rebuilt = deserialize(serialized.split(","));
        List<Integer> origLevels  = new ArrayList<>();
        List<Integer> rebuiltLevels = new ArrayList<>();
        inOrder(original, origLevels);
        inOrder(rebuilt,  rebuiltLevels);
        System.out.println("  Original in-order:  " + origLevels);
        System.out.println("  Rebuilt  in-order:  " + rebuiltLevels);
        System.out.println("  Match: " + origLevels.equals(rebuiltLevels) + " ✓");
        System.out.println("  Technique: pre-order with null markers. O(n)/O(n)");

        sub("Problem 9: Build Tree from Pre+In Order");
        int[] preorder = {3, 9, 20, 15, 7};
        int[] inorder  = {9, 3, 15, 20, 7};
        TreeNode built = buildTree(preorder, inorder);
        List<Integer> builtPost = new ArrayList<>();
        postOrder(built, builtPost);
        System.out.println("  Pre-order: " + Arrays.toString(preorder));
        System.out.println("  In-order:  " + Arrays.toString(inorder));
        System.out.println("  Built tree post-order: " + builtPost);
        System.out.println("  Technique: pre[0]=root, find in in-order → splits halves.");
        System.out.println("  Use HashMap for O(1) in-order lookup. O(n)/O(n)");

        sub("Problem 10: Zigzag Level Order");
        System.out.println("  Zigzag: " + zigzagLevelOrder(interview));
        System.out.println("  Level 0 (L→R): [1]");
        System.out.println("  Level 1 (R→L): [3, 2]");
        System.out.println("  Level 2 (L→R): [4, 5, 6]");
        System.out.println("  Technique: BFS + direction flag. Fill array at computed index.");
        System.out.println("  O(n)/O(n)");

        // GRAND SUMMARY
        section("GRAND SUMMARY");
        System.out.println();
        System.out.println("  Tree = hierarchical structure with root, edges, and leaves.");
        System.out.println();
        System.out.println("  TRAVERSALS (all O(n) time):");
        System.out.println("  Pre-order  O(h) space → copy/serialize trees");
        System.out.println("  In-order   O(h) space → BST sort (gives ascending order)");
        System.out.println("  Post-order O(h) space → delete/compute bottom-up");
        System.out.println("  Level-order O(w) space → shortest path, level problems");
        System.out.println();
        System.out.println("  BST OPERATIONS: O(log n) avg, O(n) worst (skewed)");
        System.out.println("  Use AVL/Red-Black for guaranteed O(log n)");
        System.out.println();
        System.out.println("  GOLDEN RULES:");
        System.out.println("  1. DFS = Stack/Recursion. BFS = Queue.");
        System.out.println("  2. BST in-order → always sorted. Use for kth element.");
        System.out.println("  3. Validate BST with range [min,max], NOT just left<root<right");
        System.out.println("  4. LCA: post-order returns first node where both p,q found");
        System.out.println("  5. Diameter = max(leftH + rightH) at any node, not just root");
        System.out.println("  6. Path problems: backtrack after visiting leaf");
        System.out.println("  7. Balance matters: skewed tree → O(n) per op + stack overflow");
    }
}
