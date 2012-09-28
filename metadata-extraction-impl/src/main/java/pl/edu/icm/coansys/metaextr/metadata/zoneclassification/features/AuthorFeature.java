package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxBounds;
import pl.edu.icm.coansys.metaextr.structure.model.BxChunk;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxWord;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class AuthorFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "Author";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"author"};

        int count = 0;
        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().startsWith(keyword)) {
                count++;
            }
        }

        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                for (BxChunk chunk : word.getChunks()) {
                    BxBounds chb = chunk.getBounds();
                    BxBounds lb = line.getBounds();
                    String cht = chunk.getText();
                    if ((cht.matches("\\d") || cht.equals("*")) 
                            && word.getChunks().indexOf(chunk) > word.getChunks().size() - 3
                            && chb.getHeight() < 3 * lb.getHeight() / 4
                            && chb.getY() + chb.getHeight() < lb.getY() + lb.getHeight()) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

}
