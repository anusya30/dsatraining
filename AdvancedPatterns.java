import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 * ADVANCED PROBLEM SOLVING PATTERNS — Complete Executable Reference
 * ============================================================
 * Topics:
 *  1. Sliding Window               (fixed window: max sum, first negative,
 *                                   variable window: longest no-repeat,
 *                                   k-distinct, min window substring,
 *                                   max ones III, sliding window max,
 *                                   product < k, anagram count)
 *  2. Two Pointer Pattern          (two sum sorted, three sum, four sum,
 *                                   container water, trap rain, sort colors,
 *                                   slow/fast cycle detect, find duplicate,
 *                                   remove duplicates, palindrome check)
 *  3. Bit Manipulation             (essential tricks, XOR magic, subsets,
 *                                   single/two numbers, missing number,
 *                                   hamming weight, count bits DP,
 *                                   reverse bits, power of two, bitmask DP)
 *  4. Segment Trees                (sum + point update, range min,
 *                                   range max, lazy propagation,
 *                                   fenwick tree, range GCD,
 *                                   coordinate compression)
 *  5. Real-World Applications      (rate limiter, moving average,
 *                                   merge join, permission system,
 *                                   bloom filter, order book range query,
 *                                   stock price range max)
 *  6. Complexity Deep Dive         (live benchmarks, operation counts,
 *                                   amortized analysis demos)
 *
 * Compile : javac AdvancedPatterns.java
 * Run     : java AdvancedPatterns
 * ============================================================
 */
public class AdvancedPatterns {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("ADVANCED PROBLEM SOLVING PATTERNS — COMPLETE DEMO");

        section1_SlidingWindow();
        section2_TwoPointers();
        section3_BitManipulation();
        section4_SegmentTrees();
        section5_RealWorldApplications();
        section6_ComplexityDeepDive();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — SLIDING WINDOW
    // =========================================================
    static void section1_SlidingWindow() {
        printSection("1. SLIDING WINDOW PATTERN");

        // 1a. Fixed window — max sum
        System.out.println("--- 1a. Fixed Window: Max Sum Subarray of Size K ---");
        int[][] fixedTests = {{2,1,5,1,3,2},{2,3,4,1,5},{1,4,2,10,23,3,1,0,20},{2,3}};
        int[] ks = {3,2,4,2};
        for (int i = 0; i < fixedTests.length; i++) {
            System.out.printf("  arr=%-25s k=%d → maxSum=%d%n",
                    Arrays.toString(fixedTests[i]), ks[i],
                    maxSumFixed(fixedTests[i], ks[i]));
        }

        // 1b. Fixed window — first negative
        System.out.println("\n--- 1b. Fixed Window: First Negative in Every Window ---");
        int[][] negTests = {{-8,2,-3,4,-10},{12,-1,-7,8,-15,30,16,28},{1,2,3,4}};
        int[] nks = {2,3,2};
        for (int i = 0; i < negTests.length; i++) {
            System.out.printf("  arr=%-25s k=%d → %s%n",
                    Arrays.toString(negTests[i]), nks[i],
                    firstNegativeInWindow(negTests[i], nks[i]));
        }

        // 1c. Variable window — longest no-repeat substring
        System.out.println("\n--- 1c. Variable Window: Longest Substring Without Repeating ---");
        String[] strs = {"abcabcbb","bbbbb","pwwkew","","a","dvdf","anviaj"};
        for (String s : strs)
            System.out.printf("  \"%s\" → %d%n", s, longestNoRepeat(s));

        // 1d. Variable window — k distinct characters
        System.out.println("\n--- 1d. Variable Window: Longest K-Distinct Substring ---");
        String[][] kdTests = {{"araaci","eceba","aabacbebebe"},{"2","2","3"}};
        for (int i = 0; i < 3; i++) {
            int k = Integer.parseInt(kdTests[1][i]);
            System.out.printf("  s=\"%s\" k=%d → longest=%d%n",
                    kdTests[0][i], k, longestKDistinct(kdTests[0][i], k));
        }

        // 1e. Min window substring
        System.out.println("\n--- 1e. Variable Window: Minimum Window Substring ---");
        String[][] mwTests = {{"ADOBECODEBANC","ABC"},{"a","a"},{"a","aa"},{"ADOBECODEBANC","XYZ"}};
        for (String[] t : mwTests)
            System.out.printf("  s=\"%s\" t=\"%s\" → \"%s\"%n", t[0], t[1], minWindow(t[0], t[1]));

        // 1f. Max consecutive ones with flips
        System.out.println("\n--- 1f. Variable Window: Max Consecutive Ones (flip k zeros) ---");
        int[][] onesTests = {
            {1,1,1,0,0,0,1,1,1,1,0},
            {0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1,0},
            {1,0,1,0,1}
        };
        int[] onesKs = {2, 3, 1};
        for (int i = 0; i < onesTests.length; i++)
            System.out.printf("  arr=%-45s k=%d → %d%n",
                    Arrays.toString(onesTests[i]), onesKs[i],
                    longestOnes(onesTests[i], onesKs[i]));

        // 1g. Sliding window maximum (monotonic deque)
        System.out.println("\n--- 1g. Sliding Window Maximum (Monotonic Deque) ---");
        int[] swMax = {1,3,-1,-3,5,3,6,7};
        System.out.println("  arr=" + Arrays.toString(swMax) + " k=3");
        System.out.println("  sliding max: " + Arrays.toString(maxSlidingWindow(swMax, 3)));
        int[] swMax2 = {9,11,8,5,7,10,4,2};
        System.out.println("  arr=" + Arrays.toString(swMax2) + " k=3");
        System.out.println("  sliding max: " + Arrays.toString(maxSlidingWindow(swMax2, 3)));

        // 1h. Count subarrays product < k
        System.out.println("\n--- 1h. Count Subarrays with Product < K ---");
        int[][] prodTests = {{10,5,2,6},{1,2,3},{1,1,1,1,1}};
        int[] prodKs = {100, 10, 2};
        for (int i = 0; i < prodTests.length; i++)
            System.out.printf("  arr=%s k=%d → %d subarrays%n",
                    Arrays.toString(prodTests[i]), prodKs[i],
                    numSubarrayProductLessThanK(prodTests[i], prodKs[i]));

        // 1i. Find anagrams in string (fixed window + char frequency)
        System.out.println("\n--- 1i. Fixed Window: Find All Anagrams ---");
        String[][] anaTests = {{"cbaebabacd","abc"},{"abab","ab"},{"baa","aa"}};
        for (String[] t : anaTests)
            System.out.printf("  s=\"%s\" p=\"%s\" → anagram starts at: %s%n",
                    t[0], t[1], findAnagrams(t[0], t[1]));
    }

    // --- Sliding window implementations ---
    static int maxSumFixed(int[] arr, int k) {
        int sum = 0;
        for (int i = 0; i < k; i++) sum += arr[i];
        int max = sum;
        for (int i = k; i < arr.length; i++) {
            sum += arr[i] - arr[i-k];
            max = Math.max(max, sum);
        }
        return max;
    }
    static List<Integer> firstNegativeInWindow(int[] arr, int k) {
        Deque<Integer> dq = new ArrayDeque<>();
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < k; i++) if (arr[i] < 0) dq.offer(i);
        res.add(dq.isEmpty() ? 0 : arr[dq.peek()]);
        for (int i = k; i < arr.length; i++) {
            if (!dq.isEmpty() && dq.peek() <= i-k) dq.poll();
            if (arr[i] < 0) dq.offer(i);
            res.add(dq.isEmpty() ? 0 : arr[dq.peek()]);
        }
        return res;
    }
    static int longestNoRepeat(String s) {
        Map<Character,Integer> last = new HashMap<>();
        int max = 0, left = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            if (last.containsKey(c) && last.get(c) >= left) left = last.get(c)+1;
            last.put(c, r);
            max = Math.max(max, r-left+1);
        }
        return max;
    }
    static int longestKDistinct(String s, int k) {
        Map<Character,Integer> freq = new HashMap<>();
        int max = 0, left = 0;
        for (int r = 0; r < s.length(); r++) {
            freq.merge(s.charAt(r), 1, Integer::sum);
            while (freq.size() > k) {
                char lc = s.charAt(left);
                freq.merge(lc, -1, Integer::sum);
                if (freq.get(lc) == 0) freq.remove(lc);
                left++;
            }
            max = Math.max(max, r-left+1);
        }
        return max;
    }
    static String minWindow(String s, String t) {
        Map<Character,Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);
        Map<Character,Integer> win = new HashMap<>();
        int left = 0, matched = 0, minLen = Integer.MAX_VALUE, minStart = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r); win.merge(c, 1, Integer::sum);
            if (need.containsKey(c) && win.get(c).equals(need.get(c))) matched++;
            while (matched == need.size()) {
                if (r-left+1 < minLen) { minLen = r-left+1; minStart = left; }
                char lc = s.charAt(left); win.merge(lc, -1, Integer::sum);
                if (need.containsKey(lc) && win.get(lc) < need.get(lc)) matched--;
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart+minLen);
    }
    static int longestOnes(int[] nums, int k) {
        int left = 0, zeros = 0, max = 0;
        for (int r = 0; r < nums.length; r++) {
            if (nums[r] == 0) zeros++;
            while (zeros > k) { if (nums[left++] == 0) zeros--; }
            max = Math.max(max, r-left+1);
        }
        return max;
    }
    static int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> dq = new ArrayDeque<>();
        int[] res = new int[nums.length-k+1];
        for (int i = 0; i < nums.length; i++) {
            while (!dq.isEmpty() && dq.peek() < i-k+1) dq.poll();
            while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();
            dq.offer(i);
            if (i >= k-1) res[i-k+1] = nums[dq.peek()];
        }
        return res;
    }
    static int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;
        int left = 0, prod = 1, count = 0;
        for (int r = 0; r < nums.length; r++) {
            prod *= nums[r];
            while (prod >= k) prod /= nums[left++];
            count += r-left+1;
        }
        return count;
    }
    static List<Integer> findAnagrams(String s, String p) {
        int[] need = new int[26], win = new int[26];
        for (char c : p.toCharArray()) need[c-'a']++;
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            win[s.charAt(i)-'a']++;
            if (i >= p.length()) win[s.charAt(i-p.length())-'a']--;
            if (Arrays.equals(need, win)) res.add(i-p.length()+1);
        }
        return res;
    }

    // =========================================================
    // SECTION 2 — TWO POINTERS
    // =========================================================
    static void section2_TwoPointers() {
        printSection("2. TWO POINTER PATTERN");

        // 2a. Two sum sorted
        System.out.println("--- 2a. Two Sum in Sorted Array ---");
        int[][] tsArrs = {{1,2,3,4,6},{2,7,11,15},{1,3,4,5,7,10,11}};
        int[] tsTargets = {6,9,9};
        for (int i = 0; i < tsArrs.length; i++)
            System.out.printf("  arr=%s target=%d → %s%n",
                    Arrays.toString(tsArrs[i]), tsTargets[i],
                    Arrays.toString(twoSumSorted(tsArrs[i], tsTargets[i])));

        // 2b. Three sum
        System.out.println("\n--- 2b. Three Sum (all unique triplets = 0) ---");
        int[][] threeArrs = {{-1,0,1,2,-1,-4},{0,0,0,0},{-2,0,0,1,1},{-4,-2,-2,-2,0,1,2,2,2,3,3,4,4,6,6}};
        for (int[] arr : threeArrs)
            System.out.printf("  %s → %s%n", Arrays.toString(arr), threeSum(arr));

        // 2c. Four sum
        System.out.println("\n--- 2c. Four Sum ---");
        System.out.println("  [1,0,-1,0,-2,2] target=0 → " + fourSum(new int[]{1,0,-1,0,-2,2}, 0));
        System.out.println("  [2,2,2,2,2] target=8 → " + fourSum(new int[]{2,2,2,2,2}, 8));

        // 2d. Container with most water
        System.out.println("\n--- 2d. Container with Most Water ---");
        int[][] cwArrs = {{1,8,6,2,5,4,8,3,7},{1,1},{4,3,2,1,4},{1,2,1}};
        for (int[] arr : cwArrs)
            System.out.printf("  %s → %d%n", Arrays.toString(arr), maxWater(arr));

        // 2e. Trap rain water
        System.out.println("\n--- 2e. Trapping Rain Water ---");
        int[][] rainArrs = {{0,1,0,2,1,0,1,3,2,1,2,1},{4,2,0,3,2,5},{3,0,0,2,0,4},{1,0,1}};
        for (int[] arr : rainArrs)
            System.out.printf("  %s → %d%n", Arrays.toString(arr), trapRain(arr));

        // 2f. Sort colors (Dutch National Flag)
        System.out.println("\n--- 2f. Sort Colors (Dutch National Flag) ---");
        int[][] colorArrs = {{2,0,2,1,1,0},{2,0,1},{0},{1,2,0},{0,0,0},{2,2,2,1,1,0,0}};
        for (int[] arr : colorArrs) {
            int[] copy = arr.clone();
            sortColors(copy);
            System.out.printf("  %s → %s%n", Arrays.toString(arr), Arrays.toString(copy));
        }

        // 2g. Linked list cycle (simulated with array)
        System.out.println("\n--- 2g. Linked List Cycle Detection (Floyd's) ---");
        ListNode h1 = buildList(new int[]{3,2,0,-4}, 1); // cycle at index 1
        ListNode h2 = buildList(new int[]{1,2}, 0);       // cycle at index 0
        ListNode h3 = buildList(new int[]{1}, -1);        // no cycle
        System.out.println("  [3→2→0→-4→2...] hasCycle: " + hasCycle(h1));
        System.out.println("  [1→2→1...]       hasCycle: " + hasCycle(h2));
        System.out.println("  [1]              hasCycle: " + hasCycle(h3));
        ListNode entry = detectCycleStart(h1);
        System.out.println("  Cycle entry in [3,2,0,-4,cycle@1]: val=" + (entry!=null?entry.val:"null"));

        // 2h. Find duplicate (Floyd's on array)
        System.out.println("\n--- 2h. Find Duplicate (Floyd's Cycle) ---");
        int[][] dupArrs = {{1,3,4,2,2},{3,1,3,4,2},{2,2,2,2,2},{1,1}};
        for (int[] arr : dupArrs)
            System.out.printf("  %s → duplicate=%d%n", Arrays.toString(arr), findDuplicate(arr));

        // 2i. Remove duplicates
        System.out.println("\n--- 2i. Remove Duplicates (In-Place) ---");
        int[][] dedupArrs = {{0,0,1,1,1,2,2,3,3,4},{1,1,2},{1,2,3,4,5}};
        for (int[] arr : dedupArrs) {
            int[] copy = arr.clone();
            int len = removeDuplicates(copy);
            System.out.printf("  %s → len=%d arr=%s%n",
                    Arrays.toString(arr), len, Arrays.toString(Arrays.copyOf(copy, len)));
        }

        // 2j. Valid palindrome (two pointers on string)
        System.out.println("\n--- 2j. Valid Palindrome (Two Pointers) ---");
        String[] palTests = {"A man, a plan, a canal: Panama","race a car","","Was it a car or a cat I saw?"};
        for (String s : palTests)
            System.out.printf("  \"%s\" → %s%n", s, isPalindrome(s));
    }

    // --- Two pointer implementations ---
    static int[] twoSumSorted(int[] arr, int target) {
        int l = 0, r = arr.length-1;
        while (l < r) {
            int s = arr[l]+arr[r];
            if (s == target) return new int[]{l,r};
            else if (s < target) l++; else r--;
        }
        return new int[]{-1,-1};
    }
    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums); List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < nums.length-2; i++) {
            if (i > 0 && nums[i] == nums[i-1]) continue;
            int l = i+1, r = nums.length-1;
            while (l < r) {
                int s = nums[i]+nums[l]+nums[r];
                if (s == 0) {
                    res.add(Arrays.asList(nums[i],nums[l],nums[r]));
                    while (l<r&&nums[l]==nums[l+1]) l++;
                    while (l<r&&nums[r]==nums[r-1]) r--;
                    l++; r--;
                } else if (s < 0) l++; else r--;
            }
        }
        return res;
    }
    static List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums); List<List<Integer>> res = new ArrayList<>();
        int n = nums.length;
        for (int i = 0; i < n-3; i++) {
            if (i > 0 && nums[i] == nums[i-1]) continue;
            for (int j = i+1; j < n-2; j++) {
                if (j > i+1 && nums[j] == nums[j-1]) continue;
                int l = j+1, r = n-1;
                while (l < r) {
                    long s = (long)nums[i]+nums[j]+nums[l]+nums[r];
                    if (s == target) {
                        res.add(Arrays.asList(nums[i],nums[j],nums[l],nums[r]));
                        while (l<r&&nums[l]==nums[l+1]) l++;
                        while (l<r&&nums[r]==nums[r-1]) r--;
                        l++; r--;
                    } else if (s < target) l++; else r--;
                }
            }
        }
        return res;
    }
    static int maxWater(int[] h) {
        int l = 0, r = h.length-1, max = 0;
        while (l < r) {
            max = Math.max(max, Math.min(h[l],h[r])*(r-l));
            if (h[l] < h[r]) l++; else r--;
        }
        return max;
    }
    static int trapRain(int[] h) {
        int l = 0, r = h.length-1, lmax = 0, rmax = 0, water = 0;
        while (l < r) {
            if (h[l] <= h[r]) {
                if (h[l] >= lmax) lmax = h[l]; else water += lmax-h[l];
                l++;
            } else {
                if (h[r] >= rmax) rmax = h[r]; else water += rmax-h[r];
                r--;
            }
        }
        return water;
    }
    static void sortColors(int[] nums) {
        int lo = 0, mid = 0, hi = nums.length-1;
        while (mid <= hi) {
            if (nums[mid] == 0) { swap(nums,lo++,mid++); }
            else if (nums[mid] == 1) { mid++; }
            else { swap(nums,mid,hi--); }
        }
    }
    static void swap(int[] arr, int i, int j) { int t=arr[i];arr[i]=arr[j];arr[j]=t; }
    static boolean hasCycle(ListNode head) {
        ListNode s=head, f=head;
        while (f!=null&&f.next!=null) { s=s.next;f=f.next.next;if(s==f) return true; }
        return false;
    }
    static ListNode detectCycleStart(ListNode head) {
        ListNode s=head,f=head;
        while (f!=null&&f.next!=null) {
            s=s.next;f=f.next.next;
            if (s==f){s=head;while(s!=f){s=s.next;f=f.next;}return s;}
        }
        return null;
    }
    static int findDuplicate(int[] nums) {
        int s=nums[0],f=nums[0];
        do{s=nums[s];f=nums[nums[f]];}while(s!=f);
        s=nums[0];while(s!=f){s=nums[s];f=nums[f];}return s;
    }
    static int removeDuplicates(int[] nums) {
        if (nums.length==0) return 0; int slow=0;
        for (int fast=1;fast<nums.length;fast++)
            if (nums[fast]!=nums[slow]) nums[++slow]=nums[fast];
        return slow+1;
    }
    static boolean isPalindrome(String s) {
        int l=0,r=s.length()-1;
        while (l<r) {
            while (l<r&&!Character.isLetterOrDigit(s.charAt(l))) l++;
            while (l<r&&!Character.isLetterOrDigit(s.charAt(r))) r--;
            if (Character.toLowerCase(s.charAt(l))!=Character.toLowerCase(s.charAt(r))) return false;
            l++;r--;
        }
        return true;
    }

    // Linked list support
    static class ListNode { int val; ListNode next; ListNode(int v){val=v;} }
    static ListNode buildList(int[] vals, int cycleAt) {
        ListNode dummy=new ListNode(0),cur=dummy; ListNode cycleNode=null;
        for (int i=0;i<vals.length;i++) { cur.next=new ListNode(vals[i]); cur=cur.next; if(i==cycleAt) cycleNode=cur; }
        if (cycleAt>=0) cur.next=cycleNode;
        return dummy.next;
    }

    // =========================================================
    // SECTION 3 — BIT MANIPULATION
    // =========================================================
    static void section3_BitManipulation() {
        printSection("3. BIT MANIPULATION");

        // 3a. Essential bit tricks
        System.out.println("--- 3a. Essential Bit Tricks ---");
        int n = 42; // 101010
        System.out.printf("  n=%d (binary=%s)%n", n, Integer.toBinaryString(n));
        for (int i = 0; i < 6; i++)
            System.out.printf("  bit %d: set=%s clear=%d toggle=%d%n",
                    i, isBitSet(n,i), clearBit(n,i), toggleBit(n,i));
        System.out.printf("  isEven(%d)=%s  isOdd(%d)=%s%n", n,isEven(n),n,isOdd(n));
        System.out.printf("  isPowerOf2(64)=%s isPowerOf2(63)=%s%n",isPow2(64),isPow2(63));
        System.out.printf("  lowestSetBit(%d)=%d clearLowest(%d)=%d%n",n,lowestSetBit(n),n,clearLowest(n));

        // 3b. Count set bits
        System.out.println("\n--- 3b. Count Set Bits (Hamming Weight) ---");
        int[] bitNums = {0,1,7,42,255,1023,Integer.MAX_VALUE};
        for (int x : bitNums)
            System.out.printf("  %10d  binary=%-33s  bits=%d%n",
                    x, Integer.toBinaryString(x), countSetBits(x));

        // 3c. Count bits DP (0 to n)
        System.out.println("\n--- 3c. Count Bits DP for 0..12 ---");
        int[] dp = countBitsDP(12);
        System.out.println("  n  : " + IntStream.rangeClosed(0,12).mapToObj(i->String.format("%2d",i)).collect(Collectors.joining(" ")));
        System.out.println("  bits: " + IntStream.range(0,dp.length).mapToObj(i->String.format("%2d",dp[i])).collect(Collectors.joining(" ")));

        // 3d. XOR magic
        System.out.println("\n--- 3d. XOR Magic ---");
        int[][] singleNums = {{4,1,2,1,2},{2,2,1},{1}};
        for (int[] arr : singleNums)
            System.out.printf("  singleNumber(%s)=%d%n", Arrays.toString(arr), singleNumber(arr));

        int[][] twoSingles = {{1,2,3,2,1,4},{4,3,2,4,1,3}};
        for (int[] arr : twoSingles)
            System.out.printf("  twoSingles(%s)=%s%n", Arrays.toString(arr), Arrays.toString(twoSingleNumbers(arr)));

        int[][] missingNums = {{3,0,1},{0,1},{9,6,4,2,3,5,7,0,1}};
        for (int[] arr : missingNums)
            System.out.printf("  missingNumber(%s)=%d%n", Arrays.toString(arr), missingNumber(arr));

        // 3e. Reverse bits
        System.out.println("\n--- 3e. Reverse Bits ---");
        int[] revNums = {43261596, 0, -3, 1};
        for (int x : revNums)
            System.out.printf("  reverseBits(%11d)=%11d%n", x, reverseBits(x));

        // 3f. Bitmask subset enumeration
        System.out.println("\n--- 3f. Subset Enumeration (Bitmask) ---");
        System.out.println("  All subsets of {A,B,C} using bitmask:");
        String[] elems = {"A","B","C"};
        for (int mask = 0; mask < (1<<3); mask++) {
            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < 3; i++) if ((mask&(1<<i))!=0) sb.append(elems[i]).append(",");
            if (sb.length()>1) sb.setLength(sb.length()-1);
            sb.append("}");
            System.out.printf("  mask=%03d(bin): %s%n", Integer.parseInt(Integer.toBinaryString(mask)), sb);
        }

        // 3g. Power of two, three, four checks
        System.out.println("\n--- 3g. Power Checks ---");
        for (int x : new int[]{1,2,3,4,8,16,64,100}) {
            System.out.printf("  n=%-4d  pow2=%s pow4=%s%n",
                    x, isPow2(x), isPow4(x));
        }

        // 3h. Swap without temp
        System.out.println("\n--- 3h. Swap Without Temp (XOR) ---");
        int[] swapArr = {5, 9, 3, 7};
        System.out.println("  Before: " + Arrays.toString(swapArr));
        xorSwap(swapArr, 0, 3);
        xorSwap(swapArr, 1, 2);
        System.out.println("  After swap(0,3) swap(1,2): " + Arrays.toString(swapArr));

        // 3i. Bit tricks for competitive programming
        System.out.println("\n--- 3i. Advanced Bit Tricks ---");
        System.out.println("  n=6:  n&(n-1)=" + (6&5) + " (clear lowest bit)");
        System.out.println("  n=12: n&(-n)="  + (12&-12) + " (isolate lowest set bit)");
        System.out.println("  n=7:  n|(n+1)=" + (7|8) + " (set lowest clear bit)");
        System.out.printf("  XOR all 1..10 = %d%n", xorUpTo(10));
        System.out.printf("  MaxXOR([3,10,5,25,2,8]) = %d%n", maxXOR(new int[]{3,10,5,25,2,8}));
    }

    // --- Bit manipulation implementations ---
    static boolean isBitSet(int n,int i){return (n&(1<<i))!=0;}
    static int clearBit(int n,int i){return n&~(1<<i);}
    static int toggleBit(int n,int i){return n^(1<<i);}
    static boolean isEven(int n){return (n&1)==0;}
    static boolean isOdd(int n){return (n&1)==1;}
    static boolean isPow2(int n){return n>0&&(n&(n-1))==0;}
    static boolean isPow4(int n){return isPow2(n)&&(n&0x55555555)!=0;}
    static int lowestSetBit(int n){return n&(-n);}
    static int clearLowest(int n){return n&(n-1);}
    static int countSetBits(int n){int c=0;while(n!=0){n&=(n-1);c++;}return c;}
    static int[] countBitsDP(int n){int[] dp=new int[n+1];for(int i=1;i<=n;i++) dp[i]=dp[i>>1]+(i&1);return dp;}
    static int singleNumber(int[] nums){int x=0;for(int n:nums) x^=n;return x;}
    static int[] twoSingleNumbers(int[] nums){
        int xor=0; for(int n:nums) xor^=n;
        int diff=xor&(-xor); int a=0,b=0;
        for(int n:nums){if((n&diff)!=0) a^=n; else b^=n;}
        return new int[]{a,b};
    }
    static int missingNumber(int[] nums){
        int n=nums.length,xor=0;
        for(int i=0;i<=n;i++) xor^=i;
        for(int num:nums) xor^=num;
        return xor;
    }
    static int reverseBits(int n){int r=0;for(int i=0;i<32;i++){r<<=1;r|=(n&1);n>>=1;}return r;}
    static void xorSwap(int[] arr,int i,int j){if(i==j) return;arr[i]^=arr[j];arr[j]^=arr[i];arr[i]^=arr[j];}
    static int xorUpTo(int n){switch(n%4){case 0:return n;case 1:return 1;case 2:return n+1;default:return 0;}}
    static int maxXOR(int[] nums){
        int max=0,mask=0;
        for(int i=31;i>=0;i--){
            mask|=(1<<i); Set<Integer> pre=new HashSet<>();
            for(int n:nums) pre.add(n&mask);
            int cand=max|(1<<i);
            for(int p:pre) if(pre.contains(cand^p)){max=cand;break;}
        }
        return max;
    }

    // =========================================================
    // SECTION 4 — SEGMENT TREES
    // =========================================================
    static void section4_SegmentTrees() {
        printSection("4. SEGMENT TREES");

        // 4a. Basic segment tree — sum
        System.out.println("--- 4a. Segment Tree: Range Sum + Point Update ---");
        int[] arr = {1, 3, 5, 7, 9, 11};
        System.out.println("  Array: " + Arrays.toString(arr));
        SegTree st = new SegTree(arr);
        System.out.println("  sum(0,5)=" + st.query(0,5) + " (expected 36)");
        System.out.println("  sum(1,4)=" + st.query(1,4) + " (expected 24)");
        System.out.println("  sum(2,4)=" + st.query(2,4) + " (expected 21)");
        st.update(2, 10); // arr[2] = 10
        System.out.println("  After update(2, 10): sum(0,5)=" + st.query(0,5));
        System.out.println("  sum(1,3)=" + st.query(1,3));

        // 4b. Segment tree — range min
        System.out.println("\n--- 4b. Segment Tree: Range Minimum Query ---");
        int[] arr2 = {4, 2, 7, 1, 9, 3, 6, 8};
        System.out.println("  Array: " + Arrays.toString(arr2));
        SegTreeMin stMin = new SegTreeMin(arr2);
        int[][] minQueries = {{0,7},{1,5},{2,6},{3,4},{0,3}};
        for (int[] q : minQueries)
            System.out.printf("  min(%d,%d)=%d%n", q[0], q[1], stMin.query(q[0], q[1]));

        // 4c. Segment tree — range max
        System.out.println("\n--- 4c. Segment Tree: Range Maximum Query ---");
        int[] arr3 = {6,3,8,2,7,9,1,5};
        SegTreeMax stMax = new SegTreeMax(arr3);
        System.out.println("  Array: " + Arrays.toString(arr3));
        int[][] maxQueries = {{0,7},{0,3},{4,7},{2,5}};
        for (int[] q : maxQueries)
            System.out.printf("  max(%d,%d)=%d%n", q[0], q[1], stMax.query(q[0], q[1]));

        // 4d. Lazy propagation — range update + range query
        System.out.println("\n--- 4d. Lazy Propagation: Range Add + Range Sum ---");
        int[] arr4 = {1,3,5,7,9,11};
        System.out.println("  Array: " + Arrays.toString(arr4));
        LazySegTree lazy = new LazySegTree(arr4);
        System.out.println("  sum(0,5)=" + lazy.query(0,5));
        lazy.update(1, 3, 2); // Add 2 to arr[1..3]
        System.out.println("  After rangeAdd(1,3,+2): sum(0,5)=" + lazy.query(0,5));
        System.out.println("  sum(1,3)=" + lazy.query(1,3) + " (expected 3+2+5+2+7+2=21)");
        lazy.update(0, 5, 3); // Add 3 to all
        System.out.println("  After rangeAdd(0,5,+3): sum(0,5)=" + lazy.query(0,5));

        // 4e. Fenwick tree
        System.out.println("\n--- 4e. Fenwick Tree (BIT) ---");
        int[] arr5 = {3,2,4,5,1,6,7,8};
        FenwickTree fw = new FenwickTree(arr5);
        System.out.println("  Array: " + Arrays.toString(arr5));
        System.out.println("  prefix(4)=" + fw.prefixSum(4));
        System.out.println("  range(2,6)=" + fw.rangeSum(2,6));
        fw.update(3, 5); // Add 5 to index 3
        System.out.println("  After update(3,+5): prefix(4)=" + fw.prefixSum(4));
        System.out.println("  range(1,8)=" + fw.rangeSum(1,8));

        // 4f. Count inversions using Fenwick tree
        System.out.println("\n--- 4f. Count Inversions (Fenwick Tree) ---");
        int[][] invArrays = {{5,4,2,4,1},{2,4,1,3,5},{1,2,3,4,5},{5,4,3,2,1}};
        for (int[] a : invArrays)
            System.out.printf("  %s → inversions=%d%n", Arrays.toString(a), countInversions(a));

        // 4g. Segment tree vs prefix sum performance
        System.out.println("\n--- 4g. Performance Comparison ---");
        System.out.println("  Prefix sum: O(1) query, O(n) update → static arrays");
        System.out.println("  Fenwick:    O(log n) both → sum/XOR queries");
        System.out.println("  Seg tree:   O(log n) both → any operation (min/max/gcd)");
        System.out.println("  Lazy seg:   O(log n) both → range updates supported");
    }

    // --- Segment tree implementations ---
    static class SegTree {
        int[] t; int n;
        SegTree(int[] arr){n=arr.length;t=new int[4*n];build(arr,0,0,n-1);}
        void build(int[] arr,int nd,int s,int e){
            if(s==e){t[nd]=arr[s];return;}int m=(s+e)/2;
            build(arr,2*nd+1,s,m);build(arr,2*nd+2,m+1,e);t[nd]=t[2*nd+1]+t[2*nd+2];}
        void update(int nd,int s,int e,int i,int v){
            if(s==e){t[nd]=v;return;}int m=(s+e)/2;
            if(i<=m) update(2*nd+1,s,m,i,v); else update(2*nd+2,m+1,e,i,v);
            t[nd]=t[2*nd+1]+t[2*nd+2];}
        int query(int nd,int s,int e,int l,int r){
            if(r<s||e<l) return 0; if(l<=s&&e<=r) return t[nd]; int m=(s+e)/2;
            return query(2*nd+1,s,m,l,r)+query(2*nd+2,m+1,e,l,r);}
        void update(int i,int v){update(0,0,n-1,i,v);}
        int query(int l,int r){return query(0,0,n-1,l,r);}
    }
    static class SegTreeMin {
        int[] t; int n;
        SegTreeMin(int[] arr){n=arr.length;t=new int[4*n];build(arr,0,0,n-1);}
        void build(int[] arr,int nd,int s,int e){
            if(s==e){t[nd]=arr[s];return;}int m=(s+e)/2;
            build(arr,2*nd+1,s,m);build(arr,2*nd+2,m+1,e);t[nd]=Math.min(t[2*nd+1],t[2*nd+2]);}
        int query(int nd,int s,int e,int l,int r){
            if(r<s||e<l) return Integer.MAX_VALUE; if(l<=s&&e<=r) return t[nd]; int m=(s+e)/2;
            return Math.min(query(2*nd+1,s,m,l,r),query(2*nd+2,m+1,e,l,r));}
        int query(int l,int r){return query(0,0,n-1,l,r);}
    }
    static class SegTreeMax {
        int[] t; int n;
        SegTreeMax(int[] arr){n=arr.length;t=new int[4*n];build(arr,0,0,n-1);}
        void build(int[] arr,int nd,int s,int e){
            if(s==e){t[nd]=arr[s];return;}int m=(s+e)/2;
            build(arr,2*nd+1,s,m);build(arr,2*nd+2,m+1,e);t[nd]=Math.max(t[2*nd+1],t[2*nd+2]);}
        void update(int nd,int s,int e,int i,int v){
            if(s==e){t[nd]=v;return;}int m=(s+e)/2;
            if(i<=m) update(2*nd+1,s,m,i,v); else update(2*nd+2,m+1,e,i,v);
            t[nd]=Math.max(t[2*nd+1],t[2*nd+2]);}
        int query(int nd,int s,int e,int l,int r){
            if(r<s||e<l) return Integer.MIN_VALUE; if(l<=s&&e<=r) return t[nd]; int m=(s+e)/2;
            return Math.max(query(2*nd+1,s,m,l,r),query(2*nd+2,m+1,e,l,r));}
        int query(int l,int r){return query(0,0,n-1,l,r);}
        void update(int i,int v){update(0,0,n-1,i,v);}
    }
    static class LazySegTree {
        long[] t,lazy; int n;
        LazySegTree(int[] arr){n=arr.length;t=new long[4*n];lazy=new long[4*n];build(arr,0,0,n-1);}
        void build(int[] arr,int nd,int s,int e){
            if(s==e){t[nd]=arr[s];return;}int m=(s+e)/2;
            build(arr,2*nd+1,s,m);build(arr,2*nd+2,m+1,e);t[nd]=t[2*nd+1]+t[2*nd+2];}
        void push(int nd,int s,int e){if(lazy[nd]!=0){int m=(s+e)/2;
            t[2*nd+1]+=lazy[nd]*(m-s+1);t[2*nd+2]+=lazy[nd]*(e-m);
            lazy[2*nd+1]+=lazy[nd];lazy[2*nd+2]+=lazy[nd];lazy[nd]=0;}}
        void update(int nd,int s,int e,int l,int r,long v){
            if(r<s||e<l) return; if(l<=s&&e<=r){t[nd]+=v*(e-s+1);lazy[nd]+=v;return;}
            push(nd,s,e);int m=(s+e)/2;
            update(2*nd+1,s,m,l,r,v);update(2*nd+2,m+1,e,l,r,v);t[nd]=t[2*nd+1]+t[2*nd+2];}
        long query(int nd,int s,int e,int l,int r){
            if(r<s||e<l) return 0; if(l<=s&&e<=r) return t[nd];
            push(nd,s,e);int m=(s+e)/2;
            return query(2*nd+1,s,m,l,r)+query(2*nd+2,m+1,e,l,r);}
        void update(int l,int r,long v){update(0,0,n-1,l,r,v);}
        long query(int l,int r){return query(0,0,n-1,l,r);}
    }
    static class FenwickTree {
        int[] t; int n;
        FenwickTree(int n){this.n=n;t=new int[n+1];}
        FenwickTree(int[] arr){n=arr.length;t=new int[n+1];for(int i=0;i<n;i++) update(i+1,arr[i]);}
        void update(int i,int d){for(;i<=n;i+=i&(-i)) t[i]+=d;}
        int prefixSum(int i){int s=0;for(;i>0;i-=i&(-i)) s+=t[i];return s;}
        int rangeSum(int l,int r){return prefixSum(r)-prefixSum(l-1);}
    }
    static long countInversions(int[] arr) {
        int n=arr.length; int[] sorted=arr.clone(); Arrays.sort(sorted);
        Map<Integer,Integer> rank=new HashMap<>();
        for(int i=0;i<n;i++) rank.put(sorted[i],i+1);
        FenwickTree fw=new FenwickTree(n); long inv=0;
        for(int i=0;i<n;i++){
            int r=rank.get(arr[i]);
            inv+=fw.prefixSum(n)-fw.prefixSum(r);
            fw.update(r,1);
        }
        return inv;
    }

    // =========================================================
    // SECTION 5 — REAL-WORLD APPLICATIONS
    // =========================================================
    static void section5_RealWorldApplications() {
        printSection("5. REAL-WORLD APPLICATIONS");

        // 5a. Rate limiter (sliding window)
        System.out.println("--- 5a. Rate Limiter: Sliding Window Counter ---");
        RateLimiter rl = new RateLimiter(5, 1000); // 5 requests per 1000ms
        long now = System.currentTimeMillis();
        System.out.println("  Limit: 5 req/sec");
        for (int i = 1; i <= 8; i++) {
            boolean allowed = rl.allow("user1", now + i*50);
            System.out.printf("  Request %d at t+%3dms: %s%n",
                    i, i*50, allowed ? "ALLOWED ✓" : "BLOCKED ✗");
        }

        // 5b. Moving average (sliding window)
        System.out.println("\n--- 5b. Moving Average (Stock Price) ---");
        int[] prices = {100,102,98,105,103,107,110,108,115,112};
        System.out.println("  Prices: " + Arrays.toString(prices));
        System.out.println("  5-day moving average:");
        double[] ma = movingAverage(prices, 5);
        for (int i = 0; i < ma.length; i++)
            System.out.printf("  day %-3d: price=%3d  5-day-avg=%.1f%n",
                    i, prices[i], ma[i]);

        // 5c. Unix permission check (bit manipulation)
        System.out.println("\n--- 5c. Unix File Permissions (Bit Manipulation) ---");
        int[] perms = {0755, 0644, 0600, 0777, 0000};
        String[] labels = {"rwxr-xr-x","rw-r--r--","rw-------","rwxrwxrwx","---------"};
        for (int i = 0; i < perms.length; i++) {
            System.out.printf("  %04o (%s): owner-read=%s owner-write=%s exec=%s%n",
                    perms[i], labels[i],
                    (perms[i]&0400)!=0, (perms[i]&0200)!=0, (perms[i]&0100)!=0);
        }

        // 5d. Feature flags (bit manipulation)
        System.out.println("\n--- 5d. Feature Flag System (Bit Fields) ---");
        long flags = 0L;
        int DARK_MODE=0, BETA_UI=1, A_B_TEST=3, NEW_CHECKOUT=7, AI_SEARCH=15;
        flags = setBitL(flags, DARK_MODE);
        flags = setBitL(flags, A_B_TEST);
        flags = setBitL(flags, AI_SEARCH);
        System.out.printf("  flags=0x%X%n", flags);
        System.out.println("  DARK_MODE enabled:    " + isBitSetL(flags, DARK_MODE));
        System.out.println("  BETA_UI enabled:      " + isBitSetL(flags, BETA_UI));
        System.out.println("  A_B_TEST enabled:     " + isBitSetL(flags, A_B_TEST));
        System.out.println("  NEW_CHECKOUT enabled: " + isBitSetL(flags, NEW_CHECKOUT));
        System.out.println("  AI_SEARCH enabled:    " + isBitSetL(flags, AI_SEARCH));

        // 5e. Stock price range query (segment tree)
        System.out.println("\n--- 5e. Stock Price Range Query (Segment Tree) ---");
        int[] stockPrices = {142,145,138,152,149,160,155,163,158,170};
        System.out.println("  Daily prices: " + Arrays.toString(stockPrices));
        SegTreeMax stockTree = new SegTreeMax(stockPrices);
        SegTreeMin stockMinT = new SegTreeMin(stockPrices);
        int[][] ranges = {{0,4},{3,7},{5,9},{0,9}};
        for (int[] r : ranges)
            System.out.printf("  days[%d..%d]: max=%d min=%d range=%d%n",
                    r[0],r[1], stockTree.query(r[0],r[1]),
                    stockMinT.query(r[0],r[1]),
                    stockTree.query(r[0],r[1]) - stockMinT.query(r[0],r[1]));

        // 5f. Merge join (two pointers)
        System.out.println("\n--- 5f. Database Merge Join (Two Pointers) ---");
        int[] tableA = {1,3,5,7,9,11,13,15};
        int[] tableB = {2,5,7,8,11,13,16};
        System.out.println("  Table A (sorted): " + Arrays.toString(tableA));
        System.out.println("  Table B (sorted): " + Arrays.toString(tableB));
        System.out.println("  INNER JOIN (intersection): " + mergeJoin(tableA, tableB));
        System.out.println("  MERGE (union): " + mergeSorted(tableA, tableB));

        // 5g. Bloom filter simulation (bit manipulation)
        System.out.println("\n--- 5g. Bloom Filter Simulation ---");
        BloomFilter bf = new BloomFilter(64);
        String[] words = {"apple","banana","cherry","date"};
        for (String w : words) { bf.add(w); System.out.println("  Added: " + w); }
        String[] checks = {"apple","grape","banana","mango","cherry","fig"};
        for (String w : checks)
            System.out.printf("  '%s' in filter? %s%n", w, bf.mightContain(w) ? "MAYBE YES" : "DEFINITELY NO");
    }

    // --- Real-world helpers ---
    static class RateLimiter {
        int limit; long window;
        Map<String,Deque<Long>> requests = new HashMap<>();
        RateLimiter(int limit, long windowMs){this.limit=limit;this.window=windowMs;}
        boolean allow(String user, long now) {
            requests.putIfAbsent(user, new ArrayDeque<>());
            Deque<Long> q = requests.get(user);
            while (!q.isEmpty() && now - q.peek() >= window) q.poll();
            if (q.size() < limit) { q.offer(now); return true; }
            return false;
        }
    }
    static double[] movingAverage(int[] prices, int k) {
        double[] result = new double[prices.length]; double sum = 0;
        for (int i = 0; i < prices.length; i++) {
            sum += prices[i];
            if (i >= k) sum -= prices[i-k];
            result[i] = i >= k-1 ? sum/k : sum/(i+1);
        }
        return result;
    }
    static long setBitL(long n,int i){return n|(1L<<i);}
    static boolean isBitSetL(long n,int i){return (n&(1L<<i))!=0;}
    static List<Integer> mergeJoin(int[] a, int[] b) {
        List<Integer> res = new ArrayList<>();
        int i=0,j=0;
        while(i<a.length&&j<b.length){
            if(a[i]==b[j]){res.add(a[i]);i++;j++;}
            else if(a[i]<b[j]) i++; else j++;
        }
        return res;
    }
    static List<Integer> mergeSorted(int[] a, int[] b) {
        List<Integer> res = new ArrayList<>();
        int i=0,j=0;
        while(i<a.length&&j<b.length){if(a[i]<=b[j]) res.add(a[i++]); else res.add(b[j++]);}
        while(i<a.length) res.add(a[i++]);
        while(j<b.length) res.add(b[j++]);
        return res;
    }
    static class BloomFilter {
        long bits; int size;
        BloomFilter(int size){this.size=size;}
        void add(String s){bits|=(1L<<(hash1(s)%size));bits|=(1L<<(hash2(s)%size));bits|=(1L<<(hash3(s)%size));}
        boolean mightContain(String s){return (bits&(1L<<(hash1(s)%size)))!=0&&(bits&(1L<<(hash2(s)%size)))!=0&&(bits&(1L<<(hash3(s)%size)))!=0;}
        int hash1(String s){return Math.abs(s.hashCode())%size;}
        int hash2(String s){return Math.abs(s.hashCode()*31+7)%size;}
        int hash3(String s){return Math.abs(s.hashCode()*37+13)%size;}
    }

    // =========================================================
    // SECTION 6 — COMPLEXITY DEEP DIVE
    // =========================================================
    static void section6_ComplexityDeepDive() {
        printSection("6. COMPLEXITY DEEP DIVE");

        // 6a. Sliding window amortized proof
        System.out.println("--- 6a. Sliding Window: Amortized Analysis ---");
        System.out.println("  Each element enters window once, leaves once → 2n ops total");
        System.out.println("  Even with nested while loop → O(n) amortized");
        int[] countArr = new int[1000000];
        Random rng = new Random(42);
        for (int i = 0; i < countArr.length; i++) countArr[i] = rng.nextInt(10)+1;
        long t0 = System.nanoTime();
        numSubarrayProductLessThanK(countArr, 100);
        long sw = System.nanoTime()-t0;
        System.out.printf("  Variable window n=1M: %,d ms%n", sw/1_000_000);

        // 6b. Operation count comparison
        System.out.println("\n--- 6b. Operation Counts: Brute Force vs Pattern ---");
        System.out.printf("  %-35s %-15s %-15s%n","Problem","BruteForce","Pattern");
        System.out.printf("  %-35s %-15s %-15s%n","Max sum window k=100 n=10K","~1,000,000","~10,000");
        System.out.printf("  %-35s %-15s %-15s%n","Longest no-repeat n=10K","~50,000,000","~10,000");
        System.out.printf("  %-35s %-15s %-15s%n","Two sum n=10K (sorted)","~50,000,000","~10,000");
        System.out.printf("  %-35s %-15s %-15s%n","Three sum n=1K","~166,000,000","~500,000");
        System.out.printf("  %-35s %-15s %-15s%n","Seg tree 1M queries n=1M","~1,000,000,000","~20,000,000");

        // 6c. Segment tree vs prefix sum timing
        System.out.println("\n--- 6c. Live Timing: Segment Tree vs Prefix Sum ---");
        int n = 100000, queries = 50000;
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = rng.nextInt(100);

        // Prefix sum
        int[] prefix = new int[n+1];
        for (int i = 0; i < n; i++) prefix[i+1] = prefix[i]+data[i];
        t0 = System.nanoTime();
        int dummy1 = 0;
        for (int i = 0; i < queries; i++) {
            int l=rng.nextInt(n/2), r=l+rng.nextInt(n/2);
            dummy1 += prefix[r+1]-prefix[l];
        }
        long prefixTime = System.nanoTime()-t0;

        // Segment tree
        SegTree segTree = new SegTree(data);
        t0 = System.nanoTime();
        int dummy2 = 0;
        for (int i = 0; i < queries; i++) {
            int l=rng.nextInt(n/2), r=l+rng.nextInt(n/2);
            dummy2 += segTree.query(l,r);
        }
        long segTime = System.nanoTime()-t0;
        System.out.printf("  %,d range queries on n=%,d (no updates):%n", queries, n);
        System.out.printf("    Prefix sum:    %,d ms (O(1) per query)%n", prefixTime/1_000_000);
        System.out.printf("    Segment tree:  %,d ms (O(log n) per query)%n", segTime/1_000_000);
        System.out.println("  → Prefix sum wins when no updates needed");

        // Mix of updates and queries
        SegTree st2 = new SegTree(data.clone());
        t0 = System.nanoTime();
        for (int i = 0; i < queries; i++) {
            if (i%10 == 0) st2.update(rng.nextInt(n), rng.nextInt(100)); // 10% updates
            else { int l=rng.nextInt(n/2); st2.query(l,l+rng.nextInt(n/2)); }
        }
        long mixTime = System.nanoTime()-t0;
        System.out.printf("  %,d mixed ops (10%% updates): seg tree %,d ms%n", queries, mixTime/1_000_000);
        System.out.println("  → Segment tree wins when updates are frequent");

        // 6d. Bit manipulation speed
        System.out.println("\n--- 6d. Bit Manipulation vs Division/Modulo ---");
        int REPS = 10_000_000;
        t0 = System.nanoTime();
        int s1 = 0; for (int i=0;i<REPS;i++) s1 += (i%2==0)?1:0;
        long modTime = System.nanoTime()-t0;
        t0 = System.nanoTime();
        int s2 = 0; for (int i=0;i<REPS;i++) s2 += ((i&1)==0)?1:0;
        long bitTime = System.nanoTime()-t0;
        System.out.printf("  %,dx isEven check: modulo=%,dms bit=%,dms speedup=%.1fx%n",
                REPS, modTime/1_000_000, bitTime/1_000_000, (double)modTime/Math.max(bitTime,1));

        // 6e. Pattern summary
        System.out.println("\n--- 6e. Pattern Complexity Summary ---");
        System.out.printf("  %-35s %-12s %-10s%n","Pattern","Time","Space");
        System.out.printf("  %-35s %-12s %-10s%n","Fixed sliding window","O(n)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Variable sliding window","O(n)","O(k)");
        System.out.printf("  %-35s %-12s %-10s%n","Sliding window max (deque)","O(n)","O(k)");
        System.out.printf("  %-35s %-12s %-10s%n","Two pointers opposite","O(n)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Two pointers same dir","O(n)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Floyd's cycle detect","O(n)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Bit check/set/clear","O(1)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Count set bits (Kernighan)","O(bits)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Subset enumeration","O(2^n)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Segment tree build","O(n)","O(4n)");
        System.out.printf("  %-35s %-12s %-10s%n","Segment tree query/update","O(log n)","O(1)");
        System.out.printf("  %-35s %-12s %-10s%n","Lazy seg tree range update","O(log n)","O(4n)");
        System.out.printf("  %-35s %-12s %-10s%n","Fenwick tree","O(log n)","O(n)");
    }

    // =========================================================
    // UTILITIES
    // =========================================================
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
