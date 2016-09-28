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

/**
 * The code was originally copied from Java Machine Learning Library.
 * Changes have been introduced in the original code.
 * 
 * Copyright (c) 2006-2009, Thomas Abeel (original author)
 * Project: http://java-ml.sourceforge.net/ (original project)
 * 
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.tools.classification.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.distance.FeatureVectorDistanceMetric;
import pl.edu.icm.cermine.tools.distance.FeatureVectorEuclideanMetric;

public class KMeansWithInitialCentroids {
    /**
     * The number of clusters.
     */
    private int numberOfClusters = -1;

    /**
     * The number of iterations the algorithm should make. If this value is
     * Integer.INFINITY, then the algorithm runs until the centroids no longer
     * change.
     * 
     */
    private int numberOfIterations = -1;

    /**
     * Random generator for this clusterer.
     */
    private Random rg;

    /**
     * The distance measure used in the algorithm, defaults to Euclidean
     * distance.
     */
    private FeatureVectorDistanceMetric dm;

    /**
     * The centroids of the different clusters.
     */
    private FeatureVector[] centroids;

    /**
     * Constuct a default K-means clusterer with 100 iterations, 4 clusters, a
     * default random generator and using the Euclidean distance.
     */
    public KMeansWithInitialCentroids() {
        this(4);
    }

    /**
     * Constuct a default K-means clusterer with the specified number of
     * clusters, 100 iterations, a default random generator and using the
     * Euclidean distance.
     * 
     * @param k the number of clusters to create
     */
    public KMeansWithInitialCentroids(int k) {
        this(k, 100);
    }

    /**
     * Create a new Simple K-means clusterer with the given number of clusters
     * and iterations. The internal random generator is a new one based upon the
     * current system time. For the distance we use the Euclidean n-space
     * distance.
     * 
     * @param clusters
     *            the number of clusters
     * @param iterations
     *            the number of iterations
     */
    public KMeansWithInitialCentroids(int clusters, int iterations) {
        this(clusters, iterations, new FeatureVectorEuclideanMetric());
    }

    /**
     * Create a new K-means clusterer with the given number of clusters and
     * iterations. Also the Random Generator for the clusterer is given as
     * parameter.
     * 
     * @param clusters
     *            the number of clustesr
     * @param iterations
     *            the number of iterations
     * 
     * @param dm
     *            the distance measure to use
     */
    public KMeansWithInitialCentroids(int clusters, int iterations, FeatureVectorDistanceMetric dm) {
        this.numberOfClusters = clusters;
        this.numberOfIterations = iterations;
        this.dm = dm;
        rg = new Random(System.currentTimeMillis());
    }

    public void setCentroids(FeatureVector[] centroids) {
        this.centroids = new FeatureVector[centroids.length];
        System.arraycopy(centroids, 0, this.centroids, 0, centroids.length);
    }
    
    public List<FeatureVector>[] cluster(List<FeatureVector> data) {
        if (data.isEmpty()) {
            throw new RuntimeException("The dataset should not be empty");
        }
        if (numberOfClusters == 0) {
            throw new RuntimeException("There should be at least one cluster");
        }
        // Place K points into the space represented by the objects that are
        // being clustered. These points represent the initial group of
        // centroids.
        // DatasetTools.
        int instanceLength = data.get(0).size();
        double[] min = new double[instanceLength];
        double[] max = new double[instanceLength];
        for (int i = 0; i < instanceLength; i++) {
            min[i] = data.get(0).getValue(i);
            max[i] = data.get(0).getValue(i);
        }
        for (FeatureVector fv : data) {
            for (int i = 0; i < instanceLength; i++) {
                if (fv.getValue(i) < min[i]) {
                    min[i] = fv.getValue(i);
                }
                if (fv.getValue(i) > max[i]) {
                    max[i] = fv.getValue(i);
                }
            }
        }
        
        if (this.centroids == null) {
            this.centroids = new FeatureVector[numberOfClusters];
            for (int j = 0; j < numberOfClusters; j++) {
                double[] randomInstance = new double[instanceLength];
                for (int i = 0; i < instanceLength; i++) {
                    double dist = Math.abs(max[i] - min[i]);
                    randomInstance[i] = (float) (min[i] + rg.nextDouble() * dist);

                }
                this.centroids[j] = data.get(0).copy();
                this.centroids[j].setValues(randomInstance);
            }
        }

        int iterationCount = 0;
        boolean centroidsChanged = true;
        boolean randomCentroids = true;
        while (randomCentroids || (iterationCount < this.numberOfIterations && centroidsChanged)) {
            iterationCount++;
            // Assign each object to the group that has the closest centroid.
            int[] assignment = new int[data.size()];
            for (int i = 0; i < data.size(); i++) {
                int tmpCluster = 0;
                double minDistance = dm.getDistance(centroids[0], data.get(i));
                for (int j = 1; j < centroids.length; j++) {
                    double dist = dm.getDistance(centroids[j], data.get(i));
                    if (dist < minDistance) {
                        minDistance = dist;
                        tmpCluster = j;
                    }
                }
                assignment[i] = tmpCluster;

            }

            // When all objects have been assigned, recalculate the positions of
            // the K centroids and start over.
            // The new position of the centroid is the weighted center of the
            // current cluster.
            double[][] sumPosition = new double[this.numberOfClusters][instanceLength];
            int[] countPosition = new int[this.numberOfClusters];
            for (int i = 0; i < data.size(); i++) {
                FeatureVector in = data.get(i);
                for (int j = 0; j < instanceLength; j++) {

                    sumPosition[assignment[i]][j] += in.getValue(j);

                }
                countPosition[assignment[i]]++;
            }
            
            centroidsChanged = false;
            randomCentroids = false;
            for (int i = 0; i < this.numberOfClusters; i++) {
                if (countPosition[i] > 0) {
                    double[] tmp = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        tmp[j] = (float) sumPosition[i][j] / countPosition[i];
                    }
                    FeatureVector newCentroid = data.get(0).copy();
                    newCentroid.setValues(tmp);
                    if (dm.getDistance(newCentroid, centroids[i]) > 0.0001) {
                        centroidsChanged = true;
                        centroids[i] = newCentroid;
                    }
                } else {
                    double[] randomInstance = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        double dist = Math.abs(max[j] - min[j]);
                        randomInstance[j] = (float) (min[j] + rg.nextDouble() * dist);

                    }
                    randomCentroids = true;
                    this.centroids[i] = data.get(0).copy();
                    this.centroids[i].setValues(randomInstance);
                }

            }
        }
        List<FeatureVector>[] output = new List[centroids.length];
        for (int i = 0; i < centroids.length; i++) {
            output[i] = new ArrayList<FeatureVector>();
        }
        for (int i = 0; i < data.size(); i++) {
            int tmpCluster = 0;
            double minDistance = dm.getDistance(centroids[0], data.get(i));
            for (int j = 0; j < centroids.length; j++) {
                double dist = dm.getDistance(centroids[j], data.get(i));
                if (dist < minDistance) {
                    minDistance = dist;
                    tmpCluster = j;
                }
            }
            output[tmpCluster].add(data.get(i));

        }
        return output;
    }
}
