// ================================================================
//   HEAP & PRIORITY QUEUE — Complete Deep Dive in Java
// ================================================================
//   Compile:  javac HeapPQ.java
//   Run:      java HeapPQ
// ================================================================
//
//   TOPICS:
//   1. Heap Fundamentals
//   2. Min Heap vs Max Heap
//   3. Heap Operations & Complexity
//   4. PriorityQueue in Java
//   5. Real-World Applications
//   6. Heap-Based Problem Patterns
//   7. Interview-Level Problems
// ================================================================

import java.util.*;

public class HeapPQ {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    static void sub(String title) {
        System.out.println("\n  ── " + title + " ──");
    }


    // ============================================================
    // MIN HEAP — Built from scratch using array
    // ============================================================
    //
    //   A Heap is a COMPLETE BINARY TREE stored as an ARRAY.
    //   Complete = all levels filled except possibly the last,
    //              which is filled LEFT TO RIGHT.
    //
    //   ARRAY STORAGE — no need for Node objects or pointers!
    //   For element at index i:
    //     Parent:      (i - 1) / 2
    //     Left child:  2 * i + 1
    //     Right child: 2 * i + 2
    //
    //   MIN HEAP PROPERTY: every parent <= both children
    //   So arr[0] is ALWAYS the minimum element.
    //
    //   Visual for heap [1, 3, 5, 7, 9, 8, 6]:
    //
    //              1          ← index 0 (root = min)
    //           /     \
    //          3       5      ← indices 1, 2
    //         / \     / \
    //        7   9   8   6   ← indices 3, 4, 5, 6
    //
    //   Array: [1, 3, 5, 7, 9, 8, 6]
    //   index:  0  1  2  3  4  5  6
    // ============================================================
    static class MinHeap {
        private int[] arr;
        private int   size;
        private int   capacity;

        MinHeap(int capacity) {
            this.capacity = capacity;
            this.arr      = new int[capacity];
            this.size     = 0;
        }

        // ── INDEX HELPERS
        int parent(int i)     { return (i - 1) / 2; }
        int leftChild(int i)  { return 2 * i + 1;   }
        int rightChild(int i) { return 2 * i + 2;   }
        boolean hasLeft(int i)  { return leftChild(i)  < size; }
        boolean hasRight(int i) { return rightChild(i) < size; }

        void swap(int i, int j) {
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }

        // ── INSERT (PUSH UP / HEAPIFY UP) — O(log n)
        //
        //   1. Add new element at the END of the array (maintain complete tree)
        //   2. "Bubble up": if new element < parent, swap them, repeat
        //   3. Stop when parent <= element OR we reach root
        //
        //   Trace for insert(2) into [1, 3, 5, 7, 9, 8, 6]:
        //   Add at index 7: [1, 3, 5, 7, 9, 8, 6, 2]
        //   parent(7) = 3 → arr[3]=7 > 2 → swap → [1, 3, 5, 2, 9, 8, 6, 7]
        //   parent(3) = 1 → arr[1]=3 > 2 → swap → [1, 2, 5, 3, 9, 8, 6, 7]
        //   parent(1) = 0 → arr[0]=1 <= 2 → STOP
        void insert(int val) {
            if (size == capacity) throw new RuntimeException("Heap is full!");
            arr[size] = val;       // add at end
            size++;
            heapifyUp(size - 1);   // bubble up to correct position
        }

        void heapifyUp(int i) {
            while (i > 0 && arr[parent(i)] > arr[i]) {
                swap(i, parent(i));
                i = parent(i);     // move up
            }
        }

        // ── EXTRACT MIN (HEAPIFY DOWN / PUSH DOWN) — O(log n)
        //
        //   1. Save arr[0] (the minimum)
        //   2. Move LAST element to root (maintain complete tree shape)
        //   3. "Bubble down": if root > smaller child, swap, repeat
        //   4. Stop when element <= both children OR reach leaf
        //
        //   Trace for extractMin() from [1, 2, 5, 3, 9, 8, 6, 7]:
        //   Save 1. Move last (7) to root: [7, 2, 5, 3, 9, 8, 6]
        //   7 > min(2,5)=2 → swap(0,1): [2, 7, 5, 3, 9, 8, 6]
        //   7 > min(3,9)=3 → swap(1,3): [2, 3, 5, 7, 9, 8, 6]
        //   7 <= both children (none) → STOP. Return 1.
        int extractMin() {
            if (size == 0) throw new RuntimeException("Heap is empty!");
            int min = arr[0];
            arr[0] = arr[size - 1]; // move last to root
            size--;
            heapifyDown(0);         // restore heap property
            return min;
        }

        void heapifyDown(int i) {
            int smallest = i;
            if (hasLeft(i)  && arr[leftChild(i)]  < arr[smallest]) smallest = leftChild(i);
            if (hasRight(i) && arr[rightChild(i)] < arr[smallest]) smallest = rightChild(i);
            if (smallest != i) {
                swap(i, smallest);
                heapifyDown(smallest); // continue down
            }
        }

        // ── PEEK — O(1) — just return arr[0]
        int peek() {
            if (size == 0) throw new RuntimeException("Heap is empty!");
            return arr[0];
        }

        // ── BUILD HEAP from array — O(n) ← NOT O(n log n)!
        //
        //   Naive: insert n elements one by one → O(n log n)
        //   Smart: heapifyDown from last non-leaf to root → O(n)
        //
        //   WHY O(n)?  Most nodes are near leaves (height ~0),
        //   only a few are near root (height ~log n).
        //   Total work = sum of heights = O(n) (geometric series)
        //   Last non-leaf index = (n/2) - 1
        void buildHeap(int[] input) {
            System.arraycopy(input, 0, arr, 0, input.length);
            size = input.length;
            for (int i = size / 2 - 1; i >= 0; i--) {
                heapifyDown(i); // heapify every non-leaf
            }
        }

        // ── DECREASE KEY — O(log n)
        // Update a value at index i to a SMALLER value, then bubble up
        void decreaseKey(int i, int newVal) {
            if (newVal > arr[i]) throw new IllegalArgumentException(
                    "New value must be smaller than current");
            arr[i] = newVal;
            heapifyUp(i);
        }

        // ── DELETE at index i — O(log n)
        // Decrease to -∞, then extract min
        void delete(int i) {
            decreaseKey(i, Integer.MIN_VALUE);
            extractMin();
        }

        boolean isEmpty() { return size == 0; }
        int size()        { return size; }

        void print(String label) {
            System.out.print("  " + label + " [");
            for (int i = 0; i < size; i++) {
                System.out.print(arr[i]);
                if (i < size - 1) System.out.print(", ");
            }
            System.out.println("]  root(min)=" + (size > 0 ? arr[0] : "empty"));
        }

        // Print tree shape
        void printTree() {
            if (size == 0) { System.out.println("  (empty)"); return; }
            int levels = (int)(Math.log(size) / Math.log(2)) + 1;
            int idx = 0;
            for (int lvl = 0; lvl < levels && idx < size; lvl++) {
                int nodes = (int) Math.pow(2, lvl);
                int spaces = (int) Math.pow(2, levels - lvl) - 1;
                System.out.print("  ");
                for (int sp = 0; sp < spaces; sp++) System.out.print(" ");
                for (int n = 0; n < nodes && idx < size; n++, idx++) {
                    System.out.print(arr[idx]);
                    for (int sp = 0; sp < spaces * 2; sp++) System.out.print(" ");
                }
                System.out.println();
            }
        }
    }


    // ============================================================
    // MAX HEAP — Mirror of MinHeap
    // ============================================================
    //
    //   MAX HEAP PROPERTY: every parent >= both children
    //   arr[0] is ALWAYS the maximum element.
    //
    //   Implementation: same as MinHeap but flip ALL comparisons:
    //     MinHeap: parent > child → swap  (bubble up smaller)
    //     MaxHeap: parent < child → swap  (bubble up larger)
    // ============================================================
    static class MaxHeap {
        private int[] arr;
        private int   size;
        private int   capacity;

        MaxHeap(int capacity) {
            this.capacity = capacity;
            this.arr      = new int[capacity];
            this.size     = 0;
        }

        int parent(int i)     { return (i - 1) / 2; }
        int leftChild(int i)  { return 2 * i + 1;   }
        int rightChild(int i) { return 2 * i + 2;   }

        void swap(int i, int j) {
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }

        void insert(int val) {
            if (size == capacity) throw new RuntimeException("Heap is full!");
            arr[size++] = val;
            heapifyUp(size - 1);
        }

        void heapifyUp(int i) {
            while (i > 0 && arr[parent(i)] < arr[i]) { // FLIPPED: < instead of >
                swap(i, parent(i));
                i = parent(i);
            }
        }

        int extractMax() {
            if (size == 0) throw new RuntimeException("Heap is empty!");
            int max = arr[0];
            arr[0] = arr[--size];
            heapifyDown(0);
            return max;
        }

        void heapifyDown(int i) {
            int largest = i;
            int l = leftChild(i), r = rightChild(i);
            if (l < size && arr[l] > arr[largest]) largest = l; // FLIPPED: > instead of <
            if (r < size && arr[r] > arr[largest]) largest = r;
            if (largest != i) { swap(i, largest); heapifyDown(largest); }
        }

        int peek()    { return arr[0]; }
        boolean isEmpty() { return size == 0; }

        void print(String label) {
            System.out.print("  " + label + " [");
            for (int i = 0; i < size; i++) {
                System.out.print(arr[i]);
                if (i < size - 1) System.out.print(", ");
            }
            System.out.println("]  root(max)=" + (size > 0 ? arr[0] : "empty"));
        }
    }


    // ============================================================
    // HEAP SORT — O(n log n), in-place, not stable
    // ============================================================
    //
    //   Phase 1: Build Max Heap from array  → O(n)
    //   Phase 2: Extract max n times        → O(n log n)
    //     Each extraction: swap root with last, heapifyDown
    //     Sorted portion grows from the RIGHT
    //
    //   Trace for [4, 2, 7, 1, 5]:
    //   After build: [7, 5, 4, 1, 2]  (max heap)
    //   Step 1: swap 7↔2 → [2,5,4,1 | 7]   heapify → [5,2,4,1 | 7]
    //   Step 2: swap 5↔1 → [1,2,4 | 5,7]   heapify → [4,2,1 | 5,7]
    //   Step 3: swap 4↔1 → [1,2 | 4,5,7]   heapify → [2,1 | 4,5,7]
    //   Step 4: swap 2↔1 → [1 | 2,4,5,7]
    //   Done! [1,2,4,5,7] sorted ascending ✓
    // ============================================================
    static void heapSort(int[] arr) {
        int n = arr.length;

        // Phase 1: build max heap
        for (int i = n / 2 - 1; i >= 0; i--) heapifyDownSort(arr, n, i);

        // Phase 2: extract max n-1 times
        for (int end = n - 1; end > 0; end--) {
            int tmp = arr[0]; arr[0] = arr[end]; arr[end] = tmp; // move max to end
            heapifyDownSort(arr, end, 0); // restore heap for shrinking array
        }
    }

    static void heapifyDownSort(int[] arr, int heapSize, int i) {
        int largest = i, l = 2*i+1, r = 2*i+2;
        if (l < heapSize && arr[l] > arr[largest]) largest = l;
        if (r < heapSize && arr[r] > arr[largest]) largest = r;
        if (largest != i) {
            int tmp = arr[i]; arr[i] = arr[largest]; arr[largest] = tmp;
            heapifyDownSort(arr, heapSize, largest);
        }
    }


    // ============================================================
    // INTERVIEW PROBLEMS
    // ============================================================

    // ── PROBLEM 1: KTH LARGEST ELEMENT — O(n log k) time, O(k) space
    //
    //   Use a MIN HEAP of size k.
    //   Keep only k largest elements seen so far.
    //   When heap exceeds k: pop the minimum (smallest of k-largest).
    //   After n elements: root of heap = kth largest.
    //
    //   Why MinHeap of size k?
    //   Invariant: heap holds the k largest elements seen so far.
    //   Root = smallest of those k = kth largest overall.
    //   If new element > root → it belongs in top-k, push it in, pop root.
    static int kthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) minHeap.poll(); // remove smallest
        }
        return minHeap.peek(); // root = kth largest
    }

    // ── PROBLEM 2: KTH SMALLEST ELEMENT — O(n log k) time, O(k) space
    //
    //   Mirror: use MAX HEAP of size k.
    //   Root = largest of k smallest = kth smallest.
    static int kthSmallest(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap =
                new PriorityQueue<>(Collections.reverseOrder());
        for (int num : nums) {
            maxHeap.offer(num);
            if (maxHeap.size() > k) maxHeap.poll(); // remove largest
        }
        return maxHeap.peek(); // root = kth smallest
    }

    // ── PROBLEM 3: TOP K FREQUENT ELEMENTS — O(n log k)
    //
    //   Step 1: Count frequencies with HashMap → O(n)
    //   Step 2: Min Heap of size k (by frequency) → O(n log k)
    //   Root of heap = kth most frequent.
    static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);

        // Min heap ordered by frequency
        PriorityQueue<Integer> heap =
                new PriorityQueue<>(Comparator.comparingInt(freq::get));

        for (int num : freq.keySet()) {
            heap.offer(num);
            if (heap.size() > k) heap.poll(); // evict least frequent
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = heap.poll();
        return result;
    }

    // ── PROBLEM 4: MERGE K SORTED LISTS — O(n log k)
    //
    //   Use Min Heap of (value, listIndex, elementIndex).
    //   Start: add first element of each list.
    //   Each step: extract min, add its successor from same list.
    //
    //   Trace for [[1,4,7],[2,5,8],[3,6,9]] k=3:
    //   Init heap: {(1,0,0),(2,1,0),(3,2,0)}
    //   Extract (1,0,0)→result=[1], add (4,0,1): {(2,1,0),(3,2,0),(4,0,1)}
    //   Extract (2,1,0)→result=[1,2], add (5,1,1): {(3,2,0),(4,0,1),(5,1,1)}
    //   ... result=[1,2,3,4,5,6,7,8,9]
    static int[] mergeKSorted(int[][] lists) {
        // Min heap: int[]{value, listIdx, elemIdx}
        PriorityQueue<int[]> heap =
                new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        int totalSize = 0;
        for (int i = 0; i < lists.length; i++) {
            if (lists[i].length > 0) {
                heap.offer(new int[]{lists[i][0], i, 0});
                totalSize += lists[i].length;
            }
        }

        int[] result = new int[totalSize];
        int idx = 0;
        while (!heap.isEmpty()) {
            int[] top  = heap.poll();
            result[idx++] = top[0];       // value
            int li = top[1], ei = top[2]; // list index, element index
            if (ei + 1 < lists[li].length) {
                heap.offer(new int[]{lists[li][ei + 1], li, ei + 1});
            }
        }
        return result;
    }

    // ── PROBLEM 5: FIND MEDIAN FROM DATA STREAM — O(log n) add, O(1) median
    //
    //   Two heaps partition the stream into two halves:
    //   maxHeap: lower half  (root = max of lower half)
    //   minHeap: upper half  (root = min of upper half)
    //
    //   Invariant:
    //   1. maxHeap.size() == minHeap.size() OR maxHeap.size() == minHeap.size()+1
    //   2. maxHeap.peek() <= minHeap.peek() (lower half <= upper half)
    //
    //   Median:
    //   - Even total: (maxHeap.peek() + minHeap.peek()) / 2.0
    //   - Odd total:  maxHeap.peek() (lower half has 1 extra)
    //
    //   Example stream: 1, 7, 3, 5, 2
    //   After 1: maxH=[1]  minH=[]  median=1
    //   After 7: maxH=[1]  minH=[7] median=(1+7)/2=4
    //   After 3: maxH=[3,1]minH=[7] median=3
    //   After 5: maxH=[3,1]minH=[5,7] median=(3+5)/2=4
    //   After 2: maxH=[3,2,1]minH=[5,7] median=3
    static class MedianFinder {
        private PriorityQueue<Integer> maxHeap = // lower half
                new PriorityQueue<>(Collections.reverseOrder());
        private PriorityQueue<Integer> minHeap = // upper half
                new PriorityQueue<>();

        void addNum(int num) {
            maxHeap.offer(num);
            // Balance: ensure maxHeap.peek() <= minHeap.peek()
            if (!minHeap.isEmpty() && maxHeap.peek() > minHeap.peek()) {
                minHeap.offer(maxHeap.poll());
            }
            // Balance sizes: maxHeap can have at most 1 more element
            if (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            } else if (minHeap.size() > maxHeap.size()) {
                maxHeap.offer(minHeap.poll());
            }
        }

        double findMedian() {
            if (maxHeap.size() == minHeap.size()) {
                return (maxHeap.peek() + minHeap.peek()) / 2.0;
            }
            return maxHeap.peek(); // odd count: lower half has 1 extra
        }
    }

    // ── PROBLEM 6: TASK SCHEDULER — O(n log n)
    //
    //   Given tasks and cooldown n, find minimum CPU time.
    //   Greedy: always execute the most frequent available task.
    //   Max Heap gives the most frequent task in O(log n).
    static int taskScheduler(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char t : tasks) freq[t - 'A']++;

        PriorityQueue<Integer> maxHeap =
                new PriorityQueue<>(Collections.reverseOrder());
        for (int f : freq) if (f > 0) maxHeap.offer(f);

        int time = 0;
        while (!maxHeap.isEmpty()) {
            List<Integer> temp = new ArrayList<>();
            for (int i = 0; i <= n; i++) { // one cycle = n+1 slots
                if (!maxHeap.isEmpty()) temp.add(maxHeap.poll() - 1);
                time++;
                if (maxHeap.isEmpty() && temp.isEmpty()) break;
            }
            for (int f : temp) if (f > 0) maxHeap.offer(f);
        }
        return time;
    }

    // ── PROBLEM 7: SLIDING WINDOW MEDIAN — O(n log k)
    //
    //   Two heaps (same as MedianFinder) but we also need to
    //   REMOVE elements that slide out of the window.
    //   Use lazy deletion: mark elements as deleted, ignore when popped.
    static double[] slidingWindowMedian(int[] nums, int k) {
        double[] result    = new double[nums.length - k + 1];
        PriorityQueue<Integer> maxH = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> minH = new PriorityQueue<>();
        Map<Integer, Integer>  lazy = new HashMap<>(); // lazy deletion counter

        // Balance helper
        for (int i = 0; i < nums.length; i++) {
            // ADD
            maxH.offer(nums[i]);
            if (!minH.isEmpty() && maxH.peek() > minH.peek())
                minH.offer(maxH.poll());
            // size balance
            if (maxH.size() > minH.size() + 1) minH.offer(maxH.poll());
            else if (minH.size() > maxH.size()) maxH.offer(minH.poll());

            // REMOVE outgoing element (lazy)
            if (i >= k) {
                int out = nums[i - k];
                lazy.merge(out, 1, Integer::sum);
                // Adjust size balance after lazy mark
                if (out <= maxH.peek()) {
                    if (maxH.size() > minH.size() + 1) minH.offer(maxH.poll());
                } else {
                    if (minH.size() > maxH.size()) maxH.offer(minH.poll());
                }
                // Clean stale tops
                while (!maxH.isEmpty() && lazy.getOrDefault(maxH.peek(), 0) > 0) {
                    lazy.merge(maxH.poll(), -1, Integer::sum);
                }
                while (!minH.isEmpty() && lazy.getOrDefault(minH.peek(), 0) > 0) {
                    lazy.merge(minH.poll(), -1, Integer::sum);
                }
            }

            if (i >= k - 1) {
                result[i - k + 1] = maxH.size() == minH.size()
                        ? (maxH.peek() + minH.peek()) / 2.0
                        : maxH.peek();
            }
        }
        return result;
    }

    // ── PROBLEM 8: KTH LARGEST STREAM (Online Algorithm)
    //
    //   Design a class that finds the kth largest element
    //   in a stream (not a static array).
    //   Maintain a min heap of size k at all times.
    //   Root = kth largest seen so far.
    static class KthLargestStream {
        private PriorityQueue<Integer> heap;
        private int k;

        KthLargestStream(int k, int[] initialNums) {
            this.k    = k;
            this.heap = new PriorityQueue<>(k);
            for (int n : initialNums) add(n);
        }

        int add(int val) {
            heap.offer(val);
            if (heap.size() > k) heap.poll();
            return heap.peek();
        }
    }

    // ── PROBLEM 9: REORGANIZE STRING — O(n log n)
    //
    //   Rearrange string so no two adjacent characters are same.
    //   Greedy: always place the most frequent available character.
    //   Use Max Heap (by frequency).
    //   If most-frequent == last placed → use second-most-frequent.
    static String reorganizeString(String s) {
        int[] freq = new int[26];
        for (char c : s.toCharArray()) freq[c - 'a']++;

        PriorityQueue<int[]> maxHeap =
                new PriorityQueue<>((a, b) -> b[1] - a[1]); // by frequency desc
        for (int i = 0; i < 26; i++) {
            if (freq[i] > 0) maxHeap.offer(new int[]{i, freq[i]});
        }

        StringBuilder sb = new StringBuilder();
        while (maxHeap.size() >= 2) {
            int[] first  = maxHeap.poll();
            int[] second = maxHeap.poll();
            sb.append((char)('a' + first[0]));
            sb.append((char)('a' + second[0]));
            if (--first[1]  > 0) maxHeap.offer(first);
            if (--second[1] > 0) maxHeap.offer(second);
        }
        if (!maxHeap.isEmpty()) {
            int[] last = maxHeap.poll();
            if (last[1] > 1) return ""; // impossible
            sb.append((char)('a' + last[0]));
        }
        return sb.toString();
    }

    // ── PROBLEM 10: MINIMUM COST TO CONNECT ROPES — O(n log n)
    //
    //   Connect n ropes into one. Cost = length of two ropes joined.
    //   Minimize total cost.
    //   Greedy: always join the two SHORTEST ropes (Min Heap).
    //   This is essentially Huffman encoding!
    static long minCostConnectRopes(int[] ropes) {
        PriorityQueue<Long> minHeap = new PriorityQueue<>();
        for (int r : ropes) minHeap.offer((long) r);
        long totalCost = 0;
        while (minHeap.size() > 1) {
            long a = minHeap.poll();
            long b = minHeap.poll();
            totalCost += a + b;
            minHeap.offer(a + b); // combined rope goes back
        }
        return totalCost;
    }


    // ============================================================
    //   MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   HEAP & PRIORITY QUEUE — Complete Deep Dive in Java     ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");


        // ═══════════════════════════════════════════════════════
        // TOPIC 1 — HEAP FUNDAMENTALS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 1 — Heap Fundamentals");

        sub("What is a Heap?");
        System.out.println("  A Heap is a COMPLETE BINARY TREE satisfying the HEAP PROPERTY.");
        System.out.println("  Complete = every level filled except possibly the last,");
        System.out.println("             filled LEFT TO RIGHT (no gaps).");
        System.out.println();
        System.out.println("  MIN HEAP: parent <= both children  (root = minimum)");
        System.out.println("  MAX HEAP: parent >= both children  (root = maximum)");
        System.out.println();
        System.out.println("  KEY INSIGHT: A heap does NOT store elements in fully sorted order.");
        System.out.println("  It only guarantees root = min (or max). Children of a node");
        System.out.println("  can be in any order relative to each other.");

        sub("The Array Trick — no pointers needed!");
        System.out.println("  A complete binary tree maps PERFECTLY to an array.");
        System.out.println("  For element at index i:");
        System.out.println("    Parent:       (i - 1) / 2");
        System.out.println("    Left  child:  2 * i + 1");
        System.out.println("    Right child:  2 * i + 2");
        System.out.println();
        System.out.println("  Tree:              Array:");
        System.out.println("        1            [1, 3, 5, 7, 9, 8, 6]");
        System.out.println("       / \\            0  1  2  3  4  5  6");
        System.out.println("      3   5");
        System.out.println("     / \\ / \\");
        System.out.println("    7  9 8  6");
        System.out.println();
        System.out.println("  Index 1 (val=3): parent=(1-1)/2=0 ✓, left=3, right=4 ✓");
        System.out.println("  Index 3 (val=7): parent=(3-1)/2=1 ✓ (parent is 3)");

        sub("Why array storage is better than nodes");
        System.out.println("  Node-based: 24 bytes per node (data + left ptr + right ptr)");
        System.out.println("  Array-based: 4 bytes per int element — 6× more memory-efficient");
        System.out.println("  No pointer chasing → CPU cache friendly → faster in practice");

        sub("Heap vs BST — critical distinction");
        System.out.println("  ┌─────────────────────┬──────────────────┬──────────────────┐");
        System.out.println("  │  Property           │  Heap            │  BST             │");
        System.out.println("  ├─────────────────────┼──────────────────┼──────────────────┤");
        System.out.println("  │  Ordering           │  Parent vs child │  Left < root < R │");
        System.out.println("  │  Min/Max access     │  O(1) — root     │  O(log n)        │");
        System.out.println("  │  Arbitrary search   │  O(n)            │  O(log n)        │");
        System.out.println("  │  In-order traversal │  Not meaningful  │  Sorted output   │");
        System.out.println("  │  Storage            │  Array (compact) │  Node objects    │");
        System.out.println("  │  Best for           │  Priority access │  Sorted data     │");
        System.out.println("  └─────────────────────┴──────────────────┴──────────────────┘");
        System.out.println("  Use HEAP when you repeatedly need the min or max.");
        System.out.println("  Use BST when you need sorted order or arbitrary search.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 2 — MIN HEAP vs MAX HEAP
        // ═══════════════════════════════════════════════════════
        section("TOPIC 2 — Min Heap vs Max Heap");

        sub("Min Heap — root is always MINIMUM");
        MinHeap minH = new MinHeap(20);
        int[] insertOrder = {5, 3, 8, 1, 9, 2, 7};
        for (int v : insertOrder) minH.insert(v);
        minH.print("After inserts " + Arrays.toString(insertOrder));
        minH.printTree();
        System.out.println();
        System.out.println("  peek()  = " + minH.peek() + "  ← minimum always at root, O(1)");
        System.out.println("  Drain order (extractMin repeatedly):");
        System.out.print("  ");
        MinHeap drainMin = new MinHeap(20);
        for (int v : insertOrder) drainMin.insert(v);
        while (!drainMin.isEmpty()) System.out.print(drainMin.extractMin() + " ");
        System.out.println("← ascending order!");

        sub("Max Heap — root is always MAXIMUM");
        MaxHeap maxH = new MaxHeap(20);
        for (int v : insertOrder) maxH.insert(v);
        maxH.print("After inserts " + Arrays.toString(insertOrder));
        System.out.println("  peek()  = " + maxH.peek() + "  ← maximum always at root, O(1)");
        System.out.println("  Drain order (extractMax repeatedly):");
        System.out.print("  ");
        MaxHeap drainMax = new MaxHeap(20);
        for (int v : insertOrder) drainMax.insert(v);
        while (!drainMax.isEmpty()) System.out.print(drainMax.extractMax() + " ");
        System.out.println("← descending order!");

        sub("Min Heap vs Max Heap — when to use which");
        System.out.println("  MIN HEAP → always gives smallest first:");
        System.out.println("    Dijkstra's shortest path, Prim's MST,");
        System.out.println("    merge k sorted lists, kth LARGEST (counterintuitive!)");
        System.out.println();
        System.out.println("  MAX HEAP → always gives largest first:");
        System.out.println("    Job scheduling, top-k most frequent,");
        System.out.println("    kth SMALLEST (counterintuitive!)");
        System.out.println();
        System.out.println("  ── The counterintuitive trick ──");
        System.out.println("  kth LARGEST  → MIN heap of size k");
        System.out.println("                 Root = smallest of top-k = kth largest");
        System.out.println("  kth SMALLEST → MAX heap of size k");
        System.out.println("                 Root = largest of bottom-k = kth smallest");

        sub("Heap variants");
        System.out.println("  Binary Heap:   2 children per node (what we implemented)");
        System.out.println("  D-ary Heap:    d children per node (flatter, fewer swaps up)");
        System.out.println("  Fibonacci Heap:amortized O(1) decrease-key (Dijkstra optimal)");
        System.out.println("  Binomial Heap: mergeable heap for union in O(log n)");


        // ═══════════════════════════════════════════════════════
        // TOPIC 3 — HEAP OPERATIONS & COMPLEXITY
        // ═══════════════════════════════════════════════════════
        section("TOPIC 3 — Heap Operations & Complexity");

        sub("INSERT (heapify up) — O(log n)");
        MinHeap opHeap = new MinHeap(20);
        opHeap.insert(10); opHeap.insert(20); opHeap.insert(15);
        opHeap.insert(30); opHeap.insert(40);
        opHeap.print("Initial heap");
        System.out.println("  Inserting 5:");
        System.out.println("  Step 1: Add 5 at end → [10,20,15,30,40,5]");
        System.out.println("  Step 2: parent(5)=index 2(val=15) > 5 → swap");
        System.out.println("          [10,20,5,30,40,15]");
        System.out.println("  Step 3: parent(2)=index 0(val=10) > 5 → swap");
        System.out.println("          [5,20,10,30,40,15]");
        System.out.println("  Step 4: reached root → STOP");
        opHeap.insert(5);
        opHeap.print("After insert(5)");

        sub("EXTRACT MIN (heapify down) — O(log n)");
        System.out.println("  extractMin() on [5,20,10,30,40,15]:");
        System.out.println("  Step 1: Save root 5 (answer)");
        System.out.println("  Step 2: Move last element 15 to root → [15,20,10,30,40]");
        System.out.println("  Step 3: 15 > min(20,10)=10 → swap with right child");
        System.out.println("          [10,20,15,30,40]");
        System.out.println("  Step 4: 15 has no children → STOP");
        int extracted = opHeap.extractMin();
        System.out.println("  Extracted: " + extracted);
        opHeap.print("After extractMin()");

        sub("BUILD HEAP — O(n) not O(n log n)");
        int[] rawArr = {9, 4, 7, 1, 5, 3, 6, 2, 8};
        System.out.println("  Input array: " + Arrays.toString(rawArr));
        System.out.println("  Naive approach: insert n elements one by one → O(n log n)");
        System.out.println("  Smart buildHeap: heapifyDown from last non-leaf → O(n)");
        System.out.println();
        System.out.println("  Why O(n)? Most nodes are near the bottom:");
        System.out.println("  n/2 nodes are leaves         → 0 swaps each");
        System.out.println("  n/4 nodes are at height 1    → ≤1 swap each");
        System.out.println("  n/8 nodes are at height 2    → ≤2 swaps each");
        System.out.println("  Total = n/2·0 + n/4·1 + n/8·2 + ... = O(n) (geometric series)");
        MinHeap buildDemo = new MinHeap(20);
        buildDemo.buildHeap(rawArr);
        buildDemo.print("After buildHeap()");

        sub("HEAP SORT — O(n log n), in-place");
        int[] sortArr = {64, 25, 12, 22, 11};
        System.out.println("  Before: " + Arrays.toString(sortArr));
        heapSort(sortArr);
        System.out.println("  After:  " + Arrays.toString(sortArr));
        System.out.println();
        System.out.println("  Phase 1: buildMaxHeap → O(n)");
        System.out.println("  Phase 2: n-1 times: swap root with last, shrink, heapifyDown");
        System.out.println("  Each extraction: O(log n) → n extractions → O(n log n)");
        System.out.println("  Space: O(1) in-place! (unlike merge sort's O(n))");
        System.out.println("  Note: NOT stable (equal elements may swap relative order)");

        sub("Complexity Summary");
        System.out.println("  ┌─────────────────────┬────────────┬────────────────────────┐");
        System.out.println("  │  Operation          │  Time      │  Why                   │");
        System.out.println("  ├─────────────────────┼────────────┼────────────────────────┤");
        System.out.println("  │  insert             │  O(log n)  │  Bubble up height = h  │");
        System.out.println("  │  extractMin/Max     │  O(log n)  │  Bubble down height=h  │");
        System.out.println("  │  peek (min/max)     │  O(1)      │  Always at root arr[0] │");
        System.out.println("  │  buildHeap          │  O(n)      │  Geometric series proof│");
        System.out.println("  │  heapSort           │  O(n log n)│  n extractions         │");
        System.out.println("  │  search (arbitrary) │  O(n)      │  No ordering guarantee │");
        System.out.println("  │  delete (arbitrary) │  O(log n)  │  decreaseKey + extract │");
        System.out.println("  ├─────────────────────┼────────────┼────────────────────────┤");
        System.out.println("  │  Space              │  O(n)      │  n elements in array   │");
        System.out.println("  └─────────────────────┴────────────┴────────────────────────┘");


        // ═══════════════════════════════════════════════════════
        // TOPIC 4 — PRIORITY QUEUE IN JAVA
        // ═══════════════════════════════════════════════════════
        section("TOPIC 4 — PriorityQueue in Java");

        sub("Java PriorityQueue — Min Heap by default");
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        System.out.println("  PriorityQueue<Integer> pq = new PriorityQueue<>()");
        System.out.println("  Default: MIN heap (natural ordering)");
        System.out.println();
        pq.offer(5); pq.offer(1); pq.offer(8); pq.offer(3); pq.offer(9);
        System.out.println("  After offer(5,1,8,3,9): " + pq);
        System.out.println("  peek() = " + pq.peek() + "  ← minimum");
        System.out.println("  poll() = " + pq.poll() + "  ← removes minimum");
        System.out.println("  poll() = " + pq.poll());
        System.out.println("  After two polls: " + pq);

        sub("Max Heap using reverseOrder()");
        PriorityQueue<Integer> maxPQ =
                new PriorityQueue<>(Collections.reverseOrder());
        maxPQ.offer(5); maxPQ.offer(1); maxPQ.offer(8); maxPQ.offer(3);
        System.out.println("  PriorityQueue<>(Collections.reverseOrder())");
        System.out.println("  After offer(5,1,8,3): " + maxPQ);
        System.out.println("  peek() = " + maxPQ.peek() + "  ← maximum");

        sub("Custom Comparator — Priority Queue of objects");
        record Employee(String name, int priority) {}

        PriorityQueue<Employee> taskQueue =
                new PriorityQueue<>(Comparator.comparingInt(Employee::priority));
        taskQueue.offer(new Employee("Alice", 3));
        taskQueue.offer(new Employee("Navaneeth", 1)); // highest priority (lowest number)
        taskQueue.offer(new Employee("Priya", 2));
        taskQueue.offer(new Employee("Ravi", 1));

        System.out.println("  Task priority queue (1=highest):");
        System.out.println("  Employees: Alice(3), Navaneeth(1), Priya(2), Ravi(1)");
        System.out.println("  Processing order:");
        while (!taskQueue.isEmpty()) {
            Employee e = taskQueue.poll();
            System.out.println("    → " + e.name() + " (priority=" + e.priority() + ")");
        }

        sub("Key PriorityQueue methods");
        System.out.println("  offer(e)  / add(e)     → insert. offer=null-safe, add throws");
        System.out.println("  poll()    / remove()   → remove+return min. poll=null-safe");
        System.out.println("  peek()    / element()  → view min. peek=null-safe");
        System.out.println("  size()                 → number of elements");
        System.out.println("  isEmpty()              → true if empty");
        System.out.println("  contains(o)            → O(n) linear scan!");
        System.out.println("  remove(o)              → O(n) find + O(log n) remove");
        System.out.println("  toArray()              → O(n), NOT sorted");
        System.out.println();
        System.out.println("  IMPORTANT: Iterating PriorityQueue does NOT give sorted order!");
        System.out.println("  Only poll() gives sorted order (O(log n) per element).");

        sub("PriorityQueue with pairs — common pattern");
        PriorityQueue<int[]> pairPQ =
                new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pairPQ.offer(new int[]{3, 100});
        pairPQ.offer(new int[]{1, 200});
        pairPQ.offer(new int[]{2, 300});
        System.out.println("  Min heap of [priority, value] pairs:");
        while (!pairPQ.isEmpty()) {
            int[] p = pairPQ.poll();
            System.out.println("    priority=" + p[0] + " value=" + p[1]);
        }


        // ═══════════════════════════════════════════════════════
        // TOPIC 5 — REAL-WORLD APPLICATIONS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 5 — Real-World Applications");

        sub("1. OS Process Scheduling");
        System.out.println("  Each process has a priority. CPU executes highest-priority first.");
        System.out.println("  OS maintains a Max Priority Queue (Max Heap).");
        PriorityQueue<int[]> os =
                new PriorityQueue<>((a,b) -> b[0] - a[0]); // max by priority
        os.offer(new int[]{5, 1001}); // [priority, pid]
        os.offer(new int[]{8, 1002});
        os.offer(new int[]{3, 1003});
        os.offer(new int[]{8, 1004});
        System.out.println("  CPU dispatch order:");
        while (!os.isEmpty()) {
            int[] proc = os.poll();
            System.out.println("    PID=" + proc[1] + " priority=" + proc[0]);
        }

        sub("2. Dijkstra's Shortest Path — Min Heap");
        System.out.println("  Graph with weighted edges:");
        System.out.println("  A─(4)─B─(3)─D");
        System.out.println("  A─(2)─C─(1)─B");
        System.out.println();
        System.out.println("  Dijkstra uses Min Heap: (distance, node)");
        System.out.println("  Start from A:");
        System.out.println("  Heap: [(0,A)]");
        System.out.println("  Pop (0,A) → relax B(4), C(2). Heap: [(2,C),(4,B)]");
        System.out.println("  Pop (2,C) → relax B(2+1=3). Heap: [(3,B),(4,B)]");
        System.out.println("  Pop (3,B) → relax D(3+3=6). Heap: [(4,B),(6,D)]");
        System.out.println("  Pop (4,B) already visited. Pop (6,D) → done.");
        System.out.println("  Shortest: A→C→B→D = 6");
        System.out.println("  Time: O((V + E) log V) where V=vertices, E=edges");

        sub("3. Huffman Encoding — Min Heap");
        System.out.println("  Data compression: frequent chars get shorter codes.");
        System.out.println("  Build Huffman tree: always merge two LOWEST frequency nodes.");
        System.out.println();
        System.out.println("  Text: 'aabbcde'");
        System.out.println("  Frequencies: a=2, b=2, c=1, d=1, e=1");
        PriorityQueue<long[]> huffman = new PriorityQueue<>(Comparator.comparingLong(x -> x[0]));
        huffman.offer(new long[]{2,'a'}); huffman.offer(new long[]{2,'b'});
        huffman.offer(new long[]{1,'c'}); huffman.offer(new long[]{1,'d'});
        huffman.offer(new long[]{1,'e'});
        System.out.println("  Merging steps:");
        while (huffman.size() > 1) {
            long[] a = huffman.poll(), b = huffman.poll();
            long merged = a[0] + b[0];
            System.out.printf("    Merge freq=%d + freq=%d → combined freq=%d%n",
                    a[0], b[0], merged);
            huffman.offer(new long[]{merged, 0});
        }
        System.out.println("  Result: optimal prefix-free binary code");
        System.out.println("  Used in: GZIP, JPEG, MP3 compression");

        sub("4. Event-Driven Simulation");
        System.out.println("  Events processed in time order using Min Heap.");
        PriorityQueue<long[]> events =
                new PriorityQueue<>(Comparator.comparingLong(e -> e[0]));
        events.offer(new long[]{100, 1}); // [timestamp, eventId]
        events.offer(new long[]{50, 2});
        events.offer(new long[]{200, 3});
        events.offer(new long[]{75, 4});
        System.out.println("  Event queue processing (chronological):");
        while (!events.isEmpty()) {
            long[] e = events.poll();
            System.out.println("    t=" + e[0] + " Event-" + e[1]);
        }
        System.out.println("  Used in: network packet scheduling, game engines, simulations");

        sub("5. Median Maintenance — Two Heaps");
        MedianFinder mf = new MedianFinder();
        int[] stream = {5, 15, 1, 3, 8, 7, 9, 2};
        System.out.println("  Stream:  " + Arrays.toString(stream));
        System.out.print("  Medians: ");
        for (int val : stream) {
            mf.addNum(val);
            System.out.printf("%.1f ", mf.findMedian());
        }
        System.out.println();
        System.out.println("  Used in: real-time analytics, stock price tracking");


        // ═══════════════════════════════════════════════════════
        // TOPIC 6 — HEAP-BASED PROBLEM PATTERNS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 6 — Heap-Based Problem Patterns");

        sub("Pattern 1 — K-th Element (Min/Max Heap of size K)");
        System.out.println("  Problem type: kth largest / kth smallest in array or stream");
        System.out.println("  Template:");
        System.out.println("    kth LARGEST  → Min heap of size k");
        System.out.println("                   For each element: push, if size>k pop min");
        System.out.println("                   Root = kth largest");
        System.out.println("    kth SMALLEST → Max heap of size k");
        System.out.println("                   For each element: push, if size>k pop max");
        System.out.println("                   Root = kth smallest");
        System.out.println();
        int[] kArr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        System.out.println("  Array: " + Arrays.toString(kArr));
        System.out.println("  3rd largest:  " + kthLargest(kArr, 3));
        System.out.println("  3rd smallest: " + kthSmallest(kArr, 3));

        sub("Pattern 2 — Top K Elements (Freq Heap)");
        int[] freqArr = {1,1,1,2,2,3,3,3,3,4};
        int[] topK = topKFrequent(freqArr, 2);
        System.out.println("  Array: " + Arrays.toString(freqArr));
        System.out.println("  Top 2 frequent: " + Arrays.toString(topK));
        System.out.println("  Template: freq map → min heap of size k by frequency");

        sub("Pattern 3 — Merge K Sorted Sequences");
        int[][] kLists = {{1,4,7},{2,5,8},{3,6,9}};
        System.out.println("  Lists: " + Arrays.deepToString(kLists));
        System.out.println("  Merged: " + Arrays.toString(mergeKSorted(kLists)));
        System.out.println("  Template: init heap with first of each list,");
        System.out.println("            extract min, add its successor");

        sub("Pattern 4 — Two Heaps (Median / Balance)");
        MedianFinder mf2 = new MedianFinder();
        int[] mStream = {6, 3, 8, 2, 9, 1};
        System.out.println("  Stream: " + Arrays.toString(mStream));
        System.out.print("  Running medians: ");
        for (int v : mStream) { mf2.addNum(v); System.out.printf("%.1f ",mf2.findMedian()); }
        System.out.println();
        System.out.println("  Template: maxHeap (lower half) + minHeap (upper half)");
        System.out.println("            Keep sizes balanced. Root of maxHeap = median.");

        sub("Pattern 5 — Greedy with Heap");
        char[] tasks = {'A','A','A','B','B','B'};
        System.out.println("  Tasks: " + Arrays.toString(tasks) + " cooldown=2");
        System.out.println("  Min time: " + taskScheduler(tasks, 2) + " units");
        System.out.println("  Template: Max heap gives most frequent available task.");
        System.out.println("  Always make locally optimal choice (greedy).");

        sub("Pattern 6 — Sliding Window with Heap");
        int[] swArr = {1,3,-1,-3,5,3,6,7};
        double[] medians = slidingWindowMedian(swArr, 3);
        System.out.println("  Array: " + Arrays.toString(swArr) + " k=3");
        System.out.println("  Window medians: " + Arrays.toString(medians));
        System.out.println("  Template: two heaps + lazy deletion for O(n log k)");

        sub("Pattern Cheat Sheet");
        System.out.println("  ┌─────────────────────────────────┬─────────────────────────┐");
        System.out.println("  │  Problem Signal                 │  Heap Pattern           │");
        System.out.println("  ├─────────────────────────────────┼─────────────────────────┤");
        System.out.println("  │  kth largest / running top-k    │  Min heap, size k        │");
        System.out.println("  │  kth smallest                   │  Max heap, size k        │");
        System.out.println("  │  Top k frequent                 │  Min heap by freq, size k│");
        System.out.println("  │  Merge k sorted                 │  Min heap, one per list  │");
        System.out.println("  │  Running median                 │  Two heaps (max+min)     │");
        System.out.println("  │  Greedy + always pick min/max   │  PriorityQueue           │");
        System.out.println("  │  Sliding window min/max         │  Deque (monotonic)       │");
        System.out.println("  │  Sliding window median          │  Two heaps + lazy delete │");
        System.out.println("  └─────────────────────────────────┴─────────────────────────┘");


        // ═══════════════════════════════════════════════════════
        // TOPIC 7 — INTERVIEW-LEVEL PROBLEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 7 — Interview-Level Problems");

        sub("Problem 1: Kth Largest Element in Array");
        int[][] kthTests = {{3,2,1,5,6,4},{3,2,3,1,2,4,5,5,6}};
        int[] kthKs = {2, 4};
        for (int i = 0; i < kthTests.length; i++) {
            System.out.printf("  nums=%-25s k=%d → %d%n",
                    Arrays.toString(kthTests[i]), kthKs[i],
                    kthLargest(kthTests[i], kthKs[i]));
        }
        System.out.println("  Technique: Min heap size k. Root = kth largest. O(n log k)/O(k)");

        sub("Problem 2: Top K Frequent Elements");
        int[][] tfTests = {{1,1,1,2,2,3},{1},{4,1,2,2,3,3,3}};
        int[] tfKs = {2, 1, 2};
        for (int i = 0; i < tfTests.length; i++) {
            System.out.printf("  nums=%-20s k=%d → %s%n",
                    Arrays.toString(tfTests[i]), tfKs[i],
                    Arrays.toString(topKFrequent(tfTests[i], tfKs[i])));
        }
        System.out.println("  Technique: freq map + min heap by freq, size k. O(n log k)/O(n)");

        sub("Problem 3: Merge K Sorted Lists");
        int[][][] mTests = {
            {{1,4,7},{2,5,8},{3,6,9}},
            {{1,3,5},{2,4,6}},
            {{1}}
        };
        for (int[][] lists : mTests) {
            System.out.printf("  %-35s → %s%n",
                    Arrays.deepToString(lists),
                    Arrays.toString(mergeKSorted(lists)));
        }
        System.out.println("  Technique: min heap with (val,listIdx,elemIdx). O(n log k)/O(k)");

        sub("Problem 4: Find Median from Data Stream");
        MedianFinder mFinder = new MedianFinder();
        int[] addSeq = {1, 7, 3, 5, 2, 9, 4};
        System.out.println("  Stream sequence: " + Arrays.toString(addSeq));
        for (int val : addSeq) {
            mFinder.addNum(val);
            System.out.printf("  add(%d) → median=%.1f%n", val, mFinder.findMedian());
        }
        System.out.println("  Technique: maxHeap(lower)+minHeap(upper). O(log n) add, O(1) median");

        sub("Problem 5: Task Scheduler");
        char[][] taskSets = {
            {'A','A','A','B','B','B'},
            {'A','A','A','B','B','B'},
            {'A','A','A','A','A','A'}
        };
        int[] ns = {2, 0, 2};
        for (int i = 0; i < taskSets.length; i++) {
            System.out.printf("  tasks=%s n=%d → min_time=%d%n",
                    Arrays.toString(taskSets[i]), ns[i],
                    taskScheduler(taskSets[i], ns[i]));
        }
        System.out.println("  Technique: max heap + cycle simulation. O(n log n)/O(1)");

        sub("Problem 6: Sliding Window Median");
        int[][] swTests = {
            {1,3,-1,-3,5,3,6,7},
            {1,2,3,4,2,3,1,4,2}
        };
        int[] swKs = {3, 4};
        for (int i = 0; i < swTests.length; i++) {
            System.out.printf("  nums=%-25s k=%d → %s%n",
                    Arrays.toString(swTests[i]), swKs[i],
                    Arrays.toString(slidingWindowMedian(swTests[i], swKs[i])));
        }
        System.out.println("  Technique: two heaps + lazy deletion. O(n log k)/O(k)");

        sub("Problem 7: Kth Largest in Stream");
        KthLargestStream kls = new KthLargestStream(3, new int[]{4, 5, 8, 2});
        int[] addToStream = {3, 5, 10, 9, 4};
        System.out.println("  Initial: [4,5,8,2], k=3");
        for (int v : addToStream) {
            System.out.printf("  add(%2d) → 3rd largest = %d%n", v, kls.add(v));
        }
        System.out.println("  Technique: min heap size k maintained online. O(log k) per add");

        sub("Problem 8: Reorganize String");
        String[] reorgTests = {"aab","aaab","aabb","vvvlo"};
        for (String s : reorgTests) {
            String result = reorganizeString(s);
            System.out.printf("  %-8s → %-8s %s%n", "\""+s+"\"",
                    result.isEmpty() ? "impossible" : "\""+result+"\"",
                    result.isEmpty() ? "" : "(no adjacent same chars ✓)");
        }
        System.out.println("  Technique: max heap by freq, place 2 most frequent alternately.");
        System.out.println("  O(n log n)/O(n)");

        sub("Problem 9: Minimum Cost to Connect Ropes");
        int[][] ropeTests = {{4,3,2,6},{1,2,3,4,5},{3,3,3}};
        for (int[] r : ropeTests) {
            System.out.printf("  ropes=%-15s → min cost=%d%n",
                    Arrays.toString(r), minCostConnectRopes(r));
        }
        System.out.println("  Technique: always merge two shortest (min heap).");
        System.out.println("  Greedy proof: merging shorter ropes first minimizes total additions.");
        System.out.println("  O(n log n)/O(n)");

        sub("Problem 10: Meeting Rooms II (Min Heap)");
        System.out.println("  Find minimum number of meeting rooms needed.");
        System.out.println("  Approach: sort by start time. Min heap tracks end times.");
        System.out.println("  If new meeting starts after heap.peek() ends → reuse room.");
        int[][] meetings = {{0,30},{5,10},{15,20}};
        Arrays.sort(meetings, Comparator.comparingInt(m -> m[0]));
        PriorityQueue<Integer> endTimes = new PriorityQueue<>();
        for (int[] m : meetings) {
            if (!endTimes.isEmpty() && endTimes.peek() <= m[0]) {
                endTimes.poll(); // reuse room
            }
            endTimes.offer(m[1]);
        }
        System.out.println("  Meetings: " + Arrays.deepToString(meetings));
        System.out.println("  Min rooms needed: " + endTimes.size());
        System.out.println("  Technique: sort + min heap of end times. O(n log n)/O(n)");

        // GRAND SUMMARY
        section("GRAND SUMMARY");
        System.out.println();
        System.out.println("  Heap = Complete Binary Tree stored as Array.");
        System.out.println("  MinHeap root = min. MaxHeap root = max. Always O(1) peek.");
        System.out.println();
        System.out.println("  OPERATIONS:");
        System.out.println("  peek()    → O(1)      always — root is min/max");
        System.out.println("  insert()  → O(log n)  bubble up");
        System.out.println("  extract() → O(log n)  bubble down");
        System.out.println("  buildHeap → O(n)      NOT O(n log n)!");
        System.out.println("  heapSort  → O(n log n)in-place, not stable");
        System.out.println();
        System.out.println("  GOLDEN RULES:");
        System.out.println("  1. kth LARGEST  → Min heap of size k (counterintuitive!)");
        System.out.println("  2. kth SMALLEST → Max heap of size k");
        System.out.println("  3. Running median → Two heaps (maxH lower, minH upper)");
        System.out.println("  4. Merge k sorted → Min heap with one entry per list");
        System.out.println("  5. Greedy + repeated min/max → Priority Queue");
        System.out.println("  6. Java PriorityQueue: MIN by default. MAX needs reverseOrder()");
        System.out.println("  7. Iterating PQ ≠ sorted. Only poll() gives sorted order.");
    }
}
