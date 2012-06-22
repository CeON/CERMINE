package pl.edu.icm.yadda.analysis.bibref.parsing.examples;

import java.net.URISyntaxException;
import pl.edu.icm.yadda.analysis.textr.*;
import java.io.File;
import java.net.URL;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationTokenLabel;
import java.util.Arrays;

import pl.edu.icm.yadda.analysis.bibref.BibEntry;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.bibref.parsing.features.*;
import pl.edu.icm.yadda.analysis.bibref.parsing.nodes.CitationsFromNLMExtractorNode;
import pl.edu.icm.yadda.analysis.bibref.parsing.nodes.CitationsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMService;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMServiceImpl;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfoFactory;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;

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

        CitationsFromNLMExtractorNode citationsExtractorNode = new CitationsFromNLMExtractorNode();
        Citation[] citations = citationsExtractorNode.process(new File[] {new File(u.toURI())}, null);

        CitationsToFVHMMTrainingElementsConverterNode citationsToHMMTEsNode = new CitationsToFVHMMTrainingElementsConverterNode();
        citationsToHMMTEsNode.setFeatureVectorBuilder(vectorBuilder);
        HMMTrainingElement<CitationTokenLabel, FeatureVector>[] trainingElements = citationsToHMMTEsNode.process(citations, null);

        // 3. HMM training. The resulting probabilities object should be serialized for further usage
        HMMProbabilityInfo<CitationTokenLabel, FeatureVector> hmmProbabilities
                = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, vectorBuilder);

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
