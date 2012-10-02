package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.bibref.BibReferenceParser;
import pl.edu.icm.coansys.metaextr.bibref.CRFBibReferenceParser;
import pl.edu.icm.coansys.metaextr.bibref.model.BibEntry;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class CiteAsEnhancer extends AbstractFilterEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "Cite this article as: (.*)",
            Pattern.DOTALL);

    private BibReferenceParser<BibEntry> referenceParser = new CRFBibReferenceParser();

    public CiteAsEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
    }

    public void setReferenceParser(BibReferenceParser<BibEntry> referenceParser) {
        this.referenceParser = referenceParser;
    }

    @Override
    public void enhanceMetadata(BxDocument document, Element metadata, Set<EnhancedField> enhancedFields) {
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
