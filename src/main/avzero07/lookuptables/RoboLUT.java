package avzero07.lookuptables;

import robocode.*;
import robocode.util.Utils;

import java.io.*;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RobotLUT Implementation Class.
 * @date 09-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.8
 */

/*
Changelog
---------------
Version 0.0.8
---------------
- Tweaks to learning operation
    --  To accommodate learn function
        --- chosenAction Global variable
        --- maxAction function (LUT method) to provide index of best action in a state
- Re-implemented Learning
    --  Q-Learning Incorporated
    --  Fixed Terminal Rewards
    --  SARSA Incorporated
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
    static double epsilon = 0.2;    //(1-e) Probability of picking greedily
    static double gamma = 0.9;      //Discount Factor
    static double alpha = 0.2;      //Step Size
    double reward = 0;       //Tracks the instantaneous reward. Should be reset after update
    double aggReward = 0;    //Tracks the aggregate reward. Resets every episode [round]

    static boolean win = false;
    static int winCount = 0;
    static int aggWinCount = 0;

    //Track Win Rate Per 20 battles
    static double[] wins = new double[1000];

    static int roundCount = 0;

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
    State s1;                           //Current State : State before action
    State s2;                           //Next State    : State after action
    State[] stateArray = new State[2];  //Always tracks current and previous states
    int chosenAction;   //Global variable to track the chosen action
    int prevAction;     //For Sarsa
    int firstMove = 0;  //For Sarsa


    //Flags
    boolean firstSeek = true;
    boolean trueFirstSeek = true;   //Will be true only once
    static boolean ON_POLICY = false;      //Used to Toggle between ON and OFF Policy Learning
    static boolean LEARNING = true;        //Used to Toggle between Learning and no Learning

    /*
    * Instantiate LUT
    * */
    LUT lut1 = new LUT(d2eLevels, myEnLevels, enEnLevels, numActions);

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
            //To update S1 for the first time
            if(trueFirstSeek){
                turnRadarLeft(360);
                trueFirstSeek = false;
            }
            firstSeek = false;

            //SARSA (On Policy TD)
            if(ON_POLICY==true){

                //To update s1 for the first time in SARSA. Populates prevAction
                if(firstMove==0){
                    double chance = LUT.randDoub(0.0,1.0);
                    int maxActionInt = lut1.maxAction(s1);  //Used later for update
                    if(chance<=(1-epsilon)){
                        chosenAction = maxActionInt;
                    }
                    else if(chance>(1-epsilon)){
                        chosenAction = LUT.randInt(0,4); //Need to come back to this later
                    }
                    firstMove++;
                    prevAction = chosenAction;
                }

                //Takes action that was previously decided
                takeAction(prevAction);

                //Get State Again
                turnRadarLeft(360);

                //Choose Action for new state
                double chance = LUT.randDoub(0.0,1.0);
                int maxActionInt = lut1.maxAction(s2);  //Used later for update
                if(chance<=(1-epsilon)){
                    chosenAction = maxActionInt;
                }
                else if(chance>(1-epsilon)){
                    chosenAction = LUT.randInt(0,4); //Need to come back to this later
                }

                //Update Table
                if(LEARNING){
                    lut1.qUpdate(s2,s1,alpha,gamma,reward,chosenAction,prevAction,true,false);
                }

                prevAction = chosenAction;
                reward = 0;
                s1 = s2;
            }

            //Q-Learning (Off Policy TD)
            if(ON_POLICY==false){
                double chance = LUT.randDoub(0.0,1.0);
                int maxActionInt = lut1.maxAction(s1);  //Used later for update
                if(chance<=(1-epsilon)){
                    chosenAction = maxActionInt;
                }
                else if(chance>(1-epsilon)){
                    chosenAction = LUT.randInt(0,4); //Need to come back to this later
                }

                takeAction(chosenAction);

                //Get State Again
                turnRadarLeft(360);

                if(s2==null)
                    turnRadarLeft(360);;

                int curMaxAction = lut1.maxAction(s2);
                if(LEARNING){
                    lut1.qUpdate(s2,s1,alpha,gamma,reward,curMaxAction,chosenAction,false,false);
                }

                reward = 0;

                s1 = s2;
            }
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
        roundCount++;
        /*String pathToRoundWeights = pathToBattle+"/Rounds/";
        File f = new File(pathToRoundWeights);
        f.mkdirs();

        try {
            lut1.saveWeights(pathToRoundWeights,"Round-"+getRoundNum());
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/

        try {
            lut1.saveWeights(temp,"tmp");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //out.println("Aggregate Reward = "+aggReward);
        aggReward = 0;
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        File f = new File(temp+"/tmp.txt");
        //f.delete();
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        reward = reward + 0.5;
        aggReward = aggReward + 0.5;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        reward = reward - 0.5;
        aggReward = aggReward - 0.5;
    }

    @Override
    public void onDeath(DeathEvent event) {
        reward = reward -1000;
        if(LEARNING){
            lut1.qUpdate(s2,s1,alpha,gamma,reward,0,chosenAction,ON_POLICY,true);
        }
        reward = 0;
        aggReward = aggReward - 1000;
    }

    @Override
    public void onWin(WinEvent event) {
        reward = reward + 1000;
        if(LEARNING){
            lut1.qUpdate(s2,s1,alpha,gamma,reward,0,chosenAction,ON_POLICY,true);
        }
        reward = 0;
        aggReward = aggReward + 1000;
        win = true;
        winCount++;

        int roundCount = getRoundNum();
        wins[(roundCount/20)]++;
        if((roundCount+1)%20==0){
            wins[(roundCount/20)] = wins[(roundCount/20)]/20;
        }

        String res = "";
        for(int i=0;i<wins.length;i++){
            res = res + " " + wins[i] + "\n";
        }

        try {
            LUT.writeToFile(resPath,res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        reward = reward - 0.25;
        aggReward = aggReward - 0.25;
    }

    /*
    * Start Methods for Actions
    * */
    public void takeAction(int action){
        switch(action){
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
    }

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

    public static int randInt(int lower, int upper){
        Random r = new Random();
        return (r.nextInt((upper-lower)+1)+lower);
    }

}
