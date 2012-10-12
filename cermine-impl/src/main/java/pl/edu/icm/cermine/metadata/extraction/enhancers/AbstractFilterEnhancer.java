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
abstract public class AbstractFilterEnhancer implements Enhancer {

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

    private static abstract class FilterIterable<T> implements Iterable<T> {

        private List<T> list;

        public FilterIterable(List<T> list) {
            this.list = list;
        }

        abstract protected boolean match(T zone, int index, List<T> zones);

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
