/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
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

package pl.edu.icm.cermine.bx;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class BxDocRewriter {

    protected abstract BxDocument transform(BxDocument document) throws AnalysisException;
    
    public void run(String[] args) throws ParseException, TransformationException, IOException, AnalysisException {
        Options options = new Options();
        options.addOption("input", true, "input path");
        options.addOption("output", true, "output path");
        options.addOption("ext", true, "extension");
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);
        String inDir = line.getOptionValue("input");
        String outDir = line.getOptionValue("output");
        String extension = line.getOptionValue("ext");

        File dir = new File(inDir);
        
        for (File f : FileUtils.listFiles(dir, new String[]{extension}, true)) {
            TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
            List<BxPage> pages = tvReader.read(new FileReader(f));
            BxDocument doc = new BxDocument().setPages(pages);
            doc.setFilename(f.getName());
           
            BxDocument rewritten = transform(doc);

            File f2 = new File(outDir+doc.getFilename());
            BxDocumentToTrueVizWriter wrt = new BxDocumentToTrueVizWriter();
            boolean created = f2.createNewFile();
            if (!created) {
                throw new IOException("Cannot create file: ");
            }
            FileWriter fw = new FileWriter(f2);
            wrt.write(fw, Lists.newArrayList(rewritten));
            fw.flush();
            fw.close();
        }
    }
    
}
