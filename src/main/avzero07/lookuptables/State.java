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

    double d2enem, myEner, enEner, ex, yi;
    int d2enemInt, myEnerInt, enEnerInt, exInt, yiInt;

    public State(double d2e, double myEn, double enEn, double x, double y){
        this.d2enem = d2e;
        this.myEner = myEn;
        this.enEner = enEn;
        this.ex = x;
        this.yi = y;
    }

    public State(int d2ei, int myEni, int enEni, int x, int y){
        this.d2enemInt = d2ei;
        this.myEnerInt = myEni;
        this.enEnerInt = enEni;
        this.exInt = x;
        this.yiInt = y;
    }

}
