package avzero07.reinforcementlearning;

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
 * Implementation Class for Back-propagation Learning
 *
 * Includes Loops and Stats
 *
 * Assignment 1.b) Using Bipolar Representation with momentum 0.0
 *
 * @date 19-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.7
 */

/*
Changelog
---------------
Version 0.7
---------------
Date 20-Oct-2019
- Added user interaction
    -- Accepts User Input to set the number of trials
- Writes the following to "C:/Users/Akshay/Desktop/Reinforcement Learning Data/Q1B/"
    -- Each run gets its own Timestamped folder
    -- Each such folder contains
        --- CSV files named <trial>.<epoch>.txt
        --- Statistics-Q1B.txt which contains some stats across all trials for the current run
        --- "Epoch List (Converged) Q1C.txt" which contains Epochs to convergence for each trial
---------------
Version 0.5
---------------
Date 19-Oct-2019
- Initial Implementation
* */

public class BPLearning2 {

    public static void main(String[] args) throws IOException {

        double[][] x = {{-1,-1},{-1,1},{1,-1},{1,1}};
        double[][] y = {{-1},{1},{1},{-1}};

        int numInputNeurons = 2;
        int numHiddenNeurons = 4;
        int numOutputNeurons = 1;

        double learningRate = 0.2;
        double momentum = 0.0;

        double argA = 0;
        double argB = 0;

        int t = 1;

        double E = 0;

        //Receive Input from User to Decide Number of Trials
        Scanner scan = new Scanner(System.in);
        System.out.println("How many trials would you like to perform?");
        int loop = scan.nextInt();
        scan.close();

        double lowestK = 100000;
        double highestK = 0;
        double sumK = 0;
        double sumConvergeK = 0;
        double avgK;
        double avgConvergeK;
        double numIter = loop;
        double numConv = 0;
        double ratioConv;

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss");
        String strDate = dateFormat.format(date);

        String pathString = "C:/Users/Akshay/Desktop/Reinforcement Learning Data/Q1B/Test "+strDate+"/";
        File dir = new File(pathString);
        dir.mkdirs();

        String kCount = "";
        while(loop!=0){
            loop--;
            NeuralNet nn1 = new NeuralNet(numInputNeurons, numHiddenNeurons, numOutputNeurons, learningRate, momentum, argA, argB);
            nn1.initWeights(-0.5,0.5);

            //Track Epoch using k
            int k = 0;
            String s = "";
            while(true){
                k++;
                if(k>100000){
                    System.out.println("Trial Number: "+loop+" Still yet to converge at Epoch "+k);
                    break;
                }
                for(int i=0;i<x.length;i++){
                    nn1.propagateForward(x[i],t);
                    nn1.propagateBackwardOutput(y[i],t);
                    nn1.weightUpdateOutput(learningRate,momentum);
                    nn1.propagateBackwardHidden(t);
                    nn1.weightUpdateHidden(learningRate,momentum,x[i]);

                    E = E + Math.pow((nn1.finalOutput[0] - y[i][0]),2);
                }
                E = 0.5*E;
                s = s + "\n"+k+", "+E;

                if(E<0.05){
                    kCount = kCount+"\n"+k;
                    System.out.println("Trial Number: "+loop+" Converged at Epoch "+k);
                    break;
                }
            }
            writeToFile(pathString+loop+"."+k+".txt",s);
            writeToFile(pathString+"Epoch List (Converged) Q1B.txt",kCount+"\n");
            if(k<lowestK)
                lowestK = k;
            sumK = sumK+k;
            if(k<100000){
                sumConvergeK = sumConvergeK + k;
                if(k>highestK)
                    highestK = k;
                numConv++;
            }
        }
        avgK = sumK/(numIter);
        avgConvergeK = sumConvergeK/(numIter);
        ratioConv = numConv/numIter;

        String statistics = "\nNumber of Tests Unconverged = "+(numIter-numConv)
                            +"\nNumber of Tests Converged = "+numConv
                            +"\nConvergence Ratio = "+ratioConv
                            +"\nSlowest Convergence (out of all converged) = "+highestK+" Epochs"
                            +"\nFastest Convergence (out of all converged) = "+lowestK+" Epochs"
                            +"\nAverage Number of Epochs (Total) = "+avgK
                            +"\nAverage Number of Epochs (Converged) = "+avgConvergeK
                            +"\n";
        writeToFile(pathString+"/Stats-Q1B.txt",statistics);
        System.out.println("\nStatistics"+statistics);
    }

    /*
    * Method Area
    * */

    /*
    * Disp2D = To Display a 2D Array
    * */
    public static void disp2D(double[][] matrix, int rowLength, int collength){
        System.out.println("Is this your Matrix?");
        System.out.println("");
        for(int i=0;i<(rowLength);i++){
            for(int j=0;j<(collength);j++){
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    /*
    * Disp1D = To Display a 1D Array
    * */
    public static void disp1D(double[] array, int arrayLength){
        System.out.println("Is this your Array?");
        System.out.println("");
        for(int i=0;i<arrayLength;i++){
            System.out.print(array[i]+" ");
        }
        System.out.println("");
    }

    public static void writeToFile(String path, String text) throws IOException {
        Charset charSet = Charset.forName("US-ASCII");
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(text,0,text.length());
        writer.close();
    }
}


