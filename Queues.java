// ================================================================
//   QUEUES — Complete Deep Dive in Java
// ================================================================
//   Compile:  javac Queues.java
//   Run:      java Queues
// ================================================================
//
//   TOPICS:
//   1. Queue Fundamentals (FIFO)
//   2. Implementation Approaches
//   3. Circular Queue
//   4. Deque (Double-Ended Queue)
//   5. Real-World Use Cases
//   6. BFS & Sliding Window
//   7. Interview-Level Problems
// ================================================================

import java.util.*;

public class Queues {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    static void sub(String title) {
        System.out.println("\n  ── " + title + " ──");
    }


    // ============================================================
    // IMPLEMENTATION 1 — QUEUE USING ARRAY (Linear)
    // ============================================================
    //
    //   Internal model:
    //   - Fixed int[] as storage
    //   - 'front' points to the first element (dequeue side)
    //   - 'rear'  points to the last element  (enqueue side)
    //   - Both start at -1
    //
    //   Visual after enqueue(10, 20, 30):
    //   index:  [0]  [1]  [2]  [3]  [4]
    //   array:  [10] [20] [30] [  ] [  ]
    //            ↑              ↑
    //          front=0        rear=2
    //
    //   PROBLEM: after many enqueue+dequeue operations,
    //   front drifts right → wasted space at left → "false full"
    //   Solution: Circular Queue (covered in Topic 3)
    //
    //   Time: enqueue O(1), dequeue O(1), peek O(1)
    //   Space: O(capacity)
    // ============================================================
    static class ArrayQueue {
        private int[] arr;
        private int front, rear, size, capacity;

        ArrayQueue(int capacity) {
            this.capacity = capacity;
            this.arr      = new int[capacity];
            this.front    = 0;
            this.rear     = -1;
            this.size     = 0;
        }

        // ENQUEUE — add to rear — O(1)
        void enqueue(int value) {
            if (size == capacity)
                throw new RuntimeException("Queue is Full!");
            rear++;
            arr[rear] = value;
            size++;
        }

        // DEQUEUE — remove from front — O(1)
        int dequeue() {
            if (isEmpty())
                throw new RuntimeException("Queue is Empty!");
            int val = arr[front];
            front++;
            size--;
            return val;
        }

        // PEEK — view front without removing — O(1)
        int peek()    { return arr[front]; }
        boolean isEmpty() { return size == 0; }
        int size()        { return size; }

        void print(String label) {
            System.out.print("  " + label + " [front→rear]: ");
            if (isEmpty()) { System.out.println("(empty)"); return; }
            for (int i = front; i <= rear; i++) {
                System.out.print(arr[i]);
                if (i < rear) System.out.print(" → ");
            }
            System.out.println("  front=" + arr[front] +
                    "  rear=" + arr[rear] + "  size=" + size);
        }
    }


    // ============================================================
    // IMPLEMENTATION 2 — QUEUE USING LINKED LIST
    // ============================================================
    //
    //   Internal model:
    //   - head → FRONT (dequeue end)
    //   - tail → REAR  (enqueue end)
    //
    //   Visual after enqueue(10, 20, 30):
    //   head(front) → [10] → [20] → [30] ← tail(rear)
    //
    //   Enqueue: create node, tail.next=newNode, tail=newNode → O(1)
    //   Dequeue: val=head.data, head=head.next              → O(1)
    //
    //   No capacity limit — grows dynamically.
    // ============================================================
    static class LinkedQueue {
        private static class Node {
            int  data;
            Node next;
            Node(int data) { this.data = data; }
        }

        private Node head; // front
        private Node tail; // rear
        private int  size;

        // ENQUEUE — add to tail — O(1)
        void enqueue(int value) {
            Node newNode = new Node(value);
            if (tail != null) tail.next = newNode;
            tail = newNode;
            if (head == null) head = tail;
            size++;
        }

        // DEQUEUE — remove from head — O(1)
        int dequeue() {
            if (isEmpty()) throw new RuntimeException("Queue is Empty!");
            int val = head.data;
            head = head.next;
            if (head == null) tail = null; // queue now empty
            size--;
            return val;
        }

        int  peek()    { return head.data; }
        boolean isEmpty() { return head == null; }
        int  size()    { return size; }

        void print(String label) {
            System.out.print("  " + label + " [front→rear]: ");
            if (isEmpty()) { System.out.println("(empty)"); return; }
            Node curr = head;
            while (curr != null) {
                System.out.print(curr.data);
                if (curr.next != null) System.out.print(" → ");
                curr = curr.next;
            }
            System.out.println("  front=" + head.data +
                    "  rear=" + tail.data + "  size=" + size);
        }
    }


    // ============================================================
    // IMPLEMENTATION 3 — CIRCULAR QUEUE (Ring Buffer)
    // ============================================================
    //
    //   PROBLEM WITH LINEAR ARRAY QUEUE:
    //   enqueue(10,20,30), dequeue(), dequeue():
    //   [_][_][30][  ][  ]   front=2, rear=2
    //   Indices 0,1 are wasted! If we keep enqueuing we hit
    //   a "false overflow" even though space is available.
    //
    //   CIRCULAR QUEUE SOLUTION:
    //   Wrap around using modulo arithmetic:
    //   rear = (rear + 1) % capacity
    //
    //   Visualize as a RING:
    //         [10]
    //      [50]  [20]
    //         [40][30]
    //   front and rear chase each other around the ring.
    //
    //   FULL condition:  (rear + 1) % capacity == front
    //   EMPTY condition: front == rear (both at same spot, size=0)
    //
    //   Time: all operations O(1)
    //   Space: O(capacity) — fixed, no waste
    // ============================================================
    static class CircularQueue {
        private int[] arr;
        private int front, rear, size, capacity;

        CircularQueue(int capacity) {
            this.capacity = capacity;
            this.arr      = new int[capacity];
            this.front    = 0;
            this.rear     = 0;
            this.size     = 0;
        }

        // ENQUEUE — O(1)
        boolean enqueue(int value) {
            if (isFull()) return false; // cannot enqueue
            arr[rear] = value;
            rear = (rear + 1) % capacity; // WRAP AROUND
            size++;
            return true;
        }

        // DEQUEUE — O(1)
        int dequeue() {
            if (isEmpty()) throw new RuntimeException("Queue is Empty!");
            int val = arr[front];
            front = (front + 1) % capacity; // WRAP AROUND
            size--;
            return val;
        }

        int  peek()    { return arr[front]; }
        boolean isEmpty() { return size == 0; }
        boolean isFull()  { return size == capacity; }
        int  size()    { return size; }

        void print(String label) {
            System.out.print("  " + label + " [circular, front→rear]: ");
            if (isEmpty()) { System.out.println("(empty)"); return; }
            int idx = front;
            for (int i = 0; i < size; i++) {
                System.out.print(arr[idx]);
                if (i < size - 1) System.out.print(" → ");
                idx = (idx + 1) % capacity;
            }
            System.out.println("  (capacity=" + capacity + ", size=" + size + ")");
        }
    }


    // ============================================================
    // IMPLEMENTATION 4 — PRIORITY QUEUE
    // ============================================================
    //
    //   Unlike regular FIFO queue, PriorityQueue dequeues the
    //   element with HIGHEST PRIORITY (smallest by default in Java).
    //
    //   Backed by a MIN-HEAP internally.
    //   Enqueue: O(log n)  — heap insertion
    //   Dequeue: O(log n)  — heap removal + rebalance
    //   Peek:    O(1)      — min always at root
    //
    //   For MAX priority: use Collections.reverseOrder()
    //   or custom Comparator.
    // ============================================================


    // ============================================================
    // DEQUE IMPLEMENTATION — Double-Ended Queue
    // ============================================================
    //
    //   A Deque (deck) supports insert and remove from BOTH ends.
    //   It generalises both Stack (LIFO) and Queue (FIFO).
    //
    //   FRONT ← [A] [B] [C] [D] → REAR
    //
    //   addFirst()   → add to front — O(1)
    //   addLast()    → add to rear  — O(1)
    //   removeFirst()→ remove from front — O(1)
    //   removeLast() → remove from rear  — O(1)
    //   peekFirst()  → view front — O(1)
    //   peekLast()   → view rear  — O(1)
    //
    //   Java: ArrayDeque implements Deque
    // ============================================================


    // ============================================================
    // BFS — Breadth-First Search on a Graph
    // ============================================================
    //
    //   BFS explores a graph LEVEL BY LEVEL using a queue.
    //   Start at source → visit all neighbours → then their neighbours...
    //
    //   Graph (adjacency list):
    //   0 → [1, 2]
    //   1 → [0, 3, 4]
    //   2 → [0, 5]
    //   3 → [1]
    //   4 → [1]
    //   5 → [2]
    //
    //   BFS from node 0:
    //   Level 0: [0]
    //   Level 1: [1, 2]      (neighbours of 0)
    //   Level 2: [3, 4, 5]   (neighbours of 1 and 2)
    //
    //   Queue state:
    //   Start:    [0]
    //   Dequeue 0, enqueue 1,2  → [1, 2]
    //   Dequeue 1, enqueue 3,4  → [2, 3, 4]
    //   Dequeue 2, enqueue 5    → [3, 4, 5]
    //   Dequeue 3 (no new)      → [4, 5]
    //   Dequeue 4 (no new)      → [5]
    //   Dequeue 5 (no new)      → []  done
    //
    //   Time:  O(V + E)  V=vertices, E=edges
    //   Space: O(V)      visited array + queue
    // ============================================================
    static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer>    order   = new ArrayList<>();
        boolean[]        visited = new boolean[graph.size()];
        Queue<Integer>   queue   = new LinkedList<>();

        visited[start] = true;
        queue.offer(start);                           // enqueue start

        while (!queue.isEmpty()) {
            int node = queue.poll();                  // dequeue front
            order.add(node);

            for (int neighbour : graph.get(node)) {
                if (!visited[neighbour]) {
                    visited[neighbour] = true;
                    queue.offer(neighbour);           // enqueue unvisited neighbours
                }
            }
        }
        return order;
    }

    // BFS — Level-by-Level traversal (returns each level as a list)
    static List<List<Integer>> bfsLevels(Map<Integer, List<Integer>> graph, int start) {
        List<List<Integer>> levels  = new ArrayList<>();
        boolean[]           visited = new boolean[graph.size()];
        Queue<Integer>      queue   = new LinkedList<>();

        visited[start] = true;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();           // all nodes at current level
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {  // process exactly this level
                int node = queue.poll();
                level.add(node);
                for (int nb : graph.get(node)) {
                    if (!visited[nb]) { visited[nb] = true; queue.offer(nb); }
                }
            }
            levels.add(level);
        }
        return levels;
    }


    // ============================================================
    // SLIDING WINDOW MAXIMUM — Deque-based O(n)
    // ============================================================
    //
    //   Problem: given array and window size k, find the maximum
    //   in every sliding window of size k.
    //
    //   Brute force: for each window, scan k elements → O(nk). Too slow.
    //
    //   Deque approach: maintain a MONOTONIC DECREASING DEQUE of INDICES.
    //   Invariant: front of deque = index of max in current window.
    //
    //   Rules:
    //   1. Remove indices from FRONT if they're outside the window
    //   2. Remove indices from REAR while arr[rear] <= arr[i] (useless)
    //   3. Add current index i to REAR
    //   4. Front of deque = max of current window
    //
    //   Trace for arr=[3,1,2,5,4,3], k=3:
    //   i=0: dq=[0(3)]
    //   i=1: 1<3, dq=[0(3),1(1)]
    //   i=2: 2>1 pop 1; 2<3, dq=[0(3),2(2)]  → win[0,2]: max=arr[0]=3
    //   i=3: 5>2 pop 2; 5>3 pop 0, dq=[3(5)]  → win[1,3]: max=arr[3]=5
    //   i=4: 4<5, dq=[3(5),4(4)]              → win[2,4]: max=arr[3]=5
    //   i=5: front=3 still in window[3,5], 3<4, dq=[3(5),4(4),5(3)] → max=5
    //
    //   Time:  O(n) — each element pushed and popped at most once
    //   Space: O(k) — deque holds at most k indices
    // ============================================================
    static int[] slidingWindowMax(int[] arr, int k) {
        int n      = arr.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>(); // stores INDICES

        for (int i = 0; i < n; i++) {
            // Remove indices outside current window from front
            while (!dq.isEmpty() && dq.peekFirst() < i - k + 1) {
                dq.pollFirst();
            }
            // Remove indices whose values are less than arr[i] from rear
            // (they can never be the max while arr[i] is in the window)
            while (!dq.isEmpty() && arr[dq.peekLast()] <= arr[i]) {
                dq.pollLast();
            }
            dq.offerLast(i);

            // Record result once we have our first full window
            if (i >= k - 1) {
                result[i - k + 1] = arr[dq.peekFirst()]; // front = max
            }
        }
        return result;
    }


    // ============================================================
    // INTERVIEW PROBLEMS
    // ============================================================

    // ── PROBLEM 1: IMPLEMENT QUEUE USING TWO STACKS — O(1) amortized
    //
    //   Two stacks: inStack (for enqueue), outStack (for dequeue)
    //   Enqueue: always push to inStack           → O(1)
    //   Dequeue: if outStack empty, pour all from inStack → O(n) transfer
    //            else pop from outStack            → O(1)
    //
    //   Amortized analysis: each element is pushed to inStack once,
    //   transferred to outStack once, popped from outStack once = O(1) avg.
    //
    //   Trace: enqueue 1,2,3 then dequeue twice
    //   After enqueue: inStack=[1,2,3], outStack=[]
    //   Dequeue 1st: outStack empty → pour → inStack=[], outStack=[3,2,1]
    //                pop outStack → returns 1
    //   Dequeue 2nd: outStack=[3,2], pop → returns 2  (no pour needed!)
    static class QueueUsingTwoStacks {
        private Deque<Integer> inStack  = new ArrayDeque<>();
        private Deque<Integer> outStack = new ArrayDeque<>();

        void enqueue(int val) {
            inStack.push(val); // always push to inStack
        }

        int dequeue() {
            if (outStack.isEmpty()) {
                // pour ALL from inStack → outStack (reverses order → FIFO!)
                while (!inStack.isEmpty()) outStack.push(inStack.pop());
            }
            if (outStack.isEmpty()) throw new RuntimeException("Queue is Empty!");
            return outStack.pop();
        }

        int peek() {
            if (outStack.isEmpty())
                while (!inStack.isEmpty()) outStack.push(inStack.pop());
            return outStack.peek();
        }

        boolean isEmpty() { return inStack.isEmpty() && outStack.isEmpty(); }
    }

    // ── PROBLEM 2: IMPLEMENT STACK USING TWO QUEUES — O(n) push
    //
    //   Approach: on each push, enqueue to q1, then rotate q1 so
    //   the new element is at the FRONT (acting as top of stack).
    //
    //   push(x): enqueue x to q1.
    //            rotate q1: move all existing elements to back
    //            (dequeue all EXCEPT last enqueued, re-enqueue them)
    //   pop():  dequeue from q1 → O(1)
    static class StackUsingTwoQueues {
        private Queue<Integer> q1 = new LinkedList<>();
        private Queue<Integer> q2 = new LinkedList<>();

        void push(int val) {
            q2.offer(val); // put new element in empty q2
            while (!q1.isEmpty()) q2.offer(q1.poll()); // move q1 → q2
            Queue<Integer> temp = q1; q1 = q2; q2 = temp; // swap names
        }

        int pop()  { return q1.poll(); }
        int peek() { return q1.peek(); }
        boolean isEmpty() { return q1.isEmpty(); }
    }

    // ── PROBLEM 3: FIRST NON-REPEATING CHARACTER IN A STREAM — O(n)
    //
    //   As characters arrive in a stream, at each step return
    //   the first character that has appeared exactly once so far.
    //   If none, return '#'.
    //
    //   Approach:
    //   - Queue holds candidates (chars seen once so far, in order)
    //   - int[26] tracks frequency of each character
    //   - On new char c: freq[c]++, enqueue c
    //     Then drain queue front while freq[front] > 1
    //     Peek queue front = answer (first non-repeating)
    static char[] firstNonRepeating(String stream) {
        char[] result = new char[stream.length()];
        int[]  freq   = new int[26];
        Queue<Character> queue = new LinkedList<>();

        for (int i = 0; i < stream.length(); i++) {
            char c = stream.charAt(i);
            freq[c - 'a']++;
            queue.offer(c);
            // Remove front while it's repeated
            while (!queue.isEmpty() && freq[queue.peek() - 'a'] > 1) {
                queue.poll();
            }
            result[i] = queue.isEmpty() ? '#' : queue.peek();
        }
        return result;
    }

    // ── PROBLEM 4: ROTTEN ORANGES (Multi-source BFS) — O(m×n)
    //
    //   Grid: 0=empty, 1=fresh, 2=rotten
    //   Rotten oranges spread to 4-adjacent cells each minute.
    //   Return minimum minutes until all fresh oranges rot,
    //   or -1 if impossible.
    //
    //   Approach: Multi-source BFS — enqueue ALL rotten oranges first,
    //   then BFS level by level (each level = 1 minute).
    static int rottenOranges(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;

        // Enqueue ALL rotten oranges as starting points
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) queue.offer(new int[]{r, c});
                else if (grid[r][c] == 1) fresh++;
            }
        }

        if (fresh == 0) return 0; // no fresh oranges

        int minutes = 0;
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}}; // 4 directions

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            minutes++;
            for (int i = 0; i < levelSize; i++) {
                int[] cell = queue.poll();
                for (int[] d : dirs) {
                    int nr = cell[0] + d[0];
                    int nc = cell[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2; // mark rotten
                        fresh--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
        }
        return fresh == 0 ? minutes - 1 : -1;
    }

    // ── PROBLEM 5: GENERATE BINARY NUMBERS 1 TO N USING QUEUE — O(n)
    //
    //   Observe the pattern:
    //   "1" → enqueue "10", "11"
    //   "10"→ enqueue "100", "101"
    //   "11"→ enqueue "110", "111"
    //   Each dequeued string generates its two children by appending 0 and 1.
    static String[] generateBinaryNumbers(int n) {
        String[] result = new String[n];
        Queue<String> queue = new LinkedList<>();
        queue.offer("1");

        for (int i = 0; i < n; i++) {
            String front = queue.poll();
            result[i] = front;
            queue.offer(front + "0"); // left child
            queue.offer(front + "1"); // right child
        }
        return result;
    }

    // ── PROBLEM 6: TASK SCHEDULER — O(n log n) using PriorityQueue
    //
    //   Given tasks with cooldown n, find the minimum time to execute all.
    //   Strategy: always execute the most frequent task available.
    //   Use a MaxHeap (PriorityQueue with reverse order).
    static int taskScheduler(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char t : tasks) freq[t - 'A']++;

        PriorityQueue<Integer> maxHeap =
                new PriorityQueue<>(Collections.reverseOrder());
        for (int f : freq) if (f > 0) maxHeap.offer(f);

        int time = 0;
        while (!maxHeap.isEmpty()) {
            List<Integer> cooldown = new ArrayList<>();
            int cycle = n + 1; // one cycle = n+1 slots

            for (int i = 0; i < cycle && !maxHeap.isEmpty(); i++) {
                int top = maxHeap.poll() - 1; // execute task
                if (top > 0) cooldown.add(top);
                time++;
            }
            maxHeap.addAll(cooldown);
            if (!maxHeap.isEmpty()) time += (cycle - cooldown.size() - 1); // idle slots
        }
        return time;
    }


    // ============================================================
    //   MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         QUEUES — Complete Deep Dive in Java              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");


        // ═══════════════════════════════════════════════════════
        // TOPIC 1 — QUEUE FUNDAMENTALS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 1 — Queue Fundamentals (FIFO)");

        sub("What is a Queue?");
        System.out.println("  A Queue is a LINEAR data structure following");
        System.out.println("  FIFO — First In, First Out.");
        System.out.println();
        System.out.println("  Think of a ticket counter line:");
        System.out.println("  → People JOIN at the BACK (enqueue/offer)");
        System.out.println("  → People LEAVE from the FRONT (dequeue/poll)");
        System.out.println("  → First person to join = first person served");

        sub("Core Operations");
        System.out.println("  enqueue / offer  → Add element to REAR  → O(1)");
        System.out.println("  dequeue / poll   → Remove from FRONT     → O(1)");
        System.out.println("  peek / element   → View FRONT element     → O(1)");
        System.out.println("  isEmpty()        → Check if empty         → O(1)");
        System.out.println("  size()           → Number of elements      → O(1)");

        sub("FIFO Visualization");
        System.out.println("  enqueue(10): REAR→ [ 10 ] ←FRONT");
        System.out.println("  enqueue(20): REAR→ [ 10 | 20 ] ←FRONT");
        System.out.println("  enqueue(30): REAR→ [ 10 | 20 | 30 ] ←FRONT");
        System.out.println("  dequeue()  : returns 10 → REAR→ [ 20 | 30 ] ←FRONT");
        System.out.println("  dequeue()  : returns 20 → REAR→ [ 30 ] ←FRONT");
        System.out.println("  peek()     : returns 30 (not removed)");

        sub("Queue vs Stack — Key Difference");
        System.out.println("  Same enqueue order: 1, 2, 3");
        System.out.println("  Queue dequeue order: 1, 2, 3  ← SAME ORDER  (FIFO)");
        System.out.println("  Stack pop order:     3, 2, 1  ← REVERSED    (LIFO)");
        System.out.println();
        System.out.println("  Queue = fair ordering. Stack = reverse ordering.");
        System.out.println("  Queue is used when order of processing matters.");

        sub("Java Queue Interface Hierarchy");
        System.out.println("  Iterable");
        System.out.println("    └─ Collection");
        System.out.println("         └─ Queue              ← interface");
        System.out.println("              ├─ LinkedList     ← general purpose");
        System.out.println("              ├─ ArrayDeque     ← fast, recommended");
        System.out.println("              ├─ PriorityQueue  ← ordered by priority");
        System.out.println("              └─ Deque          ← double-ended queue");
        System.out.println("                   └─ ArrayDeque");


        // ═══════════════════════════════════════════════════════
        // TOPIC 2 — IMPLEMENTATION APPROACHES
        // ═══════════════════════════════════════════════════════
        section("TOPIC 2 — Implementation Approaches");

        // ── ARRAY QUEUE
        sub("Approach 1 — Array-based Queue");
        ArrayQueue aq = new ArrayQueue(8);
        System.out.println("  ArrayQueue(capacity=8), front=0, rear=-1");
        aq.enqueue(10); aq.enqueue(20); aq.enqueue(30); aq.enqueue(40);
        aq.print("After enqueue(10,20,30,40)");
        System.out.println("  peek()    = " + aq.peek());
        System.out.println("  dequeue() = " + aq.dequeue());
        System.out.println("  dequeue() = " + aq.dequeue());
        aq.print("After two dequeues");
        System.out.println();
        System.out.println("  ⚠ Problem: front drifted to index 2 → indices 0,1 wasted!");
        System.out.println("  If we enqueue 6 more items → 'Queue Full' even though");
        System.out.println("  2 slots are wasted at the front. → Fix: Circular Queue");

        // ── LINKED QUEUE
        sub("Approach 2 — LinkedList-based Queue");
        LinkedQueue lq = new LinkedQueue();
        System.out.println("  LinkedQueue — dynamic, no capacity limit");
        lq.enqueue(10); lq.enqueue(20); lq.enqueue(30); lq.enqueue(40);
        lq.print("After enqueue(10,20,30,40)");
        System.out.println("  peek()    = " + lq.peek());
        System.out.println("  dequeue() = " + lq.dequeue());
        System.out.println("  dequeue() = " + lq.dequeue());
        lq.print("After two dequeues");
        System.out.println();
        System.out.println("  No wasted space. head=front, tail=rear, both O(1).");

        // ── JAVA BUILT-IN
        sub("Approach 3 — Java Built-in: ArrayDeque (RECOMMENDED)");
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(10); q.offer(20); q.offer(30);
        System.out.println("  Queue<Integer> q = new ArrayDeque<>()");
        System.out.println("  offer(10,20,30) → front: " + q.peek());
        System.out.println("  poll()  = " + q.poll());
        System.out.println("  peek()  = " + q.peek());
        System.out.println("  size()  = " + q.size());
        System.out.println();
        System.out.println("  offer() vs add():  offer returns false on full; add throws exception");
        System.out.println("  poll()  vs remove(): poll returns null on empty; remove throws");
        System.out.println("  peek()  vs element(): peek returns null on empty; element throws");

        // ── PRIORITY QUEUE
        sub("Approach 4 — PriorityQueue (Min-Heap by default)");
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(30); pq.offer(10); pq.offer(50); pq.offer(20);
        System.out.println("  Inserted: 30, 10, 50, 20  (any order)");
        System.out.print("  Drain order (always min first): ");
        while (!pq.isEmpty()) System.out.print(pq.poll() + " ");
        System.out.println();

        PriorityQueue<Integer> maxPQ =
                new PriorityQueue<>(Collections.reverseOrder());
        maxPQ.offer(30); maxPQ.offer(10); maxPQ.offer(50); maxPQ.offer(20);
        System.out.print("  Max-Heap drain order: ");
        while (!maxPQ.isEmpty()) System.out.print(maxPQ.poll() + " ");
        System.out.println();
        System.out.println("  enqueue/dequeue: O(log n) — heap operations");
        System.out.println("  peek: O(1) — min/max always at root");


        // ═══════════════════════════════════════════════════════
        // TOPIC 3 — CIRCULAR QUEUE
        // ═══════════════════════════════════════════════════════
        section("TOPIC 3 — Circular Queue (Ring Buffer)");

        sub("The Problem with Linear Array Queue");
        System.out.println("  Linear queue after enqueue(10,20,30,40) + 2 dequeues:");
        System.out.println("  index:  [0]  [1]  [2]  [3]  [4]  [5]  [6]  [7]");
        System.out.println("  array:  [ _] [ _] [30] [40] [  ] [  ] [  ] [  ]");
        System.out.println("                ↑             ↑");
        System.out.println("              front=2       rear=3");
        System.out.println("  Indices 0,1 are WASTED and cannot be reused!");
        System.out.println("  4 more enqueues → 'Queue Full' even though capacity=8");

        sub("Circular Queue Solution — Modulo Arithmetic");
        System.out.println("  rear  = (rear  + 1) % capacity  ← wraps around");
        System.out.println("  front = (front + 1) % capacity  ← wraps around");
        System.out.println();
        System.out.println("  Ring visualization (capacity=5):");
        System.out.println("          [A]");
        System.out.println("       [E]   [B]");
        System.out.println("          [D][C]");
        System.out.println("  front and rear move clockwise. Slots are reused.");

        CircularQueue cq = new CircularQueue(6);
        cq.enqueue(10); cq.enqueue(20); cq.enqueue(30); cq.enqueue(40);
        cq.print("After enqueue(10,20,30,40)");
        System.out.println("  dequeue() = " + cq.dequeue());
        System.out.println("  dequeue() = " + cq.dequeue());
        cq.print("After two dequeues");
        cq.enqueue(50); cq.enqueue(60);
        cq.print("After enqueue(50,60) — reused freed slots!");
        System.out.println("  isFull()  = " + cq.isFull());
        cq.enqueue(70); // fill up
        cq.print("After enqueue(70)");
        System.out.println("  isFull()  = " + cq.isFull());
        System.out.println("  enqueue(80) = " + cq.enqueue(80) + "  ← returns false when full");

        sub("Circular Queue — Complexity");
        System.out.println("  All operations: O(1) time");
        System.out.println("  Space: O(capacity) — fixed, zero wasted slots");
        System.out.println("  Used in: OS ring buffers, audio streaming, network packet buffers");


        // ═══════════════════════════════════════════════════════
        // TOPIC 4 — DEQUE (Double-Ended Queue)
        // ═══════════════════════════════════════════════════════
        section("TOPIC 4 — Deque (Double-Ended Queue)");

        sub("What is a Deque?");
        System.out.println("  Deque = Double-Ended Queue.");
        System.out.println("  Insert AND remove from BOTH front and rear.");
        System.out.println("  Generalises both Stack (LIFO) and Queue (FIFO).");
        System.out.println();
        System.out.println("  FRONT ← [A] ⇄ [B] ⇄ [C] ⇄ [D] → REAR");
        System.out.println("  addFirst() / removeFirst() at FRONT");
        System.out.println("  addLast()  / removeLast()  at REAR");

        sub("Deque as Stack and Queue");
        Deque<Integer> deque = new ArrayDeque<>();

        System.out.println("  ── Used as STACK (LIFO):");
        deque.push(1); deque.push(2); deque.push(3);
        System.out.println("  push(1,2,3) → top: " + deque.peek());
        System.out.println("  pop(): " + deque.pop() + ", " + deque.pop());
        System.out.println("  Remaining: " + deque);

        deque.clear();
        System.out.println("\n  ── Used as QUEUE (FIFO):");
        deque.offerLast(1); deque.offerLast(2); deque.offerLast(3);
        System.out.println("  offerLast(1,2,3) → front: " + deque.peekFirst());
        System.out.println("  pollFirst(): " + deque.pollFirst() + ", " + deque.pollFirst());
        System.out.println("  Remaining: " + deque);

        sub("Deque — All Operations");
        Deque<String> d = new ArrayDeque<>();
        d.addFirst("B");
        d.addFirst("A"); // A is now at front
        d.addLast("C");
        d.addLast("D");  // D is now at rear
        System.out.println("  After addFirst(B), addFirst(A), addLast(C), addLast(D):");
        System.out.println("  Deque: " + d);
        System.out.println("  peekFirst() = " + d.peekFirst());
        System.out.println("  peekLast()  = " + d.peekLast());
        System.out.println("  removeFirst() = " + d.removeFirst());
        System.out.println("  removeLast()  = " + d.removeLast());
        System.out.println("  After removals: " + d);

        sub("Deque Complexity");
        System.out.println("  addFirst/addLast       → O(1) amortized");
        System.out.println("  removeFirst/removeLast → O(1)");
        System.out.println("  peekFirst/peekLast     → O(1)");
        System.out.println("  Space                  → O(n)");
        System.out.println();
        System.out.println("  Key use: Sliding Window Maximum problem (Topic 6)");


        // ═══════════════════════════════════════════════════════
        // TOPIC 5 — REAL-WORLD USE CASES
        // ═══════════════════════════════════════════════════════
        section("TOPIC 5 — Real-World Use Cases");

        sub("Use Case 1 — Print Spooler (OS Print Queue)");
        Queue<String> printer = new ArrayDeque<>();
        printer.offer("Resume_Navaneeth.pdf");
        printer.offer("Project_Report.docx");
        printer.offer("Lab_Assignment.pdf");
        System.out.println("  Print queue: " + printer);
        System.out.println("  Printing: " + printer.poll() + "  ← first submitted, first printed");
        System.out.println("  Printing: " + printer.poll());
        System.out.println("  Remaining: " + printer);
        System.out.println("  FIFO ensures fair order — no job starves");

        sub("Use Case 2 — CPU Task Scheduling (Round Robin)");
        Queue<String> cpu = new ArrayDeque<>();
        cpu.offer("Process-A (burst=3)");
        cpu.offer("Process-B (burst=5)");
        cpu.offer("Process-C (burst=2)");
        System.out.println("  Ready queue: " + cpu);
        System.out.println("  Time slice = 2 units. Round Robin:");
        int round = 1;
        while (!cpu.isEmpty()) {
            String proc = cpu.poll();
            System.out.println("  Round " + round++ + ": execute " + proc);
        }
        System.out.println("  Each process gets equal time, cycles until done.");

        sub("Use Case 3 — Message Queue (Async Systems)");
        Queue<String> msgQueue = new ArrayDeque<>();
        System.out.println("  Producer → Message Queue → Consumer");
        System.out.println("  Producer sends messages:");
        msgQueue.offer("MSG-001: User signup event");
        msgQueue.offer("MSG-002: Payment processed");
        msgQueue.offer("MSG-003: Email notification");
        System.out.println("  Queue: " + msgQueue);
        System.out.println("  Consumer processes:");
        while (!msgQueue.isEmpty())
            System.out.println("    Consumed: " + msgQueue.poll());
        System.out.println("  Used in: Kafka, RabbitMQ, AWS SQS, Redis streams");

        sub("Use Case 4 — BFS Web Crawler");
        System.out.println("  Queue-based web crawling:");
        System.out.println("  Start URL → enqueue → dequeue → fetch → extract links");
        System.out.println("             → enqueue new links → repeat");
        System.out.println();
        Queue<String> crawler = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        crawler.offer("https://root.com");
        while (!crawler.isEmpty()) {
            String url = crawler.poll();
            if (visited.contains(url)) continue;
            visited.add(url);
            System.out.println("  Crawling: " + url);
            if (url.equals("https://root.com")) {
                crawler.offer("https://root.com/about");
                crawler.offer("https://root.com/blog");
            }
        }

        sub("Use Case 5 — Ticket Booking System (Fair Queue)");
        Queue<String> booking = new ArrayDeque<>();
        booking.offer("User-Priya   (joined 10:00)");
        booking.offer("User-Ravi    (joined 10:01)");
        booking.offer("User-Ananya  (joined 10:02)");
        System.out.println("  Booking queue: " + booking.size() + " users waiting");
        System.out.println("  Ticket slot opens → serving: " + booking.poll());
        System.out.println("  Next in line:                " + booking.peek());


        // ═══════════════════════════════════════════════════════
        // TOPIC 6 — BFS & SLIDING WINDOW
        // ═══════════════════════════════════════════════════════
        section("TOPIC 6 — BFS & Sliding Window");

        sub("BFS — Breadth-First Search");
        // Build sample graph
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < 6; i++) graph.put(i, new ArrayList<>());
        graph.get(0).addAll(Arrays.asList(1, 2));
        graph.get(1).addAll(Arrays.asList(0, 3, 4));
        graph.get(2).addAll(Arrays.asList(0, 5));
        graph.get(3).add(1);
        graph.get(4).add(1);
        graph.get(5).add(2);

        System.out.println("  Graph edges:");
        System.out.println("  0 — 1 — 3");
        System.out.println("  |   |");
        System.out.println("  2   4");
        System.out.println("  |");
        System.out.println("  5");
        System.out.println();
        System.out.println("  BFS from node 0: " + bfs(graph, 0));
        System.out.println("  BFS by levels  : " + bfsLevels(graph, 0));
        System.out.println();
        System.out.println("  Queue state during BFS:");
        System.out.println("  Start:     [0]");
        System.out.println("  Visit 0:   [1, 2]      ← enqueue 0's neighbours");
        System.out.println("  Visit 1:   [2, 3, 4]   ← enqueue 1's unvisited");
        System.out.println("  Visit 2:   [3, 4, 5]   ← enqueue 2's unvisited");
        System.out.println("  Visit 3,4,5: []  done");
        System.out.println();
        System.out.println("  Time:  O(V + E)   V=vertices, E=edges");
        System.out.println("  Space: O(V)       for visited[] and queue");
        System.out.println("  BFS always finds SHORTEST PATH (in unweighted graphs)");

        sub("Sliding Window Maximum — Deque O(n)");
        int[][] swTests = {
            {3, 1, 2, 5, 4, 3},
            {1, 3, -1, -3, 5, 3, 6, 7},
            {9, 8, 7, 6, 5}
        };
        int[] ks = {3, 3, 2};

        for (int t = 0; t < swTests.length; t++) {
            System.out.printf("  arr=%s  k=%d%n",
                    Arrays.toString(swTests[t]), ks[t]);
            System.out.printf("  max=%s%n%n",
                    Arrays.toString(slidingWindowMax(swTests[t], ks[t])));
        }
        System.out.println("  Why Deque? Need O(1) access to both ends:");
        System.out.println("  → Remove OUTDATED indices from FRONT");
        System.out.println("  → Remove SMALLER  indices from REAR");
        System.out.println("  → FRONT always holds the current window maximum");
        System.out.println();
        System.out.println("  Brute force: O(n×k) | Deque approach: O(n)  ← key insight");


        // ═══════════════════════════════════════════════════════
        // TOPIC 7 — INTERVIEW-LEVEL PROBLEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 7 — Interview-Level Problems");

        sub("Problem 1: Queue Using Two Stacks");
        QueueUsingTwoStacks qts = new QueueUsingTwoStacks();
        qts.enqueue(1); qts.enqueue(2); qts.enqueue(3); qts.enqueue(4);
        System.out.println("  Enqueued: 1, 2, 3, 4");
        System.out.println("  peek()    = " + qts.peek());
        System.out.println("  dequeue() = " + qts.dequeue() + "  ← 1 (FIFO correct!)");
        System.out.println("  dequeue() = " + qts.dequeue() + "  ← 2");
        qts.enqueue(5);
        System.out.println("  enqueue(5)");
        System.out.println("  dequeue() = " + qts.dequeue() + "  ← 3");
        System.out.println("  dequeue() = " + qts.dequeue() + "  ← 4");
        System.out.println("  dequeue() = " + qts.dequeue() + "  ← 5");
        System.out.println("  Technique: inStack for enqueue, outStack for dequeue.");
        System.out.println("  Pour inStack→outStack only when outStack is empty.");
        System.out.println("  Amortized O(1) per operation.");

        sub("Problem 2: Stack Using Two Queues");
        StackUsingTwoQueues stq = new StackUsingTwoQueues();
        stq.push(1); stq.push(2); stq.push(3);
        System.out.println("  Pushed: 1, 2, 3");
        System.out.println("  peek() = " + stq.peek() + "  ← 3 (LIFO correct!)");
        System.out.println("  pop()  = " + stq.pop()  + "  ← 3");
        System.out.println("  pop()  = " + stq.pop()  + "  ← 2");
        System.out.println("  Technique: on each push, rotate queue so new element is front.");
        System.out.println("  push O(n), pop O(1)");

        sub("Problem 3: First Non-Repeating in Stream");
        String stream = "aabcbbdce";
        char[] fnr = firstNonRepeating(stream);
        System.out.println("  Stream:  " + stream);
        System.out.print("  Answers: ");
        for (int i = 0; i < stream.length(); i++) {
            System.out.print("'" + fnr[i] + "' ");
        }
        System.out.println();
        for (int i = 0; i < stream.length(); i++) {
            System.out.printf("  After '%c': first non-repeating = '%c'%n",
                    stream.charAt(i), fnr[i]);
        }
        System.out.println("  Technique: queue of candidates + freq[26]. O(n) time.");

        sub("Problem 4: Rotten Oranges (Multi-source BFS)");
        int[][][] grids = {
            {{2,1,1},{1,1,0},{0,1,1}},
            {{2,1,1},{0,1,1},{1,0,1}},
            {{0,2}}
        };
        int[] expected = {4, -1, 0};
        for (int g = 0; g < grids.length; g++) {
            // deep copy to avoid mutation
            int[][] copy = new int[grids[g].length][];
            for (int r = 0; r < grids[g].length; r++)
                copy[r] = Arrays.copyOf(grids[g][r], grids[g][r].length);
            int ans = rottenOranges(copy);
            System.out.println("  Grid " + (g+1) + ": minutes=" + ans +
                    (ans == expected[g] ? " ✓" : " ✗"));
        }
        System.out.println("  Technique: multi-source BFS — enqueue ALL rotten first.");
        System.out.println("  Each BFS level = 1 minute of spreading. O(m×n) time.");

        sub("Problem 5: Generate Binary Numbers 1 to N");
        int n = 10;
        String[] binary = generateBinaryNumbers(n);
        System.out.println("  Binary numbers 1 to " + n + ":");
        System.out.println("  " + Arrays.toString(binary));
        System.out.println("  Technique: BFS tree — each node generates '0' and '1' children.");
        System.out.println("  O(n) time, O(n) space.");

        sub("Problem 6: Task Scheduler");
        char[][] taskSets = {
            {'A','A','A','B','B','B'},
            {'A','A','A','B','B','B'},
            {'A','A','A','A','A','A'}
        };
        int[] ns = {2, 0, 2};
        for (int i = 0; i < taskSets.length; i++) {
            System.out.printf("  tasks=%s  n=%d  → min time=%d%n",
                    Arrays.toString(taskSets[i]), ns[i],
                    taskScheduler(taskSets[i], ns[i]));
        }
        System.out.println("  Technique: MaxHeap (PriorityQueue) to always pick most frequent.");
        System.out.println("  O(n log n) time.");

        // GRAND SUMMARY
        section("GRAND SUMMARY");
        System.out.println();
        System.out.println("  Queue = FIFO — First In, First Out.");
        System.out.println();
        System.out.println("  ┌───────────────────┬──────────┬──────────┬────────────────────┐");
        System.out.println("  │  Type             │  Enqueue │  Dequeue │  Best Use          │");
        System.out.println("  ├───────────────────┼──────────┼──────────┼────────────────────┤");
        System.out.println("  │  Array Queue      │  O(1)    │  O(1)    │  Simple, fixed     │");
        System.out.println("  │  LinkedList Queue │  O(1)    │  O(1)    │  Dynamic size      │");
        System.out.println("  │  Circular Queue   │  O(1)    │  O(1)    │  No wasted space   │");
        System.out.println("  │  Deque            │  O(1)    │  O(1)    │  Both ends access  │");
        System.out.println("  │  PriorityQueue    │  O(log n)│  O(log n)│  Priority ordering │");
        System.out.println("  └───────────────────┴──────────┴──────────┴────────────────────┘");
        System.out.println();
        System.out.println("  GOLDEN RULES:");
        System.out.println("  1. Use ArrayDeque — faster than LinkedList for most queue tasks");
        System.out.println("  2. BFS always uses a Queue — gives shortest path in O(V+E)");
        System.out.println("  3. Sliding window max → Deque gives O(n) vs O(nk) brute force");
        System.out.println("  4. Circular queue → fixes wasted space in linear array queue");
        System.out.println("  5. offer/poll/peek — prefer over add/remove/element (null-safe)");
    }
}
