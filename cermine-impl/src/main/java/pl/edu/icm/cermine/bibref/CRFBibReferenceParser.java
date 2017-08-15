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

package pl.edu.icm.cermine.bibref;

import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.pipe.iterator.LineGroupIterator;
import edu.umass.cs.mallet.base.types.InstanceList;
import edu.umass.cs.mallet.base.types.LabelsSequence;
import edu.umass.cs.mallet.grmm.learning.ACRF;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMConverter;
import pl.edu.icm.cermine.configuration.ExtractionConfigProperty;
import pl.edu.icm.cermine.configuration.ExtractionConfigRegister;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.ResourceUtils;

/**
 * CRF-based bibiliographic reference parser.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CRFBibReferenceParser implements BibReferenceParser<BibEntry> {
    
    private static final int MAX_REFERENCE_LENGTH = 3000;
    
    private ACRF model;
    
    private Set<String> terms;

    public CRFBibReferenceParser(String modelFile, String termsFile) throws AnalysisException {
        InputStream modelIS;
        InputStream termsIS;
        try {
            modelIS = ResourceUtils.openResourceStream(modelFile);
            termsIS = ResourceUtils.openResourceStream(termsFile);
            loadModels(modelIS, termsIS);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } finally {
        }
    }
    
    public CRFBibReferenceParser(InputStream modelIS, InputStream termsIS) throws AnalysisException {
        loadModels(modelIS, termsIS);
    }

    public void loadModels(InputStream modelIS, InputStream termsIS) throws AnalysisException {
        // prevents MALLET from printing info messages
        System.setProperty("java.util.logging.config.file",
            "edu/umass/cs/mallet/base/util/resources/logging.properties");
        
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(modelIS)));
            model = (ACRF)(ois.readObject());
        } catch (IOException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } catch (ClassNotFoundException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                throw new AnalysisException("Cannot set model!", ex);
            }
        }
        
        terms = new HashSet<String>();
        try {
            terms.addAll(IOUtils.readLines(termsIS, "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(CRFBibReferenceParser.class.getName()).log(Level.SEVERE, "Cannot load common words!", ex);
        }
    }
    
    @Override
    public BibEntry parseBibReference(String text) throws AnalysisException {
        if (text.length() > MAX_REFERENCE_LENGTH) {
            return new BibEntry().setText(text);
        }
        
        if (model == null) {
            throw new AnalysisException("Model object is not set!");
        }
        
        Citation citation = CitationUtils.stringToCitation(text);
        String data = StringUtils.join(CitationUtils.citationToMalletInputFormat(citation, terms), "\n");
        
        Pipe pipe = model.getInputPipe();
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(new LineGroupIterator(new StringReader(data), Pattern.compile ("\\s*"), true)); 
        if (model.getBestLabels(instanceList).isEmpty()) {
            return new BibEntry().setText(text);
        }
        LabelsSequence labelSequence = (LabelsSequence)model.getBestLabels(instanceList).get(0);
           
        for (int i = 0; i < labelSequence.size(); i++) {
            citation.getTokens().get(i).setLabel(CitationTokenLabel.valueOf(labelSequence.get(i).toString()));
        }
        
        return CitationUtils.citationToBibref(citation);
    }
    
    public Citation parseToTokenList(String text) throws AnalysisException {
        if (model == null) {
            throw new AnalysisException("Model object is not set!");
        }
        Citation citation = CitationUtils.stringToCitation(text);
        if (text.length() > MAX_REFERENCE_LENGTH) {
            return citation;
        }
        
        String data = StringUtils.join(CitationUtils.citationToMalletInputFormat(citation, terms), "\n");
        
        Pipe pipe = model.getInputPipe();
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(new LineGroupIterator(new StringReader(data), Pattern.compile ("\\s*"), true)); 
        LabelsSequence labelSequence = (LabelsSequence)model.getBestLabels(instanceList).get(0);
           
        for (int i = 0; i < labelSequence.size(); i++) {
            citation.getTokens().get(i).setLabel(CitationTokenLabel.valueOf(labelSequence.get(i).toString()));
        }
        
        return citation;
    }
  
    public static CRFBibReferenceParser getInstance() throws AnalysisException {
        return new CRFBibReferenceParser(ExtractionConfigRegister.get().getStringProperty(ExtractionConfigProperty.BIBREF_MODEL_PATH),
            ExtractionConfigRegister.get().getStringProperty(ExtractionConfigProperty.BIBREF_TERMS_PATH));
    }
    
    public static void main(String[] args) throws ParseException, AnalysisException, TransformationException {
        Options options = new Options();
        options.addOption("reference", true, "reference text");
        options.addOption("format", true, "output format");
        
        CommandLineParser clParser = new DefaultParser();
        CommandLine line = clParser.parse(options, args);
        String referenceText = line.getOptionValue("reference");
        String outputFormat = line.getOptionValue("format");
        
        if (referenceText == null || (outputFormat != null && !outputFormat.equals("bibtex") && !outputFormat.equals("nlm"))) {
       		System.err.println("Usage: CRFBibReferenceParser -ref <reference text> [-format <output format>]\n\n"
                             + "Tool for extracting metadata from reference strings.\n\n"
                             + "Arguments:\n"
                             + "  -reference            the text of the reference\n"
                             + "  -format (optional)    the format of the output,\n"
                             + "                        possible values: BIBTEX (default) and NLM");
            System.exit(1);
        }
        
        CRFBibReferenceParser parser = CRFBibReferenceParser.getInstance();
        BibEntry reference = parser.parseBibReference(referenceText);
        if (outputFormat == null || outputFormat.equals("bibtex")) {
            System.out.println(reference.toBibTeX());
        } else {
            BibEntryToNLMConverter converter = new BibEntryToNLMConverter();
            Element element = converter.convert(reference);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            System.out.println(outputter.outputString(element));
        }
    }
    
}
