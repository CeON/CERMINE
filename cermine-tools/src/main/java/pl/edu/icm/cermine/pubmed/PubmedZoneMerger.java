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
package pl.edu.icm.cermine.pubmed;

import com.google.common.collect.Lists;
import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.DisjointSets;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PubmedZoneMerger {

    public static void main(String[] args) throws TransformationException, AnalysisException, IOException {
        ReadingOrderResolver roResolver = new HierarchicalReadingOrderResolver();
        File dir = new File(args[0]);
        int i = 0;
        Collection<File> files = FileUtils.listFiles(dir, new String[]{args[1]}, true);
        for (File tv : files) {
            System.out.println(tv.getPath());

            String newPath = tv.getPath().replaceFirst(args[1], args[2]);
            File newFile = new File(newPath);
            if (newFile.exists()) {
                i++;
                continue;
            }

            InputStream is = new FileInputStream(tv);
            TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
            Reader r = new InputStreamReader(is);
            BxDocument bxDoc = new BxDocument().setPages(reader.read(r));

            double avgDiffZone = 0;
            int countDZ = 0;

            for (BxLine line : bxDoc.asLines()) {
                if (!line.hasNext()) {
                    continue;
                }

                double y1 = line.getY();
                double y2 = line.getNext().getY();
                double h1 = line.getHeight();
                double h2 = line.getNext().getHeight();

                if (y1 >= y2) {
                    continue;
                }

                double x1 = line.getX();
                double x1e = line.getX() + line.getWidth();
                double x2 = line.getNext().getX();
                double x2e = line.getNext().getX() + line.getNext().getWidth();

                if (x1e < x2 || x2e < x1 || (x1 + 5 < x2 && x1e + 5 < x2e) || (x2 + 5 < x1 && x2e + 5 < x1e)) {
                    continue;
                }

                double diff = y2 - y1 - h1;
                if (line.getParent().equals(line.getNext().getParent())) {
                    avgDiffZone += diff;
                    countDZ++;
                }
            }

            avgDiffZone /= countDZ;

            for (BxPage page : bxDoc) {
                List<BxLine> lines = new ArrayList<BxLine>();
                for (BxZone z : page) {
                    for (BxLine l : z) {
                        lines.add(l);
                    }
                }
                DisjointSets<BxLine> dLines = new DisjointSets<BxLine>(lines);
                for (BxLine line : lines) {
                    for (BxLine ll : lines) {
                        if (line.getParent().equals(ll.getParent())) {
                            dLines.union(line, ll);
                        }
                    }
                }

                for (BxLine line : lines) {
                    if (!line.hasNext()) {
                        continue;
                    }

                    double y1 = line.getY();
                    double y2 = line.getNext().getY();
                    double h1 = line.getHeight();
                    double h2 = line.getNext().getHeight();

                    if (y1 >= y2) {
                        continue;
                    }

                    double x1 = line.getX();
                    double x1e = line.getX() + line.getWidth();
                    double x2 = line.getNext().getX();
                    double x2e = line.getNext().getX() + line.getNext().getWidth();

                    if (x1e < x2 || x2e < x1 || (x1 + 5 < x2 && x1e + 5 < x2e) || (x2 + 5 < x1 && x2e + 5 < x1e)) {
                        continue;
                    }

                    double diff = y2 - y1 - h1;
                    if (!line.getParent().equals(line.getNext().getParent())
                            && (line.getParent().getLabel().equals(line.getNext().getParent().getLabel())
                            || line.getParent().getLabel().equals(BxZoneLabel.OTH_UNKNOWN)
                            || line.getParent().getNext().getLabel().equals(BxZoneLabel.OTH_UNKNOWN))) {
                        if (diff < 4.5 && Math.abs(diff - avgDiffZone) < 4.5) {
                            dLines.union(line, line.getNext());
                        }
                    }
                }

                Iterator<Set<BxLine>> it = dLines.iterator();
                List<Set<BxLine>> l = new ArrayList<Set<BxLine>>();
                while (it.hasNext()) {
                    l.add(it.next());
                }
                for (Set<BxLine> l1 : l) {
                    for (Set<BxLine> l2 : l) {
                        if (l1.equals(l2)) {
                            continue;
                        }
                        BxBoundsBuilder b1 = new BxBoundsBuilder();
                        for (BxLine ll1 : l1) {
                            b1.expand(ll1.getBounds());
                        }
                        BxBounds bb1 = b1.getBounds();

                        BxBoundsBuilder b2 = new BxBoundsBuilder();
                        for (BxLine ll2 : l2) {
                            b2.expand(ll2.getBounds());
                        }
                        BxBounds bb2 = b2.getBounds();

                        if (l1.iterator().next().getParent().getLabel().equals(l2.iterator().next().getParent().getLabel())
                                && bb1.getX() <= bb2.getX() + bb2.getWidth()
                                && bb2.getX() <= bb1.getX() + bb1.getWidth()
                                && bb1.getY() <= bb2.getY() + bb2.getHeight()
                                && bb2.getY() <= bb1.getY() + bb1.getHeight()) {
                            dLines.union(l1.iterator().next(), l2.iterator().next());
                        }
                    }
                }

                page.setZones(new ArrayList<BxZone>());
                it = dLines.iterator();
                while (it.hasNext()) {
                    Set<BxLine> group = it.next();
                    BxBoundsBuilder builder = new BxBoundsBuilder();
                    BxZoneLabel label = null;
                    BxZone zone = new BxZone();

                    List<BxLine> mylines = new ArrayList<BxLine>();
                    mylines.addAll(group);
                    Collections.sort(mylines, new Comparator<BxLine>() {

                        @Override
                        public int compare(BxLine t, BxLine t1) {
                            return Double.compare(t1.getY(), t.getY());
                        }
                    });

                    BxLine prev = null;
                    for (BxLine line : mylines) {
                        label = line.getParent().getLabel();
                        builder.expandByWords(line);

                        if (prev != null) {
                            if (Math.abs(prev.getY() - line.getY()) < 1) {
                                for (BxWord w : line) {
                                    prev.addWord(w);
                                    w.setParent(prev);
                                }
                            } else {
                                zone.addLine(prev);
                                prev.setParent(zone);
                                prev = line;
                            }
                        } else {
                            prev = line;
                        }
                    }
                    if (prev != null) {
                        zone.addLine(prev);
                        prev.setParent(zone);
                    }

                    zone.setLabel(label);
                    zone.setBounds(builder.getBounds());

                    page.addZone(zone);
                }
            }

            roResolver.resolve(bxDoc);

            FileWriter fstream = new FileWriter(newPath);
            BufferedWriter out = new BufferedWriter(fstream);
            BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
            out.write(writer.write(Lists.newArrayList(bxDoc)));
            out.close();
            i++;
            System.out.println("Progress: " + i + " out of " + files.size() + " (" + (i * 100. / files.size()) + "%)");
        }
    }

}
