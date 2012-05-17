package pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.tools.content.IAuthorParser;
import pl.edu.icm.yadda.tools.metadata.model.DocAuthor;
import pl.edu.icm.yadda.tools.trans.DiacriticsRemover;

public class NameProceeder {
	private static final Logger log = LoggerFactory.getLogger(NameProceeder .class);
	
	public static String unifyForenames(String fname){
		return DiacriticsRemover.removeDiacritics(fname).toLowerCase().replaceAll("[^a-zA-Z ]", "").trim();
	}
	
	public static String unifySurname(String sname){
		Matcher less = Pattern.compile("\\b(van|von|der|den|de|di|le|el|et)\\b",
				Pattern.CASE_INSENSITIVE).matcher(sname);
		StringBuilder sb; 
		boolean next = less.find();
		if(next){
			sb = new StringBuilder(sname);
			sb.delete(less.start(), less.end());
			while(true){
				less = Pattern.compile("\\b(van|von|der|den|de|di|le|el|et)\\b",Pattern.CASE_INSENSITIVE).matcher(sb);
				if(!less.find()) break;
				sb.delete(less.start(), less.end());
			}
			sname = sb.toString();
		}
		return DiacriticsRemover.removeDiacritics(sname).toLowerCase().replaceAll("-","!").replaceAll("[^a-zA-Z!]", "").replaceAll("!", " ").trim();
	}

	public static String unifyCanonical(String cname){
		return DiacriticsRemover.removeDiacritics(cname).toLowerCase().trim().replaceAll("  ", " ");
	}
	
	/**
	 * 
	 * @param cname
	 * @return String array of size 3; 
	 * 0 contains forenames, 
	 * 1 contains surname, 
	 * 2 contains canonical
	 */
	public static String[] extractNamesFromCanonical(String cname) {
		String sname = "";
		String fname = "";
		String iname = "";
		try{
			IAuthorParser rap = PubParser.getAuthorParser();
			DocAuthor author = rap.parse(cname);
			for(String s : author.getForenames().split(" "))
				//the condition "if(s.length()>0): is always valid here, 
				//otherwise split f-tion would be defective
					iname = iname + " " + s.substring(0,1);
			
			iname = unifyInitials(iname);
			fname = unifyForenames(author.getForenames()) ;
			sname = unifySurname(author.getSurname());
			cname = unifyCanonical(cname);
		}catch(Exception e){
			log.error("RegexpAuthorParser can not match String \""+cname+"\"\n" +
					  "This means either that surname connected with adequate contributor will be \"\" " +
					  "or that there will be no contributor forenames, surname and canonical-name recorded"  );
			//Following code gives log-spam,informing about the nullpointerexception in line 57 
//			log.error("Report connected with this raport:\n"+
//					   e.toString());
//			for(StackTraceElement ste : e.getStackTrace())
//				log.error(ste.toString());
		}
		String[] names = {fname,sname,cname, iname};
		return names;
	}

	public static String unifyInitials(String iname) {
		return unifyForenames(iname);
	}
	
}
