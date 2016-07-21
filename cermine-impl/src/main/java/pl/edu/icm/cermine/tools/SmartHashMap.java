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

import java.util.HashMap;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Pawel Szostek
 */
public class SmartHashMap extends HashMap<String, BxZoneLabel> {

    private static final long serialVersionUID = 74383628471L;

    public SmartHashMap putIf(String string, BxZoneLabel label) {
        if (string != null && !string.isEmpty()) {
            string = string.toLowerCase();
            put(string, label);
        }
        return this;
    }

    public SmartHashMap putIf(List<String> strings, BxZoneLabel label) {
        for (String string : strings) {
            putIf(string, label);
        }
        return this;
    }
}
