import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 * SORTING ALGORITHMS — Complete Executable Reference
 * ============================================================
 * Topics covered:
 *  1. Why Sorting Matters          (live performance demo)
 *  2. Basic Sorting Algorithms     (bubble, selection, insertion, shell)
 *  3. Efficient Sorting Algorithms (heap sort, tim sort)
 *  4. Divide & Conquer             (merge sort variants, quick sort variants)
 *  5. Non-Comparison Sorts         (counting, radix, bucket)
 *  6. Java Built-in Sorting        (Arrays.sort, Collections, streams,
 *                                   Comparator chains, PriorityQueue)
 *  7. Real-World Systems           (DB order-by, search ranking, log merge,
 *                                   e-commerce, scheduling, external sort)
 *
 * Compile : javac SortingAlgorithms.java
 * Run     : java SortingAlgorithms
 * ============================================================
 */
public class SortingAlgorithms {

    static final Random RNG = new Random(42); // Fixed seed for reproducibility

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("SORTING ALGORITHMS — COMPLETE DEMO");

        section1_WhySortingMatters();
        section2_BasicSorts();
        section3_EfficientSorts();
        section4_DivideAndConquer();
        section5_NonComparisonSorts();
        section6_JavaBuiltIn();
        section7_RealWorldSystems();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — WHY SORTING MATTERS
    // =========================================================
    static void section1_WhySortingMatters() {
        printSection("1. WHY SORTING MATTERS");

        int n = 500_000;
        int[] arr = randomArray(n, 0, n);

        // 1a. Search comparison: linear vs binary (needs sorted)
        System.out.println("--- 1a. Search: Linear vs Binary after Sort ---");
        int target = arr[n / 3]; // guaranteed to exist

        long t0 = System.nanoTime();
        int linIdx = linearSearch(arr, target);
        long linTime = System.nanoTime() - t0;

        int[] sorted = arr.clone();
        t0 = System.nanoTime();
        Arrays.sort(sorted);
        long sortTime = System.nanoTime() - t0;

        t0 = System.nanoTime();
        int binIdx = Arrays.binarySearch(sorted, target);
        long binTime = System.nanoTime() - t0;

        System.out.printf("Array size        : %,d%n", n);
        System.out.printf("Linear search     : %d ns  (found: %s)%n", linTime, linIdx >= 0);
        System.out.printf("Sort once         : %d ns%n", sortTime);
        System.out.printf("Binary search     : %d ns  (found: %s)%n", binTime, binIdx >= 0);
        System.out.printf("Binary search 1000x would save ~%.0fx total time%n",
                (1000.0 * linTime) / (sortTime + 1000.0 * binTime));

        // 1b. Deduplication: unsorted vs sorted
        System.out.println("\n--- 1b. Deduplication: O(n²) vs O(n) after Sort ---");
        int[] small = randomArray(10, 0, 5);
        System.out.println("Input          : " + Arrays.toString(small));
        System.out.println("Dedupe O(n²)   : " + dedupeUnsorted(small));
        System.out.println("Dedupe O(n)    : " + dedupeSorted(small.clone()));

        // 1c. Complexity table
        System.out.println("\n--- 1c. log₂ scale vs linear ---");
        System.out.printf("%-15s %-15s %-15s%n", "n", "O(n) ops", "O(n log n) ops");
        for (int size : new int[]{100, 1_000, 10_000, 1_000_000}) {
            System.out.printf("%-15,d %-15,d %-15,d%n",
                    size, size, (int)(size * Math.log(size) / Math.log(2)));
        }
    }

    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == target) return i;
        return -1;
    }

    static List<Integer> dedupeUnsorted(int[] arr) {
        List<Integer> result = new ArrayList<>();
        for (int x : arr) if (!result.contains(x)) result.add(x); // O(n²)
        Collections.sort(result);
        return result;
    }

    static List<Integer> dedupeSorted(int[] arr) {
        Arrays.sort(arr);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < arr.length; i++)
            if (i == 0 || arr[i] != arr[i - 1]) result.add(arr[i]); // O(n)
        return result;
    }

    // =========================================================
    // SECTION 2 — BASIC SORTING ALGORITHMS
    // =========================================================
    static void section2_BasicSorts() {
        printSection("2. BASIC SORTING ALGORITHMS");

        // 2a. Bubble Sort
        System.out.println("--- 2a. Bubble Sort ---");
        int[] b1 = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        System.out.println("Before: " + Arrays.toString(b1));
        bubbleSort(b1);
        System.out.println("After : " + Arrays.toString(b1));

        System.out.println("\nOptimized (early exit on sorted input):");
        int[] b2 = {1, 2, 3, 4, 5}; // already sorted
        System.out.println("Before: " + Arrays.toString(b2));
        int passes = bubbleSortOptimized(b2);
        System.out.println("After : " + Arrays.toString(b2) + " — passes needed: " + passes);

        System.out.println("\nWorst case (reverse sorted):");
        int[] b3 = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        passes = bubbleSortOptimized(b3);
        System.out.println("Sorted: " + Arrays.toString(b3) + " — passes needed: " + passes);

        // 2b. Selection Sort
        System.out.println("\n--- 2b. Selection Sort ---");
        int[] s1 = {64, 25, 12, 22, 11};
        System.out.println("Before: " + Arrays.toString(s1));
        selectionSort(s1);
        System.out.println("After : " + Arrays.toString(s1));

        System.out.println("\nDescending:");
        int[] s2 = {64, 25, 12, 22, 11};
        selectionSortDesc(s2);
        System.out.println("After : " + Arrays.toString(s2));

        System.out.println("\nSwap count (selection always = n-1):");
        int[] s3 = randomArray(8, 1, 20);
        System.out.println("Input : " + Arrays.toString(s3));
        int swaps = selectionSortCountSwaps(s3);
        System.out.println("Sorted: " + Arrays.toString(s3) + " — swaps: " + swaps);

        // 2c. Insertion Sort
        System.out.println("\n--- 2c. Insertion Sort ---");
        int[] i1 = {12, 11, 13, 5, 6};
        System.out.println("Before: " + Arrays.toString(i1));
        insertionSort(i1);
        System.out.println("After : " + Arrays.toString(i1));

        System.out.println("\nNearly sorted (best case for insertion):");
        int[] i2 = {1, 2, 3, 5, 4}; // one swap needed
        insertionSort(i2);
        System.out.println("Sorted: " + Arrays.toString(i2));

        System.out.println("\nString insertion sort:");
        String[] words = {"banana", "apple", "cherry", "date", "elderberry"};
        insertionSortStrings(words);
        System.out.println("Sorted: " + Arrays.toString(words));

        // 2d. Shell Sort
        System.out.println("\n--- 2d. Shell Sort ---");
        int[] sh1 = {12, 34, 54, 2, 3, 22, 48, 1, 9, 7};
        System.out.println("Before: " + Arrays.toString(sh1));
        shellSort(sh1);
        System.out.println("After : " + Arrays.toString(sh1));

        // 2e. Performance comparison
        System.out.println("\n--- 2e. Basic Sorts Performance (n=5000) ---");
        int size = 5000;
        int[] base = randomArray(size, 0, size);
        benchmarkSort("Bubble Sort",    base.clone(), arr -> bubbleSort(arr));
        benchmarkSort("Selection Sort", base.clone(), arr -> selectionSort(arr));
        benchmarkSort("Insertion Sort", base.clone(), arr -> insertionSort(arr));
        benchmarkSort("Shell Sort",     base.clone(), arr -> shellSort(arr));
    }

    // --- Basic Sort Implementations ---

    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (arr[j] > arr[j + 1]) swap(arr, j, j + 1);
    }

    static int bubbleSortOptimized(int[] arr) {
        int n = arr.length, passes = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++)
                if (arr[j] > arr[j + 1]) { swap(arr, j, j + 1); swapped = true; }
            passes++;
            if (!swapped) break; // Already sorted
        }
        return passes;
    }

    static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++)
                if (arr[j] < arr[minIdx]) minIdx = j;
            if (minIdx != i) swap(arr, i, minIdx);
        }
    }

    static void selectionSortDesc(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < n; j++)
                if (arr[j] > arr[maxIdx]) maxIdx = j;
            swap(arr, i, maxIdx);
        }
    }

    static int selectionSortCountSwaps(int[] arr) {
        int n = arr.length, swaps = 0;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++)
                if (arr[j] < arr[minIdx]) minIdx = j;
            if (minIdx != i) { swap(arr, i, minIdx); swaps++; }
        }
        return swaps;
    }

    static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i], j = i - 1;
            while (j >= 0 && arr[j] > key) { arr[j + 1] = arr[j]; j--; }
            arr[j + 1] = key;
        }
    }

    static void insertionSortStrings(String[] arr) {
        for (int i = 1; i < arr.length; i++) {
            String key = arr[i]; int j = i - 1;
            while (j >= 0 && arr[j].compareTo(key) > 0) { arr[j + 1] = arr[j]; j--; }
            arr[j + 1] = key;
        }
    }

    static void shellSort(int[] arr) {
        int n = arr.length, gap = 1;
        while (gap < n / 3) gap = gap * 3 + 1; // Knuth sequence
        while (gap >= 1) {
            for (int i = gap; i < n; i++) {
                int key = arr[i], j = i - gap;
                while (j >= 0 && arr[j] > key) { arr[j + gap] = arr[j]; j -= gap; }
                arr[j + gap] = key;
            }
            gap /= 3;
        }
    }

    // =========================================================
    // SECTION 3 — EFFICIENT SORTING ALGORITHMS
    // =========================================================
    static void section3_EfficientSorts() {
        printSection("3. EFFICIENT SORTING ALGORITHMS");

        // 3a. Heap Sort
        System.out.println("--- 3a. Heap Sort ---");
        int[] h1 = {12, 11, 13, 5, 6, 7, 3, 1};
        System.out.println("Before: " + Arrays.toString(h1));
        heapSort(h1);
        System.out.println("After : " + Arrays.toString(h1));

        System.out.println("\nHeap sort on reverse sorted:");
        int[] h2 = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        heapSort(h2);
        System.out.println("Sorted: " + Arrays.toString(h2));

        System.out.println("\nTop-K elements using heap:");
        int[] h3 = randomArray(15, 1, 100);
        System.out.println("Array : " + Arrays.toString(h3));
        System.out.println("Top 3 : " + topK(h3, 3));

        // 3b. Tim Sort (simplified)
        System.out.println("\n--- 3b. Tim Sort (Hybrid: InsertionSort + MergeSort) ---");
        int[] t1 = {5, 21, 7, 23, 19, 0, 3, 44, 2, 8, 1, 11};
        System.out.println("Before: " + Arrays.toString(t1));
        timSort(t1);
        System.out.println("After : " + Arrays.toString(t1));

        // 3c. Performance: all efficient sorts
        System.out.println("\n--- 3c. Efficient Sorts Performance (n=100,000) ---");
        int size = 100_000;
        int[] base = randomArray(size, 0, size);
        benchmarkSort("Heap Sort",      base.clone(), arr -> heapSort(arr));
        benchmarkSort("Tim Sort",       base.clone(), arr -> timSort(arr));
        benchmarkSort("Arrays.sort",    base.clone(), arr -> Arrays.sort(arr));
    }

    // --- Efficient Sort Implementations ---

    static void heapSort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i); // Build max-heap
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);       // Move root to end
            heapify(arr, i, 0);   // Heapify reduced heap
        }
    }

    static void heapify(int[] arr, int n, int i) {
        int largest = i, l = 2*i+1, r = 2*i+2;
        if (l < n && arr[l] > arr[largest]) largest = l;
        if (r < n && arr[r] > arr[largest]) largest = r;
        if (largest != i) { swap(arr, i, largest); heapify(arr, n, largest); }
    }

    static List<Integer> topK(int[] arr, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        for (int x : arr) maxHeap.add(x);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < k; i++) result.add(maxHeap.poll());
        return result;
    }

    static void timSort(int[] arr) {
        int n = arr.length, RUN = 32;
        for (int i = 0; i < n; i += RUN)
            insertionSortRange(arr, i, Math.min(i + RUN - 1, n - 1));
        for (int size = RUN; size < n; size *= 2)
            for (int left = 0; left < n; left += 2 * size) {
                int mid   = left + size - 1;
                int right = Math.min(left + 2 * size - 1, n - 1);
                if (mid < right) merge(arr, left, mid, right);
            }
    }

    static void insertionSortRange(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i], j = i - 1;
            while (j >= left && arr[j] > key) { arr[j + 1] = arr[j]; j--; }
            arr[j + 1] = key;
        }
    }

    // =========================================================
    // SECTION 4 — DIVIDE & CONQUER SORTING
    // =========================================================
    static void section4_DivideAndConquer() {
        printSection("4. DIVIDE & CONQUER SORTING");

        // 4a. Merge Sort — top-down
        System.out.println("--- 4a. Merge Sort (Top-Down Recursive) ---");
        int[] m1 = {38, 27, 43, 3, 9, 82, 10};
        System.out.println("Before: " + Arrays.toString(m1));
        mergeSort(m1, 0, m1.length - 1);
        System.out.println("After : " + Arrays.toString(m1));

        // 4b. Merge Sort — bottom-up (iterative)
        System.out.println("\n--- 4b. Merge Sort (Bottom-Up Iterative) ---");
        int[] m2 = {38, 27, 43, 3, 9, 82, 10};
        mergeSortBottomUp(m2);
        System.out.println("Sorted: " + Arrays.toString(m2));

        // 4c. Merge Sort — count inversions
        System.out.println("\n--- 4c. Count Inversions (merge sort application) ---");
        int[] inv1 = {2, 4, 1, 3, 5};
        int[] inv2 = {5, 4, 3, 2, 1};
        int[] inv3 = {1, 2, 3, 4, 5};
        System.out.println(Arrays.toString(inv1) + " → inversions: " + countInversions(inv1.clone(), 0, inv1.length-1));
        System.out.println(Arrays.toString(inv2) + " → inversions: " + countInversions(inv2.clone(), 0, inv2.length-1));
        System.out.println(Arrays.toString(inv3) + " → inversions: " + countInversions(inv3.clone(), 0, inv3.length-1));

        // 4d. Merge Sort — merge two sorted arrays
        System.out.println("\n--- 4d. Merge Two Sorted Arrays ---");
        int[] a = {1, 3, 5, 7, 9};
        int[] b = {2, 4, 6, 8, 10};
        System.out.println("A: " + Arrays.toString(a));
        System.out.println("B: " + Arrays.toString(b));
        System.out.println("Merged: " + Arrays.toString(mergeTwoSorted(a, b)));

        // 4e. Quick Sort — Lomuto
        System.out.println("\n--- 4e. Quick Sort (Lomuto Partition) ---");
        int[] q1 = {10, 7, 8, 9, 1, 5};
        System.out.println("Before: " + Arrays.toString(q1));
        quickSort(q1, 0, q1.length - 1);
        System.out.println("After : " + Arrays.toString(q1));

        // 4f. Quick Sort — 3-way (Dutch National Flag)
        System.out.println("\n--- 4f. Quick Sort 3-Way (Dutch National Flag — handles duplicates) ---");
        int[] q2 = {4, 2, 4, 4, 1, 3, 4, 2};
        System.out.println("Before: " + Arrays.toString(q2));
        quickSort3Way(q2, 0, q2.length - 1);
        System.out.println("After : " + Arrays.toString(q2));

        // 4g. Quick Sort — randomized pivot
        System.out.println("\n--- 4g. Quick Sort (Randomized Pivot) ---");
        int[] q3 = {1, 2, 3, 4, 5, 6, 7}; // sorted input — worst case for basic quicksort
        System.out.println("Before (sorted = worst case): " + Arrays.toString(q3));
        quickSortRandom(q3, 0, q3.length - 1);
        System.out.println("After : " + Arrays.toString(q3));

        // 4h. Quick Select — kth smallest
        System.out.println("\n--- 4h. Quick Select (k-th Smallest) ---");
        int[] qs = {7, 10, 4, 3, 20, 15};
        System.out.println("Array: " + Arrays.toString(qs));
        for (int k = 0; k < qs.length; k++) {
            int[] copy = qs.clone();
            System.out.printf("k=%d (0-indexed) → %d%n", k, quickSelect(copy, 0, copy.length-1, k));
        }

        // 4i. Performance comparison
        System.out.println("\n--- 4i. Divide & Conquer Performance (n=100,000) ---");
        int size = 100_000;
        int[] base = randomArray(size, 0, size);
        benchmarkSort("Merge Sort (top-down)",  base.clone(), arr -> mergeSort(arr, 0, arr.length-1));
        benchmarkSort("Merge Sort (bottom-up)", base.clone(), arr -> mergeSortBottomUp(arr));
        benchmarkSort("Quick Sort (Lomuto)",    base.clone(), arr -> quickSort(arr, 0, arr.length-1));
        benchmarkSort("Quick Sort (3-way)",     base.clone(), arr -> quickSort3Way(arr, 0, arr.length-1));
        benchmarkSort("Quick Sort (random)",    base.clone(), arr -> quickSortRandom(arr, 0, arr.length-1));
        benchmarkSort("Arrays.sort (baseline)", base.clone(), arr -> Arrays.sort(arr));
    }

    // --- Merge Sort Implementations ---

    static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1, n2 = right - mid;
        int[] L = Arrays.copyOfRange(arr, left, left + n1);
        int[] R = Arrays.copyOfRange(arr, mid + 1, mid + 1 + n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2)
            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    static void mergeSortBottomUp(int[] arr) {
        int n = arr.length;
        for (int size = 1; size < n; size *= 2)
            for (int left = 0; left < n - size; left += 2 * size) {
                int mid   = left + size - 1;
                int right = Math.min(left + 2 * size - 1, n - 1);
                merge(arr, left, mid, right);
            }
    }

    static long countInversions(int[] arr, int left, int right) {
        long inv = 0;
        if (left < right) {
            int mid = left + (right - left) / 2;
            inv += countInversions(arr, left, mid);
            inv += countInversions(arr, mid + 1, right);
            inv += mergeCountInversions(arr, left, mid, right);
        }
        return inv;
    }

    static long mergeCountInversions(int[] arr, int left, int mid, int right) {
        int[] L = Arrays.copyOfRange(arr, left, mid + 1);
        int[] R = Arrays.copyOfRange(arr, mid + 1, right + 1);
        int i = 0, j = 0, k = left; long inv = 0;
        while (i < L.length && j < R.length) {
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else { inv += L.length - i; arr[k++] = R[j++]; }
        }
        while (i < L.length) arr[k++] = L[i++];
        while (j < R.length) arr[k++] = R[j++];
        return inv;
    }

    static int[] mergeTwoSorted(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length)
            result[k++] = (a[i] <= b[j]) ? a[i++] : b[j++];
        while (i < a.length) result[k++] = a[i++];
        while (j < b.length) result[k++] = b[j++];
        return result;
    }

    // --- Quick Sort Implementations ---

    static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int p = partition(arr, low, high);
            quickSort(arr, low, p - 1);
            quickSort(arr, p + 1, high);
        }
    }

    static int partition(int[] arr, int low, int high) {
        int pivot = arr[high], i = low - 1;
        for (int j = low; j < high; j++)
            if (arr[j] <= pivot) swap(arr, ++i, j);
        swap(arr, i + 1, high);
        return i + 1;
    }

    static void quickSort3Way(int[] arr, int low, int high) {
        if (low >= high) return;
        int pivot = arr[low], lt = low, gt = high, i = low + 1;
        while (i <= gt) {
            if      (arr[i] < pivot) swap(arr, lt++, i++);
            else if (arr[i] > pivot) swap(arr, i, gt--);
            else                     i++;
        }
        quickSort3Way(arr, low, lt - 1);
        quickSort3Way(arr, gt + 1, high);
    }

    static void quickSortRandom(int[] arr, int low, int high) {
        if (low < high) {
            int r = low + RNG.nextInt(high - low + 1);
            swap(arr, r, high);
            int p = partition(arr, low, high);
            quickSortRandom(arr, low, p - 1);
            quickSortRandom(arr, p + 1, high);
        }
    }

    static int quickSelect(int[] arr, int low, int high, int k) {
        if (low == high) return arr[low];
        int p = partition(arr, low, high);
        if (p == k)     return arr[p];
        if (p < k)      return quickSelect(arr, p + 1, high, k);
        return              quickSelect(arr, low, p - 1, k);
    }

    // =========================================================
    // SECTION 5 — NON-COMPARISON SORTS
    // =========================================================
    static void section5_NonComparisonSorts() {
        printSection("5. NON-COMPARISON SORTS");

        // 5a. Counting Sort
        System.out.println("--- 5a. Counting Sort ---");
        int[] c1 = {4, 2, 2, 8, 3, 3, 1, 0, 6};
        System.out.println("Before: " + Arrays.toString(c1));
        countingSort(c1);
        System.out.println("After : " + Arrays.toString(c1));

        System.out.println("\nCounting sort on exam scores (0-100):");
        int[] scores = {95, 72, 88, 95, 63, 72, 80, 88, 91, 72};
        System.out.println("Scores: " + Arrays.toString(scores));
        countingSort(scores);
        System.out.println("Sorted: " + Arrays.toString(scores));

        System.out.println("\nCounting sort on characters:");
        String s = "geeksforgeeks";
        System.out.println("String: " + s);
        System.out.println("Sorted: " + countingSortString(s));

        System.out.println("\nFrequency map (side benefit of counting sort):");
        int[] data = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        System.out.println("Data  : " + Arrays.toString(data));
        frequencyMap(data);

        // 5b. Radix Sort
        System.out.println("\n--- 5b. Radix Sort ---");
        int[] r1 = {170, 45, 75, 90, 802, 24, 2, 66};
        System.out.println("Before: " + Arrays.toString(r1));
        radixSort(r1);
        System.out.println("After : " + Arrays.toString(r1));

        System.out.println("\nRadix sort on large numbers:");
        int[] r2 = {1000000, 50000, 9999, 123456, 7, 999999};
        System.out.println("Before: " + Arrays.toString(r2));
        radixSort(r2);
        System.out.println("After : " + Arrays.toString(r2));

        System.out.println("\nRadix sort on phone numbers (10 digits):");
        int[] phones = {9876543210 % Integer.MAX_VALUE,
                        4155551234 % Integer.MAX_VALUE,
                        2125550100 % Integer.MAX_VALUE};
        System.out.println("Before: " + Arrays.toString(phones));
        radixSort(phones);
        System.out.println("After : " + Arrays.toString(phones));

        // 5c. Bucket Sort
        System.out.println("\n--- 5c. Bucket Sort ---");
        float[] bk1 = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        System.out.println("Before: " + Arrays.toString(bk1));
        bucketSort(bk1);
        System.out.println("After : " + Arrays.toString(bk1));

        System.out.println("\nBucket sort on integers with range:");
        int[] bk2 = {29, 25, 3, 49, 9, 37, 21, 43};
        System.out.println("Before: " + Arrays.toString(bk2));
        bucketSortInt(bk2, 5);
        System.out.println("After : " + Arrays.toString(bk2));

        // 5d. Performance comparison
        System.out.println("\n--- 5d. Non-Comparison Sorts Performance (n=100,000) ---");
        int size = 100_000;
        int[] base = randomArray(size, 0, 1000); // bounded range for counting sort
        benchmarkSort("Counting Sort", base.clone(), arr -> countingSort(arr));
        benchmarkSort("Radix Sort",    base.clone(), arr -> radixSort(arr));
        benchmarkSort("Arrays.sort",   base.clone(), arr -> Arrays.sort(arr));
    }

    // --- Non-Comparison Sort Implementations ---

    static void countingSort(int[] arr) {
        if (arr.length == 0) return;
        int max = arr[0], min = arr[0];
        for (int x : arr) { max = Math.max(max, x); min = Math.min(min, x); }
        int[] count = new int[max - min + 1];
        int[] output = new int[arr.length];
        for (int x : arr) count[x - min]++;
        for (int i = 1; i < count.length; i++) count[i] += count[i - 1];
        for (int i = arr.length - 1; i >= 0; i--)
            output[--count[arr[i] - min]] = arr[i];
        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    static String countingSortString(String s) {
        int[] count = new int[256];
        for (char c : s.toCharArray()) count[c]++;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++) while (count[i]-- > 0) sb.append((char) i);
        return sb.toString();
    }

    static void frequencyMap(int[] arr) {
        int max = Arrays.stream(arr).max().getAsInt();
        int[] count = new int[max + 1];
        for (int x : arr) count[x]++;
        for (int i = 0; i <= max; i++)
            if (count[i] > 0) System.out.printf("  %d appears %d time(s)%n", i, count[i]);
    }

    static void radixSort(int[] arr) {
        int max = Arrays.stream(arr).max().getAsInt();
        for (int exp = 1; max / exp > 0; exp *= 10)
            countingSortByDigit(arr, exp);
    }

    static void countingSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n], count = new int[10];
        for (int x : arr) count[(x / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--)
            output[--count[(arr[i] / exp) % 10]] = arr[i];
        System.arraycopy(output, 0, arr, 0, n);
    }

    static void bucketSort(float[] arr) {
        int n = arr.length;
        @SuppressWarnings("unchecked")
        List<Float>[] buckets = new List[n];
        for (int i = 0; i < n; i++) buckets[i] = new ArrayList<>();
        for (float v : arr) { int idx = Math.min((int)(v * n), n - 1); buckets[idx].add(v); }
        int k = 0;
        for (List<Float> b : buckets) { Collections.sort(b); for (float v : b) arr[k++] = v; }
    }

    static void bucketSortInt(int[] arr, int numBuckets) {
        int max = Arrays.stream(arr).max().getAsInt();
        int min = Arrays.stream(arr).min().getAsInt();
        int range = max - min + 1;
        @SuppressWarnings("unchecked")
        List<Integer>[] buckets = new List[numBuckets];
        for (int i = 0; i < numBuckets; i++) buckets[i] = new ArrayList<>();
        for (int v : arr) {
            int idx = Math.min((int)((long)(v - min) * numBuckets / range), numBuckets - 1);
            buckets[idx].add(v);
        }
        int k = 0;
        for (List<Integer> b : buckets) { Collections.sort(b); for (int v : b) arr[k++] = v; }
    }

    // =========================================================
    // SECTION 6 — JAVA BUILT-IN SORTING
    // =========================================================
    static void section6_JavaBuiltIn() {
        printSection("6. JAVA BUILT-IN SORTING");

        // 6a. Arrays.sort primitives
        System.out.println("--- 6a. Arrays.sort — Primitives (Dual-Pivot QuickSort) ---");
        int[]    ints    = {5, 2, 8, 1, 9, 3};
        double[] doubles = {3.14, 2.71, 1.41, 1.73};
        char[]   chars   = {'z', 'a', 'm', 'b', 'g'};
        Arrays.sort(ints);
        Arrays.sort(doubles);
        Arrays.sort(chars);
        System.out.println("int[]   sorted: " + Arrays.toString(ints));
        System.out.println("double[] sorted: " + Arrays.toString(doubles));
        System.out.println("char[]  sorted: " + Arrays.toString(chars));

        System.out.println("\nSort subarray [1,4):");
        int[] sub = {9, 5, 2, 8, 1, 3};
        System.out.println("Before: " + Arrays.toString(sub));
        Arrays.sort(sub, 1, 4); // sort indices 1,2,3
        System.out.println("After : " + Arrays.toString(sub));

        // 6b. Arrays.sort objects — TimSort
        System.out.println("\n--- 6b. Arrays.sort — Objects (TimSort, Stable) ---");
        String[] names = {"Charlie", "Alice", "Bob", "Diana", "Eve"};
        Arrays.sort(names);
        System.out.println("Natural order: " + Arrays.toString(names));

        Integer[] nums = {5, 2, 8, 1, 9};
        Arrays.sort(nums, Comparator.reverseOrder());
        System.out.println("Reverse order: " + Arrays.toString(nums));

        // 6c. Custom Comparator chains
        System.out.println("\n--- 6c. Custom Comparator Chains ---");
        Employee[] employees = {
            new Employee("Alice",   "Engineering", 85000),
            new Employee("Bob",     "Marketing",   60000),
            new Employee("Carol",   "Engineering", 90000),
            new Employee("Diana",   "Marketing",   72000),
            new Employee("Eve",     "Engineering", 85000),
            new Employee("Frank",   "HR",          55000),
        };

        System.out.println("Sort by salary ascending:");
        Arrays.sort(employees, Comparator.comparingDouble(Employee::getSalary));
        Arrays.stream(employees).forEach(e -> System.out.println("  " + e));

        System.out.println("\nSort by dept ASC, then salary DESC, then name ASC:");
        Arrays.sort(employees, Comparator
            .comparing(Employee::getDept)
            .thenComparingDouble(Employee::getSalary).reversed()
            .thenComparing(Employee::getName));
        Arrays.stream(employees).forEach(e -> System.out.println("  " + e));

        // 6d. Collections.sort and List.sort
        System.out.println("\n--- 6d. Collections.sort and List.sort ---");
        List<Integer> list = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3));
        System.out.println("Before: " + list);
        list.sort(Comparator.naturalOrder());
        System.out.println("Sorted: " + list);
        list.sort(Comparator.reverseOrder());
        System.out.println("Reverse:" + list);

        List<String> words = Arrays.asList("banana", "apple", "cherry", "date", "elderberry");
        words.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        System.out.println("By length then alpha: " + words);

        // 6e. Stream sorting
        System.out.println("\n--- 6e. Stream Sorting ---");
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        System.out.println("Original  : " + Arrays.toString(arr));

        List<Integer> streamSorted = Arrays.stream(arr).boxed()
                .sorted().collect(Collectors.toList());
        System.out.println("Ascending : " + streamSorted);

        List<Integer> streamDesc = Arrays.stream(arr).boxed()
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        System.out.println("Descending: " + streamDesc);

        List<Integer> top3 = Arrays.stream(arr).boxed()
                .sorted(Comparator.reverseOrder()).limit(3).collect(Collectors.toList());
        System.out.println("Top 3     : " + top3);

        // 6f. TreeMap and TreeSet
        System.out.println("\n--- 6f. TreeMap and TreeSet (Always Sorted) ---");
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(3, "three"); treeMap.put(1, "one"); treeMap.put(2, "two");
        System.out.println("TreeMap (auto-sorted): " + treeMap);
        System.out.println("First key: " + treeMap.firstKey() + ", Last key: " + treeMap.lastKey());
        System.out.println("SubMap [1,3): " + treeMap.subMap(1, 3));

        TreeSet<Integer> treeSet = new TreeSet<>();
        treeSet.addAll(Arrays.asList(5, 1, 3, 8, 2, 7, 4, 6));
        System.out.println("TreeSet: " + treeSet);
        System.out.println("Floor of 4: " + treeSet.floor(4) + ", Ceiling of 4: " + treeSet.ceiling(4));

        // 6g. PriorityQueue
        System.out.println("\n--- 6g. PriorityQueue ---");
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.addAll(Arrays.asList(5, 1, 8, 3, 9, 2));
        System.out.print("Min-heap poll order: ");
        while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " ");
        System.out.println();

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.addAll(Arrays.asList(5, 1, 8, 3, 9, 2));
        System.out.print("Max-heap poll order: ");
        while (!maxHeap.isEmpty()) System.out.print(maxHeap.poll() + " ");
        System.out.println();

        PriorityQueue<Employee> empHeap = new PriorityQueue<>(
            Comparator.comparingDouble(Employee::getSalary));
        Collections.addAll(empHeap, employees);
        System.out.println("Lowest salary first: " + empHeap.poll());

        // 6h. Parallel sort
        System.out.println("\n--- 6h. Arrays.parallelSort ---");
        int[] large = randomArray(1_000_000, 0, 1_000_000);
        long t0 = System.nanoTime();
        Arrays.sort(large.clone());
        long seqTime = System.nanoTime() - t0;
        t0 = System.nanoTime();
        Arrays.parallelSort(large);
        long parTime = System.nanoTime() - t0;
        System.out.printf("Sequential sort 1M: %,d ms%n", seqTime / 1_000_000);
        System.out.printf("Parallel sort   1M: %,d ms%n", parTime / 1_000_000);
    }

    // =========================================================
    // SECTION 7 — REAL-WORLD SYSTEMS
    // =========================================================
    static void section7_RealWorldSystems() {
        printSection("7. REAL-WORLD SYSTEMS");

        // 7a. Database ORDER BY simulation
        System.out.println("--- 7a. Database ORDER BY Simulation ---");
        Order[] orders = {
            new Order(1, 102, 250.0, "2024-01-15"),
            new Order(2, 101, 150.0, "2024-01-10"),
            new Order(3, 102, 350.0, "2024-01-12"),
            new Order(4, 103,  80.0, "2024-01-18"),
            new Order(5, 101, 500.0, "2024-01-05"),
        };
        System.out.println("SELECT * FROM orders ORDER BY customer_id ASC, total DESC:");
        Arrays.sort(orders, Comparator
            .comparingInt(Order::getCustomerId)
            .thenComparingDouble(o -> -o.getTotal()));
        Arrays.stream(orders).forEach(o -> System.out.println("  " + o));

        // 7b. Search engine result ranking
        System.out.println("\n--- 7b. Search Engine Result Ranking ---");
        SearchResult[] results = {
            new SearchResult("Java Sorting Guide",    0.95, 0.8, 1706745600L),
            new SearchResult("Algorithms 101",        0.70, 0.9, 1706832000L),
            new SearchResult("Data Structures Java",  0.88, 0.7, 1706918400L),
            new SearchResult("Sort in O(n log n)",    0.92, 0.6, 1707004800L),
        };
        System.out.println("Ranked by TF-IDF DESC, then PageRank DESC:");
        Arrays.sort(results, Comparator
            .comparingDouble(SearchResult::getTfIdf).reversed()
            .thenComparingDouble(SearchResult::getPageRank).reversed());
        Arrays.stream(results).forEach(r -> System.out.println("  " + r));

        // 7c. Log aggregation — k-way merge of sorted streams
        System.out.println("\n--- 7c. Log Aggregation — K-Way Merge ---");
        List<List<LogEntry>> serverLogs = Arrays.asList(
            Arrays.asList(
                new LogEntry("server-1", 100, "INFO start"),
                new LogEntry("server-1", 300, "INFO process"),
                new LogEntry("server-1", 500, "INFO done")
            ),
            Arrays.asList(
                new LogEntry("server-2", 150, "WARN slow"),
                new LogEntry("server-2", 350, "ERROR fail"),
                new LogEntry("server-2", 600, "INFO retry")
            ),
            Arrays.asList(
                new LogEntry("server-3", 200, "INFO connect"),
                new LogEntry("server-3", 400, "INFO query")
            )
        );
        System.out.println("Merged chronological log:");
        kWayMergeLogs(serverLogs).forEach(l -> System.out.println("  " + l));

        // 7d. E-commerce product sorting
        System.out.println("\n--- 7d. E-Commerce Product Sorting ---");
        Product[] products = {
            new Product("Laptop",   999.99, 4.5, 1500),
            new Product("Mouse",     29.99, 4.8,  800),
            new Product("Monitor",  349.99, 4.2,  300),
            new Product("Keyboard",  79.99, 4.6,  600),
            new Product("Webcam",    59.99, 4.3,  450),
        };
        System.out.println("Sort by price ASC:");
        Product[] byPrice = products.clone();
        Arrays.sort(byPrice, Comparator.comparingDouble(Product::getPrice));
        Arrays.stream(byPrice).forEach(p -> System.out.println("  " + p));

        System.out.println("Sort by rating DESC then price ASC:");
        Product[] byRating = products.clone();
        Arrays.sort(byRating, Comparator.comparingDouble(Product::getRating)
            .reversed().thenComparingDouble(Product::getPrice));
        Arrays.stream(byRating).forEach(p -> System.out.println("  " + p));

        // 7e. OS Process Scheduling
        System.out.println("\n--- 7e. OS Process Scheduling ---");
        Process[] processes = {
            new Process("P1", 3, 4, 10),
            new Process("P2", 1, 8,  5),
            new Process("P3", 4, 2, 15),
            new Process("P4", 2, 6,  8),
        };
        System.out.println("Priority Scheduling (high priority first):");
        Arrays.sort(processes, Comparator.comparingInt(Process::getPriority).reversed());
        Arrays.stream(processes).forEach(p -> System.out.println("  " + p));

        System.out.println("Shortest Job First (SJF):");
        Arrays.sort(processes, Comparator.comparingInt(Process::getBurstTime));
        Arrays.stream(processes).forEach(p -> System.out.println("  " + p));

        // 7f. Leaderboard — Top Scores
        System.out.println("\n--- 7f. Game Leaderboard ---");
        PlayerScore[] scores = {
            new PlayerScore("Alice",   9500, 15),
            new PlayerScore("Bob",     8200, 22),
            new PlayerScore("Charlie", 9500, 18),
            new PlayerScore("Diana",   7800, 12),
            new PlayerScore("Eve",     9500, 15),
        };
        // Sort: score DESC, then time ASC (faster is better), then name ASC
        Arrays.sort(scores, Comparator
            .comparingInt(PlayerScore::getScore).reversed()
            .thenComparingInt(PlayerScore::getTime)
            .thenComparing(PlayerScore::getName));
        System.out.println("Rank | Player   | Score | Time");
        System.out.println("-----|----------|-------|-----");
        for (int i = 0; i < scores.length; i++)
            System.out.printf("  %d  | %-8s | %5d | %3ds%n",
                i+1, scores[i].getName(), scores[i].getScore(), scores[i].getTime());

        // 7g. Frequency-based sort (like sort characters by frequency)
        System.out.println("\n--- 7g. Sort by Frequency (like 'sort chars by freq') ---");
        String input = "tree";
        System.out.println("Input: '" + input + "' → sorted by freq: '" + sortByFrequency(input) + "'");
        input = "cccaaa";
        System.out.println("Input: '" + input + "' → sorted by freq: '" + sortByFrequency(input) + "'");
        input = "Aabb";
        System.out.println("Input: '" + input + "' → sorted by freq: '" + sortByFrequency(input) + "'");
    }

    // --- Real-World Implementations ---

    static List<LogEntry> kWayMergeLogs(List<List<LogEntry>> streams) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(
            Comparator.comparingLong(idx -> streams.get((int)idx[0]).get((int)idx[1]).getTimestamp()));
        for (int i = 0; i < streams.size(); i++)
            if (!streams.get(i).isEmpty()) pq.offer(new int[]{i, 0});
        List<LogEntry> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            int[] idx = pq.poll();
            int si = idx[0], li = idx[1];
            result.add(streams.get(si).get(li));
            if (li + 1 < streams.get(si).size()) pq.offer(new int[]{si, li + 1});
        }
        return result;
    }

    static String sortByFrequency(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
        return s.chars().boxed()
            .sorted(Comparator.comparingInt((Integer c) -> freq.get((char)(int)c)).reversed())
            .collect(StringBuilder::new, (sb, c) -> sb.append((char)(int)c), StringBuilder::append)
            .toString();
    }

    // =========================================================
    // DATA MODELS
    // =========================================================
    static class Employee {
        String name, dept; double salary;
        Employee(String name, String dept, double salary) {
            this.name = name; this.dept = dept; this.salary = salary;
        }
        String getName() { return name; }
        String getDept() { return dept; }
        double getSalary() { return salary; }
        public String toString() {
            return String.format("%-8s | %-12s | $%,.0f", name, dept, salary);
        }
    }

    static class Order {
        int id, customerId; double total; String date;
        Order(int id, int customerId, double total, String date) {
            this.id = id; this.customerId = customerId; this.total = total; this.date = date;
        }
        int getCustomerId() { return customerId; }
        double getTotal()   { return total; }
        public String toString() {
            return String.format("Order#%d | Customer=%d | Total=$%.2f | Date=%s",
                    id, customerId, total, date);
        }
    }

    static class SearchResult {
        String title; double tfIdf, pageRank; long timestamp;
        SearchResult(String title, double tfIdf, double pageRank, long ts) {
            this.title = title; this.tfIdf = tfIdf; this.pageRank = pageRank; this.timestamp = ts;
        }
        double getTfIdf()    { return tfIdf; }
        double getPageRank() { return pageRank; }
        public String toString() {
            return String.format("%-35s | TF-IDF=%.2f | PR=%.1f", title, tfIdf, pageRank);
        }
    }

    static class LogEntry {
        String server, message; long timestamp;
        LogEntry(String server, long ts, String message) {
            this.server = server; this.timestamp = ts; this.message = message;
        }
        long getTimestamp() { return timestamp; }
        public String toString() {
            return String.format("[t=%3d] %s: %s", timestamp, server, message);
        }
    }

    static class Product {
        String name; double price, rating; int reviews;
        Product(String name, double price, double rating, int reviews) {
            this.name = name; this.price = price; this.rating = rating; this.reviews = reviews;
        }
        double getPrice()  { return price; }
        double getRating() { return rating; }
        public String toString() {
            return String.format("%-12s | $%6.2f | ★%.1f | %,d reviews",
                    name, price, rating, reviews);
        }
    }

    static class Process {
        String name; int priority, burstTime, deadline;
        Process(String name, int priority, int burstTime, int deadline) {
            this.name = name; this.priority = priority;
            this.burstTime = burstTime; this.deadline = deadline;
        }
        int getPriority()  { return priority; }
        int getBurstTime() { return burstTime; }
        public String toString() {
            return String.format("%-3s | priority=%d | burst=%2dms | deadline=%2d",
                    name, priority, burstTime, deadline);
        }
    }

    static class PlayerScore {
        String name; int score, time;
        PlayerScore(String name, int score, int time) {
            this.name = name; this.score = score; this.time = time;
        }
        String getName() { return name; }
        int getScore()   { return score; }
        int getTime()    { return time; }
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    static void swap(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    static int[] randomArray(int size, int min, int max) {
        return RNG.ints(size, min, max).toArray();
    }

    @FunctionalInterface
    interface SortFn { void sort(int[] arr); }

    static void benchmarkSort(String name, int[] arr, SortFn fn) {
        long t0 = System.nanoTime();
        fn.sort(arr);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("  %-28s : %,d ms | sorted: %s%n",
                name, ms, isSorted(arr) ? "✓" : "✗");
    }

    static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) if (arr[i] < arr[i-1]) return false;
        return true;
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
