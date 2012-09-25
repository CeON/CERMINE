package pl.edu.icm.coansys.metaextr.bibref;

//package pl.edu.icm.yadda.analysis.bibref;
//
//import java.util.ArrayList;
//import java.util.List;
//import pl.edu.icm.yadda.bwmeta.model.YAffiliation;
//import pl.edu.icm.yadda.bwmeta.model.YConstants;
//import pl.edu.icm.yadda.bwmeta.model.YContributor;
//import pl.edu.icm.yadda.bwmeta.model.YDate;
//import pl.edu.icm.yadda.bwmeta.model.YDescription;
//import pl.edu.icm.yadda.bwmeta.model.YElement;
//import pl.edu.icm.yadda.bwmeta.model.YExportable;
//import pl.edu.icm.yadda.bwmeta.model.YName;
//import pl.edu.icm.yadda.bwmeta.model.YStructure;
//import pl.edu.icm.yadda.bwmeta.model.YTagList;
//import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
//import pl.edu.icm.yadda.metadata.transformers.IMetadataModelConverter;
//import pl.edu.icm.yadda.metadata.transformers.MetadataModel;
//import pl.edu.icm.yadda.metadata.transformers.TransformationException;
//
///**
// * "Y" model to BibEntry model transformer.
// *
// * @author estocka
// * @author Lukasz Bolikowski (bolo@icm.edu.pl)
// *
// */
//public class YToBibEntryTransformer implements IMetadataModelConverter<YExportable, BibEntry> {
//
//    @Override
//    public MetadataModel<YExportable> getSourceModel() {
//        return BwmetaTransformers.Y;
//    }
//
//    @Override
//    public MetadataModel<BibEntry> getTargetModel() {
//        return BibRefTransformers.BibEntry;
//    }
//
//    @Override
//    public BibEntry convert(YExportable source, Object... hints) throws TransformationException {
//
//        BibEntry bibEntry = new BibEntry();
//
//        if (source instanceof YElement) {
//
//            YElement yElement = (YElement) source;
//
//            //article
//            YStructure yElementJournalStructure = yElement.getStructure(YConstants.EXT_HIERARCHY_JOURNAL);
//
//
//            if (yElementJournalStructure != null) {
//                if (yElementJournalStructure.getCurrent().getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_ARTICLE)) {
//                    bibEntry.setType(BibEntry.TYPE_ARTICLE);
//
//                   
//                    String pages = yElementJournalStructure.getCurrent().getPosition();
//                    bibEntry.setField(BibEntry.FIELD_PAGES, pages);
//
//                    if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER)!=null){
//                        if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER).getOneName()!=null){
//                            String publisher = yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER).getOneName().getText();
//                            bibEntry.setField(BibEntry.FIELD_PUBLISHER, publisher);
//                        }
//                    }
//                    if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL)!=null){
//                        if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).getOneName()!=null){
//                            String journal = yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).getOneName().getText();
//                            bibEntry.setField(BibEntry.FIELD_JOURNAL, journal);
//                        }
//                    }
//                    if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME)!=null){
//                        if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME).getOneName()!=null){
//                            String volume = yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME).getOneName().getText();
//                            bibEntry.setField(BibEntry.FIELD_VOLUME, volume);
//                        }
//                    }
//                    if (yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE) != null){
//                        if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE).getOneName()!=null){
//                            String number = yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE).getOneName().getText();
//                            bibEntry.setField(BibEntry.FIELD_NUMBER, number);
//                        }
//                    }
//                    if (yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR) != null){
//                        if(yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).getOneName()!=null){
//                            String year = yElementJournalStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).getOneName().getText();
//                            bibEntry.setField(BibEntry.FIELD_YEAR, year);
//                        }
//                    }
//                }
//            }
//            //book
//            YStructure yElementBookStructure = yElement.getStructure(YConstants.EXT_HIERARCHY_BOOK);
//
//            if (yElementBookStructure != null) {
//                if (yElementBookStructure.getCurrent().getLevel().equals(YConstants.EXT_LEVEL_BOOK_BOOK)) {
//                    bibEntry.setType(BibEntry.TYPE_BOOK);
//
//                    if(yElementBookStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_PUBLISHER).getOneName()!=null){
//                    String publisher = yElementBookStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_PUBLISHER).getOneName().getText();
//                    bibEntry.setField(BibEntry.FIELD_PUBLISHER, publisher);
//                    }
//                    if(yElementBookStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_SERIES).getOneName()!=null){
//                    String series = yElementBookStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_SERIES).getOneName().getText();
//                    bibEntry.setField(BibEntry.FIELD_SERIES, series);
//                    }
//                }
//
//            }
//            //title
//            if(yElement.getOneName()!=null){
//            String name = yElement.getOneName().getText();
//            bibEntry.setField(BibEntry.FIELD_TITLE, name);
//            }
//            //language
//            if(yElement.getOneLanguage()!=null){
//            bibEntry.setField(BibEntry.FIELD_LANGUAGE, yElement.getOneLanguage().getBibliographicCode());
//            }
//
//            //copyright holder
//            String copyrightHolder = yElement.getOneAttributeSimpleValue(YConstants.AT_COPYRIGHT_HOLDER);
//            bibEntry.setField(BibEntry.FIELD_COPYRIGHT, copyrightHolder);
//
//
//            //contributors
//
//            convertContributors(yElement, bibEntry);
//
//            //affiliations
//            convertAffiliations(yElement, bibEntry);
//
//            //descriptions
//
//            convertDescription(yElement, bibEntry);
//
//            //keywords
//
//            convertKeywords(yElement, bibEntry);
//
//            //doi, issn, isbn
//
//            convertIds(yElement, bibEntry);
//
//            //date
//
//            convertDate(yElement, bibEntry);
//
//
//
//        } else {
//            throw new UnsupportedOperationException("Not implemented");
//        }
//        return bibEntry;
//    }
//
//    protected List<String> bibEntryPerson(List<YContributor> contributorsList) {
////        StringBuilder sb = new StringBuilder();
////        int i = 0;
//        List<String> fullNamesList = new ArrayList<String>();
//        for (YContributor yContributor : contributorsList) {
//            StringBuilder fullName = new StringBuilder();
//            boolean isForename = false;
//            boolean isSurname = true;
//            //format: surname, name name and surname...
//
//            if (yContributor.getOneName(YConstants.NM_SURNAME) != null) {
//                isSurname = true;
//                fullName.append(yContributor.getOneName(YConstants.NM_SURNAME).getText());
//            }
//            //forename
//
//            for (YName yName : yContributor.getNames()) {
//                if (yName.getType().equals(YConstants.NM_FORENAME)) {
//
//
//                    if ((isSurname && !isForename)) {
//                        fullName.append(",");
//                    }
//                    isForename = true;
//                    fullName.append(" ");
//                    fullName.append(yName.getText());
//                }
//            }
//            //forenames
//            if (!isForename) {
//                if (yContributor.getOneName(YConstants.NM_FORENAMES) != null) {
//                    isForename = true;
//                    String forenames = yContributor.getOneName(YConstants.NM_FORENAMES).getText();
//                    String[] forenameList = forenames.split(" ");
//                    if (forenameList.length > 0) {
//                        if (isSurname && isForename) {
//                            fullName.append(",");
//                        }
//                    }
//                    for (String forename : forenameList) {
//                        fullName.append(" ");
//                        fullName.append(forename);
//                    }
//                }
//            }
//            //suffix
//            if (yContributor.getOneName(YConstants.NM_SUFFIX) != null) {
//                if (isSurname || isForename) {
//                    fullName.append(", ");
//                }
//                String suffix = yContributor.getOneName(YConstants.NM_SUFFIX).getText();
//                fullName.append(suffix);
//            }
//
//
//            fullNamesList.add(fullName.toString());
//
//        }
//        /*if (fullNamesList.size() == 1) {
//            sb.append(fullNamesList.get(0));
//        } else if (fullNamesList.size() > 1) {
//            for (int j = 0; j < fullNamesList.size() - 1; j++) {
//                sb.append(fullNamesList.get(j));
//                sb.append(" and ");
//            }
//            sb.append(fullNamesList.get(fullNamesList.size() - 1));
//        }*/
//        return fullNamesList;
//    }
//
//    protected void convertContributors(YElement yElement, BibEntry bibEntry) {
//        List<YContributor> yContributorList = yElement.getContributors();
//        List<YContributor> yAuthorList = new ArrayList<YContributor>();
//        List<YContributor> yEditorList = new ArrayList<YContributor>();
//        List<YContributor> yPublisherList = new ArrayList<YContributor>();
//
//        for (YContributor yContributor : yContributorList) {
//            if (yContributor.getRole().equals(YConstants.CR_AUTHOR)) {
//                yAuthorList.add(yContributor);
//            }
//        }
//        for (String fullName : bibEntryPerson(yAuthorList)) {
//            bibEntry.addField(BibEntry.FIELD_AUTHOR, fullName);
//        }
//
//        for (YContributor yContributor : yContributorList) {
//            if (yContributor.getRole().equals(YConstants.CR_EDITOR)) {
//                yEditorList.add(yContributor);
//            }
//        }
//        for (String fullName : bibEntryPerson(yEditorList)) {
//            bibEntry.addField(BibEntry.FIELD_EDITOR, fullName);
//        }
//
//        for (YContributor yContributor : yContributorList) {
//            if (yContributor.getRole().equals(YConstants.CR_PUBLISHER)) {
//                yPublisherList.add(yContributor);
//            }
//        }
//        if (!yPublisherList.isEmpty()) {
//            if (bibEntry.getFirstFieldValue(BibEntry.FIELD_PUBLISHER) == null) {
//                bibEntry.setField(BibEntry.FIELD_PUBLISHER, yPublisherList.get(0).getOneName(YConstants.NM_CANONICAL).getText());
//            }
//            if (yPublisherList.get(0).getOneAttribute(YConstants.AT_ADDRESS_CITY) != null) {
//                String address = yPublisherList.get(0).getOneAttribute(YConstants.AT_ADDRESS_CITY).getValue();
//                bibEntry.setField(BibEntry.FIELD_ADDRESS, address);
//            }
//
//        }
//
//    }
//
//    protected void convertDescription(YElement yElement, BibEntry bibEntry) {
//        List<YDescription> descriptions = yElement.getDescriptions();
//        for (YDescription description : descriptions) {
//            if (description.getType().equals(YConstants.DS_ABSTRACT)) {
//                bibEntry.setField(BibEntry.FIELD_ABSTRACT, description.getText());
//            }
//            if (description.getType().equals(YConstants.DS_NOTE)) {
//                bibEntry.setField(BibEntry.FIELD_NOTE, description.getText());
//            }
//        }
//    }
//
//    protected void convertKeywords(YElement yElement, BibEntry bibEntry) {
//        YTagList tagList = yElement.getTagList(YConstants.TG_KEYWORD);
//        List<String> keywordsList = new ArrayList<String>();
//        if (tagList != null) {
//            keywordsList = tagList.getValues();
//
//            StringBuilder bibEntryKeywords = new StringBuilder();
//
//            for (int i = 0; i < keywordsList.size() - 1; i++) {
//                bibEntryKeywords.append(keywordsList.get(i));
//                bibEntryKeywords.append("; ");
//            }
//            bibEntryKeywords.append(keywordsList.get(keywordsList.size() - 1));
//            bibEntry.setField(BibEntry.FIELD_KEYWORDS, bibEntryKeywords.toString());
//        }
//    }
//
//    protected void convertIds(YElement yElement, BibEntry bibEntry) {
//        String doi = yElement.getId(YConstants.EXT_SCHEME_DOI);
//        bibEntry.setField(BibEntry.FIELD_DOI, doi);
//
//        String issn = yElement.getId(YConstants.EXT_SCHEME_ISSN);
//        bibEntry.setField(BibEntry.FIELD_ISSN, issn);
//
//        String isbn = yElement.getId(YConstants.EXT_SCHEME_ISBN);
//        bibEntry.setField(BibEntry.FIELD_ISBN, isbn);
//
//    }
//
//    protected void convertDate(YElement yElement, BibEntry bibEntry) {
//        YDate date = yElement.getDate(YConstants.DT_PUBLISHED);
//        if (date != null) {
//            if (bibEntry.getFirstFieldValue(BibEntry.FIELD_YEAR) == null) {
//                bibEntry.setField(BibEntry.FIELD_YEAR, String.valueOf(date.getYear()));
//            }
//            bibEntry.setField(BibEntry.FIELD_MONTH, String.valueOf(date.getMonth()));
//
//            date.getYear();
//        }
//    }
//
//    protected void convertAffiliations(YElement yElement, BibEntry bibEntry) {
//        List<YAffiliation> affiliations = yElement.getAffiliations();
//        if (affiliations.size() == 1) {
//            bibEntry.setField(BibEntry.FIELD_AFFILIATION, affiliations.get(0).getSimpleText());
//        } else if (affiliations.size() > 1) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(affiliations.get(0).getSimpleText());
//            for (int i = 1; i < affiliations.size(); i++) {
//                sb.append("; ");
//                sb.append(affiliations.get(i).getSimpleText());
//            }
//            bibEntry.setField(BibEntry.FIELD_AFFILIATION, sb.toString());
//        }
//    }
//}
