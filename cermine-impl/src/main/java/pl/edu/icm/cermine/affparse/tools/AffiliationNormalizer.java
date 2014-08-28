package pl.edu.icm.cermine.affparse.tools;

import java.text.Normalizer;

public class AffiliationNormalizer {
	public static String normalize(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFKD);
	}
}
