package pl.edu.icm.cermine.content.filtering;

import java.io.IOException;
import java.util.List;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.optimization.LibSVMExporter;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 *
 * @author Dominika Tkaczyk
 */
public class LibSVMContentFilteringExporter {
    
    public static void main(String[] args) throws AnalysisException, IOException {
        String trainPath = "/home/domin/newexamples/all/train/";
        List<TrainingSample<BxZoneLabel>> TrainingSamples = ContentFilterTools.toTrainingSamples(trainPath);

        System.out.println("ILE "+TrainingSamples.size());
        for (TrainingSample<BxZoneLabel>te : TrainingSamples) {
            FeatureVector fv = te.getFeatures();
            for (String f : fv.getFeatureNames()) {
                if (Double.isNaN(fv.getFeature(f))) {
                    System.out.println("SUPA "+f);
                }
            }
            System.out.println("");
            System.out.println(te.getLabel()+" "+te.getFeatures().dump());
        }
        
        LibSVMExporter.toLibSVM(TrainingSamples, "/tmp/junk_classification.dat");
    }
            
}
