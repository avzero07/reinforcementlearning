package avzero07.reinforcementlearning;

import java.io.File;
import java.io.IOException;

/**
 * Common Interface for both the NeuralNet (NN) and LookUp Table (LUT) Interfaces.
 * @date 18-October-2019
 * @author avzero07 (Akshay V)
 * @email akshay.viswakumar@gmail.com
 * @version 0.0.7
 */

/*
Changelog
---------------
Version 0.0.8
---------------
- Fixed package name: removed leading java
---------------
Version 0.0.7
---------------
- Initial implementation
 */

public interface CommonInterface {
    /**
     * @param x Input Vector for the NN or LUT
     * @return Value 'y' determined by the NN or LUT for the given input 'x'
     */
    public double compOutput(double[] x);

    /**
     * Informs the NN or LUT about what output value should be mapped
     * to the specified input vector. Pertains to training data.
     * @param x Input Vector
     * @param argValue New Value to be learned
     * @return Error in output computed for the given input
     */
    public double train(double[] x, double argValue);

    /**
     * Writes the LUT or NN Weights to a File. Design Headers (first 2 lines)
     * to specify NN or LUT to make load more meaningful.
     * @param argFile Object of File Class
     */
    public void saveWeights(File argFile);

    /**
     * Load the LUT or NN from a file. Check the headers first to determine whether
     * the file is suitable for the NN/LUT that is being used and throw an appropriate
     * error if not.
     * @param argFileName Path to the File
     * @throws IOException To raise issues with File or Path
     */
    public void load(String argFileName) throws IOException;
}