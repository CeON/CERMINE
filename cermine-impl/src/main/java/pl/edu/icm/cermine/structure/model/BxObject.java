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

import java.util.Set;

/**
 * Common class for all Bx* classes having physical properties (this is to say
 * BxBounds).
 * 
 * @param <S> the actual type
 * @param <T> context type (e.g. BxPage for BxZone)
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @date 05.2012
 * 
 */

public abstract class BxObject <S, T> implements Indexable<S> {

    /** zone's bounding box */
    private BxBounds bounds;
    /** page number in the document */
    private String objId;

    /** number of the next page from the sequence */
    private String nextObjId;

    /** next page in the linked list of pages. Stored in the logical reading order */
    private S nextObj;
    private S prevObj;

    private T parent;
    
    private String text;
    
    public void setParent(T parent) {
    	this.parent = parent;
    }
    
    public T getParent() {
    	return parent;
    }
    
    @Override
    public void setId(String objId) {
    	this.objId = objId;
    }
    
    @Override
    public String getId() {
    	return this.objId;
    }
    
    @Override
    public void setNextId(String nextObjId) {
    	this.nextObjId = nextObjId;
    }
    
    @Override
    public String getNextId() {
    	return this.nextObjId;
    }

    @Override
    public void setNext(S nextObj) {
    	this.nextObj = nextObj;
    }

    @Override
    public S getNext() {
    	return this.nextObj;
    }

    @Override
	public boolean hasNext() {
		return getNext() != null;
	}    

    @Override
    public void setPrev(S prevObj) {
    	this.prevObj = prevObj;
    }

    @Override
    public S getPrev() {
    	return this.prevObj;
    }

    @Override
	public boolean hasPrev() {
		return getPrev() != null;
	}

    public double getArea() {
    	return (bounds.getHeight() * bounds.getWidth());
    }

    public BxBounds getBounds() {
        return bounds;
    }

    public S setBounds(BxBounds bounds) {
    	this.bounds = bounds;
    	return (S)this;
    }
    
    public double getX() {
    	return bounds.getX();
    }
    
    public double getY() {
    	return bounds.getY();
    }
    
    public double getWidth() {
    	return bounds.getWidth();
    }
    
    public double getHeight() {
    	return bounds.getHeight();
    }

    protected String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }
    
    public void resetText() {
        this.text = null;
    }
    
    public abstract String getMostPopularFontName();
    
    public abstract Set<String> getFontNames();

}
