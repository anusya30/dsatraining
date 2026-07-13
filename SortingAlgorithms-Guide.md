# Sorting Algorithms — Complete Guide
> Covers: Why Sorting Matters · Basic Sorts · Efficient Sorts · Divide & Conquer · Non-Comparison Sorts · Java Built-in Sorting · Real-World Systems

---

## Table of Contents
1. [Why Sorting Matters](#1-why-sorting-matters)
2. [Basic Sorting Algorithms](#2-basic-sorting-algorithms)
3. [Efficient Sorting Algorithms](#3-efficient-sorting-algorithms)
4. [Divide & Conquer Sorting](#4-divide--conquer-sorting)
5. [Non-Comparison Sorts](#5-non-comparison-sorts)
6. [Java Built-in Sorting](#6-java-built-in-sorting)
7. [Real-World Systems](#7-real-world-systems)

---

## 1. Why Sorting Matters

### What Is Sorting?
Sorting is the process of **arranging elements in a defined order** (ascending, descending, or by a custom key). It is one of the most studied problems in computer science — not because sorting itself is the end goal, but because **sorted data enables everything else to be faster**.

### The Foundational Impact

| Without Sorting | With Sorting |
|---|---|
| Binary search impossible | Binary search: O(log n) |
| Duplicate detection: O(n²) | Duplicate detection: O(n) |
| Merge two datasets: O(n²) | Merge two datasets: O(n) |
| Finding min/max range: O(n) | Finding min/max: O(1) |
| Database joins: full scan | Database joins: merge join |

### Why It Matters for Three Core Areas

**1. Searching Optimization**
You cannot run binary search on unsorted data. Sort once → search in O(log n) forever.
```
Unsorted 1M records: linear search = 1,000,000 comparisons
Sorted   1M records: binary search = 20 comparisons
```

**2. Data Organization**
- Leaderboards, rankings, scheduling, priority queues
- File system directory listings
- Report generation (sorted by date, name, amount)
- Event logs in chronological order

**3. Performance Engineering**
- Cache efficiency: sorted data has better locality
- Compression: sorted data compresses better (run-length encoding)
- Deduplication: adjacent duplicates trivially removed after sort
- Join algorithms: sort-merge join is O(n log n) vs hash join memory trade-offs

### Real-World Analogy
```
Unsorted deck of cards: finding the Ace of Spades = scan all 52 cards
Sorted deck of cards:   finding the Ace of Spades = go to section A, position 1
```

### The Sorting Landscape

```
Sorting Algorithms
├── Comparison-based (cannot do better than O(n log n))
│   ├── Basic O(n²)
│   │   ├── Bubble Sort
│   │   ├── Selection Sort
│   │   └── Insertion Sort
│   └── Efficient O(n log n)
│       ├── Merge Sort      ← stable, guaranteed O(n log n)
│       ├── Quick Sort      ← fastest in practice, O(n²) worst case
│       ├── Heap Sort       ← in-place, guaranteed O(n log n)
│       └── Tim Sort        ← Java/Python default, hybrid
└── Non-comparison (can beat O(n log n) on integers)
    ├── Counting Sort       ← O(n + k)
    ├── Radix Sort          ← O(nk)
    └── Bucket Sort         ← O(n) average
```

### Theoretical Lower Bound
Any comparison-based sort requires at least **Ω(n log n)** comparisons in the worst case.

**Proof sketch:** n elements have n! possible orderings. Each comparison eliminates half the possibilities. We need at least log₂(n!) comparisons ≈ n log n (Stirling's approximation).

---

## 2. Basic Sorting Algorithms

### 2.1 Bubble Sort

#### Concept
Repeatedly **swap adjacent elements** that are out of order. After each pass, the largest unsorted element "bubbles up" to its correct position.

#### Step-by-Step Trace
```
Array: [5, 3, 8, 1, 2]

Pass 1: [3,5,8,1,2] → [3,5,8,1,2] → [3,5,1,8,2] → [3,5,1,2,8]  ← 8 settled
Pass 2: [3,5,1,2,8] → [3,1,5,2,8] → [3,1,2,5,8]                 ← 5 settled
Pass 3: [1,3,2,5,8] → [1,2,3,5,8]                                ← 3 settled
Pass 4: [1,2,3,5,8]                                               ← done
```

#### Java Implementation
```java
// Basic Bubble Sort
public static void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {   // Last i elements already sorted
            if (arr[j] > arr[j + 1]) {
                swap(arr, j, j + 1);
            }
        }
    }
}

// Optimized Bubble Sort — stops early if already sorted
public static void bubbleSortOptimized(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                swap(arr, j, j + 1);
                swapped = true;
            }
        }
        if (!swapped) break;   // Array already sorted — exit early O(n) best case
    }
}
```

#### Complexity
| Case | Time | Space | Stable? |
|---|---|---|---|
| Best (already sorted) | O(n) | O(1) | Yes |
| Average | O(n²) | O(1) | Yes |
| Worst (reverse sorted) | O(n²) | O(1) | Yes |

#### When to Use
- Teaching purposes — easiest to understand
- Nearly sorted data (optimized version)
- Tiny arrays (< 10 elements)

#### Real-World Example
- Visualizing sort animations in educational tools
- Embedded systems with tiny memory and small fixed-size arrays

---

### 2.2 Selection Sort

#### Concept
Repeatedly **find the minimum element** from the unsorted portion and place it at the beginning. Unlike bubble sort, it makes exactly n-1 swaps.

#### Step-by-Step Trace
```
Array: [5, 3, 8, 1, 2]

Pass 1: min=1 at index 3 → swap with index 0 → [1, 3, 8, 5, 2]
Pass 2: min=2 at index 4 → swap with index 1 → [1, 2, 8, 5, 3]
Pass 3: min=3 at index 4 → swap with index 2 → [1, 2, 3, 5, 8]
Pass 4: min=5 at index 3 → swap with index 3 → [1, 2, 3, 5, 8]
Done!
```

#### Java Implementation
```java
public static void selectionSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        int minIdx = i;                             // Assume current is minimum
        for (int j = i + 1; j < n; j++) {
            if (arr[j] < arr[minIdx]) {
                minIdx = j;                         // Found new minimum
            }
        }
        if (minIdx != i) {
            swap(arr, i, minIdx);                   // Place minimum at position i
        }
    }
}

// Selection sort — find max instead (sort descending)
public static void selectionSortDesc(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        int maxIdx = i;
        for (int j = i + 1; j < n; j++) {
            if (arr[j] > arr[maxIdx]) maxIdx = j;
        }
        swap(arr, i, maxIdx);
    }
}
```

#### Complexity
| Case | Time | Space | Stable? |
|---|---|---|---|
| Best | O(n²) | O(1) | No |
| Average | O(n²) | O(1) | No |
| Worst | O(n²) | O(1) | No |

> **Key characteristic:** Always exactly n(n-1)/2 comparisons, regardless of input. Exactly n-1 swaps — useful when write operations are expensive (e.g., flash memory).

#### When to Use
- When the cost of swapping is high (each swap writes to memory/disk)
- Small arrays where simplicity matters
- When number of writes must be minimized

---

### 2.3 Insertion Sort

#### Concept
Build a sorted array **one element at a time** by inserting each new element into its correct position among the already-sorted elements. Like sorting playing cards in your hand.

#### Step-by-Step Trace
```
Array: [5, 3, 8, 1, 2]

Step 1: key=3  → [3, 5, 8, 1, 2]     ← 3 inserted before 5
Step 2: key=8  → [3, 5, 8, 1, 2]     ← 8 already in place
Step 3: key=1  → [1, 3, 5, 8, 2]     ← 1 inserted at beginning
Step 4: key=2  → [1, 2, 3, 5, 8]     ← 2 inserted after 1
Done!
```

#### Java Implementation
```java
// Basic Insertion Sort
public static void insertionSort(int[] arr) {
    int n = arr.length;
    for (int i = 1; i < n; i++) {
        int key = arr[i];                   // Element to be inserted
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {   // Shift larger elements right
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;                  // Insert key at correct position
    }
}

// Insertion Sort with binary search to find insert position (fewer comparisons)
public static void insertionSortBinary(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
        int key = arr[i];
        int pos = binarySearchPosition(arr, 0, i - 1, key);
        // Shift elements right to make room
        System.arraycopy(arr, pos, arr, pos + 1, i - pos);
        arr[pos] = key;
    }
}

private static int binarySearchPosition(int[] arr, int low, int high, int key) {
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] > key) high = mid - 1;
        else low = mid + 1;
    }
    return low;
}

// Insertion Sort on strings
public static void insertionSortStrings(String[] arr) {
    for (int i = 1; i < arr.length; i++) {
        String key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j].compareTo(key) > 0) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```

#### Complexity
| Case | Time | Space | Stable? |
|---|---|---|---|
| Best (sorted) | O(n) | O(1) | Yes |
| Average | O(n²) | O(1) | Yes |
| Worst (reverse sorted) | O(n²) | O(1) | Yes |

#### When to Use
- **Nearly sorted data** — extremely efficient (O(n) for almost-sorted)
- **Small arrays** — Java's TimSort uses insertion sort for arrays < 32 elements
- **Online sorting** — sort as elements arrive (streaming data)
- **Stable sort needed** on small input

#### Real-World Example
- TimSort (Java/Python default) uses insertion sort for small subarrays
- Sorting a hand of cards as you draw them
- Maintaining a sorted leaderboard as new scores arrive one at a time

---

### 2.4 Shell Sort

#### Concept
Generalization of insertion sort. Sort elements **far apart first** using a gap sequence, then reduce the gap until gap=1 (regular insertion sort). This moves elements closer to their final position faster.

```java
public static void shellSort(int[] arr) {
    int n = arr.length;
    // Knuth sequence: 1, 4, 13, 40, 121, 364...
    int gap = 1;
    while (gap < n / 3) gap = gap * 3 + 1;

    while (gap >= 1) {
        // Insertion sort with this gap
        for (int i = gap; i < n; i++) {
            int key = arr[i];
            int j = i - gap;
            while (j >= 0 && arr[j] > key) {
                arr[j + gap] = arr[j];
                j -= gap;
            }
            arr[j + gap] = key;
        }
        gap /= 3;   // Reduce gap
    }
}
```

**Complexity:** O(n log² n) with Knuth sequence — significantly better than O(n²)

---

## 3. Efficient Sorting Algorithms

### 3.1 Heap Sort

#### Concept
Uses a **max-heap** data structure. First builds a max-heap from the array, then repeatedly extracts the maximum element (root) and places it at the end of the array.

#### How a Max-Heap Works
```
Array: [4, 10, 3, 5, 1]

Build Max-Heap:
         10
        /  \
       5    3
      / \
     4   1

After heapify: [10, 5, 3, 4, 1]

Extract max (10), place at end → heapify rest:
         5
        / \
       4   3
      /
     1
[5, 4, 3, 1, 10]

Repeat → [4, 1, 3, 5, 10] → [3, 1, 4, 5, 10] → [1, 3, 4, 5, 10]
Final: [1, 3, 4, 5, 10] ✓
```

#### Java Implementation
```java
public static void heapSort(int[] arr) {
    int n = arr.length;

    // Phase 1: Build max-heap
    // Start from last non-leaf node and heapify down
    for (int i = n / 2 - 1; i >= 0; i--) {
        heapify(arr, n, i);
    }

    // Phase 2: Extract elements from heap one by one
    for (int i = n - 1; i > 0; i--) {
        swap(arr, 0, i);        // Move current root (max) to end
        heapify(arr, i, 0);    // Heapify the reduced heap
    }
}

// Heapify subtree rooted at index i in heap of size n
private static void heapify(int[] arr, int n, int i) {
    int largest = i;            // Assume root is largest
    int left  = 2 * i + 1;    // Left child index
    int right = 2 * i + 2;    // Right child index

    if (left  < n && arr[left]  > arr[largest]) largest = left;
    if (right < n && arr[right] > arr[largest]) largest = right;

    if (largest != i) {
        swap(arr, i, largest);
        heapify(arr, n, largest);  // Recursively heapify affected subtree
    }
}
```

#### Complexity
| Case | Time | Space | Stable? |
|---|---|---|---|
| Best | O(n log n) | O(1) | No |
| Average | O(n log n) | O(1) | No |
| Worst | O(n log n) | O(1) | No |

**Key advantage:** Guaranteed O(n log n) AND O(1) space — no worst-case degradation like QuickSort.

#### When to Use
- Memory-constrained environments (O(1) space)
- Real-time systems needing guaranteed O(n log n)
- Priority queue operations
- When you need in-place sorting with guaranteed performance

#### Real-World Example
- OS process scheduling (priority queues)
- Network packet scheduling
- Dijkstra's algorithm internally uses a heap

---

## 4. Divide & Conquer Sorting

### 4.1 Merge Sort

#### Concept
**Divide** the array into two halves, **recursively sort** each half, then **merge** the sorted halves. Classic divide and conquer.

#### Divide & Conquer Tree
```
[5, 3, 8, 1, 2, 7, 4, 6]
         DIVIDE
    [5,3,8,1]    [2,7,4,6]
    [5,3] [8,1]  [2,7] [4,6]
    [5][3][8][1] [2][7][4][6]
         MERGE
    [3,5] [1,8]  [2,7] [4,6]
    [1,3,5,8]    [2,4,6,7]
    [1,2,3,4,5,6,7,8]
```

#### Java Implementation
```java
// Top-down Merge Sort (recursive)
public static void mergeSort(int[] arr, int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;   // Find midpoint (overflow-safe)
        mergeSort(arr, left, mid);               // Sort left half
        mergeSort(arr, mid + 1, right);          // Sort right half
        merge(arr, left, mid, right);            // Merge sorted halves
    }
}

private static void merge(int[] arr, int left, int mid, int right) {
    // Sizes of two subarrays
    int n1 = mid - left + 1;
    int n2 = right - mid;

    // Temporary arrays
    int[] L = new int[n1];
    int[] R = new int[n2];

    // Copy data to temp arrays
    System.arraycopy(arr, left,     L, 0, n1);
    System.arraycopy(arr, mid + 1,  R, 0, n2);

    // Merge the temp arrays back
    int i = 0, j = 0, k = left;
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) {     // <= makes it stable
            arr[k++] = L[i++];
        } else {
            arr[k++] = R[j++];
        }
    }
    // Copy remaining elements
    while (i < n1) arr[k++] = L[i++];
    while (j < n2) arr[k++] = R[j++];
}

// Bottom-up Merge Sort (iterative — no recursion stack)
public static void mergeSortBottomUp(int[] arr) {
    int n = arr.length;
    // Merge subarrays of size 1, then 2, then 4, then 8...
    for (int size = 1; size < n; size *= 2) {
        for (int left = 0; left < n - size; left += 2 * size) {
            int mid   = left + size - 1;
            int right = Math.min(left + 2 * size - 1, n - 1);
            merge(arr, left, mid, right);
        }
    }
}

// Merge Sort for linked-list style (counting inversions)
public static long countInversions(int[] arr, int left, int right) {
    long inversions = 0;
    if (left < right) {
        int mid = left + (right - left) / 2;
        inversions += countInversions(arr, left, mid);
        inversions += countInversions(arr, mid + 1, right);
        inversions += mergeCount(arr, left, mid, right);
    }
    return inversions;
}

private static long mergeCount(int[] arr, int left, int mid, int right) {
    int[] L = Arrays.copyOfRange(arr, left, mid + 1);
    int[] R = Arrays.copyOfRange(arr, mid + 1, right + 1);
    int i = 0, j = 0, k = left;
    long inversions = 0;
    while (i < L.length && j < R.length) {
        if (L[i] <= R[j]) {
            arr[k++] = L[i++];
        } else {
            inversions += L.length - i;  // All remaining L elements form inversions
            arr[k++] = R[j++];
        }
    }
    while (i < L.length) arr[k++] = L[i++];
    while (j < R.length) arr[k++] = R[j++];
    return inversions;
}
```

#### Complexity
| Case | Time | Space | Stable? |
|---|---|---|---|
| Best | O(n log n) | O(n) | Yes |
| Average | O(n log n) | O(n) | Yes |
| Worst | O(n log n) | O(n) | Yes |

**Key advantages:**
- Guaranteed O(n log n) in ALL cases
- Stable sort — equal elements maintain relative order
- Best algorithm for sorting **linked lists**
- Can sort data larger than RAM (external sorting)

#### Real-World Example
- **External sorting** — sorting files too large for RAM (merge sorted chunks)
- **Merge of sorted database tables** — sort-merge join
- **Inversion counting** — measuring how unsorted a dataset is
- **Git merge** — conceptually similar to merging two sorted change sets

---

### 4.2 Quick Sort

#### Concept
**Choose a pivot**, **partition** the array so all elements less than pivot are on the left and greater are on the right, then **recursively sort** both partitions. The pivot is now in its final position.

#### Partition Trace
```
Array: [3, 6, 8, 10, 1, 2, 1], pivot = last element = 1

i = -1 (tracks boundary of elements ≤ pivot)

j=0: arr[0]=3 > 1 → skip
j=1: arr[1]=6 > 1 → skip
j=2: arr[2]=8 > 1 → skip
j=3: arr[3]=10> 1 → skip
j=4: arr[4]=1 ≤ 1 → i++, swap(0,4) → [1, 6, 8, 10, 3, 2, 1]
j=5: arr[5]=2 > 1 → skip
Place pivot: swap(i+1=1, end) → [1, 1, 8, 10, 3, 2, 6]
                                       ↑ pivot in final position
Recurse left [1] and right [8,10,3,2,6]
```

#### Java Implementation
```java
// Quick Sort — Lomuto partition scheme
public static void quickSort(int[] arr, int low, int high) {
    if (low < high) {
        int pivotIdx = partition(arr, low, high);
        quickSort(arr, low, pivotIdx - 1);   // Sort left of pivot
        quickSort(arr, pivotIdx + 1, high);  // Sort right of pivot
    }
}

private static int partition(int[] arr, int low, int high) {
    int pivot = arr[high];      // Choose last element as pivot
    int i = low - 1;            // i tracks last element ≤ pivot

    for (int j = low; j < high; j++) {
        if (arr[j] <= pivot) {
            i++;
            swap(arr, i, j);
        }
    }
    swap(arr, i + 1, high);    // Place pivot in correct position
    return i + 1;              // Return pivot's final index
}

// Quick Sort — Hoare partition (faster in practice, fewer swaps)
public static void quickSortHoare(int[] arr, int low, int high) {
    if (low < high) {
        int pivotIdx = partitionHoare(arr, low, high);
        quickSortHoare(arr, low, pivotIdx);
        quickSortHoare(arr, pivotIdx + 1, high);
    }
}

private static int partitionHoare(int[] arr, int low, int high) {
    int pivot = arr[low + (high - low) / 2]; // Middle element as pivot
    int i = low - 1, j = high + 1;
    while (true) {
        do { i++; } while (arr[i] < pivot);
        do { j--; } while (arr[j] > pivot);
        if (i >= j) return j;
        swap(arr, i, j);
    }
}

// Quick Sort — 3-way partition (Dutch National Flag — handles duplicates well)
public static void quickSort3Way(int[] arr, int low, int high) {
    if (low >= high) return;
    int pivot = arr[low];
    int lt = low, gt = high, i = low + 1;
    // Invariant: arr[low..lt-1] < pivot, arr[lt..i-1] == pivot, arr[gt+1..high] > pivot
    while (i <= gt) {
        if      (arr[i] < pivot) swap(arr, lt++, i++);
        else if (arr[i] > pivot) swap(arr, i, gt--);
        else                     i++;
    }
    quickSort3Way(arr, low, lt - 1);
    quickSort3Way(arr, gt + 1, high);
}

// Quick Sort — randomized pivot (avoids O(n²) on sorted input)
public static void quickSortRandom(int[] arr, int low, int high) {
    if (low < high) {
        int pivotIdx = randomPartition(arr, low, high);
        quickSortRandom(arr, low, pivotIdx - 1);
        quickSortRandom(arr, pivotIdx + 1, high);
    }
}

private static int randomPartition(int[] arr, int low, int high) {
    int randomIdx = low + (int)(Math.random() * (high - low + 1));
    swap(arr, randomIdx, high);        // Move random element to end as pivot
    return partition(arr, low, high);  // Standard partition
}

// Quick Select — find kth smallest element in O(n) average
public static int quickSelect(int[] arr, int low, int high, int k) {
    if (low == high) return arr[low];
    int pivotIdx = partition(arr, low, high);
    if (pivotIdx == k)      return arr[pivotIdx];
    else if (pivotIdx < k)  return quickSelect(arr, pivotIdx + 1, high, k);
    else                    return quickSelect(arr, low, pivotIdx - 1, k);
}
```

#### Complexity
| Case | Time | Space | Stable? |
|---|---|---|---|
| Best | O(n log n) | O(log n) | No |
| Average | O(n log n) | O(log n) | No |
| Worst (sorted input, bad pivot) | O(n²) | O(n) | No |

**Why QuickSort is fastest in practice despite O(n²) worst case:**
- Excellent cache performance (in-place, sequential access)
- Low constant factors
- With randomized pivot: O(n²) probability is astronomically low
- 3-way partition handles duplicates efficiently

#### When to Use
- General-purpose sorting when average-case matters
- When in-place sorting required
- QuickSelect for order statistics (kth smallest/largest)

#### Real-World Example
- C++ `std::sort` uses Introsort (QuickSort + HeapSort fallback)
- Database query optimizers
- Finding median of a dataset (QuickSelect)

---

### 4.3 Tim Sort

#### Concept
**Hybrid algorithm** combining Insertion Sort (for small runs) and Merge Sort (for combining runs). Used as the default sort in Java (`Arrays.sort` for objects) and Python.

```
Key ideas:
1. Find or create sorted "runs" of minimum size (32 or 64)
2. Use insertion sort to extend small runs to minRun size
3. Merge runs using a modified merge sort with galloping mode
4. Galloping mode: when one run dominates, use binary search to skip ahead
```

```java
// Simplified TimSort implementation
public static void timSort(int[] arr) {
    int n = arr.length;
    int RUN = 32;

    // Sort individual runs with insertion sort
    for (int i = 0; i < n; i += RUN) {
        insertionSortRange(arr, i, Math.min(i + RUN - 1, n - 1));
    }

    // Merge runs: start with size RUN, then 2*RUN, then 4*RUN...
    for (int size = RUN; size < n; size *= 2) {
        for (int left = 0; left < n; left += 2 * size) {
            int mid   = left + size - 1;
            int right = Math.min(left + 2 * size - 1, n - 1);
            if (mid < right) {
                merge(arr, left, mid, right);
            }
        }
    }
}

private static void insertionSortRange(int[] arr, int left, int right) {
    for (int i = left + 1; i <= right; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= left && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```

#### Complexity
| Case | Time | Space | Stable? |
|---|---|---|---|
| Best (sorted) | O(n) | O(n) | Yes |
| Average | O(n log n) | O(n) | Yes |
| Worst | O(n log n) | O(n) | Yes |

---

## 5. Non-Comparison Sorts

### 5.1 Counting Sort

#### Concept
**Count occurrences** of each element, then reconstruct the sorted array from counts. Does not compare elements — beats O(n log n) lower bound by exploiting integer constraints.

**Constraint:** Elements must be non-negative integers within a known range [0, k].

#### Step-by-Step Trace
```
Array: [4, 2, 2, 8, 3, 3, 1]  range k = 8

Count:  [0, 1, 2, 2, 1, 0, 0, 0, 1]
index:   0  1  2  3  4  5  6  7  8

Prefix sum (cumulative): [0, 1, 3, 5, 6, 6, 6, 6, 7]
                          → tells us ending position of each value

Place elements (right to left for stability):
  8 → position 7-1=6  → output[6]=8
  3 → position 5-1=4  → output[4]=3
  3 → position 4-1=3  → output[3]=3
  ...

Output: [1, 2, 2, 3, 3, 4, 8]
```

#### Java Implementation
```java
public static void countingSort(int[] arr) {
    if (arr.length == 0) return;

    int max = Arrays.stream(arr).max().getAsInt();
    int min = Arrays.stream(arr).min().getAsInt();
    int range = max - min + 1;

    int[] count  = new int[range];     // Count occurrences
    int[] output = new int[arr.length];

    // Count each element
    for (int val : arr) count[val - min]++;

    // Prefix sum — count[i] now holds actual position
    for (int i = 1; i < count.length; i++) count[i] += count[i - 1];

    // Build output array (right to left for stability)
    for (int i = arr.length - 1; i >= 0; i--) {
        output[--count[arr[i] - min]] = arr[i];
    }

    System.arraycopy(output, 0, arr, 0, arr.length);
}

// Counting Sort for characters (sort a string)
public static String countingSortString(String s) {
    int[] count = new int[256]; // ASCII range
    for (char c : s.toCharArray()) count[c]++;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 256; i++) {
        while (count[i]-- > 0) sb.append((char) i);
    }
    return sb.toString();
}
```

#### Complexity
| Case | Time | Space |
|---|---|---|
| All cases | O(n + k) | O(n + k) |

> k = range of values. Efficient when k = O(n). Poor when k >> n.

#### When to Use
- Sorting ages (range 0-150)
- Sorting exam scores (range 0-100)
- Sorting small integers with known bounded range
- As a subroutine in Radix Sort

---

### 5.2 Radix Sort

#### Concept
Sort integers **digit by digit** from least significant to most significant digit (LSD), using Counting Sort as a stable subroutine for each digit position.

#### Step-by-Step Trace
```
Array: [170, 45, 75, 90, 802, 24, 2, 66]

Sort by ones digit:
  170→0  90→0  802→2  2→2  24→4  45→5  75→5  66→6
  [170, 90, 802, 2, 24, 45, 75, 66]

Sort by tens digit:
  802→0  2→0  24→2  45→4  66→6  170→7  75→7  90→9
  [802, 2, 24, 45, 66, 170, 75, 90]

Sort by hundreds digit:
  2→0  24→0  45→0  66→0  75→0  90→0  170→1  802→8
  [2, 24, 45, 66, 75, 90, 170, 802]  ✓
```

#### Java Implementation
```java
public static void radixSort(int[] arr) {
    int max = Arrays.stream(arr).max().getAsInt();

    // Sort by each digit position (1s, 10s, 100s, ...)
    for (int exp = 1; max / exp > 0; exp *= 10) {
        countingSortByDigit(arr, exp);
    }
}

private static void countingSortByDigit(int[] arr, int exp) {
    int n = arr.length;
    int[] output = new int[n];
    int[] count  = new int[10];       // 10 digits: 0-9

    // Count occurrences of each digit
    for (int val : arr) count[(val / exp) % 10]++;

    // Prefix sum
    for (int i = 1; i < 10; i++) count[i] += count[i - 1];

    // Build output (right to left for stability)
    for (int i = n - 1; i >= 0; i--) {
        int digit = (arr[i] / exp) % 10;
        output[--count[digit]] = arr[i];
    }

    System.arraycopy(output, 0, arr, 0, n);
}

// Radix Sort for strings (by character position, MSD)
public static void radixSortStrings(String[] arr, int maxLen) {
    // Sort from most significant character to least
    for (int pos = maxLen - 1; pos >= 0; pos--) {
        final int p = pos;
        // Stable sort by character at position p
        Arrays.sort(arr, Comparator.comparingInt(s ->
            p < s.length() ? s.charAt(p) : 0));
    }
}
```

#### Complexity
| Case | Time | Space |
|---|---|---|
| All cases | O(nk) | O(n + k) |

> n = number of elements, k = number of digits. For fixed-length integers: O(n).

#### When to Use
- Large sets of integers with bounded digit count
- Sorting IP addresses (4 bytes = 4 digit positions in base 256)
- Sorting fixed-length strings (ZIP codes, phone numbers)
- Sorting records by multiple keys (radix property)

#### Real-World Example
- **IP routing tables** — routers use radix tries (prefix-based radix sort)
- **Suffix array construction** — DC3/skew algorithm uses radix sort
- **Sorting credit card numbers**

---

### 5.3 Bucket Sort

#### Concept
**Distribute elements into buckets** based on value range, sort each bucket (usually with insertion sort), then concatenate. Works best when input is **uniformly distributed**.

```java
public static void bucketSort(float[] arr) {
    int n = arr.length;
    @SuppressWarnings("unchecked")
    List<Float>[] buckets = new List[n];
    for (int i = 0; i < n; i++) buckets[i] = new ArrayList<>();

    // Distribute elements into buckets
    for (float val : arr) {
        int idx = (int)(val * n);               // Map [0,1) to bucket index
        idx = Math.min(idx, n - 1);             // Guard against val = 1.0
        buckets[idx].add(val);
    }

    // Sort individual buckets and concatenate
    int k = 0;
    for (List<Float> bucket : buckets) {
        Collections.sort(bucket);               // Insertion sort internally for small lists
        for (float val : bucket) arr[k++] = val;
    }
}

// Bucket sort for arbitrary range integers
public static void bucketSortInt(int[] arr, int numBuckets) {
    int max = Arrays.stream(arr).max().getAsInt();
    int min = Arrays.stream(arr).min().getAsInt();
    int range = max - min + 1;

    List<Integer>[] buckets = new List[numBuckets];
    for (int i = 0; i < numBuckets; i++) buckets[i] = new ArrayList<>();

    for (int val : arr) {
        int idx = (int)((long)(val - min) * numBuckets / range);
        idx = Math.min(idx, numBuckets - 1);
        buckets[idx].add(val);
    }

    int k = 0;
    for (List<Integer> bucket : buckets) {
        Collections.sort(bucket);
        for (int val : bucket) arr[k++] = val;
    }
}
```

#### Complexity
| Case | Time | Space |
|---|---|---|
| Best / Average (uniform) | O(n + k) | O(n + k) |
| Worst (all in one bucket) | O(n²) | O(n) |

---

## 6. Java Built-in Sorting

### 6.1 Arrays.sort() — Primitives
Java uses **Dual-Pivot QuickSort** for primitive arrays (int, long, double, etc.).

```java
import java.util.Arrays;

// Primitive arrays — uses Dual-Pivot QuickSort
int[]    ints    = {5, 2, 8, 1, 9};
double[] doubles = {3.14, 2.71, 1.41};
char[]   chars   = {'z', 'a', 'm', 'b'};

Arrays.sort(ints);                           // Full sort
Arrays.sort(ints, 1, 4);                     // Sort subarray [1,4)
Arrays.sort(doubles);
Arrays.sort(chars);

System.out.println(Arrays.toString(ints));   // [1, 2, 5, 8, 9]
```

### 6.2 Arrays.sort() — Objects (TimSort)
Java uses **TimSort** for object arrays — stable, O(n log n).

```java
// Object arrays — uses TimSort (stable)
String[] names = {"Charlie", "Alice", "Bob", "Diana"};
Arrays.sort(names);                           // Natural order (alphabetical)

Integer[] nums = {5, 2, 8, 1, 9};
Arrays.sort(nums, Comparator.reverseOrder()); // Descending

// Custom object sorting
Employee[] employees = {
    new Employee("Alice", 75000),
    new Employee("Bob",   50000),
    new Employee("Carol", 90000)
};

// Sort by salary ascending
Arrays.sort(employees, Comparator.comparingDouble(Employee::getSalary));

// Sort by salary descending, then by name ascending
Arrays.sort(employees, Comparator
    .comparingDouble(Employee::getSalary).reversed()
    .thenComparing(Employee::getName));

// Sort by multiple fields using Comparator chain
Arrays.sort(employees, Comparator
    .comparing(Employee::getDepartment)
    .thenComparingDouble(Employee::getSalary)
    .thenComparing(Employee::getName));
```

### 6.3 Collections.sort() and List.sort()
```java
List<Integer> list = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9));

// Method 1
Collections.sort(list);

// Method 2 (preferred — in-place on the list)
list.sort(Comparator.naturalOrder());
list.sort(Comparator.reverseOrder());

// Custom comparator
List<String> words = Arrays.asList("banana", "apple", "cherry", "date");
words.sort(Comparator.comparingInt(String::length));          // by length
words.sort(Comparator.comparingInt(String::length)
           .thenComparing(Comparator.naturalOrder()));        // by length then alpha
```

### 6.4 Stream Sorting
```java
import java.util.stream.*;

int[] arr = {5, 2, 8, 1, 9, 3};

// Sort and collect
List<Integer> sorted = Arrays.stream(arr)
    .boxed()
    .sorted()
    .collect(Collectors.toList());

// Sort descending
List<Integer> desc = Arrays.stream(arr)
    .boxed()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());

// Sort objects by field
List<Employee> sortedEmployees = employees.stream()
    .sorted(Comparator.comparing(Employee::getName))
    .collect(Collectors.toList());

// Sort and find top 3
List<Employee> top3 = employees.stream()
    .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
    .limit(3)
    .collect(Collectors.toList());
```

### 6.5 TreeMap and TreeSet — Sorted by Default
```java
// TreeMap — sorted by key naturally
TreeMap<Integer, String> treeMap = new TreeMap<>();
treeMap.put(3, "three");
treeMap.put(1, "one");
treeMap.put(2, "two");
// Iterates in key order: 1, 2, 3

// TreeSet — sorted set
TreeSet<Integer> treeSet = new TreeSet<>();
treeSet.add(5); treeSet.add(1); treeSet.add(3);
System.out.println(treeSet.first());  // 1
System.out.println(treeSet.last());   // 5

// Custom ordering in TreeMap
TreeMap<String, Integer> byLength = new TreeMap<>(
    Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())
);
```

### 6.6 PriorityQueue — Sorted Retrieval
```java
// Min-heap (default)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.addAll(Arrays.asList(5, 1, 8, 3));
while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " "); // 1 3 5 8

// Max-heap
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

// Custom priority
PriorityQueue<Employee> byPriority = new PriorityQueue<>(
    Comparator.comparingDouble(Employee::getSalary).reversed()
);
```

### 6.7 Parallel Sort
```java
// Parallel sort — uses Fork/Join pool, faster on large arrays with multiple cores
int[] largeArr = new int[10_000_000];
// ... populate ...
Arrays.parallelSort(largeArr);                     // Parallel dual-pivot QuickSort
Arrays.parallelSort(largeArr, 0, 5_000_000);      // Parallel sort subrange
```

### 6.8 When to Use Which Java Sort
```
Primitive array (int[], double[])    → Arrays.sort()         (Dual-Pivot QuickSort)
Object array (String[], Employee[])  → Arrays.sort()         (TimSort, stable)
List<T>                              → list.sort()           (TimSort, stable)
Need stable sort                     → Arrays.sort(objects) or Collections.sort()
Need sorted structure always         → TreeMap / TreeSet
Need top-k elements                  → PriorityQueue
Need parallel sort (n > 1M)         → Arrays.parallelSort()
Custom multi-field sort              → Comparator.comparing().thenComparing()
```

---

## 7. Real-World Systems

### 7.1 Database Sort — ORDER BY Implementation
```java
// Simulated ORDER BY with multiple columns
// SELECT * FROM orders ORDER BY customer_id ASC, total DESC, date ASC
Order[] orders = loadOrders();
Arrays.sort(orders, Comparator
    .comparingInt(Order::getCustomerId)
    .thenComparingDouble(o -> -o.getTotal())      // DESC = negate
    .thenComparing(Order::getDate));
```

**In real databases:**
- Small result sets: in-memory quicksort
- Large result sets: external merge sort (sort chunks, merge from disk)
- Indexed columns: B-tree already keeps data sorted — no sort needed

### 7.2 Search Engine — Ranking Results
```java
// Sort search results by relevance score (multi-factor ranking)
SearchResult[] results = fetchResults(query);
Arrays.sort(results, Comparator
    .comparingDouble(SearchResult::getTfIdfScore).reversed()
    .thenComparingDouble(SearchResult::getPageRank).reversed()
    .thenComparingLong(SearchResult::getRecency).reversed());
```

### 7.3 Log Processing — Chronological Sort
```java
// Sort distributed log entries by timestamp (from multiple servers)
LogEntry[] logs = aggregateLogs(servers);
// Each server produces sorted logs → merge sort is optimal
Arrays.sort(logs, Comparator.comparing(LogEntry::getTimestamp));

// For streaming logs: use priority queue (k-way merge)
PriorityQueue<LogEntry> pq = new PriorityQueue<>(
    Comparator.comparing(LogEntry::getTimestamp));
// Add head of each server's sorted stream
// Poll to get globally sorted output
```

### 7.4 E-Commerce — Product Sorting
```java
// Sort products by multiple criteria based on user selection
public static Product[] sortProducts(Product[] products, String sortBy) {
    Comparator<Product> comparator = switch (sortBy) {
        case "price_asc"    -> Comparator.comparingDouble(Product::getPrice);
        case "price_desc"   -> Comparator.comparingDouble(Product::getPrice).reversed();
        case "rating"       -> Comparator.comparingDouble(Product::getRating).reversed();
        case "newest"       -> Comparator.comparing(Product::getListedDate).reversed();
        case "relevance"    -> Comparator.comparingDouble(Product::getRelevanceScore).reversed();
        default             -> Comparator.comparingDouble(Product::getPopularity).reversed();
    };
    Product[] result = products.clone();
    Arrays.sort(result, comparator);
    return result;
}
```

### 7.5 Operating System — Process Scheduling
```java
// CPU scheduling: sort ready queue by priority + waiting time
Process[] readyQueue = getReadyProcesses();
Arrays.sort(readyQueue, Comparator
    .comparingInt(Process::getPriority).reversed()
    .thenComparingLong(Process::getWaitingTime).reversed());

// Earliest Deadline First (EDF) scheduling
Arrays.sort(readyQueue, Comparator.comparingLong(Process::getDeadline));
```

### 7.6 Network Routing — Route Table Longest Prefix Match
```java
// Sort routing table entries by prefix length (longest prefix first)
Route[] routingTable = loadRoutes();
Arrays.sort(routingTable,
    Comparator.comparingInt(Route::getPrefixLength).reversed());

// Binary search for longest matching prefix
public static Route findRoute(Route[] table, long ipAddress) {
    for (Route route : table) {                          // Table is sorted: longest first
        if (route.matches(ipAddress)) return route;
    }
    return defaultRoute;
}
```

### 7.7 External Sort — Files Too Large for RAM
```java
// External merge sort for large files
public static void externalSort(String inputFile, String outputFile,
                                 int chunkSize) throws IOException {
    List<String> chunkFiles = new ArrayList<>();

    // Phase 1: Sort chunks that fit in memory
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
        List<Integer> chunk = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            chunk.add(Integer.parseInt(line.trim()));
            if (chunk.size() >= chunkSize) {
                Collections.sort(chunk);                 // Sort in memory
                chunkFiles.add(writeSortedChunk(chunk)); // Write to temp file
                chunk.clear();
            }
        }
        if (!chunk.isEmpty()) {
            Collections.sort(chunk);
            chunkFiles.add(writeSortedChunk(chunk));
        }
    }

    // Phase 2: k-way merge of sorted chunks
    kWayMerge(chunkFiles, outputFile);
}
```

---

## Summary

### Algorithm Cheat Sheet

| Algorithm | Best | Average | Worst | Space | Stable | Best Use Case |
|---|---|---|---|---|---|---|
| Bubble Sort | O(n) | O(n²) | O(n²) | O(1) | Yes | Educational, nearly sorted |
| Selection Sort | O(n²) | O(n²) | O(n²) | O(1) | No | Min writes needed |
| Insertion Sort | O(n) | O(n²) | O(n²) | O(1) | Yes | Small/nearly sorted arrays |
| Shell Sort | O(n log n) | O(n log²n) | O(n²) | O(1) | No | Medium arrays |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No | Guaranteed perf + O(1) space |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes | Linked lists, stable sort, external |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) | No | General purpose, fastest avg |
| Tim Sort | O(n) | O(n log n) | O(n log n) | O(n) | Yes | Java/Python default, real data |
| Counting Sort | O(n+k) | O(n+k) | O(n+k) | O(k) | Yes | Small integer range |
| Radix Sort | O(nk) | O(nk) | O(nk) | O(n+k) | Yes | Fixed-width integers/strings |
| Bucket Sort | O(n+k) | O(n+k) | O(n²) | O(n) | Yes | Uniform distribution |

### The Decision Tree
```
What kind of data?
├── Integers with small range (0-1000)  → Counting Sort O(n+k)
├── Fixed-width integers (phone, zip)   → Radix Sort    O(nk)
├── Floating point 0-1, uniform         → Bucket Sort   O(n)
└── General / objects
    ├── Size < 32 elements              → Insertion Sort O(n²) (fast in practice)
    ├── Need stable sort                → Merge Sort or TimSort
    ├── Need O(1) space guaranteed      → Heap Sort
    ├── Need fastest average case       → Quick Sort (randomized)
    └── Unknown / Java default          → Arrays.sort() (TimSort/DualPivot)
```

> **The single most important insight:** Sorting is not the end goal — it's the prerequisite. Sort once, and every subsequent operation (search, merge, dedup, range query) becomes dramatically cheaper.
