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

package pl.edu.icm.cermine;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMElementConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * NLM-based content extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class ContentExtractor {
    
    private PdfBxStructureExtractor structureExtractor;
    
    private PdfNLMMetadataExtractor metadataExtractor;
    
    private PdfBibEntryReferencesExtractor referencesExtractor;
    
    private PdfRawTextExtractor rawTextExtractor;
    
    private PdfNLMTextExtractor textExtractor;
           
    
    private InputStream pdfFile;
    private BxDocument bxDocument;
    private Element nlmMetadata;
    private List<BibEntry> bibEntryReferences;
    private List<Element> nlmReferences;
    private String rawFullText;
    private Element nlmFullText;
    private Element nlmContent;

    
    public ContentExtractor() throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor();
        metadataExtractor = new PdfNLMMetadataExtractor();
        referencesExtractor = new PdfBibEntryReferencesExtractor();
        rawTextExtractor = new PdfRawTextExtractor();
        textExtractor = new PdfNLMTextExtractor();
    }

    
    public void uploadPDF(InputStream pdfFile) throws IOException {
        this.reset();
        this.pdfFile = pdfFile;
    }
    
    public BxDocument getBxDocument() throws AnalysisException {
        if (pdfFile == null) {
            throw new AnalysisException("No PDF document uploaded!");
        }
        if (bxDocument == null) {
            bxDocument = structureExtractor.extractStructure(pdfFile);
        }
        return bxDocument;
    }
    
    public List<BibEntry> getBibEntryReferences() throws AnalysisException {
        if (bibEntryReferences == null) {
            getBxDocument();
            bibEntryReferences = Lists.newArrayList(referencesExtractor.extractReferences(bxDocument));
        }
        return bibEntryReferences;
    }
    
    public String getRawFullText() throws AnalysisException {
        if (rawFullText == null) {
            getBxDocument();
            rawFullText = rawTextExtractor.extractText(bxDocument);
        }
        return rawFullText;
    }
  
    public Element getNLMMetadata() throws AnalysisException {
        if (nlmMetadata == null) {
            getBxDocument();
            nlmMetadata = metadataExtractor.extractMetadata(bxDocument);
        }
        return nlmMetadata;
    }
    
    public List<Element> getNLMReferences() throws AnalysisException {
        if (nlmReferences == null) {
            getBibEntryReferences();
            nlmReferences = new ArrayList<Element>(bibEntryReferences.size());
            BibEntryToNLMElementConverter converter = new BibEntryToNLMElementConverter();
            for (BibEntry entry : bibEntryReferences) {
                try {
                    nlmReferences.add(converter.convert(entry));
                } catch (TransformationException ex) {
                    throw new AnalysisException(ex);
                }
            }
        }
        return nlmReferences;
    }
    
    public Element getNLMText() throws AnalysisException {
        if (nlmFullText == null) {
            getBxDocument();
            nlmFullText = textExtractor.extractText(bxDocument);
        }
        return nlmFullText;
    }
    
    public Element getNLMContent() throws AnalysisException {
        if (nlmContent == null) {
            getNLMMetadata();
            getNLMReferences();
            getNLMText();
            
            nlmContent = new Element("article");
            
            Element metadata = (Element) nlmMetadata.getChild("front").clone();
            nlmContent.addContent(metadata);
            
            nlmContent.addContent(nlmFullText);
            
            Element back = new Element("back");
            Element refList = new Element("ref-list");
            for (Element ref : nlmReferences) {
                Element r = new Element("ref");
                r.addContent(ref);
                refList.addContent(r);
            }
            back.addContent(refList);
            nlmContent.addContent(back);
        }
        return nlmContent;
    }
    
    public void reset() throws IOException {
        bxDocument = null;
        nlmMetadata = null;
        bibEntryReferences = null;
        nlmReferences = null;
        rawFullText = null;
        nlmFullText = null;
        nlmContent = null;
        if (pdfFile != null) {
            pdfFile.close();
        }
        pdfFile = null;
    }

    public void setMetadataExtractor(PdfNLMMetadataExtractor metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
    }

    public void setRawTextExtractor(PdfRawTextExtractor rawTextExtractor) {
        this.rawTextExtractor = rawTextExtractor;
    }

    public void setReferencesExtractor(PdfBibEntryReferencesExtractor referencesExtractor) {
        this.referencesExtractor = referencesExtractor;
    }

    public void setStructureExtractor(PdfBxStructureExtractor structureExtractor) {
        this.structureExtractor = structureExtractor;
    }

    public void setTextExtractor(PdfNLMTextExtractor textExtractor) {
        this.textExtractor = textExtractor;
    }
    
    public static void main(String[] args) throws AnalysisException, XPathExpressionException, JDOMException, IOException, ParseException {
        Options options = new Options();
        options.addOption("path", true, "file or directory path");
        options.addOption("ext", true, "file extension");
        
        CommandLineParser clParser = new GnuParser();
        CommandLine line = clParser.parse(options, args);
        String path = line.getOptionValue("path");
        String extension = "cermxml";
        if (line.hasOption("ext")) {
            extension = line.getOptionValue("ext");
        }
    	if (path == null){
    		System.err.println("USAGE: program DIR_PATH <EXTENSION>");
            System.err.println("Usage: ContentExtractor -path <path> [-ext <extension>]\n\n"
                             + "Tool for extracting metadata and content from PDF files.\n\n"
                             + "Arguments:\n"
                             + "  -path                 path to a PDF file or directory containing PDF files\n"
                             + "  -ext (optional)       the extension of the resulting metadata file;\n"
                             + "                        used only if passed path is a directory");
    		System.exit(1);
        }
        
        File file = new File(path);
        if (file.isFile()) {
            ContentExtractor extractor = new ContentExtractor();
            InputStream in = new FileInputStream(file);
            extractor.uploadPDF(in);
            Element result = extractor.getNLMContent();
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            System.out.println(outputter.outputString(result));
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
            
                System.out.println(pdf.getName());
 
                ContentExtractor extractor = new ContentExtractor();
                InputStream in = new FileInputStream(pdf);
                extractor.uploadPDF(in);
                Element result = extractor.getNLMContent();

                long end = System.currentTimeMillis();
                float elapsed = (end - start) / 1000F;
            
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                if (!xmlF.createNewFile()) {
                    System.out.println("Cannot create new file!");
                }
                FileUtils.writeStringToFile(xmlF, outputter.outputString(result));            
                i++;
                int percentage = i*100/files.size();
                System.out.println("Extraction time: " + Math.round(elapsed) + "s");
                System.out.println(percentage + "% done (" + i +" out of " + files.size() + ")");
                System.out.println("");
            }
        }
    }
    
}
