package pl.edu.icm.cermine.libsvm;

import java.io.IOException;
import java.util.List;

import pl.edu.icm.cermine.content.headers.HeaderExtractingTools;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class LibSVMHeaderLinesExporter {
    
    public static void main(String[] args) throws AnalysisException, IOException, TransformationException {
        if (args.length < 2) {
            System.out.println("Usage: LibSVMContentHeadersExporter <input dir> <output>");
            return;
        }
        List<TrainingSample<BxZoneLabel>> trainingSamples = HeaderExtractingTools.toTrainingSamples(args[0]);
        LibSVMExporter.toLibSVM(trainingSamples, args[1]);
    }

    private LibSVMHeaderLinesExporter() {
    }
            
}
