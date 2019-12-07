package avzero07.lookuptables;

import avzero07.reinforcementlearning.NeuralNet;

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
- Added NN related RL Methods
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

    /**
     * Method to return the Max Action of a Given State
     */
    public static int maxAction(State s, NeuralNet nn, int actVar){

        int maxint=-7;
        double maxAction=-100;
        try{

            //Collect and Convert States for Input
            double di = absScale(s.d2enem,0,1000,-0.8,0.8);
            double meni = absScale(s.myEner,0,100,-0.8,0.8);
            double eeni = absScale(s.enEner,0,360,-0.8,0.8);
            double exi = absScale(s.ex,0,800,-0.8,0.8);
            double yii = absScale(s.yi,0,600,-0.8,0.8);

            //Retrieve Possible Actions by ForwardPropagation Through NN
            double[] possibleActions = new double[8]; //To store all 8 Q Values

            double[] inpVec = new double[13];

            //Loop Through All Possible Inputs and Fill possibleActions
            for(int i = 0; i < 8; i++){
                switch(i){
                    case 0: inpVec = new double[] {di,meni,eeni,exi,yii,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                        break;
                    case 1: inpVec = new double[] {di,meni,eeni,exi,yii,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                        break;
                    case 2: inpVec = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                        break;
                    case 3: inpVec = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8};
                        break;
                    case 4: inpVec = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8};
                        break;
                    case 5: inpVec = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8};
                        break;
                    case 6: inpVec = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8};
                        break;
                    case 7: inpVec = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8};
                        break;
                }
                nn.propagateForward(inpVec,actVar);
                possibleActions[i] = nn.finalOutput[0];
            }

            for(int i=0;i<possibleActions.length;i++){
                if(i==0){
                    maxAction = possibleActions[i];
                    maxint = i;
                }
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
        //Returns Index of Best Action
        return maxint;
    }

    //Q Update Function
    /**
     * Method to perform the learning update
     */
    public static void qUpdate(NeuralNet nn, int actVar, double learningRate, double momentum, State current, State previous, double alpha, double gamma, double reward, int s2Action, int s1Action, boolean ON_POLICY, boolean terminal){

        /*
         * SARSA (On Policy TD)
         *
         * Q(S,A) <-- Q(S,A) + alpha*(R + gamma*(Q(S',A'))-Q(S,A))
         *
         * */

        double[] inpVecQ = new double[13];

        //Fill inpVecQ Using State previous and s1Action

        //Scale Inputs related to State previous
        double pdi = absScale(previous.d2enem,0,1000,-0.8,0.8);
        double pmeni = absScale(previous.myEner,0,100,-0.8,0.8);
        double peeni = absScale(previous.enEner,0,360,-0.8,0.8);
        double pexi = absScale(previous.ex,0,800,-0.8,0.8);
        double pyii = absScale(previous.yi,0,600,-0.8,0.8);
        switch(s1Action){
            case 0: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 1: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 2: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 3: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 4: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8};
                break;
            case 5: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8};
                break;
            case 6: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8};
                break;
            case 7: inpVecQ = new double[] {pdi,pmeni,peeni,pexi,pyii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8};
                break;
        }

        double[] inpVecQNew = new double[13];

        //Fill inpVecQNew using State current and Action s2Action

        //Scale Inputs related to State previous
        double cdi = absScale(current.d2enem,0,1000,-0.8,0.8);
        double cmeni = absScale(current.myEner,0,100,-0.8,0.8);
        double ceeni = absScale(current.enEner,0,360,-0.8,0.8);
        double cexi = absScale(current.ex,0,800,-0.8,0.8);
        double cyii = absScale(current.yi,0,600,-0.8,0.8);
        switch(s2Action){
            case 0: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 1: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 2: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 3: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8};
                break;
            case 4: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8};
                break;
            case 5: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8};
                break;
            case 6: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8};
                break;
            case 7: inpVecQNew = new double[] {cdi,cmeni,ceeni,cexi,cyii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8};
                break;
        }

        //Forward Propagate To Assign q and qnew their Values (Remember to Scale Q back)
        nn.propagateForward(inpVecQ,actVar);
        double q = absScale(nn.finalOutput[0],-1,1,-1000,1000);

        nn.propagateForward(inpVecQNew,actVar);
        double qnew = absScale(nn.finalOutput[0],-1,1,-1000,1000);
        double qup;

        if(ON_POLICY==true){
            if(terminal==true){
                return;
            }
            if(terminal==false){
                qup = q + (alpha*(reward+(gamma*qnew)-q));
                double qupAr[] = {absScale(qup,-1000,1000,-1,1)};

                //1 Backpropagation to Update New Q Value
                nn.propagateForward(inpVecQ,actVar);
                nn.propagateBackwardOutput(qupAr,actVar);
                nn.weightUpdateOutput(learningRate,momentum);
                nn.propagateBackwardHidden(actVar);
                nn.weightUpdateHidden(learningRate,momentum,inpVecQ);
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
                double qupAr[] = {absScale(qup,-1000,1000,-1,1)};

                //1 Backpropagation to Update New Q Value
                //Experience Replay Code Goes Here
                nn.propagateForward(inpVecQ,actVar);
                nn.propagateBackwardOutput(qupAr,actVar);
                nn.weightUpdateOutput(learningRate,momentum);
                nn.propagateBackwardHidden(actVar);
                nn.weightUpdateHidden(learningRate,momentum,inpVecQ);
                return;
            }
            if(terminal==false){
                qup = q + (alpha*(reward+(gamma*qnew)-q));
                double qupAr[] = {absScale(qup,-1000,1000,-1,1)};

                //1 Backpropagation to Update New Q Value
                //Experience Replay Code Goes Here
                nn.propagateForward(inpVecQ,actVar);
                nn.propagateBackwardOutput(qupAr,actVar);
                nn.weightUpdateOutput(learningRate,momentum);
                nn.propagateBackwardHidden(actVar);
                nn.weightUpdateHidden(learningRate,momentum,inpVecQ);
                return;
            }
        }
    }

    /**
     * Function to Scale Things from one range to another
     * @param val Input to be scaled
     * @param min Lower Limit of Input Range
     * @param max Upper Limit of Input Range
     * @param a  Lower Limit of Target Range
     * @param b Upper Limit of Target Range
     */
    public static double absScale( double val, double min, double max, double a, double b){
        double result;
        result = ((b - a) * (val - min) / (max - min)) + a;
        return result;
    }

}
