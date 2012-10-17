package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class YearFeature extends FeatureCalculator<BxZone, BxPage> {

    private static final int MIN_YEAR = 1800;
    private static final int MAX_YEAR = 2100;
    
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
                    if (i >= MIN_YEAR && i < MAX_YEAR) {
                        yearCount++;
                    }
                } catch (NumberFormatException ex) {}
                toMatch = matcher.group(2);
            }
        }
        return (double)yearCount / (double)zone.getLines().size();
    }

}
