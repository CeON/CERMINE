package pl.edu.icm.yadda.analysis.metadata.zoneclassification;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfGaussianMixtureFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussian;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfoFactory;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.classification.hmm.training.SimpleHMMTrainingElement;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AbstractFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AcknowledgementFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AffiliationFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AtCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AtRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AuthorFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BibinfoFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BracketRelativeCount;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BracketedLineRelativeCount;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CuePhrasesCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CuePhrasesRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DatesFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DistanceFromNearestNeighbourFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.EmptySpaceRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FigureFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FontHeightMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FreeSpaceWithinZoneFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HorizontalRelativeProminenceFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsFirstPageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsFontBiggerThanNeighboursFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsHighestOnThePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsItemizeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLastPageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLowestOnThePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.KeywordsFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PageNumberFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PunctuationRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.StartsWithDigitFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.VerticalProminenceFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WhitespaceCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WhitespaceRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordLengthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordLengthMedianFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YearFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneGeneralLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.ProbabilityDistribution;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

public class JaHMMDemo {
	/* Possible packet reception status */
	
	protected static final String hmmTrainingFile = "/pl/edu/icm/yadda/analysis/metadata/zoneclassification/xmls.zip";
	private static final String hmmTestFile = "/pl/edu/icm/yadda/analysis/logicstr/train/01.xml";

	
	static public void main(String[] argv) 
	throws Exception
	{	
		/* Build a HMM and generate observation sequences using this HMM */
        // 1. construct vector of features builder
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
        		new AbstractFeature(),
        		new AcknowledgementFeature(),
        		new AffiliationFeature(),
                new AtCountFeature(),
                new AtRelativeCountFeature(),
                new AuthorFeature(),
                new BibinfoFeature(),
        		new BracketRelativeCount(),
        		new BracketedLineRelativeCount(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
        		new CuePhrasesCountFeature(),
        		new CuePhrasesRelativeCountFeature(),
        		new DatesFeature(),
                new DigitCountFeature(),
                new DigitRelativeCountFeature(),
        		new DistanceFromNearestNeighbourFeature(),
        		new DotCountFeature(),
        		new DotRelativeCountFeature(),
        		new EmptySpaceRelativeFeature(),
        		new FontHeightMeanFeature(),
        		new FigureFeature(),
        		new FreeSpaceWithinZoneFeature(),
                new HeightFeature(),
                new HeightRelativeFeature(),
        		new HorizontalRelativeProminenceFeature(),
        		new IsFirstPageFeature(),
        		new IsFontBiggerThanNeighboursFeature(),
        		new IsHighestOnThePageFeature(),
        		new IsLastPageFeature(),
        		new IsLowestOnThePageFeature(),
        		new IsFontBiggerThanNeighboursFeature(),
        		new IsItemizeFeature(),
                new KeywordsFeature(),
                new LineCountFeature(),
                new LineRelativeCountFeature(),
                new LineHeightMeanFeature(),
                new LineWidthMeanFeature(),
                new LineXPositionMeanFeature(),
                new LineXPositionDiffFeature(),
                new LineXWidthPositionDiffFeature(),
                new LetterCountFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseCountFeature(),
                new LowercaseRelativeCountFeature(),
                new PageNumberFeature(),
                new ProportionsFeature(),
                new PunctuationRelativeCountFeature(),
         //       new ReferencesFeature(),
                new StartsWithDigitFeature(),
                new UppercaseCountFeature(),
                new UppercaseRelativeCountFeature(),
                new UppercaseWordCountFeature(),
                new UppercaseWordRelativeCountFeature(),
        		new VerticalProminenceFeature(),
                new WidthFeature(),
                new WordCountFeature(),
                new WordCountRelativeFeature(),
        		new WordWidthMeanFeature(),
        		new WordLengthMeanFeature(),
        		new WordLengthMedianFeature(),
        		new WhitespaceCountFeature(),
        		new WhitespaceRelativeCountFeature(),
                new WidthRelativeFeature(),
                new XPositionFeature(),
                new XPositionRelativeFeature(),
                new YPositionFeature(),
                new YPositionRelativeFeature(),
                new YearFeature()
                ));

		/* Baum-Welch learning */
		
		BaumWelchLearner bwl = new BaumWelchLearner();
		List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements = prepareTrainingList(vectorBuilder);
		Hmm<ObservationVector> learntHmm = buildInitHmm(trainingElements, vectorBuilder);
		List<List<ObservationVector>> sequences = generateSequences(trainingElements);
		
		// This object measures the distance between two HMMs
		KullbackLeiblerDistanceCalculator klc = new KullbackLeiblerDistanceCalculator();
		
		// Incrementally improve the solution
		for (int i = 0; i < 10; i++) {
			learntHmm = bwl.iterate(learntHmm, sequences);
		}
		
		System.out.println("Resulting HMM:\n" + learntHmm);
		
		/* Computing the probability of a sequence */
		
		(new GenericHmmDrawerDot()).write(learntHmm, "learntHmm.dot");
	}
	
	
	private static List<List<ObservationVector>> generateSequences(
			List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements) 
	{
		List<List<ObservationVector>> ret = new ArrayList<List<ObservationVector>>();
		List<ObservationVector> cur = null;
		for(HMMTrainingElement<BxZoneGeneralLabel> elem: trainingElements) {
			if(elem.isFirst()) {
				cur = new ArrayList<ObservationVector>();
				ret.add(cur);
			}
			int idx = 0;
			double[] values = new double[elem.getObservation().size()];
			for(String featureName: elem.getObservation().getFeatureNames()) {
				values[idx++] = elem.getObservation().getFeature(featureName);
			}
			cur.add(new ObservationVector(values));
		}
		return ret;
	}


	/* Initial guess for the Baum-Welch algorithm */
	private static Integer indexOf(BxZoneGeneralLabel elem)
	{
		Integer ret = 0;
		for(; ret < BxZoneGeneralLabel.values().length; ++ret) {
			if(BxZoneGeneralLabel.values()[ret] == elem)
				break;
		}
		return ret;
	}
	
	private static BxZoneLabel[] integerToStates(int[] intTab)
	{
		BxZoneLabel[] ret = new BxZoneLabel[intTab.length];
		for(int idx = 0; idx < intTab.length; ++idx)
			ret[idx] = BxZoneLabel.values()[intTab[idx]];
		return ret;
	}
	
	private static double[] calculateMeanVector(List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements, BxZoneGeneralLabel state)
	{
		double[] mean = new double[trainingElements.get(0).getObservation().size()];
		int count = 0;
		Set<String> featureNames = trainingElements.get(0).getObservation().getFeatureNames();
		for(HMMTrainingElement<BxZoneGeneralLabel> elem: trainingElements) {
			FeatureVector fv = elem.getObservation();
			Integer idx = 0;
			if(elem.getLabel() == state) {
				for(String name: featureNames) {
					mean[idx] += fv.getFeature(name);
					++count;
				}
			}
			++idx;
		}
		for(int idx = 0; idx < mean.length; ++idx)
			mean[idx] /= count;
		return mean;
	}
	
	
	private static double[][] calculateCovarianceMatrix(List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements, BxZoneGeneralLabel state)
	{
		Integer dims = trainingElements.get(0).getObservation().size();
		double[][] covarianceMatrix = new double[dims][dims];
		Set<String> featureNames = trainingElements.get(0).getObservation().getFeatureNames();
		
		Integer idx0 = 0;
		
		for(String dim0: featureNames) {
			Integer idx1 = 0;
			for(String dim1: featureNames) {
				double e_0_1 = 0.0;
				double mean_0 = 0.0;
				double mean_1 = 0.0;

				for(HMMTrainingElement<BxZoneGeneralLabel> elem: trainingElements) {
					if(elem.getLabel() == state) {
						FeatureVector fv = elem.getObservation();
						e_0_1 += fv.getFeature(dim0)*fv.getFeature(dim1);
						mean_0 += fv.getFeature(dim0);
						mean_1 += fv.getFeature(dim1);
					}
				}
				e_0_1 /= trainingElements.size();
				mean_0 /= trainingElements.size();
				mean_1 /= trainingElements.size();
				
				covarianceMatrix[idx0][idx1] += e_0_1 - mean_0*mean_1;
				++idx1;
			}
			++idx0;
		}
		return covarianceMatrix;
	}
	
	static Hmm<ObservationVector> buildInitHmm(List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements, FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) throws Exception
	{	
		Integer dims = BxZoneGeneralLabel.values().length;
		HMMProbabilityInfo<BxZoneGeneralLabel> hmmProbabilities = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, vectorBuilder);

		Hmm<ObservationVector> hmm = new Hmm<ObservationVector>(dims, new OpdfMultiGaussianFactory(dims));

		for(BxZoneGeneralLabel lab: BxZoneGeneralLabel.values()) {
			hmm.setPi(indexOf(lab), hmmProbabilities.getInitialProbability(lab));
		}
		
		for(BxZoneGeneralLabel from: BxZoneGeneralLabel.values()) {
			for(BxZoneGeneralLabel to: BxZoneGeneralLabel.values()) {
				hmm.setAij(indexOf(from),
						   indexOf(to),
						   hmmProbabilities.getTransitionProbability(from, to));
			}
		}
		
		for(BxZoneGeneralLabel lab: BxZoneGeneralLabel.values()) {
			double[][] covMatrix = calculateCovarianceMatrix(trainingElements, lab);
			double[] meanVector = calculateMeanVector(trainingElements, lab);
			for(double[] row: covMatrix) {
				for(double val: row) 
					System.out.print(val+" ");
				System.out.println();
			}
			hmm.setOpdf(indexOf(lab), new OpdfMultiGaussian(meanVector, covMatrix));
		}
		return hmm;
	}
	
	
	static List<HMMTrainingElement<BxZoneGeneralLabel>> prepareTrainingList(FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) throws Exception
	{
		List<BxDocument> documents = HMMZoneGeneralClassificationBigDemo.getDocumentsFromZip(hmmTrainingFile);
		List<BxZone> allZones = new ArrayList<BxZone>();
		for(BxDocument doc: documents)
			allZones.addAll(doc.asZones());

		BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
		node.setFeatureVectorBuilder(vectorBuilder);

		List<HMMTrainingElement<BxZoneLabel>> unconvertedTrainingElements = node.process(documents, null);
		BxZoneLabelDetailedToGeneralMapper mapper = new BxZoneLabelDetailedToGeneralMapper();
		
		List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements = new ArrayList<HMMTrainingElement<BxZoneGeneralLabel>>();
		SimpleHMMTrainingElement<BxZoneGeneralLabel> prev = null;
		for(HMMTrainingElement<BxZoneLabel> elem: unconvertedTrainingElements) {
			SimpleHMMTrainingElement<BxZoneGeneralLabel> convertedElem = 
					new SimpleHMMTrainingElement<BxZoneGeneralLabel>(elem.getObservation(), mapper.map(elem.getLabel()),  elem.isFirst());
			if(prev != null)
				prev.setNextLabel(convertedElem.getLabel());
			trainingElements.add(convertedElem);
			prev = convertedElem;
		}
		return trainingElements;
	}
}