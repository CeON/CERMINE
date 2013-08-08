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

package pl.edu.icm.cermine.content;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.JDOMException;
import pl.edu.icm.cermine.content.filtering.KnnContentFilter;
import pl.edu.icm.cermine.content.headers.KnnContentHeadersExtractor;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.transformers.HTMLToDocContentStructReader;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ModelGeneratorDemo {
    
    public static void main(String[] args) throws TransformationException, JDOMException, IOException {
        String trainPath = args[0];
        
        List<String> trainNames = new ArrayList<String>();
        List<BxDocument> trainDocuments = new ArrayList<BxDocument>();
        List<DocumentContentStructure> trainStructures = new ArrayList<DocumentContentStructure>();

        File trainDir = new File(trainPath);
        for (File trainFile : trainDir.listFiles()) {
            if (trainFile.getName().endsWith(".xml")) {
                trainNames.add(trainFile.getName().replaceAll("\\.xml$", ""));
            }
        }
        
        HierarchicalReadingOrderResolver roa = new HierarchicalReadingOrderResolver();
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        HTMLToDocContentStructReader redaer2 = new HTMLToDocContentStructReader();
        
        for (String name : trainNames) {
            File sourceFile = new File(trainPath+name+".xml");
            FileReader fr = new FileReader(sourceFile);
            List<BxPage> pages = reader.read(fr);
            trainDocuments.add(roa.resolve(new BxDocument().setPages(pages)));
            
            File strFile = new File(trainPath+name+".html");
            FileReader fr2 = new FileReader(strFile);
            DocumentContentStructure hs = redaer2.read(fr2);
                        
            trainStructures.add(hs);
        }
        
        System.out.println(trainNames.size());
        System.out.println(trainDocuments.size());
        System.out.println(trainStructures.size());

        KnnModel<BxZoneLabel> classModel = KnnContentHeadersExtractor.buildModel(trainDocuments, trainStructures);
        KnnModel<BxZoneLabel> junkModel = KnnContentFilter.buildModel(trainDocuments);

        XStream xs = new XStream();
        
        System.out.println(xs.toXML(classModel));
        System.out.println("");
        System.out.println(xs.toXML(junkModel));
    }

    private ModelGeneratorDemo() {
    }
        
}
