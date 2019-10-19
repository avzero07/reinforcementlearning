package avzero07.reinforcementlearning;

/**
 * NeuralNet Interface that extends the CommonInterface.
 * @date 19-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.99
 */

/*
Changelog
---------------
Version 0.0.99
---------------
Date 19-Oct-2019
- Added methods
    -- weightUpdateOutput()
    -- weightUpdateHidden()
- Modified methods
    -- propagateBackward() split into two methods
        --- propagateBackwardOutput()
        --- propagateBackwardHidden()
---------------
Version 0.0.95
---------------
Date 19-Oct-2019
- Added methods
    -- propagateForward()   : forward propagation for NN
    -- propagateBackward()  : backward propagation for NN
---------------
Version 0.0.8
---------------
- Fixed package name: removed leading java
---------------
Version 0.0.7
---------------
- Initial implementation
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
     * General sigmoid with asymptotes bounded by (a,b)
     * @param x The input to the custom Sigmoidal Function.
     * @return Value of f(x) = <Equation TBD> (Placeholder: b_minus_a / (1 + e(-x)) - minus_a)
     */
    public double customSigmoid(double x);

    /**
     * Set the weights to random values. Will be used to initialize.
     * <Implementation Set to Change based on Implementation>
     */
    public void initWeights(double lower, double upper);

    /**
     * Set the weights to Zero
     */
    public void zeroWeights();

    /**
    * Function to carry out Forward Propagation
    * It will update/populate the intermediate and final output matrices
    * Essentially will only call compOutput()
    * New Name to make the method meaningful for a NeuralNet
     * @param x is the input array
    * */
    public void propagateForward(double[] x);

    /**
    * Function to perform Backward Propagation at the OP layer
    * NOTE: Function if for a single pattern and not for an Epoch
    * It will update/populate the delta arrays in the output layer
     * @param outputPatter is the output pattern for a given input x
    * */
    public void propagateBackwardOutput(double[] outputPatter);

    /**
     * Function to perform Backward Propagation at the hidden layer
     * NOTE: Function if for a single pattern and not for an Epoch
     * It will update/populate the delta arrays in the hidden layer
     * */
    public void propagateBackwardHidden();

    /**
     * Function to make weight updates to the hidden to output layer
     * @param learningRate is the learning rate to be used while training
     * @param momentum is the momentum for weight update
     * */
    public void weightUpdateOutput(double learningRate, double momentum);

    /**
     * Function to make weight updates to the input to hidden layer
     * @param learningRate  is the learning rate to be used while training
     * @param momentum is the momentum for weight update
     * @param inputPattern is the input pattern x
     * */
    public void weightUpdateHidden(double learningRate, double momentum, double[] inputPattern);
}
