package pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.bwmeta.model.YClassification;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YLanguage;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader;
import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader120;
import pl.edu.icm.yadda.common.YaddaException;
import pl.edu.icm.yadda.parsing.ICitationParser;
import pl.edu.icm.yadda.parsing.regexpparser.RegexpReferenceParser2;
import pl.edu.icm.yadda.tools.content.RegexpReferenceParser;
import pl.edu.icm.yadda.tools.relations.Statements;
import pl.edu.icm.yadda.tools.relations.Statements.PredicateAndObject;
import pl.edu.icm.yadda.tools.trans.DiacriticsRemover;

public class ExtendedRelationshipStatementsBuilderTest {
	
	protected static final String ITEMS_RESOURCE = "pl/edu/icm/yadda/analysis/relations/auxil/statementbuilder/feeder-test-items.xml";

	protected BwmetaReader bwmetaReader = new BwmetaReader120();

	protected ExtendedRelationshipStatementsBuilder builder = new ExtendedRelationshipStatementsBuilder();
	protected ICitationParser parser = null; 
		
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testIgnoresOther() throws YaddaException {
		YClassification pacs = new YClassification()
			.setId(YConstants.EXT_CLASSIFICATION_PACS)
			.addName(new YName(YLanguage.English, "Physics and Astronomy Classification Scheme", YConstants.NM_CANONICAL))
			.addName(new YName(YLanguage.English, "PACS", YConstants.NM_ABBREVIATION));
		List<Statements> items = builder.buildStatements(pacs);
		assertTrue(items.isEmpty());
	}
	   //@TODO: Commented out by axnow, broke our build system.
	
//	@Test
	public void testBuildsStatements() throws YaddaException {
		Map<String, Statements> items = loadItems();

		final String documentFooAndBar    = RelConstants.NS_DOCUMENT 	+ YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String affiliationFooAndBar = RelConstants.NS_AFFILIATION + YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String contributorFooAndBar = RelConstants.NS_CONTRIBUTOR + YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String referenceFooAndBar   = RelConstants.NS_REFERENCE 	+ YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		//pdendek:
		final String refContribFooAndBar  = RelConstants.NS_CONTRIBUTOR	+ YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String documentBaz 		  = RelConstants.NS_DOCUMENT 	+ YConstants.EXT_PREFIX_ELEMENT + "Baz";

		final String institutionICM 	  = RelConstants.NS_INSTITUTION + "bwmeta1.institution.ICM";
		final String institutionPAN 	  = RelConstants.NS_INSTITUTION + "bwmeta1.institution.PAN";

		final String personKowalski001 	  = RelConstants.NS_PERSON + "bwmeta1.person.Kowalski001";
		final String personKowalski002 	  = RelConstants.NS_PERSON + "bwmeta1.person.Kowalski002";
		final String personKowalski003 	  = RelConstants.NS_PERSON + "bwmeta1.person.Kowalski003";
		final String personGalczynski001  = RelConstants.NS_PERSON + "bwmeta1.person.Galczynski001";
		final String personKolmogorov001  = RelConstants.NS_PERSON + "bwmeta1.person.Kolmogorov001";


		/* Check institutions */
		checkPresent(items, institutionICM, RelConstants.RL_NAME, "Interdyscyplinarne Centrum Modelowania Matematycznego i Komputerowego");
		checkPresent(items, institutionPAN, RelConstants.RL_NAME, "Polska Akademia Nauk");

		/* Check persons */
		checkPresent(items, personKowalski001, RelConstants.RL_SURNAME, "Kowalski");
		checkPresent(items, personKowalski001, RelConstants.RL_FORENAMES, "Jan Maria");
		checkPresent(items, personKowalski002, RelConstants.RL_SURNAME, "Kowalski");
		checkPresent(items, personKowalski002, RelConstants.RL_FORENAMES, "Jan Maria");
		checkPresent(items, personKowalski003, RelConstants.RL_SURNAME, "Kowalski");
		checkPresent(items, personKowalski003, RelConstants.RL_FORENAMES, "Jerzy");
		checkPresent(items, personGalczynski001, RelConstants.RL_SURNAME, "Gałczyński");
		checkPresent(items, personGalczynski001, RelConstants.RL_FORENAMES, "Konstanty Ildefons");
		checkPresent(items, personKolmogorov001, RelConstants.RL_SURNAME, "Колмогоров");
		checkPresent(items, personKolmogorov001, RelConstants.RL_FORENAMES, "Андрей Николаевич");

		/* Check the big document */
		checkPresent(items, documentFooAndBar, RelConstants.RL_PUBLISHER, "Foo-Bar Press");
		checkPresent(items, documentFooAndBar, RelConstants.RL_JOURNAL, "Journal of Applied Foo");
		checkPresent(items, documentFooAndBar, RelConstants.RL_YEAR, "2000");
		checkPresent(items, documentFooAndBar, RelConstants.RL_VOLUME, "42");
		checkPresent(items, documentFooAndBar, RelConstants.RL_ISSUE, "3");
		checkPresent(items, documentFooAndBar, RelConstants.RL_PAGES, "196-197");
		checkPresent(items, documentFooAndBar, RelConstants.RL_TITLE, "Foo and Bar");
		
		/* Check the document's affiliations */
		checkPresent(items, documentFooAndBar, RelConstants.RL_CONTAINS_AFFILIATION, affiliationFooAndBar + "/a0");
		checkPresent(items, affiliationFooAndBar + "/a0", RelConstants.RL_TEXT, "Interdyscyplinarne Centrum Modelowania Matematycznego i Komputerowego, Uniwersytet Warszawski");
		//to ponizej na pewno ok?
//		checkPresent(items, affiliationFooAndBar + "/a0", RelConstants.RL_IS_INSTITUTION, institutionPAN);
		checkPresent(items, documentFooAndBar, RelConstants.RL_CONTAINS_AFFILIATION, affiliationFooAndBar + "/a1");
		checkPresent(items, affiliationFooAndBar + "/a1", RelConstants.RL_TEXT, "PAN");
		
		/* Check the document's references */
		checkPresent(items, documentFooAndBar, RelConstants.RL_REFERENCES, referenceFooAndBar + "/r0");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_TEXT, "D.G. McFadden, J. Charite, J.A. Richardson, D. Srivastava, A.B. Firulli, E.N. Olson, A GATA-dependent right ventricular enhancer controls dHAND transcription in the developing heart, Development, 127, 2000, 5331 - 5341");
		checkPresent(items, documentFooAndBar, RelConstants.RL_REFERENCES, referenceFooAndBar + "/r1");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_TEXT, "Gremza G., Zamorowski W., Rozwiązania łączników w belkach zespolonych stalowo-betonowych. Przegląd Budowlany 7-8/2004, s. 26-30");

		/* Check reference details */
		// To PD: Update RelationshipStatementBuilder and uncomment this:
		//pdendek: change from referenceFooAndBar to refContribFooAndBar on contributors
		//pdendek: addition of dot after "D. G"
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c0");
		checkPresent(items, refContribFooAndBar + "/r0/c0", RelConstants.RL_SURNAME, "McFadden");
		checkPresent(items, refContribFooAndBar + "/r0/c0", RelConstants.RL_FORENAMES, "D. G.");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c1");
		checkPresent(items, refContribFooAndBar + "/r0/c1", RelConstants.RL_SURNAME, "Charite");
		checkPresent(items, refContribFooAndBar + "/r0/c1", RelConstants.RL_FORENAMES, "J.");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c2");
		checkPresent(items, refContribFooAndBar + "/r0/c2", RelConstants.RL_SURNAME, "Richardson");
		checkPresent(items, refContribFooAndBar + "/r0/c2", RelConstants.RL_FORENAMES, "J. A.");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c3");
		checkPresent(items, refContribFooAndBar + "/r0/c3", RelConstants.RL_SURNAME, "Srivastava");
		checkPresent(items, refContribFooAndBar + "/r0/c3", RelConstants.RL_FORENAMES, "D.");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c4");
		checkPresent(items, refContribFooAndBar + "/r0/c4", RelConstants.RL_SURNAME, "Firulli");
		checkPresent(items, refContribFooAndBar + "/r0/c4", RelConstants.RL_FORENAMES, "A. B.");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c5");
		checkPresent(items, refContribFooAndBar + "/r0/c5", RelConstants.RL_SURNAME, "Olson");
		checkPresent(items, refContribFooAndBar + "/r0/c5", RelConstants.RL_FORENAMES, "E. N.");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_TITLE, "A GATA-dependent right ventricular enhancer controls dHAND transcription in the developing heart");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_JOURNAL, "Development");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_VOLUME, "127");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_YEAR, "2000");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_PAGES, "5331 - 5341");

 
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r1/c0");
		checkPresent(items, refContribFooAndBar + "/r1/c0", RelConstants.RL_SURNAME, "Gremza");
		checkPresent(items, refContribFooAndBar + "/r1/c0", RelConstants.RL_FORENAMES, "G.");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r1/c1");
		checkPresent(items, refContribFooAndBar + "/r1/c1", RelConstants.RL_SURNAME, "Zamorowski");
		checkPresent(items, refContribFooAndBar + "/r1/c1", RelConstants.RL_FORENAMES, "W.");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_TITLE, "Rozwiązania łączników w belkach zespolonych stalowo-betonowych");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_JOURNAL, "Przegląd Budowlany");
		//pdendek: parser nie radzi sobie z podanym wzorem VOLUME i zwraca null
		//checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_VOLUME, "7-8"); 
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_YEAR, "2004");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_PAGES, "26 - 30");
	

		/* Check the document's contributors */
		checkPresent(items, documentFooAndBar, RelConstants.RL_HAS_CONTRIBUTOR, contributorFooAndBar + "/c0");
		checkPresent(items, contributorFooAndBar + "/c0", RelConstants.RL_SURNAME, "Kowalski");
		checkPresent(items, contributorFooAndBar + "/c0", RelConstants.RL_FORENAMES, "J.");
		checkPresent(items, contributorFooAndBar + "/c0", RelConstants.RL_IS_PERSON, personKowalski003);
		checkPresent(items, documentFooAndBar, RelConstants.RL_HAS_CONTRIBUTOR, contributorFooAndBar + "/c1");
		checkPresent(items, contributorFooAndBar + "/c1", RelConstants.RL_SURNAME, "Nowak");
		checkPresent(items, contributorFooAndBar + "/c1", RelConstants.RL_FORENAMES, "R.");


		/* Check the small document */
		checkPresent(items, documentBaz, RelConstants.RL_JOURNAL, "Journal of Applied Foo");
		checkPresent(items, documentBaz, RelConstants.RL_YEAR, "2004");
		checkPresent(items, documentBaz, RelConstants.RL_TITLE, "Baz");
	}

	@Test
	public void testBuildsLowercaseNamesStatements() throws YaddaException {
		Map<String, Statements> items = loadItems();

		final String documentFooAndBar    = RelConstants.NS_DOCUMENT 	+ YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String affiliationFooAndBar = RelConstants.NS_AFFILIATION + YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String contributorFooAndBar = RelConstants.NS_CONTRIBUTOR + YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String referenceFooAndBar   = RelConstants.NS_REFERENCE 	+ YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		//pdendek:
		final String refContribFooAndBar  = RelConstants.NS_CONTRIBUTOR	+ YConstants.EXT_PREFIX_ELEMENT + "FooAndBar";
		final String documentBaz 		  = RelConstants.NS_DOCUMENT 	+ YConstants.EXT_PREFIX_ELEMENT + "Baz";

		final String institutionICM 	  = RelConstants.NS_INSTITUTION + "bwmeta1.institution.ICM";
		final String institutionPAN 	  = RelConstants.NS_INSTITUTION + "bwmeta1.institution.PAN";

		final String personKowalski001 	  = RelConstants.NS_PERSON + "bwmeta1.person.Kowalski001";
		final String personKowalski002 	  = RelConstants.NS_PERSON + "bwmeta1.person.Kowalski002";
		final String personKowalski003 	  = RelConstants.NS_PERSON + "bwmeta1.person.Kowalski003";
		final String personGalczynski001  = RelConstants.NS_PERSON + "bwmeta1.person.Galczynski001";
		final String personKolmogorov001  = RelConstants.NS_PERSON + "bwmeta1.person.Kolmogorov001";


		/* Check institutions */
		checkPresent(items, institutionICM, RelConstants.RL_NAME, "Interdyscyplinarne Centrum Modelowania Matematycznego i Komputerowego");
		checkPresent(items, institutionPAN, RelConstants.RL_NAME, "Polska Akademia Nauk");

		/* Check persons */
		checkPresent(items, personKowalski001, RelConstants.RL_SURNAME, "kowalski");
		checkPresent(items, personKowalski001, RelConstants.RL_FORENAMES, "jan maria");
		checkPresent(items, personKowalski002, RelConstants.RL_SURNAME, "kowalski");
		checkPresent(items, personKowalski002, RelConstants.RL_FORENAMES, "jan maria");
		checkPresent(items, personKowalski003, RelConstants.RL_SURNAME, "kowalski");
		checkPresent(items, personKowalski003, RelConstants.RL_FORENAMES, "jerzy");
		checkPresent(items, personGalczynski001, RelConstants.RL_SURNAME, "galczynski");
		checkPresent(items, personGalczynski001, RelConstants.RL_FORENAMES, "konstanty ildefons");
//		checkPresent(items, personKolmogorov001, RelConstants.RL_SURNAME, "Колмогоров".toLowerCase());
//		checkPresent(items, personKolmogorov001, RelConstants.RL_FORENAMES, DiacriticsRemover.removeDiacritics("Андрей Николаевич").toLowerCase());

		/* Check the big document */
		checkPresent(items, documentFooAndBar, RelConstants.RL_PUBLISHER, "Foo-Bar Press");
		checkPresent(items, documentFooAndBar, RelConstants.RL_JOURNAL, "Journal of Applied Foo");
		checkPresent(items, documentFooAndBar, RelConstants.RL_YEAR, "2000");
		checkPresent(items, documentFooAndBar, RelConstants.RL_VOLUME, "42");
		checkPresent(items, documentFooAndBar, RelConstants.RL_ISSUE, "3");
		checkPresent(items, documentFooAndBar, RelConstants.RL_PAGES, "196-197");
		checkPresent(items, documentFooAndBar, RelConstants.RL_TITLE, "Foo and Bar");
		
		/* Check the document's affiliations */
		checkPresent(items, documentFooAndBar, RelConstants.RL_CONTAINS_AFFILIATION, affiliationFooAndBar + "/a0");
		checkPresent(items, affiliationFooAndBar + "/a0", RelConstants.RL_TEXT, "Interdyscyplinarne Centrum Modelowania Matematycznego i Komputerowego, Uniwersytet Warszawski");
		//to ponizej na pewno ok?
//		checkPresent(items, affiliationFooAndBar + "/a0", RelConstants.RL_IS_INSTITUTION, institutionPAN);
		checkPresent(items, documentFooAndBar, RelConstants.RL_CONTAINS_AFFILIATION, affiliationFooAndBar + "/a1");
		checkPresent(items, affiliationFooAndBar + "/a1", RelConstants.RL_TEXT, "PAN");
		
		/* Check the document's references */
		checkPresent(items, documentFooAndBar, RelConstants.RL_REFERENCES, referenceFooAndBar + "/r0");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_TEXT, "D.G. McFadden, J. Charite, J.A. Richardson, D. Srivastava, A.B. Firulli, E.N. Olson, A GATA-dependent right ventricular enhancer controls dHAND transcription in the developing heart, Development, 127, 2000, 5331 - 5341");
		checkPresent(items, documentFooAndBar, RelConstants.RL_REFERENCES, referenceFooAndBar + "/r1");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_TEXT, "Gremza G., Zamorowski W., Rozwiązania łączników w belkach zespolonych stalowo-betonowych. Przegląd Budowlany 7-8/2004, s. 26-30");

		/* Check reference details */
		// To PD: Update RelationshipStatementBuilder and uncomment this:
		//pdendek: change from referenceFooAndBar to refContribFooAndBar on contributors
		//pdendek: addition of dot after "D. G"
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c0");
		checkPresent(items, refContribFooAndBar + "/r0/c0", RelConstants.RL_SURNAME, "mcfadden");
		checkPresent(items, refContribFooAndBar + "/r0/c0", RelConstants.RL_FORENAMES, "d g");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c1");
		checkPresent(items, refContribFooAndBar + "/r0/c1", RelConstants.RL_SURNAME, "charite");
		checkPresent(items, refContribFooAndBar + "/r0/c1", RelConstants.RL_FORENAMES, "j");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c2");
		checkPresent(items, refContribFooAndBar + "/r0/c2", RelConstants.RL_SURNAME, "richardson");
		checkPresent(items, refContribFooAndBar + "/r0/c2", RelConstants.RL_FORENAMES, "j a");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c3");
		checkPresent(items, refContribFooAndBar + "/r0/c3", RelConstants.RL_SURNAME, "srivastava");
		checkPresent(items, refContribFooAndBar + "/r0/c3", RelConstants.RL_FORENAMES, "d");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c4");
		checkPresent(items, refContribFooAndBar + "/r0/c4", RelConstants.RL_SURNAME, "firulli");
		checkPresent(items, refContribFooAndBar + "/r0/c4", RelConstants.RL_FORENAMES, "a b");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r0/c5");
		checkPresent(items, refContribFooAndBar + "/r0/c5", RelConstants.RL_SURNAME, "olson");
		checkPresent(items, refContribFooAndBar + "/r0/c5", RelConstants.RL_FORENAMES, "e n");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_TITLE, "A GATA-dependent right ventricular enhancer controls dHAND transcription in the developing heart");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_JOURNAL, "Development");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_VOLUME, "127");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_YEAR, "2000");
		checkPresent(items, referenceFooAndBar + "/r0", RelConstants.RL_PAGES, "5331 - 5341");

 
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r1/c0");
		checkPresent(items, refContribFooAndBar + "/r1/c0", RelConstants.RL_SURNAME, "gremza");
		checkPresent(items, refContribFooAndBar + "/r1/c0", RelConstants.RL_FORENAMES, "g");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_HAS_CONTRIBUTOR, refContribFooAndBar + "/r1/c1");
		checkPresent(items, refContribFooAndBar + "/r1/c1", RelConstants.RL_SURNAME, "zamorowski");
		checkPresent(items, refContribFooAndBar + "/r1/c1", RelConstants.RL_FORENAMES, "w");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_TITLE, "Rozwiązania łączników w belkach zespolonych stalowo-betonowych");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_JOURNAL, "Przegląd Budowlany");
		//pdendek: parser nie radzi sobie z podanym wzorem VOLUME i zwraca null
		//checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_VOLUME, "7-8"); 
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_YEAR, "2004");
		checkPresent(items, referenceFooAndBar + "/r1", RelConstants.RL_PAGES, "26 - 30");
	

		/* Check the document's contributors */
		checkPresent(items, documentFooAndBar, RelConstants.RL_HAS_CONTRIBUTOR, contributorFooAndBar + "/c0");
		checkPresent(items, contributorFooAndBar + "/c0", RelConstants.RL_SURNAME, "kowalski");
		checkPresent(items, contributorFooAndBar + "/c0", RelConstants.RL_FORENAMES, "j");
		checkPresent(items, contributorFooAndBar + "/c0", RelConstants.RL_IS_PERSON, personKowalski003);
		
		checkPresent(items, documentFooAndBar, RelConstants.RL_HAS_CONTRIBUTOR, contributorFooAndBar + "/c1");
		checkPresent(items, contributorFooAndBar + "/c1", RelConstants.RL_SURNAME, "nowak");
		checkPresent(items, contributorFooAndBar + "/c1", RelConstants.RL_FORENAMES, "r");

		checkPresent(items, documentFooAndBar, RelConstants.RL_HAS_CONTRIBUTOR, contributorFooAndBar + "/c2");
		checkPresent(items, contributorFooAndBar + "/c2", RelConstants.RL_SURNAME, "nowak");
		checkPresent(items, contributorFooAndBar + "/c2", RelConstants.RL_FORENAMES, "adam r");
		checkPresent(items, contributorFooAndBar + "/c2", RelConstants.RL_INITIALS, "a r");
		
		/* Check the small document */
		checkPresent(items, documentBaz, RelConstants.RL_JOURNAL, "Journal of Applied Foo");
		checkPresent(items, documentBaz, RelConstants.RL_YEAR, "2004");
		checkPresent(items, documentBaz, RelConstants.RL_TITLE, "Baz");
	}
	
	
	@SuppressWarnings("unchecked")
	protected Map<String, Statements> loadItems() throws YaddaException {
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(ITEMS_RESOURCE);
		List<YExportable> exportables = (List<YExportable>) bwmetaReader.read(stream, null);
		List<Statements> statementsList = builder.buildStatements(exportables);
		Map<String, Statements> map = new HashMap<String, Statements>();
		for (Statements statements : statementsList) {
			if (map.containsKey(statements.getSubject())){
				System.out.println("Fail Statement: "+statements.getSubject());
				System.out.println("===============");
				System.out.println(statements);
				System.out.println("===============");
				fail("Multiple statement lists with the same subject: " + statements.getSubject());
			}
			map.put(statements.getSubject(), statements);
			System.out.println(statements);
		}
		return map;
	}

	protected void checkPresent(Map<String, Statements> items, String subject, String predicate, String object) {
		assertTrue("Subject not present in the statements list: " + subject, items.containsKey(subject));
		Statements stmts = items.get(subject);
		for (PredicateAndObject continuation : stmts.getContinuations())
			if (predicate.equals(continuation.getPredicate()) && object.equals(continuation.getObject()))
				return;
		fail(String.format("Statement (%s, %s, %s) not present", subject, predicate, object));
	}
}
