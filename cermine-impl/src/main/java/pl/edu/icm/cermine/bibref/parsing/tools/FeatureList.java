package pl.edu.icm.cermine.bibref.parsing.tools;

import java.util.Arrays;
import pl.edu.icm.cermine.bibref.parsing.features.*;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class FeatureList {

    public static final FeatureVectorBuilder<CitationToken, Citation> VECTOR_BUILDER;
    
    static {
        VECTOR_BUILDER = new SimpleFeatureVectorBuilder<CitationToken, Citation>();
        VECTOR_BUILDER.setFeatureCalculators(Arrays.<FeatureCalculator<CitationToken, Citation>>asList(
                new IsAllDigitsFeature(),
                new IsAllLettersFeature(),
                new IsAllLettersOrDigitsFeature(),
                new IsAllLowercaseFeature(),
                new IsAllRomanDigitsFeature(),
                new IsAllUppercaseFeature(),
                new IsAndFeature(),
                new IsCityFeature(),
                new IsClosingParenthesisFeature(),
                new IsClosingSquareBracketFeature(),
                new IsCommaFeature(),
                new IsCommonPublisherWordFeature(),
                new IsCommonSeriesWordFeature(),
                new IsCommonSourceWordFeature(),
                new IsCommonSurnamePartFeature(),
                new IsDashBetweenWordsFeature(),
                new IsDashFeature(),
                new IsDigitFeature(),
                new IsDotFeature(),
                new IsLaquoFeature(),
                new IsLowercaseLetterFeature(),
                new IsNumberTextFeature(),
                new IsOpeningParenthesisFeature(),
                new IsOpeningSquareBracketFeature(),
                new IsPagesTextFeature(),
                new IsQuoteFeature(),
                new IsRaquoFeature(),
                new IsSingleQuoteBetweenWordsFeature(),
                new IsSlashFeature(),
                new IsUppercaseLetterFeature(),
                new IsUppercaseWordFeature(),
                new IsVolumeTextFeature(),
                new IsWordAndFeature(),
                new IsWordDeFeature(),
                new IsWordHttpFeature(),
                new IsWordJrFeature(),
                new IsWordLeFeature(),
                new IsWordTheFeature(),
                new IsWordTheoryFeature(),
                new IsYearFeature(),
                new StartsWithUppercaseFeature(),
                new StartsWithWordMcFeature()));
    }

    private FeatureList() {}
    
}
