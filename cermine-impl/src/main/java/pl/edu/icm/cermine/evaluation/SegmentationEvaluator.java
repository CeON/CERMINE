package pl.edu.icm.cermine.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.PageSegmenter;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.structure.tools.UnsegmentedPagesFlattener;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author krusek
 */
public class SegmentationEvaluator extends AbstractSingleInputEvaluator<BxDocument, BxDocument, BxPage, SegmentationEvaluator.Results> {

    private static final String DEFAULT_CONFIGURATION_PATH =
            "pl/edu/icm/cermine/metadata/evaluation/segmentation-configuration.xml";

    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.+)\\.xml");
    
    private PageSegmenter pageSegmenter;

    private final Set<BxZoneLabel> ignoredLabels = EnumSet.noneOf(BxZoneLabel.class);

    private UnsegmentedPagesFlattener flattener = new UnsegmentedPagesFlattener();

	private final ReadingOrderResolver resolver = new HierarchicalReadingOrderResolver();

	private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();

	private BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();

    @Override
	protected Pattern getFilenamePattern() {
		return FILENAME_PATTERN;
	}
	
    public void setPageSegmenter(PageSegmenter pageSegmenter) {
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
    
    @Override
    protected void preprocessDocument(BxDocument document) {
        flattener.process(document);
    }

    @Override
    protected BxDocument processDocument(BxDocument document) throws AnalysisException {
        return pageSegmenter.segmentPages(document);
    }

    @Override
    protected Results compareItems(BxPage expected, BxPage actual) {
        Results results = new Results();
        results.zoneLevel = compareZones(expected, actual);
        results.lineLevel = compareLines(expected, actual);
        results.wordLevel = compareWords(expected, actual);
        return results;
    }

    private void printSeparator() {
        System.out.print(" +----------+");
        Results.printSeparator();
    }

    @Override
    protected void printDocumentStart() {
        System.out.print(" |   Page   |");
        Results.printLevelHeader();
        System.out.print(" |          |");
        Results.printColumnHeader();
        printSeparator();
    }

    @Override
    protected void printItemResults(BxPage expected, BxPage actual, int idx, Results results) {
    	printItemResults(idx, results);
    }
    
    protected void printItemResults(int pageIndex, Results results) {
        Formatter formatter = new Formatter(System.out, Locale.US);
        formatter.format(" | %8d |", pageIndex + 1);
        results.printResults(formatter);
    }

    @Override
    protected void printDocumentResults(Results results) {
        printSeparator();
        Formatter formatter = new Formatter(System.out, Locale.US);
        formatter.format(" |   Total: |");
        results.printResults(formatter);
    }

    @Override
    protected Results newResults() {
        return new Results();
    }

    @Override
    protected void printFinalResults(Results results) {
        results.printSummary();
    }

    private LevelResults compareWords(BxPage expected, BxPage actual) {
        Map<BxChunk, BxWord> map = BxModelUtils.mapChunksToWords(actual);

        LevelResults results = new LevelResults();
        for (BxZone expectedZone : expected.getZones()) {
            if (ignoredLabels.contains(expectedZone.getLabel())) {
                continue;
            }
            for (BxLine expectedLine : expectedZone.getLines()) {
                for (BxWord expectedWord : expectedLine.getWords()) {
                    Set<BxWord> actualWords = new HashSet<BxWord>();
                    for (BxChunk chunk : expectedWord.getChunks()) {
                        actualWords.add(map.get(chunk));
                    }
                    if (actualWords.size() == 1) {
                        for (BxWord actualWord : actualWords) {
                            if (actualWord.getChunks().size() == expectedWord.getChunks().size()) {
                                results.matched++;
                            }
                            else {
                                results.merged++;
                            }
                        }
                    }
                    else {
                        results.splitted++;
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
        for (BxZone expectedZone : expected.getZones()) {
            if (ignoredLabels.contains(expectedZone.getLabel())) {
                continue;
            }
            for (BxLine expectedLine : expectedZone.getLines()) {
                Set<BxLine> actualLines = new HashSet<BxLine>();
                for (BxWord word : expectedLine.getWords()) {
                    for (BxChunk chunk : word.getChunks()) {
                        actualLines.add(map.get(chunk));
                    }
                }
                if (actualLines.size() == 1) {
                    for (BxLine actualLine : actualLines) {
                        if (BxModelUtils.countChunks(actualLine) == BxModelUtils.countChunks(expectedLine)) {
                            results.matched++;
                        }
                        else {
                            results.merged++;
                        }
                    }
                }
                else {
                    results.splitted++;
                }
                results.all++;
            }
        }

        return results;
    }

    private LevelResults compareZones(BxPage expected, BxPage actual) {
        Map<BxChunk, BxZone> map = BxModelUtils.mapChunksToZones(actual);

        LevelResults results = new LevelResults();
        for (BxZone expectedZone : expected.getZones()) {
            if (ignoredLabels.contains(expectedZone.getLabel())) {
                continue;
            }
            Set<BxZone> actualZones = new HashSet<BxZone>();
            for (BxLine line : expectedZone.getLines()) {
                for (BxWord word : line.getWords()) {
                    for (BxChunk chunk : word.getChunks()) {
                        actualZones.add(map.get(chunk));
                    }
                }
            }
            if (actualZones.size() == 1) {
                for (BxZone actualZone : actualZones) {
                    if (BxModelUtils.countChunks(actualZone) == BxModelUtils.countChunks(expectedZone)) {
                        results.matched++;
                    }
                    else {
                        results.merged++;
                    }
                }
            }
            else {
                results.splitted++;
            }
            results.all++;
        }

        return results;
    }

	@Override
	protected BxDocument prepareActualDocument(BxDocument document) throws AnalysisException {
	    document = BxModelUtils.deepClone(document);
	    preprocessDocument(document);
	    return processDocument(document);
	}

	@Override
	protected BxDocument prepareExpectedDocument(BxDocument document) throws AnalysisException {
		resolver.resolve(document);
		return document;
	}

	@Override
	protected BxDocument readDocument(Reader input) throws TransformationException {
	    return new BxDocument().setPages(reader.read(input));
	}

	@Override
	protected void writeDocument(BxDocument document, Writer output) throws TransformationException {
        writer.write(output, document.getPages());
	}

	@Override
	protected Iterator<BxPage> iterateItems(BxDocument document) {
	    return document.getPages().iterator();
	}

	public static class Results implements AbstractEvaluator.Results<Results> {
        private LevelResults zoneLevel = new LevelResults();
        private LevelResults lineLevel = new LevelResults();
        private LevelResults wordLevel = new LevelResults();

        @Override
        public void add(Results results) {
            zoneLevel.add(results.zoneLevel);
            lineLevel.add(results.lineLevel);
            wordLevel.add(results.wordLevel);
        }

        public void printResults(Formatter formatter) {
            zoneLevel.printResults(formatter);
            lineLevel.printResults(formatter);
            wordLevel.printResults(formatter);
            formatter.format("\n");
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
        private int all;
        private int matched;
        private int splitted;
        private int merged;

        public void add(LevelResults results) {
            all += results.all;
            matched += results.matched;
            splitted += results.splitted;
            merged += results.merged;
        }

        public void printResults(Formatter formatter) {
            formatter.format(" %8d %8d %8d %8d %7.2f%% |",
                    all, matched, splitted, merged, getScore() * 100);
        }

        public static void printHeader() {
            System.out.print("   All    Matched  Splitted  Merged   Score   |");
        }

        public static void printSeparator() {
            System.out.print("----------------------------------------------+");
        }

        public void printSummary(Formatter formatter) {
            formatter.format("      * all      : %8d\n", all);
            formatter.format("      * matched  : %8d\n", matched);
            formatter.format("      * splitted : %8d\n", splitted);
            formatter.format("      * merged   : %8d\n", merged);
            formatter.format("      * score    : %7.2f%%\n", getScore() * 100);
        }

        public double getScore() {
            if (all == 0) {
                return 1.0;
            }
            else {
                return ((double) matched) / all;
            }
        }
    }
    
    public static void main(String[] args) throws AnalysisException, FileNotFoundException, IOException, TransformationException {
        main("SegmentationEvaluator", args, DEFAULT_CONFIGURATION_PATH);
    }
}
