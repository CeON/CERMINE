package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.bibref.BibEntry;
import pl.edu.icm.yadda.analysis.bibref.BibReferenceParser;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 *
 * @author krusek
 */
public class CiteAsEnhancer extends AbstractFilterEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "Cite this article as: (.*)",
            Pattern.DOTALL);

    private BibReferenceParser<BibEntry> referenceParser;

    public CiteAsEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
    }

    public void setReferenceParser(BibReferenceParser<BibEntry> referenceParser) {
        this.referenceParser = referenceParser;
    }

    @Override
    public void enhanceMetadata(BxDocument document, YElement metadata, Set<EnhancedField> enhancedFields) {
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                Matcher matcher = PATTERN.matcher(zone.toText());
                if (matcher.find()) {
                    BibEntry bibEntry = referenceParser.parseBibReference(matcher.group(1));
                    if (!enhancedFields.contains(EnhancedField.JOURNAL)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL);
                        if (value != null) {
                            Enhancers.addJournal(metadata, value);
                            enhancedFields.add(EnhancedField.JOURNAL);
                        }
                    }
                    if (!enhancedFields.contains(EnhancedField.VOLUME)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME);
                        if (value != null) {
                            Enhancers.addVolume(metadata, value);
                            enhancedFields.add(EnhancedField.VOLUME);
                        }
                    }
                    if (!enhancedFields.contains(EnhancedField.ISSUE)) {
                        String value = bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER);
                        if (value != null) {
                            Enhancers.addIssue(metadata, value);
                            enhancedFields.add(EnhancedField.ISSUE);
                        }
                    }
                }
            }
        }
    }

}
