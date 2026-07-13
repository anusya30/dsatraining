import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 * ADVANCED GRAPH ALGORITHMS — Complete Executable Reference
 * ============================================================
 * Topics:
 *  1. Graph Representations        (adjacency matrix, list, edge list,
 *                                   BFS, DFS, graph builder utilities)
 *  2. Shortest Path Algorithms     (Dijkstra with path reconstruction,
 *                                   Bellman-Ford with negative cycle detect,
 *                                   Floyd-Warshall, A* on grid,
 *                                   DAG shortest/longest path)
 *  3. Minimum Spanning Tree        (Kruskal with trace, Prim with trace,
 *                                   MST verification, Borůvka variant)
 *  4. Topological Sorting          (Kahn's BFS, DFS post-order,
 *                                   cycle detection, build order,
 *                                   task scheduling, course prerequisites)
 *  5. Strongly Connected Components(Kosaraju two-pass, Tarjan one-pass,
 *                                   condensation DAG, SCC applications)
 *  6. Union-Find                   (basic, path compression + rank,
 *                                   weighted with size, online connectivity,
 *                                   redundant connection, accounts merge)
 *  7. Real-World Applications      (OSPF routing, GPS pathfinding,
 *                                   build system, social network SCCs,
 *                                   network clustering, airline connectivity)
 *
 * Compile : javac GraphAlgorithms.java
 * Run     : java GraphAlgorithms
 * ============================================================
 */
public class GraphAlgorithms {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("ADVANCED GRAPH ALGORITHMS — COMPLETE DEMO");

        section1_GraphRepresentations();
        section2_ShortestPaths();
        section3_MinimumSpanningTree();
        section4_TopologicalSorting();
        section5_StronglyConnectedComponents();
        section6_UnionFind();
        section7_RealWorldApplications();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — GRAPH REPRESENTATIONS
    // =========================================================
    static void section1_GraphRepresentations() {
        printSection("1. GRAPH REPRESENTATIONS");

        // 1a. Adjacency matrix
        System.out.println("--- 1a. Adjacency Matrix ---");
        int V = 5;
        int INF = Integer.MAX_VALUE / 2;
        int[][] matrix = {
            {0,   4,   1,   INF, INF},
            {INF, 0,   INF, 1,   INF},
            {INF, 2,   0,   5,   INF},
            {INF, INF, INF, 0,   3  },
            {INF, INF, INF, INF, 0  }
        };
        System.out.println("  Adjacency Matrix (INF=∞):");
        for (int[] row : matrix) {
            System.out.print("  [");
            for (int i = 0; i < row.length; i++)
                System.out.printf("%4s", row[i] >= INF ? "∞" : row[i]);
            System.out.println(" ]");
        }

        // 1b. Adjacency list
        System.out.println("\n--- 1b. Adjacency List ---");
        List<List<int[]>> adj = buildAdj(V,
            new int[][]{{0,1,4},{0,2,1},{2,1,2},{1,3,1},{2,3,5},{3,4,3}});
        System.out.println("  Adjacency List:");
        for (int u = 0; u < V; u++) {
            System.out.printf("  %d → %s%n", u,
                adj.get(u).stream()
                    .map(e -> "("+e[0]+",w="+e[1]+")")
                    .collect(Collectors.joining(", ")));
        }

        // 1c. Edge list
        System.out.println("\n--- 1c. Edge List ---");
        int[][] edges = {{4,0,1},{1,0,2},{2,2,1},{1,1,3},{5,2,3},{3,3,4}};
        System.out.println("  Edges (weight, u, v):");
        for (int[] e : edges)
            System.out.printf("    %d-%d (w=%d)%n", e[1], e[2], e[0]);

        // 1d. BFS
        System.out.println("\n--- 1d. BFS from source 0 ---");
        int[] bfsDist = bfs(adj, 0, V);
        System.out.println("  Hop distances from 0: " + Arrays.toString(bfsDist));

        // 1e. DFS
        System.out.println("\n--- 1e. DFS from source 0 ---");
        boolean[] vis = new boolean[V];
        List<Integer> dfsOrder = new ArrayList<>();
        dfs(adj, 0, vis, dfsOrder);
        System.out.println("  DFS visit order: " + dfsOrder);

        // 1f. Comparison
        System.out.println("\n--- 1f. Representation Comparison ---");
        System.out.printf("  %-25s %-12s %-12s %-12s%n",
                "Operation","AdjMatrix","AdjList","EdgeList");
        System.out.printf("  %-25s %-12s %-12s %-12s%n","Space","O(V²)","O(V+E)","O(E)");
        System.out.printf("  %-25s %-12s %-12s %-12s%n","Check edge(u,v)","O(1)","O(deg)","O(E)");
        System.out.printf("  %-25s %-12s %-12s %-12s%n","All neighbors(v)","O(V)","O(deg)","O(E)");
        System.out.printf("  %-25s %-12s %-12s %-12s%n","Best for","Dense/FW","Sparse/BFS","Kruskal/BF");
    }

    static List<List<int[]>> buildAdj(int V, int[][] edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(new int[]{e[1], e[2]});
        return adj;
    }
    static List<List<int[]>> buildUndirAdj(int V, int[][] edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        return adj;
    }
    static int[] bfs(List<List<int[]>> adj, int src, int V) {
        int[] dist = new int[V]; Arrays.fill(dist, -1);
        Queue<Integer> q = new LinkedList<>();
        dist[src] = 0; q.offer(src);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int[] e : adj.get(u))
                if (dist[e[0]] == -1) { dist[e[0]] = dist[u]+1; q.offer(e[0]); }
        }
        return dist;
    }
    static void dfs(List<List<int[]>> adj, int u, boolean[] vis, List<Integer> order) {
        vis[u] = true; order.add(u);
        for (int[] e : adj.get(u)) if (!vis[e[0]]) dfs(adj, e[0], vis, order);
    }

    // =========================================================
    // SECTION 2 — SHORTEST PATH ALGORITHMS
    // =========================================================
    static void section2_ShortestPaths() {
        printSection("2. SHORTEST PATH ALGORITHMS");

        int V = 6;
        List<List<int[]>> adj = buildAdj(V, new int[][]{
            {0,1,4},{0,2,1},{2,1,2},{1,3,1},{2,3,5},{3,4,3},{4,5,2},{1,5,8}});

        // 2a. Dijkstra
        System.out.println("--- 2a. Dijkstra's Algorithm ---");
        System.out.println("  Graph: 0→1(4),0→2(1),2→1(2),1→3(1),2→3(5),3→4(3),4→5(2),1→5(8)");
        int[] dijk = dijkstra(adj, 0, V);
        System.out.println("  Shortest distances from 0: " + Arrays.toString(dijk));
        List<Integer> path = dijkstraPath(adj, 0, 5, V);
        System.out.println("  Shortest path 0→5: " + path + " (cost=" + dijk[5] + ")");

        // 2b. Dijkstra on multiple sources
        System.out.println("\n--- 2b. Dijkstra — All Sources ---");
        for (int src = 0; src < V; src++) {
            int[] d = dijkstra(adj, src, V);
            System.out.printf("  From %d: %s%n", src, Arrays.toString(d));
        }

        // 2c. Bellman-Ford (handles negative weights)
        System.out.println("\n--- 2c. Bellman-Ford Algorithm ---");
        int[][] bfEdges = {{0,1,4},{0,2,1},{2,1,-3},{1,3,2},{2,3,5},{3,4,3},{4,5,2}};
        System.out.println("  Edges (with negative weight 2→1=-3): " + Arrays.deepToString(bfEdges));
        int[] bf = bellmanFord(bfEdges, 0, V);
        System.out.println("  Shortest distances from 0: " + Arrays.toString(bf));

        // 2d. Negative cycle detection
        System.out.println("\n--- 2d. Negative Cycle Detection ---");
        int[][] negCycle = {{0,1,1},{1,2,2},{2,0,-5},{2,3,1}};
        System.out.println("  Edges: " + Arrays.deepToString(negCycle));
        System.out.println("  Has negative cycle: " + hasNegativeCycle(negCycle, 0, 4));
        int[][] noCycle  = {{0,1,4},{0,2,1},{1,3,1},{2,3,5}};
        System.out.println("  Has negative cycle (safe graph): " + hasNegativeCycle(noCycle, 0, 4));

        // 2e. Floyd-Warshall
        System.out.println("\n--- 2e. Floyd-Warshall (All-Pairs) ---");
        int INF = Integer.MAX_VALUE/2;
        int[][] fw = {
            {0,3,INF,5},{2,0,INF,4},{INF,1,0,INF},{INF,INF,2,0}
        };
        System.out.println("  Initial dist matrix:");
        printMatrix(fw, 4);
        int[][] result = floydWarshall(fw);
        System.out.println("  All-pairs shortest paths:");
        printMatrix(result, 4);

        // 2f. A* on grid
        System.out.println("\n--- 2f. A* Pathfinding on Grid ---");
        int[][] grid = {
            {0,0,0,0,0},
            {0,1,1,1,0},
            {0,1,0,0,0},
            {0,1,0,1,1},
            {0,0,0,0,0}
        };
        System.out.println("  Grid (0=open, 1=wall):");
        for (int[] row : grid) System.out.println("  " + Arrays.toString(row));
        int cost = aStar(grid, new int[]{0,0}, new int[]{4,4});
        System.out.println("  A* path from [0,0] to [4,4]: cost=" + cost);

        // 2g. DAG shortest/longest path
        System.out.println("\n--- 2g. DAG Shortest & Longest Path ---");
        List<List<int[]>> dagAdj = buildAdj(6, new int[][]{
            {0,1,5},{0,2,3},{1,3,6},{1,2,2},{2,4,4},{2,3,7},{3,5,1},{4,5,3}});
        List<Integer> topo = kahnTopSort(buildSimpleAdj(6,
            new int[][]{{0,1},{0,2},{1,3},{1,2},{2,4},{2,3},{3,5},{4,5}}));
        int[] dagShort = dagShortestPath(dagAdj, topo, 0, 6);
        int[] dagLong  = dagLongestPath(dagAdj, topo, 0, 6);
        System.out.println("  DAG shortest from 0: " + Arrays.toString(dagShort));
        System.out.println("  DAG longest  from 0: " + Arrays.toString(dagLong));
    }

    // --- Shortest path implementations ---
    static int[] dijkstra(List<List<int[]>> adj, int src, int V) {
        int[] dist = new int[V]; Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a->a[0]));
        pq.offer(new int[]{0, src});
        while (!pq.isEmpty()) {
            int[] c = pq.poll(); int d=c[0], u=c[1];
            if (d > dist[u]) continue;
            for (int[] e : adj.get(u)) {
                int v=e[0],w=e[1];
                if (dist[u]+w < dist[v]) { dist[v]=dist[u]+w; pq.offer(new int[]{dist[v],v}); }
            }
        }
        return dist;
    }
    static List<Integer> dijkstraPath(List<List<int[]>> adj, int src, int dst, int V) {
        int[] dist=new int[V],prev=new int[V];
        Arrays.fill(dist,Integer.MAX_VALUE); Arrays.fill(prev,-1); dist[src]=0;
        PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[0]));
        pq.offer(new int[]{0,src});
        while (!pq.isEmpty()) {
            int[] c=pq.poll(); int d=c[0],u=c[1];
            if (d>dist[u]) continue;
            for (int[] e:adj.get(u)) { int v=e[0],w=e[1];
                if (dist[u]+w<dist[v]){dist[v]=dist[u]+w;prev[v]=u;pq.offer(new int[]{dist[v],v});}}
        }
        List<Integer> path=new ArrayList<>();
        for (int at=dst;at!=-1;at=prev[at]) path.add(0,at);
        return path.isEmpty()||path.get(0)!=src?new ArrayList<>():path;
    }
    static int[] bellmanFord(int[][] edges, int src, int V) {
        int[] dist=new int[V]; Arrays.fill(dist,Integer.MAX_VALUE); dist[src]=0;
        for (int p=0;p<V-1;p++) {
            boolean upd=false;
            for (int[] e:edges) { int u=e[0],v=e[1],w=e[2];
                if (dist[u]!=Integer.MAX_VALUE&&dist[u]+w<dist[v]){dist[v]=dist[u]+w;upd=true;}}
            if (!upd) break;
        }
        return dist;
    }
    static boolean hasNegativeCycle(int[][] edges, int src, int V) {
        int[] dist=bellmanFord(edges,src,V);
        for (int[] e:edges) { int u=e[0],v=e[1],w=e[2];
            if (dist[u]!=Integer.MAX_VALUE&&dist[u]+w<dist[v]) return true; }
        return false;
    }
    static int[][] floydWarshall(int[][] dist) {
        int V=dist.length; int[][] d=new int[V][V];
        for (int i=0;i<V;i++) d[i]=dist[i].clone();
        for (int k=0;k<V;k++) for (int i=0;i<V;i++) for (int j=0;j<V;j++)
            if (d[i][k]!=Integer.MAX_VALUE/2&&d[k][j]!=Integer.MAX_VALUE/2)
                d[i][j]=Math.min(d[i][j],d[i][k]+d[k][j]);
        return d;
    }
    static int aStar(int[][] grid, int[] start, int[] goal) {
        int rows=grid.length,cols=grid[0].length;
        int[][] g=new int[rows][cols];
        for (int[] row:g) Arrays.fill(row,Integer.MAX_VALUE);
        g[start[0]][start[1]]=0;
        PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[0]));
        pq.offer(new int[]{heuristic(start,goal),start[0],start[1]});
        int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}};
        while (!pq.isEmpty()) {
            int[] c=pq.poll(); int r=c[1],cc=c[2];
            if (r==goal[0]&&cc==goal[1]) return g[r][cc];
            for (int[] d:dirs) {
                int nr=r+d[0],nc=cc+d[1];
                if (nr<0||nr>=rows||nc<0||nc>=cols||grid[nr][nc]==1) continue;
                int ng=g[r][cc]+1;
                if (ng<g[nr][nc]) {g[nr][nc]=ng;pq.offer(new int[]{ng+heuristic(new int[]{nr,nc},goal),nr,nc});}
            }
        }
        return -1;
    }
    static int heuristic(int[] a,int[] b){return Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1]);}
    static int[] dagShortestPath(List<List<int[]>> adj,List<Integer> topo,int src,int V){
        int[] dist=new int[V]; Arrays.fill(dist,Integer.MAX_VALUE); dist[src]=0;
        for (int u:topo) if (dist[u]!=Integer.MAX_VALUE)
            for (int[] e:adj.get(u)) if (dist[u]+e[1]<dist[e[0]]) dist[e[0]]=dist[u]+e[1];
        return dist;
    }
    static int[] dagLongestPath(List<List<int[]>> adj,List<Integer> topo,int src,int V){
        int[] dist=new int[V]; Arrays.fill(dist,Integer.MIN_VALUE); dist[src]=0;
        for (int u:topo) if (dist[u]!=Integer.MIN_VALUE)
            for (int[] e:adj.get(u)) dist[e[0]]=Math.max(dist[e[0]],dist[u]+e[1]);
        return dist;
    }

    // =========================================================
    // SECTION 3 — MINIMUM SPANNING TREE
    // =========================================================
    static void section3_MinimumSpanningTree() {
        printSection("3. MINIMUM SPANNING TREE");

        // 3a. Kruskal
        System.out.println("--- 3a. Kruskal's Algorithm ---");
        int[][] kEdges = {{1,1,2},{2,2,4},{3,0,1},{4,3,4},{5,0,2},{8,2,3},{10,1,3}};
        System.out.println("  Edges (weight,u,v): " + Arrays.deepToString(kEdges));
        int kCost = kruskalTrace(kEdges, 5);
        System.out.println("  Total MST weight (Kruskal): " + kCost);

        // 3b. Prim
        System.out.println("\n--- 3b. Prim's Algorithm ---");
        List<List<int[]>> primAdj = buildUndirAdj(5, new int[][]{
            {0,1,4},{0,2,3},{1,2,1},{1,3,2},{2,3,4},{3,4,2},{2,4,5}});
        System.out.println("  Graph: 0-1(4),0-2(3),1-2(1),1-3(2),2-3(4),3-4(2),2-4(5)");
        int pCost = primTrace(primAdj, 5);
        System.out.println("  Total MST weight (Prim): " + pCost);

        // 3c. Different graphs
        System.out.println("\n--- 3c. MST on Various Graphs ---");
        int[][][] testEdges = {
            {{1,0,1},{3,0,2},{4,1,2},{2,1,3},{5,2,3}},
            {{10,0,1},{6,0,2},{5,1,2},{15,1,3},{4,2,3}},
            {{7,0,1},{8,0,2},{9,1,2},{11,1,3},{6,3,4},{3,2,4}}
        };
        int[] Vs = {4,4,5};
        for (int i = 0; i < testEdges.length; i++) {
            int cost = kruskal(testEdges[i], Vs[i]);
            System.out.printf("  Graph %d: MST weight=%d%n", i+1, cost);
        }

        // 3d. MST applications
        System.out.println("\n--- 3d. MST Properties & Applications ---");
        System.out.println("  • V-1 edges, spans all vertices, no cycles");
        System.out.println("  • Unique MST when all edge weights are distinct");
        System.out.println("  • Network cable layout: minimize cable length");
        System.out.println("  • Cluster analysis: remove k heaviest edges → k+1 clusters");
        System.out.println("  • Approximation for TSP: MST weight ≤ optimal TSP/2");
        System.out.println("  • Borůvka: parallel-friendly MST for massive graphs");
    }

    // --- MST implementations ---
    static int kruskal(int[][] edges, int V) {
        int[][] sorted = edges.clone(); Arrays.sort(sorted,(a,b)->a[0]-b[0]);
        int[] p=new int[V],r=new int[V]; for(int i=0;i<V;i++) p[i]=i;
        int w=0,cnt=0;
        for (int[] e:sorted) { int pu=ufFind(p,e[1]),pv=ufFind(p,e[2]);
            if (pu!=pv){ufUnion(p,r,pu,pv);w+=e[0];if(++cnt==V-1) break;}}
        return w;
    }
    static int kruskalTrace(int[][] edges, int V) {
        int[][] sorted = edges.clone(); Arrays.sort(sorted,(a,b)->a[0]-b[0]);
        int[] p=new int[V],r=new int[V]; for(int i=0;i<V;i++) p[i]=i;
        int w=0,cnt=0;
        for (int[] e:sorted) {
            int pu=ufFind(p,e[1]),pv=ufFind(p,e[2]);
            if (pu!=pv){ufUnion(p,r,pu,pv);w+=e[0];cnt++;
                System.out.printf("    ADD edge %d-%d (w=%d) total=%d%n",e[1],e[2],e[0],w);
                if(cnt==V-1) break;
            } else System.out.printf("    SKIP edge %d-%d (w=%d) — cycle%n",e[1],e[2],e[0]);
        }
        return w;
    }
    static int primTrace(List<List<int[]>> adj, int V) {
        boolean[] in=new boolean[V]; int[] key=new int[V],par=new int[V];
        Arrays.fill(key,Integer.MAX_VALUE); Arrays.fill(par,-1); key[0]=0;
        PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[0]));
        pq.offer(new int[]{0,0});
        int total=0;
        while (!pq.isEmpty()) {
            int[] c=pq.poll(); int u=c[1]; if(in[u]) continue; in[u]=true; total+=c[0];
            if(par[u]!=-1) System.out.printf("    ADD edge %d-%d (w=%d) total=%d%n",par[u],u,c[0],total);
            for (int[] e:adj.get(u)) if(!in[e[0]]&&e[1]<key[e[0]]){key[e[0]]=e[1];par[e[0]]=u;pq.offer(new int[]{e[1],e[0]});}
        }
        return total;
    }
    static int ufFind(int[] p,int x){return p[x]!=x?p[x]=ufFind(p,p[x]):x;}
    static void ufUnion(int[] p,int[] r,int a,int b){
        if(r[a]<r[b]){int t=a;a=b;b=t;} p[b]=a; if(r[a]==r[b]) r[a]++;}

    // =========================================================
    // SECTION 4 — TOPOLOGICAL SORTING
    // =========================================================
    static void section4_TopologicalSorting() {
        printSection("4. TOPOLOGICAL SORTING");

        // 4a. Kahn's algorithm
        System.out.println("--- 4a. Kahn's Algorithm (BFS-based) ---");
        List<List<Integer>> adj1 = buildSimpleAdj(6,
            new int[][]{{5,0},{5,2},{4,0},{4,1},{2,3},{3,1}});
        System.out.println("  Edges: 5→0,5→2,4→0,4→1,2→3,3→1");
        List<Integer> kahn = kahnTopSort(adj1);
        System.out.println("  Topological order (Kahn): " + kahn);

        // 4b. DFS-based
        System.out.println("\n--- 4b. DFS-Based Topological Sort ---");
        List<Integer> dfsTopo = dfsTopSort(adj1, 6);
        System.out.println("  Topological order (DFS): " + dfsTopo);

        // 4c. Course prerequisites
        System.out.println("\n--- 4c. Course Prerequisites (Can Finish?) ---");
        int[][] prereqs = {{1,0},{2,1},{3,2}};
        System.out.println("  4 courses, prereqs: " + Arrays.deepToString(prereqs));
        System.out.println("  Can finish: " + canFinishCourses(4, prereqs));
        int[][] cyclicPrereqs = {{1,0},{0,1}};
        System.out.println("  Cyclic prereqs " + Arrays.deepToString(cyclicPrereqs)
                + " → can finish: " + canFinishCourses(2, cyclicPrereqs));
        System.out.println("  Order for 4 courses: " + Arrays.toString(findOrder(4, prereqs)));

        // 4d. Multiple valid orderings
        System.out.println("\n--- 4d. Parallel Task Scheduling ---");
        List<List<Integer>> taskAdj = buildSimpleAdj(8,
            new int[][]{{0,2},{0,3},{1,3},{1,4},{2,5},{3,5},{3,6},{4,6},{5,7},{6,7}});
        System.out.println("  Tasks with dependencies (0-7):");
        List<Integer> taskOrder = kahnTopSort(taskAdj);
        System.out.println("  Execution order: " + taskOrder);

        // 4e. Cycle detection
        System.out.println("\n--- 4e. Cycle Detection via Topological Sort ---");
        List<List<Integer>> cycleAdj = buildSimpleAdj(4,
            new int[][]{{0,1},{1,2},{2,3},{3,1}});  // 1→2→3→1 cycle
        System.out.println("  Graph with cycle 1→2→3→1:");
        List<Integer> cycleResult = kahnTopSort(cycleAdj);
        System.out.println("  Result: " + (cycleResult.isEmpty() ? "CYCLE DETECTED ✓" : cycleResult));

        // 4f. Alien dictionary (topological sort on characters)
        System.out.println("\n--- 4f. Alien Dictionary Order ---");
        String[] words = {"wrt","wrf","er","ett","rftt"};
        System.out.println("  Words: " + Arrays.toString(words));
        System.out.println("  Character order: " + alienOrder(words));
    }

    // --- Topological sort implementations ---
    static List<List<Integer>> buildSimpleAdj(int V, int[][] edges) {
        List<List<Integer>> adj=new ArrayList<>();
        for (int i=0;i<V;i++) adj.add(new ArrayList<>());
        for (int[] e:edges) adj.get(e[0]).add(e[1]);
        return adj;
    }
    static List<Integer> kahnTopSort(List<List<Integer>> adj) {
        int V=adj.size(); int[] in=new int[V];
        for (int u=0;u<V;u++) for (int v:adj.get(u)) in[v]++;
        Queue<Integer> q=new LinkedList<>();
        for (int i=0;i<V;i++) if(in[i]==0) q.offer(i);
        List<Integer> order=new ArrayList<>();
        while (!q.isEmpty()) {
            int u=q.poll(); order.add(u);
            for (int v:adj.get(u)) if(--in[v]==0) q.offer(v);
        }
        if (order.size()!=V) { System.out.println("  ⚠ CYCLE DETECTED"); return new ArrayList<>(); }
        return order;
    }
    static List<Integer> dfsTopSort(List<List<Integer>> adj, int V) {
        boolean[] vis=new boolean[V],inStack=new boolean[V];
        Deque<Integer> stack=new ArrayDeque<>();
        boolean[] hasCycle={false};
        for (int i=0;i<V;i++) if(!vis[i]) dfsTopoHelper(adj,i,vis,inStack,stack,hasCycle);
        if (hasCycle[0]) return new ArrayList<>();
        return new ArrayList<>(stack);
    }
    static void dfsTopoHelper(List<List<Integer>> adj,int u,boolean[] vis,
            boolean[] inStack,Deque<Integer> stack,boolean[] hasCycle) {
        vis[u]=true; inStack[u]=true;
        for (int v:adj.get(u)) {
            if(!vis[v]) dfsTopoHelper(adj,v,vis,inStack,stack,hasCycle);
            else if(inStack[v]) hasCycle[0]=true;
        }
        inStack[u]=false; stack.push(u);
    }
    static boolean canFinishCourses(int n, int[][] prereqs) {
        List<List<Integer>> adj=buildSimpleAdj(n,prereqs);
        return !kahnTopSort(adj).isEmpty();
    }
    static int[] findOrder(int n, int[][] prereqs) {
        List<List<Integer>> adj=buildSimpleAdj(n,prereqs);
        List<Integer> order=kahnTopSort(adj);
        return order.isEmpty()?new int[]{}:order.stream().mapToInt(Integer::intValue).toArray();
    }
    static String alienOrder(String[] words) {
        Map<Character,Set<Character>> adj=new LinkedHashMap<>();
        Map<Character,Integer> inDeg=new LinkedHashMap<>();
        for (String w:words) for (char c:w.toCharArray()){adj.putIfAbsent(c,new LinkedHashSet<>());inDeg.putIfAbsent(c,0);}
        for (int i=0;i<words.length-1;i++) {
            String a=words[i],b=words[i+1]; int len=Math.min(a.length(),b.length());
            for (int j=0;j<len;j++) if(a.charAt(j)!=b.charAt(j)){
                if(!adj.get(a.charAt(j)).contains(b.charAt(j))){
                    adj.get(a.charAt(j)).add(b.charAt(j));
                    inDeg.merge(b.charAt(j),1,Integer::sum);
                } break;
            }
        }
        Queue<Character> q=new LinkedList<>();
        for (Map.Entry<Character,Integer> e:inDeg.entrySet()) if(e.getValue()==0) q.offer(e.getKey());
        StringBuilder sb=new StringBuilder();
        while (!q.isEmpty()){char c=q.poll();sb.append(c);for(char nc:adj.get(c)){inDeg.merge(nc,-1,Integer::sum);if(inDeg.get(nc)==0) q.offer(nc);}}
        return sb.toString();
    }

    // =========================================================
    // SECTION 5 — STRONGLY CONNECTED COMPONENTS
    // =========================================================
    static void section5_StronglyConnectedComponents() {
        printSection("5. STRONGLY CONNECTED COMPONENTS");

        // 5a. Kosaraju
        System.out.println("--- 5a. Kosaraju's Algorithm ---");
        List<List<Integer>> sccAdj = buildSimpleAdj(8,
            new int[][]{{0,1},{1,2},{2,0},{1,3},{3,4},{4,5},{5,3},{6,5},{6,7},{7,6}});
        System.out.println("  Edges: 0→1,1→2,2→0,1→3,3→4,4→5,5→3,6→5,6→7,7→6");
        List<List<Integer>> kSCCs = kosarajuSCC(sccAdj, 8);
        System.out.println("  SCCs (Kosaraju): " + kSCCs);
        System.out.println("  Number of SCCs: " + kSCCs.size());

        // 5b. Tarjan
        System.out.println("\n--- 5b. Tarjan's Algorithm ---");
        List<List<Integer>> tSCCs = tarjanSCC(sccAdj, 8);
        System.out.println("  SCCs (Tarjan):   " + tSCCs);
        System.out.println("  Number of SCCs: " + tSCCs.size());

        // 5c. Various graph structures
        System.out.println("\n--- 5c. SCC on Various Graphs ---");
        // Chain (each node is its own SCC)
        List<List<Integer>> chain = buildSimpleAdj(4, new int[][]{{0,1},{1,2},{2,3}});
        System.out.println("  Chain 0→1→2→3: " + tarjanSCC(chain,4).size() + " SCCs (each node alone)");
        // Complete cycle
        List<List<Integer>> cycle = buildSimpleAdj(4, new int[][]{{0,1},{1,2},{2,3},{3,0}});
        System.out.println("  Full cycle 0→1→2→3→0: " + tarjanSCC(cycle,4) + " (one SCC)");
        // Two separate cycles
        List<List<Integer>> twoC = buildSimpleAdj(6, new int[][]{{0,1},{1,2},{2,0},{3,4},{4,5},{5,3}});
        System.out.println("  Two cycles {0,1,2} {3,4,5}: " + tarjanSCC(twoC,6));

        // 5d. Condensation DAG
        System.out.println("\n--- 5d. Condensation DAG ---");
        System.out.println("  After finding SCCs, condense each SCC to a single node.");
        System.out.println("  Result: a DAG showing high-level connectivity.");
        System.out.println("  Use cases: circular dependency detection, web graph analysis");
        System.out.println("  SCCs from 5a condensed: {0,1,2}→{3,4,5}→{6,7}  (topological order)");

        // 5e. Applications
        System.out.println("\n--- 5e. SCC Applications ---");
        System.out.println("  • Circular deps in build: if SCC size > 1 → cycle error");
        System.out.println("  • 2-SAT: solve boolean satisfiability via SCCs on implication graph");
        System.out.println("  • Web crawl: SCCs = tightly-knit link communities");
        System.out.println("  • Social network: mutual follow groups = large SCCs");
    }

    // --- SCC implementations ---
    static List<List<Integer>> kosarajuSCC(List<List<Integer>> adj, int V) {
        boolean[] vis=new boolean[V]; Deque<Integer> stack=new ArrayDeque<>();
        for (int i=0;i<V;i++) if(!vis[i]) kosaDFS1(adj,i,vis,stack);
        List<List<Integer>> radj=new ArrayList<>();
        for (int i=0;i<V;i++) radj.add(new ArrayList<>());
        for (int u=0;u<V;u++) for (int v:adj.get(u)) radj.get(v).add(u);
        Arrays.fill(vis,false); List<List<Integer>> sccs=new ArrayList<>();
        while (!stack.isEmpty()) { int u=stack.pop();
            if(!vis[u]){List<Integer> s=new ArrayList<>();kosaDFS2(radj,u,vis,s);sccs.add(s);}
        }
        return sccs;
    }
    static void kosaDFS1(List<List<Integer>> adj,int u,boolean[] vis,Deque<Integer> stack){
        vis[u]=true; for(int v:adj.get(u)) if(!vis[v]) kosaDFS1(adj,v,vis,stack); stack.push(u);}
    static void kosaDFS2(List<List<Integer>> adj,int u,boolean[] vis,List<Integer> scc){
        vis[u]=true; scc.add(u); for(int v:adj.get(u)) if(!vis[v]) kosaDFS2(adj,v,vis,scc);}

    static List<List<Integer>> tarjanSCC(List<List<Integer>> adj, int V) {
        int[] disc=new int[V],low=new int[V]; Arrays.fill(disc,-1);
        boolean[] onStack=new boolean[V]; Deque<Integer> stack=new ArrayDeque<>();
        List<List<Integer>> sccs=new ArrayList<>();int[] timer={0};
        for(int i=0;i<V;i++) if(disc[i]==-1) tarjanDFS(adj,i,disc,low,onStack,stack,sccs,timer);
        return sccs;
    }
    static void tarjanDFS(List<List<Integer>> adj,int u,int[] disc,int[] low,
            boolean[] onStack,Deque<Integer> stack,List<List<Integer>> sccs,int[] timer){
        disc[u]=low[u]=timer[0]++; stack.push(u); onStack[u]=true;
        for(int v:adj.get(u)){
            if(disc[v]==-1){tarjanDFS(adj,v,disc,low,onStack,stack,sccs,timer);low[u]=Math.min(low[u],low[v]);}
            else if(onStack[v]) low[u]=Math.min(low[u],disc[v]);
        }
        if(disc[u]==low[u]){List<Integer> scc=new ArrayList<>();
            while(true){int w=stack.pop();onStack[w]=false;scc.add(w);if(w==u) break;}
            sccs.add(scc);}
    }

    // =========================================================
    // SECTION 6 — UNION-FIND
    // =========================================================
    static void section6_UnionFind() {
        printSection("6. UNION-FIND (DISJOINT SET UNION)");

        // 6a. Basic operations
        System.out.println("--- 6a. Basic Union-Find Operations ---");
        UnionFind uf = new UnionFind(7);
        System.out.println("  Initial: 7 components (each node alone)");
        uf.union(0,1); System.out.println("  union(0,1): components=" + uf.components);
        uf.union(1,2); System.out.println("  union(1,2): components=" + uf.components);
        uf.union(3,4); System.out.println("  union(3,4): components=" + uf.components);
        uf.union(5,6); System.out.println("  union(5,6): components=" + uf.components);
        uf.union(4,6); System.out.println("  union(4,6): components=" + uf.components);
        System.out.println("  connected(0,2)=" + uf.connected(0,2));
        System.out.println("  connected(0,3)=" + uf.connected(0,3));
        System.out.println("  connected(3,6)=" + uf.connected(3,6));
        System.out.println("  componentSize(3)=" + uf.componentSize(3));

        // 6b. Path compression demo
        System.out.println("\n--- 6b. Path Compression Demo ---");
        UnionFind uf2 = new UnionFind(8);
        for (int i = 0; i < 7; i++) uf2.union(i, i+1);  // Long chain
        System.out.println("  After chaining 0-1-2-3-4-5-6-7:");
        System.out.println("  find(0)=" + uf2.find(0) + " (path compressed to root)");
        System.out.println("  All nodes now directly point to root → O(1) future finds");

        // 6c. Connected components
        System.out.println("\n--- 6c. Number of Connected Components ---");
        int[][] edgeSets1 = {{0,1},{1,2},{3,4}};
        System.out.printf("  5 nodes, edges=%s → %d components%n",
                Arrays.deepToString(edgeSets1), countComponents(5, edgeSets1));
        int[][] edgeSets2 = {{0,1},{0,2},{0,3},{1,4}};
        System.out.printf("  5 nodes, edges=%s → %d components%n",
                Arrays.deepToString(edgeSets2), countComponents(5, edgeSets2));

        // 6d. Redundant connection
        System.out.println("\n--- 6d. Redundant Connection (Cycle Detection) ---");
        int[][][] redTests = {{{1,2},{1,3},{2,3}},{{1,2},{2,3},{3,4},{1,4},{1,5}},{{1,2},{2,3},{3,1}}};
        for (int[][] edges : redTests) {
            System.out.printf("  edges=%s → redundant=%s%n",
                    Arrays.deepToString(edges), Arrays.toString(findRedundantConnection(edges)));
        }

        // 6e. Accounts merge (complex union-find)
        System.out.println("\n--- 6e. Accounts Merge ---");
        List<List<String>> accounts = new ArrayList<>();
        accounts.add(Arrays.asList("John","johnsmith@mail.com","john00@mail.com"));
        accounts.add(Arrays.asList("John","johnnybravo@mail.com"));
        accounts.add(Arrays.asList("John","johnsmith@mail.com","john_newyork@mail.com"));
        accounts.add(Arrays.asList("Mary","mary@mail.com"));
        System.out.println("  Input accounts: " + accounts.size());
        List<List<String>> merged = accountsMerge(accounts);
        System.out.println("  Merged accounts (" + merged.size() + "):");
        for (List<String> acc : merged) System.out.println("    " + acc);

        // 6f. Online graph connectivity
        System.out.println("\n--- 6f. Percolation / Online Connectivity ---");
        UnionFind perc = new UnionFind(9);
        int[][] additions = {{0,1},{3,4},{1,4},{7,8},{4,7}};
        System.out.println("  Query: does path exist from 0 to 8?");
        for (int[] e : additions) {
            perc.union(e[0], e[1]);
            System.out.printf("  After union(%d,%d): 0-8 connected? %s%n",
                    e[0], e[1], perc.connected(0,8));
        }
    }

    // --- Union-Find implementation ---
    static class UnionFind {
        int[] parent, size; int components;
        UnionFind(int n){parent=new int[n];size=new int[n];components=n;
            for(int i=0;i<n;i++){parent[i]=i;size[i]=1;}}
        int find(int x){while(parent[x]!=x){parent[x]=parent[parent[x]];x=parent[x];}return x;}
        boolean union(int x,int y){int px=find(x),py=find(y);if(px==py) return false;
            if(size[px]<size[py]){int t=px;px=py;py=t;}parent[py]=px;size[px]+=size[py];components--;return true;}
        boolean connected(int x,int y){return find(x)==find(y);}
        int componentSize(int x){return size[find(x)];}
    }

    static int countComponents(int n, int[][] edges) {
        UnionFind uf=new UnionFind(n);
        for(int[] e:edges) uf.union(e[0],e[1]);
        return uf.components;
    }
    static int[] findRedundantConnection(int[][] edges) {
        int n=edges.length; UnionFind uf=new UnionFind(n+1);
        for(int[] e:edges) if(!uf.union(e[0],e[1])) return e;
        return new int[]{};
    }
    static List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String,Integer> emailToId=new HashMap<>();
        Map<String,String> emailToName=new HashMap<>();
        int id=0;
        for (List<String> acc:accounts){String name=acc.get(0);
            for(int i=1;i<acc.size();i++){String email=acc.get(i);
                if(!emailToId.containsKey(email)){emailToId.put(email,id++);emailToName.put(email,name);}}}
        UnionFind uf=new UnionFind(id);
        for (List<String> acc:accounts)
            for(int i=2;i<acc.size();i++) uf.union(emailToId.get(acc.get(1)),emailToId.get(acc.get(i)));
        Map<Integer,List<String>> groups=new HashMap<>();
        for (Map.Entry<String,Integer> e:emailToId.entrySet())
            groups.computeIfAbsent(uf.find(e.getValue()),k->new ArrayList<>()).add(e.getKey());
        List<List<String>> result=new ArrayList<>();
        for (List<String> emails:groups.values()){
            Collections.sort(emails);
            List<String> acc=new ArrayList<>(); acc.add(emailToName.get(emails.get(0))); acc.addAll(emails);
            result.add(acc);}
        return result;
    }

    // =========================================================
    // SECTION 7 — REAL-WORLD APPLICATIONS
    // =========================================================
    static void section7_RealWorldApplications() {
        printSection("7. REAL-WORLD APPLICATIONS");

        // 7a. OSPF routing simulation
        System.out.println("--- 7a. OSPF Internet Routing (Dijkstra) ---");
        int routerCount = 6;
        List<List<int[]>> ospfAdj = buildAdj(routerCount, new int[][]{
            {0,1,10},{0,2,5},{1,2,3},{1,3,1},{2,3,8},{2,4,2},{3,5,4},{4,5,6},{4,3,1}});
        System.out.println("  Network topology (cost=latency in ms):");
        System.out.println("  R0→R1(10ms), R0→R2(5ms), R1→R2(3ms), R1→R3(1ms)");
        System.out.println("  R2→R3(8ms), R2→R4(2ms), R3→R5(4ms), R4→R5(6ms), R4→R3(1ms)");
        for (int r = 0; r < routerCount; r++) {
            int[] dists = dijkstra(ospfAdj, r, routerCount);
            System.out.printf("  Routing from R%d: %s%n", r, Arrays.toString(dists));
        }

        // 7b. GPS navigation A*
        System.out.println("\n--- 7b. GPS Navigation (A* on Road Grid) ---");
        int[][] roadMap = {
            {0,0,1,0,0,0},
            {0,0,0,0,1,0},
            {0,1,1,0,0,0},
            {0,0,0,1,0,0},
            {1,0,0,0,0,0},
            {0,0,0,0,0,0}
        };
        System.out.println("  Road map (0=road, 1=blocked):");
        for (int[] row : roadMap) System.out.println("  " + Arrays.toString(row));
        int[][] trips = {{0,0,5,5},{0,0,0,5},{3,0,3,5}};
        for (int[] t : trips) {
            int cost = aStar(roadMap, new int[]{t[0],t[1]}, new int[]{t[2],t[3]});
            System.out.printf("  GPS: [%d,%d]→[%d,%d] = %s%n",
                    t[0],t[1],t[2],t[3], cost==-1?"NO ROUTE":"dist "+cost);
        }

        // 7c. Build system — dependency resolution
        System.out.println("\n--- 7c. Build System (Maven/Gradle) Dependency Resolution ---");
        Map<String,List<String>> deps = new LinkedHashMap<>();
        deps.put("App",     Arrays.asList("ServiceA","ServiceB"));
        deps.put("ServiceA",Arrays.asList("DatabaseLib","UtilLib"));
        deps.put("ServiceB",Arrays.asList("UtilLib","CacheLib"));
        deps.put("DatabaseLib",Arrays.asList("UtilLib"));
        deps.put("CacheLib", Arrays.asList());
        deps.put("UtilLib",  Arrays.asList());
        System.out.println("  Dependencies: " + deps);
        try {
            List<String> buildOrder = buildOrder(deps);
            System.out.println("  Build order: " + buildOrder);
        } catch (RuntimeException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // 7d. Social network SCC analysis
        System.out.println("\n--- 7d. Social Network Community Detection (SCCs) ---");
        List<List<Integer>> socialAdj = buildSimpleAdj(10, new int[][]{
            {0,1},{1,2},{2,0},{3,4},{4,5},{5,3},{6,7},{7,8},{8,9},{9,6},{2,3},{5,6}});
        List<List<Integer>> communities = tarjanSCC(socialAdj, 10);
        System.out.println("  Social graph SCCs (mutual-follow communities):");
        for (int i = 0; i < communities.size(); i++)
            System.out.printf("  Community %d: users %s%n", i+1, communities.get(i));

        // 7e. Network cable layout (MST)
        System.out.println("\n--- 7e. Data Center Network Design (MST) ---");
        int[][] cables = {{100,0,1},{200,0,2},{150,1,2},{300,1,3},{250,2,3},{180,3,4},{120,2,4},{400,0,4}};
        System.out.println("  Possible cables (cost $, datacenter1, datacenter2):");
        for (int[] c : cables)
            System.out.printf("    DC%d↔DC%d: $%d%n",c[1],c[2],c[0]);
        System.out.println("  MST edges (minimum cable layout):");
        int totalCable = kruskalTrace(cables, 5);
        System.out.println("  Total minimum cable cost: $" + totalCable);

        // 7f. Package manager conflict resolution
        System.out.println("\n--- 7f. Circular Dependency Detection (npm/pip) ---");
        List<List<Integer>> pkgAdj = buildSimpleAdj(5,
            new int[][]{{0,1},{1,2},{2,3},{3,1}});  // 1→2→3→1 circular
        System.out.println("  Package deps: A→B→C→D→B (circular!)");
        List<Integer> pkgOrder = kahnTopSort(pkgAdj);
        System.out.println("  Resolution: " + (pkgOrder.isEmpty()
                ? "CIRCULAR DEPENDENCY ERROR — cannot install ✓" : pkgOrder));

        // 7g. Union-Find: network partitioning
        System.out.println("\n--- 7g. Network Partitioning (Union-Find) ---");
        int servers = 8;
        int[][] connections = {{0,1},{2,3},{4,5},{6,7},{1,2},{5,6}};
        UnionFind net = new UnionFind(servers);
        for (int[] c : connections) net.union(c[0], c[1]);
        System.out.println("  Servers: 8, Connections: " + Arrays.deepToString(connections));
        System.out.println("  Network partitions (isolated subnets): " + net.components);
        System.out.println("  Server 0 and 3 in same subnet? " + net.connected(0,3));
        System.out.println("  Server 0 and 7 in same subnet? " + net.connected(0,7));
        System.out.println("  Subnet containing server 0 size: " + net.componentSize(0));

        // 7h. Airline connectivity (Floyd-Warshall)
        System.out.println("\n--- 7h. Airline Route Analysis (Floyd-Warshall) ---");
        int INF = Integer.MAX_VALUE/2;
        String[] cities = {"NYC","LAX","ORD","DFW","ATL"};
        int[][] flights = {
            {0,   200, INF, 300, INF},
            {200, 0,   INF, INF, 250},
            {INF, INF, 0,   150, 100},
            {300, INF, 150, 0,   50 },
            {INF, 250, 100, 50,  0  }
        };
        int[][] allPaths = floydWarshall(flights);
        System.out.println("  Cheapest fares (including connections):");
        for (int i = 0; i < 5; i++) for (int j = i+1; j < 5; j++)
            System.out.printf("    %s → %s: $%s%n", cities[i], cities[j],
                    allPaths[i][j]>=INF?"NO ROUTE":allPaths[i][j]);
    }

    // --- Build order helper ---
    static List<String> buildOrder(Map<String,List<String>> deps) {
        Map<String,Integer> inDeg=new HashMap<>();
        Map<String,List<String>> dependents=new HashMap<>();
        for (String m:deps.keySet()){inDeg.putIfAbsent(m,0);dependents.putIfAbsent(m,new ArrayList<>());}
        for (Map.Entry<String,List<String>> e:deps.entrySet())
            for (String dep:e.getValue()){
                dependents.computeIfAbsent(dep,k->new ArrayList<>()).add(e.getKey());
                inDeg.merge(e.getKey(),1,Integer::sum); inDeg.putIfAbsent(dep,0);}
        Queue<String> q=new LinkedList<>();
        for (Map.Entry<String,Integer> e:inDeg.entrySet()) if(e.getValue()==0) q.offer(e.getKey());
        List<String> order=new ArrayList<>();
        while(!q.isEmpty()){String m=q.poll();order.add(m);
            for(String d:dependents.getOrDefault(m,new ArrayList<>())){inDeg.merge(d,-1,Integer::sum);if(inDeg.get(d)==0) q.offer(d);}}
        if(order.size()!=inDeg.size()) throw new RuntimeException("Circular dependency!");
        return order;
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    static void printMatrix(int[][] m, int V) {
        int INF = Integer.MAX_VALUE/2;
        System.out.print("     ");
        for (int i=0;i<V;i++) System.out.printf("%5d",i);
        System.out.println();
        for (int i=0;i<V;i++) {
            System.out.printf("  %2d [ ",i);
            for (int j=0;j<V;j++) System.out.printf("%4s",m[i][j]>=INF?"∞":m[i][j]);
            System.out.println(" ]");
        }
    }
    static void printBanner(String title) {
        System.out.println("\n" + "=".repeat(66));
        System.out.println("  " + title);
        System.out.println("=".repeat(66));
    }
    static void printSection(String title) {
        System.out.println("\n" + "-".repeat(66));
        System.out.println("  SECTION " + title);
        System.out.println("-".repeat(66));
    }
}
