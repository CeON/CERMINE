/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.icm.cermine.libsvm.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.BxDocUtils.DocumentsIterator;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleFilter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class LibSVMExporter {

    public static void toLibSVM(TrainingSample<BxZoneLabel> trainingElement, BufferedWriter fileWriter) throws IOException {
        if (trainingElement.getLabel() == null) {
            return;
        }
        fileWriter.write(String.valueOf(trainingElement.getLabel().ordinal()));
        fileWriter.write(" ");

        Integer featureCounter = 1;
        for (Double value : trainingElement.getFeatureVector().getValues()) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%d:%.5f", featureCounter++, value);
            fileWriter.write(sb.toString());
            fileWriter.write(" ");
        }
        fileWriter.write("\n");
    }

    public static void toLibSVM(List<TrainingSample<BxZoneLabel>> trainingElements, String filePath) throws IOException {
        BufferedWriter svmDataFile = null;
        try {
            FileWriter fstream = new FileWriter(filePath);
            svmDataFile = new BufferedWriter(fstream);
            for (TrainingSample<BxZoneLabel> elem : trainingElements) {
                if (elem.getLabel() == null) {
                    continue;
                }
                svmDataFile.write(String.valueOf(elem.getLabel().ordinal()));
                svmDataFile.write(" ");

                Integer featureCounter = 1;
                for (Double value : elem.getFeatureVector().getValues()) {
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
        } finally {
            if (svmDataFile != null) {
                svmDataFile.close();
            }
        }

        System.out.println("Done.");
    }

    public static void main(String[] args) throws ParseException, IOException, TransformationException, AnalysisException, CloneNotSupportedException {
        Options options = new Options();

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (args.length != 2) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(" [-options] input-directory extension", options);
            System.exit(1);
        }
        String inputDirPath = line.getArgs()[0];
        File inputDirFile = new File(inputDirPath);

        Integer docIdx = 0;

        HierarchicalReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
        DocumentsIterator iter = new DocumentsIterator(inputDirPath, line.getArgs()[1]);

        FeatureVectorBuilder<BxZone, BxPage> metaVectorBuilder = SVMMetadataZoneClassifier.getFeatureVectorBuilder();
        FeatureVectorBuilder<BxZone, BxPage> initialVectorBuilder = SVMInitialZoneClassifier.getFeatureVectorBuilder();

        SampleFilter metaSamplesFilter = new SampleFilter(
                BxZoneLabelCategory.CAT_METADATA);

        FileWriter initialStream = new FileWriter("initial_"
                + inputDirFile.getName() + ".dat");
        BufferedWriter svmInitialFile = new BufferedWriter(initialStream);

        FileWriter metaStream = new FileWriter("meta_" + inputDirFile.getName()
                + ".dat");
        BufferedWriter svmMetaFile = new BufferedWriter(metaStream);

        for (BxDocument doc : iter) {
            System.out.println(docIdx + ": " + doc.getFilename());
            String filename = doc.getFilename();
            doc = ror.resolve(doc);
            doc.setFilename(filename);

            for (BxZone zone : doc.asZones()) {
                if (zone.getLabel() != null) {
                    if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                        zone.setLabel(zone.getLabel().getGeneralLabel());
                    }
                } else {
                    zone.setLabel(BxZoneLabel.OTH_UNKNOWN);
                }
            }
            List<TrainingSample<BxZoneLabel>> newMetaSamples = BxDocsToTrainingSamplesConverter
                    .getZoneTrainingSamples(doc, metaVectorBuilder,
                            BxZoneLabel.getIdentityMap());
            newMetaSamples = metaSamplesFilter.pickElements(newMetaSamples);

            List<TrainingSample<BxZoneLabel>> newInitialSamples = BxDocsToTrainingSamplesConverter
                    .getZoneTrainingSamples(doc, initialVectorBuilder,
                            BxZoneLabel.getLabelToGeneralMap());

            for (TrainingSample<BxZoneLabel> sample : newMetaSamples) {
                toLibSVM(sample, svmMetaFile);
            }
            for (TrainingSample<BxZoneLabel> sample : newInitialSamples) {
                toLibSVM(sample, svmInitialFile);
            }
            ++docIdx;
        }
        svmInitialFile.close();
        svmMetaFile.close();
    }
}
