package pl.edu.icm.cermine.bibref.parsing.examples;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.bibref.HMMBibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.features.*;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationsToHMMConverter;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.HMMService;
import pl.edu.icm.cermine.tools.classification.hmm.HMMServiceImpl;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfoFactory;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMTrainingSample;

/**
 * HMM-based bibliographic reference parsing example.
 *
 * Parameter classes used:
 * - Citation - objects representing the entire HMM sequence
 * - CitationToken - objects representing single HMM object to classify
 * - CitationTokenLabel - possible HMM classifications (labels)
 * - FeatureVector - HMM visible observations
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class HMMBibRefParsingExample {

    protected static final String HMM_TRAIN_FILE = ".xml";

    private HMMBibRefParsingExample() {}
    
    public static void main(String[] args) throws URISyntaxException, JDOMException, IOException {
        
        // 1. construct vector of features builder
        FeatureVectorBuilder<CitationToken, Citation> vectorBuilder = new FeatureVectorBuilder<CitationToken, Citation>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<CitationToken, Citation>>asList(
                new DigitRelativeCountFeature(),
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
                new IsDashBetweenWordsFeature(),
                new IsDashFeature(),
                new IsDigitFeature(),
                new IsDotFeature(),
                new IsLaquoFeature(),
                new IsLowercaseLetterFeature(),
                new IsOpeningParenthesisFeature(),
                new IsOpeningSquareBracketFeature(),
                new IsQuoteFeature(),
                new IsRaquoFeature(),
                new IsSingleQuoteBetweenWordsFeature(),
                new IsSlashFeature(),
                new IsUppercaseLetterFeature(),
                new IsUppercaseWordFeature(),
                new IsWordAndFeature(),
                new IsWordDeFeature(),
                new IsWordHttpFeature(),
                new IsWordJrFeature(),
                new IsWordLeFeature(),
                new IsNumberTextFeature(),
                new IsPagesTextFeature(),
                new IsWordTheFeature(),
                new IsWordTheoryFeature(),
                new IsCommonSurnamePartFeature(),
                new IsVolumeTextFeature(),
                new IsYearFeature(),
                new LengthFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseRelativeCountFeature(),
                new StartsWithUppercaseFeature(),
                new StartsWithWordMcFeature(),
                new UppercaseRelativeCountFeature()));

        // 2. import and generate training set based on sequences and vector of features
        URL u = HMMBibRefParsingExample.class.getResource(HMM_TRAIN_FILE);

        List<Citation> citations = NlmCitationExtractor.extractCitations(new InputSource(u.openStream()));
        
        HMMTrainingSample<CitationTokenLabel>[] trainingElements = CitationsToHMMConverter.convertToHMM(Sets.newHashSet(citations), vectorBuilder);

		// 3. HMM training. The resulting probabilities object should be
		// serialized for further usage
		HMMProbabilityInfo<CitationTokenLabel> hmmProbabilities = HMMProbabilityInfoFactory
				.getFVHMMProbability(
						new ArrayList<HMMTrainingSample<CitationTokenLabel>>(
								Arrays.asList(trainingElements)), vectorBuilder);

        // 4. create an HMM service instance
        HMMService hmmService = new HMMServiceImpl();

        // 5. bibliographic refs parser instance
        HMMBibReferenceParser bibReferenceParser = new HMMBibReferenceParser(hmmService, hmmProbabilities, vectorBuilder);

        // 6. find the most probable labels for HMM objects
        BibEntry bibEntry = bibReferenceParser.parseBibReference(
                "[BP] R. Burton and R. Pemantle, Local characteristics, entropy and limit theorems for spanning trees and domino tilings via transfer impedances, Ann. Probab., Vol. 21, 1993, pp. 1329-1371.");
        System.out.println(bibEntry.toBibTeX());
    }

}
