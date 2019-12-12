package avzero07.Final;

/**
 * ReplayMemory Class File. Instantiates Replay Memory
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

public class ReplayMemory extends CircularQueue<Memory>{
    public ReplayMemory(int capacity)
    {
        super(capacity);
    }
}
