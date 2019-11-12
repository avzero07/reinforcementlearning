package avzero07.lookuptables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.Scanner;

/**
 * LUT Class that implements the LUTInterface.
 * @date 11-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.8
 */

/*
Changelog
---------------
Version 0.0.8
---------------
- Implemented randInt() and randDoub()
    --  Passes tests
- Q Update Method
    --  For Q Learning and SARSA with Terminal Rewards
---------------
Version 0.0.7
---------------
- Added loaded field to LUT
    --  loaded tracks whether weights were loaded in a given round
    --  loaded starts out at 0 and is set to 1 at the first load (at first call of run() in a round)
    --  Simple rules: Don't load in round 0
- Added upper and lower Bound arrays for States
    -- Useful for dynamically quantizing sensed state values
- LUT Constructor Updated
    --  Always randomizes weights. Useful for round 0
    --  Load will ensure weights are restored in second and subsequent rounds
    --  Sets values in the upper and lower bound arrays
---------------
Version 0.0.5
---------------
- Implemented LUT Constructor
- Implemented initLut() and initLutTrav()
    --  Passes Tests
- Implemented saveWeights()
    --  Passes Tests
- Implemented load()
    -- Passes Tests
- Corrected LUT by adding Action
    -- Corrected all methods
    -- Passing all Tests
---------------
Version 0.0.1
---------------
- Initial Implementation
*/

public class LUT implements LUTInterface {

    int d2eLevels, myEnLevels, enEnLevels, numActions, loaded, posXLevels, posYLevels;
    double[] lowerBound, upperBound;
    double[][][][][][] lookUpTable,lookUpTableTrav;

    /**
     * Constructor Method for LUT. Choosing to implement as an Array
     * Later improvement will upgrade this to a Hash Table
     */
    public LUT(int d2elev, int myEnLev, int enEnLev, int numActions, int posXLevels, int posYLevels){

        /*
        * Implementing 3 States
        * */
        this.posXLevels = posXLevels;
        this.posYLevels = posYLevels;
        this.d2eLevels = d2elev;
        this.myEnLevels = myEnLev;
        this.enEnLevels = enEnLev;
        this.loaded = 0;
        this.lowerBound = new double[5];
        this.upperBound = new double[5];

        for(int i = 0; i < 5; i++){
            this.lowerBound[i] = 0;

            int up = 0;

            switch(i){
                case 0: up = 1000;
                break;
                case 1: up = 100;
                break;
                case 2: up = 360;
                break;
                case 3: up = 800;
                break;
                case 4: up = 600;
                break;
            }
            this.upperBound[i] = up;
        }

        /*
        * Implementing 3 Actions
        * 0 -- Scan
        * 1 -- Move
        * 2 -- Shoot
        * */
        this.numActions = numActions;

        this.lookUpTable = new double[this.d2eLevels][this.myEnLevels][this.enEnLevels][this.posXLevels][this.posYLevels][numActions];
        this.lookUpTableTrav = new double[this.d2eLevels][this.myEnLevels][this.enEnLevels][this.posXLevels][this.posYLevels][numActions];
        this.initLutTrav(this,0);
        this.initLut(this,1);
    }

    /**
     * Method to fill the LUT
     * @param table : Specify the LUT Object to act on
     * @param type : Specify Fill. 0 or 1. 0 For 0 and 1 for Random
     */
    @Override
    public void initLut(LUT table, int type) {

        double fill;

        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.myEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++)
                    for(int ex = 0; ex < table.posXLevels; ex++)
                        for(int yi = 0; yi < table.posYLevels; yi++)
                            for(int act=0;act<this.numActions;act++){
                                fill = (type == 0) ? 0 : Math.random();
                                table.lookUpTable[d][men][een][ex][yi][act] = fill;
                            }
    }

    /**
     * Method to fill the Traversal Table
     * @param table : Specify the LUT Object to act on
     * @param type : Specify Fill. 0 or 1. 0 for 0 and 1 for Random
     */
    public void initLutTrav(LUT table, int type) {

        double fill;

        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.myEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++)
                    for(int ex = 0; ex < table.posXLevels; ex++)
                        for(int yi = 0; yi < table.posYLevels; yi++)
                            for(int act=0;act<this.numActions;act++){
                                fill = (type == 0) ? 0 : Math.random();
                                table.lookUpTableTrav[d][men][een][ex][yi][act] = fill;
                            }
    }

    @Override
    public int indexFor(int[] x) {
        return 0;
    }

    @Override
    public void compOutput(double[] x) {

    }

    @Override
    public double train(double[] x, double argValue) {
        return 0;
    }

    /**
     * Method to perform the learning update
     */
    public void qUpdate(State current, State previous, double alpha, double gamma, double reward, int s2Action, int s1Action, boolean ON_POLICY, boolean terminal){

        /*
        * SARSA (On Policy TD)
        *
        * Q(S,A) <-- Q(S,A) + alpha*(R + gamma*(Q(S',A'))-Q(S,A))
        *
        * */

        double q = this.lookUpTable[previous.d2enemInt][previous.myEnerInt][previous.enEnerInt][previous.exInt][previous.yiInt][s1Action];
        double qnew = this.lookUpTable[current.d2enemInt][current.myEnerInt][current.enEnerInt][previous.exInt][previous.yiInt][s2Action];
        double qup;

        if(ON_POLICY==true){
            if(terminal==true){
                return;
            }
            if(terminal==false){
                qup = q + (alpha*(reward+(gamma*qnew)-q));
                this.lookUpTable[previous.d2enemInt][previous.myEnerInt][previous.enEnerInt][previous.exInt][previous.yiInt][s1Action] = qup;
                return;
            }
        }

        /*
        * Q-Learning (Off Policy TD)
        *
        * Q(S,A) <-- Q(S,A) + alpha*(R + gamma*max-a(Q(S',A))-Q(S,A))
        * */
        if(ON_POLICY==false){
            if(terminal==true){
                qnew = 0;
                qup = q + (alpha*(reward+(gamma*qnew)-q));
                this.lookUpTable[previous.d2enemInt][previous.myEnerInt][previous.enEnerInt][previous.exInt][previous.yiInt][s1Action] = qup;
                return;
            }
            if(terminal==false){
                qup = q + (alpha*(reward+(gamma*qnew)-q));
                this.lookUpTable[previous.d2enemInt][previous.myEnerInt][previous.enEnerInt][previous.exInt][previous.yiInt][s1Action] = qup;
                return;
            }
        }
    }

    /**
     * Method to return the Max Action of a Given State
     */
    public int maxAction(State s){

        int maxint=-7;
        double maxAction = -100;
        try{
            double[] possibleActions = this.lookUpTable[s.d2enemInt][s.myEnerInt][s.enEnerInt][s.exInt][s.yiInt];
            for(int i=0;i<possibleActions.length;i++){
                if(possibleActions[i]>maxAction){
                    maxAction = possibleActions[i];
                    maxint = i;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return maxint;
    }

    /**
     * Method to save LUT to file
     * @param pathToDirectory specifies the directory to where files will be written
     * @param identifier to name the files appropriately
     */
    @Override
    public void saveWeights(String pathToDirectory, String identifier) throws IOException {

        String op = "";

        for (int d = 0; d < this.d2eLevels; d++)
            for (int men = 0; men < this.myEnLevels; men++)
                for (int een = 0; een < this.enEnLevels; een++)
                    for(int ex = 0; ex < this.posXLevels; ex++)
                        for(int yi = 0; yi < this.posYLevels; yi++){
                            for(int act=0;act<this.numActions;act++){
                                op = op + this.lookUpTable[d][men][een][ex][yi][act] + " ";
                            }
                            op = op + "\n";
                        }

        File f = new File(pathToDirectory);
        f.mkdirs();

        String filepath = pathToDirectory+"/"+identifier+".txt";
        writeToFile(filepath,op);
    }

    @Override
    public void load(String argFileName) throws IOException {
        File f = new File(argFileName);
        Scanner scan = new Scanner(f);

        for (int d = 0; d < this.d2eLevels; d++)
            for (int men = 0; men < this.myEnLevels; men++)
                for (int een = 0; een < this.enEnLevels; een++)
                    for(int ex = 0; ex < this.posXLevels; ex++)
                        for(int yi = 0; yi < this.posYLevels; yi++)
                            for(int act=0;act<this.numActions;act++){
                                try{
                                    this.lookUpTable[d][men][een][ex][yi][act] = scan.nextDouble();
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }

        scan.close();
        this.loaded=1;
    }

    //Additional Methods
    /**
     * Method writes to file
     */
    public static void writeToFile(String path, String text) throws IOException {
        Charset charSet = Charset.forName("US-ASCII");
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(text,0,text.length());
        writer.close();
    }

    /**
     * Method to return a random number in specified range (inclusive)
     * @param lower lower bound
     * @param upper upper bound
     * @return return a random int in the range
     */
    public static int randInt(int lower, int upper){
        Random r = new Random();
        return (r.nextInt((upper-lower)+1)+lower);
    }

    /**
     * Method to return a random number in specified range (inclusive)
     * @param lower lower bound
     * @param upper upper bound
     * @return return a random double in the range
     */
    public static double randDoub(double lower, double upper){
        double r = new Random().nextDouble();
        double res = lower + (r*(upper-lower));
        return res;
    }
}
