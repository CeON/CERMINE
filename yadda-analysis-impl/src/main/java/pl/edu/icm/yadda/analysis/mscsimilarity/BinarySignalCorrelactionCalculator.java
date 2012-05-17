package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.io.*;
import java.util.*;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;

/**
 * Calculates (on-line) Pearson coefficient of two binary signals.
 * 
 * @author tkusm
 * 
 */
public class BinarySignalCorrelactionCalculator {

	/**
	 * Signals length.
	 */
	protected int n;
	/**
	 * Sum over values of first signal.
	 */
	protected int sum_x;
	/**
	 * Sum over values of second signal.
	 */
	protected int sum_y;
	/**
	 * Sum over multiplication of values of both signals.
	 */
	protected int sum_xy;

	public BinarySignalCorrelactionCalculator() {
		reset();
	}

	/**
	 * Start calculating new signals' correlation.
	 */
	public void reset() {
		n = 0;
		sum_x = 0;
		sum_y = 0;
		sum_xy = 0;
	}

	/**
	 * Add next value to both signals.
	 * 
	 * @param x
	 *            first signal's value
	 * @param y
	 *            second signal's value
	 */
	public void nextValue(boolean x, boolean y) {
		insertSingal(x, y, 1);
	}

	/**
	 * Appends to signals constant value of given length.
	 * 
	 * @param x
	 *            what value add to first signal
	 * @param y
	 *            what value add to second signal
	 * @param length
	 *            length of signals to be inserted
	 */
	public void insertSingal(boolean x, boolean y, int length) {
		if (x) {
			sum_x += length;
		}
		if (y) {
			sum_y += length;
		}
		if (x && y) {
			sum_xy += length;
		}
		n += length;
	}

	/**
	 * Calculates Pearson correlation coefficient.
	 * 
	 * @return current value of the correlation coefficient
	 */
	public double getCorrelation() {
		double numerator = n * sum_xy - sum_x * sum_y;
		double denominator = Math.sqrt(n * sum_x - sum_x * sum_x)
				* Math.sqrt(n * sum_y - sum_y * sum_y);
		return numerator / denominator;
	}

	/**
	 * Sets up the signals. Resets previous results.
	 * 
	 * @param positiveX
	 *            moments when first signal was 1
	 * @param positiveY
	 *            moments when second signal was 2
	 * @param length
	 *            total number of moments
	 */
	public void setSignals(List<Integer> positiveX, List<Integer> positiveY,
			int length) {
		n = length;
		sum_x = positiveX.size();
		sum_y = positiveY.size();

		sum_xy = 0;
		Set<Integer> positiveYSet = new HashSet<Integer>(positiveY);
		for (Integer moment_x : positiveX) {
			if (positiveYSet.contains(moment_x)) {
				sum_xy++;
			}
		}
	}

	/**
	 * Generates correlation matrix of all elements.
	 * 
	 * @param positiveIxs map where @key element @value list of moments (moment no) when signal was positive
	 * @param signalLength total length of a signal
	 * @return element-vs-element correlation matrix
	 */
	public static <KEYT> SymmetricTreeMapMatrix<KEYT, Double> generateCorrelationMatrix(
			Map<KEYT, List<Integer>> positiveIxs, int signalLength) {
		SymmetricTreeMapMatrix<KEYT, Double> mx = new SymmetricTreeMapMatrix<KEYT, Double>();
		BinarySignalCorrelactionCalculator calc = new BinarySignalCorrelactionCalculator();
		for (KEYT msc1 : positiveIxs.keySet()) {
			for (KEYT msc2 : positiveIxs.keySet()) {
				calc.setSignals(positiveIxs.get(msc1), positiveIxs.get(msc2),
						signalLength);
				mx.set(msc1, msc2, calc.getCorrelation());
			}
		}
		return mx;
	}

	/**
	 * Prints to stream correlation matrix of all elements.
	 * 
	 * @param osw output stream 
	 * @param positiveIxs map where @key element @value list of moments (moment no) when signal was positive
	 * @param signalLength total length of a signal
	 */
	public static <KEYT> void printCorrelationMatrix(PrintStream osw,
			Map<KEYT, List<Integer>> positiveIxs, int signalLength) {
		printCategories(osw, positiveIxs);
		printCategories(osw, positiveIxs);

		BinarySignalCorrelactionCalculator calc = new BinarySignalCorrelactionCalculator();
		for (KEYT msc1 : positiveIxs.keySet()) {
			for (KEYT msc2 : positiveIxs.keySet()) {
				calc.setSignals(positiveIxs.get(msc1), positiveIxs.get(msc2),
						signalLength);
				osw.print(calc.getCorrelation());
				osw.print(TreeMapMatrix.SUGGESTED_SEPARATOR);
			}
			osw.print("\n");
		}
	}

	private static <KEYT> void printCategories(PrintStream osw,
			Map<KEYT, List<Integer>> positiveIxs) {
		for (KEYT msc : positiveIxs.keySet()) {
			osw.print(msc.toString() + TreeMapMatrix.SUGGESTED_SEPARATOR);
		}
		osw.print("\n");
	}

}
