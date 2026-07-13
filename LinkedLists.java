// ================================================================
//   LINKED LISTS — Complete Deep Dive in Java
// ================================================================
//   Compile:  javac LinkedLists.java
//   Run:      java LinkedLists
// ================================================================
//
//   TOPICS:
//   1. Why Linked Lists?
//   2. Memory Model
//   3. Types of Linked Lists
//   4. Core Operations + Complexity
//   5. Fast & Slow Pointer Patterns
//   6. Real-world Use Cases
//   7. Interview-level Problems
// ================================================================

public class LinkedLists {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    static void sub(String title) {
        System.out.println("\n  ── " + title + " ──");
    }


    // ============================================================
    // NODE DEFINITIONS
    // ============================================================

    // Singly Linked Node
    static class SNode {
        int data;
        SNode next;
        SNode(int data) { this.data = data; this.next = null; }
    }

    // Doubly Linked Node
    static class DNode {
        int data;
        DNode prev;
        DNode next;
        DNode(int data) { this.data = data; this.prev = null; this.next = null; }
    }


    // ============================================================
    // SINGLY LINKED LIST — Full Implementation
    // ============================================================
    static class SinglyLinkedList {
        SNode head;
        int size;

        SinglyLinkedList() { head = null; size = 0; }

        // ── INSERT AT HEAD — O(1)
        // Just update head pointer — no traversal needed
        void insertAtHead(int data) {
            SNode newNode = new SNode(data);
            newNode.next = head;
            head = newNode;
            size++;
        }

        // ── INSERT AT TAIL — O(n)
        // Must traverse to find the last node
        void insertAtTail(int data) {
            SNode newNode = new SNode(data);
            if (head == null) { head = newNode; size++; return; }
            SNode curr = head;
            while (curr.next != null) curr = curr.next; // walk to last node
            curr.next = newNode;
            size++;
        }

        // ── INSERT AT POSITION — O(n)
        void insertAtPosition(int data, int pos) {
            if (pos == 0) { insertAtHead(data); return; }
            SNode newNode = new SNode(data);
            SNode curr = head;
            for (int i = 0; i < pos - 1 && curr != null; i++) curr = curr.next;
            if (curr == null) return;
            newNode.next = curr.next;
            curr.next = newNode;
            size++;
        }

        // ── DELETE AT HEAD — O(1)
        int deleteAtHead() {
            if (head == null) throw new RuntimeException("List is empty");
            int val = head.data;
            head = head.next;
            size--;
            return val;
        }

        // ── DELETE BY VALUE — O(n)
        boolean deleteByValue(int data) {
            if (head == null) return false;
            if (head.data == data) { head = head.next; size--; return true; }
            SNode curr = head;
            while (curr.next != null) {
                if (curr.next.data == data) {
                    curr.next = curr.next.next; // bypass the node
                    size--;
                    return true;
                }
                curr = curr.next;
            }
            return false;
        }

        // ── SEARCH — O(n)
        int search(int data) {
            SNode curr = head;
            int idx = 0;
            while (curr != null) {
                if (curr.data == data) return idx;
                curr = curr.next;
                idx++;
            }
            return -1;
        }

        // ── PRINT — O(n)
        void print(String label) {
            System.out.print("  " + label + ": ");
            SNode curr = head;
            while (curr != null) {
                System.out.print(curr.data);
                if (curr.next != null) System.out.print(" → ");
                curr = curr.next;
            }
            System.out.println(" → null  (size=" + size + ")");
        }

        // ── REVERSE — O(n) time, O(1) space
        // Three-pointer technique: prev, curr, next
        //
        // Before:  null ← 1 → 2 → 3 → 4 → null
        // Step 1:  prev=null, curr=1 → next=2, 1.next=null, prev=1, curr=2
        // Step 2:  prev=1, curr=2    → next=3, 2.next=1,    prev=2, curr=3
        // Step 3:  prev=2, curr=3    → next=4, 3.next=2,    prev=3, curr=4
        // Step 4:  prev=3, curr=4    → next=null,4.next=3,  prev=4, curr=null
        // After:   null ← 4 → 3 → 2 → 1 → null  (head = prev = 4)
        void reverse() {
            SNode prev = null;
            SNode curr = head;
            while (curr != null) {
                SNode nextTemp = curr.next; // save next
                curr.next = prev;           // reverse pointer
                prev = curr;               // move prev forward
                curr = nextTemp;           // move curr forward
            }
            head = prev; // new head is the last node
        }

        // ── LENGTH — O(n)
        int length() {
            int count = 0;
            SNode curr = head;
            while (curr != null) { count++; curr = curr.next; }
            return count;
        }
    }


    // ============================================================
    // DOUBLY LINKED LIST — Full Implementation
    // ============================================================
    static class DoublyLinkedList {
        DNode head;
        DNode tail;
        int size;

        DoublyLinkedList() { head = null; tail = null; size = 0; }

        // ── INSERT AT HEAD — O(1)
        void insertAtHead(int data) {
            DNode newNode = new DNode(data);
            if (head == null) { head = tail = newNode; size++; return; }
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            size++;
        }

        // ── INSERT AT TAIL — O(1) because we track tail!
        // This is the KEY advantage over Singly Linked List
        void insertAtTail(int data) {
            DNode newNode = new DNode(data);
            if (tail == null) { head = tail = newNode; size++; return; }
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
            size++;
        }

        // ── DELETE AT TAIL — O(1) because tail.prev gives us the previous node
        int deleteAtTail() {
            if (tail == null) throw new RuntimeException("List is empty");
            int val = tail.data;
            if (head == tail) { head = tail = null; size--; return val; }
            tail = tail.prev;
            tail.next = null;
            size--;
            return val;
        }

        // ── PRINT FORWARD — O(n)
        void printForward(String label) {
            System.out.print("  " + label + " (fwd): ");
            DNode curr = head;
            while (curr != null) {
                System.out.print(curr.data);
                if (curr.next != null) System.out.print(" ⇄ ");
                curr = curr.next;
            }
            System.out.println("  (size=" + size + ")");
        }

        // ── PRINT BACKWARD — O(n) — unique to doubly linked list!
        void printBackward(String label) {
            System.out.print("  " + label + " (bwd): ");
            DNode curr = tail;
            while (curr != null) {
                System.out.print(curr.data);
                if (curr.prev != null) System.out.print(" ⇄ ");
                curr = curr.prev;
            }
            System.out.println();
        }
    }


    // ============================================================
    // CIRCULAR LINKED LIST
    // ============================================================
    static class CircularLinkedList {
        SNode head;
        int size;

        CircularLinkedList() { head = null; size = 0; }

        void insert(int data) {
            SNode newNode = new SNode(data);
            if (head == null) {
                head = newNode;
                head.next = head; // points to itself!
                size++;
                return;
            }
            // Find last node (the one pointing back to head)
            SNode last = head;
            while (last.next != head) last = last.next;
            last.next = newNode;
            newNode.next = head; // complete the circle
            size++;
        }

        void print(String label) {
            if (head == null) return;
            System.out.print("  " + label + ": ");
            SNode curr = head;
            do {
                System.out.print(curr.data);
                if (curr.next != head) System.out.print(" → ");
                curr = curr.next;
            } while (curr != head);
            System.out.println(" → (back to " + head.data + ")  (size=" + size + ")");
        }
    }


    // ============================================================
    // FAST & SLOW POINTER ALGORITHMS
    // ============================================================

    // ── FIND MIDDLE NODE — O(n) time, O(1) space
    // Fast pointer moves 2 steps, slow moves 1 step.
    // When fast reaches end, slow is at middle.
    //
    // Example: 1 → 2 → 3 → 4 → 5
    // Start:   slow=1, fast=1
    // Step 1:  slow=2, fast=3
    // Step 2:  slow=3, fast=5  ← fast.next == null → stop
    // Middle = slow = 3  ✓
    static SNode findMiddle(SNode head) {
        if (head == null) return null;
        SNode slow = head;
        SNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;        // 1 step
            fast = fast.next.next;   // 2 steps
        }
        return slow;
    }

    // ── DETECT CYCLE — O(n) time, O(1) space — Floyd's Algorithm
    // If a cycle exists, fast will eventually lap slow and they meet.
    // If no cycle, fast reaches null.
    //
    // Imagine two runners on a circular track — the faster one
    // will eventually catch up to the slower one.
    static boolean hasCycle(SNode head) {
        SNode slow = head;
        SNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true; // they met — cycle detected!
        }
        return false;
    }

    // ── FIND CYCLE START — O(n) time, O(1) space
    // After detecting cycle (slow == fast at meeting point):
    // Reset one pointer to head. Move both ONE step at a time.
    // Where they meet again = start of cycle.
    //
    // Mathematical proof:
    // Let: F = distance from head to cycle start
    //      C = cycle length
    //      h = distance from cycle start to meeting point
    // When slow==fast: slow traveled F+h, fast traveled 2(F+h)
    // Extra distance fast covered = F+h = multiple of C
    // So: F = C - h = distance from meeting point back to cycle start
    // Reset pointer to head → both travel F → meet at cycle start!
    static SNode findCycleStart(SNode head) {
        SNode slow = head, fast = head;
        // Phase 1: detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) break;
        }
        if (fast == null || fast.next == null) return null; // no cycle
        // Phase 2: find cycle start
        slow = head; // reset to head
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next; // BOTH move one step now
        }
        return slow; // meeting point = cycle start
    }

    // ── Nth NODE FROM END — O(n) time, O(1) space
    // Move fast pointer n steps ahead.
    // Then move both until fast reaches end.
    // Slow is now at nth node from end.
    static SNode nthFromEnd(SNode head, int n) {
        SNode slow = head, fast = head;
        for (int i = 0; i < n; i++) { // advance fast by n
            if (fast == null) return null;
            fast = fast.next;
        }
        while (fast != null) { // move both
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }


    // ============================================================
    // INTERVIEW PROBLEMS
    // ============================================================

    // ── PROBLEM 1: REVERSE A LINKED LIST — O(n) time, O(1) space
    // Already in SinglyLinkedList.reverse() above

    // ── PROBLEM 2: MERGE TWO SORTED LISTS — O(n+m) time, O(1) space
    // Compare heads of both lists. Pick smaller. Advance that pointer.
    static SNode mergeSorted(SNode l1, SNode l2) {
        SNode dummy = new SNode(0); // placeholder head
        SNode curr  = dummy;

        while (l1 != null && l2 != null) {
            if (l1.data <= l2.data) {
                curr.next = l1;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2 = l2.next;
            }
            curr = curr.next;
        }
        curr.next = (l1 != null) ? l1 : l2; // attach remaining
        return dummy.next;
    }

    // ── PROBLEM 3: PALINDROME LINKED LIST — O(n) time, O(1) space
    // Step 1: Find middle (fast/slow)
    // Step 2: Reverse second half
    // Step 3: Compare first half with reversed second half
    static boolean isPalindrome(SNode head) {
        if (head == null || head.next == null) return true;

        // Step 1: find middle
        SNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: reverse second half in-place
        SNode prev = null, curr = slow;
        while (curr != null) {
            SNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        SNode secondHalf = prev;

        // Step 3: compare
        SNode p1 = head, p2 = secondHalf;
        while (p2 != null) {
            if (p1.data != p2.data) return false;
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }

    // ── PROBLEM 4: REMOVE DUPLICATES FROM SORTED LIST — O(n) time, O(1) space
    static void removeDuplicates(SNode head) {
        SNode curr = head;
        while (curr != null && curr.next != null) {
            if (curr.data == curr.next.data) {
                curr.next = curr.next.next; // skip duplicate
            } else {
                curr = curr.next;
            }
        }
    }

    // ── PROBLEM 5: INTERSECTION OF TWO LISTS — O(n+m) time, O(1) space
    // If two lists intersect, they share the same tail nodes.
    // Key insight: if both lists have different lengths,
    // advance the longer one by (len1 - len2) steps first.
    // Then advance both — they'll meet at intersection.
    static SNode findIntersection(SNode headA, SNode headB) {
        if (headA == null || headB == null) return null;
        SNode a = headA, b = headB;
        // When a reaches end, redirect to headB. Same for b.
        // They will meet at intersection after at most m+n steps.
        while (a != b) {
            a = (a == null) ? headB : a.next;
            b = (b == null) ? headA : b.next;
        }
        return a;
    }

    // ── PROBLEM 6: ADD TWO NUMBERS (digits stored in reverse)
    // 342 stored as 2→4→3, 465 stored as 5→6→4
    // Sum = 807 stored as 7→0→8
    // O(max(n,m)) time, O(max(n,m)) space
    static SNode addTwoNumbers(SNode l1, SNode l2) {
        SNode dummy = new SNode(0);
        SNode curr  = dummy;
        int carry   = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) { sum += l1.data; l1 = l1.next; }
            if (l2 != null) { sum += l2.data; l2 = l2.next; }
            carry = sum / 10;
            curr.next = new SNode(sum % 10);
            curr = curr.next;
        }
        return dummy.next;
    }

    // ── HELPER: build list from array
    static SNode buildList(int[] arr) {
        if (arr.length == 0) return null;
        SNode head = new SNode(arr[0]);
        SNode curr = head;
        for (int i = 1; i < arr.length; i++) {
            curr.next = new SNode(arr[i]);
            curr = curr.next;
        }
        return head;
    }

    // ── HELPER: print list inline
    static String listToString(SNode head) {
        StringBuilder sb = new StringBuilder();
        SNode curr = head;
        while (curr != null) {
            sb.append(curr.data);
            if (curr.next != null) sb.append(" → ");
            curr = curr.next;
        }
        sb.append(" → null");
        return sb.toString();
    }


    // ============================================================
    //   MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         LINKED LISTS — Complete Deep Dive in Java        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");


        // ═══════════════════════════════════════════════════════
        // TOPIC 1 — WHY LINKED LISTS?
        // ═══════════════════════════════════════════════════════
        section("TOPIC 1 — Why Linked Lists?");

        sub("The Array Limitation — fixed size & costly insert");
        System.out.println("  int[] arr = new int[5];  ← size locked at creation");
        System.out.println();
        System.out.println("  Insert 99 at index 1 in [10, 20, 30, 40, 50]:");
        System.out.println("  [10, 20, 30, 40, 50]  →  must shift everything right");
        System.out.println("  [10, 99, 20, 30, 40, 50]  ← O(n) shifts, new array needed");
        System.out.println();
        System.out.println("  Linked List insert at index 1:");
        System.out.println("  Just update 2 pointers — O(1) once node is located.");
        System.out.println("  No shifting. No fixed size. No pre-allocation.");

        sub("When to choose Linked List over Array");
        System.out.println("  ┌────────────────────────────┬───────────┬─────────────┐");
        System.out.println("  │  Need                      │  Array    │ Linked List │");
        System.out.println("  ├────────────────────────────┼───────────┼─────────────┤");
        System.out.println("  │  Random access by index    │  O(1) ✅ │  O(n) ❌   │");
        System.out.println("  │  Insert/delete at front    │  O(n) ❌ │  O(1) ✅   │");
        System.out.println("  │  Insert/delete at back     │  O(1) ✅ │  O(1)* ✅  │");
        System.out.println("  │  Dynamic size              │  ❌       │  ✅         │");
        System.out.println("  │  Memory efficiency         │  ✅       │  ❌ (ptrs) │");
        System.out.println("  │  Cache friendliness        │  ✅       │  ❌        │");
        System.out.println("  └────────────────────────────┴───────────┴─────────────┘");
        System.out.println("  * O(1) at back only if tail pointer is maintained");


        // ═══════════════════════════════════════════════════════
        // TOPIC 2 — MEMORY MODEL
        // ═══════════════════════════════════════════════════════
        section("TOPIC 2 — Memory Model");

        sub("Node structure in memory");
        System.out.println("  Each node is an OBJECT on the heap with two fields:");
        System.out.println();
        System.out.println("  class SNode {");
        System.out.println("      int  data;   // 4 bytes — the actual value");
        System.out.println("      SNode next;  // 8 bytes — pointer to next node");
        System.out.println("  }               // Total: ~16 bytes per node (with object header)");
        System.out.println();
        System.out.println("  Compare to int[] where each element = 4 bytes.");
        System.out.println("  Linked list uses 4× the memory per element for next pointer alone.");

        sub("Visual memory layout — Array vs Linked List");
        System.out.println();
        System.out.println("  Array [10, 20, 30, 40] — CONTIGUOUS memory:");
        System.out.println("  Addr: 1000  1004  1008  1012");
        System.out.println("  Val : [ 10] [ 20] [ 30] [ 40]");
        System.out.println("  CPU cache loads a block → accessing neighbours is 'free'");
        System.out.println();
        System.out.println("  Linked List 10→20→30→40 — SCATTERED memory:");
        System.out.println("  Addr: 1000       3048       512        7200");
        System.out.println("        [10|3048]→ [20|512] → [30|7200]→ [40|null]");
        System.out.println("  Each 'next' dereference = potential cache miss → slower in practice");

        sub("Pointer mechanics — what actually happens");
        SinglyLinkedList memDemo = new SinglyLinkedList();
        memDemo.insertAtTail(10);
        memDemo.insertAtTail(20);
        memDemo.insertAtTail(30);
        memDemo.print("List");
        System.out.println();
        System.out.println("  head ─────────────────────┐");
        System.out.println("                            ↓");
        System.out.println("  [data=10 | next=●]──→ [data=20 | next=●]──→ [data=30 | next=null]");
        System.out.println();
        System.out.println("  Inserting 99 between 10 and 20:");
        System.out.println("  Step 1: Create [data=99 | next=null]");
        System.out.println("  Step 2: 99.next = node(20)");
        System.out.println("  Step 3: node(10).next = node(99)");
        System.out.println("  Done! 2 pointer changes. Zero shifting.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 3 — TYPES OF LINKED LISTS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 3 — Types of Linked Lists");

        // ── SINGLY
        sub("1. Singly Linked List — one direction only");
        SinglyLinkedList sll = new SinglyLinkedList();
        sll.insertAtTail(1);
        sll.insertAtTail(2);
        sll.insertAtTail(3);
        sll.insertAtTail(4);
        sll.print("Singly");
        System.out.println("  Structure: [1|→] → [2|→] → [3|→] → [4|null]");
        System.out.println("  Traverse: FORWARD only");
        System.out.println("  Memory per node: data + 1 pointer");

        // ── DOUBLY
        sub("2. Doubly Linked List — both directions");
        DoublyLinkedList dll = new DoublyLinkedList();
        dll.insertAtTail(1);
        dll.insertAtTail(2);
        dll.insertAtTail(3);
        dll.insertAtTail(4);
        dll.printForward("Doubly");
        dll.printBackward("Doubly");
        System.out.println("  Structure: null ← [1] ⇄ [2] ⇄ [3] ⇄ [4] → null");
        System.out.println("  Traverse: FORWARD and BACKWARD");
        System.out.println("  Extra: O(1) delete from tail (knows tail.prev)");
        System.out.println("  Memory per node: data + 2 pointers (~24 bytes)");

        // ── CIRCULAR
        sub("3. Circular Linked List — last points back to head");
        CircularLinkedList cll = new CircularLinkedList();
        cll.insert(1); cll.insert(2); cll.insert(3); cll.insert(4);
        cll.print("Circular");
        System.out.println("  Structure: [1] → [2] → [3] → [4] → (back to 1)");
        System.out.println("  Use cases: Round-robin scheduling, music playlist loop");
        System.out.println("  Warning: Traversal must track visited nodes to avoid infinite loop!");

        sub("Type Comparison");
        System.out.println("  ┌─────────────────┬────────────┬──────────────┬────────────┐");
        System.out.println("  │  Type           │  Forward   │  Backward    │  Memory    │");
        System.out.println("  ├─────────────────┼────────────┼──────────────┼────────────┤");
        System.out.println("  │  Singly         │  ✅        │  ❌          │  Low       │");
        System.out.println("  │  Doubly         │  ✅        │  ✅          │  Medium    │");
        System.out.println("  │  Circular       │  ✅ (loop) │  ❌          │  Low       │");
        System.out.println("  │  Doubly Circular│  ✅ (loop) │  ✅ (loop)   │  High      │");
        System.out.println("  └─────────────────┴────────────┴──────────────┴────────────┘");


        // ═══════════════════════════════════════════════════════
        // TOPIC 4 — CORE OPERATIONS + COMPLEXITY
        // ═══════════════════════════════════════════════════════
        section("TOPIC 4 — Core Operations + Complexity");

        SinglyLinkedList ops = new SinglyLinkedList();

        sub("insertAtHead — O(1)");
        ops.insertAtHead(30);
        ops.insertAtHead(20);
        ops.insertAtHead(10);
        ops.print("After insertAtHead(10,20,30)");
        System.out.println("  Only 2 steps: newNode.next=head, head=newNode");
        System.out.println("  Same cost whether list has 3 nodes or 3 million");

        sub("insertAtTail — O(n)");
        ops.insertAtTail(40);
        ops.insertAtTail(50);
        ops.print("After insertAtTail(40,50)");
        System.out.println("  Must walk entire list to find last node → O(n)");
        System.out.println("  Fix: maintain a tail pointer → O(1) (DoublyLinkedList does this)");

        sub("insertAtPosition — O(n)");
        ops.insertAtPosition(99, 2);
        ops.print("After insertAtPosition(99, index=2)");
        System.out.println("  Walk to position-1, then update 2 pointers → O(n)");

        sub("deleteAtHead — O(1)");
        int deleted = ops.deleteAtHead();
        ops.print("After deleteAtHead() removed " + deleted);

        sub("deleteByValue — O(n)");
        ops.deleteByValue(99);
        ops.print("After deleteByValue(99)");
        System.out.println("  Worst case: walk entire list → O(n)");

        sub("search — O(n)");
        int idx = ops.search(40);
        System.out.println("  search(40) found at index: " + idx);
        System.out.println("  No random access — must traverse from head → O(n)");

        sub("reverse — O(n) time, O(1) space");
        ops.print("Before reverse");
        ops.reverse();
        ops.print("After  reverse");

        sub("Complexity Summary");
        System.out.println("  ┌──────────────────────┬──────────────┬────────────┐");
        System.out.println("  │  Operation           │  Singly      │  Doubly    │");
        System.out.println("  ├──────────────────────┼──────────────┼────────────┤");
        System.out.println("  │  Insert at head      │  O(1)        │  O(1)      │");
        System.out.println("  │  Insert at tail      │  O(n)*       │  O(1) ✅  │");
        System.out.println("  │  Insert at position  │  O(n)        │  O(n)      │");
        System.out.println("  │  Delete at head      │  O(1)        │  O(1)      │");
        System.out.println("  │  Delete at tail      │  O(n)*       │  O(1) ✅  │");
        System.out.println("  │  Delete by value     │  O(n)        │  O(n)      │");
        System.out.println("  │  Search              │  O(n)        │  O(n)      │");
        System.out.println("  │  Reverse             │  O(n)        │  O(n)      │");
        System.out.println("  │  Access by index     │  O(n)        │  O(n)      │");
        System.out.println("  └──────────────────────┴──────────────┴────────────┘");
        System.out.println("  * O(n) without tail pointer. O(1) with tail pointer.");
        System.out.println("  Space Complexity: O(n) for storing n nodes");


        // ═══════════════════════════════════════════════════════
        // TOPIC 5 — FAST & SLOW POINTER PATTERNS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 5 — Fast & Slow Pointer Patterns");

        System.out.println("  The fast & slow pointer (Floyd's Tortoise & Hare) technique");
        System.out.println("  solves linked list problems in O(n) time and O(1) space.");
        System.out.println("  Two pointers traverse the list at different speeds.");

        sub("Pattern 1: Find Middle Node");
        SNode midList = buildList(new int[]{1, 2, 3, 4, 5});
        SNode middle  = findMiddle(midList);
        System.out.println("  List  : " + listToString(midList));
        System.out.println("  Middle: " + middle.data + "  (slow moved n/2 steps while fast moved n)");
        System.out.println();
        System.out.println("  Trace:");
        System.out.println("  Step 0: slow=1, fast=1");
        System.out.println("  Step 1: slow=2, fast=3");
        System.out.println("  Step 2: slow=3, fast=5  ← fast.next=null, stop");
        System.out.println("  Middle = slow = 3  ✓");

        sub("Pattern 1b: Middle of even-length list");
        SNode midEven = buildList(new int[]{1, 2, 3, 4, 5, 6});
        SNode middleE = findMiddle(midEven);
        System.out.println("  List  : " + listToString(midEven));
        System.out.println("  Middle: " + middleE.data + "  (returns second middle for even length)");

        sub("Pattern 2: Detect Cycle — Floyd's Algorithm");
        // Create a list with a cycle: 1→2→3→4→5→(back to 3)
        SNode cycleHead = new SNode(1);
        SNode n2 = new SNode(2);
        SNode n3 = new SNode(3);
        SNode n4 = new SNode(4);
        SNode n5 = new SNode(5);
        cycleHead.next = n2; n2.next = n3; n3.next = n4; n4.next = n5;
        n5.next = n3; // cycle: 5 → back to 3

        SNode noCycleHead = buildList(new int[]{1, 2, 3, 4, 5});

        System.out.println("  List with cycle    (1→2→3→4→5→3...): hasCycle = " + hasCycle(cycleHead));
        System.out.println("  List without cycle (1→2→3→4→5→null): hasCycle = " + hasCycle(noCycleHead));
        System.out.println();
        System.out.println("  Why it works: on a circular track, a faster runner");
        System.out.println("  always laps the slower one. Fast ptr = 2x speed.");

        sub("Pattern 3: Nth Node from End");
        SNode nthList = buildList(new int[]{1, 2, 3, 4, 5, 6, 7});
        int n = 3;
        SNode nthNode = nthFromEnd(nthList, n);
        System.out.println("  List : " + listToString(nthList));
        System.out.printf ("  %drd from end = %d%n", n, nthNode.data);
        System.out.println();
        System.out.println("  Trace: advance fast by 3 → fast=4, slow=1");
        System.out.println("  Move both: slow=2,fast=5 → slow=3,fast=6 → slow=4,fast=7");
        System.out.println("  fast=null → stop. slow=5 = 3rd from end  ✓");

        sub("Pattern Summary");
        System.out.println("  ┌────────────────────────────┬───────────┬──────────┐");
        System.out.println("  │  Problem                   │  Time     │  Space   │");
        System.out.println("  ├────────────────────────────┼───────────┼──────────┤");
        System.out.println("  │  Find middle               │  O(n)     │  O(1)    │");
        System.out.println("  │  Detect cycle              │  O(n)     │  O(1)    │");
        System.out.println("  │  Find cycle start          │  O(n)     │  O(1)    │");
        System.out.println("  │  Nth node from end         │  O(n)     │  O(1)    │");
        System.out.println("  │  Palindrome check          │  O(n)     │  O(1)    │");
        System.out.println("  └────────────────────────────┴───────────┴──────────┘");
        System.out.println("  All patterns: one pass, two pointers, O(1) space.");


        // ═══════════════════════════════════════════════════════
        // TOPIC 6 — REAL-WORLD USE CASES
        // ═══════════════════════════════════════════════════════
        section("TOPIC 6 — Real-World Use Cases");

        sub("Use Case 1 — Browser Back/Forward (Doubly Linked List)");
        DoublyLinkedList browser = new DoublyLinkedList();
        browser.insertAtTail(1); // google.com
        browser.insertAtTail(2); // github.com
        browser.insertAtTail(3); // stackoverflow.com
        browser.insertAtTail(4); // youtube.com
        browser.printForward("Browser history");
        System.out.println("  Current page: node at tail (youtube.com)");
        System.out.println("  Press Back:   tail.prev (stackoverflow.com) — O(1)");
        System.out.println("  Press Forward:curr.next  (youtube.com)       — O(1)");
        System.out.println("  New page:     delete everything after current, insertAtTail");

        sub("Use Case 2 — Music Playlist Loop (Circular Linked List)");
        CircularLinkedList playlist = new CircularLinkedList();
        playlist.insert(101); // Song 101
        playlist.insert(102);
        playlist.insert(103);
        playlist.print("Playlist");
        System.out.println("  Loop forever: after last song, next = first song");
        System.out.println("  Shuffle: insert/delete at any position — O(1) with node ref");

        sub("Use Case 3 — Undo/Redo Stack (Singly Linked List as Stack)");
        SinglyLinkedList undoStack = new SinglyLinkedList();
        undoStack.insertAtHead(1); // action: type 'H'
        undoStack.insertAtHead(2); // action: type 'e'
        undoStack.insertAtHead(3); // action: type 'l'
        undoStack.print("Undo stack (top=head)");
        System.out.println("  Undo: deleteAtHead() — O(1)");
        int undone = undoStack.deleteAtHead();
        undoStack.print("After undo (removed action " + undone + ")");

        sub("Use Case 4 — Java's LinkedList as Deque (Queue + Stack)");
        System.out.println("  java.util.LinkedList implements both List AND Deque.");
        System.out.println("  addFirst() / removeFirst() → Stack (LIFO)");
        System.out.println("  addLast()  / removeFirst() → Queue (FIFO)");
        System.out.println("  Used in: BFS (Queue), DFS (Stack), job schedulers");

        sub("Use Case 5 — LRU Cache (Doubly Linked List + HashMap)");
        System.out.println("  LRU (Least Recently Used) Cache evicts oldest item.");
        System.out.println("  Doubly Linked List: most-recent at head, least-recent at tail");
        System.out.println("  HashMap: key → node reference (O(1) access to any node)");
        System.out.println("  Access item: move its node to head — O(1)");
        System.out.println("  Evict: delete tail node — O(1)");
        System.out.println("  Combined: O(1) get AND put — industry-standard cache design");
        System.out.println("  Used in: CPU cache, Redis, browser cache, CDN edge nodes");


        // ═══════════════════════════════════════════════════════
        // TOPIC 7 — INTERVIEW-LEVEL PROBLEMS
        // ═══════════════════════════════════════════════════════
        section("TOPIC 7 — Interview-Level Problems");

        sub("Problem 1: Reverse a Linked List");
        SinglyLinkedList rev = new SinglyLinkedList();
        for (int v : new int[]{1, 2, 3, 4, 5}) rev.insertAtTail(v);
        rev.print("Before");
        rev.reverse();
        rev.print("After reverse");
        System.out.println("  Technique: 3 pointers (prev, curr, next). O(n) time, O(1) space.");

        sub("Problem 2: Merge Two Sorted Lists");
        SNode l1 = buildList(new int[]{1, 3, 5, 7});
        SNode l2 = buildList(new int[]{2, 4, 6, 8});
        SNode merged = mergeSorted(l1, l2);
        System.out.println("  List 1 : " + listToString(buildList(new int[]{1, 3, 5, 7})));
        System.out.println("  List 2 : " + listToString(buildList(new int[]{2, 4, 6, 8})));
        System.out.println("  Merged : " + listToString(merged));
        System.out.println("  Technique: dummy head + compare & advance. O(n+m) time, O(1) space.");

        sub("Problem 3: Palindrome Linked List");
        int[][] palTests = {{1,2,3,2,1},{1,2,3,4,5},{1,2,2,1}};
        for (int[] arr : palTests) {
            SNode head = buildList(arr);
            System.out.printf("  %-25s → isPalindrome: %s%n",
                    listToString(head), isPalindrome(head));
        }
        System.out.println("  Technique: find-middle + reverse-half + compare. O(n) time, O(1) space.");

        sub("Problem 4: Remove Duplicates from Sorted List");
        SNode dupList = buildList(new int[]{1, 1, 2, 3, 3, 3, 4, 5, 5});
        System.out.println("  Before: " + listToString(dupList));
        removeDuplicates(dupList);
        System.out.println("  After : " + listToString(dupList));
        System.out.println("  Technique: skip consecutive equal nodes. O(n) time, O(1) space.");

        sub("Problem 5: Add Two Numbers (reverse digits)");
        // 342 + 465 = 807
        SNode num1   = buildList(new int[]{2, 4, 3}); // represents 342
        SNode num2   = buildList(new int[]{5, 6, 4}); // represents 465
        SNode result = addTwoNumbers(num1, num2);
        System.out.println("  342 stored as: " + listToString(num1));
        System.out.println("  465 stored as: " + listToString(num2));
        System.out.println("  Sum (807) as : " + listToString(result));
        System.out.println("  Technique: digit-by-digit addition with carry. O(max(n,m)) time.");

        sub("Interview Complexity Cheat Sheet");
        System.out.println("  ┌────────────────────────────────────┬──────────┬──────────┐");
        System.out.println("  │  Problem                           │  Time    │  Space   │");
        System.out.println("  ├────────────────────────────────────┼──────────┼──────────┤");
        System.out.println("  │  Reverse linked list               │  O(n)    │  O(1)    │");
        System.out.println("  │  Find middle                       │  O(n)    │  O(1)    │");
        System.out.println("  │  Detect cycle                      │  O(n)    │  O(1)    │");
        System.out.println("  │  Find cycle start                  │  O(n)    │  O(1)    │");
        System.out.println("  │  Nth from end                      │  O(n)    │  O(1)    │");
        System.out.println("  │  Merge two sorted lists            │  O(n+m)  │  O(1)    │");
        System.out.println("  │  Palindrome check                  │  O(n)    │  O(1)    │");
        System.out.println("  │  Remove duplicates (sorted)        │  O(n)    │  O(1)    │");
        System.out.println("  │  Intersection of two lists         │  O(n+m)  │  O(1)    │");
        System.out.println("  │  Add two numbers                   │  O(n)    │  O(n)    │");
        System.out.println("  └────────────────────────────────────┴──────────┴──────────┘");

        // GRAND SUMMARY
        section("GRAND SUMMARY");
        System.out.println();
        System.out.println("  Linked List = Nodes connected by pointers, not contiguous memory.");
        System.out.println();
        System.out.println("  STRENGTHS                     WEAKNESSES");
        System.out.println("  ✅ Dynamic size               ❌ No O(1) random access");
        System.out.println("  ✅ O(1) insert/delete at head ❌ More memory (pointer overhead)");
        System.out.println("  ✅ No pre-allocation needed   ❌ Poor cache performance");
        System.out.println("  ✅ Efficient reordering       ❌ No binary search");
        System.out.println();
        System.out.println("  GOLDEN RULES:");
        System.out.println("  1. Use fast/slow pointer for O(1) space solutions");
        System.out.println("  2. Always check for null before dereferencing next");
        System.out.println("  3. Use dummy head node to simplify edge cases");
        System.out.println("  4. Maintain tail pointer for O(1) tail operations");
        System.out.println("  5. Prefer ArrayList in 90% of Java code (better cache)");
    }
}
