package java.avzero07.reinforcementlearning;

import java.io.File;
import java.io.IOException;

/**
 * The NeuralNet Class.
 * Implements the NeuralNetInterface.
 * @date 18-October-2019
 * @author avzero07 (Akshay V)
 * @email akshay.viswakumar@gmail.com
 * @version 0.0.7
 */

public class NeuralNet implements NeuralNetInterface{

    /**
     * Define Fields for the NeuralNet here
     */

    /**
     * Constructor Method for the NeuralNet Object
     */
    public NeuralNet(){

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
