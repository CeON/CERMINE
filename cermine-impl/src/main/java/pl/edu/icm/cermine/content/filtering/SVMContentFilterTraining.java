package pl.edu.icm.cermine.content.filtering;

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
public class SVMContentFilterTraining {
    
    public static void trainClassifier(List<TrainingSample<BxZoneLabel>> trainingElements) throws AnalysisException, IOException {
        SVMContentFilter contentFilter = new SVMContentFilter();
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.gamma = 8.0;
        param.C = 512.0;
        param.kernel_type = svm_parameter.RBF;
        
        contentFilter.setParameter(param);
        contentFilter.buildClassifier(trainingElements);
        contentFilter.saveModel("/tmp/junkfilter");
    }
    
    public static void main(String[] args) throws AnalysisException, IOException {
        String trainPath = "/home/domin/newexamples/all/train/";
        List<TrainingSample<BxZoneLabel>> trainingElements = ContentFilterTools.toTrainingSamples(trainPath);
        trainClassifier(trainingElements);
    }
   
}
