package avzero07.lookuptables;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * RobotLUT Implementation Class.
 * @date 08-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.5
 */

/*
Changelog
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
     * Simple Movement: Turn Left
     */
    public void turnLeft(){
        setTurnLeft(90);
    }

    /**
     * Simple Movement: Turn Right
     */
    public void turnRight(){
        setTurnRight(90);
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
