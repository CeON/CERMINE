package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.util.*;
import org.junit.*;


/**
 * Test if correlation if calculated properly.
 * 
 * @author tkusm
 *
 */
public class BinarySignalCorrelactionCalculatorTest {

	protected final boolean[] X = {true, false, true, true, false, false};
	protected final boolean[] Y = {true, false, true, false, true, false};
	protected final boolean[] Z = {false, true, false, false, true, true};
	

	
	@Test
	public void testTwoSignalsXY() {
		BinarySignalCorrelactionCalculator calculator = new BinarySignalCorrelactionCalculator();
		updateCalc(calculator, X, Y);
		
		Assert.assertEquals(0.333333, calculator.getCorrelation(), 0.000001);
	}


	
	@Test
	public void testTwoSignalsXZ() {
		BinarySignalCorrelactionCalculator calculator = new BinarySignalCorrelactionCalculator();
		updateCalc(calculator, X, Z);

		
		Assert.assertEquals(-1.0, calculator.getCorrelation(), 0.000001);
	}
	
	@Test
	public void testTwoSignalsYZ() {
		BinarySignalCorrelactionCalculator calculator = new BinarySignalCorrelactionCalculator();
		updateCalc(calculator, Y, Z);

		Assert.assertEquals(-0.333333, calculator.getCorrelation(), 0.000001);
	}
	
	@Test
	public void testCorrYZEqualsZY() {
		BinarySignalCorrelactionCalculator calculatorYZ = new BinarySignalCorrelactionCalculator();
		updateCalc(calculatorYZ, Y, Z);
		
		BinarySignalCorrelactionCalculator calculatorZY = new BinarySignalCorrelactionCalculator();
		updateCalc(calculatorZY, Z, Y);

		Assert.assertEquals(calculatorYZ.getCorrelation(), calculatorZY.getCorrelation(), 0.000001);
	}
	
	@Test
	public void testPreInsertOfTrueFalseSingals() {
		BinarySignalCorrelactionCalculator simpleCalculator = new BinarySignalCorrelactionCalculator();
		BinarySignalCorrelactionCalculator insCalculator = new BinarySignalCorrelactionCalculator();
		updateCalc(simpleCalculator, X, Y);
		updateCalc(insCalculator, X, Y);
		
		final int length = 10;		
		for (int i=0; i<length; ++i) {
			simpleCalculator.nextValue(true, false);
		}
		insCalculator.insertSingal(true, false, length);
		
		Assert.assertEquals(simpleCalculator.getCorrelation(), insCalculator.getCorrelation(), 0.000001);
	}
	
	
	@Test
	public void testPreInsertOfTrueTrueSingals() {
		BinarySignalCorrelactionCalculator simpleCalculator = new BinarySignalCorrelactionCalculator();
		BinarySignalCorrelactionCalculator insCalculator = new BinarySignalCorrelactionCalculator();
		updateCalc(simpleCalculator, X, Y);
		updateCalc(insCalculator, X, Y);
		
		final int length = 10;		
		for (int i=0; i<length; ++i) {
			simpleCalculator.nextValue(true, true);
		}
		insCalculator.insertSingal(true, true, length);
		
		Assert.assertEquals(simpleCalculator.getCorrelation(), insCalculator.getCorrelation(), 0.000001);
	}
	
	@Test
	public void testSetSignalsMethodXY() {
		BinarySignalCorrelactionCalculator simpleCalculator = new BinarySignalCorrelactionCalculator();
		BinarySignalCorrelactionCalculator setCalculator = new BinarySignalCorrelactionCalculator();
		updateCalc(simpleCalculator, X, Y);
		setCalculator.setSignals(identifyPeaks(X), identifyPeaks(Y), X.length);
		
		Assert.assertEquals(simpleCalculator.getCorrelation(), setCalculator.getCorrelation(), 0.000001);
	}
	
	@Test
	public void testSetSignalsMethodXZ() {
		BinarySignalCorrelactionCalculator simpleCalculator = new BinarySignalCorrelactionCalculator();
		BinarySignalCorrelactionCalculator setCalculator = new BinarySignalCorrelactionCalculator();
		updateCalc(simpleCalculator, X, Z);
		setCalculator.setSignals(identifyPeaks(X), identifyPeaks(Z), X.length);
		
		Assert.assertEquals(simpleCalculator.getCorrelation(), setCalculator.getCorrelation(), 0.000001);
	}
	
	@Test
	public void testSetSignalsMethodYZ() {
		BinarySignalCorrelactionCalculator simpleCalculator = new BinarySignalCorrelactionCalculator();
		BinarySignalCorrelactionCalculator setCalculator = new BinarySignalCorrelactionCalculator();
		updateCalc(simpleCalculator, Y, Z);
		setCalculator.setSignals(identifyPeaks(Y), identifyPeaks(Z), Y.length);
		
		Assert.assertEquals(simpleCalculator.getCorrelation(), setCalculator.getCorrelation(), 0.000001);
	}
	
	@Test
	public void testPreInsertOfFalseFalseSingals() {
		BinarySignalCorrelactionCalculator simpleCalculator = new BinarySignalCorrelactionCalculator();
		BinarySignalCorrelactionCalculator insCalculator = new BinarySignalCorrelactionCalculator();
		updateCalc(simpleCalculator, X, Y);
		updateCalc(insCalculator, X, Y);
		
		final int length = 10;		
		for (int i=0; i<length; ++i) {
			simpleCalculator.nextValue(false, false);
		}
		insCalculator.insertSingal(false, false, length);
		
		Assert.assertEquals(simpleCalculator.getCorrelation(), insCalculator.getCorrelation(), 0.000001);
	}
	
	protected List<Integer> identifyPeaks(boolean[] signal) {
		List<Integer> peaks = new ArrayList<Integer>();
		
		for (int moment=0; moment<signal.length; ++moment) {
			if (signal[moment]) {
				peaks.add(moment);
			}
		}
		
		return peaks;
	}
	
	private void updateCalc(BinarySignalCorrelactionCalculator calculator, boolean[] X, boolean[] Y) {
		for (int i=0; i<X.length; ++i) {
			calculator.nextValue(X[i], Y[i]);
		}
	}
}
