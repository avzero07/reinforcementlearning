package avzero07.lookuptables;

import robocode.*;
import robocode.util.Utils;

import java.io.*;
import java.util.Random;

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
    static double epsilon = 1;    //(1-e) Probability of picking greedily
    static double gamma = 0.9;      //Discount Factor
    static double alpha = 0.1;      //Step Size
    double reward = 0;       //Tracks the instantaneous reward. Should be reset after update
    double aggReward = 0;    //Tracks the aggregate reward. Resets every episode [round]

    static boolean win = false;
    static int winCount = 0;
    static int aggWinCount = 0;

    //Track Win Rate Per 20 battles
    static double[] wins = new double[1000];
    static double[] finalEnergy = new double[1000]; //Tracking Damage Taken in a round
    static double[] aggRew = new double[1000];      //Agg reward in a round

    static int roundCount = 0;

    /*
    * States
    * 0. Distance to Enemy
    * 1. My Energy
    * 2. Enemy Bearing
    * 3. My X Co-ordinate
    * 4. My Y Co-ordinate
    *
    * Actions
    * 1. Move Ahead
    * 2. Turn Left (back and front)
    * 3. Move Back
    * 4. Turn Right (back and front)
    * 5. Shoot
    * 6. Shoot (Power 2)
    * */
    static int d2eLevels = 6;
    static int myEnLevels = 4;
    static int enEnLevels = 4; //Changed to enemy bearing
    static int numActions = 8;
    static int posXlevels = 4;
    static int posYLevels = 4;

    //Robocode Complete Conditions
    private final GunTurnCompleteCondition gunMoveComplete = new GunTurnCompleteCondition(this);

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
    State s3;                           //Used for terminal Q learning death null pointer scenario
    State[] stateArray = new State[2];  //Always tracks current and previous states
    int chosenAction;   //Global variable to track the chosen action
    int prevAction;     //For Sarsa
    int firstMove = 0;  //For Sarsa


    //Flags
    boolean firstSeek = true;
    boolean trueFirstSeek = true;   //Will be true only once
    static boolean ON_POLICY = false;      //Used to Toggle between ON and OFF Policy Learning
    static boolean LEARNING = true;        //Used to Toggle between Learning and no Learning
    static boolean intermediateRewards = true; //Used to toggle intermediate rewards
    static boolean terminalRewards = true;      //Used to toggle terminal rewards

    /*
    * Instantiate LUT
    * */
    static LUT lut1 = new LUT(d2eLevels, myEnLevels, enEnLevels, numActions, posXlevels, posYLevels);

    /*
    * Run Method for default actions
    * in absence of any other events
    * */
    @Override
    public void run(){
//        if(lut1.loaded==0 && getRoundNum()!=0) {
//            try {
//                lut1.load(loadTemp);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        while(true){
            //To update S1 for the first time
            if(trueFirstSeek){
                turnRadarLeft(360);
                if(s1==null)
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

                if(s2==null)
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
                    try{
                        lut1.qUpdate(s2,s1,alpha,gamma,reward,chosenAction,prevAction,true,false);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
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
                    turnRadarLeft(360);

                int curMaxAction = lut1.maxAction(s2);
                if(LEARNING){
                    try{
                        lut1.qUpdate(s2,s1,alpha,gamma,reward,curMaxAction,chosenAction,false,false);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
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
        turnGunRightRadians(Utils.normalRelativeAngle(absoluteBearing -
                getGunHeadingRadians() + (e.getVelocity() * Math.sin(e.getHeadingRadians() -
                absoluteBearing) / 13.0)));

        int d2e = quantize(e.getDistance(),lut1.d2eLevels,lut1.lowerBound[0],lut1.upperBound[0]);
        int myEn = quantize(getEnergy(),lut1.myEnLevels,lut1.lowerBound[1],lut1.upperBound[1]);
        //Change enEn to Enemy Heading
        int enEn = quantize(e.getBearing()+180,lut1.enEnLevels, lut1.lowerBound[2],lut1.upperBound[2]);
        int exLev = quantize(getX(),lut1.posXLevels,lut1.lowerBound[3],lut1.upperBound[3]);
        int yiLev = quantize(getY(),lut1.posYLevels,lut1.lowerBound[4],lut1.upperBound[4]);

        if(firstSeek){
            s1 = new State(d2e,myEn,enEn,exLev,yiLev);
        }
        if(firstSeek!=true){
            s2 = new State(d2e,myEn,enEn,exLev,yiLev);
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent e) {
        roundCount++;
//        String pathToRoundWeights = pathToBattle+"/Rounds/";
//        File f = new File(pathToRoundWeights);
//        f.mkdirs();
//
//        try {
//            lut1.saveWeights(pathToRoundWeights,"Round-"+getRoundNum());
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        out.println("Aggregate Reward = "+aggReward);
        aggReward = 0;

        double energy = getEnergy();
        int roundCount = getRoundNum();
        finalEnergy[(roundCount/100)] = finalEnergy[(roundCount/100)] + energy;

        //Epsilon Decay
        if(roundCount%100==0 && roundCount!=0  && epsilon>0){

            if(epsilon>0 && epsilon<0.1) epsilon = 0;
            else epsilon = epsilon - 0.1;
        }

        //Overfitting Beyond This
//        if(roundCount>1500 && LEARNING==true)
//            LEARNING = false;
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        String res = "Wins\tRemaining Energy\tAggReward\n";
        for(int i=0;i<wins.length;i++){
            res = res + wins[i] + "\t" + finalEnergy[i] + "" +
                    "\t" + aggRew[i] +"\n";
        }

        try {
            LUT.writeToFile(resPath,res);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            lut1.saveWeights(temp,"tmp");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        if(intermediateRewards){
            reward = reward + 1;
        }
        aggRew[roundCount/1]++;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        if(intermediateRewards){
            reward = reward - 1;
        }
        aggRew[roundCount/100]--;
    }

    @Override
    public void onDeath(DeathEvent event) {
        if(terminalRewards){
            reward = reward -1000;
        }
        if(LEARNING){
            try{
                if(s2==null){
                    s3 = s1;
                    lut1.qUpdate(s3,s1,alpha,gamma,reward,0,chosenAction,ON_POLICY,true);
                }
                if(s2!=null){
                    lut1.qUpdate(s2,s1,alpha,gamma,reward,0,chosenAction,ON_POLICY,true);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        reward = 0;
        aggRew[roundCount/100] = aggRew[roundCount/100] - 1000;
    }

    @Override
    public void onWin(WinEvent event) {
        if(terminalRewards){
            reward = reward + 1000;
        }
        if(LEARNING){
            try{
                lut1.qUpdate(s2,s1,alpha,gamma,reward,0,chosenAction,ON_POLICY,true);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        reward = 0;
        aggRew[roundCount/100] = aggRew[roundCount/100] + 1000;
        win = true;
        winCount++;

        int roundCount = getRoundNum();
        wins[(roundCount/100)]++;
        //if((roundCount+1)%20==0){
        //    wins[(roundCount/20)] = wins[(roundCount/20)]/20;
        //}
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        if(intermediateRewards){
            reward = reward - 1;
        }
        aggRew[roundCount/100]--;
    }

    /*
    * Start Methods for Actions
    * */
    public void takeAction(int action){
        switch(action){
            case 0: turnRightForward();
            break;
            case 1: turnLeftForward();
            break;
            case 2: turnLeftBack();
            break;
            case 3: turnRightBack();
            break;
            case 4: shoot();
            break;
            case 5: shootPow();
            break;
            case 6: moveBackward();
            break;
            case 7: moveForward();
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
    public void turnLeftForward(){
        setTurnLeft(90);
        setAhead(77);
        execute();
    }
    public void turnLeftBack(){

        setTurnLeft(90);
        setBack(77);
        execute();
    }

    /**
     * Simple Movement: Turn Right
     */
    public void turnRightForward(){
        setTurnRight(90);
        setAhead(77);
        execute();
    }

    public void turnRightBack(){
        setTurnRight(90);
        setBack(77);
        execute();
    }

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
        //Choice of whether to fire
        //waitFor(gunMoveComplete);
        fire(1);
    }

    public void shootPow(){
        fire(2);
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
