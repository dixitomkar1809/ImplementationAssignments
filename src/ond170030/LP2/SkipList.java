// Change this to netid of any member of team
package ond170030.LP2;
import java.security.KeyStore.Entry;
import java.util.*;
/* Starter code for LP2 */
// Skeleton for skip list implementation.

public class SkipList<T extends Comparable<? super T>> {
    static final int PossibleLevels = 33;
    Entry<T> head, tail;
    private int size, maxLevel;
    Entry<T>[] last;
    Random random;

    static class Entry<E> {
        E element;
        Entry[] next;
        Entry prev;
        int level;
        int[] span;

        public Entry(E x, int lev) {
            element = x;
            next = new Entry[lev];
            this.level = lev;
            this.span = new int[lev];
            // add more code if needed
        }

        public E getElement() {
            return element;
        }
    }

    // Constructor
    public SkipList() {
        this.head = new Entry<>(null, PossibleLevels);
        this.tail = new Entry<>(null, PossibleLevels);
        this.size = 0;
        this.maxLevel = 1;
        this.last = new Entry[33];
        this.random = new Random();
        for(int i =0; i<= PossibleLevels-1; i++){
            this.head.next[i] = this.tail;
            this.head.span[i] = 0;
            this.tail.span[i] = 0;
            this.last[i] = this.head;
        }
    }

    // returns the level for the new skiplist node 
    private int chooseLevel(){
        // Slow Method
        int lev = 1;
        while(this.random.nextBoolean()){
            if(lev >= 33){
                break;
            }else{
                lev+=1;
            }
        }
        if(lev > this.maxLevel){
            maxLevel = lev;
        }
        return lev;
    }

    // Add x to list. If x already exists, reject it. Returns true if new node is added to list
    public boolean add(T x) { 
        if(contains(x)){
            return false;
        }else{
            int lev = this.chooseLevel();
            Entry<T> newNode = new Entry<T>(x, lev);
            // newNode.span[0] = 1;
            for(int i = 0; i <= lev-1; i++){
                newNode.next[i] = this.last[i].next[i];
                this.last[i].next[i] = newNode;
            }
            newNode.next[0].prev = newNode;
            newNode.prev = this.last[0];
            // System.out.println("Added-> "+newNode.getElement());
            this.spanFiller();
            this.size+=1;
            return true;
        }
    }
    // Fills the span array, the span of the last node will be zero as it would be pointing to the tail of the Skiplist, 
    // the span of a node at a certain height which points to the tail will be just the count of nodes it would jump you not including the tail
    private void spanFiller(){
        for(int i=this.maxLevel-1; i>=0; i-- ){
            Entry<T> cursor = this.head;
            while(cursor != this.tail){
                this.spanFinder(cursor, cursor.next[i], i);
                cursor = cursor.next[i];
            }
        }
    }

    // SpanFinder finds the number of nodes you can jump
    // here we use count to store the number of nodes we jump, which is initialized to zero incase of all elements except tail, where as tail is initialized to -1;
    private void spanFinder(Entry<T> n1, Entry<T> n2, int level){
        Entry<T> cursor = n1;
        int count = 0;
        if(n2.equals(this.tail)){
            count-=1;
        }
        while(cursor != n2){
            count+=1;
            n1.span[level] = count;
            cursor = cursor.next[0];
        }
        // System.out.println("Span of "+n1.getElement()+" is "+n1.span[level]);
    }

    // Find smallest element that is greater or equal to x
    public T ceiling(T x) {
        if(this.isEmpty()){
            return null;
        }else{
            this.find(x);
            Entry<T> cursor = this.last[0];
            if(this.contains(x) || cursor != this.tail){
                return (T) cursor.next[0].getElement();    
            }else{
                return null;
            }
        }
    }

    // fills the last array of the node with all the nodes which are visited to find T x
    private void find(T x){
        Entry<T> cursor = this.head;
        for(int i = this.maxLevel - 1; i >= 0; i--){
            while (cursor.next[i] != this.tail && x.compareTo(((T)cursor.next[i].getElement())) > 0) {
                cursor = cursor.next[i];
            }
            this.last[i] = cursor;
        }
    }

    // Does list contain x?
    public boolean contains(T x) {
        this.find(x);
        if(this.last[0].next[0] != this.tail && this.last[0].next[0].getElement().equals(x)){
            return true;
        }else{
            return false;
        }
    }

    // Return first element of list
    public T first() {
        if(isEmpty()){
            return null;
        }
        else{
            Entry<T> cursor = this.head;
            if(cursor.next[0] != this.tail){
                return (T) cursor.next[0].getElement();
            }
        }
        return null;
    }

    // Find largest element that is less than or equal to x
    public T floor(T x) {
        if(this.isEmpty()){
            return null;
        }else{
            if(this.contains(x)){
                return (T) this.last[0].next[0].getElement();
            }else{
                return this.last[0].getElement();
            }
        }
    }

    // Return element at index n of list.  First element is at index 0.
    public T get(int n){
        if(n < 0 || n > size - 1){
            System.out.println("get(): No Such Element");
            return null;
        }
        return this.getLog(n);
    }

    // O(n) algorithm for get(n)
    public T getLinear(int n) {
        if(isEmpty()){
            System.out.println("getLinear(): SkipList is empty");
            return null;
        }else{
            Entry<T> cursor =this.head.next[0];
            for(int i=1; i<=n; i++){
                cursor = cursor.next[0];
            }
            return cursor.getElement();
        }
    }

    // Optional operation: Eligible for EC.
    // O(log n) expected time for get(n). Requires maintenance of spans, as discussed in class.
    public T getLog(int n) {
        if(this.isEmpty()){
            System.out.println("empty list");
            return null;
        }
        else{
            Entry<T> cursor = this.head;
            int position = 0;
            System.out.println("getLog()");
            for(int i= this.maxLevel-1; i>=0 ; i--){
                while((position + cursor.span[i]) <= n ){
                    System.out.println("Level-> "+ i);
                    System.out.println("Cursor is-> "+cursor.getElement());
                    System.out.println("Position-> "+position);
                    position+=cursor.span[i];
                    // if(cursor.next[i] != tail){
                    //     cursor = cursor.next[i];
                    // }
                    cursor = cursor.next[i];
                }
            }
            return cursor.getElement();

        }
    }

    // Is the list empty?
    public boolean isEmpty() {
        if(this.size() == 0){
            return true;
        }else{
            return false;
        }
    }

    // Iterate through the elements of list in sorted order
    public Iterator<T> iterator() {
        return (Iterator<T>) new SkipListIterator();
    }

    protected class SkipListIterator implements SLIterator<T>{
        Entry<T> cursor, prev;
        // ready flag is used to make sure the element is ready to be remvoved
        boolean ready;

        SkipListIterator(){
            this.cursor = head;
            this.prev = null;
            this.ready = false;
        }

        @Override
        public boolean hasNext() {
            return cursor.next[0] != null;
        }

        @Override
        public T next() {
            this.prev = this.cursor;
            this.cursor = this.cursor.next[0];
            this.ready = true;
            return this.cursor.getElement();
        }

        // Removes the element (cursor) most recent next()
        // Remove can be used only after next has been called
		@Override
		public void remove() {
			if(!this.ready){
                throw new NoSuchElementException();
            }
            this.prev.next[0] = this.cursor.next[0];
            if(this.cursor == tail){
                tail = this.prev;
            }
            this.cursor = this.prev;
            ready = false;
            size--;
		}
    }

    // Return last element of list
    public T last() {
        if(this.isEmpty()){
            return null;
        }else{
            Entry<T> cursor = this.head.next[0];
            while(cursor.next[0] != this.tail){
                cursor = cursor.next[0];
            }
            return (T) cursor.getElement();
        }
    }

    // Optional operation: Reorganize the elements of the list into a perfect skip list
    // Not a standard operation in skip lists. Eligible for EC.
    public void rebuild() { 
        // T[] elementArray;
        // for(int i=0; i<=this.size()-1; i++){
        //     elementArray[i] = this.re
        // }
    }

    // Remove x from list.  Removed element is returned. Return null if x not in list
    public T remove(T x) {
        if(!this.contains(x)){
            return null;
        }
        Entry ent = last[0].next[0];
        for(int i = 0; i <= ent.next.length-1; i++){
            last[i].next[i] = ent.next[i];
        }
        this.size-=1;
        this.spanFiller();
        return (T) ent.getElement();
    }

    // Return the number of elements in the list
    public int size() {
        return this.size;
    }

    // Prints the skiplist starting from top most level of the skiplist
    private void printList(){
        System.out.println("SkipList");
        for(int i=this.maxLevel-1; i>=0; i--){
            Entry<T> cursor = this.head;
            System.out.println("Level: "+i);
            while(cursor != this.tail){
                System.out.print(cursor.getElement() + " ");
                cursor = cursor.next[i];
            }
            System.out.println();
        }
    }

    // Prints the span array of the nodes of the skiplist
    private void printSpanList(){
        Entry<T> cursor = this.head;
        while(cursor != this.tail){
            System.out.println("Size of Span array of "+cursor.getElement()+" ->"+ cursor.span.length);
            for(int i = cursor.span.length-1; i >=0 ; i-- ){
                System.out.print(cursor.span[i]+" ");
            }
            System.out.println();
            cursor = cursor.next[0];
        }
    }

    // Used this function to just check if the last array is filled with proper nodes after the find(T x) method 
    private void printLastArray(){
        System.out.println("Last Array");
        for(int i =0; i<= this.maxLevel-1; i++){
            System.out.println(this.last[i].getElement());
        }
        return;
    }

    public static void main(String[] args) {
        SkipList<Integer> sl = new SkipList<>();
        Random random = new Random();
        for(int i=10; i>=1; i--){
            sl.add(random.nextInt(60));
        }
        System.out.println("Max Level -> "+sl.maxLevel);
        System.out.println("Size -> "+sl.size());
        sl.printList();
        // sl.printSpanList();
        System.out.println(sl.get(9));
    }
}