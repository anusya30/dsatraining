// ================================================================
//   STACKS — Complete Deep Dive in Java
// ================================================================
//   Compile:  javac Stacks.java
//   Run:      java Stacks
// ================================================================
//
//   TOPICS:
//   1. Stack Fundamentals
//   2. Implementation Approaches (Array, LinkedList, Java Stack)
//   3. Time & Space Complexity
//   4. Real-World Use Cases
//   5. Monotonic Stack Pattern
//   6. Interview-Level Problems
// ================================================================

import java.util.*;

public class Stacks {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    static void sub(String title) {
        System.out.println("\n  ── " + title + " ──");
    }


    // ============================================================
    // IMPLEMENTATION 1 — STACK USING ARRAY
    // ============================================================
    //
    //   Internal model:
    //   - A fixed-size int[] as storage
    //   - An integer 'top' pointing to the last pushed element
    //   - top = -1 means stack is empty
    //
    //   Visual for push(10), push(20), push(30):
    //
    //   index:  [0]  [1]  [2]  [3]  [4]
    //   array:  [10] [20] [30] [  ] [  ]
    //                      ↑
    //                    top=2
    //
    //   Push: arr[++top] = value   → O(1)
    //   Pop:  return arr[top--]    → O(1)
    //   Peek: return arr[top]      → O(1)
    // ============================================================
    static class ArrayStack {
        private int[] arr;
        private int top;
        private int capacity;

        ArrayStack(int capacity) {
            this.capacity = capacity;
            this.arr      = new int[capacity];
            this.top      = -1; // -1 = empty
        }

        // PUSH — O(1)
        void push(int value) {
            if (top == capacity - 1)
                throw new RuntimeException("Stack Overflow! capacity=" + capacity);
            arr[++top] = value;
        }

        // POP — O(1)
        int pop() {
            if (isEmpty())
                throw new RuntimeException("Stack Underflow! Stack is empty.");
            return arr[top--];
        }

        // PEEK — O(1)
        int peek() {
            if (isEmpty()) throw new RuntimeException("Stack is empty.");
            return arr[top];
        }

        boolean isEmpty() { return top == -1; }
        int     size()    { return top + 1;   }

        void print(String label) {
            System.out.print("  " + label + " [bottom→top]: ");
            if (isEmpty()) { System.out.println("(empty)"); return; }
            for (int i = 0; i <= top; i++) {
                System.out.print(arr[i]);
                if (i < top) System.out.print(" | ");
            }
            System.out.println("  ← top=" + arr[top] + "  size=" + size());
        }
    }


    // ============================================================
    // IMPLEMENTATION 2 — STACK USING LINKED LIST
    // ============================================================
    //
    //   Internal model:
    //   - Each element is a Node with data + next pointer
    //   - 'head' always points to the TOP of the stack
    //   - Push = insertAtHead, Pop = deleteAtHead
    //   - No fixed capacity — grows/shrinks dynamically
    //
    //   Visual for push(10), push(20), push(30):
    //
    //   head → [30|→] → [20|→] → [10|null]
    //            ↑ TOP            ↑ BOTTOM
    //
    //   Push: create node, newNode.next=head, head=newNode → O(1)
    //   Pop:  val=head.data, head=head.next                → O(1)
    // ============================================================
    static class LinkedStack {
        private static class Node {
            int data;
            Node next;
            Node(int data) { this.data = data; }
        }

        private Node head; // top of stack
        private int  size;

        // PUSH — O(1)
        void push(int value) {
            Node newNode = new Node(value);
            newNode.next = head;
            head = newNode;
            size++;
        }

        // POP — O(1)
        int pop() {
            if (isEmpty()) throw new RuntimeException("Stack Underflow!");
            int val = head.data;
            head = head.next;
            size--;
            return val;
        }

        // PEEK — O(1)
        int peek() {
            if (isEmpty()) throw new RuntimeException("Stack is empty.");
            return head.data;
        }

        boolean isEmpty() { return head == null; }
        int     size()    { return size; }

        void print(String label) {
            System.out.print("  " + label + " [bottom→top]: ");
            if (isEmpty()) { System.out.println("(empty)"); return; }
            // collect to print bottom→top
            int[] vals = new int[size];
            Node curr  = head;
            for (int i = size - 1; i >= 0; i--) {
                vals[i] = curr.data;
                curr = curr.next;
            }
            for (int i = 0; i < vals.length; i++) {
                System.out.print(vals[i]);
                if (i < vals.length - 1) System.out.print(" | ");
            }
            System.out.println("  ← top=" + head.data + "  size=" + size);
        }
    }


    // ============================================================
    // IMPLEMENTATION 3 — JAVA BUILT-IN: Deque (recommended)
    // ============================================================
    //
    //   Java's java.util.Stack is a legacy class (extends Vector).
    //   Modern Java recommends: Deque<Integer> stack = new ArrayDeque<>()
    //
    //   ArrayDeque is backed by a resizable array.
    //   push() = addFirst()    → O(1) amortized
    //   pop()  = removeFirst() → O(1)
    //   peek() = peekFirst()   → O(1)
    // ============================================================


    // ============================================================
    // SECTION 5 — MONOTONIC STACK
    // ============================================================
    //
    //   A Monotonic Stack maintains elements in strictly
    //   increasing OR decreasing order at all times.
    //
    //   Before pushing a new element, POP all elements that
    //   violate the monotonic property.
    //
    //   Two flavors:
    //   Monotonic Increasing → bottom to top: 1, 3, 5, 8 (each > previous)
    //   Monotonic Decreasing → bottom to top: 9, 6, 4, 2 (each < previous)
    //
    //   Core insight: when you POP an element because of a new incoming
    //   element, the NEW element is the ANSWER for the popped element.
    //   (e.g., next greater element, next smaller element)
    // ============================================================

    // ── Next Greater Element — O(n) time, O(n) space
    // For each element, find the first element to its RIGHT that is GREATER.
    // If none exists, answer is -1.
    //
    // Use a Monotonic Decreasing Stack (stores indices).
    // When a new element arr[i] is greater than stack top → it is the
    // "next greater" for all elements smaller than it on the stack.
    //
    // Trace for [4, 5, 2, 10, 8]:
    // i=0: stack=[], push 0(val=4)   → stack=[0]
    // i=1: arr[1]=5 > arr[0]=4 → pop 0, NGE[0]=5. Push 1 → stack=[1]
    // i=2: arr[2]=2 < arr[1]=5 → push 2 → stack=[1,2]
    // i=3: arr[3]=10> arr[2]=2 → pop 2, NGE[2]=10
    //      arr[3]=10> arr[1]=5 → pop 1, NGE[1]=10. Push 3 → stack=[3]
    // i=4: arr[4]=8 < arr[3]=10→ push 4 → stack=[3,4]
    // End: remaining in stack → NGE=-1  → NGE[3]=-1, NGE[4]=-1
    //
    // Result: [5, 10, 10, -1, -1]
    static int[] nextGreaterElement(int[] arr) {
        int n   = arr.length;
        int[] result = new int[n];
        Arrays.fill(result, -1); // default: no greater element

        Deque<Integer> stack = new ArrayDeque<>(); // stores INDICES

        for (int i = 0; i < n; i++) {
            // pop all indices whose values are LESS than current
            while (!stack.isEmpty() && arr[stack.peek()] < arr[i]) {
                int idx = stack.pop();
                result[idx] = arr[i]; // current element is the NGE for idx
            }
            stack.push(i);
        }
        return result;
    }

    // ── Next Smaller Element — O(n) time, O(n) space
    // Mirror of NGE but with Monotonic Increasing Stack.
    // Pop when new element is SMALLER than top.
    static int[] nextSmallerElement(int[] arr) {
        int n        = arr.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
                result[stack.pop()] = arr[i];
            }
            stack.push(i);
        }
        return result;
    }

    // ── Daily Temperatures — O(n) time, O(n) space
    // For each day, find how many days until a warmer temperature.
    // Variant of "next greater element" but stores the DISTANCE.
    //
    // Real use case: weather APIs, trading signals ("days until price rises")
    static int[] dailyTemperatures(int[] temps) {
        int n        = temps.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // indices

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temps[stack.peek()] < temps[i]) {
                int idx    = stack.pop();
                result[idx] = i - idx; // distance between indices = days waited
            }
            stack.push(i);
        }
        return result;
    }

    // ── Largest Rectangle in Histogram — O(n) time, O(n) space
    // For each bar, find the maximum rectangle that can be formed
    // using that bar as the shortest (limiting) bar.
    //
    // Key insight: a rectangle extends LEFT until a shorter bar is hit,
    // and RIGHT until a shorter bar is hit.
    // Use monotonic increasing stack to find those boundaries.
    static int largestRectangle(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;
        int n       = heights.length;

        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i]; // sentinel 0 at end
            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                int height = heights[stack.pop()];
                int width  = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }


    // ============================================================
    // INTERVIEW PROBLEMS
    // ============================================================

    // ── PROBLEM 1: VALID PARENTHESES — O(n) time, O(n) space
    // Given a string of brackets, check if it is valid.
    // Valid: (), {[]}, ({[]})
    // Invalid: (], ([)], {
    //
    // Rule: every opening bracket must be closed by the same type
    //       in the correct order.
    //
    // Approach: push opening brackets onto stack.
    // On closing bracket: top must be the matching opener.
    static boolean isValidParentheses(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);                    // push opener
            } else {
                if (stack.isEmpty()) return false; // closer with no opener
                char top = stack.pop();
                if (c == ')' && top != '(') return false;
                if (c == ']' && top != '[') return false;
                if (c == '}' && top != '{') return false;
            }
        }
        return stack.isEmpty(); // must have no unmatched openers left
    }

    // ── PROBLEM 2: EVALUATE REVERSE POLISH NOTATION — O(n) time, O(n) space
    // RPN (postfix) eliminates the need for parentheses.
    // "2 3 + 4 *" = (2+3)*4 = 20
    // "5 1 2 + 4 * + 3 -" = 5+((1+2)*4)-3 = 14
    //
    // Rule: push numbers. On operator, pop two, apply, push result.
    static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") ||
                token.equals("*") || token.equals("/")) {
                int b = stack.pop(); // second operand
                int a = stack.pop(); // first operand
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            } else {
                stack.push(Integer.parseInt(token)); // push number
            }
        }
        return stack.pop(); // final result
    }

    // ── PROBLEM 3: MIN STACK — O(1) for all operations including getMin()
    // Design a stack that supports push, pop, peek, and getMin() in O(1).
    //
    // Naive approach: scan all elements for min → O(n). Not acceptable.
    //
    // Smart approach: maintain a PARALLEL min stack.
    // minStack always tracks the current minimum.
    // When we push x: minStack.push(min(x, minStack.peek()))
    // When we pop:    also pop minStack (keep in sync)
    static class MinStack {
        private Deque<Integer> stack    = new ArrayDeque<>();
        private Deque<Integer> minStack = new ArrayDeque<>(); // tracks min at each level

        void push(int val) {
            stack.push(val);
            // minStack tracks the MINIMUM seen up to this level
            int currentMin = minStack.isEmpty() ? val : Math.min(val, minStack.peek());
            minStack.push(currentMin);
        }

        int pop() {
            minStack.pop(); // keep in sync
            return stack.pop();
        }

        int peek()   { return stack.peek();    }
        int getMin() { return minStack.peek(); } // always O(1)!
        boolean isEmpty() { return stack.isEmpty(); }

        void print(String label) {
            System.out.println("  " + label + " → top=" +
                    (isEmpty() ? "empty" : peek()) +
                    "  min=" + (isEmpty() ? "empty" : getMin()));
        }
    }

    // ── PROBLEM 4: DECODE STRING — O(n) time, O(n) space
    // "3[a2[bc]]" → "abcbcabcbcabcbc"
    // "2[ab3[c]]" → "abcccabccc"
    //
    // Approach: two stacks — one for counts, one for strings.
    // On '[': push current string and current count to stacks, reset both.
    // On ']': pop count and prev string. Repeat current k times, append to prev.
    static String decodeString(String s) {
        Deque<Integer> countStack  = new ArrayDeque<>();
        Deque<String>  stringStack = new ArrayDeque<>();
        StringBuilder current = new StringBuilder();
        int k = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0'); // handle multi-digit numbers
            } else if (c == '[') {
                countStack.push(k);             // save count
                stringStack.push(current.toString()); // save current string
                current = new StringBuilder(); // reset
                k = 0;
            } else if (c == ']') {
                int repeat      = countStack.pop();
                String prev     = stringStack.pop();
                String repeated = current.toString().repeat(repeat);
                current         = new StringBuilder(prev + repeated);
            } else {
                current.append(c);
            }
        }
        return current.toString();
    }

    // ── PROBLEM 5: LARGEST RECTANGLE IN HISTOGRAM
    // Already defined above as largestRectangle()

    // ── PROBLEM 6: STOCK SPAN PROBLEM — O(n) time, O(n) space
    // For each day, find the number of consecutive days BEFORE it
    // (including itself) where stock price was ≤ today's price.
    //
    // Example: prices = [100, 80, 60, 70, 60, 85, 100]
    //          spans  = [1,   1,  1,  2,  1,  4,  6  ]
    //
    // Approach: monotonic decreasing stack of indices.
    // For each price, pop all indices with price ≤ current.
    // Span = i - stack.peek() (or i+1 if stack is empty)
    static int[] stockSpan(int[] prices) {
        int n      = prices.length;
        int[] span = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // stores indices

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && prices[stack.peek()] <= prices[i]) {
                stack.pop(); // these days are "dominated" by today
            }
            span[i] = stack.isEmpty() ? i + 1 : i - stack.peek();
            stack.push(i);
        }
        return span;
    }


    // ============================================================
    //   MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         STACKS — Complete Deep Dive in Java              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");


        // ═══════════════════════════════════════════════════════
        // TOPIC 1 — STACK FUNDAMENTALS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 1 — Stack Fundamentals");

        sub("What is a Stack?");
        System.out.println("  A Stack is a LINEAR data structure following");
        System.out.println("  LIFO — Last In, First Out.");
        System.out.println();
        System.out.println("  Think of a stack of plates:");
        System.out.println("  → You can only ADD a plate to the TOP");
        System.out.println("  → You can only REMOVE a plate from the TOP");
        System.out.println("  → You cannot access plates in the middle");

        sub("Core Operations");
        System.out.println("  push(x)  → Add element x to the TOP     → O(1)");
        System.out.println("  pop()    → Remove element from the TOP   → O(1)");
        System.out.println("  peek()   → View TOP without removing      → O(1)");
        System.out.println("  isEmpty()→ Check if stack is empty        → O(1)");
        System.out.println("  size()   → Number of elements             → O(1)");

        sub("LIFO Visualization");
        System.out.println("  push(10) → [ 10 ]");
        System.out.println("  push(20) → [ 10 | 20 ]");
        System.out.println("  push(30) → [ 10 | 20 | 30 ]  ← top");
        System.out.println("  pop()    → returns 30 → [ 10 | 20 ]  ← top");
        System.out.println("  peek()   → returns 20 (not removed) → [ 10 | 20 ]");
        System.out.println("  pop()    → returns 20 → [ 10 ]");
        System.out.println("  pop()    → returns 10 → [ ]  ← empty");

        sub("Stack vs Queue — Key Difference");
        System.out.println("  Stack → LIFO: last item in is first item out (plates)");
        System.out.println("  Queue → FIFO: first item in is first item out (ticket line)");
        System.out.println();
        System.out.println("  Push order: 1, 2, 3");
        System.out.println("  Stack pop order: 3, 2, 1  ← reversed");
        System.out.println("  Queue poll order: 1, 2, 3 ← same order");


        // ═══════════════════════════════════════════════════════
        // TOPIC 2 — IMPLEMENTATION APPROACHES
        // ═══════════════════════════════════════════════════════
        section("TOPIC 2 — Implementation Approaches");

        // ── ARRAY STACK
        sub("Approach 1 — Array-based Stack");
        ArrayStack arrStack = new ArrayStack(8);
        System.out.println("  Created ArrayStack with capacity 8, top=-1 (empty)");

        arrStack.push(10);
        arrStack.push(20);
        arrStack.push(30);
        arrStack.push(40);
        arrStack.print("After push(10,20,30,40)");

        System.out.println("  peek()  = " + arrStack.peek() + "   ← top element, not removed");
        System.out.println("  pop()   = " + arrStack.pop());
        System.out.println("  pop()   = " + arrStack.pop());
        arrStack.print("After two pops");
        System.out.println("  size()  = " + arrStack.size());
        System.out.println("  isEmpty = " + arrStack.isEmpty());
        System.out.println();
        System.out.println("  Advantage: Cache-friendly contiguous memory, no pointer overhead");
        System.out.println("  Limitation: Fixed capacity — must know max size upfront");

        // ── LINKED STACK
        sub("Approach 2 — LinkedList-based Stack");
        LinkedStack lnkStack = new LinkedStack();
        System.out.println("  Created LinkedStack — dynamic, no capacity limit");

        lnkStack.push(10);
        lnkStack.push(20);
        lnkStack.push(30);
        lnkStack.push(40);
        lnkStack.print("After push(10,20,30,40)");

        System.out.println("  peek()  = " + lnkStack.peek());
        System.out.println("  pop()   = " + lnkStack.pop());
        System.out.println("  pop()   = " + lnkStack.pop());
        lnkStack.print("After two pops");
        System.out.println();
        System.out.println("  Advantage: No overflow risk, grows/shrinks dynamically");
        System.out.println("  Limitation: ~3× memory per node (data + next + obj header)");

        // ── JAVA BUILT-IN
        sub("Approach 3 — Java Built-in: ArrayDeque (RECOMMENDED)");
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(10); stack.push(20); stack.push(30);
        System.out.println("  Deque<Integer> stack = new ArrayDeque<>()");
        System.out.println("  push(10,20,30) → top: " + stack.peek());
        System.out.println("  pop()          = " + stack.pop());
        System.out.println("  peek()         = " + stack.peek());
        System.out.println("  size()         = " + stack.size());
        System.out.println();
        System.out.println("  Why NOT java.util.Stack?");
        System.out.println("  → Stack extends Vector (synchronized on every method)");
        System.out.println("  → Vector is legacy — inefficient in single-threaded apps");
        System.out.println("  → ArrayDeque is 2x faster in benchmarks");

        sub("Comparison Table");
        System.out.println("  ┌──────────────────┬────────────┬──────────────┬──────────────────┐");
        System.out.println("  │  Approach        │  Capacity  │  Memory      │  Best For        │");
        System.out.println("  ├──────────────────┼────────────┼──────────────┼──────────────────┤");
        System.out.println("  │  Array Stack     │  Fixed     │  Low         │  Known max size  │");
        System.out.println("  │  LinkedList Stack│  Dynamic   │  Higher      │  Unknown size    │");
        System.out.println("  │  ArrayDeque      │  Dynamic   │  Low+resize  │  General use ✅  │");
        System.out.println("  │  java.util.Stack │  Dynamic   │  Medium      │  Avoid (legacy)  │");
        System.out.println("  └──────────────────┴────────────┴──────────────┴──────────────────┘");


        // ═══════════════════════════════════════════════════════
        // TOPIC 3 — TIME & SPACE COMPLEXITY
        // ═══════════════════════════════════════════════════════
        section("TOPIC 3 — Time & Space Complexity");

        sub("All core operations are O(1)");
        System.out.println("  ┌────────────────┬──────────────────────────────────────────────┐");
        System.out.println("  │  Operation     │  Why O(1)                                    │");
        System.out.println("  ├────────────────┼──────────────────────────────────────────────┤");
        System.out.println("  │  push(x)       │  Array: arr[++top]=x.  LL: newNode→head      │");
        System.out.println("  │  pop()         │  Array: arr[top--].    LL: head=head.next     │");
        System.out.println("  │  peek()        │  Array: arr[top].      LL: head.data          │");
        System.out.println("  │  isEmpty()     │  Array: top==-1.       LL: head==null         │");
        System.out.println("  │  size()        │  Stored as a counter field                    │");
        System.out.println("  └────────────────┴──────────────────────────────────────────────┘");

        sub("Space Complexity");
        System.out.println("  Overall: O(n) — n elements stored in memory");
        System.out.println();
        System.out.println("  Array Stack   → O(capacity)  fixed allocation even if not full");
        System.out.println("  LinkedList    → O(n)         exact, but ~3x overhead per node");
        System.out.println("  ArrayDeque    → O(n)         slightly over-allocated (like ArrayList)");

        sub("Call Stack — O(n) implicit space in recursion");
        System.out.println("  Recursive functions use the JVM CALL STACK implicitly.");
        System.out.println("  factorial(5) uses 5 stack frames → O(n) space.");
        System.out.println("  Deep recursion → StackOverflowError.");
        System.out.println("  Iterative solution with explicit stack avoids this.");
        System.out.println();
        System.out.println("  Recursive factorial(10000) → StackOverflowError");
        System.out.println("  Iterative with explicit stack → works fine");

        sub("Amortized O(1) for ArrayDeque resize");
        System.out.println("  ArrayDeque doubles capacity when full (like ArrayList).");
        System.out.println("  Occasional resize costs O(n) but spread over n pushes");
        System.out.println("  → amortized O(1) per push.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 4 — REAL-WORLD USE CASES
        // ═══════════════════════════════════════════════════════
        section("TOPIC 4 — Real-World Use Cases");

        // ── USE CASE 1: BALANCED BRACKETS
        sub("Use Case 1 — Balanced Parentheses Checker (IDE / Compiler)");
        String[] expressions = {
            "({[]})",
            "((()))",
            "([)]",
            "{[}",
            "",
            "((("
        };
        System.out.println("  Expression     Valid?  (used in: IDE syntax highlighting,");
        System.out.println("                          compiler parsers, JSON validators)");
        System.out.println("  ───────────────────────");
        for (String expr : expressions) {
            System.out.printf("  %-15s %s%n",
                    "\"" + expr + "\"",
                    isValidParentheses(expr) ? "✅ Valid" : "❌ Invalid");
        }

        // ── USE CASE 2: UNDO/REDO
        sub("Use Case 2 — Undo/Redo System (Text Editor / Photoshop)");
        Deque<String> undoStack = new ArrayDeque<>();
        Deque<String> redoStack = new ArrayDeque<>();

        // simulate typing
        String[] actions = {"Type 'H'", "Type 'e'", "Type 'l'", "Type 'l'", "Type 'o'"};
        for (String a : actions) {
            undoStack.push(a);
            redoStack.clear(); // new action clears redo history
        }
        System.out.println("  Actions performed: " + Arrays.toString(actions));
        System.out.println("  Undo stack (top→bottom): " + undoStack);

        System.out.println("\n  Ctrl+Z (Undo): " + undoStack.pop() + " undone");
        redoStack.push("Type 'o'");
        System.out.println("  Ctrl+Z (Undo): " + undoStack.pop() + " undone");
        redoStack.push("Type 'l'");
        System.out.println("  Undo stack now: " + undoStack);

        System.out.println("\n  Ctrl+Y (Redo): " + redoStack.pop() + " redone");
        System.out.println("  Undo stack now: " + undoStack);

        // ── USE CASE 3: EXPRESSION EVALUATION
        sub("Use Case 3 — Expression Evaluation (Calculator / Compiler)");
        String[] rpnExpr  = {"2", "3", "+", "4", "*"};   // (2+3)*4 = 20
        String[] rpnExpr2 = {"5", "1", "2", "+", "4", "*", "+", "3", "-"}; // =14

        System.out.println("  Reverse Polish Notation eliminates need for parentheses.");
        System.out.println("  Rule: push numbers, on operator pop two and compute.");
        System.out.println();
        System.out.printf("  %-35s = %d  (=(2+3)×4)%n",
                Arrays.toString(rpnExpr), evalRPN(rpnExpr));
        System.out.printf("  %-35s = %d  (=5+((1+2)×4)-3)%n",
                Arrays.toString(rpnExpr2), evalRPN(rpnExpr2));

        // ── USE CASE 4: BROWSER HISTORY
        sub("Use Case 4 — Browser Back Navigation");
        Deque<String> backStack    = new ArrayDeque<>();
        Deque<String> forwardStack = new ArrayDeque<>();

        String[] pages = {"google.com", "github.com", "stackoverflow.com", "claude.ai"};
        for (String page : pages) {
            backStack.push(page);
            forwardStack.clear();
        }
        System.out.println("  Pages visited:  " + Arrays.toString(pages));
        System.out.println("  Current page:   " + backStack.peek());
        System.out.println();
        String current = backStack.pop();
        forwardStack.push(current);
        System.out.println("  Press Back:     now at → " + backStack.peek());
        String fwd = forwardStack.pop();
        backStack.push(fwd);
        System.out.println("  Press Forward:  now at → " + backStack.peek());

        // ── USE CASE 5: FUNCTION CALL STACK
        sub("Use Case 5 — JVM Call Stack (Method Execution)");
        System.out.println("  Every method call pushes a STACK FRAME onto the JVM call stack.");
        System.out.println("  Frame contains: local vars, return address, params.");
        System.out.println();
        System.out.println("  main() calls A():");
        System.out.println("  [ main frame ]");
        System.out.println("  [ main frame | A frame ]");
        System.out.println("  [ main frame | A frame | B frame ]   ← A calls B");
        System.out.println("  [ main frame | A frame ]              ← B returns");
        System.out.println("  [ main frame ]                        ← A returns");
        System.out.println("  []                                    ← main returns");
        System.out.println();
        System.out.println("  StackOverflowError = stack frame limit exceeded (deep recursion)");


        // ═══════════════════════════════════════════════════════
        // TOPIC 5 — MONOTONIC STACK PATTERN
        // ═══════════════════════════════════════════════════════
        section("TOPIC 5 — Monotonic Stack Pattern");

        System.out.println("  A Monotonic Stack maintains elements in sorted order.");
        System.out.println("  Elements that violate the order are POPPED before pushing new.");
        System.out.println("  The popping event IS the answer for those elements.");

        sub("Monotonic Decreasing Stack — Next Greater Element");
        int[] ngeArr = {4, 5, 2, 10, 8};
        int[] ngeResult = nextGreaterElement(ngeArr);

        System.out.println("  Input:  " + Arrays.toString(ngeArr));
        System.out.println("  NGE:    " + Arrays.toString(ngeResult));
        System.out.println();
        System.out.println("  Step-by-step trace:");
        System.out.println("  i=0: val=4  → stack empty, push 0     | stack=[0(4)]");
        System.out.println("  i=1: val=5  → 5>4, pop 0, NGE[0]=5    | push 1     | stack=[1(5)]");
        System.out.println("  i=2: val=2  → 2<5, push 2             | stack=[1(5),2(2)]");
        System.out.println("  i=3: val=10 → 10>2,pop 2,NGE[2]=10    | 10>5,pop 1,NGE[1]=10");
        System.out.println("               push 3                    | stack=[3(10)]");
        System.out.println("  i=4: val=8  → 8<10, push 4            | stack=[3(10),4(8)]");
        System.out.println("  End: remaining indices → NGE=-1        | NGE[3]=-1, NGE[4]=-1");
        System.out.println("  Result: [5, 10, 10, -1, -1]  ✓");

        sub("Monotonic Increasing Stack — Next Smaller Element");
        int[] nseArr    = {4, 5, 2, 10, 8};
        int[] nseResult = nextSmallerElement(nseArr);
        System.out.println("  Input:  " + Arrays.toString(nseArr));
        System.out.println("  NSE:    " + Arrays.toString(nseResult));
        System.out.println("  Mirror of NGE but pops when new element is SMALLER.");

        sub("Daily Temperatures — variant of NGE");
        int[] temps   = {73, 74, 75, 71, 69, 72, 76, 73};
        int[] waitDays = dailyTemperatures(temps);
        System.out.println("  Temps:    " + Arrays.toString(temps));
        System.out.println("  WaitDays: " + Arrays.toString(waitDays));
        System.out.println();
        for (int i = 0; i < temps.length; i++) {
            if (waitDays[i] > 0)
                System.out.printf("  Day %d (%d°): wait %d day(s) for warmer temp%n",
                        i, temps[i], waitDays[i]);
            else
                System.out.printf("  Day %d (%d°): no warmer day ahead%n", i, temps[i]);
        }

        sub("Largest Rectangle in Histogram");
        int[] heights = {2, 1, 5, 6, 2, 3};
        int maxArea   = largestRectangle(heights);
        System.out.println("  Heights:  " + Arrays.toString(heights));
        System.out.println("  Max Area: " + maxArea + "  (bars 5 and 6 form 5×2=10)");
        System.out.println();
        System.out.println("  Histogram:");
        for (int i = heights.length - 1; i >= 0; i--) {
            System.out.print("  ");
            for (int j = 0; j < heights.length; j++) {
                System.out.print(heights[j] > i ? " █ " : "   ");
            }
            System.out.println();
        }
        System.out.println("   2  1  5  6  2  3  ← heights");

        sub("Monotonic Stack — When to Use");
        System.out.println("  Pattern trigger words in problems:");
        System.out.println("  → 'next greater element'      → Monotonic Decreasing");
        System.out.println("  → 'next smaller element'      → Monotonic Increasing");
        System.out.println("  → 'previous greater element'  → Monotonic Decreasing (traverse right→left)");
        System.out.println("  → 'days until warmer'         → Variant of NGE");
        System.out.println("  → 'largest rectangle'         → Monotonic Increasing");
        System.out.println("  → 'trapping rain water'       → Monotonic Decreasing");
        System.out.println("  → 'stock span'                → Monotonic Decreasing");


        // ═══════════════════════════════════════════════════════
        // TOPIC 6 — INTERVIEW-LEVEL PROBLEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 6 — Interview-Level Problems");

        sub("Problem 1: Valid Parentheses");
        String[] tests = {"()[]{}", "([{}])", "(]", "([)]", "{[]}"};
        for (String t : tests) {
            System.out.printf("  %-12s → %s%n", "\"" + t + "\"",
                    isValidParentheses(t) ? "✅ Valid" : "❌ Invalid");
        }
        System.out.println("  Technique: push opener, match closer with stack top. O(n)/O(n)");

        sub("Problem 2: Evaluate Reverse Polish Notation");
        String[][] rpns = {
            {"2","3","+","4","*"},       // 20
            {"5","1","2","+","4","*","+","3","-"}, // 14
            {"4","13","5","/","+"}        // 6
        };
        for (String[] rpn : rpns) {
            System.out.printf("  %-45s = %d%n",
                    Arrays.toString(rpn), evalRPN(rpn));
        }
        System.out.println("  Technique: push numbers, pop two on operator. O(n)/O(n)");

        sub("Problem 3: Min Stack");
        MinStack ms = new MinStack();
        ms.push(5); ms.print("push(5)");
        ms.push(3); ms.print("push(3)");
        ms.push(7); ms.print("push(7)");
        ms.push(2); ms.print("push(2)");
        ms.push(4); ms.print("push(4)");
        System.out.println();
        ms.pop();   ms.print("pop() → 4 removed");
        ms.pop();   ms.print("pop() → 2 removed");
        System.out.println("  Technique: parallel minStack in sync. getMin() always O(1)");

        sub("Problem 4: Decode String");
        String[] encoded = {"3[a]", "2[bc]", "3[a2[c]]", "2[ab3[c]]"};
        for (String e : encoded) {
            System.out.printf("  %-15s → \"%s\"%n", "\"" + e + "\"", decodeString(e));
        }
        System.out.println("  Technique: two stacks (counts + strings). O(output length)");

        sub("Problem 5: Stock Span");
        int[] prices = {100, 80, 60, 70, 60, 85, 100};
        int[] spans  = stockSpan(prices);
        System.out.println("  Prices: " + Arrays.toString(prices));
        System.out.println("  Spans:  " + Arrays.toString(spans));
        System.out.println();
        for (int i = 0; i < prices.length; i++) {
            System.out.printf("  Day %d: price=%3d → span=%d " +
                    "(last %d day(s) with price ≤ %d)%n",
                    i, prices[i], spans[i], spans[i], prices[i]);
        }
        System.out.println("  Technique: monotonic decreasing stack of indices. O(n)/O(n)");

        sub("Problem 6: Largest Rectangle in Histogram");
        int[][] histTests = {
            {2, 1, 5, 6, 2, 3},
            {2, 4},
            {1, 1, 1, 1},
            {6, 2, 5, 4, 5, 1, 6}
        };
        for (int[] h : histTests) {
            System.out.printf("  heights=%-25s → maxArea=%d%n",
                    Arrays.toString(h), largestRectangle(h));
        }
        System.out.println("  Technique: monotonic increasing stack. O(n)/O(n)");

        // GRAND SUMMARY
        section("GRAND SUMMARY");
        System.out.println();
        System.out.println("  Stack = LIFO — Last In, First Out.");
        System.out.println("  All core operations (push/pop/peek) = O(1).");
        System.out.println("  Space = O(n) for n elements.");
        System.out.println();
        System.out.println("  IMPLEMENTATIONS:");
        System.out.println("  → Array Stack  : fixed capacity, cache-friendly");
        System.out.println("  → Linked Stack : dynamic, pointer overhead");
        System.out.println("  → ArrayDeque   : best general-purpose choice in Java");
        System.out.println();
        System.out.println("  PATTERNS:");
        System.out.println("  → Balanced brackets   : push openers, match closers");
        System.out.println("  → Expression eval     : push operands, pop on operator");
        System.out.println("  → Monotonic stack     : next greater/smaller in O(n)");
        System.out.println("  → Min stack           : parallel tracking in O(1)");
        System.out.println();
        System.out.println("  GOLDEN RULES:");
        System.out.println("  1. Use ArrayDeque — never java.util.Stack in new code");
        System.out.println("  2. Always check isEmpty() before pop()/peek()");
        System.out.println("  3. Monotonic stack → O(n) solution for 'next greater/smaller'");
        System.out.println("  4. Two-stack trick → getMin() in O(1) without scanning");
        System.out.println("  5. Recursion = implicit stack. Deep recursion = StackOverflowError");
    }
}
