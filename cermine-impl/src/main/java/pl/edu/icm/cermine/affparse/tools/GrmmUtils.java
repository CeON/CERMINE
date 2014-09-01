package pl.edu.icm.cermine.affparse.tools;

import java.util.List;

import pl.edu.icm.cermine.affparse.model.Label;
import pl.edu.icm.cermine.affparse.model.Token;

public class GrmmUtils {
	
	private static final String DUMMY_LABEL = "TEXT";
	private static final String LABEL_SEPARATOR = " ---- ";
	private static final String SEPARATOR = " ";
	
	public static <L extends Label, T extends Token<L>> String toGrmmInput(T token) {
		StringBuilder builder = new StringBuilder();
		builder.append(DUMMY_LABEL);
		builder.append(LABEL_SEPARATOR);
		List<String> features = token.getFeatures();
		for (int i = 0; i < features.size(); i++) {
			builder.append(features.get(i));
			if (i + 1 < features.size()) {
				builder.append(SEPARATOR);
			}
		}
		return builder.toString();
	}
}
