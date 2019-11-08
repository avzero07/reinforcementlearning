package avzero07.lookuptables;

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

public interface LUTInterface extends avzero07.reinforcementlearning.CommonInterface {

    /**
     * Used to initialize the LUT
     */
    public void initLut();

    /**
     * Translates the vector used for lookup (state-action) into
     * an ordinal that indexes the LUT
     * @param x State Action Vector used to index the LUT
     * @return The index that the vector maps to
     */
    public int indexFor(int[] x);
}
