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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

public class DocumentAllCoverageFilter {

    public static void main(String[] args) throws TransformationException, IOException, AnalysisException, ParseException, CloneNotSupportedException {
        Options options = new Options();
        options.addOption("input", true, "input path");
        options.addOption("output", true, "output path");
        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        String inDir = line.getOptionValue("input");
        String outDir = line.getOptionValue("output");

        File dir = new File(inDir);
        
        for (File f : FileUtils.listFiles(dir, new String[]{"xml"}, true)) {
            TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
            List<BxPage> pages = tvReader.read(new FileReader(f));
            BxDocument doc = new BxDocument().setPages(pages);
            doc.setFilename(f.getName());
           
            Set<BxZoneLabel> set = EnumSet.noneOf(BxZoneLabel.class);
            
            int all = 0;
            int good = 0;
            for (BxZone z: doc.asZones()) {
                all++;
                if (!z.getLabel().equals(BxZoneLabel.OTH_UNKNOWN)) {
                    good++;
                }
                if (z.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_METADATA)) {
                    set.add(z.getLabel());
                }
            }
           
            int intcov = 0;
            if (all > 0) {
                intcov = good*100/all;
            }
            System.out.println(doc.getFilename()+" "+set.size()+" "+intcov);

            File f2 = new File(outDir+doc.getFilename()+"."+set.size()+"."+intcov);
            FileUtils.copyFile(f, f2);
        }
	}
}
