package avzero07.lookuptables;

import java.io.IOException;

/**
 * LUT Interface that extends the CommonInterface.
 * @date 08-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.1
 */

/*
Changelog
---------------
Version 0.0.1
---------------
- Initial Implementation
*/

public class LUT implements LUTInterface {
    @Override
    public void initLut() {

    }

    @Override
    public int indexFor(int[] x) {
        return 0;
    }

    @Override
    public void compOutput(double[] x) {

    }

    @Override
    public double train(double[] x, double argValue) {
        return 0;
    }

    @Override
    public void saveWeights(String pathToDirectory, String identifier) {

    }

    @Override
    public void load(String argFileName) throws IOException {

    }
}
