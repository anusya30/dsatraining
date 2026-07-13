# Advanced Problem Solving Patterns — Complete Guide
> Sliding Window • Two Pointers • Bit Manipulation • Segment Trees
> Covers: Sliding Window · Two Pointer · Bit Manipulation · Segment Trees · Real-World Applications · Complexity Deep Dive

---

## Table of Contents
1. [Sliding Window Pattern](#1-sliding-window-pattern)
2. [Two Pointer Pattern](#2-two-pointer-pattern)
3. [Bit Manipulation Fundamentals](#3-bit-manipulation-fundamentals)
4. [Segment Trees](#4-segment-trees)
5. [Real-World Applications](#5-real-world-applications)
6. [Complexity Deep Dive](#6-complexity-deep-dive)

---

## 1. Sliding Window Pattern

### What Is the Sliding Window?
The sliding window technique maintains a **contiguous subarray or substring** of variable or fixed size that "slides" through the data. Instead of recomputing the result from scratch for each window, we **add the new element entering the window and remove the element leaving it** — converting O(n²) brute force to O(n).

```
Array:  [2, 1, 5, 1, 3, 2],  window size k=3

Brute force:    sum[0..2]=8, sum[1..3]=7, sum[2..4]=9, sum[3..5]=6  → O(n×k)
Sliding window: sum[0..2]=8
                sum[1..3]=8-2+1=7  ← subtract left, add right
                sum[2..4]=7-1+3=9
                sum[3..5]=9-5+2=6  → O(n)

Key insight: adjacent windows share k-1 elements.
Recomputing from scratch wastes that overlap.
```

### Window Types

```
Fixed Window:    size k stays constant (e.g., "max sum of k consecutive")
Variable Window: expand/shrink based on condition (e.g., "longest substring with k distinct")
```

---

### 1.1 Fixed Window — Maximum Sum Subarray of Size K

```java
public static int maxSumFixed(int[] arr, int k) {
    int n = arr.length;
    if (n < k) return -1;

    // Build first window
    int windowSum = 0;
    for (int i = 0; i < k; i++) windowSum += arr[i];

    int maxSum = windowSum;

    // Slide window: add arr[i], remove arr[i-k]
    for (int i = k; i < n; i++) {
        windowSum += arr[i] - arr[i - k];      // O(1) update
        maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
}
// [2,1,5,1,3,2], k=3 → 9 (subarray [5,1,3])
// [2,3,4,1,5],   k=2 → 7 (subarray [3,4])
```

### 1.2 Fixed Window — First Negative Number in Every Window of Size K

```java
public static List<Integer> firstNegativeInWindow(int[] arr, int k) {
    Deque<Integer> deque = new ArrayDeque<>();   // Stores indices of negative numbers
    List<Integer> result = new ArrayList<>();

    // Build first window
    for (int i = 0; i < k; i++)
        if (arr[i] < 0) deque.offer(i);

    result.add(deque.isEmpty() ? 0 : arr[deque.peek()]);

    // Slide window
    for (int i = k; i < arr.length; i++) {
        // Remove elements out of window
        if (!deque.isEmpty() && deque.peek() <= i - k)
            deque.poll();

        // Add new element if negative
        if (arr[i] < 0) deque.offer(i);

        result.add(deque.isEmpty() ? 0 : arr[deque.peek()]);
    }
    return result;
}
// [-8,2,-3,4,-10], k=2 → [-8,-3,-3,-10]
```

### 1.3 Variable Window — Longest Substring Without Repeating Characters

```java
public static int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastSeen = new HashMap<>();
    int maxLen = 0, left = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);

        // If character seen within current window, shrink from left
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
            left = lastSeen.get(c) + 1;    // Jump left past the duplicate
        }

        lastSeen.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
// "abcabcbb" → 3 ("abc")
// "bbbbb"    → 1 ("b")
// "pwwkew"   → 3 ("wke")

// Trace for "abcabcbb":
// right=0(a): window=[a]          len=1
// right=1(b): window=[ab]         len=2
// right=2(c): window=[abc]        len=3
// right=3(a): a seen at 0, left→1 window=[bca]  len=3
// right=4(b): b seen at 1, left→2 window=[cab]  len=3
// right=5(c): c seen at 2, left→3 window=[abc]  len=3
// right=6(b): b seen at 4, left→5 window=[cb]   len=2
// right=7(b): b seen at 6, left→7 window=[b]    len=1
// Max = 3
```

### 1.4 Variable Window — Longest Substring with K Distinct Characters

```java
public static int longestKDistinct(String s, int k) {
    Map<Character, Integer> freq = new HashMap<>();
    int maxLen = 0, left = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        freq.merge(c, 1, Integer::sum);     // Add right character

        // Shrink window until at most k distinct characters
        while (freq.size() > k) {
            char leftChar = s.charAt(left);
            freq.merge(leftChar, -1, Integer::sum);
            if (freq.get(leftChar) == 0) freq.remove(leftChar);
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
// "araaci", k=2 → 4 ("araa")
// "araaci", k=1 → 2 ("aa")
// "cbbebi", k=3 → 5 ("cbbeb")
```

### 1.5 Variable Window — Minimum Window Substring

```java
// Find smallest window in s containing all characters of t
public static String minWindowSubstring(String s, String t) {
    Map<Character, Integer> need = new HashMap<>();
    for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);

    int left = 0, matched = 0, minLen = Integer.MAX_VALUE, minStart = 0;
    Map<Character, Integer> window = new HashMap<>();

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        window.merge(c, 1, Integer::sum);

        // Check if this character's count satisfies the need
        if (need.containsKey(c) && window.get(c).equals(need.get(c))) {
            matched++;
        }

        // Shrink window from left while all characters are matched
        while (matched == need.size()) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1;
                minStart = left;
            }
            char leftChar = s.charAt(left);
            window.merge(leftChar, -1, Integer::sum);
            if (need.containsKey(leftChar) && window.get(leftChar) < need.get(leftChar)) {
                matched--;
            }
            left++;
        }
    }
    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
// s="ADOBECODEBANC", t="ABC" → "BANC"
// s="a", t="a" → "a"
// s="a", t="aa" → ""
```

### 1.6 Variable Window — Maximum Consecutive Ones III (Flip at Most K Zeros)

```java
public static int longestOnes(int[] nums, int k) {
    int left = 0, zerosUsed = 0, maxLen = 0;

    for (int right = 0; right < nums.length; right++) {
        if (nums[right] == 0) zerosUsed++;       // Flip a zero

        // Too many zeros flipped — shrink window
        while (zerosUsed > k) {
            if (nums[left] == 0) zerosUsed--;
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
// [1,1,1,0,0,0,1,1,1,1,0], k=2 → 6 ([1,1,1,0,0 → flip 2 zeros])
// [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1,0], k=3 → 10
```

### 1.7 Sliding Window Maximum (Monotonic Deque)

```java
// Maximum element in each window of size k — O(n) using deque
public static int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>();   // Stores indices, decreasing values
    int[] result = new int[nums.length - k + 1];

    for (int i = 0; i < nums.length; i++) {
        // Remove indices out of window
        while (!deque.isEmpty() && deque.peek() < i - k + 1)
            deque.poll();

        // Remove smaller elements — they'll never be the max
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i])
            deque.pollLast();

        deque.offer(i);

        // Window complete: record maximum (front of deque)
        if (i >= k - 1)
            result[i - k + 1] = nums[deque.peek()];
    }
    return result;
}
// [1,3,-1,-3,5,3,6,7], k=3 → [3,3,5,5,6,7]
```

### 1.8 Count Subarrays with Product Less Than K

```java
public static int numSubarrayProductLessThanK(int[] nums, int k) {
    if (k <= 1) return 0;
    int left = 0, product = 1, count = 0;

    for (int right = 0; right < nums.length; right++) {
        product *= nums[right];

        while (product >= k) {
            product /= nums[left];
            left++;
        }
        // All subarrays ending at right with left..right start are valid
        count += right - left + 1;
    }
    return count;
}
// [10,5,2,6], k=100 → 8
// Subarrays: [10],[5],[2],[6],[5,2],[2,6],[5,2,6]... count those with product < 100
```

### Sliding Window Decision Guide
```
Is the problem about a contiguous subarray/substring?  → Sliding Window
Is the window size fixed?                              → Fixed window
Does the window size depend on a condition?            → Variable window
Need max/min within each window efficiently?           → Monotonic deque
Counting subarrays satisfying condition?               → At-most(k) - at-most(k-1)
```

---

## 2. Two Pointer Pattern

### What Are Two Pointers?
Two pointers use **two indices** that traverse the data structure — either from both ends toward the middle, or both from the left at different speeds. Eliminates the need for nested loops.

```
Opposite direction:   left=0, right=n-1  → move toward center
Same direction:       slow=0, fast=0     → fast outpaces slow
Fixed gap:            p1=0, p2=k         → maintain distance k
```

---

### 2.1 Two Sum in Sorted Array

```java
public static int[] twoSumSorted(int[] arr, int target) {
    int left = 0, right = arr.length - 1;

    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) return new int[]{left, right};
        else if (sum < target) left++;      // Need larger sum → move left right
        else right--;                        // Need smaller sum → move right left
    }
    return new int[]{-1, -1};
}
// [1,2,3,4,6], target=6 → [1,3] (2+4)
// [2,7,11,15], target=9 → [0,1] (2+7)

// Why works: arr is sorted.
// If arr[left]+arr[right] < target: arr[left] is too small, increment left
// If arr[left]+arr[right] > target: arr[right] is too big, decrement right
// Provably exhaustive: we check all promising pairs
```

### 2.2 Three Sum (All Unique Triplets Summing to Zero)

```java
public static List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i-1]) continue;   // Skip duplicates

        int left = i + 1, right = nums.length - 1;

        while (left < right) {
            int sum = nums[i] + nums[left] + nums[right];
            if (sum == 0) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                // Skip duplicates on both sides
                while (left < right && nums[left] == nums[left+1]) left++;
                while (left < right && nums[right] == nums[right-1]) right--;
                left++; right--;
            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }
    return result;
}
// [-1,0,1,2,-1,-4] → [[-1,-1,2],[-1,0,1]]
// [0,0,0,0]        → [[0,0,0]]
```

### 2.3 Container With Most Water

```java
public static int maxWater(int[] height) {
    int left = 0, right = height.length - 1, maxArea = 0;

    while (left < right) {
        int area = Math.min(height[left], height[right]) * (right - left);
        maxArea = Math.max(maxArea, area);

        // Move the shorter wall inward (moving taller wall can only decrease area)
        if (height[left] < height[right]) left++;
        else right--;
    }
    return maxArea;
}
// [1,8,6,2,5,4,8,3,7] → 49 (between index 1 and 8: min(8,7)*7=49)
// Greedy insight: moving the taller wall can only decrease width without
// guaranteeing a taller wall → always move the shorter wall
```

### 2.4 Trapping Rain Water

```java
public static int trapRainWater(int[] height) {
    int left = 0, right = height.length - 1;
    int leftMax = 0, rightMax = 0, water = 0;

    while (left < right) {
        if (height[left] <= height[right]) {
            if (height[left] >= leftMax) leftMax = height[left];
            else water += leftMax - height[left];   // Trapped between leftMax and current
            left++;
        } else {
            if (height[right] >= rightMax) rightMax = height[right];
            else water += rightMax - height[right];
            right--;
        }
    }
    return water;
}
// [0,1,0,2,1,0,1,3,2,1,2,1] → 6
// [4,2,0,3,2,5]              → 9
```

### 2.5 Sort Colors (Dutch National Flag)

```java
// Sort array with values {0,1,2} in-place — one pass
public static void sortColors(int[] nums) {
    int low = 0, mid = 0, high = nums.length - 1;

    while (mid <= high) {
        if (nums[mid] == 0) {
            swap(nums, low, mid);
            low++; mid++;           // 0 goes to front
        } else if (nums[mid] == 1) {
            mid++;                  // 1 already in place
        } else {
            swap(nums, mid, high);
            high--;                 // 2 goes to back (don't increment mid)
        }
    }
}
// [2,0,2,1,1,0] → [0,0,1,1,2,2]
// Invariant: arr[0..low-1]=0, arr[low..mid-1]=1, arr[high+1..n-1]=2
```

### 2.6 Slow & Fast Pointer — Linked List Cycle Detection

```java
public static boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;           // Moves 1 step
        fast = fast.next.next;      // Moves 2 steps
        if (slow == fast) return true;  // They meet → cycle exists
    }
    return false;   // fast reached null → no cycle
}

// Find cycle entry point (Floyd's algorithm)
public static ListNode detectCycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {          // Cycle detected
            slow = head;             // Reset slow to head
            while (slow != fast) {   // Both move 1 step
                slow = slow.next;
                fast = fast.next;
            }
            return slow;             // Meeting point = cycle start
        }
    }
    return null;
}
// Proof: if cycle length L, head-to-entry distance h,
//        entry-to-meeting distance k, then h ≡ L-k (mod L)
//        → resetting slow to head and moving both 1 step → meet at entry
```

### 2.7 Find Duplicate Number (Floyd's Cycle)

```java
// Array has n+1 integers in range [1,n], exactly one duplicate
// Treat array as linked list: index i → node, arr[i] → next pointer
public static int findDuplicate(int[] nums) {
    int slow = nums[0], fast = nums[0];

    // Phase 1: detect cycle
    do {
        slow = nums[slow];
        fast = nums[nums[fast]];
    } while (slow != fast);

    // Phase 2: find entry of cycle = duplicate number
    slow = nums[0];
    while (slow != fast) {
        slow = nums[slow];
        fast = nums[fast];
    }
    return slow;
}
// [1,3,4,2,2] → 2
// [3,1,3,4,2] → 3
```

### 2.8 Remove Duplicates from Sorted Array

```java
public static int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    int slow = 0;                   // Slow: boundary of unique elements

    for (int fast = 1; fast < nums.length; fast++) {
        if (nums[fast] != nums[slow]) {  // New unique element found
            slow++;
            nums[slow] = nums[fast];
        }
        // If duplicate, fast just moves forward
    }
    return slow + 1;               // Length of unique portion
}
// [0,0,1,1,1,2,2,3,3,4] → 5, nums=[0,1,2,3,4,...]
```

### Two Pointer Decision Guide
```
Array/string sorted, looking for pair/triplet?     → Opposite direction pointers
Linked list cycle detection?                        → Slow/fast pointers
Remove duplicates in-place?                         → Slow/fast in same direction
Partition by condition?                             → Low/mid/high (Dutch flag)
Merging two sorted arrays?                          → Two pointers from start
```

---

## 3. Bit Manipulation Fundamentals

### Why Bit Manipulation?
- **Speed:** bitwise operations execute in a single CPU cycle
- **Space:** pack multiple booleans into one integer (32 flags in an int)
- **Elegance:** many problems have clean O(1) or O(log n) bit solutions
- **Essential for:** cryptography, compression, graphics, system programming

### Bitwise Operators Reference
```
Operation    Symbol  Example (a=5=101, b=3=011)
─────────────────────────────────────────────────
AND          &       101 & 011 = 001 = 1  (1 if BOTH bits are 1)
OR           |       101 | 011 = 111 = 7  (1 if EITHER bit is 1)
XOR          ^       101 ^ 011 = 110 = 6  (1 if bits are DIFFERENT)
NOT          ~       ~101 = ...11111010   (flip all bits)
Left shift   <<      101 << 1 = 1010 = 10 (multiply by 2)
Right shift  >>      101 >> 1 = 010 = 2   (divide by 2, signed)
Unsigned RS  >>>     fills with 0s on left regardless of sign
```

### 3.1 Essential Bit Tricks

```java
// Check if bit i is set
boolean isBitSet(int n, int i) {
    return (n & (1 << i)) != 0;
}

// Set bit i
int setBit(int n, int i) {
    return n | (1 << i);
}

// Clear bit i
int clearBit(int n, int i) {
    return n & ~(1 << i);
}

// Toggle bit i
int toggleBit(int n, int i) {
    return n ^ (1 << i);
}

// Check if n is even/odd (faster than n%2)
boolean isEven(int n) { return (n & 1) == 0; }
boolean isOdd(int n)  { return (n & 1) == 1; }

// Check if n is a power of 2
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
    // Power of 2 has exactly one bit set: 1000
    // n-1 flips trailing bits: 0111
    // AND = 0
}

// Get lowest set bit (rightmost 1)
int lowestSetBit(int n) { return n & (-n); }
// -n = ~n + 1 (two's complement), isolates the lowest set bit

// Clear lowest set bit
int clearLowestBit(int n) { return n & (n - 1); }

// Count set bits (Brian Kernighan's algorithm)
int countBits(int n) {
    int count = 0;
    while (n != 0) {
        n = n & (n - 1);   // Clears lowest set bit
        count++;
    }
    return count;
}

// Swap two numbers without temp variable
void swapBits(int[] arr, int i, int j) {
    arr[i] ^= arr[j];
    arr[j] ^= arr[i];
    arr[i] ^= arr[j];
    // arr[i]=A^B, arr[j]=B^(A^B)=A, arr[i]=(A^B)^A=B ✓
}
```

### 3.2 XOR Magic

```java
// XOR properties:
// a ^ a = 0    (self-cancellation)
// a ^ 0 = a    (identity)
// a ^ b = b ^ a (commutative)
// (a^b)^c = a^(b^c) (associative)

// Find single number in array where every other appears twice
public static int singleNumber(int[] nums) {
    int xor = 0;
    for (int n : nums) xor ^= n;   // All pairs cancel out → only single remains
    return xor;
}
// [4,1,2,1,2] → 4 (1^1=0, 2^2=0, 0^4=4)
// [2,2,1]     → 1

// Find two non-repeating elements in array (all others appear twice)
public static int[] twoSingleNumbers(int[] nums) {
    int xorAll = 0;
    for (int n : nums) xorAll ^= n;       // xorAll = a ^ b (the two singles)

    // Find a differing bit (rightmost set bit of xorAll)
    int diffBit = xorAll & (-xorAll);

    int a = 0, b = 0;
    for (int n : nums) {
        if ((n & diffBit) != 0) a ^= n;   // Group 1: diffBit is set
        else b ^= n;                         // Group 2: diffBit is not set
    }
    return new int[]{a, b};
}
// [1,2,3,2,1,4] → [3,4]

// Find missing number in [0..n]
public static int missingNumber(int[] nums) {
    int n = nums.length;
    int expected = 0;
    for (int i = 0; i <= n; i++) expected ^= i;   // XOR all expected
    for (int num : nums) expected ^= num;           // Cancel existing numbers
    return expected;
    // Alternatively: return n*(n+1)/2 - Arrays.stream(nums).sum()
}
// [3,0,1] → 2
// [9,6,4,2,3,5,7,0,1] → 8
```

### 3.3 Bit Masks and Subsets

```java
// Enumerate all subsets of set {0,1,...,n-1} using bitmask
public static List<List<Integer>> allSubsets(int n) {
    List<List<Integer>> result = new ArrayList<>();
    for (int mask = 0; mask < (1 << n); mask++) {    // 2^n masks
        List<Integer> subset = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) != 0) {             // Bit i is set
                subset.add(i);
            }
        }
        result.add(subset);
    }
    return result;
}

// Maximum AND subarray (XOR subset DP)
public static int maximumXOR(int[] nums) {
    int max = 0, mask = 0;
    for (int i = 31; i >= 0; i--) {
        mask |= (1 << i);
        Set<Integer> prefixes = new HashSet<>();
        for (int n : nums) prefixes.add(n & mask);

        int candidate = max | (1 << i);
        for (int prefix : prefixes) {
            if (prefixes.contains(candidate ^ prefix)) {
                max = candidate;
                break;
            }
        }
    }
    return max;
}
// [3,10,5,25,2,8] → 28 (5^25=28)
```

### 3.4 Number of 1 Bits / Hamming Weight

```java
public static int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        count += n & 1;    // Add LSB
        n >>>= 1;          // Unsigned right shift (avoids sign extension)
    }
    return count;
}
// Or: return Integer.bitCount(n);

// Count bits for all numbers 0 to n — O(n)
public static int[] countBitsDP(int n) {
    int[] dp = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        dp[i] = dp[i >> 1] + (i & 1);   // dp[i] = dp[i/2] + (is i odd?)
        // i >> 1 = i with last bit removed
        // dp[i>>1] = bits in i without its last bit
        // (i&1) = the last bit itself
    }
    return dp;
}
// countBitsDP(5) → [0,1,1,2,1,2] for [0,1,2,3,4,5]
```

### 3.5 Reverse Bits

```java
public static int reverseBits(int n) {
    int result = 0;
    for (int i = 0; i < 32; i++) {
        result <<= 1;           // Make room for next bit
        result |= (n & 1);      // Take LSB of n
        n >>= 1;                // Shift n right
    }
    return result;
}
// 00000010100101000001111010011100 → 00111001011110000010100101000000

// Efficient with divide & conquer (cache-friendly for multiple calls)
public static int reverseBitsDC(int n) {
    n = ((n & 0xffff0000) >>> 16) | ((n & 0x0000ffff) << 16);  // Swap halves
    n = ((n & 0xff00ff00) >>>  8) | ((n & 0x00ff00ff) <<  8);  // Swap bytes
    n = ((n & 0xf0f0f0f0) >>>  4) | ((n & 0x0f0f0f0f) <<  4);  // Swap nibbles
    n = ((n & 0xcccccccc) >>>  2) | ((n & 0x33333333) <<  2);  // Swap pairs
    n = ((n & 0xaaaaaaaa) >>>  1) | ((n & 0x55555555) <<  1);  // Swap bits
    return n;
}
```

### 3.6 Bitmask DP — Traveling Salesman

```java
// dp[mask][i] = min cost to visit exactly the cities in mask, ending at city i
public static int tspBitmask(int[][] dist, int n) {
    int[][] dp = new int[1 << n][n];
    for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE / 2);
    dp[1][0] = 0;    // Start at city 0, only city 0 visited (mask=1)

    for (int mask = 1; mask < (1 << n); mask++) {
        for (int u = 0; u < n; u++) {
            if ((mask & (1 << u)) == 0) continue;
            for (int v = 0; v < n; v++) {
                if ((mask & (1 << v)) != 0) continue;
                int newMask = mask | (1 << v);
                dp[newMask][v] = Math.min(dp[newMask][v], dp[mask][u] + dist[u][v]);
            }
        }
    }
    int full = (1 << n) - 1, minCost = Integer.MAX_VALUE;
    for (int u = 1; u < n; u++) minCost = Math.min(minCost, dp[full][u] + dist[u][0]);
    return minCost;
}
```

---

## 4. Segment Trees

### What Is a Segment Tree?
A segment tree is a binary tree where:
- **Each leaf** represents one element
- **Each internal node** represents a range (segment) of elements
- **Root** represents the entire array
- Supports **O(log n) range queries** and **O(log n) point updates**

```
Array: [1, 3, 5, 7, 9, 11]

Segment tree (sum):
                   [0..5]=36
                 /           \
          [0..2]=9          [3..5]=27
         /       \          /       \
     [0..1]=4  [2]=5  [3..4]=16  [5]=11
     /     \           /     \
  [0]=1  [1]=3     [3]=7   [4]=9

Query sum(1,4) = 3+5+7+9 = 24
  Traverse: [0..5] → [0..2]=9(partial) + [3..5]=27(partial)
            [0..2] → [1..1]=3 + [2..2]=5
            [3..5] → [3..4]=16 + skip [5]
            [3..4] → [3]=7 + [4]=9
  Result: 3+5+7+9 = 24 ✓ in O(log n) steps
```

### When to Use Segment Trees
```
Use segment tree when:
  ✓ Need repeated range queries (sum, min, max, GCD...)
  ✓ Array is updated frequently between queries
  ✓ O(n) per query is too slow

vs. Prefix sum array:
  Prefix: O(1) query, O(n) update → for static arrays
  Segment tree: O(log n) query, O(log n) update → for dynamic arrays
  Fenwick tree: O(log n) both, but only for invertible operations (sum, XOR)
```

### 4.1 Segment Tree — Range Sum + Point Update

```java
class SegmentTree {
    int[] tree;
    int n;

    SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];   // 4n is safe upper bound for tree size
        build(arr, 0, 0, n - 1);
    }

    // Build tree bottom-up
    void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];   // Leaf node
        } else {
            int mid = (start + end) / 2;
            build(arr, 2*node+1, start, mid);     // Build left child
            build(arr, 2*node+2, mid+1, end);     // Build right child
            tree[node] = tree[2*node+1] + tree[2*node+2];   // Internal = sum of children
        }
    }

    // Point update: set arr[idx] = val
    void update(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val;    // Update leaf
        } else {
            int mid = (start + end) / 2;
            if (idx <= mid) update(2*node+1, start, mid, idx, val);
            else            update(2*node+2, mid+1, end, idx, val);
            tree[node] = tree[2*node+1] + tree[2*node+2];   // Update parent
        }
    }

    // Range sum query: sum of arr[l..r]
    int query(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return 0;        // Out of range
        if (l <= start && end <= r) return tree[node];  // Fully within range
        int mid = (start + end) / 2;
        return query(2*node+1, start, mid, l, r)
             + query(2*node+2, mid+1, end, l, r);  // Partial overlap
    }

    // Public interfaces
    void update(int idx, int val) { update(0, 0, n-1, idx, val); }
    int query(int l, int r)       { return query(0, 0, n-1, l, r); }
}
```

### 4.2 Segment Tree — Range Minimum Query

```java
class SegmentTreeMin {
    int[] tree; int n;

    SegmentTreeMin(int[] arr) {
        n = arr.length; tree = new int[4*n];
        buildMin(arr, 0, 0, n-1);
    }

    void buildMin(int[] arr, int node, int s, int e) {
        if (s == e) { tree[node] = arr[s]; return; }
        int m = (s+e)/2;
        buildMin(arr, 2*node+1, s, m);
        buildMin(arr, 2*node+2, m+1, e);
        tree[node] = Math.min(tree[2*node+1], tree[2*node+2]);   // MIN of children
    }

    int queryMin(int node, int s, int e, int l, int r) {
        if (r < s || e < l) return Integer.MAX_VALUE;    // Out of range sentinel
        if (l <= s && e <= r) return tree[node];
        int m = (s+e)/2;
        return Math.min(queryMin(2*node+1, s, m, l, r),
                        queryMin(2*node+2, m+1, e, l, r));
    }

    int query(int l, int r) { return queryMin(0, 0, n-1, l, r); }
}
// arr=[1,3,2,7,9,11] → query(1,4)=min(3,2,7,9)=2
```

### 4.3 Lazy Propagation — Range Updates

```
Problem: Update all elements in range [l,r] by adding v.
Without lazy: O(n) per range update — defeats the purpose.
With lazy propagation: O(log n) range update.

Idea: Defer propagation of updates.
  When updating [l,r], mark the covered nodes as "lazy" (pending update).
  Only push lazy updates down when we need to access children.
```

```java
class LazySegTree {
    long[] tree, lazy;
    int n;

    LazySegTree(int[] arr) {
        n = arr.length;
        tree = new long[4*n]; lazy = new long[4*n];
        build(arr, 0, 0, n-1);
    }

    void build(int[] arr, int node, int s, int e) {
        if (s==e){tree[node]=arr[s];return;}
        int m=(s+e)/2;
        build(arr,2*node+1,s,m); build(arr,2*node+2,m+1,e);
        tree[node]=tree[2*node+1]+tree[2*node+2];
    }

    // Push pending lazy update down to children
    void pushDown(int node, int s, int e) {
        if (lazy[node] != 0) {
            int m = (s+e)/2;
            // Apply lazy to children
            tree[2*node+1] += lazy[node] * (m - s + 1);
            tree[2*node+2] += lazy[node] * (e - m);
            lazy[2*node+1] += lazy[node];
            lazy[2*node+2] += lazy[node];
            lazy[node] = 0;      // Clear lazy for current node
        }
    }

    // Range update: add val to all elements in [l,r]
    void update(int node, int s, int e, int l, int r, long val) {
        if (r < s || e < l) return;
        if (l <= s && e <= r) {
            tree[node] += val * (e - s + 1);   // Apply to whole segment
            lazy[node] += val;                   // Mark as lazy
            return;
        }
        pushDown(node, s, e);                    // Push before going deeper
        int m=(s+e)/2;
        update(2*node+1,s,m,l,r,val);
        update(2*node+2,m+1,e,l,r,val);
        tree[node]=tree[2*node+1]+tree[2*node+2];
    }

    // Range sum query
    long query(int node, int s, int e, int l, int r) {
        if (r < s || e < l) return 0;
        if (l <= s && e <= r) return tree[node];
        pushDown(node, s, e);
        int m=(s+e)/2;
        return query(2*node+1,s,m,l,r) + query(2*node+2,m+1,e,l,r);
    }

    void update(int l, int r, long val) { update(0,0,n-1,l,r,val); }
    long query(int l, int r)             { return query(0,0,n-1,l,r); }
}
// arr=[1,3,5,7,9,11], update(1,3,+2) → arr=[1,5,7,9,9,11]
// query(0,5)=42
```

### 4.4 Fenwick Tree (Binary Indexed Tree)

```
Fenwick Tree = simplified structure for prefix sum / point update.
Only works for invertible operations (sum, XOR, product with inverse).
More space-efficient (n array vs 4n for segment tree).

Index magic: i & (-i) gives the "responsibility range" of index i.
  i=6 (110): i&(-i)=010=2 → index 6 covers 2 elements (index 5,6)
  i=8 (1000): i&(-i)=1000=8 → index 8 covers 8 elements (1..8)
```

```java
class FenwickTree {
    int[] tree; int n;

    FenwickTree(int n) { this.n = n; tree = new int[n+1]; }

    FenwickTree(int[] arr) {
        n = arr.length; tree = new int[n+1];
        for (int i = 0; i < n; i++) update(i+1, arr[i]);
    }

    // Point update: add delta to index i (1-indexed)
    void update(int i, int delta) {
        for (; i <= n; i += i & (-i))   // Move to parent
            tree[i] += delta;
    }

    // Prefix sum: sum of arr[1..i] (1-indexed)
    int prefixSum(int i) {
        int sum = 0;
        for (; i > 0; i -= i & (-i))    // Move to responsible ancestor
            sum += tree[i];
        return sum;
    }

    // Range sum: sum of arr[l..r] (1-indexed)
    int rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }
}
// Simpler, faster constants than segment tree for sum/XOR queries
// Not suitable for range minimum/maximum queries
```

### 4.5 Segment Tree vs Fenwick vs Prefix Sum

| | Prefix Sum | Fenwick Tree | Segment Tree |
|---|---|---|---|
| Build | O(n) | O(n log n) | O(n) |
| Point update | O(n) | O(log n) | O(log n) |
| Range query | O(1) | O(log n) | O(log n) |
| Range update | O(n) | O(log n)* | O(log n) lazy |
| Operations | Sum | Sum, XOR | Any (min,max,gcd...) |
| Space | O(n) | O(n) | O(4n) |
| Complexity | Simple | Moderate | Complex |

*With difference array trick

---

## 5. Real-World Applications

### 5.1 Sliding Window in Production Systems

```
Network Throughput Monitoring:
  Problem: Track average requests per second over last 60 seconds
  Solution: Fixed sliding window of size 60
  Used by: Nginx rate limiting, AWS CloudWatch metrics

Moving Average Calculations:
  Stock price 20-day moving average → sliding window of size 20
  Weather temperature 7-day average → sliding window of size 7

Stream Processing:
  Apache Kafka, Spark Streaming use sliding/tumbling windows
  "Sum of events in last 5 minutes" → variable window on timestamps

Rate Limiting (Token Bucket variant):
  "Allow max 100 requests per user per minute"
  Sliding window counter per user: O(1) check and update
```

### 5.2 Two Pointers in Production

```
Database: Merge join (sort-merge join algorithm)
  Two sorted sorted streams → two pointers scan simultaneously → O(n)
  Used by: PostgreSQL, MySQL when both sides of JOIN are sorted

Networking: TCP/IP sliding window protocol
  Sender/receiver maintain window boundaries as two pointers
  Controls flow without blocking

Search: Google's merge of sorted posting lists
  "cat AND dog" → two sorted lists → two pointers → intersection in O(n+m)

DNS: IP address range comparison
  Range [192.168.0.0, 192.168.0.255] → start and end pointers
```

### 5.3 Bit Manipulation in Production

```
Permission Systems:
  Unix file permissions: rwxr-xr-x = 111 101 101 = 0755
  if (permissions & READ_BIT) → O(1) permission check
  Used by: Linux kernel, AWS IAM policies

Feature Flags:
  Pack 64 feature flags in one long integer
  Check if feature 37 is enabled: (flags >> 37) & 1
  Used by: Facebook, LaunchDarkly, Optimizely

Bloom Filters:
  Test set membership: hash to bit positions, set bits on insert
  Check membership: all bit positions set? → "probably present"
  Used by: Cassandra, HBase, Redis to avoid disk lookups

Graphics:
  Color channels: 0xFF0000 = red, 0x00FF00 = green
  Alpha blending: (color & 0xFFFFFF) | (alpha << 24)

Network packets:
  IP headers, TCP flags packed as bit fields
  SYN=bit1, ACK=bit4, FIN=bit0 in TCP control field
```

### 5.4 Segment Trees in Production

```
Database Query Optimization:
  B-tree index nodes perform range queries (equivalent structure)
  "SELECT * WHERE age BETWEEN 20 AND 30" → range query on sorted index

Competitive Programming Platforms (Codeforces, LeetCode):
  Judge systems use segment trees for test case generation
  Range max/min queries on test scores

Game Development:
  Collision detection in spatial partitions (segment trees in 1D)
  Audio mixing: range queries on sample ranges

Financial Systems:
  "What's the max stock price between dates X and Y?" → range max query
  Order book: range sums for volume at price ranges
  Used by: Bloomberg, trading systems

Text Editors:
  Rope data structure (variant of segment tree for strings)
  Range operations on text: O(log n) substring, insert, delete
  Used by: Xi editor, some vim variants
```

---

## 6. Complexity Deep Dive

### Sliding Window Complexity

| Problem | Brute Force | Sliding Window | Gain |
|---|---|---|---|
| Max sum subarray size k | O(n×k) | O(n) | k× speedup |
| Longest no-repeat substr | O(n²) | O(n) | n× speedup |
| Min window substring | O(n²×m) | O(n+m) | huge |
| Sliding window max | O(n×k) | O(n) | k× speedup |
| All subarrays product<k | O(n²) | O(n) | n× speedup |

**Key insight:** Each element enters the window once and leaves once → O(n) total.

### Two Pointer Complexity

| Problem | Brute Force | Two Pointers | Space |
|---|---|---|---|
| Two Sum sorted | O(n²) | O(n) | O(1) |
| Three Sum | O(n³) | O(n²) | O(1) |
| Trapping rain water | O(n²) | O(n) | O(1) |
| Container with water | O(n²) | O(n) | O(1) |
| Linked list cycle | O(n) hash | O(n) | O(1) vs O(n) |
| Remove duplicates | O(n²) shift | O(n) | O(1) |

**Key insight:** Two pointers reduce one dimension of search by eliminating provably sub-optimal options.

### Bit Manipulation Complexity

| Operation | Naive | Bit Trick | Note |
|---|---|---|---|
| Count set bits | O(log n) | O(popcount) | Hardware instruction |
| Is power of 2 | O(log n) | O(1) | n&(n-1)==0 |
| Enumerate subsets | — | O(2^n) | Unavoidable |
| Missing number | O(n) + O(n) space | O(n) O(1) | XOR trick |
| Reverse bits | O(32) | O(log 32)=5 | D&C parallel |
| Lowest set bit | O(n) | O(1) | n&(-n) |

### Segment Tree Complexity

| Operation | Naive Array | Prefix Sum | Segment Tree | Fenwick |
|---|---|---|---|---|
| Build | O(1) | O(n) | O(n) | O(n log n) |
| Point update | O(1) | O(n) | O(log n) | O(log n) |
| Range query | O(n) | O(1) | O(log n) | O(log n) |
| Range update | O(n) | O(n) | O(log n)* | O(log n) |
| Space | O(n) | O(n) | O(4n) | O(n) |

*With lazy propagation

### Amortized Analysis — Sliding Window

```
Why is sliding window O(n) even though we have two nested loops?

Each element:
  - Enters the window EXACTLY once (right pointer passes it once)
  - Leaves the window EXACTLY once (left pointer passes it once)
  - Total operations = 2n = O(n)

This is amortized O(1) per element, O(n) total.
Even though the inner while loop runs many times in one outer iteration,
it can't run more times in total than n (each element leaves at most once).
```

### Space Optimization Patterns

```
Sliding Window:
  Use HashMap/HashSet for character tracking: O(alphabet size)
  Use deque for monotonic window max: O(k)
  Rolling hash for string matching: O(1)

Two Pointers:
  In-place algorithms: O(1) extra space
  Floyd's cycle: O(1) instead of O(n) hash set

Bit Manipulation:
  Replace boolean[] of size n with single int/long: O(1) vs O(n)
  Permission sets: O(1) instead of Set<Permission>

Segment Tree:
  Coordinate compression: reduce tree size for sparse values
  Persistent segment tree: O(log n) new nodes per update (version history)
  Implicit/dynamic segment tree: only create nodes when needed
```

---

## Summary

### Pattern Selection Guide

```
Looking at contiguous subarray/substring?
  Fixed size wanted?     → Fixed sliding window
  Variable size?         → Variable sliding window + shrink condition
  Max in each window?    → Monotonic deque + sliding window

Looking for pairs/triplets in sorted or sortable data?
  Pair sum in sorted?    → Two opposite pointers
  Three sum?             → Fix one, two pointers for rest
  Partition in-place?    → Dutch national flag (low/mid/high)
  Cycle in linked list?  → Slow/fast pointers (Floyd's)
  Remove duplicates?     → Read/write pointers same direction

Need extremely fast O(1) operations on integers?
  Check/set/clear bit?   → Direct bit manipulation
  Count set bits?        → n & (n-1) loop or Integer.bitCount
  Find unique element?   → XOR trick
  Subset enumeration?    → Bitmask iteration
  Pack multiple flags?   → Bit fields in int/long

Need fast range queries on mutable arrays?
  Array changes rarely?  → Prefix sum array O(1) query
  Array changes often?   → Segment tree O(log n) both
  Only sum/XOR queries?  → Fenwick tree (simpler, faster constants)
  Range update needed?   → Lazy segment tree

The real skill is RECOGNIZING which pattern fits —
then the implementation follows naturally.
```

### The Core Insight of Each Pattern

```
Sliding Window:   "Adjacent windows share n-1 elements — don't recompute them"
Two Pointers:     "Sorted order lets you eliminate candidates without checking them"
Bit Manipulation: "The CPU already thinks in bits — align your algorithm with hardware"
Segment Tree:     "Precompute partial answers at every level — combine them in O(log n)"
```
