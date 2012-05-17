package pl.edu.icm.yadda.analysis.datastructures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author tkusm
 *
 */
public class SymmetricTreeMapMatrixTest {
    
    
    @Test
    public void symmetryTest() {
        SymmetricTreeMapMatrix<Integer, Integer> m = new SymmetricTreeMapMatrix<Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<=i; ++j) {
                m.set(i, j, 100*i+j);
            }
        }        
        
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                Assert.assertEquals(m.get(i, j), m.get(j, i));
            }
        }
    }
    
    @Test
    public void constructOfTreeMapMatrixTest() {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<=i; ++j) {
                m.set(i, j, 100*i+j);
            }
        }        
        
        SymmetricTreeMapMatrix<Integer, Integer> m2 = new SymmetricTreeMapMatrix<Integer, Integer>(m);
        
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                Assert.assertEquals(m2.get(i, j), m2.get(j, i));
            }
        }
    }
    

    @Test
    public void storeAndRestoreTest() throws IOException, ClassNotFoundException {
        SymmetricTreeMapMatrix<Integer, Integer> m = new SymmetricTreeMapMatrix<Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TreeMapMatrix.store(m, stream);
        
        ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());
        
        SymmetricTreeMapMatrix<Integer, Integer> m2 = (SymmetricTreeMapMatrix<Integer, Integer>) TreeMapMatrix.restore(is);
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                Assert.assertEquals(m.get(i, j), m2.get(i, j));                
            }
        }
    }
    
}
