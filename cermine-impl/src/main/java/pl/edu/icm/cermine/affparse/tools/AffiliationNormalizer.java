package pl.edu.icm.cermine.affparse.tools;

import java.text.Normalizer;

public class AffiliationNormalizer {
	public static String normalize(String text) {
		String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKD);
		return normalizedText.replaceAll("—", " "); // Replace EM DASHEs with spaces
	}
}
