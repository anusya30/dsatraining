# 🧩 Programming Assignment: Java Lists Deep Dive

## 📌 Objective
This assignment is designed to help students practice **Java List fundamentals**, understand the difference between **ArrayList** and **LinkedList**, and apply lists safely in simple real-world scenarios.

Students should demonstrate understanding of:

### List Fundamentals
- Ordered collections
- Duplicate elements
- Insertion order
- Index-based operations

### List Implementations
- ArrayList internals and resizing
- LinkedList node-based structure
- Time complexity tradeoffs
- Choosing the right implementation for a task

### Safe List Usage
- Safe removal during iteration
- Common mistakes with lists
- Real-world list-based design

---

# 🔹 Question 1: Student Attendance Register (List Basics + ArrayList)

A training center stores student names in a list in the order they enter the classroom. Build a small program to manage this attendance register using **ArrayList**.

### Requirements

Create a class:

`AttendanceRegister`

---

### Implement the following methods:

1. `addStudent(List<String> students, String name)`
   - Add a student name at the end of the list.

2. `insertStudent(List<String> students, int index, String name)`
   - Insert a student at a specific position.

3. `updateStudent(List<String> students, int index, String newName)`
   - Replace the student name at the given index.

4. `removeStudentByIndex(List<String> students, int index)`
   - Remove a student using the index.

5. `removeStudentByName(List<String> students, String name)`
   - Remove the first occurrence of the given name.

6. `searchStudent(List<String> students, String name)`
   - Return `true` if the name exists, otherwise return `false`.

7. `printStudents(List<String> students)`
   - Print the full list along with the size.

---

### Concepts to Demonstrate

- **List properties**
  - Duplicates should be allowed.
  - Insertion order should be maintained.

- **Complexity Annotation**
  - Add comments above the methods for:
    - `add(end)` → O(1) amortized
    - `get/set` style indexed update → O(1)
    - `add(index)` → O(n)
    - `remove(index)` → O(n)
    - `contains/search` → O(n)

- **Program to interface**
  - Declare the list using `List<String>` and create it using `new ArrayList<>()`.

- **Simple resize understanding**
  - Add a short comment explaining why `add()` in `ArrayList` is called **amortized O(1)**.

---

### In `main()`:

- Create an attendance list.
- Add at least 5 student names.
- Include one duplicate name intentionally.
- Insert one student at index `2`.
- Update one student name.
- Remove one student by index.
- Remove one student by name.
- Search for:
  - one existing student
  - one missing student
- Print the final list and size.

---

# 🔹 Question 2: Service Desk Queue Manager (LinkedList + Comparison)

A customer service desk processes support requests in arrival order. Sometimes urgent requests are inserted at the front. Build a queue-style system using **LinkedList** and compare where it is useful.

### Requirements

Create a class:

`ServiceDeskQueue`

---

### Implement the following methods:

1. `addNormalRequest(LinkedList<String> requests, String request)`
   - Add a request at the end of the queue.

2. `addPriorityRequest(LinkedList<String> requests, String request)`
   - Add a request at the front of the queue.

3. `processNextRequest(LinkedList<String> requests)`
   - Remove and return the first request.

4. `peekFirstRequest(LinkedList<String> requests)`
   - Return the first request without removing it.

5. `peekLastRequest(LinkedList<String> requests)`
   - Return the last request without removing it.

6. `getRequestAt(LinkedList<String> requests, int index)`
   - Return the request at the given index.

---

### Concepts to Demonstrate

- **LinkedList strengths**
  - Add comments explaining why:
    - `addFirst()` is O(1)
    - `addLast()` is O(1)
    - `removeFirst()` is O(1)

- **LinkedList limitation**
  - Add a comment explaining why `get(index)` is O(n).

- **ArrayList vs LinkedList choice**
  - Write a short comparison comment answering:
    - Why is `LinkedList` suitable for queue-like behavior?
    - Why is `ArrayList` better for random access?

- **Small comparison table**
  - Print a table like:

  | Operation | Best Choice | Reason |
  |----------|-------------|--------|
  | Frequent add at end | ... | ... |
  | Frequent add/remove at front | ... | ... |
  | Frequent get(index) | ... | ... |
  | Full iteration / display | ... | ... |

---

### In `main()`:

- Create a `LinkedList<String>` queue of service requests.
- Add at least 4 normal requests.
- Add 1 priority request at the front.
- Print the queue.
- Peek first and last request.
- Process two requests.
- Access one request by index.
- Print the queue again.
- Print the comparison table.

---

# 🔹 Question 3: Safe Task List Cleaner (Iteration Safety + Real-World Use)

A task tracking application stores a list of pending tasks. Some tasks are marked as completed and must be removed safely without causing runtime errors.

This question is slightly more advanced than the first two.

### Requirements

Create a class:

`TaskListCleaner`

---

### Implement the following methods:

1. `removeCompletedTasksIterator(List<String> tasks)`
   - Remove tasks ending with `"-done"` using an **Iterator**.

2. `removeCompletedTasksRemoveIf(List<String> tasks)`
   - Remove tasks ending with `"-done"` using `removeIf()`.

3. `createPendingTaskList(List<String> tasks)`
   - Return a new list containing only tasks that are still pending.

4. `demonstrateUnsafeRemoval(List<String> tasks)`
   - Show, using `try-catch`, what happens when removing an element directly inside a for-each loop.
   - Catch and print the exception name.

---

### Concepts to Demonstrate

- **ConcurrentModificationException**
  - Add a short comment explaining why removing from a list inside a for-each loop is unsafe.

- **Safe approaches**
  - Show at least two correct approaches:
    - `Iterator.remove()`
    - `removeIf()`

- **Real-world reasoning**
  - Add a short comment explaining which list implementation you would choose for:
    - chat messages
    - support queue
    - product catalog

- **Common mistake awareness**
  - Mention any two of the following in comments:
    - using `LinkedList` for frequent `get(index)`
    - assuming middle insert in `LinkedList` is always fast
    - using `Arrays.asList()` and then trying to add elements
    - using `List<Integer>` when `int[]` would be more memory-efficient for large primitive data

---

### In `main()`:

- Create a task list with at least 8 items.
- Mark at least 3 tasks as completed using the suffix `-done`.
- Run:
  - iterator-based removal
  - `removeIf()` removal
  - new-list filtering approach
- Demonstrate the unsafe removal inside a for-each loop and catch the exception.
- Print the result of each approach clearly.

---

# 📌 Submission Requirements

Students must submit:

- Complete source code (`.java` files)
- Proper class and method structure
- Code comments explaining:
  - time complexity of list operations
  - where `ArrayList` should be used
  - where `LinkedList` should be used
  - why safe removal during iteration matters
- Output demonstrating all required operations and comparisons

---

# 🎯 Evaluation Criteria

| Criteria | Marks |
|----------|-------|
| Correct use of `List` and `ArrayList` operations (Q1) | 35 |
| Correct use of `LinkedList` and comparison reasoning (Q2) | 30 |
| Safe iteration and removal techniques (Q3) | 25 |
| Code comments, readability, and structure | 10 |

Total: **100 Marks**

---

# 💡 Bonus (Optional)

Choose **one** of the following enhancements:

1. **Pre-allocation demo** — create an `ArrayList<Integer>` with and without an initial capacity and write a short observation about why pre-allocation can reduce resize overhead.

2. **Browser history mini-simulation** — use `LinkedList` or `Deque` to simulate visiting pages and going back to the previous page.

3. **Mutable copy fix** — show why `Arrays.asList()` is fixed-size and then create a proper mutable copy using `new ArrayList<>(Arrays.asList(...))`.
