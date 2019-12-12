package avzero07.Final;

import avzero07.lookuptables.State;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class BPLearningLUT_Test {

    @Test
    public void absScaleTest1(){
        double min = -1000;
        double max = 1000;
        double a = -0.8;
        double b = +0.8;

        double testval = -0.11898822251898655;

        double result  = BPLearningLUT.absScale(testval,min,max,a,b);
        System.out.println(result);
        //Assert.assertEquals(result,0.8,0);
    }

    @Test
    public void inpVecTest(){
        State s = new State(0.5,90,120,45,45);
        double op[] = State.constructInpVect(s,5);
        System.out.println(Arrays.toString(op));
    }
}
