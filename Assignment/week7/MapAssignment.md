# 🧩 Programming Assignment: Java Maps Deep Dive

## 📌 Objective
This assignment is designed to help students practice **Java Map fundamentals**, understand the differences between **HashMap**, **TreeMap**, and **LinkedHashMap**, and use maps in simple real-world scenarios.

Students should demonstrate understanding of:

### Map Fundamentals
- Key-value pairs
- Unique keys
- Duplicate values allowed
- Retrieval by key

### Map Implementations
- `HashMap` for fast average lookup
- `LinkedHashMap` for insertion-order preservation
- `TreeMap` for sorted key order

### Practical Usage
- Updating values for existing keys
- Safe retrieval using `getOrDefault()`
- Frequency counting
- Sorted reporting

---

# 🔹 Question 1: Product Price Directory (HashMap Basics)

A small store wants to maintain a product-price directory. Each product name should map to exactly one price. If the same product is inserted again, its price should be updated.

### Requirements

Create a class:

`ProductPriceDirectory`

---

### Implement the following methods:

1. `addOrUpdateProduct(Map<String, Integer> prices, String product, int price)`
   - Add a new product or update the price of an existing product.
   - Return the old price if it existed, otherwise return `null`.

2. `getPrice(Map<String, Integer> prices, String product)`
   - Return the price of the product.

3. `getPriceOrDefault(Map<String, Integer> prices, String product, int defaultValue)`
   - Return the price if present, otherwise return the default value.

4. `removeProduct(Map<String, Integer> prices, String product)`
   - Remove the product and return the old price.

5. `printDirectory(Map<String, Integer> prices)`
   - Print all products with their prices.

---

### Concepts to Demonstrate

- **Unique keys**
  - Show that inserting the same product name again updates the value instead of creating a duplicate key.

- **Return value of `put()`**
  - Print the old value returned when a product price is updated.

- **Complexity Annotation**
  - Add comments above the methods for:
    - `put()` → O(1) average
    - `get()` → O(1) average
    - `containsKey()` → O(1) average
    - `remove()` → O(1) average

- **Program to interface**
  - Declare the variable as `Map<String, Integer>` and create it with `new HashMap<>()`.

- **Safe retrieval**
  - Add a short comment explaining why `getOrDefault()` is safer than directly unboxing a missing value.

---

### In `main()`:

- Create a product-price map.
- Add at least 5 products.
- Update the price of at least 2 existing products.
- Get the price of one existing product.
- Get the price of one missing product using `getOrDefault()`.
- Remove one existing product and one missing product.
- Print the final map and size.

---

# 🔹 Question 2: Classroom Marks Register (LinkedHashMap + Iteration)

A teacher stores student marks in the order they were entered. Build a marks register using **LinkedHashMap** so that insertion order is preserved.

### Requirements

Create a class:

`MarksRegister`

---

### Implement the following methods:

1. `addStudentMark(LinkedHashMap<String, Integer> marks, String student, int score)`
   - Add or update a student's mark.

2. `printUsingEntrySet(Map<String, Integer> marks)`
   - Print all entries using `entrySet()`.

3. `printUsingForEach(Map<String, Integer> marks)`
   - Print all entries using `forEach()`.

4. `containsStudent(Map<String, Integer> marks, String student)`
   - Return whether the student exists in the register.

5. `containsScore(Map<String, Integer> marks, int score)`
   - Return whether any student has that score.

---

### Concepts to Demonstrate

- **LinkedHashMap behavior**
  - Add a comment explaining that `LinkedHashMap` preserves insertion order.

- **Key vs value lookup**
  - Add a comment explaining:
    - `containsKey()` is efficient
    - `containsValue()` is O(n)

- **Efficient iteration**
  - Add a short comment explaining why `entrySet()` is usually better than iterating with `keySet()` and then calling `get()`.

- **Output table**
  - Print a table like:

  | Student | Mark |
  |---------|------|
  | ... | ... |

---

### In `main()`:

- Create a `LinkedHashMap<String, Integer>`.
- Add at least 5 students with marks.
- Update one student's mark.
- Print the register using both `entrySet()` and `forEach()`.
- Check for:
  - one existing student
  - one missing student
  - one existing score
  - one missing score

---

# 🔹 Question 3: Word Frequency Reporter (HashMap + TreeMap - Slightly Medium)

A document processor needs to count how many times each word appears in a sentence. Build a frequency report and then print the result in sorted key order.

This question is slightly more advanced than the first two.

### Requirements

Create a class:

`WordFrequencyReporter`

---

### Implement the following methods:

1. `countWords(String[] words)`
   - Return a `HashMap<String, Integer>` containing word frequencies.

2. `countWordsUsingMerge(String[] words)`
   - Return a `HashMap<String, Integer>` using `merge()` for counting.

3. `sortedReport(Map<String, Integer> freq)`
   - Return a `TreeMap<String, Integer>` so the output is sorted by word.

4. `printReport(Map<String, Integer> freq)`
   - Print each word and its count.

---

### Concepts to Demonstrate

- **Frequency counting**
  - Add comments explaining how repeated keys are used to update counts.

- **Java 8 method usage**
  - Use `merge()` in one method.

- **Why TreeMap here?**
  - Add a short comment explaining why `TreeMap` is useful when sorted key output is required.

- **Complexity Annotation**
  - Add comments above the methods for:
    - `HashMap` insert/update → O(1) average
    - `TreeMap` insert → O(log n)

---

### In `main()`:

- Use a word array such as:
  - `{"java", "map", "java", "set", "list", "map", "java"}`
- Build the frequency map using both approaches.
- Print the unsorted frequency map.
- Convert it to a sorted `TreeMap` report.
- Print the sorted report clearly.

---

# 📌 Submission Requirements

Students must submit:

- Complete source code (`.java` files)
- Proper class and method structure
- Code comments explaining:
  - how maps store key-value pairs
  - where `HashMap`, `LinkedHashMap`, and `TreeMap` should be used
  - why `getOrDefault()` and `merge()` are useful
  - the time complexity of the main operations
- Output demonstrating all required operations and reports

---

# 🎯 Evaluation Criteria

| Criteria | Marks |
|----------|-------|
| Correct use of `HashMap` and update behavior (Q1) | 35 |
| Correct use of `LinkedHashMap` and iteration methods (Q2) | 30 |
| Correct frequency counting and sorted reporting (Q3) | 25 |
| Code comments, readability, and structure | 10 |

Total: **100 Marks**

---

# 💡 Bonus (Optional)

Choose **one** of the following enhancements:

1. **Session cache mini-demo** — use `LinkedHashMap` to store recent user sessions and explain why insertion order or access order can be useful.

2. **Anagram checker** — use a map to count character frequency in two strings and decide whether they are anagrams.

3. **Grouping data** — create a map from department name to a list of employee names using `computeIfAbsent()`.
