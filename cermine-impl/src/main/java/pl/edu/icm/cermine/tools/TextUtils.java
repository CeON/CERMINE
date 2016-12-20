/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
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
package pl.edu.icm.cermine.tools;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class TextUtils {

    public static boolean isNumberBetween(String s, int lower, int upper) {
        try {
            int n = Integer.parseInt(s);
            return n >= lower && n < upper;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String removeOrphantSpaces(String text) {
        if (text.length() < 5) {
            return text;
        }
        StringBuilder ret = new StringBuilder();
        boolean evenSpaces = true;
        for (int idx = 0; idx < text.length(); idx += 2) {
            if (text.charAt(idx) != ' ') {
                evenSpaces = false;
                break;
            }
        }
        if (evenSpaces) {
            for (int idx = 1; idx < text.length(); idx += 2) {
                ret.append(text.charAt(idx));
            }
            return ret.toString();
        }

        boolean oddSpaces = true;
        for (int idx = 1; idx < text.length(); idx += 2) {
            if (text.charAt(idx) != ' ') {
                oddSpaces = false;
                break;
            }
        }
        if (oddSpaces) {
            for (int idx = 0; idx < text.length(); idx += 2) {
                ret.append(text.charAt(idx));
            }
            return ret.toString();
        }

        return text;
    }

    public static String cleanLigatures(String str) {
        return str.replaceAll("\uFB00", "ff")
                .replaceAll("\uFB01", "fi")
                .replaceAll("\uFB02", "fl")
                .replaceAll("\uFB03", "ffi")
                .replaceAll("\uFB04", "ffl")
                .replaceAll("\uFB05", "ft")
                .replaceAll("\uFB06", "st");
    }

    public static List<String> tokenize(String text) {
        List<String> roughRet = new ArrayList<String>(Arrays.asList(text.split(" |=|\\(|\\)|\n|,|\\. |&|;|:|\\-|/")));
        List<String> ret = new ArrayList<String>();
        for (String candidate : roughRet) {
            if (candidate.length() > 1) {
                ret.add(candidate.toLowerCase());
            }
        }
        return ret;
    }

    public static int tokLen(String text) {
        return tokenize(text).size();
    }

    public static String joinStrings(List<String> strings) {
        StringBuilder ret = new StringBuilder();
        for (String str : strings) {
            if (str != null) {
                ret.append(str).append(" ");
            }
        }
        return ret.toString();
    }

    public static String joinStrings(List<String> strings, char delim) {
        if (strings.isEmpty()) {
            return "";
        } else if (strings.size() == 1) {
            return strings.get(0);
        } else {
            StringBuilder ret = new StringBuilder();
            for (int partIdx = 0; partIdx < strings.size() - 1; ++partIdx) {
                ret.append(strings.get(partIdx)).append(delim);
            }
            ret.append(strings.get(strings.size() - 1));
            return ret.toString();
        }
    }

    public static String joinStrings(String[] strings) {
        return joinStrings(new ArrayList<String>(Arrays.asList(strings)));
    }

    static String getFileCoreName(String path) {
        String[] parts = path.split("\\.");
        if (parts.length == 2) {
            return parts[0];
        } else if (parts.length > 1) {
            StringBuilder ret = new StringBuilder();
            ret.append(parts[0]);
            for (int partIdx = 1; partIdx < parts.length - 1; ++partIdx) {
                ret.append(".").append(parts[partIdx]);
            }
            return ret.toString();
        } else {
            return parts[0];
        }
    }

    public static List<String> produceDates(List<String> date) {
        List<String> ret = new ArrayList<String>();
        int monthInt = Integer.valueOf(date.get(1));
        if (monthInt >= 1 && monthInt <= 12) {
            DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
            String[] months = dfs.getMonths();
            String month = months[monthInt - 1];
            ret.add(joinStrings(new String[]{date.get(0), month, date.get(2)}));
            ret.add(joinStrings(new String[]{date.get(0), month.substring(0, 3), date.get(2)}));
        }
        ret.add(joinStrings(date));
        return ret;
    }

    public static String getTrueVizPath(String pdfPath) {
        return getFileCoreName(pdfPath) + ".xml";
    }

    public static String getNLMPath(String pdfPath) {
        return getFileCoreName(pdfPath) + ".nxml";
    }

    private static final Pattern NOT_WORD_PATTERN = Pattern.compile("[^a-zA-Z]");
    private static final Pattern NOT_NUMBER_PATTERN = Pattern.compile("[^0-9]");
    private static final Pattern NOT_UPPERCASE_PATTERN = Pattern.compile("[^A-Z]");
    private static final Pattern NOT_LOWERCASE_PATTERN = Pattern.compile("[^a-z]");
    private static final Pattern NOT_ALPHANUM_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    /**
     * @param text text
     * @return whether the text is a single word
     */
    public static boolean isWord(String text) {
        return !NOT_WORD_PATTERN.matcher(text).find();
    }

    /**
     * @param text text
     * @return whether the text is a single number
     */
    public static boolean isNumber(String text) {
        return !NOT_NUMBER_PATTERN.matcher(text).find();
    }

    /**
     * @param text text
     * @return whether the text is a single word with the first letter in upper
     * case and all the rest in lower case
     */
    public static boolean isOnlyFirstUpperCase(String text) {
        boolean firstUpperCase = !NOT_UPPERCASE_PATTERN.matcher(text.substring(0, 1)).find();
        boolean restLowerCase = !NOT_LOWERCASE_PATTERN.matcher(text.substring(1)).find();
        return firstUpperCase && restLowerCase;
    }

    /**
     * @param text text
     * @return whether the text is a single word with all letters in upper case
     */
    public static boolean isAllUpperCase(String text) {
        return !NOT_UPPERCASE_PATTERN.matcher(text).find();
    }

    /**
     * @param text text
     * @return whether the text is a single word with all letters in upper case
     */
    public static boolean isAllLowerCase(String text) {
        return !NOT_LOWERCASE_PATTERN.matcher(text).find();
    }

    /**
     * @param text text
     * @return whether the word is a commonly used separator ("." "," ";")
     */
    public static boolean isSeparator(String text) {
        return text.equals(".") || text.equals(",") || text.equals(";");
    }

    /**
     * @param text text
     * @return whether the word is not alphanumeric and is not a separator
     */
    public static boolean isNonAlphanumSep(String text) {
        return NOT_ALPHANUM_PATTERN.matcher(text).find() && !isSeparator(text);
    }

}
