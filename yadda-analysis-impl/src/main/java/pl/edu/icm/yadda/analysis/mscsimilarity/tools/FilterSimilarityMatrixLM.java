package pl.edu.icm.yadda.analysis.mscsimilarity.tools;


import java.io.*;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;

/**
 * Tool that takes similarity matrix and removes some of the columns and some of
 * the rows according to the predicate. Low Memory version. Works on any-type matrices.
 * 
 * @author tkusm
 * 
 */
public class FilterSimilarityMatrixLM {
    
    /**
     * Predicate that says what keys are allowed.
     * @param key
     * @return
     */
    private static boolean isKeyAllowed(String key) {
        return (key.length() > 3 && !key.endsWith("xx") && !key.endsWith("XX")); 
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("At least two arg expected: in-path out-path");
            System.out.println("Third argument is a default matrix value (optional)");
            return;
        }

        String path = args[0];
        String outpath = args[1];
        Double defaultMatrixValue = (args.length > 2) ? Double.parseDouble(args[2]) : 0.0;

        System.out.println("Matrix1 in-path = " + path);
        System.out.println("Matrix1 out-path = " + outpath);
        System.out.println("Default matrix value = " + defaultMatrixValue);
        
        try {
            System.out.println("Reading from file " + path+" and writing to file "+outpath);
            BufferedReader br 	= new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            PrintStream brout = new PrintStream(new FileOutputStream(outpath));
            
            String[] rows = copyFilteredHeaderLine(br, brout);            
            String[] cols = copyFilteredHeaderLine(br, brout);                                   
            System.out.println("	rows.length = "+rows.length);
            System.out.println("	cols.length = "+cols.length);
            
            int keptRows = copyFilteredData(br, brout, rows, cols);
            System.out.println("	kept rows = "+keptRows);
                        
            br.close();
            brout.close();

            System.out.println("Done.");
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

	private static int copyFilteredData(BufferedReader br, PrintStream brout,
			String[] rows, String[] cols) throws IOException {
		int keptRows = 0;
		for (int r=0; r<rows.length; ++r) {
			String[] data = TreeMapMatrix.readMatrixDataLine(br, TreeMapMatrix.SUGGESTED_SEPARATOR);
			
			if (!isKeyAllowed(rows[r])) {	//skip illegal rows
				//System.out.println(" Skipping row of no = "+r+" of label = "+rows[r]);
				continue;
			}
					 
			keptRows++;
		    for (int c=0; c<cols.length; ++c) {               
		    	if (!isKeyAllowed(cols[c])) {	//skip illegal cols
		    		//System.out.println(" Skipping col of no = "+c+" of label = "+cols[c]);
		    		continue;
		    	}
		        
		    	TreeMapMatrix.writeMatrixDataElement(brout, data[c], TreeMapMatrix.SUGGESTED_SEPARATOR);
		    }
		    TreeMapMatrix.writeMatrixDataNewLine(brout);
		}
		return keptRows;
	}

	private static String[] copyFilteredHeaderLine(BufferedReader br,
			PrintStream brout) throws IOException {
		String[] data = TreeMapMatrix.readMatrixDataLine(br, TreeMapMatrix.SUGGESTED_SEPARATOR);
		for (int r=0; r<data.length; ++r) {
			if (!isKeyAllowed(data[r])) {	//skip illegal rows
				continue;
			}
		
			TreeMapMatrix.writeMatrixDataElement(brout, data[r], TreeMapMatrix.SUGGESTED_SEPARATOR);
		}
		TreeMapMatrix.writeMatrixDataNewLine(brout);
		return data;
	}    
    

}

