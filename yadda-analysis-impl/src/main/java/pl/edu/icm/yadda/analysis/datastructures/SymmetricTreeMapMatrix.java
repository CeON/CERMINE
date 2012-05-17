package pl.edu.icm.yadda.analysis.datastructures;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

/**
 * Matrix based on tree maps that assures symmetry (M[row,col]==M[col,row]).
 * 
 * @author tkusm
 * 
 * @param <KEYT>
 *            Row/col key type
 * @param <VALT>
 *            Values type
 */
public class SymmetricTreeMapMatrix<KEYT, VALT> extends TreeMapMatrix<KEYT, KEYT, VALT> {

    private static final long serialVersionUID = 3874481594584158716L;

    public SymmetricTreeMapMatrix() {
    }

    public SymmetricTreeMapMatrix(TreeMapMatrix<KEYT, KEYT, VALT> src) {
        super();
        //System.out.println("[SymmetricTreeMapMatrix] src.values.size() = " + src.values.size());
        for (KEYT row : src.values.keySet()) {
            for (KEYT col : src.values.get(row).keySet()) {
                this.set(row, col, src.values.get(row).get(col));
            }
        }
    }

    @Override
    public VALT get(KEYT row, KEYT col, VALT defaultValue) {
        return super.get(row, col, defaultValue);
    }

    @Override
    public VALT get(KEYT row, KEYT col) {
        return super.get(row, col);
    }

    @Override
    public void set(KEYT row, KEYT col, VALT value) {
        super.set(row, col, value);
        super.set(col, row, value);
    }
    
    @Override
    public int getNumCols() {
        return super.getNumRows(); //symmetric!
    }

    public static void main(String[] args) {
        {
            SymmetricTreeMapMatrix<Integer, Integer> m = new SymmetricTreeMapMatrix<Integer, Integer>();
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j <= i; ++j) {
                    m.set(i, j, 10 * i + j);
                }
            }

            print(m, System.out);
        }
        {
            TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j <= i; ++j) {
                    m.set(i, j, 100 * i + j);
                }
            }

            SymmetricTreeMapMatrix<Integer, Integer> m2 = new SymmetricTreeMapMatrix<Integer, Integer>(m);
            print(m2, System.out);
            
        }
    }

    public static void print(TreeMapMatrix<Integer, Integer, Integer> m, PrintStream out) {
        for (Integer row : m.values.keySet()) {
            out.print(row + ":\t");
            for (Integer col : m.values.get(row).keySet()) {
                System.out.print(col + ":" + m.values.get(row).get(col) + "\t");
            }
            out.println();
        }
    }



}
