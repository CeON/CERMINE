package pl.edu.icm.cermine.parsing.features;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.model.TokenizedString;

/**
 * A token is considered rare if its text does not belong to the set of common words.
 * 
 * @author Bartosz Tarnawski
 */
public class IsRareFeature extends BinaryTokenFeatureCalculator {

	private Set<String> commonWords;
	private boolean caseSensitive;
	
	
	/**
	 * @param setFileName
	 * @param caseSensitive
	 */
	public IsRareFeature(List<String> commonWordsList, boolean caseSensitive) {
		this.commonWords = new HashSet<String>();
		this.caseSensitive = caseSensitive;
		
		for (String commonWord : commonWordsList) {
			if (caseSensitive) {
				commonWords.add(commonWord);
			} else {
				commonWords.add(commonWord.toLowerCase());
			}
		}
	}
	
	@Override
	public boolean calculateFeaturePredicate(Token<?> token, TokenizedString<?> context) {
		String text = token.getText();
		if (!caseSensitive) {
			text = text.toLowerCase();
		}
		return !commonWords.contains(text);
	}

}
