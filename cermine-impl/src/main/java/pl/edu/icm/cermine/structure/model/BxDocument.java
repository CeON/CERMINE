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

import java.util.*;
import pl.edu.icm.cermine.tools.CountMap;

/**
 * Models a document containing pages.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class BxDocument extends BxObject<BxPage, BxDocument, Object> {

    private static final long serialVersionUID = -4826783896245709986L;

    /** list of document's pages */
    private final List<BxPage> pages = new ArrayList<BxPage>();
    
    private String filename = null;

    private int curPageNumber = 0;

    public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

    public BxDocument setPages(Collection<BxPage> pages) {
        if (pages != null) {
            this.pages.clear();
            curPageNumber = 0;
            for (BxPage page : pages) {
                addPage(page);
            }
        }
        return this;
    }

    public BxDocument addPage(BxPage page) {
        if (page != null) {
            page.setId(Integer.toString(this.curPageNumber++));
            page.setParent(this);
            this.pages.add(page);
        }
        return this;
    }

    @Override
    public String toText() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Printable w : pages) {
            if (!first) {
                sb.append("\n");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
    
    /**
     * 
     * @return a list of constituent pages. The list keeps the order used in the original file
     */
    public Iterable<BxPage> asPages() {
        return new Iterable<BxPage>() {

            @Override
            public Iterator<BxPage> iterator() {
                return pages.listIterator();
            }
        };
    }
    
    /**
     * 
     * @return a list of constituent zones. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public Iterable<BxZone> asZones() {
        return new Iterable<BxZone>() {
            
            @Override
            public Iterator<BxZone> iterator() {
                List<BxZone> zones = new ArrayList<BxZone>();
            	for (BxPage page : asPages()) {
                    for (BxZone zone : page) {
                        zones.add(zone);
                    }       
                }
                return zones.listIterator();
            }
        };
    }
    
    /**
     * 
     * @return a list of constituent text lines. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public Iterable<BxLine> asLines() {
        return new Iterable<BxLine>() {
            
            @Override
            public Iterator<BxLine> iterator() {
                List<BxLine> lines = new ArrayList<BxLine>();
            	for (BxZone zone : asZones()) {
                    for (BxLine line : zone) {
                        lines.add(line);
                    }       
                }
                return lines.listIterator();
            }
        };
    }
    
    /**
     * 
     * @return a list of constituent words. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public Iterable<BxWord> asWords() {
        return new Iterable<BxWord>() {
            
            @Override
            public Iterator<BxWord> iterator() {
                List<BxWord> words = new ArrayList<BxWord>();
            	for (BxLine line : asLines()) {
                    for (BxWord word : line) {
                        words.add(word);
                    }       
                }
                return words.listIterator();
            }
        };
    }
    
    /**
     * 
     * @return a list of constituent letters. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public Iterable<BxChunk> asChunks() {
    	return new Iterable<BxChunk>() {
            
            @Override
            public Iterator<BxChunk> iterator() {
                List<BxChunk> chunks = new ArrayList<BxChunk>();
            	for (BxWord word : asWords()) {
                    for (BxChunk chunk : word) {
                        chunks.add(chunk);
                    }       
                }
                return chunks.listIterator();
            }
        };
    }
    
    @Override
    public String getMostPopularFontName() {
        CountMap<String> map = new CountMap<String>();
        for (BxChunk chunk : asChunks()) {
            if (chunk.getFontName() != null) {
                map.add(chunk.getFontName());
            }    
        }
        return map.getMaxCountObject();
    }
    
    @Override
    public Set<String> getFontNames() {
        Set<String> names = new HashSet<String>();
        for (BxPage page : pages) {
            names.addAll(page.getFontNames());
        }
        return names;
    }

    @Override
    public Iterator<BxPage> iterator() {
        return pages.listIterator();
    }

    @Override
    public int childrenCount() {
        return pages.size();
    }

    @Override
    public BxPage getChild(int index) {
        if (index < 0 || index >= pages.size()) {
            throw new IndexOutOfBoundsException();
        }
        return pages.get(index);
    }
    
}
