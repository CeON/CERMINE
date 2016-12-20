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

package pl.edu.icm.cermine.bibref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.cermine.bibref.extraction.features.*;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.bibref.extraction.tools.BibRefExtractionUtils;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.CharacterUtils;
import pl.edu.icm.cermine.tools.classification.clustering.KMeansWithInitialCentroids;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.distance.FeatureVectorDistanceMetric;
import pl.edu.icm.cermine.tools.distance.FeatureVectorEuclideanMetric;

/**
 * Clustering-based bibliographic reference extractor.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class KMeansBibReferenceExtractor implements BibReferenceExtractor {

    public static final int MAX_REF_LINES_COUNT = 10000;
    
    public static final int MAX_REFS_COUNT = 1000;

    public static final int MAX_REF_LENGTH = 1500;
    
    private static final FeatureVectorBuilder<BxLine, BxDocumentBibReferences> VECTOR_BUILDER =
                new FeatureVectorBuilder<BxLine, BxDocumentBibReferences>();
    static {
        VECTOR_BUILDER.setFeatureCalculators(Arrays.<FeatureCalculator<BxLine, BxDocumentBibReferences>>asList(
                new PrevEndsWithDotFeature(),
                new PrevRelativeLengthFeature(),
                new RelativeStartTresholdFeature(),
                new SpaceBetweenLinesFeature(),
                new StartsWithNumberFeature()
                ));
    }

    /**
     * Extracts individual bibliographic references from the document. The references lines
     * are clustered based on feature vector computed for them. The cluster containing the first line
     * is then assumed to be the set of all first lines, which allows for splitting references blocks
     * into individual references.
     * 
     * @param document document
     * @return an array of extracted references
     * @throws AnalysisException AnalysisException
     */
    @Override
    public String[] extractBibReferences(BxDocument document) throws AnalysisException {
        BxDocumentBibReferences documentReferences = BibRefExtractionUtils.extractBibRefLines(document);
        documentReferences.limit(MAX_REF_LINES_COUNT);
        
        List<String> lines = new ArrayList<String>();
        List<FeatureVector> instances = new ArrayList<FeatureVector>();
        FeatureVectorDistanceMetric metric = new FeatureVectorEuclideanMetric();
        FeatureVector farthestInstance = null;
        double farthestDistance = 0;
        for (BxLine line : documentReferences.getLines()) {
            lines.add(ContentCleaner.clean(line.toText()));
            FeatureVector featureVector = VECTOR_BUILDER.getFeatureVector(line, documentReferences);
            instances.add(featureVector);
            if (farthestInstance == null) {
                farthestInstance = instances.get(0);
            }
            double distance = metric.getDistance(instances.get(0), featureVector);
            if (distance > farthestDistance) {
                farthestInstance = featureVector;
                farthestDistance = distance;
            }
        }

        if (lines.size() <= 1 || farthestDistance < 0.001) {
            if (lines.size() > MAX_REFS_COUNT) {
                return new String[]{};
            } else {
                return lines.toArray(new String[lines.size()]);
            }
        }
        
        KMeansWithInitialCentroids clusterer = new KMeansWithInitialCentroids(2);
        FeatureVector[] centroids = new FeatureVector[2];
        centroids[0] = instances.get(0);
        centroids[1] = farthestInstance;
        clusterer.setCentroids(centroids);
        List<FeatureVector>[] clusters = clusterer.cluster(instances);
        
        int firstInstanceClusterNum = 0;
        if (clusters[1].contains(instances.get(0))) {
            firstInstanceClusterNum = 1;
        }
        
        List<String> references = new ArrayList<String>();
        String actRef = "";
        for (int i = 0; i < lines.size(); i++) {
            if (clusters[firstInstanceClusterNum].contains(instances.get(i))) {
                if (!actRef.isEmpty() && actRef.matches(".*[0-9].*") && actRef.matches(".*[a-zA-Z].*")
			&& actRef.length() < MAX_REF_LENGTH) {
                    references.add(actRef);
                }
                actRef = lines.get(i);
            } else {
                String hyphenList = String.valueOf(CharacterUtils.DASH_CHARS);
                hyphenList = hyphenList.replaceAll("-", "") + "-";
                if (actRef.matches(".*[a-zA-Z]["+hyphenList+"]")) {
                    actRef = actRef.substring(0, actRef.length()-1);
                } else {
                    actRef += " ";
                }
                actRef += lines.get(i);
            }
        }
        if (!actRef.isEmpty() && actRef.matches(".*[0-9].*") && actRef.matches(".*[a-zA-Z].*")
		&& actRef.length() < MAX_REF_LENGTH) {
            references.add(actRef);
        }
        
        if (references.size() > MAX_REFS_COUNT) {
            references.clear();
        }
        
        return references.toArray(new String[references.size()]);
    }

}
