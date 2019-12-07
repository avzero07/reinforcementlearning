package avzero07.Final;

import avzero07.lookuptables.State;
import avzero07.reinforcementlearning.NeuralNet;
import robocode.*;
import robocode.util.Utils;

import java.io.*;
import java.util.Random;

/**
 * RobotNN Implementation Class.
 * @date 06-December-2019
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

public class RoboNN extends AdvancedRobot {
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
    * Initialize NN Here
    * */
    //Initialize Neural Net
    static int numInputNeurons = 13; //5 State Variables and 8 for Action with 1 hot Encoding
    static int numHiddenNeurons = 7;
    static int numOutputNeurons = 1;

    static double learningRate = 0.01;
    static double momentum = 0;

    static double argA = 0;
    static double argB = 0;

    static int weightFlag = 0;

    //Bipolar Sigmoid
    int t = 1;
    String activation = "Bipolar Sigmoid";

    double E = 0;

    static NeuralNet nn1 = new NeuralNet(numInputNeurons, numHiddenNeurons, numOutputNeurons, learningRate, momentum, argA, argB);
    /*
     * Run Method for default actions
     * in absence of any other events
     * */
    @Override
    public void run(){

        //Initialize the Weights During First Battle
        if(weightFlag==0){
            nn1.initWeights(-0.5,0.5);
            weightFlag++;
        }

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
                    double chance = randDoub(0.0,1.0);
                    int maxActionInt = State.maxAction(s1,nn1,t);  //Used later for update
                    if(chance<=(1-epsilon)){
                        chosenAction = maxActionInt;
                    }
                    else if(chance>(1-epsilon)){
                        chosenAction = randInt(0,numActions-1); //Need to come back to this later
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
                double chance = randDoub(0.0,1.0);
                int maxActionInt = State.maxAction(s2,nn1,t);  //Used later for update
                if(chance<=(1-epsilon)){
                    chosenAction = maxActionInt;
                }
                else if(chance>(1-epsilon)){
                    chosenAction = randInt(0,numActions-1); //Need to come back to this later
                }

                //Update Table
                if(LEARNING){
                    try{
                        //lut1.qUpdate(s2,s1,alpha,gamma,reward,chosenAction,prevAction,true,false);
                        State.qUpdate(nn1,t,learningRate,momentum,s2,s1,alpha,gamma,reward,chosenAction,prevAction,true,false);
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
                double chance = randDoub(0.0,1.0);
                int maxActionInt = State.maxAction(s1,nn1,t);  //Used later for update
                if(chance<=(1-epsilon)){
                    chosenAction = maxActionInt;
                }
                else if(chance>(1-epsilon)){
                    chosenAction = randInt(0,numActions-1); //Need to come back to this later
                }

                takeAction(chosenAction);

                //Get State Again
                turnRadarLeft(360);

                if(s2==null)
                    turnRadarLeft(360);

                int curMaxAction = State.maxAction(s2,nn1,t);
                if(LEARNING){
                    try{
                        //lut1.qUpdate(s2,s1,alpha,gamma,reward,curMaxAction,chosenAction,false,false);
                        State.qUpdate(nn1,t,learningRate,momentum,s2,s1,alpha,gamma,reward,curMaxAction,chosenAction,false,false);
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

        double d2e = e.getDistance();
        double myEn = getEnergy();
        //Change enEn to Enemy Bearing
        double enEn = e.getBearing();
        double exLev = getX();
        double yiLev = getY();

        if(firstSeek){
            s1 = new State(d2e,myEn,enEn,exLev,yiLev);
        }
        if(firstSeek!=true){
            s2 = new State(d2e,myEn,enEn,exLev,yiLev);
        }
    }


    //Add Event Handlers Above

    //Add Other Methods Below

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


    //RL Related Methods


    //Helper Methods
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
}
