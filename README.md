# Fibonacci Heap
This is a Java implementation of the Fibonacci Heap data structure.

More information about Fibonacci Heaps can be found at https://en.wikipedia.org/wiki/Fibonacci_heap.

They are a fascinating (if not terribly practical) data structure.

This implementation allows the user to create a heap to store a type that implements the [`Comparable`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Comparable.html) interface.

* Values can be inserted in $\mathcal{O} \left( 1 \right)$ time.

* The minimum value can be read in $\mathcal{O} \left( 1 \right)$ time.

* The minimum value can be extracted in $\mathcal{O} \left( \log n \right)$ amortized time.

* An existing key can have its value decreased in $\mathcal{O} \left( 1 \right)$ amortized time.