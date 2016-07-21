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
public final class PubMedToNLM {
    
    private static final String NLM_DIR = "refs/data/";
    private static final String OUT_NLM = "results/citations/citations.nxml";
    private static final String OUT_BT = "results/citations/citations.bibtex";
    private static final String OUT_TXT = "results/citations/citations.txt";
    

    public static void main(String[] args) throws JDOMException, IOException, AnalysisException, TransformationException {
        
        File dir = new File(NLM_DIR);
        
        Set<BibEntry> citations = new HashSet<BibEntry>();
        
        Collection<File> files = FileUtils.listFiles(dir, new String[]{"refs"}, true);
        int i = 0;
        for (File file : files) {
            InputStream is = null;
            List<Citation> extrCitations;
            try {
                is = new FileInputStream(file);
                InputSource source = new InputSource(is);
                extrCitations = NlmCitationExtractor.extractCitations(source);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            File nlm = new File(file.getPath().replace(".refs", ".nxml"));
            FileInputStream fis = new FileInputStream(nlm);
            InputSource source2 = new InputSource(fis);
            List<Citation> origCitations = NlmCitationExtractor.extractCitations(source2);
                
            List<BibEntry> entries = new ArrayList<BibEntry>();

            for (int j = 0; j < 10; j++) {
                int index = (int) (origCitations.size() * Math.random());
                Citation chosen = origCitations.get(index);
                BibEntry be = CitationUtils.citationToBibref(chosen);
                String title = be.getFirstFieldValue(BibEntry.FIELD_TITLE);
                if (title == null) {
                    continue;
                }
                title = title.replaceAll("\\s+", " ");
                for (Citation extrCitation: extrCitations) {
                    String citationText = extrCitation.getText().replaceAll("\\s+", " ");
                    if (citationText.contains(title)) {
                        Citation matchedCitation = CitationUtils.stringToCitation(citationText);
                        List<CitationToken> origTokens = chosen.getTokens();
                        for (CitationToken ct : matchedCitation.getTokens()) {
                            ct.setLabel(CitationTokenLabel.TEXT);
                            if (ct.getText().equals(",") || ct.getText().equals(".") || ct.getText().equals(":") || ct.getText().equals(";")) {
                                continue;
                            }
                            
                            CitationToken chosenToken = null;
                            for (CitationToken ct2 : origTokens) {
                                if (ct2.getText().equals(ct.getText())) {
                                    ct.setLabel(ct2.getLabel());
                                    chosenToken = ct2;
                                    break;
                                }
                            }
                            if (chosenToken != null) {
                                origTokens.remove(chosenToken);
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
                        
                        BibEntry matchedEntry = CitationUtils.citationToBibref(matchedCitation);
                        
                        int sum = 0;
                        int all = 0;
                        for (CitationToken ct : matchedCitation.getTokens()) {
                            all += ct.getText().length();
                            if (ct.getLabel().equals(CitationTokenLabel.TEXT)) {
                                sum += ct.getText().length();
                            }
                        }
                        if (sum > 0.25 * all) {
                            continue;
                        }
                        boolean shouldAdd = true;
                        for (BibEntry b : entries) {
                            if (b.getText().equals(matchedEntry.getText())) {
                                shouldAdd = false;
                            }
                        }
                        if (shouldAdd) {
                            entries.add(matchedEntry);
                        }
                    }
                }
            }
                
            citations.addAll(entries);
            
            i++;
            System.out.println(i+" "+i*100./files.size()+"%");
        }

        File nlm = new File(OUT_NLM);
        File bt = new File(OUT_BT);
        File txt = new File(OUT_TXT);
        
        BibEntryToNLMConverter conv = new BibEntryToNLMConverter();
        XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
        
        int k = 1;
        for (BibEntry citation : citations) {
            Element e = conv.convert(citation);
            e.setAttribute("id", String.valueOf(k++));
            
            FileUtils.writeStringToFile(nlm, outputter.outputString(e), "UTF-8", true);
            FileUtils.writeStringToFile(bt, citation.toBibTeX(), "UTF-8", true);
            FileUtils.writeStringToFile(txt, citation.getText(), "UTF-8", true);
            FileUtils.writeStringToFile(nlm, "\n", "UTF-8", true);
            FileUtils.writeStringToFile(bt, "\n", "UTF-8", true);
            FileUtils.writeStringToFile(txt, "\n", "UTF-8", true);
        }

    }

    private PubMedToNLM() {
    }

}
