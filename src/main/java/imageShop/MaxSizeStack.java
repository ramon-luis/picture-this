package imageShop;

import java.util.*;

/**
 * Custom stack implementation with max size limit
 * removes oldest elements when adding a new limit brings stack size over limit
 */

public class MaxSizeStack<E> {

    // private members
    private LinkedList<E> mElements;
    private int mSizeLimit;

    // constructor
    public MaxSizeStack(int iMaxSize) {
        if (iMaxSize < 0) {
            throw new IllegalArgumentException("size limit for an ImageStack cannot be negative");
        }
        mElements = new LinkedList<>();
        mSizeLimit = iMaxSize;
    }

    // push a new element to the front of the linked list
    public void push(E element) {
        // new elements are added to front of list
        mElements.addFirst(element);

        // oldest elements at end of list are removed if over size limit
        while (mElements.size() > mSizeLimit) {
            mElements.removeLast();
        }
    }

    // pop most recent element from the front of the linked list
    public E pop() {
        // check if linked list is empty
        if (mElements.isEmpty()) {
            throw new NoSuchElementException("cannot pop an image from empty ImageStack");
        }

        // get most recent element from front of the list & remove it
        final E eFirst = mElements.getFirst();
        mElements.removeFirst();
        return eFirst;
    }

    // check if linked list is empty
    public boolean isEmpty() {
        return mElements.size() == 0;
    }

    // get the list of elements
    public LinkedList<E> getAsLinkedList() {
        return mElements;
    }

    // clear the list
    public void clear() {
        mElements.clear();
    }

    @Override
    public String toString() {
        return "MaxSizeStack{" +
                "mElements=" + mElements +
                ", mSizeLimit=" + mSizeLimit +
                '}';
    }
}
