# 🧩 Programming Assignment: Arrays Deep Dive in Java

## 📌 Objective
This assignment is designed to help students practice **array fundamentals**, **array-based problem-solving patterns**, and **2D array processing** in Java.

Students should demonstrate understanding of:

### Array Fundamentals
- Array memory model
- Core operations and their time complexity
- Fixed-size nature of arrays
- Copying and updating array data

### Array Problem-Solving Patterns
- Two pointers
- Sliding window
- Prefix sum
- Choosing the right approach for performance

### 2D Arrays
- Row/column traversal
- Matrix-based computations
- 2D prefix sum for rectangle queries
- Real-world use of tabular data

---

# 🔹 Question 1: Event Seat Manager (Array Basics + Complexity)

An auditorium stores booked seat numbers in an integer array. Since arrays are fixed-size, your job is to simulate common array operations such as **searching, inserting, deleting, and copying** using helper methods.

### Requirements

Create a class:

`SeatManager`

---

### Implement the following methods:

1. `linearSearch(int[] seats, int target)`
   - Return the index of `target` if found, otherwise return `-1`.

2. `insertSeat(int[] seats, int currentSize, int index, int newSeat)`
   - Insert `newSeat` at the given index.
   - Shift elements to the right.
   - Return the updated logical size.

3. `deleteSeat(int[] seats, int currentSize, int index)`
   - Delete the element at the given index.
   - Shift remaining elements to the left.
   - Return the updated logical size.

4. `copyBookedSeats(int[] seats, int currentSize)`
   - Return a new array containing only the currently used elements.

5. `printSeats(int[] seats, int currentSize)`
   - Print only the logical elements, not the unused capacity.

---

### Requirements

Your program must demonstrate:

- **Time Complexity Annotation**
  - Add a comment above each method stating its time complexity:
    - Search → O(n)
    - Insert → O(n)
    - Delete → O(n)
    - Copy → O(n)

- **Operation Counting**
  - Add a static `operationCount` variable.
  - Increment it for every comparison or element shift in `linearSearch`, `insertSeat`, and `deleteSeat`.
  - Print the count after each operation.

- **Array Capacity vs Logical Size**
  - Use an array with extra capacity, such as `new int[10]`, while the actual number of booked seats may be smaller.
  - Clearly print both:
    - physical capacity
    - logical size

- **Scaling Observation**
  - Test search on arrays of size 100, 1,000, 10,000, and 50,000.
  - Search for:
    - an element near the beginning
    - an element near the end
    - a missing element
  - Print a small comparison table showing how operation count changes.

---

### In `main()`:

- Start with booked seats: `{101, 104, 108, 112, 116}` stored inside a larger array.
- Search for:
  - `108`
  - `999`
- Insert seat `110` at index `3`.
- Delete the seat at index `1`.
- Create a copy of the active seats and print it.
- Print the scaling table for search.

---

# 🔹 Question 2: Delivery Analytics Dashboard (Two Pointers + Sliding Window + Prefix Sum)

A delivery company tracks the number of packages handled per hour in an array. Build a small analytics utility that answers different questions efficiently using array patterns.

### Requirements

Create a class:

`DeliveryAnalytics`

---

### Implement the following methods:

1. `closestLoadPair(int[] loads, int target)`
   - The input array is sorted.
   - Find the pair of hourly loads whose sum is **closest** to the target.
   - Return the two values as an array of size 2.
   - Use the **two pointers** technique.

2. `maxPackagesInKHours(int[] loads, int k)`
   - Return the maximum total packages handled in any contiguous block of `k` hours.
   - Use the **sliding window** technique.

3. `minHoursToReachTarget(int[] loads, int target)`
   - Return the minimum number of consecutive hours needed to reach at least `target` packages.
   - If impossible, return `-1`.
   - Use a **variable sliding window**.

4. `buildPrefixSum(int[] loads)`
   - Build and return the prefix sum array.

5. `rangeTotal(int[] prefix, int left, int right)`
   - Return the total packages from index `left` to `right` inclusive using the prefix sum array.

---

### Concepts to Demonstrate

- **Pattern Selection**
  - Add comments explaining why:
    - two pointers works because the array is sorted
    - sliding window avoids recomputing the same sum repeatedly
    - prefix sum makes repeated range queries efficient

- **Complexity Annotation**
  - Add a comment above each method stating time and space complexity.

- **Comparison with a Naive Approach**
  - For `maxPackagesInKHours`, also write a simple brute-force method:
    - `maxPackagesInKHoursBruteForce(int[] loads, int k)`
  - Print both results and compare their operation counts.

- **Output Table**
  - Print a table like:

  | Task | Result |
  |------|--------|
  | Closest pair to target | ... |
  | Max packages in k hours | ... |
  | Minimum hours to reach target | ... |
  | Range total (l..r) | ... |

---

### In `main()`:

- Use a sorted array for pair analysis:
  - `{8, 12, 15, 19, 24, 31, 37, 42}`
- Use hourly loads for the remaining tasks:
  - `{14, 9, 12, 18, 7, 15, 11, 20, 13, 10}`
- Find the pair closest to target `40`.
- Find the maximum packages in any `3` consecutive hours.
- Find the minimum consecutive hours needed to reach at least `45` packages.
- Build a prefix sum array and answer these range queries:
  - total from hour `2` to hour `5`
  - total from hour `0` to hour `9`
- Compare brute force vs sliding window for the max-sum problem.

---

# 🔹 Question 3: City Rainfall Grid Analyzer (2D Arrays + 2D Prefix Sum)

A weather department stores daily rainfall readings for different zones of a city in a 2D integer array. Build a system to analyze this grid efficiently.

### Requirements

Create a class:

`RainfallGrid`

---

### Implement the following methods:

1. `printGrid(int[][] grid)`
   - Print the matrix neatly row by row.

2. `rowTotals(int[][] grid)`
   - Return an array containing total rainfall for each row.

3. `columnTotals(int[][] grid)`
   - Return an array containing total rainfall for each column.

4. `findPeakCell(int[][] grid)`
   - Return the row index, column index, and value of the cell with the highest rainfall.

5. `buildPrefix2D(int[][] grid)`
   - Build and return a 2D prefix sum matrix.

6. `rectangleSum(int[][] prefix, int r1, int c1, int r2, int c2)`
   - Return the total rainfall inside the rectangle from `(r1, c1)` to `(r2, c2)` inclusive.

---

### Concepts to Demonstrate

- **2D Array Traversal**
  - Use nested loops correctly.
  - Add a comment explaining why row-major traversal is natural in Java.

- **2D Prefix Sum**
  - Add comments showing the formula used to build the prefix matrix.
  - Add comments showing the rectangle sum formula.

- **Complexity Annotation**
  - `rowTotals` → O(rows × cols)
  - `columnTotals` → O(rows × cols)
  - `findPeakCell` → O(rows × cols)
  - `buildPrefix2D` → O(rows × cols)
  - `rectangleSum` → O(1)

- **Real-World Interpretation**
  - After each rectangle query, print a sentence such as:
    - `"Total rainfall in the selected city zone = ... mm"`

---

### In `main()`:

- Use this rainfall grid:

```java
int[][] rainfall = {
    {12, 18, 10, 14},
    {20, 16, 22, 11},
    {15, 19, 17, 13},
    {9,  21, 25, 18}
};
```

- Print the full grid.
- Print rainfall total for each row.
- Print rainfall total for each column.
- Print the peak rainfall cell.
- Build the 2D prefix sum matrix.
- Answer these rectangle queries:
  - `(0, 0)` to `(1, 1)`
  - `(1, 1)` to `(3, 3)`
  - `(2, 0)` to `(3, 2)`

---

# 📌 Submission Requirements

Students must submit:

- Complete source code (`.java` files)
- Proper class and method structure
- Code comments explaining:
  - Time complexity and space complexity
  - Why each array pattern is used
  - How logical size differs from physical capacity
  - How prefix sums improve repeated range queries
- Output demonstrating all required operations and tables

---

# 🎯 Evaluation Criteria

| Criteria | Marks |
|----------|-------|
| Correct implementation of array basics and complexity analysis (Q1) | 25 |
| Correct use of two pointers, sliding window, and prefix sum (Q2) | 30 |
| Correct 2D array processing and 2D prefix sum queries (Q3) | 30 |
| Code comments, structure, and readability | 10 |
| Program correctness and output formatting | 5 |

Total: **100 Marks**

---

# 💡 Bonus (Optional)

Choose **one** of the following enhancements:

1. **Seat waitlist merge** — extend Q1 to merge two sorted seat arrays into one sorted result without using collection classes. Analyze the time complexity.

2. **Daily average alert** — extend Q2 to print every contiguous window of size `k` whose average load exceeds a chosen threshold. Explain why sliding window is better than recomputing every window from scratch.

3. **Sub-rectangle maximum** — extend Q3 to find which queried city zone has the highest rainfall among a list of rectangle queries. Use the same 2D prefix sum matrix to answer all queries efficiently.
