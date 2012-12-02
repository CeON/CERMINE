package pl.edu.icm.cermine.pubmed;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringTools {

    public static List<String> tokenize(String text) {
        List<String> roughRet = new ArrayList<String>(Arrays.asList(text.split(" |\n|,|\\. |&|;|:|\\-")));
        List<String> ret = new ArrayList<String>();
        for (String candidate : roughRet) {
            if (candidate.length() > 1) {
                ret.add(candidate.toLowerCase());
            }
        }
        return ret;
    }

    public static Integer tokLen(String text) {
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
        if (strings.size() == 0) {
            return "";
        } else if (strings.size() == 1) {
            return strings.get(0);
        } else {
            StringBuilder ret = new StringBuilder();
            for (Integer partIdx = 0; partIdx < strings.size() - 1; ++partIdx) {
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
        } else {
            if (parts.length > 1) {
                StringBuilder ret = new StringBuilder();
                ret.append(parts[0]);
                for (Integer partIdx = 1; partIdx < parts.length - 1; ++partIdx) {
                    ret.append(".").append(parts[partIdx]);
                }
                return ret.toString();
            } else {
                return parts[0];
            }
        }
    }

    static List<String> produceDates(List<String> date) {
        List<String> ret = new ArrayList<String>();
        Integer monthInt = Integer.valueOf(date.get(1));
        if (monthInt >= 1 && monthInt <= 12) {
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();
            String month = months[monthInt - 1];
            ret.add(joinStrings(new String[]{date.get(0), month, date.get(2)}));
            ret.add(joinStrings(new String[]{date.get(0), month.substring(0, 3), date.get(2)}));
        }
        ret.add(joinStrings(date));
        return ret;
    }

    static String getTrueVizPath(String pdfPath) {
        return getFileCoreName(pdfPath) + ".xml";
    }

    static String getNLMPath(String pdfPath) {
        return getFileCoreName(pdfPath) + ".nxml";
    }
}
