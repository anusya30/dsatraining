# Scenario-Based Interview Questions
> 20 Real-World Use Cases · Covering Advanced OOP, Design Patterns, Algorithms, Data Structures & System Design
> Each question blends multiple topic areas — just like real engineering interviews

---

## How to Use This File
- Read the **scenario** to understand the business context
- Understand the **problem statement** before looking at hints
- Use **hints** only if you are stuck for more than 15 minutes
- Topics covered are tagged at the start of each question
- Solutions are in the accompanying Java files

---

## Question 1 — The Banking Account Hierarchy
**Topics:** Advanced OOP · SOLID (SRP, OCP, LSP) · Design Thinking

### Scenario
You are a senior engineer at **FinTrust Bank**. The bank offers three account types: `SavingsAccount`, `CurrentAccount`, and `FixedDepositAccount`. Each account can deposit, withdraw, and calculate interest differently. A junior developer wrote a single `BankAccount` class with `if-else` blocks checking account type everywhere. The code breaks every time a new account type is added.

### Problem Statement
1. Redesign the system using **OOP principles** — encapsulate all state, use abstraction to expose only behavior, and use polymorphism so the caller never checks account type.
2. Demonstrate **LSP** — any `BankAccount` subtype must be substitutable without breaking the caller.
3. Apply **OCP** — adding a `PremiumAccount` type must require zero changes to existing classes.
4. `SavingsAccount` cannot be overdrawn. `CurrentAccount` allows overdraft up to a limit. Ensure this constraint lives inside the right class (tell-don't-ask).
5. Calculate monthly interest for a list of mixed accounts without the caller knowing account types.

### Hints
- `abstract class BankAccount` with `abstract double calculateInterest()` and concrete `deposit()`/`withdraw()` with validation in the base class
- Override `withdraw()` in `SavingsAccount` to throw `InsufficientFundsException` if `balance < amount`
- Override `withdraw()` in `CurrentAccount` to allow overdraft up to `overdraftLimit`
- A single `List<BankAccount>` loop calling `account.calculateInterest()` proves OCP and LSP together
- `FixedDepositAccount.withdraw()` should throw `OperationNotSupportedException` — is this an LSP violation? Think carefully before you answer.

---

## Question 2 — The E-Commerce Order Pipeline
**Topics:** Creational Patterns (Builder, Factory Method) · Behavioral Patterns (Chain of Responsibility, Observer) · SOLID (DIP)

### Scenario
You are building the order placement system for **ShopStream**, a mid-sized e-commerce platform. An order has many optional fields: discount codes, gift wrapping, delivery instructions, multiple items, and payment methods. Orders go through validation → fraud check → inventory reservation → payment → confirmation. Every successful order must trigger email, SMS, and analytics updates.

### Problem Statement
1. Use the **Builder pattern** to construct an `Order` object. Required fields: `userId`, `items`. Optional: `discountCode`, `giftWrapping`, `deliveryNote`, `paymentMethod`. Ensure an invalid order (no items) cannot be built.
2. Use **Factory Method** to create the right `PaymentProcessor` (`CreditCardProcessor`, `UPIProcessor`, `WalletProcessor`) based on the payment method — the caller should never `new` a concrete processor.
3. Build the order validation pipeline as a **Chain of Responsibility**: `StockValidator → FraudValidator → PriceValidator`. Any handler can reject the order and stop the chain.
4. When an order is confirmed, notify three downstream systems using the **Observer pattern**: `EmailNotifier`, `SMSNotifier`, `AnalyticsTracker`. Adding a fourth notifier must require zero changes to `Order`.
5. The `OrderService` (high-level) must depend only on `PaymentProcessor` and `OrderEventPublisher` interfaces — never on concrete classes (DIP).

### Hints
- Builder: private constructor on `Order`, static `Order.Builder` inner class, `build()` validates required fields
- Factory Method: `abstract PaymentProcessor createProcessor()` in `PaymentProcessorFactory`, concrete factories per type
- Chain: each handler has a `setNext(handler)` and calls `next.handle(order)` only if its own check passes
- Observer: `Order` holds `List<OrderEventListener>`, calls `listener.onOrderConfirmed(event)` after payment
- DIP: `OrderService(PaymentProcessorFactory factory, OrderEventPublisher publisher)` — inject both interfaces

---

## Question 3 — The Ride-Sharing Driver Matcher
**Topics:** Structural Patterns (Adapter, Proxy) · Behavioral Patterns (Strategy, State) · Advanced OOP

### Scenario
**RideGo** integrates with two third-party mapping APIs — `HereMapsAPI` and `GoogleMapsAPI` — which have completely different method signatures. The app also has driver states: `Available → EnRoute → OnTrip → Completed`. The matching algorithm (nearest driver vs highest-rated driver) needs to be swappable at runtime based on city configuration.

### Problem Statement
1. Both mapping APIs need to satisfy a common `DistanceCalculator` interface used by the app. Write **Adapter** classes for each.
2. The `DriverLocationService` makes expensive API calls to fetch driver positions. Add a **Proxy** that caches responses for 30 seconds and logs every cache hit/miss.
3. Implement the driver lifecycle as a **State Machine** — illegal transitions (e.g., `Available → Completed`) must throw `IllegalStateTransitionException`.
4. Implement two **Strategy** classes for driver matching: `NearestDriverStrategy` and `HighestRatedDriverStrategy`. The `MatchingService` switches strategy based on a city config map at startup.
5. Combine: when a match is found via a strategy, the distance is calculated via the proxied adapter, and the driver's state transitions from `Available → EnRoute`.

### Hints
- Adapter: `class HereMapsAdapter implements DistanceCalculator { private HereMapsAPI api; ... }`
- Proxy: hold a `Map<String, CachedResult>` with timestamps, check age before delegating to real service
- State: each `DriverState` enum value overrides `transitionTo(DriverState next)` with allowed transitions
- Strategy interface: `Driver findBestMatch(Location pickup, List<Driver> availableDrivers)`
- In `MatchingService`, inject `Map<String, MatchingStrategy>` — key is city code, value is the strategy

---

## Question 4 — The Log Anomaly Detector
**Topics:** Sliding Window Pattern · Sliding Window + Hashing Hybrid · Complexity Deep Dive

### Scenario
**CloudWatch Systems** monitors server logs in real time. The platform needs to detect anomalies:
- More than `K` unique error codes in any window of `W` consecutive log lines
- The longest stretch of logs with no more than 2 distinct error severity levels
- Any substring of log codes that is an exact anagram of a known attack signature

### Problem Statement
1. Given a `String[] logs` where each entry is an error code, find all starting indices of windows of size `W` where the count of **distinct error codes equals exactly K**. Return the count.
2. Find the **longest contiguous subarray** of logs that contains at most 2 distinct severity levels (e.g., `"WARN"`, `"ERROR"`, `"INFO"`). Return the length.
3. Given a `String logStream` and a `String attackSignature`, find all **starting positions** where a permutation of `attackSignature` appears in `logStream`. This is the "find all anagrams" problem — solve it in O(n).
4. For question 1, prove why the naïve O(n × W) approach fails at 10M log lines and why the sliding window reduces it to O(n). Show operation counts.

### Hints
- Q1: `exactly K = atMost(K) - atMost(K-1)` — the classic sliding window count trick
- Q1: `atMost(k)`: expand right, shrink left when distinct count exceeds k, count += right - left + 1
- Q2: Variable window with a `HashMap<String, Integer>` frequency map — shrink left when `map.size() > 2`
- Q3: Fixed window of size `attackSignature.length()`, maintain two frequency arrays of size 26, compare arrays — `Arrays.equals` is O(26) = O(1)
- Q4: At 10M lines × window 1000 = 10 billion operations; sliding window is exactly 2 × 10M = 20M operations

---

## Question 5 — The Social Network Friend Suggester
**Topics:** Graph + DP · Multi-Pattern Integration · Advanced OOP

### Scenario
**ConnectMe** is a professional network. You need to: find the shortest connection path between two users (degrees of separation), suggest friends-of-friends, and detect user communities (clusters of mutually connected users). The social graph has 1M nodes and 50M edges.

### Problem Statement
1. Find the **shortest path (minimum hops)** between two users in an unweighted undirected graph. Return the path as a list of user IDs, not just the distance.
2. For a given user, suggest **top 5 people-you-may-know** — users reachable in exactly 2 hops who are not already direct connections. Order by number of mutual friends descending.
3. Find **all connected components** (friend communities) using Union-Find with path compression and union by rank. Return the size of the largest community.
4. Using DP on a graph: find the **maximum number of users** you can invite to an event such that every invitee knows at least one other invitee (maximum clique approximation on a tree — use tree DP).
5. The graph class must use **dependency injection** and program to interfaces — `GraphRepository`, `PathFinder`, `CommunityDetector` — never expose concrete implementations to the caller.

### Hints
- Q1: BFS from source, track `prev[]` map, reconstruct path by following `prev` from target back to source
- Q2: BFS to depth 2, exclude direct neighbors and self; for each 2-hop candidate, count common neighbors
- Q3: `int[] parent, rank` arrays; `find()` with path compression: `parent[x] = find(parent[x])`; `union()` attaches smaller rank tree under larger
- Q4: Root the tree, `dp[node][0]` = max invitees in subtree without `node`, `dp[node][1]` = max with `node`
- Q5: `interface PathFinder { List<Integer> findPath(int from, int to); }` — BFS and Dijkstra are two implementations

---

## Question 6 — The Real-Time Stock Ticker
**Topics:** Segment Trees · Two Pointer Pattern · Optimization Thinking

### Scenario
**TradeFloor Analytics** processes live stock price feeds. Traders query: maximum price in a date range, minimum price in a date range, and range sum for average calculation — thousands of times per second. Prices are also updated in real time.

### Problem Statement
1. Build a **Segment Tree** that supports: `rangeMax(l, r)`, `rangeMin(l, r)`, `rangeSum(l, r)`, and `pointUpdate(index, newPrice)` — all in O(log n).
2. Given a sorted array of timestamps and a price array, use **two pointers** to find all pairs of timestamps `(i, j)` where `price[j] - price[i] >= targetGain` and `timestamp[j] - timestamp[i] <= maxHoldingDays`. Return count.
3. A trader wants the **largest price drop** (max of `price[i] - price[j]` where `i < j`) in a given range. Solve this using the segment tree — each node stores `max`, `min`, and `maxDrop` for its range. Merging two nodes requires careful logic.
4. Prove that rebuilding prefix sums on every update is O(n) per update and O(1) per query, while the segment tree gives O(log n) for both. At 10K updates/sec × 1M prices, show the operations saved per second.

### Hints
- Q1: `tree[node] = merge(tree[2n+1], tree[2n+2])` where merge computes max, min, sum; size the array as `4 * n`
- Q2: Fix left pointer at `i`, advance `j` as far as `timestamp[j]-timestamp[i] <= maxDays`, count all valid `j`s; advance `i` when no more valid `j`
- Q3: `node.maxDrop = max(left.maxDrop, right.maxDrop, left.max - right.min)` — the largest drop can span the left-right boundary
- Q4: Prefix sum rebuild = 10K × 1M = 10 billion ops/sec; segment tree = 10K × 20 = 200K update ops/sec + 200K query ops/sec = tiny fraction

---

## Question 7 — The Autocomplete Search Engine
**Topics:** Advanced String Algorithms (Trie + KMP) · Sliding Window + Hashing · Multi-Pattern Integration

### Scenario
**QuickSearch** powers the search bar for a content platform with 50M articles. It needs: prefix-based autocomplete, fuzzy search detecting near-duplicate queries, and spam detection finding repeated attack patterns in query streams.

### Problem Statement
1. Build a **Trie** that supports: `insert(word)`, `search(word)`, `startsWith(prefix)`, and `topKSuggestions(prefix, k)` — return the k most frequently searched completions for a given prefix.
2. Use **Rabin-Karp rolling hash** to detect if any query in the last 100 queries is a substring of the current query (recent query plagiarism check). Solve in O(n × m) average case with O(1) per roll.
3. Use **KMP** to find all positions where a known spam pattern appears in a concatenated stream of recent queries. Explain why KMP never re-examines a character.
4. Using a **sliding window of 1000 queries**, find the query string that appears most frequently using a `HashMap`. When the window slides, decrement the outgoing query's count — demonstrate O(1) per slide.
5. Combine: `insert` into Trie, simultaneously check KMP against spam patterns, and count frequency in sliding window — all in a single `processQuery(String query)` method.

### Hints
- Q1 Trie node: `Map<Character, TrieNode> children`, `int frequency`, `boolean isEndOfWord`; DFS from prefix node collecting all words + frequencies, return top-k via `PriorityQueue`
- Q2 Rabin-Karp: `hash = (hash * BASE + char) % MOD`; rolling remove: `hash = (hash - oldChar * power % MOD + MOD) % MOD`
- Q3 KMP: build `lps[]` (longest proper prefix that is also suffix), use it to skip ahead on mismatch — O(n+m) total
- Q4: `Map<String, Integer>` + a `Queue<String>` to track window order; `map.merge(outgoing, -1, Integer::sum)` + remove if 0
- Q5: All three structures are independent — call them sequentially in `processQuery`; each is O(L) where L = query length

---

## Question 8 — The Task Scheduler with Dependencies
**Topics:** Graph + DP · Behavioral Patterns (Command + Template Method) · Advanced OOP

### Scenario
**DevFlow CI/CD** runs build pipelines where tasks have dependencies (task B cannot start until A completes), estimated durations, and retry logic. The system must find the critical path (longest dependency chain), detect circular dependencies, and support undo of a scheduled batch.

### Problem Statement
1. Model the pipeline as a **DAG**. Detect circular dependencies using DFS (colour marking: white/grey/black). If a cycle is found, report which tasks form it.
2. Find the **critical path** (longest path in the DAG weighted by task duration) using topological sort + DP. Return the sequence of task names and total duration.
3. Implement each task as a **Command** with `execute()` and `undo()`. A `BatchScheduler` (Invoker) runs tasks in topological order and can `undoLastBatch()`.
4. Use **Template Method** for `AbstractTask`: the algorithm is `validate() → prepare() → execute() → cleanup()`. Subclasses override only `prepare()` and `execute()` — `validate()` and `cleanup()` are fixed.
5. The `CriticalPathFinder` and `CycleDetector` must be separate classes implementing `GraphAnalyzer` interface — demonstrate SRP and DIP together.

### Hints
- Q1 DFS cycle: `color[node] = GREY` on entry, `BLACK` on exit; if you visit a GREY node → cycle; reconstruct cycle by following the DFS path back
- Q2: Topological sort → then `dp[node] = max(dp[predecessor] + duration[predecessor])` for all predecessors
- Q3: `BatchScheduler` maintains a `Deque<Command>` history; `undoLastBatch()` pops and calls `command.undo()` in reverse
- Q4: `AbstractTask.run()` calls `validate(); prepare(); execute(); cleanup();` — `final` keyword prevents subclasses from overriding `run()`
- Q5: `interface GraphAnalyzer { AnalysisResult analyze(Graph g); }` — both `CycleDetector` and `CriticalPathFinder` implement it

---

## Question 9 — The Memory-Efficient Bit Permissions System
**Topics:** Bit Manipulation Fundamentals · Advanced OOP · Optimization Thinking

### Scenario
**SecureVault** manages file permissions for 10 million files, each with up to 32 permission flags (READ, WRITE, EXECUTE, SHARE, DELETE, AUDIT, etc.). Storing a `Set<Permission>` per file consumes too much memory. The team needs a compact representation and fast permission checks.

### Problem Statement
1. Represent all permissions as **bit flags** in a single `int`. Implement `grantPermission(int permissions, Permission p)`, `revokePermission(int permissions, Permission p)`, `hasPermission(int permissions, Permission p)`, and `togglePermission(int permissions, Permission p)`.
2. Given two permission sets as integers, compute: their **union** (either set has the permission), **intersection** (both sets have it), and **difference** (permissions in A but not in B).
3. Implement `countPermissions(int permissions)` using **Brian Kernighan's algorithm** (`n & (n-1)` clears the lowest set bit) — prove it runs in O(number of set bits) not O(32).
4. A file has a **permission history** stored as a list of `int` snapshots. Find the permission flag that appeared in the **most snapshots** — use bit manipulation to extract and count each bit position across all snapshots.
5. Prove the memory saving: `Set<Permission>` for 10M files with avg 8 permissions each = how many bytes? `int` bitmask for 10M files = how many bytes? Show the ratio.

### Hints
- `grant`: `permissions | (1 << p.bit)`
- `revoke`: `permissions & ~(1 << p.bit)`
- `has`: `(permissions & (1 << p.bit)) != 0`
- `toggle`: `permissions ^ (1 << p.bit)`
- Union: `a | b`, Intersection: `a & b`, Difference: `a & ~b`
- Brian Kernighan: each `n = n & (n-1)` iteration removes exactly one set bit; loop runs exactly `popcount(n)` times
- Memory: `Set<Permission>` = each `Permission` enum reference = 8 bytes (64-bit ref) × 8 permissions × 10M = 640 MB; `int[]` = 4 bytes × 10M = 40 MB — 16× saving

---

## Question 10 — The Warehouse Robot Path Optimizer
**Topics:** Graph + DP · Two Pointer · Optimization Thinking · Multi-Pattern Integration

### Scenario
**AutoStore** runs a warehouse with robots on a grid. Robots fetch items from shelves and return to packing stations. The grid has obstacles. Robots need shortest paths, and the warehouse manager wants to know the minimum cost to visit all priority shelves (TSP variant) and detect if two robots will collide.

### Problem Statement
1. Find the **shortest path** from robot start position to target shelf on a 2D grid with obstacles using BFS. Return the path as a list of coordinates.
2. Find the **minimum cost to visit all K priority shelves** (K ≤ 15) starting from the depot, using **bitmask DP** (TSP). State: `dp[mask][node]` = min cost to have visited exactly the shelves in `mask`, currently at `node`.
3. Given two robots' paths as lists of `(x, y, time)` triplets, use **two pointers** to detect if they occupy the same cell at the same time step (collision detection). Return the first collision point if any.
4. Given a grid where each cell has a traversal cost, find the **minimum cost path** from top-left to bottom-right using DP (can only move right or down). Then answer range queries: "what is the min cost for paths that pass through cell (r,c)?" — use a 2D prefix DP.
5. Combine: BFS to precompute pairwise shortest distances between shelves, then bitmask DP uses those precomputed distances.

### Hints
- Q1: BFS with `Queue<int[]>`, visited boolean grid, `prev[][]` for path reconstruction
- Q2: Initialize `dp[1<<i][i] = dist[depot][shelf[i]]`; transition: `dp[mask|(1<<j)][j] = min(dp[mask][i] + dist[i][j])`; final answer: min over all `i` of `dp[fullMask][i] + dist[i][depot]`
- Q3: Two pointers on sorted-by-time path lists; if `time[i] == time[j]` and positions match → collision; advance the pointer with smaller time
- Q4: `dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])`; for query through `(r,c)`: `topLeft[r][c] + bottomRight[r][c] - grid[r][c]` where each is a separate DP table
- Q5: Run BFS from each shelf as source → O(K × V) preprocessing; then bitmask DP is O(2^K × K²)

---

## Question 11 — The Decorator-Based Data Pipeline
**Topics:** Structural Patterns (Decorator, Composite) · Creational Patterns (Prototype) · Behavioral Patterns (Strategy)

### Scenario
**DataFusion** processes ETL pipelines where raw data passes through a series of transformations: encryption, compression, deduplication, schema validation, and audit logging. The order of transformations varies by data source. New transformations must be addable without touching existing code.

### Problem Statement
1. Implement a **Decorator chain** where each transformation wraps the previous: `AuditDecorator(CompressionDecorator(EncryptionDecorator(RawDataProcessor)))`. Each decorator calls `super.process(data)` then adds its behaviour.
2. A pipeline **configuration** (which decorators, in what order) can be saved as a **Prototype** — clone it to create a new pipeline with the same configuration for a different data source without re-specifying every step.
3. Build a **Composite** pipeline where a `PipelineGroup` contains multiple sub-pipelines and processes data through all of them, treating a single pipeline and a group uniformly through a `Pipeline` interface.
4. The **deduplication strategy** (hash-based vs bloom-filter-based vs sorted-merge) is injected as a **Strategy** and can be switched at runtime without modifying the `DeduplicationDecorator`.
5. Prove with complexity analysis: if deduplication uses a `HashSet`, it's O(1) per record but O(n) space; bloom filter is O(1) time and O(m) space (where m << n) but has a false positive rate. Show the trade-off equation.

### Hints
- Decorator: `abstract class PipelineDecorator implements DataProcessor { protected final DataProcessor wrapped; }`
- Each concrete decorator: `@Override public byte[] process(byte[] data) { byte[] result = wrapped.process(data); return myTransform(result); }`
- Prototype: `PipelineConfig implements Cloneable { @Override public PipelineConfig clone() { return deepCopy(); } }`
- Composite: `PipelineGroup implements Pipeline { List<Pipeline> children; void process(data) { children.forEach(p -> p.process(data)); } }`
- Bloom filter FP rate: `(1 - e^(-kn/m))^k` where k=hash functions, n=elements, m=bits; show at n=1M, m=10M bits → ~0.8% FP rate vs HashSet needing ~32MB

---

## Question 12 — The Recommendation Engine
**Topics:** Advanced Tree Problems · Graph + DP · Sliding Window + Hashing

### Scenario
**StreamBox** builds a content recommendation engine. User preferences are stored in a trie-like category tree. Collaborative filtering uses a user-item interaction graph. Real-time trending content is tracked in a sliding window.

### Problem Statement
1. Given a **binary tree of content categories** where each node's value is a relevance score, find the **path from root to leaf with maximum total score** (max root-to-leaf path sum). Return both the score and the path.
2. In the category tree, find the **lowest common ancestor** of two categories that a user has interacted with — this common ancestor is the suggested broad topic. Solve for a general binary tree (no BST property).
3. The **diameter of the category tree** (longest path between any two nodes) defines the maximum "topic distance" between two pieces of content. Compute it in O(n) using a single DFS with a global max.
4. Using the **sliding window of last 1000 user actions**, find the content category that appears most frequently using a `LinkedHashMap` maintaining insertion order. Slide the window in O(1) per action.
5. Model the user-item graph: users and content are nodes, interactions are weighted edges. Use **DP on the graph** to find the most influential user — the one whose removal reduces total reachability the most (approximate with betweenness via repeated BFS).

### Hints
- Q1: `maxPathDFS(node)` returns `(score, path)` tuple; at each node, compare left-path-total vs right-path-total, take max
- Q2: LCA recursive: `if root == p or root == q: return root`; recurse left and right; if both return non-null → root is LCA
- Q3: `int[] maxDiameter = {0}; int dfsDepth(node) { left = dfsDepth(node.left); right = dfsDepth(node.right); maxDiameter[0] = max(maxDiameter[0], left+right); return 1 + max(left,right); }`
- Q4: Fixed-size window using `Queue<String>` + `HashMap<String, Integer>`; on slide: remove head from map, add new tail
- Q5: Run BFS from every node → `O(V × (V+E))`; removal impact of user u = sum of `reachable(v)` differences; approximate by comparing BFS reach before/after removing u

---

## Question 13 — The Fraud Detection System
**Topics:** Sliding Window · Bit Manipulation · Hashing Hybrid · Optimization Thinking

### Scenario
**PayGuard** detects fraudulent transactions in real time. Fraud signals include: too many transactions in a short time window, the same merchant appearing too frequently, transaction amounts that together form a suspicious sum, and device fingerprints that partially match known fraud devices.

### Problem Statement
1. Given a stream of transactions with timestamps, use a **sliding window** to flag any user who makes more than `K` transactions within any `W`-second window. Return the first timestamp that triggers the flag.
2. Within a sliding window of 500 transactions, find the **merchant that appears more than `W/3` times** (majority-like element in a window). Use a frequency map and update in O(1) per slide.
3. Given transaction amounts as integers, use **bit manipulation** to find two amounts that XOR to exactly `targetSignal` (a known fraud fingerprint value). Do this in O(n) using a `HashSet`.
4. Given device fingerprints as 32-bit integers, find all pairs where the **Hamming distance** (number of differing bits) is exactly 1 — these are likely the same device with one bit flipped. Solve in O(n × 32) instead of O(n²).
5. Prove the time complexity improvement: naïve O(n²) pair check at 1M fingerprints = 10¹² operations; the O(n × 32) approach = 32M operations. At 10⁹ operations/sec CPU, show wall-clock time difference.

### Hints
- Q1: Sliding window on sorted timestamp list; `Deque<Long>` stores timestamps; pop front while `timestamps[right] - timestamps[front] > W`; check `deque.size() > K`
- Q2: `HashMap<String, Integer>` frequency; on slide out: `map.merge(outgoing, -1, Integer::sum); if map.get(outgoing)==0 map.remove(outgoing)`; find max-entry each step in O(1) with a separate `maxEntry` variable
- Q3: For each `amount`, check if `targetSignal ^ amount` exists in the set; add `amount` to set first
- Q4: For each fingerprint `f`, generate all 32 possible 1-bit-flip variants `f ^ (1 << i)` for i in 0..31; check if each variant exists in a `HashSet<Integer>`
- Q5: Naïve: 10¹² / 10⁹ = 1000 seconds; O(n×32): 32×10⁶ / 10⁹ = 0.032 seconds — 31,250× faster

---

## Question 14 — The Live Leaderboard
**Topics:** Segment Trees · Advanced OOP · Creational Patterns (Singleton) · Behavioral Patterns (Observer)

### Scenario
**GameArena** runs a live multiplayer leaderboard with 1 million players. Scores update thousands of times per second. The leaderboard needs: rank of any player, top-K players, count of players in a score range, and live push notifications to players when they move up or down in rank.

### Problem Statement
1. Design a **Segment Tree on score ranges** (scores 0–10⁶) where each node stores the **count of players** in that score range. Support `addScore(playerId, score)`, `removeScore(playerId, oldScore)`, and `countPlayersInRange(lo, hi)` — all O(log maxScore).
2. Implement `getRank(playerId)` — the rank of a player with score `s` is `1 + count of players with score > s` — using the segment tree in O(log maxScore).
3. Implement `getTopK(k)` — find the top K players by score using the segment tree: find the smallest score `s*` such that `countPlayersInRange(s*, maxScore) == k`, then collect those players.
4. The leaderboard service must be a **Singleton** (only one instance manages the global state). Use the thread-safe initialization-on-demand holder pattern.
5. When a player's rank changes by more than 10 positions, notify them via the **Observer pattern**. The `LeaderboardService` publishes `RankChangeEvent`; `PushNotificationObserver` and `EmailObserver` subscribe to it.

### Hints
- Q1: Segment tree over score axis 0..MAX_SCORE; `update(score, +1)` for add, `update(score, -1)` for remove; `query(lo, hi)` returns count
- Q2: `getRank(s) = 1 + query(s+1, MAX_SCORE)` — count all players with strictly higher score
- Q3: Binary search on the segment tree: at each node, if `rightChildCount >= k` go right else `k -= rightChildCount` go left; this finds the k-th largest score in O(log maxScore)
- Q4: `private static class Holder { static final LeaderboardService INSTANCE = new LeaderboardService(); }` — JVM class loading guarantees thread safety
- Q5: After every `addScore`, compare `newRank` vs `oldRank`; if `|newRank - oldRank| > 10` call `eventBus.publish(new RankChangeEvent(playerId, oldRank, newRank))`

---

## Question 15 — The Versioned Configuration Store
**Topics:** Advanced Tree Problems (Persistent/Immutable Tree) · Behavioral Patterns (Memento, Command) · SOLID

### Scenario
**ConfigHub** manages application configurations with full version history. Every change creates a new version. Operators can roll back to any previous version, diff two versions, and audit what changed between version 5 and version 12.

### Problem Statement
1. Store each configuration version as an **immutable tree node** (each change creates new nodes along the path, reusing unchanged subtrees — persistent data structure). Implement `set(key, value)` that returns a new root while preserving all old roots.
2. Implement `get(version, key)` that retrieves the value of `key` at a given `version` in O(log n) where n is the number of keys.
3. Use **Command pattern** — each `SetConfigCommand` has `execute()` (returns new version root) and `undo()` (returns previous version root). A `VersionHistory` stack tracks all commands.
4. Use **Memento** to snapshot the entire configuration at a checkpoint, allowing one-step restore of the full state (vs Command which replays individual operations).
5. Implement `diff(v1, v2)` — walk both version trees simultaneously and collect keys whose values differ between version `v1` and `v2`. Return as `Map<String, Pair<String,String>>` (key → old value, new value).

### Hints
- Q1: `TrieNode { char c; Map<Character, TrieNode> children; String value; }` — on `set(key,value)`, copy nodes along the path, create new nodes, leave unchanged branches shared
- Q2: Follow the version root for the given version number → traverse the persistent trie with the key characters → O(key_length) = O(log n) with bounded key length
- Q3: `interface ConfigCommand { TrieNode execute(TrieNode currentRoot); TrieNode undo(TrieNode currentRoot); }` — `VersionHistory` is a `Deque<ConfigCommand>`
- Q4: `ConfigSnapshot { Map<String, String> flatConfig; Instant timestamp; }` — deep copy of the flattened key-value map at checkpoint time
- Q5: Recursive `diff(node1, node2, prefix, result)`: if both have a value and they differ → add to result; recurse into children present in either node

---

## Question 16 — The Airline Seat Booking Engine
**Topics:** Greedy + DP · Graph Algorithms · Structural Patterns (Facade) · Two Pointer

### Scenario
**SkyBook** manages flight seat assignments. Passengers have preferences (window, aisle, adjacent seats for groups). The system must maximize revenue (higher-tier passengers get preferred seats), find minimum-cost upgrade paths, and handle group bookings atomically.

### Problem Statement
1. Given `n` seats and `m` passengers each with a preference score for each seat, use a **greedy assignment** (sort passengers by tier descending, assign best available preferred seat) and prove it maximizes total preference score among all greedy orderings.
2. Model flights as a graph where edges are connecting flights with costs. Find the **cheapest upgrade path** from economy to business for any origin-destination pair using Dijkstra with a state `(currentFlight, currentClass)`.
3. For group bookings of size G, use **two pointers** on the sorted seat array to find the first window of G consecutive available seats. If no consecutive block exists, find G seats minimizing the maximum gap between any two adjacent selected seats.
4. Build a `BookingFacade` that hides the complexity of: `SeatInventoryService`, `PricingService`, `PaymentService`, and `NotificationService` — the client calls one method `bookSeats(BookingRequest)` and gets a `BookingConfirmation`.
5. Use **DP** to solve: given K flight segments with variable delays, find the assignment of passengers to segments that minimizes the maximum wait time across all passengers (minimax DP).

### Hints
- Q1: Sort passengers by tier descending; for each passenger, iterate their preference list and take first available → greedy exchange argument proves optimality for same-tier passengers
- Q2: Dijkstra state: `(node, classLevel)`; edge `(flight_i → flight_j, upgradeClass, cost)`; `dist[node][class] = minCost`
- Q3: Two pointer: `left=0, right=0`; advance `right` while `seats[right]-seats[right-1]==1`; when `right-left+1 == G` → found; if no block: sliding window minimum-max-gap using sorted available seats
- Q4: `BookingFacade(SeatInventory inv, Pricing price, Payment pay, Notification notif)` — one `bookSeats()` method orchestrates all four services
- Q5: `dp[i][j]` = min max wait when assigning first `i` passengers to first `j` segments; `dp[i][j] = min(dp[i-1][j-1] if passenger_i fits segment_j, dp[i][j-1])`; binary search on answer + greedy check is O(n log(maxWait))

---

## Question 17 — The Distributed Cache Eviction Policy
**Topics:** Advanced OOP · Behavioral Patterns (Strategy, Template Method) · Complexity Deep Dive · Bit Manipulation

### Scenario
**NimbusCache** implements multiple eviction policies for a distributed cache: LRU (Least Recently Used), LFU (Least Frequently Used), and ARC (Adaptive Replacement Cache). Each policy must support `get(key)` and `put(key, value)` in O(1).

### Problem Statement
1. Implement **LRU Cache** using a `HashMap` + `DoublyLinkedList`. `get` moves the node to the head (MRU end). `put` evicts the tail (LRU end) when capacity is reached. Both operations O(1).
2. Implement **LFU Cache** — the key with the lowest access frequency is evicted; on frequency tie, the LRU among them is evicted. Use a `HashMap<key, Node>` + `HashMap<freq, DoublyLinkedList>` + a `minFreq` tracker. O(1) for both operations.
3. Use the **Strategy pattern** to make the cache eviction policy swappable at runtime: `CacheService` holds an `EvictionPolicy` reference and calls `policy.onAccess(key)`, `policy.onEvict()` — switching from LRU to LFU requires one line of code change.
4. Use **Template Method** for `AbstractCachePolicy`: `accessKey(key)` calls `recordAccess(key)` (abstract) then `evictIfNeeded()` (calls abstract `selectEvictionCandidate()`). Concrete classes implement the abstract steps.
5. Prove that a naïve LRU using a `LinkedList.remove(key)` is O(n) per operation (must scan the list). Show that HashMap + DoublyLinkedList achieves O(1) by explaining that the HashMap stores a direct pointer to the node — no scan needed.

### Hints
- Q1: `HashMap<Integer, DLLNode>` maps key → node; DLL has dummy head/tail; `moveToHead(node)`: `removeNode(node)` then `addToHead(node)` — all pointer operations, O(1)
- Q2: `HashMap<Integer, Integer> keyToFreq`; `HashMap<Integer, LinkedHashSet<Integer>> freqToKeys`; on access: increment freq, move key from old freq bucket to new; on evict: remove from `freqToKeys.get(minFreq)` (LRU within bucket via LinkedHashSet order)
- Q3: `interface EvictionPolicy { void onAccess(int key); int evict(); }` — `CacheService(EvictionPolicy policy)` calls the interface; swap policy via `setPolicy(newPolicy)`
- Q4: `abstract class AbstractCachePolicy { final void accessKey(int key) { recordAccess(key); evictIfNeeded(); } abstract void recordAccess(int key); abstract int selectEvictionCandidate(); }`
- Q5: `LinkedList.remove(Object o)` iterates from head: O(n) average; `HashMap.get(key)` → direct reference to `DLLNode` → `node.prev.next = node.next` → O(1) — the map pointer is the key insight

---

## Question 18 — The Code Plagiarism Detector
**Topics:** Advanced String Algorithms (Rolling Hash + Suffix Array) · Sliding Window + Hashing · Optimization Thinking

### Scenario
**CodeGuard** checks student submissions for plagiarism. Given thousands of code files, it must: find the longest common substring between any two submissions, detect all submissions that share a block of 50+ tokens, and flag submissions that are permutations of each other.

### Problem Statement
1. Use **Rabin-Karp rolling hash** to find all pairs of submissions that share any common substring of length ≥ 50 tokens. Hash every 50-token window in every submission, store in a `HashMap<Long, List<SubmissionId>>`. Collisions in the same bucket = potential plagiarism.
2. Build a **Suffix Array** for a concatenated string of two submissions (separated by a sentinel character). The **Longest Common Extension** between suffixes that cross the sentinel gives the longest common substring.
3. Two submissions are **permutations of each other** if and only if their sorted token arrays are identical. Prove this and implement it using `Arrays.sort` + `Arrays.equals` in O(n log n).
4. Using a **sliding window of 50 tokens** across a submission, detect if the submission is self-plagiarising (same 50-token block appears twice). Store hashes in a `HashSet` — second occurrence = self-copy. O(n) total.
5. Given that hashing has O(1) average lookup but O(n) worst case (all keys hash to same bucket), explain when to use **polynomial rolling hash with double hashing** to reduce collision probability below 1/10⁹.

### Hints
- Q1: For each submission, slide a window of 50 tokens computing hash incrementally; store `(hash → [submissionId, position])` in global map; any bucket with 2+ different submission IDs = plagiarism candidate; verify with string comparison to eliminate hash collision false positives
- Q2: Concatenate `A + '#' + B`; build suffix array (sort all suffixes); scan adjacent suffixes where one comes from A and one from B; LCP between them = common substring length
- Q3: Sort both token arrays → O(n log n); if `Arrays.equals(sortedA, sortedB)` they are permutations; this is exact because two arrays are permutations iff they have the same multiset of elements iff they are equal after sorting
- Q4: Fixed window of 50: maintain rolling hash; `Set<Long> seen`; if `seen.contains(hash)` → potential duplicate (verify); `seen.add(hash)`
- Q5: Single hash collision probability ≈ 1/MOD ≈ 1/10⁹; double hash uses two independent hash functions → collision requires both to collide → probability ≈ 1/10¹⁸; cost: two hash computations vs one — constant factor overhead, same O(n) complexity

---

## Question 19 — The Hospital Appointment Scheduler
**Topics:** Greedy Algorithms · Two Pointer · Segment Trees · Advanced OOP (Immutability)

### Scenario
**MediBook** schedules doctor appointments. Doctors have available time slots. Patients have priority levels. The system must maximize the number of patients seen, minimise idle time between appointments, and answer "how many slots are available between time A and B?" in O(log n).

### Problem Statement
1. Given a list of `[start, end]` appointment intervals, use a **greedy algorithm** (sort by end time) to select the maximum number of non-overlapping appointments a doctor can take in a day. Return the selected appointments.
2. Use **two pointers** on a sorted patient list and sorted slot list to greedily assign the smallest-fit slot to each patient based on their minimum required duration — similar to the "assign cookies" problem.
3. Build a **Segment Tree on the time axis** (minutes 0–1440 for a 24-hour day) where each node stores the count of available slots in that time range. Support `bookSlot(start, end)` (range update: decrement) and `availableCount(start, end)` (range query).
4. An `Appointment` is an **immutable value object**: once created, its `patientId`, `doctorId`, `start`, and `end` cannot change. Cancellation creates a new availability record rather than mutating the original appointment.
5. Combine: greedy maximum scheduling (Q1) runs first to select appointments, then the segment tree (Q3) is updated for each booked appointment, and the two-pointer matcher (Q2) assigns patients to remaining slots.

### Hints
- Q1: `Arrays.sort(appointments, Comparator.comparingInt(a -> a[1]))`; maintain `lastEnd = -1`; for each appointment: if `start >= lastEnd` → select it, `lastEnd = end`
- Q2: Sort patients by required duration ascending, sort slots by duration ascending; two pointers: for each patient advance slot pointer until `slot.duration >= patient.requiredDuration`; assign and advance both
- Q3: Segment tree over [0, 1440]; `bookSlot(start, end)` → range update decrements count in [start, end]; lazy propagation for range update; `availableCount(a, b)` → range query
- Q4: `final class Appointment { final String patientId; final String doctorId; final int start; final int end; }` — no setters; `cancel()` method returns a new `AvailableSlot` record, doesn't modify `Appointment`
- Q5: Flow: `List<Appointment> selected = greedySchedule(requests)`; for each selected: `segTree.bookSlot(a.start, a.end)`; then `remainingSlots = getAvailableSlots()`; `assignments = twoPointerAssign(waitingPatients, remainingSlots)`

---

## Question 20 — The Compiler Symbol Table
**Topics:** Multi-Pattern Integration · Advanced String · Creational Patterns (Prototype) · Graph + DP · Complexity Deep Dive

### Scenario
**TinyLang** is a toy programming language compiler. The symbol table tracks variable declarations across nested scopes. The compiler needs: scope-aware variable lookup, detection of forward references and circular dependencies between functions, generation of all valid topological orderings for independent functions, and deep-cloning the symbol table for template function instantiation.

### Problem Statement
1. Implement a **scope-aware symbol table** as a stack of `HashMap<String, Symbol>` — `enterScope()` pushes a new map, `exitScope()` pops it, `lookup(name)` searches from top to bottom of the stack. O(depth) lookup where depth = nesting level.
2. Model function call dependencies as a **directed graph**. Detect **circular dependencies** (recursive calls without a base case guard) using DFS cycle detection. Report the cycle as a list of function names.
3. If the call graph is a DAG, find **all valid topological orderings** using backtracking — at each step, choose any node with in-degree 0, recurse, then restore. This generates all valid compilation orders.
4. For **template function instantiation**, the compiler deep-clones the symbol table using the **Prototype pattern** — `SymbolTable.clone()` must deep-copy all scope frames so the instantiated function's symbols don't pollute the original.
5. Prove the complexity of Q3: the number of valid topological orderings of a DAG can be exponential (a DAG of n independent nodes has n! orderings); backtracking generates all of them — this is unavoidable for the general case. Show a 5-node independent DAG has 120 orderings.

### Hints
- Q1: `Deque<Map<String, Symbol>> scopeStack`; `lookup`: iterate from `scopeStack.peekFirst()` down; `declare(name, sym)`: `scopeStack.peekFirst().put(name, sym)`; throw `DuplicateDeclarationException` if already in current scope
- Q2: DFS with `state[node]` ∈ {UNVISITED, IN_STACK, DONE}; if you reach an `IN_STACK` node → cycle; reconstruct cycle by tracing back the DFS path
- Q3: `void allTopOrders(int[] inDegree, boolean[] visited, List<Integer> current, List<List<Integer>> result)`; find all nodes with `inDegree[i]==0 && !visited[i]`, choose one, reduce neighbours' in-degrees, recurse, then restore (backtrack)
- Q4: `class SymbolTable implements Cloneable { @Override public SymbolTable clone() { SymbolTable copy = new SymbolTable(); for (Map<String,Symbol> frame : this.scopeStack) copy.scopeStack.addLast(new HashMap<>(frame)); return copy; } }`
- Q5: n=5 fully independent nodes (no edges): every permutation is valid → 5! = 120 orderings; backtracking visits each leaf of the recursion tree exactly once → Ω(n!) time is unavoidable; contrast with single-ordering topological sort = O(V+E)

---

## Topic Coverage Matrix

| Q# | OOP | SOLID | Patterns | Sliding Window | Two Ptr | Bit Manip | Seg Tree | Graph+DP | Tree | String | Hashing | Greedy | Optimization |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | ✓ | ✓ | | | | | | | | | | | |
| 2 | ✓ | ✓ | ✓ Creational+Behavioral | | | | | | | | | | |
| 3 | ✓ | | ✓ Structural+Behavioral | | | | | | | | | | |
| 4 | | | | ✓ | | | | | | | ✓ | | ✓ |
| 5 | ✓ | ✓ | | | | | | ✓ | | | | | |
| 6 | | | | | ✓ | | ✓ | | | | | | ✓ |
| 7 | | | | ✓ | | | | | | ✓ | ✓ | | |
| 8 | ✓ | ✓ | ✓ Behavioral | | | | | ✓ | | | | | |
| 9 | ✓ | | | | | ✓ | | | | | | | ✓ |
| 10 | | | | | ✓ | | | ✓ | | | | | ✓ |
| 11 | ✓ | ✓ | ✓ Structural+Creational | | | | | | | | | | ✓ |
| 12 | | | | ✓ | | | | ✓ | ✓ | | ✓ | | |
| 13 | | | | ✓ | | ✓ | | | | | ✓ | | ✓ |
| 14 | ✓ | | ✓ Creational+Behavioral | | | | ✓ | | | | | | |
| 15 | ✓ | ✓ | ✓ Behavioral | | | | | | ✓ | | | | |
| 16 | | | ✓ Structural | | ✓ | | | ✓ | | | | ✓ | |
| 17 | ✓ | | ✓ Behavioral | | | ✓ | | | | | | | ✓ |
| 18 | | | | ✓ | | | | | | ✓ | ✓ | | ✓ |
| 19 | ✓ | | | | ✓ | | ✓ | | | | | ✓ | |
| 20 | ✓ | | ✓ Creational | | | | | ✓ | | ✓ | | | ✓ |
