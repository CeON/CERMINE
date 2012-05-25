package pl.edu.icm.yadda.analysis.bibref.manual;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pl.edu.icm.yadda.analysis.bibref.manual.search.LocalSearchStrategy;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.tools.bibref.model.BibReferenceTriple;

/**
 * Does manual citation match for NLM files with parsed citations.
 * 
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 *
 */
public class BibReferenceMatcherExecutor {

    public static void main(String[] args) throws TransformationException, IOException {
        if (args.length != 1) {
            System.out.println("Specify source directory");
            return;
        }
        String inDir = args[0];
        //processFile(new File(inDir, "plain.xml"));
        for (File f : new File(inDir).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("xml");
            }
        })) {
            System.out.println("Processing " + f + "...");
            processFile(f);
        }
    }

    /**
     * Reads file contents into a string
     * @param path a file path
     * @return the file contents
     * @throws IOException
     */
    private static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }
    
    /**
     * Does matching for a given NLM file. It should contain references list with parsed citations.
     * @param f a file to process
     * @throws IOException
     * @throws TransformationException
     */
    private static void processFile(File f) throws IOException, TransformationException {
        MetadataBibReferenceMatcher matcher = new MetadataBibReferenceMatcher();
        matcher.setSearchStrategy(new LocalSearchStrategy());
        String nlm = readFile(f.getAbsolutePath());
        nlm = nlm.replaceAll("<mixed-citation[^<]*</mixed-citation>", "");
        Set<BibReferenceTriple> results = matcher.matchBibReferencedIds(nlm);
        Map<Integer, String> mapping = new HashMap<Integer, String>();
        for (BibReferenceTriple triple : results) {
            mapping.put(triple.getBibReferencePosition(), triple.getBibReferenceId());
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(f.getAbsolutePath() + "_result")));
        for (int i = 1; i <= 1200; ++i) {
            if (mapping.containsKey(i)) {
                writer.write(mapping.get(i) + "\n");
            } else {
                writer.write("None\n");
            }
        }
        writer.close();
    }

}
