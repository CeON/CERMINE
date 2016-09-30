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

package pl.edu.icm.cermine.evaluation.tools;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DocumentSetResult {

    private final List<EvalInformationType> evalTypes;
    
    private final Map<String, Map<EvalInformationType, SingleInformationDocResult>> results;
    
    private Map<EvalInformationType, Double> precision;
    private Map<EvalInformationType, Double> recall;
    private Map<EvalInformationType, Double> f1;
        
    public DocumentSetResult(List<EvalInformationType> types) {
        this.evalTypes = types;
        results = new HashMap<String, Map<EvalInformationType, SingleInformationDocResult>>();
    }
            
    public void addResult(String doc, SingleInformationDocResult result) {
        if (results.get(doc) == null) {
            results.put(doc, new EnumMap<EvalInformationType, SingleInformationDocResult>(EvalInformationType.class));
        }
        results.get(doc).put(result.getType(), result);
    }
    
    public void evaluate() {
        precision = new EnumMap<EvalInformationType, Double>(EvalInformationType.class);
        recall = new EnumMap<EvalInformationType, Double>(EvalInformationType.class);
        f1 = new EnumMap<EvalInformationType, Double>(EvalInformationType.class);
        
        Map<EvalInformationType, Integer> precisionCount = new EnumMap<EvalInformationType, Integer>(EvalInformationType.class);
        Map<EvalInformationType, Integer> recallCount = new EnumMap<EvalInformationType, Integer>(EvalInformationType.class);
        Map<EvalInformationType, Integer> f1Count = new EnumMap<EvalInformationType, Integer>(EvalInformationType.class);
        
        for (EvalInformationType type: evalTypes) {
            precision.put(type, 0.);
            recall.put(type, 0.);
            f1.put(type, 0.);
            precisionCount.put(type, 0);
            recallCount.put(type, 0);
            f1Count.put(type, 0);
        }
        
        for (Map.Entry<String, Map<EvalInformationType, SingleInformationDocResult>> result: results.entrySet()) {
            for (EvalInformationType type: evalTypes) {
                SingleInformationDocResult sResult = result.getValue().get(type);
                if (sResult == null) {
                    throw new IllegalArgumentException("Document " + result.getKey() + " does not contain result for " + type);
                }
                if (sResult.getPrecision() != null) {
                    precision.put(type, precision.get(type) + sResult.getPrecision());
                    precisionCount.put(type, precisionCount.get(type) + 1);
                }
                if (sResult.getRecall() != null) {
                    recall.put(type, recall.get(type) + sResult.getRecall());
                    recallCount.put(type, recallCount.get(type) + 1);
                }
                if (sResult.getF1() != null) {
                    f1.put(type, f1.get(type) + sResult.getF1());
                    f1Count.put(type, f1Count.get(type) + 1);
                }
            }
        }
        for (EvalInformationType type: evalTypes) {
            precision.put(type, precision.get(type)/precisionCount.get(type));
            recall.put(type, recall.get(type)/recallCount.get(type));
            f1.put(type, f1.get(type)/f1Count.get(type));
        }
    }
    
    public void printTotalSummary() {
        double avgPrecision = 0;
        for (Double prec: precision.values()) {
            avgPrecision += prec;
        }
        avgPrecision /= evalTypes.size();
        double avgRecall = 0;
        for (Double rec: recall.values()) {
            avgRecall += rec;
        }
        avgRecall /= evalTypes.size();
        double avgF1 = 0;
        for (Double f: f1.values()) {
            avgF1 += f;
        }
        avgF1 /= evalTypes.size();
        System.out.printf("Average precision\t\t%4.2f\n", 100 * avgPrecision);
        System.out.printf("Average recall\t\t%4.2f\n", 100 * avgRecall);
        System.out.printf("Average F1 score\t\t%4.2f\n", 100 * avgF1);
    }
    
    public void printTypeSummary(EvalInformationType type) {
        System.out.printf(type + ": precision: %4.2f" + ", recall: %4.2f" + ", F1: %4.2f\n\n",
                    getPrecision(type) == null ? -1. : 100 * getPrecision(type), 
                    getRecall(type) == null ? -1. : 100 * getRecall(type), 
                    getF1(type) == null ? -1. : 100 * getF1(type));
    }
    
    public void printDocument(String doc, int i) {
        Map<EvalInformationType, SingleInformationDocResult> docResults = results.get(doc);
        System.out.println("");
        System.out.println(">>>>>>>>> "+i);
        System.out.println(doc);
        for (EvalInformationType type: evalTypes) {
            System.out.println("");
            System.out.println(type);
            docResults.get(type).prettyPrint();
        }
        System.out.println("");
    }

    public void printCSV(String doc) {
        Map<EvalInformationType, SingleInformationDocResult> docResults = results.get(doc);
        System.out.print(doc);
        for (EvalInformationType type: evalTypes) {
            System.out.print(",");
            docResults.get(type).printCSV();
        }
        System.out.println("");
    }

    public Double getF1(EvalInformationType type) {
        return f1.get(type);
    }

    public Double getPrecision(EvalInformationType type) {
        return precision.get(type);
    }

    public Double getRecall(EvalInformationType type) {
        return recall.get(type);
    }

}
