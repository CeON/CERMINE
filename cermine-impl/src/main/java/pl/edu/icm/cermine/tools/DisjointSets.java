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

package pl.edu.icm.cermine.tools;

import java.util.*;

/**
 * A disjoint-set data structure.
 *
 * @author Krzysztof Rusek
 * @param <E> element type
 */
public class DisjointSets<E> implements Iterable<Set<E>> {

    private final Map<E, Entry<E>> map = new HashMap<E, Entry<E>>();

    /**
     * Constructs a new set of singletons.
     *
     * @param c elements of singleton sets
     */
    public DisjointSets(Collection<? extends E> c) {
        for (E element : c) {
            map.put(element, new Entry<E>(element));
        }
    }

    /**
     * Checks if elements are in the same subsets.
     *
     * @param e1 element from a subset
     * @param e2 element from a subset
     * @return true if elements are in the same subset; false otherwise
     */
    public boolean areTogether(E e1, E e2) {
        return map.get(e1).findRepresentative() == map.get(e2).findRepresentative();
    }

    /**
     * Merges subsets which elements e1 and e2 belong to.
     *
     * @param e1 element from a subset
     * @param e2 element from a subset
     */
    public void union(E e1, E e2) {
        Entry<E> r1 = map.get(e1).findRepresentative();
        Entry<E> r2 = map.get(e2).findRepresentative();
        if (r1 != r2) {
            if (r1.size <= r2.size) {
                r2.mergeWith(r1);
            } else {
                r1.mergeWith(r2);
            }
        }
    }

    @Override
    public Iterator<Set<E>> iterator() {
        return new Iterator<Set<E>>() {

            private final Iterator<Entry<E>> iterator = map.values().iterator();
            private Entry<E> nextRepresentative;

            {
                findNextRepresentative();
            }

            @Override
            public boolean hasNext() {
                return nextRepresentative != null;
            }

            @Override
            public Set<E> next() {
                if (nextRepresentative == null) {
                    throw new NoSuchElementException();
                }
                Set<E> result = nextRepresentative.asSet();
                findNextRepresentative();
                return result;
            }

            private void findNextRepresentative() {
                while(iterator.hasNext()) {
                    Entry<E> candidate = iterator.next();
                    if (candidate.isRepresentative()) {
                        nextRepresentative = candidate;
                        return;
                    }
                }
                nextRepresentative = null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    public static <E extends Enum<E>> DisjointSets<E> singletonsOf(Class<E> elementType) {
        return new DisjointSets(Arrays.asList(elementType.getEnumConstants()));
    }

    private static class Entry<E> {
        private int size = 1;
        private final E value;
        private Entry<E> parent = this;
        private Entry<E> next = null;
        private Entry<E> last = this;

        Entry(E value) {
            this.value = value;
        }

        void mergeWith(Entry<E> otherRepresentative) {
            size += otherRepresentative.size;
            last.next = otherRepresentative;
            last = otherRepresentative.last;
            otherRepresentative.parent = this;
        }

        Entry<E> findRepresentative() {
            Entry<E> representative = parent;
            while (representative.parent != representative) {
                representative = representative.parent;
            }
            for (Entry<E> entry = this; entry != representative; ) {
                Entry<E> nextEntry = entry.parent;
                entry.parent = representative;
                entry = nextEntry;
            }
            return representative;
        }

        boolean isRepresentative() {
            return parent == this;
        }

        Set<E> asSet() {
            return new AbstractSet<E>() {

                @Override
                public Iterator<E> iterator() {
                    return new Iterator<E>() {

                        private Entry<E> nextEntry = findRepresentative();

                        @Override
                        public boolean hasNext() {
                            return nextEntry != null;
                        }

                        @Override
                        public E next() {
                            if (nextEntry == null) {
                                throw new NoSuchElementException();
                            }
                            E result = nextEntry.value;
                            nextEntry = nextEntry.next;
                            return result;
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                    };
                }

                @Override
                public int size() {
                    return findRepresentative().size;
                }
            };
        }
    }
}
