package pl.edu.icm.coansys.metaextr.bibref.parsing.tools;

import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordLeFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonSeriesWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsNumberTextFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsRaquoFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsUppercaseWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordTheoryFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsDotFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsUppercaseLetterFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllDigitsFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllLettersOrDigitsFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllUppercaseFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsClosingParenthesisFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordHttpFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.StartsWithWordMcFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordDeFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsSingleQuoteBetweenWordsFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordTheFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsDigitFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsLaquoFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllLowercaseFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsOpeningParenthesisFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordJrFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordAndFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsDashBetweenWordsFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsOpeningSquareBracketFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCityFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllRomanDigitsFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsPagesTextFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsDashFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonSourceWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsQuoteFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsVolumeTextFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommaFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsYearFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllLettersFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsLowercaseLetterFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAndFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.StartsWithUppercaseFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsClosingSquareBracketFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonSurnamePartFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonPublisherWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsSlashFeature;
import java.util.Arrays;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.features.SimpleFeatureVectorBuilder;

/**
 *
 * @author Dominika Tkaczyk
 */
public class FeatureList {

    public static final FeatureVectorBuilder<CitationToken, Citation> vectorBuilder;

    static {
        vectorBuilder = new SimpleFeatureVectorBuilder<CitationToken, Citation>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<CitationToken, Citation>>asList(
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
}
