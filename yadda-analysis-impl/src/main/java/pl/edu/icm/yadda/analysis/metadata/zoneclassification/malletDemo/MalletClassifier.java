package pl.edu.icm.yadda.analysis.metadata.zoneclassification.malletDemo;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.Trial;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.Labeling;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author Krzysztof Werys
 */
public class MalletClassifier {
    
    private Classifier classifier;
    
    XMLToMalletFormatConverter converter;
    private static String directoryForTemporaryFiles = Utils.getPathOfResource("/pl/edu/icm/yadda/analysis/metadata/zoneclassification/trainingdata/");
    
    public MalletClassifier(MalletTrainer trainer) {
        this.classifier = trainer.getClassifier();
        this.converter = new XMLToMalletFormatConverter();
    }
   
    
    public BxDocument classify(String XMLPath) throws IOException, TransformationException {
        
        String pathOfAFileInMalletFormat = directoryForTemporaryFiles + "temp.txt";
        converter.setReturnFilePath(pathOfAFileInMalletFormat);
        File fileInMalletFormat = converter.convertFile(XMLPath);
        
        List<String> classifiedMetadataLabels = classifyAndGetLabels(fileInMalletFormat);
        
        Utils utils = new Utils();
        BxDocument bxDocument = utils.getBxDocumentFromXML(XMLPath);
        
        int metadataZoneIndex = 0;
        for (BxPage page : bxDocument.getPages()) {
            
            for (BxZone zone : page.getZones()) {
                
                BxZoneLabel zoneLabel = zone.getLabel().getGeneralLabel();
                
                if (zoneLabel == BxZoneLabel.GEN_METADATA){
                    
                    String newLabel = classifiedMetadataLabels.get(metadataZoneIndex);
                    
                    zone.setLabel(BxZoneLabel.valueOf(newLabel));
                    metadataZoneIndex++;
                }
            }
        }
        
        return bxDocument;  
    }
    
    
    public List<String> classifyAndGetLabels(String XMLPath) throws TransformationException, IOException {
        
        String pathOfAFileInMalletFormat = directoryForTemporaryFiles + "temp.txt";
        converter.setReturnFilePath(pathOfAFileInMalletFormat);
        File fileInMalletFormat = converter.convertFile(XMLPath);
        
        return classifyAndGetLabels(fileInMalletFormat);
    }
    
    public List<String> classifyAndGetLabels(File dataInMalletFormat) throws FileNotFoundException {
        // Create a new iterator that will read raw instance data from                                     
        //  the lines of a file.                                                                           
        // Lines should be formatted as:                                                                   
        //                                                                                                 
        //   [name] [label] [data ... ]                                                                    

        CsvIterator reader =
            new CsvIterator(new FileReader(dataInMalletFormat),
                            "(\\w+)\\s+(\\w+)\\s+(.*)",
                            3, 2, 1);  // (data, label, name) field indices              
        

        // Create an iterator that will pass each instance through                                         
        //  the same pipe that was used to create the training data                                        
        //  for the classifier.                                                                            
        java.util.Iterator<Instance> instances = classifier.getInstancePipe().newIteratorFrom(reader);

        List<String> classificationResult = new ArrayList<String>();
        
        
        // Classifier.classify() returns a Classification object                                           
        //  that includes the instance, the classifier, and the                                            
        //  classification results (the labeling). Here we only                                            
        //  care about the Labeling.                                                                       
        while (instances.hasNext()) {
            Instance instance = instances.next();
            Labeling labeling = classifier.classify(instance).getLabeling();

            int firstRank = 0;
            String label = labeling.getLabelAtRank(firstRank).toString();
            classificationResult.add(label);
            
        }
        
        return classificationResult;
    }
   
    public void evaluate(String XMLPath) throws IOException, TransformationException {
        
        String pathOfAFileInMalletFormat = directoryForTemporaryFiles + "temp.txt";
        converter.setReturnFilePath(pathOfAFileInMalletFormat);
        File fileInMalletFormat = converter.convertFile(XMLPath);
        
        evaluate(fileInMalletFormat);
    }
    
    public void evaluate(File dataInMalletFormat) throws IOException {
        
        if (classifier == null) {
            throw new NullPointerException("Classifier should be trained first");
        }

        // Create an InstanceList that will contain the test data.                                         
        // In order to ensure compatibility, process instances                                             
        //  with the pipe used to process the original training                                            
        //  instances.                                                                                     

        InstanceList testInstances = new InstanceList(classifier.getInstancePipe());

        // Create a new iterator that will read raw instance data from                                     
        //  the lines of a file.                                                                           
        // Lines should be formatted as:                                                                   
        //                                                                                                 
        //   [name] [label] [data ... ]                                                                    

        CsvIterator reader =
            new CsvIterator(new FileReader(dataInMalletFormat),
                            "(\\w+)\\s+(\\w+)\\s+(.*)",
                            3, 2, 1);  // (data, label, name) field indices               

        // Add all instances loaded by the iterator to                                                     
        //  our instance list, passing the raw input data                                                  
        //  through the classifier's original input pipe.                                                  

        testInstances.addThruPipe(reader);

        Trial trial = new Trial(classifier, testInstances);

        // The Trial class implements many standard evaluation                                             
        //  metrics. See the JavaDoc API for more details.                                                 

        System.out.println("Accuracy: " + trial.getAccuracy());

	// precision, recall, and F1 are calcuated for a specific                                          
        //  class, which can be identified by an object (usually                                           
	//  a String) or the integer ID of the class                                                       

        for (int i = 0; i < classifier.getLabelAlphabet().size(); i++) {
            Label label = classifier.getLabelAlphabet().lookupLabel(i);
            System.out.println("Precision for class '" + label + "': " +
                           trial.getPrecision(i));           
        }
        
        for (int i = 0; i < classifier.getLabelAlphabet().size(); i++) {
            Label label = classifier.getLabelAlphabet().lookupLabel(i);
            System.out.println("F1 for class '" + label + "': " + trial.getF1(label.toString()));
           
        }
    }
    

}
