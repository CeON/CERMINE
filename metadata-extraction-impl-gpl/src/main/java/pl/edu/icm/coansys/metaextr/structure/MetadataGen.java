package pl.edu.icm.coansys.metaextr.structure;

import java.io.*;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import pl.edu.icm.coansys.metaextr.bibref.CRFBibReferenceParser;
import pl.edu.icm.coansys.metaextr.bibref.ClusteringBibReferenceExtractor;
import pl.edu.icm.coansys.metaextr.bibref.model.BibEntry;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.exception.TransformationException;
import pl.edu.icm.coansys.metaextr.metadata.EnhancerMetadataExtractor;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.readingorder.HierarchicalReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.structure.tools.BxModelUtils;
import pl.edu.icm.coansys.metaextr.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.coansys.metaextr.tools.classification.svm.SVMZoneClassifier;


/**
 *
 * @author Dominika Tkaczyk
 */
public class MetadataGen {
    
    static double seconds = 0.0;
    
    private static void startTime() {
        seconds = System.currentTimeMillis() / 1000;
    }
    
    private static void endTime(String task) {
        double diff = System.currentTimeMillis() / 1000 - seconds;
        System.out.println("TIME "+task +" " + diff);
    }

    public static void main(String[] args) throws IOException, TransformationException, AnalysisException {
        String dir = "/home/domin/toxml/";
       
        CharacterExtractor glyphExtractor = new ITextCharacterExtractor();
        
        DocstrumPageSegmenter pageSegmenter = new DocstrumPageSegmenter();
        
        ReadingOrderResolver roRes = new HierarchicalReadingOrderResolver();
        
        InputStreamReader modelISR = new InputStreamReader(MetadataGen.class
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_initial_classifier"));
        BufferedReader modelFile = new BufferedReader(modelISR);
        InputStreamReader rangeISR = new InputStreamReader(MetadataGen.class
		        .getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_initial_classifier.range"));
        BufferedReader rangeFile = new BufferedReader(rangeISR);
        SVMZoneClassifier class1 = new SVMInitialZoneClassifier(modelFile, rangeFile);
        
        ClusteringBibReferenceExtractor extr = new ClusteringBibReferenceExtractor();
        
        CRFBibReferenceParser parser = new CRFBibReferenceParser();
        
        InputStreamReader modelISR2 = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_metadata_classifier"));
        BufferedReader modelFile2 = new BufferedReader(modelISR2);
        InputStreamReader rangeISR2 = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/svm_metadata_classifier.range"));
        BufferedReader rangeFile2 = new BufferedReader(rangeISR2);
        SVMZoneClassifier classifier = new SVMMetadataZoneClassifier(modelFile2, rangeFile2);
        
        EnhancerMetadataExtractor extractor = new EnhancerMetadataExtractor();
        
        File folder = new File(dir);
        for (File file : folder.listFiles()) {
            if (!file.getName().matches("^.*\\.pdf$")) {
                continue;
            }
            File xf = new File(dir + file.getName().replaceFirst("\\.pdf$", "") + ".xml");
            File mf = new File(dir + file.getName().replaceFirst("\\.pdf$", "") + ".met");
            File bf = new File(dir + file.getName().replaceFirst("\\.pdf$", "") + ".bib");

            System.out.println(file.getName());
            
            startTime();
            FileInputStream is = new FileInputStream(file);
            endTime("openfile");
            
            startTime();
            BxDocument doc = glyphExtractor.extractCharacters(is);
            endTime("characters extraction");
            
            startTime();
            doc = pageSegmenter.segmentPages(doc);
            endTime("page segmentation");
            
            startTime();
            BxModelUtils.setParents(doc);
            endTime("setting doc's elements' parents");
            
            startTime();
            doc = roRes.resolve(doc);
            endTime("reading order");
            
            startTime();
            doc = class1.classifyZones(doc);
            endTime("initial classification");
            
            startTime();
            String[] refs = extr.extractBibReferences(doc);
            endTime("refs extraction");
            
            FileWriter bWriter = new FileWriter(bf);
            
            startTime();
            for (String ref : refs ) {
                BibEntry parsed = parser.parseBibReference(ref);
                bWriter.write(parsed.toBibTeX());
                bWriter.write("\n");
            }
            endTime("refs parsing");
            
            bWriter.flush();
            bWriter.close();

            startTime();
            doc = classifier.classifyZones(doc);
            endTime("metadata classification");

            startTime();
            Element el = extractor.extractMetadata(doc);
            endTime("metadata etxraction");
            
            XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
            String s = outp.outputString(el);
            FileWriter mWriter = new FileWriter(mf);
            mWriter.write(s);
            mWriter.flush();
            mWriter.close();
            
            BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
            FileWriter fWriter = new FileWriter(xf);
            writer.write(fWriter, doc.getPages());
        }
    }
   
}
