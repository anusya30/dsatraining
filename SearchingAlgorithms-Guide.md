# Searching Algorithms — Complete Guide
> Covers: Why Searching Matters · Linear Search · Binary Search · Binary Search Variations · Time & Space Complexity · Real-World Systems · Interview-Level Problems

---

## Table of Contents
1. [Why Searching Matters](#1-why-searching-matters)
2. [Linear Search](#2-linear-search)
3. [Binary Search](#3-binary-search)
4. [Binary Search Variations](#4-binary-search-variations)
5. [Time & Space Complexity](#5-time--space-complexity)
6. [Real-World Systems](#6-real-world-systems)
7. [Interview-Level Problems](#7-interview-level-problems)

---

## 1. Why Searching Matters

### What Is Searching?
Searching is the process of **locating a specific element or condition** within a data collection. It is one of the most fundamental operations in computer science — virtually every software system searches for something.

### Why It Matters
| Without Optimized Search | With Optimized Search |
|---|---|
| Scan all 1 billion records to find one | Find it in ~30 comparisons (binary search) |
| O(n) every time | O(log n) or better |
| Unusable at scale | Powers Google, databases, filesystems |

### Core Principles Searching Teaches
- **Fundamental optimization** — don't do more work than necessary
- **Logarithmic performance** — halving the problem space on each step
- **Divide & conquer** — the core idea behind merge sort, quicksort, and trees
- **Base for advanced algorithms** — B-trees, tries, hash maps all build on search

### Real-World Analogy
Imagine finding a word in a dictionary:
- **Linear search** = reading every word from page 1
- **Binary search** = opening the middle, deciding left or right, repeating

### Where Searching Appears
- Database `WHERE` clause lookups
- File system inode lookups
- DNS resolution
- Auto-complete / type-ahead
- Git bisect (finding a breaking commit)
- Spell checkers
- Version control conflict detection

---

## 2. Linear Search

### Concept
Linear search (also called **sequential search**) scans every element in a collection **one by one** from the beginning until the target is found or the collection is exhausted.

### When to Use Linear Search
- The array/list is **unsorted**
- The collection is **small** (< 100 elements)
- You're searching a **linked list** (no random access)
- You need to find **all occurrences**, not just the first
- The data changes **frequently** (re-sorting would be expensive)

### Algorithm — Step by Step
```
Given: array = [5, 3, 8, 1, 9, 2], target = 8

Step 1: index 0 → 5 == 8? No
Step 2: index 1 → 3 == 8? No
Step 3: index 2 → 8 == 8? YES → return index 2
```

### Java Implementation

#### Basic Linear Search
```java
public static int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            return i; // Found — return index
        }
    }
    return -1; // Not found
}
```

#### Linear Search — Find All Occurrences
```java
public static List<Integer> linearSearchAll(int[] arr, int target) {
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            indices.add(i);
        }
    }
    return indices; // Returns all positions where target appears
}
```

#### Linear Search on Strings
```java
public static int linearSearchString(String[] arr, String target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i].equals(target)) {
            return i;
        }
    }
    return -1;
}
```

#### Linear Search with Sentinel (Optimization)
```java
// Sentinel eliminates the bounds check on every iteration
public static int sentinelLinearSearch(int[] arr, int target) {
    int n = arr.length;
    int last = arr[n - 1];       // Save last element
    arr[n - 1] = target;         // Place sentinel at end

    int i = 0;
    while (arr[i] != target) {   // No bounds check needed
        i++;
    }

    arr[n - 1] = last;           // Restore last element

    if (i < n - 1 || arr[n - 1] == target) {
        return i;
    }
    return -1;
}
```

### Complexity
| Case | Time | Explanation |
|---|---|---|
| Best | O(1) | Target is first element |
| Average | O(n/2) → O(n) | Target is in the middle on average |
| Worst | O(n) | Target is last or not present |
| Space | O(1) | No extra memory used |

### Real-World Examples
- **Contact list scroll** — finding a contact by name before search is indexed
- **Log file scan** — `grep` searching for an error pattern in a log file
- **Unsorted inventory** — scanning a warehouse list for a product ID
- **Small config lookup** — finding a key in a 10-item properties map

---

## 3. Binary Search

### Concept
Binary search works on a **sorted** array by repeatedly **halving the search space**. It compares the target to the middle element and eliminates half the remaining elements each time.

**Precondition:** The array MUST be sorted.

### Core Idea — Divide & Conquer
```
array = [1, 3, 5, 7, 9, 11, 13, 15, 17, 19], target = 7

Step 1: low=0, high=9, mid=4 → arr[4]=9 → 7 < 9 → search LEFT half
Step 2: low=0, high=3, mid=1 → arr[1]=3 → 7 > 3 → search RIGHT half
Step 3: low=2, high=3, mid=2 → arr[2]=5 → 7 > 5 → search RIGHT half
Step 4: low=3, high=3, mid=3 → arr[3]=7 → FOUND at index 3
```
Only 4 comparisons for 10 elements. At 1,000,000 elements, max 20 comparisons.

### Java Implementation

#### Iterative Binary Search (Preferred)
```java
public static int binarySearch(int[] arr, int target) {
    int low = 0;
    int high = arr.length - 1;

    while (low <= high) {
        // Use (low + high) / 2 carefully — can overflow for large indices
        int mid = low + (high - low) / 2; // Overflow-safe

        if (arr[mid] == target) {
            return mid;             // Found
        } else if (arr[mid] < target) {
            low = mid + 1;          // Target is in right half
        } else {
            high = mid - 1;         // Target is in left half
        }
    }
    return -1; // Not found
}
```

#### Recursive Binary Search
```java
public static int binarySearchRecursive(int[] arr, int target, int low, int high) {
    if (low > high) return -1; // Base case: not found

    int mid = low + (high - low) / 2;

    if (arr[mid] == target) {
        return mid;
    } else if (arr[mid] < target) {
        return binarySearchRecursive(arr, target, mid + 1, high); // Search right
    } else {
        return binarySearchRecursive(arr, target, low, mid - 1);  // Search left
    }
}
```

#### Why `low + (high - low) / 2` and NOT `(low + high) / 2`?
```java
// DANGER: If low = 1_500_000_000 and high = 1_600_000_000
int mid = (low + high) / 2;        // OVERFLOW — sum exceeds Integer.MAX_VALUE
int mid = low + (high - low) / 2;  // SAFE — subtraction stays within bounds
```

### Complexity
| Case | Time | Explanation |
|---|---|---|
| Best | O(1) | Target is the middle element |
| Average | O(log n) | Halving search space each step |
| Worst | O(log n) | Target not present or at boundary |
| Space (iterative) | O(1) | No extra memory |
| Space (recursive) | O(log n) | Call stack depth = log n |

### Why O(log n)?
```
n = 1,000,000,000 (1 billion elements)

After 1 step:  500,000,000 remain
After 2 steps: 250,000,000 remain
After 10 steps:      976,562 remain
After 20 steps:          954 remain
After 30 steps:            1 remains

log₂(1,000,000,000) ≈ 30 steps maximum
```

---

## 4. Binary Search Variations

### 4.1 Find First Occurrence (Left Boundary)
When duplicates exist, find the **leftmost** position of the target.

```java
public static int findFirst(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    int result = -1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) {
            result = mid;       // Record position
            high = mid - 1;     // Keep searching LEFT for earlier occurrence
        } else if (arr[mid] < target) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }
    return result;
}

// Example: arr = [1, 2, 2, 2, 3], target = 2 → returns index 1
```

### 4.2 Find Last Occurrence (Right Boundary)
Find the **rightmost** position of the target.

```java
public static int findLast(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    int result = -1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) {
            result = mid;       // Record position
            low = mid + 1;      // Keep searching RIGHT for later occurrence
        } else if (arr[mid] < target) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }
    return result;
}

// Example: arr = [1, 2, 2, 2, 3], target = 2 → returns index 3
```

### 4.3 Count Occurrences of a Target
```java
public static int countOccurrences(int[] arr, int target) {
    int first = findFirst(arr, target);
    if (first == -1) return 0;           // Target not in array
    int last = findLast(arr, target);
    return last - first + 1;
}

// Example: arr = [1, 2, 2, 2, 3], target = 2 → returns 3
```

### 4.4 Find Floor (Largest Element ≤ Target)
```java
public static int floor(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    int result = -1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] <= target) {
            result = arr[mid];  // Candidate for floor
            low = mid + 1;      // Try to find larger value still ≤ target
        } else {
            high = mid - 1;
        }
    }
    return result;
}

// Example: arr = [1, 3, 5, 7, 9], target = 6 → returns 5
```

### 4.5 Find Ceiling (Smallest Element ≥ Target)
```java
public static int ceiling(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    int result = -1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] >= target) {
            result = arr[mid];  // Candidate for ceiling
            high = mid - 1;     // Try to find smaller value still ≥ target
        } else {
            low = mid + 1;
        }
    }
    return result;
}

// Example: arr = [1, 3, 5, 7, 9], target = 6 → returns 7
```

### 4.6 Search in Rotated Sorted Array
A sorted array that has been rotated at an unknown pivot.
```
Original: [1, 2, 3, 4, 5, 6, 7]
Rotated:  [4, 5, 6, 7, 1, 2, 3]  ← pivot at index 3
```

```java
public static int searchRotated(int[] arr, int target) {
    int low = 0, high = arr.length - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;

        if (arr[mid] == target) return mid;

        // Left half is sorted
        if (arr[low] <= arr[mid]) {
            if (target >= arr[low] && target < arr[mid]) {
                high = mid - 1; // Target in left sorted half
            } else {
                low = mid + 1;  // Target in right half
            }
        }
        // Right half is sorted
        else {
            if (target > arr[mid] && target <= arr[high]) {
                low = mid + 1;  // Target in right sorted half
            } else {
                high = mid - 1; // Target in left half
            }
        }
    }
    return -1;
}

// Example: arr = [4,5,6,7,1,2,3], target = 1 → returns index 4
```

### 4.7 Find Peak Element
A peak element is greater than its neighbors.

```java
public static int findPeak(int[] arr) {
    int low = 0, high = arr.length - 1;

    while (low < high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] > arr[mid + 1]) {
            high = mid;         // Peak is in left half (including mid)
        } else {
            low = mid + 1;      // Peak is in right half
        }
    }
    return low; // low == high == peak index
}

// Example: arr = [1, 3, 20, 4, 1] → returns index 2 (value 20)
```

### 4.8 Binary Search on Answer (Search Space is a Value Range)
Find the **minimum capacity** to ship packages within D days.

```java
// Classic "binary search on answer" pattern
public static int shipWithinDays(int[] weights, int days) {
    int low = 0, high = 0;
    for (int w : weights) {
        low = Math.max(low, w);   // Min capacity = heaviest single package
        high += w;                // Max capacity = all packages at once
    }

    while (low < high) {
        int mid = low + (high - low) / 2;
        if (canShip(weights, days, mid)) {
            high = mid;           // Try smaller capacity
        } else {
            low = mid + 1;        // Need larger capacity
        }
    }
    return low;
}

private static boolean canShip(int[] weights, int days, int capacity) {
    int daysNeeded = 1, currentLoad = 0;
    for (int w : weights) {
        if (currentLoad + w > capacity) {
            daysNeeded++;
            currentLoad = 0;
        }
        currentLoad += w;
    }
    return daysNeeded <= days;
}
```

### 4.9 Square Root via Binary Search
```java
public static int mySqrt(int x) {
    if (x < 2) return x;
    int low = 1, high = x / 2;
    int result = 1;

    while (low <= high) {
        long mid = low + (high - low) / 2;
        if (mid * mid == x) return (int) mid;
        if (mid * mid < x) {
            result = (int) mid;   // Record valid floor sqrt
            low = (int) mid + 1;
        } else {
            high = (int) mid - 1;
        }
    }
    return result;
}

// Example: mySqrt(8) → 2  (floor of √8 = 2.82...)
```

### 4.10 Find Minimum in Rotated Sorted Array
```java
public static int findMin(int[] arr) {
    int low = 0, high = arr.length - 1;

    while (low < high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] > arr[high]) {
            low = mid + 1;  // Minimum is in right half
        } else {
            high = mid;     // Minimum is in left half (including mid)
        }
    }
    return arr[low];
}

// Example: arr = [4,5,6,7,0,1,2] → returns 0
```

---

## 5. Time & Space Complexity

### Comprehensive Complexity Table

| Algorithm | Best | Average | Worst | Space | Sorted Required? |
|---|---|---|---|---|---|
| Linear Search | O(1) | O(n) | O(n) | O(1) | No |
| Binary Search (iterative) | O(1) | O(log n) | O(log n) | O(1) | Yes |
| Binary Search (recursive) | O(1) | O(log n) | O(log n) | O(log n) | Yes |
| Find First/Last | O(1) | O(log n) | O(log n) | O(1) | Yes |
| Search Rotated Array | O(1) | O(log n) | O(log n) | O(1) | Partially |
| Binary Search on Answer | O(1) | O(log n × f(n)) | O(log n × f(n)) | O(1) | Value Range |

> f(n) = cost of the feasibility check function

### Growth Rate Comparison

```
n           Linear O(n)    Binary O(log n)
---------   -----------    ---------------
10          10             4
100         100            7
1,000       1,000          10
10,000      10,000         14
1,000,000   1,000,000      20
1,000,000,000  1,000,000,000  30
```

### Visualizing Binary Search Depth
```
n = 16 elements:

Level 0:  [................................]  16 elements
Level 1:  [................]                   8 elements
Level 2:  [........]                           4 elements
Level 3:  [....]                               2 elements
Level 4:  [..]                                 1 element  ← found or not found

Max comparisons = log₂(16) = 4
```

### Space Complexity Deep Dive

#### Iterative Binary Search — O(1) Space
```java
// Only 3 variables: low, high, mid
// No matter how large n is, always 3 integers
int low = 0, high = n - 1, mid;
```

#### Recursive Binary Search — O(log n) Space
```java
// Each recursive call adds a stack frame
// Stack depth = log n frames
binarySearch(arr, target, 0, n-1)         // Frame 1
  binarySearch(arr, target, mid+1, n-1)   // Frame 2
    binarySearch(arr, target, mid+1, n-1) // Frame 3
      ...                                  // log n frames total
```

### When to Choose Linear vs Binary

```
Is the data sorted?
├── NO  → Linear Search (O(n)) — no choice
└── YES → Is data size large (n > 100)?
          ├── YES → Binary Search (O(log n)) — always prefer
          └── NO  → Either works; linear is simpler to implement
```

### Amortized Thinking — Sort Then Search
```
Scenario: 1,000 searches on n = 1,000,000 elements

Option A — Linear each time:
  1,000 × O(n) = 1,000 × 1,000,000 = 1,000,000,000 operations

Option B — Sort once, then Binary Search:
  Sort: O(n log n) = ~20,000,000 operations
  1,000 × O(log n) = 1,000 × 20 = 20,000 operations
  Total: ~20,020,000 operations

Option B is ~50× faster
```

---

## 6. Real-World Systems

### 6.1 Database Index Lookups (B-Tree)
Databases use **B-Trees** — a generalization of binary search trees — for index lookups.

```
SQL: SELECT * FROM users WHERE id = 500000;

Without index (Linear):  Scan all 1M rows → O(n)
With B-Tree index:       3-4 node traversals → O(log n)

PostgreSQL, MySQL, Oracle all use B-Tree by default for primary keys.
```

```java
// Simulated DB index lookup using binary search
public static User findUserById(User[] sortedUsers, int targetId) {
    int low = 0, high = sortedUsers.length - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (sortedUsers[mid].getId() == targetId) return sortedUsers[mid];
        if (sortedUsers[mid].getId() < targetId) low = mid + 1;
        else high = mid - 1;
    }
    return null;
}
```

### 6.2 Git Bisect — Finding a Breaking Commit
`git bisect` uses binary search to find which commit introduced a bug.

```
500 commits, bug introduced somewhere:

git bisect start
git bisect bad HEAD         # Current commit is bad
git bisect good v1.0        # v1.0 was good

Git now checks commit #250 → good? → checks #375 → bad? → checks #312...
Finds bug commit in log₂(500) ≈ 9 steps instead of 500
```

```java
// Simulated git bisect
public static int gitBisect(boolean[] commits) {
    // commits[i] = true means "good", false means "bad (bug present)"
    int low = 0, high = commits.length - 1;
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (!commits[mid]) {
            high = mid;       // Bug exists at mid, search left for first occurrence
        } else {
            low = mid + 1;    // mid is good, bug must be to the right
        }
    }
    return low; // First bad commit
}
```

### 6.3 Auto-Complete / Type-Ahead (Prefix Search)
```java
// Find all words with a given prefix using binary search
public static List<String> autoComplete(String[] sortedDictionary, String prefix) {
    List<String> results = new ArrayList<>();
    int start = lowerBound(sortedDictionary, prefix);

    for (int i = start; i < sortedDictionary.length; i++) {
        if (sortedDictionary[i].startsWith(prefix)) {
            results.add(sortedDictionary[i]);
        } else {
            break; // Words are sorted; once prefix doesn't match, we're done
        }
    }
    return results;
}

private static int lowerBound(String[] arr, String prefix) {
    int low = 0, high = arr.length;
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (arr[mid].compareTo(prefix) < 0) low = mid + 1;
        else high = mid;
    }
    return low;
}
```

### 6.4 DNS Resolution (Hierarchical Binary Search)
```
Lookup: www.jpmorgan.com

Root DNS:     Knows .com servers             → directs query
.com DNS:     Binary searches jpmorgan.com   → directs query
jpmorgan DNS: Binary searches www record     → returns IP

Each level is a sorted zone file searched with binary search.
```

### 6.5 Load Balancer — Consistent Hashing Ring
```java
// Find the correct server node for a request (sorted ring of virtual nodes)
public static String findServer(int[] ring, String[] servers, int requestHash) {
    int low = 0, high = ring.length - 1;

    // Find ceiling — first node >= requestHash
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (ring[mid] < requestHash) {
            low = mid + 1;
        } else {
            high = mid;
        }
    }
    // Wrap around the ring
    int nodeIndex = (low == ring.length) ? 0 : low;
    return servers[nodeIndex];
}
```

### 6.6 Spell Checker
```java
// Dictionary stored as sorted array — binary search for word lookup
public static boolean isSpelledCorrectly(String[] dictionary, String word) {
    return binarySearch(dictionary, word) != -1;
}

public static int binarySearch(String[] arr, String target) {
    int low = 0, high = arr.length - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        int cmp = arr[mid].compareTo(target);
        if (cmp == 0) return mid;
        if (cmp < 0) low = mid + 1;
        else high = mid - 1;
    }
    return -1;
}
```

### 6.7 Financial Systems — Price Feed Binary Search
```java
// Find the best ask price >= your bid in an order book
public static double findBestAsk(double[] askPrices, double bidPrice) {
    // askPrices is sorted ascending
    int low = 0, high = askPrices.length - 1;
    int result = -1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (askPrices[mid] >= bidPrice) {
            result = mid;
            high = mid - 1; // Find lowest valid ask
        } else {
            low = mid + 1;
        }
    }
    return result == -1 ? -1 : askPrices[result];
}
```

---

## 7. Interview-Level Problems

### Problem 1 — Search Insert Position (LeetCode 35)
Find the index where target exists, or where it would be inserted to keep order sorted.

```java
public static int searchInsert(int[] nums, int target) {
    int low = 0, high = nums.length - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) low = mid + 1;
        else high = mid - 1;
    }
    return low; // low is the insertion point when not found
}
// [1,3,5,6], target=5 → 2
// [1,3,5,6], target=2 → 1
// [1,3,5,6], target=7 → 4
```

**Why does `low` equal the insert position?**
When the loop ends, `low > high`. `low` points to the first element greater than target — exactly where target should be inserted.

---

### Problem 2 — Find Minimum in Rotated Sorted Array (LeetCode 153)
```java
public static int findMinRotated(int[] nums) {
    int low = 0, high = nums.length - 1;

    while (low < high) {
        int mid = low + (high - low) / 2;
        if (nums[mid] > nums[high]) {
            low = mid + 1;  // Min is in the right half
        } else {
            high = mid;     // Min is in the left half (including mid)
        }
    }
    return nums[low];
}
// [3,4,5,1,2] → 1
// [4,5,6,7,0,1,2] → 0
```

---

### Problem 3 — Search in Rotated Sorted Array (LeetCode 33)
```java
public static int searchInRotated(int[] nums, int target) {
    int low = 0, high = nums.length - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (nums[mid] == target) return mid;

        if (nums[low] <= nums[mid]) {           // Left half is sorted
            if (target >= nums[low] && target < nums[mid]) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        } else {                                 // Right half is sorted
            if (target > nums[mid] && target <= nums[high]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
    }
    return -1;
}
// [4,5,6,7,0,1,2], target=0 → 4
// [4,5,6,7,0,1,2], target=3 → -1
```

---

### Problem 4 — Find Peak Element (LeetCode 162)
```java
public static int findPeakElement(int[] nums) {
    int low = 0, high = nums.length - 1;

    while (low < high) {
        int mid = low + (high - low) / 2;
        if (nums[mid] > nums[mid + 1]) {
            high = mid;       // Peak is to the left (including mid)
        } else {
            low = mid + 1;    // Peak is to the right
        }
    }
    return low;
}
// [1,2,3,1] → index 2 (value 3)
// [1,2,1,3,5,6,4] → index 1 or 5 (either valid)
```

---

### Problem 5 — Median of Two Sorted Arrays (LeetCode 4) — Hard
```java
public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
    // Always binary search on the smaller array
    if (nums1.length > nums2.length) return findMedianSortedArrays(nums2, nums1);

    int m = nums1.length, n = nums2.length;
    int low = 0, high = m;

    while (low <= high) {
        int partitionX = (low + high) / 2;
        int partitionY = (m + n + 1) / 2 - partitionX;

        int maxLeftX  = (partitionX == 0) ? Integer.MIN_VALUE : nums1[partitionX - 1];
        int minRightX = (partitionX == m) ? Integer.MAX_VALUE : nums1[partitionX];
        int maxLeftY  = (partitionY == 0) ? Integer.MIN_VALUE : nums2[partitionY - 1];
        int minRightY = (partitionY == n) ? Integer.MAX_VALUE : nums2[partitionY];

        if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
            // Correct partition found
            if ((m + n) % 2 == 0) {
                return (Math.max(maxLeftX, maxLeftY) + Math.min(minRightX, minRightY)) / 2.0;
            } else {
                return Math.max(maxLeftX, maxLeftY);
            }
        } else if (maxLeftX > minRightY) {
            high = partitionX - 1;
        } else {
            low = partitionX + 1;
        }
    }
    throw new IllegalArgumentException("Input arrays are not sorted");
}
// [1,3], [2] → 2.0
// [1,2], [3,4] → 2.5
```

---

### Problem 6 — Koko Eating Bananas (LeetCode 875) — Binary Search on Answer
```java
public static int minEatingSpeed(int[] piles, int h) {
    int low = 1;
    int high = 0;
    for (int pile : piles) high = Math.max(high, pile);

    while (low < high) {
        int mid = low + (high - low) / 2;
        if (canFinish(piles, h, mid)) {
            high = mid;         // Try eating slower
        } else {
            low = mid + 1;      // Need to eat faster
        }
    }
    return low;
}

private static boolean canFinish(int[] piles, int h, int speed) {
    int hours = 0;
    for (int pile : piles) {
        hours += (pile + speed - 1) / speed; // Ceiling division
    }
    return hours <= h;
}
// piles=[3,6,7,11], h=8 → 4
```

---

### Problem 7 — First Bad Version (LeetCode 278)
```java
// API provided: boolean isBadVersion(int version)
public static int firstBadVersion(int n) {
    int low = 1, high = n;

    while (low < high) {
        int mid = low + (high - low) / 2;
        if (isBadVersion(mid)) {
            high = mid;         // mid could be first bad version
        } else {
            low = mid + 1;      // mid is good, look right
        }
    }
    return low;
}
// n=5, bad from version 4 → returns 4
```

---

### Problem 8 — Count Negative Numbers in Sorted Matrix (LeetCode 1351)
```java
public static int countNegatives(int[][] grid) {
    int count = 0;
    int cols = grid[0].length;

    for (int[] row : grid) {
        // Binary search for first negative in each sorted row
        int low = 0, high = cols;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (row[mid] < 0) high = mid;
            else low = mid + 1;
        }
        count += cols - low; // All elements from 'low' to end are negative
    }
    return count;
}
// [[4,3,2,-1],[3,2,1,-1],[1,1,-1,-2],[-1,-1,-2,-3]] → 8
```

---

### Interview Tips & Common Patterns

#### The 5 Binary Search Templates

```
Template 1 — Basic (find exact match):
  while (low <= high) { ... }

Template 2 — Find left boundary:
  while (low < high) { high = mid; or low = mid + 1; }

Template 3 — Find right boundary:
  while (low < high) { low = mid; or high = mid - 1; }
  (requires mid = low + (high - low + 1) / 2 to avoid infinite loop)

Template 4 — Binary search on answer:
  Define search space as value range
  Check feasibility with a helper function

Template 5 — Two-pointer binary search (matrix search):
  Start at top-right or bottom-left corner
```

#### Common Mistakes in Interviews
```
❌ Using (low + high) / 2    → Integer overflow
✅ Use low + (high - low) / 2

❌ while (low < high) with return mid   → May miss target
✅ while (low <= high) for exact search

❌ Forgetting to check arr[mid] bounds in rotated array
✅ Always determine which half is sorted first

❌ Off-by-one: high = mid - 1 vs high = mid
✅ Choose based on whether mid is already eliminated
```

#### Quick Recognition — When Is It Binary Search?
```
Is the problem asking for:
  ✓ A value in a sorted array?
  ✓ First/last/count of a condition in sorted data?
  ✓ Minimum/maximum value satisfying a condition?
  ✓ A problem with monotonic YES/NO condition on a range?

→ If YES to any: think Binary Search
```

---

## Summary

| Topic | Key Takeaway |
|---|---|
| Why searching matters | Foundation of every system at scale |
| Linear search | Simple, unsorted, O(n) — use for small or unsorted data |
| Binary search | Sorted only, O(log n) — always prefer at scale |
| Variations | First/last, floor/ceiling, rotated, peak, answer search |
| Complexity | O(log n) turns billions of ops into ~30 comparisons |
| Real-world | DB indexes, git bisect, DNS, load balancers, autocomplete |
| Interviews | Master 5 templates, avoid overflow, recognize monotonic conditions |

> **The single most important insight:** Binary search isn't just an algorithm — it's a *way of thinking*. Whenever you can define a sorted or monotonic search space and eliminate half of it per step, you have binary search.
