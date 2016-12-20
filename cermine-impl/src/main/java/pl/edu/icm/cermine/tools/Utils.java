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

package pl.edu.icm.cermine.tools;

/**
 * General purpose utility class.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class Utils {
    
    /**
     * Compares two doubles according to a given precision.
     * 
     * @param d1 double
     * @param d2 double
     * @param precision precision
     * @return 0 if arguments are equal, 1 if the first argument is greater, -1 otherwise
     */
    public static int compareDouble(double d1, double d2, double precision) {
        if (Double.isNaN(d1) || Double.isNaN(d2)) {
            return Double.compare(d1, d2);
        }
        if (precision == 0) {
            precision = 1;
        }
        long i1 = Math.round(d1 / precision);
        long i2 = Math.round(d2 / precision);
        return Long.valueOf(i1).compareTo(i2);
    }

}
