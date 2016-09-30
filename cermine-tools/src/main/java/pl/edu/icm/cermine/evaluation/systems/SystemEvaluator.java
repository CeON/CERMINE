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

package pl.edu.icm.cermine.evaluation.systems;

import java.io.FileNotFoundException;
import java.io.FileReader;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils;
import pl.edu.icm.cermine.evaluation.tools.NlmPair;
import pl.edu.icm.cermine.evaluation.tools.NlmIterator;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.content.model.DocumentSection;
import pl.edu.icm.cermine.evaluation.exception.EvaluationException;
import pl.edu.icm.cermine.evaluation.tools.RelationInformationResult.StringRelation;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentAuthor;
import pl.edu.icm.cermine.metadata.model.DocumentDate;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.model.Document;
import pl.edu.icm.cermine.tools.transformers.FormatToModelReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class SystemEvaluator {
    
    protected abstract List<EvalInformationType> getTypes();
    
    protected abstract FormatToModelReader<Document> getOrigReader();
    
    protected abstract FormatToModelReader<Document> getExtrReader();

    protected Document getOriginal(NlmPair pair) throws EvaluationException {
        try {
            return getOrigReader().read(new FileReader(pair.getOriginalNlm()));
        } catch (TransformationException ex) {
            throw new EvaluationException(ex);
        } catch (FileNotFoundException ex) {
            throw new EvaluationException(ex);
        }
    }

    protected Document getExtracted(NlmPair pair) throws EvaluationException {
        try {
            return getExtrReader().read(new FileReader(pair.getExtractedNlm()));
        } catch (TransformationException ex) {
            throw new EvaluationException(ex);
        } catch (FileNotFoundException ex) {
            throw new EvaluationException(ex);
        }
    }
    
    public DocumentSetResult evaluate(int mode, NlmIterator files) throws EvaluationException {
        DocumentSetResult results = new DocumentSetResult(getTypes());
        
        if (mode == 1) {
            System.out.println("path," + StringUtils.join(getTypes(), ","));
        }
        
        int i = 0;
        for (NlmPair pair : files) {
            i++;
            
            String file = pair.getOriginalNlm().getPath();
            Document origDoc = getOriginal(pair);
            Document testDoc = getExtracted(pair);
            
            for (EvalInformationType type: getTypes()) {
                switch (type) {
                    case TITLE:
                        results.addResult(file, new SimpleInformationResult(type,
                                EvaluationUtils.swComparator, 
                                origDoc.getMetadata().getTitle(), 
                                testDoc.getMetadata().getTitle()));
                        break;
                    case ABSTRACT:
                        results.addResult(file, new SimpleInformationResult(type,
                                EvaluationUtils.swComparator, 
                                origDoc.getMetadata().getAbstrakt(), 
                                testDoc.getMetadata().getAbstrakt()));
                        break;
                    case KEYWORDS:
                        results.addResult(file, new ListInformationResult(type,
                                origDoc.getMetadata().getKeywords(), 
                                testDoc.getMetadata().getKeywords()));
                        break;
                    case AUTHORS:
                        List<String> authorOrig = new ArrayList<String>();
                        for (DocumentAuthor author: origDoc.getMetadata().getAuthors()) {
                            authorOrig.add(author.getName());
                        }
                        List<String> authorTest = new ArrayList<String>();
                        for (DocumentAuthor author: testDoc.getMetadata().getAuthors()) {
                            authorTest.add(author.getName());
                        }
                        results.addResult(file, new ListInformationResult(type,
                                EvaluationUtils.authorComparator,
                                authorOrig, authorTest));
                        break;
                    case AFFILIATIONS:
                        List<String> affOrig = new ArrayList<String>();
                        for (DocumentAffiliation aff: origDoc.getMetadata().getAffiliations()) {
                            affOrig.add(aff.getRawText());
                        }
                        List<String> affTest = new ArrayList<String>();
                        for (DocumentAffiliation aff: testDoc.getMetadata().getAffiliations()) {
                            affTest.add(aff.getRawText());
                        }
                        results.addResult(file, new ListInformationResult(type,
                                EvaluationUtils.cosineComparator(),
                                affOrig, affTest));
                        break;
                    case AUTHOR_AFFILIATIONS:
                        Set<StringRelation> relOrig = new HashSet<StringRelation>();
                        for (DocumentAuthor author: origDoc.getMetadata().getAuthors()) {
                            for (DocumentAffiliation aff: author.getAffiliations()) {
                                relOrig.add(new StringRelation(author.getName(), aff.getRawText()));
                            }
                        }
                        Set<StringRelation> relTest = new HashSet<StringRelation>();
                        for (DocumentAuthor author: testDoc.getMetadata().getAuthors()) {
                            for (DocumentAffiliation aff: author.getAffiliations()) {
                                relTest.add(new StringRelation(author.getName(), aff.getRawText()));
                            }
                        }
                        results.addResult(file, new RelationInformationResult(type,
                                EvaluationUtils.authorComparator, EvaluationUtils.cosineComparator(),
                                relOrig, relTest));
                        break;
                    case EMAILS:
                        results.addResult(file, new ListInformationResult(type,
                                EvaluationUtils.emailComparator,
                                origDoc.getMetadata().getEmails(),
                                testDoc.getMetadata().getEmails()));
                        break;
                    case AUTHOR_EMAILS:
                        Set<StringRelation> emailsOrig = new HashSet<StringRelation>();
                        for (DocumentAuthor author: origDoc.getMetadata().getAuthors()) {
                            for (String email: author.getEmails()) {
                                emailsOrig.add(new StringRelation(author.getName(), email));
                            }
                        }
                        Set<StringRelation> emailsTest = new HashSet<StringRelation>();
                        for (DocumentAuthor author: testDoc.getMetadata().getAuthors()) {
                            for (String email: author.getEmails()) {
                                emailsTest.add(new StringRelation(author.getName(), email));
                            }
                        }
                        results.addResult(file, new RelationInformationResult(type,
                                EvaluationUtils.authorComparator, EvaluationUtils.emailComparator,
                                emailsOrig, emailsTest));
                        break;
                    case JOURNAL:
                        results.addResult(file, new SimpleInformationResult(type,
                                EvaluationUtils.journalComparator, 
                                origDoc.getMetadata().getJournal(), 
                                testDoc.getMetadata().getJournal()));
                        break;
                    case VOLUME:
                        results.addResult(file, new SimpleInformationResult(type,
                                origDoc.getMetadata().getVolume(), 
                                testDoc.getMetadata().getVolume()));
                        break;
                    case ISSUE:
                        results.addResult(file, new SimpleInformationResult(type,
                                origDoc.getMetadata().getIssue(),
                                testDoc.getMetadata().getIssue()));
                        break;
                    case PAGES:
                        results.addResult(file, new SimpleInformationResult(type,
                                origDoc.getMetadata().getFirstPage() + "--" + origDoc.getMetadata().getLastPage(),
                                testDoc.getMetadata().getFirstPage() + "--" + testDoc.getMetadata().getLastPage()));
                        break;
                    case YEAR:
                        String origYear = null;
                        if (origDoc.getMetadata().getDate(DocumentDate.DATE_PUBLISHED) != null) {
                            origYear = origDoc.getMetadata().getDate(DocumentDate.DATE_PUBLISHED).getYear();
                        }
                        String testYear = null;
                        if (testDoc.getMetadata().getDate(DocumentDate.DATE_PUBLISHED) != null) {
                            testYear = origDoc.getMetadata().getDate(DocumentDate.DATE_PUBLISHED).getYear();
                        }
                        results.addResult(file, new SimpleInformationResult(type,
                                origYear, testYear));
                        break;
                    case DOI:
                        results.addResult(file, new SimpleInformationResult(type,
                                origDoc.getMetadata().getId(DocumentMetadata.ID_DOI),
                                testDoc.getMetadata().getId(DocumentMetadata.ID_DOI)));
                        break;
                    case HEADERS:
                        List<String> headerOrig = new ArrayList<String>();
                        for (DocumentSection section: origDoc.getContent().getSections(true)) {
                            headerOrig.add(section.getTitle());
                        }
                        List<String> headerTest = new ArrayList<String>();
                        for (DocumentSection section: testDoc.getContent().getSections(true)) {
                            headerTest.add(section.getTitle());
                        }
                        results.addResult(file, new ListInformationResult(type,
                                EvaluationUtils.swComparator,
                                headerOrig, headerTest));
                        break;
                    case HEADER_LEVELS:
                        Set<StringRelation> headersOrig = new HashSet<StringRelation>();
                        for (DocumentSection section: origDoc.getContent().getSections(true)) {
                            headersOrig.add(new StringRelation(
                                    String.valueOf(section.getLevel()), 
                                    section.getTitle()));
                        }
                        Set<StringRelation> headersTest = new HashSet<StringRelation>();
                        for (DocumentSection section: testDoc.getContent().getSections(true)) {
                            headersTest.add(new StringRelation(
                                    String.valueOf(section.getLevel()), 
                                    section.getTitle()));
                        }
                        results.addResult(file, new RelationInformationResult(type,
                                EvaluationUtils.defaultComparator, EvaluationUtils.swComparator,
                                headersOrig, headersTest));
                        break;
                    case REFERENCES:
                        List<String> origRefs = new ArrayList<String>();
                        for (BibEntry entry: origDoc.getReferences()) {
                            origRefs.add(entry.getText());
                        }
                        List<String> testRefs = new ArrayList<String>();
                        for (BibEntry entry: testDoc.getReferences()) {
                            testRefs.add(entry.getText());
                        }
                        results.addResult(file, new ListInformationResult(type,
                                EvaluationUtils.cosineComparator(.6),
                                origRefs, testRefs));
                        break;
                }
            }
            
            if (mode == 1) {
                results.printCSV(file);
            } else if (mode == 0) {
                results.printDocument(file, i);
            }
        }
        
        results.evaluate();
            
        if (mode != 1) {
            System.out.println("==== Summary (" + files.size() + " docs)====");
            for (EvalInformationType type: getTypes()) {
                results.printTypeSummary(type);
            }
            results.printTotalSummary();
        }
        return results;
    }

    public void process(String[] args) throws EvaluationException {
        if (args.length != 3 && args.length != 4) {
            System.out.println("Usage: " + this.getClass().getSimpleName() + " <input dir> <orig extension> <extract extension> <mode>");
            return;
        }
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];

        int mode = 0;
        if (args.length == 4 && args[3].equals("csv")) {
            mode = 1;
        }
        if (args.length == 4 && args[3].equals("q")) {
            mode = 2;
        }

        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        evaluate(mode, iter);
    }
    
}
