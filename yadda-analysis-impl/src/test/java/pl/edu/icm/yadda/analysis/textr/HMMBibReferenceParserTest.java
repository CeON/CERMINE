//package pl.edu.icm.yadda.analysis.textr;
//
//import com.thoughtworks.xstream.XStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Arrays;
//import java.util.List;
//import static org.junit.Assert.assertEquals;
//import org.junit.Before;
//import org.junit.Test;
//import pl.edu.icm.yadda.analysis.bibref.BibEntry;
//import pl.edu.icm.yadda.analysis.bibref.BibEntryToYTransformer;
//import pl.edu.icm.yadda.analysis.bibref.HMMBibReferenceParser;
//import pl.edu.icm.yadda.analysis.bibref.YToBibEntryTransformer;
//import pl.edu.icm.yadda.analysis.bibref.parsing.features.*;
//import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
//import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
//import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationTokenLabel;
//import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
//import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
//import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
//import pl.edu.icm.yadda.analysis.classification.hmm.HMMService;
//import pl.edu.icm.yadda.analysis.classification.hmm.HMMServiceImpl;
//import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
//import pl.edu.icm.yadda.bwmeta.model.YElement;
//import pl.edu.icm.yadda.bwmeta.model.YExportable;
//import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
//import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
//import pl.edu.icm.yadda.metadata.transformers.TransformationException;
//
///**
// *
// * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
// */
//public class HMMBibReferenceParserTest {
//
//    protected static final String hmmProbabilitiesFile = "/pl/edu/icm/yadda/analysis/textr/hmmCitationProbabilities.xml";
//    protected static final String testResource = "/pl/edu/icm/yadda/analysis/textr/sampleBibReferences.xml";
//
//    private HMMService hmmService = new HMMServiceImpl();
//
//    private HMMBibReferenceParser bibReferenceParser;
//
//    @Before
//    public void setUp() {
//        InputStream is = this.getClass().getResourceAsStream(hmmProbabilitiesFile);
//        XStream xstream = new XStream();
//        HMMProbabilityInfo<CitationTokenLabel> hmmProbabilities = 
//            (HMMProbabilityInfo<CitationTokenLabel>) xstream.fromXML(is);
//
//        FeatureVectorBuilder<CitationToken, Citation> vectorBuilder =
//        		new SimpleFeatureVectorBuilder<CitationToken, Citation>();
//        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<CitationToken, Citation>>asList(
//                new DigitRelativeCountFeature(),
//                new IsAllDigitsFeature(),
//                new IsAllLettersFeature(),
//                new IsAllLettersOrDigitsFeature(),
//                new IsAllLowercaseFeature(),
//                new IsAllRomanDigitsFeature(),
//                new IsAllUppercaseFeature(),
//                new IsAndFeature(),
//                new IsCityFeature(),
//                new IsClosingParenthesisFeature(),
//                new IsClosingSquareBracketFeature(),
//                new IsCommaFeature(),
//                new IsCommonPublisherWordFeature(),
//                new IsCommonSeriesWordFeature(),
//                new IsCommonSourceWordFeature(),
//                new IsDashBetweenWordsFeature(),
//                new IsDashFeature(),
//                new IsDigitFeature(),
//                new IsDotFeature(),
//                new IsLaquoFeature(),
//                new IsLowercaseLetterFeature(),
//                new IsOpeningParenthesisFeature(),
//                new IsOpeningSquareBracketFeature(),
//                new IsQuoteFeature(),
//                new IsRaquoFeature(),
//                new IsSingleQuoteBetweenWordsFeature(),
//                new IsSlashFeature(),
//                new IsUppercaseLetterFeature(),
//                new IsUppercaseWordFeature(),
//                new IsWordAndFeature(),
//                new IsWordDeFeature(),
//                new IsWordHttpFeature(),
//                new IsWordJrFeature(),
//                new IsWordLeFeature(),
//                new IsNumberTextFeature(),
//                new IsPagesTextFeature(),
//                new IsWordTheFeature(),
//                new IsWordTheoryFeature(),
//                new IsCommonSurnamePartFeature(),
//                new IsVolumeTextFeature(),
//                new IsYearFeature(),
//                new LengthFeature(),
//                new LetterRelativeCountFeature(),
//                new LowercaseRelativeCountFeature(),
//                new StartsWithUppercaseFeature(),
//                new StartsWithWordMcFeature(),
//                new UppercaseRelativeCountFeature()));
//
//        bibReferenceParser = new HMMBibReferenceParser(hmmService, hmmProbabilities, vectorBuilder);
//    }
//
//    @Test
//    public void hmmBibReferenceParserTest() throws TransformationException {
//        BibEntryToYTransformer bibEntryToY = new BibEntryToYTransformer();
//        YToBibEntryTransformer yToBibEntry = new YToBibEntryTransformer();
//
//        IMetadataReader<YExportable> reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_1,
//                                                                               BwmetaTransformers.Y);
//        InputStream is = this.getClass().getResourceAsStream(testResource);
//
//        List<YExportable> testElements = reader.read(new InputStreamReader(is));
//
//        for (YExportable testElement: testElements) {
//            BibEntry expectedBibEntry = yToBibEntry.convert(testElement);
//
//            BibEntry testBibEntry = bibReferenceParser.parseBibReference(((YElement) testElement)
//                                                            .getOneAttributeSimpleValue("text"));
//            testBibEntry = yToBibEntry.convert(bibEntryToY.convert(testBibEntry));
//
//            for (String key : expectedBibEntry.getFieldKeys()) {
//                assertEquals(expectedBibEntry.getAllFieldValues(key), testBibEntry.getAllFieldValues(key));
//            }
//        }
//    }
//}
