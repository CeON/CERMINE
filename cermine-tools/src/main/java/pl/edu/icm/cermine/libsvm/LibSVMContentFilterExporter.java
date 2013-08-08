/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.libsvm;

import java.io.IOException;
import java.util.List;
import pl.edu.icm.cermine.content.filtering.ContentFilterTools;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class LibSVMContentFilterExporter {
    
    public static void main(String[] args) throws AnalysisException, IOException, TransformationException {
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
