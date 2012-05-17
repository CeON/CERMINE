package pl.edu.icm.yadda.analysis.bibref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author estocka
 */
public class BibReferenceGenerator implements IBibReferenceGenerator<BibEntry> {

    protected static final List<String> FORMATS = Arrays.asList(
            BibReferenceFormatConstants.ChicagoNotesAndBibliography, BibReferenceFormatConstants.ChicagoAuthorDate,
            BibReferenceFormatConstants.MLA, BibReferenceFormatConstants.APA, BibReferenceFormatConstants.RIS);

    @Override
    public List<String> getFormats() {
        return FORMATS;
    }

    @Override
    public String toBibReference(BibEntry metadata, String format, Object... options) {
        String bibReference = "";
        if (format.equals(BibReferenceFormatConstants.ChicagoNotesAndBibliography)) {
            bibReference = toChicagoNotesAndBibliography((BibEntry) metadata);
        }
        if (format.equals(BibReferenceFormatConstants.ChicagoAuthorDate)) {
            bibReference = toChicagoAuthorDate((BibEntry) metadata);
        }
        if (format.equals(BibReferenceFormatConstants.MLA)) {
            bibReference = toMLA((BibEntry) metadata);
        }
        if (format.equals(BibReferenceFormatConstants.APA)) {
            bibReference = toAPA((BibEntry) metadata);
        }
        if (format.equals(BibReferenceFormatConstants.RIS)) {
            bibReference = toRIS((BibEntry) metadata);
        }
        return bibReference;
    }

    /**
     * returns reference in Chicago Manual of Style (16th edition)- Notes and
     * Bibliography version Processes the following types of BibEntry:
     * BibEntry.TYPE_ARTICLE: processed fields: author, title, journal, volume,
     * year, pages BibEntry.TYPE_BOOK: processed fields: author, title, editor,
     * address, publisher, year BibEntry.TYPE_INPROCEEDINGS: processed fields:
     * author, title, booktitle, address, publisher, year, pages
     */
    protected String toChicagoNotesAndBibliography(BibEntry bibEntry) {
        List<String> authors = bibEntry.getAllFieldValues(BibEntry.FIELD_AUTHOR);
        StringBuilder sb = new StringBuilder();
        ArrayList<Person> parsedAuthors = new ArrayList<Person>();
        for (String author : authors) {
            parsedAuthors.add(parsePerson(author));
        }

        if (bibEntry.getType().equals(BibEntry.TYPE_ARTICLE)) {
            sb.append(parseToChicagoAuthors(parsedAuthors));
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append("\"");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".\"");
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL));
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME));
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
                sb.append(" (");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
                sb.append(")");
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))
                        || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))) {
                    sb.append(":");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));
            }
            if ((!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))
                    || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME)))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_YEAR)))) {
                sb.append(".");

            }
        }
        if (bibEntry.getType().equals(BibEntry.TYPE_BOOK)) {
            sb.append(parseToChicagoAuthors(parsedAuthors));
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(". ");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_EDITOR))) {
                ArrayList<Person> parsedEditors = new ArrayList<Person>();
                List<String> editors = bibEntry.getAllFieldValues(BibEntry.FIELD_EDITOR);
                for (String editor : editors) {
                    parsedEditors.add(parsePerson(editor));
                }
                sb.append("Edited by ");
                sb.append(parseToChicagoEditors(parsedEditors));
                sb.append(". ");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
                sb.append(", ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_YEAR)))) {
                sb.append(".");
            }
        }
        if (bibEntry.getType().equals(BibEntry.TYPE_INPROCEEDINGS)) {

            sb.append(parseToChicagoAuthors(parsedAuthors));
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append("\"");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".\"");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))) {
                sb.append(" In ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE));

            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))
                    || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {

                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                        || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_YEAR)))) {
                sb.append(".");
            }

        }
        return sb.toString();
    }

    /**
     * returns reference in Chicago Manual of Style (16th edition)- Author-Date
     * version Processes the following types of BibEntry: BibEntry.TYPE_ARTICLE:
     * processed fields: author, title, journal, volume, year, pages
     * BibEntry.TYPE_BOOK: processed fields: author, title, editor, address,
     * publisher, year BibEntry.TYPE_INPROCEEDINGS: processed fields: author,
     * title, booktitle, address, publisher, year, pages
     */
    protected String toChicagoAuthorDate(BibEntry bibEntry) {

        StringBuilder sb = new StringBuilder();
        List<String> authors = bibEntry.getAllFieldValues(BibEntry.FIELD_AUTHOR);
        ArrayList<Person> parsedAuthors = new ArrayList<Person>();
        for (String author : authors) {
            parsedAuthors.add(parsePerson(author));
        }
        sb.append(parseToChicagoAuthors(parsedAuthors));
        if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
            sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
            sb.append(". ");
        }
        if (bibEntry.getType().equals(BibEntry.TYPE_ARTICLE)) {
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append("\"");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".\"");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))) {
                    sb.append(":");
                } else {
                    sb.append(" ");
                }
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_PAGES)))) {
                sb.append(".");
            }
        }

        if (bibEntry.getType().equals(BibEntry.TYPE_BOOK)) {

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(". ");
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_EDITOR))) {
                ArrayList<Person> parsedEditors = new ArrayList<Person>();
                List<String> editors = bibEntry.getAllFieldValues(BibEntry.FIELD_EDITOR);
                for (String editor : editors) {
                    parsedEditors.add(parsePerson(editor));
                }
                sb.append("Edited by ");
                sb.append(parseToChicagoEditors(parsedEditors));
                sb.append(". ");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER)))) {
                sb.append(".");
            }
        }
        if (bibEntry.getType().equals(BibEntry.TYPE_INPROCEEDINGS)) {
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append("\"");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".\"");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))) {
                sb.append(" In ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE));

            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))
                    || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {

                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * returns reference in MLA style (7th Edition) Processes the following
     * types of BibEntry: BibEntry.TYPE_ARTICLE: processed fields: author,
     * title, journal, volume, number, year, pages BibEntry.TYPE_BOOK: processed
     * fields: author, title, edition, address, publisher, year
     * BibEntry.TYPE_INPROCEEDINGS: processed fields: author, title, booktitle,
     * editor, address, publisher, year, pages
     */
    protected String toMLA(BibEntry bibEntry) {
        List<String> authors = bibEntry.getAllFieldValues(BibEntry.FIELD_AUTHOR);
        StringBuilder sb = new StringBuilder();
        ArrayList<Person> parsedAuthors = new ArrayList<Person>();
        for (String author : authors) {
            parsedAuthors.add(parsePerson(author));
        }
        sb.append(parseToChicagoAuthors(parsedAuthors));
        if (bibEntry.getType().equals(BibEntry.TYPE_ARTICLE)) {
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append("\"");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".\"");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER))) {
                    sb.append(".");
                    sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER));
                }
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
                sb.append(" (");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
                sb.append(")");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))
                        || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))) {
                    sb.append(":");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));
            }
            if ((!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL)) || !nullOrEmpty(bibEntry
                    .getFirstFieldValue(BibEntry.FIELD_VOLUME)))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_YEAR)))) {
                sb.append(".");
            }
            sb.append(" Print.");
        }
        if (bibEntry.getType().equals(BibEntry.TYPE_BOOK)) {

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(". ");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_EDITION))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_EDITION));
                sb.append(" ed. ");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
                sb.append(", ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_YEAR)))) {
                sb.append(".");
            }
            sb.append(" Print.");
        }

        if (bibEntry.getType().equals(BibEntry.TYPE_INPROCEEDINGS)) {
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append("\"");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".\"");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE));
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_EDITOR))) {
                ArrayList<Person> parsedEditors = new ArrayList<Person>();
                List<String> editors = bibEntry.getAllFieldValues(BibEntry.FIELD_EDITOR);
                for (String editor : editors) {
                    parsedEditors.add(parsePerson(editor));
                }
                sb.append(" Ed. ");
                sb.append(parseToChicagoEditors(parsedEditors));
                sb.append(".");
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {

                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                        || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_YEAR)))) {
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {

                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));
                sb.append(".");
            }
            sb.append(" Print.");
        }
        return sb.toString();
    }

    /**
     * returns reference in APA style Processes the following types of BibEntry:
     * BibEntry.TYPE_ARTICLE: processed fields: author, title, journal, volume,
     * number, year, pages BibEntry.TYPE_BOOK: processed fields: author, title,
     * edition, address, publisher, year BibEntry.TYPE_INPROCEEDINGS: processed
     * fields: author, title, booktitle, address, publisher, year, pages
     */
    protected String toAPA(BibEntry bibEntry) {
        StringBuilder sb = new StringBuilder();
        List<String> authors = bibEntry.getAllFieldValues(BibEntry.FIELD_AUTHOR);
        ArrayList<Person> parsedAuthors = new ArrayList<Person>();
        for (String author : authors) {
            parsedAuthors.add(parsePerson(author));
        }
        sb.append(parseToApaAuthors(parsedAuthors));
        if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR))) {
            sb.append("(");
            sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR));
            sb.append("). ");
        }

        if (bibEntry.getType().equals(BibEntry.TYPE_ARTICLE)) {
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER))) {
                    sb.append("(");
                    sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER));
                    sb.append(")");
                }
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))
                        || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL))) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));

            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME))
                    || (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES)) || !nullOrEmpty(bibEntry
                            .getFirstFieldValue(BibEntry.FIELD_JOURNAL)))) {
                sb.append(".");
            }
        }
        if (bibEntry.getType().equals(BibEntry.TYPE_BOOK)) {
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));

            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_EDITION))) {
                sb.append(" (");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_EDITION));
                sb.append(" ed.)");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {

                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                sb.append(".");
            }
        }
        if (bibEntry.getType().equals(BibEntry.TYPE_INPROCEEDINGS)) {

            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE))) {
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE));
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))) {
                sb.append(" In ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE));

            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                sb.append(" (p. ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES));
                sb.append(")");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE))
                    || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES))) {
                sb.append(".");
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))) {
                sb.append(" ");
                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
                if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                    sb.append(": ");
                }
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {

                sb.append(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
            }
            if (!nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS))
                    || !nullOrEmpty(bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))) {
                sb.append(".");
            }
        }

        return sb.toString();
    }

    /**
     * returns reference in RIS format Processes the following fields of
     * BibEntry: type(article, book, inproceedings, title, booktitle, authors
     * year, month, note, keywords, pages, journal, volume, number, address,
     * publisher, series, abstract, issn, isbn)
     */

    protected String toRIS(BibEntry bibEntry) {
        StringBuilder sb = new StringBuilder();
        sb.append("TY  - ");
        String type = bibEntry.getType();

        if (type.equals(BibEntry.TYPE_ARTICLE)) {
            sb.append("JOUR\n");
        } else if (type.equals(BibEntry.TYPE_BOOK)) {
            sb.append("BOOK\n");
        } else if (type.equals(BibEntry.TYPE_INPROCEEDINGS)) {
            sb.append("CONF\n");
        }

        String title = bibEntry.getFirstFieldValue(BibEntry.FIELD_TITLE);
        if (title != null) {
            sb.append("T1  - ");
            sb.append(title);
            sb.append("\n");
        }
        String booktitle = bibEntry.getFirstFieldValue(BibEntry.FIELD_BOOKTITLE);

        if (booktitle != null) {
            sb.append("TI  - ");
            sb.append(booktitle);
            sb.append("\n");
        }
        List<String> authors = bibEntry.getAllFieldValues(BibEntry.FIELD_AUTHOR);
        for (String author: authors) {
            sb.append("AU  - ");
            sb.append(author);
            sb.append("\n");
        }
        String year = bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR);
        String month = bibEntry.getFirstFieldValue(BibEntry.FIELD_MONTH);
        if (year != null) {
            sb.append("Y1  - ");
            sb.append(year);
            if (month != null) {
                sb.append("/" + month);
            }
            sb.append("\n");
        }
        String note = bibEntry.getFirstFieldValue(BibEntry.FIELD_NOTE);
        if (note != null) {
            sb.append("N1  - ");
            sb.append(note);
            sb.append("\n");
        }
        String keywords = bibEntry.getFirstFieldValue(BibEntry.FIELD_KEYWORDS);
        if (keywords != null) {
            String[] split = keywords.split("; ");
            for (String keyword : split) {
                sb.append("KW  - ");
                sb.append(keyword);
                sb.append("\n");
            }
        }
        String pages = bibEntry.getFirstFieldValue(BibEntry.FIELD_PAGES);
        if (pages != null) {
            String[] split = pages.split(" - ");
            if (split.length == 2) {
                sb.append("SP  - ");
                sb.append(split[0]);
                sb.append("\n");
                sb.append("EP  - ");
                sb.append(split[1]);
                sb.append("\n");
            }
        }
        String journal = bibEntry.getFirstFieldValue(BibEntry.FIELD_JOURNAL);
        if (journal != null) {
            sb.append("JF  - ");
            sb.append(journal);
            sb.append("\n");
        }
        String volume = bibEntry.getFirstFieldValue(BibEntry.FIELD_VOLUME);
        if (volume != null) {
            sb.append("VL  - ");
            sb.append(volume);
            sb.append("\n");
        }
        String number = bibEntry.getFirstFieldValue(BibEntry.FIELD_NUMBER);
        if (number != null) {
            sb.append("IS  - ");
            sb.append(number);
            sb.append("\n");
        }
        String address = bibEntry.getFirstFieldValue(BibEntry.FIELD_ADDRESS);
        if (address != null) {
            sb.append("CY  - ");
            sb.append(address);
            sb.append("\n");
        }

        String publisher = bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER);
        if (publisher != null) {
            sb.append("PB  - ");
            sb.append(publisher);
            sb.append("\n");
        }

        String series = bibEntry.getFirstFieldValue(BibEntry.FIELD_SERIES);
        if (series != null) {
            sb.append("SN  - ");
            sb.append(series);
            sb.append("\n");
        }

        String abstractString = bibEntry.getFirstFieldValue(BibEntry.FIELD_ABSTRACT);
        if (abstractString != null) {
            sb.append("N2  - ");
            sb.append(abstractString);
            sb.append("\n");
        }
        String issn = bibEntry.getFirstFieldValue(BibEntry.FIELD_ISSN);
        String isbn = bibEntry.getFirstFieldValue(BibEntry.FIELD_ISBN);

        if (issn != null) {
            sb.append("SN  - ");
            sb.append("ISSN " + issn);
            sb.append("\n");
        }

        if (isbn != null) {
            sb.append("SN  - ");
            sb.append("ISBN " + isbn);
            sb.append("\n");
        }

        sb.append("ER  -");
        return sb.toString();
    }

    protected class Person {

        String[] forenames;
        String surname;
        String pedigree;

        protected Person(String[] forenames, String surname, String pedigree) {
            this.forenames = forenames;
            this.pedigree = pedigree;
            this.surname = surname;
        }
    }

    protected Person parsePerson(String person) {
        Person parsedAuthor;

        String[] parts = person.split(", ");
        if (parts.length == 2) {
            parsedAuthor = new Person(seperateForenames(parts[1]), parts[0], "");
        } else if (parts.length == 3) {
            parsedAuthor = new Person(seperateForenames(parts[2]), parts[0], parts[1]);
        } else {
            String[] empty = new String[1];
            empty[0] = "";
            parsedAuthor = new Person(empty, person, "");
        }
        return parsedAuthor;
    }

    protected String[] seperateForenames(String forenames) {
        String[] names;

        names = forenames.split(" ");

        return names;
    }

    protected String concatenateForenames(String[] forenames) {
        String concatName = "";
        for (String forename : forenames) {
            concatName = concatName + forename + " ";
        }
        concatName = concatName.substring(0, concatName.length() - 1);
        return concatName;
    }

    protected String fornamesInitials(String[] forenames) {
        String initials = "";
        for (String forename : forenames) {
            initials = initials + " " + forename.charAt(0) + ".";
        }
        return initials;
    }

    protected String parseToChicagoEditors(List<Person> editors) {
        StringBuilder sb = new StringBuilder();
        if (editors.size() == 1) {
            sb.append(concatenateForenames(editors.get(0).forenames));
            sb.append(" ");
            sb.append(editors.get(0).surname);
            if (!editors.get(0).pedigree.isEmpty()) {
                sb.append(" ");
                sb.append(editors.get(0).pedigree);
            }

        } else if (editors.size() > 1) {
            for (int i = 0; i < editors.size() - 1; i++) {
                sb.append(concatenateForenames(editors.get(i).forenames));
                sb.append(" ");
                sb.append(editors.get(i).surname);
                if (!editors.get(i).pedigree.isEmpty()) {
                    sb.append(" ");
                    sb.append(editors.get(i).pedigree);

                }
                sb.append(", ");
            }
            Person lastEditor = editors.get(editors.size() - 1);
            sb.append("and ");
            sb.append(concatenateForenames(lastEditor.forenames));
            sb.append(" ");
            sb.append(lastEditor.surname);
            if (!lastEditor.pedigree.isEmpty()) {
                sb.append(" ");
                sb.append(lastEditor.pedigree);
            }

        }
        return sb.toString();
    }

    protected String parseToApaAuthors(List<Person> authors) {
        StringBuilder sb = new StringBuilder();
        // Author, A. A., Author, B. B., & Author, C. C.
        if (authors.size() == 1) {
            Person author = authors.get(0);

            sb.append(author.surname);
            sb.append(",");
            sb.append(fornamesInitials(author.forenames));
            if (!author.pedigree.isEmpty()) {
                sb.append(", ");
                sb.append(author.pedigree);
            }
            if (sb.charAt(sb.length() - 1) != '.') {
                sb.append(".");
            }
            sb.append(" ");
        } else if (authors.size() > 1) {
            for (int i = 0; i < authors.size() - 1; i++) {
                sb.append(authors.get(i).surname);
                sb.append(",");
                sb.append(fornamesInitials(authors.get(i).forenames));
                if (!authors.get(i).pedigree.isEmpty()) {
                    sb.append(", ");
                    sb.append(authors.get(i).pedigree);
                    sb.append(", ");
                }

            }
            Person lastAuthor = authors.get(authors.size() - 1);
            sb.append(", & ");
            sb.append(lastAuthor.surname);
            sb.append(",");
            sb.append(fornamesInitials(lastAuthor.forenames));
            if (!lastAuthor.pedigree.isEmpty()) {
                sb.append(", ");
                sb.append(lastAuthor.pedigree);
            }
            if (sb.charAt(sb.length() - 1) != '.') {
                sb.append(".");

                sb.append(" ");
            }

        }

        return sb.toString();
    }

    protected String parseToChicagoAuthors(List<Person> authors) {
        StringBuilder sb = new StringBuilder();
        if (authors.size() == 1) {
            Person author = authors.get(0);

            sb.append(author.surname);
            sb.append(", ");
            sb.append(concatenateForenames(author.forenames));
            if (!author.pedigree.isEmpty()) {
                sb.append(", ");
                sb.append(author.pedigree);
            }
            if (sb.charAt(sb.length() - 1) != '.') {
                sb.append(".");
            }
            sb.append(" ");

        } else if (authors.size() > 1) {
            Person firstAuthor = authors.get(0);
            sb.append(firstAuthor.surname);
            sb.append(", ");
            sb.append(concatenateForenames(firstAuthor.forenames));
            sb.append(", ");
            if (!firstAuthor.pedigree.isEmpty()) {
                sb.append(firstAuthor.pedigree);
                sb.append(", ");
            }
            for (int i = 1; i < authors.size() - 1; i++) {
                sb.append(concatenateForenames(authors.get(i).forenames));
                sb.append(" ");
                sb.append(authors.get(i).surname);
                if (!authors.get(i).pedigree.isEmpty()) {
                    sb.append(" ");
                    sb.append(authors.get(i).pedigree);

                }
                sb.append(", ");
            }
            Person lastAuthor = authors.get(authors.size() - 1);
            sb.append("and ");
            sb.append(concatenateForenames(lastAuthor.forenames));
            sb.append(" ");
            sb.append(lastAuthor.surname);
            if (!lastAuthor.pedigree.isEmpty()) {
                sb.append(" ");
                sb.append(lastAuthor.pedigree);
            }
            sb.append(". ");
        }
        return sb.toString();
    }

    protected boolean nullOrEmpty(String field) {
        boolean isNullOrEmpty = false;
        if (field == null) {
            isNullOrEmpty = true;
        } else if (field != null) {
            if (field.isEmpty()) {
                isNullOrEmpty = true;
            }
        }
        return isNullOrEmpty;
    }
}
