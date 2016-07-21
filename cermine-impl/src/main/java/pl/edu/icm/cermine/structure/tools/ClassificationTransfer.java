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

package pl.edu.icm.cermine.structure.tools;

import java.util.*;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Krzysztof Rusek
 */
public class ClassificationTransfer {

    private static final double TOLERANCE = 1.0e-3;

    public void transferClassification(BxDocument source, BxDocument target) {
        if (source.childrenCount() != target.childrenCount()) {
            throw new IllegalArgumentException("Page counts of the documents must be equal");
        }
        for (int i = 0; i < source.childrenCount(); i++) {
            transferClassification(source.getChild(i), target.getChild(i));
        }
    }

    public void transferClassification(BxPage source, BxPage target) {
        Map<String, Map<BxChunk, Entry>> map = new HashMap<String, Map<BxChunk, Entry>>();
        List<Entry> entries = new ArrayList<Entry>();
        for (BxZone zone : target) {
            Entry entry = new Entry();
            entry.zone = zone;
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        Map<BxChunk, Entry> map2 = map.get(chunk.toText());
                        if (map2 == null) {
                            map2 = new HashMap<BxChunk, Entry>();
                            map.put(chunk.toText(), map2);
                        }
                        map2.put(chunk, entry);
                    }
                }
            }
            entries.add(entry);
        }

        for (BxZone zone : source) {
            if (zone.getLabel() == null || zone.getLabel() == BxZoneLabel.OTH_UNKNOWN) {
                continue;
            }
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    chunkLoop: for (BxChunk chunk : word) {
                        Map<BxChunk, Entry> map2 = map.get(chunk.toText());
                        if (map2 != null) {
                            for(Map.Entry<BxChunk, Entry> mapEntry : map2.entrySet()) {
                                if (mapEntry.getKey().getBounds().isSimilarTo(chunk.getBounds(), TOLERANCE)) {
                                    mapEntry.getValue().hit(zone.getLabel());
                                    continue chunkLoop;
                                }
                            }
                        }
                    }
                }
            }
        }

        for(Entry entry : entries) {
            entry.zone.setLabel(entry.findBest());
        }
    }

    private static class Entry {
        BxZone zone;
        Map<BxZoneLabel, Integer> hits = new EnumMap<BxZoneLabel, Integer>(BxZoneLabel.class);

        Entry() {
            for(BxZoneLabel label : BxZoneLabel.values()) {
                hits.put(label, 0);
            }
        }

        void hit(BxZoneLabel label) {
            hits.put(label, hits.get(label) + 1);
        }

        BxZoneLabel findBest() {
            BxZoneLabel best = BxZoneLabel.OTH_UNKNOWN;
            int bestHits = 0;
            for (Map.Entry<BxZoneLabel, Integer> entry : hits.entrySet()) {
                if (entry.getValue() > bestHits) {
                    bestHits = entry.getValue();
                    best = entry.getKey();
                }
            }
            return best;
        }
    }
}
