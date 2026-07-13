# Java Mastery Program — 15-Week Curriculum
> Hands-On Training · 3.5–4 Hours Per Session · ~58 Hours Total
> 7 Phases · From Core Foundations to Enterprise Certification

---

## Program Overview

| Phase | Weeks | Focus Area | Sessions |
|---|---|---|---|
| Phase 1 | 1–3 | Core Java & Foundations | 3 |
| Phase 2 | 4–7 | Data Structures | 4 |
| Phase 3 | 8 | Searching & Sorting | 1 |
| Phase 4 | 9–11 | Algorithmic Thinking | 3 |
| Phase 5 | 12–13 | Architecture & Design | 2 |
| Phase 6 | 14 | Interview & Production Readiness | 1 |
| Phase 7 | 15 | Final Certification | 1 |

**Total:** 15 weeks · ~58 hours of hands-on training

---

## Phase 1 — Core Java & Foundations
> Weeks 1–3 · ~12 hours

---

### Week 1 — OOP Fundamentals + Advanced OOP + Complexity
**Duration:** 4 hours

#### Topics
- **OOP Fundamentals** — Encapsulation, Abstraction, Inheritance, Polymorphism
- **Advanced OOP** — Interfaces vs Abstract classes, Composition over Inheritance, Covariant return types, Sealed classes (Java 17+), Records (Java 16+)
- **SOLID Principles** — SRP, OCP, LSP, ISP, DIP with violation demos and correct patterns
- **Complexity Analysis** — Big-O notation, time vs space trade-offs, best/average/worst case

#### Hands-On Exercises
- Build a `Shape` hierarchy demonstrating runtime polymorphism
- Refactor a violating class to satisfy each SOLID principle
- Analyze time complexity of 5 given code snippets

#### Deliverable
A mini OOP design for a `Library Management System` using all four pillars + at least 3 SOLID principles.

---

### Week 2 — Collections: List, Set, Map
**Duration:** 4 hours

#### Topics
- **List** — `ArrayList` (dynamic array internals, resizing), `LinkedList` (doubly linked, Deque usage), `Vector` vs `ArrayList`
- **Set** — `HashSet` (bucket internals), `LinkedHashSet` (insertion order), `TreeSet` (Red-Black tree, `Comparator`)
- **Map** — `HashMap` (hashing, load factor, collision), `LinkedHashMap`, `TreeMap` (sorted keys), `Hashtable` vs `HashMap`
- **Choosing the right structure** — time complexity comparison table across all types

#### Hands-On Exercises
- Implement a frequency counter using `HashMap` and sort output with `TreeMap`
- Demonstrate `TreeSet` with a custom `Comparator` for a `Student` object
- Identify the correct collection for 5 real-world scenarios (with justification)

#### Deliverable
A `ContactBook` application using appropriate List, Set, and Map implementations with documented trade-off decisions.

---

### Week 3 — Collections Framework + Streams + Functional Programming
**Duration:** 4 hours

#### Topics
- **Collections utility methods** — `Collections.sort()`, `Collections.unmodifiableList()`, `Collections.synchronizedMap()`
- **ConcurrentHashMap** — atomic operations (`merge`, `compute`, `putIfAbsent`), segment locking, when to use vs `HashMap`
- **Streams API** — `filter`, `map`, `flatMap`, `reduce`, `collect`, `groupingBy`, `partitioningBy`, lazy evaluation
- **Functional Programming** — `Predicate`, `Function`, `Consumer`, `Supplier`, `BiFunction`, method references, lambda expressions
- **Optional** — eliminating null checks, `map`, `flatMap`, `orElse`, `ifPresent`

#### Hands-On Exercises
- Rewrite 3 imperative loops using Stream pipelines
- Build a data processing pipeline: filter → transform → aggregate on an `Employee` list
- Chain 4 functional interfaces to build a validation pipeline

#### Deliverable
A `Sales Report Generator` using Streams and functional interfaces — group sales by region, filter by quarter, compute totals.

---

## Phase 2 — Data Structures
> Weeks 4–7 · ~16 hours

---

### Week 4 — Arrays, Strings & Linked Lists
**Duration:** 4 hours

#### Topics
- **Arrays (1D & 2D)** — memory layout, cache locality, rotation, spiral traversal, prefix sums, sliding window foundation
- **Strings** — immutability, string pool, `StringBuilder` vs `String`, palindrome checks, anagram detection, KMP intro
- **Linked Lists** — singly/doubly linked, cycle detection (Floyd's algorithm), reversal, merge sorted lists, find middle node

#### Hands-On Exercises
- Rotate a 2D matrix in-place
- Find all anagrams of a pattern in a string in O(n)
- Detect and find the entry point of a cycle in a linked list

#### Deliverable
Solve 6 LeetCode-style problems (2 per topic) with documented approach, complexity, and test cases.

---

### Week 5 — Stacks, Queues & Hashing
**Duration:** 4 hours

#### Topics
- **Stacks** — LIFO principle, `ArrayDeque` vs `Stack`, monotonic stacks, next greater element, valid parentheses
- **Queues** — FIFO, `LinkedList`, `ArrayDeque`, circular queues, sliding window maximum with deque
- **Hashing** — hash functions, collision resolution (chaining vs open addressing), load factor, `hashCode` + `equals` contract, frequency maps

#### Hands-On Exercises
- Implement a `MinStack` with O(1) `getMin()`
- Solve sliding window maximum using a monotonic deque
- Implement a `TwoSum` solution in O(n) using hashing and explain why it works

#### Deliverable
Build a `Browser History` navigator using a stack for back/forward + a `LRU Cache` using `LinkedHashMap`.

---

### Week 6 — Trees & Binary Search Trees
**Duration:** 4 hours

#### Topics
- **Binary Trees** — node structure, BFS (level-order), DFS (pre/in/post-order), height, diameter, mirror
- **Binary Search Trees** — insertion, deletion, search, in-order gives sorted output, floor/ceiling
- **AVL Trees** (intro) — balance factor, why self-balancing matters, rotation concept
- **Tree problems pattern** — "left-right subtree DFS with global max" pattern, path sums, LCA

#### Hands-On Exercises
- Serialize and deserialize a binary tree
- Find the lowest common ancestor (LCA) without parent pointers
- Validate whether a given binary tree is a valid BST

#### Deliverable
Implement a `BST` from scratch with insert, delete, search, and all three traversals. Add a `kth smallest element` method.

---

### Week 7 — Heaps, Priority Queues & Graph Basics
**Duration:** 4 hours

#### Topics
- **Heaps** — min-heap/max-heap property, heapify up/down, heap sort, heap as array
- **Priority Queue** — Java's `PriorityQueue`, custom `Comparator`, K largest/smallest elements
- **Graph Basics** — directed vs undirected, weighted vs unweighted, adjacency matrix vs adjacency list (trade-offs)
- **BFS & DFS** — traversal, cycle detection, connected components, flood fill

#### Hands-On Exercises
- Find top K frequent elements using a min-heap of size K
- Detect a cycle in a directed graph using DFS coloring (white/grey/black)
- Find the number of islands using BFS/DFS

#### Deliverable
Implement `Dijkstra's shortest path` on an adjacency list graph using `PriorityQueue`.

---

## Phase 3 — Searching & Sorting
> Week 8 · ~3.5 hours

---

### Week 8 — Searching + Sorting Algorithms
**Duration:** 3.5 hours

#### Topics
- **Searching** — Linear O(n), Binary search O(log n) (iterative + recursive), search in rotated sorted array, binary search on answer (capacity problems)
- **Sorting** — Bubble, Selection, Insertion (O(n²)), Merge sort O(n log n), Quick sort (pivot strategies, worst case avoidance), Heap sort
- **Java built-in sorting** — `Arrays.sort`, `Collections.sort`, `Comparator` chaining, `TimSort` internals
- **When to use which** — stability, in-place, best/worst case, real-world recommendations

#### Hands-On Exercises
- Binary search on a 2D matrix (row-sorted + first element > last of previous row)
- Implement 3-way quicksort (Dutch National Flag) for arrays with duplicates
- Sort a list of `Employee` objects by department ascending, then salary descending

#### Deliverable
Benchmarking exercise: implement merge sort and quick sort, time them on sizes 1K / 10K / 100K, document and explain observed differences.

---

## Phase 4 — Algorithmic Thinking
> Weeks 9–11 · ~12 hours

---

### Week 9 — Recursion, Backtracking & Greedy Algorithms
**Duration:** 4 hours

#### Topics
- **Recursion** — call stack mechanics, recurrence relations, memoization intro, tail recursion
- **Backtracking** — decision tree, pruning, subsets, permutations, N-Queens, Sudoku solver
- **Greedy Algorithms** — greedy choice property, exchange argument proof, activity selection, fractional knapsack, Huffman encoding, gas station
- **Greedy vs DP** — when greedy works, when it fails (coin change counterexample)

#### Hands-On Exercises
- Generate all valid parentheses combinations (backtracking)
- Solve the word search II problem (Trie + DFS with pruning)
- Prove why Earliest Deadline First scheduling is greedy-optimal

#### Deliverable
Implement a `Sudoku solver` using backtracking with bitmasking for O(1) constraint checks.

---

### Week 10 — Dynamic Programming + Advanced Graph Algorithms
**Duration:** 4 hours

#### Topics
- **Dynamic Programming** — overlapping subproblems, optimal substructure, memoization vs tabulation, rolling arrays for space optimization
- **Classic DP problems** — LCS, LIS, Edit Distance, 0/1 Knapsack, Coin Change, Longest Palindromic Subsequence
- **Advanced Graphs** — Dijkstra (with priority queue), Bellman-Ford (negative weights), Floyd-Warshall (all-pairs), Kruskal & Prim (MST), Topological Sort (Kahn's + DFS), Strongly Connected Components (Tarjan/Kosaraju)

#### Hands-On Exercises
- Solve the `Cheapest Flights Within K Stops` problem (Bellman-Ford DP)
- Build topological sort and use it for course prerequisite scheduling
- Trace Floyd-Warshall on a 4-node graph manually, then code it

#### Deliverable
Implement a `Shortest Path + MST` system for a city road network: Dijkstra for routing, Kruskal for minimum cable layout.

---

### Week 11 — Advanced Patterns + Hard DSA Problems
**Duration:** 4 hours

#### Topics
- **Sliding Window** — fixed window, variable window, monotonic deque (sliding window max), minimum window substring
- **Two Pointers** — opposite direction (two sum, trap water), same direction (remove duplicates), Floyd's cycle (find duplicate)
- **Bit Manipulation** — AND/OR/XOR tricks, bit masking, power-of-2 checks, bitmask DP (TSP), count set bits
- **Segment Trees** — range sum + point update, range min/max, lazy propagation, Fenwick tree comparison
- **Multi-pattern integration** — problems combining DP + graph, Trie + backtracking, Union-Find + grid

#### Hands-On Exercises
- Solve `Subarray Sum Equals K` using prefix sum + hashmap (the classic hybrid)
- Implement a segment tree with lazy propagation for range add + range sum queries
- Solve `Word Search II` with Trie + DFS pruning — explain complexity improvement

#### Deliverable
Solve 5 hard LeetCode problems (one per pattern area), each with written complexity analysis and pattern identification.

---

## Phase 5 — Architecture & Design
> Weeks 12–13 · ~8 hours

---

### Week 12 — Advanced OOP + Design Patterns
**Duration:** 4 hours

#### Topics
- **Advanced OOP Design** — immutable value objects, entities vs value objects, tell-don't-ask, fluent builder APIs, sealed class hierarchies
- **Creational Patterns** — Singleton (all variants, enum singleton), Factory Method, Abstract Factory, Builder, Prototype
- **Structural Patterns** — Adapter, Decorator (data pipelines), Facade (order workflow), Proxy (caching + auth), Composite (file system), Bridge
- **Behavioral Patterns** — Observer (type-safe EventBus), Strategy (swappable algorithms), Command + Undo, Template Method, Chain of Responsibility (HTTP middleware), State Machine, Memento

#### Hands-On Exercises
- Design a `Notification System` using Factory Method + Observer — one publisher, multiple channels (email, SMS, push)
- Build an HTTP middleware chain using Chain of Responsibility with auth, rate-limiting, and logging handlers
- Implement a `TextEditor` with unlimited undo using Command + Memento

#### Deliverable
E-commerce order system combining 6+ patterns: Builder (order), Strategy (pricing), Command (actions), Observer (events), Facade (checkout), Circuit Breaker (payment).

---

### Week 13 — System Design + Concurrency + JVM Internals
**Duration:** 4 hours

#### Topics
- **System Design** — scalability (vertical vs horizontal), load balancing, caching strategies (LRU, write-through, cache-aside), rate limiting (token bucket, sliding window), CAP theorem, monolith vs microservices, SAGA pattern, CQRS
- **Concurrency** — thread lifecycle, `synchronized`, `volatile`, `ReentrantLock`, `ReadWriteLock`, `AtomicXxx`, `LongAdder`, deadlock prevention, `CompletableFuture` pipelines, thread pool sizing
- **JVM Internals** — heap regions (young gen, old gen, metaspace), GC collectors (G1, ZGC), GC tuning flags, memory leaks (ThreadLocal, static collections, inner classes), `SoftReference`/`WeakReference`

#### Hands-On Exercises
- Design a food delivery platform at 10M DAU — back-of-envelope estimation + component diagram
- Build a thread-safe `BoundedBuffer` using `ReentrantLock` + `Condition`
- Identify and fix 3 memory leaks in provided buggy code snippets

#### Deliverable
Design document for a real-time notification microservice: service decomposition, Kafka event bus, circuit breaker, concurrency model, GC config recommendations.

---

## Phase 6 — Interview & Production Readiness
> Week 14 · ~4 hours

---

### Week 14 — Mock Interviews + Enterprise Spring Deep Dive
**Duration:** 4 hours

#### Topics
- **Advanced Java Interview Mock** — 2 live coding rounds (45 min each), one DSA problem + one system design problem, interviewer-style feedback
- **Enterprise Spring Boot** — layered architecture (Controller → Service → Repository), Spring Data JPA, `@Transactional`, REST API design (versioning, error handling, pagination), Spring Security (JWT + OAuth2 flow), Actuator + Micrometer metrics

#### Hands-On Exercises
- Live coding: `LRU Cache` O(1) — design, implement, test in 45 minutes
- Live system design: design a URL shortener — requirements → estimation → components → deep dive
- Build a secure paginated REST endpoint with Spring Boot + Spring Security + JPA

#### Deliverable
Mock interview scorecard (self-assessed + peer-assessed) covering: problem understanding, approach articulation, code quality, edge case handling, complexity analysis, communication.

---

## Phase 7 — Final Certification
> Week 15 · ~4 hours

---

### Week 15 — Capstone Project + Final Review + Certification Mock
**Duration:** 4 hours

#### Topics
- **Capstone Project Architecture** — present system design for a chosen domain (e-commerce, ride-sharing, social feed); justify every architectural decision
- **Case Studies review** — revisit real-world architectures: how Netflix uses queues, how Google uses consistent hashing, how GitHub uses event sourcing
- **Complete Course Final Review** — rapid-fire Q&A across all 7 phases: OOP, DSA, algorithms, design patterns, system design, concurrency
- **Certification Mock Exam** — timed 90-minute exam: 20 MCQ + 2 coding problems + 1 system design sketch

#### Exam Structure
| Section | Questions | Time | Weight |
|---|---|---|---|
| Core Java & OOP | 6 MCQ | 10 min | 15% |
| Data Structures | 6 MCQ | 10 min | 15% |
| Algorithms | 4 MCQ + 1 coding | 25 min | 25% |
| Design Patterns | 4 MCQ | 8 min | 10% |
| System Design | 1 architecture sketch | 20 min | 20% |
| Concurrency & JVM | 1 coding | 17 min | 15% |

#### Deliverable
Capstone presentation (10 min) + passing score ≥ 70% on certification mock.

---

## Consolidated Topic Map

| Original Sessions | Consolidated Into | Week |
|---|---|---|
| 1 (OOP) | Week 1 | Phase 1 |
| 2, 3, 4 (List, Set, Map) | Week 2 | Phase 1 |
| 5 (Collections + Streams) | Week 3 | Phase 1 |
| 6, 7, 8 (Arrays, Strings, LL) | Week 4 | Phase 2 |
| 9, 10, 11 (Stack, Queue, Hash) | Week 5 | Phase 2 |
| 12, 13 (Trees + BST) | Week 6 | Phase 2 |
| 14, 15 (Heaps + Graph Basics) | Week 7 | Phase 2 |
| 16, 17 (Search + Sort) | Week 8 | Phase 3 |
| 18, 19 (Recursion + Greedy) | Week 9 | Phase 4 |
| 20, 21 (DP + Adv Graphs) | Week 10 | Phase 4 |
| 22, 23 (Patterns + Hard DSA) | Week 11 | Phase 4 |
| 24 (Design Patterns) | Week 12 | Phase 5 |
| 25, 26 (System Design + Concurrency + JVM) | Week 13 | Phase 5 |
| 27, 29 (Mock + Spring) | Week 14 | Phase 6 |
| 28, 30 (Capstone + Certification) | Week 15 | Phase 7 |

---

## Weekly Hours Summary

| Week | Phase | Hours |
|---|---|---|
| 1 | Core Java | 4.0 |
| 2 | Core Java | 4.0 |
| 3 | Core Java | 4.0 |
| 4 | Data Structures | 4.0 |
| 5 | Data Structures | 4.0 |
| 6 | Data Structures | 4.0 |
| 7 | Data Structures | 4.0 |
| 8 | Searching & Sorting | 3.5 |
| 9 | Algorithms | 4.0 |
| 10 | Algorithms | 4.0 |
| 11 | Algorithms | 4.0 |
| 12 | Architecture | 4.0 |
| 13 | Architecture | 4.0 |
| 14 | Interview Prep | 4.0 |
| 15 | Certification | 4.0 |
| **Total** | | **~59.5 hrs** |

---

## Prerequisites

- Basic Java syntax (variables, loops, conditionals, methods)
- Familiarity with an IDE (IntelliJ IDEA recommended)
- Git basics for submitting deliverables
- JDK 17+ installed

## Tools & Setup

```
JDK:         Java 17 LTS or Java 21 LTS (recommended)
IDE:         IntelliJ IDEA Community Edition
Build:       Maven or Gradle
Testing:     JUnit 5 + Mockito
DB (Week 14):PostgreSQL + H2 (in-memory for tests)
Spring:      Spring Boot 3.x (Week 14)
Profiling:   VisualVM or IntelliJ Profiler (Week 13)
```

## Assessment Model

| Assessment Type | Frequency | Weight |
|---|---|---|
| Weekly deliverable | Every week | 40% |
| Mid-program check (end of Week 7) | Once | 20% |
| Mock interview score (Week 14) | Once | 20% |
| Final certification mock (Week 15) | Once | 20% |

---

*Program designed for engineers with basic Java experience targeting senior/principal-level roles.*
*Each session is independent — if a topic needs more depth, carry exercises into the next session.*
