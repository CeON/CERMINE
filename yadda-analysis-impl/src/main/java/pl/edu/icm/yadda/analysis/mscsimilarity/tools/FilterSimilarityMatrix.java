package pl.edu.icm.yadda.analysis.mscsimilarity.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;

/**
 * Tool that takes similarity matrix and removes some of the columns and some of
 * the rows according to the predicate. Works on Integer matrices.
 * 
 * @author tkusm
 * 
 */
public class FilterSimilarityMatrix {
    
    /**
     * Predicate that says what keys are allowed.
     * @param key
     * @return
     */
    private static boolean isKeyAllowed(String key) {
        return (key.length() > 3 && !key.endsWith("xx") && !key.endsWith("XX")); 
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("At least single arg expected: path1");
            System.out.println("Second argument is a default matrix value (optional)");
            return;
        }

        String path = args[0];
        Integer defaultMatrixValue = (args.length > 1) ? Integer.parseInt(args[1]) : 0;

        System.out.println("Matrix1 path = " + path);
        System.out.println("Default matrix value = " + defaultMatrixValue);

        try {
            System.out.println("Reading from file " + path);
            FileInputStream f = new FileInputStream(path);
            SymmetricTreeMapMatrix<String, Integer> m = new SymmetricTreeMapMatrix<String, Integer>(
                    TreeMapMatrix.restorePlainText(f, TreeMapMatrix.SUGGESTED_SEPARATOR, defaultMatrixValue));
            f.close();

            int mCols = m.getNumCols();
            int mRows = m.getNumRows();

            System.out.println("   m1Cols = " + mCols);
            System.out.println("   m1Rows = " + mRows);

            if (mCols != mRows) {
                System.out.println("Error: Matrix dimensions must agree!");
                return;
            }

            System.out.println("Filtering " + path);
            SymmetricTreeMapMatrix<String, Integer> m2 = filterMatrix(m);
            System.out.println("    output size = "+m2.getRows().size());
            System.out.println("Overwriting file " + path);
            FileOutputStream fout = new FileOutputStream(path);
            TreeMapMatrix.storePlainText(m2, fout, TreeMapMatrix.SUGGESTED_SEPARATOR, defaultMatrixValue);
            fout.close();

            System.out.println("Done.");
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
    
    public  static <VALT> SymmetricTreeMapMatrix<String, VALT>  filterMatrix(TreeMapMatrix<String, String, VALT> m) {
        SymmetricTreeMapMatrix<String, VALT> m2 = new SymmetricTreeMapMatrix<String, VALT>();
        
        for (String row: m.getRows()) {
            if (!isKeyAllowed(row)) {
                continue;
            } //skip whole row
            
            for (String col: m.getCols(row)) {
                if (!isKeyAllowed(col)) {
                    continue;
                } //skip column
                
                m2.set(row, col, m.get(row, col));
            }
        }
        
        return m2;        
    }
    

}
