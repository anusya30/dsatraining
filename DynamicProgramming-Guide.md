# Dynamic Programming — Complete Guide
> Optimization Through Memorization
> Covers: Why DP · Recursion vs Memoization · Tabulation · Classic Problems · 1D & 2D Patterns · Optimization Strategies · Interview Problem Solving

---

## Table of Contents
1. [Why Dynamic Programming?](#1-why-dynamic-programming)
2. [Recursion vs Memoization](#2-recursion-vs-memoization)
3. [Tabulation (Bottom-Up)](#3-tabulation-bottom-up)
4. [Classic DP Problems](#4-classic-dp-problems)
5. [1D & 2D DP Patterns](#5-1d--2d-dp-patterns)
6. [Optimization Strategies](#6-optimization-strategies)
7. [Interview Problem Solving](#7-interview-problem-solving)

---

## 1. Why Dynamic Programming?

### The Core Problem DP Solves
Many problems have **overlapping subproblems** — the same smaller problem is solved
repeatedly. DP solves each subproblem **exactly once** and stores the result.

```
Without DP: fib(40) makes ~2 billion recursive calls
With DP:    fib(40) makes exactly 40 calls

Without DP: 0/1 Knapsack with n=30 items → 2^30 ≈ 1 billion combinations
With DP:    O(n × W) where W = capacity → manageable polynomial time
```

### Two Core Properties (Both Must Hold)

#### Property 1: Optimal Substructure
```
An optimal solution to the problem contains optimal solutions to its subproblems.

Example — Shortest Path:
  Shortest path from A to C through B =
    shortest(A→B) + shortest(B→C)
  Each sub-path is itself optimal.

Counterexample — Longest Simple Path:
  Longest path from A to C ≠ longest(A→B) + longest(B→C)
  (Paths might share vertices, so cannot be independently optimized)
```

#### Property 2: Overlapping Subproblems
```
The same subproblems are solved multiple times.

fib(5) call tree:
  fib(5) = fib(4) + fib(3)
  fib(4) = fib(3) + fib(2)   ← fib(3) solved AGAIN
  fib(3) = fib(2) + fib(1)   ← fib(2) solved AGAIN
  fib(2) = fib(1) + fib(0)

Without memoization: O(2^n) time
With memoization:    O(n) time — each fib(i) computed once
```

### DP vs Other Approaches

| Approach | Strategy | When to Use |
|---|---|---|
| Brute Force | Try all possibilities | Never, if avoidable |
| Divide & Conquer | Split into independent subproblems | Merge sort, FFT |
| Greedy | Local optimal choice | Provable greedy choice property |
| **Dynamic Programming** | Store overlapping subproblem results | Optimal substructure + overlapping |

### The DP Mindset — Four Steps
```
Step 1: DEFINE the subproblem
  "dp[i] = ..." — what does this value mean?
  This is the hardest and most important step.

Step 2: IDENTIFY the recurrence
  How does dp[i] relate to dp[i-1], dp[i-2], ...?
  "dp[i] = max(dp[i-1] + arr[i], arr[i])"

Step 3: ESTABLISH base cases
  What are the smallest subproblems with known answers?
  "dp[0] = arr[0]"

Step 4: DETERMINE computation order
  Top-down (memoization): let recursion handle order
  Bottom-up (tabulation): fill table in dependency order
```

### Real-World Impact
```
Problems that seem intractable without DP:

1. DNA Sequence Alignment (Bioinformatics)
   Align two DNA sequences of length 1000
   Brute force: 2^1000 alignments
   DP (Edit Distance variant): O(n²) — milliseconds

2. Speech Recognition (NLP)
   Viterbi algorithm decodes spoken words using DP
   Hidden Markov Model + DP = O(n × states²)

3. Portfolio Optimization (Finance)
   Select assets to maximize return given risk constraints
   DP over asset combinations: O(n × budget)

4. Compiler Optimization
   Register allocation, instruction scheduling use DP
   Matrix chain multiplication for expression evaluation

5. Video Game Pathfinding
   A* uses DP principles; optimal level traversal
```

---

## 2. Recursion vs Memoization

### Pure Recursion — The Problem
```java
// Fibonacci — naive recursive: O(2^n) time
public static long fibNaive(int n) {
    if (n <= 1) return n;              // Base cases
    return fibNaive(n - 1) + fibNaive(n - 2); // Redundant recomputation
}

// Call tree for fib(6):
//                    fib(6)
//                 /          \
//            fib(5)          fib(4)
//           /      \        /      \
//       fib(4)   fib(3) fib(3)  fib(2)
//       /   \    /   \
//   fib(3) fib(2) ...  ← same subproblems everywhere
//
// fib(3) computed: 3 times
// fib(2) computed: 5 times
// Total nodes ≈ 2^n
```

### Top-Down DP: Memoization
```java
// Fibonacci — memoized: O(n) time, O(n) space
public static long fibMemo(int n, long[] memo) {
    if (n <= 1) return n;
    if (memo[n] != 0) return memo[n];      // Cache hit — return stored result
    memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo); // Compute once
    return memo[n];
}

// Call tree for fibMemo(6) — each node computed ONCE:
//            fib(6)
//           /       \
//       fib(5)    fib(4)*  ← * = cache hit, O(1)
//       /    \
//   fib(4)  fib(3)*
//   /    \
// fib(3) fib(2)*
// /    \
// fib(2) fib(1)*
// /    \
// fib(1) fib(0)
//
// Total unique calls: n+1 = O(n)
```

### Memoization with HashMap (When Keys Are Complex)
```java
// Rod cutting — memo with HashMap for complex keys
public static int rodCutting(int[] prices, int n, Map<Integer, Integer> memo) {
    if (n == 0) return 0;
    if (memo.containsKey(n)) return memo.get(n);

    int maxVal = Integer.MIN_VALUE;
    for (int i = 1; i <= n; i++) {
        // prices[i-1] = price of rod of length i
        int val = prices[i - 1] + rodCutting(prices, n - i, memo);
        maxVal = Math.max(maxVal, val);
    }
    memo.put(n, maxVal);
    return maxVal;
}
// prices = [1,5,8,9,10,17,17,20], n=8
// Max revenue = 22 (cut into 2+6 or 2+2+4)
```

### Memoization Template
```java
// Generic memoization pattern
Map<StateKey, Answer> memo = new HashMap<>();

Answer solve(State state) {
    // 1. Base case
    if (isBaseCase(state)) return baseAnswer(state);

    // 2. Check cache
    if (memo.containsKey(state)) return memo.get(state);

    // 3. Compute by combining subproblem answers
    Answer result = combine(solve(subproblem1(state)),
                            solve(subproblem2(state)));

    // 4. Cache and return
    memo.put(state, result);
    return result;
}
```

### When to Use Memoization vs Tabulation
```
Use MEMOIZATION (top-down) when:
  ✓ Not all subproblems need to be solved (sparse)
  ✓ Problem naturally expressed recursively
  ✓ Easier to reason about (follows natural problem structure)
  ✓ State space is complex (2D, 3D, string keys)
  → Time: same as tabulation; Space: O(n) + recursion stack

Use TABULATION (bottom-up) when:
  ✓ All subproblems must be solved anyway (dense)
  ✓ Want to avoid recursion stack overhead
  ✓ Want to optimize space (rolling array possible)
  ✓ Need to iterate in a specific order
  → Time: same as memoization; Space: O(n), no stack
```

---

## 3. Tabulation (Bottom-Up)

### The Bottom-Up Approach
Instead of recursing down and caching, **fill a table starting from base cases** and build up to the answer.

```
fib(6) via tabulation:

Table:  [0, 1, 1, 2, 3, 5, 8]
Index:   0  1  2  3  4  5  6

Fill order: left to right (dp[i] depends on dp[i-1] and dp[i-2])
Answer: dp[6] = 8
```

### Fibonacci — All Three Approaches
```java
// Approach 1: Naive recursion — O(2^n)
public static long fib1(int n) {
    if (n <= 1) return n;
    return fib1(n-1) + fib1(n-2);
}

// Approach 2: Memoization — O(n) time, O(n) space
public static long fib2(int n, long[] memo) {
    if (n <= 1) return n;
    if (memo[n] != 0) return memo[n];
    return memo[n] = fib2(n-1, memo) + fib2(n-2, memo);
}

// Approach 3: Tabulation — O(n) time, O(n) space
public static long fib3(int n) {
    if (n <= 1) return n;
    long[] dp = new long[n + 1];
    dp[0] = 0; dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i-1] + dp[i-2];  // Recurrence
    }
    return dp[n];
}

// Approach 4: Space-optimized tabulation — O(n) time, O(1) space
public static long fib4(int n) {
    if (n <= 1) return n;
    long a = 0, b = 1;
    for (int i = 2; i <= n; i++) {
        long c = a + b; a = b; b = c;  // Only need last two values
    }
    return b;
}
```

### Tabulation Template
```java
// Generic tabulation pattern

// 1. Allocate table
int[] dp = new int[n + 1];    // Or int[][] dp for 2D

// 2. Initialize base cases
dp[0] = base0;
dp[1] = base1;

// 3. Fill table using recurrence (in dependency order)
for (int i = 2; i <= n; i++) {
    dp[i] = recurrence(dp[i-1], dp[i-2], ...);
}

// 4. Return answer
return dp[n];      // Or dp[n][m] for 2D
```

### Coin Change — Tabulation Deep Dive
```java
// Minimum coins to make amount
// coins = [1,5,6,9], amount = 11

public static int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);    // Initialize to "infinity"
    dp[0] = 0;                      // Base case: 0 coins to make amount 0

    // Fill for each amount from 1 to target
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}

// Trace for coins=[1,5,6,9], amount=11:
// dp[0]  = 0
// dp[1]  = dp[0]+1 = 1    (use coin 1)
// dp[2]  = dp[1]+1 = 2
// dp[5]  = dp[0]+1 = 1    (use coin 5)
// dp[6]  = dp[0]+1 = 1    (use coin 6)
// dp[9]  = dp[0]+1 = 1    (use coin 9)
// dp[10] = dp[9]+1 = 2    (9+1)
// dp[11] = dp[5]+1 = 2    (5+6=11) ← Greedy would give 9+1+1=3
```

---

## 4. Classic DP Problems

### 4.1 Longest Common Subsequence (LCS)

#### Problem
Find the longest subsequence common to two strings. A subsequence doesn't need to be contiguous.

#### Subproblem Definition
```
dp[i][j] = length of LCS of s1[0..i-1] and s2[0..j-1]
```

#### Recurrence
```
if s1[i-1] == s2[j-1]:
    dp[i][j] = dp[i-1][j-1] + 1     ← characters match: extend LCS
else:
    dp[i][j] = max(dp[i-1][j], dp[i][j-1])  ← skip one character from either
```

#### Trace for "ABCBDAB" and "BDCABA"
```
    ""  B  D  C  A  B  A
""   0  0  0  0  0  0  0
A    0  0  0  0  1  1  1
B    0  1  1  1  1  2  2
C    0  1  1  2  2  2  2
B    0  1  1  2  2  3  3
D    0  1  2  2  2  3  3
A    0  1  2  2  3  3  4
B    0  1  2  2  3  4  4

LCS length = 4 ("BCBA" or "BDAB")
```

```java
public static int lcs(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1] + 1;           // Characters match
            } else {
                dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]); // Take max
            }
        }
    }
    return dp[m][n];
}

// Reconstruct the actual LCS string
public static String lcsString(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            dp[i][j] = s1.charAt(i-1) == s2.charAt(j-1)
                ? dp[i-1][j-1] + 1
                : Math.max(dp[i-1][j], dp[i][j-1]);

    // Traceback
    StringBuilder sb = new StringBuilder();
    int i = m, j = n;
    while (i > 0 && j > 0) {
        if (s1.charAt(i-1) == s2.charAt(j-1)) {
            sb.append(s1.charAt(i-1));
            i--; j--;
        } else if (dp[i-1][j] > dp[i][j-1]) {
            i--;
        } else {
            j--;
        }
    }
    return sb.reverse().toString();
}
```

---

### 4.2 0/1 Knapsack

#### Problem
Given items with weights and values, fill a knapsack of capacity W to maximize total value. Each item can be taken at most once (0/1).

#### Subproblem Definition
```
dp[i][w] = maximum value using first i items with weight capacity w
```

#### Recurrence
```
dp[i][w] = dp[i-1][w]                              if weight[i] > w (can't take item i)
dp[i][w] = max(dp[i-1][w],                         otherwise: max of skip or take
               dp[i-1][w - weight[i]] + value[i])
```

#### Trace
```
items: weight=[2,3,4,5], value=[3,4,5,6], capacity=8

     w=0  1  2  3  4  5  6  7  8
i=0   0   0  0  0  0  0  0  0  0
i=1   0   0  3  3  3  3  3  3  3  ← item1(w=2,v=3)
i=2   0   0  3  4  4  7  7  7  7  ← item2(w=3,v=4)
i=3   0   0  3  4  5  7  8  9  9  ← item3(w=4,v=5)
i=4   0   0  3  4  5  7  8  9 10  ← item4(w=5,v=6)

Answer: dp[4][8] = 10
Items taken: item4(v=6,w=5) + item2(v=4,w=3) = w:8, v:10
```

```java
public static int knapsack01(int[] weights, int[] values, int W) {
    int n = weights.length;
    int[][] dp = new int[n + 1][W + 1];

    for (int i = 1; i <= n; i++) {
        for (int w = 0; w <= W; w++) {
            dp[i][w] = dp[i-1][w];                    // Option 1: skip item i
            if (weights[i-1] <= w) {
                dp[i][w] = Math.max(dp[i][w],
                    dp[i-1][w - weights[i-1]] + values[i-1]); // Option 2: take item i
            }
        }
    }
    return dp[n][W];
}

// Space-optimized: use 1D array (iterate w backwards)
public static int knapsack01Opt(int[] weights, int[] values, int W) {
    int[] dp = new int[W + 1];
    for (int i = 0; i < weights.length; i++) {
        // Iterate BACKWARDS to prevent using item i twice
        for (int w = W; w >= weights[i]; w--) {
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
        }
    }
    return dp[W];
}
```

---

### 4.3 Longest Increasing Subsequence (LIS)

#### Problem
Find the length of the longest strictly increasing subsequence.

#### Two Approaches
```java
// O(n²) DP approach
public static int lisDP(int[] nums) {
    int n = nums.length;
    int[] dp = new int[n];
    Arrays.fill(dp, 1);              // Each element is LIS of length 1

    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[j] < nums[i]) {
                dp[i] = Math.max(dp[i], dp[j] + 1); // Extend LIS ending at j
            }
        }
    }
    return Arrays.stream(dp).max().getAsInt();
}

// O(n log n) patience sorting approach
public static int lisBinarySearch(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    for (int num : nums) {
        int pos = Collections.binarySearch(tails, num);
        if (pos < 0) pos = -(pos + 1);  // Insertion point
        if (pos == tails.size()) {
            tails.add(num);              // Extend LIS
        } else {
            tails.set(pos, num);         // Replace to maintain smallest tails
        }
    }
    return tails.size();
}

// nums = [10,9,2,5,3,7,101,18]
// dp   = [1, 1,1,2,2,3,  4,  4]
// LIS  = 4  (e.g., [2,3,7,101] or [2,5,7,101])
```

---

### 4.4 Edit Distance (Levenshtein)

#### Problem
Minimum operations (insert, delete, replace) to convert string s1 to s2.

#### Subproblem Definition
```
dp[i][j] = min operations to convert s1[0..i-1] to s2[0..j-1]
```

#### Recurrence
```
if s1[i-1] == s2[j-1]:
    dp[i][j] = dp[i-1][j-1]             ← no operation needed
else:
    dp[i][j] = 1 + min(
        dp[i-1][j],                       ← delete from s1
        dp[i][j-1],                       ← insert into s1
        dp[i-1][j-1]                      ← replace in s1
    )
```

#### Trace for "horse" → "ros"
```
     ""  r  o  s
""    0  1  2  3
h     1  1  2  3
o     2  2  1  2
r     3  2  2  2
s     4  3  3  2
e     5  4  4  3

Edit distance = 3:
  horse → rorse (replace h→r)
  rorse → rose  (delete r)
  rose  → ros   (delete e)
```

```java
public static int editDistance(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];

    // Base cases: convert to/from empty string
    for (int i = 0; i <= m; i++) dp[i][0] = i;  // Delete all of s1
    for (int j = 0; j <= n; j++) dp[0][j] = j;  // Insert all of s2

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1];           // Characters match
            } else {
                dp[i][j] = 1 + Math.min(dp[i-1][j-1],   // Replace
                               Math.min(dp[i-1][j],       // Delete
                                        dp[i][j-1]));     // Insert
            }
        }
    }
    return dp[m][n];
}
```

#### Real-World Use
```
- Spell checkers: "speling" → "spelling" (edit distance 1)
- DNA sequence alignment: measure genetic similarity
- Git diff: finding differences between file versions
- Search autocorrect: "teh" → "the" (distance 1)
- Plagiarism detection: measuring text similarity
```

---

### 4.5 Matrix Chain Multiplication

#### Problem
Given matrices A₁, A₂, ..., Aₙ, find the parenthesization that minimizes the number of scalar multiplications.

#### Key Insight
```
Two matrices A(p×q) and B(q×r) → C(p×r) requires p×q×r multiplications.
Different parenthesizations give different costs:

A₁(10×30) × A₂(30×5) × A₃(5×60):
  (A₁×A₂)×A₃ = 10×30×5 + 10×5×60 = 1500 + 3000 = 4500
  A₁×(A₂×A₃) = 30×5×60 + 10×30×60 = 9000 + 18000 = 27000
  4500 vs 27000 — 6x difference!
```

#### Subproblem Definition
```
dp[i][j] = minimum multiplications to compute Aᵢ × Aᵢ₊₁ × ... × Aⱼ
```

```java
public static int matrixChain(int[] dims) {
    int n = dims.length - 1;     // Number of matrices
    int[][] dp = new int[n][n];  // dp[i][j] = min cost for matrices i..j

    // Length 1: single matrix, cost = 0
    // Length l = 2, 3, ..., n
    for (int len = 2; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            dp[i][j] = Integer.MAX_VALUE;
            for (int k = i; k < j; k++) {
                // Split: (i..k) × (k+1..j)
                int cost = dp[i][k] + dp[k+1][j]
                           + dims[i] * dims[k+1] * dims[j+1];
                dp[i][j] = Math.min(dp[i][j], cost);
            }
        }
    }
    return dp[0][n-1];
}
// dims = [10,30,5,60] → 4500
// dims = [40,20,30,10,30] → 26000
```

---

### 4.6 Subset Sum

#### Problem
Can we select a subset of numbers that sum to a target?

```java
public static boolean subsetSum(int[] nums, int target) {
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;                // Empty subset sums to 0

    for (int num : nums) {
        // Traverse backwards (like 0/1 knapsack — each element used once)
        for (int j = target; j >= num; j--) {
            dp[j] = dp[j] || dp[j - num];
        }
    }
    return dp[target];
}
// nums=[3,34,4,12,5,2], target=9 → true (4+3+2 or 4+5)
// nums=[3,34,4,12,5,2], target=30 → false

// Count subsets summing to target
public static int countSubsets(int[] nums, int target) {
    int[] dp = new int[target + 1];
    dp[0] = 1;
    for (int num : nums)
        for (int j = target; j >= num; j--)
            dp[j] += dp[j - num];
    return dp[target];
}
```

---

## 5. 1D & 2D DP Patterns

### 5.1 Key 1D DP Patterns

#### Pattern 1 — "Current or Nothing" (House Robber)
```
dp[i] = max value considering first i elements where we might skip some

Recurrence: dp[i] = max(dp[i-1],          // Skip current
                        dp[i-2] + arr[i])  // Take current (can't take i-1)
```

```java
public static int houseRobber(int[] nums) {
    if (nums.length == 1) return nums[0];
    int prev2 = 0, prev1 = 0;
    for (int num : nums) {
        int curr = Math.max(prev1, prev2 + num);
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
// [2,7,9,3,1] → 12 (2+9+1)
// [1,2,3,1]   → 4  (1+3)

// House Robber II — circular (first and last can't both be robbed)
public static int houseRobber2(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    // Case 1: rob houses 0..n-2  (exclude last)
    // Case 2: rob houses 1..n-1  (exclude first)
    return Math.max(robRange(nums, 0, n-2), robRange(nums, 1, n-1));
}
static int robRange(int[] nums, int start, int end) {
    int prev2 = 0, prev1 = 0;
    for (int i = start; i <= end; i++) {
        int curr = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1; prev1 = curr;
    }
    return prev1;
}
```

#### Pattern 2 — "Extend or Restart" (Maximum Subarray — Kadane's)
```
dp[i] = maximum subarray sum ending at index i

Recurrence: dp[i] = max(arr[i],           // Start fresh
                        dp[i-1] + arr[i]) // Extend previous subarray
```

```java
public static int maxSubarray(int[] nums) {
    int maxSum = nums[0], currentSum = nums[0];
    for (int i = 1; i < nums.length; i++) {
        currentSum = Math.max(nums[i], currentSum + nums[i]); // Extend or restart
        maxSum = Math.max(maxSum, currentSum);
    }
    return maxSum;
}
// [-2,1,-3,4,-1,2,1,-5,4] → 6 ([4,-1,2,1])

// Return actual subarray (not just sum)
public static int[] maxSubarrayWithIndices(int[] nums) {
    int maxSum = nums[0], currSum = nums[0];
    int start = 0, end = 0, tempStart = 0;
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] > currSum + nums[i]) { currSum = nums[i]; tempStart = i; }
        else currSum += nums[i];
        if (currSum > maxSum) { maxSum = currSum; start = tempStart; end = i; }
    }
    return Arrays.copyOfRange(nums, start, end + 1);
}
```

#### Pattern 3 — "Take or Skip With Constraint" (Jump Game DP)
```java
public static int minJumps(int[] nums) {
    int n = nums.length;
    int[] dp = new int[n];
    Arrays.fill(dp, Integer.MAX_VALUE);
    dp[0] = 0;
    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] != Integer.MAX_VALUE && j + nums[j] >= i) {
                dp[i] = Math.min(dp[i], dp[j] + 1);
            }
        }
    }
    return dp[n-1];
}
```

#### Pattern 4 — "Partition Into Segments" (Word Break)
```java
public static boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;                          // Empty string always valid
    for (int i = 1; i <= s.length(); i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    return dp[s.length()];
}
// "leetcode", ["leet","code"] → true
// "catsandog", ["cats","dog","sand","an","cat"] → false
```

#### Pattern 5 — "Count Distinct Ways" (Climbing Stairs / Coin Combos)
```java
// Climbing stairs: 1 or 2 steps at a time — how many ways to reach step n?
public static int climbStairs(int n) {
    if (n <= 2) return n;
    int a = 1, b = 2;
    for (int i = 3; i <= n; i++) { int c = a + b; a = b; b = c; }
    return b;
}
// n=5 → 8 ways

// Decode ways: how many ways to decode a digit string?
public static int decodeWays(String s) {
    int n = s.length();
    int[] dp = new int[n + 1];
    dp[0] = 1;
    dp[1] = s.charAt(0) == '0' ? 0 : 1;
    for (int i = 2; i <= n; i++) {
        int oneDigit = Integer.parseInt(s.substring(i-1, i));
        int twoDigit = Integer.parseInt(s.substring(i-2, i));
        if (oneDigit >= 1) dp[i] += dp[i-1];
        if (twoDigit >= 10 && twoDigit <= 26) dp[i] += dp[i-2];
    }
    return dp[n];
}
// "12" → 2 ("AB" or "L")
// "226" → 3 ("BZF", "VF", "BBF")
```

---

### 5.2 Key 2D DP Patterns

#### Pattern 1 — Grid Path (Unique Paths)
```java
public static int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    // First row and column: only one way to reach each cell
    for (int i = 0; i < m; i++) dp[i][0] = 1;
    for (int j = 0; j < n; j++) dp[0][j] = 1;

    for (int i = 1; i < m; i++)
        for (int j = 1; j < n; j++)
            dp[i][j] = dp[i-1][j] + dp[i][j-1]; // From above or left

    return dp[m-1][n-1];
}

// With obstacles
public static int uniquePathsObstacles(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    if (grid[0][0] == 1 || grid[m-1][n-1] == 1) return 0;
    int[][] dp = new int[m][n];
    dp[0][0] = 1;
    for (int i = 1; i < m; i++) dp[i][0] = grid[i][0] == 1 ? 0 : dp[i-1][0];
    for (int j = 1; j < n; j++) dp[0][j] = grid[0][j] == 1 ? 0 : dp[0][j-1];
    for (int i = 1; i < m; i++)
        for (int j = 1; j < n; j++)
            dp[i][j] = grid[i][j] == 1 ? 0 : dp[i-1][j] + dp[i][j-1];
    return dp[m-1][n-1];
}
```

#### Pattern 2 — String Comparison (LCS, Edit Distance)
```
dp[i][j] depends on dp[i-1][j-1], dp[i-1][j], dp[i][j-1]
Fill: row by row, left to right
Common problems: LCS, Edit Distance, Regex Matching, Wildcard Matching
```

#### Pattern 3 — Interval DP (Matrix Chain, Burst Balloons)
```java
// Burst Balloons (LC 312)
public static int maxBurst(int[] nums) {
    int n = nums.length;
    int[] arr = new int[n + 2];
    arr[0] = arr[n + 1] = 1;
    for (int i = 0; i < n; i++) arr[i + 1] = nums[i];
    int m = arr.length;
    int[][] dp = new int[m][m];

    for (int len = 2; len < m; len++) {
        for (int left = 0; left < m - len; left++) {
            int right = left + len;
            for (int k = left + 1; k < right; k++) {
                // k is the LAST balloon burst in range (left, right)
                dp[left][right] = Math.max(dp[left][right],
                    dp[left][k] + arr[left]*arr[k]*arr[right] + dp[k][right]);
            }
        }
    }
    return dp[0][m - 1];
}
// [3,1,5,8] → 167
```

#### Pattern 4 — DP on Trees
```java
// Diameter of binary tree using DP on subtrees
public static int diameterOfBinaryTree(TreeNode root) {
    int[] maxDiameter = {0};
    depthDP(root, maxDiameter);
    return maxDiameter[0];
}
private static int depthDP(TreeNode node, int[] maxDiameter) {
    if (node == null) return 0;
    int left  = depthDP(node.left,  maxDiameter);
    int right = depthDP(node.right, maxDiameter);
    maxDiameter[0] = Math.max(maxDiameter[0], left + right);
    return 1 + Math.max(left, right);
}
```

---

## 6. Optimization Strategies

### 6.1 Space Optimization — Rolling Arrays

```java
// LCS: O(m×n) → O(n) space by keeping only two rows
public static int lcsSpaceOpt(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[] prev = new int[n + 1];
    int[] curr = new int[n + 1];

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            curr[j] = s1.charAt(i-1) == s2.charAt(j-1)
                ? prev[j-1] + 1
                : Math.max(prev[j], curr[j-1]);
        }
        int[] temp = prev; prev = curr; curr = temp; // Swap rows
        Arrays.fill(curr, 0);
    }
    return prev[n];
}

// Edit distance: O(m×n) → O(n) space
public static int editDistanceOpt(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[] dp = new int[n + 1];
    for (int j = 0; j <= n; j++) dp[j] = j;

    for (int i = 1; i <= m; i++) {
        int prev = dp[0];
        dp[0] = i;
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];
            if (s1.charAt(i-1) == s2.charAt(j-1)) dp[j] = prev;
            else dp[j] = 1 + Math.min(prev, Math.min(dp[j], dp[j-1]));
            prev = temp;
        }
    }
    return dp[n];
}
```

### 6.2 State Compression (Bitmask DP)

```java
// Traveling Salesman Problem with bitmask DP
// dp[mask][i] = min cost to visit all cities in mask, ending at city i
public static int tsp(int[][] dist, int n) {
    int FULL = (1 << n) - 1;
    int[][] dp = new int[1 << n][n];
    for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE / 2);
    dp[1][0] = 0;   // Start at city 0, only city 0 visited

    for (int mask = 1; mask < (1 << n); mask++) {
        for (int u = 0; u < n; u++) {
            if ((mask & (1 << u)) == 0 || dp[mask][u] == Integer.MAX_VALUE/2) continue;
            for (int v = 0; v < n; v++) {
                if ((mask & (1 << v)) != 0) continue;  // Already visited
                int newMask = mask | (1 << v);
                dp[newMask][v] = Math.min(dp[newMask][v], dp[mask][u] + dist[u][v]);
            }
        }
    }
    // Return to start city 0
    int minCost = Integer.MAX_VALUE;
    for (int u = 1; u < n; u++)
        minCost = Math.min(minCost, dp[FULL][u] + dist[u][0]);
    return minCost;
}
// O(2^n × n²) — exponential but much better than n! brute force
```

### 6.3 DP on Digits (Digit DP)

```java
// Count numbers from 1 to n with specific digit properties
// Example: count numbers without digit 4 in [1, 1000]
public static int countWithoutFour(int n) {
    String s = String.valueOf(n);
    int len = s.length();
    // dp[pos][tight][started]
    // Use memoization with recursion
    Integer[][][] memo = new Integer[len][2][2];
    return digitDP(s, 0, true, false, memo);
}
private static int digitDP(String s, int pos, boolean tight, boolean started,
                             Integer[][][] memo) {
    if (pos == s.length()) return started ? 1 : 0;
    if (memo[pos][tight?1:0][started?1:0] != null)
        return memo[pos][tight?1:0][started?1:0];

    int limit = tight ? s.charAt(pos) - '0' : 9;
    int result = 0;
    for (int d = 0; d <= limit; d++) {
        if (d == 4) continue;   // Skip digit 4
        result += digitDP(s, pos+1, tight && d==limit, started || d>0, memo);
    }
    return memo[pos][tight?1:0][started?1:0] = result;
}
```

### 6.4 Divide and Conquer Optimization

```java
// When dp[i][j] = min over k of (dp[i-1][k] + cost[k+1][j])
// and opt(i,j) <= opt(i,j+1) (monotone optimal split point)
// → O(n² log n) → O(n²) using the SMAWK algorithm or D&C opt

// Example: Optimal BST cost — O(n³) → O(n²) with D&C opt
// (Illustration of the pattern; full SMAWK out of scope here)
public static int optimalBST(int[] freq) {
    int n = freq.length;
    int[] prefix = new int[n + 1];
    for (int i = 0; i < n; i++) prefix[i+1] = prefix[i] + freq[i];
    int[][] dp = new int[n + 2][n + 1];

    for (int len = 1; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            dp[i][j] = Integer.MAX_VALUE;
            int rangeSum = prefix[j+1] - prefix[i];
            for (int r = i; r <= j; r++) {
                int left  = r > i ? dp[i][r-1] : 0;
                int right = r < j ? dp[r+1][j] : 0;
                dp[i][j] = Math.min(dp[i][j], left + right + rangeSum);
            }
        }
    }
    return dp[0][n-1];
}
```

### 6.5 Memoization vs Tabulation Trade-offs

```
Memoization:
  Pro:  Only computes needed subproblems
  Pro:  Natural recursive structure
  Con:  Recursion stack overhead (O(n) stack frames)
  Con:  Function call overhead per subproblem
  Con:  HashMap overhead if using string/complex keys

Tabulation:
  Pro:  No recursion overhead
  Pro:  Cache-friendly (sequential array access)
  Pro:  Space optimization via rolling array
  Pro:  Avoids stack overflow for large n
  Con:  Must solve ALL subproblems even if not needed
  Con:  Less intuitive for complex state spaces

Rule of thumb:
  → Start with memoization to verify correctness
  → Convert to tabulation for performance/space optimization
```

---

## 7. Interview Problem Solving

### 7.1 The DP Interview Framework

```
Step 1: RECOGNIZE DP
  - Optimization problem (min/max)?
  - Counting problem (how many ways)?
  - Feasibility problem (is it possible)?
  - Contains "optimal substructure" + "overlapping subproblems"?

Step 2: DEFINE STATE
  - "dp[i] = ..." or "dp[i][j] = ..."
  - The state captures ALL information needed to solve from this point
  - Smaller state = better; start with simplest that works

Step 3: WRITE RECURRENCE
  - How does dp[i] relate to smaller subproblems?
  - Check every possible "last decision" made

Step 4: BASE CASES
  - What's the answer for the smallest valid input?
  - Usually dp[0] = 0 or dp[0] = 1

Step 5: COMPUTATION ORDER
  - Ensure dp[i] is computed before it's needed by dp[i+k]
  - For 2D: typically row by row

Step 6: EXTRACT ANSWER
  - Usually dp[n] or dp[n][m] or max/min over dp[n][*]

Step 7: OPTIMIZE SPACE
  - Can we use rolling array (1D instead of 2D)?
  - Do we only need last row/column?
```

### 7.2 Must-Know Interview Problems

#### Stock Problems Family
```java
// Best Time to Buy/Sell Stock I — one transaction
public static int maxProfitI(int[] prices) {
    int minPrice = Integer.MAX_VALUE, maxProfit = 0;
    for (int price : prices) {
        minPrice   = Math.min(minPrice, price);
        maxProfit  = Math.max(maxProfit, price - minPrice);
    }
    return maxProfit;
}

// Best Time to Buy/Sell Stock II — unlimited transactions
public static int maxProfitII(int[] prices) {
    int profit = 0;
    for (int i = 1; i < prices.length; i++)
        if (prices[i] > prices[i-1]) profit += prices[i] - prices[i-1];
    return profit;
}

// Best Time to Buy/Sell Stock III — at most 2 transactions
public static int maxProfitIII(int[] prices) {
    int buy1 = Integer.MIN_VALUE, sell1 = 0;
    int buy2 = Integer.MIN_VALUE, sell2 = 0;
    for (int price : prices) {
        buy1  = Math.max(buy1,  -price);          // Best buy for txn 1
        sell1 = Math.max(sell1, buy1 + price);    // Best sell for txn 1
        buy2  = Math.max(buy2,  sell1 - price);   // Best buy for txn 2
        sell2 = Math.max(sell2, buy2 + price);    // Best sell for txn 2
    }
    return sell2;
}

// Best Time to Buy/Sell Stock IV — at most k transactions
public static int maxProfitIV(int k, int[] prices) {
    int n = prices.length;
    if (k >= n / 2) return maxProfitII(prices);  // Unlimited effectively
    int[] buy = new int[k]; int[] sell = new int[k];
    Arrays.fill(buy, Integer.MIN_VALUE);
    for (int price : prices) {
        for (int i = 0; i < k; i++) {
            buy[i]  = Math.max(buy[i],  (i == 0 ? 0 : sell[i-1]) - price);
            sell[i] = Math.max(sell[i], buy[i] + price);
        }
    }
    return sell[k-1];
}
```

#### Palindrome Problems Family
```java
// Longest Palindromic Subsequence
public static int longestPalinSubseq(String s) {
    String rev = new StringBuilder(s).reverse().toString();
    return lcs(s, rev);  // LCS of s and reverse(s)
}

// Longest Palindromic Substring
public static String longestPalinSubstr(String s) {
    int n = s.length(), start = 0, maxLen = 1;
    boolean[][] dp = new boolean[n][n];
    for (int i = 0; i < n; i++) dp[i][i] = true;
    for (int i = 0; i < n - 1; i++) {
        if (s.charAt(i) == s.charAt(i+1)) {
            dp[i][i+1] = true; start = i; maxLen = 2;
        }
    }
    for (int len = 3; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            if (s.charAt(i) == s.charAt(j) && dp[i+1][j-1]) {
                dp[i][j] = true;
                if (len > maxLen) { maxLen = len; start = i; }
            }
        }
    }
    return s.substring(start, start + maxLen);
}

// Minimum insertions to make string palindrome
public static int minInsertionsPalin(String s) {
    int n = s.length();
    return n - longestPalinSubseq(s);  // Insert n - LPS characters
}
```

#### Interval DP Problems
```java
// Minimum cost to cut a stick (LC 1547)
public static int minCostCut(int n, int[] cuts) {
    int[] arr = new int[cuts.length + 2];
    arr[0] = 0; arr[arr.length-1] = n;
    System.arraycopy(cuts, 0, arr, 1, cuts.length);
    Arrays.sort(arr);
    int m = arr.length;
    int[][] dp = new int[m][m];

    for (int len = 2; len < m; len++) {
        for (int i = 0; i + len < m; i++) {
            int j = i + len;
            dp[i][j] = Integer.MAX_VALUE;
            for (int k = i + 1; k < j; k++) {
                dp[i][j] = Math.min(dp[i][j],
                    dp[i][k] + dp[k][j] + arr[j] - arr[i]);
            }
        }
    }
    return dp[0][m-1];
}
```

### 7.3 State Machine DP

```java
// Regular expression matching (LC 10)
public static boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m+1][n+1];
    dp[0][0] = true;
    // Handle patterns like a*, a*b*, a*b*c* that can match empty string
    for (int j = 2; j <= n; j += 2)
        dp[0][j] = dp[0][j-2] && p.charAt(j-1) == '*';

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j-1) == '*') {
                dp[i][j] = dp[i][j-2]  // Zero occurrences of preceding char
                    || (dp[i-1][j] && (p.charAt(j-2) == s.charAt(i-1)
                                       || p.charAt(j-2) == '.'));
            } else {
                dp[i][j] = dp[i-1][j-1]
                    && (p.charAt(j-1) == s.charAt(i-1) || p.charAt(j-1) == '.');
            }
        }
    }
    return dp[m][n];
}
```

### 7.4 DP with Multiple Dimensions

```java
// Cherry Pickup (LC 741) — two robots, one grid, maximize cherries
// dp[r1][c1][c2] = max cherries when robot1 at (r1,c1), robot2 at (r2,c2)
// where r2 = r1 + c1 - c2 (they move same number of steps)
public static int cherryPickup(int[][] grid) {
    int n = grid.length;
    // Simulate two people going from (0,0) to (n-1,n-1) simultaneously
    int[][][] dp = new int[n][n][n];
    for (int[][] d2 : dp) for (int[] d1 : d2) Arrays.fill(d1, Integer.MIN_VALUE);
    dp[0][0][0] = grid[0][0];

    for (int step = 1; step <= 2*(n-1); step++) {
        int[][][] ndp = new int[n][n][n];
        for (int[][] d2 : ndp) for (int[] d1 : d2) Arrays.fill(d1, Integer.MIN_VALUE);
        for (int r1 = Math.max(0,step-(n-1)); r1 <= Math.min(n-1,step); r1++) {
            for (int r2 = r1; r2 <= Math.min(n-1,step); r2++) {
                int c1 = step - r1, c2 = step - r2;
                if (c1 >= n || c2 >= n) continue;
                if (grid[r1][c1] < 0 || grid[r2][c2] < 0) continue;
                int cherries = grid[r1][c1] + (r1 != r2 ? grid[r2][c2] : 0);
                int best = Integer.MIN_VALUE;
                for (int pr1 : new int[]{r1-1,r1}) {
                    for (int pr2 : new int[]{r2-1,r2}) {
                        int pc1 = step-1-pr1, pc2 = step-1-pr2;
                        if (pr1>=0&&pc1>=0&&pc1<n&&pr2>=0&&pc2>=0&&pc2<n)
                            best = Math.max(best, dp[pr1][pr2][r2]);
                    }
                }
                if (best != Integer.MIN_VALUE) ndp[r1][r2][r2] = best + cherries;
            }
        }
        dp = ndp;
    }
    return Math.max(0, dp[n-1][n-1][n-1]);
}
```

### 7.5 Common DP Patterns Quick Reference

```
Pattern                 State Definition          Recurrence
──────────────────────────────────────────────────────────────────
Linear DP              dp[i]                     dp[i] = f(dp[i-1])
Two-sequence DP        dp[i][j]                  match or skip
Knapsack               dp[i][w]                  take or leave item
Interval DP            dp[i][j]                  split at k
Tree DP                dp[node]                  children results
Bitmask DP             dp[mask]                  toggle bits
Digit DP               dp[pos][tight]            digit choices
State Machine DP       dp[i][state]              state transitions
Grid DP                dp[i][j]                  from neighbors
Partition DP           dp[i][k]                  k partitions of i
```

### 7.6 Complexity Summary

| Problem | Time | Space | Optimized Space |
|---|---|---|---|
| Fibonacci | O(n) | O(n) | O(1) |
| Coin Change | O(n × amount) | O(amount) | O(amount) |
| 0/1 Knapsack | O(n × W) | O(n × W) | O(W) |
| LCS | O(m × n) | O(m × n) | O(n) |
| Edit Distance | O(m × n) | O(m × n) | O(n) |
| LIS (DP) | O(n²) | O(n) | O(n) |
| LIS (binary) | O(n log n) | O(n) | O(n) |
| Matrix Chain | O(n³) | O(n²) | O(n²) |
| TSP (bitmask) | O(2^n × n²) | O(2^n × n) | — |
| Subset Sum | O(n × target) | O(target) | O(target) |
| Max Subarray | O(n) | O(1) | O(1) |
| Unique Paths | O(m × n) | O(m × n) | O(n) |
| Burst Balloons | O(n³) | O(n²) | O(n²) |

---

## Summary

### The DP Blueprint
```
1. RECOGNIZE: Does it have optimal substructure + overlapping subproblems?
2. DEFINE:    dp[i] = "maximum/minimum/count of X considering first i elements"
3. RECURRENCE: dp[i] = f(dp[i-1], dp[i-2], ...)
4. BASE CASE:  dp[0] = known answer for empty/trivial input
5. ORDER:      Fill dp[] such that dp[i] depends only on already-filled values
6. ANSWER:     Usually dp[n] or max(dp[*])
7. OPTIMIZE:   Can we reduce 2D to 1D? Can we use O(1) variables?
```

### The Most Important Insight
```
DP converts EXPONENTIAL brute force into POLYNOMIAL computation
by storing and reusing subproblem results.

The key question isn't "what algorithm to use?"
It's "what is the right subproblem definition?"

Once dp[i] is defined correctly,
the recurrence usually follows naturally from the problem structure.
The subproblem definition IS the invention.
```
