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
import pl.edu.icm.cermine.configuration.ExtractionConfigBuilder;
import pl.edu.icm.cermine.configuration.ExtractionConfigProperty;
import pl.edu.icm.cermine.configuration.ExtractionConfigRegister;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.PrefixTree;
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
    
    private Set<String> journals;
    private Set<String> surnames;
    private Set<String> insts;

    public CRFBibReferenceParser(String modelFile, String termsFile, String journalsFile, String surnamesFile, String instsFile) throws AnalysisException {
        InputStream modelIS;
        InputStream termsIS;
        InputStream journalsIS;
        InputStream surnamesIS;
        InputStream instsIS;
        try {
            modelIS = ResourceUtils.openResourceStream(modelFile);
            termsIS = ResourceUtils.openResourceStream(termsFile);
            journalsIS = ResourceUtils.openResourceStream(journalsFile);
            surnamesIS = ResourceUtils.openResourceStream(surnamesFile);
            instsIS = ResourceUtils.openResourceStream(instsFile);
            loadModels(modelIS, termsIS, journalsIS, surnamesIS, instsIS);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot set model!", ex);
        } finally {
        }
    }
    
    public CRFBibReferenceParser(InputStream modelIS, InputStream termsIS, InputStream journalsIS, InputStream surnamesIS, InputStream instsIS) throws AnalysisException {
        loadModels(modelIS, termsIS, journalsIS, surnamesIS, instsIS);
    }

    public void loadModels(InputStream modelIS, InputStream termsIS, InputStream journalsIS, InputStream surnamesIS, InputStream instsIS) throws AnalysisException {
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
        
        journals = new HashSet<String>();
        try {
            journals.addAll(IOUtils.readLines(journalsIS, "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(CRFBibReferenceParser.class.getName()).log(Level.SEVERE, "Cannot load common journals!", ex);
        }
        
        surnames = new HashSet<String>();
        try {
            surnames.addAll(IOUtils.readLines(surnamesIS, "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(CRFBibReferenceParser.class.getName()).log(Level.SEVERE, "Cannot load common surnames!", ex);
        }
        
        insts = new HashSet<String>();
        try {
            insts.addAll(IOUtils.readLines(instsIS, "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(CRFBibReferenceParser.class.getName()).log(Level.SEVERE, "Cannot load common institutions!", ex);
        }
    }
    
    @Override
    public BibEntry parseBibReference(String text) throws AnalysisException {
        Citation citation = parseToTokenList(text);
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
        
        PrefixTree journalsPt = new PrefixTree(PrefixTree.START_TERM);
        journalsPt.build(journals);
        PrefixTree surnamesPt = new PrefixTree(PrefixTree.START_TERM);
        surnamesPt.build(surnames);
        PrefixTree instPt = new PrefixTree(PrefixTree.START_TERM);
        instPt.build(insts);
        String data = StringUtils.join(CitationUtils.citationToMalletInputFormat(citation, terms, journalsPt, surnamesPt, instPt), "\n");
        
        Pipe pipe = model.getInputPipe();
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(new LineGroupIterator(new StringReader(data), Pattern.compile ("\\s*"), true)); 
        if (model.getBestLabels(instanceList).isEmpty()) {
            return citation;
        }
        LabelsSequence labelSequence = (LabelsSequence)model.getBestLabels(instanceList).get(0);
        
        for (int i = 0; i < labelSequence.size(); i++) {
            CitationTokenLabel label = CitationTokenLabel.valueOf(labelSequence.get(i).toString());
            if (CitationTokenLabel.getNormalizedLabel(label) != null) {
                label = CitationTokenLabel.getNormalizedLabel(label);
            }
            citation.getTokens().get(i).setLabel(label);
        }
        
        return citation;
    }
  
    public static CRFBibReferenceParser getInstance() throws AnalysisException {
        return new CRFBibReferenceParser(ExtractionConfigRegister.get().getStringProperty(ExtractionConfigProperty.BIBREF_MODEL_PATH),
            ExtractionConfigRegister.get().getStringProperty(ExtractionConfigProperty.BIBREF_TERMS_PATH),
            ExtractionConfigRegister.get().getStringProperty(ExtractionConfigProperty.BIBREF_JOURNALS_PATH),
            ExtractionConfigRegister.get().getStringProperty(ExtractionConfigProperty.BIBREF_SURNAMES_PATH),
            ExtractionConfigRegister.get().getStringProperty(ExtractionConfigProperty.BIBREF_INSTITUTIONS_PATH));
    }
    
    public static void main(String[] args) throws ParseException, AnalysisException, TransformationException {
        Options options = new Options();
        options.addOption("reference", true, "reference text");
        options.addOption("format", true, "output format");
        options.addOption("configuration", true, "config path");
        
        CommandLineParser clParser = new DefaultParser();
        CommandLine line = clParser.parse(options, args);
        String referenceText = line.getOptionValue("reference");
        String outputFormat = line.getOptionValue("format");

        ExtractionConfigBuilder builder = new ExtractionConfigBuilder();
        if (line.hasOption("configuration")) {
            builder.addConfiguration(line.getOptionValue("configuration"));
        }
        ExtractionConfigRegister.set(builder.buildConfiguration());

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
