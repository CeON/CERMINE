package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import pl.edu.icm.yadda.analysis.bibref.BibEntry;
import pl.edu.icm.yadda.analysis.bibref.BibReferenceParser;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YRelation;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Bibliographic references parser node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibliographicReferencesParserNode
        implements IProcessingNode<EnrichedPayload<YElement>, EnrichedPayload<YElement>> {

    private BibReferenceParser<BibEntry> bibReferenceParser;

    @Override
    public EnrichedPayload<YElement> process(EnrichedPayload<YElement> input, ProcessContext ctx) throws Exception {
        for (YRelation relation : input.getObject().getRelations()) {
            String text = relation.getOneAttributeSimpleValue(YConstants.AT_REFERENCE_TEXT);
            BibEntry parsed = bibReferenceParser.parseBibReference(text);

            for (String author : parsed.getAllFieldValues(BibEntry.FIELD_AUTHOR)) {
                YAttribute ySurname = new YAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME, author);
                YAttribute yForenames = new YAttribute(YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES, author);
                               
                YAttribute yAuthor = new YAttribute();
                yAuthor.setKey(YConstants.AT_REFERENCE_PARSED_AUTHOR);
                
                yAuthor.addAttribute(ySurname);
                yAuthor.addAttribute(yForenames);
                
                relation.addAttribute(yAuthor);
            }

            for (String title : parsed.getAllFieldValues(BibEntry.FIELD_TITLE)) {
                YAttribute yTitle = new YAttribute(YConstants.AT_REFERENCE_PARSED_TITLE, title);
                relation.addAttribute(yTitle);
            }

            for (String year : parsed.getAllFieldValues(BibEntry.FIELD_YEAR)) {
                YAttribute yYear = new YAttribute(YConstants.AT_REFERENCE_PARSED_YEAR, year);
                relation.addAttribute(yYear);
            }

            for (String journal : parsed.getAllFieldValues(BibEntry.FIELD_JOURNAL)) {
                YAttribute yJournal = new YAttribute(YConstants.AT_REFERENCE_PARSED_JOURNAL, journal);
                relation.addAttribute(yJournal);
            }

            for (String volume : parsed.getAllFieldValues(BibEntry.FIELD_VOLUME)) {
                YAttribute yVolume = new YAttribute(YConstants.AT_REFERENCE_PARSED_VOLUME, volume);
                relation.addAttribute(yVolume);
            }

            for (String issue : parsed.getAllFieldValues(BibEntry.FIELD_NUMBER)) {
                YAttribute yIssue = new YAttribute(YConstants.AT_REFERENCE_PARSED_ISSUE, issue);
                relation.addAttribute(yIssue);
            }

            for (String publisher : parsed.getAllFieldValues(BibEntry.FIELD_PUBLISHER)) {
                YAttribute yPublisher = new YAttribute(YConstants.AT_REFERENCE_PARSED_PUBLISHER, publisher);
                relation.addAttribute(yPublisher);
            }

            for (String pages : parsed.getAllFieldValues(BibEntry.FIELD_PAGES)) {
                YAttribute yPages = new YAttribute(YConstants.AT_REFERENCE_PARSED_PAGES, pages);
                relation.addAttribute(yPages);
            }

            for (String location : parsed.getAllFieldValues(BibEntry.FIELD_LOCATION)) {
                YAttribute yLocation = new YAttribute(YConstants.AT_REFERENCE_PARSED_CITY, location);
                relation.addAttribute(yLocation);
            }
        }

        return input;
    }

    public void setBibReferenceParser(BibReferenceParser<BibEntry> bibReferenceParser) {
        this.bibReferenceParser = bibReferenceParser;
    }

}
