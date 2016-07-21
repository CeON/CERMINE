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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Krzysztof Rusek
 */
public class CiteAsEnhancer extends AbstractFilterEnhancer {

    private static final String MODEL_FILE = "/pl/edu/icm/cermine/bibref/acrf.ser.gz";

    private static final Pattern PATTERN = Pattern.compile(
            "Cite this article as: (.*)",
            Pattern.DOTALL);

    private BibReferenceParser<BibEntry> referenceParser;

    public CiteAsEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
        try {
            referenceParser = new CRFBibReferenceParser(CiteAsEnhancer.class.getResourceAsStream(MODEL_FILE));
        } catch (AnalysisException ex) {
            referenceParser = null;
        }
    }

    public void setReferenceParser(BibReferenceParser<BibEntry> referenceParser) {
        this.referenceParser = referenceParser;
    }

    @Override
    public void enhanceMetadata(BxDocument document, DocumentMetadata metadata, Set<EnhancedField> enhancedFields) {
        if (referenceParser == null) {
            return;
        }
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                Matcher matcher = PATTERN.matcher(zone.toText());
                if (matcher.find()) {
                    BibEntry bibEntry;
                    try {
                        bibEntry = referenceParser.parseBibReference(matcher.group(1));
                    } catch (AnalysisException ex) {
                        return;
                    }
                    if (!enhancedFields.contains(EnhancedField.JOURNAL)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL);
                        if (value != null) {
                            metadata.setJournal(value);
                            enhancedFields.add(EnhancedField.JOURNAL);
                        }
                    }
                    if (!enhancedFields.contains(EnhancedField.VOLUME)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME);
                        if (value != null) {
                            metadata.setVolume(value);
                            enhancedFields.add(EnhancedField.VOLUME);
                        }
                    }
                    if (!enhancedFields.contains(EnhancedField.ISSUE)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER);
                        if (value != null) {
                            metadata.setIssue(value);
                            enhancedFields.add(EnhancedField.ISSUE);
                        }
                    }
                }
            }
        }
    }

}
