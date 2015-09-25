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

package pl.edu.icm.cermine.structure;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.tools.DocumentProcessor;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMInitialClassifierTest extends AbstractDocumentProcessorTest {
	protected static final String zipResources = "/pl/edu/icm/cermine/structure/roa_test_small.zip";
	
	protected static final double testSuccessPercentage = 80;
	
	protected SVMZoneClassifier classifier;
    
    protected ReadingOrderResolver ror;
	
    int allZones = 0;
    int badZones = 0;
    
    @Before
    public void setUp() throws IOException, AnalysisException {
    	classifier = new SVMInitialZoneClassifier();
        ror = new HierarchicalReadingOrderResolver();
    	startProcessFlattener = new DocumentProcessor() {
			@Override
			public void process(BxDocument document) throws AnalysisException {
				ror.resolve(document);
			}
		};
        endProcessFlattener = new DocumentProcessor() {
			@Override
			public void process(BxDocument document) throws AnalysisException {
				ror.resolve(document);
			}
		};
    }
    
    @Test
    public void SVMInitialZoneClassifierTest() throws URISyntaxException, ZipException, IOException, 
            ParserConfigurationException, SAXException, AnalysisException, TransformationException {
        testAllFilesFromZip(Arrays.asList(zipResources), testSuccessPercentage);

        System.out.println("all zones: "+this.allZones);
        System.out.println("bad zones: "+this.badZones);
    }

	@Override
	protected boolean compareDocuments(BxDocument testDoc,	BxDocument expectedDoc) {
		if (testDoc.childrenCount() != expectedDoc.childrenCount()) {
			return false;
        }
		
        List<BxZone> testZones = Lists.newArrayList(testDoc.asZones());
        List<BxZone> expZones = Lists.newArrayList(expectedDoc.asZones());
		Integer correctZones = 0;
		Integer zones = testZones.size();
		for (Integer zoneIdx = 0; zoneIdx < testZones.size(); ++zoneIdx) {
			BxZone testZone = testZones.get(zoneIdx);
			BxZone expectedZone = expZones.get(zoneIdx);
			++allZones;
			if (testZone.getLabel() == expectedZone.getLabel().getGeneralLabel()) {
				++correctZones;
            } else {
				++badZones;
            }
		}

		return ((double)correctZones/(double)zones) >= testSuccessPercentage/100.0;
	}

	@Override
	protected BxDocument process(BxDocument doc) throws AnalysisException {
        for (BxZone z : doc.asZones()) {
            z.setLabel(null);
        }
		classifier.classifyZones(doc);
		return doc;
	}

}
