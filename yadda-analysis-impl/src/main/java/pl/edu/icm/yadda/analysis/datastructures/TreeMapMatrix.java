package pl.edu.icm.yadda.analysis.datastructures;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Matrix implemented on tree maps.
 * 
 * @author tkusm
 * 
 * @param <K1>
 *            Row key type
 * @param <K2>
 *            Col key type
 * @param <V>
 *            Value type
 */
public class TreeMapMatrix<K1, K2, V> implements AbstractMatrix<K1, K2, V>,
		Serializable {

	private static final long serialVersionUID = 1235431;

	protected Map<K1, Map<K2, V>> values = new TreeMap<K1, Map<K2, V>>();

	@Override
	public V get(K1 row, K2 col, V defaultValue) {
		if (!values.containsKey(row) || !values.get(row).containsKey(col)) {
			return defaultValue;
		}

		return values.get(row).get(col);
	}

	@Override
	public V get(K1 row, K2 col) {
		if (!values.containsKey(row) || !values.get(row).containsKey(col)) {
			throw new AbstractMatrix.PositionNotFound("Position [row=" + row
					+ " col=" + col + "] not found!");
		}

		return values.get(row).get(col);
	}

	@Override
	public void set(K1 row, K2 col, V value) {
		if (!values.containsKey(row)) {
			values.put(row, new TreeMap<K2, V>());
		}
		values.get(row).put(col, value);
	}

	// -------------------------------------------------------------------------

	public static void store(TreeMapMatrix<?, ?, ?> matrix, OutputStream os)
			throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(matrix);
		oos.close();
	}

	public static void storeXML(TreeMapMatrix<?, ?, ?> matrix, OutputStream os) {
		XMLEncoder e = new XMLEncoder(os);
		e.writeObject(matrix);
		e.close();
	}

	/**
	 * Separator that should be used in export files.
	 */
	public final static Character SUGGESTED_SEPARATOR = '\t';

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITING PLAIN-TEXT DATA
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void storePlainText(TreeMapMatrix<?, ?, ?> matrix,
			OutputStream os, Character separator) throws IOException {
		storePlainText(matrix, os, separator, null);
	}

	public static void storePlainText(TreeMapMatrix<?, ?, ?> matrix,
			OutputStream os, Character separator, Object emptyElement)
			throws IOException {
		PrintStream osw = new PrintStream(os);

		// Write rows:
		Object[] rows = matrix.values.keySet().toArray();
		writeMatrixDataLine(osw, rows, separator);

		// Write cols:
		Object[] cols = matrix.getCols().toArray();
		writeMatrixDataLine(osw, cols, separator);

		// Write data:
		for (Object row : rows) {
			for (Object col : cols) {
				storeMatrixElement(osw, matrix, row, col, emptyElement,
						separator);
			}
			writeMatrixDataNewLine(osw);
		}

	}

	private static void storeMatrixElement(PrintStream osw,
			TreeMapMatrix<?, ?, ?> matrix, Object row, Object col,
			Object emptyElement, Character separator) {
		if (matrix.values.get(row).containsKey(col)) {
			Object element = matrix.values.get(row).get(col);
			writeMatrixDataElement(osw, element, separator);
		} else if (emptyElement != null) {
			writeMatrixDataElement(osw, emptyElement, separator);
		} else {
			writeMatrixDataElement(osw, "", separator);
		}
	}

	private static void printStream(PrintStream osw, String txt) {
		osw.print(txt);
		// System.out.print(txt);
	}

	/**
	 * Stores line of matrix values in output stream.
	 * 
	 * @param osw
	 *            output stream
	 * @param dataLine
	 *            data
	 * @param separator
	 *            separator of elements
	 */
	public static void writeMatrixDataLine(PrintStream osw, Object[] dataLine,
			Character separator) {
		for (Object element : dataLine) {
			writeMatrixDataElement(osw, element, separator);
		}
		writeMatrixDataNewLine(osw);
	}

	/**
	 * Stores single value to the stream.
	 * 
	 * @param osw
	 * @param element
	 * @param separator
	 */
	public static void writeMatrixDataElement(PrintStream osw, Object element,
			Character separator) {
		printStream(osw, element.toString() + separator);
	}

	/**
	 * Stores information about next line of data into output stream.
	 * 
	 * @param osw
	 *            output stream
	 */
	public static void writeMatrixDataNewLine(PrintStream osw) {
		printStream(osw, "\n");
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END OF WRITING PLAIN-TEXT DATA
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// READING PLAIN-TEXT DATA
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static TreeMapMatrix<String, String, Integer> restorePlainText(
			InputStream is, Character separator) throws IOException {
		return restorePlainText(is, separator, null);
	}

	public static TreeMapMatrix<String, String, Integer> restorePlainText(
			InputStream is, Character separator, Integer emptyElement)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		TreeMapMatrix<String, String, Integer> result = new TreeMapMatrix<String, String, Integer>();

		String[] rows = readMatrixDataLine(br, separator);
		String[] cols = readMatrixDataLine(br, separator);

		for (int r = 0; r < rows.length; ++r) {
			String[] data = readMatrixDataLine(br, separator);

			for (int c = 0; c < cols.length; ++c) {
				if (isMatrixDataElementProperValue(data, c)) {
					restoreMatrixElement(result, rows[r], cols[c],
							Integer.parseInt(data[c]), emptyElement);
				}
			}
		}

		return result;
	}

	public static TreeMapMatrix<String, String, Double> restorePlainTextDouble(
			InputStream is, Character separator, Double emptyElement)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		TreeMapMatrix<String, String, Double> result = new TreeMapMatrix<String, String, Double>();

		String[] rows = readMatrixDataLine(br, separator);
		String[] cols = readMatrixDataLine(br, separator);

		for (int r = 0; r < rows.length; ++r) {
			String[] data = readMatrixDataLine(br, separator);			

			for (int c = 0; c < cols.length; ++c) {
				if (isMatrixDataElementProperValue(data, c)) {
					restoreMatrixElement(result, rows[r], cols[c],
							Double.parseDouble(data[c]), emptyElement);
				}
			}
		}

		return result;
	}

	private static <VALT> void restoreMatrixElement(
			TreeMapMatrix<String, String, VALT> result, String row, String col,
			VALT retrievedValue, VALT emptyElement) {
		if (emptyElement == null) {
			result.set(row, col, retrievedValue);
		} else if (!retrievedValue.equals(emptyElement)) {
			result.set(row, col, retrievedValue);
		}
	}

	/**
	 * Reads single row of values from input stream.
	 * 
	 * @param br
	 *            input stream
	 * @param separator
	 *            separator
	 * @return row of values read from input stream.
	 * @throws IOException
	 */
	public static String[] readMatrixDataLine(BufferedReader br,
			Character separator) throws IOException {
		String separatorStr = "" + separator;
		String elemsStr = br.readLine();
		//System.out.println("[readMatrixDataLine] elemsStr = "+elemsStr);
		String[] elems = elemsStr.split(separatorStr);
		//System.out.println("[readMatrixDataLine] elems = "+toStr(elems)+" of size = "+elems.length);
		return elems;
	}
		
	/*private static String toStr(Object[] a) {
		StringBuilder b = new StringBuilder();
		for (Object e: a) {
			b.append(e.toString()+" ");
		}
		return b.toString();
	}*/

	/**
	 * Verifies if value in row of read-from-file values is valid (non-empty).
	 * 
	 * @param data
	 *            row of values that were read from file
	 * @param pos
	 *            position in a row
	 * @return true = non-empty value
	 */
	public static boolean isMatrixDataElementProperValue(String[] data, int pos) {
		return data[pos] != null && data[pos].length() > 0;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END OF READING PLAIN-TEXT DATA
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static TreeMapMatrix<?, ?, ?> restore(InputStream is)
			throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		TreeMapMatrix<?, ?, ?> result = (TreeMapMatrix<?, ?, ?>) ois
				.readObject();
		ois.close();
		return result;
	}

	public static TreeMapMatrix<?, ?, ?> restoreXML(InputStream is) {
		XMLDecoder ois = new XMLDecoder(is);
		TreeMapMatrix<?, ?, ?> result = (TreeMapMatrix<?, ?, ?>) ois
				.readObject();
		ois.close();
		return result;
	}

	@Override
	public Set<K1> getRows() {
		Set<K1> rows = new TreeSet<K1>();
		for (K1 row : values.keySet()) {
			rows.add(row);
		}
		return rows;
	}

	@Override
	public Set<K2> getCols() {
		Set<K2> cols = new TreeSet<K2>();
		for (K1 row : values.keySet()) {
			for (K2 col : values.get(row).keySet()) {
				cols.add(col);
			}
		}
		return cols;
	}

	@Override
	public Set<K1> getRows(K2 col) {
		throw new UnsupportedOperationException(
				"getRows(col) is not supported yet for TreeMapMatrix!");
	}

	@Override
	public Set<K2> getCols(K1 row) {
		Set<K2> cols = new TreeSet<K2>();
		for (K2 col : values.get(row).keySet()) {
			cols.add(col);
		}
		return cols;

	}

	/**
	 * Returns all columns for the given row. Operation is faster than
	 * getCols(...) because original set of columns' keys is returned.
	 * 
	 * @param row
	 *            name of the row
	 * @return original set of keys of columns (should not be modified!)
	 */
	public Set<K2> getColsFastButUnsafe(K1 row) {
		return values.get(row).keySet();
	}

	@Override
	public int getNumRows() {
		return values.size();
	}

	@Override
	public int getNumCols() {
		return getCols().size();
	}

	/**
	 * UNSAFE: public just for serialization purposes.
	 * 
	 * @return
	 */
	public Map<K1, Map<K2, V>> getValues() {
		return values;
	}

	/**
	 * UNSAFE: public just for serialization purposes.
	 * 
	 * @param values
	 */
	public void setValues(Map<K1, Map<K2, V>> values) {
		this.values = values;
	}

	public static void main(String[] args) {
		TreeMapMatrix<String, String, Integer> m = new TreeMapMatrix<String, String, Integer>();
		for (int i = 0; i < 100; ++i) {
			for (int j = 0; j < 100; ++j) {
				m.set(Integer.toString(i, 10), Integer.toString(j, 10), i);
			}
		}

		TreeMapMatrix<String, String, Integer> m2 = new TreeMapMatrix<String, String, Integer>();
		for (int i = 0; i < 100; ++i) {
			for (int j = 0; j < i; ++j) {
				m2.set(Integer.toString(i, 10), Integer.toString(j, 10), i);
			}
		}

		FileOutputStream stream;

		try {
			stream = new FileOutputStream("/tmp/plainTextMatrix.txt");
			try {
				TreeMapMatrix.storePlainText(m, stream, '\t');
				stream.flush();
				stream.close();

				FileInputStream ifs = new FileInputStream(
						"/tmp/plainTextMatrix.txt");
				;
				TreeMapMatrix<String, String, Integer> m2r = TreeMapMatrix
						.restorePlainText(ifs, '\t');
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			stream = new FileOutputStream("/tmp/plainTextMatrix2.txt");
			try {
				TreeMapMatrix.storePlainText(m2, stream, '\t');
				stream.flush();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			stream = new FileOutputStream("/tmp/xmlMatrix.xml");
			try {
				stream.flush();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			TreeMapMatrix.storeXML(m, stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
