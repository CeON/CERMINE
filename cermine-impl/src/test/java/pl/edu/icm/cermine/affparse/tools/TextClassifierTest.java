package pl.edu.icm.cermine.affparse.tools;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextClassifierTest {

	@Test
	public void testIsWord() {
		String word = "BabamaKota";
		String notWord1 = "Baba ma";
		String notWord2 = "Baba123";
		assertEquals(true, TextClassifier.isWord(word));
		assertEquals(false, TextClassifier.isWord(notWord1));
		assertEquals(false, TextClassifier.isWord(notWord2));
	}

	@Test
	public void testIsNumber() {
		String number = "53253325";
		String notNumber1 = "123 ";
		String notNumber2 = "123b";
		assertEquals(true, TextClassifier.isNumber(number));
		assertEquals(false, TextClassifier.isNumber(notNumber1));
		assertEquals(false, TextClassifier.isNumber(notNumber2));
	}

	@Test
	public void testIsOnlyFirstUpperCase() {
		String firstUpperCase1 = "M";
		String firstUpperCase2 = "Mama";
		String notUpperCase = "aM";
		String moreUpperCase = "MM";
		String notWord = "Wombat1";
		assertEquals(true, TextClassifier.isOnlyFirstUpperCase(firstUpperCase1));
		assertEquals(true, TextClassifier.isOnlyFirstUpperCase(firstUpperCase2));
		assertEquals(false, TextClassifier.isOnlyFirstUpperCase(notUpperCase));
		assertEquals(false, TextClassifier.isOnlyFirstUpperCase(moreUpperCase));
		assertEquals(false, TextClassifier.isOnlyFirstUpperCase(notWord));
	}

	@Test
	public void testAllUpperCase() {
		String firstUpperCase1 = "M";
		String firstUpperCase2 = "Mama";
		String notUpperCase = "aM";
		String moreUpperCase = "MM";
		String notWord = "W1";
		assertEquals(true, TextClassifier.isAllUpperCase(firstUpperCase1));
		assertEquals(false, TextClassifier.isAllUpperCase(firstUpperCase2));
		assertEquals(false, TextClassifier.isAllUpperCase(notUpperCase));
		assertEquals(true, TextClassifier.isAllUpperCase(moreUpperCase));
		assertEquals(false, TextClassifier.isAllUpperCase(notWord));
	}

	@Test
	public void testIsSeparator() {
		String separator1 = ".";
		String separator2 = ",";
		String separator3 = ";";
		String notSeparator = ":";
		String twoSeparators = "..";
		assertEquals(true, TextClassifier.isSeparator(separator1));
		assertEquals(true, TextClassifier.isSeparator(separator2));
		assertEquals(true, TextClassifier.isSeparator(separator3));
		assertEquals(false, TextClassifier.isSeparator(notSeparator));
		assertEquals(false, TextClassifier.isSeparator(twoSeparators));
	}
	
	@Test
	public void testIsNonAlphanumSep() {
		String alpha = "a";
		String num = "1";
		String sep = ",";
		String other = "(";
		assertEquals(false, TextClassifier.isNonAlphanumSep(alpha));
		assertEquals(false, TextClassifier.isNonAlphanumSep(num));
		assertEquals(false, TextClassifier.isNonAlphanumSep(sep));
		assertEquals(true, TextClassifier.isNonAlphanumSep(other));
	}
	
}
