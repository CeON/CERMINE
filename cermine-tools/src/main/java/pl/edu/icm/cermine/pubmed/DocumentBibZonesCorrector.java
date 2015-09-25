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
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

public class DocumentBibZonesCorrector {

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
           
            for (BxZone z: doc.asZones()) {
                if (!BxZoneLabel.MET_BIB_INFO.equals(z.getLabel()) &&
                        !BxZoneLabel.REFERENCES.equals(z.getLabel()) &&
                        z.childrenCount() <= 2 && 
                        (z.toText().toLowerCase().contains("journal ")
                        || z.toText().toLowerCase().contains("vol.")
                        || z.toText().toLowerCase().contains("vol ")
                        || z.toText().toLowerCase().contains("pp.")
                        || z.toText().toLowerCase().contains("volume ")
                        || z.toText().toLowerCase().contains("pp ")
                        || z.toText().toLowerCase().contains("issn")
                        || z.toText().toLowerCase().contains("doi:")
                        || z.toText().toLowerCase().contains("doi ")
                        || z.toText().toLowerCase().contains("citation:"))) {
                    System.out.println("DETECTED BIBINFO: ");
                    System.out.println(z.getLabel()+" "+z.toText());
                    System.out.println("");
                    z.setLabel(BxZoneLabel.MET_BIB_INFO);
                } else
                if (!BxZoneLabel.OTH_UNKNOWN.equals(z.getLabel()) &&
                        !BxZoneLabel.MET_BIB_INFO.equals(z.getLabel()) &&
                        z.childrenCount() <= 2 && 
                        (z.toText().toLowerCase().contains("page "))) {
                    System.out.println("DETECTED PAGE: ");
                    System.out.println(z.getLabel()+" "+z.toText());
                    System.out.println("");
                    z.setLabel(BxZoneLabel.OTH_UNKNOWN);
                }
            }

            File f2 = new File(outDir+doc.getFilename());
            BxDocumentToTrueVizWriter wrt = new BxDocumentToTrueVizWriter();
            boolean created = f2.createNewFile();
            if (!created) {
                throw new IOException("Cannot create file: ");
            }
            FileWriter fw = new FileWriter(f2);
            wrt.write(fw, Lists.newArrayList(doc));
            fw.flush();
            fw.close();
        }
	}
}
