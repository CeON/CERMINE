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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxDocShrinker {

    public static void main(String[] args) throws ParseException, TransformationException, IOException {
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
        
        Collection<File> files = FileUtils.listFiles(dir, new String[]{extension}, true);
        
        int i = 0;
        for (File f : files) {
            System.out.println(f.getPath());
            TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
            List<BxPage> pages = tvReader.read(new FileReader(f));
            
            File f2 = new File(outDir + f.getName());
            BxDocumentToTrueVizWriter wrt = new BxDocumentToTrueVizWriter();
            boolean created = f2.createNewFile();
            if (!created) {
                throw new IOException("Cannot create file: ");
            }
            FileWriter fw = new FileWriter(f2);
            wrt.write(fw, pages, BxDocumentToTrueVizWriter.MINIMAL_OUTPUT_SIZE);
            fw.flush();
            fw.close();
            
            i++;
            
            System.out.println("Progress: "+((double)i*100./(double)files.size()));
        }
    }
    
}
