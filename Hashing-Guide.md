# Hashing — Complete Deep Dive in Java

---

## How to Run

```bash
javac Hashing.java
java Hashing
```

> Requires Java 8 or higher. Check with `java -version`.

---

## File Structure

```
Hashing.java
│
├── CustomHashMap<K,V>           → HashMap built from scratch
│   ├── Entry<K,V>               → Node: key, value, hash, next
│   ├── hash(key)                → bit-spreading hash function
│   ├── put(key, value)          → O(1) average
│   ├── get(key)                 → O(1) average
│   ├── remove(key)              → O(1) average
│   ├── resize()                 → O(n) — doubles capacity + rehash
│   └── printBuckets()           → visualize bucket distribution
│
├── LinearProbingMap             → Open addressing implementation
│   ├── put() / get() / remove() → with tombstone deletion
│   └── probe = (hash + i) % cap → linear probing formula
│
├── LRUCache                     → HashMap + Deque, O(1) all ops
│
├── twoSum()                     → Interview Problem 1
├── groupAnagrams()              → Interview Problem 2
├── longestConsecutive()         → Interview Problem 3
├── subarraySum()                → Interview Problem 4
├── topKFrequent()               → Interview Problem 5
├── wordPattern()                → Interview Problem 6
│
└── main()                       → Runs all 7 topics
```

---

## Topic 1 — What is Hashing?

### The Core Problem

How do you store and retrieve a value by a key — any key, any type — in constant time?

```
Array lookup by index: arr[5]           → O(1)  ✅ but only integer indices
Linear search:         scan until found → O(n)  ❌ too slow
Binary search:         requires sorted  → O(log n) — better but not O(1)
Hashing:               hash(key)→index → O(1)  ✅ any key, any type
```

### The Big Idea

A **hash function** converts any key (String, Integer, custom object) into an integer — the **bucket index**. The value is stored at that index in an internal array.

```
put("Navaneeth", 9876):
  Step 1: hash("Navaneeth") = 1234567 (some integer)
  Step 2: 1234567 % 16 = 7            (compress to bucket)
  Step 3: table[7] = Entry("Navaneeth", 9876)

get("Navaneeth"):
  Step 1: hash("Navaneeth") = 1234567  (same hash, always)
  Step 2: 1234567 % 16 = 7             (same bucket)
  Step 3: return table[7].value = 9876 ✓
```

The key insight: **the same key always produces the same hash**, so you can always find where you stored a value without scanning.

### What Hashing Enables

| Operation | Without Hashing | With Hashing |
|-----------|----------------|--------------|
| Find by key | O(n) linear scan | **O(1) average** |
| Insert | O(log n) BST | **O(1) average** |
| Delete | O(n) or O(log n) | **O(1) average** |
| Membership check | O(n) | **O(1) average** |

---

## Topic 2 — Hash Function Fundamentals

### Properties of a Good Hash Function

```
1. DETERMINISTIC  → hash("cat") always returns the same number
2. UNIFORM        → keys spread evenly across all buckets
3. FAST           → O(1) to compute
4. AVALANCHE      → small key change → wildly different hash
```

### Simple Integer Hashing — Modulo

```
For integer keys and a table of size 10:
hash(k) = k % 10

15 → bucket 5
27 → bucket 7
36 → bucket 6
37 → bucket 7  ← collision with 27!
```

**Problem with poor table sizes:** if tableSize is not prime (or power-of-2), certain patterns cluster badly. E.g., tableSize=100 with all even keys → only even buckets used.

### Java String hashCode() — Polynomial Hashing

Java computes String hash as a polynomial:

```
hash(s) = s[0]×31^(n-1) + s[1]×31^(n-2) + ... + s[n-1]×31^0
```

Implemented with Horner's method for efficiency:
```java
int hash = 0;
for (char c : s.toCharArray()) {
    hash = 31 * hash + c;
}
```

**Why 31?**
1. It's prime — prime multipliers produce fewer patterns/collisions
2. `31 * n = (n << 5) - n` — the JVM can optimize multiplication by 31 into a bit shift + subtraction, making it extremely fast

**The Avalanche Effect:**
```
"cat"  hashCode: 98262 →  bucket 6 (for capacity 16)
"dog"  hashCode: 99333 →  bucket 5
"cats" hashCode: 3045744 → very different from "cat"!
```

One character change → completely different hash. This ensures good spread.

### The `hashCode()` Contract

```java
// These rules are part of Java's Object specification:

// Rule 1: Equal objects MUST have equal hash codes
if (a.equals(b)) {
    assert a.hashCode() == b.hashCode();  // REQUIRED
}

// Rule 2: Equal hash codes don't mean equal objects (collision allowed)
if (a.hashCode() == b.hashCode()) {
    // a.equals(b) may be true OR false — both are valid
}
```

**The most dangerous mistake in Java:**
```java
class Student {
    String name;
    int id;

    @Override
    public boolean equals(Object o) {
        return this.id == ((Student)o).id;
    }
    // ❌ forgot to override hashCode()!
}

Student s1 = new Student("Alice", 101);
Student s2 = new Student("Alice", 101);

s1.equals(s2);  // true ✅ (same id)
s1.hashCode() == s2.hashCode();  // false ❌ (default Object.hashCode uses memory address!)

Map<Student, String> map = new HashMap<>();
map.put(s1, "Engineer");
map.get(s2);  // null! — goes to wrong bucket, never finds s1
```

**Always override both together.** IDEs (IntelliJ, Eclipse) can generate both at once.

### Bit Spreading — Java's Internal Trick

Raw `hashCode()` often has poor entropy in the lower bits (where bucket index is computed). Java HashMap applies:

```java
h = h ^ (h >>> 16);  // XOR high 16 bits into low 16 bits
index = h & (capacity - 1);  // fast modulo for power-of-2
```

This "spreads" the hash, using information from ALL 32 bits to determine the bucket — not just the bottom few bits.

---

## Topic 3 — HashMap Internal Working

### The Data Structure

```
HashMap<K, V>
  │
  └── Node<K,V>[] table   (array of buckets, default size 16)
       │
       ├── table[0]  → null
       ├── table[1]  → Node(k1,v1,hash1,next=null)
       ├── table[2]  → null
       ├── table[3]  → Node(k2,v2,hash2, next=Node(k3,v3,hash3,null))  ← collision!
       └── ...
```

Each `Node` (called `Entry` in our custom implementation) contains:
- `key` — the key
- `value` — the value
- `hash` — cached hash (avoids recomputing on resize)
- `next` — pointer to next node in same bucket (for chaining)

### Step-by-Step: `put("name", "Navaneeth")`

```
1. Compute hashCode("name")           = some int, e.g. 3373752
2. Spread bits: h ^ (h >>> 16)        = slightly different int
3. Bucket index: spreadHash & 15      = 0..15 (for capacity=16)
4. Go to table[index]
5. Walk the chain: is any node's key.equals("name")?
     YES → update that node's value
     NO  → create new Node, insert at HEAD of chain
6. size++
7. if (size / capacity >= 0.75) → resize()
```

### Resizing — The O(n) Operation Done Right

```
Initial: capacity=16, threshold=12
After 12 puts: resize triggered!
  New capacity = 32
  New threshold = 24
  ALL entries rehashed with new hash function (capacity changed!)
  Every node goes to a potentially different bucket
```

**Why rehash?** Because bucket index = `hash & (capacity - 1)`. When capacity changes from 16 to 32, `& 15` becomes `& 31` — different indices for most keys. You can't just copy the array.

**Cost:** O(n) for one resize, but it happens at n=12, 24, 48, 96... Total rehash work across all resizes:
```
12 + 24 + 48 + ... + n ≈ 2n = O(n) total
Amortized per put: O(n) / n = O(1)
```

### Java 8+ Treeification

When a single bucket's chain grows beyond **8 nodes** (extremely rare with good hash), Java converts it to a **Red-Black Tree**:

```
Before (Linked List, O(n)):
bucket[3] → [A] → [B] → [C] → [D] → [E] → [F] → [G] → [H] → [I]

After treeification (Red-Black Tree, O(log n)):
bucket[3] →     [E]
               /     \
            [B]         [G]
           /   \       /   \
         [A]   [C]  [F]     [H]
                  \             \
                  [D]           [I]
```

This prevents O(n) worst-case in adversarial inputs (deliberately crafted to all hash to the same bucket).

Converts back to list when size drops below **6 nodes**.

---

## Topic 4 — Collision Handling

### Why Collisions Are Inevitable

With `n` keys and `m` buckets, if `n > m`, by the **Pigeonhole Principle**, at least one bucket must contain more than one key. Even with `n < m`, probability theory shows collisions become likely well before the table is full (Birthday Paradox — 50% chance of collision with just √m keys).

### Strategy 1 — Separate Chaining (Java HashMap)

Each bucket holds a linked list (or tree) of all colliding entries:

```
put("a", 1):  hash("a") % 5 = 2
put("f", 2):  hash("f") % 5 = 2  ← collision!

table[2] → [f=2|→] → [a=1|null]
```

**On get("a"):**
```
1. Compute hash("a") % 5 = 2
2. Go to table[2]
3. Walk chain: f != a → next; a == a → return 1  ✓
```

**Performance:**
```
Load factor α = n / m (average chain length)
Expected chain length = α
With α = 0.75 → average 0.75 nodes per bucket
get() touches ≈ 1 node on average → O(1)
```

**Worst case:** all n keys hash to the same bucket → one chain of length n → O(n). This is prevented by good hash functions and treeification.

---

### Strategy 2 — Open Addressing: Linear Probing

All entries stored **in the table itself**. On collision, probe the next slot:

```
probe(i) = (hash + i) % capacity   (linear probing)
probe(i) = (hash + i²) % capacity  (quadratic probing)
probe(i) = (hash + i×h2) % capacity (double hashing)
```

```
capacity = 7, hash("cat") = 3, hash("bat") = 3  ← collision

put("cat", 3): table[3] = "cat"
put("bat", 3): table[3] occupied → probe table[4] = empty → table[4] = "bat"
```

**The Tombstone Problem:**
```
put("cat"):    table[3] = "cat"
put("bat"):    table[4] = "bat"  (probed past "cat")
remove("cat"): table[3] = null   ← WRONG! Breaks "bat" lookup!

get("bat"):
  hash("bat") = 3 → table[3] = null → STOP → returns "not found"!
  But "bat" is in table[4]!
```

**Fix: Tombstone marker:**
```
remove("cat"): table[3] = DELETED  ← tombstone, not null

get("bat"):
  hash("bat") = 3 → table[3] = DELETED → continue probing
  table[4] = "bat" → found! ✓
```

Tombstone means "something WAS here, keep looking" vs null which means "nothing was ever here, stop."

### Strategy Comparison

| Property | Separate Chaining | Open Addressing |
|----------|------------------|-----------------|
| Memory | Extra pointer per node | In-table, compact |
| Cache performance | Poor (pointer-chasing) | **Excellent (contiguous)** |
| Deletion | Easy (unlink node) | Needs tombstone |
| Max load factor | Can exceed 1.0 | Must stay < 1.0 |
| Clustering | No | Primary clustering risk |
| Used by | **Java HashMap** | Python dict, C++ unordered_map |

---

## Topic 5 — Time & Space Complexity

### All Operations

| Operation | Average | Worst Case | Why Worst O(n)? |
|-----------|---------|------------|-----------------|
| `put(k,v)` | **O(1)** | O(n) | All keys in one bucket |
| `get(k)` | **O(1)** | O(n) | Walk entire bucket chain |
| `remove(k)` | **O(1)** | O(n) | Walk entire bucket chain |
| `containsKey` | **O(1)** | O(n) | Walk entire bucket chain |
| `resize()` | O(n) | O(n) | Rehash every entry |
| Iteration | O(n) | O(n) | Visit every bucket |
| **Space** | **O(n)** | O(n) | n entries stored |

Java 8+: worst case per bucket is O(log n) due to treeification — not O(n).

### Amortized O(1) for `put()`

A single `put()` occasionally triggers a resize (O(n)). But spread over many operations:

```
n puts trigger resizes at capacities: 12, 24, 48, 96, ..., n

Total rehash cost = 12 + 24 + 48 + ... ≈ 2n (geometric series)
Total put cost    = n (one array write each)
Grand total       = 3n = O(n)
Per operation     = O(n) / n = O(1) amortized
```

### Choosing the Right Map

| Class | `get/put` | Ordered | Thread-safe | Use When |
|-------|----------|---------|-------------|----------|
| `HashMap` | **O(1) avg** | No | No | General purpose ✅ |
| `TreeMap` | O(log n) | **Sorted by key** | No | Need sorted iteration |
| `LinkedHashMap` | **O(1) avg** | **Insertion order** | No | LRU cache, ordered iteration |
| `ConcurrentHashMap` | O(1) avg | No | **Yes** | Multi-threaded access |
| `Hashtable` | O(1) avg | No | Yes (slow) | Legacy — avoid |

### HashSet Internals

`HashSet<K>` is literally implemented as `HashMap<K, Object>` where the dummy value is `new Object()`:

```java
private static final Object PRESENT = new Object();
private HashMap<E, Object> map;

public boolean add(E e) { return map.put(e, PRESENT) == null; }
public boolean contains(Object o) { return map.containsKey(o); }
```

All Set operations have the same complexity as their HashMap counterparts.

---

## Topic 6 — Real-World Systems

### 1. Memoization / Caching

Any recursive problem with overlapping subproblems benefits from a HashMap cache:

```java
Map<Integer, Long> cache = new HashMap<>();

long fib(int n) {
    if (n <= 1) return n;
    if (cache.containsKey(n)) return cache.get(n);  // O(1) lookup
    long result = fib(n-1) + fib(n-2);
    cache.put(n, result);  // O(1) store
    return result;
}
```

Without cache: O(2ⁿ). With HashMap cache: O(n). Same concept used in CPU caches, web caches, CDN edge nodes.

---

### 2. Word Frequency Counter — Search Engines / NLP

```java
Map<String, Integer> freq = new HashMap<>();
for (String word : text.split(" ")) {
    freq.merge(word, 1, Integer::sum);  // O(1) per word
}
// Total: O(n) for n words
```

Used in: Google's PageRank, Elasticsearch indexing, spam detection, autocomplete.

---

### 3. Duplicate Detection — Data Pipelines

```java
Set<String> seen = new HashSet<>();
for (String item : stream) {
    if (!seen.add(item)) {
        // duplicate detected — O(1) check
    }
}
```

`seen.add()` returns `false` if the element was already present. One pass, O(n) total. Used in: data deduplication, event sourcing systems, ad impression tracking.

---

### 4. Database Index

A database B-Tree or Hash index is conceptually the same idea:

```
Without index: SELECT * WHERE id=1002 → scan all rows → O(n)
With hash index: hash(1002) → page 7 → O(1) row lookup
```

Hash indexes are used for equality lookups (`=`). B-Tree indexes are used for range queries (`BETWEEN`, `<`, `>`).

---

### 5. LRU Cache — The Classic HashMap + DLL Combo

```
HashMap: key → node reference  (O(1) access to any node)
Doubly Linked List: MRU at head, LRU at tail

get(key):
  1. HashMap.get(key)         → O(1) find the node
  2. Move node to DLL head    → O(1) pointer updates
  3. Return value

put(key, value):
  1. If exists: update + move to head   → O(1)
  2. If full: remove DLL tail, HashMap.remove(lruKey)  → O(1)
  3. Insert new node at head + HashMap.put()  → O(1)
```

**Result: O(1) for both get and put.** Used in: Redis, Memcached, CPU instruction cache, OS page replacement.

---

### 6. Consistent Hashing — Distributed Databases

Regular hashing in distributed systems:
```
server = hash(key) % numServers
```
Adding/removing a server changes `numServers` → almost ALL keys remap → massive data migration.

**Consistent hashing** solution:
```
Place both servers and keys on a "ring" (hash space 0..2³²)
key maps to the nearest server clockwise on the ring
Adding server S: only the keys between S's predecessor and S remap
Removing server S: only S's keys move to S's successor
Migration: ~1/n keys instead of all keys
```

Used in: Amazon DynamoDB, Apache Cassandra, Chord P2P, Redis Cluster, CDN routing.

---

## Topic 7 — Interview-Level Problems

### Problem 1: Two Sum — O(n) time, O(n) space

**Pattern:** For each element, check if its complement `(target - num)` has been seen before.

```java
Map<Integer, Integer> map = new HashMap<>();  // value → index
for (int i = 0; i < nums.length; i++) {
    int complement = target - nums[i];
    if (map.containsKey(complement))
        return new int[]{map.get(complement), i};
    map.put(nums[i], i);
}
```

**Trace for `[2,7,11,15], target=9`:**
```
i=0: num=2, complement=7. Map={} → 7 not found. map.put(2,0)
i=1: num=7, complement=2. Map={2:0} → 2 found! return [0,1] ✓
```

**Why store index, not just a boolean?** The problem asks for indices. O(n) vs O(n²) brute force.

---

### Problem 2: Group Anagrams — O(n × k log k) time

**Key Insight:** All anagrams of a word produce the same string when sorted.

```
"eat" → sort → "aet"   ← same key
"tea" → sort → "aet"   ← same key
"ate" → sort → "aet"   ← same key
"tan" → sort → "ant"   ← different group
```

```java
Map<String, List<String>> groups = new HashMap<>();
for (String word : words) {
    char[] chars = word.toCharArray();
    Arrays.sort(chars);                      // O(k log k)
    String key = new String(chars);
    groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
}
```

**Optimization:** Instead of sorting (O(k log k)), use a 26-element frequency array as key (O(k)):
```java
int[] freq = new int[26];
for (char c : word.toCharArray()) freq[c-'a']++;
String key = Arrays.toString(freq);  // "[1,0,0,0,1,0,...,1,0,0]"
```

---

### Problem 3: Longest Consecutive Sequence — O(n) time

**Brute force:** sort, then scan → O(n log n). We can do O(n) with HashSet.

**Key insight:** only start counting from the beginning of a sequence (where `n-1` is NOT in the set):

```java
Set<Integer> set = new HashSet<>(Arrays.asList(all nums));

for (int n : set) {
    if (!set.contains(n - 1)) {  // n is the START of a sequence
        int len = 1;
        while (set.contains(++n)) len++;
        maxLen = Math.max(maxLen, len);
    }
}
```

**For `[100,4,200,1,3,2]`:**
```
set = {1,2,3,4,100,200}
n=100: 99 not in set → start. 101 not in set. len=1
n=4:   3 IS in set → skip (not a start)
n=200: 199 not in set → start. 201 not in set. len=1
n=1:   0 not in set → start. 2,3,4 in set. len=4 ← max!
```

---

### Problem 4: Subarray Sum Equals K — O(n) time

**Key insight:** `prefixSum[j] - prefixSum[i] = k` → subarray `[i+1..j]` sums to k.

```java
Map<Integer, Integer> prefixCount = new HashMap<>();
prefixCount.put(0, 1);  // empty prefix has sum 0
int sum = 0, count = 0;

for (int num : nums) {
    sum += num;
    count += prefixCount.getOrDefault(sum - k, 0);
    prefixCount.merge(sum, 1, Integer::sum);
}
```

**Trace for `[1,2,3], k=3`:**
```
Start: prefixCount={0:1}, sum=0

num=1: sum=1, check sum-k=1-3=-2 → not in map → 0
       prefixCount={0:1, 1:1}
num=2: sum=3, check sum-k=3-3=0 → in map with count 1 → count=1
       prefixCount={0:1, 1:1, 3:1}
num=3: sum=6, check sum-k=6-3=3 → in map with count 1 → count=2
       prefixCount={0:1, 1:1, 3:1, 6:1}

Result: 2 subarrays ([1,2] and [3]) ✓
```

---

### Problem 5: Top K Frequent Elements — O(n log k) time

```java
Map<Integer, Integer> freq = new HashMap<>();
for (int n : nums) freq.merge(n, 1, Integer::sum);

// Min-heap of size k (by frequency)
PriorityQueue<Integer> heap =
    new PriorityQueue<>(Comparator.comparingInt(freq::get));

for (int num : freq.keySet()) {
    heap.offer(num);
    if (heap.size() > k) heap.poll();  // evict least frequent
}
```

**Why min-heap of size k?** We want to keep the k LARGEST frequencies. A min-heap of size k always evicts the smallest, leaving the k largest. O(n log k) vs O(n log n) for full sort.

---

### Problem 6: Word Pattern — O(n) bijection check

```java
Map<Character, String> charToWord = new HashMap<>();
Map<String, Character> wordToChar = new HashMap<>();

for (int i = 0; i < pattern.length(); i++) {
    char c = pattern.charAt(i); String w = words[i];

    if (charToWord.containsKey(c) && !charToWord.get(c).equals(w)) return false;
    if (wordToChar.containsKey(w) && wordToChar.get(w) != c) return false;

    charToWord.put(c, w);
    wordToChar.put(w, c);
}
```

**Why two maps?** One map catches `"a"→"dog"` and `"b"→"dog"` (same word for different chars — violates bijection). The second map catches `"a"→"dog"` and `"a"→"cat"` (same char maps to different words).

---

### Interview Complexity Cheat Sheet

| Problem | Time | Space | Key Technique |
|---------|------|-------|---------------|
| Two Sum | O(n) | O(n) | Complement lookup |
| Group Anagrams | O(nk log k) | O(nk) | Sorted key |
| Longest Consecutive | O(n) | O(n) | HashSet + start detection |
| Subarray Sum = K | O(n) | O(n) | Prefix sum map |
| Top K Frequent | O(n log k) | O(n) | Freq map + min-heap |
| Word Pattern | O(n) | O(n) | Bijection two maps |
| LRU Cache | O(1) all | O(n) | HashMap + DLL |
| Four Sum Count | O(n²) | O(n²) | Split into two pairs |

---

## Common Mistakes to Avoid

```java
// ❌ 1. Override equals() without hashCode()
class Point {
    int x, y;
    @Override
    public boolean equals(Object o) { return x==((Point)o).x && y==((Point)o).y; }
    // Missing hashCode! Two equal Points go to different buckets!
}
// ✅ Always generate both together (IDE: Generate → equals() and hashCode())

// ❌ 2. Using mutable key in HashMap
Map<int[], String> map = new HashMap<>();
int[] key = {1, 2};
map.put(key, "hello");
key[0] = 99;              // MUTATED KEY!
map.get(key);             // null! hash changed, bucket changed

// ✅ Use immutable keys: String, Integer, or make defensive copy

// ❌ 3. iterating and modifying simultaneously
for (String key : map.keySet()) {
    if (condition) map.remove(key);  // ConcurrentModificationException!
}
// ✅ use iterator
Iterator<Map.Entry<K,V>> it = map.entrySet().iterator();
while (it.hasNext()) {
    if (condition) it.remove();  // safe removal
}

// ❌ 4. Checking containsKey then get — two lookups
if (map.containsKey(k)) return map.get(k);  // two hash computations!
// ✅ One lookup
V val = map.get(k);
if (val != null) return val;

// ❌ 5. getOrDefault vs merge — wrong tool for counting
map.put(word, map.get(word) + 1);   // NullPointerException if key absent!
map.put(word, map.getOrDefault(word, 0) + 1);  // ✅ safe
map.merge(word, 1, Integer::sum);               // ✅ even cleaner

// ❌ 6. LinkedHashMap vs HashMap — wrong choice for LRU
HashMap<K,V> cache = new HashMap<>();  // no access-order tracking
// ✅ LinkedHashMap with accessOrder=true
LinkedHashMap<K,V> cache = new LinkedHashMap<>(16, 0.75f, true);
```

---

## The 6 Golden Rules

```
1. Override equals() AND hashCode() together — never one without the other
2. Use immutable keys — mutable keys that change after insertion break lookups
3. Prefer HashMap over Hashtable (legacy) and TreeMap (slower) for general use
4. Prefix sum + HashMap = O(n) solution for subarray sum problems
5. Two-map bijection = O(n) solution for pattern matching problems
6. HashMap + Doubly Linked List = O(1) LRU Cache (industry standard)
```
