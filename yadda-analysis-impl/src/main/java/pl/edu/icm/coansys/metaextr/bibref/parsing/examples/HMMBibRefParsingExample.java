package pl.edu.icm.coansys.metaextr.bibref.parsing.examples;

import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsWordLeFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonSeriesWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsRaquoFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsUppercaseWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsNumberTextFeature;
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
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.LengthFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsOpeningSquareBracketFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCityFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllRomanDigitsFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsDashFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsPagesTextFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsQuoteFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonSourceWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsVolumeTextFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommaFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsYearFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.LowercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAllLettersFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsLowercaseLetterFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsAndFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.LetterRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.StartsWithUppercaseFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsClosingSquareBracketFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.DigitRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.UppercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonPublisherWordFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsSlashFeature;
import pl.edu.icm.coansys.metaextr.bibref.parsing.features.IsCommonSurnamePartFeature;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import org.xml.sax.InputSource;
import pl.edu.icm.coansys.metaextr.bibref.HMMBibReferenceParser;
import pl.edu.icm.coansys.metaextr.bibref.model.BibEntry;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.Citation;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationToken;
import pl.edu.icm.coansys.metaextr.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.coansys.metaextr.bibref.parsing.tools.CitationsToHMMConverter;
import pl.edu.icm.coansys.metaextr.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.HMMService;
import pl.edu.icm.coansys.metaextr.classification.hmm.HMMServiceImpl;
import pl.edu.icm.coansys.metaextr.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.classification.hmm.probability.HMMProbabilityInfoFactory;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;

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
public class HMMBibRefParsingExample {

    protected static final String hmmTrainFile = "/pl/edu/icm/yadda/analysis/bibref/sampleNumdamBibReferences.xml";

    public static void main(String[] args) throws URISyntaxException, Exception {
        
        // 1. construct vector of features builder
        FeatureVectorBuilder<CitationToken, Citation> vectorBuilder =
                new SimpleFeatureVectorBuilder<CitationToken, Citation>();
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
        URL u = HMMBibRefParsingExample.class.getResource(hmmTrainFile);

        Set<Citation> citations = NlmCitationExtractor.extractCitations(new InputSource(u.openStream()));
        
        CitationsToHMMConverter citationsToHMMTEsNode = new CitationsToHMMConverter();
        citationsToHMMTEsNode.setFeatureVectorBuilder(vectorBuilder);
        TrainingElement<CitationTokenLabel>[] trainingElements = citationsToHMMTEsNode.process(citations);

		// 3. HMM training. The resulting probabilities object should be
		// serialized for further usage
		HMMProbabilityInfo<CitationTokenLabel> hmmProbabilities = HMMProbabilityInfoFactory
				.getFVHMMProbability(
						new ArrayList<TrainingElement<CitationTokenLabel>>(
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
