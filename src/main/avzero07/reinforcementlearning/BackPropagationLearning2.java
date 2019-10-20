package avzero07.reinforcementlearning;

/**
 * Implementation Class for Back-propagation Learning
 *
 * Assignment 1.b) Using Bipolar Representation and momentum 0
 *
 * @date 19-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.5
 */

/*
*
Changelog
---------------
Version 0.5
---------------
Date 19-Oct-2019
- Initial Implementation
- Running without issues
- Converges for Bipolar XOR but is slow
* */

public class BackPropagationLearning2 {

    public static void main(String[] args){

        /*
        * Training Data
        *
        * XOR
        *
        * Representation: Bipolar
        * x1       x2      y
        * -1      -1      -1
        * -1       1       1
        *  1      -1       1
        *  1       1      -1
        * */

        double[][] x = {{-1,-1},{-1,1},{1,-1},{1,1}};
        double[][] y = {{-1},{1},{1},{-1}};

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
        int t = 1;

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
        NeuralNet nn2 = new NeuralNet(numInputNeurons, numHiddenNeurons, numOutputNeurons, learningRate, momentum, argA, argB);

        /*
        * Start implementation of the BP Learning Algorithm
        * */

        //Step 1: Initialize Weights
        nn2.initWeights(-0.5,0.5);

        //disp2D(nn2.weightsInput,nn2.argNumInputs+1,nn1.argNumHidden);
        //disp2D(nn2.weightsOutput,nn2.argNumHidden+1,nn1.argNumOutputs);

        //Step 2: Start iteration through the list of input patterns

        /*
        * Break from the while loop when Total Error, E is acceptable
        * */

        //Track Epoch using k
        int k = 0;

        while(true){
            k++;

            for(int i=0;i<x.length;i++){

                //Perform Forward Propagation
                nn2.propagateForward(x[i],t);

                /*
                * Perform Back Propagation in Output Layer
                * to generate finalDelta
                * */
                nn2.propagateBackwardOutput(y[i],t);

                //Update weights in the hidden to output layer
                nn2.weightUpdateOutput(learningRate,momentum);

                /*
                * Perform Back Propagation in hidden layer
                * to generate intermediateDelta
                * */
                nn2.propagateBackwardHidden(t);

                //Update weights in the input to hidden layer
                nn2.weightUpdateHidden(learningRate,momentum,x[i]);

                /*
                * At this point a single pattern has been
                * propagated through the NeuralNet nn1 and
                * it's weights have been updated using the
                * Back Propagation algorithm
                *
                * Next Step is to compute the Total Error
                * */

                //disp1D(nn1.finalOutput,nn1.finalOutput.length);

                E = E + Math.pow((nn2.finalOutput[0] - y[i][0]),2);
            }

            //Halving the computed total error for an Epoch per the formula
            E = 0.5*E;

            System.out.println("Epoch : "+k+" Total Error : "+E);

            if(E<0.05)
                break;
        }
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
}

