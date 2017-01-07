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

package pl.edu.icm.cermine;

import com.google.common.collect.Lists;
import java.io.*;
import java.util.Collection;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.configuration.ExtractionConfigRegister;
import pl.edu.icm.cermine.configuration.ExtractionConfigBuilder;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;

/**
 * Document geometric structure extractor. Extracts the geometric hierarchical structure
 * (pages, zones, lines, words and characters) from a PDF file and stores it as a BxDocument object.
 *
 * @deprecated use {@link ContentExtractor} instead.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
@Deprecated
public class PdfBxStructureExtractor {

    private final ContentExtractor extractor;

    public PdfBxStructureExtractor() throws AnalysisException {
        extractor = new ContentExtractor();
    }

    /**
     * Extracts the geometric structure from a PDF file and stores it as BxDocument.
     * 
     * @param stream PDF stream
     * @return BxDocument object storing the geometric structure
     * @throws AnalysisException AnalysisException
     */
    public BxDocument extractStructure(InputStream stream) throws AnalysisException {
        try {
            extractor.setPDF(stream);
            return extractor.getBxDocument();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }

    public ComponentConfiguration getConf() {
        return extractor.getConf();
    }

    public void setConf(ComponentConfiguration conf) {
        extractor.setConf(conf);
    }
    
    public static void main(String[] args) throws ParseException, IOException {
        CommandLineOptionsParser parser = new CommandLineOptionsParser();
        String error = parser.parse(args);
        if (error != null) {
            System.err.println(error + "\n");
            System.err.println(
                    "Usage: PdfBxStructureExtractor -path <path> [optional parameters]\n\n"
                  + "Tool for extracting structured content from PDF files.\n\n"
                  + "Arguments:\n"
                  + "  -path <path>              path to a PDF file or directory containing PDF files\n"
                  + "  -configuration <path>     (optional) path to configuration properties file\n"
                  + "                            see https://github.com/CeON/CERMINE\n"
                  + "                            for description of available configuration properties\n"
                  + "  -strext <extension>       (optional) the extension of the structure (TrueViz) file;\n"
                  + "                            default: \"cxml\"; used only if passed path is a directory\n"
                );
            System.exit(1);
        }
        
        String path = parser.getPath();
        String strExtension = parser.getBxExtension();

        File file = new File(path);
        Collection<File> files = FileUtils.listFiles(file, new String[]{"pdf"}, true);
    
        ExtractionConfigBuilder builder = new ExtractionConfigBuilder();
        if (parser.getConfigurationPath() != null) {
            builder.addConfiguration(parser.getConfigurationPath());
        }
        ExtractionConfigRegister.set(builder.buildConfiguration());
        
        int i = 0;
        for (File pdf : files) {
            File strF = new File(pdf.getPath().replaceAll("pdf$", strExtension));
            if (strF.exists()) {
                i++;
                continue;
            }
 
            long start = System.currentTimeMillis();
            float elapsed = 0;
            
            System.out.println(pdf.getPath());
 
            try {
                PdfBxStructureExtractor extractor = new PdfBxStructureExtractor();

                InputStream in = new FileInputStream(pdf);
                BxDocument doc = extractor.extractStructure(in);
                doc = extractor.getConf().getMetadataClassifier().classifyZones(doc);

                long end = System.currentTimeMillis();
                elapsed = (end - start) / 1000F;
            
                BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
                Writer fw = new OutputStreamWriter(new FileOutputStream(strF), "UTF-8");
                writer.write(fw, Lists.newArrayList(doc), "UTF-8");
            } catch (AnalysisException ex) {
               ex.printStackTrace();
            } catch (TransformationException ex) {
               ex.printStackTrace();
            }
                
            i++;
            int percentage = i*100/files.size();
            if (elapsed == 0) {
                elapsed = (System.currentTimeMillis() - start) / 1000F;
            }
            System.out.println("Extraction time: " + Math.round(elapsed) + "s");
            System.out.println(percentage + "% done (" + i +" out of " + files.size() + ")");
            System.out.println("");
        }
    }
    
}
