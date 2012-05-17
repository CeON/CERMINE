package pl.edu.icm.yadda.analysis.jrlsimilarity.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Help-class with tools provided as static methods
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalTitleTools {
	
	protected final static Logger log = LoggerFactory.getLogger(JournalTitleTools.class);


	/** Helper method for {@link #getAcronymedTitle(String)} method */
    private static boolean isLetter(char ch)
    {
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
            return true;
 
        else
            return false;
    }
	
	/**
	 * Return acronymed version of title given (actually all strings given)
	 * @param title
	 * @return
	 */
	public static String getAcronymedTitle(String title){

		StringBuilder acro = new StringBuilder();
	        String[] words = title.split(" "); /* get the individual words (that were separated by spaces) */
	        char currentChar;
	 
	        for (String string : words)
	        {
	        	if(string.length() > 0)
	        		currentChar = string.charAt(0);
	        	else{
	        		log.info("One of words of title is empty, it's ommited:" + title);
	        		continue;
	        	}
	 
	            if (isLetter(currentChar))
	                acro.append(string.charAt(0)); /* set each of the beginning letters to the acro variable */
	        }
	        return acro.toString();
	}
}
