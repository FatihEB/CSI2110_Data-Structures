// Fatih Bahceci - fbahc080@uOttawa.ca - 300348275
import java.util.*;

class Stack<T> {
    private LinkedList<T> list = new LinkedList<>();
    // Stacks an item ont op
    public void push(T element) {
        list.addFirst(element);
    }
    // Pops an item from the satck (removes the top item)
    public T pop() {
        return list.removeFirst();
    }
    // Checks if stack is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }
}