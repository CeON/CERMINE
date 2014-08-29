package pl.edu.icm.cermine.affparse.model;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

public abstract class Token<L extends Label> {

	protected String text;
	protected int startIndex;
	protected int endIndex;
	protected L label;
	protected List<Feature> features;
	
	public String getText() {
		return text;
	}
	
	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public L getLabel() {
		return label;
	}
	
	public void setLabel(L label) {
		this.label = label;
	}
	
	public Token(String text, int startIndex, int endIndex, L label) {
		this.text = text;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.label = label;
	}
	
	public Token(String text, int startIndex, int endIndex) {
		this(text, startIndex, endIndex, null);
	}
	
	// Ignore label for testing purposes
	public boolean equals(Object obj) {
	       if (!(obj instanceof Token))
	            return false;
	        if (obj == this)
	            return true;

	        @SuppressWarnings("rawtypes")
			Token rhs = (Token) obj;
	        return new EqualsBuilder().
	            // if deriving: appendSuper(super.equals(obj)).
	            append(text, rhs.text).
	            append(startIndex, rhs.startIndex).
	            append(endIndex, rhs.endIndex).
	            isEquals();
	    }
}
