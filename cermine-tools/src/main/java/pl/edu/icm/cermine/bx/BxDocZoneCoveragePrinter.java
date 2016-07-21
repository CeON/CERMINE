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

package pl.edu.icm.cermine.bx;

import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxDocZoneCoveragePrinter extends BxDocStatisticsPrinter {

    public static void main(String[] args) throws ParseException, TransformationException, FileNotFoundException {
        BxDocZoneCoveragePrinter printer = new BxDocZoneCoveragePrinter();
        printer.run(args);
    }

    @Override
    protected Map<String, String> getStatistics(BxDocument document) {
        Map<String, String> statistics = new HashMap<String, String>();
        
        Set<BxZoneLabel> set = EnumSet.noneOf(BxZoneLabel.class);
        int all = 0;
        int good = 0;
        for (BxZone z: document.asZones()) {
            all++;
            if (!z.getLabel().equals(BxZoneLabel.OTH_UNKNOWN)) {
                good++;
            }
            if (z.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_METADATA)) {
                set.add(z.getLabel());
            }
        }
        int intcov = 0;
        if (all > 0) {
            intcov = good*100/all;
        }
        
        statistics.put("Metadata labels number", String.valueOf(set.size()));
        statistics.put("Known label coverage", String.valueOf(intcov));
        return statistics;
    }
    
}
