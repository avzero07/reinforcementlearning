package avzero07.reinforcementlearning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation Class for Back-propagation Learning
 *
 * Assignment 1.a) Using Binary Representation with momentum 0.0
 *
 * @date 19-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.5
 */

/*
Changelog
---------------
Version 0.5
---------------
Date 19-Oct-2019
- Fixed bug with output pattern representation
- Converges sometimes for Binary XOR
---------------
Version 0.1
---------------
Date 19-Oct-2019
- Initial implementation
* */

public class BackPropagationLearning1 {

    public static void main(String[] args) throws IOException {

        /*
        * Training Data
        *
        * XOR
        *
        * Representation: Binary
        * x1       x2      y
        * 0        0       0
        * 0        1       1
        * 1        0       1
        * 1        1       0
        *
        * */

        double[][] x = {{0,0},{0,1},{1,0},{1,1}};
        double[][] y = {{0},{1},{1},{0}};

        /*
        * Specify the fields and parameters to use
        * */

        int numInputNeurons = 2;
        int numHiddenNeurons = 4;
        int numOutputNeurons = 1;

        double learningRate = 0.2;
        double momentum = 0.0;

        double argA = 0;
        double argB = 0;

        /*
        * Specified the Sigmoid
        * t = 0 : Sigmoid
        * t = 1 : Bipolar Sigmoid
        *
        * Default (t!=1) : Sigmoid
        * */
        int t = 0;

        /*
        * Total Error, E = 0.5 * sum(yp - cp)^2
        * Where yp = output of the NeuralNet for pattern p
        * Where cp = expected output for pattern p
        *
        * The sum is iterative over the number of patterns
        * */
        double E = 0;

        /*
        * Instantiate and Initialize NeuralNet object
        * All relevant data structures for nn1 will be created
        * */
        NeuralNet nn1 = new NeuralNet(numInputNeurons, numHiddenNeurons, numOutputNeurons, learningRate, momentum, argA, argB);

        int loop = 5;

        while(loop!=0){

            /*
            * Start implementation of the BP Learning Algorithm
            * */

            //Step 1: Initialize Weights
            nn1.initWeights(-0.5,0.5);

            //disp2D(nn1.weightsInput,nn1.argNumInputs+1,nn1.argNumHidden);
            //disp2D(nn1.weightsOutput,nn1.argNumHidden+1,nn1.argNumOutputs);

            //Step 2: Start iteration through the list of input patterns

            /*
            * Break from the while loop when Total Error, E is acceptable
            * */

            loop--;
            //Track Epoch using k
            int k = 0;
            String s = "\n";
            while(true){
                k++;

                for(int i=0;i<x.length;i++){

                    //Perform Forward Propagation
                    nn1.propagateForward(x[i],t);

                    /*
                     * Perform Back Propagation in Output Layer
                     * to generate finalDelta
                     * */
                    nn1.propagateBackwardOutput(y[i],t);

                    //Update weights in the hidden to output layer
                    nn1.weightUpdateOutput(learningRate,momentum);

                    /*
                     * Perform Back Propagation in hidden layer
                     * to generate intermediateDelta
                     * */
                    nn1.propagateBackwardHidden(t);

                    //Update weights in the input to hidden layer
                    nn1.weightUpdateHidden(learningRate,momentum,x[i]);

                    /*
                     * At this point a single pattern has been
                     * propagated through the NeuralNet nn1 and
                     * it's weights have been updated using the
                     * Back Propagation algorithm
                     *
                     * Next Step is to compute the Total Error
                     * */

                    //disp1D(nn1.finalOutput,nn1.finalOutput.length);

                    E = E + Math.pow((nn1.finalOutput[0] - y[i][0]),2);
                }

                //Halving the computed total error for an Epoch per the formula
                E = 0.5*E;

                s = s + "\n"+k+", "+E;
                System.out.println("Epoch : "+k+" Total Error : "+E);

                if(E<0.05)
                    break;
                if(k>100000)
                    break;
            }
            writeToFile("C:/Users/Akshay/Desktop/Reinforcement Learning Data/Q1A/"+loop+"."+k+".txt",s);
        }

        //writeToFile("C:/Users/Akshay/Desktop/Reinforcement Learning Data/sample.txt",s);

        //disp2D(nn1.weightsInput,nn1.argNumInputs+1,nn1.argNumHidden);
        //disp2D(nn1.weightsOutput,nn1.argNumHidden+1,nn1.argNumOutputs);
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
