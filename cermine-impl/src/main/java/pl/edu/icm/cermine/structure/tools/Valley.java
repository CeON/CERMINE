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

/**
 *
 * @author estocka
 */
public class Valley implements Comparable {

    private double valleyStart;
    private double valleyEnd;
    private double length;
    private Direction direction;

    public Valley() {
        this.valleyStart = 0.0;
        this.valleyEnd = 0.0;
        this.length = 0.0;


    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getValleyEnd() {
        return valleyEnd;
    }

    public void setValleyEnd(double valleyEnd) {
        this.valleyEnd = valleyEnd;
        computeLength();
    }

    public double getValleyStart() {
        return valleyStart;
    }

    public void setValleyStart(double valleyStart) {
        this.valleyStart = valleyStart;
        computeLength();
    }

    public double getLength() {
        return length;
    }

    void computeLength() {
        this.length = this.valleyEnd - this.valleyStart;
    }

    @Override
    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }
        int result = 0;
        Valley v = (Valley) o;
        if (this.getLength() > v.getLength()) {
            result = 1;
        } else if (this.getLength() < v.getLength()) {
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
        final Valley other = (Valley) obj;
        if (Double.doubleToLongBits(this.length) != Double.doubleToLongBits(other.length)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.length) ^ (Double.doubleToLongBits(this.length) >>> 32));
        return hash;
    }
    
}
