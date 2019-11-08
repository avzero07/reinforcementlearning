package avzero07.lookuptables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * LUT Class that implements the LUTInterface.
 * @date 08-November-2019
 * @author avzero07 (Akshay V)
 * @email "akshay.viswakumar@gmail.com"
 * @version 0.0.5
 */

/*
Changelog
---------------
Version 0.0.5
---------------
- Implemented LUT Constructor
- Implemented initLut() and initLutTrav()
    --  Passes Tests
- Implemented saveWeights()
    --  Passes Tests
- Implemented load()
    -- Passes Tests
---------------
Version 0.0.1
---------------
- Initial Implementation
*/

public class LUT implements LUTInterface {

    int d2eLevels, myEnLevels, enEnLevels;
    double[][][] lookUpTable,lookUpTableTrav;

    /**
     * Constructor Method for LUT. Choosing to implement as an Array
     * Later improvement will upgrade this to a Hash Table
     */
    public LUT(int d2elev, int myEnLev, int enEnLev){
        //this.posXLevels = posXlev;
        //this.posYLevels = posYlev;
        this.d2eLevels = d2elev;
        this.myEnLevels = myEnLev;
        this.enEnLevels = enEnLev;

        this.lookUpTable = new double[this.d2eLevels][this.myEnLevels][this.enEnLevels];
        this.lookUpTableTrav = new double[this.d2eLevels][this.myEnLevels][this.enEnLevels];
        this.initLutTrav(this,0);
    }

    /**
     * Method to fill the LUT
     * @param table : Specify the LUT Object to act on
     * @param type : Specify Fill. 0 or 1. 0 For 0 and 1 for Random
     */
    @Override
    public void initLut(LUT table, int type) {

        double fill;

        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.myEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++) {
                    fill = (type == 0) ? 0 : Math.random();
                    table.lookUpTable[d][men][een] = fill;
                }
    }

    /**
     * Method to fill the Traversal Table
     * @param table : Specify the LUT Object to act on
     * @param type : Specify Fill. 0 or 1. 0 for 0 and 1 for Random
     */
    public void initLutTrav(LUT table, int type) {

        double fill;

        for (int d = 0; d < table.d2eLevels; d++)
            for (int men = 0; men < table.myEnLevels; men++)
                for (int een = 0; een < table.enEnLevels; een++) {
                    fill = (type == 0) ? 0 : Math.random();
                    table.lookUpTableTrav[d][men][een] = fill;
                }
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

    /**
     * Method to save LUT to file
     * @param pathToDirectory specifies the directory to where files will be written
     * @param identifier to name the files appropriately
     */
    @Override
    public void saveWeights(String pathToDirectory, String identifier) throws IOException {

        String op = "";

        for (int d = 0; d < this.d2eLevels; d++)
            for (int men = 0; men < this.enEnLevels; men++) {
                for (int een = 0; een < this.enEnLevels; een++) {
                    op = op + this.lookUpTable[d][men][een] + " ";
                }
                op = op + "\n";
            }

        File f = new File(pathToDirectory);
        f.mkdirs();

        String filepath = pathToDirectory+"/"+identifier+".txt";
        writeToFile(filepath,op);
    }

    @Override
    public void load(String argFileName) throws IOException {
        File f = new File(argFileName);
        Scanner scan = new Scanner(f);

        for (int d = 0; d < this.d2eLevels; d++)
            for (int men = 0; men < this.enEnLevels; men++)
                for (int een = 0; een < this.enEnLevels; een++) {
                    this.lookUpTable[d][men][een] = scan.nextDouble();
                }
        scan.close();
    }

    //Additional Methods
    /**
     *
     */
    public static void writeToFile(String path, String text) throws IOException {
        Charset charSet = Charset.forName("US-ASCII");
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(text,0,text.length());
        writer.close();
    }
}
