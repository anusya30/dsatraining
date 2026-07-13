# 🧩 Programming Assignment: Arrays Summer Holiday Assignment

## 📌 Objective
This assignment is designed to help students practice **array basics**, **array traversal**, **searching**, and **simple in-place array manipulation** in Java.

Students should demonstrate understanding of:

### Array Fundamentals
- Storing values in arrays
- Accessing elements using index
- Traversing arrays using loops
- Updating array contents

### Array Problem Solving
- Finding minimum and maximum
- Reversing array output
- Counting values by condition
- Searching for an element
- Removing duplicates from a sorted array

---

# 🔹 Question 1: Find Minimum and Maximum

Write a program to store integers in an array and find the **smallest** and **largest** values.

### Requirements

Create a class:

`MinMaxArray`

---

### Implement the following methods:

1. `findMin(int[] arr)`
   - Return the smallest element in the array.

2. `findMax(int[] arr)`
   - Return the largest element in the array.

3. `printArray(int[] arr)`
   - Print all elements of the array.

---

### Concepts to Demonstrate

- Array traversal using loops
- Comparing elements one by one
- Updating min and max while scanning the array

- **Complexity Annotation**
  - `findMin()` → O(n)
  - `findMax()` → O(n)

---

### In `main()`:

- Create an integer array with at least 10 values.
- Print the array.
- Print the minimum value.
- Print the maximum value.

---

# 🔹 Question 2: Reverse an Array Output

Write a program to print the elements of an array in both **original order** and **reverse order**.

### Requirements

Create a class:

`ReverseArrayDisplay`

---

### Implement the following methods:

1. `printOriginal(int[] arr)`
   - Print the array in normal order.

2. `printReverse(int[] arr)`
   - Print the array in reverse order.

---

### Concepts to Demonstrate

- Forward traversal
- Backward traversal
- Using indexes correctly

- **Complexity Annotation**
  - `printOriginal()` → O(n)
  - `printReverse()` → O(n)

---

### In `main()`:

- Create an integer array with at least 8 elements.
- Print the array in original order.
- Print the array in reverse order.

---

# 🔹 Question 3: Count Even and Odd Numbers

Write a program to count how many numbers in an array are **even** and how many are **odd**.

### Requirements

Create a class:

`EvenOddCounter`

---

### Implement the following methods:

1. `countEven(int[] arr)`
   - Return the number of even elements.

2. `countOdd(int[] arr)`
   - Return the number of odd elements.

3. `printArray(int[] arr)`
   - Print all elements of the array.

---

### Concepts to Demonstrate

- Condition checking using `% 2`
- Counting values using variables
- Looping through the entire array

- **Complexity Annotation**
  - `countEven()` → O(n)
  - `countOdd()` → O(n)

---

### In `main()`:

- Create an integer array with at least 10 elements.
- Print the array.
- Print the count of even numbers.
- Print the count of odd numbers.

---

# 🔹 Question 4: Search for an Element

Write a program to search for a given element in an array and print whether it is found.

### Requirements

Create a class:

`LinearSearchDemo`

---

### Implement the following methods:

1. `searchElement(int[] arr, int target)`
   - Return the index of the target if found.
   - Return `-1` if the target is not found.

2. `printArray(int[] arr)`
   - Print the array.

---

### Concepts to Demonstrate

- Linear search
- Returning index position
- Handling the "not found" case clearly

- **Complexity Annotation**
  - `searchElement()` → O(n)

---

### In `main()`:

- Create an integer array with at least 8 elements.
- Print the array.
- Search for one value that exists in the array.
- Search for one value that does not exist in the array.
- Print:
  - `Found at index ...`
  - or `Not found`

---

# 🔹 Question 5: Remove Duplicates from a Sorted Array

Write a program for a **sorted array** that removes duplicate elements and prints only the unique values.

This question is slightly more advanced than the first four.

### Requirements

Create a class:

`SortedArrayDuplicates`

---

### Implement the following methods:

1. `removeDuplicates(int[] arr)`
   - Modify the sorted array so that the first part contains only unique values.
   - Return the new logical size of the unique portion.

2. `printUniqueElements(int[] arr, int size)`
   - Print only the valid unique elements up to the new logical size.

---

### Concepts to Demonstrate

- Understanding sorted arrays
- Comparing current and previous elements
- Maintaining a write position for unique values
- Logical size vs physical array size

- **Complexity Annotation**
  - `removeDuplicates()` → O(n)
  - `printUniqueElements()` → O(n)

---

### In `main()`:

- Create a sorted array such as:
  - `{1, 1, 2, 2, 3, 4, 4, 5, 5, 6}`
- Print the original array.
- Remove duplicates.
- Print:
  - the unique elements
  - the new logical size

---

# 📌 Submission Requirements

Students must submit:

- Complete source code (`.java` files)
- Proper class and method structure
- Code comments where needed
- Output for all 5 questions

---

# 🎯 Evaluation Criteria

| Criteria | Marks |
|----------|-------|
| Correct implementation of array logic | 40 |
| Proper use of loops and conditions | 25 |
| Correct output and testing | 20 |
| Code readability and comments | 15 |

Total: **100 Marks**

---

# 💡 Bonus (Optional)

Do **one** of the following:

1. Write a program to find the **second largest** element in an array.

2. Write a program to **left rotate** an array by one position.

3. Write a program to merge two arrays into a third array and print the result.
