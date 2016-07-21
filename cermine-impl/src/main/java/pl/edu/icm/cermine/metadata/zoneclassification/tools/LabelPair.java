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
package pl.edu.icm.cermine.metadata.zoneclassification.tools;

import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Pawel Szostek
 */
public class LabelPair {

    /**
     * expected
     */
    public BxZoneLabel l1;
    /**
     * predicted *
     */
    public BxZoneLabel l2;

    public LabelPair(BxZoneLabel l1, BxZoneLabel l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LabelPair other = (LabelPair) obj;
        if (l1 != other.l1) {
            return false;
        }
        return l2 == other.l2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((l1 == null) ? 0 : l1.hashCode());
        result = prime * result + ((l2 == null) ? 0 : l2.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "(" + l1 + ", " + l2 + ")";
    }
}
