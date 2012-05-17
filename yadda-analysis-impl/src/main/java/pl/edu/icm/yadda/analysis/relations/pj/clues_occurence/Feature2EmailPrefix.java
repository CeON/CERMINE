package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Feature2EmailPrefix extends Feature1Email{

	private static final Logger log = LoggerFactory.getLogger(Feature2EmailPrefix.class);
	
	protected String postprocess(String prefix, HashSet<String> emails) {
		
		try{
			prefix = prefix.substring(0, prefix.indexOf("@"));
		}catch (Exception e) {
			try{
				prefix = prefix.substring(0, prefix.indexOf("®"));
			}catch (Exception e2) {
				System.out.println(prefix);
				e2.printStackTrace();
			}
		}
		prefix = prefix.toLowerCase();
		emails.add(prefix);
		return prefix;
	}
	
	@Override
	public String id() {
		return "prefix-email-clue";
	}
}
