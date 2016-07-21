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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * Abstract enhancer with page and zone filtering implementation.
 * 
 * @author Krzysztof Rusek
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
        return new FilterIterable<BxZone>(page) {

            @Override
            protected boolean match(BxZone zone) {
                return searchedZoneLabels.contains(zone.getLabel());
            }
        };
    }

    protected Iterable<BxPage> filterPages(BxDocument document) {
        return new FilterIterable<BxPage>(document) {

            @Override
            protected boolean match(BxPage page) {
                return !searchedFirstPageOnly || !page.hasPrev();
            }
        };
    }

    private abstract static class FilterIterable<T> implements Iterable<T> {

        private final Iterable<T> it;

        public FilterIterable(Iterable<T> it) {
            this.it = it;
        }

        protected abstract boolean match(T zone);

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                private final Iterator<T> iterator;
                private T next = null;

                {
                    iterator = it.iterator();
                    findNext();
                }

                private void findNext() {
                    if (!iterator.hasNext()) {
                        next = null;
                    }
                    while (iterator.hasNext()) {
                        next = iterator.next();
                        if (match(next)) {
                            break;
                        }
                    }
                    if (next != null && !iterator.hasNext() && !match(next)) {
                        next = null;
                    }
                }

                @Override
                public boolean hasNext() {
                    return next != null;
                }

                @Override
                public T next() {
                    T ret = this.next;
                    findNext();
                    return ret;
                }

                @Override
                public void remove() {
                }

            };
        }
    }
}
