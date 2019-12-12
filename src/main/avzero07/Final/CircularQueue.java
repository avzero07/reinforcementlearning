package avzero07.Final;

import java.util.LinkedList;

/**
 * CircularQueue Class File.
 * @date 05-December-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.1
 */

/*
Changelog
---------------
Version 0.0.1
---------------
- Initial implementation.
*/

public class CircularQueue<E> extends LinkedList<E> {
    private int capacity = 10;

    public CircularQueue(int capacity){
        this.capacity = capacity;
    }

    public boolean add(E e) {
        if(size() >= capacity)
            removeFirst();
        return super.add(e);
    }
}
