# Greedy Algorithms — Complete Guide
> Local Optimization → Global Optimization?
> Covers: What is Greedy · When Does It Work · Greedy vs DP · Classic Problems · Real-World Applications · Complexity Analysis · Interview Strategies

---

## Table of Contents
1. [What is Greedy?](#1-what-is-greedy)
2. [When Does Greedy Work?](#2-when-does-greedy-work)
3. [Greedy vs Dynamic Programming](#3-greedy-vs-dynamic-programming)
4. [Classic Greedy Problems](#4-classic-greedy-problems)
5. [Real-World Applications](#5-real-world-applications)
6. [Complexity Analysis](#6-complexity-analysis)
7. [Interview Strategies](#7-interview-strategies)

---

## 1. What is Greedy?

### Core Idea
A greedy algorithm makes the **locally optimal choice at each step** with the hope that these local choices lead to a **globally optimal solution**.

```
At every decision point:
  → Pick the option that looks BEST RIGHT NOW
  → Never look back, never reconsider
  → Trust that local best → global best
```

### The Greedy Mindset
```
Problem: Travel from city A to city Z
Greedy:  At each intersection, take the road that looks shortest
         Never backtrack. Never re-evaluate past choices.

This works for Dijkstra's shortest path (with non-negative weights).
This FAILS for general shortest path with negative weights.
```

### Key Properties of Greedy Algorithms
```
1. Greedy Choice Property:
   A globally optimal solution can be reached by making locally optimal choices.
   "The best local choice is always part of some globally optimal solution."

2. Optimal Substructure:
   An optimal solution to the problem contains optimal solutions to subproblems.
   (Shared with Dynamic Programming)

3. No backtracking:
   Once a choice is made, it is never reconsidered.
   (Unlike Backtracking which explores and undoes choices)
```

### Greedy vs Other Paradigms

| Paradigm | Strategy | Reconsiders? | Example |
|---|---|---|---|
| Brute Force | Try everything | Yes, exhaustively | Traveling Salesman exact |
| Backtracking | Try + undo | Yes, on failure | N-Queens, Sudoku |
| Dynamic Programming | Store subproblem results | No backtrack, full exploration | Knapsack 0/1 |
| Greedy | Best local choice now | Never | Huffman, Activity Selection |
| Divide & Conquer | Split + combine | No | Merge Sort, FFT |

### Real-World Analogy
```
Coin Change (US coins: 25¢, 10¢, 5¢, 1¢):
  Make change for 41¢

  Greedy:
    Take largest coin ≤ remaining:
    41¢ → take 25¢ → 16¢ remaining
    16¢ → take 10¢ →  6¢ remaining
     6¢ → take  5¢ →  1¢ remaining
     1¢ → take  1¢ →  0¢ done
    Result: 4 coins [25, 10, 5, 1] ✓ OPTIMAL for US coins

  But for coins {1, 3, 4}, change for 6:
    Greedy: 4 + 1 + 1 = 3 coins
    Optimal: 3 + 3   = 2 coins ← Greedy FAILS here
```

---

## 2. When Does Greedy Work?

### The Two Conditions (Both Must Hold)

#### Condition 1: Greedy Choice Property
```
The globally optimal solution can always include the locally optimal choice.

Proof technique: Exchange argument
  Assume an optimal solution S* does NOT include the greedy choice g.
  Show you can SWAP something in S* with g and get a solution at least as good.
  Therefore, there EXISTS an optimal solution that includes g.
  → Greedy choice is safe.
```

#### Condition 2: Optimal Substructure
```
After making the greedy choice, the remaining subproblem must also
have an optimal solution that combines with the greedy choice to
give a globally optimal solution.
```

### Exchange Argument — Proof Template
```
Theorem: Greedy algorithm produces an optimal solution.

Proof by exchange argument:
1. Let G = greedy solution, O = any optimal solution
2. Find the first point where G and O differ
3. Show swapping O's choice with G's choice at that point
   produces a solution O' that is at least as good as O
4. Repeat until O' = G (or O is no better than G)
5. Conclude: G is optimal
```

### Problems Where Greedy Works

```
✓ Activity Selection (Interval Scheduling)  — proven by exchange argument
✓ Fractional Knapsack                       — take highest value/weight ratio
✓ Huffman Encoding                           — build tree from least frequent
✓ Dijkstra's Shortest Path                  — always expand closest unvisited
✓ Prim's MST / Kruskal's MST               — always add cheapest safe edge
✓ Job Scheduling (minimize lateness)         — sort by deadline
✓ Coin Change (canonical coin systems)       — take largest fitting coin
✓ Gas Station                               — start at surplus station
```

### Problems Where Greedy FAILS

```
✗ 0/1 Knapsack        — must consider ALL combinations; fractional version is greedy
✗ Coin Change (arbitrary coins) — {1,3,4} for 6: greedy gives 3 coins, DP gives 2
✗ Traveling Salesman  — nearest neighbor heuristic is NOT optimal
✗ Longest Path        — greedy doesn't work; shortest path does
✗ Matrix Chain Mult.  — local split choice doesn't optimize globally
✗ Edit Distance       — local character matching doesn't minimize total edits
```

### The Greedy Proof Checklist
```
Before claiming greedy works, verify:
  □ Can you prove greedy choice property? (exchange argument)
  □ Does optimal substructure hold?
  □ Is there a counterexample? (try small cases first)
  □ Does the problem have the "staying ahead" property?
    (greedy solution is always ≥ as good as any other at each step)
```

---

## 3. Greedy vs Dynamic Programming

### The Fundamental Difference

```
DYNAMIC PROGRAMMING:              GREEDY:
─────────────────────────         ─────────────────────────
Explores ALL subproblems          Makes ONE choice per step
Stores results (memoization)      No storage of past states
Bottom-up or top-down             Always forward only
Correct for overlapping           Correct only when greedy
  subproblems                       choice property holds
O(n²) or O(n³) typical           O(n log n) typical
Fibonacci, Knapsack 0/1           Huffman, Activity Selection
```

### Side-by-Side: Coin Change

```java
// Problem: Make change for amount using given coins
// Coins: {1, 5, 6, 9}  Amount: 11

// GREEDY (wrong for this coin system):
// 9 + 1 + 1 = 3 coins

// DP (optimal):
// 6 + 5 = 2 coins

// GREEDY APPROACH:
public static int coinChangeGreedy(int[] coins, int amount) {
    Arrays.sort(coins);
    int count = 0;
    for (int i = coins.length - 1; i >= 0 && amount > 0; i--) {
        count += amount / coins[i];    // Take as many of largest coin as possible
        amount %= coins[i];            // Remainder
    }
    return amount == 0 ? count : -1;
}

// DP APPROACH (always correct):
public static int coinChangeDP(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);       // Initialize to "infinity"
    dp[0] = 0;
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
```

### Side-by-Side: Knapsack

```java
// 0/1 Knapsack: items {weight, value} = {(2,6),(2,10),(3,12)}, capacity=5

// GREEDY (by value/weight ratio) — WRONG for 0/1:
// Item 2: ratio=5.0  take it  → weight=2, value=10
// Item 3: ratio=4.0  take it  → weight=5, value=22
// But item 3 doesn't fit! → weight=2, value=10

// Item 1: ratio=3.0  take it  → weight=4, value=16
// weight=5 limit → take item 2 (ratio 5.0) + item 1 (ratio 3.0)?
// → weight=4, value=16 ← suboptimal

// OPTIMAL (DP): item 2 + item 3 = weight 5, value 22

// FRACTIONAL KNAPSACK: Greedy IS optimal
// → Sort by value/weight ratio, take fractionally
public static double fractionalKnapsack(int[][] items, int capacity) {
    // items[i] = {weight, value}
    Arrays.sort(items, (a, b) -> Double.compare(
        (double)b[1]/b[0], (double)a[1]/a[0]));   // Sort by ratio DESC
    double totalValue = 0;
    for (int[] item : items) {
        if (capacity <= 0) break;
        if (item[0] <= capacity) {
            totalValue += item[1];                  // Take whole item
            capacity -= item[0];
        } else {
            totalValue += (double) item[1] * capacity / item[0]; // Take fraction
            capacity = 0;
        }
    }
    return totalValue;
}
```

### When to Use Which

```
Use GREEDY when:
  ✓ Greedy choice property is provable
  ✓ You can prove by exchange argument
  ✓ Problem involves intervals, scheduling, or spanning trees
  ✓ Fractional quantities are allowed
  ✓ You need O(n log n) solution

Use DP when:
  ✓ Greedy fails (counterexample exists)
  ✓ Problem has overlapping subproblems
  ✓ 0/1 decisions (can't take fractions)
  ✓ Multiple constraints simultaneously
  ✓ Correctness is more important than speed
```

---

## 4. Classic Greedy Problems

### 4.1 Activity Selection (Interval Scheduling)

#### Problem
Given n activities with start and end times, select the maximum number of non-overlapping activities.

#### Greedy Strategy
**Sort by end time, then always pick the earliest-ending activity that doesn't conflict.**

#### Why This Works — Exchange Argument
```
Claim: The activity with the earliest finish time is always in some optimal solution.

Proof: Let O = optimal solution. Let g = greedy's first choice (earliest end).
  If g ∈ O → done.
  If g ∉ O → let a = O's first activity (end time ≥ g's end time)
  Replace a with g in O → g ends earlier → g conflicts with nothing a didn't
  New solution O' has same size as O → O' is optimal and contains g ✓
```

```java
public static List<int[]> activitySelection(int[][] activities) {
    // Sort by end time
    Arrays.sort(activities, (a, b) -> a[1] - b[1]);
    List<int[]> selected = new ArrayList<>();
    int lastEnd = Integer.MIN_VALUE;

    for (int[] activity : activities) {
        if (activity[0] >= lastEnd) {          // No conflict with last selected
            selected.add(activity);
            lastEnd = activity[1];             // Update last end time
        }
    }
    return selected;
}
// Input:  [(1,4),(3,5),(0,6),(5,7),(3,9),(5,9),(6,10),(8,11),(8,12),(2,14),(12,16)]
// Output: [(1,4),(5,7),(8,11),(12,16)] — 4 activities
```

#### Step-by-Step Trace
```
Activities (sorted by end): [(1,4),(3,5),(0,6),(5,7),...]
  Last end = -∞

  Activity (1,4):  start=1 ≥ -∞   → SELECT, lastEnd=4
  Activity (3,5):  start=3 ≥ 4?  No → SKIP
  Activity (0,6):  start=0 ≥ 4?  No → SKIP
  Activity (5,7):  start=5 ≥ 4?  Yes → SELECT, lastEnd=7
  Activity (8,11): start=8 ≥ 7?  Yes → SELECT, lastEnd=11
  Activity (12,16):start=12≥ 11? Yes → SELECT, lastEnd=16

  Result: 4 non-overlapping activities ✓
```

---

### 4.2 Job Scheduling to Minimize Lateness

#### Problem
Schedule n jobs on one machine. Job i has processing time tᵢ and deadline dᵢ. Minimize the maximum lateness (max(0, finish_time - deadline)).

#### Greedy Strategy: Earliest Deadline First (EDF)

```java
public static int minimizeMaxLateness(int[][] jobs) {
    // jobs[i] = {processingTime, deadline}
    Arrays.sort(jobs, (a, b) -> a[1] - b[1]);  // Sort by deadline ASC

    int currentTime = 0, maxLateness = 0;
    for (int[] job : jobs) {
        currentTime += job[0];                   // Finish time
        int lateness = Math.max(0, currentTime - job[1]);
        maxLateness = Math.max(maxLateness, lateness);
        System.out.printf("  Job (t=%d,d=%d): finish=%d lateness=%d%n",
            job[0], job[1], currentTime, lateness);
    }
    return maxLateness;
}
// jobs = [(1,3),(2,5),(3,4),(2,6)] → sorted by deadline [(1,3),(3,4),(2,5),(2,6)]
// Max lateness = 1
```

---

### 4.3 Fractional Knapsack

```java
public static double fractionalKnapsack(int capacity, int[] weights, int[] values) {
    int n = weights.length;
    Integer[] idx = new Integer[n];
    for (int i = 0; i < n; i++) idx[i] = i;

    // Sort items by value/weight ratio DESC
    Arrays.sort(idx, (a, b) -> Double.compare(
        (double) values[b] / weights[b],
        (double) values[a] / weights[a]));

    double totalValue = 0;
    int remaining = capacity;

    for (int i : idx) {
        if (remaining == 0) break;
        int take = Math.min(weights[i], remaining);    // Take as much as possible
        totalValue += (double) values[i] * take / weights[i];
        remaining -= take;
        System.out.printf("  Item %d (w=%d,v=%d,ratio=%.2f): take %d → value+=%.2f%n",
            i, weights[i], values[i], (double)values[i]/weights[i], take,
            (double)values[i]*take/weights[i]);
    }
    return totalValue;
}
```

---

### 4.4 Huffman Encoding

#### Problem
Given character frequencies, build an optimal prefix-free binary code that minimizes total encoded length.

#### Greedy Strategy
**Always merge the two nodes with the lowest frequency.**

#### Why It Works
```
Each time we combine two subtrees, we add their combined weight to total cost.
Combining the two SMALLEST weights first minimizes the cost at every step.
Proof: Exchange argument shows swapping any two nodes doesn't improve cost.

Total bits = Σ (frequency[c] × depth[c])
Huffman minimizes this over all possible prefix-free codes.
```

#### Huffman Tree Construction
```
Characters: {a:5, b:9, c:12, d:13, e:16, f:45}

Step 1: MinHeap = [5(a), 9(b), 12(c), 13(d), 16(e), 45(f)]

Step 2: Extract 5(a) + 9(b) → create node 14, heap = [12,13,14,16,45]
Step 3: Extract 12(c) + 13(d) → create node 25, heap = [14,16,25,45]
Step 4: Extract 14 + 16(e) → create node 30, heap = [25,30,45]
Step 5: Extract 25 + 30 → create node 55, heap = [45,55]
Step 6: Extract 45(f) + 55 → create root 100

Tree:
        100
       /    \
      55     f(45)
     /  \
   25   30
  / \  /  \
 c  d 14  e(16)
    / \
   a(5) b(9)

Codes: f=0, c=100, d=101, a=1100, b=1101, e=111
```

```java
public static Map<Character, String> huffmanEncoding(Map<Character, Integer> freq) {
    PriorityQueue<HuffNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
    for (Map.Entry<Character, Integer> e : freq.entrySet())
        pq.offer(new HuffNode(e.getKey(), e.getValue()));

    while (pq.size() > 1) {
        HuffNode left  = pq.poll();    // Smallest freq
        HuffNode right = pq.poll();    // Second smallest
        HuffNode merged = new HuffNode('\0', left.freq + right.freq);
        merged.left = left;
        merged.right = right;
        pq.offer(merged);
    }

    Map<Character, String> codes = new HashMap<>();
    buildCodes(pq.poll(), "", codes);
    return codes;
}

private static void buildCodes(HuffNode node, String code, Map<Character, String> codes) {
    if (node == null) return;
    if (node.left == null && node.right == null) {
        codes.put(node.ch, code.isEmpty() ? "0" : code);
        return;
    }
    buildCodes(node.left,  code + "0", codes);
    buildCodes(node.right, code + "1", codes);
}

static class HuffNode {
    char ch; int freq; HuffNode left, right;
    HuffNode(char ch, int freq) { this.ch = ch; this.freq = freq; }
}
```

---

### 4.5 Gas Station (Circular Route)

#### Problem
N gas stations in a circle. Station i has `gas[i]` fuel and costs `cost[i]` to reach station i+1. Find the starting station to complete the circuit, or return -1.

#### Greedy Strategy
```
Key Insight 1: If total gas >= total cost, a solution always exists.
Key Insight 2: If you run out of gas at station k starting from station s,
               then no station between s and k can be the answer.
               → Skip ahead and start from k+1.
```

```java
public static int canCompleteCircuit(int[] gas, int[] cost) {
    int totalSurplus = 0, currentSurplus = 0, startStation = 0;

    for (int i = 0; i < gas.length; i++) {
        int net = gas[i] - cost[i];
        totalSurplus   += net;
        currentSurplus += net;

        if (currentSurplus < 0) {       // Can't reach next station from startStation
            startStation   = i + 1;     // Try starting from next station
            currentSurplus = 0;         // Reset tank
        }
    }
    return totalSurplus >= 0 ? startStation : -1;
}
// gas=[1,2,3,4,5] cost=[3,4,5,1,2] → returns 3 (start at station index 3)
```

#### Why This Works
```
If running from s to k produces negative sum:
  - Any station between s and k would ALSO fail at k
    (they start with less accumulated gas than s)
  - So we can safely skip all stations up to k
  - Try k+1 as the new start

If total gas >= total cost:
  - Exactly one valid starting point exists
  - The greedy skip finds it in O(n)
```

---

### 4.6 Jump Game

#### Problem: Can You Reach the Last Index?
```java
public static boolean canJump(int[] nums) {
    int maxReach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false;              // Can't reach index i
        maxReach = Math.max(maxReach, i + nums[i]);  // Update furthest reachable
    }
    return true;
}
// [2,3,1,1,4] → true  (0→1→4)
// [3,2,1,0,4] → false (stuck at index 3, maxReach never exceeds 3)
```

#### Problem: Minimum Jumps to Reach End
```java
public static int jumpMinimum(int[] nums) {
    int jumps = 0, currentEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);    // Extend farthest reachable
        if (i == currentEnd) {                          // Reached end of current jump
            jumps++;
            currentEnd = farthest;                      // Make the jump
            if (currentEnd >= nums.length - 1) break;  // Reached end
        }
    }
    return jumps;
}
// [2,3,1,1,4] → 2 (jump from 0→1, then 1→4)
// [2,3,0,1,4] → 2
```

---

### 4.7 Meeting Rooms — Minimum Rooms Required

```java
// Minimum meeting rooms needed (all meetings must run simultaneously if overlapping)
public static int minMeetingRooms(int[][] intervals) {
    int n = intervals.length;
    int[] starts = new int[n];
    int[] ends   = new int[n];
    for (int i = 0; i < n; i++) {
        starts[i] = intervals[i][0];
        ends[i]   = intervals[i][1];
    }
    Arrays.sort(starts);
    Arrays.sort(ends);

    int rooms = 0, maxRooms = 0, endPtr = 0;
    for (int i = 0; i < n; i++) {
        if (starts[i] < ends[endPtr]) {
            rooms++;                        // New meeting starts before any ends → need room
        } else {
            endPtr++;                       // A meeting ended → reuse its room
        }
        maxRooms = Math.max(maxRooms, rooms);
    }
    return maxRooms;
}
// [(0,30),(5,10),(15,20)] → 2 rooms
```

---

### 4.8 Assign Cookies

```java
// Greedily assign cookies to children (satisfy as many as possible)
public static int findContentChildren(int[] greed, int[] cookies) {
    Arrays.sort(greed);    // Children sorted by greed factor ASC
    Arrays.sort(cookies);  // Cookies sorted by size ASC

    int child = 0, cookie = 0;
    while (child < greed.length && cookie < cookies.length) {
        if (cookies[cookie] >= greed[child]) {
            child++;        // This cookie satisfies this child → move both
        }
        cookie++;           // Try next cookie either way
    }
    return child;   // Number of content children
}
// greed=[1,2,3] cookies=[1,1] → 1
// greed=[1,2] cookies=[1,2,3] → 2
```

---

### 4.9 Task Scheduler

```java
// Minimum time to execute all tasks with cooldown n between same tasks
public static int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    Arrays.sort(freq);

    int maxFreq = freq[25];          // Most frequent task
    int maxCount = 0;
    for (int f : freq) if (f == maxFreq) maxCount++;

    // Formula: max(tasks.length, (maxFreq-1)*(n+1) + maxCount)
    return Math.max(tasks.length, (maxFreq - 1) * (n + 1) + maxCount);
}
// tasks=[A,A,A,B,B,B] n=2 → 8 (ABXABXAB where X=idle)
// tasks=[A,A,A,B,B,B,C,C,C] n=2 → 9 (ABCABCABC, no idle needed)
```

---

### 4.10 Minimum Spanning Tree — Kruskal's Algorithm

#### Problem
Connect all nodes in a weighted graph with minimum total edge weight.

#### Greedy Strategy
**Sort edges by weight, add edge if it doesn't create a cycle (using Union-Find).**

```java
public static int kruskalMST(int[][] edges, int n) {
    // edges[i] = {weight, u, v}
    Arrays.sort(edges, (a, b) -> a[0] - b[0]);  // Sort by weight ASC

    int[] parent = new int[n];
    int[] rank   = new int[n];
    for (int i = 0; i < n; i++) parent[i] = i;

    int totalWeight = 0, edgesAdded = 0;
    for (int[] edge : edges) {
        int w = edge[0], u = edge[1], v = edge[2];
        int pu = find(parent, u), pv = find(parent, v);
        if (pu != pv) {                          // No cycle → add edge
            union(parent, rank, pu, pv);
            totalWeight += w;
            edgesAdded++;
            System.out.printf("  Add edge (%d-%d, weight=%d)%n", u, v, w);
            if (edgesAdded == n - 1) break;      // MST complete
        }
    }
    return totalWeight;
}

static int find(int[] parent, int x) {
    if (parent[x] != x) parent[x] = find(parent, parent[x]); // Path compression
    return parent[x];
}

static void union(int[] parent, int[] rank, int a, int b) {
    if (rank[a] < rank[b]) { int t = a; a = b; b = t; }
    parent[b] = a;
    if (rank[a] == rank[b]) rank[a]++;
}
```

---

### 4.11 Dijkstra's Shortest Path

#### Greedy Strategy
**Always expand the unvisited node with the smallest known distance.**

```java
public static int[] dijkstra(int[][] graph, int src) {
    int n = graph.length;
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{src, 0});

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int u = curr[0], d = curr[1];
        if (d > dist[u]) continue;     // Stale entry — skip

        for (int v = 0; v < n; v++) {
            if (graph[u][v] > 0) {     // Edge exists
                int newDist = dist[u] + graph[u][v];
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{v, newDist});
                }
            }
        }
    }
    return dist;
}
// Greedy: always pick closest unvisited → proves optimal with non-negative weights
```

---

### 4.12 Largest Number

```java
// Arrange numbers to form the largest possible number
public static String largestNumber(int[] nums) {
    String[] strs = new String[nums.length];
    for (int i = 0; i < nums.length; i++) strs[i] = String.valueOf(nums[i]);

    // Custom comparator: prefer a+b over b+a if a+b > b+a numerically
    Arrays.sort(strs, (a, b) -> (b + a).compareTo(a + b));

    if (strs[0].equals("0")) return "0"; // All zeros
    return String.join("", strs);
}
// [3,30,34,5,9] → "9534330"
// [10,2] → "210"
// [1,20,23,4,8] → "8423201"
```

#### Why This Comparator Works (Greedy Proof)
```
Claim: If a+b > b+a (lexicographically), then a should come before b.

Proof:
  If a+b > b+a then string representation of (a×10^|b| + b) > (b×10^|a| + a)
  So placing a before b always gives a larger number.
  The comparator defines a total order → sorting by it gives globally optimal arrangement.
```

---

### 4.13 Partition Labels

```java
// Partition string into max parts where each letter appears in only one part
public static List<Integer> partitionLabels(String s) {
    int[] last = new int[26];
    for (int i = 0; i < s.length(); i++)
        last[s.charAt(i) - 'a'] = i;              // Last occurrence of each char

    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, last[s.charAt(i) - 'a']); // Extend current partition
        if (i == end) {                                 // Current partition complete
            result.add(end - start + 1);
            start = i + 1;
        }
    }
    return result;
}
// "ababcbacadefegdehijhklij" → [9,7,8]
// Partitions: "ababcbaca" | "defegde" | "hijhklij"
```

---

## 5. Real-World Applications

### 5.1 Data Compression — Huffman Coding
```
Used in: ZIP, JPEG, MP3, DEFLATE, HTTP/2 header compression (HPACK)

How:
  - Frequent characters get shorter codes (e.g., 'e' → 010)
  - Rare characters get longer codes (e.g., 'z' → 10110110)
  - Prefix-free: no code is a prefix of another → unambiguous decoding
  - Optimal: minimizes expected bits per character

Real impact:
  English text compressed ~40% with Huffman alone
  JPEG uses Huffman for the DCT coefficient entropy coding stage
```

### 5.2 Network Routing — Dijkstra / OSPF
```
Internet routing protocols:
  OSPF (Open Shortest Path First) — uses Dijkstra internally
  Each router maintains a graph of the network
  Greedy expansion finds shortest paths to all destinations
  Routing table = precomputed Dijkstra result from each router

Why greedy works: Edge weights (latency) are non-negative
                  Negative latency is physically impossible
```

### 5.3 Operating Systems — CPU Scheduling
```
Shortest Job First (SJF) — greedy minimizes average waiting time
  Sort jobs by burst time → execute shortest first
  Proven optimal for minimizing average turnaround time

Earliest Deadline First (EDF) — greedy for real-time systems
  Sort by deadline → execute closest deadline first
  Proven optimal for meeting all deadlines if feasible

Priority Scheduling:
  Always execute highest priority process
  Greedy local choice = best CPU utilization
```

### 5.4 File Merge — Optimal Merge Pattern
```
Merge k sorted files of sizes n1, n2, ..., nk
Cost of merging two files of sizes a and b = a + b

Greedy: Always merge the two smallest files first (like Huffman)
Total cost is minimized by merging smallest pairs repeatedly.

Example: Files [20, 30, 10, 5]
  Merge 5+10=15, cost=15
  Merge 15+20=35, cost=35
  Merge 35+30=65, cost=65
  Total = 115

  vs. naive order: 20+30=50, 50+10=60, 60+5=65 → Total = 175
  Greedy saves 33%
```

### 5.5 Financial Markets — Greedy Trading
```java
// Best time to buy/sell stocks (unlimited transactions)
public static int maxProfit(int[] prices) {
    int profit = 0;
    for (int i = 1; i < prices.length; i++) {
        if (prices[i] > prices[i-1]) {
            profit += prices[i] - prices[i-1];  // Capture every upward move
        }
    }
    return profit;
}
// Greedy insight: sum of all positive daily differences = maximum total profit
// [7,1,5,3,6,4] → (5-1)+(6-3)=4+3=7
```

### 5.6 Networking — Minimum Spanning Tree
```
MST Applications:
  - Network cable layout (minimize wire length)
  - Road network design (connect all cities cheaply)
  - Cluster analysis (remove heaviest MST edge → two clusters)
  - Approximation for TSP (MST gives 2x approximation)

Kruskal's algorithm: O(E log E) — greedy, sort edges, add if no cycle
Prim's algorithm:    O(E log V) — greedy, grow tree from one node
```

### 5.7 Resource Allocation — Activity Selection
```
Real-world uses:
  - Meeting room scheduler (Google Calendar, Outlook)
  - CPU time slice allocation
  - Bandwidth reservation in networks
  - Factory machine scheduling
  - Aircraft gate assignment at airports

All reduce to: Maximum non-overlapping interval selection
→ Always assign earliest-ending request first
```

---

## 6. Complexity Analysis

### Per-Algorithm Complexity

| Algorithm | Time | Space | Key Operation |
|---|---|---|---|
| Activity Selection | O(n log n) | O(n) | Sort by end time |
| Fractional Knapsack | O(n log n) | O(1) | Sort by ratio |
| Huffman Encoding | O(n log n) | O(n) | Priority queue operations |
| Dijkstra (binary heap) | O((V+E) log V) | O(V) | PQ extract-min |
| Dijkstra (Fibonacci heap) | O(E + V log V) | O(V) | Decrease-key O(1) |
| Kruskal's MST | O(E log E) | O(V) | Sort edges + Union-Find |
| Prim's MST | O(E log V) | O(V) | PQ operations |
| Job Scheduling (EDF) | O(n log n) | O(1) | Sort by deadline |
| Gas Station | O(n) | O(1) | Single pass |
| Jump Game | O(n) | O(1) | Single pass |
| Coin Change (greedy) | O(n log n) | O(1) | Sort + scan |
| Largest Number | O(n log n) | O(n) | Custom sort |
| Task Scheduler | O(n log n) | O(1) | Frequency count |
| Huffman Decode | O(n) | O(1) | Tree traversal |

### Sorting Dominates
```
Most greedy algorithms are O(n log n) because sorting is usually step 1.
After sorting, a single linear scan produces the answer.

Total: O(n log n) sort + O(n) scan = O(n log n)

Exceptions (O(n)):
  - Gas Station: scan once, no sort needed
  - Jump Game: scan once with running max
  - Best Stock Profit: scan once for positive diffs
```

### Space Complexity Patterns
```
O(1) extra space:
  - Gas Station, Jump Game, Stock Profit, Coin Change
  - When answer can be computed from a running variable

O(n) extra space:
  - Huffman tree storage
  - MST result storage
  - When intermediate results need storing

O(V + E) for graphs:
  - Dijkstra (adjacency list + PQ)
  - Kruskal (Union-Find)
```

### Greedy vs DP Complexity Comparison
```
Problem           Greedy          DP
─────────────────────────────────────────
Coin Change       O(n log n)*     O(n × amount)
Knapsack (0/1)    N/A (wrong)     O(n × W)
Knapsack (frac)   O(n log n)      N/A (overkill)
Shortest Path     O(E log V)      O(V²) Bellman-Ford
Activity Select   O(n log n)      O(n²) if DP
Huffman           O(n log n)      N/A

* Only correct for canonical coin systems
```

---

## 7. Interview Strategies

### 7.1 Recognizing Greedy Problems

```
Signals that greedy MIGHT work:
  ✓ Problem asks for min/max of something
  ✓ "Optimal" solution involves making sequential choices
  ✓ Intervals, scheduling, ordering problems
  ✓ Graph problems (shortest path, spanning tree)
  ✓ Compression or encoding problems
  ✓ Problem has sorted order that makes choices obvious
  ✓ "Greedy works on this" if you can state "always pick X first"

Signals that greedy MIGHT fail:
  ✗ 0/1 binary choices (take or leave items)
  ✗ Multiple interacting constraints
  ✗ Need to optimize over all subsets
  ✗ Counterexample found with small input
```

### 7.2 The Greedy Interview Framework

```
Step 1: IDENTIFY the greedy choice
  "What is the locally optimal thing to do at each step?"
  Example: "Always schedule the job with the earliest deadline"

Step 2: JUSTIFY the greedy choice (exchange argument)
  "If I don't make this choice, can I always swap to get something as good?"
  Example: "If we schedule a non-deadline job first, we can swap it with
            the deadline job and not increase max lateness"

Step 3: IDENTIFY remaining subproblem
  "After making the greedy choice, what's left?"
  Example: "Schedule remaining jobs on the remaining time"

Step 4: PROVE optimal substructure
  "Does optimal solution to subproblem + greedy choice = global optimal?"

Step 5: IMPLEMENT cleanly
  Sort → Scan → Build result
```

### 7.3 Key Interview Problems to Master

#### Must-Know Greedy Problems
```
1. Activity Selection / Meeting Rooms I & II  (LC 252, 253)
2. Jump Game I & II                           (LC 55, 45)
3. Best Time to Buy/Sell Stock II             (LC 122)
4. Gas Station                               (LC 134)
5. Assign Cookies                            (LC 455)
6. Task Scheduler                            (LC 621)
7. Largest Number                            (LC 179)
8. Partition Labels                          (LC 763)
9. Non-overlapping Intervals                 (LC 435)
10. Minimum Number of Arrows                 (LC 452)
```

#### LC 435 — Non-overlapping Intervals (Minimum Removals)
```java
public static int eraseOverlapIntervals(int[][] intervals) {
    if (intervals.length == 0) return 0;
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]);  // Sort by end time
    int count = 0, lastEnd = intervals[0][1];
    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] < lastEnd) {
            count++;    // Overlap → remove current (keep earlier-ending one)
        } else {
            lastEnd = intervals[i][1];  // No overlap → keep, update lastEnd
        }
    }
    return count;
}
// Equivalent to: n - maxNonOverlapping (Activity Selection count)
```

#### LC 452 — Minimum Arrows to Burst Balloons
```java
public static int findMinArrowShots(int[][] points) {
    Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
    int arrows = 1, arrowPos = points[0][1];
    for (int i = 1; i < points.length; i++) {
        if (points[i][0] > arrowPos) {   // Current balloon beyond arrow position
            arrows++;
            arrowPos = points[i][1];     // Shoot new arrow at end of this balloon
        }
        // Otherwise: current arrow bursts this balloon too
    }
    return arrows;
}
// [[10,16],[2,8],[1,6],[7,12]] → 2
```

#### LC 860 — Lemonade Change
```java
public static boolean lemonadeChange(int[] bills) {
    int fives = 0, tens = 0;
    for (int bill : bills) {
        if (bill == 5) {
            fives++;
        } else if (bill == 10) {
            if (fives == 0) return false;
            fives--; tens++;
        } else { // bill == 20
            // Greedy: prefer to give back 10+5 (save 5s for future)
            if (tens > 0 && fives > 0) { tens--; fives--; }
            else if (fives >= 3)        { fives -= 3; }
            else return false;
        }
    }
    return true;
}
// [5,5,5,10,20] → true
// [5,5,10,10,20] → false
```

#### LC 1029 — Two City Scheduling
```java
// Send n people to city A and n to city B, minimize total cost
// costs[i] = {costA, costB}
public static int twoCitySchedCost(int[][] costs) {
    // Sort by savings of going to A instead of B (costA - costB)
    // Greedy: people with biggest "savings" going to A should go to A
    Arrays.sort(costs, (a, b) -> (a[0] - a[1]) - (b[0] - b[1]));
    int total = 0, n = costs.length / 2;
    for (int i = 0;     i < n;             i++) total += costs[i][0];   // First half → A
    for (int i = n; i < costs.length; i++) total += costs[i][1];        // Second half → B
    return total;
}
// [[10,20],[30,200],[400,50],[30,20]] → 110
```

### 7.4 Common Greedy Mistakes & How to Avoid Them

```
Mistake 1: Assuming greedy works without proof
  → Always test with counterexample first
  → Try: does a small case fail?

Mistake 2: Wrong sort order
  → Activity selection: sort by END not start
  → Job scheduling (min lateness): sort by DEADLINE not processing time
  → Tip: think about what property you want to "stay ahead" on

Mistake 3: Off-by-one in interval problems
  → [1,4] and [4,5]: do they overlap?
  → Clarify: open vs closed intervals
  → In most LeetCode problems: [s,e] overlap if s < e' (not <=)

Mistake 4: Greedy on 0/1 Knapsack
  → Always fails for non-uniform items
  → Use DP for 0/1, greedy only for fractional

Mistake 5: Not handling ties correctly
  → Ties often need secondary sort criterion
  → Largest Number: (b+a) vs (a+b) handles ties implicitly

Mistake 6: Not proving greedy choice property
  → In interviews, explain WHY your greedy works
  → "I sort by X because Y" is better than just coding
```

### 7.5 Complexity Quick Reference

| Pattern | Sort By | Scan Invariant | Result |
|---|---|---|---|
| Max non-overlapping intervals | End time | Keep earliest-ending | Count selected |
| Min removals for non-overlap | End time | Remove overlap | Count removed |
| Min arrows (balloons) | End time | Arrow at end of first | Count arrows |
| Job lateness | Deadline | EDF scheduling | Max lateness |
| Meeting rooms | Start & end separate | Active room count | Max concurrent |
| Fractional knapsack | Value/weight DESC | Fill greedily | Total value |
| Largest number | a+b vs b+a | Concatenation order | Joined string |
| Assign cookies | Greed & size ASC | Match smallest fitting | Count matched |
| Huffman | Frequency ASC (PQ) | Merge two smallest | Code tree |
| MST Kruskal | Weight ASC | Union-Find cycle check | Total weight |

---

## Summary

### The Greedy Algorithm Blueprint

```
1. SORT: Order elements by the key property (end time, ratio, deadline...)
2. SCAN: Iterate once, making locally optimal choices
3. COMMIT: Never revisit or undo choices
4. TRUST: Local optimality + greedy choice property → global optimality
```

### When Greedy Works
```
Greedy Property:  "The best local choice is always part of a global optimum"
Optimal Substructure: "Solving the remaining subproblem optimally + greedy choice = optimal"
Exchange Argument: "Any other choice can be swapped with the greedy choice without loss"
```

### The Most Important Insight
```
Greedy algorithms are elegant precisely because they avoid the exponential
explosion of trying all possibilities. The art of greedy algorithm design
is identifying the right "greediness criterion" — the local property that
provably leads to global optimality.

Not every problem has this property. The skill is knowing when it exists,
proving it rigorously (even informally in an interview), and exploiting it
for dramatic efficiency gains.

O(n log n) instead of O(2^n) — that's the power of a valid greedy choice.
```
