// ================================================================
//   HASHING — Complete Deep Dive in Java
// ================================================================
//   Compile:  javac Hashing.java
//   Run:      java Hashing
// ================================================================
//
//   TOPICS:
//   1. What is Hashing?
//   2. Hash Function Fundamentals
//   3. HashMap Internal Working
//   4. Collision Handling
//   5. Time & Space Complexity
//   6. Real-World Systems
//   7. Interview-Level Problems
// ================================================================

import java.util.*;

public class Hashing {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    static void sub(String title) {
        System.out.println("\n  ── " + title + " ──");
    }


    // ============================================================
    // CUSTOM HASH MAP IMPLEMENTATION
    // ============================================================
    //
    //   Building our own HashMap from scratch teaches exactly
    //   how Java's HashMap works internally.
    //
    //   INTERNAL STRUCTURE:
    //   - An array of "buckets" (called table[])
    //   - Each bucket holds a linked list of Entry nodes
    //     (for collision handling via Separate Chaining)
    //   - Default capacity: 16 buckets
    //   - Load factor: 0.75 (resize when 75% full)
    //
    //   KEY → hash(key) → compress → bucket index → store Entry
    //
    //   HASH FORMULA:
    //   index = hash(key) % capacity
    //   Java actually uses: index = (capacity - 1) & hash(key)
    //   (bitwise AND with power-of-2 capacity is faster than modulo)
    // ============================================================
    static class CustomHashMap<K, V> {
        private static final int   DEFAULT_CAPACITY    = 16;
        private static final float DEFAULT_LOAD_FACTOR = 0.75f;

        // Each Entry = one key-value pair + pointer to next (for chaining)
        static class Entry<K, V> {
            K key;
            V value;
            Entry<K, V> next; // next entry in same bucket (collision chain)
            int hash;         // cached hash to avoid recomputing

            Entry(K key, V value, int hash) {
                this.key   = key;
                this.value = value;
                this.hash  = hash;
            }
        }

        @SuppressWarnings("unchecked")
        private Entry<K, V>[] table =
                (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        private int size     = 0;
        private int capacity = DEFAULT_CAPACITY;

        // ── HASH FUNCTION
        // Step 1: get Java's hashCode() for the key
        // Step 2: spread bits to reduce clustering (XOR upper/lower bits)
        // Step 3: compress into bucket index using modulo
        private int hash(K key) {
            if (key == null) return 0;
            int h = key.hashCode();
            h = h ^ (h >>> 16); // XOR high and low 16 bits — "bit spreading"
            return h & (capacity - 1); // fast modulo for power-of-2 capacity
        }

        // ── PUT — O(1) average, O(n) worst (all in one bucket)
        void put(K key, V value) {
            int idx   = hash(key);
            Entry<K, V> curr = table[idx];

            // Check if key already exists in this bucket — update value
            while (curr != null) {
                if (curr.key.equals(key)) {
                    curr.value = value; // UPDATE existing key
                    return;
                }
                curr = curr.next;
            }

            // Key not found — INSERT new entry at head of bucket chain
            Entry<K, V> newEntry = new Entry<>(key, value, idx);
            newEntry.next = table[idx]; // new entry points to old chain
            table[idx]    = newEntry;   // new entry becomes head
            size++;

            // Resize if load factor exceeded
            if ((float) size / capacity >= DEFAULT_LOAD_FACTOR) resize();
        }

        // ── GET — O(1) average
        V get(K key) {
            int idx  = hash(key);
            Entry<K, V> curr = table[idx];
            while (curr != null) {
                if (curr.key.equals(key)) return curr.value;
                curr = curr.next;
            }
            return null; // not found
        }

        // ── REMOVE — O(1) average
        boolean remove(K key) {
            int idx = hash(key);
            Entry<K, V> curr = table[idx];
            Entry<K, V> prev = null;

            while (curr != null) {
                if (curr.key.equals(key)) {
                    if (prev == null) table[idx] = curr.next; // remove head
                    else              prev.next  = curr.next; // bypass node
                    size--;
                    return true;
                }
                prev = curr;
                curr = curr.next;
            }
            return false;
        }

        // ── CONTAINS KEY — O(1) average
        boolean containsKey(K key) { return get(key) != null; }

        // ── RESIZE — O(n) — doubles capacity, rehashes all entries
        // Called when size/capacity >= loadFactor (0.75)
        // New capacity = old capacity * 2 (keeps power-of-2 for fast indexing)
        @SuppressWarnings("unchecked")
        private void resize() {
            int oldCap = capacity;
            capacity   = oldCap * 2;
            Entry<K, V>[] oldTable = table;
            table = (Entry<K, V>[]) new Entry[capacity];
            size  = 0;

            // Rehash every existing entry into the new table
            for (int i = 0; i < oldCap; i++) {
                Entry<K, V> curr = oldTable[i];
                while (curr != null) {
                    put(curr.key, curr.value); // reinsert with new hash
                    curr = curr.next;
                }
            }
        }

        int size() { return size; }

        // Print bucket distribution — shows how entries are spread
        void printBuckets(String label) {
            System.out.println("  " + label + " (size=" + size +
                    ", capacity=" + capacity + "):");
            for (int i = 0; i < capacity; i++) {
                if (table[i] != null) {
                    System.out.print("    Bucket[" + i + "]: ");
                    Entry<K, V> curr = table[i];
                    while (curr != null) {
                        System.out.print("[" + curr.key + "=" + curr.value + "]");
                        if (curr.next != null) System.out.print(" → ");
                        curr = curr.next;
                    }
                    System.out.println();
                }
            }
        }
    }


    // ============================================================
    // OPEN ADDRESSING — LINEAR PROBING
    // ============================================================
    //
    //   Alternative collision strategy: instead of chaining,
    //   store all entries in the table itself.
    //   On collision, probe the NEXT slot until an empty one is found.
    //
    //   Linear Probing: probe index = (hash + i) % capacity
    //   Quadratic:      probe index = (hash + i²) % capacity
    //   Double Hashing: probe index = (hash + i × hash2) % capacity
    //
    //   PROBLEM with Linear Probing: PRIMARY CLUSTERING
    //   Occupied slots tend to form long runs → more collisions
    //
    //   DELETED slots use a "tombstone" marker — important!
    //   Without tombstones, GET stops too early after a DELETE.
    // ============================================================
    static class LinearProbingMap {
        private static final String DELETED = "__DELETED__"; // tombstone

        private String[] keys;
        private Integer[] values;
        private int capacity;
        private int size;

        LinearProbingMap(int capacity) {
            this.capacity = capacity;
            this.keys     = new String[capacity];
            this.values   = new Integer[capacity];
        }

        private int hash(String key) {
            return Math.abs(key.hashCode()) % capacity;
        }

        // PUT — probe until empty or same key found
        void put(String key, int value) {
            int idx = hash(key);
            for (int i = 0; i < capacity; i++) {
                int probe = (idx + i) % capacity; // linear probe
                if (keys[probe] == null || keys[probe].equals(DELETED)) {
                    keys[probe]   = key;
                    values[probe] = value;
                    size++;
                    return;
                }
                if (keys[probe].equals(key)) {
                    values[probe] = value; // update
                    return;
                }
            }
            throw new RuntimeException("Map is full!");
        }

        // GET — probe until key found or empty slot (not tombstone)
        Integer get(String key) {
            int idx = hash(key);
            for (int i = 0; i < capacity; i++) {
                int probe = (idx + i) % capacity;
                if (keys[probe] == null) return null; // truly empty → not found
                if (keys[probe].equals(key)) return values[probe];
                // DELETED (tombstone) → continue probing
            }
            return null;
        }

        // DELETE — replace with tombstone, NOT null
        boolean remove(String key) {
            int idx = hash(key);
            for (int i = 0; i < capacity; i++) {
                int probe = (idx + i) % capacity;
                if (keys[probe] == null) return false;
                if (keys[probe].equals(key)) {
                    keys[probe]   = DELETED; // tombstone, NOT null!
                    values[probe] = null;
                    size--;
                    return true;
                }
            }
            return false;
        }

        void print(String label) {
            System.out.println("  " + label + ":");
            for (int i = 0; i < capacity; i++) {
                if (keys[i] != null && !keys[i].equals(DELETED)) {
                    System.out.printf("    slot[%2d]: %s = %d%n",
                            i, keys[i], values[i]);
                } else if (keys[i] != null) {
                    System.out.printf("    slot[%2d]: [TOMBSTONE]%n", i);
                }
            }
        }
    }


    // ============================================================
    // INTERVIEW PROBLEM HELPERS
    // ============================================================

    // ── TWO SUM — O(n) time, O(n) space
    // For each element, check if (target - element) is already in map.
    // Map stores element → index for O(1) lookup.
    static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>(); // value → index
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }

    // ── ANAGRAM GROUPING — O(n × k) time where k = avg word length
    // Group words that are anagrams of each other.
    // Key insight: sorted characters of anagrams are identical.
    // "eat","tea","tan","ate","nat","bat" →
    // [["eat","tea","ate"],["tan","nat"],["bat"]]
    static Map<String, List<String>> groupAnagrams(String[] words) {
        Map<String, List<String>> map = new HashMap<>();
        for (String word : words) {
            char[] chars = word.toCharArray();
            Arrays.sort(chars); // sort → same key for all anagrams
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        return map;
    }

    // ── LONGEST CONSECUTIVE SEQUENCE — O(n) time, O(n) space
    // Find length of longest consecutive integer sequence in unsorted array.
    // Key insight: only start counting from the sequence START
    // (number n where n-1 is NOT in set → avoids counting from middle).
    static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);

        int maxLen = 0;
        for (int n : set) {
            if (!set.contains(n - 1)) { // n is a sequence START
                int curr = n, len = 1;
                while (set.contains(curr + 1)) { curr++; len++; }
                maxLen = Math.max(maxLen, len);
            }
        }
        return maxLen;
    }

    // ── SUBARRAY SUM EQUALS K — O(n) time, O(n) space
    // Count subarrays whose elements sum to k.
    // Key insight: prefix sum. If prefixSum[j] - prefixSum[i] = k,
    // then subarray [i+1..j] sums to k.
    // Map stores prefixSum → how many times it occurred.
    static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1); // empty subarray has sum 0
        int sum = 0, count = 0;

        for (int num : nums) {
            sum += num;
            // If (sum - k) was seen before, those subarrays sum to k
            count += prefixCount.getOrDefault(sum - k, 0);
            prefixCount.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // ── TOP K FREQUENT ELEMENTS — O(n log k) time
    // Count frequencies, then use min-heap of size k.
    static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);

        // Min-heap of size k — keeps only k largest frequencies
        PriorityQueue<Integer> heap =
                new PriorityQueue<>(Comparator.comparingInt(freq::get));

        for (int num : freq.keySet()) {
            heap.offer(num);
            if (heap.size() > k) heap.poll(); // remove smallest
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = heap.poll();
        return result;
    }

    // ── FOUR SUM COUNT — O(n²) time, O(n²) space
    // Count tuples (i,j,k,l) such that A[i]+B[j]+C[k]+D[l]=0
    // Split into two pairs: store all A+B sums, then check -(C+D) in map.
    static int fourSumCount(int[] A, int[] B, int[] C, int[] D) {
        Map<Integer, Integer> abSums = new HashMap<>();
        for (int a : A) for (int b : B)
            abSums.merge(a + b, 1, Integer::sum);

        int count = 0;
        for (int c : C) for (int d : D)
            count += abSums.getOrDefault(-(c + d), 0);
        return count;
    }

    // ── WORD PATTERN MATCH — O(n) time, O(n) space
    // "abba" matches "dog cat cat dog"? YES
    // "abba" matches "dog cat cat fish"? NO
    // Bijection: pattern char ↔ word must be one-to-one
    static boolean wordPattern(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) return false;

        Map<Character, String> charToWord = new HashMap<>();
        Map<String, Character> wordToChar = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String w = words[i];

            if (charToWord.containsKey(c) && !charToWord.get(c).equals(w))
                return false;
            if (wordToChar.containsKey(w) && wordToChar.get(w) != c)
                return false;

            charToWord.put(c, w);
            wordToChar.put(w, c);
        }
        return true;
    }

    // ── LRU CACHE — O(1) get and put
    // Doubly Linked List + HashMap
    // DLL: maintains order (head=most recent, tail=least recent)
    // HashMap: key → node reference for O(1) access
    static class LRUCache {
        private final int capacity;
        private final Map<Integer, int[]> map; // key → [key, value]
        private final Deque<Integer> order;    // most recent at front

        LRUCache(int capacity) {
            this.capacity = capacity;
            this.map      = new HashMap<>();
            this.order    = new LinkedList<>();
        }

        int get(int key) {
            if (!map.containsKey(key)) return -1;
            // Move to front (most recently used)
            order.remove(key);
            order.addFirst(key);
            return map.get(key)[1];
        }

        void put(int key, int value) {
            if (map.containsKey(key)) {
                order.remove(key);
            } else if (map.size() == capacity) {
                int lru = order.removeLast(); // evict least recently used
                map.remove(lru);
            }
            map.put(key, new int[]{key, value});
            order.addFirst(key);
        }

        @Override
        public String toString() {
            return "Cache: " + order + " (front=most recent)";
        }
    }


    // ============================================================
    //   MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         HASHING — Complete Deep Dive in Java             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");


        // ═══════════════════════════════════════════════════════
        // TOPIC 1 — WHAT IS HASHING?
        // ═══════════════════════════════════════════════════════
        section("TOPIC 1 — What is Hashing?");

        sub("The Core Idea");
        System.out.println("  Hashing = converting ANY key into an ARRAY INDEX.");
        System.out.println();
        System.out.println("  Problem: How do you store and find \"Navaneeth\" instantly?");
        System.out.println("  Array:   scan every element        → O(n)");
        System.out.println("  BST:     compare and branch        → O(log n)");
        System.out.println("  HashMap: hash(\"Navaneeth\") = index → O(1) ← magic!");
        System.out.println();
        System.out.println("  The hash function converts a key to a number (bucket index).");
        System.out.println("  The value is stored at that index in an internal array.");
        System.out.println("  Retrieval: hash the key again → same index → same value.");

        sub("The Telephone Directory Analogy");
        System.out.println("  Finding 'Ravi' in a phone book:");
        System.out.println("  Without hashing: flip every page until R section → O(n)");
        System.out.println("  With hashing:    hash('Ravi') → page 342 → directly there → O(1)");
        System.out.println();
        System.out.println("  Real Java HashMap: same principle but for any key type.");

        sub("What Hashing Enables");
        System.out.println("  ┌────────────────────┬────────────┬────────────────────────┐");
        System.out.println("  │  Operation         │  No Hash   │  With Hashing          │");
        System.out.println("  ├────────────────────┼────────────┼────────────────────────┤");
        System.out.println("  │  Find by key       │  O(n)      │  O(1) average          │");
        System.out.println("  │  Insert            │  O(n) BST  │  O(1) average          │");
        System.out.println("  │  Delete            │  O(n)      │  O(1) average          │");
        System.out.println("  │  Membership check  │  O(n)      │  O(1) average          │");
        System.out.println("  └────────────────────┴────────────┴────────────────────────┘");

        sub("Key vs Value — Java HashMap Demo");
        Map<String, Integer> phoneBook = new HashMap<>();
        phoneBook.put("Alice",    9876543210L > 0 ? 9876543210 : 0);
        phoneBook.put("Navaneeth",8765432109L > 0 ? 8765432109 : 0);
        phoneBook.put("Priya",    7654321098L > 0 ? 7654321098 : 0);
        phoneBook.put("Ravi",     6543210987L > 0 ? 6543210987 : 0);

        Map<String, String> contacts = new HashMap<>();
        contacts.put("Alice",     "9876543210");
        contacts.put("Navaneeth", "8765432109");
        contacts.put("Priya",     "7654321098");
        contacts.put("Ravi",      "6543210987");

        System.out.println("  Phone book (HashMap<String, String>):");
        contacts.forEach((name, num) ->
                System.out.println("    " + name + " → " + num));
        System.out.println();
        System.out.println("  contacts.get(\"Navaneeth\") = " + contacts.get("Navaneeth"));
        System.out.println("  Time: O(1) — directly computed from key, no scan needed");


        // ═══════════════════════════════════════════════════════
        // TOPIC 2 — HASH FUNCTION FUNDAMENTALS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 2 — Hash Function Fundamentals");

        sub("What makes a GOOD hash function?");
        System.out.println("  1. DETERMINISTIC  → same key always gives same hash");
        System.out.println("  2. UNIFORM        → distributes keys evenly across buckets");
        System.out.println("  3. FAST           → O(1) computation");
        System.out.println("  4. AVALANCHE      → small key change → wildly different hash");

        sub("Simple modulo hash — building intuition");
        System.out.println("  hash(key) = key % tableSize  (for integer keys)");
        System.out.println("  tableSize = 10  (10 buckets: 0..9)");
        System.out.println();
        int[] intKeys = {15, 27, 36, 45, 55, 73, 84, 93};
        System.out.println("  Key  →  hash (key % 10)  →  Bucket");
        System.out.println("  ────────────────────────────────────");
        for (int k : intKeys) {
            System.out.printf("   %2d  →      %d          →   [%d]%n",
                    k, k % 10, k % 10);
        }

        sub("Java String hashCode() — polynomial hashing");
        System.out.println("  Java computes string hash as a POLYNOMIAL:");
        System.out.println("  hash = s[0]×31^(n-1) + s[1]×31^(n-2) + ... + s[n-1]");
        System.out.println("  Why 31? It's prime → fewer collisions. Also: 31n = 32n - n");
        System.out.println("  = (n << 5) - n  ← JVM optimizes this to bit shift");
        System.out.println();
        String[] hashWords = {"cat", "tac", "act", "dog", "god"};
        System.out.println("  Word     hashCode()        % 16 bucket");
        System.out.println("  ─────────────────────────────────────");
        for (String w : hashWords) {
            int h    = w.hashCode();
            int bucket = h & 15; // same as h % 16 for power-of-2
            System.out.printf("  %-6s   %12d    %3d%n", w, h, bucket);
        }

        sub("The Avalanche Effect — good hash spreads tiny changes");
        System.out.println("  Key 'a' hashCode:  " + "a".hashCode());
        System.out.println("  Key 'b' hashCode:  " + "b".hashCode());
        System.out.println("  Key 'aa' hashCode: " + "aa".hashCode());
        System.out.println("  Key 'ab' hashCode: " + "ab".hashCode());
        System.out.println("  Small input change → dramatically different hash → good spread");

        sub("Java's hashCode() contract");
        System.out.println("  Rule 1: If a.equals(b) → a.hashCode() == b.hashCode()");
        System.out.println("  Rule 2: If a.hashCode() == b.hashCode() → a may or may not equal b");
        System.out.println("  (hash collision is allowed; hash equality without equals() is not)");
        System.out.println();
        System.out.println("  DANGER: if you override equals() you MUST override hashCode()!");
        System.out.println("  Otherwise two 'equal' objects hash to different buckets → key never found.");

        sub("Bit Spreading — Java HashMap's internal trick");
        System.out.println("  Raw hashCode may have poor distribution in lower bits.");
        System.out.println("  Java HashMap applies: h = h ^ (h >>> 16)");
        System.out.println("  This mixes upper 16 bits into lower 16 bits.");
        int rawHash    = "Navaneeth".hashCode();
        int spreadHash = rawHash ^ (rawHash >>> 16);
        System.out.println("  'Navaneeth' raw    hash: " + rawHash);
        System.out.println("  'Navaneeth' spread hash: " + spreadHash);
        System.out.println("  Bucket (capacity=16):    " + (spreadHash & 15));


        // ═══════════════════════════════════════════════════════
        // TOPIC 3 — HASHMAP INTERNAL WORKING
        // ═══════════════════════════════════════════════════════
        section("TOPIC 3 — HashMap Internal Working");

        sub("Internal array of buckets");
        System.out.println("  HashMap stores data in Node[] table — an array of buckets.");
        System.out.println("  Default capacity: 16 buckets (always a power of 2).");
        System.out.println("  Each bucket: null OR a chain of Node(key, value, hash, next).");
        System.out.println();
        System.out.println("  table[] array (16 slots):");
        System.out.println("  [0]  → null");
        System.out.println("  [1]  → Node(key1, val1) → null");
        System.out.println("  [2]  → null");
        System.out.println("  [3]  → Node(key2, val2) → Node(key3, val3) → null  ← collision!");
        System.out.println("  ...");

        sub("Step-by-step: put(\"name\", \"Navaneeth\")");
        System.out.println("  Step 1: Compute hashCode(\"name\")     = " + "name".hashCode());
        System.out.println("  Step 2: Spread bits h^(h>>>16)       = " +
                ("name".hashCode() ^ ("name".hashCode() >>> 16)));
        int nameIdx = ("name".hashCode() ^ ("name".hashCode() >>> 16)) & 15;
        System.out.println("  Step 3: Bucket index (spread & 15)   = " + nameIdx);
        System.out.println("  Step 4: Walk bucket[" + nameIdx +
                "]. Key found? Update. Not found? Insert at head.");
        System.out.println("  Step 5: size++. If size/capacity >= 0.75: resize!");

        sub("Custom HashMap — building and inspecting");
        CustomHashMap<String, Integer> custom = new CustomHashMap<>();
        custom.put("apple",   100);
        custom.put("banana",  200);
        custom.put("cherry",  300);
        custom.put("date",    400);
        custom.put("elderberry", 500);
        custom.put("fig",     600);
        custom.printBuckets("After inserting 6 fruits");
        System.out.println();
        System.out.println("  get(\"banana\") = " + custom.get("banana"));
        System.out.println("  get(\"mango\")  = " + custom.get("mango") + " (not found)");
        custom.put("banana", 999);
        System.out.println("  put(\"banana\", 999) → update existing key");
        System.out.println("  get(\"banana\") = " + custom.get("banana"));
        custom.remove("cherry");
        System.out.println("  remove(\"cherry\") → deleted");
        System.out.println("  get(\"cherry\") = " + custom.get("cherry") + " (removed)");

        sub("Load Factor & Resizing");
        System.out.println("  Load Factor = size / capacity");
        System.out.println("  Default threshold: 0.75 (resize when 75% full)");
        System.out.println();
        System.out.println("  WHY 0.75?");
        System.out.println("  → Load=1.0: too many collisions → O(n) operations");
        System.out.println("  → Load=0.5: wastes memory → too many empty buckets");
        System.out.println("  → Load=0.75: empirically best balance");
        System.out.println();
        System.out.println("  When resizing: capacity doubles (16→32→64...)");
        System.out.println("  ALL entries are REHASHED into the new table → O(n)");
        System.out.println("  But happens rarely → amortized O(1) per put");

        sub("Java 8+ Treeification");
        System.out.println("  Java 8 change: when a bucket chain exceeds 8 nodes,");
        System.out.println("  Java converts the chain to a RED-BLACK TREE.");
        System.out.println("  Bucket chain (linked list): O(n) worst case");
        System.out.println("  Bucket tree  (red-black):   O(log n) worst case");
        System.out.println("  Threshold:   > 8 nodes → tree. < 6 nodes → back to list.");
        System.out.println("  This prevents O(n) worst case in adversarial inputs.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 4 — COLLISION HANDLING
        // ═══════════════════════════════════════════════════════
        section("TOPIC 4 — Collision Handling");

        sub("What is a Collision?");
        System.out.println("  Two DIFFERENT keys produce the SAME bucket index.");
        System.out.println("  hash(\"a\") % 5 = 2");
        System.out.println("  hash(\"f\") % 5 = 2  ← collision!");
        System.out.println("  Collisions are INEVITABLE (pigeonhole principle).");
        System.out.println("  With n keys and m buckets (n > m), pigeonhole guarantees collision.");
        System.out.println("  Good hash + good capacity minimizes collision FREQUENCY.");

        sub("Strategy 1 — Separate Chaining (Java HashMap uses this)");
        System.out.println("  Each bucket holds a LINKED LIST of all colliding entries.");
        System.out.println();
        System.out.println("  hash(\"a\") = 2, hash(\"f\") = 2:");
        System.out.println("  Bucket[2] → [a=1] → [f=2] → null");
        System.out.println();
        System.out.println("  put(\"a\", 1): bucket[2] = [a=1]");
        System.out.println("  put(\"f\", 2): bucket[2] = [f=2] → [a=1]  ← insert at head");
        System.out.println("  get(\"a\"): bucket[2] → walk chain → find 'a' → O(chain length)");
        System.out.println();
        System.out.println("  Worst case: ALL keys in one bucket → O(n)");
        System.out.println("  Average with good hash: O(1) (short chains)");

        // Demonstrate separate chaining with our custom map
        CustomHashMap<String, Integer> chainDemo = new CustomHashMap<>();
        // Force collisions by using keys that share a bucket
        chainDemo.put("AaAaAa", 1);
        chainDemo.put("AaAaBB", 2);
        chainDemo.put("AaBBAa", 3);
        chainDemo.put("BBAaAa", 4);
        System.out.println();
        System.out.println("  Java String anagram 'AaAaAa' and 'AaAaBB' may share bucket:");
        System.out.println("  AaAaAa hashCode: " + "AaAaAa".hashCode());
        System.out.println("  AaAaBB hashCode: " + "AaAaBB".hashCode());
        System.out.println("  AaBBAa hashCode: " + "AaBBAa".hashCode());
        System.out.println("  BBAaAa hashCode: " + "BBAaAa".hashCode());

        sub("Strategy 2 — Open Addressing: Linear Probing");
        LinearProbingMap lpMap = new LinearProbingMap(11);
        System.out.println("  All keys stored IN the table. On collision, probe next slot.");
        System.out.println("  probe(i) = (hash + i) % capacity");
        System.out.println();
        String[] lpKeys = {"cat", "dog", "rat", "bat", "hat"};
        for (String k : lpKeys) {
            int h = Math.abs(k.hashCode()) % 11;
            System.out.printf("  put(\"%s\"): hash=%d → try slot %d%n", k, h, h);
            lpMap.put(k, k.length());
        }
        lpMap.print("Linear Probing Map state");
        System.out.println();
        System.out.println("  Tombstone deletion: replacing deleted slot with DELETED marker");
        System.out.println("  (NOT null — otherwise GET would stop probing too early)");
        lpMap.remove("dog");
        lpMap.print("After remove(\"dog\") — tombstone at slot");

        sub("Strategy Comparison");
        System.out.println("  ┌─────────────────────┬──────────────────────┬─────────────────────┐");
        System.out.println("  │  Property           │  Separate Chaining   │  Open Addressing    │");
        System.out.println("  ├─────────────────────┼──────────────────────┼─────────────────────┤");
        System.out.println("  │  Memory             │  Extra (node ptrs)   │  In-table, compact  │");
        System.out.println("  │  Cache performance  │  Poor (linked list)  │  Good (contiguous)  │");
        System.out.println("  │  Deletion           │  Easy (remove node)  │  Needs tombstone    │");
        System.out.println("  │  Load factor        │  Can exceed 1.0      │  Must stay < 1.0    │");
        System.out.println("  │  Used by            │  Java HashMap        │  Python dict, C++   │");
        System.out.println("  └─────────────────────┴──────────────────────┴─────────────────────┘");


        // ═══════════════════════════════════════════════════════
        // TOPIC 5 — TIME & SPACE COMPLEXITY
        // ═══════════════════════════════════════════════════════
        section("TOPIC 5 — Time & Space Complexity");

        sub("Operation Complexity");
        System.out.println("  ┌──────────────┬──────────────┬────────────────┬───────────────────┐");
        System.out.println("  │  Operation   │  Average     │  Worst Case    │  Why worst O(n)?  │");
        System.out.println("  ├──────────────┼──────────────┼────────────────┼───────────────────┤");
        System.out.println("  │  put(k,v)    │  O(1)        │  O(n)          │  All keys collide │");
        System.out.println("  │  get(k)      │  O(1)        │  O(n)          │  Long bucket chain│");
        System.out.println("  │  remove(k)   │  O(1)        │  O(n)          │  Long bucket chain│");
        System.out.println("  │  containsKey │  O(1)        │  O(n)          │  Long bucket chain│");
        System.out.println("  │  resize      │  O(n)        │  O(n)          │  Rehash all keys  │");
        System.out.println("  │  iteration   │  O(n)        │  O(n)          │  Visit all entries│");
        System.out.println("  └──────────────┴──────────────┴────────────────┴───────────────────┘");
        System.out.println("  Java 8+: worst case O(log n) per bucket (treeification)");
        System.out.println("  Space: O(n) — n key-value pairs stored");

        sub("Amortized O(1) for put — why resize doesn't hurt");
        System.out.println("  Resize happens at: 12, 24, 48, 96... inserts");
        System.out.println("  (capacity × 0.75 = resize threshold)");
        System.out.println();
        System.out.println("  Total work for n inserts:");
        System.out.println("  = n inserts + resize costs (12 + 24 + 48 + ... + n)");
        System.out.println("  = n + 2n (geometric series) = 3n = O(n) total");
        System.out.println("  = O(1) per insert amortized");

        sub("HashMap vs TreeMap vs LinkedHashMap");
        System.out.println("  ┌──────────────────┬────────────┬────────────┬────────────────────┐");
        System.out.println("  │  Class           │  get/put   │  Ordered?  │  Use when          │");
        System.out.println("  ├──────────────────┼────────────┼────────────┼────────────────────┤");
        System.out.println("  │  HashMap         │  O(1) avg  │  No        │  General use ✅    │");
        System.out.println("  │  TreeMap         │  O(log n)  │  Sorted    │  Sorted key order  │");
        System.out.println("  │  LinkedHashMap   │  O(1) avg  │  Insertion │  LRU cache, order  │");
        System.out.println("  │  HashTable       │  O(1) avg  │  No        │  Avoid (legacy)    │");
        System.out.println("  └──────────────────┴────────────┴────────────┴────────────────────┘");

        sub("HashSet — same internals, no values");
        System.out.println("  HashSet<K> is literally a HashMap<K, PRESENT>");
        System.out.println("  where PRESENT = new Object() (a dummy value).");
        System.out.println("  add()     → O(1) average");
        System.out.println("  contains()→ O(1) average");
        System.out.println("  remove()  → O(1) average");

        sub("Live timing — HashMap vs Linear Search");
        int N = 100_000;
        int[] arr = new int[N];
        Map<Integer, Boolean> hashLookup = new HashMap<>();
        for (int i = 0; i < N; i++) {
            arr[i] = i;
            hashLookup.put(i, true);
        }

        int target = N - 1; // worst case for linear
        long t1 = System.nanoTime();
        for (int x : arr) if (x == target) break;
        long linearTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        hashLookup.containsKey(target);
        long hashTime = System.nanoTime() - t2;

        System.out.printf("%n  Linear scan (100K elements): %,d ns%n", linearTime);
        System.out.printf("  HashMap lookup (100K elements): %,d ns%n", hashTime);
        System.out.println("  HashMap is orders of magnitude faster for membership checks.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 6 — REAL-WORLD SYSTEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 6 — Real-World Systems");

        sub("1. Caching — Memoization");
        Map<Integer, Long> fibCache = new HashMap<>();
        fibCache.put(0, 0L);
        fibCache.put(1, 1L);

        System.out.println("  Fibonacci with HashMap memoization:");
        for (int i = 2; i <= 10; i++) {
            long val = fibCache.get(i-1) + fibCache.get(i-2);
            fibCache.put(i, val);
            System.out.printf("    fib(%2d) = %d%n", i, val);
        }
        System.out.println("  Each fib(n) computed ONCE, O(1) lookup thereafter.");

        sub("2. Word Frequency Counter (Search Engine / NLP)");
        String text = "the quick brown fox jumps over the lazy dog the fox";
        Map<String, Integer> wordFreq = new LinkedHashMap<>();
        for (String word : text.split(" ")) {
            wordFreq.merge(word, 1, Integer::sum);
        }
        System.out.println("  Text: \"" + text + "\"");
        System.out.println("  Word frequencies:");
        wordFreq.entrySet().stream()
            .sorted((a,b) -> b.getValue() - a.getValue())
            .forEach(e -> System.out.printf("    %-10s → %d%n",
                    e.getKey(), e.getValue()));

        sub("3. Duplicate Detection (Data Pipelines)");
        String[] emails = {
            "alice@gmail.com", "bob@yahoo.com", "alice@gmail.com",
            "carol@gmail.com", "bob@yahoo.com", "dave@gmail.com"
        };
        Set<String> seen      = new HashSet<>();
        List<String> unique   = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();

        for (String email : emails) {
            if (!seen.add(email)) duplicates.add(email); // add() returns false if already present
            else                  unique.add(email);
        }
        System.out.println("  Emails:     " + Arrays.toString(emails));
        System.out.println("  Unique:     " + unique);
        System.out.println("  Duplicates: " + duplicates);
        System.out.println("  O(n) time to detect all duplicates using HashSet");

        sub("4. Database Index Simulation");
        Map<Integer, String> userIndex = new HashMap<>(); // userId → name
        userIndex.put(1001, "Navaneeth Kumar");
        userIndex.put(1002, "Priya Sharma");
        userIndex.put(1003, "Ravi Patel");
        System.out.println("  Database index (userId → name):");
        System.out.println("  SELECT name FROM users WHERE id=1002 →");
        System.out.println("  Index lookup: " + userIndex.get(1002) +
                "  (O(1) vs O(n) full table scan)");

        sub("5. LRU Cache — HashMap + Doubly Linked List");
        LRUCache lru = new LRUCache(3);
        System.out.println("  LRU Cache capacity=3:");
        lru.put(1, 10); System.out.println("  put(1,10): " + lru);
        lru.put(2, 20); System.out.println("  put(2,20): " + lru);
        lru.put(3, 30); System.out.println("  put(3,30): " + lru);
        System.out.println("  get(1)=" + lru.get(1) + ": " + lru + "  ← 1 moved to front");
        lru.put(4, 40);
        System.out.println("  put(4,40): " + lru + "  ← 2 evicted (LRU)");
        System.out.println("  get(2)=" + lru.get(2) + "  ← evicted, returns -1");
        System.out.println("  All operations O(1): HashMap for access, DLL for order");

        sub("6. Consistent Hashing (Distributed Systems)");
        System.out.println("  Used in: Redis Cluster, Cassandra, Amazon DynamoDB");
        System.out.println("  Problem: with n servers, keys map to server as: hash(key) % n");
        System.out.println("  Adding/removing server → ALL keys remap → massive migration!");
        System.out.println();
        System.out.println("  Consistent Hashing solution:");
        System.out.println("  Hash both SERVERS and KEYS onto a ring (0..2^32)");
        System.out.println("  Key maps to the nearest server clockwise on the ring.");
        System.out.println("  Add/remove server → only ~1/n keys remap. Minimal disruption.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 7 — INTERVIEW-LEVEL PROBLEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 7 — Interview-Level Problems");

        sub("Problem 1: Two Sum");
        int[][] tsCases = {{2,7,11,15},{3,2,4},{3,3}};
        int[]   tsTargets = {9, 6, 6};
        for (int i = 0; i < tsCases.length; i++) {
            int[] res = twoSum(tsCases[i], tsTargets[i]);
            System.out.printf("  nums=%-15s target=%d → indices%s%n",
                    Arrays.toString(tsCases[i]), tsTargets[i], Arrays.toString(res));
        }
        System.out.println("  Technique: map stores (value→index). For each num,");
        System.out.println("  check if (target-num) already in map. O(n)/O(n)");

        sub("Problem 2: Group Anagrams");
        String[] words = {"eat","tea","tan","ate","nat","bat","arc","car"};
        Map<String, List<String>> groups = groupAnagrams(words);
        System.out.println("  Input:  " + Arrays.toString(words));
        System.out.println("  Groups:");
        groups.forEach((k,v) -> System.out.println("    " + v));
        System.out.println("  Technique: sort each word as key. O(n×k log k)/O(n×k)");

        sub("Problem 3: Longest Consecutive Sequence");
        int[][] lcsCases = {
            {100,4,200,1,3,2},
            {0,3,7,2,5,8,4,6,0,1},
            {9,1,4,7,3,-1,0,5,8,-1,6}
        };
        for (int[] arr : lcsCases) {
            System.out.printf("  %-30s → longest=%d%n",
                    Arrays.toString(arr), longestConsecutive(arr));
        }
        System.out.println("  Technique: HashSet for O(1) lookup. Only count from");
        System.out.println("  sequence start (n where n-1 not in set). O(n)/O(n)");

        sub("Problem 4: Subarray Sum Equals K");
        int[][] ssCases = {{1,1,1},{1,2,3},{-1,1,0,1}};
        int[] ssK = {2, 3, 1};
        for (int i = 0; i < ssCases.length; i++) {
            System.out.printf("  nums=%-15s k=%d → count=%d%n",
                    Arrays.toString(ssCases[i]), ssK[i],
                    subarraySum(ssCases[i], ssK[i]));
        }
        System.out.println("  Technique: prefix sum + HashMap. Count[sum-k] gives");
        System.out.println("  subarrays ending here with sum=k. O(n)/O(n)");

        sub("Problem 5: Top K Frequent Elements");
        int[][] tkfCases = {{1,1,1,2,2,3},{1},{4,1,2,2,3,3,3}};
        int[] tkfK = {2, 1, 2};
        for (int i = 0; i < tkfCases.length; i++) {
            System.out.printf("  nums=%-20s k=%d → %s%n",
                    Arrays.toString(tkfCases[i]), tkfK[i],
                    Arrays.toString(topKFrequent(tkfCases[i], tkfK[i])));
        }
        System.out.println("  Technique: freq map + min-heap size k. O(n log k)/O(n)");

        sub("Problem 6: Word Pattern");
        String[][] wpCases = {
            {"abba","dog cat cat dog"},
            {"abba","dog cat cat fish"},
            {"aaaa","dog cat cat dog"},
            {"abba","dog dog dog dog"}
        };
        for (String[] c : wpCases) {
            System.out.printf("  pattern=%-6s  s=%-22s → %s%n",
                    "\""+c[0]+"\"", "\""+c[1]+"\"",
                    wordPattern(c[0], c[1]) ? "true ✅" : "false ❌");
        }
        System.out.println("  Technique: two maps (char→word, word→char) bijection. O(n)/O(n)");

        sub("Problem 7: LRU Cache");
        LRUCache cache = new LRUCache(2);
        System.out.println("  LRUCache(2):");
        cache.put(1, 1); System.out.println("  put(1,1)");
        cache.put(2, 2); System.out.println("  put(2,2)");
        System.out.println("  get(1)=" + cache.get(1) + "  ← 1 is now MRU");
        cache.put(3, 3); System.out.println("  put(3,3) → evicts 2 (LRU)");
        System.out.println("  get(2)=" + cache.get(2) + "  ← evicted");
        System.out.println("  get(1)=" + cache.get(1));
        System.out.println("  get(3)=" + cache.get(3));
        System.out.println("  Technique: HashMap + Deque. All operations O(1).");

        // GRAND SUMMARY
        section("GRAND SUMMARY");
        System.out.println();
        System.out.println("  Hashing = key → hash function → array index → O(1) access");
        System.out.println();
        System.out.println("  KEY CLASSES:");
        System.out.println("  HashMap     → key-value, O(1) avg, unordered");
        System.out.println("  HashSet     → unique keys, O(1) avg, unordered");
        System.out.println("  LinkedHashMap → insertion-ordered HashMap");
        System.out.println("  TreeMap     → sorted keys, O(log n)");
        System.out.println();
        System.out.println("  GOLDEN RULES:");
        System.out.println("  1. Override equals() AND hashCode() together — never one without other");
        System.out.println("  2. Mark visited with HashSet in graph/tree problems");
        System.out.println("  3. Prefix sum + HashMap → O(n) subarray sum problems");
        System.out.println("  4. Two HashMap bijection → pattern matching problems");
        System.out.println("  5. Frequency map → most common element problems");
        System.out.println("  6. HashMap + DLL → LRU Cache O(1) get and put");
    }
}
