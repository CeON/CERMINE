package pl.edu.icm.cermine.affparse.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

public abstract class Token<L extends Label> {

	protected String text;
	protected int startIndex;
	protected int endIndex;
	protected L label;
	protected List<String> features;

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
	
	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}
	
    public void addFeature(String feature) {
    	features.add(feature);
    }
	
	public Token(String text, int startIndex, int endIndex, L label) {
		this.text = text;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.label = label;
		this.features = new ArrayList<String>();
	}
	
	public Token(String text, int startIndex, int endIndex) {
		this(text, startIndex, endIndex, null);
	}

	public Token(String text) {
		this(text, 0, 0);
	}
	
	public Token() {
		this("");
	}
	
	public abstract String getValidLabel();
	
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
	
	// Used for dictionary lookups
	@SuppressWarnings("rawtypes")
	public static <T extends Token> boolean sequenceEquals(List<T> lhs, List<T> rhs,
			boolean useLowerCase) {
		if (lhs.size() != rhs.size()) {
			return false;
		}
		
		for (int i = 0; i < lhs.size(); i++) {
			String lhsString = lhs.get(i).getText();
			String rhsString = rhs.get(i).getText();
			if (useLowerCase) {
				lhsString = lhsString.toLowerCase();
				rhsString = rhsString.toLowerCase();
			}
			if (!lhsString.equals(rhsString)) {
				return false;
			}
		}
		
		return true;
	}
}
