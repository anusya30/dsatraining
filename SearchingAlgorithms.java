import java.util.*;

/**
 * ============================================================
 * SEARCHING ALGORITHMS — Complete Executable Reference
 * ============================================================
 * Topics covered:
 *  1. Why Searching Matters        (demonstration)
 *  2. Linear Search                (basic, all occurrences, string, sentinel)
 *  3. Binary Search                (iterative, recursive, overflow-safe)
 *  4. Binary Search Variations     (first/last, floor/ceil, rotated, peak,
 *                                   answer search, sqrt, matrix)
 *  5. Time & Space Complexity      (live comparison demo)
 *  6. Real-World Systems           (DB lookup, git bisect, autocomplete,
 *                                   load balancer, spell check, order book)
 *  7. Interview-Level Problems     (LeetCode 35, 33, 153, 162, 4, 875, 278, 1351)
 *
 * Compile : javac SearchingAlgorithms.java
 * Run     : java SearchingAlgorithms
 * ============================================================
 */
public class SearchingAlgorithms {

    // =========================================================
    // MAIN — runs all sections with output
    // =========================================================
    public static void main(String[] args) {
        printBanner("SEARCHING ALGORITHMS — COMPLETE DEMO");

        section1_WhySearchingMatters();
        section2_LinearSearch();
        section3_BinarySearch();
        section4_BinarySearchVariations();
        section5_ComplexityComparison();
        section6_RealWorldSystems();
        section7_InterviewProblems();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — WHY SEARCHING MATTERS
    // =========================================================
    static void section1_WhySearchingMatters() {
        printSection("1. WHY SEARCHING MATTERS");

        int n = 1_000_000;
        int[] bigArray = new int[n];
        for (int i = 0; i < n; i++) bigArray[i] = i * 2; // sorted even numbers

        int target = 999_998; // near the end

        // Linear search timing
        long start = System.nanoTime();
        int linResult = linearSearch(bigArray, target);
        long linTime = System.nanoTime() - start;

        // Binary search timing
        start = System.nanoTime();
        int binResult = binarySearch(bigArray, target);
        long binTime = System.nanoTime() - start;

        System.out.println("Array size        : " + n + " elements");
        System.out.println("Target            : " + target);
        System.out.println("Linear Search     : found at index " + linResult
                + " | time: " + linTime + " ns");
        System.out.println("Binary Search     : found at index " + binResult
                + " | time: " + binTime + " ns");
        System.out.printf("Speedup           : ~%.0fx faster%n",
                (double) linTime / Math.max(binTime, 1));

        System.out.println("\nlog₂ scale — max comparisons needed:");
        int[] sizes = {10, 100, 1_000, 1_000_000, 1_000_000_000};
        for (int size : sizes) {
            System.out.printf("  n=%-15d  linear=%-15d  binary=%-3d%n",
                    size, size, (int) (Math.log(size) / Math.log(2)) + 1);
        }
    }

    // =========================================================
    // SECTION 2 — LINEAR SEARCH
    // =========================================================
    static void section2_LinearSearch() {
        printSection("2. LINEAR SEARCH");

        int[] arr = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        System.out.println("Array: " + Arrays.toString(arr));

        // 2a. Basic linear search
        System.out.println("\n--- 2a. Basic Linear Search ---");
        System.out.println("Search 7  → index: " + linearSearch(arr, 7));
        System.out.println("Search 10 → index: " + linearSearch(arr, 10) + " (not found)");

        // 2b. Find all occurrences
        System.out.println("\n--- 2b. Find All Occurrences ---");
        int[] arrDupes = {3, 1, 4, 1, 5, 9, 2, 6, 1, 3};
        System.out.println("Array: " + Arrays.toString(arrDupes));
        System.out.println("All indices of 1: " + linearSearchAll(arrDupes, 1));
        System.out.println("All indices of 3: " + linearSearchAll(arrDupes, 3));

        // 2c. String linear search
        System.out.println("\n--- 2c. String Linear Search ---");
        String[] names = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
        System.out.println("Names: " + Arrays.toString(names));
        System.out.println("Search 'Charlie' → index: " + linearSearchString(names, "Charlie"));
        System.out.println("Search 'Zara'    → index: " + linearSearchString(names, "Zara"));

        // 2d. Sentinel linear search
        System.out.println("\n--- 2d. Sentinel Linear Search ---");
        int[] sentArr = {5, 3, 8, 1, 9, 2, 7, 4, 6, 0}; // extra slot for sentinel
        System.out.println("Array: " + Arrays.toString(sentArr));
        System.out.println("Search 9 (sentinel) → index: " + sentinelLinearSearch(sentArr, 9));

        // 2e. Linear search on objects
        System.out.println("\n--- 2e. Linear Search on Objects ---");
        Product[] products = {
            new Product(101, "Laptop", 999.99),
            new Product(102, "Mouse", 29.99),
            new Product(103, "Keyboard", 79.99),
            new Product(104, "Monitor", 399.99)
        };
        Product found = linearSearchProduct(products, 103);
        System.out.println("Search productId=103 → "
                + (found != null ? found : "not found"));
    }

    // --- Linear Search Implementations ---

    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    static List<Integer> linearSearchAll(int[] arr, int target) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) indices.add(i);
        }
        return indices;
    }

    static int linearSearchString(String[] arr, String target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(target)) return i;
        }
        return -1;
    }

    static int sentinelLinearSearch(int[] arr, int target) {
        int n = arr.length;
        int last = arr[n - 1];
        arr[n - 1] = target;            // Place sentinel — eliminates bounds check
        int i = 0;
        while (arr[i] != target) i++;
        arr[n - 1] = last;              // Restore original last element
        if (i < n - 1 || arr[n - 1] == target) return i;
        return -1;
    }

    static Product linearSearchProduct(Product[] products, int id) {
        for (Product p : products) {
            if (p.id == id) return p;
        }
        return null;
    }

    // =========================================================
    // SECTION 3 — BINARY SEARCH
    // =========================================================
    static void section3_BinarySearch() {
        printSection("3. BINARY SEARCH");

        int[] sorted = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        System.out.println("Sorted Array: " + Arrays.toString(sorted));

        // 3a. Iterative
        System.out.println("\n--- 3a. Iterative Binary Search ---");
        System.out.println("Search 7  → index: " + binarySearch(sorted, 7));
        System.out.println("Search 1  → index: " + binarySearch(sorted, 1));
        System.out.println("Search 19 → index: " + binarySearch(sorted, 19));
        System.out.println("Search 10 → index: " + binarySearch(sorted, 10) + " (not found)");

        // 3b. Recursive
        System.out.println("\n--- 3b. Recursive Binary Search ---");
        System.out.println("Search 13 (recursive) → index: "
                + binarySearchRecursive(sorted, 13, 0, sorted.length - 1));
        System.out.println("Search 6  (recursive) → index: "
                + binarySearchRecursive(sorted, 6, 0, sorted.length - 1));

        // 3c. Overflow safety demo
        System.out.println("\n--- 3c. Overflow Safety ---");
        System.out.println("Safe   mid = low + (high - low) / 2");
        System.out.println("Unsafe mid = (low + high) / 2  ← can overflow for large indices");
        int low = 1_500_000_000, high = 1_600_000_000;
        // Demonstrate: (low + high) overflows
        long unsafeMid = ((long) low + high) / 2; // cast to long to show actual value
        int safeMid = low + (high - low) / 2;
        System.out.println("low=" + low + ", high=" + high);
        System.out.println("Safe   mid = " + safeMid);
        System.out.println("Unsafe sum = " + (low + high) + " (overflowed to negative!)");
        System.out.println("Actual mid = " + unsafeMid);

        // 3d. Step-by-step trace
        System.out.println("\n--- 3d. Step-by-Step Trace ---");
        binarySearchVerbose(sorted, 7);
    }

    // --- Binary Search Implementations ---

    static int binarySearch(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;    // Overflow-safe
            if (arr[mid] == target)  return mid;
            else if (arr[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    static int binarySearchRecursive(int[] arr, int target, int low, int high) {
        if (low > high) return -1;
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) return binarySearchRecursive(arr, target, mid + 1, high);
        return binarySearchRecursive(arr, target, low, mid - 1);
    }

    static void binarySearchVerbose(int[] arr, int target) {
        int low = 0, high = arr.length - 1, step = 1;
        System.out.println("Searching for " + target + " in " + Arrays.toString(arr));
        while (low <= high) {
            int mid = low + (high - low) / 2;
            System.out.printf("  Step %d: low=%d high=%d mid=%d arr[mid]=%d → %s%n",
                    step++, low, high, mid, arr[mid],
                    arr[mid] == target ? "FOUND!" :
                    arr[mid] < target  ? "go RIGHT" : "go LEFT");
            if (arr[mid] == target) return;
            if (arr[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        System.out.println("  Not found.");
    }

    // =========================================================
    // SECTION 4 — BINARY SEARCH VARIATIONS
    // =========================================================
    static void section4_BinarySearchVariations() {
        printSection("4. BINARY SEARCH VARIATIONS");

        int[] dupes = {1, 2, 2, 2, 2, 3, 4, 5};
        System.out.println("Array with duplicates: " + Arrays.toString(dupes));

        // 4a. First occurrence
        System.out.println("\n--- 4a. Find First Occurrence ---");
        System.out.println("First index of 2: " + findFirst(dupes, 2));  // expects 1
        System.out.println("First index of 5: " + findFirst(dupes, 5));  // expects 7
        System.out.println("First index of 9: " + findFirst(dupes, 9));  // expects -1

        // 4b. Last occurrence
        System.out.println("\n--- 4b. Find Last Occurrence ---");
        System.out.println("Last index of 2: " + findLast(dupes, 2));   // expects 4
        System.out.println("Last index of 1: " + findLast(dupes, 1));   // expects 0

        // 4c. Count occurrences
        System.out.println("\n--- 4c. Count Occurrences ---");
        System.out.println("Count of 2: " + countOccurrences(dupes, 2)); // expects 4
        System.out.println("Count of 3: " + countOccurrences(dupes, 3)); // expects 1
        System.out.println("Count of 9: " + countOccurrences(dupes, 9)); // expects 0

        // 4d. Floor and Ceiling
        System.out.println("\n--- 4d. Floor & Ceiling ---");
        int[] floorArr = {1, 3, 5, 7, 9};
        System.out.println("Array: " + Arrays.toString(floorArr));
        System.out.println("Floor of 6  → " + floor(floorArr, 6));   // expects 5
        System.out.println("Floor of 3  → " + floor(floorArr, 3));   // expects 3
        System.out.println("Floor of 0  → " + floor(floorArr, 0));   // expects -1
        System.out.println("Ceiling of 6 → " + ceiling(floorArr, 6)); // expects 7
        System.out.println("Ceiling of 9 → " + ceiling(floorArr, 9)); // expects 9
        System.out.println("Ceiling of 10 → " + ceiling(floorArr, 10)); // expects -1

        // 4e. Rotated sorted array search
        System.out.println("\n--- 4e. Search in Rotated Sorted Array ---");
        int[] rotated = {4, 5, 6, 7, 1, 2, 3};
        System.out.println("Rotated Array: " + Arrays.toString(rotated));
        System.out.println("Search 1 → index: " + searchRotated(rotated, 1)); // expects 4
        System.out.println("Search 6 → index: " + searchRotated(rotated, 6)); // expects 2
        System.out.println("Search 9 → index: " + searchRotated(rotated, 9)); // expects -1

        // 4f. Find peak element
        System.out.println("\n--- 4f. Find Peak Element ---");
        int[] peak1 = {1, 3, 20, 4, 1};
        int[] peak2 = {1, 2, 3, 4, 5};
        int[] peak3 = {5, 4, 3, 2, 1};
        System.out.println("Array " + Arrays.toString(peak1)
                + " → peak index: " + findPeak(peak1) + " (value: " + peak1[findPeak(peak1)] + ")");
        System.out.println("Array " + Arrays.toString(peak2)
                + " → peak index: " + findPeak(peak2) + " (value: " + peak2[findPeak(peak2)] + ")");
        System.out.println("Array " + Arrays.toString(peak3)
                + " → peak index: " + findPeak(peak3) + " (value: " + peak3[findPeak(peak3)] + ")");

        // 4g. Find minimum in rotated array
        System.out.println("\n--- 4g. Find Minimum in Rotated Array ---");
        int[] rot1 = {3, 4, 5, 1, 2};
        int[] rot2 = {4, 5, 6, 7, 0, 1, 2};
        int[] rot3 = {11, 13, 15, 17};
        System.out.println(Arrays.toString(rot1) + " → min: " + findMin(rot1)); // 1
        System.out.println(Arrays.toString(rot2) + " → min: " + findMin(rot2)); // 0
        System.out.println(Arrays.toString(rot3) + " → min: " + findMin(rot3)); // 11

        // 4h. Square root via binary search
        System.out.println("\n--- 4h. Integer Square Root ---");
        int[] sqrtInputs = {0, 1, 4, 8, 16, 25, 100, 2147395600};
        for (int x : sqrtInputs) {
            System.out.println("sqrt(" + x + ") = " + mySqrt(x));
        }

        // 4i. Binary search on answer — ship packages
        System.out.println("\n--- 4i. Binary Search on Answer: Ship Packages ---");
        int[] weights1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] weights2 = {3, 2, 2, 4, 1, 4};
        System.out.println("Weights " + Arrays.toString(weights1)
                + ", days=5 → min capacity: " + shipWithinDays(weights1, 5)); // 15
        System.out.println("Weights " + Arrays.toString(weights2)
                + ", days=3 → min capacity: " + shipWithinDays(weights2, 3)); // 6

        // 4j. Search in 2D matrix
        System.out.println("\n--- 4j. Search in Sorted 2D Matrix ---");
        int[][] matrix = {
            {1,  3,  5,  7},
            {10, 11, 16, 20},
            {23, 30, 34, 60}
        };
        System.out.println("Search 3  in matrix → " + searchMatrix(matrix, 3));   // true
        System.out.println("Search 13 in matrix → " + searchMatrix(matrix, 13));  // false
        System.out.println("Search 60 in matrix → " + searchMatrix(matrix, 60));  // true
    }

    // --- Variation Implementations ---

    static int findFirst(int[] arr, int target) {
        int low = 0, high = arr.length - 1, result = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) { result = mid; high = mid - 1; } // go left
            else if (arr[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return result;
    }

    static int findLast(int[] arr, int target) {
        int low = 0, high = arr.length - 1, result = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) { result = mid; low = mid + 1; } // go right
            else if (arr[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return result;
    }

    static int countOccurrences(int[] arr, int target) {
        int first = findFirst(arr, target);
        if (first == -1) return 0;
        return findLast(arr, target) - first + 1;
    }

    static int floor(int[] arr, int target) {
        int low = 0, high = arr.length - 1, result = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] <= target) { result = arr[mid]; low = mid + 1; }
            else high = mid - 1;
        }
        return result;
    }

    static int ceiling(int[] arr, int target) {
        int low = 0, high = arr.length - 1, result = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] >= target) { result = arr[mid]; high = mid - 1; }
            else low = mid + 1;
        }
        return result;
    }

    static int searchRotated(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) return mid;
            if (arr[low] <= arr[mid]) {                          // Left sorted
                if (target >= arr[low] && target < arr[mid]) high = mid - 1;
                else low = mid + 1;
            } else {                                              // Right sorted
                if (target > arr[mid] && target <= arr[high]) low = mid + 1;
                else high = mid - 1;
            }
        }
        return -1;
    }

    static int findPeak(int[] arr) {
        int low = 0, high = arr.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] > arr[mid + 1]) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    static int findMin(int[] arr) {
        int low = 0, high = arr.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] > arr[high]) low = mid + 1;
            else high = mid;
        }
        return arr[low];
    }

    static int mySqrt(int x) {
        if (x < 2) return x;
        int low = 1, high = x / 2, result = 1;
        while (low <= high) {
            long mid = low + (high - low) / 2;
            if (mid * mid == x) return (int) mid;
            if (mid * mid < x) { result = (int) mid; low = (int) mid + 1; }
            else high = (int) mid - 1;
        }
        return result;
    }

    static int shipWithinDays(int[] weights, int days) {
        int low = 0, high = 0;
        for (int w : weights) { low = Math.max(low, w); high += w; }
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (canShip(weights, days, mid)) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    static boolean canShip(int[] weights, int days, int capacity) {
        int needed = 1, current = 0;
        for (int w : weights) {
            if (current + w > capacity) { needed++; current = 0; }
            current += w;
        }
        return needed <= days;
    }

    static boolean searchMatrix(int[][] matrix, int target) {
        int rows = matrix.length, cols = matrix[0].length;
        int low = 0, high = rows * cols - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int val = matrix[mid / cols][mid % cols]; // Map 1D index to 2D
            if (val == target) return true;
            if (val < target) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }

    // =========================================================
    // SECTION 5 — TIME & SPACE COMPLEXITY COMPARISON
    // =========================================================
    static void section5_ComplexityComparison() {
        printSection("5. TIME & SPACE COMPLEXITY");

        System.out.println("Live comparison — actual operation counts:\n");
        System.out.printf("%-12s %-18s %-18s %-12s%n",
                "Array Size", "Linear (worst)", "Binary (worst)", "Speedup");
        System.out.println("-".repeat(62));

        int[] sizes = {10, 100, 1_000, 10_000, 100_000, 1_000_000};
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;
            int target = n - 1; // Worst case: last element

            int[] linearOps = {0};
            int[] binaryOps = {0};

            // Count linear ops
            for (int i = 0; i < arr.length; i++) {
                linearOps[0]++;
                if (arr[i] == target) break;
            }

            // Count binary ops
            int lo = 0, hi = arr.length - 1;
            while (lo <= hi) {
                binaryOps[0]++;
                int mid = lo + (hi - lo) / 2;
                if (arr[mid] == target) break;
                if (arr[mid] < target) lo = mid + 1;
                else hi = mid - 1;
            }

            System.out.printf("%-12d %-18d %-18d %-12.0fx%n",
                    n, linearOps[0], binaryOps[0],
                    (double) linearOps[0] / binaryOps[0]);
        }

        System.out.println("\nSpace Complexity:");
        System.out.println("  Linear Search    → O(1)      — only loop index variable");
        System.out.println("  Binary (iter)    → O(1)      — only low, high, mid");
        System.out.println("  Binary (recur)   → O(log n)  — call stack depth = log n");

        System.out.println("\nAmortized: Sort once, binary search 1000 times:");
        int n = 1_000_000;
        double sortCost = n * Math.log(n) / Math.log(2);
        double linearTotal = 1000.0 * n;
        double binaryTotal = sortCost + 1000 * (Math.log(n) / Math.log(2));
        System.out.printf("  Linear  1000 searches: %.0f ops%n", linearTotal);
        System.out.printf("  Sort + Binary 1000:    %.0f ops%n", binaryTotal);
        System.out.printf("  Binary is ~%.0fx more efficient%n", linearTotal / binaryTotal);
    }

    // =========================================================
    // SECTION 6 — REAL-WORLD SYSTEMS
    // =========================================================
    static void section6_RealWorldSystems() {
        printSection("6. REAL-WORLD SYSTEMS");

        // 6a. Database-style index lookup
        System.out.println("--- 6a. Database Index Lookup ---");
        User[] users = {
            new User(100, "Alice"),
            new User(200, "Bob"),
            new User(300, "Charlie"),
            new User(400, "Diana"),
            new User(500, "Eve")
        };
        System.out.println("SELECT * FROM users WHERE id = 300 → "
                + findUserById(users, 300));
        System.out.println("SELECT * FROM users WHERE id = 999 → "
                + findUserById(users, 999));

        // 6b. Git bisect simulation
        System.out.println("\n--- 6b. Git Bisect (Find First Bad Commit) ---");
        // true = good commit, false = bad commit (bug introduced)
        boolean[] commits = new boolean[20];
        Arrays.fill(commits, 0, 13, true);   // commits 0-12 are good
        Arrays.fill(commits, 13, 20, false);  // commits 13-19 have bug
        System.out.println("20 commits, bug from commit #13");
        System.out.println("First bad commit: #" + gitBisect(commits));

        // 6c. Auto-complete
        System.out.println("\n--- 6c. Auto-Complete (Prefix Search) ---");
        String[] dict = {"apple", "application", "apply", "apt", "array",
                          "banana", "band", "bandwidth", "binary", "bind"};
        System.out.println("Dictionary: " + Arrays.toString(dict));
        System.out.println("Prefix 'ap' → " + autoComplete(dict, "ap"));
        System.out.println("Prefix 'ban' → " + autoComplete(dict, "ban"));
        System.out.println("Prefix 'z'  → " + autoComplete(dict, "z"));

        // 6d. Load balancer consistent hash ring
        System.out.println("\n--- 6d. Load Balancer — Hash Ring ---");
        int[] ring = {100, 250, 400, 600, 800};
        String[] servers = {"server-A", "server-B", "server-C", "server-D", "server-E"};
        int[] requestHashes = {50, 175, 300, 550, 750, 850};
        for (int hash : requestHashes) {
            System.out.println("Request hash=" + hash
                    + " → routed to: " + findServer(ring, servers, hash));
        }

        // 6e. Spell checker
        System.out.println("\n--- 6e. Spell Checker ---");
        String[] dictionary = {"algorithm", "array", "binary", "code",
                                "data", "graph", "hash", "search", "sort", "tree"};
        String[] words = {"binary", "srach", "sort", "trie", "graph"};
        for (String word : words) {
            System.out.println("'" + word + "' → "
                    + (isSpelledCorrectly(dictionary, word) ? "✓ correct" : "✗ misspelled"));
        }

        // 6f. Financial order book
        System.out.println("\n--- 6f. Financial Order Book (Best Ask Price) ---");
        double[] askPrices = {101.5, 102.0, 102.5, 103.0, 103.5, 104.0};
        double[] bids = {100.0, 102.0, 103.2, 104.5};
        System.out.println("Ask prices: " + Arrays.toString(askPrices));
        for (double bid : bids) {
            double ask = findBestAsk(askPrices, bid);
            System.out.println("Bid=" + bid + " → Best ask: "
                    + (ask == -1 ? "No match" : ask));
        }
    }

    // --- Real-World Implementations ---

    static User findUserById(User[] sortedUsers, int id) {
        int low = 0, high = sortedUsers.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (sortedUsers[mid].id == id) return sortedUsers[mid];
            if (sortedUsers[mid].id < id) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    static int gitBisect(boolean[] commits) {
        int low = 0, high = commits.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (!commits[mid]) high = mid;    // bad commit — search left
            else low = mid + 1;               // good commit — search right
        }
        return low;
    }

    static List<String> autoComplete(String[] sortedDict, String prefix) {
        List<String> results = new ArrayList<>();
        int start = lowerBound(sortedDict, prefix);
        for (int i = start; i < sortedDict.length; i++) {
            if (sortedDict[i].startsWith(prefix)) results.add(sortedDict[i]);
            else break;
        }
        return results;
    }

    static int lowerBound(String[] arr, String prefix) {
        int low = 0, high = arr.length;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (arr[mid].compareTo(prefix) < 0) low = mid + 1;
            else high = mid;
        }
        return low;
    }

    static String findServer(int[] ring, String[] servers, int requestHash) {
        int low = 0, high = ring.length - 1;
        int idx = 0;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (ring[mid] >= requestHash) { idx = mid; high = mid - 1; }
            else low = mid + 1;
        }
        return servers[idx % servers.length];
    }

    static boolean isSpelledCorrectly(String[] dictionary, String word) {
        int low = 0, high = dictionary.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = dictionary[mid].compareTo(word);
            if (cmp == 0) return true;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }

    static double findBestAsk(double[] askPrices, double bidPrice) {
        int low = 0, high = askPrices.length - 1, result = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (askPrices[mid] >= bidPrice) { result = mid; high = mid - 1; }
            else low = mid + 1;
        }
        return result == -1 ? -1 : askPrices[result];
    }

    // =========================================================
    // SECTION 7 — INTERVIEW-LEVEL PROBLEMS
    // =========================================================
    static void section7_InterviewProblems() {
        printSection("7. INTERVIEW-LEVEL PROBLEMS");

        // P1. Search Insert Position (LC 35)
        System.out.println("--- P1. Search Insert Position (LC 35) ---");
        int[] p1 = {1, 3, 5, 6};
        System.out.println("Array: " + Arrays.toString(p1));
        System.out.println("target=5 → " + searchInsert(p1, 5));  // 2
        System.out.println("target=2 → " + searchInsert(p1, 2));  // 1
        System.out.println("target=7 → " + searchInsert(p1, 7));  // 4
        System.out.println("target=0 → " + searchInsert(p1, 0));  // 0

        // P2. Find Minimum in Rotated (LC 153)
        System.out.println("\n--- P2. Find Min in Rotated Sorted Array (LC 153) ---");
        System.out.println("[3,4,5,1,2]     → " + findMinRotated(new int[]{3,4,5,1,2}));     // 1
        System.out.println("[4,5,6,7,0,1,2] → " + findMinRotated(new int[]{4,5,6,7,0,1,2})); // 0
        System.out.println("[11,13,15,17]   → " + findMinRotated(new int[]{11,13,15,17}));    // 11

        // P3. Search in Rotated Sorted Array (LC 33)
        System.out.println("\n--- P3. Search in Rotated Sorted Array (LC 33) ---");
        int[] p3 = {4,5,6,7,0,1,2};
        System.out.println("Array: " + Arrays.toString(p3));
        System.out.println("target=0 → index: " + searchInRotated(p3, 0)); // 4
        System.out.println("target=3 → index: " + searchInRotated(p3, 3)); // -1
        System.out.println("target=4 → index: " + searchInRotated(p3, 4)); // 0

        // P4. Find Peak Element (LC 162)
        System.out.println("\n--- P4. Find Peak Element (LC 162) ---");
        System.out.println("[1,2,3,1]       → peak index: "
                + findPeakElement(new int[]{1,2,3,1}));     // 2
        System.out.println("[1,2,1,3,5,6,4] → peak index: "
                + findPeakElement(new int[]{1,2,1,3,5,6,4})); // 5

        // P5. Median of Two Sorted Arrays (LC 4) — Hard
        System.out.println("\n--- P5. Median of Two Sorted Arrays (LC 4) — Hard ---");
        System.out.println("[1,3] + [2]       → " + findMedianSortedArrays(
                new int[]{1,3}, new int[]{2}));           // 2.0
        System.out.println("[1,2] + [3,4]     → " + findMedianSortedArrays(
                new int[]{1,2}, new int[]{3,4}));         // 2.5
        System.out.println("[0,0] + [0,0]     → " + findMedianSortedArrays(
                new int[]{0,0}, new int[]{0,0}));         // 0.0
        System.out.println("[]    + [1]        → " + findMedianSortedArrays(
                new int[]{}, new int[]{1}));              // 1.0

        // P6. Koko Eating Bananas (LC 875)
        System.out.println("\n--- P6. Koko Eating Bananas (LC 875) ---");
        System.out.println("[3,6,7,11], h=8 → min speed: "
                + minEatingSpeed(new int[]{3,6,7,11}, 8));   // 4
        System.out.println("[30,11,23,4,20], h=5 → min speed: "
                + minEatingSpeed(new int[]{30,11,23,4,20}, 5)); // 30

        // P7. First Bad Version (LC 278)
        System.out.println("\n--- P7. First Bad Version (LC 278) ---");
        System.out.println("n=5, bad from version 4 → first bad: " + firstBadVersion(5, 4)); // 4
        System.out.println("n=1, bad from version 1 → first bad: " + firstBadVersion(1, 1)); // 1

        // P8. Count Negatives in Sorted Matrix (LC 1351)
        System.out.println("\n--- P8. Count Negatives in Sorted Matrix (LC 1351) ---");
        int[][] grid1 = {{4,3,2,-1},{3,2,1,-1},{1,1,-1,-2},{-1,-1,-2,-3}};
        int[][] grid2 = {{3,2},{1,0}};
        System.out.println("Grid1 → negative count: " + countNegatives(grid1)); // 8
        System.out.println("Grid2 → negative count: " + countNegatives(grid2)); // 0

        // P9. Find range (first and last position) LC 34
        System.out.println("\n--- P9. Find First and Last Position (LC 34) ---");
        int[] p9a = {5,7,7,8,8,10};
        int[] p9b = {5,7,7,8,8,10};
        System.out.println("Array: " + Arrays.toString(p9a));
        System.out.println("target=8  → " + Arrays.toString(searchRange(p9a, 8)));  // [3,4]
        System.out.println("target=6  → " + Arrays.toString(searchRange(p9b, 6)));  // [-1,-1]

        // P10. Valid Perfect Square (LC 367)
        System.out.println("\n--- P10. Valid Perfect Square (LC 367) ---");
        int[] squares = {1, 4, 9, 14, 16, 25, 26};
        for (int n : squares) {
            System.out.println(n + " is perfect square: " + isPerfectSquare(n));
        }
    }

    // --- Interview Problem Implementations ---

    static int searchInsert(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return low; // Insert position when not found
    }

    static int findMinRotated(int[] nums) {
        int low = 0, high = nums.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] > nums[high]) low = mid + 1;
            else high = mid;
        }
        return nums[low];
    }

    static int searchInRotated(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) return mid;
            if (nums[low] <= nums[mid]) {
                if (target >= nums[low] && target < nums[mid]) high = mid - 1;
                else low = mid + 1;
            } else {
                if (target > nums[mid] && target <= nums[high]) low = mid + 1;
                else high = mid - 1;
            }
        }
        return -1;
    }

    static int findPeakElement(int[] nums) {
        int low = 0, high = nums.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] > nums[mid + 1]) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length)
            return findMedianSortedArrays(nums2, nums1);
        int m = nums1.length, n = nums2.length;
        int low = 0, high = m;
        while (low <= high) {
            int px = (low + high) / 2;
            int py = (m + n + 1) / 2 - px;
            int maxLX = (px == 0) ? Integer.MIN_VALUE : nums1[px - 1];
            int minRX = (px == m) ? Integer.MAX_VALUE : nums1[px];
            int maxLY = (py == 0) ? Integer.MIN_VALUE : nums2[py - 1];
            int minRY = (py == n) ? Integer.MAX_VALUE : nums2[py];
            if (maxLX <= minRY && maxLY <= minRX) {
                if ((m + n) % 2 == 0)
                    return (Math.max(maxLX, maxLY) + Math.min(minRX, minRY)) / 2.0;
                return Math.max(maxLX, maxLY);
            } else if (maxLX > minRY) high = px - 1;
            else low = px + 1;
        }
        return 0;
    }

    static int minEatingSpeed(int[] piles, int h) {
        int low = 1, high = 0;
        for (int p : piles) high = Math.max(high, p);
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (canFinish(piles, h, mid)) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    static boolean canFinish(int[] piles, int h, int speed) {
        int hours = 0;
        for (int p : piles) hours += (p + speed - 1) / speed;
        return hours <= h;
    }

    // firstBad: the version number from which bug starts
    static int firstBadVersion(int n, int firstBad) {
        int low = 1, high = n;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (mid >= firstBad) high = mid;  // Simulated isBadVersion(mid)
            else low = mid + 1;
        }
        return low;
    }

    static int countNegatives(int[][] grid) {
        int count = 0, cols = grid[0].length;
        for (int[] row : grid) {
            int low = 0, high = cols;
            while (low < high) {
                int mid = low + (high - low) / 2;
                if (row[mid] < 0) high = mid;
                else low = mid + 1;
            }
            count += cols - low;
        }
        return count;
    }

    static int[] searchRange(int[] nums, int target) {
        return new int[]{findFirst(nums, target), findLast(nums, target)};
    }

    static boolean isPerfectSquare(int num) {
        if (num < 2) return true;
        long low = 2, high = num / 2;
        while (low <= high) {
            long mid = low + (high - low) / 2;
            if (mid * mid == num) return true;
            if (mid * mid < num) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }

    // =========================================================
    // HELPER DATA MODELS
    // =========================================================
    static class Product {
        int id; String name; double price;
        Product(int id, String name, double price) {
            this.id = id; this.name = name; this.price = price;
        }
        public String toString() {
            return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
        }
    }

    static class User {
        int id; String name;
        User(int id, String name) { this.id = id; this.name = name; }
        public String toString() { return "User{id=" + id + ", name='" + name + "'}"; }
    }

    // =========================================================
    // DISPLAY HELPERS
    // =========================================================
    static void printBanner(String title) {
        System.out.println("\n" + "=".repeat(62));
        System.out.println("  " + title);
        System.out.println("=".repeat(62));
    }

    static void printSection(String title) {
        System.out.println("\n" + "-".repeat(62));
        System.out.println("  SECTION " + title);
        System.out.println("-".repeat(62));
    }
}
