package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import java.util.Arrays;
import java.util.HashSet;


public class BigdataFeature5CoKeywordWords extends BigdataFeature5CoKeywordPhrase{

	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		
		askWho = "k";
		
		String[] sa = prefix.split(" ");
		for(int i=0;i<sa.length;i++){
			sa[i]=sa[i].trim();
		}
		emails.addAll(Arrays.asList(sa));
		return null;
	}
}
