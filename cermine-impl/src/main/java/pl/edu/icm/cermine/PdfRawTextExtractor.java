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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Text extractor from PDF files. Extracted text includes 
 * all text strings found in the document in correct reading order.
 *
 * @deprecated use {@link ContentExtractor} instead.
 * 
 * @author Pawel Szostek
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
@Deprecated
public class PdfRawTextExtractor {
    
    private final ContentExtractor extractor;
    
    public PdfRawTextExtractor() throws AnalysisException {
        extractor = new ContentExtractor();
    }
    
    /**
     * Extracts content of a PDF as a plain text.
     * 
     * @param stream input stream
     * @return pdf's content as plain text
     * @throws AnalysisException AnalysisException
     */
    public String extractText(InputStream stream) throws AnalysisException {
        try {
            extractor.setPDF(stream);
            return extractor.getRawFullText();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }
    
    /**
     * Extracts content of a PDF as a plain text.
     * 
     * @param document document's structure
     * @return pdf's content as plain text
     * @throws AnalysisException AnalysisException
     */
    public String extractText(BxDocument document) throws AnalysisException {
        try {
            extractor.setBxDocument(document);
            return extractor.getRawFullText();
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
                    "Usage: PdfRawTextExtractor -path <path> [optional parameters]\n\n"
                  + "Tool for extracting full text in the right reading order from PDF files.\n\n"
                  + "Arguments:\n"
                  + "  -path <path>              path to a PDF file or directory containing PDF files\n"
                  + "  -ext <extension>          (optional) the extension of the resulting text file;\n"
                  + "                            default: \"cermtxt\"; used only if passed path is a directory\n"
                  + "  -threads <num>            number of threads for parallel processing\n");
            System.exit(1);
        }
        
        String path = parser.getPath();
        String extension = parser.getTextExtension();
        InternalContentExtractor.THREADS_NUMBER = parser.getThreadsNumber();
 
        File file = new File(path);
        if (file.isFile()) {
            try {
                PdfRawTextExtractor extractor = new PdfRawTextExtractor();
                InputStream in = new FileInputStream(file);
                String result = extractor.extractText(in);
                System.out.println(result);
            } catch (AnalysisException ex) {
                ex.printStackTrace();
            }
        } else {
        
            Collection<File> files = FileUtils.listFiles(file, new String[]{"pdf"}, true);
    
            int i = 0;
            for (File pdf : files) {
                File xmlF = new File(pdf.getPath().replaceAll("pdf$", extension));
                if (xmlF.exists()) {
                    i++;
                    continue;
                }
 
                long start = System.currentTimeMillis();
                float elapsed = 0;
            
                System.out.println(pdf.getPath());
 
                try {
                    PdfRawTextExtractor extractor = new PdfRawTextExtractor();
                    InputStream in = new FileInputStream(pdf);
                    String result = extractor.extractText(in);

                    long end = System.currentTimeMillis();
                    elapsed = (end - start) / 1000F;
 
                    if (!xmlF.createNewFile()) {
                        System.out.println("Cannot create new file!");
                    }
                    FileUtils.writeStringToFile(xmlF, result);
                } catch (AnalysisException ex) {
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
    
}
