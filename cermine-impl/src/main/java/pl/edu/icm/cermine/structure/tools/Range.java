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

package pl.edu.icm.cermine.structure.tools;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxChunk;

/**
 *
 * @author estocka
 */
public class Range implements Comparable {

    private double rangeStart;
    private double rangeEnd;
    private List<BxChunk> chunksList;

    public Range() {
        this.chunksList = new ArrayList<BxChunk>();
        this.rangeStart = 0.0;
        this.rangeEnd = 0.0;

    }

    public List<BxChunk> getChunksList() {
        return chunksList;
    }

    public void setChunksList(List<BxChunk> chunksList) {
        this.chunksList = chunksList;
    }

    public double getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(double rangeYEnd) {
        this.rangeEnd = rangeYEnd;
    }

    public double getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(double rangeYStart) {
        this.rangeStart = rangeYStart;
    }

    public void addChunk(BxChunk chunk) {
        this.chunksList.add(chunk);
    }
    //checks range and if one of ends is in range and the other not,
    //the range is exeeded

    public boolean inRange(double start, double end) {
        boolean inRangeStart = false;
        boolean inRangeEnd = false;
        boolean inRange = false;
        if (start >= getRangeStart() && start <= getRangeEnd()) {
            inRangeStart = true;
        }
        if (end >= getRangeStart() && end <= getRangeEnd()) {
            inRangeEnd = true;
        }
        if (inRangeStart || inRangeEnd) {
            inRange = true;

        }
        if (inRange && !inRangeStart) {
            this.setRangeStart(start);
        }
        if (inRange && !inRangeEnd) {
            this.setRangeEnd(end);
        }
        return inRange;
    }

    @Override
    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }
        int result = 0;
        Range sr = (Range) o;
        if (this.getRangeStart() > sr.getRangeStart()) {
            result = 1;
        } else if (this.getRangeStart() < sr.getRangeStart()) {
            result = -1;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Range other = (Range) obj;
        if (Double.doubleToLongBits(this.rangeStart) != Double.doubleToLongBits(other.rangeStart)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.rangeStart) ^ (Double.doubleToLongBits(this.rangeStart) >>> 32));
        return hash;
    }
    
}
