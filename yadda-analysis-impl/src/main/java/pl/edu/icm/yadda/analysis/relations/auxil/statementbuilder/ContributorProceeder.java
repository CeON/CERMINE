package pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.tools.relations.Statements.PredicateAndObject;

public class ContributorProceeder {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ContributorProceeder.class);
	
	
	static void proceed(YContributor yc, LinkedList<PredicateAndObject> pao, YElement in_item) {
		proceedEmail(yc,pao);
		proceedZblFingerPrint(yc,pao);
		proceedRole(yc,pao);
		proceedIdentity(yc,pao);				
		proceedNames(yc,pao);
		proceedInstitutions(yc,pao);
		proceedAffiliationRefs(yc,pao, in_item);
	}

	private static void proceedEmail(YContributor yc,
			LinkedList<PredicateAndObject> pao) {
		for(YAttribute ya : yc.getAttributes(YConstants.AT_CONTACT_EMAIL))
			pao.add(new PredicateAndObject(RelConstants.RL_CONTACT_EMAIL,ya.getValue()));
	}

	private static void proceedZblFingerPrint(YContributor yc,
			LinkedList<PredicateAndObject> pao) {
		//FIXME getOneAttributeSimpleValue może zwracać null; 
		//trzeba sprawdzić czy takie sytuacje się jescze zdarzają 
		//(czy istnieje ryzyko null pointer exceptiona)
		String zblfingerprint = yc.getOneAttributeSimpleValue(YConstants.AT_ZBL_AUTHOR_FINGERPRINT);
		if(zblfingerprint!=null && !zblfingerprint.isEmpty()){
			pao.add(new PredicateAndObject(RelConstants.RL_IS_PERSON, RelConstants.NS_ZBL_PERSON + zblfingerprint));
		}
	}

	private static void proceedIdentity(YContributor yc,
			LinkedList<PredicateAndObject> pao) {
		if(!yc.getIdentity().isEmpty()) 
			pao.add(new PredicateAndObject(RelConstants.RL_IS_PERSON,RelConstants.NS_PERSON+yc.getIdentity()));
	}

	private static void proceedRole(YContributor yc, LinkedList<PredicateAndObject> pao) {
		if(!yc.getRole().isEmpty())
			pao.add(new PredicateAndObject(RelConstants.RL_HAS_ROLE,yc.getRole()));
	}

	private static void proceedAffiliationRefs(YContributor yc,
			LinkedList<PredicateAndObject> pao, YElement in_item) {
		if(!yc.getAffiliationRefs().isEmpty())
			for(String ya : yc.getAffiliationRefs())
				pao.add(new PredicateAndObject(RelConstants.RL_IS_AFFILIATED_WITH_ID,ya));
				
	}

	private static void proceedInstitutions(YContributor yc,
			LinkedList<PredicateAndObject> pao) {
		if(yc.isInstitution()){
            if (yc.getIdentity()!=null & ! yc.getIdentity().isEmpty())
            	pao.add(new PredicateAndObject(RelConstants.RL_IS_INSTITUTION,RelConstants.NS_INSTITUTION+yc.getIdentity()));
		}
	}

	/**
	 * 
	 * @param yc
	 * @return String array of size 3; 
	 * 0 contains forenames, 
	 * 1 contains surname, 
	 * 2 contains canonical  
	 */
	private static String[] getContributorNames(YContributor yc) {
		String sname = "";
		String fname = "";
		String cname = null;
		String iname = "";
		
		for(YName yn : yc.getNames()){
			if("canonical".equals(yn.getType())){
				cname = yn.getText().trim();
			}else if("forename".equals(yn.getType())){
				fname = fname + " " + yn.getText();
				for(String s : yn.getText().split(" "))
					//the condition "if(s.length()>0): is always valid here, 
					//otherwise split f-tion would be defective
						iname = iname + " " + s.substring(0,1);
				
			}else if("forenames".equals(yn.getType())){
				fname = fname + " " + yn.getText();
				for(String s : yn.getText().split(" "))
					//the condition "if(s.length()>0): is always valid here, 
					//otherwise split f-tion would be defective
						iname = iname + " " + s.substring(0,1);
				
			}else if("surname".equals(yn.getType())){
				sname = yn.getText().trim();
				sname = NameProceeder.unifySurname(sname);
			}
		}
		fname = NameProceeder.unifyForenames(fname);
		iname = NameProceeder.unifyInitials(iname);
		String[] ret = {fname,sname,cname,iname};
		return ret;
	}
	
	private static void proceedNames(YContributor yc, LinkedList<PredicateAndObject> pao) {
		if(!yc.getNames().isEmpty()){
			String[] names = getContributorNames(yc);
			String fname = names[0];
			String sname = names[1];
			String cname = names[2];
			String iname = names[3];
			
			if(cname == null) pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,(fname+" "+sname).trim()));
			else if(sname.equals("")){
				names = NameProceeder.extractNamesFromCanonical(cname);
				fname = names[0];
				sname = names[1];
				cname = names[2];
				iname = names[3];
			}
			//if a surname had been sucessfully obtained, e.g. from the parsing method
			//data about contribution will be added.
			//FIXME we should avoid adding useless data with nameless contributors 
			if(sname!=null && sname!=""){ 
				pao.add(new PredicateAndObject(RelConstants.RL_FORENAMES,fname));
				pao.add(new PredicateAndObject(RelConstants.RL_INITIALS,iname));
				pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,sname));
				pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,cname));
			}else{
				pao.add(new PredicateAndObject(RelConstants.RL_SURNAME,""));
				pao.add(new PredicateAndObject(RelConstants.RL_CANONICAL_NAME,cname));
			}
		}
	}



}
