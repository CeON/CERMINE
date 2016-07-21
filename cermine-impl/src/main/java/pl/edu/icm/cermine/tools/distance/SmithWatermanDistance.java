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

package pl.edu.icm.cermine.tools.distance;

import java.util.List;

/**
 * @author Pawel Szostek
 */
public class SmithWatermanDistance {

    private final double mu;
    private final double delta;

    public SmithWatermanDistance(double mu, double delta) {
        this.mu = mu;
        this.delta = delta;
    }

    private double similarityScore(String a, String b) {

        double result;
        if (a.equals(b)) {
            result = 1.;
        } else {
            result = -mu;
        }
        return result;
    }

    private double findArrayMax(double array[]) {

        double max = array[0];            // start with max = first element

        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;                    // return highest value in array
    }

    private enum Move {

        OMIT_S1, OMIT_S2, OMIT_BOTH, MATCH, NN
    };

    public double compare(List<String> s1, List<String> s2) {
        int N_a = s1.size();                     // get the actual lengths of the sequences
        int N_b = s2.size();

        // initialize H
        double H[][] = new double[N_a + 1][N_b + 1];
        for (int i = 0; i <= N_a; i++) {
            for (int j = 0; j <= N_b; j++) {
                H[i][j] = 0;
            }
        }

        double temp[] = new double[4];
        // here comes the actual algorithm

        for (int i = 1; i <= N_a; i++) {
            for (int j = 1; j <= N_b; j++) {
                temp[0] = H[i - 1][j - 1] + similarityScore(s1.get(i - 1), s2.get(j - 1));
                temp[1] = H[i - 1][j] - delta;
                temp[2] = H[i][j - 1] - delta;
                temp[3] = 0.;
                H[i][j] = findArrayMax(temp);
            }
        }
        // search H for the maximal score
        double H_max = 0.;
        for (int i = 1; i <= N_a; i++) {
            for (int j = 1; j <= N_b; j++) {
                if (H[i][j] > H_max) {
                    H_max = H[i][j];
                }
            }
        }

        return H_max;
    }

}
