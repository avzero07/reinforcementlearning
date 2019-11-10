package avzero07.lookuptables;

import robocode.*;
import robocode.util.Utils;

import java.io.*;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RobotLUT Implementation Class.
 * @date 09-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.7
 */

/*
Changelog
---------------
Version 0.0.7
---------------
- File Handling
    --  Temp File
    --  Saves weights at the end of the round
    --  Loads weights (from temp) from second round onwards
    --  Save and Load Working Across Rounds
    --  Cleans up Temp at the End of Battle
- Implemented Quantization Method
    --  Passes test
    --  Corrected severe BUG (n levels with n+1 labels)
- First implementation of Off Policy Q Learning
- Decent Firing mechanic
---------------
Version 0.0.5
---------------
- Implemented basic Action methods
    --  Move (moveForward() and moveBackward()) and Turn (turnLeft() and turnRight())
---------------
Version 0.0.1
---------------
- Initial Implementation
- Environment Works
*/

public class RoboLUT extends AdvancedRobot {

    /*
    * Start defining global variables
    * */
    static double epsilon = 0;    //(1-e) Probability of picking greedily
    static double gamma = 0.9;      //Discount Factor
    static double alpha = 0.2;      //Step Size
    static double reward = 0;       //Tracks the instantaneous reward. Should be reset after update
    static double aggReward = 0;    //Tracks the aggregate reward. Resets every episode [round]

    static boolean win = false;
    static int winCount = 0;
    static int aggWinCount = 0;

    /*
    * States
    * 0. Distance to Enemy
    * 1. My Energy
    * 2. Enemy Energy
    *
    * Actions
    * 0. Seek
    * 1. Move Ahead
    * 2. Turn Left
    * 3. Move Back
    * 4. Turn Right
    * 5. Shoot
    * */
    static int d2eLevels = 5;
    static int myEnLevels = 16;
    static int enEnLevels = 16;
    static int numActions = 5;

    //int round = getRoundNum();
//    Date date = Calendar.getInstance().getTime();
//    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
//    String strDate = dateFormat.format(date);

    //Directory Path for Current Battle
    String pathToBattle = "C:/Users/Akshay/Desktop/Robocode Runtime/";
    String temp = "C:/Users/Akshay/Desktop/Robocode Runtime/";
    String loadTemp = "C:/Users/Akshay/Desktop/Robocode Runtime/tmp.txt";
    String resPath = "C:/Users/Akshay/Desktop/Robocode Runtime/ScoreResult.txt";

    //Instantiate State Objects
    State s1;           //Current State : State before action
    State s2;           //Next State    : State after action


    //Flags
    boolean firstSeek = true;
    boolean trueFirstSeek = true;   //Will be true only once

    /*
    * Instantiate LUT
    * */
    static LUT lut1 = new LUT(d2eLevels, myEnLevels, enEnLevels, numActions);

    /*
    * Run Method for default actions
    * in absence of any other events
    * */
    @Override
    public void run(){
        if(lut1.loaded==0 && getRoundNum()!=0) {
            try {
                lut1.load(loadTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while(true){
            //Get State
            if(trueFirstSeek){
                turnRadarLeft(360);
                System.out.println("First Seek");
                trueFirstSeek = false;
            }
            firstSeek = false;

            //Take Action
            //Greediness comes into play here. Take the greedy action with (1-e) probability
            double[] possibleActions = lut1.lookUpTable[s1.d2enemInt][s1.myEnerInt][s1.enEnerInt];

            double chance = Math.random();

            double maxAction = 0;     //Used later for update
            int maxint = 0;  //Used later for update
            if(chance<=(1-epsilon)){
                for(int i=0;i<possibleActions.length;i++){
                    if(possibleActions[i]>maxAction){
                        maxAction = possibleActions[i];
                        maxint = i;
                    }
                }
            }
            else if(chance>(1-epsilon)){
                maxint = ThreadLocalRandom.current().nextInt(0, 5); //Need to come back to this later
                maxAction = possibleActions[maxint];
            }

            switch(maxint){
                case 0: moveForward();
                break;
                case 1: turnLeft();
                break;
                case 2: moveBackward();
                break;
                case 3: turnRight();
                break;
                case 4: shoot();
                break;
            }

            //Get State Again
            turnRadarLeft(360);
            firstSeek = true;

            //Update Q
            /*
            * On Policy Formula
            *
            * Q(S,A) <-- Q(S,A) + alpha*(R + gamma*(Q(S',A'))-Q(S,A))
            *
            * Q Learning Formula (Off Policy)
            *
            * Q(S,A) <-- Q(S,A) + alpha*(R + gamma*max-a(Q(S',A))-Q(S,A))
            * */
            double q = lut1.lookUpTable[s1.d2enemInt][s1.myEnerInt][s1.enEnerInt][maxint];
            q = q + alpha*(reward+(gamma*lut1.lookUpTable[s2.d2enemInt][s2.myEnerInt][s2.enEnerInt][maxint])-q);
            lut1.lookUpTable[s1.d2enemInt][s1.myEnerInt][s1.enEnerInt][maxint] = q;

            reward = 0;

            //Make S1 <-- S2
            s1.d2enemInt = s2.d2enemInt;
            s1.myEnerInt = s2.myEnerInt;
            s1.enEnerInt = s2.enEnerInt;
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e){
        //double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        //setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        //setTurnGunLeft(getHeading() - getGunHeading() + normalizeBearing(e.getBearing()));

        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        setTurnGunRightRadians(Utils.normalRelativeAngle(absoluteBearing -
                getGunHeadingRadians() + (e.getVelocity() * Math.sin(e.getHeadingRadians() -
                absoluteBearing) / 13.0)));
        setFire(1.0);

        int d2e = quantize(e.getDistance(),lut1.d2eLevels,lut1.lowerBound[0],lut1.upperBound[0]);
        int myEn = quantize(getEnergy(),lut1.myEnLevels,lut1.lowerBound[1],lut1.upperBound[1]);
        int enEn = quantize(e.getEnergy(),lut1.enEnLevels,lut1.lowerBound[2],lut1.upperBound[2]);



        if(firstSeek){
            s1 = new State(d2e,myEn,enEn);
        }
        if(firstSeek!=true){
            s2 = new State(d2e,myEn,enEn);
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent e) {
        String pathToRoundWeights = pathToBattle+"/Rounds/";
        File f = new File(pathToRoundWeights);
        f.mkdirs();

        try {
            lut1.saveWeights(pathToRoundWeights,"Round-"+getRoundNum());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            lut1.saveWeights(temp,"tmp");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        File f = new File(temp+"/tmp.txt");
        //f.delete();
    }

    @Override
    public void onBulletHit(BulletHitEvent event) { reward = reward + 0.5; }

    @Override
    public void onHitByBullet(HitByBulletEvent event) { reward = reward -0.5; }

    @Override
    public void onDeath(DeathEvent event) { reward = reward -1; }

    @Override
    public void onWin(WinEvent event) {
        reward = reward + 1;
        win = true;
        winCount++;
    }

    @Override
    public void onHitWall(HitWallEvent event) { reward = reward -0.25; }

    /*
    * Start Methods for Actions
    * */

    /**
     * Method to scan/seek enemy robot and close in on it
     */
    public void seek(){
        turnRadarLeft(360);
    }

    /**
     * Simple Movement: Turn Left
     */
    public void turnLeft(){
        turnLeft(90);
    }

    /**
     * Simple Movement: Turn Right
     */
    public void turnRight(){ turnRight(90); }

    /**
     * Simple Movement: Move Forward
     */
    public void moveForward(){
        ahead(50);
    }

    /**
     * Simple Movement: Move Back
     */
    public void moveBackward(){
        back(50);
    }

    /**
     * Method to move around
     */
    public void moveComplex(){
        double x = getX();
        double y = getY();

        double height = getBattleFieldHeight();
        double width = getBattleFieldWidth();

        double heading = getHeading();
        //Q 1
        if((x>=width/2)&&(y>=height/2)){

        }

        //Q 2
        if((x<width/2)&&(y>=height/2)){

        }

        //Q 3
        if((x<width/2)&&(y<height/2)){

        }

        //Q 4
        if((x>=height/2)&&(y<height/2)){

        }
    }

    /**
     * Method to return the normalized bearing
     */
    // normalizes a bearing to between +180 and -180
    public static double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    /**
     * Method to fire
     */
    public void shoot(){
        // if the gun is cool and we're pointed at the target, shoot!
        execute();
    }

    /*
     * Helper Methods
     */
    public static int quantize(double val, int levels, double lowerBound, double upperBound){
        int res = 0;

        if(val>upperBound)
            val = upperBound;

        double rat = val/(upperBound-lowerBound);
        double temp = rat*(levels-1);
        res = (int) Math.round(temp);

        return res;
    }
}
