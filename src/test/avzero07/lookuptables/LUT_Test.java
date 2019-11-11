package avzero07.lookuptables;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Test Class for Testing the LUT class implementation
 * @date 11-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.6
 */

/*
Changelog
---------------
Version 0.0.6
---------------
- Implemented Tests for randInt() and randDoub()
---------------
Version 0.0.5
---------------
- Implemented Tests for initLut()
    --  Separate tests for 0 and Random
- Implemented Test for initLutTrav()
- Implemented Test for saveWeights()
- Implemented Test for load()
- Corrected Tests to account for Actions
---------------
Version 0.0.1
---------------
- Initial Implementation
*/

public class LUT_Test {

    int posXlev = 10;
    int posYlev = 10;
    int d2elev = 5;
    int myEnLev = 20;
    int enEnLev = 20;
    int numAct = 6;

    /**
     * Tests whether the look up table is filled with non-zeros properly
     */
    @Test
    public void fillTestRand(){
        LUT table = new LUT(d2elev, myEnLev, enEnLev, numAct);
        double sum = 0;
        table.initLut(table,1);
        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.enEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++)
                    for(int act = 0; act < table.numActions; act++){
                        sum = sum + table.lookUpTable[d][men][een][act];
                    }
        Assert.assertNotEquals(0,sum,0);
    }

    /**
     * Tests whether the look up table is filled with zeros properly
     */
    @Test
    public void fillTestZero(){
        LUT table = new LUT(d2elev, myEnLev, enEnLev, numAct);
        double sum = 0;
        table.initLut(table,0);
        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.enEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++)
                    for(int act = 0; act < table.numActions; act++){
                        sum = sum + table.lookUpTable[d][men][een][act];
                    }
        Assert.assertEquals(0,sum,0);
    }

    /**
     * Tests whether the traversal table is filled with zeros properly
     */
    @Test
    public void fillTravZero(){
        LUT table = new LUT(d2elev, myEnLev, enEnLev, numAct);
        double sum = 0;
        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.enEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++)
                    for(int act = 0; act < table.numActions; act++){
                        sum = sum + table.lookUpTableTrav[d][men][een][act];
                    }
        Assert.assertEquals(0,sum,0);
    }

    /**
     * Test to check whether LUT table is saved properly
     */
    @Test
    public void testSave() throws IOException {
        LUT table = new LUT(d2elev, myEnLev, enEnLev, numAct);
        table.initLut(table,1);
        String path = "C:/Users/Akshay/AppData/Local/Temp/LUT_Test";
        table.saveWeights(path,"test-file");

        File f = new File(path+"/test-file.txt");
        Scanner scan = new Scanner(f);

        double[][][][] loaded = new double[d2elev][myEnLev][enEnLev][numAct];

        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.enEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++)
                    for(int act = 0; act < table.numActions; act++){
                        loaded[d][men][een][act] = scan.nextDouble();
                    }
        scan.close();
        f.delete();

        //System.out.println(table.lookUpTable[3][15][8]+" "+loaded[3][15][8]);

        boolean res = Arrays.deepEquals(table.lookUpTable,loaded);

        Assert.assertEquals(true,res);
    }

    @Test
    public void testLoad() throws IOException {
        LUT table = new LUT(d2elev, myEnLev, enEnLev, numAct);
        table.initLut(table,1);
        String path = "C:/Users/Akshay/AppData/Local/Temp/LUT_Test";
        table.saveWeights(path,"test-file");

        table.initLut(table,0);

        table.load("C:/Users/Akshay/AppData/Local/Temp/LUT_Test/test-file.txt");

        double sum = 0;
        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.enEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++)
                    for(int act = 0; act < table.numActions; act++){
                        sum = sum + table.lookUpTable[d][men][een][act];
                    }
        Assert.assertNotEquals(0,sum,0);
    }

    @Test
    public void testQuant(){
        //Remember that Energy can go above 100
        double val = 120;
        int numLev = 20;
        double lowerBound = 0;
        double upperBound = 100;

        int res = quantize(val,numLev,lowerBound,upperBound);
        System.out.println(res);
        //Assert.assertEquals(11,res);
    }

    @Test
    public void testRand(){
        int upper = 5;
        int lower = 0;

        int score = 100;

        for(int i=0;i<100;i++){
            int r = LUT.randInt(lower,upper);
            if(r>upper || r<lower)
                score--;
        }
        Assert.assertEquals(100,score);
    }

    @Test
    public void testRandAgain(){
        int upper = 5;
        int lower = 0;

        int scoreL = 0;
        int scoreU = 0;

        for(int i=0;i<100;i++){
            int r = LUT.randInt(lower,upper);
            if(r==lower)
                scoreL++;
            if(r==upper)
                scoreU++;
        }
        Assert.assertTrue((scoreL*scoreU)>0);
    }

    @Test
    public void testRandOnceAgain(){
        int upper = 100;
        int lower = 0;

        int score = 0;
        int perc = 34;

        for(int i=0;i<1000;i++){
            int r = LUT.randInt(lower,upper);
            if(r<=perc)
                score++;
        }
        double res = (score/1000.0)*100;
        System.out.println(res);
        Assert.assertEquals(34,res,5);
    }

    @Test
    public void testRandDoub(){
        double upper = 1.0;
        double lower = 0.0;

        int score = 100;

        for(int i=0;i<100;i++){
            double r = LUT.randDoub(lower,upper);
            if(r>upper || r<lower)
                score--;
        }
        Assert.assertEquals(100,score);
    }

    @Test
    public void testRandDoubOnceAgain(){
        double upper = 1.0;
        double lower = 0.0;

        int score = 0;
        double perc = 0.34;

        for(int i=0;i<1000;i++){
            double r = LUT.randDoub(lower,upper);
            if(r<=perc)
                score++;
        }
        double res = (score/1000.0);
        System.out.println(res);
        Assert.assertEquals(perc,res,0.05);
    }

    /**
     * Additional Methods
     */
    public static int quantize(double val, int levels, double lowerBound, double upperBound){
        int res = 0;

        if(val>upperBound)
            val = upperBound;

        double rat = val/(upperBound-lowerBound);
        double temp = rat*(levels-1);
        res = (int) Math.round(temp);

        return res;
    }
}
