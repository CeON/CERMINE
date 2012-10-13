package pl.edu.icm.cermine.metadata.optimization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.InvalidParameterException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.CrossvalidatingZoneClassificationEvaluator;
import pl.edu.icm.cermine.evaluation.EvaluationUtils;
import pl.edu.icm.cermine.evaluation.SVMInitialZoneClassificationEvaluator;
import pl.edu.icm.cermine.evaluation.SVMMetadataClassificationEvaluator;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;
import pl.edu.icm.cermine.tools.classification.sampleselection.NormalSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.UndersamplingSelector;

public class LibSVMExporter {

    public static void toLibSVM(List<TrainingElement<BxZoneLabel>> trainingElements, String filePath) {
        try {
            FileWriter fstream = new FileWriter(filePath);
            BufferedWriter svmDataFile = new BufferedWriter(fstream);
            for (TrainingElement<BxZoneLabel> elem : trainingElements) {
                svmDataFile.write(String.valueOf(elem.getLabel().ordinal()));
                svmDataFile.write(" ");

                Integer featureCounter = 1;
                for (Double value : elem.getObservation().getFeatures()) {
                    StringBuilder sb = new StringBuilder();
                    Formatter formatter = new Formatter(sb, Locale.US);
                    formatter.format("%d:%.5f", featureCounter++, value);
                    svmDataFile.write(sb.toString());
                    svmDataFile.write(" ");
                }
                svmDataFile.write("\n");
            }
            svmDataFile.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
        System.out.println("Done.");
    }

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("meta", false, "export data for metadata classification");
        options.addOption("initial", false, "export data for initial classification");
        options.addOption("under", false, "use undersampling for data selection");
        options.addOption("over", false, "use oversampling for data selection");
        options.addOption("normal", false, "don't use any special strategy for data selection");

        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);

        if (args.length != 3 || !(line.hasOption("initial") ^ line.hasOption("meta")) || !(line.hasOption("under") ^ line.hasOption("over") ^ line.hasOption("normal"))) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(args[0] + " [-options] input-directory", options);
        }
        String inputDirPath = line.getArgs()[0];
        List<BxDocument> evaluationDocs = EvaluationUtils.getDocumentsFromPath(inputDirPath);
        List<TrainingElement<BxZoneLabel>> trainingElements;

        CrossvalidatingZoneClassificationEvaluator evaluator;
        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
        BxZoneLabelCategory category;

        if (line.hasOption("initial")) {
            evaluator = new SVMInitialZoneClassificationEvaluator();
            node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
            category = BxZoneLabelCategory.CAT_GENERAL;
        } else if (line.hasOption("meta")) {
            evaluator = new SVMMetadataClassificationEvaluator();
            category = BxZoneLabelCategory.CAT_METADATA;
            for (BxDocument doc : evaluationDocs) {
                for (BxZone zone : doc.asZones()) {
                    if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                        zone.setLabel(zone.getLabel().getGeneralLabel());
                    }
                }
            }
        } else {
            throw new InvalidParameterException("Extraction purpose not specified!");
        }

        SampleSelector<BxZoneLabel> selector = null;
        if (line.hasOption("over")) {
            selector = new OversamplingSelector<BxZoneLabel>(1.0);
        } else if (line.hasOption("under")) {
            selector = new UndersamplingSelector<BxZoneLabel>(2.0);
        } else if (line.hasOption("normal")) {
            selector = new NormalSelector<BxZoneLabel>();
        } else {
            System.err.println("Sampling strategy is not specified!");
            System.exit(1);
        }

        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = evaluator.getFeatureVectorBuilder();
        node.setFeatureVectorBuilder(vectorBuilder);
        try {
            trainingElements = node.process(evaluationDocs);
        } catch (Exception e) {
            throw new RuntimeException("Unable to process the delivered training documents!");
        }
        trainingElements = ClassificationUtils.filterElements(trainingElements, category);
        trainingElements = selector.pickElements(trainingElements);
        toLibSVM(trainingElements, "zone_classification.dat");
    }
}