/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.metadata.tools;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author krusek
 * @author Dominika Tkaczyk
 */
public class MetadataTools {
    
    public static String cleanOther(String str) {
        return str.replaceAll("[’‘]", "'")
                  .replaceAll("[–]", "-")  // EN DASH \u2013
                  .replaceAll("[—]", "-"); // EM DASH \u2014
    }
    
    public static String cleanLigatures(String str) {
        return str.replaceAll("\uFB00", "ff")
                  .replaceAll("\uFB01", "fi")
                  .replaceAll("\uFB02", "fl")
                  .replaceAll("\uFB03", "ffi")
                  .replaceAll("\uFB04", "ffl")
                  .replaceAll("\uFB05", "ft")
                  .replaceAll("\uFB06", "st")
                  .replaceAll("\u00E6", "ae")
                  .replaceAll("\u0153", "oe");
    }
    
    public static String cleanHyphenation(String str) {
        str = str.replace("$", "\\$");
        
        String hyphenList = "\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212-";
        Pattern p = Pattern.compile("([^" + hyphenList + "]*\\S+)[" + hyphenList + "]\n", Pattern.DOTALL);
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1));
        }
        m.appendTail(sb);
        return sb.toString().replaceAll("\n", " ").replace("\\$", "$");
    }
    
    public static String clean(String str) {
        if (str == null) {
            return null;
        }
        return cleanOther(cleanHyphenation(cleanLigatures(str)));
    }
    
    public static String cleanAndNormalize(String str) {
        return Normalizer.normalize(clean(str), Normalizer.Form.NFKD);
    }
}
