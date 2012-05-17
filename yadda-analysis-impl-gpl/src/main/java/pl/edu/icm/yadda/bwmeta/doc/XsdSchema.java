package pl.edu.icm.yadda.bwmeta.doc;

import java.util.SortedMap;
import java.util.TreeMap;

public class XsdSchema {
	protected SortedMap<String, XsdElement> attributeGroups = new TreeMap<String, XsdElement>();
	protected String documentation;
	protected SortedMap<String, XsdElement> elements = new TreeMap<String, XsdElement>();
	protected int revision;
	
	public SortedMap<String, XsdElement> getAttributeGroups() {
		return attributeGroups;
	}

	public String getDocumentation() {
		return documentation;
	}
	
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}
	
	public SortedMap<String, XsdElement> getElements() {
		return elements;
	}

	public int getRevision() {
		return revision;
	}
	
	public void setRevision(int revision) {
		this.revision = revision;
	}
}
