import java.util.*;
import java.util.stream.*;

/**
 * SOLUTIONS: Questions 6–10
 * Q6  — Real-Time Stock Ticker (Segment Tree + Two Pointer)
 * Q7  — Autocomplete Search Engine (Trie + KMP + Rolling Hash + Sliding Window)
 * Q8  — Task Scheduler with Dependencies (Graph Cycle + Critical Path + Command + Template)
 * Q9  — Memory-Efficient Bit Permissions (Bit Manipulation)
 * Q10 — Warehouse Robot Path Optimizer (BFS + Bitmask DP + Two Pointer + Grid DP)
 *
 * Compile: javac Solutions_Q6_Q10.java
 * Run:     java Solutions_Q6_Q10
 */
public class Solutions_Q6_Q10 {

    public static void main(String[] args) {
        System.out.println("=== Q6: Stock Ticker Segment Tree ===");
        Q6_StockTicker.demo();

        System.out.println("\n=== Q7: Autocomplete Search Engine ===");
        Q7_Autocomplete.demo();

        System.out.println("\n=== Q8: Task Scheduler ===");
        Q8_TaskScheduler.demo();

        System.out.println("\n=== Q9: Bit Permissions System ===");
        Q9_BitPermissions.demo();

        System.out.println("\n=== Q10: Warehouse Robot ===");
        Q10_WarehouseRobot.demo();
    }

    // =========================================================
    // Q6 — REAL-TIME STOCK TICKER
    // Topics: Segment Tree (max/min/sum/maxDrop) · Two Pointer
    // =========================================================
    static class Q6_StockTicker {

        // Segment Tree storing max, min, sum, and maxDrop per range
        static class StockSegmentTree {
            int n;
            long[] maxV, minV, sumV, maxDrop; // maxDrop = max(price[i]-price[j]) for i<j in range

            StockSegmentTree(int[] prices) {
                n = prices.length;
                maxV = new long[4*n]; minV = new long[4*n];
                sumV = new long[4*n]; maxDrop = new long[4*n];
                Arrays.fill(minV, Long.MAX_VALUE);
                build(prices, 0, 0, n-1);
            }

            void build(int[] arr, int nd, int s, int e) {
                if (s == e) {
                    maxV[nd] = minV[nd] = sumV[nd] = arr[s];
                    maxDrop[nd] = 0;
                    return;
                }
                int m = (s+e)/2;
                build(arr, 2*nd+1, s, m);
                build(arr, 2*nd+2, m+1, e);
                pushUp(nd);
            }

            void pushUp(int nd) {
                int l = 2*nd+1, r = 2*nd+2;
                maxV[nd] = Math.max(maxV[l], maxV[r]);
                minV[nd] = Math.min(minV[l], minV[r]);
                sumV[nd] = sumV[l] + sumV[r];
                // maxDrop can be: within left, within right, or left.max - right.min
                maxDrop[nd] = Math.max(Math.max(maxDrop[l], maxDrop[r]),
                                       maxV[l] - minV[r]);
            }

            void update(int nd, int s, int e, int i, int val) {
                if (s == e) { maxV[nd]=minV[nd]=sumV[nd]=val; maxDrop[nd]=0; return; }
                int m=(s+e)/2;
                if (i<=m) update(2*nd+1,s,m,i,val);
                else      update(2*nd+2,m+1,e,i,val);
                pushUp(nd);
            }

            long[] query(int nd, int s, int e, int l, int r) {
                // Returns [max, min, sum, maxDrop]
                if (r<s || e<l) return new long[]{Long.MIN_VALUE, Long.MAX_VALUE, 0, 0};
                if (l<=s && e<=r) return new long[]{maxV[nd], minV[nd], sumV[nd], maxDrop[nd]};
                int m=(s+e)/2;
                long[] left  = query(2*nd+1, s, m, l, r);
                long[] right = query(2*nd+2, m+1, e, l, r);
                long qMax = Math.max(left[0], right[0]);
                long qMin = Math.min(left[1], right[1]);
                long qSum = left[2] + right[2];
                long qDrop = Math.max(Math.max(left[3], right[3]), left[0]-right[1]);
                return new long[]{qMax, qMin, qSum, qDrop};
            }

            void update(int i, int val) { update(0, 0, n-1, i, val); }

            long rangeMax(int l, int r)  { return query(0,0,n-1,l,r)[0]; }
            long rangeMin(int l, int r)  { return query(0,0,n-1,l,r)[1]; }
            long rangeSum(int l, int r)  { return query(0,0,n-1,l,r)[2]; }
            long maxDrop(int l, int r)   { return query(0,0,n-1,l,r)[3]; }
        }

        // Q6.2 — Two Pointer: count pairs with profit >= targetGain within maxDays
        static int countProfitPairs(long[] prices, long[] timestamps,
                                     long targetGain, long maxHoldingDays) {
            int n = prices.length, count = 0;
            int right = 0;
            for (int left = 0; left < n; left++) {
                // Advance right as far as timestamp constraint allows
                while (right + 1 < n &&
                       timestamps[right+1] - timestamps[left] <= maxHoldingDays) {
                    right++;
                }
                // Count valid j > left where profit >= target and within time window
                for (int j = left+1; j <= right; j++) {
                    if (prices[j] - prices[left] >= targetGain) count++;
                }
                if (right <= left) right = left + 1;
            }
            return count;
        }

        static void demo() {
            int[] prices = {100, 180, 90, 210, 150, 300, 250, 400};
            StockSegmentTree tree = new StockSegmentTree(prices);

            System.out.println("  Prices: " + Arrays.toString(prices));
            System.out.printf("  rangeMax(1,5)=%.0f  rangeMin(1,5)=%.0f  rangeSum(1,5)=%.0f%n",
                (double)tree.rangeMax(1,5), (double)tree.rangeMin(1,5), (double)tree.rangeSum(1,5));
            System.out.printf("  maxDrop(0,7)=%.0f (max price fall within range)%n",
                (double)tree.maxDrop(0,7));

            tree.update(2, 50); // Update price at index 2
            System.out.printf("  After update(2,50): rangeMin(0,3)=%.0f%n", (double)tree.rangeMin(0,3));

            // Two pointer profit pairs
            long[] ts = {1,2,3,4,5,6,7,8};
            long[] px = {100,180,90,210,150,300,250,400};
            int pairs = countProfitPairs(px, ts, 100, 4);
            System.out.println("  Profit pairs (gain≥100, hold≤4 days): " + pairs);

            // Complexity proof
            System.out.println("  Prefix sum rebuild at 10K updates/sec × 1M prices = 10B ops/sec");
            System.out.println("  Segment tree: 10K × log(1M) ≈ 10K × 20 = 200K update ops/sec ✓");
        }
    }

    // =========================================================
    // Q7 — AUTOCOMPLETE SEARCH ENGINE
    // Topics: Trie · KMP · Rabin-Karp · Sliding Window + Hashing
    // =========================================================
    static class Q7_Autocomplete {

        // ── Trie with frequency and top-K suggestions ─────────
        static class TrieNode {
            Map<Character, TrieNode> children = new HashMap<>();
            int frequency = 0;
            boolean isEnd = false;
            String word = null;
        }

        static class Trie {
            TrieNode root = new TrieNode();

            void insert(String word) {
                TrieNode cur = root;
                for (char c : word.toCharArray()) {
                    cur.children.putIfAbsent(c, new TrieNode());
                    cur = cur.children.get(c);
                }
                cur.isEnd = true;
                cur.word = word;
                cur.frequency++;
            }

            List<String> topKSuggestions(String prefix, int k) {
                TrieNode cur = root;
                for (char c : prefix.toCharArray()) {
                    if (!cur.children.containsKey(c)) return List.of();
                    cur = cur.children.get(c);
                }
                PriorityQueue<TrieNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.frequency));
                collectAll(cur, pq, k);
                List<String> result = new ArrayList<>();
                while (!pq.isEmpty()) result.add(0, pq.poll().word);
                return result;
            }

            void collectAll(TrieNode node, PriorityQueue<TrieNode> pq, int k) {
                if (node == null) return;
                if (node.isEnd) {
                    pq.offer(node);
                    if (pq.size() > k) pq.poll();
                }
                for (TrieNode child : node.children.values()) collectAll(child, pq, k);
            }
        }

        // ── KMP pattern search ────────────────────────────────
        static int[] buildLPS(String pattern) {
            int n = pattern.length();
            int[] lps = new int[n];
            int len = 0, i = 1;
            while (i < n) {
                if (pattern.charAt(i) == pattern.charAt(len)) { lps[i++] = ++len; }
                else if (len != 0) { len = lps[len - 1]; }
                else { lps[i++] = 0; }
            }
            return lps;
        }

        static List<Integer> kmpSearch(String text, String pattern) {
            int[] lps = buildLPS(pattern);
            List<Integer> result = new ArrayList<>();
            int i = 0, j = 0;
            while (i < text.length()) {
                if (text.charAt(i) == pattern.charAt(j)) { i++; j++; }
                if (j == pattern.length()) { result.add(i - j); j = lps[j-1]; }
                else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
                    if (j != 0) j = lps[j-1]; else i++;
                }
            }
            return result;
        }

        // ── Rabin-Karp rolling hash ───────────────────────────
        static List<Integer> rabinKarpSearch(String text, String pattern) {
            long BASE = 31, MOD = 1_000_000_007L;
            int m = pattern.length(), n = text.length();
            if (m > n) return List.of();
            long power = 1;
            for (int i = 0; i < m-1; i++) power = power * BASE % MOD;
            long patHash = 0, winHash = 0;
            for (int i = 0; i < m; i++) {
                patHash = (patHash * BASE + pattern.charAt(i)) % MOD;
                winHash = (winHash * BASE + text.charAt(i)) % MOD;
            }
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i <= n - m; i++) {
                if (winHash == patHash && text.substring(i, i+m).equals(pattern)) result.add(i);
                if (i < n - m) {
                    winHash = (winHash - text.charAt(i) * power % MOD + MOD) % MOD;
                    winHash = (winHash * BASE + text.charAt(i+m)) % MOD;
                }
            }
            return result;
        }

        // ── Sliding window most frequent query ────────────────
        static String mostFrequentInWindow(List<String> queries, int windowSize) {
            Map<String, Integer> freq = new HashMap<>();
            Deque<String> window = new ArrayDeque<>();
            String mostFrequent = null; int maxFreq = 0;

            for (String q : queries) {
                window.offer(q);
                freq.merge(q, 1, Integer::sum);
                if (window.size() > windowSize) {
                    String out = window.poll();
                    freq.merge(out, -1, Integer::sum);
                    if (freq.get(out) == 0) freq.remove(out);
                }
                int f = freq.getOrDefault(q, 0);
                if (f > maxFreq) { maxFreq = f; mostFrequent = q; }
            }
            return mostFrequent;
        }

        static void demo() {
            Trie trie = new Trie();
            String[] words = {"apple","application","apply","apt","aptitude","apparel","art","article"};
            for (String w : words) { trie.insert(w); trie.insert(w); } // Insert twice for frequency
            trie.insert("apple"); // apple has highest frequency now

            System.out.println("  Top 3 for 'app': " + trie.topKSuggestions("app", 3));
            System.out.println("  Top 2 for 'ar':  " + trie.topKSuggestions("ar", 2));

            String stream = "aababcabcdabcde";
            String spam = "abc";
            System.out.println("  KMP '" + spam + "' in '" + stream + "': " + kmpSearch(stream, spam));
            System.out.println("  RK  '" + spam + "' in '" + stream + "': " + rabinKarpSearch(stream, spam));

            List<String> queries = Arrays.asList("java","python","java","go","java","python","rust","python");
            System.out.println("  Most frequent in window-4: " + mostFrequentInWindow(queries, 4));
        }
    }

    // =========================================================
    // Q8 — TASK SCHEDULER WITH DEPENDENCIES
    // Topics: DAG Cycle Detection · Critical Path (Topo+DP) · Command · Template Method
    // =========================================================
    static class Q8_TaskScheduler {

        // Q8.1 & Q8.2 — DAG cycle detection + critical path
        enum Color { WHITE, GREY, BLACK }

        static class TaskGraph {
            Map<String, List<String>> adj = new LinkedHashMap<>();
            Map<String, Integer> duration = new HashMap<>();

            void addTask(String name, int dur) { adj.putIfAbsent(name, new ArrayList<>()); duration.put(name, dur); }
            void addDependency(String from, String to) { adj.computeIfAbsent(from, k -> new ArrayList<>()).add(to); }

            // Cycle detection — DFS coloring
            List<String> detectCycle() {
                Map<String, Color> color = new HashMap<>();
                adj.keySet().forEach(k -> color.put(k, Color.WHITE));
                List<String> cyclePath = new ArrayList<>();
                for (String node : adj.keySet()) {
                    if (color.get(node) == Color.WHITE) {
                        if (dfsCycle(node, color, new ArrayDeque<>(), cyclePath)) return cyclePath;
                    }
                }
                return Collections.emptyList();
            }

            boolean dfsCycle(String u, Map<String,Color> color, Deque<String> path, List<String> cycle) {
                color.put(u, Color.GREY); path.push(u);
                for (String v : adj.getOrDefault(u, List.of())) {
                    if (color.get(v) == Color.GREY) {
                        // Found cycle — collect it
                        List<String> c = new ArrayList<>();
                        for (String s : path) { c.add(0, s); if (s.equals(v)) break; }
                        cycle.addAll(c);
                        return true;
                    }
                    if (color.get(v) == Color.WHITE && dfsCycle(v, color, path, cycle)) return true;
                }
                color.put(u, Color.BLACK); path.pop();
                return false;
            }

            // Critical path via topological sort + DP
            List<String> criticalPath() {
                // Kahn's topo sort
                Map<String, Integer> inDeg = new HashMap<>();
                adj.keySet().forEach(k -> inDeg.put(k, 0));
                adj.values().forEach(vs -> vs.forEach(v -> inDeg.merge(v, 1, Integer::sum)));

                Queue<String> queue = new LinkedList<>();
                inDeg.entrySet().stream().filter(e -> e.getValue()==0).map(Map.Entry::getKey).forEach(queue::offer);
                List<String> topo = new ArrayList<>();
                while (!queue.isEmpty()) {
                    String u = queue.poll(); topo.add(u);
                    adj.getOrDefault(u, List.of()).forEach(v -> { if (inDeg.merge(v,-1,Integer::sum)==0) queue.offer(v); });
                }

                // DP on topological order
                Map<String, Integer> dp = new HashMap<>();
                Map<String, String> parent = new HashMap<>();
                topo.forEach(t -> { dp.put(t, duration.get(t)); parent.put(t, null); });

                for (String u : topo) {
                    for (String v : adj.getOrDefault(u, List.of())) {
                        int newVal = dp.get(u) + duration.get(v);
                        if (newVal > dp.get(v)) { dp.put(v, newVal); parent.put(v, u); }
                    }
                }

                // Find end of critical path
                String end = dp.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                List<String> path = new ArrayList<>();
                for (String at = end; at != null; at = parent.get(at)) path.add(0, at);
                return path;
            }
        }

        // Q8.3 — Command pattern with undo
        interface TaskCommand {
            void execute();
            void undo();
            String describe();
        }

        static class RunTaskCommand implements TaskCommand {
            String taskName; List<String> executedLog;
            RunTaskCommand(String n, List<String> log) { taskName=n; executedLog=log; }
            public void execute() { executedLog.add(taskName); System.out.println("  [CMD] Execute: "+taskName); }
            public void undo()    { executedLog.remove(taskName); System.out.println("  [CMD] Undo: "+taskName); }
            public String describe() { return "Run:" + taskName; }
        }

        static class BatchScheduler {
            Deque<TaskCommand> history = new ArrayDeque<>();
            List<String> log = new ArrayList<>();

            void execute(TaskCommand cmd) { cmd.execute(); history.push(cmd); }
            void undoLast() { if (!history.isEmpty()) { history.pop().undo(); } }
            List<String> getLog() { return log; }
        }

        // Q8.4 — Template Method for tasks
        static abstract class AbstractTask {
            final String name;
            AbstractTask(String n) { name = n; }

            // Template method — final so no subclass can change the algorithm
            public final void run() {
                validate();
                prepare();
                executeTask();
                cleanup();
            }

            private void validate() { System.out.println("  [" + name + "] Validating..."); }
            protected abstract void prepare();
            protected abstract void executeTask();
            private void cleanup() { System.out.println("  [" + name + "] Cleanup done."); }
        }

        static class CompileTask extends AbstractTask {
            CompileTask(String n) { super(n); }
            protected void prepare()      { System.out.println("  ["+name+"] Loading source files..."); }
            protected void executeTask()  { System.out.println("  ["+name+"] Compiling Java sources..."); }
        }

        static class TestTask extends AbstractTask {
            TestTask(String n) { super(n); }
            protected void prepare()      { System.out.println("  ["+name+"] Setting up test fixtures..."); }
            protected void executeTask()  { System.out.println("  ["+name+"] Running JUnit tests..."); }
        }

        static void demo() {
            TaskGraph graph = new TaskGraph();
            graph.addTask("A", 3); graph.addTask("B", 2); graph.addTask("C", 5);
            graph.addTask("D", 1); graph.addTask("E", 4);
            graph.addDependency("A","B"); graph.addDependency("A","C");
            graph.addDependency("B","D"); graph.addDependency("C","D");
            graph.addDependency("D","E");

            System.out.println("  Cycle detection: " + (graph.detectCycle().isEmpty() ? "No cycle ✓" : "CYCLE!"));
            List<String> critical = graph.criticalPath();
            System.out.println("  Critical path: " + critical);

            // Command + undo
            BatchScheduler scheduler = new BatchScheduler();
            critical.forEach(t -> scheduler.execute(new RunTaskCommand(t, scheduler.log)));
            System.out.println("  Log after execution: " + scheduler.log);
            scheduler.undoLast();
            System.out.println("  Log after undo: " + scheduler.log);

            // Template Method
            new CompileTask("javac").run();
            new TestTask("junit").run();
        }
    }

    // =========================================================
    // Q9 — MEMORY-EFFICIENT BIT PERMISSIONS
    // Topics: Bit Manipulation · Optimization
    // =========================================================
    static class Q9_BitPermissions {

        enum Permission {
            READ(0), WRITE(1), EXECUTE(2), SHARE(3), DELETE(4), AUDIT(5);
            final int bit;
            Permission(int b) { bit = b; }
        }

        static int grant(int permissions, Permission p)  { return permissions |  (1 << p.bit); }
        static int revoke(int permissions, Permission p) { return permissions & ~(1 << p.bit); }
        static boolean has(int permissions, Permission p){ return (permissions & (1 << p.bit)) != 0; }
        static int toggle(int permissions, Permission p) { return permissions ^  (1 << p.bit); }

        static int union(int a, int b)        { return a | b; }
        static int intersection(int a, int b) { return a & b; }
        static int difference(int a, int b)   { return a & ~b; }

        // Brian Kernighan: O(number of set bits)
        static int countPermissions(int permissions) {
            int count = 0;
            while (permissions != 0) {
                permissions &= (permissions - 1); // Clears the lowest set bit
                count++;
            }
            return count;
        }

        static String describe(int permissions) {
            StringBuilder sb = new StringBuilder("[");
            for (Permission p : Permission.values()) {
                if (has(permissions, p)) sb.append(p.name()).append(",");
            }
            if (sb.length() > 1) sb.setLength(sb.length()-1);
            return sb.append("]").toString();
        }

        static void demo() {
            int perms = 0;
            perms = grant(perms, Permission.READ);
            perms = grant(perms, Permission.WRITE);
            perms = grant(perms, Permission.EXECUTE);
            System.out.println("  Permissions: " + describe(perms));
            System.out.println("  Has READ: " + has(perms, Permission.READ));
            System.out.println("  Has DELETE: " + has(perms, Permission.DELETE));

            perms = revoke(perms, Permission.WRITE);
            System.out.println("  After revoke WRITE: " + describe(perms));

            perms = toggle(perms, Permission.SHARE);
            System.out.println("  After toggle SHARE: " + describe(perms));

            int adminPerms = 0;
            for (Permission p : Permission.values()) adminPerms = grant(adminPerms, p);
            System.out.println("  Admin permissions: " + describe(adminPerms));
            System.out.println("  Union (user|admin): " + describe(union(perms, adminPerms)));
            System.out.println("  Intersection:       " + describe(intersection(perms, adminPerms)));
            System.out.println("  Difference (admin-user): " + describe(difference(adminPerms, perms)));

            System.out.println("  Count (Brian Kernighan): " + countPermissions(perms));

            // Memory proof
            long setPerms  = 8L * 8 * 10_000_000 / 1_000_000;  // 8 ref × 8 bytes × 10M files
            long bitmaskMB = 4L * 10_000_000 / 1_000_000;        // 4 bytes × 10M files
            System.out.printf("  Memory: Set<Permission> ≈ %dMB | int bitmask ≈ %dMB (%.0fx saving)%n",
                setPerms, bitmaskMB, (double)setPerms/bitmaskMB);
        }
    }

    // =========================================================
    // Q10 — WAREHOUSE ROBOT PATH OPTIMIZER
    // Topics: BFS + Bitmask DP (TSP) + Two Pointer + Grid DP
    // =========================================================
    static class Q10_WarehouseRobot {

        // Q10.1 — BFS shortest path on grid
        static List<int[]> bfsPath(int[][] grid, int[] start, int[] goal) {
            int rows = grid.length, cols = grid[0].length;
            boolean[][] visited = new boolean[rows][cols];
            int[][][] prev = new int[rows][cols][2];
            for (int[][] row : prev) for (int[] cell : row) Arrays.fill(cell, -1);

            Queue<int[]> queue = new LinkedList<>();
            visited[start[0]][start[1]] = true;
            queue.offer(start);

            int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
            while (!queue.isEmpty()) {
                int[] cur = queue.poll();
                if (cur[0]==goal[0] && cur[1]==goal[1]) break;
                for (int[] d : dirs) {
                    int nr=cur[0]+d[0], nc=cur[1]+d[1];
                    if (nr>=0&&nr<rows&&nc>=0&&nc<cols&&!visited[nr][nc]&&grid[nr][nc]==0) {
                        visited[nr][nc] = true;
                        prev[nr][nc] = new int[]{cur[0], cur[1]};
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
            // Reconstruct
            List<int[]> path = new ArrayList<>();
            int[] at = goal.clone();
            while (prev[at[0]][at[1]][0] != -1 || (at[0]==start[0] && at[1]==start[1])) {
                path.add(0, at.clone());
                if (at[0]==start[0] && at[1]==start[1]) break;
                int[] p = prev[at[0]][at[1]];
                at = p;
            }
            return path;
        }

        // Q10.2 — Bitmask DP for TSP (visit all shelves)
        static int tspBitmask(int[][] dist, int n) {
            int[][] dp = new int[1<<n][n];
            for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE/2);
            dp[1][0] = 0; // Start at depot (node 0), mask=1 (visited depot)

            for (int mask = 1; mask < (1<<n); mask++) {
                for (int u = 0; u < n; u++) {
                    if ((mask & (1<<u)) == 0 || dp[mask][u] >= Integer.MAX_VALUE/2) continue;
                    for (int v = 0; v < n; v++) {
                        if ((mask & (1<<v)) != 0) continue;
                        int nm = mask | (1<<v);
                        dp[nm][v] = Math.min(dp[nm][v], dp[mask][u] + dist[u][v]);
                    }
                }
            }
            int full = (1<<n)-1, ans = Integer.MAX_VALUE;
            for (int u = 1; u < n; u++) ans = Math.min(ans, dp[full][u] + dist[u][0]);
            return ans;
        }

        // Q10.3 — Two pointer collision detection
        static int[] detectCollision(int[][] pathA, int[][] pathB) {
            // pathA[i] = {x, y, time}, sorted by time
            int i=0, j=0;
            while (i < pathA.length && j < pathB.length) {
                if (pathA[i][2] == pathB[j][2]) {
                    if (pathA[i][0]==pathB[j][0] && pathA[i][1]==pathB[j][1])
                        return new int[]{pathA[i][0], pathA[i][1], pathA[i][2]};
                    i++; j++;
                } else if (pathA[i][2] < pathB[j][2]) i++;
                else j++;
            }
            return null;
        }

        // Q10.4 — Min cost grid DP
        static int minPathSum(int[][] grid) {
            int m=grid.length, n=grid[0].length;
            int[][] dp = new int[m][n];
            dp[0][0] = grid[0][0];
            for (int i=1;i<m;i++) dp[i][0] = dp[i-1][0]+grid[i][0];
            for (int j=1;j<n;j++) dp[0][j] = dp[0][j-1]+grid[0][j];
            for (int i=1;i<m;i++) for (int j=1;j<n;j++)
                dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + grid[i][j];
            return dp[m-1][n-1];
        }

        static void demo() {
            // Q10.1 BFS
            int[][] warehouseGrid = {
                {0,0,0,0,1},
                {0,1,1,0,0},
                {0,0,0,0,0},
                {1,1,0,1,0},
                {0,0,0,0,0}
            };
            List<int[]> path = bfsPath(warehouseGrid, new int[]{0,0}, new int[]{4,4});
            System.out.print("  BFS path: ");
            path.forEach(p -> System.out.print("["+p[0]+","+p[1]+"] "));
            System.out.println("("+path.size()+" steps)");

            // Q10.2 TSP bitmask
            int[][] dist = {{0,10,15,20},{10,0,35,25},{15,35,0,30},{20,25,30,0}};
            System.out.println("  Min TSP tour (4 shelves): " + tspBitmask(dist, 4));

            // Q10.3 collision detection
            int[][] pathA = {{0,0,0},{0,1,1},{1,1,2},{2,1,3}};
            int[][] pathB = {{3,0,0},{2,0,1},{1,1,2},{0,1,3}};
            int[] collision = detectCollision(pathA, pathB);
            System.out.println("  Collision: " + (collision!=null ? Arrays.toString(collision) : "none"));

            // Q10.4 min path sum
            int[][] costGrid = {{1,3,1},{1,5,1},{4,2,1}};
            System.out.println("  Min path sum (top-left→bottom-right): " + minPathSum(costGrid));
        }
    }
}
