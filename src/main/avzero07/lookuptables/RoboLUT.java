package avzero07.lookuptables;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    * Start defining global variables and init LUT
    *
    * States
    * 1. Distance to Enemy
    * 2. My Energy
    * 3. Enemy Energy
    *
    * Actions
    * 1. Seek
    * 2. Move Ahead
    * 3. Turn Left
    * 4. Move Back
    * 5. Turn Right
    * 6. Shoot
    * */
    static int d2eLevels = 5;
    static int myEnLevels = 20;
    static int enEnLevels = 20;
    static int numActions = 6;

    //int round = getRoundNum();
    Date date = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    String strDate = dateFormat.format(date);

    //Directory Path for Current Battle
    String pathToBattle = "C:/Users/Akshay/Desktop/Robocode Runtime/";
    String temp = "C:/Users/Akshay/Desktop/Robocode Runtime/";
    String loadTemp = "C:/Users/Akshay/Desktop/Robocode Runtime/tmp.txt";

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
            //getState();
            //takeAction();
            //getState();
            //update();
            moveForward();
            turnLeft();
            moveForward();
            turnLeft();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e){
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
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
        f.delete();
    }

    /*
    * Start Methods for Actions
    * */

    /**
     * Method to scan/seek enemy robot and close in on it
     */
    public void seek(){
        turnRadarLeft(Double.POSITIVE_INFINITY);
        scan();
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
     * Method to fire
     */
    public void shoot(){
        fire(1);
    }
}
