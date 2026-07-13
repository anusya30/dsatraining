# 🧩 Programming Assignment: Algorithm Analysis & Recursion in Java

## 📌 Objective
This assignment is designed to help students practice **Algorithm Analysis** (Time & Space Complexity) and **Recursion & Mathematical Thinking** in Java.

Students should demonstrate understanding of:

### Algorithm Analysis
- Big-O Notation
- Time Complexity (Best, Average, Worst Case)
- Space Complexity
- Comparing algorithmic strategies on the same problem

### Recursion & Mathematical Thinking
- Base Case and Recursive Case
- Call Stack behavior
- Memoization (caching to avoid redundant computation)
- Recursion vs Iteration tradeoffs

---

# 🔹 Question 1: Inventory Duplicate Detector (Algorithm Analysis)

An e-commerce warehouse receives daily shipments of products. Due to scanning errors, the product ID list often contains **duplicate entries**. Your job is to build a system that **detects all duplicate product IDs** from a given array.

### Requirements

Create a class:

`DuplicateDetector`

---

### Implement the following methods to find duplicates:

1. `findDuplicatesBruteForce(int[] productIds)`
   - Uses a **nested loop** — for each element, scan the rest of the array to check for a match.
   - Returns a list of duplicate product IDs.

2. `findDuplicatesSortAndScan(int[] productIds)`
   - First **sorts** the array (use `Arrays.sort()`).
   - Then scans the sorted array in a single pass — adjacent equal elements are duplicates.
   - Returns a list of duplicate product IDs.

---

### Requirements

Your program must demonstrate:

- **Big-O Analysis**
  - Add a static `operationCount` variable. Increment it for every comparison or set lookup inside each method.
  - Print the operation count after each method call.

- **Time Complexity Annotation**
  - Add a comment above each method stating its time complexity and a one-line justification:
    - Brute Force → O(n²) — why?
    - Sort + Scan → O(n log n) — why?

- **Space Complexity Annotation**
  - Add a comment above each method stating its space complexity:
    - Brute Force → O(1) extra space (ignoring output list)
    - Sort + Scan → O(1) extra (in-place sort) or O(n) if the original array can't be modified

- **Scaling Table**
  - Generate random arrays (with injected duplicates) of sizes: 100, 1,000, 10,000, 50,000, and 100,000.
  - Run both methods on each size and print a table:

  | Array Size | Brute Force Ops | Sort+Scan Ops |
  |-----------|----------------|---------------|

  - For sizes above 50,000, skip brute force (it will be too slow) and note this in the output.

---

### In `main()`:

- Create a sample array: `{101, 204, 305, 204, 408, 101, 507, 305, 612, 408}`.
- Run both methods and print:
  - The duplicates found.
  - The operation count for each method.
- Print the scaling comparison table.

---

# 🔹 Question 2: Recursive Text Processor (Recursion)

Design a **Recursive Text Processor** — a utility class that performs common string manipulations using **only recursion** (no loops allowed in the recursive methods).

---

### Requirements

Implement the following **recursive** methods:

1. `reverseString(String s)`
   - Returns the reversed version of the input string.
   - Base case: empty string or single character → return as-is.
   - Recursive case: last character + `reverseString(remaining characters)`
   - Example: `reverseString("hello")` → `"olleh"`

2. `isPalindrome(String s)`
   - Returns `true` if the string reads the same forwards and backwards.
   - Base case: string length ≤ 1 → return `true`.
   - Recursive case: check if first and last characters match, then recurse on the substring in between.
   - Example: `isPalindrome("racecar")` → `true`

3. `countOccurrences(String s, char target)`
   - Returns the number of times `target` appears in the string.
   - Base case: empty string → return 0.
   - Recursive case: (1 if first char matches, else 0) + `countOccurrences(rest of string, target)`
   - Example: `countOccurrences("banana", 'a')` → `3`

---

### For each method, students must:

- **Identify the base case and recursive case** — add comments in the code labeling each clearly.
- **Trace the call stack** — for one sample input per method, write a comment showing the full chain of recursive calls and return values as they unwind.
- **Handle edge cases** — null input, empty string. Add validation at the top of each method.

---

### Concepts to Demonstrate

- **Base Case & Recursive Case**
  - Every method must have a clearly labeled stopping condition. Without it → `StackOverflowError`.

- **Call Stack Understanding**
  - For `reverseString("cat")`, write a comment tracing:
    ```
    reverseString("cat")
      → 't' + reverseString("ca")
                → 'a' + reverseString("c")
                          → return "c"    ← BASE CASE
                → return "ac"
      → return "tac"
    ```

- **Recursion shrinks the problem**
  - Each recursive call operates on a **shorter string** — moving strictly toward the base case.

---

### In `main()`:

- Reverse at least 3 strings (e.g., `"algorithm"`, `"Java"`, `"a"`).
- Check palindrome for at least 4 strings (e.g., `"madam"`, `"hello"`, `"abcba"`, `""`).
- Count occurrences for at least 3 cases (e.g., `'a'` in `"abracadabra"`, `'z'` in `"hello"`, `'s'` in `"mississippi"`).
- Print call stack trace for `reverseString("code")`.

---

# 🔹 Question 3: Staircase Climbing Planner (Algorithm Analysis + Recursion)

A person is standing at the bottom of a staircase with **n steps**. They can climb either **1 step** or **2 steps** at a time. Your task is to compute how many **distinct ways** they can reach the top.

This is a classic combinatorics problem with a direct recursive structure.

---

### Mathematical Setup

```
ways(1) = 1            → {1}
ways(2) = 2            → {1+1, 2}
ways(3) = 3            → {1+1+1, 1+2, 2+1}
ways(4) = 5            → {1+1+1+1, 1+1+2, 1+2+1, 2+1+1, 2+2}
ways(n) = ways(n-1) + ways(n-2)
```

Notice: this follows the **same recurrence as Fibonacci** — but the context and application are completely different.

---

### Requirements

Implement two versions:

1. `climbNaive(int n)`
   - Pure recursive — directly translates the recurrence `ways(n) = ways(n-1) + ways(n-2)`.
   - Add a static `callCount` variable. Increment it on every function entry.

2. `climbMemo(int n)`
   - Uses **memoization** — stores results in a `long[] cache` array.
   - Uses a `boolean[] solved` array to track which values have been cached.
   - Add a static `callCount` variable. Increment it on every function entry.

---

### Implement a comparison method:

`compareApproaches(int n)`

This method should:
- Run both `climbNaive(n)` and `climbMemo(n)`.
- Record the **wall-clock time** (using `System.nanoTime()` for better precision) for each.
- Record the **call count** for each.
- Print a comparison row showing: n, result, naive call count, memo call count, naive time, memo time.

---

### Concepts to Demonstrate

- **Time Complexity Comparison**
  - `climbNaive` is O(2ⁿ) — explain in a comment why each call branches into two sub-calls, leading to exponential growth.
  - `climbMemo` is O(n) — explain in a comment why each unique sub-problem is solved exactly once.

- **Space Complexity Comparison**
  - `climbNaive` uses O(n) space — call stack depth only.
  - `climbMemo` uses O(n) space — call stack + cache array.
  - Add comments explaining each.

- **Memoization as a Time-Space Tradeoff**
  - Add a comment: "We trade O(n) extra memory for a reduction from O(2ⁿ) to O(n) time — an exponential improvement."

- **Recursion Tree Illustration**
  - For `climbNaive(5)`, draw the full recursion tree as a comment showing which sub-problems are recomputed. Label the redundant calls.

---

### Print a scaling table:

| Steps (n) | Naive Calls | Memo Calls | Naive Time (µs) | Memo Time (µs) |
|-----------|------------|------------|-----------------|----------------|

Run for n = 10, 15, 20, 25, 30, 35, 40. Mark naive as `SKIPPED` for n > 40.

---

### In `main()`:

- Print the number of ways to climb for n = 1 to 20.
- Run `compareApproaches(n)` for each value in the scaling table.
- Print a final observation: *"For 40 steps, the naive approach makes ~330 million calls. Memoization solves the same problem in 40 calls."*

---

# 📌 Submission Requirements

Students must submit:

- Complete source code (`.java` files)
- Proper method structure with clear separation of logic
- Code comments explaining:
  - Base cases and recursive cases
  - Time and Space complexity of each method (using Big-O notation)
  - Why memoization improves performance
  - Why different data structures (HashSet, arrays) affect space complexity
- Output demonstrating all required tables and traces

---

# 🎯 Evaluation Criteria

| Criteria | Marks |
|----------|-------|
| Correct duplicate detection with three complexity tiers (Q1) | 25 |
| Correct recursive string methods with call stack traces (Q2) | 25 |
| Staircase naive vs memoized comparison with benchmarking (Q3) | 25 |
| Code comments, complexity annotations, and readability | 15 |
| Program correctness and output formatting | 10 |

Total: **100 Marks**

---

# 💡 Bonus (Optional)

Choose **one** of the following enhancements:

1. **Staircase with 3-step option** — extend Q3 so the person can climb 1, 2, or **3** steps at a time. Implement both naive and memoized versions. How does the recurrence change? How does the recursion tree branching factor affect time complexity?

2. **Duplicate detection with boolean visited array** — extend Q1 with a third method that uses a `boolean[] visited` array (sized to the max product ID) to track seen IDs. Analyze its time and space complexity vs. the sort+scan approach.

3. **Tower of Hanoi** — implement the classic Tower of Hanoi puzzle recursively. Print each move. Count total moves for n disks and prove that it's always `2ⁿ - 1`. Compare with the theoretical prediction in a table for n = 1 to 20.
