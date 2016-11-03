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

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.ContentExtractor;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMConverter;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class CitationDatasetGenerator {

    private static final String OUT_NLM = "citations.nxml";
    private static final String OUT_BT = "citations.bibtex";
    private static final String OUT_TXT = "citations.txt";

    private static final int MAX_SET_SIZE = 100;
    private static final int TRIES_PER_FILE = 10;
    
    public static void main(String[] args) throws JDOMException, IOException, AnalysisException, TransformationException {
        if (args.length != 2) {
            System.out.println("Usage: PubMedToNLM <INPUT_DIR> <OUTPUT_DIR>");
            System.exit(1);
        }
        Set<BibEntry> citations = new HashSet<BibEntry>();
        
        File dir = new File(args[0]);
        Collection<File> files = FileUtils.listFiles(dir, new String[]{"pdf"}, true);
        Random random = new Random(5268);
        for (File file : files) {
            if (citations.size() == MAX_SET_SIZE) {
                break;
            }
            System.out.println("Processing: " + file.getPath());
            InputStream isRefs = new FileInputStream(file);
            ContentExtractor extractor = new ContentExtractor();
            extractor.setPDF(isRefs);
            List<BibEntry> cermineCitations = extractor.getReferences();

            File nlm = new File(file.getPath().replace(".pdf", ".nxml"));
            FileInputStream isNLM = new FileInputStream(nlm);
            InputSource sourceNlm = new InputSource(isNLM);
            List<Citation> nlmCitations = NlmCitationExtractor.extractCitations(sourceNlm);
                
            Set<Integer> usedIndices = new HashSet<Integer>();
            int added = 0;
            for (int j = 0; j < Math.min(nlmCitations.size(), TRIES_PER_FILE); j++) {
                if (citations.size() == MAX_SET_SIZE) {
                   break;
                }
                int index = random.nextInt(nlmCitations.size());
                if (usedIndices.contains(index)) {
                    continue;
                }
                usedIndices.add(index);
                Citation nlmCitation = nlmCitations.get(index);
                BibEntry nlmBibEntry = CitationUtils.citationToBibref(nlmCitation);
                String nlmTitle = nlmBibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE);
                if (nlmTitle == null) {
                    continue;
                }
                nlmTitle = nlmTitle.replaceAll("\\s+", " ");
                for (BibEntry cermineCitation: cermineCitations) {
                    String cermineText = cermineCitation.getText().replaceAll("\\s+", " ");
                    if (cermineText.contains(nlmTitle)) {
                        Citation matchedCitation = CitationUtils.stringToCitation(cermineText);
                        List<CitationToken> nlmTokens = Lists.newArrayList(nlmCitation.getTokens());
                        for (CitationToken matchedToken : matchedCitation.getTokens()) {
                            matchedToken.setLabel(CitationTokenLabel.TEXT);
                            if (matchedToken.getText().equals(",") 
                                    || matchedToken.getText().equals(".") 
                                    || matchedToken.getText().equals(":") 
                                    || matchedToken.getText().equals(";")) {
                                continue;
                            }
                            
                            CitationToken chosenNlmToken = null;
                            for (CitationToken nlmToken : nlmTokens) {
                                if (nlmToken.getText().equals(matchedToken.getText())) {
                                    matchedToken.setLabel(nlmToken.getLabel());
                                    chosenNlmToken = nlmToken;
                                    break;
                                }
                            }
                            if (chosenNlmToken != null) {
                                nlmTokens.remove(chosenNlmToken);
                            }
                        }
                        List<CitationToken> tokens = matchedCitation.getTokens();
                        for (int k = 1; k < tokens.size()-1; k++) {
                            CitationToken prev = tokens.get(k-1);
                            CitationToken ct = tokens.get(k);
                            CitationToken next = tokens.get(k+1);
                            if (ct.getText().length() == 1 && prev.getLabel().equals(next.getLabel())
                                    && !CitationTokenLabel.TEXT.equals(next.getLabel())) {
                                ct.setLabel(prev.getLabel());
                            }
                        }
                        
                        for (int k = 1; k < tokens.size()-1; k++) {
                            CitationToken prev = tokens.get(k-1);
                            CitationToken ct = tokens.get(k);
                            if (ct.getLabel().equals(CitationTokenLabel.ARTICLE_TITLE)) {
                                break;
                            }
                            if (ct.getText().matches("[A-Z]")
                                    || (prev.getText().matches("[A-Z]") && ct.getText().equals("."))) {
                                ct.setLabel(CitationTokenLabel.GIVENNAME);
                            }
                        }
                        
                        BibEntry matchedBibEntry = CitationUtils.citationToBibref(matchedCitation);
                        
                        int unlabelledTotalLen = 0;
                        int totalLen = 0;
                        for (CitationToken ct : matchedCitation.getTokens()) {
                            totalLen += ct.getText().length();
                            if (ct.getLabel().equals(CitationTokenLabel.TEXT)) {
                                unlabelledTotalLen += ct.getText().length();
                            }
                        }
                        if (unlabelledTotalLen > 0.25 * totalLen) {
                            continue;
                        }
                        citations.add(matchedBibEntry);
                        added++;
                    }
                }
            }

            System.out.println("Citations added: " + added);
        }

        File nlm = new File(args[1] + OUT_NLM);
        File bt = new File(args[1] + OUT_BT);
        File txt = new File(args[1] + OUT_TXT);
        
        BibEntryToNLMConverter converter = new BibEntryToNLMConverter();
        XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
        
        int k = 1;
        for (BibEntry citation : citations) {
            Element element = converter.convert(citation);
            element.setAttribute("id", String.valueOf(k++));
            
            FileUtils.writeStringToFile(nlm, outputter.outputString(element), "UTF-8", true);
            FileUtils.writeStringToFile(bt, citation.toBibTeX(), "UTF-8", true);
            FileUtils.writeStringToFile(txt, citation.getText(), "UTF-8", true);
            FileUtils.writeStringToFile(nlm, "\n", "UTF-8", true);
            FileUtils.writeStringToFile(bt, "\n", "UTF-8", true);
            FileUtils.writeStringToFile(txt, "\n", "UTF-8", true);
        }

    }

    private CitationDatasetGenerator() {
    }

}
