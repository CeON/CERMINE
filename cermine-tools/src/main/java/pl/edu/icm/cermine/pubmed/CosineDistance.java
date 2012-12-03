package pl.edu.icm.cermine.pubmed;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CosineDistance {

    private HashMap<String, Integer> calculateVector(List<String> tokens) {
        HashMap<String, Integer> vector = new HashMap<String, Integer>();
        for (String token : tokens) {
            if (vector.containsKey(token)) {
                vector.put(token, vector.get(token) + 1);
            } else {
                vector.put(token, 1);
            }
        }
        return vector;
    }

    private Double vectorLength(HashMap<String, Integer> vector) {
        Double ret = 0.0;
        for (Entry<String, Integer> entry : vector.entrySet()) {
            ret += entry.getValue() * entry.getValue();
        }
        return Math.sqrt(ret);
    }

    private Double dotProduct(HashMap<String, Integer> vector1, HashMap<String, Integer> vector2) {
        Double ret = 0.0;
        for (Entry<String, Integer> entry : vector1.entrySet()) {
            if (vector2.containsKey(entry.getKey())) {
                ret += entry.getValue() * vector2.get(entry.getKey());
            }
        }
        return ret;
    }

    public Double compare(List<String> s1, List<String> s2) {
        HashMap<String, Integer> v1 = calculateVector(s1);
        HashMap<String, Integer> v2 = calculateVector(s2);

        return dotProduct(v1, v2) / (vectorLength(v1) * vectorLength(v2));
    }
}
