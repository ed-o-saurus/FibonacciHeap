package us.behn.fibonacci_heap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of a Fibonacci Heap.
 * See <a href="https://en.wikipedia.org/wiki/Fibonacci_heap">here</a> for more details. 
 * 
 * @author Ed Behn (ed@behn.us)
 * @param <T> The type of value to be stored in the heap
 */
final public class FibonacciHeap<T extends Comparable<T>> implements Iterable<T>, Iterator<T> {
	/**
	 * A node in a Fibonacci heap
	 */
	final public class Node implements Comparable<Node>{
		/**
		 * The value of the node
		 */
		private T value;
		
		/**
		 * Number of children of the node
		 */
		private int degree;
		
		/**
		 * Has this node lost a child?
		 */
		private boolean mark;
		
		/**
		 * Parent node (null if a root node)
		 */
		private Node parent;
		
		/**
		 * One child node (null if there are no children)
		 */
		private Node child;
		
		/**
		 * Sibling of the node.
		 * References other children of the same parent or other root nodes. 
		 */
		private Node prevSib, nextSib;
		
		/**
		 * Has this node been removed from the heap?
		 */
		private boolean orphan;
		
		/**
		 * Creates a new node with no siblings.
		 * @param value value to be stored by node
		 */
		private Node(T value) {
			this.value = value;
			
			prevSib = this;
			nextSib = this;
		}
		
		/** Returns the value of the node.
		 * @return value of node
		 */
		public T getValue() { return value; } 
		
		/**
		 * Joins two sets of siblings.
		 * @param other a node in the other set of siblings
		 */
		private void join(Node other) {
			this.nextSib.prevSib = other.prevSib;
			other.prevSib.nextSib = this.nextSib;
			
			this.nextSib = other;
			other.prevSib = this;
		}
		
		/**
		 * Sets the parents of this node and its siblings to null.
		 */
		private void clearParents() {
			Node node = this;
			
			do {
				node.parent = null;
				
				node = node.nextSib;
			} while(node != this);
		}
		
		/**
		 * Removes a node from its siblings.
		 * @return A (now former) sibling of the node or null if there are none
		 */
		private Node removeSib() {
			if(prevSib == this)
				return null;
			
			Node otherSib = prevSib;
			
			prevSib.nextSib = nextSib;
			nextSib.prevSib = prevSib;
			
			prevSib = this;
			nextSib = this;
			
			return otherSib;
		}
		
		/**
		 * Adds a child to a node.
		 * @param child the child to be added
		 */
		private void addChild(Node child)
		{
			if(this.child == null)
				this.child = child;
			else
				this.child.join(child);
			
			child.parent = this;
			
			degree++;
		}
		
		/**
		 * Has this node been removed from the heap?
		 * @return {@code true} iff the node has been removed from the heap
		 */
		public boolean isOrphan() {
			return orphan;
		}
		
		/**
		 * Decreases the value of the node. The new value must be less than or equal to the former value.
		 * @param newValue the new value of the node
		 * @throws IllegalArgumentException if the node has been removed form the heap or the new value is greater than the former value
		 */
		public void decrease(T newValue) throws IllegalArgumentException {
			if(isOrphan())
				throw new IllegalArgumentException("Node does not belong to heap");
			
			if(value.compareTo(newValue) < 0) 
				throw new IllegalArgumentException("New value must be less than current value");
			
			value = newValue;
			
			if(parent != null && compareTo(parent) < 0) {
				Node node_parent = parent;
				cut();
				node_parent.cascadingCut();
			}
			
			if(compareTo(min) < 0)
				min = this;
		}
		
		/**
		 * Removes node from the heap.
		 * @throws IllegalArgumentException if the node has been removed form the heap
		 */
		public void remove() throws IllegalArgumentException {
			if(isOrphan())
				throw new IllegalArgumentException("Node does not belong to heap");
			
			value = null;
			
			if(parent != null) {
				Node node_parent = parent;
				cut();
				node_parent.cascadingCut();
			}
			
			min = this;
			extractMin();
		}
		
		/**
		 * Cuts the node from its tree and makes it a new root node.
		 */
		private void cut()
		{
			parent.degree--;
			parent.child = removeSib();
			
			parent = null;
			min.join(this);
			
			mark = false;
		}
		
		/**
		 * Cascades the cutting of nodes if node is marked.
		 */
		private void cascadingCut()
		{
			if(parent == null)
				return;
			
			if(mark) {
				Node parent_node = parent;
				cut();
				parent_node.cascadingCut();
			} else
				mark = true;
		}
		
		@Override
		public int compareTo(Node node) {
			if (value == null && node.value == null)
				return 0;
			
			if (value == null) 
				return -1;
			
			if (node.value == null)
				return 1;
			
			return value.compareTo(node.value);
		}
	}
	
	/**
	 * The node with the minimum value or null if the heap is empty
	 */
	private Node min;
	
	/**
	 *  The number of nodes in the heap
	 */
	private int size;
	
	/**
	 * Creates empty heap.
	 */
	public FibonacciHeap() {}
	
	/**
	 * Returns the number of nodes in the heap.
	 * @return number of nodes in the heap
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Inserts new value into the heap
	 * @param value value to be inserted
	 * @return newly inserted Node object 
	 * @throws IllegalArgumentException if the value is null
	 */
	public Node insert(T value) throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("value must not be null");
		
		Node node = new Node(value);
		
		if(min == null)
			min = node;
		else {
			min.join(node);
			
			if(node.compareTo(min) < 0)
				min = node;
		}
		
		size++;
		
		return node;
	}
	
	/**
	 * Returns the minimum value of all nodes in heap.
	 * @return minimum value of all nodes in heap
	 * @throws NoSuchElementException if the heap is empty
	 */
	public T getMin() throws NoSuchElementException {
		if(min == null)
			throw new NoSuchElementException("Empty Heap");
		
		return min.value;
	}
	
	/**
	 * Removes and returns the minimum value of all nodes in heap.
	 * @return minimum value of all nodes in heap
	 * @throws NoSuchElementException if the heap is empty
	 */
	public T extractMin() throws NoSuchElementException {
		Node node = min;
		
		if(node == null)
			throw new NoSuchElementException("Empty Heap");
		
		min = node.removeSib();
		
		if(node.child != null) {
			node.child.clearParents();
			
			if(min != null)
				min.join(node.child);
			else
				min = node.child;
		}
		
		size--;
		if(min != null)
			consolidate();
		
		node.orphan = true;
		return node.value;
	}
	
	/**
	 * Is the heap empty?
	 * @return {@code true} iff the heap is empty
	 */
	public boolean isEmpty() { return min == null; }
	
	/**
	 * Natural logarithm of the golden ratio
	 */
	private static final double LOG_PHI = Math.log((1 + Math.sqrt(5))/2);
	
	/**
	 * Consolidates trees in the heap.
	 * Makes it so that there are no two trees of the same degree. 
	 */
	private void consolidate() {
		int maxDegree = (int)(Math.log(size)/LOG_PHI);
		
		ArrayList<Node> A = new ArrayList<Node>(maxDegree+1);
		for(int degree = 0; degree <= maxDegree; degree++)
			A.add(null);
		
		while(min != null) {
			Node node;
			
			node = min;
			min = min.removeSib();
			
			addToArray(A, node);
		}
		
		for(Node node : A) {
			if(node == null)
				continue;
			
			if(min == null)
				min = node;
			else {
				min.join(node);
				
				if(node.compareTo(min) < 0)
					min = node;
			}
		}
	}
	
	/**
	 * Add {@code node} to {@code A} at index degree of {@code node}.
	 * If the element is occupied, the existing tree and the new tree are merged and added to the appropriate spot.
	 * Continues recursively. 
	 * @param A array containing trees created so far indexed by degree
	 * @param node node to be added to the array
	 */
	private void addToArray(ArrayList<Node> A, Node node) {
		if(A.get(node.degree) == null )
			A.set(node.degree, node);
		else {
			Node existing_node = A.get(node.degree);
			A.set(node.degree, null);
			
			if(existing_node.compareTo(node) < 0) { // existing_node < node
				existing_node.addChild(node);
				addToArray(A, existing_node);
			} else {
				node.addChild(existing_node);
				addToArray(A, node);
			}
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return this;
	}
	
	@Override
	public boolean hasNext() {
		return !isEmpty();
	}
	
	@Override
	public T next() throws NoSuchElementException {
		return extractMin();
	}
}
