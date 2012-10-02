package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.malletDemo;

//import cc.mallet.classify.MaxEntTrainer;
import java.io.IOException;
import java.util.List;
import pl.edu.icm.coansys.metaextr.exception.TransformationException;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/**
 * 
 * @author Krzysztof Werys
 */
public class MalletZoneClassificationDemo {

    private static String trainingDataDirectory = Utils.getPathOfResource("/pl/edu/icm/yadda/analysis/metadata/zoneclassification/trainingdata/");
    private static String trainingData = Utils.getPathOfResource("/pl/edu/icm/yadda/analysis/metadata/zoneclassification/09629351.xml");
    private static String testingData = Utils.getPathOfResource("/pl/edu/icm/yadda/analysis/metadata/zoneclassification/09629351.xml");
 
//    private static String trainingDataInMalletFormatPath = Utils.getPathOfResource("/pl/edu/icm/yadda/analysis/metadata/zoneclassification/trainingData.txt");
//    private static String testingDataInMalletFormatPath = Utils.getPathOfResource("/pl/edu/icm/yadda/analysis/metadata/zoneclassification/testingData.txt");
    
    public static void main(String[] args) throws IOException, TransformationException {

    
  
        // 1. Set the trainer and the classification algorithm (e.g. MaxEnt, NaiveBayes, C45, DecisionTree, ...)
//        MalletTrainer trainer = new MalletTrainer(new MaxEntTrainer());
        
        // 2. Train our classifier by giving a directory with training files in XML
//        trainer.trainClassifier(trainingDataDirectory);
        
        // It is also possible to pass a file in Mallet format but first convert from XML
        // Uncomment the lines below.
//        XMLToMalletFormatConverter converter = new XMLToMalletFormatConverter();
//        converter.setReturnFilePath(trainingDataInMalletFormatPath);
//        File trainingDataInMalletFormat =  converter.convertFile(trainingData);
//        trainer.trainClassifier(trainingDataInMalletFormat);
        
        // save a trained classifier
//        trainer.saveClassifier(new File("/home/werys/Pulpit/classificator"));
        
        // convert training file from XML to mallet format and save it on disk
        XMLToMalletFormatConverter converter = new XMLToMalletFormatConverter();
        converter.setReturnFilePath("/home/werys/Pulpit/testingFile.txt");
        converter.convertFile(testingData);
        
        // 3. Build classifier.
//        MalletClassifier classifier = new MalletClassifier(trainer);
        
        // 4. And classify a file in XML
//        BxDocument classifiedDocument = classifier.classify(testingData);
        
        // get some info about the classification
//        classifier.evaluate(testingData);
        
        // display result of the classification in readable format
//        List<String> classifiedMetadataLabels = classifier.classifyAndGetLabels(testingData);
//        printResultOfClassification(testingData, classifiedMetadataLabels);
         
    }
     
    private static void printResultOfClassification(String XMLTestingData, List<String> classifiedMetadataLabels) throws TransformationException {
        System.out.println();
        
        Utils utils = new Utils();
        List<BxZone> metadataZones = utils.getMetadataZones(XMLTestingData);
        
        for(int i = 0; i < metadataZones.size(); i++) {
            BxZone zone = metadataZones.get(i);
            
            String zoneLabel = zone.getLabel().toString();
            String zoneText = zone.toText();
            
            System.out.println("[zone label] = " + zoneLabel);
            System.out.println("[predicted label] = " + classifiedMetadataLabels.get(i));
            System.out.println(zoneText);
            System.out.println();
        }
    }
}
