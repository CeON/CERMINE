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
package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import libsvm.svm_parameter;
import org.apache.commons.cli.ParseException;
import pl.edu.icm.cermine.content.filtering.ContentFilterTools;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;
import pl.edu.icm.cermine.tools.BxDocUtils.DocumentsIterator;
import pl.edu.icm.cermine.tools.classification.general.*;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SVMBodyClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {

    @Override
    protected SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples, int kernelType, double gamma, double C, int degree)
            throws IOException, AnalysisException, CloneNotSupportedException {

        PenaltyCalculator pc = new PenaltyCalculator(trainingSamples);
        int[] intClasses = new int[pc.getClasses().size()];
        double[] classesWeights = new double[pc.getClasses().size()];

        int labelIdx = 0;
        for (BxZoneLabel label : pc.getClasses()) {
            intClasses[labelIdx] = label.ordinal();
            classesWeights[labelIdx] = pc.getPenaltyWeigth(label);
            ++labelIdx;
        }

        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(getFeatureVectorBuilder());
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
        param.gamma = gamma;
        param.C = C;
        System.out.println(degree);
        param.degree = degree;
        param.kernel_type = kernelType;
        param.weight = classesWeights;
        param.weight_label = intClasses;

        zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingSamples);

        return zoneClassifier;
    }

    public static void main(String[] args)
            throws ParseException, AnalysisException, IOException, TransformationException, CloneNotSupportedException {
        CrossvalidatingZoneClassificationEvaluator.main(args, new SVMBodyClassificationEvaluator());
    }

    @Override
    protected FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
        return ContentFilterTools.VECTOR_BUILDER;
    }

    @Override
    public List<TrainingSample<BxZoneLabel>> getSamples(String inputFile, String ext) throws AnalysisException {
        Map<BxZoneLabel, BxZoneLabel> map = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);
        map.put(BxZoneLabel.BODY_ACKNOWLEDGMENT, BxZoneLabel.BODY_CONTENT);
        map.put(BxZoneLabel.BODY_CONFLICT_STMT, BxZoneLabel.BODY_CONTENT);
        map.put(BxZoneLabel.BODY_EQUATION, BxZoneLabel.BODY_JUNK);
        map.put(BxZoneLabel.BODY_FIGURE, BxZoneLabel.BODY_JUNK);
        map.put(BxZoneLabel.BODY_GLOSSARY, BxZoneLabel.BODY_JUNK);
        map.put(BxZoneLabel.BODY_TABLE, BxZoneLabel.BODY_JUNK);

        DocumentsIterator it = new DocumentsIterator(inputFile, ext);
        List<TrainingSample<BxZoneLabel>> samples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(it.iterator(),
                getFeatureVectorBuilder(), map);
        List<TrainingSample<BxZoneLabel>> tss = ClassificationUtils.filterElements(samples, BxZoneLabelCategory.CAT_BODY);

        return tss;
    }

}
