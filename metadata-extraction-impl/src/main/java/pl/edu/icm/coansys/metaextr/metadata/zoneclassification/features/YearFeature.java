package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class YearFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "Year";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
 
        int yearCount = 0;
        for (BxLine line : zone.getLines()) {
            
            String toMatch = line.toText();
            Pattern pattern = Pattern.compile("^\\D*(\\d+)(.*)$");
            while (Pattern.matches("^.*\\d.*", toMatch)) {
                Matcher matcher = pattern.matcher(toMatch);
                if (!matcher.matches()) {
                    break;
                }
                String numbers = matcher.group(1);
                try {
                    int i = Integer.parseInt(numbers);
                    if (i >= 1500 && i < 2100) {
                        yearCount++;
                    }
                } catch (NumberFormatException ex) {}
                toMatch = matcher.group(2);
            }
        }
        return (double)yearCount / (double)zone.getLines().size();
    }

}
