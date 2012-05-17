package pl.edu.icm.yadda.analysis.relations.auxil.parallel.bwmeta2surnameSesame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.bwmeta.model.YAffiliation;
import pl.edu.icm.yadda.bwmeta.model.YAncestor;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YCurrent;
import pl.edu.icm.yadda.bwmeta.model.YDate;
import pl.edu.icm.yadda.bwmeta.model.YDescription;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YInstitution;
import pl.edu.icm.yadda.bwmeta.model.YLanguage;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YPerson;
import pl.edu.icm.yadda.bwmeta.model.YRelation;
import pl.edu.icm.yadda.bwmeta.model.YStructure;
import pl.edu.icm.yadda.bwmeta.model.YTagList;
import pl.edu.icm.yadda.common.YaddaException;
import pl.edu.icm.yadda.desklight.model.RepositoryStringConstants;
import pl.edu.icm.yadda.parsing.ICitationParser;
import pl.edu.icm.yadda.parsing.regexpparser.RegexpReferenceParser2;
import pl.edu.icm.yadda.tools.content.IAuthorParser;
import pl.edu.icm.yadda.tools.content.RegexpAuthorParser;
import pl.edu.icm.yadda.tools.metadata.model.DocAuthor;
import pl.edu.icm.yadda.tools.relations.Statements;
import pl.edu.icm.yadda.tools.relations.Statements.PredicateAndObject;
import pl.edu.icm.yadda.tools.trans.DiacriticsRemover;

/**
 * Builds relationships describing elements, persons and institutions.
 * 
 * @author pdendek (p.dendek@icm.edu.pl)
 *
 */
public class ExtendedSurnameRelationshipStatementsBuilder {
	private static final Logger log = LoggerFactory.getLogger(ExtendedSurnameRelationshipStatementsBuilder.class);
	private static ICitationParser parser = null;
	private HashMap<String,AtomicInteger> hm; 
		
		
	public ExtendedSurnameRelationshipStatementsBuilder(
			HashMap<String, AtomicInteger> hm) {
			this.hm=hm;
	}

	public synchronized void setDefaultParser(){
		synchronized(ExtendedSurnameRelationshipStatementsBuilder.class){
			try {
				if(parser==null)
				parser = new RegexpReferenceParser2("pl/edu/icm/yadda/tools/content/config/referenceParser.properties");
			} catch (YaddaException e) {
				parser = null;
				log.error(e.toString());
				for(StackTraceElement s: e.getStackTrace())
					log.error(s.toString());
			}
		}
	}
	
	public static void setParser(ICitationParser outparser){
		synchronized(ExtendedSurnameRelationshipStatementsBuilder.class){
			parser=outparser;
		}
	}
	
	/**
	 * Builds a list of statements describing given exportable items.
	 * 
	 * @param items  Exportable items to process.  Items other than element, person or institution are ignored.
	 * @return  Statements describing given exportable items.
	 * @see #buildStatements(YExportable...)
	 */
	public ReturnObject buildStatements(Collection<YExportable> items) {
		setDefaultParser();
		
		if (items == null){
			ReturnObject ro = new ReturnObject();
			ro.setStatements(Collections.emptyList());
			ro.setSurnames(Collections.emptyList());
			return ro;
		}
		
		List statements = new ArrayList<Statements>();
		List names = new ArrayList<String>();
		
		for (YExportable item : items) {
			if (item instanceof YElement){
				ReturnObject ro = processElement((YElement) item);
				statements.addAll(ro.getStatements());
				names.addAll(ro.getSurnames());
			}
			else if (item instanceof YInstitution)
				statements.addAll(processInstitution((YInstitution) item));
			else if (item instanceof YPerson)
				statements.addAll(processPerson((YPerson) item));
			else
				log.warn("Ignoring item of type " + item.getClass().getName());
		}

		ReturnObject ro = new ReturnObject(); 
		ro.setStatements(statements);
		ro.setSurnames(names);
		return ro;
	}
	
	/**
	 * Builds a list of statements describing given exportable items.
	 * 
	 * @param items  Exportable items to process.  Items other than element, person or institution are ignored.
	 * @return  Statements describing given exportable items.
	 * @see #buildStatements(Collection)
	 */
	public ReturnObject buildStatements(YExportable... items) {
		return buildStatements(Arrays.asList(items));
	}

	List<Statements> statements = new LinkedList<Statements>();
	List<String> surnames = new LinkedList<String>();
	Statements s_doc = new Statements();
	LinkedList<PredicateAndObject> paos_doc = new LinkedList<PredicateAndObject>();
	YElement in_item;
	IAuthorParser rap = null;
	final String regexpAuthorParserProp = "pl/edu/icm/yadda/tools/content/config/authorParser.properties";
	
	protected ReturnObject processElement(YElement item) {
		//element
		in_item=item;
		statements.add(s_doc);
		s_doc.setSubject(RelConstants.NS_DOCUMENT+in_item.getId().substring(0));
		
		proceedTags();
		proceedLanguage();
		proceedAffiliations();
		proceedCategoryRefs();
		parseContributors();
		parseTitle();
		parseHierarchy();
		parseDates();
		parseDescriptions();
		parseRelations();

		s_doc.setContinuations(paos_doc);
		
		ReturnObject ro = new ReturnObject();
		ro.setStatements(statements);
		ro.setSurnames(surnames);
		return ro;
	}

	/**
	 * accomplished 
	 * 
	 * FIXME: popraw wszystko co można zliczać np int refno;
	 * iteracje nie działają, refno nigdy nie jest inkrementowane!!!!!
	 */
	private void parseRelations() {
		if(!in_item.getRelations().isEmpty()){
			int refno = 0;
			for(YRelation yr : in_item.getRelations()){
				paos_doc.add(new PredicateAndObject(RelConstants.RL_REFERENCES, RelConstants.NS_REFERENCE+in_item.getId().substring(0)+"#r"+refno));
				Statements s_ref = new Statements();s_ref.setSubject(RelConstants.NS_REFERENCE+in_item.getId().substring(0)+"#r"+refno);
				LinkedList<PredicateAndObject> paos_ref = new LinkedList<PredicateAndObject>();
				LinkedList<PredicateAndObject> paos_ref2 = new LinkedList<PredicateAndObject>();
				int conid = 0;
				for(YAttribute ya : yr.getAttributes()){
					if(parseReferenceId(ya,paos_ref,refno));/*1*/
					else if(parseReferenceType(ya,paos_ref,refno));/*2*/
					else if(parseReferenceCategory(ya,paos_ref,refno));/*4*/
					else if(parseReferenceAuthor(ya,paos_ref,refno, conid)) conid++;/*5*/
					else if(parseReferenceTag(ya,paos_ref,refno));/*9*/
					else if(parseReferenceName(ya,paos_ref,refno));/*8*/
					else if(parseReferenceText(ya,paos_ref2,refno));/*3*/
				}
				//if there is any information about this reference
				//in regulat tag: take it!
				//if the only piece of information is reference-text:
				//parse it and add
				if(paos_ref.size()>0) s_ref.setContinuations(paos_ref); 
				else s_ref.setContinuations(paos_ref2);
				statements.add(s_ref);
				refno++;
			}
		}
	}

	/**
	 * accomplished
	 * @param ya
	 * @param paos_ref
	 * @param refno
	 * @return
	 */
	private boolean parseReferenceName(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(YConstants.AT_ENHANCED_FROM_ZBL_NAME.equals(ya.getKey())){/*8*/
			for(YAttribute inner : ya.getAttributes()){
				if(YConstants.AT_ENHANCED_FROM_ZBL_TYPE.equals(inner.getKey())){/*8.1*/
					
				}else if(YConstants.AT_ENHANCED_FROM_ZBL_LANGUAGE.equals(inner.getKey())){/*8.2*/
					
				}else if(YConstants.AT_ENHANCED_FROM_ZBL_VALUE.equals(inner.getKey())){/*8.3*/
					paos_ref.add(new PredicateAndObject(RelConstants.RL_TITLE, inner.getValue()));
				}
			}
			return true;
		}else return false;
	}

	/**
	 * accomplished
	 * @param ya
	 * @param paos_ref
	 * @param refno
	 * @return
	 */
	private boolean parseReferenceTag(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(YConstants.AT_ENHANCED_FROM_ZBL_TAG.equals(ya.getKey())){/*9*/
			
			
			String lang = "", type = "";
			for(YAttribute inner : ya.getAttributes()){
				if(YConstants.AT_ENHANCED_FROM_ZBL_TYPE.equals(inner.getKey())){/*9.1*/
					type = inner.getValue().toString();
				}else if(YConstants.AT_ENHANCED_FROM_ZBL_LANGUAGE.equals(inner.getKey())){/*9.2*/
					lang = inner.getValue().toString();
				}
			}
			
			
			int tagid=0;
			for(YAttribute inner : ya.getAttributes()){
				if(YConstants.AT_ENHANCED_FROM_ZBL_TYPE.equals(inner.getKey())){
					continue;
				}else if(YConstants.AT_ENHANCED_FROM_ZBL_LANGUAGE.equals(inner.getKey())){
					continue;
				}
				
				tagid++;
				String id = RelConstants.NS_TAG+"-"+in_item.getId()+"#r#"+refno+"#t#"+tagid;
				paos_ref.add(new PredicateAndObject(RelConstants.RL_TAG, id));		
				Statements t_affil = new Statements(); 
				t_affil.setSubject(id);
				LinkedList<PredicateAndObject> paos_affil = new LinkedList<PredicateAndObject>();
				if(YConstants.AT_ENHANCED_FROM_ZBL_VALUE.equals(inner.getKey())){/*9.3*/
					paos_affil.add(new PredicateAndObject(RelConstants.RL_TYPE, type));
					paos_affil.add(new PredicateAndObject(RelConstants.RL_LANGUAGE, lang));
					paos_affil.add(new PredicateAndObject(RelConstants.RL_TEXT, inner.getValue().toLowerCase()));
				}
				t_affil.setContinuations(paos_affil);
				statements.add(t_affil);	
			}
			return true;
		}else return false;
	}

	/**
	 * accomplished
	 * @param ya
	 * @param paos_ref
	 * @param refno
	 * @return
	 */
	private boolean parseReferenceAuthor(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno, int contr) {
		
		if(YConstants.AT_REFERENCE_PARSED_AUTHOR.equals(ya.getKey())){/*5*/
			String contrId = RelConstants.NS_CONTRIBUTOR +in_item.getId().substring(0) +"#r"+refno+"#c"+contr; 
			paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_CONTRIBUTOR, contrId));
			Statements s = new Statements(); s.setSubject(contrId); statements.add(s);
			LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>(); 
			
			String sname = null;
			String fname = null;
			String cname = null;
			
			pao.add(new PredicateAndObject(RelConstants.RL_HAS_POSITION_IN_REFERENCE_DOCUMENT,contr+""));
			for(YAttribute inner : ya.getAttributes()){
				if(YConstants.AT_ZBL_AUTHOR_FINGERPRINT.equals(inner.getKey()) && !"-".equals(inner.getValue())){/*5.1*/
					String zblfingerprint=inner.getValue();
					synchronized(hm){
						AtomicInteger ai = hm.get(zblfingerprint);
						if(ai!=null)ai.incrementAndGet();
						else hm.put(zblfingerprint, new AtomicInteger(1));
					}
					pao.add(new PredicateAndObject(RelConstants.RL_IS_PERSON, RelConstants.NS_ZBL_PERSON + zblfingerprint)); 
				}else if(YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES.equals(inner.getKey())){/*5.2*/
					fname=inner.getValue();
					pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,fname));
				}else if(YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME.equals(inner.getKey())){/*5.3*/
					sname=inner.getValue();
					pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,sname));
				}
			}
			if(!ya.getValue().isEmpty()){//canonical name
				cname=ya.getValue();
				pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,cname));
			}else{
				if(fname!=null)
					pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,fname+" "+sname));
				else
					pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,sname));
			}
			
			s.setContinuations(pao);
			contr++;
			
			return true;
		}else return false;
	}

	/**
	 * transplantation form RelationStatementBuilder
	 * @param ya
	 * @param paos_ref
	 * @param refno
	 * @return
	 */
	private boolean parseReferenceCategory(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(YConstants.TG_CATEGORY.equals(ya.getKey())){/*6*/
			for(YAttribute inner : ya.getAttributes()){
				if(YConstants.EXT_SCHEME_ZBL.equals(inner.getKey())){/*6.1*/
					paos_ref.add(new PredicateAndObject(RelConstants.RL_IS_DOCUMENT, RelConstants.NS_ZBL_DOCUMENT+ inner.getValue()));
				}else if(YConstants.EXT_SCHEME_ISSN.equals(inner.getKey())){/*6.2*/
					paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_ISSN, RelConstants.NS_ISSN+ inner.getValue()));
				}else if(YConstants.EXT_SCHEME_ISSN.equals(inner.getKey())){/*6.2*/
					paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_ISBN, RelConstants.NS_ISBN+ inner.getValue()));					
				}else if(YConstants.EXT_SCHEME_ISBN.equals(inner.getKey())){/*6.3*/
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_CEJSH, inner.getValue()));
				}else if(YConstants.EXT_CLASSIFICATION_CEJSH.equals(inner.getKey())){
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_CEJSH, inner.getValue()));
				}else if(YConstants.EXT_CLASSIFICATION_CLC.equals(inner.getKey())){
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_CLC, inner.getValue()));
				}else if(YConstants.EXT_CLASSIFICATION_JEL.equals(inner.getKey())){
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_JEL, inner.getValue()));
				}else if(YConstants.EXT_CLASSIFICATION_MSC.equals(inner.getKey())){
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_MSC, inner.getValue()));
				}else if(YConstants.EXT_CLASSIFICATION_PACS.equals(inner.getKey())){
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_PACS, inner.getValue()));
				}else if(YConstants.EXT_CLASSIFICATION_QICS.equals(inner.getKey())){
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_QICS, inner.getValue()));
				}else if(YConstants.EXT_CLASSIFICATION_ZDM.equals(inner.getKey())){
					paos_ref.add(new PredicateAndObject(RelConstants.RL_CATEGORY_ZDM, inner.getValue()));
				}
			}
			return true;
		}else return false;
	}

	/**
	 * transplantation form RelationStatementBuilder 
	 * @param ya
	 * @param paos_ref
	 * @param refno
	 * @return
	 */
	private boolean parseReferenceText(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(YConstants.AT_REFERENCE_TEXT.equals(ya.getKey())){/*3*/
			parseReferenceText(ya.getValue(), paos_ref, refno);
			return true;
		}else return false;
	}

	/**
	 * transplantation form RelationStatementBuilder
	 * @param value
	 * @param paos_ref
	 * @param refno
	 */
	private void parseReferenceText(String value, LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(parser!=null){
			try {
				YRelation parsed = parser.parse(value);
				if(parsed!=null){
					String title = parsed.getOneAttributeSimpleValue(YConstants.AT_REFERENCE_PARSED_TITLE);
					String journal = parsed.getOneAttributeSimpleValue(YConstants.AT_REFERENCE_PARSED_JOURNAL);
					String volume = parsed.getOneAttributeSimpleValue(YConstants.AT_REFERENCE_PARSED_VOLUME);
					String year = parsed.getOneAttributeSimpleValue(YConstants.AT_REFERENCE_PARSED_YEAR);
					String pages = parsed.getOneAttributeSimpleValue(YConstants.AT_REFERENCE_PARSED_PAGES);
					
					if(title!=null) paos_ref.add(new PredicateAndObject(RelConstants.RL_TITLE, title));
					if(journal!=null) paos_ref.add(new PredicateAndObject(RelConstants.RL_JOURNAL, journal));
					if(volume!=null) paos_ref.add(new PredicateAndObject(RelConstants.RL_VOLUME, volume.replace(" ", "")));
					if(year!=null) paos_ref.add(new PredicateAndObject(RelConstants.RL_YEAR, year));
					if(pages!=null) paos_ref.add(new PredicateAndObject(RelConstants.RL_PAGES, pages));
				
				
					List<YAttribute> parsedContrib = parsed.
					getAttributes(YConstants.AT_REFERENCE_PARSED_AUTHOR);
					int innerContrib = 0;
					for(YAttribute contrib : parsedContrib){
						String forename = contrib.getOneAttributeValue(YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES);
						forename = forename.replaceAll("(\\.| )", "");						
						String surname = contrib.getOneAttributeValue(YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME);
						Matcher less = Pattern.compile("\\b(van|von|der|den|de|di|le|el)\\b",
								Pattern.CASE_INSENSITIVE).matcher(surname);
						StringBuilder sb; 
						boolean next = less.find();
						if(next){
							sb = new StringBuilder(surname);
							sb.delete(less.start(), less.end());
							int iaaaa = 0;
							while(true){
								less = Pattern.compile("\\b(van|von|der|den|de|di|le|el)\\b",Pattern.CASE_INSENSITIVE).matcher(sb);
								if(!less.find()) break;
								sb.delete(less.start(), less.end());
							}
							surname = sb.toString().trim();
						}
						
						
						String contrId = RelConstants.NS_CONTRIBUTOR+in_item.getId().substring(0)
						+"#r"+refno+"#c"+innerContrib; 
						
						paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_CONTRIBUTOR,
								contrId));
						
						Statements s = new Statements(); s.setSubject(contrId); statements.add(s);
						LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>(); 
						pao.add(new PredicateAndObject(RelConstants.RL_HAS_POSITION_IN_REFERENCE_DOCUMENT,innerContrib+""));
						pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,forename));
						pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,surname));
						s.setContinuations(pao);
						innerContrib++;
					}
				}
			} catch (YaddaException e) {
				log.error("Failure during parsing of reference in RelationStatementBuilder;" +
						"Continuing work... \nError code:");
				log.error(e.toString());
			}
		}
	}

	/**
	 * TODO, so far: ignore
	 * @param ya
	 * @param paos_ref
	 * @param refno
	 * @return
	 */
	private boolean parseReferenceType(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(YConstants.AT_REFERENCE_PARSED_TYPE.equals(ya.getKey())){/*2*/
			
			return true;
		}else return false;
	}

	/**
	 * accomplished
	 * @param ya
	 * @param paos_ref
	 * @param refno
	 * @param refid 
	 * @return
	 */
	private boolean parseReferenceId(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(YConstants.AT_REFERENCE_PARSED_ID_MR.equals(ya.getKey())){/*1.1*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_IS_DOCUMENT, RelConstants.NS_MR_DOCUMENT+ ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_ID_ZBL.equals(ya.getKey())){/*1.2*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_IS_DOCUMENT, RelConstants.NS_ZBL_DOCUMENT+ ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_ID_ISBN.equals(ya.getKey())){
			paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_ISBN, RelConstants.NS_ISBN+ ya.getValue()));
			return true;
		}else return false;
	}

	/**
	 * accomplished
	 */
	private void parseDescriptions() {
		if(!in_item.getDescriptions().isEmpty()){
			int descriptionid = 0;
			for(YDescription yd : in_item.getDescriptions()){
				descriptionid++;
				Statements s_desc = new Statements();statements.add(s_desc);
				s_desc.setSubject(RelConstants.NS_DESCRIPTION+in_item.getId()+"#"+descriptionid);
				LinkedList<PredicateAndObject> paos_description = new LinkedList<PredicateAndObject>();
				paos_description.add(new PredicateAndObject(RelConstants.RL_LANGUAGE, yd.getLanguage().getName()));
				paos_description.add(new PredicateAndObject(RelConstants.RL_TYPE, yd.getType()));
				paos_description.add(new PredicateAndObject(RelConstants.RL_TEXT, yd.getText()));				
				paos_doc.add(new PredicateAndObject(RelConstants.RL_HAS_DESCRIPTION , RelConstants.NS_DESCRIPTION+in_item.getId()+"#"+descriptionid));
			}
		}
	}

	/**
	 * accomplished
	 */
	private void parseDates() {
		if(!in_item.getDates().isEmpty()){
			for(YDate yd : in_item.getDates()){
				if(yd.getYear()!=0)
					paos_doc.add(new PredicateAndObject(RelConstants.RL_YEAR, ""+yd.getYear()));
			}
		}
	}

	/**
	 * transplantation form RelationStatementBuilder
	 */
	private void parseHierarchy() {
		//hierarchy
		YStructure hier_journal = in_item.getStructure(RepositoryStringConstants.HIERARCHY_JOURNAL);
		if(hier_journal != null){
			YAncestor hier_journal_publisher = hier_journal.getAncestor(RepositoryStringConstants.HIERARCHY_JOURNAL_PUBLISHER);
			if(hier_journal_publisher != null && hier_journal_publisher.getOneName()!=null){
				paos_doc.add(new PredicateAndObject(RelConstants.RL_PUBLISHER, hier_journal_publisher.getOneName().getText()));
			}
			YAncestor hier_journal_journal = hier_journal.getAncestor(RepositoryStringConstants.HIERARCHY_JOURNAL_JOURNAL);
			if(hier_journal_journal != null && hier_journal_journal.getOneName()!=null){
				paos_doc.add(new PredicateAndObject(RelConstants.RL_JOURNAL, hier_journal_journal.getOneName().getText()));
			}
			YAncestor hier_journal_year = hier_journal.getAncestor(RepositoryStringConstants.HIERARCHY_JOURNAL_YEAR);
			if(hier_journal_year != null && hier_journal_year.getOneName()!=null){
				paos_doc.add(new PredicateAndObject(RelConstants.RL_YEAR, hier_journal_year.getOneName().getText()));
			}
			YAncestor hier_journal_volume = hier_journal.getAncestor(RepositoryStringConstants.HIERARCHY_JOURNAL_VOLUME);
			if(hier_journal_volume != null && hier_journal_volume.getOneName()!=null){
				paos_doc.add(new PredicateAndObject(RelConstants.RL_VOLUME, hier_journal_volume.getOneName().getText()));
			}
			YAncestor hier_journal_issue = hier_journal.getAncestor(RepositoryStringConstants.HIERARCHY_JOURNAL_ISSUE);
			if(hier_journal_issue != null && hier_journal_issue.getOneName()!=null){
				paos_doc.add(new PredicateAndObject(RelConstants.RL_ISSUE, hier_journal_issue.getOneName().getText()));
			}
			
			YAncestor hier_journal_article = hier_journal.getAncestor(RepositoryStringConstants.HIERARCHY_JOURNAL_ARTICLE);
			if(hier_journal_article != null && hier_journal_article.getPosition()!=null){
				if(hier_journal_article.getPosition().length()!=0) paos_doc.add(new PredicateAndObject(RelConstants.RL_PAGES, hier_journal_article.getPosition()));
			}
			YCurrent ha = hier_journal.getCurrent();
			if(ha != null && ha.getPosition()!=null){
				if(ha.getPosition().length()!=0) paos_doc.add(new PredicateAndObject(RelConstants.RL_PAGES, ha.getPosition()));
			}	
			
			
//			YAncestor hier_journal_XYZ = hier_journal.getAncestor(RepositoryStringConstants.HIERARCHY_JOURNAL_XYZ);
//			if(hier_journal_XYZ != null && hier_journal_XYZ.getOneName()!=null){
//				paos_doc.add(new PredicateAndObject(RelConstants.RL_XYZ, hier_journal_XYZ.getOneName().getText()));
//			}			
		}
	}

	/**
	 * accomplished
	 */
	private void parseTitle() {
		if(in_item.getOneName()!=null && in_item.getOneName().getText()!=null){
			paos_doc.add(new PredicateAndObject(RelConstants.RL_TITLE, in_item.getOneName().getText()));
		}
	}

	/**
	 * accomplished
	 * @throws YaddaException 
	 */
	private void parseContributors(){
		//contributors
		int contr = 0;
		if(!in_item.getContributors().isEmpty()){
			for(YContributor yc : in_item.getContributors()){
				 paos_doc.add(new PredicateAndObject(RelConstants.RL_HAS_CONTRIBUTOR, RelConstants.NS_CONTRIBUTOR+in_item.getId().substring(0)+"#c"+contr));
				
				Statements s = new Statements(); s.setSubject(RelConstants.NS_CONTRIBUTOR+ in_item.getId()+"#c"+contr); statements.add(s);
				LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>(); 
				
				pao.add(new PredicateAndObject(RelConstants.RL_HAS_POSITION_IN_DOCUMENT,contr+""));
				
				for(YAttribute ya : yc.getAttributes(YConstants.AT_CONTACT_EMAIL))
					pao.add(new PredicateAndObject(RelConstants.RL_CONTACT_EMAIL,ya.getValue()));
				
				//FIXME getOneAttributeSimpleValue może zwracać null; 
				//trzeba sprawdzić czy takie sytuacje się jescze zdarzają 
				//(czy istnieje ryzyko null pointer exceptiona) 
				String zblfingerprint = yc.getOneAttributeSimpleValue(YConstants.AT_ZBL_AUTHOR_FINGERPRINT);
				if(zblfingerprint!=null && !zblfingerprint.isEmpty()){
					pao.add(new PredicateAndObject(RelConstants.RL_IS_PERSON, RelConstants.NS_ZBL_PERSON + zblfingerprint));
					synchronized(hm){
						AtomicInteger ai = hm.get(zblfingerprint);
						if(ai!=null)ai.incrementAndGet();
						else hm.put(zblfingerprint, new AtomicInteger(1));
					}
				}
				
				
				if(!yc.getRole().isEmpty())
					pao.add(new PredicateAndObject(RelConstants.RL_HAS_ROLE,yc.getRole()));
				if(!yc.getIdentity().isEmpty()) 
					pao.add(new PredicateAndObject(RelConstants.RL_IS_PERSON,RelConstants.NS_PERSON+yc.getIdentity()));
				if(!yc.getNames().isEmpty()){
					String sname = "";
					String fname = "";
					String cname = null;
					for(YName yn : yc.getNames()){
						if("canonical".equals(yn.getType())){
							cname = yn.getText().trim();
						}else if("forenames".equals(yn.getType())){
							fname = yn.getText().trim();
							fname = fname.replaceAll("(\\.| )", "");
						}else if("surname".equals(yn.getType())){
							sname = yn.getText().trim();
							
							Matcher less = Pattern.compile("\\b(van|von|der|den|de|di|le|el|et)\\b",
									Pattern.CASE_INSENSITIVE).matcher(sname);
							StringBuilder sb; 
							boolean next = less.find();
							if(next){
								sb = new StringBuilder(sname);
								sb.delete(less.start(), less.end());
								while(true){
									less = Pattern.compile("\\b(van|von|der|den|de|di|le|el|et)\\b",Pattern.CASE_INSENSITIVE).matcher(sb);
									if(!less.find()) break;
									sb.delete(less.start(), less.end());
								}
								sname = sb.toString().trim();
							}
						}
					}
					if(cname == null) pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,(fname+" "+sname).trim()));
					else if(sname.equals("")){
						try{
							int i = 1;
							IAuthorParser rap = getRegexpAuthorParser();
							DocAuthor author = rap.parse(cname);
							fname = author.getForenames();
							sname = author.getSurname();
							
							Matcher less = Pattern.compile("\\b(van|von|der|den|de|di|le|el|et)\\b",
									Pattern.CASE_INSENSITIVE).matcher(sname);
							StringBuilder sb; 
							boolean next = less.find();
							if(next){
								sb = new StringBuilder(sname);
								sb.delete(less.start(), less.end());
								while(true){
									less = Pattern.compile("\\b(van|von|der|den|de|di|le|el|et)\\b",Pattern.CASE_INSENSITIVE).matcher(sb);
									if(!less.find()) break;
									sb.delete(less.start(), less.end());
								}
								sname = sb.toString().trim();
							}
							
						}catch(Exception e){
							log.error("RegexpAuthorParser can not match String \""+cname+"\"\n" +
									  "This means that surname connected with adequate contributor will be \"\"");
							log.error("Report connected with this raport:\n"+
									   e.toString());
							for(StackTraceElement ste : e.getStackTrace())
								log.error(ste.toString());
						}
					}
					
					sname = DiacriticsRemover.removeDiacritics(sname);
					
					pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,cname));
					pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,fname));
					pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,sname));
					this.surnames.add(sname);
				}

				if(yc.isInstitution()){
                                    if (yc.getIdentity()!=null & ! yc.getIdentity().isEmpty())
					pao.add(new PredicateAndObject(RelConstants.RL_IS_INSTITUTION,RelConstants.NS_INSTITUTION+yc.getIdentity()));
				}
				
				if(yc.getAffiliationRefs().isEmpty()){
					for(String ya : yc.getAffiliationRefs()){
						int aff_inner=0;
						for(YAffiliation a : in_item.getAffiliations()){
							if(ya.equals(a.getId())){
								pao.add(new PredicateAndObject(RelConstants.RL_IS_AFFILIATED_WITH_ID,RelConstants.NS_AFFILIATION+"#a"+aff_inner));
								aff_inner++;
							}
						}
					}
				}
				contr++;
				s.setContinuations(pao);
			}
		}
		
	}

	private synchronized IAuthorParser getRegexpAuthorParser() throws YaddaException {
		if(this.rap == null); this.rap = 
			new RegexpAuthorParser
			(this.regexpAuthorParserProp);
		return rap;
	}

	/**
	 * accomplished
	 */
	private void proceedCategoryRefs() {
		if(!in_item.getCategoryRefs().isEmpty()){
			for(YCategoryRef ycr : in_item.getCategoryRefs()){
				if(YConstants.EXT_CLASSIFICATION_CEJSH.equals(ycr.getClassification())) paos_doc.add(new PredicateAndObject(RelConstants.RL_CATEGORY_CEJSH, ycr.getCode().toString()));
				else if(YConstants.EXT_CLASSIFICATION_CLC.equals(ycr.getClassification())) paos_doc.add(new PredicateAndObject(RelConstants.RL_CATEGORY_CLC, ycr.getCode().toString()));
				else if(YConstants.EXT_CLASSIFICATION_JEL.equals(ycr.getClassification())) paos_doc.add(new PredicateAndObject(RelConstants.RL_CATEGORY_JEL, ycr.getCode().toString()));
				else if(YConstants.EXT_CLASSIFICATION_MSC.equals(ycr.getClassification())) paos_doc.add(new PredicateAndObject(RelConstants.RL_CATEGORY_MSC, ycr.getCode().toString()));
				else if(YConstants.EXT_CLASSIFICATION_PACS.equals(ycr.getClassification())) paos_doc.add(new PredicateAndObject(RelConstants.RL_CATEGORY_PACS, ycr.getCode().toString()));
				else if(YConstants.EXT_CLASSIFICATION_QICS.equals(ycr.getClassification())) paos_doc.add(new PredicateAndObject(RelConstants.RL_CATEGORY_QICS, ycr.getCode().toString()));
				else if(YConstants.EXT_CLASSIFICATION_ZDM.equals(ycr.getClassification())) paos_doc.add(new PredicateAndObject(RelConstants.RL_CATEGORY_ZDM, ycr.getCode().toString()));
			}
		} 
	}

	/**
	 * accomplished
	 */
	private void proceedAffiliations() {
		if(!in_item.getAffiliations().isEmpty()){
			int aff = 0;
			for(YAffiliation ya : in_item.getAffiliations()){
				 paos_doc.add(new PredicateAndObject(RelConstants.RL_CONTAINS_AFFILIATION, RelConstants.NS_AFFILIATION+in_item.getId().substring(0)+"#a"+aff));
				 Statements s_affil = new Statements(); 
				 s_affil.setSubject(RelConstants.NS_AFFILIATION+in_item.getId().substring(0)+"#a"+aff);
				 LinkedList<PredicateAndObject> paos_affil = new LinkedList<PredicateAndObject>();
				 
				 if(!ya.getSimpleText().isEmpty() && ya.getSimpleText()!=null)
					paos_affil.add(new PredicateAndObject(RelConstants.RL_TEXT, ya.getSimpleText()));
//				 if(ya.getIdentity()!=null && ya.getIdentity().length()!=0)
//					 paos_affil.add(new PredicateAndObject(RelConstants.RL_TEXT, ya.getIdentity()));
				 //TODO inne parametry afiliacji
				s_affil.setContinuations(paos_affil);
				statements.add(s_affil);
				aff++;
			}
		}
	}

	/**
	 * accomplished
	 */
	private void proceedLanguage() {
		for(YLanguage yl : in_item.getLanguages()){
			paos_doc.add(new PredicateAndObject(RelConstants.RL_LANGUAGE, yl.getName().toLowerCase()));
		}
	}

	/**
	 * accomplished
	 */
	private void proceedTags() {/*9*/
		int tagid=0;
		for(YTagList yt : in_item.getTagLists()){
			YLanguage lang = yt.getLanguage();
			String id = null;
			for(String tag : yt.getValues()){
				id = RelConstants.NS_TAG+"-"+in_item.getId()+"-"+tagid;
				paos_doc.add(new PredicateAndObject(RelConstants.RL_TAG, id));
				tagid++;
				Statements t_affil = new Statements(); 
				t_affil.setSubject(id);
				LinkedList<PredicateAndObject> paos_affil = new LinkedList<PredicateAndObject>();
				paos_affil.add(new PredicateAndObject(RelConstants.RL_TYPE, tag.toLowerCase()));
				paos_affil.add(new PredicateAndObject(RelConstants.RL_TEXT, tag.toLowerCase()));
				paos_affil.add(new PredicateAndObject(RelConstants.RL_LANGUAGE, lang.toString()));
				t_affil.setContinuations(paos_affil);
				statements.add(t_affil);	
			}
		}	
	}

	protected List<Statements> processInstitution(YInstitution item) {
		List<Statements> retL = new LinkedList<Statements>(); 
		
		if(!item.getNames().isEmpty()){
			Statements s = new Statements();
			s.setSubject(RelConstants.NS_INSTITUTION+item.getId());
			LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>();
			for(YName name : item.getNames()){
				pao.add(new PredicateAndObject(RelConstants.RL_NAME,name.getText()));				
			}
			s.setContinuations(pao);
			retL.add(s);
		}else;
		
		return retL;
	}

	protected List<Statements> processPerson(YPerson item) {
		List<Statements> retL = new LinkedList<Statements>();
		
		if(!item.getNames().isEmpty()){
			Statements s = new Statements();
			s.setSubject(RelConstants.NS_PERSON+item.getId());
			LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>();
			for(YName name : item.getNames()){
				if(name.getType().equals("canonical"))
					pao.add(new PredicateAndObject(RelConstants.RL_NAME,name.getText()));
				else if(name.getType().equals("forenames")){
					pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,name.getText().replace("[ \\\"]", "")));
				}
				else if(name.getType().equals("surname")){
					pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,name.getText().replace("[ \\\"]", "") ));
				}					
			}
			s.setContinuations(pao);
			retL.add(s);
		}else;
		
		return retL;
	}
}
