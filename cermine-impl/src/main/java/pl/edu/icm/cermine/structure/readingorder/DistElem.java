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
package pl.edu.icm.cermine.structure.readingorder;

import pl.edu.icm.cermine.tools.Utils;

/**
 * Tuple containing magic value c, object1, object2 and distance between them.
 *
 * @author Pawel Szostek
 * @param <E> element type
 */
public class DistElem<E> implements Comparable<DistElem<E>> {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (c ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(dist);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((obj1 == null) ? 0 : obj1.hashCode());
        result = prime * result + ((obj2 == null) ? 0 : obj2.hashCode());
        return result;
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
        DistElem other = (DistElem) obj;
        if (c != other.c) {
            return false;
        }
        if (Double.doubleToLongBits(dist) != Double
                .doubleToLongBits(other.dist)) {
            return false;
        }
        if (obj1 == null) {
            if (other.obj1 != null) {
                return false;
            }
        } else if (!obj1.equals(other.obj1)) {
            return false;
        }
        if (obj2 == null) {
            if (other.obj2 != null) {
                return false;
            }
        } else if (!obj2.equals(other.obj2)) {
            return false;
        }
        return true;
    }

    boolean c;
    double dist;
    E obj1;
    E obj2;

    public boolean isC() {
        return c;
    }

    public void setC(boolean c) {
        this.c = c;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public E getObj1() {
        return obj1;
    }

    public void setObj1(E obj1) {
        this.obj1 = obj1;
    }

    public E getObj2() {
        return obj2;
    }

    public void setObj2(E obj2) {
        this.obj2 = obj2;
    }

    public DistElem(boolean c, double dist, E obj1, E obj2) {
        this.c = c;
        this.dist = dist;
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    @Override
    public int compareTo(DistElem<E> compareObject) {
        double eps = 1E-3;
        if (c == compareObject.c) {
            return Utils.compareDouble(dist, compareObject.dist, eps);
        } else {
            return c ? -1 : 1;
        }
    }

}
