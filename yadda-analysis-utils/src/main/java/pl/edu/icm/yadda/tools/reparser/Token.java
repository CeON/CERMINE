/**
 * (C)2007 ICM Warsaw University
 *
 * package:	pl.edu.icm.yadda.tools.reparser
 * file:	Token.java
 * date:    2007-06-06
 * svnid:   $Id$
 */
package pl.edu.icm.yadda.tools.reparser;

/**
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class Token {
	
	private String content;
	private int start = -1;
	private int end = -1;
	
	private Token previous;
	private Token next;
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}

	public Token getNext() {
		return next;
	}

	public void setNext(Token next) {
		this.next = next;
	}

	public Token getPrevious() {
		return previous;
	}

	public void setPrevious(Token previous) {
		this.previous = previous;
	}
	
	@Override
	public String toString() {
		return content;
	}
}
