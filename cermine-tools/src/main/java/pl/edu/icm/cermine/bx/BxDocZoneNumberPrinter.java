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

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxDocZoneNumberPrinter extends BxDocStatisticsPrinter {

    public static void main(String[] args) throws ParseException, TransformationException, FileNotFoundException {
        BxDocZoneNumberPrinter printer = new BxDocZoneNumberPrinter();
        printer.run(args);
    }

    @Override
    protected Map<String, String> getStatistics(BxDocument document) {
        Map<String, String> statistics = new HashMap<String, String>();
        int size = Lists.newArrayList(document.asZones()).size();
        statistics.put("Zone number", String.valueOf(size));
        return statistics;
    }
    
}
