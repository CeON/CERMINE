package pl.edu.icm.yadda.analysis.relations.pj.auxil;
	  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	  
	  public final class Nysiis{
	  
		private static final Logger log = LoggerFactory.getLogger(Nysiis.class);
		  
	    /** Enable/disable internal debugging. */
	    private boolean debug = false;
	  
	    /** The name to be encoded. */
	    private StringBuffer word  = null;
	  
	    /**
	     * Static version of encode.  This method was originaly created to allow this
	     * encoder to be used as a Java Stored Procedure in Oracle.
	     * @param word the data to encode.
	     * @return the encoded string.
	     */
	    public static String sencode( String word ) {
	      Nysiis ny = new Nysiis();
	      return ny.encode(word);
	      
	    }
	  
	    /**
	     * Encode the given string using the Nysiis phonetic encoding algorithm.
	     * @param  String originalWord
	     * @return String - the encoded word
	     */
	    public String encode( String originalWord ) {
	  
	        try {
				if( originalWord != null &&
 	  originalWord.length() > 0 ) {
 	  word = new StringBuffer( originalWord.toUpperCase() );
				} else {
 	  return "";
				}
     char first;
  
     /**
      * @before strip any trailing S or Zs
      * @after 'Z' letters stays
      * @author pdendek
      * @date 2010-11-30 
      */
     while(word.toString().endsWith("S")) {
				word.deleteCharAt( word.length() - 1 );
     }
  
     replaceFront( "MAC", "MC" );
     replaceFront( "PF",  "F" );
     replaceEnd(   "IX",  "IC" );
     replaceEnd(   "EX",  "EC" );
  
     /**
      * @before no "TZ"/"Z" action
      * @after additional replacements
      * @author pdendek
      * @date 2010-11-30 
      */
     replaceEnd(   "TZ",  "C" );
     replaceEnd(   "Z",  "C" );
     
     replaceEnd(   "YE",  "Y" );
     replaceEnd(   "EE",  "Y" );
     replaceEnd(   "IE",  "Y" );
  
     replaceEnd(   "DT",  "D" );
     replaceEnd(   "RT",  "D" );
     replaceEnd(   "RD",  "D" );
  
  
     replaceEnd(   "NT",  "N" );
     replaceEnd(   "ND",  "N" );
  
     // .EV => .EF
     replaceAll(   "EV", "EF", 1 );
  
     first = word.charAt(0);
  
  
     // replace all vowels with 'A'
     // word = replaceAll(   word, "A",  "A" );
     replaceAll(   "E",  "A" );
     replaceAll(   "I",  "A" );
     replaceAll(   "O",  "A" );
     replaceAll(   "U",  "A" );
     
     /**
      * @before no "CH" action
      * @after additional replacements
      * @author pdendek
      * @date 2010-11-30 
      */
     replaceAll(   "CH",  "H" );
     replaceAll(   "SH",  "S" );
     
     // remove any 'W' that follows a vowel
     replaceAll(   "AW", "A" );
  
     replaceAll(   "GHT", "GT" );
     replaceAll(   "DG", "G" );
     replaceAll(   "PH", "F" );
  
     replaceAll(   "AH", "A", 1 );
     replaceAll(   "HA", "A", 1 );
  
     replaceAll(   "KN", "N" );
     replaceAll(   "K", "C" );
  
     replaceAll(   "M", "N", 1 );
     replaceAll(   "Q", "G", 1 );
  
     replaceAll(   "SH",  "S" );
     replaceAll(   "SCH", "S" );
  
     replaceAll(   "YW",  "Y" );
  
     replaceAll(   "Y",  "A", 1, word.length() - 2 );
  
     replaceAll(   "WR",  "R" );
  
     replaceAll(   "Z",  "S", 1 );
  
     replaceEnd(   "AY",  "Y" );
  
     while(word.toString().endsWith("A")) {
				word.deleteCharAt( word.length() - 1 );
     }
  
     reduceDuplicates();
     
     /**
      * @before no word.len()>0
      * @author pdendek
      * @date 2010-11-30 
      */
     if(word.length()>0 &&
				(  'A' == first
				|| 'E' == first
				|| 'I' == first
				|| 'O' == first
				|| 'U' == first 
				)){
				  	word.deleteCharAt(0);
				  	word.insert(0,first);
				}
   
				return word.toString();
			} catch (RuntimeException e) {
				log.error("A catched exception in Nysiis.encode("+originalWord+")");
				log.error("Returning oryginal surname\n");
				e.printStackTrace();
				return originalWord;
			}
	    }
	  
	    /**
	     * Traverse the string reducing duplicated characters.
	     */
	    private void reduceDuplicates() {
	      char lastChar;
	      StringBuffer newWord = new StringBuffer();
	  
	      if(0 == word.length()) {
	        return;
	      }
	  
	      lastChar = word.charAt(0);
	      newWord.append(lastChar);
	      for(int i = 1; i < word.length(); ++i) {
	        if(lastChar != word.charAt(i)) {
	          newWord.append(word.charAt(i));
	        }
	        lastChar = word.charAt(i);
	      }
	  
	      log("reduceDuplicates: " + word);
	  
	      word = newWord;
	    }
	  
	    /**
	     * Replace all occurances of the given pattern in the string to be encoded
	     * with the given replacement.
	     * @param find the sequence to locate
	     * @param repl the string to replace it with
	     */
	    private void replaceAll( String find, 
	                             String repl ) {
	      replaceAll(find,repl,0,-1);
	    }
	  
	    /**
	     * Replace all occurances of the given pattern in the string to be encoded
	     * with the given replacement, beginning at the given staring position.
	     * @param find the sequence to locate
	     * @param repl the string to replace it with
	     * @param startPos the position to begin at
	     */
	    private void replaceAll( String find, 
	                             String repl,
	                             int startPos ) {
	      replaceAll(find,repl,startPos,-1);
	    }
	  
	    /**
	     * Replace all occurances of the given pattern in the string to be encoded
	     * with the given replacement, beginning at the given staring position up to
	     * the given end position.
	     * @param find the sequence to locate
	     * @param repl the string to replace it with
	     * @param startPos the position to begin at
	     * @param endPos the position to stop at
	     */
	    private void replaceAll( String find, 
	                             String repl,
	                             int startPos,
	                             int endPos ) {
	      int pos = word.toString().indexOf(find,startPos);
	  
	      /*
	      log("Nysiis.replaceAll(): "
	        + "pos: "      + pos      + " "
	        + "word: "     + word     + " "
	        + "find: "     + find     + " "
	        + "repl: "     + repl     + " "
	        + "startPos: " + startPos + " "
	        + "endPos: "   + endPos   + " "
	      );
	      */
	  
	      if(-1 == endPos) {
	        endPos = word.length() - 1;
	      }
	  
	      while(-1 != pos) {
	        if(-1 != endPos && pos > endPos) {
	          log("stopping pos > endPos: " + pos + ":" + endPos);
	          break;
	        }
	        // log("word[" + word.length() + "]: " + word);
	        // log("deleting at: " + pos + ", " + (find.length() - 1));
	  
	        word.delete( pos, pos + find.length() );
	        // log("del[" + word.length() + "]:  " + word);
	  
	        word.insert( pos, repl );
	        // log("ins[" + word.length() + "]:  " + word);
	  
	        pos = word.toString().indexOf(find);
	        // log("new pos[" + word.length() + "]: " + pos);
	        log("replaceAll[" + find + "," + repl + "]: " + word);
	      }
	      
	    }
	  
	    /**
	     * If the encoded string begins with the given find string, replace it.
	     * @param find the prefix to test for
	     * @param repl the replacement to substitue
	     */
	    private void replaceFront( String find, 
	                               String repl ) {
	      if(word.toString().startsWith(find)) {
	        word.delete( 0, find.length() );
	        word.insert( 0, repl );
	        log("replaceFront[" + find + "]: " + word);
	      }
	    }
	  
	    /**
	     * If the encoded string ends with the given find string, replace it.
	     * @param find the suffix to test for
	     * @param repl the replacement to substitue
	     */
	    private void replaceEnd( String find, 
	                             String repl ) {
	      if(word.toString().endsWith(find)) {
	        word.delete( word.length() - find.length(), word.length() );
	        word.append(repl);
	        log("replaceEnd[" + find + "]: " + word);
	      }
	    }
	  
	    /**
	     * Logging statement controlled by the debug member.
	     * @param msg the message to optionaly log.
	     */
	    private void log( String msg ) {
	      if(!debug) { return; }
	      log.error(msg);
	      System.out.flush();
	    }
	  
	    /**
	     * Check if the two strings encode to the same primary or alternate encodings
	     * using the Nysiis algorithm.
	     * @param s1
	     * @param s2
	     * @return true/false
	     */
	    public static boolean isEncodeEqual( String s1, String s2 ) {
	      return sencode( s1 ).equals( sencode( s2 ) );
	    }
	    
	  }
