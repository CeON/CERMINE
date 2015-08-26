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

package pl.edu.icm.cermine.structure.model;

public interface Indexable<A> {
    
	/** Getter for the value based on TrueViz XxxID field */
	String getId();
	
    /** Getter for the value based on TrueViz XxxNext field */
	String getNextId();
	
    /** Setter for the value based on TrueViz XxxID field */
	void setId(String id);
	
    /** Setter for the value based on TrueViz XxxNext field */
	void setNextId(String nextId);
	
	/** Get next linked list element */
	A getNext();
	
    /** Set next linked list element */
	void setNext(A elem);
	
    boolean hasNext();
	
	A getPrev();
	
    void setPrev(A elem);
	
    boolean hasPrev();
    
}
