package avzero07.reinforcementlearning;

import java.io.File;
import java.io.IOException;

/**
 * The NeuralNet Class.
 * Implements the NeuralNetInterface.
 * @date 18-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.89
 */

/*
Changelog
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
    }

    @Override
    /*
    * Implementation for the Bipolar Sigmoid
    * f(x) = (2/(1+Math.pow(Math.E,-x)))-1
    * f(x) = 1   at x = +ve
    * f(x) = 0   at x = 0
    * f(x) = -1  at x = -ve
    * */
    public double sigmoid(double x) {
        return (2 / (1 + Math.pow(Math.E, -1 * x))) - 1;
    }

    @Override
    public double customSigmoid(double x) {
        return 0;
    }

    @Override
    public void initWeights() {

    }

    @Override
    public void zeroWeights() {

    }

    @Override
    public double compOutput(double[] x) {
        return 0;
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
