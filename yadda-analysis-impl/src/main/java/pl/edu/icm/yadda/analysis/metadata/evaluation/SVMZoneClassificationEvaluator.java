package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections.iterators.ArrayIterator;


import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.classification.svm.SVMZoneClassifier;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabelCategory;
import pl.edu.icm.yadda.analysis.textr.tools.InitiallyClassifiedZonesPreprocessor;

public class SVMZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator{ 
	@Override
	protected SVMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder)
	{
        BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(featureVectorBuilder);
        List<HMMTrainingElement<BxZoneLabel>> trainingElementsUnrevised;
        try {
        	trainingElementsUnrevised = node.process(trainingDocuments, null);
        } catch(Exception e) {
			throw new RuntimeException("Unable to process the delivered training documents!");
		}        

        // Filter the training documents
        // so that in the learning examples all classes are
        // represented equally

        Map<BxZoneLabel, Integer> labelCount = new HashMap<BxZoneLabel, Integer>();
        labelCount.put(BxZoneLabel.GEN_BODY, 0);
        labelCount.put(BxZoneLabel.GEN_METADATA, 0);
        labelCount.put(BxZoneLabel.GEN_OTHER, 0);
        labelCount.put(BxZoneLabel.GEN_REFERENCES, 0);

        for(HMMTrainingElement<BxZoneLabel> elem: trainingElementsUnrevised) {
        	labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        }

        Integer max = Integer.MAX_VALUE;
        for(BxZoneLabel lab: labelCount.keySet()) {
        	if(labelCount.get(lab) < max)
        		max = labelCount.get(lab);
        	System.out.println(lab + " " + labelCount.get(lab));
        }
        
        labelCount.put(BxZoneLabel.GEN_BODY, 0);
        labelCount.put(BxZoneLabel.GEN_METADATA, 0);
        labelCount.put(BxZoneLabel.GEN_OTHER, 0);
        labelCount.put(BxZoneLabel.GEN_REFERENCES, 0);
        List<HMMTrainingElement<BxZoneLabel>> trainingElements = new ArrayList<HMMTrainingElement<BxZoneLabel>>();
        
        final double INEQUALITY_FACTOR = 1.3;
        for(HMMTrainingElement<BxZoneLabel> elem: trainingElementsUnrevised) {
        	if(labelCount.get(elem.getLabel()) < max*INEQUALITY_FACTOR) {
        		trainingElements.add(elem);
        		labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        	}
        }
        
        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_GENERAL), featureVectorBuilder);
        zoneClassifier.buildClassifier(trainingElements);
        Set<String> fnames = featureVectorBuilder.getFeatureNames();
        Iterator<String> namesIt = fnames.iterator();
        Iterator<Double> valueIt = (Iterator<Double>)new ArrayIterator(zoneClassifier.getWeights());
        
        assert fnames.size() == zoneClassifier.getWeights().length;
        while(namesIt.hasNext() && valueIt.hasNext()) {
        	String name = namesIt.next();
        	Double val = valueIt.next();
        	System.out.println(name + " " + val);
        }
        
		return zoneClassifier;
	}	
	
	public static void main(String[] args) throws ParseException
	{
		Options options = new Options();
		options.addOption("compact", false, "do not print results for pages");
		options.addOption("ratio", true, "cross-valifation training-to-testing files ratio");
		options.addOption("iter", true, "number of cross-validation iterations");
		options.addOption("help", false, "print this help message");
		options.addOption("minimal", false, "print only final summary");
		options.addOption("full", false, "print all possible messages");

		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);

		if (line.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(args[0] + " [-options] input-directory",
					options);
		} else {
			SVMZoneClassificationEvaluator evaluator = new SVMZoneClassificationEvaluator();
			String[] remaining = line.getArgs();
			
			if (remaining.length != 1) {
				throw new ParseException("Input directory is missing!");
			}
			if (!line.hasOption("ratio")) {
				throw new ParseException("Cross-valdation ratio is not given!");
			} else {
				evaluator.ratio = Double.valueOf(line.getOptionValue("ratio"));
			}

			if (!line.hasOption("iter")) {
				throw new ParseException("Number of iterations for cross-validation is not given!");
			} else {
				evaluator.iterations = Integer.valueOf(line.getOptionValue("iter"));
			}
			String inputDir = remaining[0];

			if (line.hasOption("minimal")) {
				evaluator.detail = Detail.MINIMAL;
			} else if (line.hasOption("compact")) {
				evaluator.detail = Detail.COMPACT;
			} else if (line.hasOption("full")) {
				evaluator.detail = Detail.FULL;
			}
			evaluator.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
			evaluator.setPreprocessor(new InitiallyClassifiedZonesPreprocessor());
		
			evaluator.run(inputDir, null);
		
		}
	}
}

