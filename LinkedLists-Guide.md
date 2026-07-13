# Linked Lists — Complete Deep Dive in Java

---

## How to Run

```bash
javac LinkedLists.java
java LinkedLists
```

> Requires Java 8 or higher. Check with `java -version`.

---

## File Structure

```
LinkedLists.java
│
├── SNode                        → Singly linked node (data + next)
├── DNode                        → Doubly linked node (data + prev + next)
│
├── SinglyLinkedList             → Full implementation
│   ├── insertAtHead()           → O(1)
│   ├── insertAtTail()           → O(n)
│   ├── insertAtPosition()       → O(n)
│   ├── deleteAtHead()           → O(1)
│   ├── deleteByValue()          → O(n)
│   ├── search()                 → O(n)
│   ├── reverse()                → O(n) time, O(1) space
│   └── length()                 → O(n)
│
├── DoublyLinkedList             → Full implementation with tail pointer
│   ├── insertAtHead()           → O(1)
│   ├── insertAtTail()           → O(1) ← tail pointer advantage
│   ├── deleteAtTail()           → O(1) ← tail.prev advantage
│   ├── printForward()           → O(n)
│   └── printBackward()          → O(n)
│
├── CircularLinkedList           → Insert + circular print
│
├── findMiddle()                 → Fast/slow pointer
├── hasCycle()                   → Floyd's algorithm
├── findCycleStart()             → Floyd's phase 2
├── nthFromEnd()                 → Two-pointer gap technique
│
├── mergeSorted()                → Interview problem
├── isPalindrome()               → Interview problem
├── removeDuplicates()           → Interview problem
├── findIntersection()           → Interview problem
├── addTwoNumbers()              → Interview problem
│
└── main()                       → Runs all 7 topics
```

---

## Topic 1 — Why Linked Lists?

### The Array's Hidden Cost

Arrays store elements in contiguous memory. That's their strength for random access. But it becomes a weakness when you need to insert or delete in the middle:

```
Array: insert 99 at index 1
Before: [10, 20, 30, 40, 50]
Step 1: shift 50 right → [10, 20, 30, 40, _, 50]
Step 2: shift 40 right → [10, 20, 30, _, 40, 50]
Step 3: shift 30 right → [10, 20, _, 30, 40, 50]
Step 4: shift 20 right → [10, _, 20, 30, 40, 50]
Step 5: insert 99      → [10, 99, 20, 30, 40, 50]
Cost: O(n) shifts + possibly a new array if size exceeded
```

Linked list insert at the same position:
```
Step 1: Create node [99]
Step 2: 99.next = node(20)
Step 3: node(10).next = 99
Done. 2 pointer updates. O(1) once you have the position.
```

### When to Use Which

| Need | Array / ArrayList | Linked List |
|------|-------------------|-------------|
| Random access by index | **O(1) ✅** | O(n) ❌ |
| Insert / delete at front | O(n) ❌ | **O(1) ✅** |
| Insert / delete at back | O(1) ✅ | O(1) ✅ (with tail) |
| Dynamic size | Manual resize | **Natural ✅** |
| Memory efficiency | **Better ✅** | Extra pointer overhead |
| Cache friendliness | **Excellent ✅** | Poor ❌ |
| Binary search | **O(log n) ✅** | Not possible ❌ |

**Practical rule:** Use `ArrayList` by default in Java. Use `LinkedList` when your primary operations are insertions/deletions at the front or back and you never need random access.

---

## Topic 2 — Memory Model

### Node Structure

Every element in a linked list is wrapped in a **Node object**:

```java
class SNode {
    int   data;   // 4 bytes  — the actual value
    SNode next;   // 8 bytes  — reference (pointer) to next node
}
// Total per node: ~16 bytes (4 + 8 + 8 for object header)
// vs int[] which stores just 4 bytes per element
```

A linked list uses approximately **4× more memory** than a raw array for the same integer data. This is the price paid for dynamic sizing and O(1) head operations.

### Memory Layout Comparison

**Array — contiguous (side-by-side):**
```
Address: 1000   1004   1008   1012
Value:   [ 10 ] [ 20 ] [ 30 ] [ 40 ]

Accessing arr[2]: base(1000) + 2×4 = 1008 → instant
CPU loads a cache line covering multiple elements → neighbours are free
```

**Linked List — scattered:**
```
Address: 1000          3048          512           7200
Node:    [10 | →3048]  [20 | →512]   [30 | →7200]  [40 | null]
         head

Accessing index 2: follow 1000→3048→512 → three memory jumps
Each jump is potentially a cache miss → real-world slowdown
```

This is why benchmark tests often show `ArrayList` outperforming `LinkedList` even for operations where LinkedList is theoretically O(1) — CPU cache behavior dominates at realistic sizes.

### Pointer Mechanics — What Happens at Insert

```
Current list: head → [10] → [20] → [30] → null

Insert 99 between 10 and 20:

Step 1: Create new node
        newNode = [99 | null]

Step 2: Connect new node to rest of list
        newNode.next = node(20)
        [99 | →20]

Step 3: Connect node(10) to new node
        node(10).next = newNode
        [10 | →99]

Result: head → [10] → [99] → [20] → [30] → null

Two pointer assignments. Zero data movement.
```

---

## Topic 3 — Types of Linked Lists

### 1. Singly Linked List

```
head → [1|→] → [2|→] → [3|→] → [4|null]
```

- Each node has one pointer: `next`
- Traversal: **forward only**
- Deletion from tail: O(n) — must traverse to find the second-to-last node
- Memory: data + 1 pointer per node

**Use when:** you only ever need forward traversal and simple push/pop from the head (stack behavior).

### 2. Doubly Linked List

```
null ← [1|⇄] ⇄ [2|⇄] ⇄ [3|⇄] ⇄ [4|⇄] → null
       ↑head                          ↑tail
```

- Each node has two pointers: `prev` and `next`
- Traversal: **forward and backward**
- With a `tail` pointer: insert and delete at both ends in **O(1)**
- Memory: data + 2 pointers per node (~24 bytes for int)

**The key advantage:** `tail.prev` gives you the second-to-last node instantly, making `deleteAtTail()` O(1) — impossible in singly linked list without traversal.

**Use when:** you need bidirectional traversal (browser history, text editor cursor), or frequent insertions/deletions at both ends.

### 3. Circular Linked List

```
head → [1] → [2] → [3] → [4] → (back to 1)
```

- Last node's `next` points back to `head` — no null terminator
- Traversal: **must track visited nodes** to avoid infinite loops
- Use `do-while` loop with stop condition `curr.next != head`

**Use when:** the data is naturally circular — round-robin CPU scheduling, music playlists with loop-all, token ring networks.

### 4. Doubly Circular Linked List

```
[1] ⇄ [2] ⇄ [3] ⇄ [4] ⇄ (back to 1)
```

- Combines both directions with circular structure
- Used in: Linux kernel's `list_head`, deque implementations

### Type Comparison

| Type | Forward | Backward | Tail Insert | Memory |
|------|---------|----------|-------------|--------|
| Singly | ✅ | ❌ | O(n) | Low |
| Doubly | ✅ | ✅ | **O(1)** | Medium |
| Circular | ✅ loop | ❌ | O(n) | Low |
| Doubly Circular | ✅ loop | ✅ loop | **O(1)** | High |

---

## Topic 4 — Core Operations + Complexity

### Insert at Head — O(1)

```java
void insertAtHead(int data) {
    SNode newNode = new SNode(data);  // Step 1: create node
    newNode.next = head;              // Step 2: point to old head
    head = newNode;                   // Step 3: update head
}
```

Always exactly 3 steps regardless of list size → **O(1)**.

### Insert at Tail — O(n) for Singly, O(1) for Doubly

```java
// Singly — must walk to end
void insertAtTail(int data) {
    SNode curr = head;
    while (curr.next != null) curr = curr.next;  // O(n) traversal
    curr.next = new SNode(data);
}

// Doubly — tail pointer eliminates traversal
void insertAtTail(int data) {
    DNode newNode = new DNode(data);
    newNode.prev = tail;
    tail.next = newNode;
    tail = newNode;   // update tail — O(1)
}
```

### Delete by Value — O(n)

```java
void deleteByValue(int data) {
    // Special case: deleting the head
    if (head.data == data) { head = head.next; return; }

    SNode curr = head;
    while (curr.next != null) {
        if (curr.next.data == data) {
            curr.next = curr.next.next;  // bypass the node — it becomes garbage
            return;
        }
        curr = curr.next;
    }
}
```

**The bypass trick:** `curr.next = curr.next.next` makes the target node unreachable. Java's garbage collector reclaims it automatically.

### Reverse — O(n) time, O(1) space — Three Pointer Technique

This is one of the most important linked list algorithms:

```java
void reverse() {
    SNode prev = null;
    SNode curr = head;

    while (curr != null) {
        SNode nextTemp = curr.next;  // save next before overwriting
        curr.next = prev;            // reverse the pointer
        prev = curr;                 // advance prev
        curr = nextTemp;             // advance curr
    }
    head = prev;  // prev stopped at the old tail (new head)
}
```

**Step-by-step trace for `1 → 2 → 3 → null`:**

```
Start:  prev=null, curr=1, head=1

Step 1: nextTemp=2, 1.next=null,  prev=1, curr=2
        null ← 1   2 → 3 → null

Step 2: nextTemp=3, 2.next=1, prev=2, curr=3
        null ← 1 ← 2   3 → null

Step 3: nextTemp=null, 3.next=2, prev=3, curr=null
        null ← 1 ← 2 ← 3

curr==null → exit loop. head = prev = 3
Result: 3 → 2 → 1 → null  ✓
```

No extra array or stack used — **O(1) space**.

### Complexity Table

| Operation | Singly | Doubly | Why |
|-----------|--------|--------|-----|
| Insert at head | O(1) | O(1) | Just update head pointer |
| Insert at tail | O(n)* | **O(1)** | Doubly has tail pointer |
| Insert at position | O(n) | O(n) | Traverse to position |
| Delete at head | O(1) | O(1) | Just update head pointer |
| Delete at tail | O(n)* | **O(1)** | Doubly: tail.prev gives prev node |
| Delete by value | O(n) | O(n) | Search + bypass |
| Search | O(n) | O(n) | No random access |
| Reverse | O(n) | O(n) | Visit every node |
| Access index i | O(n) | O(n) | Must traverse |
| **Space** | **O(n)** | **O(n)** | n nodes stored |

*O(n) without dedicated tail pointer. Maintaining a tail pointer makes these O(1).

---

## Topic 5 — Fast & Slow Pointer Patterns

The **fast & slow pointer** technique (Floyd's Tortoise and Hare) is a family of algorithms that uses two pointers moving at different speeds to solve linked list problems in **O(n) time with O(1) space**.

### Pattern 1: Find the Middle Node

```java
SNode slow = head;
SNode fast = head;

while (fast != null && fast.next != null) {
    slow = slow.next;       // moves 1 step
    fast = fast.next.next;  // moves 2 steps
}
// When fast reaches end, slow is at middle
return slow;
```

**Why it works:** fast travels twice as far as slow. When fast reaches the end (after n/2 steps of fast = n steps total), slow has traveled n/2 steps — exactly the middle.

```
List: 1 → 2 → 3 → 4 → 5

Start:  slow=1, fast=1
Step 1: slow=2, fast=3
Step 2: slow=3, fast=5  → fast.next=null → stop
Middle = slow = 3  ✓
```

**Even-length list (1→2→3→4):** returns node `3` (second of two middle nodes). Adjust by starting fast one step ahead if you need the first middle.

---

### Pattern 2: Detect a Cycle — Floyd's Algorithm

```java
SNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow == fast) return true;  // they met → cycle!
}
return false;
```

**Intuition:** imagine two runners on a circular track. The faster runner will always lap the slower one and they'll meet. If the track is a straight road (no cycle), the faster runner just runs off the end.

**Why they always meet inside the cycle:** once both pointers enter the cycle, fast gains one step on slow per iteration. The cycle has `C` nodes, so they meet in at most `C` iterations.

---

### Pattern 3: Find the Cycle Start

After detecting the meeting point (slow == fast), there's a beautiful mathematical property:

```
Let:  F = distance from head to cycle start
      h = distance from cycle start to meeting point
      C = cycle length

When slow == fast:
  slow traveled: F + h
  fast traveled: F + h + k×C  (k full laps more)
  fast = 2 × slow:  F + h + k×C = 2(F + h)
  → k×C = F + h
  → F = k×C - h

This means: distance from meeting point back to cycle start
= C - h = F (modulo full laps)
```

So reset one pointer to head, keep the other at the meeting point, advance both one step at a time — they meet exactly at the cycle start.

```java
// Phase 1: find meeting point
while (fast != null && fast.next != null) {
    slow = slow.next; fast = fast.next.next;
    if (slow == fast) break;
}

// Phase 2: find cycle start
slow = head;
while (slow != fast) {
    slow = slow.next;  // both move 1 step
    fast = fast.next;
}
return slow;  // cycle start
```

---

### Pattern 4: Nth Node From End

```java
SNode slow = head, fast = head;

// Advance fast by n steps (creates a gap of n)
for (int i = 0; i < n; i++) fast = fast.next;

// Move both until fast reaches end
while (fast != null) {
    slow = slow.next;
    fast = fast.next;
}
return slow;  // slow is n steps behind end = nth from end
```

```
List: 1→2→3→4→5→6→7,  n=3

Advance fast 3 steps: fast=4, slow=1
Gap: slow is 3 behind fast

Move both:
  slow=2, fast=5
  slow=3, fast=6
  slow=4, fast=7
  slow=5, fast=null → stop

Answer: slow = 5 = 3rd from end  ✓
```

### Pattern Summary

| Problem | Fast Step | Slow Step | Termination | Answer |
|---------|-----------|-----------|-------------|--------|
| Find middle | 2 | 1 | fast == null | slow |
| Detect cycle | 2 | 1 | slow == fast | true/false |
| Find cycle start | Phase 2: both 1 | Phase 2: both 1 | slow == fast | slow |
| Nth from end | Start n ahead | 1 | fast == null | slow |

All patterns: **O(n) time, O(1) space** — no extra data structures.

---

## Topic 6 — Real-World Use Cases

### 1. Browser Back/Forward Navigation — Doubly Linked List

```
Pages visited: google.com ⇄ github.com ⇄ stackoverflow.com ⇄ youtube.com
                                                                ↑ current

Press Back:   current = current.prev → stackoverflow.com  O(1)
Press Forward: current = current.next → youtube.com       O(1)
New page:     delete everything after current, add new tail O(1)
```

The doubly linked list is perfect here because back/forward are equally frequent and both need O(1) pointer updates.

### 2. Music Playlist — Circular Linked List

```
Song1 → Song2 → Song3 → Song4 → (back to Song1)
```

- **Loop All mode:** traverse forever, `last.next` wraps to `head`
- **Shuffle:** insert/delete nodes at any point without shifting
- **Current song pointer:** just a pointer to a node — no index tracking

### 3. Undo/Redo — Singly Linked List as Stack

```java
// Undo stack: most recent action at head
insertAtHead(action)  // push — O(1)
deleteAtHead()        // pop  — O(1)
```

Each action (typing a character, formatting text, drawing a shape) is a node. `Ctrl+Z` pops the head. `Ctrl+Y` pushes it onto a redo stack.

### 4. Java's LinkedList as Deque

`java.util.LinkedList` implements both `List` and `Deque`:

```java
LinkedList<Integer> deque = new LinkedList<>();

// Used as Stack (LIFO):
deque.push(1);    // addFirst()    O(1)
deque.pop();      // removeFirst() O(1)

// Used as Queue (FIFO):
deque.offer(1);   // addLast()     O(1)
deque.poll();     // removeFirst() O(1)
```

Used in BFS (queue), DFS (stack), task schedulers, and undo systems.

### 5. LRU Cache — Doubly Linked List + HashMap

The LRU (Least Recently Used) cache is one of the most important real-world applications. It evicts the least recently accessed item when capacity is full.

```
HashMap:  key → node reference  (O(1) access to any node)
DLL:      most-recent ← head ... tail → least-recent

Get(key):
  1. Find node via HashMap — O(1)
  2. Move node to head of DLL — O(1)
  3. Return value

Put(key, value):
  1. If key exists: update, move to head — O(1)
  2. If at capacity: delete tail (LRU item), insert new head — O(1)
  3. Add to HashMap — O(1)

Overall: O(1) get AND O(1) put
```

**Where you'll find LRU caches:** Redis, CPU instruction cache, browser cache, CDN edge servers, database buffer pools, OS page replacement.

---

## Topic 7 — Interview-Level Problems

### Problem 1: Reverse a Linked List

**Technique:** Three pointers (prev, curr, next). Reverse one pointer at a time.

```java
SNode prev = null, curr = head;
while (curr != null) {
    SNode next = curr.next;
    curr.next = prev;   // reverse
    prev = curr;
    curr = next;
}
head = prev;
```

**Complexity:** O(n) time, O(1) space.

---

### Problem 2: Merge Two Sorted Lists

**Technique:** Dummy head node eliminates edge cases. Compare heads, pick smaller, advance that pointer.

```java
SNode dummy = new SNode(0);
SNode curr  = dummy;
while (l1 != null && l2 != null) {
    if (l1.data <= l2.data) { curr.next = l1; l1 = l1.next; }
    else                    { curr.next = l2; l2 = l2.next; }
    curr = curr.next;
}
curr.next = (l1 != null) ? l1 : l2;
return dummy.next;
```

**The dummy head pattern** is used in many linked list problems to avoid special-casing the first node.

**Complexity:** O(n + m) time, O(1) space.

---

### Problem 3: Palindrome Linked List

**Technique:** Find middle → reverse second half → compare both halves.

```
1 → 2 → 3 → 2 → 1

Step 1: Find middle → node(3)
Step 2: Reverse 3→2→1 → 1→2→3
Step 3: Compare 1→2→3 (first half) with 1→2→3 (reversed second half)
        All match → palindrome!
```

**Why not just copy to an array?** You could, but it costs O(n) extra space. The in-place approach uses O(1) space by using the fast/slow + reverse pattern.

**Complexity:** O(n) time, O(1) space.

---

### Problem 4: Remove Duplicates from Sorted List

**Technique:** Since the list is sorted, duplicates are adjacent. Skip consecutive equal nodes.

```java
SNode curr = head;
while (curr != null && curr.next != null) {
    if (curr.data == curr.next.data)
        curr.next = curr.next.next;  // skip duplicate
    else
        curr = curr.next;
}
```

**Important:** only advance `curr` when there's no duplicate. If there are three consecutive equal nodes, we need to skip all of them.

**Complexity:** O(n) time, O(1) space.

---

### Problem 5: Add Two Numbers

**Technique:** Digit-by-digit addition with carry. Process both lists simultaneously.

```
342 → stored as 2→4→3 (reverse: units first)
465 → stored as 5→6→4

  2 + 5 = 7,  carry=0  → node(7)
  4 + 6 = 10, carry=1  → node(0)
  3 + 4 + 1 = 8, carry=0 → node(8)

Result: 7→0→8 → represents 807  ✓
```

**Complexity:** O(max(n, m)) time, O(max(n, m)) space for result list.

---

### Interview Cheat Sheet

| Problem | Time | Space | Key Technique |
|---------|------|-------|---------------|
| Reverse list | O(n) | **O(1)** | 3 pointers |
| Find middle | O(n) | **O(1)** | Fast/slow |
| Detect cycle | O(n) | **O(1)** | Floyd's |
| Find cycle start | O(n) | **O(1)** | Floyd's phase 2 |
| Nth from end | O(n) | **O(1)** | Gap pointer |
| Merge sorted lists | O(n+m) | **O(1)** | Dummy head + compare |
| Palindrome check | O(n) | **O(1)** | Find middle + reverse half |
| Remove duplicates | O(n) | **O(1)** | Skip consecutive equal |
| Intersection | O(n+m) | **O(1)** | Redirect at end |
| Add two numbers | O(n) | O(n) | Digit + carry |

---

## Common Mistakes to Avoid

```java
// ❌ 1. Not checking for null before accessing .next
SNode curr = head;
while (curr.next != null) { ... }   // crashes if head is null!
while (curr != null && curr.next != null) { ... }  // ✅ safe

// ❌ 2. Losing the reference to next before reversing
curr.next = prev;         // ❌ lost curr.next forever!
SNode next = curr.next;   // ✅ save first
curr.next = prev;

// ❌ 3. Infinite loop in circular list
while (curr != null) { curr = curr.next; }   // ❌ never null in circular!
SNode start = head;
do { curr = curr.next; } while (curr != start);  // ✅

// ❌ 4. Off-by-one in nth from end
for (int i = 0; i < n; i++) fast = fast.next;   // ✅ n steps (0-indexed)
for (int i = 0; i <= n; i++) fast = fast.next;  // ❌ n+1 steps

// ❌ 5. Forgetting to update size counter
head = head.next;  // ❌ deleted head but size wasn't decremented
head = head.next; size--;  // ✅

// ❌ 6. Not handling single-node and two-node edge cases
// Always test: null, single node, two nodes, even/odd length
```

---

## The 5 Golden Rules

```
1. Use fast/slow pointer → O(1) space solutions for middle, cycle, nth-from-end
2. Always null-check before dereferencing .next
3. Use a dummy head node → eliminates special cases for head deletion/insertion
4. Maintain a tail pointer → makes tail insert/delete O(1) in both directions
5. In 90% of Java code, ArrayList outperforms LinkedList (cache effects win)
```
