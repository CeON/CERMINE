package pl.edu.icm.coansys.metaextr.content.features.line;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class WordsUppercaseFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "WordsUppercase";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        String text = line.toText();
        String[] words = text.split("\\s");
        int upperWordsCount = 0;
        for (String word : words) {
            if (word.matches("^[A-Z][-'a-z]+$")) {
                upperWordsCount++;
            }
        }
        return (double) upperWordsCount / (double) words.length;
    }
    
}
