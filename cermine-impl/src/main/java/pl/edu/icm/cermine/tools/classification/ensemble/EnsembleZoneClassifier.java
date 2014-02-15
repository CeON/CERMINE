/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.tools.classification.ensemble;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;

public class EnsembleZoneClassifier implements ZoneClassifier {

    private List<ZoneClassifier> classifiers;
    private List<Double> wrongClassificationCosts;
    private List<BxZoneLabel> zoneLabels = BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_GENERAL);

    public EnsembleZoneClassifier(List<ZoneClassifier> classifiers, List<Double> wrongClassificationCosts, List<BxZoneLabel> zoneLabels) {
        assert wrongClassificationCosts.size() == zoneLabels.size();

        this.classifiers = classifiers;
        this.wrongClassificationCosts = wrongClassificationCosts;
        this.zoneLabels = zoneLabels;
    }

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        List<Map<BxZoneLabel, Integer>> votesForZones = new ArrayList<Map<BxZoneLabel, Integer>>(document.asZones().size());
        for (int zoneIdx = 0; zoneIdx < document.asZones().size(); ++zoneIdx) {
            votesForZones.add(new EnumMap<BxZoneLabel, Integer>(BxZoneLabel.class));
            for (BxZoneLabel label : zoneLabels) {
                votesForZones.get(zoneIdx).put(label, 0);
            }
        }

        for (ZoneClassifier classifier : classifiers) {
            classifier.classifyZones(document);

            for (int zoneIdx = 0; zoneIdx < votesForZones.size(); ++zoneIdx) {
                BxZoneLabel curZoneLabel = document.asZones().get(zoneIdx).getLabel();
                int votesUntilNow = votesForZones.get(zoneIdx).get(curZoneLabel);
                votesForZones.get(zoneIdx).put(curZoneLabel, votesUntilNow + 1);
            }
        }

        chooseBestLabels(document.asZones(), votesForZones);

        for (BxZone zone : document.asZones()) {
            assert zoneLabels.contains(zone.getLabel());
        }

        return document;
    }

    private void chooseBestLabels(List<BxZone> zones, List<Map<BxZoneLabel, Integer>> votesForZones) {
        //iterate over all the zones
        for (int zoneIdx = 0; zoneIdx < zones.size(); ++zoneIdx) {
            int bestLabelIdx = -1;
            double bestLabelVote = Double.NEGATIVE_INFINITY;

            //iterate over all possible labels
            for (int labelIdx = 0; labelIdx < zoneLabels.size(); ++labelIdx) {
                //check current label counter
                int labelCounter = votesForZones.get(zoneIdx).get(zoneLabels.get(labelIdx));
                //calculate biased counter value
                double labelVote = wrongClassificationCosts.get(labelIdx) * labelCounter;
                //check if it's the best one
                if (labelVote > bestLabelVote) {
                    bestLabelIdx = labelIdx;
                    bestLabelVote = labelVote;
                }
            }
            assert bestLabelIdx != -1 && bestLabelVote != Double.NEGATIVE_INFINITY;
            BxZoneLabel chosenLabel = zoneLabels.get(bestLabelIdx);
            //assign the best label to the current zone
            zones.get(zoneIdx).setLabel(chosenLabel);
        }
    }
}
