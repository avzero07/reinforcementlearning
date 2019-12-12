package avzero07.Final;

import avzero07.lookuptables.State;

/**
 * ReplayMemory Class File.
 * @date 11-December-2019
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

public class Memory {

    public double qValue;
    public State sT;
    public int actionIndexT;
    public double rewardTplusOne;
    public State sTplusOne;

    /**
     * Constructor to initialize the Memory Object
     * @param q Updated Q(s,a) value calculated via a prior RL back-step
     * @param st State to recall Q(s,a) from NN
     * @param actT Action to recall Q(s,a) from NN
     * @param rtplusone Reward for taking Action actT at State st
     * @param stplus Successor State to st
     */
    public Memory(double q, State st, int actT, double rtplusone, State stplus){
        this.qValue = q;
        this.sT = st;
        this.actionIndexT = actT;
        this.rewardTplusOne = rtplusone;
        this.sTplusOne = stplus;
    }
}
