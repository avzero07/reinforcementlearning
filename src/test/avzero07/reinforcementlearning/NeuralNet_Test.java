package avzero07.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test Class for Testing the NeuralNet class implementation
 * @date 19-October-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.95
 */

/*
Changelog
---------------
Version 0.0.95
---------------
Date 19-Oct-2019
- Added test for propagateForward()
- Fixed declaration mistake for output matrices
    -- [][] --> []
- Added test for propagateBackward()
---------------
Version 0.0.91
---------------
- Added test for compOut()
- Implemented dispMatrix() to print wight matrices
- Implemented dispOutputMatrix() to print the intermediate and final output matrices
---------------
Version 0.0.9
---------------
- Added test for initWeight()
- Added test for zeroWeights()
    -- Had to fix loop fallacy
---------------
Version 0.0.89
---------------
- Updated constructorTest()
   -- Added condition for argNumOutput
   -- Reduced to a single assert
       --- Other asserts are still present (commented out)
- Added test for sigmoid() [Bipolar Sigmoid]
   -- Tests for values across the function
---------------
Version 0.0.8
---------------
- Defined initial test for the constructor
   -- Simple test. Just checks initialization.
- Fixed package name: removed leading java
 ---------------
 Version 0.0.7
 ---------------
- Initial implementation
 */

public class NeuralNet_Test {

    private int argNumInputs = 2;
    private int argNumHidden = 4;
    private int argNumOutputs = 1;
    private double[][] weightsInput;
    private double[][] weightsOutput;
    private double[] intermediateOutput;
    private double[] intermediateDelta;
    private double[] finalOutput;
    private double[] finalDelta;
    private double argLearningRate = 2.5;
    private double argMomentum = 3.0;
    private double argA = 1.7;
    private double argB = 0.7;

    private NeuralNet nn = new NeuralNet(argNumInputs, argNumHidden, argNumOutputs, argLearningRate, argMomentum, argA, argB);

    /*
     * This first test merely checks whether the Constructor is initializing the
     * NeuralNet object with the values correctly
     */
    @Test
    public void constructorTest(){
        String expectedOutput = String.format("%d %d %d %f %f %f %f",argNumInputs,argNumHidden,argNumOutputs,argLearningRate,argMomentum,argA,argB);
        String actualOutput = String.format("%d %d %d %f %f %f %f",nn.argNumInputs,nn.argNumHidden,nn.argNumOutputs,nn.argLearningRate,nn.argMomentum,nn.argA,nn.argB);

        /*Assert.assertEquals(argNumInputs,nn.argNumInputs);
        Assert.assertEquals(argNumHidden,nn.argNumHidden);
        Assert.assertEquals(argNumOutputs,nn.argNumOutputs);
        Assert.assertEquals(argLearningRate,nn.argLearningRate,0);
        Assert.assertEquals(argMomentum,nn.argMomentum,0);
        Assert.assertEquals(argA,nn.argA,0);
        Assert.assertEquals(argB,nn.argB,0);*/

        //Modified to Use a single Assert. Other Asserts still being retained.
        Assert.assertEquals(expectedOutput,actualOutput);
    }

    //The second Test will be to check the Sigmoid Implementation

    //Test at x = 0
    @Test
    public void sigmoidTest0(){
        double x1 = 0;
        double res = nn.sigmoid(x1);
        Assert.assertEquals(0,res,0);
    }

    //Test at x = -100
    @Test
    public void sigmoidTestLow(){
        double x1 = -100;
        double res = nn.sigmoid(x1);
        Assert.assertEquals(-1,res,0);
    }

    //Test at x = -1000
    @Test
    public void sigmoidTestReallyLow(){
        double x1 = -1000;
        double res = nn.sigmoid(x1);
        Assert.assertEquals(-1,res,0);
    }

    //Test at x = 100
    @Test
    public void sigmoidTestHigh(){
        double x1 = 100;
        double res = nn.sigmoid(x1);
        Assert.assertEquals(1,res,0);
    }

    //Test at x = 1000
    @Test
    public void sigmoidTestReallyHigh(){
        double x1 = 1000;
        double res = nn.sigmoid(x1);
        Assert.assertEquals(1,res,0);
    }

    /*
    * Test to check whether a matrix with desired values
    * has been initialized.
    * */
    @Test
    public void initWeightsTest(){
        int scoreWip = 0;
        int scoreWop = 0;
        double lb = -5;
        double ub = -3;
        nn.initWeights(lb,ub);

        /*
        * Iterate through WIP
        * Increment scoreWip only if the Element weightsInput[i][j]
        * is within the bounds.
        * */
        for(int i=0;i<(nn.argNumInputs+1);i++){
            for(int j=0;j<(nn.argNumHidden);j++){
                if(nn.weightsInput[i][j]<=ub && nn.weightsInput[i][j]>=lb){
                    scoreWip++;
                }
            }
        }

        /*
         * Iterate through WOP
         * Increment scoreWop only if the Element weightsOutput[i][j]
         * is within the bounds.
         * */
        for(int i=0;i<(nn.argNumHidden+1);i++){
            for(int j=0;j<(nn.argNumOutputs);j++){
                if(nn.weightsOutput[i][j]<=ub && nn.weightsOutput[i][j]>=lb){
                    scoreWop++;
                }
            }
        }
        int expectedScore = ((nn.argNumInputs+1)*(nn.argNumHidden))+((nn.argNumHidden+1)*(nn.argNumOutputs));
        int actualScore = scoreWip+scoreWop;

        Assert.assertEquals(expectedScore,actualScore);
    }

    /*
     * Test to check whether the function can reduce all weights in the weight matrices to zero.
     * */
    @Test
    public void zeroWeightsTest(){
        double sumWip = 0;
        double sumWop = 0;
        nn.zeroWeights();

        /*
         * Iterate through WIP
         * Expecting all elements to be 0. Hence Zero sum
         * */
        for(int i=0;i<(nn.argNumInputs+1);i++){
            for(int j=0;j<(nn.argNumHidden);j++){
                sumWip = sumWip + nn.weightsInput[i][j];
            }
        }

        /*
         * Iterate through WOP
         * Expecting all elements to be 0. Hence Zero sum
         * */
        for(int i=0;i<(nn.argNumHidden+1);i++){
            for(int j=0;j<(nn.argNumOutputs);j++){
                sumWop = sumWop + nn.weightsOutput[i][j];
            }
        }
        double expectedScore = 0;
        double actualScore = sumWip+sumWop;

        Assert.assertEquals(expectedScore,actualScore,0);
    }

    /*
     * Test to check whether the function can fill all the weights as 'f'.
     * */
    @Test
    public void fillWeightsTest(){
        double sumWip = 0;
        double sumWop = 0;
        double f = 5.0;
        nn.fillWeights(f);

        /*
         * Iterate through WIP
         * Expecting all elements to be 0. Hence Zero sum
         * */
        for(int i=0;i<(nn.argNumInputs+1);i++){
            for(int j=0;j<(nn.argNumHidden);j++){
                sumWip = sumWip + nn.weightsInput[i][j];
            }
        }

        /*
         * Iterate through WOP
         * Expecting all elements to be 0. Hence Zero sum
         * */
        for(int i=0;i<(nn.argNumHidden+1);i++){
            for(int j=0;j<(nn.argNumOutputs);j++){
                sumWop = sumWop + nn.weightsOutput[i][j];
            }
        }
        double expectedScore = f*(((nn.argNumInputs+1)*nn.argNumHidden)+((nn.argNumHidden+1)*(nn.argNumOutputs)));
        double actualScore = sumWip+sumWop;

        Assert.assertEquals(expectedScore,actualScore,0);
    }

    /*
    * Test to check the compOutput function which
    * calculates the end-to-end weighted sum
    *  */
    double[] ip1 = {1,1};

    @Test
    public void compOutputTest(){
        double f = 2.0;
        nn.fillWeights(f);
        nn.compOutput(ip1);

        double[] expectedY = {0.999};
        double[] actualY = {nn.finalOutput[0]};

        //dispWeightMatrix();
        //dispOutputMatrix(nn.intermediateOutput,nn.argNumHidden,nn.finalOutput,nn.argNumOutputs);

        Assert.assertArrayEquals(expectedY,actualY,0.001);
    }

    /*
    * Inserting a Test for propagateForward()
    * Essentially identical to the previous test for compOutput()
    * */
    @Test
    public void propagateForwardTest(){
        nn.zeroWeights();
        compOutputTest();
    }

    /*
    * Test for propagateBackward()
    * Checks whether delta arrays are correctly populated
    * */
    @Test
    public void propagateBackwardTest(){
        double[] opPattern = {0};

        nn.zeroWeights();
        nn.fillWeights(2.0);

        nn.propagateForward(ip1);

        //Result of forwardPropagation (finalOutput) will be 0.9999055403765511

        nn.propagateBackward(opPattern);

        //For output layer
        //Expected op delta = (0-0.9999055403765511)*(0.9999055403765511)*(1-0.9999055403765511) = -0.00009444177
        double expectedFinalOp = (0-0.9999055403765511)*(0.9999055403765511)*(1-0.9999055403765511);

        //For hidden layer
        //Intermediate Output for a given neuron = 0.9950547536867307
        //Expected hidden delta = (2.0*-0.00009444177)*(0.9950547536867307)*(1-0.9950547536867307) = -9.29456374e-7
        double expectedHiddenOp = (2.0*-0.00009444177)*(0.9950547536867307)*(1-0.9950547536867307);

        double[] expected = {4*expectedHiddenOp,expectedFinalOp};

        //Calculate the sum of the hidden layer outputs
        double actHidOpSum = 0;
        for(int i=0;i<nn.argNumHidden;i++){
            actHidOpSum = actHidOpSum + nn.intermediateDelta[i];
        }

        double[] actual = {actHidOpSum,nn.finalDelta[0]};

        /*
        * For LOLs
        * arrays first differed at element [0];
        * Expected :-3.717825583030255E-6
        * Actual   :-3.717825939326925E-6
        *
        * Setting delta to 0.000000000005
        * */

        Assert.assertArrayEquals(expected,actual,0.000000000005);
    }

    /*
    * Utility methods to help debug
    * */

    /*
    * Method to display weightMatrices
    * */
    public void dispWeightMatrix(){
        for(int i=0;i<(nn.argNumInputs+1);i++){
            for(int j=0;j<(nn.argNumHidden);j++){
                System.out.print(nn.weightsInput[i][j]+" ");
            }
            System.out.println();
        }

        System.out.println();

        for(int i=0;i<(nn.argNumHidden+1);i++){
            for(int j=0;j<(nn.argNumOutputs);j++){
                System.out.print(nn.weightsOutput[i][j]+" ");
            }
            System.out.println();
        }
    }

    /*
     * Method to display intermediate and outputMatrices
     * */
    public void dispOutputMatrix(double[] interOp, int interOpLen, double[] finalOp, int finalOpLen){
        for(int i=0;i<interOpLen;i++){
            System.out.println(interOp[i]);
        }

        System.out.println();
        System.out.println(finalOp[0]);
    }


}
