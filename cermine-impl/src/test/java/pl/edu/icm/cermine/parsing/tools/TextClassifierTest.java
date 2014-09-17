package pl.edu.icm.cermine.parsing.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TextClassifierTest {

	@Test
	public void testIsWord() {
		String word = "BabamaKota";
		String notWord1 = "Baba ma";
		String notWord2 = "Baba123";
		assertTrue(TextClassifier.isWord(word));
		assertFalse(TextClassifier.isWord(notWord1));
		assertFalse(TextClassifier.isWord(notWord2));
	}

	@Test
	public void testIsNumber() {
		String number = "53253325";
		String notNumber1 = "123 ";
		String notNumber2 = "123b";
		assertTrue(TextClassifier.isNumber(number));
		assertFalse(TextClassifier.isNumber(notNumber1));
		assertFalse(TextClassifier.isNumber(notNumber2));
	}

	@Test
	public void testIsOnlyFirstUpperCase() {
		String firstUpperCase1 = "M";
		String firstUpperCase2 = "Mama";
		String notUpperCase = "aM";
		String moreUpperCase = "MM";
		String notWord = "Wombat1";
		assertTrue(TextClassifier.isOnlyFirstUpperCase(firstUpperCase1));
		assertTrue(TextClassifier.isOnlyFirstUpperCase(firstUpperCase2));
		assertFalse(TextClassifier.isOnlyFirstUpperCase(notUpperCase));
		assertFalse(TextClassifier.isOnlyFirstUpperCase(moreUpperCase));
		assertFalse(TextClassifier.isOnlyFirstUpperCase(notWord));
	}

	@Test
	public void testAllUpperCase() {
		String firstUpperCase1 = "M";
		String firstUpperCase2 = "Mama";
		String notUpperCase = "aM";
		String moreUpperCase = "MM";
		String notWord = "W1";
		assertTrue(TextClassifier.isAllUpperCase(firstUpperCase1));
		assertFalse(TextClassifier.isAllUpperCase(firstUpperCase2));
		assertFalse(TextClassifier.isAllUpperCase(notUpperCase));
		assertTrue(TextClassifier.isAllUpperCase(moreUpperCase));
		assertFalse(TextClassifier.isAllUpperCase(notWord));
	}

	@Test
	public void testIsSeparator() {
		String separator1 = ".";
		String separator2 = ",";
		String separator3 = ";";
		String notSeparator = ":";
		String twoSeparators = "..";
		assertTrue(TextClassifier.isSeparator(separator1));
		assertTrue(TextClassifier.isSeparator(separator2));
		assertTrue(TextClassifier.isSeparator(separator3));
		assertFalse(TextClassifier.isSeparator(notSeparator));
		assertFalse(TextClassifier.isSeparator(twoSeparators));
	}
	
	@Test
	public void testIsNonAlphanumSep() {
		String alpha = "a";
		String num = "1";
		String sep = ",";
		String other = "(";
		assertFalse(TextClassifier.isNonAlphanumSep(alpha));
		assertFalse(TextClassifier.isNonAlphanumSep(num));
		assertFalse(TextClassifier.isNonAlphanumSep(sep));
		assertTrue(TextClassifier.isNonAlphanumSep(other));
	}
	
}
