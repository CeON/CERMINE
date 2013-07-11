package pl.edu.icm.cermine.pubmed;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

public class DocumentKeyZonesFilter {

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
           
            Set<BxZoneLabel> set = new HashSet<BxZoneLabel>();

            int keys = 0;
            int all = 0;
            int good = 0;
            for (BxZone z: doc.asZones()) {
                all++;
                if (BxZoneLabel.REFERENCES.equals(z.getLabel())) {
                    keys = 1;
                }
                if (!z.getLabel().equals(BxZoneLabel.OTH_UNKNOWN)) {
                    good++;
                }
                if (z.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_METADATA)) {
                    set.add(z.getLabel());
                }
            }

            System.out.println(set);
            
            if (set.contains(BxZoneLabel.MET_AFFILIATION)) {
                keys++;
            }
            if (set.contains(BxZoneLabel.MET_AUTHOR)) {
                keys++;
            }
            if (set.contains(BxZoneLabel.MET_BIB_INFO)) {
                keys++;
            }
            if(set.contains(BxZoneLabel.MET_TITLE)) {
                keys++;
            }
            
            int intcov = 0;
            if (all > 0)
                intcov = good*100/all;
            System.out.println(doc.getFilename()+" "+set.size()+" "+intcov+" "+keys);

            File f2 = new File(outDir+doc.getFilename()+"."+set.size()+"."+intcov+"."+keys);
            FileUtils.copyFile(f, f2);
        }
	}
}
