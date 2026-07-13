# 🧩 Programming Assignment: Java Sets Deep Dive

## 📌 Objective
This assignment is designed to help students practice **Java Set fundamentals**, understand the differences between **HashSet**, **LinkedHashSet**, and **TreeSet**, and use sets for simple real-world tasks.

Students should demonstrate understanding of:

### Set Fundamentals
- No duplicate elements
- Implementation-dependent ordering
- Basic set operations
- Using the return value of `add()` and `remove()`

### Set Implementations
- `HashSet` for fast average lookup
- `LinkedHashSet` for insertion-order preservation
- `TreeSet` for sorted order

### Practical Usage
- Duplicate removal
- Ordered unique collections
- Set union, intersection, and difference
- Sorted ranking style output

---

# 🔹 Question 1: Unique Book ID Tracker (HashSet Basics)

A library stores book IDs in a set. Some IDs may be entered multiple times by mistake. Build a small system using **HashSet** to track only unique book IDs.

### Requirements

Create a class:

`BookIdTracker`

---

### Implement the following methods:

1. `addBookId(Set<Integer> bookIds, int id)`
   - Add the ID to the set.
   - Return whether the ID was added successfully.

2. `removeBookId(Set<Integer> bookIds, int id)`
   - Remove the ID from the set.
   - Return whether the ID existed.

3. `checkBookId(Set<Integer> bookIds, int id)`
   - Return `true` if the ID exists, otherwise `false`.

4. `printBookIds(Set<Integer> bookIds)`
   - Print all IDs and the set size.

---

### Concepts to Demonstrate

- **No duplicates**
  - Show that adding the same ID again does not increase the size.

- **Return value of `add()`**
  - Print whether each insert was a new value or a duplicate.

- **Complexity Annotation**
  - Add comments above the methods for:
    - `add()` → O(1) average
    - `contains()` → O(1) average
    - `remove()` → O(1) average

- **Program to interface**
  - Declare the variable as `Set<Integer>` and create it with `new HashSet<>()`.

---

### In `main()`:

- Create a set of book IDs.
- Add at least 6 IDs.
- Intentionally repeat at least 2 IDs.
- Check whether two IDs exist.
- Remove one existing ID and one missing ID.
- Print the final set and size.

---

# 🔹 Question 2: Workshop Registration Manager (LinkedHashSet + Set Operations)

A college workshop stores participant names in the order they registered. Duplicate names should not be stored. The system must also compare two workshop groups.

### Requirements

Create a class:

`WorkshopRegistration`

---

### Implement the following methods:

1. `registerParticipant(LinkedHashSet<String> participants, String name)`
   - Add a participant while preserving insertion order.

2. `printParticipants(LinkedHashSet<String> participants)`
   - Print participants in registration order.

3. `getUnion(Set<String> a, Set<String> b)`
   - Return a new set containing all unique names from both sets.

4. `getIntersection(Set<String> a, Set<String> b)`
   - Return a new set containing only names present in both sets.

5. `getDifference(Set<String> a, Set<String> b)`
   - Return names present in `a` but not in `b`.

---

### Concepts to Demonstrate

- **LinkedHashSet behavior**
  - Add a comment explaining that `LinkedHashSet` keeps insertion order while still preventing duplicates.

- **Set operations**
  - Use copy sets and methods like `addAll()`, `retainAll()`, and `removeAll()`.

- **Complexity awareness**
  - Add short comments explaining that the main operations are efficient because hashing is used internally.

- **Comparison output**
  - Print a table like:

  | Operation | Result |
  |----------|--------|
  | Workshop A | ... |
  | Workshop B | ... |
  | Union | ... |
  | Intersection | ... |
  | Difference (A-B) | ... |

---

### In `main()`:

- Create two workshop registration sets.
- Add at least 5 names to each set.
- Include some repeated names across both groups.
- Print both groups.
- Print the union, intersection, and difference.

---

# 🔹 Question 3: Game Score Leaderboard (TreeSet - Slightly Medium)

A game displays scores in sorted order. You need to maintain a leaderboard of unique scores and print them from lowest to highest.

This question is slightly more advanced than the first two.

### Requirements

Create a class:

`GameLeaderboard`

---

### Implement the following methods:

1. `addScore(TreeSet<Integer> scores, int score)`
   - Add a score to the leaderboard.

2. `printScores(TreeSet<Integer> scores)`
   - Print all unique scores in sorted order.

3. `printHighestScore(TreeSet<Integer> scores)`
   - Print the highest score.

4. `printLowestScore(TreeSet<Integer> scores)`
   - Print the lowest score.

5. `printScoresAbove(TreeSet<Integer> scores, int target)`
   - Print all scores strictly greater than the target.

---

### Concepts to Demonstrate

- **TreeSet sorting**
  - Add a comment explaining that `TreeSet` stores elements in sorted order.

- **Complexity Annotation**
  - Add comments above the methods for:
    - `add()` → O(log n)
    - `contains()` → O(log n)
    - `first()` / `last()` → O(log n)

- **Why TreeSet here?**
  - Add a short comment explaining why `TreeSet` is more suitable than `HashSet` when sorted output is required.

- **Leaderboard meaning**
  - Since duplicates are not stored, equal scores should appear only once.

---

### In `main()`:

- Create a `TreeSet<Integer>` for scores.
- Add at least 8 scores.
- Intentionally repeat at least 2 scores.
- Print all scores.
- Print the lowest and highest score.
- Print all scores above `70`.

---

# 📌 Submission Requirements

Students must submit:

- Complete source code (`.java` files)
- Proper class and method structure
- Code comments explaining:
  - how sets prevent duplicates
  - where `HashSet`, `LinkedHashSet`, and `TreeSet` should be used
  - the time complexity of the main operations
- Output demonstrating all required tasks and results

---

# 🎯 Evaluation Criteria

| Criteria | Marks |
|----------|-------|
| Correct use of `HashSet` and duplicate handling (Q1) | 35 |
| Correct use of `LinkedHashSet` and set operations (Q2) | 35 |
| Correct use of `TreeSet` and sorted leaderboard logic (Q3) | 20 |
| Code comments, readability, and structure | 10 |

Total: **100 Marks**

---

# 💡 Bonus (Optional)

Choose **one** of the following enhancements:

1. **Custom object in HashSet** — create a small `Student` class or `record` and show why correct `equals()` and `hashCode()` are needed to prevent duplicates.

2. **Recent search history** — use `LinkedHashSet` to store recent unique search terms while preserving insertion order.

3. **Top scorer filter** — extend the leaderboard to print scores within a range, such as 60 to 90.
