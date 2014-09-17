package pl.edu.icm.cermine.parsing.features;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.parsing.model.ParsableString;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.tools.TextClassifier;

/**
 * A token is considered as rare if it is word that does not belong to the set of common words.
 * 
 * @author Bartosz Tarnawski
 */
public class IsRareFeature extends BinaryTokenFeatureCalculator {

	private Set<String> commonWords;
	private boolean caseSensitive;
	
	
	/**
	 * @param commonWordsList the words that are not considered as rare
	 * @param caseSensitive whether the lookups should be case-sensitive
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
	public boolean calculateFeaturePredicate(Token<?> token, ParsableString<?> context) {
		String text = token.getText();
		if (!TextClassifier.isWord(text)) {
			return false;
		}
		if (!caseSensitive) {
			text = text.toLowerCase();
		}
		return !commonWords.contains(text);
	}

}
