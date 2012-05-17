/**
 * (C)2007 ICM Warsaw University
 *
 * package:	pl.edu.icm.yadda.tools.reparser
 * file:	NodeCategory.java
 * date:    2007-06-15
 * svnid:   $Id$
 */
package pl.edu.icm.yadda.tools.reparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class NodeCategory {
    private static final String CHUNK_SEPARATOR_RE = "[\u00a0\\s]";
	
	private String id;
	private String name;
	private String regexp;
	private List<Template> templates;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
	
	public void addTemplate(Template template) {
		if (templates == null)
			templates = new ArrayList<Template>();
		templates.add(template);
	}

	public List<Template> getTemplates() {
		return templates;
	}

	@Override
	public String toString() {
		return id;
	}
	
	public Node match(String text) {
		if (getTemplates() == null || getTemplates().isEmpty()) {
			Node n = new Node();
			n.setName(getName());
			n.setValue(text.trim());
//			log.trace("Matched terminal node " + n.toString());
			return n;
		}

		List<Token> tokens = tokenize(text);

		Node first = null;
		Node prev = null;
		for (Template t : getTemplates()) {
			Node n = t.match(tokens);
			if (n == null)
				continue;
			n.setName(getName());
			n.setValue(text.trim());
			if (prev != null)
				prev.setNextAlternative(n);
			else
				first = n;
			prev = n;
//			log.trace("Template " + t.toString() +" matched non-terminal node " + n.toString());
		}
		
		return first;
	}

	public static List<Token> tokenize(String input) {
		String separated = input.replaceAll("([.,:;\"\'\\/(\\)\\[\\]\u2013-])", " $1 ");
		String[] chunks = separated.trim().split(CHUNK_SEPARATOR_RE + "+");
		
		List<Token> tokens = new ArrayList<Token>(chunks.length);
		
		int position = 0;
		int chunkIndex = 0;
		Token previousToken = null;
		while (position < input.length()) {
			while (position < input.length() && input.substring(position, position + 1).matches(CHUNK_SEPARATOR_RE)) {
				position++;
			}
			if (position == input.length()) {
				break;
			}
			
			assert(input.substring(position).startsWith(chunks[chunkIndex]));
			Token t = new Token();
			t.setContent(chunks[chunkIndex]);
			t.setStart(position);
			t.setEnd(position + chunks[chunkIndex].length() - 1);
			if (previousToken != null) {
				t.setPrevious(previousToken);
				previousToken.setNext(t);
			}
			tokens.add(t);
			
			position += chunks[chunkIndex].length();
			chunkIndex++;
			previousToken = t;
		}
		return tokens;
	}
}
