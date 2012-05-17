package pl.edu.icm.yadda.analysis.bibref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bibliographic reference extractor utilizing a simple character frequency heuristic.
 * First, lines with a sufficiently high frequency of digits and punctuation are selected.
 * Next, isolated lines are removed and gaps filled.
 * Finally, the result (lines of the text that probably contain references)
 * is concatenated and references split.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class CharFreqBasedBibReferenceExtractor implements IBibReferenceExtractor {

	protected int minLineLength = 10;
	protected int maxLineLength = 120;
	protected String refCharPattern = "[0123456789()\\[\\].,:;-]";
	protected double refCharRatioThreshold = 0.15;
	protected int minRefChars = 3;
	protected int maxGapLength = 5;
	protected int minBlockLength = 10;
	protected int minRefLength = 20;
	protected double shortLineFrac = 0.6;
	protected double prefixRefsRatio = 0.3;

	@Override
	public String[] extractBibReferences(String text) {
		List<String> lineList = new ArrayList<String>();
		for (String line : text.split("\n")) {
			boolean loop = (line.length() > maxLineLength);
			while (loop) {
				int pos = line.indexOf(' ', maxLineLength);
				if (pos == -1)
					loop = false;
				else {
					lineList.add(line.substring(0, pos).trim());
					line = line.substring(pos + 1);
					loop = (line.length() > maxLineLength);
				}
			}
			if (line.trim().length() > 0)
				lineList.add(line.trim());
		}
		
		String[] lines = lineList.toArray(new String[] { });
		int length = lines.length;
		
		boolean lotsOfRefChars[] = new boolean[length];
		double[] tmp = new double[length];
		for (int i = 0; i < length; i++) {
			String line = lines[i].trim();
			if (line.length() == 0)
				continue;
			int allChars = line.length();
			int notRefChars = line.replaceAll(refCharPattern, "").length();
			int count = (allChars - notRefChars);
			double ratio = 1.0 * count / allChars;
			tmp[i] = ratio;
			lotsOfRefChars[i] = (ratio > refCharRatioThreshold) && (count >= minRefChars);
		}

		smear(lotsOfRefChars, true, false, maxGapLength);
		smear(lotsOfRefChars, false, true, 2*maxGapLength);
		smear(lotsOfRefChars, true, false, maxGapLength);

		smear(lotsOfRefChars, false, true, minBlockLength);
		smear(lotsOfRefChars, true, false, 2*minBlockLength);
		smear(lotsOfRefChars, false, true, minBlockLength);

		smear(lotsOfRefChars, true, false, 1);

		// debugMarks(lines, lotsOfRefChars);

		markLastRun(lotsOfRefChars);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length; i++)
			if (lotsOfRefChars[i])
				sb.append(lines[i]).append(" ");
		String concatenated = sb.toString();
		
		String[] squareBracketRefs = findOrderedRefs(concatenated, "[", "]");
		String[] parenthesesRefs = findOrderedRefs(concatenated, "(", ")");
		String[] dotRefs = findOrderedRefs(concatenated, "", ".");
		String[] spaceRefs = findOrderedRefs(concatenated, "", " ");
		String[] otherRefs = findPatternRefs(concatenated, Pattern.compile("\\[[a-zA-Z]+ ?\\d*\\]"));
		
		String[] best = new String[] { };
		if (squareBracketRefs.length > best.length)
			best = squareBracketRefs;
		if (parenthesesRefs.length > best.length)
			best = parenthesesRefs;
		if (dotRefs.length > best.length)
			best = dotRefs;
		if (spaceRefs.length > best.length)
			best = spaceRefs;
		if (0.5 * otherRefs.length > best.length)
			best = otherRefs;
		
		if (1.0 * maxLineLength * best.length < prefixRefsRatio * concatenated.length()) {
			String[] noPrefixRefs = findNoPrefixRefs(lines, lotsOfRefChars);
			best = noPrefixRefs;
		}
		
		if (best.length == 0) {
			List<String> refs = new ArrayList<String>();
			for (int i = 0; i < lines.length; i++)
				if (lotsOfRefChars[i])
					refs.add(lines[i]);
		}
		
		if (best.length == 1 && best[0].length() > 0.4 * text.length())
			return new String[] { };
		return best;
	}

	protected void markLongestRun(boolean[] lotsOfRefChars) {
		int currentStart = -1;
		int longestRun = -1;
		int longestRunStart = -1;
		for (int pos = 0; pos < lotsOfRefChars.length; pos++) {
			if (lotsOfRefChars[pos]) {
				if (currentStart == -1)
					currentStart = pos;
			} else {
				if (currentStart != -1 && (pos - currentStart) >= longestRun) {
					longestRun = pos - currentStart;
					longestRunStart = currentStart;
				}
				currentStart = -1;
			}
		}
		if (currentStart != -1 && (lotsOfRefChars.length - currentStart) >= longestRun) {
			longestRun = lotsOfRefChars.length - currentStart;
			longestRunStart = currentStart;
		}
		
		for (int i = 0; i < longestRunStart; i++)
			lotsOfRefChars[i] = false;
		for (int i = longestRunStart + longestRun; i < lotsOfRefChars.length; i++)
			lotsOfRefChars[i] = false;
	}

	protected void markLastRun(boolean[] lotsOfRefChars) {
		int stage = 0;
		int pos = lotsOfRefChars.length;
		while (pos-- > 0) {
			if (stage == 0 && lotsOfRefChars[pos])
				stage = 1;
			if (stage == 1 && !lotsOfRefChars[pos])
				stage = 2;
			if (stage == 2)
				lotsOfRefChars[pos] = false;
		}
	}

	protected String[] findOrderedRefs(String text, String prefix, String suffix) {
		List<String> refs = new ArrayList<String>();

		int index = 1;
		
		int start = text.indexOf(prefix + index + suffix);
		
		while (start != -1) {
			int cont = start + prefix.length();
			while (Character.isDigit(text.charAt(cont)))
				cont++;
			cont += suffix.length();
			index += 1;
			int next = text.indexOf(prefix + index + suffix, cont);

			if (next == -1)
				refs.add(text.substring(start).trim());
			else
				refs.add(text.substring(start, next).trim());
			start = next;
		}
		return refs.toArray(new String[] { });
	}
	
	protected String[] findPatternRefs(String text, Pattern pattern) {
		List<String> refs = new ArrayList<String>();

		Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			int start = matcher.start();
			while (matcher.find()) {
				refs.add(text.substring(start, matcher.start()).trim());
				start = matcher.start();
			}
			refs.add(text.substring(start).trim());
		}
		return refs.toArray(new String[] { });
	}

	protected void mapInc(Map<Integer, Integer> map, Integer key) {
		Integer value = map.get(key);
		if (value == null)
			value = 0;
		map.put(key, value + 1);
	}
	
	protected String[] findNoPrefixRefs(String[] lines, boolean[] refLines) {
		List<String> refs = new ArrayList<String>();

		Map<Integer, Integer> freqMap = new TreeMap<Integer, Integer>();
		for (int i = 0; i < refLines.length; i++)
			if (refLines[i])
				mapInc(freqMap, lines[i].length());
		
		int lengths[] = new int[freqMap.size()];
		int freqs[] = new int[freqMap.size()];
		int tmp = 0;
		for (int len : freqMap.keySet()) {
			lengths[tmp] = len;
			freqs[tmp] = freqMap.get(len);
			tmp += 1;
		}
		
		int maxFreq = -1;
		for (int i = 0; i < lengths.length; i++)
			if (maxFreq < freqs[i])
				maxFreq = freqs[i];

		double avgs[] = new double[freqs.length];
		for (int i = 0; i < avgs.length; i++) {
			int sum = 0;
			int cnt = 0;
			for (int j = i - 1; j < i + 2; j++) {
				if (j < 0 || j > freqs.length - 1)
					continue;
				sum += freqs[j];
				cnt += 1;
			}
			avgs[i] = 1.0 * sum / cnt;
		}
		
		double maxAvg = -1.0;
		for (int i = 0; i < lengths.length; i++)
			if (maxAvg < avgs[i])
				maxAvg = avgs[i];
		
		int threshold = -1;
		for (int i = 0; i < lengths.length; i++) {
			if (lengths[i] < minLineLength)
				continue;
			threshold = lengths[i];
			if (shortLineFrac * maxAvg < avgs[i])
				break;
		}
		
		if (threshold < minLineLength)
			threshold = minLineLength;
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length; i++)
			if (refLines[i]) {
				sb.append(lines[i]).append(" ");
				if (lines[i].length() < threshold) {
					String ref = sb.toString().trim();
					if (ref.length() > minRefLength)
						refs.add(ref);
					sb = new StringBuilder();
				}
			}
		String ref = sb.toString().trim();
		if (ref.length() > minRefLength)
			refs.add(ref);

		return refs.toArray(new String[] { });
	}

	protected void smear(boolean[] tab, boolean value, boolean forward, int size) {
		int pos = forward ? 0 : (tab.length - 1);
		int left = size;
		while (pos >= 0 && pos < tab.length) {
			if (tab[pos] == value)
				left = size;
			else
				left--;
			
			if (left >= 0)
				tab[pos] = value;
			else
				tab[pos] = !value;

			if (forward)
				pos++;
			else
				pos--;
		}
	}

	public int getMinLineLength() {
		return minLineLength;
	}

	public void setMinLineLength(int minLineLength) {
		this.minLineLength = minLineLength;
	}

	public String getRefCharPattern() {
		return refCharPattern;
	}

	public void setRefCharPattern(String refCharPattern) {
		this.refCharPattern = refCharPattern;
	}

	public double getRefCharRatioThreshold() {
		return refCharRatioThreshold;
	}

	public void setRefCharRatioThreshold(double refCharRatioThreshold) {
		this.refCharRatioThreshold = refCharRatioThreshold;
	}

	public int getMaxGapLength() {
		return maxGapLength;
	}

	public void setMaxGapLength(int maxGapLength) {
		this.maxGapLength = maxGapLength;
	}

	public int getMaxLineLength() {
		return maxLineLength;
	}

	public void setMaxLineLength(int maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

	public int getMinBlockLength() {
		return minBlockLength;
	}

	public void setMinBlockLength(int minBlockLength) {
		this.minBlockLength = minBlockLength;
	}

	public int getMinRefLength() {
		return minRefLength;
	}

	public void setMinRefLength(int minRefLength) {
		this.minRefLength = minRefLength;
	}
}
