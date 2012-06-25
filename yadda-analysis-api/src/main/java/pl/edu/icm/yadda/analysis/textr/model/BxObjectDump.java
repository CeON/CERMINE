package pl.edu.icm.yadda.analysis.textr.model;

/**
 * Debugging class aiming to dump content of a document to the standard output
 * in a form of a tree of objects.
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @date 05.2012
 * 
 */

public class BxObjectDump {
	static Integer INDENT_WIDTH = 4;
	public String dump(BxDocument doc) {
		return dump(doc, -1, 0, false, true);
	}
	
	public String dump(BxDocument doc, Integer levels, Integer indent, Boolean dumpReference, Boolean dumpContent) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i<indent*INDENT_WIDTH; ++i)
			stringBuilder.append(" ");
		stringBuilder.append("BxDocument");
		if(dumpReference) {
			stringBuilder.append(" @")
						 .append(cutOutReference(doc.toString()));
		}
		stringBuilder.append("\n");
		
		if(levels != 0) {
			--levels;
			for(BxPage page: doc.getPages())
				stringBuilder.append(dump(page, levels, indent+1, dumpReference, dumpContent));
		}
		return stringBuilder.toString();
	}
	
	public String dump(BxPage page, Integer levels, Integer indent, Boolean dumpReference, Boolean dumpContent) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i<indent*INDENT_WIDTH; ++i)
			stringBuilder.append(" ");
		stringBuilder.append("BxPage(");
		if(page.getId() != null) {
			stringBuilder.append(page.getId());
		} else {
			stringBuilder.append("");
		}
		stringBuilder.append(")");
		if(dumpReference) {
			stringBuilder.append(" @")
						 .append(cutOutReference(page.toString()));
		}
		if(dumpContent) {
			stringBuilder.append(dumpContentBriefly(page));
		}
		stringBuilder.append("\n");
		
		if(levels != 0) {
			--levels;
			for(BxZone zone: page.getZones())
				stringBuilder.append(dump(zone, levels, indent+1, dumpReference, dumpContent));
		}
		return stringBuilder.toString();
	}
	
	private String brief(String text) {
		if(text.length() < 10)
			return text;
		else
			return text.substring(0, 10) + "...";
	}
	
	private <A extends Printable> String dumpContentBriefly(A obj) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" [")
		 			 .append(brief(obj.toText()))
		 			 .append("]");
		return stringBuilder.toString();
	}
	
	public String dump(BxZone zone, Integer levels, Integer indent, Boolean dumpReference, Boolean dumpContent) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i<indent*INDENT_WIDTH; ++i)
			stringBuilder.append(" ");
		stringBuilder.append("BxZone(");
		if(zone.getId() != null) {
			stringBuilder.append(zone.getId());
		} else {
			stringBuilder.append("");
		}
		stringBuilder.append(")");
		if(dumpReference) {
			stringBuilder.append("@")
						 .append(cutOutReference(zone.toString()));
		}
		if(dumpContent) {
			stringBuilder.append(dumpContentBriefly(zone));
		}
		stringBuilder.append("\n");
		
		if(levels != 0) {
			--levels;
			for(BxLine line: zone.getLines())
				stringBuilder.append(dump(line, levels, indent+1, dumpReference, dumpContent));
		}
		return stringBuilder.toString();
	}
	
	public String dump(BxLine line, Integer levels, Integer indent, Boolean dumpReference, Boolean dumpContent) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i<indent*INDENT_WIDTH; ++i)
			stringBuilder.append(" ");
		stringBuilder.append("BxLine(");
		if(line.getId() != null) {
			stringBuilder.append(line.getId());
		} else {
			stringBuilder.append("");
		}
		stringBuilder.append(")");
		if(dumpReference) {
			stringBuilder.append(" @")
						 .append(cutOutReference(line.toString()));
		}
		if(dumpContent) {
			stringBuilder.append(dumpContentBriefly(line));
		}
		stringBuilder.append("\n");
		
		if(levels != 0) {
			--levels;
			for(BxWord word: line.getWords())
				stringBuilder.append(dump(word, levels, indent+1, dumpReference, dumpContent));
		}
		return stringBuilder.toString();
	}
	
	public String dump(BxWord word, Integer levels, Integer indent, Boolean dumpReference, Boolean dumpContent) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i<indent*INDENT_WIDTH; ++i)
			stringBuilder.append(" ");
		stringBuilder.append("BxWord(");
		if(word.getId() != null) {
			stringBuilder.append(word.getId());
		} else {
			stringBuilder.append("");
		}
		stringBuilder.append(")");
		if(dumpReference) {
			stringBuilder.append("@")
						 .append(cutOutReference(word.toString()));
		}
		if(dumpContent) {
			stringBuilder.append(dumpContentBriefly(word));
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
	
	protected String cutOutReference(String str) {
		String[] parts = str.split("@");
		String addr = parts[parts.length-1];
		return addr;
	}
}
