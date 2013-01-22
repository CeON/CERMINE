package pl.edu.icm.cermine.evaluation.tools;

import java.util.List;

import org.apache.lucene.analysis.Tokenizer;

public class DateComparator {
	
	public static Boolean yearsMatch(List<String> expected, List<String> extracted) {
		for(String expectedDate: expected) {
			List<String> expectedParts = StringTools.tokenize(expectedDate);
			String expectedYear = null;
			for(String part: expectedParts) {
				if(part.length() == 4 && Integer.parseInt(part) < 2100 && Integer.parseInt(part) > 1900) {
					expectedYear = part;
					break;
				}
			}
			if(expectedYear == null) {
				return null;
			} else {
				String extractedYear = null;
				for(String extractedDate: extracted) {
					List<String> extractedParts = StringTools.tokenize(extractedDate);
					for(String part: extractedParts) {
						if(part.length() == 4 && Integer.parseInt(part) < 2100 && Integer.parseInt(part) > 1900) {
							extractedYear = part;
							break;
						}
					}
					if(extractedYear.equals(expectedYear)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Boolean datesMatch(List<String> expected, List<String> extracted) {
		Boolean anyExpectedOk = false;
		for(String expectedDate: expected) {
			List<String> expectedParts = StringTools.tokenize(expectedDate);
			if(expectedParts.size() == 1) {
				continue;
			}
			anyExpectedOk = true;

			for(String extractedDate: extracted) {
				List<String> extractedParts = StringTools.tokenize(extractedDate);
				if(extractedParts.size() == 1) {
					continue;
				}
				if(new CosineDistance().compare(expectedParts, extractedParts)  > 0.95) {
					return true;
				}
			}
		}
		if(!anyExpectedOk) {
			return null;
		}
		return false;
	}
}

