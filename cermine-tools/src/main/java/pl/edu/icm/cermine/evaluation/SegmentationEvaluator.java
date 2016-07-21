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

package pl.edu.icm.cermine.evaluation;

import com.google.common.collect.Lists;
import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.DocstrumSegmenter;
import pl.edu.icm.cermine.structure.DocumentSegmenter;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.structure.tools.UnsegmentedPagesFlattener;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Krzysztof Rusek
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SegmentationEvaluator {

    private DocumentSegmenter pageSegmenter = new DocstrumSegmenter();

    private final Set<BxZoneLabel> ignoredLabels = EnumSet.noneOf(BxZoneLabel.class);

    private final UnsegmentedPagesFlattener flattener = new UnsegmentedPagesFlattener();

    private final ReadingOrderResolver resolver = new HierarchicalReadingOrderResolver();

    private final TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();

    private final BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();

    public void setPageSegmenter(DocumentSegmenter pageSegmenter) {
        this.pageSegmenter = pageSegmenter;
    }

    public void setIgnoredLabels(Collection<BxZoneLabel> labels) {
        ignoredLabels.clear();
        ignoredLabels.addAll(labels);
    }

    public void setLabels(Collection<BxZoneLabel> labels) {
        ignoredLabels.addAll(EnumSet.allOf(BxZoneLabel.class));
        ignoredLabels.removeAll(labels);
    }

    protected void preprocessDocument(BxDocument document) {
        flattener.process(document);
    }

    protected BxDocument processDocument(BxDocument document) throws AnalysisException {
        return pageSegmenter.segmentDocument(document);
    }

    private void printSeparator() {
        System.out.print(" +----------+");
        Results.printSeparator();
    }

    protected void printDocumentStart() {
        System.out.print(" |   Page   |");
        Results.printLevelHeader();
        System.out.print(" |          |");
        Results.printColumnHeader();
        printSeparator();
    }

    protected void printItemResults(BxDocument expected, BxDocument actual, int idx, Results results) {
        printItemResults(idx, results);
    }

    protected void printItemResults(int pageIndex, Results results) {
        Formatter formatter = new Formatter(System.out, Locale.US);
        formatter.format(" | %8d |", pageIndex + 1);
        results.printResults(formatter);
    }

    protected void printDocumentResults(Results results) {
        printSeparator();
        Formatter formatter = new Formatter(System.out, Locale.US);
        formatter.format(" |   Total: |");
        results.printResults(formatter);
    }

    protected void printFinalResults(Results results) {
        results.printSummary();
    }

    private LevelResults compareWords(BxPage expected, BxPage actual) {
        Map<BxChunk, BxWord> map = BxModelUtils.mapChunksToWords(actual);

        LevelResults results = new LevelResults();
        for (BxZone expectedZone : expected) {
            if (ignoredLabels.contains(expectedZone.getLabel())) {
                continue;
            }
            for (BxLine expectedLine : expectedZone) {
                for (BxWord expectedWord : expectedLine) {
                    Set<BxWord> actualWords = new HashSet<BxWord>();
                    for (BxChunk chunk : expectedWord) {
                        actualWords.add(map.get(chunk));
                    }
                    if (actualWords.size() == 1) {
                        for (BxWord actualWord : actualWords) {
                            if (actualWord.childrenCount() == expectedWord.childrenCount()) {
                                results.matched++;
                            }
                        }
                    }
                    results.all++;
                }
            }

        }

        return results;
    }

    private LevelResults compareLines(BxPage expected, BxPage actual) {
        Map<BxChunk, BxLine> map = BxModelUtils.mapChunksToLines(actual);

        LevelResults results = new LevelResults();
        for (BxZone expectedZone : expected) {
            if (ignoredLabels.contains(expectedZone.getLabel())) {
                continue;
            }
            for (BxLine expectedLine : expectedZone) {
                Set<BxLine> actualLines = new HashSet<BxLine>();
                for (BxWord word : expectedLine) {
                    for (BxChunk chunk : word) {
                        actualLines.add(map.get(chunk));
                    }
                }
                if (actualLines.size() == 1) {
                    for (BxLine actualLine : actualLines) {
                        if (BxModelUtils.countChunks(actualLine) == BxModelUtils.countChunks(expectedLine)) {
                            results.matched++;
                        }
                    }
                }
                results.all++;
            }
        }

        return results;
    }

    private LevelResults compareZones(BxPage expected, BxPage actual) {
        Map<BxChunk, BxZone> map = BxModelUtils.mapChunksToZones(actual);

        LevelResults results = new LevelResults();
        for (BxZone expectedZone : expected) {
            if (ignoredLabels.contains(expectedZone.getLabel())) {
                continue;
            }
            Set<BxZone> actualZones = new HashSet<BxZone>();
            for (BxLine line : expectedZone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        actualZones.add(map.get(chunk));
                    }
                }
            }
            if (actualZones.size() == 1) {
                for (BxZone actualZone : actualZones) {
                    if (BxModelUtils.countChunks(actualZone) == BxModelUtils.countChunks(expectedZone)) {
                        results.matched++;
                    }
                }
            }
            results.all++;
        }

        return results;
    }

    protected Results compareItems(BxDocument expected, BxDocument actual) {
        Results results = new Results();
        for (int i = 0; i < expected.childrenCount(); i++) {
            BxPage expPage = expected.getChild(i);
            BxPage actPage = actual.getChild(i);
            results.zoneLevel.add(compareZones(expPage, actPage));
            results.lineLevel.add(compareLines(expPage, actPage));
            results.wordLevel.add(compareWords(expPage, actPage));
        }
        return results;
    }
    
    protected BxDocument prepareActualDocument(BxDocument document) throws AnalysisException {
        document = BxModelUtils.deepClone(document);
        preprocessDocument(document);
        return processDocument(document);
    }

    protected BxDocument prepareExpectedDocument(BxDocument document) throws AnalysisException {
        resolver.resolve(document);
        return document;
    }

    protected BxDocument readDocument(Reader input) throws TransformationException {
        return new BxDocument().setPages(reader.read(input));
    }

    protected void writeDocument(BxDocument document, Writer output) throws TransformationException {
        writer.write(output, Lists.newArrayList(document));
    }

    public static class Results {

        private LevelResults zoneLevel = new LevelResults();
        private LevelResults lineLevel = new LevelResults();
        private LevelResults wordLevel = new LevelResults();

        public void add(Results results) {
            zoneLevel.add(results.zoneLevel);
            lineLevel.add(results.lineLevel);
            wordLevel.add(results.wordLevel);
        }

        public void printResults(Formatter formatter) {
            zoneLevel.printResults(formatter);
            lineLevel.printResults(formatter);
            wordLevel.printResults(formatter);
            formatter.format("%n");
        }

        public static void printLevelHeader() {
            System.out.print("                    Zones                     |");
            System.out.print("                    Lines                     |");
            System.out.print("                    Words                     |");
            System.out.println();
        }

        public static void printColumnHeader() {
            LevelResults.printHeader();
            LevelResults.printHeader();
            LevelResults.printHeader();
            System.out.println();
        }

        public static void printSeparator() {
            LevelResults.printSeparator();
            LevelResults.printSeparator();
            LevelResults.printSeparator();
            System.out.println();
        }

        public void printSummary() {
            Formatter formatter = new Formatter(System.out, Locale.US);
            System.out.println("  * zones");
            zoneLevel.printSummary(formatter);
            System.out.println("  * lines");
            lineLevel.printSummary(formatter);
            System.out.println("  * words");
            wordLevel.printSummary(formatter);
        }
    }

    public static class LevelResults {

        private int all = 0;
        private int matched = 0;

        public void add(LevelResults results) {
            all += results.all;
            matched += results.matched;
        }

        public void printResults(Formatter formatter) {
            formatter.format(" %8d %8d %7.2f%% |",
                    all, matched, getScore() * 100);
        }

        public static void printHeader() {
            System.out.print("   All    Matched  Score   |");
        }

        public static void printSeparator() {
            System.out.print("----------------------------------------------+");
        }

        public void printSummary(Formatter formatter) {
            formatter.format("      * all      : %8d%n", all);
            formatter.format("      * matched  : %8d%n", matched);
            formatter.format("      * score    : %7.2f%%%n", getScore() * 100);
        }

        public double getScore() {
            if (all == 0) {
                return 1.0;
            } else {
                return ((double) matched) / all;
            }
        }
    }

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException {

        SegmentationEvaluator evaluator = new SegmentationEvaluator();
        evaluator.ignoredLabels.add(BxZoneLabel.BODY_TABLE);
        evaluator.ignoredLabels.add(BxZoneLabel.BODY_FIGURE);
        evaluator.ignoredLabels.add(BxZoneLabel.BODY_EQUATION);

        File file = new File(args[0]);
        Collection<File> files = FileUtils.listFiles(file, new String[]{"xml"}, true);
        Results results = new Results();
        int i = 0;

        double zoneScores = 0;
        double lineScores = 0;
        double wordScores = 0;
        BxDocument origDoc;
        BxDocument testDoc;
        FileReader reader;
        for (File filee : files) {
            System.out.println(new Date(System.currentTimeMillis()));
            System.out.println(filee.getName());

            reader = new FileReader(filee);
            origDoc = evaluator.prepareExpectedDocument(evaluator.readDocument(reader));
            testDoc = evaluator.prepareActualDocument(origDoc);
            Results docRes = evaluator.compareItems(origDoc, testDoc);
            results.add(docRes);
            zoneScores += results.zoneLevel.getScore();
            lineScores += results.lineLevel.getScore();
            wordScores += results.wordLevel.getScore();
            System.out.println(++i);
        }
        zoneScores /= i;
        lineScores /= i;
        wordScores /= i;
        System.out.println("Documents: " + i);
        System.out.println("Average zone score: " + zoneScores);
        System.out.println("Average line score: " + lineScores);
        System.out.println("Average word score: " + wordScores);
        results.printSummary();
    }
}
