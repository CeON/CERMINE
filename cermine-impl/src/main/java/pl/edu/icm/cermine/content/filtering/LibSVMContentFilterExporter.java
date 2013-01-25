package pl.edu.icm.cermine.content.filtering;

import java.io.IOException;
import java.util.List;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.optimization.LibSVMExporter;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class LibSVMContentFilterExporter {
    
    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, CloneNotSupportedException {
        if (args.length < 2) {
            System.out.println("Usage: LibSVMContentFilterExporter <input dir> <output>");
            return;
        }
        
        List<TrainingSample<BxZoneLabel>> trainingSamples = ContentFilterTools.toTrainingSamples(args[0]);
        LibSVMExporter.toLibSVM(trainingSamples, args[1]);
    }

    private LibSVMContentFilterExporter() {
    }
   
}
