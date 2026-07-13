import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 * DYNAMIC PROGRAMMING — Complete Executable Reference
 * ============================================================
 * Topics:
 *  1. Why Dynamic Programming?     (exponential vs polynomial demo,
 *                                   overlapping subproblems proof,
 *                                   call count comparison)
 *  2. Recursion vs Memoization     (fib all 4 approaches, rod cutting,
 *                                   memoization template, cache hits demo)
 *  3. Tabulation (Bottom-Up)       (coin change trace, knapsack trace,
 *                                   space optimization, fill order demo)
 *  4. Classic DP Problems          (LCS, 0/1 Knapsack, LIS, Edit Distance,
 *                                   Matrix Chain, Subset Sum, Burst Balloons)
 *  5. 1D & 2D DP Patterns          (house robber I/II, max subarray, word break,
 *                                   climbing stairs, decode ways, unique paths,
 *                                   regex matching, palindrome problems)
 *  6. Optimization Strategies      (rolling arrays, bitmask DP/TSP, digit DP,
 *                                   space-optimized LCS/edit, optimal BST)
 *  7. Interview Problem Solving    (stock problems I-IV, palindrome family,
 *                                   interval DP, partition problems,
 *                                   target sum, perfect squares)
 *
 * Compile : javac DynamicProgramming.java
 * Run     : java DynamicProgramming
 * ============================================================
 */
public class DynamicProgramming {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("DYNAMIC PROGRAMMING — COMPLETE DEMO");

        section1_WhyDP();
        section2_RecursionVsMemoization();
        section3_Tabulation();
        section4_ClassicDPProblems();
        section5_OneDAndTwoDPatterns();
        section6_OptimizationStrategies();
        section7_InterviewProblems();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — WHY DYNAMIC PROGRAMMING?
    // =========================================================
    static void section1_WhyDP() {
        printSection("1. WHY DYNAMIC PROGRAMMING?");

        // 1a. Exponential vs polynomial call count
        System.out.println("--- 1a. Call Count: Naive vs Memoized Fibonacci ---");
        System.out.printf("  %-6s %-15s %-15s %-12s%n",
                "n", "Naive calls", "Memo calls", "Speedup");
        for (int n : new int[]{5, 10, 15, 20, 25, 30, 35}) {
            naiveCalls = 0; memoCalls = 0;
            fibNaiveCount(n);
            fibMemoCount(n, new long[n + 2]);
            System.out.printf("  %-6d %-15d %-15d %-12.0fx%n",
                    n, naiveCalls, memoCalls,
                    (double) naiveCalls / Math.max(memoCalls, 1));
        }

        // 1b. Time demo
        System.out.println("\n--- 1b. Time Demo: Naive vs DP for fib(40) ---");
        long t0 = System.nanoTime();
        long r1 = fibNaive(40);
        long naive = System.nanoTime() - t0;
        t0 = System.nanoTime();
        long r2 = fibDP(40);
        long dp = System.nanoTime() - t0;
        System.out.printf("  fib(40) naive = %d | time = %,d ms%n", r1, naive/1_000_000);
        System.out.printf("  fib(40) DP    = %d | time = %,d ns%n", r2, dp);
        System.out.printf("  Speedup: ~%.0fx%n", (double) naive / Math.max(dp, 1));

        // 1c. Where DP is used
        System.out.println("\n--- 1c. Real-World DP Applications ---");
        String[] apps = {
            "Bioinformatics  : DNA alignment via Edit Distance — O(n²) vs O(2^n)",
            "Speech Recognition: Viterbi algorithm (HMM + DP)",
            "Compilers       : Optimal code generation, register allocation",
            "GPS/Routing     : Shortest path (Dijkstra, Bellman-Ford)",
            "Finance         : Portfolio optimization, option pricing",
            "NLP             : CYK parsing, machine translation alignment",
            "Games           : Optimal strategy (chess endgame tablebases)",
        };
        for (String a : apps) System.out.println("  • " + a);

        // 1d. Properties check
        System.out.println("\n--- 1d. DP Properties Verification ---");
        System.out.println("  Fibonacci:");
        System.out.println("    Optimal substructure? YES — fib(n)=fib(n-1)+fib(n-2)");
        System.out.println("    Overlapping subproblems? YES — fib(3) computed multiple times");
        System.out.println("  Shortest Path:");
        System.out.println("    Optimal substructure? YES — sub-paths are optimal");
        System.out.println("    Overlapping? YES — nodes visited via multiple paths");
        System.out.println("  Longest Simple Path (general graphs):");
        System.out.println("    Optimal substructure? NO — paths can share vertices");
        System.out.println("    → DP does NOT apply (NP-hard problem)");
    }

    // --- Section 1 helpers ---
    static int naiveCalls = 0, memoCalls = 0;
    static long fibNaiveCount(int n) {
        naiveCalls++;
        if (n <= 1) return n;
        return fibNaiveCount(n-1) + fibNaiveCount(n-2);
    }
    static long fibMemoCount(int n, long[] memo) {
        memoCalls++;
        if (n <= 1) return n;
        if (memo[n] != 0) return memo[n];
        return memo[n] = fibMemoCount(n-1, memo) + fibMemoCount(n-2, memo);
    }
    static long fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n-1) + fibNaive(n-2);
    }
    static long fibDP(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) { long c = a+b; a = b; b = c; }
        return b;
    }

    // =========================================================
    // SECTION 2 — RECURSION VS MEMOIZATION
    // =========================================================
    static void section2_RecursionVsMemoization() {
        printSection("2. RECURSION VS MEMOIZATION");

        // 2a. Four fib approaches
        System.out.println("--- 2a. Fibonacci — Four Approaches ---");
        for (int n : new int[]{0,1,5,10,20,30,45}) {
            long[] memo = new long[n+2];
            long m1 = n <= 35 ? fibNaive(n) : -1L; // skip large naive
            long m2 = fibMemo(n, memo);
            long m3 = fibTab(n);
            long m4 = fibDP(n);
            System.out.printf("  fib(%2d): naive=%s memo=%d tab=%d space-opt=%d %s%n",
                    n, m1==-1?"(skip)":Long.toString(m1), m2, m3, m4,
                    (m2==m3&&m3==m4?"✓":"✗ mismatch"));
        }

        // 2b. Rod cutting with memoization
        System.out.println("\n--- 2b. Rod Cutting (Memoization) ---");
        int[] prices = {1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println("  Prices (length 1..8): " + Arrays.toString(prices));
        for (int n = 1; n <= 8; n++) {
            int rev = rodCutting(prices, n, new HashMap<>());
            System.out.printf("  Rod length %d → max revenue = %d%n", n, rev);
        }

        // 2c. Cache hit demo — show memo's power
        System.out.println("\n--- 2c. Cache Hit Analysis ---");
        long[] memoArr = new long[50];
        cacheHits = 0; cacheMisses = 0;
        fibMemoTracked(45, memoArr);
        System.out.println("  fibMemo(45): cache hits=" + cacheHits
                + " misses=" + cacheMisses + " (total unique=" + cacheMisses + ")");
        System.out.println("  naive fib(45) would make ~2^45 ≈ "
                + String.format("%,d", 1L<<45) + " calls");

        // 2d. Memoization with string keys
        System.out.println("\n--- 2d. Memoization with String Keys (Word Break) ---");
        List<String> dict = Arrays.asList("leet","code","apple","pen","applepen","pine","pineapple");
        String[] words = {"leetcode","applepenapple","pineapplepenapple","catsandog"};
        for (String w : words) {
            Map<String,Boolean> memo = new HashMap<>();
            System.out.printf("  %-22s → %s%n", "\""+w+"\"",
                    wordBreakMemo(w, new HashSet<>(dict), memo));
        }

        // 2e. Unbounded knapsack (memoization)
        System.out.println("\n--- 2e. Unbounded Knapsack (items reusable) ---");
        int[] wts = {1,3,4,5}; int[] vals = {10,40,50,70}; int cap = 8;
        System.out.println("  Weights=" + Arrays.toString(wts)
                + " Values=" + Arrays.toString(vals) + " Capacity=" + cap);
        System.out.println("  Max value = " + unboundedKnapsack(wts, vals, cap,
                new Integer[cap+1]));
    }

    // --- Section 2 helpers ---
    static long fibMemo(int n, long[] memo) {
        if (n <= 1) return n;
        if (memo[n] != 0) return memo[n];
        return memo[n] = fibMemo(n-1, memo) + fibMemo(n-2, memo);
    }
    static long fibTab(int n) {
        if (n <= 1) return n;
        long[] dp = new long[n+1]; dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i-1]+dp[i-2];
        return dp[n];
    }
    static int rodCutting(int[] prices, int n, Map<Integer,Integer> memo) {
        if (n == 0) return 0;
        if (memo.containsKey(n)) return memo.get(n);
        int max = Integer.MIN_VALUE;
        for (int i = 1; i <= n; i++)
            max = Math.max(max, prices[i-1] + rodCutting(prices, n-i, memo));
        return memo.put(n, max), max;
    }
    static int cacheHits = 0, cacheMisses = 0;
    static long fibMemoTracked(int n, long[] memo) {
        if (n <= 1) return n;
        if (memo[n] != 0) { cacheHits++; return memo[n]; }
        cacheMisses++;
        return memo[n] = fibMemoTracked(n-1,memo) + fibMemoTracked(n-2,memo);
    }
    static boolean wordBreakMemo(String s, Set<String> dict, Map<String,Boolean> memo) {
        if (s.isEmpty()) return true;
        if (memo.containsKey(s)) return memo.get(s);
        boolean result = false;
        for (int i = 1; i <= s.length(); i++)
            if (dict.contains(s.substring(0,i)) && wordBreakMemo(s.substring(i),dict,memo))
                { result = true; break; }
        return memo.put(s, result), result;
    }
    static int unboundedKnapsack(int[] w, int[] v, int cap, Integer[] memo) {
        if (cap == 0) return 0;
        if (memo[cap] != null) return memo[cap];
        int max = 0;
        for (int i = 0; i < w.length; i++)
            if (w[i] <= cap)
                max = Math.max(max, v[i] + unboundedKnapsack(w, v, cap-w[i], memo));
        return memo[cap] = max;
    }

    // =========================================================
    // SECTION 3 — TABULATION (BOTTOM-UP)
    // =========================================================
    static void section3_Tabulation() {
        printSection("3. TABULATION (BOTTOM-UP)");

        // 3a. Coin change with full trace
        System.out.println("--- 3a. Coin Change — Full DP Table Trace ---");
        int[] coins = {1, 5, 6, 9};
        int amount = 11;
        System.out.println("  Coins: " + Arrays.toString(coins) + " Amount: " + amount);
        int[] dp = coinChangeTrace(coins, amount);
        System.out.println("  Min coins = " + dp[amount]);
        System.out.println("  Coins used: " + coinChangeCoins(coins, amount));

        // 3b. Multiple coin change examples
        System.out.println("\n--- 3b. Coin Change Variations ---");
        int[][] coinSets = {{1,5,10,25},{1,2,5},{2},{1,5,6,9}};
        int[] amounts   = {41, 11, 3, 11};
        for (int i = 0; i < coinSets.length; i++) {
            int res = coinChange(coinSets[i], amounts[i]);
            System.out.printf("  coins=%-15s amount=%-3d → %s%n",
                    Arrays.toString(coinSets[i]), amounts[i],
                    res == -1 ? "impossible" : res + " coins");
        }

        // 3c. Coin change ways (counting)
        System.out.println("\n--- 3c. Count Ways to Make Change ---");
        int[] c = {1, 2, 5};
        System.out.println("  Coins: " + Arrays.toString(c));
        for (int amt : new int[]{5, 10, 15}) {
            System.out.printf("  Ways to make %2d = %d%n", amt, coinWays(c, amt));
        }

        // 3d. Space optimization demo
        System.out.println("\n--- 3d. Space Optimization: 2D → 1D ---");
        int[] weights = {2,3,4,5}; int[] values = {3,4,5,6}; int cap = 8;
        System.out.println("  Knapsack weights=" + Arrays.toString(weights)
                + " values=" + Arrays.toString(values) + " cap=" + cap);
        System.out.println("  2D DP O(n×W): " + knapsack2D(weights, values, cap));
        System.out.println("  1D DP O(W):   " + knapsack1D(weights, values, cap));

        // 3e. Tabulation fill order visualization
        System.out.println("\n--- 3e. Fill Order: Fibonacci Tabulation ---");
        int fibN = 8;
        long[] fibArr = new long[fibN+1];
        fibArr[0] = 0; fibArr[1] = 1;
        System.out.print("  Fill order: dp[0]=0 dp[1]=1");
        for (int i = 2; i <= fibN; i++) {
            fibArr[i] = fibArr[i-1]+fibArr[i-2];
            System.out.print(" dp["+i+"]="+fibArr[i]);
        }
        System.out.println("\n  Answer: dp[" + fibN + "] = " + fibArr[fibN]);
    }

    // --- Section 3 helpers ---
    static int[] coinChangeTrace(int[] coins, int amount) {
        int[] dp = new int[amount+1];
        Arrays.fill(dp, amount+1); dp[0] = 0;
        for (int i = 1; i <= amount; i++)
            for (int c : coins)
                if (c <= i) dp[i] = Math.min(dp[i], dp[i-c]+1);
        System.out.print("  dp table: ");
        for (int i = 0; i <= amount; i++) System.out.print(dp[i]>(amount)?  "∞ ":""+dp[i]+" ");
        System.out.println();
        return dp;
    }
    static List<Integer> coinChangeCoins(int[] coins, int amount) {
        int[] dp = new int[amount+1]; Arrays.fill(dp, amount+1); dp[0] = 0;
        int[] from = new int[amount+1]; Arrays.fill(from, -1);
        for (int i = 1; i <= amount; i++)
            for (int c : coins)
                if (c <= i && dp[i-c]+1 < dp[i]) { dp[i] = dp[i-c]+1; from[i] = c; }
        List<Integer> used = new ArrayList<>();
        if (dp[amount] > amount) return used;
        for (int cur = amount; cur > 0; cur -= from[cur]) used.add(from[cur]);
        return used;
    }
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount+1]; Arrays.fill(dp, amount+1); dp[0] = 0;
        for (int i = 1; i <= amount; i++)
            for (int c : coins) if (c <= i) dp[i] = Math.min(dp[i], dp[i-c]+1);
        return dp[amount] > amount ? -1 : dp[amount];
    }
    static long coinWays(int[] coins, int amount) {
        long[] dp = new long[amount+1]; dp[0] = 1;
        for (int c : coins) for (int j = c; j <= amount; j++) dp[j] += dp[j-c];
        return dp[amount];
    }
    static int knapsack2D(int[] w, int[] v, int W) {
        int n = w.length; int[][] dp = new int[n+1][W+1];
        for (int i = 1; i <= n; i++)
            for (int wt = 0; wt <= W; wt++) {
                dp[i][wt] = dp[i-1][wt];
                if (w[i-1] <= wt) dp[i][wt] = Math.max(dp[i][wt], dp[i-1][wt-w[i-1]]+v[i-1]);
            }
        return dp[n][W];
    }
    static int knapsack1D(int[] w, int[] v, int W) {
        int[] dp = new int[W+1];
        for (int i = 0; i < w.length; i++)
            for (int wt = W; wt >= w[i]; wt--)
                dp[wt] = Math.max(dp[wt], dp[wt-w[i]]+v[i]);
        return dp[W];
    }

    // =========================================================
    // SECTION 4 — CLASSIC DP PROBLEMS
    // =========================================================
    static void section4_ClassicDPProblems() {
        printSection("4. CLASSIC DP PROBLEMS");

        // 4a. LCS
        System.out.println("--- 4a. Longest Common Subsequence ---");
        String[][] pairs = {{"ABCBDAB","BDCABA"},{"AGGTAB","GXTXAYB"},{"ABC","AC"},{"","ABC"}};
        for (String[] p : pairs) {
            System.out.printf("  LCS(\"%s\",\"%s\") = %d \"%s\"%n",
                    p[0], p[1], lcs(p[0],p[1]), lcsString(p[0],p[1]));
        }

        // 4b. 0/1 Knapsack
        System.out.println("\n--- 4b. 0/1 Knapsack ---");
        int[][] wArr = {{2,3,4,5},{1,3,4,5},{2,2,2,2}};
        int[][] vArr = {{3,4,5,6},{1,4,5,7},{1,2,3,4}};
        int[] caps   = {8, 7, 5};
        for (int i = 0; i < wArr.length; i++) {
            int res = knapsack2D(wArr[i], vArr[i], caps[i]);
            System.out.printf("  w=%s v=%s cap=%d → maxValue=%d%n",
                    Arrays.toString(wArr[i]), Arrays.toString(vArr[i]), caps[i], res);
        }

        // 4c. LIS
        System.out.println("\n--- 4c. Longest Increasing Subsequence ---");
        int[][] lisArrs = {{10,9,2,5,3,7,101,18},{0,1,0,3,2,3},{7,7,7,7},{1,3,6,7,9,4,10,5,6}};
        for (int[] arr : lisArrs) {
            int dp = lisDP(arr);
            int bs = lisBinarySearch(arr);
            System.out.printf("  %-30s → LIS=%d (dp=%d bs=%d) %s%n",
                    Arrays.toString(arr), dp, dp, bs, dp==bs?"✓":"✗");
        }

        // 4d. Edit Distance
        System.out.println("\n--- 4d. Edit Distance ---");
        String[][] edPairs = {{"horse","ros"},{"intention","execution"},{"","abc"},{"abc","abc"},{"sunday","saturday"}};
        for (String[] p : edPairs) {
            System.out.printf("  edit(\"%s\" → \"%s\") = %d%n",
                    p[0], p[1], editDistance(p[0], p[1]));
        }

        // 4e. Matrix chain multiplication
        System.out.println("\n--- 4e. Matrix Chain Multiplication ---");
        int[][] dimSets = {{10,30,5,60},{40,20,30,10,30},{1,2,3,4,3}};
        for (int[] dims : dimSets) {
            System.out.printf("  dims=%s → min multiplications=%d%n",
                    Arrays.toString(dims), matrixChain(dims));
        }

        // 4f. Subset sum
        System.out.println("\n--- 4f. Subset Sum ---");
        int[][] numSets = {{3,34,4,12,5,2},{1,2,3,7},{2,3,7,8,10}};
        int[] targets   = {9, 6, 11};
        for (int i = 0; i < numSets.length; i++) {
            System.out.printf("  nums=%s target=%d → %s (count=%d)%n",
                    Arrays.toString(numSets[i]), targets[i],
                    subsetSum(numSets[i], targets[i]),
                    countSubsets(numSets[i], targets[i]));
        }

        // 4g. Burst Balloons
        System.out.println("\n--- 4g. Burst Balloons (Interval DP) ---");
        int[][] balloons = {{3,1,5,8},{1,5},{1},{3,1,5,8,2,6,4}};
        for (int[] b : balloons) {
            System.out.printf("  %s → max coins=%d%n",
                    Arrays.toString(b), burstBalloons(b));
        }
    }

    // --- Section 4 helpers ---
    static int lcs(String a, String b) {
        int m=a.length(),n=b.length(); int[][] dp=new int[m+1][n+1];
        for (int i=1;i<=m;i++) for (int j=1;j<=n;j++)
            dp[i][j]=a.charAt(i-1)==b.charAt(j-1)?dp[i-1][j-1]+1:Math.max(dp[i-1][j],dp[i][j-1]);
        return dp[m][n];
    }
    static String lcsString(String a, String b) {
        int m=a.length(),n=b.length(); int[][] dp=new int[m+1][n+1];
        for (int i=1;i<=m;i++) for (int j=1;j<=n;j++)
            dp[i][j]=a.charAt(i-1)==b.charAt(j-1)?dp[i-1][j-1]+1:Math.max(dp[i-1][j],dp[i][j-1]);
        StringBuilder sb=new StringBuilder(); int i=m,j=n;
        while (i>0&&j>0) {
            if (a.charAt(i-1)==b.charAt(j-1)){sb.append(a.charAt(i-1));i--;j--;}
            else if (dp[i-1][j]>dp[i][j-1]) i--; else j--;
        }
        return sb.reverse().toString();
    }
    static int lisDP(int[] nums) {
        int[] dp=new int[nums.length]; Arrays.fill(dp,1);
        for (int i=1;i<nums.length;i++)
            for (int j=0;j<i;j++)
                if (nums[j]<nums[i]) dp[i]=Math.max(dp[i],dp[j]+1);
        return Arrays.stream(dp).max().getAsInt();
    }
    static int lisBinarySearch(int[] nums) {
        List<Integer> tails=new ArrayList<>();
        for (int num:nums){
            int pos=Collections.binarySearch(tails,num);
            if (pos<0) pos=-(pos+1);
            if (pos==tails.size()) tails.add(num); else tails.set(pos,num);
        }
        return tails.size();
    }
    static int editDistance(String a, String b) {
        int m=a.length(),n=b.length(); int[][] dp=new int[m+1][n+1];
        for (int i=0;i<=m;i++) dp[i][0]=i;
        for (int j=0;j<=n;j++) dp[0][j]=j;
        for (int i=1;i<=m;i++) for (int j=1;j<=n;j++)
            dp[i][j]=a.charAt(i-1)==b.charAt(j-1)?dp[i-1][j-1]:
                1+Math.min(dp[i-1][j-1],Math.min(dp[i-1][j],dp[i][j-1]));
        return dp[m][n];
    }
    static int matrixChain(int[] dims) {
        int n=dims.length-1; int[][] dp=new int[n][n];
        for (int len=2;len<=n;len++)
            for (int i=0;i<=n-len;i++){
                int j=i+len-1; dp[i][j]=Integer.MAX_VALUE;
                for (int k=i;k<j;k++)
                    dp[i][j]=Math.min(dp[i][j],dp[i][k]+dp[k+1][j]+dims[i]*dims[k+1]*dims[j+1]);
            }
        return dp[0][n-1];
    }
    static boolean subsetSum(int[] nums, int target) {
        boolean[] dp=new boolean[target+1]; dp[0]=true;
        for (int n:nums) for (int j=target;j>=n;j--) dp[j]=dp[j]||dp[j-n];
        return dp[target];
    }
    static int countSubsets(int[] nums, int target) {
        int[] dp=new int[target+1]; dp[0]=1;
        for (int n:nums) for (int j=target;j>=n;j--) dp[j]+=dp[j-n];
        return dp[target];
    }
    static int burstBalloons(int[] nums) {
        int n=nums.length; int[] arr=new int[n+2];
        arr[0]=arr[n+1]=1;
        for (int i=0;i<n;i++) arr[i+1]=nums[i];
        int m=arr.length; int[][] dp=new int[m][m];
        for (int len=2;len<m;len++)
            for (int l=0;l<m-len;l++){
                int r=l+len;
                for (int k=l+1;k<r;k++)
                    dp[l][r]=Math.max(dp[l][r],dp[l][k]+arr[l]*arr[k]*arr[r]+dp[k][r]);
            }
        return dp[0][m-1];
    }

    // =========================================================
    // SECTION 5 — 1D & 2D DP PATTERNS
    // =========================================================
    static void section5_OneDAndTwoDPatterns() {
        printSection("5. 1D & 2D DP PATTERNS");

        // 5a. House Robber I & II
        System.out.println("--- 5a. House Robber I & II ---");
        int[][] houses = {{1,2,3,1},{2,7,9,3,1},{2,1,1,2},{1,2,3,4,5,1,2,3,4,5}};
        for (int[] h : houses) {
            System.out.printf("  %-30s → rob1=%d rob2=%d%n",
                    Arrays.toString(h), houseRobber1(h), houseRobber2(h));
        }

        // 5b. Maximum subarray (Kadane's)
        System.out.println("\n--- 5b. Maximum Subarray (Kadane's Algorithm) ---");
        int[][] subarrays = {{-2,1,-3,4,-1,2,1,-5,4},{1},{5,4,-1,7,8},{-1,-2,-3,-4}};
        for (int[] arr : subarrays) {
            System.out.printf("  %-30s → maxSum=%d subarray=%s%n",
                    Arrays.toString(arr), maxSubarray(arr),
                    Arrays.toString(maxSubarrayIndices(arr)));
        }

        // 5c. Climbing stairs and variants
        System.out.println("\n--- 5c. Climbing Stairs & Variants ---");
        for (int n = 1; n <= 10; n++)
            System.out.printf("  n=%2d: ways=%3d%n", n, climbStairs(n));

        // 5d. Decode ways
        System.out.println("\n--- 5d. Decode Ways ---");
        String[] codes = {"12","226","0","06","11106","2101","111111111111"};
        for (String s : codes)
            System.out.printf("  \"%s\" → %d ways%n", s, decodeWays(s));

        // 5e. Word break
        System.out.println("\n--- 5e. Word Break ---");
        List<String> dict = Arrays.asList("leet","code","apple","pen","pineapple","pine","penapple");
        String[] wbWords = {"leetcode","applepenapple","pineapplepenapple","catsandog"};
        for (String w : wbWords)
            System.out.printf("  \"%-25s → %s%n", w+"\"", wordBreakDP(w, dict));

        // 5f. Unique paths
        System.out.println("\n--- 5f. Unique Paths in Grid ---");
        int[][] grids = {{3,7},{3,3},{2,2},{5,5}};
        for (int[] g : grids)
            System.out.printf("  %dx%d grid → %d paths%n", g[0],g[1],uniquePaths(g[0],g[1]));

        int[][] obstacle = {{0,0,0},{0,1,0},{0,0,0}};
        System.out.println("  3x3 with obstacle at [1][1]: " + uniquePathsObs(obstacle));

        // 5g. Palindrome DP
        System.out.println("\n--- 5g. Palindrome DP ---");
        String[] palStrs = {"babad","cbbd","racecar","aacecaaa","abcba"};
        for (String s : palStrs)
            System.out.printf("  \"%s\" → longest palindrome substring: \"%s\"%n",
                    s, longestPalinSubstr(s));

        // 5h. Regex matching
        System.out.println("\n--- 5h. Regex Matching (. and *) ---");
        String[][] reTests = {{"aa","a"},{"aa","a*"},{"ab",".*"},{"aab","c*a*b"},{"mississippi","mis*is*p*."}};
        for (String[] t : reTests)
            System.out.printf("  s=\"%s\" p=\"%s\" → %s%n", t[0],t[1],isMatch(t[0],t[1]));
    }

    // --- Section 5 helpers ---
    static int houseRobber1(int[] nums) {
        int p2=0,p1=0;
        for (int n:nums){int c=Math.max(p1,p2+n);p2=p1;p1=c;}
        return p1;
    }
    static int houseRobber2(int[] nums) {
        int n=nums.length; if(n==1) return nums[0];
        return Math.max(robRange(nums,0,n-2),robRange(nums,1,n-1));
    }
    static int robRange(int[] nums,int s,int e){
        int p2=0,p1=0;
        for(int i=s;i<=e;i++){int c=Math.max(p1,p2+nums[i]);p2=p1;p1=c;}
        return p1;
    }
    static int maxSubarray(int[] nums) {
        int max=nums[0],curr=nums[0];
        for (int i=1;i<nums.length;i++){curr=Math.max(nums[i],curr+nums[i]);max=Math.max(max,curr);}
        return max;
    }
    static int[] maxSubarrayIndices(int[] nums) {
        int max=nums[0],curr=nums[0],s=0,e=0,ts=0;
        for (int i=1;i<nums.length;i++){
            if (nums[i]>curr+nums[i]){curr=nums[i];ts=i;} else curr+=nums[i];
            if (curr>max){max=curr;s=ts;e=i;}
        }
        return Arrays.copyOfRange(nums,s,e+1);
    }
    static int climbStairs(int n) {
        if(n<=2)return n; int a=1,b=2;
        for(int i=3;i<=n;i++){int c=a+b;a=b;b=c;} return b;
    }
    static int decodeWays(String s) {
        int n=s.length(); int[] dp=new int[n+1]; dp[0]=1;
        dp[1]=s.charAt(0)=='0'?0:1;
        for(int i=2;i<=n;i++){
            int one=Integer.parseInt(s.substring(i-1,i));
            int two=Integer.parseInt(s.substring(i-2,i));
            if(one>=1) dp[i]+=dp[i-1];
            if(two>=10&&two<=26) dp[i]+=dp[i-2];
        }
        return dp[n];
    }
    static boolean wordBreakDP(String s, List<String> dict) {
        Set<String> d=new HashSet<>(dict); boolean[] dp=new boolean[s.length()+1]; dp[0]=true;
        for(int i=1;i<=s.length();i++) for(int j=0;j<i;j++)
            if(dp[j]&&d.contains(s.substring(j,i))){dp[i]=true;break;}
        return dp[s.length()];
    }
    static int uniquePaths(int m,int n) {
        int[][] dp=new int[m][n];
        for(int i=0;i<m;i++) dp[i][0]=1;
        for(int j=0;j<n;j++) dp[0][j]=1;
        for(int i=1;i<m;i++) for(int j=1;j<n;j++) dp[i][j]=dp[i-1][j]+dp[i][j-1];
        return dp[m-1][n-1];
    }
    static int uniquePathsObs(int[][] grid) {
        int m=grid.length,n=grid[0].length;
        if(grid[0][0]==1||grid[m-1][n-1]==1) return 0;
        int[][] dp=new int[m][n]; dp[0][0]=1;
        for(int i=1;i<m;i++) dp[i][0]=grid[i][0]==1?0:dp[i-1][0];
        for(int j=1;j<n;j++) dp[0][j]=grid[0][j]==1?0:dp[0][j-1];
        for(int i=1;i<m;i++) for(int j=1;j<n;j++)
            dp[i][j]=grid[i][j]==1?0:dp[i-1][j]+dp[i][j-1];
        return dp[m-1][n-1];
    }
    static String longestPalinSubstr(String s) {
        int n=s.length(),start=0,maxLen=1;
        boolean[][] dp=new boolean[n][n];
        for(int i=0;i<n;i++) dp[i][i]=true;
        for(int i=0;i<n-1;i++) if(s.charAt(i)==s.charAt(i+1)){dp[i][i+1]=true;start=i;maxLen=2;}
        for(int len=3;len<=n;len++) for(int i=0;i<=n-len;i++){
            int j=i+len-1;
            if(s.charAt(i)==s.charAt(j)&&dp[i+1][j-1]){dp[i][j]=true;if(len>maxLen){maxLen=len;start=i;}}
        }
        return s.substring(start,start+maxLen);
    }
    static boolean isMatch(String s, String p) {
        int m=s.length(),n=p.length(); boolean[][] dp=new boolean[m+1][n+1];
        dp[0][0]=true;
        for(int j=2;j<=n;j+=2) dp[0][j]=dp[0][j-2]&&p.charAt(j-1)=='*';
        for(int i=1;i<=m;i++) for(int j=1;j<=n;j++){
            if(p.charAt(j-1)=='*') dp[i][j]=dp[i][j-2]||(dp[i-1][j]&&(p.charAt(j-2)==s.charAt(i-1)||p.charAt(j-2)=='.'));
            else dp[i][j]=dp[i-1][j-1]&&(p.charAt(j-1)==s.charAt(i-1)||p.charAt(j-1)=='.');
        }
        return dp[m][n];
    }

    // =========================================================
    // SECTION 6 — OPTIMIZATION STRATEGIES
    // =========================================================
    static void section6_OptimizationStrategies() {
        printSection("6. OPTIMIZATION STRATEGIES");

        // 6a. Space-optimized LCS
        System.out.println("--- 6a. Space-Optimized LCS (O(n) space) ---");
        String[][] lcsPairs = {{"ABCBDAB","BDCABA"},{"AGGTAB","GXTXAYB"},{"abcde","ace"}};
        for (String[] p : lcsPairs) {
            int full = lcs(p[0],p[1]);
            int opt  = lcsSpaceOpt(p[0],p[1]);
            System.out.printf("  LCS(\"%s\",\"%s\") full=%d opt=%d %s%n",
                    p[0],p[1],full,opt,full==opt?"✓":"✗");
        }

        // 6b. Space-optimized edit distance
        System.out.println("\n--- 6b. Space-Optimized Edit Distance ---");
        String[][] edPairs = {{"horse","ros"},{"kitten","sitting"},{"sunday","saturday"}};
        for (String[] p : edPairs) {
            int full = editDistance(p[0],p[1]);
            int opt  = editDistanceOpt(p[0],p[1]);
            System.out.printf("  edit(\"%s\",\"%s\") full=%d opt=%d %s%n",
                    p[0],p[1],full,opt,full==opt?"✓":"✗");
        }

        // 6c. Bitmask DP — TSP
        System.out.println("\n--- 6c. Bitmask DP: Traveling Salesman Problem ---");
        int[][] dist = {{0,10,15,20},{10,0,35,25},{15,35,0,30},{20,25,30,0}};
        System.out.println("  4-city distance matrix: " + Arrays.deepToString(dist));
        System.out.println("  Min TSP tour cost = " + tsp(dist, 4));

        // 6d. Optimal BST
        System.out.println("\n--- 6d. Optimal Binary Search Tree ---");
        int[] freq = {34, 8, 50, 5, 10, 3, 25};
        System.out.println("  Frequencies: " + Arrays.toString(freq));
        System.out.println("  Optimal BST cost = " + optimalBST(freq));

        // 6e. Memoization vs tabulation trade-off
        System.out.println("\n--- 6e. Memoization vs Tabulation ---");
        int n = 1000;
        long t0 = System.nanoTime();
        for (int i = 0; i < 100; i++) fibDP(n);
        long tabTime = System.nanoTime() - t0;
        t0 = System.nanoTime();
        for (int i = 0; i < 100; i++) fibMemo(n, new long[n+2]);
        long memoTime = System.nanoTime() - t0;
        System.out.printf("  fib(%d) x100: tabulation=%,dns memoization=%,dns%n",
                n, tabTime, memoTime);
        System.out.println("  Tabulation avoids recursion overhead + better cache locality");

        // 6f. Rolling array for knapsack
        System.out.println("\n--- 6f. Space Comparison: 2D vs 1D Knapsack ---");
        int[] w = {2,3,4,5,6,7,8}; int[] v = {3,4,5,6,7,8,9}; int cap = 15;
        System.out.printf("  2D uses O(n×W)=%d cells, 1D uses O(W)=%d cells%n",
                (w.length+1)*(cap+1), cap+1);
        System.out.println("  Both give: " + knapsack2D(w,v,cap) + " = " + knapsack1D(w,v,cap));
    }

    // --- Section 6 helpers ---
    static int lcsSpaceOpt(String a, String b) {
        int m=a.length(),n=b.length();
        int[] prev=new int[n+1],curr=new int[n+1];
        for(int i=1;i<=m;i++){
            for(int j=1;j<=n;j++)
                curr[j]=a.charAt(i-1)==b.charAt(j-1)?prev[j-1]+1:Math.max(prev[j],curr[j-1]);
            int[] t=prev;prev=curr;curr=t;Arrays.fill(curr,0);
        }
        return prev[n];
    }
    static int editDistanceOpt(String a, String b) {
        int m=a.length(),n=b.length(); int[] dp=new int[n+1];
        for(int j=0;j<=n;j++) dp[j]=j;
        for(int i=1;i<=m;i++){
            int prev=dp[0]; dp[0]=i;
            for(int j=1;j<=n;j++){
                int tmp=dp[j];
                dp[j]=a.charAt(i-1)==b.charAt(j-1)?prev:1+Math.min(prev,Math.min(dp[j],dp[j-1]));
                prev=tmp;
            }
        }
        return dp[n];
    }
    static int tsp(int[][] dist, int n) {
        int FULL=(1<<n)-1; int[][] dp=new int[1<<n][n];
        for(int[] row:dp) Arrays.fill(row,Integer.MAX_VALUE/2);
        dp[1][0]=0;
        for(int mask=1;mask<(1<<n);mask++)
            for(int u=0;u<n;u++){
                if((mask&(1<<u))==0||dp[mask][u]==Integer.MAX_VALUE/2) continue;
                for(int v=0;v<n;v++){
                    if((mask&(1<<v))!=0) continue;
                    int nm=mask|(1<<v);
                    dp[nm][v]=Math.min(dp[nm][v],dp[mask][u]+dist[u][v]);
                }
            }
        int min=Integer.MAX_VALUE;
        for(int u=1;u<n;u++) min=Math.min(min,dp[FULL][u]+dist[u][0]);
        return min;
    }
    static int optimalBST(int[] freq) {
        int n=freq.length; int[] pre=new int[n+1];
        for(int i=0;i<n;i++) pre[i+1]=pre[i]+freq[i];
        int[][] dp=new int[n+1][n];
        for(int len=1;len<=n;len++) for(int i=0;i<=n-len;i++){
            int j=i+len-1; dp[i][j]=Integer.MAX_VALUE;
            int sum=pre[j+1]-pre[i];
            for(int r=i;r<=j;r++){
                int l=r>i?dp[i][r-1]:0,ri=r<j?dp[r+1][j]:0;
                dp[i][j]=Math.min(dp[i][j],l+ri+sum);
            }
        }
        return dp[0][n-1];
    }

    // =========================================================
    // SECTION 7 — INTERVIEW PROBLEM SOLVING
    // =========================================================
    static void section7_InterviewProblems() {
        printSection("7. INTERVIEW PROBLEM SOLVING");

        // P1. Stock problems I-IV
        System.out.println("--- P1. Stock Problems I–IV ---");
        int[][] stockPrices = {{7,1,5,3,6,4},{1,2,3,4,5},{7,6,4,3,1},{3,3,5,0,0,3,1,4},{1,2,4,2,5,7,2,4,9,0}};
        for (int[] p : stockPrices) {
            System.out.printf("  prices=%-25s I=%d II=%d III=%d IV(k=2)=%d%n",
                    Arrays.toString(p),
                    stockI(p), stockII(p), stockIII(p), stockIV(2,p));
        }

        // P2. Palindrome problems
        System.out.println("\n--- P2. Palindrome DP Family ---");
        String[] palStrs = {"bbbab","cbbd","agbdba","a","abba"};
        for (String s : palStrs) {
            System.out.printf("  \"%s\" → longestPalinSubseq=%d longestPalinSubstr=\"%s\" minCutPalin=%d%n",
                    s, longestPalinSubseq(s), longestPalinSubstr(s), minCutPalin(s));
        }

        // P3. Target sum (LC 494)
        System.out.println("\n--- P3. Target Sum (LC 494) ---");
        int[][] nums3 = {{1,1,1,1,1},{1},{1,0},{0,0,0,0,0,0,0,0,1}};
        int[] targets3= {3,1,1,1};
        for (int i = 0; i < nums3.length; i++)
            System.out.printf("  nums=%s target=%d → %d ways%n",
                    Arrays.toString(nums3[i]), targets3[i],
                    targetSum(nums3[i], targets3[i]));

        // P4. Perfect squares (LC 279)
        System.out.println("\n--- P4. Perfect Squares — Min Count (LC 279) ---");
        for (int n : new int[]{12,13,1,4,7,100})
            System.out.printf("  n=%3d → %d perfect squares%n", n, perfectSquares(n));

        // P5. Longest common substring (differs from LCS)
        System.out.println("\n--- P5. Longest Common Substring (contiguous) ---");
        String[][] subPairs = {{"ABAB","BABA"},{"OldSite","NewSite"},{"abcde","abfce"}};
        for (String[] p : subPairs)
            System.out.printf("  LCSubstr(\"%s\",\"%s\") = %d \"%s\"%n",
                    p[0],p[1],longestCommonSubstrLen(p[0],p[1]),longestCommonSubstr(p[0],p[1]));

        // P6. Partition equal subset sum (LC 416)
        System.out.println("\n--- P6. Partition Equal Subset Sum (LC 416) ---");
        int[][] partTests = {{1,5,11,5},{1,2,3,5},{3,3,3,4,5},{1,1}};
        for (int[] t : partTests)
            System.out.printf("  %s → %s%n",
                    Arrays.toString(t), canPartition(t));

        // P7. Minimum path sum in grid (LC 64)
        System.out.println("\n--- P7. Minimum Path Sum in Grid (LC 64) ---");
        int[][][] grids = {
            {{1,3,1},{1,5,1},{4,2,1}},
            {{1,2,3},{4,5,6}},
            {{1,2},{1,1}}
        };
        for (int[][] g : grids)
            System.out.printf("  %s → %d%n", Arrays.deepToString(g), minPathSum(g));

        // P8. Coin change 2 — number of combinations (LC 518)
        System.out.println("\n--- P8. Coin Change II — Count Combinations (LC 518) ---");
        int[][] cc2Coins = {{1,2,5},{2},{10}};
        int[] cc2Amts   = {5,3,10};
        for (int i = 0; i < cc2Coins.length; i++)
            System.out.printf("  coins=%s amount=%d → %d combinations%n",
                    Arrays.toString(cc2Coins[i]), cc2Amts[i],
                    coinChange2(cc2Coins[i], cc2Amts[i]));

        // P9. Largest divisible subset (LC 368)
        System.out.println("\n--- P9. Largest Divisible Subset (LC 368) ---");
        int[][] ldsSets = {{1,2,3},{1,2,4,8},{1,4,16,2}};
        for (int[] nums : ldsSets)
            System.out.printf("  %s → %s%n",
                    Arrays.toString(nums), largestDivSubset(nums));

        // P10. DP framework reminder
        System.out.println("\n--- P10. DP Interview Framework ---");
        System.out.println("  1. RECOGNIZE: optimization/counting/feasibility?");
        System.out.println("  2. DEFINE STATE: dp[i] means what exactly?");
        System.out.println("  3. RECURRENCE: dp[i] = f(dp[i-1],...)?");
        System.out.println("  4. BASE CASE: dp[0]=?");
        System.out.println("  5. FILL ORDER: left→right, top→bottom");
        System.out.println("  6. ANSWER: dp[n] or max(dp[*])?");
        System.out.println("  7. OPTIMIZE: rolling array, O(1) vars?");
    }

    // --- Section 7 helpers ---
    static int stockI(int[] prices) {
        int min=Integer.MAX_VALUE,profit=0;
        for(int p:prices){min=Math.min(min,p);profit=Math.max(profit,p-min);}
        return profit;
    }
    static int stockII(int[] prices) {
        int profit=0;
        for(int i=1;i<prices.length;i++) if(prices[i]>prices[i-1]) profit+=prices[i]-prices[i-1];
        return profit;
    }
    static int stockIII(int[] prices) {
        int b1=Integer.MIN_VALUE,s1=0,b2=Integer.MIN_VALUE,s2=0;
        for(int p:prices){b1=Math.max(b1,-p);s1=Math.max(s1,b1+p);b2=Math.max(b2,s1-p);s2=Math.max(s2,b2+p);}
        return s2;
    }
    static int stockIV(int k, int[] prices) {
        int n=prices.length; if(k>=n/2) return stockII(prices);
        int[] buy=new int[k],sell=new int[k]; Arrays.fill(buy,Integer.MIN_VALUE);
        for(int p:prices) for(int i=0;i<k;i++){
            buy[i]=Math.max(buy[i],(i==0?0:sell[i-1])-p);
            sell[i]=Math.max(sell[i],buy[i]+p);
        }
        return sell[k-1];
    }
    static int longestPalinSubseq(String s) { return lcs(s, new StringBuilder(s).reverse().toString()); }
    static int minCutPalin(String s) {
        int n=s.length(); boolean[][] pal=new boolean[n][n];
        for(int i=0;i<n;i++) pal[i][i]=true;
        for(int i=0;i<n-1;i++) pal[i][i+1]=s.charAt(i)==s.charAt(i+1);
        for(int len=3;len<=n;len++) for(int i=0;i<=n-len;i++){int j=i+len-1;pal[i][j]=s.charAt(i)==s.charAt(j)&&pal[i+1][j-1];}
        int[] dp=new int[n]; Arrays.fill(dp,Integer.MAX_VALUE);
        for(int i=0;i<n;i++){
            if(pal[0][i]){dp[i]=0;continue;}
            for(int j=1;j<=i;j++) if(pal[j][i]&&dp[j-1]!=Integer.MAX_VALUE) dp[i]=Math.min(dp[i],dp[j-1]+1);
        }
        return dp[n-1];
    }
    static int targetSum(int[] nums, int target) {
        int sum=Arrays.stream(nums).sum();
        if(Math.abs(target)>sum||(sum+target)%2!=0) return 0;
        int s=(sum+target)/2; int[] dp=new int[s+1]; dp[0]=1;
        for(int n:nums) for(int j=s;j>=n;j--) dp[j]+=dp[j-n];
        return dp[s];
    }
    static int perfectSquares(int n) {
        int[] dp=new int[n+1]; Arrays.fill(dp,Integer.MAX_VALUE); dp[0]=0;
        for(int i=1;i<=n;i++) for(int j=1;j*j<=i;j++) dp[i]=Math.min(dp[i],dp[i-j*j]+1);
        return dp[n];
    }
    static int longestCommonSubstrLen(String a, String b) {
        int m=a.length(),n=b.length(),max=0; int[][] dp=new int[m+1][n+1];
        for(int i=1;i<=m;i++) for(int j=1;j<=n;j++)
            if(a.charAt(i-1)==b.charAt(j-1)) max=Math.max(max,dp[i][j]=dp[i-1][j-1]+1);
        return max;
    }
    static String longestCommonSubstr(String a, String b) {
        int m=a.length(),n=b.length(),max=0,end=0; int[][] dp=new int[m+1][n+1];
        for(int i=1;i<=m;i++) for(int j=1;j<=n;j++)
            if(a.charAt(i-1)==b.charAt(j-1)&&dp[i][j-1+1-1+1]>(max)){
                dp[i][j]=dp[i-1][j-1]+1;
                if(dp[i][j]>max){max=dp[i][j];end=i;}
            } else dp[i][j]=0;
        // Recompute for string
        max=longestCommonSubstrLen(a,b);
        for(int i=1;i<=m;i++) for(int j=1;j<=n;j++){
            dp[i][j]=a.charAt(i-1)==b.charAt(j-1)?dp[i-1][j-1]+1:0;
            if(dp[i][j]==max) end=i;
        }
        return a.substring(end-max,end);
    }
    static boolean canPartition(int[] nums) {
        int sum=Arrays.stream(nums).sum();
        if(sum%2!=0) return false;
        boolean[] dp=new boolean[sum/2+1]; dp[0]=true;
        for(int n:nums) for(int j=sum/2;j>=n;j--) dp[j]=dp[j]||dp[j-n];
        return dp[sum/2];
    }
    static int minPathSum(int[][] grid) {
        int m=grid.length,n=grid[0].length; int[][] dp=new int[m][n];
        dp[0][0]=grid[0][0];
        for(int i=1;i<m;i++) dp[i][0]=dp[i-1][0]+grid[i][0];
        for(int j=1;j<n;j++) dp[0][j]=dp[0][j-1]+grid[0][j];
        for(int i=1;i<m;i++) for(int j=1;j<n;j++) dp[i][j]=Math.min(dp[i-1][j],dp[i][j-1])+grid[i][j];
        return dp[m-1][n-1];
    }
    static int coinChange2(int[] coins, int amount) {
        int[] dp=new int[amount+1]; dp[0]=1;
        for(int c:coins) for(int j=c;j<=amount;j++) dp[j]+=dp[j-c];
        return dp[amount];
    }
    static List<Integer> largestDivSubset(int[] nums) {
        Arrays.sort(nums); int n=nums.length;
        int[] dp=new int[n],from=new int[n]; Arrays.fill(dp,1);
        for(int i=0;i<n;i++) from[i]=i;
        int maxLen=1,maxIdx=0;
        for(int i=1;i<n;i++) for(int j=0;j<i;j++)
            if(nums[i]%nums[j]==0&&dp[j]+1>dp[i]){dp[i]=dp[j]+1;from[i]=j;}
        for(int i=0;i<n;i++) if(dp[i]>maxLen){maxLen=dp[i];maxIdx=i;}
        List<Integer> res=new ArrayList<>();
        for(int i=maxIdx;;i=from[i]){res.add(0,nums[i]);if(from[i]==i) break;}
        return res;
    }

    // =========================================================
    // TREE NODE (for tree DP)
    // =========================================================
    static class TreeNode {
        int val; TreeNode left,right;
        TreeNode(int val){this.val=val;}
        TreeNode(int val,TreeNode l,TreeNode r){this.val=val;left=l;right=r;}
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
