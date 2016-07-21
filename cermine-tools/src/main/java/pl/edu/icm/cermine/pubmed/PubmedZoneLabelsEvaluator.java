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
package pl.edu.icm.cermine.pubmed;

import com.google.common.collect.Lists;
import java.io.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PubmedZoneLabelsEvaluator {

    public static void main(String[] args) throws FileNotFoundException, TransformationException {

        List<BxZoneLabel> labels = Lists.newArrayList(
                BxZoneLabel.MET_ABSTRACT, BxZoneLabel.BODY_ACKNOWLEDGMENT,
                BxZoneLabel.MET_AFFILIATION, BxZoneLabel.MET_AUTHOR,
                BxZoneLabel.MET_TITLE_AUTHOR, BxZoneLabel.MET_BIB_INFO,
                BxZoneLabel.BODY_CONTENT, BxZoneLabel.BODY_CONFLICT_STMT,
                BxZoneLabel.MET_COPYRIGHT, BxZoneLabel.MET_CORRESPONDENCE,
                BxZoneLabel.MET_DATES, BxZoneLabel.MET_EDITOR,
                BxZoneLabel.BODY_FIGURE, BxZoneLabel.BODY_GLOSSARY,
                BxZoneLabel.MET_KEYWORDS, BxZoneLabel.OTH_PAGE_NUMBER,
                BxZoneLabel.REFERENCES, BxZoneLabel.BODY_TABLE,
                BxZoneLabel.MET_TITLE, BxZoneLabel.MET_TYPE,
                BxZoneLabel.OTH_UNKNOWN);

        Map<BxZoneLabel, Map<BxZoneLabel, Integer>> map = new EnumMap<BxZoneLabel, Map<BxZoneLabel, Integer>>(BxZoneLabel.class);
        for (BxZoneLabel origLabel : labels) {

            Map<BxZoneLabel, Integer> smallMap = new EnumMap<BxZoneLabel, Integer>(BxZoneLabel.class);
            for (BxZoneLabel newLabel : labels) {
                smallMap.put(newLabel, 0);
            }
            map.put(origLabel, smallMap);
        }

        System.out.println(map);
        File dir = new File(args[0]);
        for (File tv : FileUtils.listFiles(dir, new String[]{"cxml-segm"}, true)) {
            System.out.println(tv.getPath());
            InputStream is = new FileInputStream(tv);
            TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
            Reader r = new InputStreamReader(is);
            BxDocument origBxDoc = new BxDocument().setPages(reader.read(r));

            File tv2 = new File(tv.getPath().replaceFirst(".cxml-segm", ".cxml-segm-m"));
            InputStream is2 = new FileInputStream(tv2);
            Reader r2 = new InputStreamReader(is2);
            BxDocument corrBxDoc = new BxDocument().setPages(reader.read(r2));

            List<BxZone> origZones = Lists.newArrayList(origBxDoc.asZones());
            List<BxZone> corrZones = Lists.newArrayList(corrBxDoc.asZones());

            for (int i = 0; i < origZones.size(); i++) {
                BxZone origZone = origZones.get(i);
                BxZone corrZone = corrZones.get(i);
                map.get(origZone.getLabel()).put(corrZone.getLabel(), 1 + map.get(origZone.getLabel()).get(corrZone.getLabel()));
            }
        }

        for (BxZoneLabel origLabel : labels) {
            System.out.println(origLabel);
            Map<BxZoneLabel, Integer> smallMap = map.get(origLabel);
            for (BxZoneLabel newLabel : labels) {
                if (smallMap.get(newLabel) > 0) {
                    System.out.println("\t" + newLabel + " " + smallMap.get(newLabel));
                }
            }
        }

        int count = 0;
        int alll = 0;
        System.out.println("");

        int sumPrec = 0;
        int countPrec = 0;
        int sumRecall = 0;
        int countRecall = 0;
        int sumF1 = 0;
        int countF1 = 0;
        for (BxZoneLabel origLabel : labels) {
            Map<BxZoneLabel, Integer> smallMap = map.get(origLabel);
            int good = smallMap.get(origLabel);
            count += good;
            int all = 0;

            for (BxZoneLabel newLabel : labels) {
                all += smallMap.get(newLabel);
            }
            alll += all;
            double prec = good * 100. / all;
            System.out.println("PREC " + origLabel + " " + (good * 100. / all));
            if (all > 0) {
                sumPrec += (good * 100. / all);
                countPrec++;
            }

            all = 0;

            for (BxZoneLabel newLabel : labels) {
                all += map.get(newLabel).get(origLabel);
            }
            System.out.println("RECALL " + origLabel + " " + (good * 100. / all));
            double rec = good * 100. / all;
            if (all > 0) {
                sumRecall += (good * 100. / all);
                countRecall++;
            }
            if (!Double.isNaN(rec) && !Double.isNaN(prec)) {
                sumF1 += (2 * prec * rec / (prec + rec));
                countF1++;
            }

            System.out.println("F1 " + origLabel + " " + (2 * prec * rec / (prec + rec)));
        }

        System.out.println("");
        System.out.println("AVG PREC " + ((double) sumPrec / (double) countPrec));
        System.out.println("AVG RECALL " + ((double) sumRecall / (double) countRecall));
        System.out.println("AVG F1 " + ((double) sumF1 / (double) countF1));

        System.out.println("");
        System.out.println("ACCURACY " + (count * 100. / alll));

    }
}
