# Recursion & Backtracking — Complete Guide
> Mathematical Thinking + State Space Exploration
> Covers: Recursion Fundamentals · Recursion Tree & Stack · Tail Recursion · Backtracking Concept · Subset & Permutation Patterns · Constraint Problems · Interview-Level Strategies

---

## Table of Contents
1. [Recursion Fundamentals](#1-recursion-fundamentals)
2. [Recursion Tree & Stack Visualization](#2-recursion-tree--stack-visualization)
3. [Tail Recursion](#3-tail-recursion)
4. [Backtracking Concept](#4-backtracking-concept)
5. [Subset & Permutation Patterns](#5-subset--permutation-patterns)
6. [Constraint Problems](#6-constraint-problems)
7. [Interview-Level Strategies](#7-interview-level-strategies)

---

## 1. Recursion Fundamentals

### What Is Recursion?
Recursion is a technique where a **function calls itself** to solve a smaller version of the same problem. Every recursive solution must have:

1. **Base case** — the condition where recursion stops
2. **Recursive case** — the problem reduced toward the base case
3. **Progress guarantee** — each call must move closer to the base case

### The Mental Model
```
Think recursively = Trust + Delegate

Trust:    Assume the recursive call correctly solves the smaller problem
Delegate: Use that result to solve the current problem
Focus:    Only on the CURRENT step, not the entire chain
```

### Why Recursion?
| Iterative | Recursive |
|---|---|
| Explicit stack management | Call stack managed by runtime |
| Verbose for tree/graph problems | Naturally mirrors problem structure |
| Better performance (no stack overhead) | Cleaner, more readable |
| Required for tail-call optimized langs | Required for divide & conquer, backtracking |

### The Three Laws of Recursion
1. Must have a **base case**
2. Must **change state** and move toward the base case
3. Must **call itself** recursively

---

### 1.1 Factorial — The Hello World of Recursion

#### Mathematical Definition
```
n! = n × (n-1) × (n-2) × ... × 1
0! = 1   (base case)
5! = 5 × 4! = 5 × 4 × 3! = 5 × 4 × 3 × 2 × 1 = 120
```

#### Java Implementation
```java
// Recursive factorial
public static long factorial(int n) {
    if (n <= 1) return 1;           // Base case: 0! = 1! = 1
    return n * factorial(n - 1);    // Recursive case: n! = n × (n-1)!
}

// Iterative equivalent — for comparison
public static long factorialIterative(int n) {
    long result = 1;
    for (int i = 2; i <= n; i++) result *= i;
    return result;
}
```

#### Call Stack Trace
```
factorial(4)
  = 4 * factorial(3)
        = 3 * factorial(2)
              = 2 * factorial(1)
                    = 1            ← base case returns
              = 2 * 1 = 2
        = 3 * 2 = 6
  = 4 * 6 = 24
```

---

### 1.2 Fibonacci — Overlapping Subproblems

```java
// Naive recursive — exponential O(2^n)
public static int fib(int n) {
    if (n <= 1) return n;                        // Base cases: fib(0)=0, fib(1)=1
    return fib(n - 1) + fib(n - 2);             // Recursive case
}

// Memoized — O(n) time, O(n) space
public static int fibMemo(int n, int[] memo) {
    if (n <= 1) return n;
    if (memo[n] != 0) return memo[n];            // Return cached result
    memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
    return memo[n];
}

// Bottom-up DP — O(n) time, O(1) space (best)
public static int fibDP(int n) {
    if (n <= 1) return n;
    int a = 0, b = 1;
    for (int i = 2; i <= n; i++) {
        int c = a + b; a = b; b = c;
    }
    return b;
}
```

#### Why Naive Fibonacci is O(2^n)
```
fib(5)
├── fib(4)
│   ├── fib(3)
│   │   ├── fib(2) ← computed twice
│   │   └── fib(1)
│   └── fib(2) ← computed twice
└── fib(3) ← computed twice
    ├── fib(2) ← computed thrice!
    └── fib(1)
```
Same subproblems computed repeatedly → exponential redundancy → memoization fixes this.

---

### 1.3 Power Function — Divide & Conquer

```java
// Naive: O(n) multiplications
public static double powerNaive(double x, int n) {
    if (n == 0) return 1;
    return x * powerNaive(x, n - 1);
}

// Fast power (Exponentiation by squaring): O(log n)
public static double power(double x, int n) {
    if (n == 0) return 1;
    if (n < 0) { x = 1 / x; n = -n; }             // Handle negative exponent
    if (n % 2 == 0) {
        double half = power(x, n / 2);
        return half * half;                          // x^n = (x^(n/2))^2
    } else {
        return x * power(x, n - 1);                 // x^n = x * x^(n-1)
    }
}
// 2^10 = (2^5)^2 = (2*(2^4))^2 = (2*(2^2)^2)^2 → 4 multiplications instead of 10
```

---

### 1.4 Sum of Digits, Palindrome, GCD

```java
// Sum of digits recursively
public static int sumDigits(int n) {
    if (n == 0) return 0;
    return (n % 10) + sumDigits(n / 10);
}
// sumDigits(1234) = 4 + sumDigits(123) = 4+3+2+1 = 10

// Palindrome check recursively
public static boolean isPalindrome(String s, int left, int right) {
    if (left >= right) return true;                  // Base case
    if (s.charAt(left) != s.charAt(right)) return false;
    return isPalindrome(s, left + 1, right - 1);
}

// GCD — Euclidean algorithm (classic recursion)
public static int gcd(int a, int b) {
    if (b == 0) return a;                            // Base case
    return gcd(b, a % b);                           // gcd(a,b) = gcd(b, a mod b)
}
// gcd(48, 18) = gcd(18, 12) = gcd(12, 6) = gcd(6, 0) = 6

// Tower of Hanoi — classic recursion
public static void hanoi(int n, char from, char to, char aux) {
    if (n == 1) {
        System.out.println("Move disk 1 from " + from + " to " + to);
        return;
    }
    hanoi(n - 1, from, aux, to);                    // Move n-1 disks to aux
    System.out.println("Move disk " + n + " from " + from + " to " + to);
    hanoi(n - 1, aux, to, from);                    // Move n-1 disks from aux to to
}
// Minimum moves for n disks = 2^n - 1
```

---

### 1.5 String Recursion

```java
// Reverse a string recursively
public static String reverse(String s) {
    if (s.isEmpty()) return s;
    return reverse(s.substring(1)) + s.charAt(0);
}

// Count occurrences of character
public static int countChar(String s, char c) {
    if (s.isEmpty()) return 0;
    return (s.charAt(0) == c ? 1 : 0) + countChar(s.substring(1), c);
}

// Check if string contains only digits
public static boolean isNumeric(String s) {
    if (s.isEmpty()) return true;
    if (!Character.isDigit(s.charAt(0))) return false;
    return isNumeric(s.substring(1));
}

// All subsequences of a string
public static void subsequences(String s, String current) {
    if (s.isEmpty()) {
        System.out.println(current.isEmpty() ? "(empty)" : current);
        return;
    }
    subsequences(s.substring(1), current + s.charAt(0)); // Include first char
    subsequences(s.substring(1), current);                // Exclude first char
}
// "abc" → "", "c", "b", "bc", "a", "ac", "ab", "abc"
```

---

## 2. Recursion Tree & Stack Visualization

### Understanding the Call Stack
Every recursive call **pushes a frame** onto the call stack. Each frame contains:
- Local variables
- Parameters
- Return address

```
Stack frame for factorial(4):
┌─────────────────────┐  ← Top of stack
│  factorial(1)        │  n=1, returns 1
├─────────────────────┤
│  factorial(2)        │  n=2, waiting for factorial(1)
├─────────────────────┤
│  factorial(3)        │  n=3, waiting for factorial(2)
├─────────────────────┤
│  factorial(4)        │  n=4, waiting for factorial(3)
├─────────────────────┤
│  main()              │  called factorial(4)
└─────────────────────┘  ← Bottom of stack
```

### Stack Overflow
```java
// StackOverflowError — missing base case or infinite recursion
public static int infinite(int n) {
    return infinite(n - 1);   // No base case → stack fills up → crash
}

// Rule: Maximum recursion depth ≈ 500-1000 calls in Java (default 512KB stack)
// Use -Xss to increase: java -Xss8m MyClass
```

### Recursion Tree for fib(5)
```
                    fib(5)
                  /        \
            fib(4)          fib(3)
           /      \        /      \
        fib(3)  fib(2)  fib(2)  fib(1)
        /    \   /   \   /   \
     fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)
     /    \
  fib(1) fib(0)

Nodes = 2^(n+1) - 1 ≈ O(2^n) for naive fib
With memoization: each node computed once → O(n)
```

### Recursion Tree Metrics
```
For a recursion with branching factor b and depth d:
  Total nodes  = O(b^d)
  Space (stack) = O(d)          ← only one path at a time on the stack

Examples:
  factorial:  b=1, d=n   → O(n) nodes, O(n) space
  fib(naive): b=2, d=n   → O(2^n) nodes, O(n) space
  merge sort: b=2, d=logn → O(n) nodes, O(log n) stack space
  permutations: b=n, d=n → O(n!) nodes, O(n) space
```

### Visualizing Subsets — Decision Tree
```
Generate subsets of {1, 2, 3}

Each level: include or exclude current element

                    []
              /           \
         [1]               []
        /    \           /    \
    [1,2]   [1]        [2]    []
    /   \   /  \      /  \   /  \
[1,2,3][1,2][1,3][1][2,3][2][3] []

Leaves (8 total = 2^3) are the subsets
```

### Visualizing Permutations — Choice Tree
```
Permutations of {1, 2, 3}

              root
         /     |     \
        1       2      3
       / \     / \    / \
      2   3   1   3  1   2
      |   |   |   |  |   |
      3   2   3   1  2   1

Leaves: 123, 132, 213, 231, 312, 321  (3! = 6)
Each level reduces choices by 1
```

---

## 3. Tail Recursion

### What Is Tail Recursion?
A recursive call is **tail recursive** if the recursive call is the **last operation** in the function — nothing happens after it returns.

```java
// NOT tail recursive — multiplication happens AFTER return
public static long factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);   // n * ... means work happens after call
}

// TAIL RECURSIVE — accumulator carries the result
public static long factorialTail(int n, long accumulator) {
    if (n <= 1) return accumulator;         // Base case returns acc directly
    return factorialTail(n - 1, n * accumulator); // Last operation IS the call
}
// Call: factorialTail(5, 1)
// → factorialTail(4, 5)
// → factorialTail(3, 20)
// → factorialTail(2, 60)
// → factorialTail(1, 120) → returns 120
```

### Why Tail Recursion Matters
```
Non-tail recursion:
  Each call WAITS for the result → frame must stay on stack
  Stack grows: O(n) frames

Tail recursion:
  Each call is the FINAL action → frame can be REUSED
  With TCO (Tail Call Optimization): O(1) stack space!

Java does NOT support TCO natively → but understanding it
helps write iterative equivalents from recursive thinking.
```

### Converting Non-Tail to Tail Recursive
```java
// Pattern: introduce an accumulator parameter

// Non-tail: sum of array
public static int sum(int[] arr, int i) {
    if (i == arr.length) return 0;
    return arr[i] + sum(arr, i + 1);   // addition after return = not tail
}

// Tail: accumulate sum as we go
public static int sumTail(int[] arr, int i, int acc) {
    if (i == arr.length) return acc;
    return sumTail(arr, i + 1, acc + arr[i]);  // last operation = tail call
}

// Non-tail: reverse list
public static List<Integer> reverse(List<Integer> list) {
    if (list.isEmpty()) return list;
    List<Integer> rest = reverse(list.subList(1, list.size()));
    rest.add(list.get(0));              // work after return = not tail
    return rest;
}

// Tail: accumulator is the reversed list so far
public static List<Integer> reverseTail(List<Integer> list, List<Integer> acc) {
    if (list.isEmpty()) return acc;
    acc.add(0, list.get(0));
    return reverseTail(list.subList(1, list.size()), acc); // tail call
}

// Tail-recursive Fibonacci
public static long fibTail(int n, long a, long b) {
    if (n == 0) return a;
    if (n == 1) return b;
    return fibTail(n - 1, b, a + b);   // tail call — no pending work
}
// fibTail(5, 0, 1) → fibTail(4, 1, 1) → fibTail(3, 1, 2)
// → fibTail(2, 2, 3) → fibTail(1, 3, 5) → 5
```

### Simulating TCO in Java — Trampoline Pattern
```java
// Trampoline: delay recursive calls as lambdas, execute iteratively
@FunctionalInterface
interface Thunk<T> {
    T call();
}

public static <T> T trampoline(Thunk<T> thunk) {
    T result = thunk.call();
    while (result instanceof Thunk) {
        result = ((Thunk<T>) result).call();
    }
    return result;
}
// This avoids stack overflow for very deep tail-recursive functions
```

---

## 4. Backtracking Concept

### What Is Backtracking?
Backtracking is a **systematic exploration of all possibilities** by:
1. **Choose** — make a choice from available options
2. **Explore** — recursively explore the consequence of that choice
3. **Unchoose (Backtrack)** — undo the choice and try the next option

```
Backtracking Template:

void backtrack(state, choices) {
    if (goal reached) {
        record solution
        return
    }
    for each choice in choices:
        if choice is valid:
            make choice           ← modify state
            backtrack(new state, remaining choices)
            undo choice           ← restore state  ← THIS is backtracking
}
```

### Backtracking vs Brute Force
```
Brute force:  Generate ALL possibilities, then filter valid ones
Backtracking: Prune EARLY — abandon paths that cannot lead to solution

Example: 8-Queens problem
  Brute force:  Place queens in all 8^8 = 16,777,216 configurations, check each
  Backtracking: Place queens one column at a time, skip if conflict found
                Explores only ~15,720 configurations — 1000x faster
```

### The State Space Tree
```
Backtracking explores a STATE SPACE TREE:
  Root: initial empty state
  Each node: a partial solution
  Each edge: a choice
  Leaves: complete solutions or dead ends

Pruning: Cut branches that CANNOT lead to valid solutions

[root: empty]
├── [choice A]
│   ├── [choice A, X] ← PRUNE if constraint violated
│   └── [choice A, Y]
│       └── [choice A, Y, Z] ← solution found!
└── [choice B]
    └── [choice B, X]
        └── ... (continue exploring)
```

### Backtracking Complexity
```
Without pruning: O(b^d)  where b = branching factor, d = depth
With pruning:    Highly problem-dependent — often much better in practice

Pruning effectiveness depends on:
  - How early you can detect invalid states
  - How strict the constraints are
  - Problem structure
```

---

## 5. Subset & Permutation Patterns

### 5.1 Generate All Subsets (Power Set)

#### Concept
For n elements, there are 2^n subsets. At each element, we make a binary choice: **include** or **exclude**.

```java
// Method 1: Include/Exclude recursion
public static void subsets(int[] nums, int index, List<Integer> current,
                            List<List<Integer>> result) {
    if (index == nums.length) {
        result.add(new ArrayList<>(current));   // Base case: add copy of current subset
        return;
    }
    // Choice 1: Include nums[index]
    current.add(nums[index]);
    subsets(nums, index + 1, current, result);
    current.remove(current.size() - 1);         // Backtrack: remove last added

    // Choice 2: Exclude nums[index]
    subsets(nums, index + 1, current, result);
}

// Method 2: Iterative extension
public static List<List<Integer>> subsetsIterative(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>());               // Start with empty set
    for (int num : nums) {
        int size = result.size();
        for (int i = 0; i < size; i++) {
            List<Integer> subset = new ArrayList<>(result.get(i));
            subset.add(num);                     // Add num to each existing subset
            result.add(subset);
        }
    }
    return result;
}

// Method 3: Bitmask — elegant for small n
public static List<List<Integer>> subsetsBitmask(int[] nums) {
    int n = nums.length;
    List<List<Integer>> result = new ArrayList<>();
    for (int mask = 0; mask < (1 << n); mask++) {  // 2^n masks
        List<Integer> subset = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) != 0) {           // Bit i is set → include nums[i]
                subset.add(nums[i]);
            }
        }
        result.add(subset);
    }
    return result;
}
```

#### Subsets with Duplicates
```java
public static void subsetsWithDups(int[] nums, int start,
                                    List<Integer> current,
                                    List<List<Integer>> result) {
    result.add(new ArrayList<>(current));
    for (int i = start; i < nums.length; i++) {
        // Skip duplicate: same value at same recursion level
        if (i > start && nums[i] == nums[i - 1]) continue;
        current.add(nums[i]);
        subsetsWithDups(nums, i + 1, current, result);
        current.remove(current.size() - 1);     // Backtrack
    }
}
// Input must be sorted first: Arrays.sort(nums)
// [1,2,2] → [], [1], [1,2], [1,2,2], [2], [2,2]  (no duplicates)
```

---

### 5.2 Generate All Permutations

#### Concept
For n elements, there are n! permutations. At each position, we choose one of the remaining unused elements.

```java
// Method 1: Swap-based (in-place)
public static void permutations(int[] nums, int start, List<List<Integer>> result) {
    if (start == nums.length) {
        List<Integer> perm = new ArrayList<>();
        for (int n : nums) perm.add(n);
        result.add(perm);
        return;
    }
    for (int i = start; i < nums.length; i++) {
        swap(nums, start, i);                       // Choose: put nums[i] at position start
        permutations(nums, start + 1, result);      // Explore: fill remaining positions
        swap(nums, start, i);                       // Unchoose: restore original order
    }
}

// Method 2: Used-array approach (clearer logic)
public static void permutations2(int[] nums, boolean[] used,
                                   List<Integer> current,
                                   List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;                      // Skip already used elements
        used[i] = true;                             // Choose
        current.add(nums[i]);
        permutations2(nums, used, current, result); // Explore
        current.remove(current.size() - 1);         // Unchoose
        used[i] = false;
    }
}

// Permutations with duplicates — skip same element at same level
public static void permsUnique(int[] nums, boolean[] used,
                                 List<Integer> current,
                                 List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        // Skip duplicate: same value, previous duplicate not used
        // (ensures we always pick leftmost duplicate first)
        if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) continue;
        used[i] = true;
        current.add(nums[i]);
        permsUnique(nums, used, current, result);
        current.remove(current.size() - 1);
        used[i] = false;
    }
}
// Input must be sorted first
// [1,1,2] → [1,1,2], [1,2,1], [2,1,1]  (3 unique, not 6)
```

---

### 5.3 Combination Sum

```java
// Find all combinations summing to target (elements reusable)
public static void combinationSum(int[] candidates, int target,
                                    int start, List<Integer> current,
                                    List<List<Integer>> result) {
    if (target == 0) {
        result.add(new ArrayList<>(current));   // Found valid combination
        return;
    }
    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > target) break;      // Pruning: sorted array, no need to continue
        current.add(candidates[i]);
        combinationSum(candidates, target - candidates[i], i, current, result);
        // Note: i (not i+1) → same element can be reused
        current.remove(current.size() - 1);     // Backtrack
    }
}

// Combination Sum II — each element used once, no duplicate combos
public static void combinationSum2(int[] candidates, int target,
                                     int start, List<Integer> current,
                                     List<List<Integer>> result) {
    if (target == 0) { result.add(new ArrayList<>(current)); return; }
    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > target) break;
        if (i > start && candidates[i] == candidates[i-1]) continue; // Skip dups
        current.add(candidates[i]);
        combinationSum2(candidates, target - candidates[i], i + 1, current, result);
        current.remove(current.size() - 1);
    }
}
```

---

### 5.4 Letter Combinations of Phone Number

```java
public static List<String> letterCombinations(String digits) {
    if (digits.isEmpty()) return new ArrayList<>();
    String[] map = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
    List<String> result = new ArrayList<>();
    backtrackPhone(digits, 0, new StringBuilder(), map, result);
    return result;
}

private static void backtrackPhone(String digits, int index, StringBuilder current,
                                     String[] map, List<String> result) {
    if (index == digits.length()) {
        result.add(current.toString());
        return;
    }
    String letters = map[digits.charAt(index) - '0'];
    for (char c : letters.toCharArray()) {
        current.append(c);                          // Choose
        backtrackPhone(digits, index + 1, current, map, result);
        current.deleteCharAt(current.length() - 1); // Unchoose
    }
}
// "23" → ["ad","ae","af","bd","be","bf","cd","ce","cf"]
```

---

## 6. Constraint Problems

### 6.1 N-Queens Problem

#### Problem
Place N queens on an N×N chessboard such that no two queens attack each other (no same row, column, or diagonal).

```java
public static List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    int[] queens = new int[n];  // queens[row] = column of queen in that row
    Arrays.fill(queens, -1);
    // Track occupied columns and diagonals
    boolean[] cols    = new boolean[n];
    boolean[] diag1   = new boolean[2 * n - 1]; // row - col + n - 1
    boolean[] diag2   = new boolean[2 * n - 1]; // row + col
    nQueensBacktrack(n, 0, queens, cols, diag1, diag2, result);
    return result;
}

private static void nQueensBacktrack(int n, int row, int[] queens,
                                      boolean[] cols, boolean[] diag1,
                                      boolean[] diag2, List<List<String>> result) {
    if (row == n) {
        result.add(buildBoard(queens, n));  // All rows filled → valid placement
        return;
    }
    for (int col = 0; col < n; col++) {
        int d1 = row - col + n - 1;
        int d2 = row + col;
        if (cols[col] || diag1[d1] || diag2[d2]) continue; // Pruning: conflict

        queens[row] = col;                  // Place queen
        cols[col] = diag1[d1] = diag2[d2] = true;

        nQueensBacktrack(n, row + 1, queens, cols, diag1, diag2, result);

        queens[row] = -1;                   // Remove queen (backtrack)
        cols[col] = diag1[d1] = diag2[d2] = false;
    }
}

private static List<String> buildBoard(int[] queens, int n) {
    List<String> board = new ArrayList<>();
    for (int row = 0; row < n; row++) {
        char[] line = new char[n];
        Arrays.fill(line, '.');
        line[queens[row]] = 'Q';
        board.add(new String(line));
    }
    return board;
}
// n=4: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
```

---

### 6.2 Sudoku Solver

```java
public static boolean solveSudoku(char[][] board) {
    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            if (board[row][col] != '.') continue;   // Skip filled cells

            for (char c = '1'; c <= '9'; c++) {
                if (isValidSudoku(board, row, col, c)) {
                    board[row][col] = c;             // Place digit

                    if (solveSudoku(board)) return true; // Explore

                    board[row][col] = '.';           // Backtrack
                }
            }
            return false;   // No valid digit → backtrack to previous cell
        }
    }
    return true;  // All cells filled → solved
}

private static boolean isValidSudoku(char[][] board, int row, int col, char c) {
    for (int i = 0; i < 9; i++) {
        if (board[row][i] == c) return false;           // Row conflict
        if (board[i][col] == c) return false;           // Column conflict
        // Box conflict: 3x3 box top-left is (row/3*3, col/3*3)
        if (board[row/3*3 + i/3][col/3*3 + i%3] == c) return false;
    }
    return true;
}
```

---

### 6.3 Word Search in Grid

```java
public static boolean wordSearch(char[][] board, String word) {
    int rows = board.length, cols = board[0].length;
    for (int r = 0; r < rows; r++)
        for (int c = 0; c < cols; c++)
            if (dfsWord(board, word, r, c, 0)) return true;
    return false;
}

private static boolean dfsWord(char[][] board, String word,
                                 int r, int c, int idx) {
    if (idx == word.length()) return true;           // All chars matched
    if (r < 0 || r >= board.length ||
        c < 0 || c >= board[0].length) return false; // Out of bounds
    if (board[r][c] != word.charAt(idx)) return false; // Mismatch

    char temp = board[r][c];
    board[r][c] = '#';  // Mark as visited (in-place, no extra space)

    boolean found = dfsWord(board, word, r+1, c, idx+1) ||
                    dfsWord(board, word, r-1, c, idx+1) ||
                    dfsWord(board, word, r, c+1, idx+1) ||
                    dfsWord(board, word, r, c-1, idx+1);

    board[r][c] = temp; // Restore (backtrack)
    return found;
}
```

---

### 6.4 Rat in a Maze

```java
public static List<String> ratInMaze(int[][] maze) {
    int n = maze.length;
    boolean[][] visited = new boolean[n][n];
    List<String> paths = new ArrayList<>();
    if (maze[0][0] == 1) ratDFS(maze, 0, 0, n, "", visited, paths);
    return paths;
}

private static void ratDFS(int[][] maze, int r, int c, int n,
                             String path, boolean[][] visited,
                             List<String> paths) {
    if (r == n-1 && c == n-1) { paths.add(path); return; } // Reached destination

    int[] dr = {1, -1, 0,  0};
    int[] dc = {0,  0, 1, -1};
    char[] dir = {'D', 'U', 'R', 'L'};

    visited[r][c] = true;
    for (int i = 0; i < 4; i++) {
        int nr = r + dr[i], nc = c + dc[i];
        if (nr >= 0 && nr < n && nc >= 0 && nc < n
                && maze[nr][nc] == 1 && !visited[nr][nc]) {
            ratDFS(maze, nr, nc, n, path + dir[i], visited, paths);
        }
    }
    visited[r][c] = false;  // Backtrack: unvisit
}
```

---

### 6.5 Palindrome Partitioning

```java
// Partition string such that every substring is a palindrome
public static List<List<String>> palindromePartition(String s) {
    List<List<String>> result = new ArrayList<>();
    partitionBacktrack(s, 0, new ArrayList<>(), result);
    return result;
}

private static void partitionBacktrack(String s, int start,
                                         List<String> current,
                                         List<List<String>> result) {
    if (start == s.length()) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int end = start + 1; end <= s.length(); end++) {
        String sub = s.substring(start, end);
        if (isPalin(sub)) {                         // Pruning: only proceed if palindrome
            current.add(sub);
            partitionBacktrack(s, end, current, result);
            current.remove(current.size() - 1);     // Backtrack
        }
    }
}

private static boolean isPalin(String s) {
    int l = 0, r = s.length() - 1;
    while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false;
    return true;
}
// "aab" → [["a","a","b"], ["aa","b"]]
```

---

### 6.6 Expression Add Operators

```java
// Add +, -, * between digits of a string to reach target
public static List<String> addOperators(String num, int target) {
    List<String> result = new ArrayList<>();
    addOpsBacktrack(num, target, 0, 0, 0, "", result);
    return result;
}

private static void addOpsBacktrack(String num, int target, int index,
                                      long eval, long mult,
                                      String expr, List<String> result) {
    if (index == num.length()) {
        if (eval == target) result.add(expr);
        return;
    }
    for (int i = index; i < num.length(); i++) {
        if (i != index && num.charAt(index) == '0') break; // No leading zeros
        String cur = num.substring(index, i + 1);
        long val = Long.parseLong(cur);
        if (index == 0) {
            addOpsBacktrack(num, target, i+1, val, val, cur, result);
        } else {
            addOpsBacktrack(num, target, i+1, eval+val, val, expr+"+"+cur, result);
            addOpsBacktrack(num, target, i+1, eval-val, -val, expr+"-"+cur, result);
            // For *, undo last addition, apply multiplication
            addOpsBacktrack(num, target, i+1, eval-mult+mult*val, mult*val, expr+"*"+cur, result);
        }
    }
}
// "123", target=6 → ["1+2+3", "1*2*3"]
```

---

## 7. Interview-Level Strategies

### 7.1 The UMPIRE Framework for Recursion Problems
```
U — Understand: What are the inputs/outputs? What does "solve" mean?
M — Match: Is this a tree problem? Graph? Subset? Permutation?
P — Plan: What is the base case? What is one recursive step?
I — Implement: Write base case first, then recursive case
R — Review: Trace through a small example manually
E — Evaluate: What is time/space complexity?
```

### 7.2 Recognizing Recursion Patterns

```
Pattern 1 — Linear Recursion (single call)
  → Factorial, sum, reverse string, GCD
  → T(n) = T(n-1) + O(1) → O(n) time, O(n) space

Pattern 2 — Binary Recursion (two calls)
  → Fibonacci, merge sort, binary tree traversals
  → T(n) = 2T(n/2) + O(1) → O(n) time (tree)
  → T(n) = 2T(n-1) + O(1) → O(2^n) time (fib naive)

Pattern 3 — Divide & Conquer
  → Merge sort, quick sort, binary search
  → T(n) = 2T(n/2) + O(n) → O(n log n) (Master Theorem)

Pattern 4 — Backtracking (choices + undo)
  → Subsets, permutations, N-Queens, Sudoku
  → Explore + Prune
  → Time: O(b^d) before pruning

Pattern 5 — Memoization (recursion + cache)
  → Fib, coin change, longest common subsequence
  → Converts O(2^n) to O(n) or O(n^2)
```

### 7.3 Master Theorem — Complexity of Divide & Conquer

```
T(n) = aT(n/b) + f(n)

where: a = subproblems, b = size reduction factor, f(n) = work at each level

Case 1: f(n) = O(n^c) where c < log_b(a)  → T(n) = O(n^(log_b a))
Case 2: f(n) = O(n^c) where c = log_b(a)  → T(n) = O(n^c log n)
Case 3: f(n) = O(n^c) where c > log_b(a)  → T(n) = O(f(n))

Examples:
  Merge Sort: T(n) = 2T(n/2) + O(n)  → a=2, b=2, c=1, log_2(2)=1 → Case 2 → O(n log n)
  Binary Search: T(n) = T(n/2) + O(1) → a=1, b=2, c=0, log_2(1)=0 → Case 2 → O(log n)
  Strassen: T(n) = 7T(n/2) + O(n²)   → log_2(7)≈2.81 > 2         → Case 1 → O(n^2.81)
```

### 7.4 Common Interview Problems

#### Generate Parentheses (LeetCode 22)
```java
public static List<String> generateParentheses(int n) {
    List<String> result = new ArrayList<>();
    parenBacktrack(n, 0, 0, new StringBuilder(), result);
    return result;
}

private static void parenBacktrack(int n, int open, int close,
                                     StringBuilder current, List<String> result) {
    if (current.length() == 2 * n) {
        result.add(current.toString());
        return;
    }
    if (open < n) {                                     // Can add open paren
        current.append('(');
        parenBacktrack(n, open + 1, close, current, result);
        current.deleteCharAt(current.length() - 1);    // Backtrack
    }
    if (close < open) {                                 // Can add close paren
        current.append(')');
        parenBacktrack(n, open, close + 1, current, result);
        current.deleteCharAt(current.length() - 1);    // Backtrack
    }
}
// n=3 → ["((()))","(()())","(())()","()(())","()()()"]
```

#### Restore IP Addresses (LeetCode 93)
```java
public static List<String> restoreIpAddresses(String s) {
    List<String> result = new ArrayList<>();
    ipBacktrack(s, 0, new ArrayList<>(), result);
    return result;
}

private static void ipBacktrack(String s, int start, List<String> parts,
                                  List<String> result) {
    if (parts.size() == 4 && start == s.length()) {
        result.add(String.join(".", parts));
        return;
    }
    if (parts.size() == 4 || start == s.length()) return; // Pruning

    for (int len = 1; len <= 3; len++) {
        if (start + len > s.length()) break;
        String segment = s.substring(start, start + len);
        if (segment.length() > 1 && segment.charAt(0) == '0') break; // No leading zero
        if (len == 3 && Integer.parseInt(segment) > 255) break;       // Max 255
        parts.add(segment);
        ipBacktrack(s, start + len, parts, result);
        parts.remove(parts.size() - 1);             // Backtrack
    }
}
// "25525511135" → ["255.255.11.135", "255.255.111.35"]
```

#### Path Sum II (LeetCode 113)
```java
// Find all root-to-leaf paths summing to target
public static List<List<Integer>> pathSum(TreeNode root, int target) {
    List<List<Integer>> result = new ArrayList<>();
    pathDFS(root, target, new ArrayList<>(), result);
    return result;
}

private static void pathDFS(TreeNode node, int remaining,
                              List<Integer> path, List<List<Integer>> result) {
    if (node == null) return;
    path.add(node.val);
    if (node.left == null && node.right == null && remaining == node.val) {
        result.add(new ArrayList<>(path));  // Found valid path
    } else {
        pathDFS(node.left,  remaining - node.val, path, result);
        pathDFS(node.right, remaining - node.val, path, result);
    }
    path.remove(path.size() - 1);           // Backtrack
}
```

#### Decode Ways (LeetCode 91) — Memoized Recursion
```java
public static int numDecodings(String s) {
    return decodeHelper(s, 0, new HashMap<>());
}

private static int decodeHelper(String s, int idx, Map<Integer, Integer> memo) {
    if (idx == s.length()) return 1;                  // Decoded successfully
    if (s.charAt(idx) == '0') return 0;               // Leading zero → invalid
    if (memo.containsKey(idx)) return memo.get(idx);  // Cached

    int result = decodeHelper(s, idx + 1, memo);      // Take one digit
    if (idx + 1 < s.length()) {
        int two = Integer.parseInt(s.substring(idx, idx + 2));
        if (two >= 10 && two <= 26) {
            result += decodeHelper(s, idx + 2, memo); // Take two digits
        }
    }
    memo.put(idx, result);
    return result;
}
// "226" → 3: "2,2,6"=BZF, "22,6"=VF, "2,26"=BZ
```

#### Word Break (LeetCode 139) — Memoized Recursion
```java
public static boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    return wordBreakHelper(s, dict, 0, new Boolean[s.length()]);
}

private static boolean wordBreakHelper(String s, Set<String> dict,
                                         int start, Boolean[] memo) {
    if (start == s.length()) return true;
    if (memo[start] != null) return memo[start];

    for (int end = start + 1; end <= s.length(); end++) {
        if (dict.contains(s.substring(start, end))
                && wordBreakHelper(s, dict, end, memo)) {
            return memo[start] = true;
        }
    }
    return memo[start] = false;
}
// "leetcode", ["leet","code"] → true
// "applepenapple", ["apple","pen"] → true
```

### 7.5 Recursion to Iteration — General Pattern
```java
// ANY recursion can be converted to iteration using an explicit stack

// Recursive DFS on tree
void recursiveDFS(TreeNode node) {
    if (node == null) return;
    process(node);
    recursiveDFS(node.left);
    recursiveDFS(node.right);
}

// Iterative equivalent using explicit stack
void iterativeDFS(TreeNode root) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        if (node == null) continue;
        process(node);
        stack.push(node.right);   // Push right first (LIFO → left processed first)
        stack.push(node.left);
    }
}
```

### 7.6 Interview Tips & Anti-Patterns

```
✅ ALWAYS define base case first before recursive case
✅ ALWAYS verify the recursion terminates (moves toward base case)
✅ Use StringBuilder for string building in backtracking (mutable)
✅ Pass index/start to avoid re-generating duplicates in subsets
✅ Sort input before subset/combination backtracking to handle dups
✅ Mark visited BEFORE recursing, unmark AFTER (backtrack)
✅ Make copies with new ArrayList<>(current) when adding to result

❌ Don't use String concatenation in tight recursion (creates garbage)
❌ Don't forget to backtrack (undo state) after recursive call
❌ Don't modify the result list directly — always add a copy
❌ Don't ignore overflow for factorial/fibonacci (use long or BigInteger)
❌ Don't recurse on entire string with substring() — use index instead
```

### 7.7 Complexity Quick Reference

| Problem | Time | Space | Notes |
|---|---|---|---|
| Factorial | O(n) | O(n) | Linear recursion |
| Fibonacci (naive) | O(2^n) | O(n) | Exponential — always memoize |
| Fibonacci (memo) | O(n) | O(n) | Memoized |
| Subsets | O(2^n) | O(n) | 2 choices per element |
| Permutations | O(n × n!) | O(n) | n! perms, n to copy each |
| Combination Sum | O(2^target) | O(target) | With pruning, much better |
| N-Queens | O(n!) | O(n) | Pruning makes it practical |
| Sudoku | O(9^m) | O(m) | m = empty cells |
| Word Search | O(m × n × 4^L) | O(L) | L = word length |
| Generate Parentheses | O(4^n / √n) | O(n) | Catalan number |
| Palindrome Partition | O(n × 2^n) | O(n) | With precompute: O(n × 2^n) |

---

## Summary

### The Recursion Mindset
```
Step 1: Trust — assume recursive call works for n-1
Step 2: Delegate — use that result for n
Step 3: Define base case — smallest valid input
Step 4: Verify termination — each call reduces problem size
```

### The Backtracking Blueprint
```java
void backtrack(state) {
    if (isGoal(state)) {
        addToResult(state);     // Record solution
        return;
    }
    for (choice : getChoices(state)) {
        if (isValid(choice, state)) {
            makeChoice(choice); // Modify state
            backtrack(state);   // Recurse
            undoChoice(choice); // RESTORE state ← the backtrack
        }
    }
}
```

### Key Insight
> Backtracking is depth-first search on a state space tree with pruning.
> Every backtracking problem has the same skeleton — the only differences are:
> what constitutes a "choice", what is "valid", and what is the "goal".
