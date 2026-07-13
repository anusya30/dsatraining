# Advanced Graph Algorithms — Complete Guide
> Shortest Paths, MST, Topological Sort & More
> Covers: Graph Representations · Shortest Path · MST · Topological Sort · SCCs · Union-Find · Real-World Applications

---

## Table of Contents
1. [Graph Representations Review](#1-graph-representations-review)
2. [Shortest Path Algorithms](#2-shortest-path-algorithms)
3. [Minimum Spanning Tree](#3-minimum-spanning-tree)
4. [Topological Sorting](#4-topological-sorting)
5. [Strongly Connected Components](#5-strongly-connected-components)
6. [Union-Find (Disjoint Set Union)](#6-union-find-disjoint-set-union)
7. [Real-World Applications](#7-real-world-applications)

---

## 1. Graph Representations Review

### What Is a Graph?
A graph G = (V, E) consists of:
- **V** = set of vertices (nodes)
- **E** = set of edges (connections between nodes)

### Graph Types
```
Undirected:   A — B   (edge has no direction)
Directed:     A → B   (edge has direction, also called digraph)
Weighted:     A —5— B (edge has a numeric weight/cost)
Unweighted:   A — B   (all edges equal)
DAG:          Directed Acyclic Graph (no cycles, used in scheduling)
```

### Representation 1: Adjacency Matrix
```
Graph: 0→1 (w=4), 0→2 (w=1), 1→3 (w=1), 2→1 (w=2), 2→3 (w=5), 3→4 (w=3)

     0    1    2    3    4
0  [ 0,   4,   1,   ∞,   ∞ ]
1  [ ∞,   0,   ∞,   1,   ∞ ]
2  [ ∞,   2,   0,   5,   ∞ ]
3  [ ∞,   ∞,   ∞,   0,   3 ]
4  [ ∞,   ∞,   ∞,   ∞,   0 ]
```

```java
// Adjacency Matrix
int[][] adjMatrix = new int[V][V];
// Add edge u→v with weight w
adjMatrix[u][v] = w;
// Check edge: O(1)
// Space: O(V²) — bad for sparse graphs
// Iteration over neighbors: O(V) — must scan entire row
```

**Best for:** Dense graphs (E ≈ V²), quick edge-weight lookup, Floyd-Warshall.

### Representation 2: Adjacency List
```java
// Adjacency List using List of Lists
List<List<int[]>> adj = new ArrayList<>();
for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
// Add edge u→v with weight w
adj.get(u).add(new int[]{v, w});
// Space: O(V + E) — efficient for sparse graphs
// Neighbor iteration: O(degree(v)) — only actual neighbors
```

```
Adjacency List for same graph:
0 → [(1,4), (2,1)]
1 → [(3,1)]
2 → [(1,2), (3,5)]
3 → [(4,3)]
4 → []
```

**Best for:** Sparse graphs, BFS/DFS, Dijkstra, most real-world graphs.

### Representation 3: Edge List
```java
// Edge List: list of all edges
int[][] edges = {
    {0, 1, 4},   // u, v, weight
    {0, 2, 1},
    {1, 3, 1},
    {2, 1, 2},
    {2, 3, 5},
    {3, 4, 3}
};
// Space: O(E)
// Best for: Kruskal's MST (needs sorted edges), Bellman-Ford
```

### Comparison Table

| Operation | Adj Matrix | Adj List | Edge List |
|---|---|---|---|
| Space | O(V²) | O(V+E) | O(E) |
| Check edge (u,v) | O(1) | O(degree) | O(E) |
| All neighbors of v | O(V) | O(degree) | O(E) |
| Add edge | O(1) | O(1) | O(1) |
| Best for | Dense, Floyd-W | Sparse, BFS/DFS | Kruskal, B-F |

### Graph Traversals (Review)

#### BFS — Breadth-First Search
```java
public static int[] bfs(List<List<int[]>> adj, int src, int V) {
    int[] dist = new int[V];
    Arrays.fill(dist, -1);
    Queue<Integer> queue = new LinkedList<>();
    dist[src] = 0;
    queue.offer(src);

    while (!queue.isEmpty()) {
        int u = queue.poll();
        for (int[] edge : adj.get(u)) {
            int v = edge[0];
            if (dist[v] == -1) {          // Not visited
                dist[v] = dist[u] + 1;    // Unweighted: distance = hops
                queue.offer(v);
            }
        }
    }
    return dist;  // dist[i] = shortest hop count from src to i
}
// Time: O(V + E)  Space: O(V)
// Use for: unweighted shortest path, level-order traversal, cycle detection
```

#### DFS — Depth-First Search
```java
public static void dfs(List<List<int[]>> adj, int u,
                        boolean[] visited, List<Integer> order) {
    visited[u] = true;
    order.add(u);
    for (int[] edge : adj.get(u)) {
        int v = edge[0];
        if (!visited[v]) {
            dfs(adj, v, visited, order);
        }
    }
}
// Time: O(V + E)  Space: O(V) stack
// Use for: topological sort, SCCs, cycle detection, path finding
```

---

## 2. Shortest Path Algorithms

### The Shortest Path Landscape

```
Single Source, Non-negative weights:  Dijkstra   O((V+E) log V)
Single Source, Negative weights:      Bellman-Ford O(VE)
All Pairs:                            Floyd-Warshall O(V³)
Single Source, Unweighted:            BFS          O(V+E)
Single Source, DAG:                   DAG Relaxation O(V+E)
Single Source, Unit weights:          BFS          O(V+E)
```

---

### 2.1 Dijkstra's Algorithm

#### Concept
Greedily expand the **closest unvisited vertex** at each step. Maintains a min-priority queue of (distance, vertex) pairs.

#### Why It Works
```
Greedy choice property:
  When we finalize vertex u (pop from min-heap), dist[u] is optimal.
  Proof: Any path to u through an unvisited vertex v would have
         dist[v] ≥ dist[u] (since we picked u as minimum).
         Adding more edges can only increase the distance.
         → dist[u] cannot be improved. ✓

FAILS with negative edges:
  A → B (w=-5), A → C (w=3), C → B (w=1)
  Dijkstra finalizes B via A (cost=-5)? No — it picks A→C→B (cost=4)?
  Actually the issue: negative edges can make a "longer" path shorter
  after Dijkstra has already finalized a vertex.
```

#### Step-by-Step Trace
```
Graph: 0→1(4), 0→2(1), 2→1(2), 1→3(1), 2→3(5), 3→4(3)
Source: 0

Initial:  dist=[0,∞,∞,∞,∞]  PQ=[(0,0)]

Step 1: Pop (0,0) → process node 0
  Update: dist[1]=4, dist[2]=1
  PQ=[(1,2),(4,1)]

Step 2: Pop (1,2) → process node 2
  Update: dist[1]=min(4,1+2)=3, dist[3]=min(∞,1+5)=6
  PQ=[(3,1),(4,1-stale),(6,3)]

Step 3: Pop (3,1) → process node 1
  Update: dist[3]=min(6,3+1)=4
  PQ=[(4,1-stale),(4,3),(6,3-stale)]

Step 4: Pop (4,1-stale) → skip (dist[1]=3 < 4)
Step 5: Pop (4,3) → process node 3
  Update: dist[4]=4+3=7
  PQ=[(6,3-stale),(7,4)]

Step 6: Pop (6,3-stale) → skip
Step 7: Pop (7,4) → process node 4

Final: dist=[0,3,1,4,7]
Path 0→4: 0→2→1→3→4 (cost 7)
```

```java
public static int[] dijkstra(List<List<int[]>> adj, int src, int V) {
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    // PQ stores [distance, vertex] — min-heap by distance
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    pq.offer(new int[]{0, src});

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int d = curr[0], u = curr[1];

        if (d > dist[u]) continue;     // Stale entry — skip

        for (int[] edge : adj.get(u)) {
            int v = edge[0], w = edge[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.offer(new int[]{dist[v], v});
            }
        }
    }
    return dist;
}

// Reconstruct path
public static List<Integer> dijkstraPath(List<List<int[]>> adj,
                                          int src, int dst, int V) {
    int[] dist = new int[V];
    int[] prev = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    Arrays.fill(prev, -1);
    dist[src] = 0;

    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    pq.offer(new int[]{0, src});

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int d = curr[0], u = curr[1];
        if (d > dist[u]) continue;
        for (int[] edge : adj.get(u)) {
            int v = edge[0], w = edge[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                prev[v] = u;
                pq.offer(new int[]{dist[v], v});
            }
        }
    }

    // Reconstruct path from dst back to src
    List<Integer> path = new ArrayList<>();
    for (int at = dst; at != -1; at = prev[at]) path.add(0, at);
    return path.get(0) == src ? path : new ArrayList<>();
}
```

**Complexity:**
- Time: O((V + E) log V) with binary heap
- Time: O(E + V log V) with Fibonacci heap (theoretical)
- Space: O(V + E)

---

### 2.2 Bellman-Ford Algorithm

#### Concept
Relax **all edges V-1 times**. Each relaxation pass propagates shortest paths one edge further. Can detect negative cycles.

#### Why V-1 Relaxations?
```
In a graph with V vertices, the shortest path can have at most V-1 edges
(otherwise it would revisit a vertex → cycle).
After k relaxations, we know the shortest path using at most k edges.
After V-1 relaxations: shortest paths using at most V-1 edges = all shortest paths.

Negative cycle detection:
  After V-1 relaxations, if any edge (u,v) still has dist[u]+w < dist[v]:
  → A shorter path exists using V edges → must contain a cycle
  → That cycle has negative total weight → NEGATIVE CYCLE detected.
```

```java
public static int[] bellmanFord(int[][] edges, int src, int V) {
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    // V-1 relaxation passes
    for (int pass = 0; pass < V - 1; pass++) {
        boolean updated = false;
        for (int[] edge : edges) {         // edges[i] = {u, v, weight}
            int u = edge[0], v = edge[1], w = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                updated = true;
            }
        }
        if (!updated) break;               // Early termination: already converged
    }
    return dist;
}

// Negative cycle detection
public static boolean hasNegativeCycle(int[][] edges, int src, int V) {
    int[] dist = bellmanFord(edges, src, V);
    // V-th pass: if still relaxes → negative cycle
    for (int[] edge : edges) {
        int u = edge[0], v = edge[1], w = edge[2];
        if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
            return true;   // Negative cycle exists
        }
    }
    return false;
}
```

**Complexity:**
- Time: O(V × E)
- Space: O(V)
- Use when: negative edge weights exist, need to detect negative cycles

---

### 2.3 Floyd-Warshall Algorithm

#### Concept
**All-pairs shortest paths** using DP. dp[i][j][k] = shortest path from i to j using only vertices {0..k} as intermediates.

#### Recurrence
```
dist[i][j] = min(dist[i][j],          // Don't use vertex k
                 dist[i][k] + dist[k][j])  // Route through vertex k
```

```java
public static int[][] floydWarshall(int[][] dist) {
    int V = dist.length;
    // dist[i][j] initially: edge weight if edge exists, 0 if i==j, INF otherwise
    int[][] d = new int[V][V];
    for (int i = 0; i < V; i++) d[i] = dist[i].clone();

    // Try each vertex k as intermediate
    for (int k = 0; k < V; k++) {
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (d[i][k] != Integer.MAX_VALUE / 2
                        && d[k][j] != Integer.MAX_VALUE / 2) {
                    d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);
                }
            }
        }
    }
    // Negative cycle: d[i][i] < 0 for any i
    return d;
}
```

**Complexity:**
- Time: O(V³)
- Space: O(V²)
- Use when: all-pairs needed, V is small (≤ 500), negative weights OK

---

### 2.4 A* (A-Star) Algorithm

#### Concept
Dijkstra + heuristic. Uses f(n) = g(n) + h(n) where:
- g(n) = actual cost from start to n
- h(n) = estimated cost from n to goal (heuristic, must be admissible: never overestimates)

```java
public static int aStar(int[][] grid, int[] start, int[] goal) {
    int rows = grid.length, cols = grid[0].length;
    int[][] gScore = new int[rows][cols];
    for (int[] row : gScore) Arrays.fill(row, Integer.MAX_VALUE);
    gScore[start[0]][start[1]] = 0;

    // PQ sorted by fScore = gScore + heuristic
    PriorityQueue<int[]> pq = new PriorityQueue<>(
        Comparator.comparingInt(a -> a[0]));   // a[0] = fScore
    pq.offer(new int[]{heuristic(start, goal), start[0], start[1]});

    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int r = curr[1], c = curr[2];
        if (r == goal[0] && c == goal[1]) return gScore[r][c];

        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols
                    || grid[nr][nc] == 1) continue;      // Wall
            int ng = gScore[r][c] + 1;
            if (ng < gScore[nr][nc]) {
                gScore[nr][nc] = ng;
                int f = ng + heuristic(new int[]{nr,nc}, goal);
                pq.offer(new int[]{f, nr, nc});
            }
        }
    }
    return -1;   // No path found
}

// Manhattan distance heuristic (admissible for grid)
static int heuristic(int[] a, int[] b) {
    return Math.abs(a[0]-b[0]) + Math.abs(a[1]-b[1]);
}
```

---

## 3. Minimum Spanning Tree

### What Is MST?
A **Minimum Spanning Tree** of a connected, weighted, undirected graph is a spanning tree with minimum total edge weight.

```
Properties:
  - Spans all V vertices
  - Uses exactly V-1 edges
  - No cycles
  - Minimum total weight among all spanning trees

Number of possible spanning trees for Kₙ (complete graph) = n^(n-2) (Cayley's formula)
MST algorithms find the minimum one efficiently.
```

### Cut Property (Why Both Algorithms Work)
```
Cut property: For any cut (S, V-S) of the graph,
the minimum weight crossing edge is always in some MST.

This is the greedy choice property for MST algorithms.
Both Kruskal's and Prim's exploit this property:
  Kruskal's: processes cheapest edge globally, adds if no cycle
  Prim's:    processes cheapest edge crossing the frontier cut
```

---

### 3.1 Kruskal's Algorithm

#### Strategy
Sort all edges by weight. Add each edge if it doesn't create a cycle (using Union-Find).

#### Step-by-Step Trace
```
Edges sorted: (1,1-2),(2,2-4),(3,0-1),(4,3-4),(5,0-2),(8,2-3),(10,1-3)
Vertices: 0,1,2,3,4

Add (1,1-2): no cycle → ADD   MST={1-2}
Add (2,2-4): no cycle → ADD   MST={1-2,2-4}
Add (3,0-1): no cycle → ADD   MST={1-2,2-4,0-1}
Add (4,3-4): no cycle → ADD   MST={1-2,2-4,0-1,3-4}
Add (5,0-2): 0 and 2 already connected → SKIP (cycle!)
Add (8,2-3): no cycle → ADD   MST={1-2,2-4,0-1,3-4,2-3} ← wait, already V-1=4 edges
→ Stop when V-1 edges added

Final MST edges: {1-2(1), 2-4(2), 0-1(3), 3-4(4)} total weight=10
```

```java
public static int kruskal(int[][] edges, int V) {
    // edges[i] = {weight, u, v}
    Arrays.sort(edges, (a, b) -> a[0] - b[0]);    // Sort by weight

    int[] parent = new int[V];
    int[] rank   = new int[V];
    for (int i = 0; i < V; i++) parent[i] = i;    // Each node is its own component

    int totalWeight = 0, edgesAdded = 0;
    List<int[]> mstEdges = new ArrayList<>();

    for (int[] edge : edges) {
        int w = edge[0], u = edge[1], v = edge[2];
        int pu = find(parent, u), pv = find(parent, v);

        if (pu != pv) {                             // No cycle
            union(parent, rank, pu, pv);
            totalWeight += w;
            edgesAdded++;
            mstEdges.add(edge);
            System.out.printf("  Add edge %d-%d (w=%d) total=%d%n", u, v, w, totalWeight);
            if (edgesAdded == V - 1) break;         // MST complete
        } else {
            System.out.printf("  Skip edge %d-%d (w=%d) — would create cycle%n", u, v, w);
        }
    }
    return totalWeight;
}

static int find(int[] parent, int x) {
    if (parent[x] != x)
        parent[x] = find(parent, parent[x]);        // Path compression
    return parent[x];
}

static void union(int[] parent, int[] rank, int a, int b) {
    if (rank[a] < rank[b]) { int t = a; a = b; b = t; }
    parent[b] = a;
    if (rank[a] == rank[b]) rank[a]++;
}
```

**Complexity:** O(E log E) — dominated by sorting edges

---

### 3.2 Prim's Algorithm

#### Strategy
Grow the MST from a starting vertex. Always add the cheapest edge connecting the current MST to a non-MST vertex.

#### Step-by-Step Trace
```
Graph: 0-1(4), 0-2(3), 1-2(1), 1-3(2), 2-3(4), 3-4(2), 2-4(5)
Start at vertex 0

MST = {0}      Available: [(3,0→2),(4,0→1)]
Pick (3,0→2):  MST = {0,2}  Available: [(1,2→1),(4,0→1),(4,2→3),(5,2→4)]
Pick (1,2→1):  MST = {0,2,1} Available: [(2,1→3),(4,0→1-skip),(4,2→3),(5,2→4)]
Pick (2,1→3):  MST = {0,2,1,3} Available: [(2,3→4),(5,2→4)]
Pick (2,3→4):  MST = {0,2,1,3,4} ← V-1=4 edges added. Done.

MST edges: 0-2(3), 2-1(1), 1-3(2), 3-4(2)  Total weight = 8
```

```java
public static int prim(List<List<int[]>> adj, int V) {
    boolean[] inMST = new boolean[V];
    int[] key = new int[V];          // Minimum edge weight to add this vertex to MST
    int[] parent = new int[V];

    Arrays.fill(key, Integer.MAX_VALUE);
    Arrays.fill(parent, -1);
    key[0] = 0;

    // PQ stores [key, vertex]
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    pq.offer(new int[]{0, 0});

    int totalWeight = 0;
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int u = curr[1];
        if (inMST[u]) continue;      // Already in MST
        inMST[u] = true;
        totalWeight += curr[0];

        if (parent[u] != -1) {
            System.out.printf("  Add edge %d-%d (w=%d) total=%d%n",
                    parent[u], u, curr[0], totalWeight);
        }

        for (int[] edge : adj.get(u)) {
            int v = edge[0], w = edge[1];
            if (!inMST[v] && w < key[v]) {
                key[v] = w;
                parent[v] = u;
                pq.offer(new int[]{w, v});
            }
        }
    }
    return totalWeight;
}
```

**Complexity:** O((V + E) log V) with binary heap

---

### 3.3 Kruskal vs Prim

| | Kruskal | Prim |
|---|---|---|
| Approach | Edge-based | Vertex-based |
| Data structure | Union-Find | Priority Queue |
| Time | O(E log E) | O((V+E) log V) |
| Better for | Sparse graphs | Dense graphs |
| Works on | Forest of components | Single growing tree |
| Parallel-friendly | Yes (sort step) | Harder |

---

## 4. Topological Sorting

### What Is Topological Sort?
A **linear ordering of vertices** in a DAG such that for every directed edge u→v, vertex u comes before v in the ordering.

```
Uses:
  - Build systems (compile A before B if B depends on A)
  - Course prerequisites (take Math before Physics)
  - Task scheduling with dependencies
  - Package managers (npm, Maven dependency resolution)
  - Spreadsheet formula evaluation
```

**Only possible on DAGs** (Directed Acyclic Graphs). A cycle makes topological sort impossible.

---

### 4.1 Kahn's Algorithm (BFS-based)

#### Concept
Repeatedly remove vertices with **in-degree 0** (no prerequisites). Uses a queue.

#### Step-by-Step Trace
```
Graph: 5→0, 5→2, 4→0, 4→1, 2→3, 3→1

In-degrees: [2,2,1,1,0,0]
Queue (in-degree 0): [4,5]

Process 4: remove edges 4→0, 4→1
  In-degrees: [1,1,1,1,0,0]  Queue: [5]
  Result: [4]

Process 5: remove edges 5→0, 5→2
  In-degrees: [0,1,0,1,0,0]  Queue: [0,2]
  Result: [4,5]

Process 0: no outgoing edges
  Queue: [2]  Result: [4,5,0]

Process 2: remove edge 2→3
  In-degrees: [0,1,0,0,0,0]  Queue: [3]
  Result: [4,5,0,2]

Process 3: remove edge 3→1
  In-degrees: [0,0,0,0,0,0]  Queue: [1]
  Result: [4,5,0,2,3]

Process 1: no outgoing edges
  Result: [4,5,0,2,3,1] ← valid topological order
```

```java
public static List<Integer> kahnTopSort(List<List<Integer>> adj, int V) {
    int[] inDegree = new int[V];
    for (int u = 0; u < V; u++)
        for (int v : adj.get(u)) inDegree[v]++;

    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < V; i++)
        if (inDegree[i] == 0) queue.offer(i);   // Start with no-prerequisite nodes

    List<Integer> order = new ArrayList<>();
    while (!queue.isEmpty()) {
        int u = queue.poll();
        order.add(u);
        for (int v : adj.get(u)) {
            inDegree[v]--;
            if (inDegree[v] == 0) queue.offer(v);  // All prerequisites met
        }
    }

    // If order doesn't contain all vertices → cycle exists
    if (order.size() != V) {
        System.out.println("  CYCLE DETECTED — topological sort impossible");
        return new ArrayList<>();
    }
    return order;
}
```

**Cycle Detection:** If the output contains fewer than V vertices, a cycle exists.

---

### 4.2 DFS-Based Topological Sort

#### Concept
Run DFS, add each vertex to a **stack after all its neighbors are processed** (post-order). Reverse the stack = topological order.

```java
public static List<Integer> dfsTopSort(List<List<Integer>> adj, int V) {
    boolean[] visited = new boolean[V];
    boolean[] inStack = new boolean[V];    // For cycle detection
    Deque<Integer> stack = new ArrayDeque<>();
    boolean[] hasCycle = {false};

    for (int i = 0; i < V; i++)
        if (!visited[i])
            dfsTopSortHelper(adj, i, visited, inStack, stack, hasCycle);

    if (hasCycle[0]) {
        System.out.println("  CYCLE DETECTED");
        return new ArrayList<>();
    }

    List<Integer> result = new ArrayList<>(stack);  // Stack is already in correct order
    return result;
}

private static void dfsTopSortHelper(List<List<Integer>> adj, int u,
                                      boolean[] visited, boolean[] inStack,
                                      Deque<Integer> stack, boolean[] hasCycle) {
    visited[u] = true;
    inStack[u] = true;     // Mark as being processed in current DFS path

    for (int v : adj.get(u)) {
        if (!visited[v]) {
            dfsTopSortHelper(adj, v, visited, inStack, stack, hasCycle);
        } else if (inStack[v]) {
            hasCycle[0] = true;   // Back edge → cycle!
        }
    }
    inStack[u] = false;
    stack.push(u);         // Push AFTER all descendants processed
}
```

### 4.3 Topological Sort Applications

#### Shortest/Longest Path in DAG
```java
// Shortest path in DAG — O(V+E) using topological order
public static int[] dagShortestPath(List<List<int[]>> adj,
                                      List<Integer> topoOrder, int src) {
    int V = topoOrder.size();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    for (int u : topoOrder) {
        if (dist[u] != Integer.MAX_VALUE) {
            for (int[] edge : adj.get(u)) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                }
            }
        }
    }
    return dist;
}

// Critical path (longest path) in DAG — negate weights or use max relaxation
public static int[] dagLongestPath(List<List<int[]>> adj,
                                    List<Integer> topoOrder, int src) {
    int V = topoOrder.size();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MIN_VALUE);
    dist[src] = 0;

    for (int u : topoOrder) {
        if (dist[u] != Integer.MIN_VALUE) {
            for (int[] edge : adj.get(u)) {
                int v = edge[0], w = edge[1];
                dist[v] = Math.max(dist[v], dist[u] + w);  // MAX instead of MIN
            }
        }
    }
    return dist;
}
```

---

## 5. Strongly Connected Components

### What Are SCCs?
In a **directed graph**, a Strongly Connected Component is a maximal set of vertices such that there is a path from each vertex to every other vertex in the set.

```
Example:
  1 → 2 → 3 → 1   (cycle: SCC = {1,2,3})
  3 → 4           (one way: separate SCCs)
  4 → 5 → 4       (cycle: SCC = {4,5})
  5 → 6           (one way: SCC = {6})

SCCs: {1,2,3}, {4,5}, {6}
```

**Uses:**
- Finding circular dependencies in build systems
- Analyzing web link structure (strongly connected web clusters)
- Compiler optimization (detecting code regions)
- Social network analysis (tight-knit communities)

---

### 5.1 Kosaraju's Algorithm

#### Two-Pass DFS Approach

**Pass 1:** Run DFS on original graph, record finish times (push to stack after processing).
**Pass 2:** Run DFS on reversed graph in order of decreasing finish times.

#### Why It Works
```
Key insight: If we reverse all edges in the graph:
  - Vertices reachable FROM u in G are exactly vertices that can REACH u in G_reverse
  - SCCs are preserved under edge reversal

After Pass 1:
  Vertex with highest finish time is in the "source" SCC.
  In G_reverse, starting DFS from this vertex reaches ONLY its own SCC.
  → Each DFS in Pass 2 finds exactly one SCC.
```

```java
public static List<List<Integer>> kosarajuSCC(List<List<Integer>> adj, int V) {
    // Pass 1: DFS on original graph, fill stack by finish time
    boolean[] visited = new boolean[V];
    Deque<Integer> stack = new ArrayDeque<>();

    for (int i = 0; i < V; i++)
        if (!visited[i])
            dfsFinish(adj, i, visited, stack);

    // Build reversed graph
    List<List<Integer>> radj = new ArrayList<>();
    for (int i = 0; i < V; i++) radj.add(new ArrayList<>());
    for (int u = 0; u < V; u++)
        for (int v : adj.get(u)) radj.get(v).add(u);

    // Pass 2: DFS on reversed graph in finish-time order
    Arrays.fill(visited, false);
    List<List<Integer>> sccs = new ArrayList<>();

    while (!stack.isEmpty()) {
        int u = stack.pop();
        if (!visited[u]) {
            List<Integer> scc = new ArrayList<>();
            dfsCollect(radj, u, visited, scc);
            sccs.add(scc);
        }
    }
    return sccs;
}

private static void dfsFinish(List<List<Integer>> adj, int u,
                                boolean[] visited, Deque<Integer> stack) {
    visited[u] = true;
    for (int v : adj.get(u))
        if (!visited[v]) dfsFinish(adj, v, visited, stack);
    stack.push(u);     // Push AFTER all descendants → records finish time
}

private static void dfsCollect(List<List<Integer>> adj, int u,
                                 boolean[] visited, List<Integer> scc) {
    visited[u] = true;
    scc.add(u);
    for (int v : adj.get(u))
        if (!visited[v]) dfsCollect(adj, v, visited, scc);
}
```

**Complexity:** O(V + E)

---

### 5.2 Tarjan's Algorithm

#### One-Pass DFS Approach
Uses discovery times and **low-link values** to identify SCCs in a single DFS.

```
low[u] = minimum discovery time reachable from u's subtree
         (including via back edges)

SCC root condition:
  Node u is the root of an SCC when disc[u] == low[u]
  (no back edge can reach an ancestor → u is the topmost node of its SCC)
```

```java
public static List<List<Integer>> tarjanSCC(List<List<Integer>> adj, int V) {
    int[] disc  = new int[V];  // Discovery time
    int[] low   = new int[V];  // Lowest disc reachable
    boolean[] onStack = new boolean[V];
    Arrays.fill(disc, -1);

    Deque<Integer> stack = new ArrayDeque<>();
    List<List<Integer>> sccs = new ArrayList<>();
    int[] timer = {0};

    for (int i = 0; i < V; i++)
        if (disc[i] == -1)
            tarjanDFS(adj, i, disc, low, onStack, stack, sccs, timer);

    return sccs;
}

private static void tarjanDFS(List<List<Integer>> adj, int u, int[] disc,
                                int[] low, boolean[] onStack,
                                Deque<Integer> stack,
                                List<List<Integer>> sccs, int[] timer) {
    disc[u] = low[u] = timer[0]++;
    stack.push(u);
    onStack[u] = true;

    for (int v : adj.get(u)) {
        if (disc[v] == -1) {                    // Tree edge
            tarjanDFS(adj, v, disc, low, onStack, stack, sccs, timer);
            low[u] = Math.min(low[u], low[v]);  // Propagate low value up
        } else if (onStack[v]) {
            low[u] = Math.min(low[u], disc[v]); // Back edge to ancestor
        }
    }

    // If u is root of SCC (disc[u] == low[u])
    if (disc[u] == low[u]) {
        List<Integer> scc = new ArrayList<>();
        while (true) {
            int w = stack.pop();
            onStack[w] = false;
            scc.add(w);
            if (w == u) break;
        }
        sccs.add(scc);
    }
}
```

**Complexity:** O(V + E) — single pass, more cache-friendly than Kosaraju's

### Kosaraju vs Tarjan

| | Kosaraju | Tarjan |
|---|---|---|
| Passes | 2 (+ graph reversal) | 1 |
| Complexity | O(V+E) | O(V+E) |
| Implementation | Simpler | More complex |
| Memory | Needs reversed graph | Only stack + arrays |
| Cache | Less friendly | More friendly |

---

## 6. Union-Find (Disjoint Set Union)

### What Is Union-Find?
A data structure that maintains a collection of **disjoint sets** (non-overlapping groups) and supports:
- **find(x):** which set does x belong to?
- **union(x, y):** merge the sets containing x and y

```
Use cases:
  - Kruskal's MST: detect cycles efficiently
  - Connected components: are u and v connected?
  - Network connectivity: is the network connected?
  - Image processing: connected pixel regions
  - Percolation: does liquid flow through?
  - Social networks: mutual friend groups
```

### Basic Union-Find
```java
class UnionFind {
    int[] parent, rank;

    UnionFind(int n) {
        parent = new int[n];
        rank   = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    // Find with path compression
    int find(int x) {
        if (parent[x] != x)
            parent[x] = find(parent[x]);   // Compress path
        return parent[x];
    }

    // Union by rank
    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;         // Already in same set
        if (rank[px] < rank[py]) { int t = px; px = py; py = t; }
        parent[py] = px;
        if (rank[px] == rank[py]) rank[px]++;
        return true;
    }

    boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}
```

### Two Critical Optimizations

#### Optimization 1: Path Compression
```
Without compression: find(x) traverses up the tree — O(tree height)

With path compression:
  find(x) flattens the path: every node on the path points directly to root
  Amortized O(α(n)) — nearly O(1)

Example:
  Before: 1 → 2 → 3 → 4 (root)
  find(1): traverses 1→2→3→4
  After:   1 → 4, 2 → 4, 3 → 4 (all point to root)
```

#### Optimization 2: Union by Rank
```
Without rank: repeatedly union could create a linked list O(n) height
With rank: always attach shorter tree under taller tree
  → Tree height ≤ log n
  Combined with path compression: O(α(n)) amortized
  α(n) = inverse Ackermann function ≈ ≤ 5 for all practical n
```

### Advanced: Weighted Union-Find (Track Component Sizes)
```java
class WeightedUnionFind {
    int[] parent, size;
    int components;

    WeightedUnionFind(int n) {
        parent = new int[n]; size = new int[n];
        components = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    int find(int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];  // Path halving (iterative compression)
            x = parent[x];
        }
        return x;
    }

    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (size[px] < size[py]) { int t = px; px = py; py = t; }
        parent[py] = px;
        size[px] += size[py];
        components--;
        return true;
    }

    int componentSize(int x) { return size[find(x)]; }
    int numComponents()      { return components; }
}
```

### Union-Find Applications

#### Number of Connected Components
```java
public static int countComponents(int n, int[][] edges) {
    WeightedUnionFind uf = new WeightedUnionFind(n);
    for (int[] e : edges) uf.union(e[0], e[1]);
    return uf.numComponents();
}
// n=5, edges=[[0,1],[1,2],[3,4]] → 2 components: {0,1,2} and {3,4}
```

#### Graph Connectivity Under Edge Additions
```java
public static boolean[] connectivityQueries(int n, int[][] edges,
                                              int[][] queries) {
    // queries[i] = [u, v]: are u and v connected?
    WeightedUnionFind uf = new WeightedUnionFind(n);
    boolean[] results = new boolean[queries.length];

    // Process queries offline (add all edges first, then check)
    for (int[] e : edges) uf.union(e[0], e[1]);
    for (int i = 0; i < queries.length; i++)
        results[i] = uf.connected(queries[i][0], queries[i][1]);

    return results;
}
```

#### Redundant Connection (Cycle Detection)
```java
public static int[] findRedundantConnection(int[][] edges) {
    int n = edges.length;
    WeightedUnionFind uf = new WeightedUnionFind(n + 1);
    for (int[] edge : edges) {
        if (!uf.union(edge[0], edge[1])) {
            return edge;    // This edge creates a cycle → redundant
        }
    }
    return new int[]{};
}
// [[1,2],[1,3],[2,3]] → [2,3] (the edge that closes the cycle)
```

---

## 7. Real-World Applications

### 7.1 Internet Routing — OSPF (Dijkstra)
```
OSPF (Open Shortest Path First) is the dominant intra-domain routing protocol.

Each router:
1. Maintains a complete graph of the network (Link State Database)
2. Runs Dijkstra to compute shortest paths to all destinations
3. Builds routing table from Dijkstra results
4. Forwards packets based on routing table

Why Dijkstra?
  - Edge weights = link costs (bandwidth, latency, reliability)
  - All costs non-negative (physical constraints)
  - Runs on each router independently
  - Reconverges when topology changes

Scale: Internet backbone routers handle V=1000s of nodes, E=10000s of links
       Dijkstra reconverges in milliseconds
```

```java
// Simulated OSPF routing table computation
public static Map<String, String> computeRoutingTable(
        Map<String, List<String[]>> topology, String routerId) {
    // topology: router → [(neighbor, cost)]
    // Returns: destination → next-hop
    Map<String, Integer> dist = new HashMap<>();
    Map<String, String> nextHop = new HashMap<>();
    PriorityQueue<String[]> pq = new PriorityQueue<>(
            Comparator.comparingInt(a -> Integer.parseInt(a[1])));

    dist.put(routerId, 0);
    pq.offer(new String[]{routerId, "0", routerId});

    while (!pq.isEmpty()) {
        String[] curr = pq.poll();
        String u = curr[0], hop = curr[2];
        int d = Integer.parseInt(curr[1]);
        if (d > dist.getOrDefault(u, Integer.MAX_VALUE)) continue;

        for (String[] edge : topology.getOrDefault(u, new ArrayList<>())) {
            String v = edge[0]; int w = Integer.parseInt(edge[1]);
            int nd = d + w;
            if (nd < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                dist.put(v, nd);
                String nh = u.equals(routerId) ? v : hop;
                nextHop.put(v, nh);
                pq.offer(new String[]{v, String.valueOf(nd), nh});
            }
        }
    }
    return nextHop;
}
```

### 7.2 GPS Navigation — A* Pathfinding
```
GPS systems use A* (or bidirectional Dijkstra) on road networks.

Graph representation:
  Vertices: road intersections, highway junctions
  Edges: road segments with travel time as weight
  Heuristic: Euclidean/haversine distance to destination

Optimizations for billion-node road graphs:
  - Contraction Hierarchies: preprocess graph, compress "unimportant" nodes
  - Bidirectional A*: search from both source and destination
  - ALT (A* + Landmarks + Triangle inequality): landmarks precompute h(n)

Google Maps: ~20ms query time on full US road network (300M nodes)
```

### 7.3 Build Systems — Topological Sort
```
Make, Maven, Gradle, npm all use topological sort for dependency resolution.

Example: Java compilation order
  If A.java imports B.java imports C.java:
  Graph: A → B → C
  Topological order: C, B, A (compile C first, then B uses C, then A uses B)

Circular dependency detection:
  If topological sort fails → circular dependency → build error
  "Circular dependency: A depends on B, B depends on A"
```

```java
// Build system: resolve compilation order
public static List<String> buildOrder(Map<String, List<String>> dependencies) {
    // dependencies: module → [modules it depends on]
    Map<String, Integer> inDegree = new HashMap<>();
    Map<String, List<String>> dependents = new HashMap<>();

    for (String mod : dependencies.keySet()) {
        inDegree.putIfAbsent(mod, 0);
        dependents.putIfAbsent(mod, new ArrayList<>());
    }
    for (Map.Entry<String, List<String>> e : dependencies.entrySet()) {
        String mod = e.getKey();
        for (String dep : e.getValue()) {
            dependents.computeIfAbsent(dep, k -> new ArrayList<>()).add(mod);
            inDegree.merge(mod, 1, Integer::sum);
            inDegree.putIfAbsent(dep, 0);
        }
    }

    Queue<String> queue = new LinkedList<>();
    for (Map.Entry<String, Integer> e : inDegree.entrySet())
        if (e.getValue() == 0) queue.offer(e.getKey());

    List<String> order = new ArrayList<>();
    while (!queue.isEmpty()) {
        String mod = queue.poll(); order.add(mod);
        for (String dependent : dependents.getOrDefault(mod, new ArrayList<>())) {
            inDegree.merge(dependent, -1, Integer::sum);
            if (inDegree.get(dependent) == 0) queue.offer(dependent);
        }
    }
    if (order.size() != inDegree.size())
        throw new RuntimeException("Circular dependency detected!");
    return order;
}
```

### 7.4 Social Networks — SCCs & Community Detection
```
Facebook/LinkedIn/Twitter graph analysis:

SCCs in directed social graphs:
  → Identify "echo chambers" (everyone follows everyone else)
  → Find isolated communities vs well-connected hub nodes
  → Detect bot networks (highly connected among themselves, one-way to real users)

Union-Find for community detection:
  → Undirected friend connections → connected components = friend groups
  → Efficiently merge communities as new connections form

Condensation DAG (SCC → single node):
  → Simplifies the overall graph structure
  → Shows high-level information flow between communities
```

### 7.5 Network Infrastructure — MST
```
Minimum cable cost to connect all data centers:
  → Model as weighted graph: nodes=data centers, edges=possible cable routes
  → MST = minimum total cable to connect all centers
  → Used by: Comcast, AT&T, Google for fiber layout

Cluster analysis using MST:
  → Build MST of all data points (edge weight = distance)
  → Remove k heaviest edges → k+1 clusters
  → Used in machine learning, bioinformatics, image segmentation
```

### 7.6 Airline / Transportation — Floyd-Warshall
```
Flight network analysis:
  → All-pairs shortest path: cheapest flight between any two cities
  → Includes layovers (intermediate vertices)
  → Transitive closure: can I reach city A from city B at all?

Floyd-Warshall for reachability:
  Replace min with OR:
  reach[i][j] = reach[i][j] OR (reach[i][k] AND reach[k][j])
  → Which cities are reachable from which others
```

---

## Summary

### Algorithm Selection Guide

```
Need shortest path from one source, no negative weights?
  → Dijkstra  O((V+E) log V)

Need shortest path with negative weights or cycle detection?
  → Bellman-Ford  O(VE)

Need all-pairs shortest paths?
  → Floyd-Warshall  O(V³)

Need minimum spanning tree, sparse graph?
  → Kruskal  O(E log E)

Need minimum spanning tree, dense graph?
  → Prim  O((V+E) log V)

Need ordering respecting dependencies?
  → Topological Sort (Kahn's or DFS)  O(V+E)

Need strongly connected components?
  → Tarjan (one pass) or Kosaraju (two pass)  O(V+E)

Need fast connectivity / cycle detection / component merging?
  → Union-Find  O(α(n)) ≈ O(1) amortized

Need pathfinding with spatial heuristic?
  → A*  O(E log V) with good heuristic
```

### Complexity Summary

| Algorithm | Time | Space | Notes |
|---|---|---|---|
| BFS | O(V+E) | O(V) | Unweighted shortest path |
| DFS | O(V+E) | O(V) | Topological sort, SCCs |
| Dijkstra | O((V+E)logV) | O(V+E) | Non-negative weights only |
| Bellman-Ford | O(VE) | O(V) | Handles negative weights |
| Floyd-Warshall | O(V³) | O(V²) | All-pairs |
| A* | O(E log V) | O(V) | With good heuristic |
| Kruskal | O(E log E) | O(V) | Sort + Union-Find |
| Prim | O((V+E)logV) | O(V) | PQ-based |
| Kahn's | O(V+E) | O(V) | BFS-based topo sort |
| Kosaraju | O(V+E) | O(V+E) | Two DFS passes |
| Tarjan | O(V+E) | O(V) | One DFS pass |
| Union-Find | O(α(n)) | O(n) | Per operation, amortized |

### The Core Insight
```
Every graph problem reduces to one of these patterns:
  1. Reachability:        BFS / DFS / Union-Find
  2. Shortest path:       Dijkstra / Bellman-Ford / Floyd-Warshall
  3. Optimal connectivity: MST (Kruskal / Prim)
  4. Ordering:            Topological Sort
  5. Component structure: SCCs (Tarjan / Kosaraju)
  6. Dynamic connectivity: Union-Find

The skill is recognizing which pattern fits — then the algorithm follows.
```
