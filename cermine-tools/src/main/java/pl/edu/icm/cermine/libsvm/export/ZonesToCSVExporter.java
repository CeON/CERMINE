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

package pl.edu.icm.cermine.libsvm.export;

import java.io.*;
import java.util.List;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.FeatureList;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ZonesToCSVExporter {

    public static void main(String[] args) throws FileNotFoundException, TransformationException {

        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = FeatureList.VECTOR_BUILDER;
        
        List<String> names = vectorBuilder.getFeatureNames();
        System.out.print("Zone,Label");
        for (String name : names) {
            System.out.print(",");
            System.out.print(name);
        }
        System.out.println("");
        File dir = new File(args[0]);
        for (File tv : FileUtils.listFiles(dir, new String[]{args[1]}, true)) {
            InputStream is = new FileInputStream(tv);
            TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
            Reader r = new InputStreamReader(is);
            BxDocument origBxDoc = new BxDocument().setPages(reader.read(r));
            
            for (BxZone z : origBxDoc.asZones()) {
                FeatureVector fv = vectorBuilder.getFeatureVector(z, z.getParent());
                String t = z.toText().replaceAll("[^a-zA-Z0-9 ]", "");
                System.out.print("\""+t.substring(0, Math.min(50, t.length()))+"\"");
                System.out.print(",");
                System.out.print(z.getLabel());
                
                for (String name : names) {
                    System.out.print(",");
                    System.out.print(fv.getValue(name));
                }
                System.out.println("");
            }
        }

    }
}