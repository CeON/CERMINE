package pl.edu.icm.cermine.pubmed;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLTools {

	static String extractTextFromNode(Node node) {
		StringBuilder ret = new StringBuilder();
		
		if(node == null) {
			return "";
		}
		if(node.getChildNodes().getLength() == 0) {
			if(node.getNodeValue() != null) {
				return node.getNodeValue() + " ";
			} else {
				return "";
			}
		} else {
			for(Integer childIdx=0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
				ret.append(extractTextFromNode(node.getChildNodes().item(childIdx)));
			}
		}
		return ret.toString();
	}

	static String extractTextFromNodes(NodeList nodes) {
		StringBuilder ret = new StringBuilder();
		for(Integer nodeIdx = 0; nodeIdx < nodes.getLength(); ++nodeIdx) {
			Node node = nodes.item(nodeIdx);
			ret.append(extractTextFromNode(node));
		}
		return ret.toString();
	}

	static List<String> extractTextAsList(NodeList nodes) {
		List<String> ret = new ArrayList<String>(); 
		for(Integer nodeIdx=0; nodeIdx < nodes.getLength(); ++nodeIdx) {
			ret.add(extractTextFromNode(nodes.item(nodeIdx)));
		}
		return ret;
	}

	static List<String> extractTextAsList(Node node) {
		List<String> ret = new ArrayList<String>();
		
		if(node == null) {
			return ret;
		}
		if(node.getChildNodes().getLength() == 0 && node.getNodeValue() != null) {
			ret.add(node.getNodeValue());
		} else {
			for(Integer childIdx=0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
				ret.addAll(extractTextAsList(node.getChildNodes().item(childIdx)));
			}
		}
		return ret;
	}

}
