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
package pl.edu.icm.cermine.tools.classification.svm;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 * @author Pawel Szostek
 */
public class SVMZoneClassifier extends SVMClassifier<BxZone, BxPage, BxZoneLabel> implements ZoneClassifier {

    public SVMZoneClassifier(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        super(featureVectorBuilder, BxZoneLabel.class);
    }

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        for (BxZone zone : document.asZones()) {
            BxZoneLabel predicted = predictLabel(zone, zone.getParent());
            zone.setLabel(predicted);
        }
        return document;
    }

    public static List<TrainingSample<BxZoneLabel>> loadProblem(String path, FeatureVectorBuilder<BxZone, BxPage> fvb) throws IOException {
        File file = new File(path);
        return loadProblem(file, fvb);
    }

    public static List<TrainingSample<BxZoneLabel>> loadProblem(File file, FeatureVectorBuilder<BxZone, BxPage> fvb) throws IOException {
        List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        final Pattern partsPattern = Pattern.compile(" ");
        final Pattern twopartPattern = Pattern.compile(":");
        while ((line = br.readLine()) != null) {
            String[] parts = partsPattern.split(line);
            BxZoneLabel label = BxZoneLabel.values()[Integer.parseInt(parts[0])];
            FeatureVector fv = new FeatureVector();
            List<String> featureNames = fvb.getFeatureNames();
            for (int partIdx = 1; partIdx < parts.length; ++partIdx) {
                String[] subparts = twopartPattern.split(parts[partIdx]);
                fv.addFeature(featureNames.get(partIdx - 1), Double.parseDouble(subparts[1]));
            }
            TrainingSample<BxZoneLabel> sample = new TrainingSample<BxZoneLabel>(fv, label);
            ret.add(sample);
        }
        br.close();
        return ret;
    }
}
