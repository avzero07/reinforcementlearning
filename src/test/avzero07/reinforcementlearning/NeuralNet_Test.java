package avzero07.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Class for Testing the NeuralNet class implementation
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

    private int argNumInputs = 100;
    private int argNumHidden = 4;
    private int argNumOutputs = 1;
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
}
