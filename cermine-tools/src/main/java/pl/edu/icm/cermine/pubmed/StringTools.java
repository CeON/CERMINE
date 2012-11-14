package pl.edu.icm.cermine.pubmed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StringTools {
	public static List<String> tokenize(String text) {
		List<String> roughRet = new ArrayList<String>(Arrays.asList(text.split(" |\n|,|\\. |&|;|:|\\-")));
		List<String> ret = new ArrayList<String>();
		for(String candidate: roughRet) {
			if(candidate.length() > 1) {
				ret.add(candidate.toLowerCase());
			}
		}
		return ret;
	}
	
	public static Integer tokLen(String text) {
		return tokenize(text).size();
	}
	
	public static String joinStrings(List<String> strings) {
		StringBuilder ret = new StringBuilder();
		for(String str: strings) {
			if(str != null) {
				ret.append(str).append(" ");
			}
		}
		return ret.toString();
	}
	
	public static String joinStrings(List<String> strings, char delim) {
		if(strings.size() == 0) {
			return "";
		} else	if(strings.size() == 1) {
			return strings.get(0);
		} else {
			StringBuilder ret = new StringBuilder();
			for(Integer partIdx = 0; partIdx < strings.size()-1; ++partIdx) {
				ret.append(strings.get(partIdx)).append(delim);
			}
			ret.append(strings.get(strings.size()-1));
			return ret.toString();	
		}
	}
	
	public static String joinStrings(String[] strings) {
		return joinStrings(new ArrayList<String>(Arrays.asList(strings)));
	}

}
