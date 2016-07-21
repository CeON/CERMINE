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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class BxDocStatisticsPrinter {

    protected abstract Map<String, String> getStatistics(BxDocument document);
    
    public void run(String[] args) throws ParseException, TransformationException, FileNotFoundException {
        Options options = new Options();
        options.addOption("input", true, "input path");
        options.addOption("ext", true, "extension");
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);
        String inDir = line.getOptionValue("input");
        String extension = line.getOptionValue("ext");
        
        File dir = new File(inDir);
        for (File f : FileUtils.listFiles(dir, new String[]{extension}, true)) {
            TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
            List<BxPage> pages = tvReader.read(new FileReader(f));
            BxDocument doc = new BxDocument().setPages(pages);
            doc.setFilename(f.getName());
            System.out.println("Document: " + f.getPath());

            Map<String, String> statistics = getStatistics(doc);
            for (Map.Entry<String, String> statistic: statistics.entrySet()) {
                System.out.println(statistic.getKey() + ": " + statistic.getValue());
            }
            System.out.println();
        }
    }
}
