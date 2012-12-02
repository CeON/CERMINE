package pl.edu.icm.cermine.content.headers;

import java.io.IOException;
import java.util.List;
import libsvm.svm_parameter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMHeaderLinesTraining {
    
    public static void trainClassifier(List<TrainingSample<BxZoneLabel>> trainingElements, String output) 
            throws AnalysisException, IOException {
        SVMHeaderLinesClassifier contentFilter = new SVMHeaderLinesClassifier();
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.gamma = 0.5;
        param.C = 32.0;
        param.kernel_type = svm_parameter.RBF;
        
        contentFilter.setParameter(param);
        contentFilter.buildClassifier(trainingElements);
        contentFilter.saveModel(output);
    }
    
    public static void main(String[] args) throws AnalysisException, IOException {
        if (args.length < 2) {
            System.out.println("Usage: SVMContentHeaderTraining <input dir> <output>");
            return;
        }
        List<TrainingSample<BxZoneLabel>> trainingElements = HeaderExtractingTools.toTrainingSamples(args[0]);
        trainClassifier(trainingElements, args[1]);
    }
   
}
