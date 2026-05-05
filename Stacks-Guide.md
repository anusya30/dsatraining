# Stacks — Complete Deep Dive in Java

---

## How to Run

```bash
javac Stacks.java
java Stacks
```

> Requires Java 8 or higher. Check with `java -version`.

---

## File Structure

```
Stacks.java
│
├── ArrayStack                   → Stack backed by fixed int[]
│   ├── push()                   → O(1)
│   ├── pop()                    → O(1)
│   ├── peek()                   → O(1)
│   └── isEmpty() / size()       → O(1)
│
├── LinkedStack                  → Stack backed by singly linked nodes
│   ├── push()                   → O(1)
│   ├── pop()                    → O(1)
│   └── peek()                   → O(1)
│
├── MinStack                     → Stack with O(1) getMin()
│   └── parallel minStack trick
│
├── nextGreaterElement()         → Monotonic Decreasing Stack
├── nextSmallerElement()         → Monotonic Increasing Stack
├── dailyTemperatures()          → NGE variant (distance)
├── largestRectangle()           → Histogram problem
├── stockSpan()                  → Monotonic Decreasing Stack
│
├── isValidParentheses()         → Interview Problem 1
├── evalRPN()                    → Interview Problem 2
├── decodeString()               → Interview Problem 4
│
└── main()                       → Runs all 6 topics
```

---

## Topic 1 — Stack Fundamentals

### What is a Stack?

A **Stack** is a linear data structure that follows **LIFO — Last In, First Out**.

The most common analogy is a **stack of plates**:
- You can only **ADD** a plate to the top
- You can only **REMOVE** a plate from the top
- You cannot pull a plate from the middle without removing everything above it

```
push(10): [ 10 ]
push(20): [ 10 | 20 ]
push(30): [ 10 | 20 | 30 ]  ← TOP
pop()   : returns 30 → [ 10 | 20 ]
peek()  : returns 20 (not removed) → [ 10 | 20 ]
pop()   : returns 20 → [ 10 ]
pop()   : returns 10 → [ ] ← empty
```

### Core Operations

| Operation | Description | Complexity |
|-----------|-------------|------------|
| `push(x)` | Add element to the top | **O(1)** |
| `pop()` | Remove and return top element | **O(1)** |
| `peek()` | Read top element without removing | **O(1)** |
| `isEmpty()` | Check if stack has no elements | **O(1)** |
| `size()` | Number of elements in stack | **O(1)** |

Every core stack operation is **O(1)** — this is what makes stacks so powerful.

### Stack vs Queue

```
Same push order: 1, 2, 3

Stack (LIFO):   pop order → 3, 2, 1   (reversed)
Queue (FIFO):  poll order → 1, 2, 3   (same order)
```

---

## Topic 2 — Implementation Approaches

### Approach 1 — Array-based Stack

```java
class ArrayStack {
    private int[] arr;
    private int top;      // index of top element, -1 = empty
    private int capacity;

    void push(int value) {
        if (top == capacity - 1) throw new RuntimeException("Stack Overflow!");
        arr[++top] = value;   // increment top, then assign
    }

    int pop() {
        if (isEmpty()) throw new RuntimeException("Stack Underflow!");
        return arr[top--];    // return value, then decrement top
    }

    int peek() { return arr[top]; }
    boolean isEmpty() { return top == -1; }
}
```

**Memory layout for push(10, 20, 30):**
```
index:  [0]  [1]  [2]  [3]  [4]  [5]  [6]  [7]
value:  [10] [20] [30] [ ]  [ ]  [ ]  [ ]  [ ]
                   ↑
                 top=2
```

**Advantages:** Contiguous memory → excellent CPU cache performance. No object overhead per element.

**Limitation:** Must declare capacity at creation. Overflow if more elements than capacity are pushed.

---

### Approach 2 — LinkedList-based Stack

```java
class LinkedStack {
    private Node head;  // head = TOP of stack

    void push(int value) {
        Node newNode = new Node(value);
        newNode.next = head;   // new node points to old head
        head = newNode;        // head moves to new top
    }

    int pop() {
        int val = head.data;
        head = head.next;      // head moves down one level
        return val;
    }
}
```

**Memory layout for push(10, 20, 30):**
```
head → [30 | →] → [20 | →] → [10 | null]
         ↑TOP                    ↑BOTTOM
```

**Why head = TOP?** Because `insertAtHead` and `deleteAtHead` are both O(1) in a linked list. If we used the tail as the top, deletion would be O(n) in a singly linked list.

**Advantages:** No overflow risk. Grows dynamically with the data.

**Limitation:** Each node uses ~16 bytes (data + next pointer + object header) vs 4 bytes in array. Poor cache locality.

---

### Approach 3 — Java Built-in: ArrayDeque (Recommended)

```java
Deque<Integer> stack = new ArrayDeque<>();

stack.push(10);      // addFirst()    → O(1) amortized
stack.pop();         // removeFirst() → O(1)
stack.peek();        // peekFirst()   → O(1)
```

**Why NOT `java.util.Stack`?**

```java
// java.util.Stack extends Vector
// Vector synchronizes EVERY method with a mutex lock
// Even in single-threaded code, you pay the synchronization cost
// It's a 1990s design that Java keeps for backward compatibility

Stack<Integer> legacy = new Stack<>();   // ❌ legacy, slow
Deque<Integer> modern = new ArrayDeque<>();  // ✅ fast, recommended
```

`ArrayDeque` is backed by a resizable array. When it's full, it doubles in capacity and copies elements — amortized O(1) per push.

### Comparison

| Approach | Capacity | Memory Overhead | Thread Safe | Use When |
|----------|----------|-----------------|-------------|----------|
| Array Stack | Fixed | Minimal | No | Max size known |
| LinkedList Stack | Dynamic | High (~3x) | No | Truly unbounded |
| ArrayDeque | Dynamic | Low+resize | No | **General use** ✅ |
| java.util.Stack | Dynamic | Medium | Yes (slow) | Avoid in new code |

---

## Topic 3 — Time & Space Complexity

### Why All Core Operations Are O(1)

**Array Stack:**
```
push: arr[++top] = value  → one array write + one increment  → O(1)
pop:  return arr[top--]   → one array read + one decrement   → O(1)
peek: return arr[top]     → one array read                   → O(1)
```

**Linked Stack:**
```
push: create node, two pointer assignments (newNode.next=head, head=newNode) → O(1)
pop:  read head.data, one pointer update (head=head.next)                    → O(1)
peek: read head.data                                                          → O(1)
```

No loops, no recursion, no searching — always a fixed number of operations.

### Space Complexity

```
Array Stack   → O(capacity)  — allocates full array upfront, even if half empty
LinkedList    → O(n)         — exact allocation per element, plus pointer overhead
ArrayDeque    → O(n)         — like ArrayList, slightly over-allocated
```

**Auxiliary space:** operations themselves use O(1) extra space (a few local variables).

### The Call Stack — O(n) Implicit Space in Recursion

Every time your code calls a method, the JVM pushes a **stack frame** onto its internal call stack. Each frame holds:
- Local variables
- Method parameters
- Return address

```java
// factorial(5) creates 5 stack frames:
factorial(5) → factorial(4) → factorial(3) → factorial(2) → factorial(1)
[frame 5]      [frame 4]      [frame 3]      [frame 2]      [frame 1] ← base

Space: O(n) — one frame per recursive call
```

When recursion goes too deep (typically ~10,000 frames in Java), you get `StackOverflowError`. The fix: convert recursion to iteration using an **explicit stack** on the heap.

```java
// Recursive factorial — O(n) call stack space, risk of overflow
long factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

// Iterative with explicit stack — O(n) heap space, no overflow risk
long factorialIterative(int n) {
    Deque<Integer> stack = new ArrayDeque<>();
    for (int i = 2; i <= n; i++) stack.push(i);
    long result = 1;
    while (!stack.isEmpty()) result *= stack.pop();
    return result;
}
```

---

## Topic 4 — Real-World Use Cases

### 1. Balanced Parentheses — IDE / Compiler

Every IDE (IntelliJ, VSCode) uses a stack to highlight matching brackets and show syntax errors:

```java
static boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '[' || c == '{') {
            stack.push(c);                   // push openers
        } else {
            if (stack.isEmpty()) return false;
            char top = stack.pop();
            if (c == ')' && top != '(') return false;
            if (c == ']' && top != '[') return false;
            if (c == '}' && top != '{') return false;
        }
    }
    return stack.isEmpty();  // unmatched openers remain?
}
```

```
"({[]})" → valid   ✅
"([)]"   → invalid ❌ — wrong close order
"((("    → invalid ❌ — unclosed openers remain in stack
```

**Also used for:** JSON validation, XML parsing, HTML tag matching, arithmetic expression validation.

---

### 2. Undo / Redo System — Text Editors, Photoshop

```java
Deque<String> undoStack = new ArrayDeque<>();
Deque<String> redoStack = new ArrayDeque<>();

// User types:
undoStack.push("Type 'H'");
undoStack.push("Type 'e'");
undoStack.push("Type 'l'");

// Ctrl+Z (undo):
String action = undoStack.pop();  // "Type 'l'"
redoStack.push(action);           // save to redo stack

// Ctrl+Y (redo):
action = redoStack.pop();
undoStack.push(action);
```

**Key insight:** a new user action always **clears** the redo stack. You can't redo after taking a new action — that's why undoStack.clear() is called on new input.

---

### 3. Expression Evaluation — Calculators, Compilers

**Reverse Polish Notation (RPN / Postfix)** eliminates parentheses entirely. Compilers convert infix expressions to RPN for evaluation.

```
Infix:    (2 + 3) × 4
RPN:      2 3 + 4 *

Evaluation:
  token "2" → push 2        stack: [2]
  token "3" → push 3        stack: [2, 3]
  token "+" → pop 3,2 → 2+3=5, push 5    stack: [5]
  token "4" → push 4        stack: [5, 4]
  token "*" → pop 4,5 → 5×4=20, push 20  stack: [20]
  Result: 20  ✓
```

**Note:** when popping for binary operators, the **second pop is the left operand**:
```java
int b = stack.pop();  // right operand  (popped first = pushed last)
int a = stack.pop();  // left operand   (popped second = pushed first)
result = a operator b;
```

Getting this order wrong is the most common bug in expression evaluators.

---

### 4. Browser Back / Forward Navigation

```
User visits: google.com → github.com → stackoverflow.com → claude.ai

backStack (top = current):
[claude.ai, stackoverflow.com, github.com, google.com]

Press Back:
  current = backStack.pop()   → "claude.ai"
  forwardStack.push("claude.ai")
  now at: stackoverflow.com

Press Forward:
  page = forwardStack.pop()   → "claude.ai"
  backStack.push("claude.ai")
  now at: claude.ai

Visit new page from stackoverflow.com:
  backStack.push("newsite.com")
  forwardStack.clear()        ← forward history always cleared on new navigation
```

---

### 5. JVM Call Stack — Function Execution

The JVM maintains a stack of **activation records (frames)**. This is why we call it "the call stack":

```
Execution of: main() → methodA() → methodB()

JVM Stack (grows downward):
┌─────────────────┐
│  methodB frame  │ ← currently executing
├─────────────────┤
│  methodA frame  │
├─────────────────┤
│   main frame    │
└─────────────────┘

methodB returns → frame popped → back to methodA
methodA returns → frame popped → back to main
```

Understanding this is essential for reading stack traces in exceptions:
```
Exception in thread "main" java.lang.NullPointerException
    at methodB(File.java:42)    ← most recent (top of stack)
    at methodA(File.java:30)
    at main(File.java:10)       ← least recent (bottom)
```

---

## Topic 5 — Monotonic Stack Pattern

A **Monotonic Stack** is a stack that maintains elements in **strictly increasing or decreasing order** at all times. Before pushing a new element, you pop all elements that violate the monotonic property.

### The Core Insight

> **When you pop element X because of incoming element Y,  
> Y is the answer to X's question.**

This transforms what would be O(n²) brute-force comparisons into a single O(n) pass.

---

### Monotonic Decreasing Stack — Next Greater Element

```java
static int[] nextGreaterElement(int[] arr) {
    int[] result = new int[arr.length];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();  // stores INDICES

    for (int i = 0; i < arr.length; i++) {
        while (!stack.isEmpty() && arr[stack.peek()] < arr[i]) {
            result[stack.pop()] = arr[i];  // arr[i] IS the next greater for popped
        }
        stack.push(i);
    }
    return result;
}
```

**Trace for `[4, 5, 2, 10, 8]`:**

```
i=0: val=4,  stack empty → push 0          | stack=[0]        | result=[-1,-1,-1,-1,-1]
i=1: val=5,  5>arr[0]=4 → pop 0, NGE[0]=5 | push 1           | stack=[1]       result=[5,-1,-1,-1,-1]
i=2: val=2,  2<arr[1]=5 → push 2           | stack=[1,2]      | result=[5,-1,-1,-1,-1]
i=3: val=10, 10>arr[2]=2→ pop 2, NGE[2]=10
             10>arr[1]=5→ pop 1, NGE[1]=10 | push 3           | stack=[3]       result=[5,10,10,-1,-1]
i=4: val=8,  8<arr[3]=10→ push 4           | stack=[3,4]
End: stack=[3,4] → all remaining get -1   | result=[5,10,10,-1,-1]  ✓
```

**Why it works:** The stack is always decreasing (stack bottom is largest). When a bigger element arrives, it "beats" all smaller elements currently waiting — they get their answer simultaneously.

---

### Daily Temperatures — NGE Variant

```java
static int[] dailyTemperatures(int[] temps) {
    int[] result = new int[temps.length];
    Deque<Integer> stack = new ArrayDeque<>();

    for (int i = 0; i < temps.length; i++) {
        while (!stack.isEmpty() && temps[stack.peek()] < temps[i]) {
            int idx = stack.pop();
            result[idx] = i - idx;  // ← distance between indices, not the value
        }
        stack.push(i);
    }
    return result;
}
```

```
temps = [73, 74, 75, 71, 69, 72, 76, 73]
result = [1,   1,   4,   2,   1,   1,   0,   0]
```

`result[0] = 1` means Day 0 (73°) must wait **1 day** for a warmer day (Day 1, 74°).

---

### Largest Rectangle in Histogram

For each bar, the maximum rectangle using it as the height extends left and right until a **shorter bar** is encountered. Monotonic increasing stack finds those boundaries:

```java
static int largestRectangle(int[] heights) {
    Deque<Integer> stack = new ArrayDeque<>();
    int maxArea = 0;

    for (int i = 0; i <= heights.length; i++) {
        int h = (i == heights.length) ? 0 : heights[i];  // sentinel
        while (!stack.isEmpty() && heights[stack.peek()] > h) {
            int height = heights[stack.pop()];
            int width  = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    return maxArea;
}
```

```
heights = [2, 1, 5, 6, 2, 3]

 █
 █ █ █
 █ █ █ █
 █ █ █ █ █ █
 2  1  5  6  2  3

Largest rectangle = 10 (bars at index 2,3 → height=5, width=2)
```

### When to Reach for Monotonic Stack

| Problem Keywords | Stack Type |
|-----------------|------------|
| "next greater element" | Monotonic Decreasing |
| "next smaller element" | Monotonic Increasing |
| "previous greater element" | Decreasing (right→left) |
| "days until warmer / larger" | Monotonic Decreasing |
| "largest rectangle" | Monotonic Increasing |
| "trapping rain water" | Monotonic Decreasing |
| "stock span" | Monotonic Decreasing |

---

## Topic 6 — Interview-Level Problems

### Problem 1: Valid Parentheses

**Pattern:** Push opener → on closer, pop and verify match.

```java
for (char c : s.toCharArray()) {
    if (isOpener(c)) stack.push(c);
    else {
        if (stack.isEmpty()) return false;       // no matching opener
        if (!matches(stack.pop(), c)) return false; // wrong type
    }
}
return stack.isEmpty();  // leftover openers = invalid
```

**Complexity:** O(n) time, O(n) space.

**Edge cases:** empty string (valid), only openers `"((("` (invalid — non-empty stack), only closers `")))"` (invalid — empty stack underflow).

---

### Problem 2: Evaluate Reverse Polish Notation

**Pattern:** Push numbers. On operator, pop two operands, compute, push result.

```
["5","1","2","+","4","*","+","3","-"]

push 5 → [5]
push 1 → [5,1]
push 2 → [5,1,2]
"+"    → pop 2,1 → 1+2=3, push → [5,3]
push 4 → [5,3,4]
"*"    → pop 4,3 → 3*4=12, push → [5,12]
"+"    → pop 12,5 → 5+12=17, push → [17]
push 3 → [17,3]
"-"    → pop 3,17 → 17-3=14, push → [14]
Result: 14  ✓
```

**Complexity:** O(n) time, O(n) space.

**Common mistake:** reversing operand order. `b = stack.pop()` is the **right** operand, `a = stack.pop()` is the **left**. For `a - b` = `5 - 3` not `3 - 5`.

---

### Problem 3: Min Stack

**Problem:** Design a stack supporting `push`, `pop`, `peek`, and `getMin()` — all in **O(1)**.

**Naive approach:** scan all elements for minimum → O(n). Rejected.

**Two-stack approach:**

```java
class MinStack {
    Deque<Integer> stack    = new ArrayDeque<>();
    Deque<Integer> minStack = new ArrayDeque<>(); // tracks running minimum

    void push(int val) {
        stack.push(val);
        int min = minStack.isEmpty() ? val : Math.min(val, minStack.peek());
        minStack.push(min);  // push CURRENT MINIMUM, not just val
    }

    void pop() {
        stack.pop();
        minStack.pop();  // must stay in sync
    }

    int getMin() { return minStack.peek(); }  // always O(1)
}
```

**Trace:**
```
push(5): stack=[5],   minStack=[5]    ← min so far = 5
push(3): stack=[5,3], minStack=[5,3]  ← min so far = 3
push(7): stack=[5,3,7], minStack=[5,3,3]  ← min still = 3
push(2): stack=[5,3,7,2], minStack=[5,3,3,2]  ← new min = 2

getMin() = minStack.peek() = 2  ✓

pop(): stack=[5,3,7], minStack=[5,3,3]
getMin() = 3  ✓  (correctly restored after 2 was removed)
```

**Complexity:** O(1) for all operations. O(n) space (two stacks of n elements each).

---

### Problem 4: Decode String

**Problem:** `"3[a2[bc]]"` → `"abcbcabcbc"`

**Pattern:** Two stacks — one for counts, one for string prefixes.

```
"3[a2[bc]]"

char '3': k=3
char '[': push k=3 to countStack, push "" to stringStack, reset k=0, current=""
char 'a': current="a"
char '2': k=2
char '[': push k=2 to countStack, push "a" to stringStack, reset k=0, current=""
char 'b': current="b"
char 'c': current="bc"
char ']': repeat=pop(2)=2, prev=pop("a")="a"
          current = "a" + "bc"×2 = "abcbc"
char ']': repeat=pop(3)=3, prev=pop("")=""
          current = "" + "abcbc"×3 = "abcbcabcbcabcbc"

Result: "abcbcabcbcabcbc"  ✓
```

**Complexity:** O(output length) time, O(output length) space.

---

### Problem 5: Stock Span

**Problem:** For each day, count how many consecutive prior days had a price ≤ today's price (including today).

```
prices = [100, 80, 60, 70, 60, 85, 100]
spans  = [1,   1,  1,  2,  1,  4,  6]
```

Day 6 (price=100): look back — 85, 60, 70, 60, 80 are all ≤ 100, plus today = span 6.

**Complexity:** O(n) time, O(n) space.

---

### Interview Complexity Cheat Sheet

| Problem | Time | Space | Key Technique |
|---------|------|-------|---------------|
| Valid parentheses | O(n) | O(n) | Push openers, match closers |
| Evaluate RPN | O(n) | O(n) | Push numbers, pop on operator |
| Min stack | O(1) all | O(n) | Parallel minStack in sync |
| Decode string | O(output) | O(n) | Two stacks (count + string) |
| Stock span | O(n) | O(n) | Monotonic decreasing |
| Next greater element | O(n) | O(n) | Monotonic decreasing |
| Daily temperatures | O(n) | O(n) | Monotonic decreasing + distance |
| Largest rectangle | O(n) | O(n) | Monotonic increasing + sentinel |

---

## Common Mistakes to Avoid

```java
// ❌ 1. pop() or peek() without checking isEmpty()
int val = stack.pop();          // RuntimeException if empty!
if (!stack.isEmpty()) stack.pop();  // ✅ safe

// ❌ 2. Using java.util.Stack in new code
Stack<Integer> s = new Stack<>();       // ❌ legacy, synchronized overhead
Deque<Integer> s = new ArrayDeque<>();  // ✅ modern, fast

// ❌ 3. Wrong operand order in RPN evaluation
int b = stack.pop();  // right operand
int a = stack.pop();  // left operand
result = a - b;       // ✅ correct: a MINUS b
result = b - a;       // ❌ reversed!

// ❌ 4. Forgetting to clear redo stack on new action
undoStack.push(newAction);
// redoStack.clear();  ← missing! now redo contains stale actions

// ❌ 5. MinStack: pushing val instead of current minimum
minStack.push(val);               // ❌ won't reflect true minimum after pops
minStack.push(Math.min(val, minStack.peek()));  // ✅

// ❌ 6. Not popping minStack in sync with main stack
stack.pop();    // ❌ minStack now out of sync
stack.pop(); minStack.pop();  // ✅ always pop both together
```

---

## The 5 Golden Rules

```
1. Use ArrayDeque — never java.util.Stack in new code
2. Always check isEmpty() before pop() or peek()
3. Monotonic stack → O(n) for any "next greater/smaller" problem
4. Two-stack trick → O(1) getMin() without any scanning
5. Recursion = implicit stack; deep recursion = StackOverflowError → use explicit stack
```
