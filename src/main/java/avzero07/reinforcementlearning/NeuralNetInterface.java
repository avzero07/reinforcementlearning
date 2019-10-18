package java.avzero07.reinforcementlearning;

/**
 * NeuralNet Interface that extends the CommonInterface.
 * @Date: 18-October-2019
 * @author avzero07 (Akshay V)
 * @email akshay.viswakumar@gmail.com
 * @version 0.0.7
 */

public interface NeuralNetInterface extends CommonInterface{

    final double bias = 1.0;  //Used as the Bias for Neurons

    /**
     * Used to implement a Bipolar Sigmoid
     * @param x The input to the Sigmoidal Function.
     * @return Value of f(x) = 2/(1+exp(e,-x)) - 1
     */
    public double sigmoid(double x);

    /**
     * General sihmoid with asymptotes bounded by (a,b)
     * @param x The input to the custom Sigmoidal Function.
     * @return Value of f(x) = <Equation TBD> (Placeholder: b_minus_a / (1 + e(-x)) - minus_a)
     */
    public double customSigmoid(double x);

    /**
     * Set the weights to random values. Will be used to initialize.
     * <Implementation Set to Change based on Implementation>
     */
    public void initWeights();

    /**
     * Set the weights to Zero
     */
    public void zeroWeights();
}
