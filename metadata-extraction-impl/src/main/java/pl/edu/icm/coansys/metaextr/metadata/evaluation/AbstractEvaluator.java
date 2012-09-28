package pl.edu.icm.coansys.metaextr.metadata.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 *
 * @author krusek
 * @param <P> prepared document type
 * @param <R> result type
 */
public abstract class AbstractEvaluator<P, R extends AbstractEvaluator.Results<R>> {

    protected Detail detail = Detail.FULL;

    /**
     * Creates new results object.
     *
     * @return newly created object for aggregating evaluation results
     */
    protected abstract R newResults();

    /**
     * Stores processed document.
     *
     * @param document document processed by evaluated processor
     * @param writer
     * @throws Exception
     */
    protected void writeDocument(P document, Writer writer) throws Exception {}

    protected void writeDocument(P document, String path) throws Exception {
        FileWriter writer = new FileWriter(path);
        try {
            writeDocument(document, writer);
        } finally {
            writer.close();
        }
    }

    protected void printDocumentStart() {}

    protected void printDocumentResults(R results) {}

    protected void printDocumentEnd() {
        System.out.println();
    }

    protected abstract void printFinalResults(R results);

    abstract protected R compareDocuments(P expected, P actual);

    abstract protected Documents<P> getDocuments(String directory, String filename) throws Exception;

    public void run(String inDir, String outDir) throws Exception {
        if (inDir == null) {
            throw new NullPointerException("Input directory must not be null.");
        }
        if (! inDir.endsWith(File.separator)) {
            inDir += File.separator;
        }
        if (outDir != null && ! outDir.endsWith(File.separator)) {
            outDir += File.separator;
        }
        R results = newResults();
        for (String filename : new File(inDir).list()) {
            if (!new File(inDir + filename).isFile()) {
                continue;
            }
            Documents<P> documents = getDocuments(inDir, filename);
            if (documents == null) {
                continue;
            }
            if (detail != Detail.MINIMAL) {
                System.out.println("=== Document " + filename);
                printDocumentStart();
            }
            R documentResults = compareDocuments(documents.getExpected(), documents.getActual());
            if (detail != Detail.MINIMAL) {
                printDocumentResults(documentResults);
                printDocumentEnd();
            }
            results.add(documentResults);
            if (outDir != null) {
                writeDocument(documents.getActual(), outDir + filename);
            }
        }
        System.out.println("=== Summary");
        printFinalResults(results);
    }

    public static void main(String programName, String[] args, String defaultConfigPath) throws Exception {
        Options options = new Options();
        options.addOption("compact", false, "do not print results for pages");
        options.addOption("config", true, "use given evaluator configuration file");
        options.addOption("help", false, "print this help message");
        options.addOption("minimal", false, "print only final summary");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(programName + " [-options] input-directory [output-directory]", options);
            }
            else {
                String[] remaining = line.getArgs();
                if (remaining.length == 0) {
                    throw new ParseException("Missing input directory.");
                }
                if (remaining.length > 2) {
                    throw new ParseException("Too many arguments.");
                }

                ApplicationContext context;
                if (line.hasOption("config")) {
                    String path = line.getOptionValue("config");
                    if (new File(path).isAbsolute()) {
                        path = "file:" + path;
                    }
                    context = new FileSystemXmlApplicationContext(path);
                }
                else {
                    context = new ClassPathXmlApplicationContext(defaultConfigPath);
                }
                AbstractEvaluator evaluator = (AbstractEvaluator) context.getBean("evaluator");
                if (line.hasOption("minimal")) {
                    evaluator.detail = Detail.MINIMAL;
                }
                else if(line.hasOption("compact")) {
                    evaluator.detail = Detail.COMPACT;
                }

                evaluator.run(remaining[0], remaining.length > 1 ? remaining[1] : null);
            }
        } catch (ParseException ex) {
            System.out.println("Parsing failed. Reason: " + ex.getMessage());
        }
    }

    public static enum Detail {
        FULL, COMPACT, MINIMAL;
    }
    
    public static interface Results<R> {
        public void add(R results);
    }

    protected static class Documents<P> {

        private P expected;
        private P actual;

        public Documents(P expected, P actual) {
            this.expected = expected;
            this.actual = actual;
        }

        public P getExpected() {
            return expected;
        }

        public P getActual() {
            return actual;
        }
    }
}
