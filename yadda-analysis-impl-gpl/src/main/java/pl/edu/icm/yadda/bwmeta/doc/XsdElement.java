package pl.edu.icm.yadda.bwmeta.doc;

import java.util.ArrayList;
import java.util.List;

public class XsdElement {
	protected String name;
	protected String documentation;
	protected ElementType type;
	protected List<XsdAttribute> attributes = new ArrayList<XsdAttribute>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}

	public List<XsdAttribute> getAttributes() {
		return attributes;
	}
}
