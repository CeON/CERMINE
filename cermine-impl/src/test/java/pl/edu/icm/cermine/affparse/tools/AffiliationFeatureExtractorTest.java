package pl.edu.icm.cermine.affparse.tools;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.affparse.model.AffiliationToken;

public class AffiliationFeatureExtractorTest {

	private class TokenContainer {
		public List<AffiliationToken> tokens;
		public List<List<String>> features;
		public TokenContainer() {
			tokens = new ArrayList<AffiliationToken>();
			features = new ArrayList<List<String>>();
		}
		public void add(String text, String... expectedFeatures) {
			tokens.add(new AffiliationToken(text));
			features.add(Arrays.asList(expectedFeatures));
		}
		public void checkFeatures() {
			for (int i = 0; i < tokens.size(); i++) {
				List<String> expected = features.get(i);
				List<String> actual = tokens.get(i).getFeatures();
				Collections.sort(expected);
				Collections.sort(actual);
				assertEquals(expected, actual);
				
			}
		}
	}
	
	@Test
	public void testExtractFeatures() {
		TokenContainer tc = new TokenContainer();
		
		tc.add("word", "W=word");
		tc.add("123", "Number");
		tc.add("Uppercaseword", "W=uppercaseword", "UpperCase");
		tc.add("ALLUPPERCASEWORD", "W=alluppercaseword", "AllUpperCase");
		tc.add(",", "W=,", "Punct");
		tc.add("@", "W=@", "WeirdLetter");
		
		tc.add("Maluwang", "W=maluwang", "UpperCase"); // sole "Maluwang" is not an address keyword
		
		tc.add("Maluwang", "W=maluwang", "UpperCase", "Address");
		tc.add("na", "W=na", "Address");
		tc.add("lansangan", "W=lansangan", "Address"); // maluwang na lansangan -- address keyword
		
		tc.add(".", "W=.", "Punct");
		
		tc.add("les", "W=les", "City");
		tc.add("escaldes","W=escaldes", "City"); // les Escaldes -- city keyword
		
		tc.add("mhm", "W=mhm");
		
		tc.add("u", "W=u", "Country");
		tc.add(".", "W=.", "Punct", "Country");
		tc.add("s", "W=s", "Country");
		tc.add(".", "W=.", "Punct", "Country");
		tc.add("a", "W=a", "Country");
		tc.add(".", "W=.", "Punct", "Country"); // U.S.A -- country keyword
		
		tc.add("New", "W=new", "UpperCase", "State");
		tc.add("Hampshire", "W=hampshire", "UpperCase", "State"); // New Hampshire -- state keyword
		
		tc.add("KS", "W=ks", "AllUpperCase", "StateCode"); // KS -- state code keyword
		
		tc.add("du", "W=du", "StopWordMulti"); // du -- a stop word
	
		AffiliationFeatureExtractor.extractFeatures(tc.tokens);
		tc.checkFeatures();
	}

}
