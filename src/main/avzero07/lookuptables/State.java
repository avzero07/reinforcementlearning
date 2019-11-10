package avzero07.lookuptables;

/**
 * State Class to track state values.
 * @date 09-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.1
 */

/*
Changelog
---------------
Version 0.0.1
---------------
- Initial implementation
 */

public class State {

    double d2enem, myEner, enEner;
    int d2enemInt, myEnerInt, enEnerInt;

    public State(double d2e, double myEn, double enEn){
        this.d2enem = d2e;
        this.myEner = myEn;
        this.enEner = enEn;
    }

    public State(int d2ei, int myEni, int enEni){
        this.d2enemInt = d2ei;
        this.myEnerInt = myEni;
        this.enEnerInt = enEni;
    }

}
