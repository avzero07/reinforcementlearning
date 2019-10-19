package avzero07.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Class for Testing the NeuralNet class implementation
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
 - Defined initial test for the constructor
    -- Simple test. Just checks initialization.
 - Fixed package name: removed leading java
 ---------------
 Version 0.0.7
 ---------------
- Initial implementation
 */

public class NeuralNet_Test {

    /**
     * This first test merely checks whether the Constructor is initializing the
     * NeuralNet object with the values correctly
     */
    @Test
    public void constructorTest(){
        int argNumInputs = 100;
        int argNumHidden = 4;
        double argLearningRate = 2.5;
        double argMomentum = 3.0;
        double argA = 1.7;
        double argB = 0.7;

        NeuralNet nn = new NeuralNet(argNumInputs, argNumHidden, argLearningRate, argMomentum, argA, argB);

        Assert.assertEquals(argNumInputs,nn.argNumInputs);
        Assert.assertEquals(argNumHidden,nn.argNumHidden);
        Assert.assertEquals(argLearningRate,nn.argLearningRate,0);
        Assert.assertEquals(argMomentum,nn.argMomentum,0);
        Assert.assertEquals(argA,nn.argA,0);
        Assert.assertEquals(argB,nn.argB,0);
    }

}
