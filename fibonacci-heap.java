// Amit Porat - amitporat1 - 315390252
// Vicktoria Kraslavski - vicktoriak - 323784488

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode min;
	private int size;
	private static int linkCount;
	private HeapNode firstRoot;
	private static int cuts;
	private int treesCount;
	private int markedCount;
	
	public FibonacciHeap(){
		this.size = 0;
		this.min = null;
		this.firstRoot = null;
	}
	
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	if (this.firstRoot == null) {
    		return true;
    	}
    	return false; 
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    * 
    * Complexity: O(1), updating pointers takes O(1), insertRootAtStart is also O(1).
    */
    public HeapNode insert(int key)
    {    
    	
    	// Creating a new node
    	HeapNode node = new HeapNode(key);
    	
    	// Heap is empty
    	if(this.isEmpty()) {
    		this.setFirstRoot(node);
    		this.setMin(node);
    	}
    	
    	// Heap is not empty
    	else {
    		// Inserting the node at start
    		this.insertRootAtStart(node);
        	
        	// Updating min
        	if(this.findMin().getKey() > key) {
        		this.setMin(node);
        	}
    	}
    	// Updating size
    	this.setSize(this.size() + 1);
    	this.setTreesCount(this.getTreesCount() + 1);
    	
    	// Returning the node inserted
    	return node; 
    }
   /**
    * private void insertRootAtStart
    * 
    * Inserts the node NewFirst at the start of the root list
    * 
    * Preconditions: heap is not empty, newFirst is not null
    * 
    * Complexity: O(1) - only updating pointers.
    */
    
    private void insertRootAtStart(HeapNode newFirst) {
    	
		// Getting the pointers of first and last roots in heap
    	HeapNode oldFirst = this.getFirstRoot();
    	HeapNode last = oldFirst.getPrev();
    	
    	// Connecting the new root at the first place
    	this.setFirstRoot(newFirst);
    	newFirst.setNext(oldFirst);
    	newFirst.setPrev(last);
    	oldFirst.setPrev(newFirst);
    	last.setNext(newFirst);
    	
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    * First deleting min and replacing him with his children
    * Then preforms successive links on the heap
    * At last updates size and min of heap
    * 
    * Complexity: amortized Cost O(logn), WC O(n), as shown in class.
    *
    */
    public void deleteMin()
    {
     	HeapNode min = this.findMin();
     	
    	// Min is the only node in heap, deleting him
    	if(this.size() == 1) {
    		this.setFirstRoot(null);
    		this.setMin(null);
    		this.setTreesCount(0);
    	}
    	
    	else { // Min is not the only node in heap
    		// Replacing min with his children
    		this.childrenReplaceMin(min);
    		
    		// Successive linking on remaining trees in heap
    		this.successiveLinking();
    		
    		// Updating min
    		findNewMin();
    	}
			// Updating size - we deleted one node
			this.setSize(this.size() - 1);
    	
    }
    
    
   /**
    * private void childrenReplaceMin(HeapNode min)
    * 
    * Delets min and replaces him with children in roots list.
    * 
    * Preconditions: min is not the only node in roots list
    * 
    * Complexity: O(logn) in WC - because using detchChildren which is O(logn)
    */
    
    private void childrenReplaceMin(HeapNode min) {
    	HeapNode minPrev = min.getPrev();
    	HeapNode minNext = min.getNext();
    	
    	// If min is first root, updating first root
    	// If min is only root - nothing changed
		
    	
    	// Min has no children - bypassing min
		if(min.getChild() == null) {
			if(this.getFirstRoot() == min) {
    			this.setFirstRoot(min.getNext());
    		}
    		minPrev.setNext(min.getNext());
    		minNext.setPrev(min.getPrev());
    		min.setNext(null);
    		min.setPrev(null);
    		
    	}
    	
    	else { // Min has at least one child
        	HeapNode firstChild = min.getChild();
        	HeapNode lastChild = firstChild.getPrev();
        	if(this.getFirstRoot() == min) {
    			this.setFirstRoot(firstChild);
    		}
        	
        	if(min.getNext() == min) { // Min is the only root in heap
        		this.setFirstRoot(firstChild);
        	}
        	else {
        		// Attaching children of min in his place
            	minPrev.setNext(firstChild);
            	firstChild.setPrev(minPrev);
            	minNext.setPrev(lastChild);
            	lastChild.setNext(minNext);
            
        	}
        	// Detaching children from min and min from list
        	min.setNext(null);
        	min.setPrev(null);
        	min.setChild(null);
        	detachChildren(firstChild);
        	
    	}
    	
    }
    
   /**
    * private void detachChildren(HeapNode firstChild)
    * 
    * looping over the list of children and detaching them from parent.
    * If the child was mark -> unmark it since it becomes a root.
    * 
    * Complexity: O(logn) in WC, in case the rank of parent is logn 
    * 
    */
    private void detachChildren(HeapNode firstChild) {
    	HeapNode pointer = firstChild;
    	while(pointer.getParent() != null) {
    		if (pointer.isMarked()) {
    			pointer.unmark();
    			this.markedCount -= 1;
    		}
    		pointer.setParent(null);
    		pointer = pointer.getNext();
    	}
    }

   /**
    * private void successiveLinking()
    *  
    * Performing successive linking on the heap.
    *  
    * Complexity: O(n) in WC.
    */
    private void successiveLinking() {
    	
    	// Creating the "buckets" array for the trees
    	int arraySize = (int)(Math.ceil(1.4404 * (Math.log(this.size()) / Math.log(2))));
    	HeapNode[] treesArray = new HeapNode[arraySize + 1];
    	
    	// Linking the trees of the same rank
    	while(this.getFirstRoot() != null) {
    		HeapNode tree = detachFirstTree(this.getFirstRoot());
    		compareAndLink(tree, treesArray);
    	}
    	
    	// Inserting the trees back to the heap
    	for(int i = treesArray.length-1; i>=0; i--) {
    		if(treesArray[i] != null) {
    			if (this.getFirstRoot() == null) { // Heap is empty
    				this.setFirstRoot(treesArray[i]);
    			}
    			else {
    				this.insertRootAtStart(treesArray[i]);
    			}
    		}
    		
    	}
    	
    }
   /**
    * private HeapNode detachFirstTree(HeapNode tree)
    * 
    * Detaching first root from the root list.
    * returning the root detached.
    * 
    * Preconditions: only activated on first root.
    * 
    * Complexity: O(1).
    */
    private HeapNode detachFirstTree(HeapNode tree) {
    	
    	if(tree.getNext() == tree) { // Tree is only tree in heap
    		this.setFirstRoot(null);
    	}
    	else {
    		
    		HeapNode next = tree.getNext();
    		HeapNode prev = tree.getPrev();
    		
    		this.setFirstRoot(next);
    		next.setPrev(prev);
    		prev.setNext(next);
    		
    		tree.setNext(tree);
    		tree.setPrev(tree);
    	}
    	
    	return tree;
    }
    
    /**
     * public HeapNode link(HeapNode tree1, HeapNode tree2)
     * 
     * Linking tree1 and tree2,
     * returning pointer to new tree.
     * 
     * Preconditions: tree1 and tree2 of equal rank.
     * 
     * Complexity: O(1).
     */
     private HeapNode link(HeapNode tree1, HeapNode tree2) {
     	// Making sure that tree1.key < tree2.key
     	if(tree1.getKey() > tree2.getKey()) {
     		HeapNode temp = tree1;
     		tree1 = tree2;
     		tree2 = temp;
     	}
     	
     	// If tree1 has children - connecting tree2 to them
     	if(tree1.getChild() != null) {
     		HeapNode firstChild = tree1.getChild();
     		HeapNode lastChild = firstChild.getPrev();
     		
     		tree2.setNext(firstChild);
     		firstChild.setPrev(tree2);
     		tree2.setPrev(lastChild);
     		lastChild.setNext(tree2);
     	}
     	
     	// Tree1 becomes the father of tree2
     	tree1.setChild(tree2);
     	tree2.setParent(tree1);
     	
     	// We added another child - updating rank
     	tree1.setRank(tree1.getRank() + 1);
     	
     	// Counting num of links in program
     	FibonacciHeap.linkCount += 1;
     	
     	return tree1;
     }
    
    /**
     * public void compareAndLink(HeapNode tree, HeapNode[] treeArray)
     * 
     * Checks if a tree of equal rank of tree exists in treeArray
     * If so, linkes them and calls itself with linked tree
     * Else, adding tree to treeArray
     * 
     * Complexity: O(logn) in case almost all entries of array is full with trees,
     * and we are linking O(logn) trees.
     */
     private void compareAndLink(HeapNode tree, HeapNode[] treeArray) {
    	 
    	 if(treeArray[tree.getRank()] == null) {
    		 treeArray[tree.getRank()] = tree;
    	 }
    	 else {
    		 HeapNode treeToLink = treeArray[tree.getRank()];
    		 treeArray[tree.getRank()] = null;
    		 HeapNode linkedTree = link(treeToLink, tree);
    		 compareAndLink(linkedTree, treeArray);
    	 }
    	 
     }
     
    /**
     * public void findNewMin() 
     * 
	 * Looping over all roots in heap, and finding the min
	 * Updating min found to be new min of heap, and updating treesCount
	 * 
	 * Complexity: O(logn) - called only after delete-min,
	 * so number of trees is at most O(long)
	 */
    public void findNewMin() {
    	HeapNode min = this.getFirstRoot();
    	HeapNode pointer = min.getNext();
    	int newTreesCount = 1;
    	
    	while(pointer != this.getFirstRoot()) {
    		newTreesCount += 1;
    		if(pointer.getKey() < min.getKey()) {
    			min = pointer;
    		}
    		pointer = pointer.getNext();
    	}
    	
    	this.setMin(min);
    	this.setTreesCount(newTreesCount);
    }
     
    /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal.
    * 
    * Complexity: O(1)
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void setMin(HeapNode newMin)
    * 
    * Sets min to be newMin
    * 
    * Complexity: O(1)
    * 
    */
    public void setMin(HeapNode newMin) {
    	this.min = newMin;
    }
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    * 
    * Complexity: O(1) - only changing pointers.
    */
    public void meld (FibonacciHeap heap2)
    {
    	
    	// Both heaps are not empty
    	if(!this.isEmpty() && !heap2.isEmpty()) {
    		// Updating min node in new heap
        	if(this.findMin().getKey() > heap2.findMin().getKey()) {
        		this.setMin(heap2.findMin());
        	}
        	
        	// Updating size in new heap
        	this.setSize(heap2.size() + this.size());
        	
        	// Connecting the heaps
        	this.addToRoots(heap2.getFirstRoot());
        	
    	}
    	// this Heap is empty
    	else if (this.isEmpty() && !heap2.isEmpty()) {
    		this.setFirstRoot(heap2.getFirstRoot());
    		this.setMin(heap2.findMin());
    		this.setSize(heap2.size());
    	}
    	
    	// else if heap 2 is empty or if both empty - nothing to do
    	// Sum the number of trees and the number of marked nodes
    	this.setTreesCount(this.getTreesCount() + heap2.getTreesCount());
    	this.setMarkedCount(this.getMarkedCount() + heap2.getMarkedCount());
    		
    }
    
   /**
    * private void addToRoots(HeapNode otherFirst)
    * 
    * Adding roots to the end of the root list
    * 
    * Preconditions: heap is not empty, otherFirst is not null
    * 
    * Complexity: O(1)
    */
    private void addToRoots(HeapNode otherFirst) 
    {
    	// Getting all the necessary pointers
    	HeapNode thisFirst = this.getFirstRoot();
    	HeapNode thisLast = thisFirst.getPrev();
    	HeapNode otherLast = otherFirst.getPrev();
    	
    	// Connecting otherFirst to roots
    	thisLast.setNext(otherFirst);
    	thisFirst.setPrev(otherLast);
    	otherFirst.setPrev(thisLast);
    	otherLast.setNext(thisFirst);
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    * 
    * Complexity: O(1)
    */
    public int size()
    {
    	return this.size; 
    }
    
   /**
    * public void setSize
    * 
    * Sets size to newSize
    * 
    * Complexity: O(1)
    */
    public void setSize(int newSize) {
    	this.size = newSize;
    }
   /**
    *  public HeapNode getFirstRoot()
    *  
    *  returns the pointer to first Root
    *  
    *  Complexity: O(1)
    */
    public HeapNode getFirstRoot() {
    	return this.firstRoot;
    }
   /**
    * public void setFirstRoot(HeapNode newFirst)
    * 
    * Sets the firstRoot to newFirst
    * 
    * Conplexity: O(1)
    */
    public void setFirstRoot(HeapNode newFirst) {
    	this.firstRoot = newFirst;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    * Complexity: O(n) - in worst case looping over n trees in heap.
    */
    public int[] countersRep()
    {	
    	
    	int[] countArr = new int[this.size()];
    	
    	if(this.isEmpty()) {
    		return countArr;
    	}
    	
    	// Counting all trees
        HeapNode pointer = this.getFirstRoot();
        countArr[pointer.getRank()] += 1;
        pointer = pointer.getNext();
        while(pointer != this.getFirstRoot()) {
        	countArr[pointer.getRank()] += 1;
        	pointer = pointer.getNext();
        }
        
        // Making result array
        
        int lastZero = this.size() - 1;
        while(countArr[lastZero] == 0) {
        	lastZero = lastZero - 1;
        }
        int[] arr = new int[lastZero + 1];
        for(int i = 0; i <= lastZero; i++) {
        	arr[i] = countArr[i];
        }
        
    	return arr; 
    }
    
    /*
     * public static void resetStatics()
     * For the measuring part
     */
    
    public static void resetStatics() {
    	FibonacciHeap.linkCount = 0;
    	FibonacciHeap.cuts = 0;
    }
    
    /*
     * public int getTreesNum()
     * getter for trees count
     * Complexity: O(1)
     */
    
    public int getTreesNum() {
    	return this.treesCount;
    }
    
    /*
     * public int getMarkedCount()
     * getter for marked count
     * Complexity: O(1)
     */
    
    public int getMarkedCount() {
    	return this.markedCount;
    }
    
    /*
     * private void setMarkedCount(int count)
     * setter for marked count
     * Complexity: O(1)
     */
    
    private void setMarkedCount(int count) {
    	this.markedCount = count;
    }
    
   /*
    * private void setTreesCount(int treesCount)
    * Sets this.treesCount
    * Complexity: O(1)
    */
    private void setTreesCount(int treesCount) {
    	this.treesCount = treesCount;
    }
    
    /* private int getTreesCount() {
     * returns this.treesCount
     */
    private int getTreesCount() {
    	return this.treesCount;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    * Complexity: O(
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x, x.getKey() - this.findMin().getKey() + 1);
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey() - delta);
    	// If the node was a root it might become the new min
		if(x.getKey() < this.findMin().getKey()) {
			this.setMin(x);
		}
    	if (!x.isValid()) {
    		this.cascadingCut(x, x.getParent());
    	}
    }
    
    
    /*
     * cascadingCut(HeapNode cutNode, HeapNode parent)
     * This function calls cut function, marks the parent if needed
     * and calls itself recursively if the parent was already marked
     * 
     * Complexity: O(logn)
     */
    private void cascadingCut(HeapNode cutNode, HeapNode parent) {
    	cut(cutNode, parent);
    	FibonacciHeap.cuts += 1;
    	
    	// The parent of the decreased node isn't a root
    	if (parent.getParent() != null) {
    		if (!parent.isMarked()) {
    			parent.mark();
    			this.markedCount += 1;
    		}
    		else {
    			
    			cascadingCut(parent, parent.getParent());
    		}
    	}
    }
    
    /*
     * cut(HeapNode cutNode, HeapNode parent)
     * This function cuts the node and its children from its parent
     * then it inserts the node at the roots list
     */
    private void cut(HeapNode cutNode, HeapNode parent) {
    	cutNode.setParent(null);
    	
    	// The cut node was marked, and now it becomes a root, so we need to unmark it
    	if(cutNode.isMarked()) {
    		cutNode.unmark();
    		this.markedCount -= 1;
    	}
    	parent.setRank(parent.getRank() - 1);
    	
    	// The cut node has no siblings 
    	if (cutNode.getNext() == cutNode) {
    		parent.setChild(null);
    	}
    	else {
    		
    		// The cut node was the leftmost child
    		if (parent.getChild() == cutNode) {
    			parent.setChild(cutNode.getNext());
    		}
    		
    		// Bypass the cut node in the siblings list
    		cutNode.prev.setNext(cutNode.getNext());
    		cutNode.next.setPrev(cutNode.getPrev());
    		
    	}
    	this.insertRootAtStart(cutNode);
    	
    	// We don't cut roots, so we added a new root to the roots list
    	this.setTreesCount(this.getTreesCount() + 1);
    }
    
    

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    * 
    * Complexity: O(1)
    */
    public int potential() 
    {    
    	return this.treesCount + 2 * this.markedCount;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    * 
    * Complexity: O(1)
    */
    public static int totalLinks()
    {    
    	return linkCount; 
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    * 
    * Complexity: O(1)
    */
    public static int totalCuts()
    {  
    	return cuts;
    	
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)). 
    * 
    * Complexity: O(k(logk + degH))
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {   
    	FibonacciHeap tempFib = new FibonacciHeap();
        int[] smallest = new int[k];
        HeapNode child = H.findMin();
        
        // Delete the min, insert the children of the deleted node
        for (int i = 0; i < k; i++) {
        	
        	// The deleted node had children
        	if (child != null) {
        		insertSiblings(tempFib, child);
        	}
        
        	HeapNode tempDeleted = tempFib.findMin();
        	smallest[i] = tempDeleted.getKey();
        	HeapNode deleted = tempDeleted.getOriginal();
        	
        	tempFib.deleteMin();
        	
        	child = deleted.getChild();
		}
        
        return smallest; 
    }
    
    /*
     * private static void insertSiblings(FibonacciHeap heap, HeapNode node) {
     * 
     * Inserts the keys of the node and  of its siblings to the heap
     * Updates the original attribute to point to the original node
     * 
     * Complexity: O(degH)
     */
    
    private static void insertSiblings(FibonacciHeap heap, HeapNode node) {
    	
    	HeapNode nextNode = node;
    	HeapNode newNode = null;
    	while (true) {
    		newNode = heap.insert(nextNode.getKey());
    		newNode.setOriginal(nextNode);
    		nextNode = nextNode.getNext();
    		if (nextNode == node)
    			break;
    	}
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	public int rank;
	public int mark;
	public HeapNode child;
	public HeapNode next;
	public HeapNode prev;
	public HeapNode parent;
	private HeapNode original;
	public static final int MARKED = 1;
	public static final int UNMARKED = 0;

  	public HeapNode(int key) {
	    this.key = key;
	    this.rank = 0;
	    this.mark = UNMARKED;
	    this.child = null;
	    this.next = this;
	    this.prev = this;
	    this.parent = null;
	    this.original = null;
      }

  	public boolean isValid() {
  		return ((this.getParent() == null) || this.getParent().getKey() < this.getKey());
  	}
  	public int getKey() {
	    return this.key;
      }
  	public void mark() {
  		this.mark = MARKED;
  	}
  	
  	public void unmark() {
  		this.mark = UNMARKED;
  	}
  	public boolean isMarked() {
  		return (this.mark == MARKED);
  	}
  	
  	public void setKey(int key) {
  		this.key = key;
  	}
  	
  	public int getRank() {
  		return this.rank;
  	}
  	
  	public HeapNode getChild()
  	{
  		return this.child;
  	}
  	
  	public void setChild(HeapNode newChild) {
  		this.child = newChild;
  	}
  	
  	public HeapNode getNext()
  	{
  		return this.next;
  	}
  	
  	public void setNext(HeapNode newNext) {
  		this.next = newNext;
  	}
  	
  	public HeapNode getPrev()
  	{
  		return this.prev;
  	}
  	
  	public void setPrev(HeapNode newPrev) {
  		this.prev = newPrev;
  	}
  	
  	public HeapNode getParent()
  	{
  		return this.parent;
  	}
  	
  	public void setParent(HeapNode newParent) {
  		this.parent = newParent;
  	}
  	
  	public void setRank(int rank) {
  		this.rank = rank;
  	}
  	public HeapNode getOriginal() {
  		return this.original;
  	}
  	public void setOriginal(HeapNode node) {
  		this.original = node;
    }
}
}
