package pl.edu.icm.cermine.content.filtering;

import java.io.IOException;
import java.util.List;
import libsvm.svm_parameter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class SVMContentFilterTraining {
    
    public static final double BEST_GAMMA = 8.0;
    
    public static final double BEST_C = 512.0;
    
    public static void trainClassifier(List<TrainingSample<BxZoneLabel>> trainingElements, String output) 
            throws AnalysisException, IOException {
        SVMContentFilter contentFilter = new SVMContentFilter();
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.gamma = BEST_GAMMA;
        param.C = BEST_C;
        param.kernel_type = svm_parameter.RBF;
        
        contentFilter.setParameter(param);
        contentFilter.buildClassifier(trainingElements);
        contentFilter.saveModel(output);
    }
    
    public static void main(String[] args) throws AnalysisException, IOException, TransformationException {
        if (args.length < 2) {
            System.out.println("Usage: SVMContentFilterTraining <training files dir> <output>");
            return;
        }
        List<TrainingSample<BxZoneLabel>> trainingElements = ContentFilterTools.toTrainingSamples(args[0]);
        trainClassifier(trainingElements, args[1]);
    }

    private SVMContentFilterTraining() {
    }
   
}
