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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.VCARD;
import java.io.*;
import java.util.Collection;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.metadata.model.DocumentAuthor;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * NLM-based content extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class RDFGenerator {

    private ComponentConfiguration conf;
    
    public RDFGenerator() throws AnalysisException {
        conf = new ComponentConfiguration();
    }

    public ComponentConfiguration getConf() {
        return conf;
    }

    public void setConf(ComponentConfiguration conf) {
        this.conf = conf;
    }
    
    public static void main(String[] args) throws ParseException, IOException {
        Model model = ModelFactory.createDefaultModel();
        String prefix = "http://ceur-ws.org/";

        CommandLineOptionsParser parser = new CommandLineOptionsParser();
        if (!parser.parse(args)) {
            System.err.println(
                    "Usage: RDFGenerator -path <path> -output <output path>\n\n"
                  + "Tool for extracting metadata and content from PDF files.\n\n"
                  + "Arguments:\n"
                  + "  -path <path>              path to a PDF file or directory containing PDF files\n"
                  + "  -output <output path      path to output RDF file\n");
            System.exit(1);
        }
        
        String path = parser.getPath();
 
        File file = new File(path);
        Collection<File> files = FileUtils.listFiles(file, new String[]{"pdf"}, true);
    
        int i = 0;
        for (File pdf : files) {
          
            long start = System.currentTimeMillis();
            float elapsed = 0;
            
            System.out.println(pdf.getPath());
 
            try {
                String docName = pdf.getName().replaceFirst(".*#", "").replaceFirst(".pdf", "");
                String volume = pdf.getName().replaceFirst("#.*", "");
     
                RDFGenerator extractor = new RDFGenerator();
                InputStream in = new FileInputStream(pdf);
                
                BxDocument doc = ExtractionUtils.extractStructure(extractor.getConf(), in);

                DocumentMetadata metadata = ExtractionUtils.extractMetadata(extractor.getConf(), doc);
                BibEntry[] references = ExtractionUtils.extractReferences(extractor.getConf(), doc);

                String docURI = prefix + volume + "/#" + docName;
                String volumeURI = prefix + volume + "/";
                String volDoc = volume + "-" + docName;
                
                Resource volumeRes = model.createResource(volumeURI);
                Resource docRes = model.createResource(docURI);
                docRes.addProperty(DC.source, volumeRes);
                docRes.addProperty(DC.identifier, prefix + volume + "/" + docName + ".pdf");
                String title = metadata.getTitle();
                if (title == null) {
                    title = "";
                }
                docRes.addProperty(DC.title, title);

                int affId = 0;
                for (DocumentAuthor docAuthor : metadata.getAuthors()) {
                    for (DocumentAffiliation docAffiliation : docAuthor.getAffiliations()) {
                        String affURI = prefix + "affiliation/" + volDoc + "_" + affId;
                        String autURI = prefix + "author/" + docAuthor.getName().replaceAll("\\s", "-");
                        String countryName = docAffiliation.getCountry();
                        if (countryName == null) {
                            countryName = "";
                        }
                        String countryURI = prefix + "country/" + countryName.replaceAll("\\s", "-");
                        String org = docAffiliation.getOrganization();
                        if (org == null) {
                            org = "";
                        }
                        Resource affiliation = model.createResource(affURI);
                        affiliation.addProperty(VCARD.Orgname, org);
                        Resource author = model.createResource(autURI);
                        author.addProperty(VCARD.FN, docAuthor.getName());
                        affiliation.addProperty(DC.contributor, author);
                        if (!countryName.isEmpty()) {
                            Resource country = model.createResource(countryURI);
                            country.addProperty(VCARD.NAME, countryName);
                            affiliation.addProperty(VCARD.Country, country);
                        }

                        docRes.addProperty(DCTerms.creator, affiliation);
                        affId++;
                    }
                }

                int citId = 0;
                for (BibEntry reference : references) {
                    String citURI = prefix + "citation/" + volDoc + "_" + citId;
                    title = reference.getFirstFieldValue(BibEntry.FIELD_TITLE);
                    if (title == null) {
                        title = "";
                    }
                    String year = reference.getFirstFieldValue(BibEntry.FIELD_YEAR);
                    if (year == null) {
                        year = "";
                    }
                    String doi = reference.getFirstFieldValue(BibEntry.FIELD_DOI);
                    if (doi == null) {
                        doi = "";
                    }
                    String journal = reference.getFirstFieldValue(BibEntry.FIELD_JOURNAL);
                    if (journal == null) {
                        journal = "";
                    }
                    Resource citation = model.createResource(citURI)
                                .addProperty(DC.title, title)
                                .addProperty(DC.source, journal)
                                .addProperty(DC.type, reference.getType())
                                .addProperty(DC.date, year)
                                .addProperty(DC.identifier, doi);
                    docRes.addProperty(DCTerms.bibliographicCitation, citation);
                    citId++;
                }

                long end = System.currentTimeMillis();
                elapsed = (end - start) / 1000F;
            
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

        String output = parser.getOutput();
        if (output == null) {
            model.write(System.out);
        } else {
            FileOutputStream fos = new FileOutputStream(output);
            model.write(fos);
            fos.close();
        }
 
    }

}
