package avzero07.reinforcementlearning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The NeuralNet Class.
 * Implements the NeuralNetInterface.
 * @date 19-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.1.5
 */

/*
Changelog
---------------
Version 0.1.5
---------------
Date 19-Oct-2019
- Implemented tanHyper()
    --- Added toggle t=2 for computeActivation() and computeActivationDeriv()
- Implemented saveWeights()
    --- Writes the weight matrices to specified path
    --- Current implementation requires path to exist
---------------
Version 0.1.1
---------------
Date 19-Oct-2019
- Fully functional version
- Fixed critical bug in propagateBackwardHidden()
    -- Derivative term was incorrectly getting multiplied with y
---------------
Version 0.1.0
---------------
Date 19-Oct-2019
- Milestone version!
- Implemented new methods
    -- computeActivation() to call the appropriate sigmoid
    -- computeActivationDerive() to compute the appropriate derivative
- Updated the following methods
    -- propagateForward() has a new parameter to specify appropriate sigmoid
        --- Now calls computeActivation() internally
    -- propagateBackwardMethods have a new parameter to specify the appropriate sigmoid derivative
        --- Now calls computeActivationDeriv() internally
    -- propagateForward() is no longer a placeholder
        --- Swapped roles with compOutput() which now calls propagateForward() with t = 0
        --- Decision to swap in order to keep compOutput() as is in the common interface
---------------
Version 0.0.99
---------------
Date 19-Oct-2019
- Added 2D Fields used with Momentum
    -- weightsInputDiff     : for storing the weight change for input weights during a weight update
    -- weightsOutputDiff    : for storing the weight change for output weights during a weight update
- Implemented weightUpdateOutput()
    -- Updates the weights in the hidden to output layer
- Implemented weightUpdateHidden()
    -- Updates the weights in the input to hidden layer
- Separated propagateBackward() to optimize error back propagation in the hidden layer
    -- propagateBackwardOutput()
    -- propagateBackwardHidden()
---------------
Version 0.0.95
---------------
Date 19-Oct-2019
- Implemented propagateForward()
    -- Just calls compOutput()
- Modified compOutput()
    -- Return type is now void
- Added 1D array fields to NeuralNet
    -- intermediateDelta    : for storing the delta at hidden layer
    -- finalDelta           : for storing the delta at output layer
- Implemented propagateBackward()
    -- Performs back propagation at output and hidden layers
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
    double[][] weightsInputDiff;
    double[][] weightsOutput;
    double[][] weightsOutputDiff;
    double[] intermediateOutput;  //Will store the hidden layer outputs for a given input pattern
    double[] intermediateDelta;   //Will store the delta values of the hidden layer for a given input pattern (considers bias)
    double[] finalOutput;         //Will store the final outputs for a given input pattern
    double[] finalDelta;          //Will store the delta values of the output layer for a given input pattern (considers bias)
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
        this.weightsInputDiff = new double[this.argNumInputs+1][this.argNumHidden];
        this.weightsOutput = new double[this.argNumHidden+1][this.argNumOutputs];
        this.weightsOutputDiff = new double[this.argNumHidden+1][this.argNumOutputs];

        this.intermediateOutput = new double[this.argNumHidden];
        this.intermediateDelta = new double[this.argNumHidden];
        this.finalOutput = new double[this.argNumOutputs];
        this.finalDelta = new double[this.argNumOutputs];
    }


    /*
    * Implementation for the Bipolar Sigmoid
    * f(x) = (2/(1+Math.pow(Math.E,-x)))-1
    * f(x) = 1      at x = +ve
    * f(x) = 0      at x = 0
    * f(x) = -1     at x = -ve
    * */
    @Override
    public double bipSigmoid(double x) {
        return ((2 / (1 + Math.pow(Math.E, -1 * x))) - 1);
    }

    /*
     * Implementation for the Sigmoid
     * f(x) = 1/(1+Math.pow(Math.E,-x))
     * f(x) = 1     at x = +ve
     * f(x) = 0.5   at x = 0
     * f(x) = 0     at x = -ve
     * */
    @Override
    public double sigmoid(double x) { return (1/(1+Math.pow(Math.E,-1*x))); }

    @Override
    public double tanHyper(double x) {
        return Math.tanh(x);
    }

    /*
    * Implements the general activation function
    * which calls either sigmoid, bipSigmoid or tanHyper based
    * on the toggle.
    *
    * 1 = bipSigmoid()
    * 2 = tanHyper()
    * Default is sigmoid()
    * */
    @Override
    public double computeActivation(double y, int t) {
        if(t==1) return bipSigmoid(y);
        if(t==2) return tanHyper(y);
        else return sigmoid(y);
    }

    /*
     * Implements the general first derivative of the activation function
     * which calls the appropriate function related to either sigmoid or
     * bipSigmoid based on the toggle.
     *
     * 1 = derivative of bipSigmoid()
     * 2 = derivative of tanHyper()
     * Default is sigmoid()
     *
     * NOTE: x is f(x) for the appropriate sigmoid. sigmoid() or
     * bipSigmoid() will not be called here.
     * */
    @Override
    public double computeActivationDeriv(double y, int t) {
        if(t==1) return (0.5*(1+y)*(1-y));
        if(t==2) return (1-Math.pow(y,2));
        else return (y*(1-y));
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
        *Fill this.weightsInput with random values between lowerBound and upperBound (Exclusive of upper bound)
         * */
        for(int i=0;i<(this.argNumInputs+1);i++){
            for(int j=0;j<(this.argNumHidden);j++){
                this.weightsInput[i][j] = ThreadLocalRandom.current().nextDouble(lowerBound,upperBound);
            }
        }

        /*
         *Fill this.weightsOutput with random values between lowerBound and upperBound (Exclusive of upper bound)
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
    * This method is only to make the implementation meaningful
    * for a NeuralNet since compOutput is too generic
    * */
    @Override
    public void propagateForward(double[] x, int t) {
        //Compute and store the intermediate output
        for(int i=0;i<this.argNumHidden;i++){
            double sum = 0;
            for(int j=0;j<=this.argNumInputs;j++){
                if(j==0) sum = sum + (this.bias*this.weightsInput[j][i]);
                if(j!=0) sum = sum + (x[j-1]*this.weightsInput[j][i]);
            }
            this.intermediateOutput[i] = this.computeActivation(sum,t);
        }

        //Compute and store the final output
        for(int i=0;i<this.argNumOutputs;i++){
            double sum = 0;
            for(int j=0;j<=this.argNumHidden;j++){
                if(j==0) sum = sum + (this.bias*this.weightsOutput[j][i]);
                if(j!=0) sum = sum + (this.intermediateOutput[j-1]*weightsOutput[j][i]);
            }
            this.finalOutput[i] = this.computeActivation(sum,t);
        }
    }

    /*
    * This method will calculate and populate the delta matrices
    * in the hidden and output layers
    * */
    @Override
    public void propagateBackwardOutput(double[] outputPattern, int t) {
        //Compute Delta for the Output Layer
        for(int i=0;i<this.argNumOutputs;i++){
            double c = outputPattern[i];
            double y = this.finalOutput[i];
            this.finalDelta[i] = (c - y)*this.computeActivationDeriv(y,t);
        }
    }

    @Override
    public void propagateBackwardHidden(int t){
        //Compute Delta for the Hidden Layer
        for(int i=0;i<this.argNumHidden;i++){
            double wDelta = 0;
            for(int j=0;j<this.argNumOutputs;j++){
                double w = this.weightsOutput[i][j];
                double del = this.finalDelta[j];
                wDelta = wDelta + (w*del);
            }
            double y = this.intermediateOutput[i];
            this.intermediateDelta[i] = wDelta*this.computeActivationDeriv(y,t);
        }
    }

    /*
    * Method to update the weights at hidden to output layer
    * */
    @Override
    public void weightUpdateOutput(double learningRate, double momentum) {
        for(int i=0;i<(this.argNumOutputs);i++){
            for(int j=0;j<(this.argNumHidden+1);j++){
                if(j==0){
                    double temp = (momentum*(this.weightsOutputDiff[j][i])) + (learningRate*(this.finalDelta[i])*(this.bias));
                    this.weightsOutput[j][i] = this.weightsOutput[j][i] + temp;
                    this.weightsOutputDiff[j][i] = temp;
                }
                if(j!=0){
                    double temp = (momentum*(this.weightsOutputDiff[j][i])) + (learningRate*(this.finalDelta[i])*(this.intermediateOutput[j-1]));
                    this.weightsOutput[j][i] = this.weightsOutput[j][i] + temp;
                    this.weightsOutputDiff[j][i] = temp;
                }
            }
        }
    }

    /*
     * Method to update the weights at the input to hidden layer
     * Implemented separate from the output layer to optimize the
     * error back propagation
     * */
    @Override
    public void weightUpdateHidden(double learningRate, double momentum, double[] inputPattern) {
        for(int i=0;i<(this.argNumHidden);i++){
            for(int j=0;j<(this.argNumInputs+1);j++){
                double temp;
                if(j==0){
                    temp = (momentum*(this.weightsInputDiff[j][i])) + (learningRate*(this.intermediateDelta[i])*(this.bias));
                    this.weightsInput[j][i] = this.weightsInput[j][i] + temp;
                    this.weightsInputDiff[j][i] = temp;
                }
                if(j!=0){
                    temp =  (momentum*(this.weightsInputDiff[j][i])) + (learningRate*(this.intermediateDelta[i])*(inputPattern[j-1]));
                    this.weightsInput[j][i] = this.weightsInput[j][i] + temp;
                    this.weightsInputDiff[j][i] = temp;
                }
            }
        }
    }

    public void nullifyStructures(){
        this.weightsInput = null;
        this.weightsInputDiff = null;
        this.weightsOutput = null;
        this.weightsOutputDiff = null;

        this.intermediateOutput = null;
        this.intermediateDelta = null;
        this.finalOutput = null;
        this.finalDelta = null;
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
    public void compOutput(double[] x) {
        propagateForward(x,0);
    }

    @Override
    public double train(double[] x, double argValue) {
        return 0;
    }

    /*
    * This method will save the weights of the NeuralNet object to
    * two files in the specified directory. One file each for the
    * two weight matrices (this.weightsInput and this.weightsOutput)
    * */
    @Override
    public void saveWeights(String pathToDirectory, String identifier) {

        String pathToDirectory1 = pathToDirectory+identifier+"IP_to_Hidden_Weights.txt";
        String pathToDirectory2 = pathToDirectory+identifier+"Hidden_to_OP_Weights.txt";

        String textIpHidden = "";
        String textHiddenOp = "";

        //Write The First Matrix
        for(int i=0;i<=(argNumInputs);i++){
            for(int j=0;j<(argNumHidden);j++){
                textIpHidden = textIpHidden + this.weightsInput[i][j] + "\t";
            }
            textIpHidden = textIpHidden+"\n";
        }

        BufferedWriter writer1 = null;
        try {
            writer1 = new BufferedWriter(new FileWriter(pathToDirectory1));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer1.write(textIpHidden,0,textIpHidden.length());
            writer1.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //Write The Second Matrix
        for(int i=0;i<=(argNumHidden);i++){
            for(int j=0;j<(argNumOutputs);j++){
                textHiddenOp = textHiddenOp + this.weightsOutput[i][j] + "\t";
            }
            textHiddenOp = textHiddenOp+"\n";
        }

        BufferedWriter writer2 = null;
        try {
            writer2 = new BufferedWriter(new FileWriter(pathToDirectory2));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer2.write(textHiddenOp,0,textHiddenOp.length());
            writer2.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(String argFileName) throws IOException {

    }
}
