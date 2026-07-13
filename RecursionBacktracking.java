import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 * RECURSION & BACKTRACKING — Complete Executable Reference
 * ============================================================
 * Topics:
 *  1. Recursion Fundamentals       (factorial, fib, power, GCD, Hanoi,
 *                                   string recursion, sum digits)
 *  2. Recursion Tree & Stack       (call stack trace, depth tracking,
 *                                   memoization comparison, tree metrics)
 *  3. Tail Recursion               (convert to tail, accumulator pattern,
 *                                   trampoline simulation)
 *  4. Backtracking Concept         (template, state space, pruning demo)
 *  5. Subset & Permutation         (subsets, permutations, combination sum,
 *                                   phone letters, duplicates)
 *  6. Constraint Problems          (N-Queens, Sudoku, word search, rat maze,
 *                                   palindrome partition, add operators)
 *  7. Interview Problems           (generate parens, restore IP, path sum II,
 *                                   decode ways, word break, gray code)
 *
 * Compile : javac RecursionBacktracking.java
 * Run     : java RecursionBacktracking
 * ============================================================
 */
public class RecursionBacktracking {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("RECURSION & BACKTRACKING — COMPLETE DEMO");

        section1_RecursionFundamentals();
        section2_RecursionTreeAndStack();
        section3_TailRecursion();
        section4_BacktrackingConcept();
        section5_SubsetsAndPermutations();
        section6_ConstraintProblems();
        section7_InterviewProblems();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — RECURSION FUNDAMENTALS
    // =========================================================
    static void section1_RecursionFundamentals() {
        printSection("1. RECURSION FUNDAMENTALS");

        // 1a. Factorial
        System.out.println("--- 1a. Factorial ---");
        for (int i = 0; i <= 10; i++)
            System.out.printf("  %2d! = %,d%n", i, factorial(i));
        System.out.println("  Iterative matches recursive: " +
                (factorial(10) == factorialIterative(10) ? "✓" : "✗"));

        // 1b. Fibonacci — naive vs memo vs DP
        System.out.println("\n--- 1b. Fibonacci (naive vs memo vs DP) ---");
        int[] fibs = {0,1,2,5,10,15,20,30};
        int[] memo = new int[35];
        System.out.printf("%-6s %-12s %-12s %-12s%n","n","Naive","Memoized","DP");
        for (int n : fibs) {
            System.out.printf("%-6d %-12d %-12d %-12d%n",
                    n, fibNaive(n), fibMemo(n, new int[n+2]), fibDP(n));
        }

        // 1b. Show exponential calls in naive
        System.out.println("\nCall counts (naive fib):");
        for (int n : new int[]{5,10,15,20}) {
            callCount = 0;
            fibCount(n);
            System.out.printf("  fib(%2d): %,d calls%n", n, callCount);
        }

        // 1c. Power function
        System.out.println("\n--- 1c. Power Function (fast exponentiation) ---");
        System.out.printf("  2^10  = %.0f  (fast)%n", power(2, 10));
        System.out.printf("  3^5   = %.0f%n", power(3, 5));
        System.out.printf("  2^-3  = %.4f%n", power(2, -3));
        System.out.printf("  10^6  = %.0f%n", power(10, 6));
        System.out.printf("  naive multiplications for 2^10: 10%n");
        System.out.printf("  fast  multiplications for 2^10: ~%d (log₂10)%n",
                (int)(Math.log(10)/Math.log(2))+1);

        // 1d. GCD
        System.out.println("\n--- 1d. GCD (Euclidean Algorithm) ---");
        int[][] pairs = {{48,18},{100,75},{17,5},{0,7},{252,105}};
        for (int[] p : pairs)
            System.out.printf("  gcd(%3d, %3d) = %d%n", p[0], p[1], gcd(p[0], p[1]));

        // 1e. Sum of digits
        System.out.println("\n--- 1e. Sum of Digits ---");
        int[] nums = {1234, 9999, 100, 0, 987654};
        for (int n : nums)
            System.out.printf("  sumDigits(%6d) = %d%n", n, sumDigits(n));

        // 1f. Palindrome check
        System.out.println("\n--- 1f. Palindrome Check (recursive) ---");
        String[] words = {"racecar", "hello", "madam", "abcba", "java", "level"};
        for (String w : words)
            System.out.printf("  %-10s → %s%n", w,
                    isPalindrome(w, 0, w.length()-1) ? "palindrome ✓" : "not palindrome");

        // 1g. Tower of Hanoi
        System.out.println("\n--- 1g. Tower of Hanoi (n=3) ---");
        hanoi(3, 'A', 'C', 'B');
        System.out.println("  Minimum moves for n disks = 2^n - 1");
        for (int n = 1; n <= 6; n++)
            System.out.printf("  n=%d disks: %d moves%n", n, (1<<n)-1);

        // 1h. String recursion
        System.out.println("\n--- 1h. String Recursion ---");
        System.out.println("  reverse('hello')  = " + reverseString("hello"));
        System.out.println("  reverse('abcde')  = " + reverseString("abcde"));
        System.out.println("  countChar('banana','a') = " + countChar("banana",'a'));
        System.out.println("  isNumeric('12345') = " + isNumeric("12345"));
        System.out.println("  isNumeric('123a5') = " + isNumeric("123a5"));

        // 1i. Subsequences
        System.out.println("\n--- 1i. All Subsequences of 'abc' ---");
        List<String> subs = new ArrayList<>();
        subsequences("abc", "", subs);
        System.out.println("  " + subs + " (" + subs.size() + " total = 2^3)");
    }

    // --- Fundamentals implementations ---
    static long factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
    static long factorialIterative(int n) {
        long r = 1; for (int i = 2; i <= n; i++) r *= i; return r;
    }
    static int fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n-1) + fibNaive(n-2);
    }
    static int fibMemo(int n, int[] memo) {
        if (n <= 1) return n;
        if (memo[n] != 0) return memo[n];
        return memo[n] = fibMemo(n-1, memo) + fibMemo(n-2, memo);
    }
    static int fibDP(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) { int c = a+b; a = b; b = c; }
        return b;
    }
    static int callCount = 0;
    static int fibCount(int n) {
        callCount++;
        if (n <= 1) return n;
        return fibCount(n-1) + fibCount(n-2);
    }
    static double power(double x, int n) {
        if (n == 0) return 1;
        if (n < 0)  { x = 1/x; n = -n; }
        if (n % 2 == 0) { double h = power(x, n/2); return h*h; }
        return x * power(x, n-1);
    }
    static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a%b); }
    static int sumDigits(int n) { return n == 0 ? 0 : (n%10) + sumDigits(n/10); }
    static boolean isPalindrome(String s, int l, int r) {
        if (l >= r) return true;
        if (s.charAt(l) != s.charAt(r)) return false;
        return isPalindrome(s, l+1, r-1);
    }
    static void hanoi(int n, char from, char to, char aux) {
        if (n == 1) { System.out.printf("  Move disk 1: %c → %c%n", from, to); return; }
        hanoi(n-1, from, aux, to);
        System.out.printf("  Move disk %d: %c → %c%n", n, from, to);
        hanoi(n-1, aux, to, from);
    }
    static String reverseString(String s) {
        if (s.isEmpty()) return s;
        return reverseString(s.substring(1)) + s.charAt(0);
    }
    static int countChar(String s, char c) {
        if (s.isEmpty()) return 0;
        return (s.charAt(0)==c?1:0) + countChar(s.substring(1), c);
    }
    static boolean isNumeric(String s) {
        if (s.isEmpty()) return true;
        if (!Character.isDigit(s.charAt(0))) return false;
        return isNumeric(s.substring(1));
    }
    static void subsequences(String s, String curr, List<String> res) {
        if (s.isEmpty()) { res.add(curr.isEmpty() ? "(empty)" : curr); return; }
        subsequences(s.substring(1), curr + s.charAt(0), res);
        subsequences(s.substring(1), curr, res);
    }

    // =========================================================
    // SECTION 2 — RECURSION TREE & STACK VISUALIZATION
    // =========================================================
    static void section2_RecursionTreeAndStack() {
        printSection("2. RECURSION TREE & STACK VISUALIZATION");

        // 2a. Factorial with depth trace
        System.out.println("--- 2a. Factorial Call Stack Trace ---");
        System.out.println("  factorial(5) call stack (building phase):");
        factorialTrace(5, 0);
        System.out.println("  (unwind phase returns values back up)");

        // 2b. Fibonacci call tree — show duplicate work
        System.out.println("\n--- 2b. Fibonacci Recursion Tree fib(5) ---");
        System.out.println("  (each indent = deeper recursion level)");
        fibTrace(5, 0, new HashMap<>());

        // 2c. Depth and node count analysis
        System.out.println("\n--- 2c. Recursion Metrics ---");
        System.out.printf("%-20s %-12s %-12s %-15s%n",
                "Problem", "Depth", "Nodes", "Space");
        System.out.printf("%-20s %-12s %-12s %-15s%n",
                "factorial(n)", "O(n)", "O(n)", "O(n)");
        System.out.printf("%-20s %-12s %-12s %-15s%n",
                "fib naive(n)", "O(n)", "O(2^n)", "O(n)");
        System.out.printf("%-20s %-12s %-12s %-15s%n",
                "fib memo(n)", "O(n)", "O(n)", "O(n)");
        System.out.printf("%-20s %-12s %-12s %-15s%n",
                "mergeSort(n)", "O(log n)", "O(n)", "O(log n) stack");
        System.out.printf("%-20s %-12s %-12s %-15s%n",
                "subsets(n)", "O(n)", "O(2^n)", "O(n)");
        System.out.printf("%-20s %-12s %-12s %-15s%n",
                "permutations(n)", "O(n)", "O(n!)", "O(n)");

        // 2d. Stack overflow demo info
        System.out.println("\n--- 2d. Stack Overflow Awareness ---");
        System.out.println("  Default Java thread stack: ~512KB");
        System.out.println("  Each stack frame: ~100-200 bytes");
        System.out.println("  Max safe depth: ~2000-5000 calls (varies by JVM)");
        System.out.println("  Fix options:");
        System.out.println("    1. Convert to iterative with explicit stack");
        System.out.println("    2. Use tail recursion + TCO language");
        System.out.println("    3. Increase stack: java -Xss8m MyClass");
        System.out.println("    4. Memoize to reduce total calls");
        maxRecursionDepth(); // safe demo
    }

    static void factorialTrace(int n, int depth) {
        String indent = "  " + "│  ".repeat(depth);
        System.out.println(indent + "├─ factorial(" + n + ") called");
        if (n <= 1) {
            System.out.println(indent + "│  └─ BASE CASE → returns 1");
            return;
        }
        factorialTrace(n-1, depth+1);
        System.out.println(indent + "│  returns " + n + " × " + factorial(n-1) + " = " + factorial(n));
    }

    static void fibTrace(int n, int depth, Map<Integer,Integer> seen) {
        String indent = "  " + "  ".repeat(depth);
        boolean dup = seen.getOrDefault(n, 0) > 0;
        System.out.println(indent + "fib(" + n + ")" + (dup ? " ← DUPLICATE CALL" : ""));
        seen.merge(n, 1, Integer::sum);
        if (n <= 1 || depth >= 4) return; // limit print depth
        fibTrace(n-1, depth+1, seen);
        fibTrace(n-2, depth+1, seen);
    }

    static void maxRecursionDepth() {
        System.out.println("\n--- 2e. Measuring Safe Recursion Depth ---");
        try {
            int depth = measureDepth(0);
            System.out.println("  Reached depth: " + depth + " (before overflow)");
        } catch (StackOverflowError e) {
            System.out.println("  StackOverflowError caught safely.");
        }
    }

    static int measureDepth(int d) {
        if (d > 10000) return d; // Safety cap for demo
        return measureDepth(d + 1);
    }

    // =========================================================
    // SECTION 3 — TAIL RECURSION
    // =========================================================
    static void section3_TailRecursion() {
        printSection("3. TAIL RECURSION");

        // 3a. Factorial — non-tail vs tail
        System.out.println("--- 3a. Factorial: Non-Tail vs Tail ---");
        System.out.println("  Non-tail factorial(10) = " + factorial(10));
        System.out.println("  Tail    factorial(10)  = " + factorialTail(10, 1));
        System.out.println("  (same result, different stack behavior)");

        // 3b. Sum — non-tail vs tail
        System.out.println("\n--- 3b. Array Sum: Non-Tail vs Tail ---");
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("  Array: " + Arrays.toString(arr));
        System.out.println("  Sum (non-tail): " + sumNonTail(arr, 0));
        System.out.println("  Sum (tail):     " + sumTail(arr, 0, 0));

        // 3c. Fibonacci tail
        System.out.println("\n--- 3c. Fibonacci: Tail Recursive ---");
        System.out.println("  fib(0..10) tail: ");
        System.out.print("  ");
        for (int i = 0; i <= 10; i++) System.out.print(fibTail(i, 0, 1) + " ");
        System.out.println();
        System.out.println("  fib(40) = " + fibTail(40, 0, 1));

        // 3d. GCD already tail recursive
        System.out.println("\n--- 3d. GCD is Already Tail Recursive ---");
        System.out.println("  gcd(b, a%b) is the last operation → tail call");
        System.out.println("  gcd(1071, 462) = " + gcd(1071, 462));

        // 3e. Reverse list tail
        System.out.println("\n--- 3e. Reverse List: Tail Recursive ---");
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("  Original: " + list);
        System.out.println("  Reversed: " + reverseTail(list, new ArrayList<>()));

        // 3f. Tail to iterative conversion insight
        System.out.println("\n--- 3f. Tail Recursion → Iteration Pattern ---");
        System.out.println("  Tail-recursive function:");
        System.out.println("    factorialTail(n, acc) = factorialTail(n-1, n*acc)");
        System.out.println("  Equivalent while loop:");
        System.out.println("    while(n>1) { acc *= n; n--; } return acc;");
        System.out.println("  Java: manually convert tail recursion to loop for performance");

        // 3g. Trampoline pattern demo
        System.out.println("\n--- 3g. Deep Recursion — Iterative Simulation ---");
        System.out.println("  Counting 1 to 10000 iteratively (simulating TCO):");
        long result = iterativeFactorial(20);
        System.out.println("  iterativeFactorial(20) = " + result);
    }

    static long factorialTail(int n, long acc) {
        if (n <= 1) return acc;
        return factorialTail(n-1, n * acc);
    }
    static int sumNonTail(int[] arr, int i) {
        if (i == arr.length) return 0;
        return arr[i] + sumNonTail(arr, i+1); // addition after return = non-tail
    }
    static int sumTail(int[] arr, int i, int acc) {
        if (i == arr.length) return acc;
        return sumTail(arr, i+1, acc + arr[i]); // last op = recursive call = tail
    }
    static long fibTail(int n, long a, long b) {
        if (n == 0) return a;
        if (n == 1) return b;
        return fibTail(n-1, b, a+b);
    }
    static List<Integer> reverseTail(List<Integer> list, List<Integer> acc) {
        if (list.isEmpty()) return acc;
        List<Integer> newAcc = new ArrayList<>();
        newAcc.add(list.get(0));
        newAcc.addAll(acc);
        return reverseTail(list.subList(1, list.size()), newAcc);
    }
    static long iterativeFactorial(int n) {
        long acc = 1;
        while (n > 1) { acc *= n; n--; } // TCO equivalent
        return acc;
    }

    // =========================================================
    // SECTION 4 — BACKTRACKING CONCEPT
    // =========================================================
    static void section4_BacktrackingConcept() {
        printSection("4. BACKTRACKING CONCEPT");

        // 4a. Template demonstration
        System.out.println("--- 4a. Backtracking Template Demo: Find paths summing to target ---");
        int[] candidates = {1, 2, 3};
        int target = 4;
        System.out.println("  Candidates: " + Arrays.toString(candidates) + ", Target: " + target);
        List<List<Integer>> combos = new ArrayList<>();
        combinationSumBasic(candidates, target, 0, new ArrayList<>(), combos);
        System.out.println("  Valid combinations: " + combos);

        // 4b. Pruning demonstration
        System.out.println("\n--- 4b. Pruning Power Demonstration ---");
        System.out.println("  Without pruning (explore all):");
        exploredCount = 0;
        solveNQueensCount(4, false);
        System.out.println("    N=4 Queens: explored " + exploredCount + " placements");
        System.out.println("  With pruning (backtrack early):");
        exploredCount = 0;
        solveNQueensCount(4, true);
        System.out.println("    N=4 Queens: explored " + exploredCount + " placements");

        // 4c. State space visualization
        System.out.println("\n--- 4c. State Space for 3 binary choices ---");
        System.out.println("  Tree of choose/skip decisions:");
        stateSpaceDemo(3, 0, new ArrayList<>());

        // 4d. Choose-Explore-Unchoose pattern
        System.out.println("\n--- 4d. Choose → Explore → Unchoose Pattern ---");
        System.out.println("  Building all binary strings of length 3:");
        buildBinaryStrings(3, new StringBuilder());
    }

    static int exploredCount = 0;
    static void solveNQueensCount(int n, boolean prune) {
        int[] board = new int[n];
        Arrays.fill(board, -1);
        countQueenPlacements(n, 0, board, prune);
    }
    static void countQueenPlacements(int n, int row, int[] board, boolean prune) {
        if (row == n) return;
        for (int col = 0; col < n; col++) {
            exploredCount++;
            if (prune && !isQueenSafe(board, row, col)) continue;
            board[row] = col;
            countQueenPlacements(n, row+1, board, prune);
            board[row] = -1;
        }
    }
    static boolean isQueenSafe(int[] board, int row, int col) {
        for (int r = 0; r < row; r++) {
            if (board[r] == col) return false;
            if (Math.abs(board[r] - col) == Math.abs(r - row)) return false;
        }
        return true;
    }
    static void stateSpaceDemo(int n, int idx, List<Integer> path) {
        String indent = "  " + "  ".repeat(idx);
        if (idx == n) {
            System.out.println(indent + "→ " + path); return;
        }
        System.out.println(indent + "choose " + (idx+1) + ":");
        path.add(idx+1); stateSpaceDemo(n, idx+1, path);
        path.remove(path.size()-1);
        System.out.println(indent + "skip " + (idx+1) + ":");
        stateSpaceDemo(n, idx+1, path);
    }
    static void buildBinaryStrings(int n, StringBuilder sb) {
        if (sb.length() == n) { System.out.println("  " + sb); return; }
        sb.append('0'); buildBinaryStrings(n, sb); sb.deleteCharAt(sb.length()-1);
        sb.append('1'); buildBinaryStrings(n, sb); sb.deleteCharAt(sb.length()-1);
    }
    static void combinationSumBasic(int[] cands, int target, int start,
                                      List<Integer> curr, List<List<Integer>> res) {
        if (target == 0) { res.add(new ArrayList<>(curr)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > target) break;
            curr.add(cands[i]);
            combinationSumBasic(cands, target - cands[i], i, curr, res);
            curr.remove(curr.size()-1);
        }
    }

    // =========================================================
    // SECTION 5 — SUBSETS & PERMUTATIONS
    // =========================================================
    static void section5_SubsetsAndPermutations() {
        printSection("5. SUBSETS & PERMUTATIONS");

        // 5a. Subsets
        System.out.println("--- 5a. Generate All Subsets ---");
        int[] nums = {1, 2, 3};
        System.out.println("  Input: " + Arrays.toString(nums));
        List<List<Integer>> res1 = new ArrayList<>();
        subsets(nums, 0, new ArrayList<>(), res1);
        System.out.println("  Method 1 (include/exclude): " + res1 + " (size=" + res1.size() + ")");
        System.out.println("  Method 2 (iterative):       " + subsetsIterative(nums));
        System.out.println("  Method 3 (bitmask):         " + subsetsBitmask(nums));

        // 5b. Subsets with duplicates
        System.out.println("\n--- 5b. Subsets with Duplicates ---");
        int[] nums2 = {1, 2, 2};
        Arrays.sort(nums2);
        System.out.println("  Input (sorted): " + Arrays.toString(nums2));
        List<List<Integer>> res2 = new ArrayList<>();
        subsetsWithDups(nums2, 0, new ArrayList<>(), res2);
        System.out.println("  Unique subsets: " + res2);

        // 5c. Permutations
        System.out.println("\n--- 5c. All Permutations ---");
        int[] pnums = {1, 2, 3};
        System.out.println("  Input: " + Arrays.toString(pnums));
        List<List<Integer>> perms1 = new ArrayList<>();
        permutationsSwap(pnums.clone(), 0, perms1);
        System.out.println("  Swap method (" + perms1.size() + " = 3!): " + perms1);
        List<List<Integer>> perms2 = new ArrayList<>();
        permutationsUsed(pnums, new boolean[pnums.length], new ArrayList<>(), perms2);
        System.out.println("  Used[] method: " + perms2);

        // 5d. Permutations with duplicates
        System.out.println("\n--- 5d. Permutations with Duplicates ---");
        int[] pnums2 = {1, 1, 2};
        Arrays.sort(pnums2);
        System.out.println("  Input (sorted): " + Arrays.toString(pnums2));
        List<List<Integer>> permsUniq = new ArrayList<>();
        permsUnique(pnums2, new boolean[pnums2.length], new ArrayList<>(), permsUniq);
        System.out.println("  Unique perms (" + permsUniq.size() + " not 6): " + permsUniq);

        // 5e. Combination sum
        System.out.println("\n--- 5e. Combination Sum (reuse allowed) ---");
        int[] cands = {2, 3, 6, 7};
        System.out.println("  Candidates: " + Arrays.toString(cands) + ", target=7");
        List<List<Integer>> combos = new ArrayList<>();
        combinationSum(cands, 7, 0, new ArrayList<>(), combos);
        System.out.println("  Combinations: " + combos);

        System.out.println("\n  Candidates: [10,1,2,7,6,1,5] (with dups), target=8");
        int[] cands2 = {10, 1, 2, 7, 6, 1, 5};
        Arrays.sort(cands2);
        List<List<Integer>> combos2 = new ArrayList<>();
        combinationSum2(cands2, 8, 0, new ArrayList<>(), combos2);
        System.out.println("  Combinations: " + combos2);

        // 5f. Letter combinations
        System.out.println("\n--- 5f. Letter Combinations of Phone Number ---");
        String[] inputs = {"2", "23", "234"};
        for (String s : inputs)
            System.out.println("  \"" + s + "\" → " + letterCombinations(s));

        // 5g. Next permutation
        System.out.println("\n--- 5g. Next Permutation ---");
        int[][] permsToNext = {{1,2,3},{3,2,1},{1,1,5},{1,3,2}};
        for (int[] p : permsToNext) {
            int[] copy = p.clone();
            nextPermutation(copy);
            System.out.println("  " + Arrays.toString(p) + " → " + Arrays.toString(copy));
        }
    }

    // --- Subset & Permutation Implementations ---
    static void subsets(int[] nums, int idx, List<Integer> curr, List<List<Integer>> res) {
        if (idx == nums.length) { res.add(new ArrayList<>(curr)); return; }
        curr.add(nums[idx]);
        subsets(nums, idx+1, curr, res);
        curr.remove(curr.size()-1);
        subsets(nums, idx+1, curr, res);
    }
    static List<List<Integer>> subsetsIterative(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        res.add(new ArrayList<>());
        for (int n : nums) {
            int sz = res.size();
            for (int i = 0; i < sz; i++) {
                List<Integer> sub = new ArrayList<>(res.get(i));
                sub.add(n); res.add(sub);
            }
        }
        return res;
    }
    static List<List<Integer>> subsetsBitmask(int[] nums) {
        int n = nums.length;
        List<List<Integer>> res = new ArrayList<>();
        for (int mask = 0; mask < (1<<n); mask++) {
            List<Integer> sub = new ArrayList<>();
            for (int i = 0; i < n; i++)
                if ((mask & (1<<i)) != 0) sub.add(nums[i]);
            res.add(sub);
        }
        return res;
    }
    static void subsetsWithDups(int[] nums, int start, List<Integer> curr,
                                  List<List<Integer>> res) {
        res.add(new ArrayList<>(curr));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i-1]) continue;
            curr.add(nums[i]);
            subsetsWithDups(nums, i+1, curr, res);
            curr.remove(curr.size()-1);
        }
    }
    static void permutationsSwap(int[] nums, int start, List<List<Integer>> res) {
        if (start == nums.length) {
            List<Integer> p = new ArrayList<>();
            for (int n : nums) p.add(n);
            res.add(p); return;
        }
        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);
            permutationsSwap(nums, start+1, res);
            swap(nums, start, i);
        }
    }
    static void permutationsUsed(int[] nums, boolean[] used, List<Integer> curr,
                                   List<List<Integer>> res) {
        if (curr.size() == nums.length) { res.add(new ArrayList<>(curr)); return; }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true; curr.add(nums[i]);
            permutationsUsed(nums, used, curr, res);
            curr.remove(curr.size()-1); used[i] = false;
        }
    }
    static void permsUnique(int[] nums, boolean[] used, List<Integer> curr,
                              List<List<Integer>> res) {
        if (curr.size() == nums.length) { res.add(new ArrayList<>(curr)); return; }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) continue;
            used[i] = true; curr.add(nums[i]);
            permsUnique(nums, used, curr, res);
            curr.remove(curr.size()-1); used[i] = false;
        }
    }
    static void combinationSum(int[] cands, int target, int start,
                                 List<Integer> curr, List<List<Integer>> res) {
        if (target == 0) { res.add(new ArrayList<>(curr)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > target) break;
            curr.add(cands[i]);
            combinationSum(cands, target-cands[i], i, curr, res); // i not i+1 → reuse
            curr.remove(curr.size()-1);
        }
    }
    static void combinationSum2(int[] cands, int target, int start,
                                  List<Integer> curr, List<List<Integer>> res) {
        if (target == 0) { res.add(new ArrayList<>(curr)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > target) break;
            if (i > start && cands[i] == cands[i-1]) continue;
            curr.add(cands[i]);
            combinationSum2(cands, target-cands[i], i+1, curr, res);
            curr.remove(curr.size()-1);
        }
    }
    static List<String> letterCombinations(String digits) {
        if (digits.isEmpty()) return new ArrayList<>();
        String[] map = {"","","abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
        List<String> res = new ArrayList<>();
        phoneBacktrack(digits, 0, new StringBuilder(), map, res);
        return res;
    }
    static void phoneBacktrack(String digits, int idx, StringBuilder curr,
                                 String[] map, List<String> res) {
        if (idx == digits.length()) { res.add(curr.toString()); return; }
        for (char c : map[digits.charAt(idx)-'0'].toCharArray()) {
            curr.append(c);
            phoneBacktrack(digits, idx+1, curr, map, res);
            curr.deleteCharAt(curr.length()-1);
        }
    }
    static void nextPermutation(int[] nums) {
        int n = nums.length, i = n-2;
        while (i >= 0 && nums[i] >= nums[i+1]) i--;
        if (i >= 0) {
            int j = n-1;
            while (nums[j] <= nums[i]) j--;
            swap(nums, i, j);
        }
        // Reverse from i+1 to end
        int l = i+1, r = n-1;
        while (l < r) { swap(nums, l++, r--); }
    }

    // =========================================================
    // SECTION 6 — CONSTRAINT PROBLEMS
    // =========================================================
    static void section6_ConstraintProblems() {
        printSection("6. CONSTRAINT PROBLEMS");

        // 6a. N-Queens
        System.out.println("--- 6a. N-Queens Problem ---");
        for (int n = 1; n <= 8; n++) {
            List<List<String>> sol = solveNQueens(n);
            System.out.printf("  N=%d: %d solution(s)%n", n, sol.size());
        }
        System.out.println("\n  N=4 all solutions:");
        for (List<String> board : solveNQueens(4)) {
            System.out.println("  ┌────┐");
            for (String row : board) System.out.println("  │" + row + "│");
            System.out.println("  └────┘");
        }

        // 6b. Sudoku solver
        System.out.println("--- 6b. Sudoku Solver ---");
        char[][] sudoku = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        System.out.println("  Before:");
        printSudoku(sudoku);
        solveSudoku(sudoku);
        System.out.println("  After solving:");
        printSudoku(sudoku);

        // 6c. Word search
        System.out.println("--- 6c. Word Search in Grid ---");
        char[][] grid = {
            {'A','B','C','E'},
            {'S','F','C','S'},
            {'A','D','E','E'}
        };
        System.out.println("  Grid:");
        for (char[] row : grid) System.out.println("  " + Arrays.toString(row));
        String[] searchWords = {"ABCCED","SEE","ABCB","SFDE"};
        for (String w : searchWords)
            System.out.printf("  Search '%s' → %s%n", w, wordSearch(grid, w));

        // 6d. Rat in maze
        System.out.println("\n--- 6d. Rat in a Maze ---");
        int[][] maze = {
            {1,0,0,0},
            {1,1,0,1},
            {0,1,0,0},
            {1,1,1,1}
        };
        System.out.println("  Maze (1=open, 0=blocked):");
        for (int[] row : maze) System.out.println("  " + Arrays.toString(row));
        System.out.println("  Paths from (0,0) to (n-1,n-1): " + ratInMaze(maze));

        // 6e. Palindrome partitioning
        System.out.println("\n--- 6e. Palindrome Partitioning ---");
        String[] strs = {"aab", "a", "racecar", "abcba"};
        for (String s : strs) {
            List<List<String>> parts = palindromePartition(s);
            System.out.printf("  \"%s\" → %s (%d ways)%n", s, parts, parts.size());
        }

        // 6f. Add operators
        System.out.println("\n--- 6f. Add Operators to Reach Target ---");
        System.out.println("  \"123\", target=6 → " + addOperators("123", 6));
        System.out.println("  \"232\", target=8 → " + addOperators("232", 8));
        System.out.println("  \"105\", target=5 → " + addOperators("105", 5));
    }

    // --- Constraint Problem Implementations ---
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> res = new ArrayList<>();
        int[] queens = new int[n];
        Arrays.fill(queens, -1);
        boolean[] cols = new boolean[n];
        boolean[] d1   = new boolean[2*n-1];
        boolean[] d2   = new boolean[2*n-1];
        nQueens(n, 0, queens, cols, d1, d2, res);
        return res;
    }
    static void nQueens(int n, int row, int[] queens, boolean[] cols,
                          boolean[] d1, boolean[] d2, List<List<String>> res) {
        if (row == n) { res.add(buildBoard(queens, n)); return; }
        for (int col = 0; col < n; col++) {
            int dd1 = row-col+n-1, dd2 = row+col;
            if (cols[col] || d1[dd1] || d2[dd2]) continue;
            queens[row] = col;
            cols[col] = d1[dd1] = d2[dd2] = true;
            nQueens(n, row+1, queens, cols, d1, d2, res);
            queens[row] = -1;
            cols[col] = d1[dd1] = d2[dd2] = false;
        }
    }
    static List<String> buildBoard(int[] queens, int n) {
        List<String> board = new ArrayList<>();
        for (int r = 0; r < n; r++) {
            char[] row = new char[n];
            Arrays.fill(row, '.');
            row[queens[r]] = 'Q';
            board.add(new String(row));
        }
        return board;
    }
    static boolean solveSudoku(char[][] board) {
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') continue;
                for (char ch = '1'; ch <= '9'; ch++) {
                    if (validSudoku(board, r, c, ch)) {
                        board[r][c] = ch;
                        if (solveSudoku(board)) return true;
                        board[r][c] = '.';
                    }
                }
                return false;
            }
        return true;
    }
    static boolean validSudoku(char[][] b, int r, int c, char ch) {
        for (int i = 0; i < 9; i++) {
            if (b[r][i] == ch || b[i][c] == ch ||
                b[r/3*3+i/3][c/3*3+i%3] == ch) return false;
        }
        return true;
    }
    static void printSudoku(char[][] b) {
        for (int r = 0; r < 9; r++) {
            if (r % 3 == 0) System.out.println("  +---------+---------+---------+");
            System.out.print("  ");
            for (int c = 0; c < 9; c++) {
                if (c % 3 == 0) System.out.print("| ");
                System.out.print(b[r][c] + " ");
            }
            System.out.println("|");
        }
        System.out.println("  +---------+---------+---------+");
    }
    static boolean wordSearch(char[][] board, String word) {
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board[0].length; c++)
                if (dfsWord(board, word, r, c, 0)) return true;
        return false;
    }
    static boolean dfsWord(char[][] board, String word, int r, int c, int idx) {
        if (idx == word.length()) return true;
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return false;
        if (board[r][c] != word.charAt(idx)) return false;
        char tmp = board[r][c]; board[r][c] = '#';
        boolean found = dfsWord(board,word,r+1,c,idx+1)||dfsWord(board,word,r-1,c,idx+1)||
                        dfsWord(board,word,r,c+1,idx+1)||dfsWord(board,word,r,c-1,idx+1);
        board[r][c] = tmp;
        return found;
    }
    static List<String> ratInMaze(int[][] maze) {
        int n = maze.length;
        List<String> paths = new ArrayList<>();
        boolean[][] vis = new boolean[n][n];
        if (maze[0][0] == 1) ratDFS(maze, 0, 0, n, "", vis, paths);
        return paths;
    }
    static void ratDFS(int[][] maze, int r, int c, int n, String path,
                         boolean[][] vis, List<String> paths) {
        if (r == n-1 && c == n-1) { paths.add(path); return; }
        int[] dr = {1,-1,0,0}, dc = {0,0,1,-1};
        char[] dir = {'D','U','R','L'};
        vis[r][c] = true;
        for (int i = 0; i < 4; i++) {
            int nr = r+dr[i], nc = c+dc[i];
            if (nr>=0&&nr<n&&nc>=0&&nc<n&&maze[nr][nc]==1&&!vis[nr][nc])
                ratDFS(maze, nr, nc, n, path+dir[i], vis, paths);
        }
        vis[r][c] = false;
    }
    static List<List<String>> palindromePartition(String s) {
        List<List<String>> res = new ArrayList<>();
        palinPart(s, 0, new ArrayList<>(), res);
        return res;
    }
    static void palinPart(String s, int start, List<String> curr,
                            List<List<String>> res) {
        if (start == s.length()) { res.add(new ArrayList<>(curr)); return; }
        for (int end = start+1; end <= s.length(); end++) {
            String sub = s.substring(start, end);
            if (isPalin(sub)) {
                curr.add(sub);
                palinPart(s, end, curr, res);
                curr.remove(curr.size()-1);
            }
        }
    }
    static boolean isPalin(String s) {
        int l=0, r=s.length()-1;
        while (l<r) if (s.charAt(l++)!=s.charAt(r--)) return false;
        return true;
    }
    static List<String> addOperators(String num, int target) {
        List<String> res = new ArrayList<>();
        addOps(num, target, 0, 0, 0, "", res);
        return res;
    }
    static void addOps(String num, int target, int idx,
                         long eval, long mult, String expr, List<String> res) {
        if (idx == num.length()) { if (eval == target) res.add(expr); return; }
        for (int i = idx; i < num.length(); i++) {
            if (i != idx && num.charAt(idx) == '0') break;
            String cur = num.substring(idx, i+1);
            long val = Long.parseLong(cur);
            if (idx == 0) addOps(num,target,i+1,val,val,cur,res);
            else {
                addOps(num,target,i+1,eval+val, val,expr+"+"+cur,res);
                addOps(num,target,i+1,eval-val,-val,expr+"-"+cur,res);
                addOps(num,target,i+1,eval-mult+mult*val,mult*val,expr+"*"+cur,res);
            }
        }
    }

    // =========================================================
    // SECTION 7 — INTERVIEW-LEVEL PROBLEMS
    // =========================================================
    static void section7_InterviewProblems() {
        printSection("7. INTERVIEW-LEVEL PROBLEMS");

        // P1. Generate Parentheses
        System.out.println("--- P1. Generate Valid Parentheses (LC 22) ---");
        for (int n = 1; n <= 4; n++)
            System.out.println("  n=" + n + ": " + generateParentheses(n));

        // P2. Restore IP Addresses
        System.out.println("\n--- P2. Restore IP Addresses (LC 93) ---");
        String[] ips = {"25525511135","0000","1111111111","010010"};
        for (String s : ips)
            System.out.println("  \"" + s + "\" → " + restoreIpAddresses(s));

        // P3. Path Sum II
        System.out.println("\n--- P3. Path Sum II — Root to Leaf (LC 113) ---");
        TreeNode tree = new TreeNode(5,
            new TreeNode(4, new TreeNode(11, new TreeNode(7), new TreeNode(2)), null),
            new TreeNode(8, new TreeNode(13), new TreeNode(4,
                new TreeNode(5), new TreeNode(1))));
        System.out.println("  Tree: 5→4→11→{7,2}, 5→8→{13,4→{5,1}}");
        System.out.println("  target=22 → " + pathSum(tree, 22));
        System.out.println("  target=27 → " + pathSum(tree, 27));

        // P4. Decode Ways
        System.out.println("\n--- P4. Decode Ways (LC 91) ---");
        String[] codes = {"12","226","0","06","11106","111111111111"};
        for (String s : codes)
            System.out.println("  \"" + s + "\" → " + numDecodings(s) + " ways");

        // P5. Word Break
        System.out.println("\n--- P5. Word Break (LC 139) ---");
        System.out.println("  \"leetcode\"   [leet,code]       → "
                + wordBreak("leetcode", Arrays.asList("leet","code")));
        System.out.println("  \"applepenapple\" [apple,pen]  → "
                + wordBreak("applepenapple", Arrays.asList("apple","pen")));
        System.out.println("  \"catsandog\" [cats,dog,sand]  → "
                + wordBreak("catsandog", Arrays.asList("cats","dog","sand","an","cat")));

        // P6. Flood Fill
        System.out.println("\n--- P6. Flood Fill (LC 733) ---");
        int[][] img = {{1,1,1},{1,1,0},{1,0,1}};
        System.out.println("  Before: " + Arrays.deepToString(img));
        floodFill(img, 1, 1, 2);
        System.out.println("  After fill(1,1,2): " + Arrays.deepToString(img));

        // P7. Gray Code
        System.out.println("\n--- P7. Gray Code (LC 89) ---");
        for (int n = 1; n <= 4; n++)
            System.out.println("  n=" + n + ": " + grayCode(n));

        // P8. All paths in DAG
        System.out.println("\n--- P8. All Paths Source to Target (LC 797) ---");
        int[][] graph = {{1,2},{3},{3},{}};
        System.out.println("  Graph: " + Arrays.deepToString(graph));
        System.out.println("  All paths 0→3: " + allPathsSourceTarget(graph));

        // P9. Partition Equal Subset Sum
        System.out.println("\n--- P9. Partition Equal Subset Sum (LC 416) ---");
        int[][] tests = {{1,5,11,5},{1,2,3,5},{3,3,3,4,5}};
        for (int[] t : tests)
            System.out.println("  " + Arrays.toString(t) + " → " + canPartition(t));

        // P10. Expression evaluation
        System.out.println("\n--- P10. Different Ways to Add Parentheses (LC 241) ---");
        System.out.println("  \"2-1-1\" → " + diffWaysCompute("2-1-1"));
        System.out.println("  \"2*3-4*5\" → " + diffWaysCompute("2*3-4*5"));
    }

    // --- Interview Problem Implementations ---
    static List<String> generateParentheses(int n) {
        List<String> res = new ArrayList<>();
        parenBT(n, 0, 0, new StringBuilder(), res);
        return res;
    }
    static void parenBT(int n, int open, int close, StringBuilder sb, List<String> res) {
        if (sb.length() == 2*n) { res.add(sb.toString()); return; }
        if (open < n)     { sb.append('('); parenBT(n,open+1,close,sb,res); sb.deleteCharAt(sb.length()-1); }
        if (close < open) { sb.append(')'); parenBT(n,open,close+1,sb,res); sb.deleteCharAt(sb.length()-1); }
    }
    static List<String> restoreIpAddresses(String s) {
        List<String> res = new ArrayList<>();
        ipBT(s, 0, new ArrayList<>(), res);
        return res;
    }
    static void ipBT(String s, int start, List<String> parts, List<String> res) {
        if (parts.size()==4 && start==s.length()) { res.add(String.join(".",parts)); return; }
        if (parts.size()==4 || start==s.length()) return;
        for (int len = 1; len <= 3; len++) {
            if (start+len > s.length()) break;
            String seg = s.substring(start, start+len);
            if (seg.length()>1 && seg.charAt(0)=='0') break;
            if (len==3 && Integer.parseInt(seg)>255) break;
            parts.add(seg);
            ipBT(s, start+len, parts, res);
            parts.remove(parts.size()-1);
        }
    }
    static List<List<Integer>> pathSum(TreeNode root, int target) {
        List<List<Integer>> res = new ArrayList<>();
        pathDFS(root, target, new ArrayList<>(), res);
        return res;
    }
    static void pathDFS(TreeNode node, int rem, List<Integer> path,
                          List<List<Integer>> res) {
        if (node == null) return;
        path.add(node.val);
        if (node.left==null && node.right==null && rem==node.val)
            res.add(new ArrayList<>(path));
        else {
            pathDFS(node.left, rem-node.val, path, res);
            pathDFS(node.right, rem-node.val, path, res);
        }
        path.remove(path.size()-1);
    }
    static int numDecodings(String s) {
        return decodeDP(s, 0, new Integer[s.length()]);
    }
    static int decodeDP(String s, int idx, Integer[] memo) {
        if (idx == s.length()) return 1;
        if (s.charAt(idx) == '0') return 0;
        if (memo[idx] != null) return memo[idx];
        int res = decodeDP(s, idx+1, memo);
        if (idx+1 < s.length()) {
            int two = Integer.parseInt(s.substring(idx, idx+2));
            if (two >= 10 && two <= 26) res += decodeDP(s, idx+2, memo);
        }
        return memo[idx] = res;
    }
    static boolean wordBreak(String s, List<String> dict) {
        return wbHelper(s, new HashSet<>(dict), 0, new Boolean[s.length()]);
    }
    static boolean wbHelper(String s, Set<String> dict, int start, Boolean[] memo) {
        if (start == s.length()) return true;
        if (memo[start] != null) return memo[start];
        for (int end = start+1; end <= s.length(); end++)
            if (dict.contains(s.substring(start,end)) && wbHelper(s,dict,end,memo))
                return memo[start] = true;
        return memo[start] = false;
    }
    static int[][] floodFill(int[][] img, int r, int c, int newColor) {
        if (img[r][c] != newColor) floodDFS(img, r, c, img[r][c], newColor);
        return img;
    }
    static void floodDFS(int[][] img, int r, int c, int orig, int newColor) {
        if (r<0||r>=img.length||c<0||c>=img[0].length||img[r][c]!=orig) return;
        img[r][c] = newColor;
        floodDFS(img,r+1,c,orig,newColor); floodDFS(img,r-1,c,orig,newColor);
        floodDFS(img,r,c+1,orig,newColor); floodDFS(img,r,c-1,orig,newColor);
    }
    static List<Integer> grayCode(int n) {
        if (n == 0) return Arrays.asList(0);
        List<Integer> prev = grayCode(n-1);
        List<Integer> res = new ArrayList<>(prev);
        int bit = 1 << (n-1);
        for (int i = prev.size()-1; i >= 0; i--) res.add(prev.get(i) | bit);
        return res;
    }
    static List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> res = new ArrayList<>();
        dagDFS(graph, 0, new ArrayList<>(Collections.singletonList(0)), res);
        return res;
    }
    static void dagDFS(int[][] graph, int node, List<Integer> path,
                         List<List<Integer>> res) {
        if (node == graph.length-1) { res.add(new ArrayList<>(path)); return; }
        for (int next : graph[node]) {
            path.add(next);
            dagDFS(graph, next, path, res);
            path.remove(path.size()-1);
        }
    }
    static boolean canPartition(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0) return false;
        return partitionHelper(nums, 0, sum/2, new Boolean[nums.length][sum/2+1]);
    }
    static boolean partitionHelper(int[] nums, int idx, int target, Boolean[][] memo) {
        if (target == 0) return true;
        if (idx >= nums.length || target < 0) return false;
        if (memo[idx][target] != null) return memo[idx][target];
        return memo[idx][target] = partitionHelper(nums,idx+1,target-nums[idx],memo)
                                || partitionHelper(nums,idx+1,target,memo);
    }
    static List<Integer> diffWaysCompute(String expr) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c=='+' || c=='-' || c=='*') {
                List<Integer> left  = diffWaysCompute(expr.substring(0,i));
                List<Integer> right = diffWaysCompute(expr.substring(i+1));
                for (int l : left) for (int r : right) {
                    if (c=='+') res.add(l+r);
                    else if (c=='-') res.add(l-r);
                    else res.add(l*r);
                }
            }
        }
        if (res.isEmpty()) res.add(Integer.parseInt(expr)); // Pure number
        return res;
    }

    // =========================================================
    // HELPER DATA STRUCTURES
    // =========================================================
    static class TreeNode {
        int val; TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val; this.left = left; this.right = right;
        }
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    static void swap(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
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
