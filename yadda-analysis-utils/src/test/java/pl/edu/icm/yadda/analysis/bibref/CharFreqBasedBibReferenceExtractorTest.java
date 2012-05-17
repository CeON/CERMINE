package pl.edu.icm.yadda.analysis.bibref;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;


public class CharFreqBasedBibReferenceExtractorTest {
	String DOC01 = "pl/edu/icm/yadda/analysis/bibref/doc01.txt";
	String DOC02 = "pl/edu/icm/yadda/analysis/bibref/doc02.txt";
	String DOC06 = "pl/edu/icm/yadda/analysis/bibref/doc06.txt";

	protected String readContents(String resource) throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = r.readLine();
		while (line != null) {
			sb.append(line).append('\n');
			line = r.readLine();
		}
		r.close();
		return sb.toString();
	}
	
	@Test
	public void testDoc01() throws Exception {
		String text = readContents(DOC01);
		IBibReferenceExtractor bre = new CharFreqBasedBibReferenceExtractor();
		String[] refs = bre.extractBibReferences(text);
		
		assertEquals(29, refs.length);
		assertEquals("[1] M. Alexa Wiener filtering of meshes Proc. Shape Modeling International 2002 IEEE Computer Society Washington, DC, USA 51 57", refs[0]);
		assertEquals("[29] S. Fleishman D. Cohen-Or C.T. Silva Robust moving least-squares fitting with sharp features ACM Trans. Graph. 24 3 2005 544 552", refs[28]);
	}

	@Test
	public void testDoc02() throws Exception {
		String text = readContents(DOC02);
		IBibReferenceExtractor bre = new CharFreqBasedBibReferenceExtractor();
		String[] refs = bre.extractBibReferences(text);
		
		assertEquals(29, refs.length);
		assertEquals("[1] M. Alexa Wiener filtering of meshes Proc. Shape Modeling International 2002 IEEE Computer Society Washington, DC, USA 51 57", refs[0]);
		assertEquals("[29] S. Fleishman D. Cohen-Or C.T. Silva Robust moving least-squares fitting with sharp features ACM Trans. Graph. 24 3 2005 544 552", refs[28]);
	}

	@Test
	public void testDoc06() throws Exception {
		String text = readContents(DOC06);
		IBibReferenceExtractor bre = new CharFreqBasedBibReferenceExtractor();
		String[] refs = bre.extractBibReferences(text);
		
		assertEquals(221, refs.length);
		assertEquals("1 Sears MR. The definition and diagnosis of asthma. Allergy 1993; 48: 12-6.", refs[0]);
		assertEquals("221 Meren M, Jannus-Pruljan L, Loit H-M, Põlluste J, Jönsson E, Kiviloog J, Lundback B. Asthma, chronic bronchitis and respiratory symptoms among adults in Estonia according to a postal questionnaire. Respir Med 2001; in press.", refs[220]);
	}

	@Test
	public void testSmear() {
		CharFreqBasedBibReferenceExtractor sbe = new CharFreqBasedBibReferenceExtractor();
		boolean[] tab = new boolean[] { false, true, false, false, true, false, true, false, false };
		sbe.smear(tab, true, true, 1);
		assertBAE(new boolean[] { true, true, true, false, true, true, true, true, false}, tab);
		sbe.smear(tab, false, false, 2);
		assertBAE(new boolean[] { true, false, false, false, true, true, false, false, false}, tab);
		sbe.smear(tab, true, true, 1);
		assertBAE(new boolean[] { true, true, false, false, true, true, true, false, false}, tab);
	}
	
	/**
	 * Asserts that two boolean arrays are equal.
	 * 
	 * @param t1 first boolean array
	 * @param t2 second boolean array
	 */
	protected void assertBAE(boolean[] t1, boolean[] t2) {
		assertEquals("length", t1.length, t2.length);
		for (int i = 0; i < t1.length; i++)
			assertEquals("position " + i, t1[i], t2[i]);
	}
}
