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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class CoraRefToNLM {

    private static final String NLM_DIR = "cermine-tests/mixed.citations.xml";
    private static final String OUT_NLM = "results/citations/citations.nxml";
    private static final String OUT_BT = "results/citations/citations.bibtex";
    private static final String OUT_TXT = "results/citations/citations.txt";
    
    
    public static void main(String[] args) throws JDOMException, IOException, TransformationException {
        InputStream is = null;
        List<Citation> citations;
        try {
            is = new FileInputStream(new File(NLM_DIR));
            InputSource source = new InputSource(is);
            citations = NlmCitationExtractor.extractCitations(source);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        outer:
        for (Citation citation : citations) {
            int ind = 0;
            boolean pagef = true;

            for (CitationToken ct : citation.getTokens()) {
                if (CitationTokenLabel.YEAR.equals(ct.getLabel())
                        && ct.getText().length() < 4) {
                    ct.setLabel(CitationTokenLabel.TEXT);
                }
                if (CitationTokenLabel.ARTICLE_TITLE.equals(ct.getLabel())
                        && ind < citation.getTokens().size() - 1) {
                    CitationToken next = citation.getTokens().get(ind + 1);
                    if ((ct.getText().equals(".") || ct.getText().equals(",")) && !CitationTokenLabel.ARTICLE_TITLE.equals(next.getLabel())) {
                        ct.setLabel(CitationTokenLabel.TEXT);
                    }
                }
                if (CitationTokenLabel.SOURCE.equals(ct.getLabel())
                        && ind < citation.getTokens().size() - 1) {
                    CitationToken next = citation.getTokens().get(ind + 1);
                    if ((ct.getText().equals(".") || ct.getText().equals(",")) && !CitationTokenLabel.SOURCE.equals(next.getLabel())) {
                        ct.setLabel(CitationTokenLabel.TEXT);
                    }
                }
                if (CitationTokenLabel.PAGEF.equals(ct.getLabel())) {
                    if (ct.getText().matches(".*\\d")) {
                        if (pagef) {
                            ct.setLabel(CitationTokenLabel.PAGEF);
                            pagef = false;
                        } else {
                            ct.setLabel(CitationTokenLabel.PAGEL);
                            pagef = true;
                        }
                    } else {
                        ct.setLabel(CitationTokenLabel.TEXT);
                    }
                }

                ind++;
            }

            ind = 0;
            CitationTokenLabel lastLabel = CitationTokenLabel.TEXT;
            for (CitationToken ct : citation.getTokens()) {
                if (CitationTokenLabel.SOURCE.equals(lastLabel)
                        && CitationTokenLabel.TEXT.equals(ct.getLabel())
                        && ct.getText().matches("\\d+")) {
                    ct.setLabel(CitationTokenLabel.VOLUME);
                }
                if (CitationTokenLabel.VOLUME.equals(lastLabel)
                        && CitationTokenLabel.TEXT.equals(ct.getLabel())
                        && ct.getText().matches("\\d+")) {
                    ct.setLabel(CitationTokenLabel.ISSUE);
                }
                if (!CitationTokenLabel.TEXT.equals(ct.getLabel())) {
                    lastLabel = ct.getLabel();
                }
                ind++;
            }

            BibEntry be = CitationUtils.citationToBibref(citation);

            List<String> toks = new ArrayList<String>();
            List<CitationTokenLabel> labs = new ArrayList<CitationTokenLabel>();

            for (String s : be.getAllFieldValues(BibEntry.FIELD_AUTHOR)) {
                String tmp = s.trim().replaceAll(" +,", ",").replaceAll("\\.", " ").replaceAll(",", ", ").replaceAll(" +", " ");
                Pattern p1 = Pattern.compile("^[A-Z] [A-Z][-A-Za-z]+ ?($|,|and|AND|&)");
                Pattern p2 = Pattern.compile("^[A-Z] [A-Z] [A-Z][-A-Za-z]+ ?($|,|and|AND|&)");
                Pattern p3 = Pattern.compile("^[A-Z][-A-Za-z]+, [A-Z] ?($|,|and|AND|&)");
                Pattern p4 = Pattern.compile("^[A-Z][-A-Za-z]+, [A-Z] ?[A-Z] ?($|,|and|AND|&)");
                Pattern p5 = Pattern.compile("^[A-Z][-A-Za-z]+ [A-Z] [A-Z][-A-Za-z]+ ?($|,|and|AND|&)");
                Pattern p6 = Pattern.compile("^[A-Z][-A-Za-z]+ [A-Z] [A-Z] [A-Z][-A-Za-z]+ ?($|,|and|AND|&)");
                Pattern p7 = Pattern.compile("^[A-Z][-A-Za-z]+ [A-Z] ?($|,|and|AND|&)");
                Pattern p8 = Pattern.compile("^[A-Z][-A-Za-z]+ [A-Z] [A-Z] ?($|,|and|AND|&)");
                Pattern p9 = Pattern.compile("^[A-Z][-A-Za-z]+, [A-Z][-A-Za-z]+ ?($|,|and|AND|&)");
                Pattern p10 = Pattern.compile("^[A-Z][-A-Za-z]+ [A-Z][-A-Za-z]+ ?($|,|and|AND|&)");

                while (!tmp.isEmpty()) {
                    tmp = tmp.trim().replaceAll("^, ", "").replaceAll("^and ", "").replaceAll("^& ", "");
                    Matcher m1 = p1.matcher(tmp);
                    Matcher m2 = p2.matcher(tmp);
                    Matcher m3 = p3.matcher(tmp);
                    Matcher m4 = p4.matcher(tmp);
                    Matcher m5 = p5.matcher(tmp);
                    Matcher m6 = p6.matcher(tmp);
                    Matcher m7 = p7.matcher(tmp);
                    Matcher m8 = p8.matcher(tmp);
                    Matcher m9 = p9.matcher(tmp);
                    Matcher m10 = p10.matcher(tmp);
                    if (m1.find() && m1.start() == 0) {

                        String author = m1.group();
                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);
                        author = author.substring(2).replaceAll("[^-A-Za-z].*", "");
                        d(author, toks, labs, CitationTokenLabel.SURNAME);

                        tmp = tmp.substring(m1.end());
                    } else if (m2.find() && m2.start() == 0) {

                        String author = m2.group();
                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);
                        initials = author.substring(2, 3);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);
                        author = author.substring(4).replaceAll("[^-A-Za-z].*", "");
                        d(author, toks, labs, CitationTokenLabel.SURNAME);
                        tmp = tmp.substring(m2.end());
                    } else if (m3.find() && m3.start() == 0) {
                        String author = m3.group();
                        String sn = author.replaceAll(",.*", "");
                        d(sn, toks, labs, CitationTokenLabel.SURNAME);
                        author = author.replaceAll("[^, ]+, ", "");

                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        tmp = tmp.substring(m3.end());
                    } else if (m4.find() && m4.start() == 0) {
                        String author = m4.group();
                        String sn = author.replaceAll(",.*", "");
                        d(sn, toks, labs, CitationTokenLabel.SURNAME);
                        author = author.replaceAll("[^, ]+, ", "");

                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        author = author.substring(1).trim();
                        initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        tmp = tmp.substring(m4.end());
                    } else if (m5.find() && m5.start() == 0) {
                        String author = m5.group();
                        String sn = author.replaceAll(" .*", "");
                        d(sn, toks, labs, CitationTokenLabel.GIVENNAME);
                        author = author.replaceAll("^[-a-zA-Z]+ ", "");
                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        author = author.substring(1).trim().replaceAll("( |,|&).*", "");
                        d(author, toks, labs, CitationTokenLabel.SURNAME);

                        tmp = tmp.substring(m5.end());
                    } else if (m6.find() && m6.start() == 0) {
                        String author = m6.group();
                        String sn = author.replaceAll(" .*", "");
                        d(sn, toks, labs, CitationTokenLabel.GIVENNAME);

                        author = author.replaceAll("^[-a-zA-Z]+ ", "");
                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        author = author.substring(1).trim();
                        initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        author = author.substring(1).trim().replaceAll("( |,|&).*", "");
                        d(author, toks, labs, CitationTokenLabel.SURNAME);

                        tmp = tmp.substring(m6.end());
                    } else if (m7.find() && m7.start() == 0) {
                        String author = m7.group();
                        String sn = author.replaceAll(" .*", "");
                        d(sn, toks, labs, CitationTokenLabel.SURNAME);
                        author = author.replaceAll("^[^ ]+ ", "");

                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);
                        tmp = tmp.substring(m7.end());
                    } else if (m8.find() && m8.start() == 0) {
                        String author = m8.group();
                        String sn = author.replaceAll(" .*", "");
                        d(sn, toks, labs, CitationTokenLabel.SURNAME);
                        author = author.replaceAll("^[^ ]+ ", "");

                        String initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        author = author.substring(1).trim();
                        initials = author.substring(0, 1);
                        toks.add(initials);
                        labs.add(CitationTokenLabel.GIVENNAME);

                        tmp = tmp.substring(m8.end());
                    } else if (m9.find() && m9.start() == 0) {
                        String author = m9.group();
                        String sn = author.replaceAll(",.*", "");
                        d(sn, toks, labs, CitationTokenLabel.SURNAME);

                        author = author.replaceAll("^[^ ]+ ", "");
                        sn = author.replaceAll("( |,|&).*", "");
                        d(sn, toks, labs, CitationTokenLabel.GIVENNAME);

                        tmp = tmp.substring(m9.end());
                    } else if (m10.find() && m10.start() == 0) {
                        String author = m10.group();
                        String sn = author.replaceAll(" .*", "");
                        d(sn, toks, labs, CitationTokenLabel.GIVENNAME);

                        author = author.replaceAll("^[^ ]+ ", "");
                        sn = author.replaceAll("( |,|&).*", "");
                        d(sn, toks, labs, CitationTokenLabel.SURNAME);

                        tmp = tmp.substring(m10.end());
                    } else {
                        break;
                    }
                }
                if (!tmp.isEmpty()) {
                    continue outer;
                }
            }

            ind = 0;
            CitationToken prev = null;
            for (CitationToken ct : citation.getTokens()) {
                if (CitationTokenLabel.SURNAME.equals(ct.getLabel())) {
                    if (toks.isEmpty()) {
                        break;
                    }
                    if (ind < toks.size() && ct.getText().equals(toks.get(ind))) {
                        ct.setLabel(labs.get(ind));
                        ind++;
                    } else if (prev != null && CitationTokenLabel.GIVENNAME.equals(prev.getLabel())
                            && ct.getText().equals(".")) {
                        ct.setLabel(CitationTokenLabel.GIVENNAME);
                    } else {
                        ct.setLabel(CitationTokenLabel.TEXT);
                    }
                }
                prev = ct;
            }
        }
        
        File nlm = new File(OUT_NLM);
        File bt = new File(OUT_BT);
        File txt = new File(OUT_TXT);
        
        BibEntryToNLMConverter conv = new BibEntryToNLMConverter();
        XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
        
        int k = 3421;
        for (Citation citation : citations) {
            Element e = conv.convert(CitationUtils.citationToBibref(citation));
            e.setAttribute("id", String.valueOf(k++));
            
            FileUtils.writeStringToFile(nlm, outputter.outputString(e), "UTF-8", true);
            FileUtils.writeStringToFile(bt, CitationUtils.citationToBibref(citation).toBibTeX(), "UTF-8", true);
            FileUtils.writeStringToFile(txt, CitationUtils.citationToBibref(citation).getText(), "UTF-8", true);
            FileUtils.writeStringToFile(nlm, "\n", "UTF-8", true);
            FileUtils.writeStringToFile(bt, "\n", "UTF-8", true);
            FileUtils.writeStringToFile(txt, "\n", "UTF-8", true);
        }
    }

    private static void d(String text, List<String> toks, List<CitationTokenLabel> labs,
            CitationTokenLabel label) {
        while (!text.isEmpty()) {
            if (text.charAt(0) == '-') {
                toks.add("-");
                labs.add(label);
                text = text.substring(1);
            } else {
                toks.add(text.replaceAll("-.*", ""));
                labs.add(label);
                text = text.replaceAll("^[a-zA-Z]+", "");
            }
        }
    }

    private CoraRefToNLM() {
    }
}
