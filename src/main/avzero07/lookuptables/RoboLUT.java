package avzero07.lookuptables;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * RobotLUT Implementation Class.
 * @date 08-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.1
 */

/*
Changelog
---------------
Version 0.0.1
---------------
- Initial Implementation
- Environment Works
*/

public class RoboLUT extends AdvancedRobot {

    /*
    * Run Method for default actions
    * in absence of any other events
    * */
    @Override
    public void run(){
        while(true){
            //getState();
            //takeAction();
            //getState();
            //update();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e){
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
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
     * Method to avoid enemy bullet
     */
    public void dodge(){

    }


    /**
     * Method to fire
     */
    public void shoot(){

    }
}
