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

package pl.edu.icm.cermine.pubmed;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

public class DocumentViewer {

    public static void main(String[] args) throws TransformationException, IOException, AnalysisException, ParseException, CloneNotSupportedException {
        Options options = new Options();
        options.addOption("input", true, "input path");
        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        String inDir = line.getOptionValue("input");

        File dir = new File(inDir);
        
        for (File f : FileUtils.listFiles(dir, new String[]{"xml"}, true)) {
            TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
            List<BxPage> pages = tvReader.read(new FileReader(f));
            BxDocument doc = new BxDocument().setPages(pages);
            System.out.println("");
            doc.setFilename("FILE: "+f.getName());
           
            for (BxZone z : doc.asZones()) {
                System.out.println();
                System.out.println(z.getLabel()+" "+z.toText());
            }
            
        }
	}
}
