package avzero07.reinforcementlearning;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The NeuralNet Class.
 * Implements the NeuralNetInterface.
 * @date 18-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.91
 */

/*
Changelog
---------------
Version 0.0.91
---------------
- Added 1D array fields to NeuralNet
    -- intermediateOutput   : for storing the output at hidden layer
    -- finalOutput          : for storing the output at output layer
- Implemented compOut() method
    -- Performs forward propagation
---------------
Version 0.0.9
---------------
- Added 2D Weight Matrices weightInput and weightOutput
    -- Constructor has been updated to dynamically create the Weight Matrices
- Implemented initWeights()
    -- Makes use of the ThreadLocalRandom class to generate pseudorandom numbers
- Implemented zeroWeights()
---------------
Version 0.0.89
---------------
- Added argNumOutputs field
- Implemented sigmoid() [Bipolar Sigmoid]
---------------
Version 0.0.8
---------------
- Defined initial set of fields
- Initial Implementation of Constructor
    -- Passes constructorTest() in NeuralNet_Test class
- Fixed package name: removed leading java
---------------
Version 0.0.7
---------------
- Initial implementation
 */

public class NeuralNet implements NeuralNetInterface{

    /*
     * Define Fields for the NeuralNet here
     */
    int argNumInputs;
    int argNumHidden;
    int argNumOutputs;
    double[][] weightsInput;
    double[][] weightsOutput;
    double[] intermediateOutput;  //Will store the intermediate outputs for a given input pattern
    double[] finalOutput;         //Will store the final outputs for a given input pattern
    double argLearningRate;
    double argMomentum;
    double argA;
    double argB;

    /*
     * Constructor Method for the NeuralNet Object
     */
    public NeuralNet(int argNumInputs, int argNumHidden, int argNumOutputs, double argLearningRate, double argMomentum, double argA, double argB){
        this.argNumInputs = argNumInputs;
        this.argNumHidden = argNumHidden;
        this.argNumOutputs = argNumOutputs;
        this.argLearningRate = argLearningRate;
        this.argMomentum = argMomentum;
        this.argA = argA;
        this.argB = argB;

        this.weightsInput = new double[this.argNumInputs+1][this.argNumHidden];
        this.weightsOutput = new double[this.argNumHidden+1][this.argNumOutputs];

        this.intermediateOutput = new double[this.argNumHidden];
        this.finalOutput = new double[this.argNumOutputs];
    }


    /*
    * Implementation for the Bipolar Sigmoid
    * f(x) = (2/(1+Math.pow(Math.E,-x)))-1
    * f(x) = 1   at x = +ve
    * f(x) = 0   at x = 0
    * f(x) = -1  at x = -ve
    * */
    @Override
    public double sigmoid(double x) {
        return (2 / (1 + Math.pow(Math.E, -1 * x))) - 1;
    }

    @Override
    public double customSigmoid(double x) {
        return 0;
    }

    /*
    * Randomizes the weights of the NeuralNets
    * Parameters will be Upper and Lower Bound
    * */
    @Override
    public void initWeights(double lowerBound, double upperBound) {
        /*
        *Fill this.weightsInput with random values between lowerBound and upperBound (Inclusive)
         * */
        for(int i=0;i<(this.argNumInputs+1);i++){
            for(int j=0;j<(this.argNumHidden);j++){
                double random = ThreadLocalRandom.current().nextDouble(lowerBound,upperBound);
                this.weightsInput[i][j] = random;
            }
        }

        /*
         *Fill this.weightsOutput with random values between lowerBound and upperBound (Inclusive)
         * */
        for(int i=0;i<(this.argNumHidden+1);i++){
            for(int j=0;j<(this.argNumOutputs);j++){
                double random = ThreadLocalRandom.current().nextDouble(lowerBound,upperBound);
                this.weightsOutput[i][j] = random;
            }
        }
    }

    /*
     * Sets the weights of the NeuralNet object to Zero
     * No parameters. Operates on this.weighsInput and
     * this.weightsOutput.
     * */
    @Override
    public void zeroWeights() {
        /*
         *Fill this.weightsInput with Zero
         * */
        for(int i=0;i<(this.argNumInputs+1);i++){
            for(int j=0;j<(this.argNumHidden);j++){
                this.weightsInput[i][j] = 0;
            }
        }

        /*
         *Fill this.weightsOutput with Zero
         * */
        for(int i=0;i<(this.argNumHidden+1);i++){
            for(int j=0;j<(this.argNumOutputs);j++){
                this.weightsOutput[i][j] = 0;
            }
        }
    }

    /*
    * To fill the weight matrices with a custom value
    * */
    public void fillWeights(double f) {
        /*
         *Fill this.weightsInput with f
         * */
        for(int i=0;i<(this.argNumInputs+1);i++){
            for(int j=0;j<(this.argNumHidden);j++){
                this.weightsInput[i][j] = f;
            }
        }

        /*
         *Fill this.weightsOutput with f
         * */
        for(int i=0;i<(this.argNumHidden+1);i++){
            for(int j=0;j<(this.argNumOutputs);j++){
                this.weightsOutput[i][j] = f;
            }
        }
    }

    /*
    * This method will compute the output 'y' for a given input 'x'
    * Forward Propagation
    * */
    @Override
    public double[] compOutput(double[] x) {
        //Compute and store the intermediate output
        for(int i=0;i<this.argNumHidden;i++){
            double sum = 0;
            for(int j=0;j<=this.argNumInputs;j++){
                if(j==0) sum = sum + (this.bias*this.weightsInput[j][i]);
                if(j!=0) sum = sum + (x[j-1]*this.weightsInput[j][i]);
            }
            this.intermediateOutput[i] = this.sigmoid(sum);
        }

        //Compute and store the final output
        for(int i=0;i<this.argNumOutputs;i++){
            double sum = 0;
            for(int j=0;j<=this.argNumHidden;j++){
                if(j==0) sum = sum + (this.bias*this.weightsOutput[j][i]);
                if(j!=0) sum = sum + (this.intermediateOutput[j-1]*weightsOutput[j][i]);
            }
            this.finalOutput[i] = this.sigmoid(sum);
        }
        return this.finalOutput;
    }

    @Override
    public double train(double[] x, double argValue) {
        return 0;
    }

    @Override
    public void saveWeights(File argFile) {

    }

    @Override
    public void load(String argFileName) throws IOException {

    }
}
