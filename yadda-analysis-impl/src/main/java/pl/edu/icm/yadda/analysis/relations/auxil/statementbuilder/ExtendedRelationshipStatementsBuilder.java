package pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import pl.edu.icm.yadda.tools.relations.Statements;
import pl.edu.icm.yadda.tools.relations.Statements.PredicateAndObject;

/**
 * Builds relationships describing elements, persons and institutions.
 * 
 * @author pdendek (p.dendek@icm.edu.pl)
 *
 */
public class ExtendedRelationshipStatementsBuilder {
	private static final Logger log = LoggerFactory.getLogger(ExtendedRelationshipStatementsBuilder.class);
	

	List<Statements> statements = null;
	Statements s_doc = null;
	LinkedList<PredicateAndObject> paos_doc = null;
	YElement in_item;
	
	/**
	 * Builds a list of statements describing given exportable items.
	 * 
	 * @param items  Exportable items to process.  Items other than element, person or institution are ignored.
	 * @return  Statements describing given exportable items.
	 * @throws YaddaException 
	 * @see #buildStatements(YExportable...)
	 */
	public List<Statements> buildStatements(Collection<YExportable> items) throws YaddaException {
		
		if (items == null)
			return Collections.emptyList();
		
		List<Statements> results = new ArrayList<Statements>();
		for (YExportable item : items) {
			if (item instanceof YElement)
				results.addAll(processElement((YElement) item));
			else if (item instanceof YInstitution)
				results.addAll(processInstitution((YInstitution) item));
			else if (item instanceof YPerson)
				results.addAll(processPerson((YPerson) item));
			else
				log.warn("Ignoring item of type " + item.getClass().getName());
		}

		return results;
	}
	
	/**
	 * Builds a list of statements describing given exportable items.
	 * 
	 * @param items  Exportable items to process.  Items other than element, person or institution are ignored.
	 * @return  Statements describing given exportable items.
	 * @throws YaddaException 
	 * @see #buildStatements(Collection)
	 */
	public List<Statements> buildStatements(YExportable... items) throws YaddaException {
		return buildStatements(Arrays.asList(items));
	}

	protected List<Statements> processElement(YElement item) throws YaddaException {
		
		in_item=item;
		
		statements = new LinkedList<Statements>();
		
		s_doc = new Statements();
		statements.add(s_doc);
		
		paos_doc = new LinkedList<PredicateAndObject>();
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
		return statements;
	}

	/**
	 * accomplished 
	 * 
	 * FIXME: popraw wszystko co można zliczać np int refno;
	 * iteracje nie działają, refno nigdy nie jest inkrementowane!!!!!
	 * @throws YaddaException 
	 */
	private void parseRelations() throws YaddaException {
		if(!in_item.getRelations().isEmpty()){
			int refno = 0;
			for(YRelation yr : in_item.getRelations()){ 
				if(parseReferences(yr,refno)) refno++;
				else if(parseComentary(yr));
			}
		}
	}

	private boolean parseComentary(YRelation yr) {
		if(YConstants.RL_COMMENTARY_TO.equals(yr.getType())){
			//TODO use this piece of information!
		}
		return false;
	}

	private boolean parseReferences(YRelation yr, int refno) throws YaddaException {
		if(YConstants.RL_REFERENCE_TO.equals(yr.getType())){
			paos_doc.add(new PredicateAndObject(RelConstants.RL_REFERENCES, RelConstants.NS_REFERENCE+in_item.getId().substring(0)+"/r"+refno));
			Statements s_ref = new Statements();s_ref.setSubject(RelConstants.NS_REFERENCE+in_item.getId().substring(0)+"/r"+refno);
			LinkedList<PredicateAndObject> paos_ref = new LinkedList<PredicateAndObject>();
//			LinkedList<PredicateAndObject> paos_ref2 = new LinkedList<PredicateAndObject>();
			LinkedList<YAttribute> lla = new LinkedList<YAttribute>();
			
			StringBuffer sb = new StringBuffer();
			int conid = 0;
			for(YAttribute ya : yr.getAttributes()){
				if(parseReferenceId(ya,paos_ref,refno));/*1*/
				else if(parseReferenceType(ya,paos_ref,refno));/*2*/
				else if(parseReferenceCategory(ya,paos_ref,refno));/*4*/
				else if(parseReferenceAuthor(ya,paos_ref,refno, conid)){
					conid++;/*5*/
				}
				else if(parseReferenceTag(ya,paos_ref,refno));/*9*/
				else if(parseReferenceName(ya,paos_ref,refno));/*8*/
				else if(parseReferenceText(ya,paos_ref,refno, sb));//adds only reference-parsed-text
				else if(parseReferenceArtifacts(ya,paos_ref,refno));/*3*/
				else lla.add(ya);
			}
			if(paos_ref.size()==1){//if reference-parsed-text is the only information we can parse it
				parseReferenceText(sb.toString(), paos_ref, refno);
			}
			
			if(lla.size()>0) System.out.println
				("Not used attributes in referenced articles metadata: "+lla);
			//if there is any information about this reference
			//in regulat tag: take it!
			//if the only piece of information is reference-text:
			//parse it and add
			if(paos_ref.size()>0) s_ref.setContinuations(paos_ref); 
//			else s_ref.setContinuations(paos_ref2);
			statements.add(s_ref);
			return true;
		}
		return false;
	}

	private boolean parseReferenceArtifacts(YAttribute ya,
			LinkedList<PredicateAndObject> paos_ref, int refno) {
		if(YConstants.AT_REFERENCE_PARSED_TITLE.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_TITLE, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_JOURNAL.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_JOURNAL, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_VOLUME.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_VOLUME, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_YEAR.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_YEAR, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_PAGES.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_PAGES, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_CITY.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_CITY, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_ISSUE.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_ISSUE, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_PUBLISHER.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_PUBLISHER, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_ID_ISSN.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_ISSN, RelConstants.NS_ISSN + ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_ID_ISBN.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_ISBN, RelConstants.NS_ISBN +  ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_ID_ZBL.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_IS_DOCUMENT, RelConstants.NS_ZBL_DOCUMENT + ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_MONTH.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_MONTH, ya.getValue()));
			return true;
		}else if(YConstants.AT_REFERENCE_PARSED_CHAPTER.equals(ya.getKey())){/*8*/
			paos_ref.add(new PredicateAndObject(RelConstants.RL_CHAPTER, ya.getValue()));
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
				String id = RelConstants.NS_TAG+"-"+in_item.getId()+"/r/"+refno+"/t/"+tagid;
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
			String contrId = RelConstants.NS_CONTRIBUTOR +in_item.getId().substring(0) +"/r"+refno+"/c"+contr; 
			paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_CONTRIBUTOR, contrId));
			Statements s = new Statements(); s.setSubject(contrId); statements.add(s);
			LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>(); 
			pao.add(new PredicateAndObject(RelConstants.RL_HAS_POSITION_IN_REFERENCE_DOCUMENT,contr+""));
			
			proceedReferenceAuthorZblFingerPrint(ya,pao);
			
			String[] names = getParseReferenceAuthorNames(ya, pao);
			
			String fname = names[0];
			String sname = names[1];
			String cname = names[2];
			String iname = names[3];
		
			if(cname != null && sname.equals("")){
				names = NameProceeder.extractNamesFromCanonical(cname);
				fname = names[0];
				sname = names[1];
				cname = names[2];
				iname = names[3];
			}else cname = fname+" "+sname;

			if(sname!=null && sname!=""){
				pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,fname));
				pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,sname));
				pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,cname));
				pao.add(new PredicateAndObject(RelConstants.RL_INITIALS,iname));
			}else{
				pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,""));
				pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,cname));
			}
			
			s.setContinuations(pao);
			contr++;
			
			return true;
		}else return false;
	}

	private void proceedReferenceAuthorZblFingerPrint(YAttribute ya,
			LinkedList<PredicateAndObject> pao) {
		for(YAttribute inner : ya.getAttributes()){
			if(YConstants.AT_ZBL_AUTHOR_FINGERPRINT.equals(inner.getKey()) && !"-".equals(inner.getValue())){/*5.1*/
				String zblfingerprint=inner.getValue();
				pao.add(new PredicateAndObject(RelConstants.RL_IS_PERSON, RelConstants.NS_ZBL_PERSON + zblfingerprint)); 
			}
		}
	}

	@SuppressWarnings({ "deprecation" })
	private String[] getParseReferenceAuthorNames(YAttribute ya,
			LinkedList<PredicateAndObject> pao) {
		String sname = "";
		String fname = "";
		String cname = null;
		String iname = "";
		
		for(YAttribute inner : ya.getAttributes()){
			if(YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES.equals(inner.getKey())){/*5.2*/
				String tmp = inner.getValue();
				fname+=" "+ tmp;
				for(String s : fname.split(" ")){
					//the condition "if(s.length()>0)" is always valid here, 
					//otherwise split f-tion would be defective,
					//hence  substring f-tion must work
					iname=iname+" "+tmp.substring(0,1);
				}
					
			}else if(YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME.equals(inner.getKey())){/*5.3*/
				sname=inner.getValue();
				sname = NameProceeder.unifySurname(sname);
			}
		}
		
		fname = NameProceeder.unifyForenames(fname);
		iname = NameProceeder.unifyInitials(iname);
		
		
		if(!ya.getValue().isEmpty()){//canonical name
			cname= NameProceeder.unifyCanonical(ya.getValue());
		}else cname = NameProceeder.unifyCanonical((fname+" "+sname).trim());
		
		
		String[] names = {fname,sname,cname,iname};
		return names;
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
	 * @param sb 
	 * @return
	 * @throws YaddaException 
	 */
	private boolean parseReferenceText(YAttribute ya, LinkedList<PredicateAndObject> paos_ref, int refno, StringBuffer sb) throws YaddaException {
		if(YConstants.AT_REFERENCE_TEXT.equals(ya.getKey())){/*3*/
			sb.append(ya.getValue().toString());
			paos_ref.add(new PredicateAndObject(RelConstants.RL_TEXT, ya.getValue().toString()));
			//further parsing of reference-parsed-text is done in parseReferences() method, 
			//hence parsing here is not allowed
			return true;
		}else return false;
	}

	/**
	 * transplantation form RelationStatementBuilder
	 * @param value
	 * @param paos_ref
	 * @param refno
	 * @throws YaddaException 
	 */
	private void parseReferenceText(String value, LinkedList<PredicateAndObject> paos_ref, int refno) throws YaddaException {
		if(PubParser.getCitationParser()!=null){
			try {
				YRelation parsed = PubParser.getCitationParser().parse(value);
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
						@SuppressWarnings("deprecation")
						String fname = contrib.getOneAttributeValue(YConstants.AT_REFERENCE_PARSED_AUTHOR_FORENAMES);
						fname = NameProceeder.unifyForenames(fname);	
						@SuppressWarnings("deprecation")
						String sname = contrib.getOneAttributeValue(YConstants.AT_REFERENCE_PARSED_AUTHOR_SURNAME);
						sname = NameProceeder.unifySurname(sname);
						
						String contrId = RelConstants.NS_CONTRIBUTOR+in_item.getId().substring(0)
						+"/r"+refno+"/c"+innerContrib; 
						
						paos_ref.add(new PredicateAndObject(RelConstants.RL_HAS_CONTRIBUTOR,
								contrId));
						
						Statements s = new Statements(); s.setSubject(contrId); statements.add(s);
						LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>(); 
						pao.add(new PredicateAndObject(RelConstants.RL_HAS_POSITION_IN_REFERENCE_DOCUMENT,innerContrib+""));
						pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,fname));
						pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,sname));
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
				s_desc.setSubject(RelConstants.NS_DESCRIPTION+in_item.getId()+"/"+descriptionid);
				LinkedList<PredicateAndObject> paos_description = new LinkedList<PredicateAndObject>();
				paos_description.add(new PredicateAndObject(RelConstants.RL_LANGUAGE, yd.getLanguage().getName()));
				paos_description.add(new PredicateAndObject(RelConstants.RL_TYPE, yd.getType()));
				paos_description.add(new PredicateAndObject(RelConstants.RL_TEXT, yd.getText()));				
				paos_doc.add(new PredicateAndObject(RelConstants.RL_HAS_DESCRIPTION , RelConstants.NS_DESCRIPTION+in_item.getId()+"/"+descriptionid));
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
				//Create Statements header for this contributor
				paos_doc.add(new PredicateAndObject(RelConstants.RL_HAS_CONTRIBUTOR, RelConstants.NS_CONTRIBUTOR+in_item.getId().substring(0)+"/c"+contr));
				Statements s = new Statements(); s.setSubject(RelConstants.NS_CONTRIBUTOR+ in_item.getId()+"/c"+contr); statements.add(s);
				LinkedList<PredicateAndObject> pao = new LinkedList<PredicateAndObject>(); 
				pao.add(new PredicateAndObject(RelConstants.RL_HAS_POSITION_IN_DOCUMENT,contr+""));
				//Create Statements body for this contributor
				ContributorProceeder.proceed(yc,pao, in_item);
				//Increment contribution counter, save data, go to next contribution
				contr++;
				s.setContinuations(pao);
			}
		}
		
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
				paos_doc.add(new PredicateAndObject(RelConstants.RL_CONTAINS_AFFILIATION, RelConstants.NS_AFFILIATION+in_item.getId().substring(0)+"/a"+aff));
				Statements s_affil = new Statements(); 
				s_affil.setSubject(RelConstants.NS_AFFILIATION+in_item.getId().substring(0)+"/a"+aff);
				LinkedList<PredicateAndObject> paos_affil = new LinkedList<PredicateAndObject>();
				 
				if(ya.getSimpleText()!=null && !ya.getSimpleText().isEmpty())	 
					paos_affil.add(new PredicateAndObject(RelConstants.RL_TEXT, ya.getSimpleText()));
				
				if(ya.getId().length()>0) paos_affil.add(new PredicateAndObject(RelConstants.RL_ID, ya.getId()));
				
//				if(ya.getIdentity()!=null && ya.getIdentity().length()!=0)
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
				id = RelConstants.NS_TAG+in_item.getId()+"/t"+tagid;
				paos_doc.add(new PredicateAndObject(RelConstants.RL_TAG, id));
				tagid++;
				Statements t_affil = new Statements(); 
				t_affil.setSubject(id);
				LinkedList<PredicateAndObject> paos_affil = new LinkedList<PredicateAndObject>();
//				paos_affil.add(new PredicateAndObject(RelConstants.RL_TYPE, tag.toLowerCase()));
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
					pao.add(new PredicateAndObject(RelConstants.RL_NAME,NameProceeder.unifyCanonical(name.getText())));
				else if(name.getType().equals("forenames")){
					pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,NameProceeder.unifyForenames(name.getText())));
				}
				else if(name.getType().equals("surname")){
					pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,NameProceeder.unifySurname(name.getText())));
				}					
			}
			s.setContinuations(pao);
			retL.add(s);
		}else;
		
		return retL;
	}
}
