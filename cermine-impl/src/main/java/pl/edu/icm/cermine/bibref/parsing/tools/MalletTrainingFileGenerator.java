package pl.edu.icm.cermine.bibref.parsing.tools;

import java.io.*;
import java.util.Map.Entry;
import java.util.*;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class MalletTrainingFileGenerator {
    
    private static String nlmDir = "/tmp/train/";
    private static String outFile = "/tmp/crf-train.txt";
    private static int minCount = 10;

    public static void main(String[] args) throws JDOMException, IOException {
        
        File dir = new File(nlmDir);
        FileWriter writer = null;
        try {
        writer = new FileWriter(outFile);
        
        Set<Citation> allcitations = new HashSet<Citation>();
        
        Map<String, Integer> wordMap = new HashMap<String, Integer>();
        
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }
            if (!file.getName().endsWith(".nxml")) {
                continue;
            }
            
            InputStream is = null;
            List<Citation> citations;
            try {
                is = new FileInputStream(file);
                InputSource source = new InputSource(is);
                citations = NlmCitationExtractor.extractCitations(source);
            } finally {
                if (is != null) {
                    is.close();
                }
                writer.close();
            }
            
            for (Citation citation : citations) {
                allcitations.add(citation);
                for (CitationToken citationToken : citation.getTokens()) {
                    if (citationToken.getText().matches("^[a-zA-Z]+$")) {
                        String word = citationToken.getText().toLowerCase();
                        if (wordMap.get(word) == null) {
                            wordMap.put(word, 0);
                        }
                        wordMap.put(word, wordMap.get(word) + 1);
                    }
                }
            }
        }
        
        List<Entry<String, Integer>> wordCounts = new ArrayList<Entry<String, Integer>>();
        for (Entry<String, Integer> entry : wordMap.entrySet()) {
            wordCounts.add(entry);
        }
        Collections.sort(wordCounts, new Comparator<Entry<String, Integer>>() {

            @Override
            public int compare(Entry<String, Integer> t1, Entry<String, Integer> t2) {
                if (t1.getValue().compareTo(t2.getValue()) != 0) {
                    return t2.getValue().compareTo(t1.getValue());
                }
                return t1.getKey().compareTo(t2.getKey());
            }

        });
     
        Set<String> additionalFeatures = new HashSet<String>();
        
        for (Entry<String, Integer> wordCount : wordCounts) {
            if (wordCount.getValue() > minCount) {
                additionalFeatures.add(wordCount.getKey());
            }
        }
        
        for (Citation citation : allcitations) {
            List<String> tokens = CitationUtils.citationToMalletInputFormat(citation);
            try {
                for (String token : tokens) {
                    writer.write(token);
                    writer.write("\n");
                }
                writer.write("\n");
            } finally {
                writer.close();
            }
        }
            
        writer.flush();
        } finally {
        	if(writer != null) {
        		writer.close();
        	}
        }
    }

    private MalletTrainingFileGenerator() {
    }

}
