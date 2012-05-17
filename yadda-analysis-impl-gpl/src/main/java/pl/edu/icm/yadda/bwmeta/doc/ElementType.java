package pl.edu.icm.yadda.bwmeta.doc;

import java.util.ArrayList;
import java.util.List;

public class ElementType {
	public enum Kind { All, Choice, Sequence, Element, Simple };

	protected int minOccurs = 1;
	protected int maxOccurs = 1;
	protected String name = null;
	protected String type = null;
	protected Kind kind = null;
	protected List<ElementType> children = new ArrayList<ElementType>();

	public int getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	public List<ElementType> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "ElementType [children=" + children + ", kind=" + kind
				+ ", maxOccurs=" + maxOccurs + ", minOccurs=" + minOccurs
				+ ", name=" + name + ", type=" + type + "]";
	}
}
