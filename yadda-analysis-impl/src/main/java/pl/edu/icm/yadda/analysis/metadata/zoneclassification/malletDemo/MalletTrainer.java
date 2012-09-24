package pl.edu.icm.yadda.analysis.metadata.zoneclassification.malletDemo;

//import cc.mallet.classify.Classifier;
//import cc.mallet.classify.ClassifierTrainer;
//import cc.mallet.types.InstanceList;
import java.io.*;
import pl.edu.icm.yadda.analysis.TransformationException;

/**
 *
 * @author Krzysztof Werys
 */
public class MalletTrainer {
    
//    private ClassifierTrainer classifierTrainer;
//    private Classifier classifier;
    
    private static String directoryForTemporaryFiles = Utils.getPathOfResource("/pl/edu/icm/yadda/analysis/metadata/zoneclassification/trainingdata/");

    /**
     * @param classifierTrainer: classification algorithm which will be used to
     * train the classifier, e.g., MaxEnt, NaiveBayes, C45, DecisionTree. See the 
     * JavaDoc API http://mallet.cs.umass.edu/api/ for the cc.mallet.classify 
     * package to see the complete current list of Trainer classes.
     */
//    public MalletTrainer(ClassifierTrainer classifierTrainer){
 //       this.classifierTrainer = classifierTrainer;
  //  }
    
    /**
     * @param dataInMalletFormat: file after conversion from XML. Use 
     * XMLToMalletFormatConverter to get a file in mallet format.
     */
    public void trainClassifier(File dataInMalletFormat) throws FileNotFoundException {
        
        MalletDataImporter importer = new MalletDataImporter();
    //    InstanceList trainingInstances = importer.readFile(dataInMalletFormat);
      //  classifier = classifierTrainer.train(trainingInstances);
    }
    
    /**
     * @param trainingDataDirectory: path to directory with training files in XML
     */
    public void trainClassifier(String trainingDataDirectoryWithXMLFiles) throws IOException, TransformationException {
        XMLToMalletFormatConverter converter = new XMLToMalletFormatConverter();
        
        converter.setReturnFilePath(directoryForTemporaryFiles + "temp.txt");
        File trainingDataInMalletFormat =  converter.convertDirectory(trainingDataDirectoryWithXMLFiles);
        trainClassifier(trainingDataInMalletFormat);
    }
    
    /**
     * @param serializedFile: File where the classifier will be saved
     */
    public void saveClassifier(File serializedFile) throws IOException {

        // The standard method for saving classifiers in                                                   
        //  Mallet is through Java serialization. Here we                                                  
        //  write the classifier object to the specified file.                                             
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream (serializedFile));
      //  oos.writeObject (classifier);
        oos.close();
    }
    
    /**
     * 
     * @param serializedFile: File from which a classifier will be loaded
     */
    public void loadClassifier(File serializedFile) throws FileNotFoundException, IOException, ClassNotFoundException {

        // The standard way to save classifiers and Mallet data                                            
        //  for repeated use is through Java serialization.                                                
        // Here we load a serialized classifier from a file.                                               

        //Classifier loadedClassifier;

//        ObjectInputStream ois = new ObjectInputStream (new FileInputStream (serializedFile));
  //      loadedClassifier = (Classifier) ois.readObject();
    //    ois.close();

      //  this.classifier = loadedClassifier;
    }
    
//    public Classifier getClassifier() {
  //      return classifier;
    //}
}
