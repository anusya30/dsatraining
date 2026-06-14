# Advanced DSA Problems — Complete Guide
> Integration of Patterns & Multi-Concept Thinking
> Covers: Multi-Pattern Integration · Graph+DP · Advanced Trees · Advanced Strings · Sliding Window+Hashing · Real Interview Problems · Optimization Thinking

---

## Table of Contents
1. [Multi-Pattern Integration](#1-multi-pattern-integration)
2. [Graph + DP Problems](#2-graph--dp-problems)
3. [Advanced Tree Problems](#3-advanced-tree-problems)
4. [Advanced String Algorithms](#4-advanced-string-algorithms)
5. [Sliding Window + Hashing Hybrids](#5-sliding-window--hashing-hybrids)
6. [Real Interview-Style Problems](#6-real-interview-style-problems)
7. [Optimization Thinking](#7-optimization-thinking)

---

## 1. Multi-Pattern Integration

### The Core Idea
Most hard DSA problems don't fit a single pattern — they require **stacking multiple techniques**.
The skill is recognizing which patterns to combine and in what order.

```
Recognition Framework:
  1. Identify the data structure (array, graph, tree, string)
  2. Identify the operation (search, count, optimize, construct)
  3. Identify constraints (sorted? bounded? repeats? cycles?)
  4. Map to primary pattern (DP, greedy, two pointer, BFS...)
  5. Identify secondary patterns needed to execute the primary

Example: "Find longest path in matrix where each cell > previous"
  Data: 2D matrix → graph
  Op:   longest path → DP
  Constraint: strictly increasing → directed edges only
  Primary: DP on DAG (topological DP)
  Secondary: DFS + memoization on each cell
  Combined: DFS + memo + DAG shortest/longest path
```

---

### 1.1 Longest Increasing Path in Matrix (DFS + Memo)

#### Pattern Combination: DFS + Memoization + DAG

```
Matrix:
  9  9  4
  6  6  8
  2  1  1

From each cell, move to adjacent cells with strictly larger values.
9→_ (no neighbors), 6→9(+), 6→8(+9), 2→6→9 or 6→8→9...
Longest path: 1→2→6→9 = length 4

Why DFS + Memo?
  - No cycles (strictly increasing → DAG)
  - Overlapping subproblems (many cells lead to same neighbors)
  - DFS explores all paths; memo avoids recomputation
```

```java
public static int longestIncreasingPath(int[][] matrix) {
    int m = matrix.length, n = matrix[0].length;
    int[][] memo = new int[m][n];   // memo[i][j] = longest path starting at (i,j)
    int maxLen = 0;

    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            maxLen = Math.max(maxLen, dfsLIP(matrix, i, j, memo));

    return maxLen;
}

private static int dfsLIP(int[][] matrix, int r, int c, int[][] memo) {
    if (memo[r][c] != 0) return memo[r][c];   // Cache hit

    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    int max = 1;   // At minimum, path of length 1 (cell itself)

    for (int[] d : dirs) {
        int nr = r + d[0], nc = c + d[1];
        if (nr >= 0 && nr < matrix.length && nc >= 0 && nc < matrix[0].length
                && matrix[nr][nc] > matrix[r][c]) {
            max = Math.max(max, 1 + dfsLIP(matrix, nr, nc, memo));
        }
    }
    return memo[r][c] = max;
}
// Time: O(m×n)  Space: O(m×n)
// [[9,9,4],[6,6,8],[2,1,1]] → 4 (path: 1→2→6→9)
```

---

### 1.2 Number of Islands II — Online (Union-Find + Grid)

#### Pattern Combination: Union-Find + 2D Grid + Online Processing

```
Process land additions one by one, answer "how many islands?" after each.

Technique: Union-Find on grid cells (convert 2D index to 1D: i*cols + j)

For each new land cell:
  1. Initialize as its own component (new island)
  2. Check 4 neighbors — if land, union with neighbor
  3. Count = previous count + 1 - (number of successful unions)
```

```java
public static List<Integer> numIslandsII(int m, int n, int[][] positions) {
    int[] parent = new int[m * n];
    int[] rank   = new int[m * n];
    Arrays.fill(parent, -1);     // -1 = water

    List<Integer> result = new ArrayList<>();
    int islands = 0;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    for (int[] pos : positions) {
        int r = pos[0], c = pos[1];
        int idx = r * n + c;

        if (parent[idx] != -1) {    // Already land (duplicate)
            result.add(islands);
            continue;
        }

        parent[idx] = idx;           // New island
        islands++;

        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            int nidx = nr * n + nc;
            if (nr >= 0 && nr < m && nc >= 0 && nc < n && parent[nidx] != -1) {
                if (ufUnion(parent, rank, idx, nidx)) {
                    islands--;      // Two islands merged into one
                }
            }
        }
        result.add(islands);
    }
    return result;
}
// positions=[[0,0],[0,1],[1,2],[2,1]] in 3×3 → [1,1,2,3]
```

---

### 1.3 Sudoku Solver (Backtracking + Bitmasking)

#### Pattern Combination: Backtracking + Bit Manipulation for O(1) Constraint Checks

```java
public static boolean solveSudoku(char[][] board) {
    // Use bitmasks for row/col/box constraints (O(1) check instead of O(9) scan)
    int[] rows = new int[9], cols = new int[9], boxes = new int[9];

    // Initialize constraint masks
    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            if (board[r][c] != '.') {
                int bit = 1 << (board[r][c] - '1');
                rows[r] |= bit;
                cols[c] |= bit;
                boxes[(r/3)*3 + c/3] |= bit;
            }
        }
    }
    return backtrackSudoku(board, rows, cols, boxes, 0);
}

private static boolean backtrackSudoku(char[][] board, int[] rows, int[] cols,
                                         int[] boxes, int pos) {
    // Find next empty cell
    while (pos < 81 && board[pos/9][pos%9] != '.') pos++;
    if (pos == 81) return true;   // All cells filled

    int r = pos / 9, c = pos % 9, box = (r/3)*3 + c/3;
    int used = rows[r] | cols[c] | boxes[box];   // All used digits as bitmask

    for (int d = 1; d <= 9; d++) {
        int bit = 1 << (d - 1);
        if ((used & bit) != 0) continue;   // Digit already used in row/col/box

        board[r][c] = (char) ('0' + d);
        rows[r] |= bit; cols[c] |= bit; boxes[box] |= bit;

        if (backtrackSudoku(board, rows, cols, boxes, pos + 1)) return true;

        board[r][c] = '.';
        rows[r] &= ~bit; cols[c] &= ~bit; boxes[box] &= ~bit;
    }
    return false;
}
```

---

### 1.4 Word Search II (Trie + Backtracking)

#### Pattern Combination: Trie + DFS Backtracking + Pruning

```
Why Trie + DFS instead of DFS for each word separately?
  - k words, each of length L, board m×n
  - Naive: O(k × m×n × 4^L)
  - Trie: O(m×n × 4^L) — search all words simultaneously!

Trie prunes dead branches instantly:
  At each cell, if no word in Trie has current prefix → stop immediately
```

```java
public static List<String> findWords(char[][] board, String[] words) {
    TrieNode root = buildTrie(words);
    List<String> result = new ArrayList<>();
    int m = board.length, n = board[0].length;

    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            dfsWordSearch(board, i, j, root, result);

    return result;
}

private static void dfsWordSearch(char[][] board, int r, int c,
                                    TrieNode node, List<String> result) {
    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length
            || board[r][c] == '#') return;   // Out of bounds or visited

    char ch = board[r][c];
    TrieNode next = node.children[ch - 'a'];
    if (next == null) return;     // No word with this prefix → prune

    if (next.word != null) {
        result.add(next.word);
        next.word = null;         // Avoid duplicates
    }

    board[r][c] = '#';            // Mark visited
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    for (int[] d : dirs)
        dfsWordSearch(board, r+d[0], c+d[1], next, result);
    board[r][c] = ch;             // Restore (backtrack)

    // Optimization: prune empty Trie branches
    if (next.isEmpty()) node.children[ch - 'a'] = null;
}

static class TrieNode {
    TrieNode[] children = new TrieNode[26];
    String word = null;
    boolean isEmpty() {
        for (TrieNode c : children) if (c != null) return false;
        return word == null;
    }
}

static TrieNode buildTrie(String[] words) {
    TrieNode root = new TrieNode();
    for (String w : words) {
        TrieNode cur = root;
        for (char c : w.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) cur.children[i] = new TrieNode();
            cur = cur.children[i];
        }
        cur.word = w;
    }
    return root;
}
// board=[["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]]
// words=["oath","pea","eat","rain"] → ["eat","oath"]
```

---

## 2. Graph + DP Problems

### 2.1 Cheapest Flights Within K Stops (Bellman-Ford + DP)

#### Problem
Find the cheapest flight from src to dst with at most K stops.

#### Why Bellman-Ford variant?
```
Regular Dijkstra doesn't account for the "at most K stops" constraint.
We need: dp[k][v] = min cost to reach v using at most k edges

Bellman-Ford naturally computes this:
  Pass k = relaxation using exactly k edges from source
  After K+1 passes: cheapest path with at most K stops
```

```java
public static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    // K+1 passes of Bellman-Ford (K stops = K+1 edges)
    for (int pass = 0; pass <= k; pass++) {
        int[] temp = dist.clone();      // Use snapshot to ensure at most k edges per pass

        for (int[] flight : flights) {
            int u = flight[0], v = flight[1], w = flight[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < temp[v]) {
                temp[v] = dist[u] + w;
            }
        }
        dist = temp;
    }
    return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
}
// n=3 flights=[[0,1,100],[1,2,100],[0,2,500]] src=0 dst=2 k=1 → 200
// n=3 flights=[[0,1,100],[1,2,100],[0,2,500]] src=0 dst=2 k=0 → 500
```

---

### 2.2 Shortest Path Visiting All Nodes (BFS + Bitmask DP)

#### Problem
Find the shortest path that visits every node in an undirected graph.

```
State: (currentNode, visitedMask)
visitedMask is a bitmask of which nodes have been visited.

BFS on state space: guarantees shortest path.
Bitmask DP: avoids revisiting same (node, visited-set) state.

Total states: n × 2^n  (n nodes × 2^n possible visit masks)
Time: O(n² × 2^n)
```

```java
public static int shortestPathAllNodes(int[][] graph) {
    int n = graph.length;
    int fullMask = (1 << n) - 1;

    // BFS state: [node, visitedMask]
    Queue<int[]> queue = new LinkedList<>();
    Set<String> visited = new HashSet<>();

    for (int i = 0; i < n; i++) {
        int mask = 1 << i;
        queue.offer(new int[]{i, mask});
        visited.add(i + "," + mask);
    }

    int steps = 0;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int s = 0; s < size; s++) {
            int[] state = queue.poll();
            int node = state[0], mask = state[1];

            if (mask == fullMask) return steps;  // All nodes visited

            for (int next : graph[node]) {
                int newMask = mask | (1 << next);
                String key = next + "," + newMask;
                if (!visited.contains(key)) {
                    visited.add(key);
                    queue.offer(new int[]{next, newMask});
                }
            }
        }
        steps++;
    }
    return steps;
}
// [[1,2,3],[0],[0],[0]] → 4 (visit all 4 nodes)
```

---

### 2.3 Minimum Cost to Reach Destination in Time (DP on Graph)

```java
// dp[time][node] = minimum cost to reach node in exactly 'time' minutes
public static int minCost(int maxTime, int[][] edges, int[] passingFees, int n) {
    int INF = Integer.MAX_VALUE / 2;
    int[][] dp = new int[maxTime + 1][n];
    for (int[] row : dp) Arrays.fill(row, INF);
    dp[0][0] = passingFees[0];

    for (int t = 1; t <= maxTime; t++) {
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], w = edge[2];
            if (t >= w) {
                if (dp[t-w][u] != INF)
                    dp[t][v] = Math.min(dp[t][v], dp[t-w][u] + passingFees[v]);
                if (dp[t-w][v] != INF)
                    dp[t][u] = Math.min(dp[t][u], dp[t-w][v] + passingFees[u]);
            }
        }
    }

    int ans = INF;
    for (int t = 1; t <= maxTime; t++) ans = Math.min(ans, dp[t][n-1]);
    return ans == INF ? -1 : ans;
}
```

---

### 2.4 Count Paths with Given Sum in Grid (DP + Prefix Sum)

```java
public static int pathSum(int[][] grid, int target) {
    int m = grid.length, n = grid[0].length;
    // dp[i][j] = number of paths from (0,0) to (i,j) with sum = target
    // Use prefix sum approach + HashMap at each row
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1);
    int runningSum = 0, count = 0;

    // For grid paths (only right/down), flatten to 1D DP approach
    int[][] dp = new int[m][n];
    Map<Integer, Integer>[] rowPrefix = new HashMap[m * n];

    // Standard grid unique paths with constraint
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            dp[i][j] = (i == 0 && j == 0) ? (grid[i][j] == target ? 1 : 0)
                     : (i == 0) ? (dp[i][j-1] + grid[i][j] == target ? 1 : 0)
                     : (j == 0) ? (dp[i-1][j] + grid[i][j] == target ? 1 : 0)
                     : dp[i-1][j] + dp[i][j-1];
        }
    }
    return dp[m-1][n-1];
}
```

---

## 3. Advanced Tree Problems

### 3.1 Serialize and Deserialize Binary Tree

#### Pattern: Preorder DFS + String Encoding

```java
public static String serialize(TreeNode root) {
    StringBuilder sb = new StringBuilder();
    serializeDFS(root, sb);
    return sb.toString();
}

private static void serializeDFS(TreeNode node, StringBuilder sb) {
    if (node == null) { sb.append("null,"); return; }
    sb.append(node.val).append(",");
    serializeDFS(node.left,  sb);
    serializeDFS(node.right, sb);
}

public static TreeNode deserialize(String data) {
    Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
    return deserializeDFS(queue);
}

private static TreeNode deserializeDFS(Queue<String> queue) {
    String val = queue.poll();
    if ("null".equals(val)) return null;
    TreeNode node = new TreeNode(Integer.parseInt(val));
    node.left  = deserializeDFS(queue);
    node.right = deserializeDFS(queue);
    return node;
}
// Tree: 1→{2,3}, 3→{4,5}
// Serialized: "1,2,null,null,3,4,null,null,5,null,null,"
```

---

### 3.2 Binary Tree Maximum Path Sum

#### Pattern: DFS with Global Max Tracking

```
At each node, we have 4 choices for contribution:
  1. Just the node itself
  2. Node + left subtree path
  3. Node + right subtree path
  4. Node + left + right (forms a path through node — can't extend further up)

For returning to parent: max of choices 1,2,3 (can only go one direction up)
For global answer: max of all 4 choices
```

```java
public static int maxPathSum(TreeNode root) {
    int[] globalMax = {Integer.MIN_VALUE};
    maxPathDFS(root, globalMax);
    return globalMax[0];
}

private static int maxPathDFS(TreeNode node, int[] globalMax) {
    if (node == null) return 0;

    // Max contribution from left and right subtrees (ignore negatives)
    int left  = Math.max(0, maxPathDFS(node.left,  globalMax));
    int right = Math.max(0, maxPathDFS(node.right, globalMax));

    // Path through this node (connecting left and right)
    globalMax[0] = Math.max(globalMax[0], node.val + left + right);

    // Return max contribution this node can give to its parent
    return node.val + Math.max(left, right);
}
// [-10,9,20,null,null,15,7] → 42 (15+20+7)
// [1,2,3] → 6
// [-3] → -3
```

---

### 3.3 Binary Tree to Doubly Linked List (Morris Traversal)

#### Pattern: In-order Traversal + O(1) Space (Morris)

```java
// Convert BST to sorted doubly linked list (in-place, O(1) space)
public static TreeNode treeToDoublyList(TreeNode root) {
    if (root == null) return null;
    TreeNode[] prev = {null}, head = {null};

    inorderDLL(root, prev, head);

    // Connect head and tail to make it circular
    head[0].left = prev[0];
    prev[0].right = head[0];
    return head[0];
}

private static void inorderDLL(TreeNode node, TreeNode[] prev, TreeNode[] head) {
    if (node == null) return;
    inorderDLL(node.left, prev, head);

    if (prev[0] == null) {
        head[0] = node;      // Leftmost node is head
    } else {
        prev[0].right = node;
        node.left = prev[0];
    }
    prev[0] = node;

    inorderDLL(node.right, prev, head);
}
```

---

### 3.4 Lowest Common Ancestor with Parent Pointers

#### Pattern: Two Pointer on Linked Lists (Applied to Trees)

```java
// LCA using path equalization trick (like finding cycle in two linked lists)
public static TreeNode lcaWithParent(TreeNode p, TreeNode q) {
    TreeNode a = p, b = q;
    while (a != b) {
        a = (a == null) ? q : a.parent;   // When a reaches null, restart at q
        b = (b == null) ? p : b.parent;   // When b reaches null, restart at p
        // After at most |depth(p)|+|depth(q)| steps, they meet at LCA
    }
    return a;
}

// LCA without parent pointers (classic DFS)
public static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left  = lca(root.left,  p, q);
    TreeNode right = lca(root.right, p, q);
    if (left != null && right != null) return root;   // p and q on different sides
    return left != null ? left : right;
}
```

---

### 3.5 Recover Binary Search Tree (Morris + O(1) Space)

#### Pattern: Morris Inorder + Swap Detection

```java
// Two nodes are swapped — find and fix them
public static void recoverTree(TreeNode root) {
    TreeNode first = null, second = null, prev = null;
    TreeNode curr = root;

    // Morris inorder traversal
    while (curr != null) {
        if (curr.left == null) {
            if (prev != null && prev.val > curr.val) {
                if (first == null) first = prev;    // First violation
                second = curr;                       // Second violation (or update)
            }
            prev = curr;
            curr = curr.right;
        } else {
            TreeNode pred = curr.left;
            while (pred.right != null && pred.right != curr) pred = pred.right;

            if (pred.right == null) {
                pred.right = curr;
                curr = curr.left;
            } else {
                pred.right = null;
                if (prev != null && prev.val > curr.val) {
                    if (first == null) first = prev;
                    second = curr;
                }
                prev = curr;
                curr = curr.right;
            }
        }
    }
    // Swap the two misplaced nodes
    if (first != null && second != null) {
        int temp = first.val;
        first.val = second.val;
        second.val = temp;
    }
}
// [1,3,null,null,2] (3 and 1 swapped) → [3,1,null,null,2]
```

---

### 3.6 Vertical Order Traversal

#### Pattern: BFS + Sorting by (col, row, val)

```java
public static List<List<Integer>> verticalOrder(TreeNode root) {
    if (root == null) return new ArrayList<>();
    // Store (node, col, row) in BFS
    Queue<int[]> queue = new LinkedList<>();   // [nodeRef-encoded, col, row]
    Map<Integer, List<int[]>> colMap = new TreeMap<>();
    Map<TreeNode, int[]> nodeMap = new HashMap<>();

    nodeMap.put(root, new int[]{0, 0});        // {col, row}
    Queue<TreeNode> nodeQueue = new LinkedList<>();
    nodeQueue.offer(root);

    while (!nodeQueue.isEmpty()) {
        TreeNode node = nodeQueue.poll();
        int[] pos = nodeMap.get(node);
        int col = pos[0], row = pos[1];

        colMap.computeIfAbsent(col, k -> new ArrayList<>()).add(new int[]{row, node.val});

        if (node.left  != null) { nodeMap.put(node.left,  new int[]{col-1, row+1}); nodeQueue.offer(node.left);  }
        if (node.right != null) { nodeMap.put(node.right, new int[]{col+1, row+1}); nodeQueue.offer(node.right); }
    }

    List<List<Integer>> result = new ArrayList<>();
    for (List<int[]> nodes : colMap.values()) {
        nodes.sort((a, b) -> a[0] != b[0] ? a[0]-b[0] : a[1]-b[1]);
        List<Integer> col = new ArrayList<>();
        for (int[] n : nodes) col.add(n[1]);
        result.add(col);
    }
    return result;
}
```

---

## 4. Advanced String Algorithms

### 4.1 KMP — Knuth-Morris-Pratt Pattern Matching

#### Concept
KMP avoids re-examining characters by precomputing a **failure function** (lps array) that tells us how far to skip after a mismatch.

```
Pattern: "AABAAB"
lps:     [0,1,0,1,2,3]

lps[i] = length of longest proper prefix of pattern[0..i] that is also a suffix

"AABAAB":
  i=0: "A"      → no proper prefix = suffix → lps[0]=0
  i=1: "AA"     → "A" is prefix and suffix → lps[1]=1
  i=2: "AAB"    → no match → lps[2]=0
  i=3: "AABA"   → "A" prefix = "A" suffix → lps[3]=1
  i=4: "AABAA"  → "AA" prefix = "AA" suffix → lps[4]=2
  i=5: "AABAAB" → "AAB" prefix = "AAB" suffix → lps[5]=3
```

```java
public static List<Integer> kmpSearch(String text, String pattern) {
    int[] lps = buildLPS(pattern);
    List<Integer> matches = new ArrayList<>();
    int i = 0, j = 0;   // i=text pointer, j=pattern pointer

    while (i < text.length()) {
        if (text.charAt(i) == pattern.charAt(j)) {
            i++; j++;
            if (j == pattern.length()) {
                matches.add(i - j);   // Match found at index i-j
                j = lps[j - 1];       // Continue using LPS for overlapping matches
            }
        } else {
            if (j != 0) {
                j = lps[j - 1];       // Skip using LPS (don't re-examine text[i])
            } else {
                i++;
            }
        }
    }
    return matches;
}

private static int[] buildLPS(String pattern) {
    int n = pattern.length();
    int[] lps = new int[n];
    int len = 0, i = 1;

    while (i < n) {
        if (pattern.charAt(i) == pattern.charAt(len)) {
            lps[i++] = ++len;
        } else {
            if (len != 0) len = lps[len - 1];   // Fall back using LPS
            else lps[i++] = 0;
        }
    }
    return lps;
}
// text="AABABCABCABABABABC" pattern="ABABC" → [2,9,13]
// Time: O(n+m)  Space: O(m)
```

---

### 4.2 Rabin-Karp — Rolling Hash Pattern Matching

#### Concept
Use a **rolling hash** to compare window hashes in O(1). Only verify character-by-character on hash match.

```
Hash function: h = (c₀×bⁿ⁻¹ + c₁×bⁿ⁻² + ... + cₙ₋₁) mod p

Rolling update:
  Remove leftmost char:  h = h - c₀×bⁿ⁻¹
  Add new rightmost char: h = h×b + cₙ
  → O(1) per slide
```

```java
public static List<Integer> rabinKarp(String text, String pattern) {
    int n = text.length(), m = pattern.length();
    if (m > n) return new ArrayList<>();

    long BASE = 31, MOD = 1_000_000_007L;
    long power = 1;
    for (int i = 0; i < m - 1; i++) power = power * BASE % MOD;

    long patHash = 0, winHash = 0;
    for (int i = 0; i < m; i++) {
        patHash = (patHash * BASE + pattern.charAt(i)) % MOD;
        winHash = (winHash * BASE + text.charAt(i)) % MOD;
    }

    List<Integer> result = new ArrayList<>();
    for (int i = 0; i <= n - m; i++) {
        if (winHash == patHash && text.substring(i, i+m).equals(pattern)) {
            result.add(i);   // Verify on hash match (handle collisions)
        }
        if (i < n - m) {
            winHash = (winHash - text.charAt(i) * power % MOD + MOD) % MOD;
            winHash = (winHash * BASE + text.charAt(i + m)) % MOD;
        }
    }
    return result;
}
// Average O(n+m), worst O(nm) on collision
// Used in: plagiarism detection, substring search, DNA matching
```

---

### 4.3 Manacher's Algorithm — O(n) Longest Palindromic Substring

#### Concept
Find all palindromes simultaneously using symmetry. Instead of O(n²) center expansion, reuse previously computed palindrome radii.

```
Transform string: "abc" → "#a#b#c#" (handles even/odd palindromes uniformly)
p[i] = radius of palindrome centered at i in transformed string

Key insight: if we're inside a known palindrome [L,R] centered at C,
  p[i] ≥ min(p[mirror], R-i)  where mirror = 2C-i
  We might expand further from this known minimum.
```

```java
public static String longestPalindrome(String s) {
    // Transform: "abc" → "#a#b#c#"
    StringBuilder t = new StringBuilder("#");
    for (char c : s.toCharArray()) t.append(c).append('#');
    String T = t.toString();
    int n = T.length();
    int[] p = new int[n];
    int center = 0, right = 0;   // Rightmost palindrome boundary

    for (int i = 0; i < n; i++) {
        if (i < right) {
            int mirror = 2 * center - i;
            p[i] = Math.min(right - i, p[mirror]);   // Reuse known info
        }
        // Try to expand beyond known boundary
        int l = i - p[i] - 1, r = i + p[i] + 1;
        while (l >= 0 && r < n && T.charAt(l) == T.charAt(r)) {
            p[i]++; l--; r++;
        }
        // Update rightmost boundary
        if (i + p[i] > right) { center = i; right = i + p[i]; }
    }

    // Find maximum palindrome
    int maxLen = 0, maxCenter = 0;
    for (int i = 0; i < n; i++) {
        if (p[i] > maxLen) { maxLen = p[i]; maxCenter = i; }
    }
    int start = (maxCenter - maxLen) / 2;
    return s.substring(start, start + maxLen);
}
// "babad" → "bab" (or "aba")   O(n) time, O(n) space
// "cbbd"  → "bb"
```

---

### 4.4 Suffix Array + LCP Array

#### Concept
Sort all suffixes of a string. LCP (Longest Common Prefix) array gives LCP between adjacent suffixes.

```
s = "banana"
Suffixes:
  0: banana
  1: anana
  2: nana
  3: ana
  4: na
  5: a

Sorted:
  5: a
  3: ana
  1: anana
  0: banana
  4: na
  2: nana

SA = [5,3,1,0,4,2]
LCP = [0,1,3,0,0,2]  (LCP between consecutive sorted suffixes)

Applications:
  - Longest repeated substring: max(LCP) = 3 ("ana")
  - Number of distinct substrings: n(n+1)/2 - sum(LCP)
  - Pattern matching: binary search on suffix array
```

```java
public static int[] buildSuffixArray(String s) {
    int n = s.length();
    Integer[] sa = new Integer[n];
    for (int i = 0; i < n; i++) sa[i] = i;
    Arrays.sort(sa, (a, b) -> s.substring(a).compareTo(s.substring(b)));  // O(n² log n)
    int[] result = new int[n];
    for (int i = 0; i < n; i++) result[i] = sa[i];
    return result;
}

public static int longestRepeatedSubstring(String s) {
    int[] sa = buildSuffixArray(s);
    int maxLCP = 0;
    for (int i = 1; i < sa.length; i++) {
        maxLCP = Math.max(maxLCP, lcp(s, sa[i-1], sa[i]));
    }
    return maxLCP;
}

private static int lcp(String s, int i, int j) {
    int len = 0;
    while (i < s.length() && j < s.length() && s.charAt(i) == s.charAt(j)) {
        len++; i++; j++;
    }
    return len;
}
// "banana" → 3 ("ana" appears twice)
// "abcabc" → 3 ("abc" appears twice)
```

---

## 5. Sliding Window + Hashing Hybrids

### 5.1 Subarray Sum Equals K (Prefix Sum + HashMap)

#### Pattern: Prefix Sum + Hash Map — The Classic Hybrid

```
Key insight: subarray sum [i..j] = prefixSum[j] - prefixSum[i-1]
We want: prefixSum[j] - prefixSum[i-1] = k
→    prefixSum[i-1] = prefixSum[j] - k

As we scan right to left, check if (currentSum - k) exists in the prefix sum map.
```

```java
public static int subarraySumEqualsK(int[] nums, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1);    // Empty prefix: sum=0, count=1
    int sum = 0, count = 0;

    for (int num : nums) {
        sum += num;
        // How many prefixes have sum = (sum - k)?
        // Those prefixes, when removed, leave a subarray summing to k
        count += prefixCount.getOrDefault(sum - k, 0);
        prefixCount.merge(sum, 1, Integer::sum);
    }
    return count;
}
// [1,1,1], k=2 → 2 ([1,1] starting at index 0 and 1)
// [1,2,3], k=3 → 2 ([1,2] and [3])
// [1,-1,0], k=0 → 3

// WHY THIS WORKS:
// At each position j, sum = prefixSum[j]
// We look for how many previous positions i have prefixSum[i] = sum - k
// Because prefixSum[j] - prefixSum[i] = k means nums[i+1..j] sums to k
```

---

### 5.2 Longest Subarray with Sum Divisible by K

```java
public static int longestSubarrayDivK(int[] nums, int k) {
    Map<Integer, Integer> remainderIndex = new HashMap<>();
    remainderIndex.put(0, -1);    // Empty prefix has remainder 0
    int sum = 0, maxLen = 0;

    for (int i = 0; i < nums.length; i++) {
        sum += nums[i];
        int rem = ((sum % k) + k) % k;    // Handle negative numbers

        if (remainderIndex.containsKey(rem)) {
            maxLen = Math.max(maxLen, i - remainderIndex.get(rem));
        } else {
            remainderIndex.put(rem, i);    // Store first occurrence
        }
    }
    return maxLen;
}
// [23,2,4,6,7], k=6 → 4 (subarray [2,4,6,7] doesn't work... [23,2,4,6,7]=42 div 6)
// [23,2,6,4,7], k=6 → 4
```

---

### 5.3 Count Subarrays with K Different Integers (AtMost trick)

```
Count(exactly k distinct) = Count(at most k) - Count(at most k-1)

This converts "exactly k" to two "at most k" sliding window problems.
```

```java
public static int subarraysWithKDistinct(int[] nums, int k) {
    return atMostK(nums, k) - atMostK(nums, k - 1);
}

private static int atMostK(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    int left = 0, count = 0;

    for (int right = 0; right < nums.length; right++) {
        freq.merge(nums[right], 1, Integer::sum);

        while (freq.size() > k) {
            int leftNum = nums[left++];
            freq.merge(leftNum, -1, Integer::sum);
            if (freq.get(leftNum) == 0) freq.remove(leftNum);
        }
        count += right - left + 1;   // All subarrays ending at right
    }
    return count;
}
// [1,2,1,2,3], k=2 → 7
// [1,2,1,3,4], k=3 → 3
```

---

### 5.4 Substring with Concatenation of All Words

```java
public static List<Integer> findSubstring(String s, String[] words) {
    if (s.isEmpty() || words.length == 0) return new ArrayList<>();
    int wordLen = words[0].length(), numWords = words.length;
    int windowLen = wordLen * numWords;
    List<Integer> result = new ArrayList<>();

    Map<String, Integer> wordCount = new HashMap<>();
    for (String w : words) wordCount.merge(w, 1, Integer::sum);

    // Try each starting offset within a word-length window
    for (int i = 0; i < wordLen; i++) {
        Map<String, Integer> seen = new HashMap<>();
        int left = i, matched = 0;

        for (int right = i; right + wordLen <= s.length(); right += wordLen) {
            String word = s.substring(right, right + wordLen);

            if (wordCount.containsKey(word)) {
                seen.merge(word, 1, Integer::sum);
                matched++;

                // Too many of this word → shrink window
                while (seen.get(word) > wordCount.get(word)) {
                    String leftWord = s.substring(left, left + wordLen);
                    seen.merge(leftWord, -1, Integer::sum);
                    matched--;
                    left += wordLen;
                }

                if (matched == numWords) result.add(left);
            } else {
                seen.clear(); matched = 0; left = right + wordLen;
            }
        }
    }
    return result;
}
// s="barfoothefoobarman" words=["foo","bar"] → [0,9]
```

---

### 5.5 Maximum Points on a Line (HashMap + GCD)

```java
public static int maxPointsOnLine(int[][] points) {
    int n = points.length;
    if (n <= 2) return n;
    int maxPoints = 2;

    for (int i = 0; i < n; i++) {
        Map<String, Integer> slopeCount = new HashMap<>();
        int duplicate = 1;

        for (int j = i + 1; j < n; j++) {
            int dx = points[j][0] - points[i][0];
            int dy = points[j][1] - points[i][1];

            if (dx == 0 && dy == 0) { duplicate++; continue; }

            int g = gcd(Math.abs(dx), Math.abs(dy));
            dx /= g; dy /= g;

            // Normalize slope representation
            if (dx < 0) { dx = -dx; dy = -dy; }
            else if (dx == 0) dy = Math.abs(dy);

            String slope = dx + "/" + dy;
            slopeCount.merge(slope, 1, Integer::sum);
            maxPoints = Math.max(maxPoints, slopeCount.get(slope) + duplicate);
        }
    }
    return maxPoints;
}
static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }
// [[1,1],[2,2],[3,3]] → 3
// [[1,1],[3,2],[5,3],[4,1],[2,3],[1,4]] → 4
```

---

## 6. Real Interview-Style Problems

### 6.1 LRU Cache (HashMap + Doubly Linked List)

#### Design an LRU Cache with O(1) get and put

```java
class LRUCache {
    private int capacity;
    private Map<Integer, DLLNode> map;
    private DLLNode head, tail;    // Dummy head (MRU side) and tail (LRU side)

    LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new DLLNode(0, 0);
        tail = new DLLNode(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        DLLNode node = map.get(key);
        remove(node);
        addToFront(node);         // Move to MRU position
        return node.val;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            DLLNode node = map.get(key);
            node.val = value;
            remove(node);
            addToFront(node);
        } else {
            if (map.size() == capacity) {
                DLLNode lru = tail.prev;   // Least recently used
                remove(lru);
                map.remove(lru.key);
            }
            DLLNode newNode = new DLLNode(key, value);
            addToFront(newNode);
            map.put(key, newNode);
        }
    }

    private void remove(DLLNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToFront(DLLNode node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    static class DLLNode {
        int key, val;
        DLLNode prev, next;
        DLLNode(int k, int v) { key = k; val = v; }
    }
}
// get O(1), put O(1) — both operations
```

---

### 6.2 Median from Data Stream (Two Heaps)

```java
class MedianFinder {
    PriorityQueue<Integer> lower;   // Max-heap: lower half
    PriorityQueue<Integer> upper;   // Min-heap: upper half

    MedianFinder() {
        lower = new PriorityQueue<>(Collections.reverseOrder());
        upper = new PriorityQueue<>();
    }

    void addNum(int num) {
        lower.offer(num);
        upper.offer(lower.poll());    // Balance: always push to upper via lower

        // Keep sizes balanced: lower can have at most 1 more than upper
        if (lower.size() < upper.size()) lower.offer(upper.poll());
    }

    double findMedian() {
        if (lower.size() > upper.size()) return lower.peek();
        return (lower.peek() + upper.peek()) / 2.0;
    }
}
// addNum(1), addNum(2) → median=1.5
// addNum(3)            → median=2.0
// Time: O(log n) add, O(1) findMedian
```

---

### 6.3 Skyline Problem (Priority Queue + Sweep Line)

```java
public static List<int[]> getSkyline(int[][] buildings) {
    // Collect all critical x-coordinates
    List<int[]> events = new ArrayList<>();
    for (int[] b : buildings) {
        events.add(new int[]{b[0], -b[2]});   // Start: negative height (enter)
        events.add(new int[]{b[1],  b[2]});   // End: positive height (leave)
    }
    // Sort: by x, then by height (starts before ends at same x)
    events.sort((a, b) -> a[0] != b[0] ? a[0]-b[0] : a[1]-b[1]);

    TreeMap<Integer, Integer> activeHeights = new TreeMap<>();
    activeHeights.put(0, 1);   // Ground level always present
    List<int[]> result = new ArrayList<>();
    int prevMax = 0;

    for (int[] event : events) {
        int x = event[0], h = Math.abs(event[1]);
        if (event[1] < 0) {   // Building start
            activeHeights.merge(h, 1, Integer::sum);
        } else {               // Building end
            activeHeights.merge(h, -1, Integer::sum);
            if (activeHeights.get(h) == 0) activeHeights.remove(h);
        }
        int currMax = activeHeights.lastKey();
        if (currMax != prevMax) {
            result.add(new int[]{x, currMax});
            prevMax = currMax;
        }
    }
    return result;
}
// [[2,9,10],[3,7,15],[5,12,12],[15,20,10],[19,24,8]] → [[2,10],[3,15],[7,12],[12,0],[15,10],[20,8],[24,0]]
```

---

### 6.4 Design Twitter (OOP + Heap)

```java
class Twitter {
    private int timestamp = 0;
    private Map<Integer, List<int[]>> tweets;    // userId → [(time, tweetId)]
    private Map<Integer, Set<Integer>> follows;  // userId → followees

    Twitter() {
        tweets  = new HashMap<>();
        follows = new HashMap<>();
    }

    void postTweet(int userId, int tweetId) {
        tweets.computeIfAbsent(userId, k -> new ArrayList<>())
              .add(new int[]{timestamp++, tweetId});
    }

    List<Integer> getNewsFeed(int userId) {
        // Max-heap: most recent tweets from user + all followees
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[0]-a[0]);
        Set<Integer> feed = new HashSet<>(follows.getOrDefault(userId, new HashSet<>()));
        feed.add(userId);

        for (int uid : feed) {
            List<int[]> userTweets = tweets.getOrDefault(uid, new ArrayList<>());
            for (int[] t : userTweets) pq.offer(t);
        }

        List<Integer> result = new ArrayList<>();
        while (!pq.isEmpty() && result.size() < 10) result.add(pq.poll()[1]);
        return result;
    }

    void follow(int followerId, int followeeId) {
        follows.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
    }

    void unfollow(int followerId, int followeeId) {
        follows.getOrDefault(followerId, new HashSet<>()).remove(followeeId);
    }
}
```

---

### 6.5 Trapping Rain Water II (3D — BFS + Min-Heap)

```java
public static int trapRainWaterII(int[][] heightMap) {
    if (heightMap.length < 3 || heightMap[0].length < 3) return 0;
    int m = heightMap.length, n = heightMap[0].length;
    boolean[][] visited = new boolean[m][n];
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

    // Add all border cells to heap
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (i == 0 || i == m-1 || j == 0 || j == n-1) {
                pq.offer(new int[]{heightMap[i][j], i, j});
                visited[i][j] = true;
            }
        }
    }

    int water = 0;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int h = curr[0], r = curr[1], c = curr[2];

        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || visited[nr][nc]) continue;
            visited[nr][nc] = true;
            water += Math.max(0, h - heightMap[nr][nc]);
            pq.offer(new int[]{Math.max(h, heightMap[nr][nc]), nr, nc});
        }
    }
    return water;
}
// [[1,4,3,1,3,2],[3,2,1,3,2,4],[2,3,3,2,3,1]] → 4
```

---

## 7. Optimization Thinking

### 7.1 The Optimization Framework

```
Step 1: BRUTE FORCE first
  Understand the problem completely before optimizing
  Brute force is often: O(n²), O(n³), O(2^n), O(n!)

Step 2: IDENTIFY the bottleneck
  Which part of the brute force is repeated unnecessarily?
  Example: "I recompute the sum of the same subarray many times"
  → Prefix sum eliminates redundant recomputation

Step 3: MATCH bottleneck to a pattern
  Repeated subproblems   → DP / memoization
  Sorted data lookups    → Binary search
  Range queries          → Segment tree / Fenwick
  String matching        → KMP / Rabin-Karp / Trie
  Connectivity           → Union-Find
  Shortest path          → Dijkstra / BFS
  Ordering dependencies  → Topological sort
  Frequency tracking     → HashMap
  Monotonic property     → Monotonic stack/deque

Step 4: VERIFY the optimization
  Does it maintain correctness?
  Does it improve worst-case complexity?
  Does it help average case?

Step 5: SPACE vs TIME trade-off
  Can we use more space to save time?
  Can we reduce space at the cost of time?
```

### 7.2 Monotonic Stack — Next Greater Element

```java
// O(n) solution using monotonic stack (each element pushed/popped once)
public static int[] nextGreaterElement(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();  // Stores indices, values decreasing

    for (int i = 0; i < n; i++) {
        // Pop all elements smaller than current — current is their next greater
        while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
            result[stack.pop()] = nums[i];
        }
        stack.push(i);
    }
    return result;
}
// [2,1,2,4,3] → [4,2,4,-1,-1]

// Circular array variant
public static int[] nextGreaterCircular(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();

    for (int i = 0; i < 2 * n; i++) {    // Loop twice for circular
        while (!stack.isEmpty() && nums[stack.peek()] < nums[i % n]) {
            result[stack.pop()] = nums[i % n];
        }
        if (i < n) stack.push(i);
    }
    return result;
}
// [1,2,1] → [2,-1,2]
```

---

### 7.3 Largest Rectangle in Histogram (Monotonic Stack)

```java
public static int largestRectangle(int[] heights) {
    Deque<Integer> stack = new ArrayDeque<>();  // Monotonically increasing indices
    int maxArea = 0;

    for (int i = 0; i <= heights.length; i++) {
        int currHeight = (i == heights.length) ? 0 : heights[i];

        // When we find a shorter bar, pop and calculate area for each taller bar
        while (!stack.isEmpty() && currHeight < heights[stack.peek()]) {
            int h = heights[stack.pop()];
            int w = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, h * w);
        }
        stack.push(i);
    }
    return maxArea;
}
// [2,1,5,6,2,3] → 10 (bars 5 and 6, width 2)
// [2,4]         → 4

// Extension: Maximal rectangle in binary matrix
public static int maximalRectangle(char[][] matrix) {
    int m = matrix.length, n = matrix[0].length;
    int[] heights = new int[n];
    int maxArea = 0;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            heights[j] = matrix[i][j] == '1' ? heights[j] + 1 : 0;
        }
        maxArea = Math.max(maxArea, largestRectangle(heights));
    }
    return maxArea;
}
// [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
// → 6
```

---

### 7.4 Jump Game III — BFS/DFS on Index Graph

```java
// Can we reach any index with value 0?
// From index i, can jump to i+arr[i] or i-arr[i]
public static boolean canReach(int[] arr, int start) {
    int n = arr.length;
    boolean[] visited = new boolean[n];
    Queue<Integer> queue = new LinkedList<>();
    queue.offer(start);

    while (!queue.isEmpty()) {
        int i = queue.poll();
        if (arr[i] == 0) return true;
        if (visited[i]) continue;
        visited[i] = true;

        int left  = i - arr[i];
        int right = i + arr[i];
        if (left  >= 0 && !visited[left])  queue.offer(left);
        if (right < n  && !visited[right]) queue.offer(right);
    }
    return false;
}
// [4,2,3,0,3,1,2], start=5 → true
// [3,0,2,1,2], start=2 → false
```

---

### 7.5 Complexity Comparison Table for Common Operations

```
Problem Type          Naive      Better      Best
─────────────────────────────────────────────────────────────────
Subarray sum = k      O(n²)      O(n)        O(n) [prefix + hash]
Substring search      O(nm)      O(n+m)      O(n+m) [KMP/RK]
Range sum query       O(n)       O(1) static O(log n) [seg tree]
Shortest path         O(V²)      O(E log V)  O(E+V) [BFS unweighted]
Connected components  O(n²)      O(n log n)  O(nα(n)) [union-find]
Frequency counting    O(n²)      O(n log n)  O(n) [hash map]
Top-k elements        O(n log n) O(n log k)  O(n) [quickselect]
Pattern matching      O(nm)      O(n+m)      O(n+m) [KMP]
All subsets sum       O(2^n×n)   O(2^n)      O(n×sum) [DP]
LIS                   O(n²)      O(n log n)  O(n log n) [patience]
Max subarray          O(n²)      O(n)        O(n) [Kadane's]
```

---

## Summary

### The Pattern Recognition Hierarchy

```
Level 1 — Single Pattern (Easy/Medium):
  "Given array, find max subarray" → Kadane's
  "Given sorted array, find pair"  → Two pointers
  "Given intervals, merge"         → Sort + greedy

Level 2 — Pattern Composition (Medium/Hard):
  "Count subarrays with sum k"     → Sliding window NEEDS prefix sum
  "Shortest path in grid"          → BFS NEEDS grid encoding
  "Word search multiple words"     → DFS NEEDS Trie for pruning

Level 3 — System Design (Hard/Expert):
  "LRU cache O(1)"                 → HashMap NEEDS doubly linked list
  "Median streaming"               → Two heaps balanced by invariant
  "Skyline"                        → Sweep line NEEDS sorted multiset

The progression: recognize → compose → design systems
```

### Final Interview Checklist

```
Before coding:
  □ Restate the problem in your own words
  □ Identify input/output format and edge cases
  □ Start with brute force and state its complexity
  □ Identify the bottleneck (what's being repeated?)
  □ Map to the right pattern combination
  □ State the optimized complexity before coding

While coding:
  □ Write helper functions (don't cram everything into main)
  □ Handle edge cases explicitly (empty, single element, all same)
  □ Use meaningful variable names (not i,j for complex logic)
  □ Comment the WHY not just the WHAT

After coding:
  □ Trace through your example manually
  □ Test edge cases: empty input, duplicates, negatives, overflow
  □ State final time and space complexity
  □ Discuss possible further optimizations
```
