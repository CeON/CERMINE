package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.BibReferenceParser;
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class CiteAsEnhancer extends AbstractFilterEnhancer {

    private static final String modelFile = "/pl/edu/icm/cermine/bibref/acrf-small.ser.gz";

    private static final Pattern PATTERN = Pattern.compile(
            "Cite this article as: (.*)",
            Pattern.DOTALL);

    private BibReferenceParser<BibEntry> referenceParser;

    public CiteAsEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
        try {
            referenceParser = new CRFBibReferenceParser(this.getClass().getResourceAsStream(modelFile));
        } catch (AnalysisException ex) {
            referenceParser = null;
        }
    }

    public void setReferenceParser(BibReferenceParser<BibEntry> referenceParser) {
        this.referenceParser = referenceParser;
    }

    @Override
    public void enhanceMetadata(BxDocument document, Element metadata, Set<EnhancedField> enhancedFields) {
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
                            Enhancers.setJournal(metadata, value);
                            enhancedFields.add(EnhancedField.JOURNAL);
                        }
                    }
                    if (!enhancedFields.contains(EnhancedField.VOLUME)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME);
                        if (value != null) {
                            Enhancers.setVolume(metadata, value);
                            enhancedFields.add(EnhancedField.VOLUME);
                        }
                    }
                    if (!enhancedFields.contains(EnhancedField.ISSUE)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER);
                        if (value != null) {
                            Enhancers.setIssue(metadata, value);
                            enhancedFields.add(EnhancedField.ISSUE);
                        }
                    }
                }
            }
        }
    }

}
