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
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jdom.JDOMException;
import org.junit.Before;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class KnnLogicalStructureExtractorTest extends AbstractLogicalStructureExtractorTest {
    
    String modelsZip = "models.zip";
    String headerModelFile = "headerClassModel.xml";
    String junkModelFile = "junkModel.xml";
       
    KnnModel<BxZoneLabel> classModel;
    KnnModel<BxZoneLabel> junkModel;

    @Before
    @Override
    public void setUp() throws IOException, TransformationException, AnalysisException, URISyntaxException, JDOMException {
        super.setUp();
        classModel = readModel(dir + modelsZip, headerModelFile);
        junkModel = readModel(dir + modelsZip, junkModelFile);
        extractor = new KnnLogicalStructureExtractor(junkModel, classModel);
    }

    private KnnModel<BxZoneLabel> readModel(String zipFileName, String modelFileName) throws IOException, TransformationException, JDOMException, URISyntaxException {
        ZipFile zipFile = new ZipFile(new File(this.getClass().getResource(zipFileName).toURI()));
        List<ZipEntry> entries = getEntries(zipFile);
        
        for (ZipEntry ze : entries) {
            if (ze.getName().matches("^"+modelFileName+"$") || ze.getName().matches("^.*/"+modelFileName+"$")) {
                InputStream xis = zipFile.getInputStream(ze);
                XStream xs = new XStream();
                return (KnnModel<BxZoneLabel>) xs.fromXML(xis);
            }
        }
        return null;
    }

}