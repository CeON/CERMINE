package pl.edu.icm.cermine.metadata.affiliations.tools;

import pl.edu.icm.cermine.parsing.tools.TextNormalizer;

public class AffiliationNormalizer extends TextNormalizer {
	public String normalize(String text) {
		String normalizedText = java.text.Normalizer.normalize(text,
				java.text.Normalizer.Form.NFKD);
		return normalizedText.replaceAll("—", " "); // Replace EM DASHEs with spaces
	}
}
