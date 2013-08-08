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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * Abstract enhancer with page and zone filtering implementation.
 * 
 * @author krusek
 */
public abstract class AbstractFilterEnhancer implements Enhancer {

    private boolean searchedFirstPageOnly = false;

    private final Set<BxZoneLabel> searchedZoneLabels = EnumSet.allOf(BxZoneLabel.class);

    public void setSearchedFirstPageOnly(boolean value) {
        searchedFirstPageOnly = value;
    }

    public final void setSearchedZoneLabels(Collection<BxZoneLabel> zoneLabels) {
        searchedZoneLabels.clear();
        searchedZoneLabels.addAll(zoneLabels);
    }

    public void setSearchedZoneLabels(BxZoneLabel... zoneLabels) {
        setSearchedZoneLabels(Arrays.asList(zoneLabels));
    }

    protected Iterable<BxZone> filterZones(BxPage page) {
        return new FilterIterable<BxZone>(page.getZones()) {

            @Override
            protected boolean match(BxZone zone, int index, List<BxZone> zones) {
                return searchedZoneLabels.contains(zone.getLabel());
            }
        };
    }

    protected Iterable<BxPage> filterPages(BxDocument document) {
        return new FilterIterable<BxPage>(document.getPages()) {

            @Override
            protected boolean match(BxPage page, int index, List<BxPage> pages) {
                return !searchedFirstPageOnly || index == 0;
            }
        };
    }

    private abstract static class FilterIterable<T> implements Iterable<T> {

        private List<T> list;

        public FilterIterable(List<T> list) {
            this.list = list;
        }

        protected abstract boolean match(T zone, int index, List<T> zones);

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                private Iterator<T> iterator;
                private int index = -1;
                private T next = null;

                {
                    iterator = list.iterator();
                    findNext();
                }

                private void findNext() {
                    while (iterator.hasNext()) {
                        next = iterator.next();
                        index++;
                        if (match(next, index, list)) {
                            return;
                        }
                    }
                    index = -1;
                }

                @Override
                public boolean hasNext() {
                    return index != -1;
                }

                @Override
                public T next() {
                    T ret = this.next;
                    findNext();
                    return ret;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

            };
        }
    }
}
