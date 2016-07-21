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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * Common class for all Bx* classes having physical properties (this is to say
 * BxBounds).
 * 
 * @param <C> child type
 * @param <T> the actual type
 * @param <P> parent type (e.g. BxPage for BxZone)
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @author Pawel Szostek
 */

public abstract class BxObject <C, T, P> implements Indexable<T>, Iterable<C>, Printable, Serializable {

    /** zone's bounding box */
    private BxBounds bounds;
    
    /** page number in the document */
    private String objId;

    /** number of the next page from the sequence */
    private String nextObjId;

    /** next page in the linked list of pages. Stored in the logical reading order */
    private T nextObj;
    
    private T prevObj;

    private P parent;
    
    private String text;
    
    public void setParent(P parent) {
    	this.parent = parent;
    }
    
    public P getParent() {
    	return parent;
    }

    @Override
    public abstract Iterator<C> iterator();
    
    public boolean hasChildren() {
        return iterator().hasNext();
    }
    
    public abstract int childrenCount();
    
    public C getFirstChild() {
        Iterator<C> it = iterator();
        return it.hasNext() ? it.next() : null;
    }
    
    public abstract C getChild(int index);
    
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
    public void setNext(T nextObj) {
    	this.nextObj = nextObj;
    }

    @Override
    public T getNext() {
    	return this.nextObj;
    }

    @Override
	public boolean hasNext() {
		return getNext() != null;
	}    

    @Override
    public void setPrev(T prevObj) {
    	this.prevObj = prevObj;
    }

    @Override
    public T getPrev() {
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

    public T setBounds(BxBounds bounds) {
    	this.bounds = bounds;
    	return (T)this;
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
