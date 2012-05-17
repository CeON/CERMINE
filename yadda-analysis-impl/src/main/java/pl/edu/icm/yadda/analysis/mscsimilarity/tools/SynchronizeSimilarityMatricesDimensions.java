package pl.edu.icm.yadda.analysis.mscsimilarity.tools;

import java.io.*;
import java.util.*;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;

/**
 * Tool that allows to synchronize matrices' dimensions.
 * 
 * @author tkusm
 * 
 */
public class SynchronizeSimilarityMatricesDimensions {


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Two args expected: path1 and path2");
            System.out.println("Thrid argument is a value to be inserted (optional)");
            return;
        }
        
        String m1Path = args[0];
        String m2Path = args[1];
        Integer insertValue = (args.length > 2)? Integer.parseInt(args[2]): 0;
        
        System.out.println("Matrix1 path = "+m1Path);
        System.out.println("Matrix2 path = "+m2Path);
        System.out.println("Value to be inserted = "+insertValue);
        
        try {
            System.out.println("Reading from file "+m1Path);
            FileInputStream f1 = new FileInputStream(m1Path);
            TreeMapMatrix<String, String, Integer> m1 = new SymmetricTreeMapMatrix<String, Integer>(TreeMapMatrix.restorePlainText(f1,
                    TreeMapMatrix.SUGGESTED_SEPARATOR, insertValue));
            f1.close();

            System.out.println("Reading from file "+m2Path);
            FileInputStream f2 = new FileInputStream(m2Path);
            TreeMapMatrix<String, String, Integer> m2 = new SymmetricTreeMapMatrix<String, Integer>(TreeMapMatrix.restorePlainText(f2,
                    TreeMapMatrix.SUGGESTED_SEPARATOR, insertValue));
            f2.close();

            int m1Cols = m1.getNumCols();
            int m1Rows = m1.getNumRows();
            int m2Cols = m2.getNumCols();
            int m2Rows = m2.getNumRows();
            
            System.out.println("   m1Cols = "+m1Cols);
            System.out.println("   m1Rows = "+m1Rows);
            System.out.println("   m2Cols = "+m2Cols);
            System.out.println("   m2Rows = "+m2Rows);

            if (m1Cols != m1Rows || m2Cols != m2Rows) {
                System.out.println("Error: Matrix dimensions must agree!");
                return;
            }
            
            if (m1Cols < m2Cols) {
                System.out.println("Scaling "+m1Path+" to "+m2Path);
                extendMatrix(m2, m1, insertValue);
                System.out.println("   m1Cols = "+m1.getNumCols());
                System.out.println("   m1Rows = "+m1.getNumRows());
                System.out.println("Overwriting file "+m1Path);
                FileOutputStream fout = new FileOutputStream(m1Path);
                TreeMapMatrix.storePlainText(m1, fout, TreeMapMatrix.SUGGESTED_SEPARATOR, insertValue);
                fout.close();
            } else {
                System.out.println("Scaling "+m2Path+" to "+m1Path);
                extendMatrix(m1, m2, insertValue);
                System.out.println("   m2Cols = "+m2.getNumCols());
                System.out.println("   m2Rows = "+m2.getNumRows());
                System.out.println("Overwriting file "+m2Path);
                FileOutputStream fout = new FileOutputStream(m2Path);
                TreeMapMatrix.storePlainText(m2, fout, TreeMapMatrix.SUGGESTED_SEPARATOR, insertValue);
                fout.close();
            }
            System.out.println("Done.");
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
    

    /**
     * 
     * @param src to be extended
     * @param dst final size matrix
     */
    public static void extendMatrix(TreeMapMatrix<String, String, Integer> src,
            TreeMapMatrix<String, String, Integer> dst, Integer insertValue) {
        Set<String> dstCols = dst.getCols();
        Set<String> dstRows = dst.getRows();
        Set<String> srcCols = src.getCols();
        Set<String> srcRows = src.getRows();
        
        for (String srcCol: srcCols) {
            if (!dstCols.contains(srcCol)) {
                for (String srcRow: srcRows) {
                    dst.set(srcRow, srcCol, insertValue);
                    break;
                }
            }
        }
        
        for (String srcRow: srcRows) {
            if (!dstRows.contains(srcRow)) {
                for (String srcCol: srcCols) {
                    dst.set(srcRow, srcCol, insertValue);
                    break;
                }
            }
        }
        
    }
}
