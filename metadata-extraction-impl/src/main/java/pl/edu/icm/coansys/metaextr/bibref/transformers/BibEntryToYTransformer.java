package pl.edu.icm.coansys.metaextr.bibref.transformers;

//package pl.edu.icm.yadda.analysis.bibref;
//
//import java.util.ArrayList;
//import java.util.List;
//import pl.edu.icm.yadda.bwmeta.model.YAffiliation;
//import pl.edu.icm.yadda.bwmeta.model.YAncestor;
//import pl.edu.icm.yadda.bwmeta.model.YConstants;
//import pl.edu.icm.yadda.bwmeta.model.YContributor;
//import pl.edu.icm.yadda.bwmeta.model.YCurrent;
//import pl.edu.icm.yadda.bwmeta.model.YDate;
//import pl.edu.icm.yadda.bwmeta.model.YDescription;
//import pl.edu.icm.yadda.bwmeta.model.YElement;
//import pl.edu.icm.yadda.bwmeta.model.YExportable;
//import pl.edu.icm.yadda.bwmeta.model.YId;
//import pl.edu.icm.yadda.bwmeta.model.YLanguage;
//import pl.edu.icm.yadda.bwmeta.model.YName;
//import pl.edu.icm.yadda.bwmeta.model.YStructure;
//import pl.edu.icm.yadda.bwmeta.model.YTagList;
//import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
//import pl.edu.icm.yadda.metadata.transformers.IMetadataModelConverter;
//import pl.edu.icm.yadda.metadata.transformers.MetadataModel;
//import pl.edu.icm.yadda.metadata.transformers.TransformationException;
//
///**
// * BibEntry model to "Y" model transformer.
// *
// * @autor estocka
// * @author Lukasz Bolikowski (bolo@icm.edu.pl)
// *
// */
//public class BibEntryToYTransformer implements IMetadataModelConverter<BibEntry, YExportable> {
//
//    @Override
//    public MetadataModel<BibEntry> getSourceModel() {
//        return BibRefTransformers.BibEntry;
//    }
//
//    @Override
//    public MetadataModel<YExportable> getTargetModel() {
//        return BwmetaTransformers.Y;
//    }
//
//    @Override
//    public YExportable convert(BibEntry source, Object... hints) throws TransformationException {
//        YElement yElement = new YElement();
//        YStructure yStructure;
//
//        //article specific fields
//        if (source.getType().equals(BibEntry.TYPE_ARTICLE)) {
//            yStructure = new YStructure(YConstants.EXT_HIERARCHY_JOURNAL);
//            yStructure.setCurrent(new YCurrent(YConstants.EXT_LEVEL_JOURNAL_ARTICLE).setPosition(source.getFirstFieldValue(BibEntry.FIELD_PAGES)));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_NUMBER))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_VOLUME))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_JOURNAL))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_YEAR))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))));
//            yElement.addStructure(yStructure);
//        }
//
//        //book specific fields
//        if (source.getType().equals(BibEntry.TYPE_BOOK)) {
//            yStructure = new YStructure(YConstants.EXT_HIERARCHY_BOOK);
//            yStructure.setCurrent(new YCurrent(YConstants.EXT_LEVEL_BOOK_BOOK));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_BOOK_SERIES).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_SERIES))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_BOOK_PUBLISHER).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_JOURNAL))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_YEAR))));
//            yStructure.addAncestor(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER).addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_PUBLISHER))));
//            yElement.addStructure(yStructure);
//        }
//
//        //title
//        yElement.addName(new YName(source.getFirstFieldValue(BibEntry.FIELD_TITLE)));
//
//        //language
//        String sLang = source.getFirstFieldValue(BibEntry.FIELD_LANGUAGE);
//        YLanguage language = YLanguage.byCode(sLang);
//        if (language != null) {
//            yElement.addLanguage(language);
//        }
//        //copyright holder
//        yElement.addAttribute(YConstants.AT_COPYRIGHT_HOLDER, source.getFirstFieldValue(BibEntry.FIELD_COPYRIGHT));
//
//        //contributors
//        convertContributors(source, yElement);
//
//        //affiliations
//        convertAffiliations(source, yElement);
//
//        //description
//        convertDescription(source, yElement);
//
//        //keywords
//        convertKeywords(source, yElement);
//
//        //doi, issn, isbn
//        convertIds(source, yElement);
//
//        //date
//        convertDate(source, yElement);
//
//        return yElement;
//    }
//
//    protected List<YContributor> parseBibEntryPersons(List<String> persons, String role) {
//
//        List<YContributor> yContributorList = new ArrayList<YContributor>();
//
//        for (String person : persons) {
//            YContributor yContributor = new YContributor();
//            yContributor.setPerson(true);
//            yContributor.setRole(role);
//
//            yContributor.addName(new YName().setType(YConstants.NM_CANONICAL).setText(person));
//            String[] split = person.split(", ");
//            if (split.length > 0) {
//                yContributor.addName(new YName().setType(YConstants.NM_SURNAME).setText(split[0]));
//            }
//            if (split.length > 1) {
//                yContributor.addName(new YName().setType(YConstants.NM_FORENAMES).setText(split[1]));
//            }
//            if (split.length > 2) {
//                yContributor.addName(new YName().setType(YConstants.NM_SUFFIX).setText(split[2]));
//            }
//
//            yContributorList.add(yContributor);
//        }
//
//        return yContributorList;
//    }
//
//    public void convertContributors(BibEntry source, YElement yElement) {
//        List<YContributor> yContributors = new ArrayList();
//        //authors
//        List<YContributor> yAuthors = parseBibEntryPersons(source.getAllFieldValues(BibEntry.FIELD_AUTHOR), YConstants.CR_AUTHOR);
//        //editors
//        List<YContributor> yEditors = parseBibEntryPersons(source.getAllFieldValues(BibEntry.FIELD_EDITOR), YConstants.CR_EDITOR);
//        yContributors.addAll(yAuthors);
//        yContributors.addAll(yEditors);
//        //publisher
//
//        if (source.getFirstFieldValue(BibEntry.FIELD_ADDRESS) != null
//                && source.getFirstFieldValue(BibEntry.FIELD_PUBLISHER) != null) {
//            String publisher = source.getFirstFieldValue(BibEntry.FIELD_PUBLISHER);
//            String address = source.getFirstFieldValue(BibEntry.FIELD_ADDRESS);
//
//            YContributor yPublisher = new YContributor();
//            yPublisher.setRole(YConstants.CR_PUBLISHER);
//            yPublisher.addName(new YName().setType(YConstants.NM_CANONICAL).setText(publisher));
//            yPublisher.addAttribute(YConstants.AT_ADDRESS_CITY, address);
//            yContributors.add(yPublisher);
//        }
//
//        yElement.setContributors(yContributors);
//    }
//
//    public void convertDescription(BibEntry source, YElement yElement) {
//        //note
//        String note = source.getFirstFieldValue(BibEntry.FIELD_NOTE);
//        if (note != null) {
//            yElement.addDescription(new YDescription().setType(YConstants.DS_NOTE).setText(note));
//        }
//        //abstract
//        String abstractField = source.getFirstFieldValue(BibEntry.FIELD_ABSTRACT);
//        if (abstractField != null) {
//            yElement.addDescription(new YDescription().setType(YConstants.DS_ABSTRACT).setText(abstractField));
//        }
//    }
//
//    protected void convertKeywords(BibEntry source, YElement yElement) {
//        String bibEntryKeywords = source.getFirstFieldValue(BibEntry.FIELD_KEYWORDS);
//
//
//        if (bibEntryKeywords != null) {
//            YTagList yTagList = new YTagList();
//            String[] split = bibEntryKeywords.split("; ");
//            yTagList.setType(YConstants.TG_KEYWORD);
//            for (String keyword : split) {
//                yTagList.addValue(keyword);
//            }
//            yElement.addTagList(yTagList);
//        }
//    }
//
//    protected void convertIds(BibEntry source, YElement yElement) {
//        String doi = source.getFirstFieldValue(BibEntry.FIELD_DOI);
//        if (doi != null) {
//            yElement.addId(new YId(YConstants.EXT_SCHEME_DOI, doi));
//        }
//
//        String issn = source.getFirstFieldValue(BibEntry.FIELD_ISSN);
//        if (issn != null) {
//            yElement.addId(new YId(YConstants.EXT_SCHEME_ISSN, issn));
//        }
//
//        String isbn = source.getFirstFieldValue(BibEntry.FIELD_ISBN);
//        if (isbn != null) {
//            yElement.addId(new YId(YConstants.EXT_SCHEME_ISBN, isbn));
//        }
//    }
//
//    protected void convertDate(BibEntry source, YElement yElement) {
//        String year = source.getFirstFieldValue(BibEntry.FIELD_YEAR);
//        String month = source.getFirstFieldValue(BibEntry.FIELD_MONTH);
//        YDate yDate = new YDate();
//        yDate.setType(YConstants.DT_PUBLISHED);
//        if (year != null) {
//            yDate.setYear(year);
//        }
//        if (month != null) {
//            yDate.setMonth(month);
//        }
//        if (month != null || year != null) {
//            yElement.addDate(yDate);
//        }
//    }
//
//    protected void convertAffiliations(BibEntry source, YElement yElement) {
//        String affiliations = source.getFirstFieldValue(BibEntry.FIELD_AFFILIATION);
//        if (affiliations != null) {
//            List<YAffiliation> yAffiliationsList = new ArrayList<YAffiliation>();
//            String[] split = affiliations.split("; ");
//            for (String affiliation : split) {
//                yAffiliationsList.add(new YAffiliation().setText(affiliation));
//            }
//            yElement.setAffiliations(yAffiliationsList);
//        }
//    }
//}
