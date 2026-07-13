import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 * GREEDY ALGORITHMS — Complete Executable Reference
 * ============================================================
 * Topics:
 *  1. What is Greedy?              (coin change greedy vs DP demo,
 *                                   fractional vs 0/1 knapsack contrast)
 *  2. When Does Greedy Work?       (exchange argument demos, counterexamples,
 *                                   "staying ahead" verification)
 *  3. Greedy vs DP                 (side-by-side coin change, knapsack,
 *                                   shortest path comparison)
 *  4. Classic Greedy Problems      (activity selection, job scheduling,
 *                                   fractional knapsack, Huffman, gas station,
 *                                   jump game, meeting rooms, task scheduler,
 *                                   Kruskal MST, Dijkstra, largest number,
 *                                   partition labels, assign cookies)
 *  5. Real-World Applications      (stock profit, lemonade change,
 *                                   two-city scheduling, bandwidth allocation,
 *                                   file merge, network design)
 *  6. Complexity Analysis          (live timing comparison, operation counts)
 *  7. Interview Strategies         (non-overlapping intervals, min arrows,
 *                                   lemonade change, two-city scheduling,
 *                                   hand of straights, reorganize string)
 *
 * Compile : javac GreedyAlgorithms.java
 * Run     : java GreedyAlgorithms
 * ============================================================
 */
public class GreedyAlgorithms {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("GREEDY ALGORITHMS — COMPLETE DEMO");

        section1_WhatIsGreedy();
        section2_WhenDoesGreedyWork();
        section3_GreedyVsDP();
        section4_ClassicGreedyProblems();
        section5_RealWorldApplications();
        section6_ComplexityAnalysis();
        section7_InterviewStrategies();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — WHAT IS GREEDY?
    // =========================================================
    static void section1_WhatIsGreedy() {
        printSection("1. WHAT IS GREEDY?");

        // 1a. Coin change — US coins (greedy works)
        System.out.println("--- 1a. Coin Change: US Coins {25,10,5,1} ---");
        int[] usCoins = {25, 10, 5, 1};
        int[] amounts = {41, 30, 99, 67, 1};
        for (int amt : amounts) {
            int[] result = coinChangeGreedyCoins(usCoins, amt);
            System.out.printf("  Change for %2d¢: %s (%d coins)%n",
                    amt, Arrays.toString(result), result.length);
        }

        // 1b. Coin change — arbitrary coins (greedy FAILS)
        System.out.println("\n--- 1b. Coin Change: Arbitrary Coins {1,3,4} — Greedy FAILS ---");
        int[] arbCoins = {1, 3, 4};
        int target = 6;
        System.out.println("  Coins: " + Arrays.toString(arbCoins) + ", Amount: " + target);
        System.out.println("  Greedy: " + coinChangeGreedyCount(arbCoins, target)
                + " coins → " + Arrays.toString(coinChangeGreedyCoins(arbCoins, target))
                + " ← WRONG (3 coins)");
        System.out.println("  DP:     " + coinChangeDP(arbCoins, target)
                + " coins → optimal (3+3=2 coins)");

        // 1c. Greedy paradigm visual
        System.out.println("\n--- 1c. Greedy Paradigm ---");
        System.out.println("  At each step: pick the LOCALLY OPTIMAL choice");
        System.out.println("  Never backtrack. Never reconsider.");
        System.out.println("  When greedy choice property holds → globally optimal.");
        System.out.println("  When it doesn't → use DP or backtracking.");

        // 1d. Comparison table
        System.out.println("\n--- 1d. Paradigm Comparison ---");
        System.out.printf("  %-20s %-12s %-12s %-10s%n",
                "Paradigm","Backtracks?","Complexity","Example");
        System.out.printf("  %-20s %-12s %-12s %-10s%n",
                "Brute Force","Yes (all)","O(2^n)","TSP exact");
        System.out.printf("  %-20s %-12s %-12s %-10s%n",
                "Backtracking","Yes (fail)","O(b^d)","N-Queens");
        System.out.printf("  %-20s %-12s %-12s %-10s%n",
                "Dynamic Prog.","No","O(n^2)","0/1 Knapsack");
        System.out.printf("  %-20s %-12s %-12s %-10s%n",
                "Greedy","Never","O(n log n)","Activity Sel.");
    }

    // --- Coin change implementations ---
    static int[] coinChangeGreedyCoins(int[] coins, int amount) {
        List<Integer> used = new ArrayList<>();
        for (int i = coins.length - 1; i >= 0 && amount > 0; i--)
            while (amount >= coins[i]) { used.add(coins[i]); amount -= coins[i]; }
        return used.stream().mapToInt(Integer::intValue).toArray();
    }
    static int coinChangeGreedyCount(int[] coins, int amount) {
        int count = 0;
        for (int i = coins.length - 1; i >= 0 && amount > 0; i--) {
            count += amount / coins[i]; amount %= coins[i];
        }
        return amount == 0 ? count : -1;
    }
    static int coinChangeDP(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++)
            for (int coin : coins)
                if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        return dp[amount] > amount ? -1 : dp[amount];
    }

    // =========================================================
    // SECTION 2 — WHEN DOES GREEDY WORK?
    // =========================================================
    static void section2_WhenDoesGreedyWork() {
        printSection("2. WHEN DOES GREEDY WORK?");

        // 2a. Activity selection — show exchange argument
        System.out.println("--- 2a. Activity Selection: Exchange Argument Demo ---");
        int[][] activities = {{1,4},{3,5},{0,6},{5,7},{3,9},{5,9},{6,10},{8,11},{8,12},{2,14},{12,16}};
        System.out.println("  Activities (start,end): " + Arrays.deepToString(activities));
        System.out.println("  Sorted by end time:");
        int[][] sorted = activities.clone();
        Arrays.sort(sorted, (a, b) -> a[1] - b[1]);
        System.out.println("  " + Arrays.deepToString(sorted));
        List<int[]> selected = activitySelection(activities);
        System.out.println("  Greedy selects (" + selected.size() + "): " +
                selected.stream().map(Arrays::toString).collect(Collectors.joining(",")));

        // 2b. Counterexamples — when greedy fails
        System.out.println("\n--- 2b. Counterexamples: When Greedy Fails ---");
        System.out.println("  Coin Change {1,3,4} for 6:");
        System.out.println("    Greedy: 4+1+1=3 coins | DP: 3+3=2 coins ← greedy fails");

        System.out.println("  0/1 Knapsack (capacity=5):");
        System.out.println("    Items: (w=2,v=6),(w=2,v=10),(w=3,v=12)");
        System.out.println("    Greedy by ratio: (10/2=5.0)→item2 + can't fit item3 → v=10");
        System.out.println("    Optimal (DP): item2+item3 = w5, v=22 ← greedy fails");

        System.out.println("  Shortest path with negative edges:");
        System.out.println("    A→B (w=1), A→C (w=3), C→B (w=-4)");
        System.out.println("    Greedy (Dijkstra): A→B cost=1");
        System.out.println("    Optimal: A→C→B cost=-1 ← greedy fails with negatives");

        // 2c. Problems where greedy provably works
        System.out.println("\n--- 2c. Provably Correct Greedy Problems ---");
        String[] problems = {
            "Activity Selection     → sort by end time, take earliest non-conflicting",
            "Fractional Knapsack   → sort by value/weight ratio, take greedily",
            "Huffman Encoding       → always merge two smallest frequency nodes",
            "Dijkstra Shortest Path → always expand closest unvisited node",
            "Kruskal MST            → always add cheapest edge that doesn't cycle",
            "Job Scheduling (EDF)   → always schedule earliest deadline first",
            "Gas Station            → if total surplus ≥ 0, start at first surplus",
            "Jump Game              → track max reachable index"
        };
        for (String p : problems) System.out.println("  ✓ " + p);

        // 2d. The "staying ahead" property demo
        System.out.println("\n--- 2d. Staying Ahead Property (Activity Selection) ---");
        System.out.println("  At each step, greedy has finished no later than any other algorithm:");
        System.out.println("  After 1st selection: greedy ends at t=4, optimal ends at t=4 (same)");
        System.out.println("  After 2nd: greedy ends at t=7, any other ≥ t=7");
        System.out.println("  After 3rd: greedy ends at t=11, any other ≥ t=11");
        System.out.println("  → Greedy is always 'ahead' → fits at least as many activities");
    }

    // =========================================================
    // SECTION 3 — GREEDY VS DYNAMIC PROGRAMMING
    // =========================================================
    static void section3_GreedyVsDP() {
        printSection("3. GREEDY VS DYNAMIC PROGRAMMING");

        // 3a. Coin change — side by side
        System.out.println("--- 3a. Coin Change: Greedy vs DP ---");
        System.out.println("  US coins {1,5,10,25}:");
        int[] usCoins = {1, 5, 10, 25};
        for (int amt : new int[]{30, 41, 63, 99}) {
            int g = coinChangeGreedyCount(usCoins, amt);
            int d = coinChangeDP(usCoins, amt);
            System.out.printf("    Amount=%2d: Greedy=%d DP=%d %s%n",
                    amt, g, d, g == d ? "✓ match" : "✗ differ");
        }
        System.out.println("  Arbitrary coins {1,3,4}:");
        int[] arbCoins = {1, 3, 4};
        for (int amt : new int[]{6, 7, 8, 11}) {
            int g = coinChangeGreedyCount(arbCoins, amt);
            int d = coinChangeDP(arbCoins, amt);
            System.out.printf("    Amount=%2d: Greedy=%d DP=%d %s%n",
                    amt, g, d, g == d ? "✓ match" : "✗ GREEDY WRONG");
        }

        // 3b. Knapsack: 0/1 (DP) vs Fractional (Greedy)
        System.out.println("\n--- 3b. Knapsack: 0/1 (DP) vs Fractional (Greedy) ---");
        int[] weights = {2, 3, 4, 5};
        int[] values  = {3, 4, 5, 6};
        int capacity  = 8;
        System.out.println("  Items: weights=" + Arrays.toString(weights)
                + " values=" + Arrays.toString(values) + " capacity=" + capacity);
        System.out.printf("  0/1 Knapsack (DP):         value=%.1f%n",
                (double) knapsack01(weights, values, capacity));
        System.out.printf("  Fractional Knapsack (Greedy): value=%.2f%n",
                fractionalKnapsack(capacity, weights, values));

        // 3c. Shortest path
        System.out.println("\n--- 3c. Shortest Path: Greedy (Dijkstra) vs DP (Bellman-Ford) ---");
        System.out.println("  Dijkstra  : Greedy expansion, O((V+E)logV), fails with negative edges");
        System.out.println("  Bellman-Ford: DP relaxation, O(VE), handles negative edges");
        System.out.println("  When all weights ≥ 0 → Dijkstra is faster and optimal");
        System.out.println("  When negative weights → must use Bellman-Ford");

        // 3d. Decision guide
        System.out.println("\n--- 3d. Decision Guide ---");
        System.out.println("  Use GREEDY when:");
        System.out.println("    ✓ Greedy choice property provable (exchange argument)");
        System.out.println("    ✓ Intervals, scheduling, ratios, spanning trees");
        System.out.println("    ✓ Need O(n log n) solution");
        System.out.println("  Use DP when:");
        System.out.println("    ✓ Greedy fails (find a counterexample)");
        System.out.println("    ✓ 0/1 binary choices");
        System.out.println("    ✓ Overlapping subproblems");
        System.out.println("    ✓ Multiple simultaneous constraints");
    }

    static int knapsack01(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++)
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i-1][w];
                if (weights[i-1] <= w)
                    dp[i][w] = Math.max(dp[i][w], dp[i-1][w-weights[i-1]] + values[i-1]);
            }
        return dp[n][capacity];
    }

    // =========================================================
    // SECTION 4 — CLASSIC GREEDY PROBLEMS
    // =========================================================
    static void section4_ClassicGreedyProblems() {
        printSection("4. CLASSIC GREEDY PROBLEMS");

        // 4a. Activity Selection
        System.out.println("--- 4a. Activity Selection (Max Non-Overlapping) ---");
        int[][] acts = {{1,4},{3,5},{0,6},{5,7},{3,9},{5,9},{6,10},{8,11},{8,12},{2,14},{12,16}};
        List<int[]> sel = activitySelection(acts);
        System.out.println("  Activities: " + acts.length + " total");
        System.out.println("  Selected (" + sel.size() + "): "
                + sel.stream().map(Arrays::toString).collect(Collectors.joining(" → ")));

        // 4b. Job Scheduling — minimize lateness
        System.out.println("\n--- 4b. Job Scheduling: Minimize Maximum Lateness ---");
        int[][] jobs = {{1,3},{2,5},{3,4},{2,6},{1,2}};
        System.out.println("  Jobs (processingTime, deadline):" + Arrays.deepToString(jobs));
        System.out.println("  EDF scheduling:");
        System.out.println("  Max lateness = " + minimizeMaxLateness(jobs));

        // 4c. Fractional Knapsack
        System.out.println("\n--- 4c. Fractional Knapsack ---");
        int[] w = {10, 20, 30};
        int[] v = {60, 100, 120};
        int cap = 50;
        System.out.println("  Weights=" + Arrays.toString(w)
                + " Values=" + Arrays.toString(v) + " Capacity=" + cap);
        double fk = fractionalKnapsack(cap, w, v);
        System.out.printf("  Maximum value = %.2f%n", fk);

        // 4d. Huffman Encoding
        System.out.println("\n--- 4d. Huffman Encoding ---");
        Map<Character, Integer> freq1 = new LinkedHashMap<>();
        freq1.put('a', 5); freq1.put('b', 9); freq1.put('c', 12);
        freq1.put('d', 13); freq1.put('e', 16); freq1.put('f', 45);
        System.out.println("  Frequencies: " + freq1);
        Map<Character, String> codes = huffmanEncoding(freq1);
        System.out.println("  Huffman codes:");
        int totalBits = 0;
        for (Map.Entry<Character, String> e : codes.entrySet()) {
            int bits = freq1.get(e.getKey()) * e.getValue().length();
            totalBits += bits;
            System.out.printf("    '%c' (freq=%2d): %s (%d bits)%n",
                    e.getKey(), freq1.get(e.getKey()), e.getValue(), bits);
        }
        System.out.println("  Total bits = " + totalBits);
        System.out.println("  Fixed 3-bit encoding would need = "
                + freq1.values().stream().mapToInt(Integer::intValue).sum() * 3 + " bits");

        // 4e. Gas Station
        System.out.println("\n--- 4e. Gas Station (Circular Route) ---");
        int[][] gasTests = {{1,2,3,4,5},{3,4,5,1,2},{2,3,4,3,1,2,3,1}};
        int[][] costTests = {{3,4,5,1,2},{1,2,3,4,5},{1,2,3,2,2,1,1,1}};
        for (int i = 0; i < gasTests.length; i++) {
            System.out.println("  gas =" + Arrays.toString(gasTests[i]));
            System.out.println("  cost=" + Arrays.toString(costTests[i]));
            System.out.println("  → start at station: " + canCompleteCircuit(gasTests[i], costTests[i]));
        }

        // 4f. Jump Game
        System.out.println("\n--- 4f. Jump Game ---");
        int[][] jumpArrs = {{2,3,1,1,4},{3,2,1,0,4},{0},{1,0},{2,0,0}};
        for (int[] arr : jumpArrs) {
            System.out.printf("  %-15s → canJump=%s, minJumps=%d%n",
                    Arrays.toString(arr), canJump(arr), jumpMinimum(arr));
        }

        // 4g. Meeting Rooms
        System.out.println("\n--- 4g. Meeting Rooms (Min Rooms Required) ---");
        int[][] meetings1 = {{0,30},{5,10},{15,20}};
        int[][] meetings2 = {{7,10},{2,4}};
        int[][] meetings3 = {{0,10},{5,15},{10,20},{15,25}};
        System.out.println("  " + Arrays.deepToString(meetings1)
                + " → " + minMeetingRooms(meetings1) + " rooms");
        System.out.println("  " + Arrays.deepToString(meetings2)
                + " → " + minMeetingRooms(meetings2) + " rooms");
        System.out.println("  " + Arrays.deepToString(meetings3)
                + " → " + minMeetingRooms(meetings3) + " rooms");

        // 4h. Assign Cookies
        System.out.println("\n--- 4h. Assign Cookies ---");
        System.out.println("  greed=[1,2,3] cookies=[1,1]       → " + findContentChildren(new int[]{1,2,3}, new int[]{1,1}));
        System.out.println("  greed=[1,2] cookies=[1,2,3]        → " + findContentChildren(new int[]{1,2}, new int[]{1,2,3}));
        System.out.println("  greed=[1,2,3] cookies=[3]          → " + findContentChildren(new int[]{1,2,3}, new int[]{3}));

        // 4i. Task Scheduler
        System.out.println("\n--- 4i. Task Scheduler ---");
        char[][] taskSets = {{'A','A','A','B','B','B'},{'A','A','A','B','B','B','C','C','C'},{'A','A','A','A','A','A','B','C','D','E','F','G'}};
        int[] ns = {2, 2, 2};
        for (int i = 0; i < taskSets.length; i++) {
            String ts = new String(taskSets[i]);
            System.out.printf("  tasks=%s n=%d → %d time units%n",
                    ts, ns[i], leastInterval(taskSets[i], ns[i]));
        }

        // 4j. Kruskal MST
        System.out.println("\n--- 4j. Kruskal's Minimum Spanning Tree ---");
        // edges: {weight, u, v}
        int[][] edges = {{4,0,1},{2,0,2},{3,1,2},{1,1,3},{5,2,3},{6,2,4},{3,3,4}};
        System.out.println("  Graph edges (weight,u,v): " + Arrays.deepToString(edges));
        System.out.println("  MST edges added:");
        int mstCost = kruskalMST(edges, 5);
        System.out.println("  Total MST weight = " + mstCost);

        // 4k. Dijkstra
        System.out.println("\n--- 4k. Dijkstra's Shortest Path ---");
        int[][] graph = {
            {0, 4, 0, 0, 0, 0, 0, 8, 0},
            {4, 0, 8, 0, 0, 0, 0,11, 0},
            {0, 8, 0, 7, 0, 4, 0, 0, 2},
            {0, 0, 7, 0, 9,14, 0, 0, 0},
            {0, 0, 0, 9, 0,10, 0, 0, 0},
            {0, 0, 4,14,10, 0, 2, 0, 0},
            {0, 0, 0, 0, 0, 2, 0, 1, 6},
            {8,11, 0, 0, 0, 0, 1, 0, 7},
            {0, 0, 2, 0, 0, 0, 6, 7, 0}
        };
        int[] dists = dijkstra(graph, 0);
        System.out.println("  Shortest distances from vertex 0:");
        for (int i = 0; i < dists.length; i++)
            System.out.printf("    0 → %d : %d%n", i, dists[i]);

        // 4l. Largest Number
        System.out.println("\n--- 4l. Largest Number (Custom Greedy Sort) ---");
        int[][] numSets = {{3,30,34,5,9},{10,2},{1,20,23,4,8},{0,0},{9,99,999}};
        for (int[] nums : numSets)
            System.out.printf("  %-20s → %s%n",
                    Arrays.toString(nums), largestNumber(nums));

        // 4m. Partition Labels
        System.out.println("\n--- 4m. Partition Labels ---");
        String[] strs = {"ababcbacadefegdehijhklij","eccbbbbdec","abcde"};
        for (String s : strs)
            System.out.printf("  \"%s\" → %s%n", s, partitionLabels(s));
    }

    // --- Classic Greedy Implementations ---
    static List<int[]> activitySelection(int[][] activities) {
        int[][] sorted = activities.clone();
        Arrays.sort(sorted, (a, b) -> a[1] - b[1]);
        List<int[]> result = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;
        for (int[] act : sorted)
            if (act[0] >= lastEnd) { result.add(act); lastEnd = act[1]; }
        return result;
    }
    static int minimizeMaxLateness(int[][] jobs) {
        int[][] sorted = jobs.clone();
        Arrays.sort(sorted, (a, b) -> a[1] - b[1]); // EDF
        int t = 0, maxLate = 0;
        for (int[] job : sorted) {
            t += job[0];
            int lateness = Math.max(0, t - job[1]);
            maxLate = Math.max(maxLate, lateness);
            System.out.printf("    Job(t=%d,d=%d): finish=%d lateness=%d%n",
                    job[0], job[1], t, lateness);
        }
        return maxLate;
    }
    static double fractionalKnapsack(int capacity, int[] weights, int[] values) {
        int n = weights.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(
                (double) values[b] / weights[b], (double) values[a] / weights[a]));
        double total = 0; int rem = capacity;
        for (int i : idx) {
            if (rem == 0) break;
            int take = Math.min(weights[i], rem);
            double gain = (double) values[i] * take / weights[i];
            total += gain; rem -= take;
            System.out.printf("    Item %d (w=%d,v=%d,r=%.2f): take %d → +%.2f%n",
                    i, weights[i], values[i], (double) values[i]/weights[i], take, gain);
        }
        return total;
    }
    static Map<Character, String> huffmanEncoding(Map<Character, Integer> freq) {
        PriorityQueue<HuffNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (Map.Entry<Character, Integer> e : freq.entrySet())
            pq.offer(new HuffNode(e.getKey(), e.getValue()));
        while (pq.size() > 1) {
            HuffNode l = pq.poll(), r = pq.poll();
            HuffNode m = new HuffNode('\0', l.freq + r.freq);
            m.left = l; m.right = r; pq.offer(m);
        }
        Map<Character, String> codes = new LinkedHashMap<>();
        buildCodes(pq.poll(), "", codes);
        return codes;
    }
    static void buildCodes(HuffNode node, String code, Map<Character, String> codes) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            codes.put(node.ch, code.isEmpty() ? "0" : code); return;
        }
        buildCodes(node.left, code + "0", codes);
        buildCodes(node.right, code + "1", codes);
    }
    static class HuffNode {
        char ch; int freq; HuffNode left, right;
        HuffNode(char ch, int freq) { this.ch = ch; this.freq = freq; }
    }
    static int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0, curr = 0, start = 0;
        for (int i = 0; i < gas.length; i++) {
            int net = gas[i] - cost[i];
            total += net; curr += net;
            if (curr < 0) { start = i + 1; curr = 0; }
        }
        return total >= 0 ? start : -1;
    }
    static boolean canJump(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) return false;
            maxReach = Math.max(maxReach, i + nums[i]);
        }
        return true;
    }
    static int jumpMinimum(int[] nums) {
        if (nums.length <= 1) return 0;
        int jumps = 0, currEnd = 0, farthest = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);
            if (i == currEnd) { jumps++; currEnd = farthest;
                if (currEnd >= nums.length - 1) break; }
        }
        return jumps;
    }
    static int minMeetingRooms(int[][] intervals) {
        int n = intervals.length;
        int[] starts = new int[n], ends = new int[n];
        for (int i = 0; i < n; i++) { starts[i] = intervals[i][0]; ends[i] = intervals[i][1]; }
        Arrays.sort(starts); Arrays.sort(ends);
        int rooms = 0, max = 0, ep = 0;
        for (int i = 0; i < n; i++) {
            if (starts[i] < ends[ep]) rooms++;
            else { ep++; }
            max = Math.max(max, rooms);
        }
        return max;
    }
    static int findContentChildren(int[] greed, int[] cookies) {
        Arrays.sort(greed); Arrays.sort(cookies);
        int child = 0, cookie = 0;
        while (child < greed.length && cookie < cookies.length) {
            if (cookies[cookie] >= greed[child]) child++;
            cookie++;
        }
        return child;
    }
    static int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char t : tasks) freq[t - 'A']++;
        Arrays.sort(freq);
        int maxF = freq[25], cnt = 0;
        for (int f : freq) if (f == maxF) cnt++;
        return Math.max(tasks.length, (maxF - 1) * (n + 1) + cnt);
    }
    static int kruskalMST(int[][] edges, int n) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (a, b) -> a[0] - b[0]);
        int[] parent = new int[n]; int[] rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        int totalW = 0, added = 0;
        for (int[] e : sorted) {
            int pu = find(parent, e[1]), pv = find(parent, e[2]);
            if (pu != pv) {
                union(parent, rank, pu, pv);
                totalW += e[0]; added++;
                System.out.printf("    Add edge %d-%d (weight=%d)%n", e[1], e[2], e[0]);
                if (added == n - 1) break;
            }
        }
        return totalW;
    }
    static int find(int[] p, int x) { return p[x] != x ? p[x] = find(p, p[x]) : x; }
    static void union(int[] p, int[] r, int a, int b) {
        if (r[a] < r[b]) { int t = a; a = b; b = t; }
        p[b] = a; if (r[a] == r[b]) r[a]++;
    }
    static int[] dijkstra(int[][] graph, int src) {
        int n = graph.length;
        int[] dist = new int[n]; Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{src, 0});
        while (!pq.isEmpty()) {
            int[] curr = pq.poll(); int u = curr[0], d = curr[1];
            if (d > dist[u]) continue;
            for (int v = 0; v < n; v++) {
                if (graph[u][v] > 0 && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                    pq.offer(new int[]{v, dist[v]});
                }
            }
        }
        return dist;
    }
    static String largestNumber(int[] nums) {
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) strs[i] = String.valueOf(nums[i]);
        Arrays.sort(strs, (a, b) -> (b + a).compareTo(a + b));
        if (strs[0].equals("0")) return "0";
        return String.join("", strs);
    }
    static List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;
        List<Integer> res = new ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']);
            if (i == end) { res.add(end - start + 1); start = i + 1; }
        }
        return res;
    }

    // =========================================================
    // SECTION 5 — REAL-WORLD APPLICATIONS
    // =========================================================
    static void section5_RealWorldApplications() {
        printSection("5. REAL-WORLD APPLICATIONS");

        // 5a. Stock profit (unlimited transactions)
        System.out.println("--- 5a. Stock Trading: Max Profit (Unlimited Transactions) ---");
        int[][] priceSets = {{7,1,5,3,6,4},{1,2,3,4,5},{7,6,4,3,1},{1,7,2,8,3,9}};
        for (int[] prices : priceSets) {
            System.out.printf("  prices=%s → max profit=%d%n",
                    Arrays.toString(prices), maxProfitUnlimited(prices));
        }

        // 5b. Huffman for text compression
        System.out.println("\n--- 5b. Text Compression: Huffman vs Fixed Encoding ---");
        String text = "this is an example of a huffman tree";
        Map<Character, Integer> freqMap = new LinkedHashMap<>();
        for (char c : text.toCharArray()) freqMap.merge(c, 1, Integer::sum);
        Map<Character, String> codes = huffmanEncoding(freqMap);
        int huffBits = 0, fixedBits = 0;
        for (Map.Entry<Character, Integer> e : freqMap.entrySet()) {
            huffBits  += e.getValue() * codes.get(e.getKey()).length();
            fixedBits += e.getValue() * 8; // ASCII
        }
        System.out.printf("  Text: \"%s\"%n", text);
        System.out.printf("  ASCII encoding: %d bits%n", fixedBits);
        System.out.printf("  Huffman encoding: %d bits%n", huffBits);
        System.out.printf("  Compression ratio: %.1f%%%n",
                100.0 * (fixedBits - huffBits) / fixedBits);

        // 5c. File merge (optimal merge pattern)
        System.out.println("\n--- 5c. Optimal File Merge (like Huffman) ---");
        int[] fileSizes = {20, 30, 10, 5, 15};
        System.out.println("  File sizes: " + Arrays.toString(fileSizes));
        System.out.println("  Optimal merge cost: " + optimalMergeCost(fileSizes));
        System.out.println("  (Always merge two smallest files first)");

        // 5d. Network design — MST cost
        System.out.println("\n--- 5d. Network Design (MST): Minimum Cable Cost ---");
        // Cities as nodes, cable cost as edge weights
        int[][] cables = {{1,0,1},{3,0,2},{4,1,2},{2,1,3},{5,2,3},{6,2,4},{3,3,4}};
        System.out.println("  Cable options (cost, city1, city2):");
        for (int[] c : cables)
            System.out.printf("    city%d ↔ city%d: $%d%n", c[1], c[2], c[0]);
        System.out.println("  MST (minimum to connect all cities):");
        int minCost = kruskalMST(cables, 5);
        System.out.println("  Minimum cable cost: $" + minCost);

        // 5e. CPU scheduling (SJF)
        System.out.println("\n--- 5e. CPU Scheduling: Shortest Job First ---");
        int[][] cpuJobs = {{6,1},{8,2},{7,3},{3,4},{4,5}};
        System.out.println("  Jobs (burst, id):");
        Arrays.sort(cpuJobs, (a, b) -> a[0] - b[0]);
        int time = 0, waitSum = 0;
        for (int[] job : cpuJobs) {
            System.out.printf("    Job %d (burst=%d): starts at t=%d, waits %d%n",
                    job[1], job[0], time, time);
            waitSum += time; time += job[0];
        }
        System.out.printf("  Average waiting time: %.2f%n", (double) waitSum / cpuJobs.length);

        // 5f. Bandwidth allocation (meeting rooms variant)
        System.out.println("\n--- 5f. Bandwidth Allocation (Interval Scheduling) ---");
        int[][] sessions = {{0,4},{1,3},{2,5},{3,6},{5,7},{4,8}};
        System.out.println("  Streaming sessions (start,end): " + Arrays.deepToString(sessions));
        System.out.println("  Max concurrent sessions (bandwidth needed): " + minMeetingRooms(sessions));
        List<int[]> scheduled = activitySelection(sessions);
        System.out.println("  Max sessions on single channel: " + scheduled.size());
    }

    static int maxProfitUnlimited(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++)
            if (prices[i] > prices[i-1]) profit += prices[i] - prices[i-1];
        return profit;
    }
    static int optimalMergeCost(int[] files) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int f : files) pq.offer(f);
        int cost = 0;
        while (pq.size() > 1) {
            int a = pq.poll(), b = pq.poll();
            cost += a + b; pq.offer(a + b);
        }
        return cost;
    }

    // =========================================================
    // SECTION 6 — COMPLEXITY ANALYSIS
    // =========================================================
    static void section6_ComplexityAnalysis() {
        printSection("6. COMPLEXITY ANALYSIS");

        // 6a. Complexity table
        System.out.println("--- 6a. Algorithm Complexity Summary ---");
        System.out.printf("  %-30s %-14s %-10s %-20s%n",
                "Algorithm","Time","Space","Key Operation");
        String[][] rows = {
            {"Activity Selection","O(n log n)","O(n)","Sort by end time"},
            {"Fractional Knapsack","O(n log n)","O(1)","Sort by ratio"},
            {"Huffman Encoding","O(n log n)","O(n)","PQ merge smallest"},
            {"Dijkstra (heap)","O((V+E)logV)","O(V)","PQ extract-min"},
            {"Kruskal MST","O(E log E)","O(V)","Sort + Union-Find"},
            {"Job Scheduling EDF","O(n log n)","O(1)","Sort by deadline"},
            {"Gas Station","O(n)","O(1)","Single scan"},
            {"Jump Game","O(n)","O(1)","Running max"},
            {"Stock Profit","O(n)","O(1)","Sum positive diffs"},
            {"Largest Number","O(n log n)","O(n)","Custom comparator"},
            {"Task Scheduler","O(n log n)","O(1)","Frequency + formula"},
            {"Partition Labels","O(n)","O(1)","Last occurrence scan"},
        };
        for (String[] row : rows)
            System.out.printf("  %-30s %-14s %-10s %-20s%n",
                    row[0], row[1], row[2], row[3]);

        // 6b. Live timing: greedy vs brute force
        System.out.println("\n--- 6b. Live Timing: Greedy vs Brute Force ---");
        int[] actCounts = {10, 100, 1000, 10000};
        for (int n : actCounts) {
            int[][] acts = generateActivities(n);
            long t0 = System.nanoTime();
            List<int[]> greedyResult = activitySelection(acts);
            long greedy = System.nanoTime() - t0;

            t0 = System.nanoTime();
            int bruteResult = activitySelectionBrute(acts);
            long brute = System.nanoTime() - t0;

            System.out.printf("  n=%-5d Greedy=%,dns(%d sel) BruteForce=%,dns(%d sel) Speedup=%.0fx%n",
                    n, greedy, greedyResult.size(), brute, bruteResult,
                    Math.max(1.0, (double) brute / greedy));
            if (n > 20) break; // Brute force too slow for large n
        }

        // 6c. Sorting dominance pattern
        System.out.println("\n--- 6c. Sorting Dominates Greedy Complexity ---");
        System.out.println("  Most greedy algorithms = O(n log n) sort + O(n) scan");
        System.out.println("  O(n log n) breakdown:");
        for (int n : new int[]{100, 1000, 10000, 100000}) {
            long nlogn = (long)(n * Math.log(n) / Math.log(2));
            long nsq = (long)n * n;
            System.out.printf("    n=%-7d O(n log n)=%-10d O(n²)=%-15d ratio=%.0fx%n",
                    n, nlogn, nsq, (double) nsq / nlogn);
        }
    }

    static int[][] generateActivities(int n) {
        Random rng = new Random(42);
        int[][] acts = new int[n][2];
        for (int i = 0; i < n; i++) {
            int s = rng.nextInt(100);
            acts[i] = new int[]{s, s + 1 + rng.nextInt(20)};
        }
        return acts;
    }
    static int activitySelectionBrute(int[][] acts) {
        if (acts.length > 20) return -1; // Too slow
        int max = 0;
        for (int mask = 0; mask < (1 << acts.length); mask++) {
            List<int[]> chosen = new ArrayList<>();
            for (int i = 0; i < acts.length; i++)
                if ((mask & (1 << i)) != 0) chosen.add(acts[i]);
            if (isNonOverlapping(chosen)) max = Math.max(max, chosen.size());
        }
        return max;
    }
    static boolean isNonOverlapping(List<int[]> acts) {
        acts.sort((a, b) -> a[0] - b[0]);
        for (int i = 1; i < acts.size(); i++)
            if (acts.get(i)[0] < acts.get(i-1)[1]) return false;
        return true;
    }

    // =========================================================
    // SECTION 7 — INTERVIEW STRATEGIES
    // =========================================================
    static void section7_InterviewStrategies() {
        printSection("7. INTERVIEW STRATEGIES");

        // P1. Non-overlapping intervals (LC 435)
        System.out.println("--- P1. Non-Overlapping Intervals — Min Removals (LC 435) ---");
        int[][][] intervalTests = {
            {{1,2},{2,3},{3,4},{1,3}},
            {{1,2},{1,2},{1,2}},
            {{1,2},{2,3}},
            {{1,3},{2,4},{3,6},{4,5}}
        };
        for (int[][] t : intervalTests)
            System.out.printf("  %-35s → remove %d%n",
                    Arrays.deepToString(t), eraseOverlapIntervals(t));

        // P2. Minimum arrows to burst balloons (LC 452)
        System.out.println("\n--- P2. Min Arrows to Burst Balloons (LC 452) ---");
        int[][][] balloons = {
            {{10,16},{2,8},{1,6},{7,12}},
            {{1,2},{3,4},{5,6},{7,8}},
            {{1,2},{2,3},{3,4},{4,5}},
            {{-2147483646,-2147483645},{2147483646,2147483647}}
        };
        for (int[][] b : balloons)
            System.out.printf("  %-45s → %d arrow(s)%n",
                    Arrays.deepToString(b), findMinArrowShots(b));

        // P3. Lemonade change (LC 860)
        System.out.println("\n--- P3. Lemonade Change (LC 860) ---");
        int[][] billSets = {{5,5,5,10,20},{5,5,10,10,20},{5,5,5,5,20,5,5,15,5}};
        for (int[] bills : billSets)
            System.out.printf("  %-30s → %s%n",
                    Arrays.toString(bills), lemonadeChange(bills));

        // P4. Two-city scheduling (LC 1029)
        System.out.println("\n--- P4. Two City Scheduling (LC 1029) ---");
        int[][][] costSets = {
            {{10,20},{30,200},{400,50},{30,20}},
            {{259,770},{448,54},{926,667},{184,139},{840,118},{577,469}}
        };
        for (int[][] costs : costSets)
            System.out.printf("  costs=%s → min total=%d%n",
                    Arrays.deepToString(costs), twoCitySchedCost(costs));

        // P5. Reorganize string (LC 767)
        System.out.println("\n--- P5. Reorganize String — No Adjacent Same (LC 767) ---");
        String[] strTests = {"aab","aaab","aabb","vvvlo","aaabbc"};
        for (String s : strTests)
            System.out.printf("  \"%s\" → \"%s\"%n", s, reorganizeString(s));

        // P6. Meeting rooms I (LC 252)
        System.out.println("\n--- P6. Meeting Rooms I — Can Attend All? (LC 252) ---");
        int[][][] meetTests = {
            {{0,30},{5,10},{15,20}},
            {{7,10},{2,4}},
            {{0,8},{8,10}}
        };
        for (int[][] m : meetTests)
            System.out.printf("  %-30s → can attend all: %s%n",
                    Arrays.deepToString(m), canAttendAll(m));

        // P7. Hand of Straights (LC 846)
        System.out.println("\n--- P7. Hand of Straights (LC 846) ---");
        int[][] hands = {{1,2,3,6,2,3,4,7,8},{1,2,3,4,5},{1,1,2,2,3,3}};
        int[] ws = {3, 3, 2};
        for (int i = 0; i < hands.length; i++)
            System.out.printf("  hand=%s groupSize=%d → %s%n",
                    Arrays.toString(hands[i]), ws[i],
                    isNStraightHand(hands[i], ws[i]));

        // P8. Best Time to Buy Stock with Cooldown (LC 309) — greedy insight
        System.out.println("\n--- P8. Stock with Cooldown — Max Profit (LC 309) ---");
        int[][] stockTests = {{1,2,3,0,2},{1},{1,2,3,4,5}};
        for (int[] prices : stockTests)
            System.out.printf("  prices=%s → %d%n",
                    Arrays.toString(prices), maxProfitCooldown(prices));

        // P9. Minimum cost to connect sticks (LC 1167)
        System.out.println("\n--- P9. Minimum Cost to Connect Sticks (LC 1167) ---");
        int[][] sticks = {{2,4,3},{1,8,3},{5},{1,2,3,4,5}};
        for (int[] s : sticks)
            System.out.printf("  sticks=%s → cost=%d%n",
                    Arrays.toString(s), connectSticks(s));

        // P10. Greedy interview template reminder
        System.out.println("\n--- P10. Greedy Interview Framework ---");
        System.out.println("  1. IDENTIFY: What is the locally optimal choice?");
        System.out.println("  2. JUSTIFY:  Exchange argument — can we swap without loss?");
        System.out.println("  3. SUBPROBLEM: What remains after the greedy choice?");
        System.out.println("  4. IMPLEMENT: Sort → Scan → Commit");
        System.out.println("  5. VERIFY: Test with small counterexample first");
        System.out.println("  6. COMPLEXITY: Usually O(n log n) from sorting step");
    }

    // --- Interview Problem Implementations ---
    static int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;
        Arrays.sort(intervals, (a, b) -> a[1] - b[1]);
        int count = 0, lastEnd = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < lastEnd) count++;
            else lastEnd = intervals[i][1];
        }
        return count;
    }
    static int findMinArrowShots(int[][] points) {
        if (points.length == 0) return 0;
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
        int arrows = 1; long pos = points[0][1];
        for (int i = 1; i < points.length; i++)
            if (points[i][0] > pos) { arrows++; pos = points[i][1]; }
        return arrows;
    }
    static boolean lemonadeChange(int[] bills) {
        int fives = 0, tens = 0;
        for (int bill : bills) {
            if (bill == 5) { fives++; }
            else if (bill == 10) { if (fives == 0) return false; fives--; tens++; }
            else {
                if (tens > 0 && fives > 0) { tens--; fives--; }
                else if (fives >= 3) { fives -= 3; }
                else return false;
            }
        }
        return true;
    }
    static int twoCitySchedCost(int[][] costs) {
        Arrays.sort(costs, (a, b) -> (a[0]-a[1]) - (b[0]-b[1]));
        int total = 0, n = costs.length / 2;
        for (int i = 0; i < n; i++) total += costs[i][0];
        for (int i = n; i < costs.length; i++) total += costs[i][1];
        return total;
    }
    static String reorganizeString(String s) {
        int[] freq = new int[26];
        for (char c : s.toCharArray()) freq[c - 'a']++;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[1] - a[1]);
        for (int i = 0; i < 26; i++) if (freq[i] > 0) pq.offer(new int[]{i, freq[i]});
        StringBuilder sb = new StringBuilder();
        while (pq.size() > 1) {
            int[] a = pq.poll(), b = pq.poll();
            sb.append((char)('a'+a[0])); sb.append((char)('a'+b[0]));
            if (--a[1] > 0) pq.offer(a);
            if (--b[1] > 0) pq.offer(b);
        }
        if (!pq.isEmpty()) {
            int[] last = pq.poll();
            if (last[1] > 1) return "";
            sb.append((char)('a' + last[0]));
        }
        return sb.toString();
    }
    static boolean canAttendAll(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        for (int i = 1; i < intervals.length; i++)
            if (intervals[i][0] < intervals[i-1][1]) return false;
        return true;
    }
    static boolean isNStraightHand(int[] hand, int groupSize) {
        if (hand.length % groupSize != 0) return false;
        TreeMap<Integer, Integer> count = new TreeMap<>();
        for (int card : hand) count.merge(card, 1, Integer::sum);
        while (!count.isEmpty()) {
            int first = count.firstKey();
            for (int i = 0; i < groupSize; i++) {
                if (!count.containsKey(first + i)) return false;
                count.merge(first + i, -1, Integer::sum);
                if (count.get(first + i) == 0) count.remove(first + i);
            }
        }
        return true;
    }
    static int maxProfitCooldown(int[] prices) {
        if (prices.length < 2) return 0;
        int hold = Integer.MIN_VALUE, sold = 0, rest = 0;
        for (int price : prices) {
            int prevSold = sold;
            sold = hold + price;
            hold = Math.max(hold, rest - price);
            rest = Math.max(rest, prevSold);
        }
        return Math.max(sold, rest);
    }
    static int connectSticks(int[] sticks) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int s : sticks) pq.offer(s);
        int cost = 0;
        while (pq.size() > 1) {
            int a = pq.poll(), b = pq.poll();
            cost += a + b; pq.offer(a + b);
        }
        return cost;
    }

    // =========================================================
    // UTILITIES
    // =========================================================
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
