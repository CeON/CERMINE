package pl.edu.icm.yadda.bwmeta.doc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import pl.edu.icm.yadda.bwmeta.doc.ElementType.Kind;

public class SchemaReader {
	@SuppressWarnings("unchecked")
	public XsdSchema parseSchema(InputStream stream) throws IOException, JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
		Document doc = saxBuilder.build(stream);
		Element root = doc.getRootElement();
		Namespace xsdNamespace = root.getNamespace();
		
		XsdSchema schema = new XsdSchema();
		
		Element xRootAnnotation = root.getChild("annotation", xsdNamespace);
		if (xRootAnnotation != null) {
			Element xDocumentation = xRootAnnotation.getChild("documentation", xsdNamespace);
			if (xDocumentation != null)
				schema.setDocumentation(xDocumentation.getTextNormalize());

			Element xAppInfo = xRootAnnotation.getChild("appinfo", xsdNamespace);
			if (xAppInfo != null) {
				String revString = xAppInfo.getTextNormalize();
				Matcher matcher = Pattern.compile("\\$Rev: (\\d+) \\$").matcher(revString);
				if (matcher.matches()) {
					int revision = new Integer(matcher.group(1)).intValue();
					schema.setRevision(revision);
				}
			}
		}
		
		for (Element xAttributeGroup : (List<Element>) root.getChildren("attributeGroup", xsdNamespace)) {
			XsdElement attributeGroup = new XsdElement();
			attributeGroup.setName(xAttributeGroup.getAttributeValue("name"));
			processAttributes(xAttributeGroup, attributeGroup, schema, false);
			schema.getAttributeGroups().put(attributeGroup.getName(), attributeGroup);
		}
		
		for (Element xElement : (List<Element>) root.getChildren("element", xsdNamespace)) {
			XsdElement element = new XsdElement();
			element.setName(xElement.getAttributeValue("name"));
			
			Element xAnnotation = xElement.getChild("annotation", xsdNamespace);
			if (xAnnotation != null) {
				Element xDocumentation = xAnnotation.getChild("documentation", xsdNamespace);
				if (xDocumentation != null)
					element.setDocumentation(xDocumentation.getTextNormalize());
			}
			
			Element xType = null;

			xType = xElement.getChild("complexType", xsdNamespace);
			if (xType != null) {
				if (xType.getChild("all", xsdNamespace) != null)
					element.setType(parseType(xType.getChild("all", xsdNamespace), schema));
				if (xType.getChild("choice", xsdNamespace) != null)
					element.setType(parseType(xType.getChild("choice", xsdNamespace), schema));
				if (xType.getChild("sequence", xsdNamespace) != null)
					element.setType(parseType(xType.getChild("sequence", xsdNamespace), schema));
				processAttributes(xType, element, schema, true);

				Element xSimpleContent = xType.getChild("simpleContent", xsdNamespace);
				if (xSimpleContent != null) {
					element.setType(parseType(xSimpleContent, schema));
					if (xSimpleContent.getChild("extension", xsdNamespace) != null)
						processAttributes(xSimpleContent.getChild("extension", xsdNamespace), element, schema, true);
					if (xSimpleContent.getChild("restriction", xsdNamespace) != null)
						processAttributes(xSimpleContent.getChild("restriction", xsdNamespace), element, schema, true);
				}
			}

			xType = xElement.getChild("simpleType", xsdNamespace);
			if (xType != null) {
				element.setType(parseType(xType, schema));
			}

			schema.getElements().put(element.getName(), element);
		}
		System.out.println("Generated reference for revision " + schema.getRevision());
		return schema; 
	}

	@SuppressWarnings("unchecked")
	protected void processAttributes(Element root, XsdElement element, XsdSchema schema, boolean useAttrGroups) {
		Namespace xsdNamespace = root.getNamespace();
		
		if (useAttrGroups) {
			for (Element xAttributeGroup : (List<Element>) root.getChildren("attributeGroup", xsdNamespace)) {
				String name = xAttributeGroup.getAttributeValue("ref");
				if (name == null)
					continue;
				XsdElement ag = schema.getAttributeGroups().get(name);
				element.getAttributes().addAll(ag.getAttributes());
			}
		}
		for (Element xAttribute : (List<Element>) root.getChildren("attribute", xsdNamespace)) {
			XsdAttribute attribute = new XsdAttribute();
			element.getAttributes().add(attribute);
			attribute.setName(xAttribute.getAttributeValue("name"));
			attribute.setRequired("required".equals(xAttribute.getAttributeValue("use")));
			attribute.setType(xAttribute.getAttributeValue("type"));
			Element xAttrAnnotation = xAttribute.getChild("annotation", xsdNamespace);
			if (xAttrAnnotation != null) {
				Element xDocumentation = xAttrAnnotation.getChild("documentation", xsdNamespace);
				if (xDocumentation != null)
					attribute.setDocumentation(xDocumentation.getTextNormalize());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected ElementType parseType(Element root, XsdSchema schema) {
		ElementType type = new ElementType();
		
		String minOccurs = root.getAttributeValue("minOccurs");
		if (minOccurs != null)
			type.setMinOccurs("unbounded".equals(minOccurs) ? -1 : new Integer(minOccurs).intValue());
		String maxOccurs = root.getAttributeValue("maxOccurs");
		if (maxOccurs != null)
			type.setMaxOccurs("unbounded".equals(maxOccurs) ? -1 : new Integer(maxOccurs).intValue());

		if ("all".equals(root.getName()))
			type.setKind(Kind.All);
		if ("choice".equals(root.getName()))
			type.setKind(Kind.Choice);
		if ("sequence".equals(root.getName()))
			type.setKind(Kind.Sequence);
		if (type.getKind() != null) {
			for (Element xChild : (List<Element>) root.getChildren()) {
				ElementType childType = parseType(xChild, schema);
				if (childType != null)
					type.getChildren().add(childType);
			}
			return type;
		}
		
		if ("simpleContent".equals(root.getName())) {
			Namespace xsdNamespace = root.getNamespace();
			type.setKind(Kind.Simple);

			if (root.getChild("extension", xsdNamespace) != null) {
				String t = root.getChild("extension", xsdNamespace).getAttributeValue("base");
				type.setType(t);
			}
			
			if (root.getChild("restriction", xsdNamespace) != null) {
				String t = root.getChild("restriction", xsdNamespace).getAttributeValue("base");
				type.setType(t);
			}
			
			return type;
		}
		
		if ("simpleType".equals(root.getName())) {
			Namespace xsdNamespace = root.getNamespace();
			type.setKind(Kind.Simple);

			if (root.getChild("restriction", xsdNamespace) != null) {
				String t = root.getChild("restriction", xsdNamespace).getAttributeValue("base");
				type.setType(t);
			}
			
			return type;
		}

		if ("element".equals(root.getName())) {
			type.setKind(Kind.Element);
			type.setName(root.getAttributeValue("ref"));
			return type;
		}
		
		return null;
	}
}
