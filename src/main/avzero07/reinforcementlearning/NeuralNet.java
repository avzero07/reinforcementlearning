package avzero07.reinforcementlearning;

import java.io.File;
import java.io.IOException;

/**
 * The NeuralNet Class.
 * Implements the NeuralNetInterface.
 * @date 18-October-2019
 * @author avzero07 (Akshay V)
 * @email akshay.viswakumar@gmail.com
 * @version 0.0.8
 */

/*
Changelog
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

    /**
     * Define Fields for the NeuralNet here
     */
    int argNumInputs;
    int argNumHidden;
    double argLearningRate;
    double argMomentum;
    double argA;
    double argB;

    /**
     * Constructor Method for the NeuralNet Object
     */
    public NeuralNet(int argNumInputs, int argNumHidden, double argLearningRate, double argMomentum, double argA, double argB){
        this.argNumInputs = argNumInputs;
        this.argNumHidden = argNumHidden;
        this.argLearningRate = argLearningRate;
        this.argMomentum = argMomentum;
        this.argA = argA;
        this.argB = argB;
    }

    @Override
    public double sigmoid(double x) {
        return 0;
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
