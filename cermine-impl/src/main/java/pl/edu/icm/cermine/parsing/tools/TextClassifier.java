package pl.edu.icm.cermine.parsing.tools;

import java.util.regex.Pattern;

public class TextClassifier {
    
	private static final Pattern notWordPattern = Pattern.compile("[^a-zA-Z]");
	private static final Pattern notNumberPattern = Pattern.compile("[^0-9]");
	private static final Pattern notUpperCasePattern = Pattern.compile("[^A-Z]");
	private static final Pattern notLowerCasePattern = Pattern.compile("[^a-z]");
	private static final Pattern notAlphanumPattern = Pattern.compile("[^a-zA-Z0-9]");
    
	public static boolean isWord(String text) {
		return !notWordPattern.matcher(text).find();
	}

	public static boolean isNumber(String text) {
		return !notNumberPattern.matcher(text).find();
	}
	
	public static boolean isOnlyFirstUpperCase(String text) {
		boolean firstUpperCase = !notUpperCasePattern.matcher(text.substring(0, 1)).find();
		boolean restLowerCase = !notLowerCasePattern.matcher(text.substring(1)).find();
		return firstUpperCase && restLowerCase;
	}
	
	public static boolean isAllUpperCase(String text) {
		return !notUpperCasePattern.matcher(text).find();
	}
	
	public static boolean isSeparator(String text) {
		return text.equals(".") || text.equals(",") || text.equals(";");
	}
	
	public static boolean isNonAlphanumSep(String text) {
		return notAlphanumPattern.matcher(text).find() && !isSeparator(text);
	}
	
}
