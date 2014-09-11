package pl.edu.icm.cermine.parsing.tools;

import java.util.regex.Pattern;

/**
 * Utility class used for text classification
 * 
 * @author Bartosz Tarnawski
 */
public class TextClassifier {
    
	private static final Pattern notWordPattern = Pattern.compile("[^a-zA-Z]");
	private static final Pattern notNumberPattern = Pattern.compile("[^0-9]");
	private static final Pattern notUpperCasePattern = Pattern.compile("[^A-Z]");
	private static final Pattern notLowerCasePattern = Pattern.compile("[^a-z]");
	private static final Pattern notAlphanumPattern = Pattern.compile("[^a-zA-Z0-9]");
    
	/**
	 * @param text
	 * @return whether the text is a single word
	 */
	public static boolean isWord(String text) {
		return !notWordPattern.matcher(text).find();
	}

	/**
	 * @param text
	 * @return whether the text is a single number
	 */
	public static boolean isNumber(String text) {
		return !notNumberPattern.matcher(text).find();
	}
	
	/**
	 * @param text
	 * @return whether the text is a single word with the first letter in upper case and all the
	 * rest in lower case
	 */
	public static boolean isOnlyFirstUpperCase(String text) {
		boolean firstUpperCase = !notUpperCasePattern.matcher(text.substring(0, 1)).find();
		boolean restLowerCase = !notLowerCasePattern.matcher(text.substring(1)).find();
		return firstUpperCase && restLowerCase;
	}
	
	/**
	 * @param text
	 * @return whether the text is a single word with all letters in upper case
	 */
	public static boolean isAllUpperCase(String text) {
		return !notUpperCasePattern.matcher(text).find();
	}
	
	/**
	 * @param text
	 * @return whether the text is a single word with all letters in upper case
	 */
	public static boolean isAllLowerCase(String text) {
		return !notLowerCasePattern.matcher(text).find();
	}	
	/**
	 * @param text
	 * @return whether the word is a commonly used separator ("." "," ";")
	 */
	public static boolean isSeparator(String text) {
		return text.equals(".") || text.equals(",") || text.equals(";");
	}
	
	/**
	 * @param text
	 * @return whether the word is not alphanumeric and is not a separator
	 */
	public static boolean isNonAlphanumSep(String text) {
		return notAlphanumPattern.matcher(text).find() && !isSeparator(text);
	}
	
}
