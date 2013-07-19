package pl.edu.icm.cermine.pubmed;

import java.util.ArrayList;
import java.util.List;

public class LevenshteinDistance {

    public Integer compare(List<String> s1, List<String> s2) {
        int retval = 0;
        final int n = s1.size();
        final int m = s2.size();
        if (n == 0) {
            retval = m;
        } else if (m == 0) {
            retval = n;
        } else {
            retval = compare(s1, n, s2, m);
        }
        return retval;
    }

    public Integer compareRec(List<String> tokens1, List<String> tokens2) {
        Integer length1 = tokens1.size();
        Integer length2 = tokens2.size();
        Integer distance = 0;
        if (length1 == 0) {
            return length2;
        } else if (length2 == 0) {
            return length1;
        } else {
            if (!tokens1.get(0).equals(tokens2.get(0))) {
                distance = 1;
            }
            return min3(compareRec(slice(tokens1, 1, length1 - 1), tokens2) + 1,
                    compareRec(tokens1, slice(tokens2, 1, length2 - 1)) + 1,
                    compareRec(slice(tokens1, 1, length1 - 1), slice(tokens2, 1, length2 - 1)) + distance);
        }
    }

    private static <T> List<T> slice(List<T> list, int index, int count) {
        List<T> result = new ArrayList<T>();
        if (index >= 0 && index < list.size()) {
            int end = index + count < list.size() ? index + count : list.size();
            for (int i = index; i < end; i++) {
                result.add(list.get(i));
            }
        }
        return result;
    }

    private int compare(List<String> s1, final int n, List<String> s2,
            final int m) {
        int matrix[][] = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            matrix[i][0] = i;
        }
        for (int i = 0; i <= m; i++) {
            matrix[0][i] = i;
        }

        for (int i = 1; i <= n; i++) {
            String s1i = s1.get(i - 1);
            for (int j = 1; j <= m; j++) {
                String s2j = s2.get(j - 1);
                final int cost = s1i.equals(s2j) ? 0 : 1;
                matrix[i][j] = min3(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1,
                        matrix[i - 1][j - 1] + cost);
            }
        }
        return matrix[n][m];
    }

    private int min3(final int a, final int b, final int c) {
        return Math.min(Math.min(a, b), c);
    }
}