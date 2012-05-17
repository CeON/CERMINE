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
public class TreeMapMatrixTest {

    @Test
    public void createMatrixTest() {
        new TreeMapMatrix<String, String, Integer>();        
    }
    
    @Test
    public void fillMatrixTest() {
        TreeMapMatrix<String, String, Integer> m = new TreeMapMatrix<String, String, Integer>();
        m.set("a", "b", 1);
        m.set("a", "c", 1);
        m.set("a", "c", 1);
        m.set("b", "e", 1);
        m.set("b", "f", 1);
        m.set("b", "g", 1);
        m.set("c", "m", 1);
        m.set("c", "p", 1);
    }
    
    @Test
    public void fillAndCheckValuesTest() {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();

        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) { 
                Integer expected = i*j+j;
                Assert.assertEquals(expected, m.get(i, j));                
            }
        }
    }
    
    @Test(expected=AbstractMatrix.PositionNotFound.class)
    public void valueNotFoundInRowTest() {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        m.get(101, 0);
    }
    
    @Test(expected=AbstractMatrix.PositionNotFound.class)
    public void valueNotFoundInColTest() {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        m.get(0, 101);
    }
    
    @Test
    public void getDefaultValueTest() {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        Assert.assertEquals(new Integer(-1), m.get(101, 101, -1));
    }
    
    @Test
    public void storeMatrixTest() throws IOException {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TreeMapMatrix.store(m, os);
    }
    
    @Test
    public void storeXMLMatrixTest() throws IOException {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TreeMapMatrix.storeXML(m, os);
    }
    
    @Test
    public void storePlainTextMatrixTest() throws IOException {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<i; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TreeMapMatrix.storePlainText(m, os, '\t');
    }
    
    @Test
    public void storeAndRestoreMatrixTest() throws IOException, ClassNotFoundException {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TreeMapMatrix.store(m, stream);
        
        ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());
        
        TreeMapMatrix<Integer, Integer, Integer> m2 = (TreeMapMatrix<Integer, Integer, Integer>) TreeMapMatrix.restore(is);
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                Assert.assertEquals(m.get(i, j), m2.get(i, j));                
            }
        }
       
    }
    

    @Test
    public void storeAndRestoreXMLMatrixTest() throws IOException, ClassNotFoundException {
        TreeMapMatrix<Integer, Integer, Integer> m = new TreeMapMatrix<Integer, Integer, Integer>();
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                m.set(i, j, i*j+j);
            }
        }
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TreeMapMatrix.storeXML(m, stream);
        
        ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());
        
        TreeMapMatrix<Integer, Integer, Integer> m2 = (TreeMapMatrix<Integer, Integer, Integer>) TreeMapMatrix.restoreXML(is);
        for (int i=0; i<100; ++i) {
            for (int j=0; j<100; ++j) {
                Assert.assertEquals(m.get(i, j), m2.get(i, j));                
            }
        }
       
    }
    
    @Test
    public void storeAndRestorePlainTextMatrixTest() throws IOException, ClassNotFoundException {
        TreeMapMatrix<String, String, Integer> m = new TreeMapMatrix<String, String, Integer>();
        for (int i=0; i<10; ++i) {
            for (int j=0; j<10; ++j) {
                String row = Integer.toString(i);
                String col = Integer.toString(j);
                m.set(row, col, i*j+j);
            }
        }
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final char separator = '\t';
        TreeMapMatrix.storePlainText(m, stream, separator);           
                
        //System.out.println(stream.toString());
        ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());               
        TreeMapMatrix<String, String, Integer> m2 = TreeMapMatrix.restorePlainText(is, separator);
        
        for (int i=0; i<10; ++i) {
            for (int j=0; j<10; ++j) {
                String row = Integer.toString(i);
                String col = Integer.toString(j);
                Assert.assertEquals(m.get(row, col), m2.get(row, col));                
            }
        }
       
    }
    
    @Test
    public void storeAndRestorePlainTextSparseMatrixTest() throws IOException, ClassNotFoundException {
        TreeMapMatrix<String, String, Integer> m = new TreeMapMatrix<String, String, Integer>();
        for (int i=0; i<10; ++i) {
            for (int j=i; j<10; ++j) {
                String row = "r"+Integer.toString(i);
                String col = "c"+Integer.toString(j);
                m.set(row, col, i*j+j);
            }
        }
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final char separator = '\t';
        TreeMapMatrix.storePlainText(m, stream, separator);           
                
        //System.out.println(stream.toString());
        ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());               
        TreeMapMatrix<String, String, Integer> m2 = TreeMapMatrix.restorePlainText(is, separator);
        
        for (int i=0; i<10; ++i) {
            for (int j=i; j<10; ++j) {
                String row = "r"+Integer.toString(i);
                String col = "c"+Integer.toString(j);
                Assert.assertEquals(m.get(row, col), m2.get(row, col));                
            }
        }
       
    }
}
