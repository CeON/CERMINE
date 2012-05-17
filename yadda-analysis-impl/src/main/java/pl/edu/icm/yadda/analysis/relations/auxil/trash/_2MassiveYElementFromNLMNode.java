package pl.edu.icm.yadda.analysis.relations.auxil.trash;

import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_CONTACT_EMAIL;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_AUTHOR;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_BOOKTITLE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_CHAPTER;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_CITY;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_ID_EUDML;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_ID_MR;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_ID_ZBL;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_ISSUE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_JOURNAL;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_MONTH;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_NOTE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_PAGES;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_PUBLISHER;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_TITLE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_TYPE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_VOLUME;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_PARSED_YEAR;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.AT_REFERENCE_TEXT;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.CR_PUBLISHER;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.DS_ABSTRACT;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_HIERARCHY_JOURNAL;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_LEVEL_JOURNAL_ARTICLE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_LEVEL_JOURNAL_ISSUE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_LEVEL_JOURNAL_JOURNAL;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_LEVEL_JOURNAL_PUBLISHER;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_LEVEL_JOURNAL_VOLUME;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_LEVEL_JOURNAL_YEAR;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.EXT_SCHEME_ISSN;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.FT_ELEMENT_WEBPAGE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.FT_FULL_TEXT;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.NM_ABBREVIATION;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.NM_ALTERNATIVE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.NM_FORENAMES;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.NM_SURNAME;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.RF_ARTICLE;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.RF_BOOK;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.RF_INPROCEEDINGS;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.RL_REFERENCE_TO;
import static pl.edu.icm.yadda.bwmeta.model.YConstants.TG_KEYWORD;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.bwmeta.model.YAncestor;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YCurrent;
import pl.edu.icm.yadda.bwmeta.model.YDate;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YId;
import pl.edu.icm.yadda.bwmeta.model.YLanguage;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YRelation;
import pl.edu.icm.yadda.bwmeta.model.YRichText;
import pl.edu.icm.yadda.bwmeta.model.YTagList;
import pl.edu.icm.yadda.bwmeta.serialization.YRTHelper;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.imports.transformers.ImportsTransformers;
import pl.edu.icm.yadda.imports.utils.YModelToolbox;
import pl.edu.icm.yadda.metadata.transformers.MetadataFormat;
import pl.edu.icm.yadda.metadata.transformers.MetadataModel;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * slight change of @author estocka NlmToYTransformer
 * @author pdendek
 */
public class _2MassiveYElementFromNLMNode implements IProcessingNode<File, List<YExportable>> {

    private static final YModelToolbox y = new YModelToolbox();

    static {
        y.setCanonicalNameDefault("[unknown]");
    }
    private static final YElement ROOT = new YElement("EuDML");
    private static final Namespace XLINK_NAMESPACE = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
	
    
    @Override
	public List<YExportable> process(File input, ProcessContext ctx)
			throws Exception {
        try {
            List<YExportable> el = null;
            el = readNlm(new FileReader(input));
            el = modifyElementIds(el);
            return el;
        } catch (Exception ex) {
            Logger log = LoggerFactory.getLogger(_2MassiveYElementFromNLMNode.class);
            log.info(ex.getMessage());
            throw new TransformationException(ex);
        }
	}

    public MetadataFormat getSourceFormat() {
        return ImportsTransformers.NLM;
    }

    public MetadataModel<YExportable> getTargetModel() {
        return BwmetaTransformers.Y;
    }

    /*
     * Reduces Hierarchy to Journal, Volume(?), Issue, Article,
     */
    protected List<YExportable> reduceHierarchy(List<YExportable> el) {
        List<YExportable> newEl = new ArrayList<YExportable>();
        YElement yJournal = null;
        YElement yVolume = null;
        YElement yIssue = null;
        YElement yArticle = null;
        for (YExportable yExportable : el) {
            YElement yElement = (YElement) yExportable;
            YCurrent current = yElement.getStructure(
                    YConstants.EXT_HIERARCHY_JOURNAL).getCurrent();
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_JOURNAL)) {
                yJournal = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_VOLUME)) {
                yVolume = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_ISSUE)) {
                yIssue = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_ISSUE)) {
                yArticle = yElement;
            }
        }

        yJournal.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).setAncestors(
                null);

        List<YAncestor> al = new ArrayList<YAncestor>();
        al.add(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL, yJournal.getId()).addId(new YId(YConstants.EXT_SCHEME_ISSN, yJournal.getId(YConstants.EXT_SCHEME_ISSN))));
        yVolume.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).setAncestors(al);

        al.add(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME, yVolume.getId()));
        yIssue.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).setAncestors(al);

        al.add(new YAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE, yIssue.getId()));
        yArticle.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).setAncestors(al);

        newEl.add(yJournal);
        newEl.add(yVolume);
        newEl.add(yIssue);
        newEl.add(yArticle);

        return newEl;

    }

    /*
     * Changes elements(journal, issue, article) id to eumdl-id if eudml-id is
     * present on ids list.
     */
    protected List<YExportable> modifyElementIds(List<YExportable> el) {
        List<YExportable> newEl = new ArrayList<YExportable>();
        YElement yPublisher = null;
        YElement yJournal = null;
        YElement yYear = null;
        YElement yVolume = null;
        YElement yIssue = null;
        YElement yArticle = null;
        for (YExportable yExportable : el) {
            YElement yElement = (YElement) yExportable;
            YCurrent current = yElement.getStructure(
                    YConstants.EXT_HIERARCHY_JOURNAL).getCurrent();
            if (current.getLevel().equals(
                    YConstants.EXT_LEVEL_JOURNAL_PUBLISHER)) {
                yPublisher = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_JOURNAL)) {
                yJournal = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_YEAR)) {
                yYear = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_VOLUME)) {
                yVolume = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_ISSUE)) {
                yIssue = yElement;
            }
            if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_ARTICLE)) {
                yArticle = yElement;
            }
        }

        // FIXME things below concerning Volume and Year are here only temporary
        // because of urgent need for EUDML Madrid meeting. Should be
        // corrected as soon as possible.
        String id = yJournal.getId(YConstants.EXT_SCHEMA_EUDML);
        if (id != null) {
            yJournal.setId(id);
        }

        yYear.setId(yJournal.getId() + "_"
                + normalizeIssue(yYear.getOneName().getText()));
        yYear.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).setIdentity(yJournal.getId()).addId(new YId(YConstants.EXT_SCHEME_ISSN, yJournal.getId(YConstants.EXT_SCHEME_ISSN)));

        yVolume.setId(yYear.getId() + "_"
                + normalizeIssue(yVolume.getOneName().getText()));
        yVolume.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).setIdentity(yJournal.getId()).addId(new YId(YConstants.EXT_SCHEME_ISSN, yJournal.getId(YConstants.EXT_SCHEME_ISSN)));
        yVolume.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).setIdentity(yYear.getId());

        yIssue.setId(yVolume.getId() + "_"
                + normalizeIssue(yIssue.getOneName().getText()));
        yIssue.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).setIdentity(yJournal.getId()).addId(new YId(YConstants.EXT_SCHEME_ISSN, yJournal.getId(YConstants.EXT_SCHEME_ISSN)));
        yIssue.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).setIdentity(yYear.getId());
        yIssue.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME).setIdentity(yVolume.getId());

        id = yArticle.getId(YConstants.EXT_SCHEMA_EUDML);
        if (id != null) {
            yArticle.setId(id);
        }
        yArticle.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).setIdentity(yJournal.getId()).addId(new YId(YConstants.EXT_SCHEME_ISSN, yJournal.getId(YConstants.EXT_SCHEME_ISSN)));
        yArticle.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).setIdentity(yYear.getId());
        yArticle.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME).setIdentity(yVolume.getId());
        yArticle.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE).setIdentity(yIssue.getId());

        newEl.add(yPublisher);
        newEl.add(yJournal);
        newEl.add(yYear);
        newEl.add(yVolume);
        newEl.add(yIssue);
        newEl.add(yArticle);

        return newEl;
    }

    // FIXME only temporary for normalizing strings for EUDML Madrid meeting
    // purposes. Should be removed when thing mentioned in previous FIXME are
    // corrected.
    public String normalizeIssue(String str) {
        return str.toLowerCase().replaceAll("[^a-z_0-9]", "_").replaceAll("_+", "_").replaceAll("_$", "").replaceAll("^_", "");
    }
    /*
     * Methods copied from NLMMetaReader
     * @author mpol
     * @author estocka: pubdate, ids added, contributors and tags conversion modified
     */

    /**
     * Processes a String containing an XML document using the NLM DTD
     * and creates bwmeta model elements for the articles and their ancestors.
     *
     * @param reader the reader to read the XML from
     * @return a list containing the created model elements
     * @throws JDOMException when XML parsing fails
     * @throws IOException when I/O fails during parsing
     */
    private List<YExportable> readNlm(Reader reader) throws JDOMException, IOException {
        List<YExportable> es = new ArrayList<YExportable>();
        org.jdom.Element a = new SAXBuilder().build(reader).getRootElement();

        es.addAll(processArticle(a));

        return es;
    }

    /**
     * Processes metadata for a single article creating bwmeta model elements.
     *
     * @param a an element containing article metadata
     * @return created model elements
     */
    private List<YExportable> processArticle(org.jdom.Element a) {
        List<YExportable> es = new ArrayList<YExportable>();

        org.jdom.Element jmeta = optDescendant(a, "front", "journal-meta");
        org.jdom.Element ameta = optDescendant(a, "front", "article-meta");

        YElement publisher = processPublisher(jmeta);
        es.add(publisher);

        YElement journal = processJournal(jmeta, publisher);
        es.add(journal);

        YElement year = processYear(ameta, journal);
        es.add(year);

        YElement volume = processVolume(ameta, year);
        es.add(volume);

        YElement issue = processIssue(ameta, volume);
        es.add(issue);

        YElement article = processArticle(ameta, optDescendant(a, "back", "ref-list"), issue);
        es.add(article);

        return es;
    }

    /**
     * Processes <code>article-meta</code> and <code>ref-list</code> metadata creating an article element.
     *
     * @param ameta the metadata element to extract article info from
     * @param reflist the metadata element to extract citations info from
     * @param parent parent element for the created element
     * @return the created article element
     */
    private YElement processArticle(org.jdom.Element ameta, org.jdom.Element reflist, YElement parent) {
        org.jdom.Element atitles = optDescendant(ameta, "title-group");
        org.jdom.Element atitle = optDescendant(atitles, "article-title");
        org.jdom.Element aabstract = ameta.getChild("abstract");

        YElement article = y.element(EXT_LEVEL_JOURNAL_ARTICLE,
                y.canonicalName(getYLang(atitle), textOfElement(atitle)),
                parent).addDescription(y.description(getYLang(aabstract), textOfElement(aabstract), DS_ABSTRACT)).addLanguage(getYLang(ameta));

        List<org.jdom.Element> trs = optChildren(atitles, "trans-title-group");
        for (org.jdom.Element tr : trs) {
            article.addName(y.name(getYLang(tr), textOfElement(tr.getChild("trans-title")), NM_ALTERNATIVE));
        }
        @SuppressWarnings("unchecked")
        List<org.jdom.Element> aids = ameta.getChildren("article-id");
        for (org.jdom.Element aid : aids) {
            if ("eudml-id".equalsIgnoreCase(aid.getAttributeValue("pub-id-type"))) {
                article.addId(new YId(YConstants.EXT_SCHEMA_EUDML, aid.getText()));
            } else {
                article.addId(new YId("bwmeta1.id-class." + aid.getAttributeValue("pub-id-type"), aid.getText()));
            }
        }

        List<org.jdom.Element> extLinks = ameta.getChildren("ext-link");
        for (org.jdom.Element extLink : extLinks) {
            if ("mr-item-id".equals(extLink.getAttributeValue("ext-link-type"))) {
                article.addId(new YId(YConstants.EXT_SCHEMA_MR, extLink.getTextTrim()));
            } else if ("zbl-item-id".equals(extLink.getAttributeValue("ext-link-type"))) {
                article.addId(new YId(YConstants.EXT_SCHEME_ZBL, extLink.getTextTrim()));
            }
        }

        // content links
        @SuppressWarnings("unchecked")
        List<org.jdom.Element> selfs = ameta.getChildren("self-uri");
        for (org.jdom.Element uri : selfs) {
            String type = FT_FULL_TEXT;
            String mime = uri.getAttributeValue("content-type");
            if (mime == null) {
                type = FT_ELEMENT_WEBPAGE;
                mime = "text/html";
            }
            String href = uri.getAttributeValue("href", XLINK_NAMESPACE);
            article.addContent(y.contentFile(href, type, mime, href));
        }

        // contributors

        List<org.jdom.Element> cg = optChildren(ameta, "contrib-group");
        List<org.jdom.Element> cs = new ArrayList<org.jdom.Element>();
        for (org.jdom.Element group : cg) {
            cs.addAll(group.getChildren("contrib"));
        }
        int n = 1;
        for (org.jdom.Element c : cs) {
            String surname = getTextTrim(optDescendant(c, "name", "surname"));
            String forenames = getTextTrim(optDescendant(c, "name", "given-names"));
            String address = getTextTrim(optDescendant(c, "address", "addr-line"));
            String aref = address.isEmpty() ? null : "" + n++;
            String ctValue = c.getAttributeValue("contrib-type");
            if (ctValue != null) {
                ctValue = ctValue.toLowerCase();

                if (ctValue.equals("organizer")) {
                    ctValue = YConstants.CR_ORM;
                }
            }
            String canonicalName = (forenames + " " + surname).trim();
            if (canonicalName.isEmpty()) {
                canonicalName = c.getValue().trim();
            }
            List<String> contributorRoles = YConstants.contributorRoles.getConstants();
            if (contributorRoles.contains(ctValue)) {
                article.addContributor(
                        new YContributor(ctValue, false).addName(y.canonicalName(YLanguage.NoLinguisticContent, canonicalName)).addName(y.name(YLanguage.NoLinguisticContent, surname, NM_SURNAME)).addName(y.name(YLanguage.NoLinguisticContent, forenames, NM_FORENAMES)).addAttribute(AT_CONTACT_EMAIL, toNull(getTextTrim(optDescendant(c, "email")))).addAffiliationRef(aref)).addAffiliation(y.affiliation(aref, address));
            } else {
                article.addContributor(
                        new YContributor(YConstants.CR_OTHER, false).addName(y.canonicalName(YLanguage.NoLinguisticContent, canonicalName)).addName(y.name(YLanguage.NoLinguisticContent, surname, NM_SURNAME)).addName(y.name(YLanguage.NoLinguisticContent, forenames, NM_FORENAMES)).addAttribute(AT_CONTACT_EMAIL, toNull(getTextTrim(optDescendant(c, "email")))).addAffiliationRef(aref)).addAffiliation(y.affiliation(aref, address));
            }
        }
        // pubdate
        org.jdom.Element pd = ameta.getChild("pub-date");
        if (pd != null) {
            String yearText = pd.getChildText("year");
            String monthText = pd.getChildText("month");
            String dayText = pd.getChildText("day");
            YDate pubdate = new YDate();
            pubdate.setType(YConstants.DT_PUBLISHED);
            String dateText = "";
            if (dayText != null) {
                pubdate.setDay(dayText);
                dateText = dateText.concat(dayText);
            }
            if (monthText != null) {
                pubdate.setMonth(monthText);
                if (dateText.length() > 0) {
                    dateText = dateText.concat(" ");
                }
                dateText = dateText.concat(monthText);
            }
            if (yearText != null) {
                pubdate.setYear(yearText);
                if (dateText.length() > 0) {
                    dateText = dateText.concat(" ");
                }
                dateText = dateText.concat(yearText);
            }
            if (dateText.length() > 0) {
                pubdate.setText(dateText);
            } else {
                String stringDateText = pd.getChildText("string-date");
                if (stringDateText != null) {
                    pubdate.setText(stringDateText);
                }
            }

            article.addDate(pubdate);
        }
        // keywords
        YTagList kwds = new YTagList(YLanguage.Undetermined, TG_KEYWORD);
        @SuppressWarnings("unchecked")
        List<org.jdom.Element> kgs = ameta.getChildren("kwd-group");
        for (org.jdom.Element kg : kgs) {
            // TODO: distinguish kwd-group-types (eg. "msc")
            @SuppressWarnings("unchecked")
            List<org.jdom.Element> ks = kg.getChildren("kwd");
            for (org.jdom.Element k : ks) {
                kwds.addValue(textOfElement(k));
            }
        }
        if (kwds.size() > 0) {
            article.addTagList(kwds);
        }

        article.getStructure(EXT_HIERARCHY_JOURNAL).getCurrent().setPosition(ameta.getChildTextTrim("fpage") + "-" + ameta.getChildTextTrim("lpage"));

        List<org.jdom.Element> refs = optChildren(reflist, "ref");
        for (org.jdom.Element ref : refs) {
            YRelation relation = new YRelation().setType(RL_REFERENCE_TO);
            org.jdom.Element mc = ref.getChild("mixed-citation");
            if (mc != null) {
                processMixedCitation(relation, mc);
            } else {
                processElementCitation(relation, ref.getChild("element-citation"));
            }
            String eudmlId = relation.getOneAttributeSimpleValue(YConstants.AT_REFERENCE_PARSED_ID_EUDML);
            if (eudmlId != null) {
                relation.setTarget(new YId(YConstants.EXT_SCHEMA_EUDML, eudmlId));
            }
            article.addRelation(relation);
        }
        return article;
    }

    private void storeElementTextInAttribute(org.jdom.Element element, String elementName,
            YRelation relation, String attributeName) {
        String text = element.getChildTextTrim(elementName);
        if (text != null && !text.isEmpty()) {
            relation.addAttribute(attributeName, text);
        }
    }
    
    private void processCommonCitation(YRelation relation, org.jdom.Element element) {
        /*
         * These elements may be children of element-citation and mixed-citation:
         * 
         * abbrev alternatives annotation article-title bold chapter-title chem-struct 
         * collab comment conf-date conf-loc conf-name conf-sponsor date date-in-citation 
         * day edition elocation-id email etal ext-link fn fpage gov hr inline-formula 
         * inline-graphic inline-supplementary-material institution isbn issn issue 
         * issue-id issue-part issue-title italic label lpage milestone-end 
         * milestone-start mml:math monospace month name named-content object-id overline 
         * overline-end overline-start page-range part-title patent person-group 
         * private-char pub-id publisher-loc publisher-name related-article related-object
         * role roman sans-serif sc season series size source std strike string-date 
         * string-name styled-content sub sup supplement target tex-math trans-source 
         * trans-title underline underline-end underline-start uri volume volume-id
         * volume-series x xref year
         */

        List<org.jdom.Element> names = new ArrayList<org.jdom.Element>();
        names.addAll(element.getChildren("string-name"));
        names.addAll(element.getChildren("name"));
        
        for (org.jdom.Element name : names) {
            String surname = name.getChildTextTrim("surname");
            if (surname == null || surname.isEmpty()) {
                continue;
            }
            String givenNames = name.getChildTextTrim("given-names");
            if (givenNames == null) {
                givenNames = "";
            }
            String text = surname + (givenNames.isEmpty() ? "" : ", ") + givenNames;
            YAttribute author = new YAttribute(AT_REFERENCE_PARSED_AUTHOR, text);
            author.addAttribute(AT_REFERENCE_PARSED_AUTHOR_SURNAME, surname);
            if (!givenNames.isEmpty()) {
                author.addAttribute(AT_REFERENCE_PARSED_AUTHOR_FORENAMES, givenNames);
            }
            relation.addAttribute(author);
        }

        
        
        for (org.jdom.Element link : (List<org.jdom.Element>) element.getChildren("ext-link")) {
            if ("eudml-item-id".equals(link.getAttributeValue("ext-link-type"))) {
                relation.addAttribute(AT_REFERENCE_PARSED_ID_EUDML, link.getTextTrim());
            } else if ("mr-item-id".equals(link.getAttributeValue("ext-link-type"))) {
                relation.addAttribute(AT_REFERENCE_PARSED_ID_MR, link.getTextTrim());
            } else if ("zbl-item-id".equals(link.getAttributeValue("ext-link-type"))) {
                relation.addAttribute(AT_REFERENCE_PARSED_ID_ZBL, link.getTextTrim());
            }
        }

        String firstPage = element.getChildTextTrim("fpage");
        String lastPage = element.getChildTextTrim("lpage");
        if (firstPage != null && !firstPage.isEmpty()) {
            if (lastPage != null && !lastPage.isEmpty()) {
                relation.addAttribute(AT_REFERENCE_PARSED_PAGES, firstPage + "--" + lastPage);
            } else {
                relation.addAttribute(AT_REFERENCE_PARSED_PAGES, firstPage);
            }
        } else {
            storeElementTextInAttribute(element, "page-range", relation, AT_REFERENCE_PARSED_PAGES);
        }
        
        storeElementTextInAttribute(element, "chapter-title", relation, AT_REFERENCE_PARSED_CHAPTER);
        storeElementTextInAttribute(element, "publisher-loc", relation, AT_REFERENCE_PARSED_CITY);
        storeElementTextInAttribute(element, "issue", relation, AT_REFERENCE_PARSED_ISSUE);
        storeElementTextInAttribute(element, "month", relation, AT_REFERENCE_PARSED_MONTH);
        storeElementTextInAttribute(element, "comment", relation, AT_REFERENCE_PARSED_NOTE);
        storeElementTextInAttribute(element, "publisher-name", relation, AT_REFERENCE_PARSED_PUBLISHER);
        storeElementTextInAttribute(element, "volume", relation, AT_REFERENCE_PARSED_VOLUME);
        storeElementTextInAttribute(element, "year", relation, AT_REFERENCE_PARSED_YEAR);
    }
    
    private void processMixedCitation(YRelation relation, org.jdom.Element element) {
        processCommonCitation(relation, element);
        
        if (element.getChild("conf-name") != null) {
            relation.addAttribute(AT_REFERENCE_PARSED_TYPE, RF_INPROCEEDINGS);
            storeElementTextInAttribute(element, "source", relation, AT_REFERENCE_PARSED_BOOKTITLE);
            storeElementTextInAttribute(element, "article-title", relation, AT_REFERENCE_PARSED_TITLE);
        } else if (element.getChild("volume") != null
                || element.getChild("issue") != null
                || element.getChild("article-title") != null) {
            relation.addAttribute(AT_REFERENCE_PARSED_TYPE, RF_ARTICLE);
            storeElementTextInAttribute(element, "source", relation, AT_REFERENCE_PARSED_JOURNAL);
            storeElementTextInAttribute(element, "article-title", relation, AT_REFERENCE_PARSED_TITLE);
        } else {
            relation.addAttribute(AT_REFERENCE_PARSED_TYPE, RF_BOOK);
            storeElementTextInAttribute(element, "source", relation, AT_REFERENCE_PARSED_TITLE);
        }

        YRichText text = textOfElement(element);
        relation.addAttribute(AT_REFERENCE_TEXT, text);
    }
    
    private void processElementCitation(YRelation relation, org.jdom.Element element) {
        processCommonCitation(relation, element);
        
        if ("article".equals(element.getAttributeValue("publication-type"))) {
            relation.addAttribute(AT_REFERENCE_PARSED_TYPE, RF_ARTICLE);
            storeElementTextInAttribute(element, "source", relation, AT_REFERENCE_PARSED_JOURNAL);
            storeElementTextInAttribute(element, "article-title", relation, AT_REFERENCE_PARSED_TITLE);
        } else { // if ("book".equals(element.getAttributeValue("publication-type"))) {
            relation.addAttribute(AT_REFERENCE_PARSED_TYPE, RF_BOOK);
            storeElementTextInAttribute(element, "source", relation, AT_REFERENCE_PARSED_TITLE);
        }

        YRichText text = new YRichText(formatElementCitation(element));
        relation.addAttribute(AT_REFERENCE_TEXT, text);
    }
    
    /**
     * Processes <code>article-meta</code> metadata creating an issue element.
     *
     * @param ameta the metadata element to extract issue info from
     * @param parent parent element for the created element
     * @return the created issue element
     */
    private YElement processIssue(org.jdom.Element ameta, YElement parent) {
        String name = getTextTrim(optDescendant(ameta, "issue"));

        YElement issue = y.element(EXT_LEVEL_JOURNAL_ISSUE,
                y.canonicalName(YLanguage.NoLinguisticContent, name),
                parent);
        List<org.jdom.Element> aids = ameta.getChildren("issue-id");
        for (org.jdom.Element aid : aids) {

            if ("eudml-id".equalsIgnoreCase(aid.getAttributeValue("pub-id-type"))) {
                issue.addId(new YId(YConstants.EXT_SCHEMA_EUDML, aid.getText()));

            } else {
                issue.addId(new YId("bwmeta1.id-class." + aid.getAttributeValue("pub-id-type"), aid.getText()));
            }
        }
        return issue;
    }

    /**
     * Processes <code>article-meta</code> metadata creating a year element.
     *
     * @param ameta the metadata element to extract volume info from
     * @param parent parent element for the created element
     * @return the created volume element
     */
    private YElement processVolume(org.jdom.Element ameta, YElement parent) {
        return y.element(EXT_LEVEL_JOURNAL_VOLUME,
                y.canonicalName(YLanguage.NoLinguisticContent, getTextTrim(optDescendant(ameta, "volume"))),
                parent);
    }

    /**
     * Processes <code>article-meta</code> metadata creating a year element.
     *
     * @param ameta the metadata element to extract year info from
     * @param parent parent element for the created element
     * @return the created year element
     */
    private YElement processYear(org.jdom.Element ameta, YElement parent) {
        return y.element(EXT_LEVEL_JOURNAL_YEAR,
                y.canonicalName(YLanguage.NoLinguisticContent, getTextTrim(optDescendant(ameta, "pub-date", "year"))),
                parent);
    }

    /**
     * Processes <code>journal-meta</code> metadata creating a journal element.
     *
     * @param jmeta the metadata element to extract journal info from
     * @param parent parent element for the created element
     * @return the created journal element
     */
    private YElement processJournal(org.jdom.Element jmeta, YElement parent) {
        org.jdom.Element jtitles = jmeta.getChild("journal-title-group");
        List<org.jdom.Element> aids = jmeta.getChildren("journal-id");
        YElement journal = y.element(EXT_LEVEL_JOURNAL_JOURNAL,
                y.canonicalName(YLanguage.Undetermined, jtitles.getChildTextTrim("journal-title")),
                parent).addName(y.name(YLanguage.Undetermined, jtitles.getChildTextTrim("abbrev-journal-title"), NM_ABBREVIATION)).addId(y.id(EXT_SCHEME_ISSN, jmeta.getChildTextTrim("issn")));

        for (org.jdom.Element aid : aids) {

            if ("eudml-id".equalsIgnoreCase(aid.getAttributeValue("journal-id-type"))) {
                journal.addId(new YId(YConstants.EXT_SCHEMA_EUDML, aid.getText()));

            } else {
                journal.addId(new YId("bwmeta1.id-class." + aid.getAttributeValue("journal-id-type"), aid.getText()));
            }
        }
        YName pname = y.canonicalName(YLanguage.Undetermined, getTextTrim(optDescendant(jmeta, "publisher", "publisher-name")));

        journal.addContributor(new YContributor(CR_PUBLISHER, true).addName(pname));
        return journal;

    }

    /**
     * Processes <code>journal-meta</code> metadata creating a publisher element.
     *
     * @param jmeta the metadata element to extract publisher info from
     * @return the created publisher element
     */
    private YElement processPublisher(org.jdom.Element jmeta) {
        YName pname = y.canonicalName(YLanguage.Undetermined, getTextTrim(optDescendant(jmeta, "publisher", "publisher-name")));
        // TODO: add publisher-loc
        return y.element(EXT_LEVEL_JOURNAL_PUBLISHER, pname, ROOT).addContributor(new YContributor(CR_PUBLISHER, true).addName(pname));
    }

    /**
     * Formats an "element-citation" element as text.
     *
     * @param ec an element containing the citation metadata
     * @return the formatted citation text
     */
    private String formatElementCitation(org.jdom.Element ec) {
        ArrayList<String> parts = new ArrayList<String>();
        List<org.jdom.Element> as = optChildren(ec, "name");
        for (org.jdom.Element a : as) {
            addNonEmpty(parts, a.getChildTextTrim("surname"));
            addNonEmpty(parts, a.getChildTextTrim("given-names"));
        }
        addNonEmpty(parts, ec.getChildTextTrim("article-title"));
        String source = ec.getChildTextTrim("source");
        if (source != null) {
            String vol = ec.getChildTextTrim("volume");
            if (vol != null) {
                source = source + " " + vol;
            }
            String year = ec.getChildTextTrim("year");
            if (year != null) {
                source = source + " (" + year + ")";
            }
        }
        addNonEmpty(parts, source);
        String pr = ec.getChildTextTrim("fpage");
        String lp = ec.getChildTextTrim("lpage");
        if (lp != null) {
            pr = pr + "-" + lp;
        }
        addNonEmpty(parts, pr);

        return StringUtils.join(parts, ", ");
    }

    /**
     * Adds a string to an array if the string is not null and not empty,
     * otherwise leaves the arrat unchanged.
     *
     * @param l the list to add the string to
     * @param s the string to add to the list, or null
     */
    private void addNonEmpty(ArrayList<String> l, String s) {
        if (s != null && !s.isEmpty()) {
            l.add(s);
        }
    }

    /**
     * Returns the content of an element as {@link YRichText}. If the element is null, returns empty text.
     *
     * @param e the element to get the text value from, may be null
     * @return text extracted from the element content
     */
    private YRichText textOfElement(org.jdom.Element e) {
        if (e == null) {
            return new YRichText();
        }
        return YRTHelper.buildYrichText(e);
    }

    /**
     * Returns the trimmed text content of an element. If the element is null, returns an empty string.
     * <p>
     * XXX: generally useful?, move to some commons?
     * XXX: use noMathMLValue everywhere?
     *
     * @param e the element to get the text content of, may be null
     * @return the trimmed text content of the element, or an empty string if e is null
     */
    private String getTextTrim(org.jdom.Element e) {
        if (e == null) {
            return "";
        }
        return e.getTextTrim();
    }

    /**
     * Converts an empty string to null.
     *
     * @param s a string, may be null
     * @return the string s, or null s is empty or null
     */
    private String toNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return s;
    }

    /**
     * Finds the effective xml:lang value of an element (that is either from
     * attribute on the element or inherited from some ancestor) and converts it to
     * a {@link YLanguage} value.
     * <p>
     * XXX: generally useful?, move to some commons?
     *
     * @param e the element to determine the language for, or null
     * @return lang value, <code>Undetermined</code> if there is none or e is null
     */
    private YLanguage getYLang(org.jdom.Element e) {
        String l = null;
        while (l == null && e != null) {
            l = e.getAttributeValue("lang", Namespace.XML_NAMESPACE);
            e = e.getParentElement();
        }
        return YLanguage.byCode(l, YLanguage.Undetermined);
    }

    /**
     * Finds the first descendant located on a path from an element
     * specified by the given child names, if it exists.
     * <p>
     * XXX: generally useful?, move to some commons?
     *
     * @param e the element to find the descendant of, may be null
     * @param names names of child elements forming the path to the requested descendant
     * @return the found descendant, null if it does not exist or e is null
     */
    private org.jdom.Element optDescendant(org.jdom.Element e, String... names) {
        for (String n : names) {
            if (e == null) {
                return null;
            }
            e = e.getChild(n);
        }
        return e;
    }

    /**
     * Finds all the children of an element which have the given name.
     * If the element is null returns an empty list (immutable).
     *
     * @param e the element to find the children of, may be null
     * @param name name of the children to match
     * @return list of the found children
     */
    @SuppressWarnings("unchecked")
    private List<org.jdom.Element> optChildren(org.jdom.Element e, String name) {
        if (e == null) {
            return Collections.emptyList();
        } else {
            return e.getChildren(name);
        }
    }
    /*
     * Methods copied from NLMMetaReader
     * @author mpol
     * @author estocka: pubdate added, contributors and tags conversion modified
     */ 
}
