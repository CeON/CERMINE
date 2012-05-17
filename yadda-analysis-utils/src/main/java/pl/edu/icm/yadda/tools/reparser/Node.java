/**
 * (C)2007 ICM Warsaw University
 *
 * package:	pl.edu.icm.yadda.tools.reparser
 * file:	Node.java
 * date:    2007-06-15
 * svnid:   $Id$
 */
package pl.edu.icm.yadda.tools.reparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class Node {
	private String type;
	private String name;
	private String value;
	private Map<String, List<Node>> fields;
	private Node nextAlternative;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void addField(String key, Node value) {
		if (fields == null)
			fields = new HashMap<String, List<Node>>();
		List<Node> values;
		if (fields.containsKey(key))
			values = fields.get(key);
		else {
			values = new ArrayList<Node>(1);
			fields.put(key, values);
		}
		values.add(value);
	}
	
	public List<Node> getFields(String key) {
		return fields.get(key);
	}
	
	public Node getFirstField(String key) {
		if (fields == null)
			return null;
		List<Node> values = fields.get(key);
		if (values == null)
			return null;
		return values.get(0);
	}

	public Set<String> getFieldNames() {
		return fields.keySet();
	}
	
	@Override
	public String toString() {
		return "Node(" + getName() + ", '" + getValue() + "')";
	}

	public Node getNextAlternative() {
		return nextAlternative;
	}

	public void setNextAlternative(Node nextAlternative) {
		this.nextAlternative = nextAlternative;
	}
}
