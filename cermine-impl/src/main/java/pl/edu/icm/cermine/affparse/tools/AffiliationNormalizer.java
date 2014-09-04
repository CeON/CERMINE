package pl.edu.icm.cermine.affparse.tools;

public class AffiliationNormalizer extends Normalizer {
	public String normalize(String text) {
		String normalizedText = java.text.Normalizer.normalize(text,
				java.text.Normalizer.Form.NFKD);
		return normalizedText.replaceAll("—", " "); // Replace EM DASHEs with spaces
	}
}
