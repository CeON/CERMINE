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
package pl.edu.icm.cermine.structure.model;

/**
 * @author Pawel Szostek
 * @param <A> indexable object class
 */
public interface Indexable<A> {

    /**
     * Getter for the value based on TrueViz XxxID field
     * @return id
     */
    String getId();

    /**
     * Getter for the value based on TrueViz XxxNext field
     * @return next id
     */
    String getNextId();

    /**
     * Setter for the value based on TrueViz XxxID field
     * @param id id
     */
    void setId(String id);

    /**
     * Setter for the value based on TrueViz XxxNext field
     * @param nextId next id
     */
    void setNextId(String nextId);

    /**
     * Get next linked list element
     * @return next object
     */
    A getNext();

    /**
     * Set next linked list element
     * @param elem next object
     */
    void setNext(A elem);

    boolean hasNext();

    A getPrev();

    void setPrev(A elem);

    boolean hasPrev();

}
