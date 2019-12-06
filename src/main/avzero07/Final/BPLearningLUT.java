package avzero07.Final;

import avzero07.reinforcementlearning.NeuralNet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * Implements a class that learns the RL LUT using a NN
 * @date 05-December-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.1
 */

/*
Changelog
---------------
Version 0.0.1
---------------
- Added absScale()
    -- Implemented and Passes Tests
- Added loadLutToArray()
    -- Load seems to work fine. Tested with Battle 3 - LUT
- Working but slow
- Adding File Handling
*/

public class BPLearningLUT {

    public static void main(String[] args) throws IOException {

        //Housekeeping Stuff
        //File Writing Variables
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        String strDate = dateFormat.format(date);
        String pathString = "C:/Users/Akshay/Desktop/RL LUT Values/Neural Net Outputs/"+strDate+"/";
        File dir = new File(pathString);
        dir.mkdirs();

        String pathStringWeights = pathString+"Final Weights/";
        File dirWeights = new File(pathStringWeights);
        dirWeights.mkdirs();


        //Declare All Variables
        double[][][][][][] LUT = new double[6][4][4][4][4][8];

        //Read Input LUT
        String pathToLut = "C:/Users/Akshay/Desktop/RL LUT Values/Battle 3.txt";
        loadLutToArray(LUT, pathToLut);

        //Initialize Neural Net
        int numInputNeurons = 13; //5 State Variables and 8 for Action with 1 hot Encoding
        int numHiddenNeurons = 7;
        int numOutputNeurons = 1;

        double learningRate = 0.01;
        double momentum = 0;

        double argA = 0;
        double argB = 0;

        int t = 1;
        String activation = "";
        switch (t) {
            case 1:
                activation = "Bipolar Sigmoid";
                break;
            case 2:
                activation = "Tan Hyperbolic";
                break;
            default:
                activation = "Sigmoid";
        }

        double E = 0;

        NeuralNet nn1 = new NeuralNet(numInputNeurons, numHiddenNeurons, numOutputNeurons, learningRate, momentum, argA, argB);
        nn1.initWeights(-0.5, 0.5);

        //Track Epoch using k
        int k = 0;
        String s = "Activation = "+activation+"\nLearning Rate = "+learningRate+"\nMomentum = "+momentum+"\nHidden Neurons = "+numHiddenNeurons;

        while(true){
            k++;

            if(k>10000){
                String identifier = k+".";
                nn1.saveWeights(pathStringWeights,identifier);
                break;
            }

            //Start Loop Across LUT
            for(int d = 0; d < 6; d++){
                double di = absScale(d,0,5,-0.8,0.8);
                for(int men = 0; men < 4; men++){
                    double meni = absScale(men,0,3,-0.8,0.8);
                    for(int een = 0; een < 4; een++){
                        double eeni = absScale(een,0,3,-0.8,0.8);
                        for(int ex = 0; ex < 4; ex++){
                            double exi = absScale(ex,0,3,-0.8,0.8);
                            for(int yi = 0; yi < 4; yi++){
                                double yii = absScale(yi,0,3,-0.8,0.8);
                                for(int act = 0; act < 8; act++){
                                    //Each action needs to be 1 hot encoded
                                    double x[] = new double[13];
                                    switch(act){
                                        case 0: x = new double[] {di,meni,eeni,exi,yii,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                                        break;
                                        case 1: x = new double[] {di,meni,eeni,exi,yii,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                                        break;
                                        case 2: x = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8,-0.8};
                                        break;
                                        case 3: x = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8,-0.8};
                                        break;
                                        case 4: x = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8,-0.8};
                                        break;
                                        case 5: x = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8,-0.8};
                                        break;
                                        case 6: x = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8,-0.8};
                                        break;
                                        case 7: x = new double[] {di,meni,eeni,exi,yii,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,-0.8,0.8};
                                        break;
                                    }

                                    //Defining Training Output For ErrorBackPropagation
                                    double y[] = {absScale(LUT[d][men][een][ex][yi][act],-1000,1000,-0.8,0.8)};

                                    //Error Back Propagation
                                    nn1.propagateForward(x,t);
                                    nn1.propagateBackwardOutput(y,t);
                                    nn1.weightUpdateOutput(learningRate,momentum);
                                    nn1.propagateBackwardHidden(t);
                                    nn1.weightUpdateHidden(learningRate,momentum,x);

                                    E = E + Math.pow((nn1.finalOutput[0] - y[0]),2);
                                }
                            }
                        }
                    }
                }
            }
            E = 0.5*E;
            //Perfect Condition Stuff
            if(E<=0.05){
                //System.out.println("Trial Number: "+loop+" Converged at Epoch "+k);
                String identifier = k+".";
                nn1.saveWeights(pathStringWeights,identifier);
                break;
            }

            //End of Epoch Code Goes Here
            String cmdOp = "Epoch "+k+", Error = "+E;
            System.out.println(cmdOp);

            s = s + "\n" + k + " " + E;
            writeToFile(pathString+"Stats.txt",s);
            E = 0;
        }
    }

    //Helper Functions

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

    public static void loadLutToArray(double[][][][][][] target, String inputFilename) throws IOException {
        File f = new File(inputFilename);
        Scanner scan = new Scanner(f);

        for (int d = 0; d < 6; d++)
            for (int men = 0; men < 4; men++)
                for (int een = 0; een < 4; een++)
                    for (int ex = 0; ex < 4; ex++)
                        for (int yi = 0; yi < 4; yi++)
                            for (int act = 0; act < 8; act++) {
                                try {
                                    target[d][men][een][ex][yi][act] = scan.nextDouble();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

        scan.close();
    }

    //Trusty File Writer Method
    public static void writeToFile(String path, String text) throws IOException {
        Charset charSet = Charset.forName("US-ASCII");
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(text,0,text.length());
        writer.close();
    }
}
