package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BigdataFeature2EmailPrefix extends BigdataFeature1Email{

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(BigdataFeature2EmailPrefix.class);
	
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
