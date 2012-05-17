/**
 * (C)2007 ICM Warsaw University
 *
 * package:	pl.edu.icm.yadda.tools.reparser
 * file:	Template.java
 * date:    2007-06-15
 * svnid:   $Id$
 */
package pl.edu.icm.yadda.tools.reparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class Template {

	private List<NodeCategory> fields = new ArrayList<NodeCategory>();
	private String type;
	
	private Pattern patternCache;
	
	private void invalidatePatternCache() {
		patternCache = null;
	}
	
	public void addField(NodeCategory nc) {
		if (nc == null)
			return;
		invalidatePatternCache();
		fields.add(nc);
	}

	public List<NodeCategory> getFields() {
		return fields;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		String ret = "";
		boolean first = true;
		for (NodeCategory nc : fields) {
			ret += (first ? "" : " ") + nc.toString();
			first = false;
		}
		return "{" + ret + "}";
	}

	public String getRegexp() {
		String regexp = "";
		for (NodeCategory nc : fields) {
			regexp += nc.getRegexp();
		}
		return "^" + regexp + "$";
	}

	private Pattern getPattern() {
		if (patternCache == null)
			patternCache = Pattern.compile(getRegexp());
		return patternCache;
	}
	
	private String spaces(int count) {
		String ret = "";
		for (int i = 0; i < count; i++)
			ret += " ";
		return ret;
	}
	
	private String findMatchedText(List<Token> tokens, int start, int end) {
		String res = "";
		int curStart = 0;
		for (Token t : tokens) {
			final int curLen = t.getContent().length();
			final int curEnd = curStart + curLen;
			if (start < curEnd && end > curStart) {
				//TODO: concatenate only the relevant (matched) part of the token
				res += t.getContent();
				//TODO: implement without using getNext()/getPrevious()
				if (t.getNext() != null)
					res += spaces(t.getNext().getStart() - t.getEnd() - 1);
			}
			curStart += curLen + 1;
		}
		return res;
	}

	public Node match(List<Token> tokens) {
		String text = "";
		for (Token t : tokens) {
			text += t.getContent() + " ";
		}

		Node n = new Node();
		n.setType(getType());
		
		Pattern p = getPattern();
		Matcher m = p.matcher(text);
		if (!m.matches())
			return null;
		
		int index = 1;
		for (NodeCategory nc : getFields()) {
			if (nc.getName() == null)
				continue;
			String matchedText = findMatchedText(tokens, m.start(index), m.end(index));
			Node nn = nc.match(matchedText);
			if (nn == null)
				return null;
			n.addField(nc.getName(), nn);
			index++;
				
		}
		return n;
	}
}
