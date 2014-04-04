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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

public class DocumentShrinker {

    public static void main(String[] args) throws TransformationException, IOException, AnalysisException, ParseException, CloneNotSupportedException {
        String inDir = args[0];

        File dir = new File(inDir);
        
        Collection<File> files = FileUtils.listFiles(dir, new String[]{"cxml"}, true);
        
        int i = 0;
        for (File f : files) {
            System.out.println(f.getPath());
            TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
            List<BxPage> pages = tvReader.read(new FileReader(f));
            
            File newF = new File(f.getPath().replaceFirst("\\.\\d+\\.cxml", ".cxml"));
            System.out.println(newF.getName());
            BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
            FileWriter fw = new FileWriter(newF);
            writer.write(fw, pages, BxDocumentToTrueVizWriter.MINIMAL_OUTPUT_SIZE);
            fw.close();
            i++;
            
            System.out.println("Progress: "+((double)i*100./(double)files.size()));
        }
	}
}
