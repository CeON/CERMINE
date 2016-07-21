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
package pl.edu.icm.cermine.structure.readingorder;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * @author Pawel Szostek
 */
public class TreeToListConverter {

    public List<BxZone> convertToList(BxZoneGroup obj) {
        List<BxZone> ret = new ArrayList<BxZone>();
        if (obj.getLeftChild() instanceof BxZone) {
            BxZone zone = (BxZone) obj.getLeftChild();
            ret.add(zone);
        } else { // obj.getLeftChild() instanceof BxZoneGroup
            ret.addAll(convertToList((BxZoneGroup) obj.getLeftChild()));
        }

        if (obj.getRightChild() instanceof BxZone) {
            BxZone zone = (BxZone) obj.getRightChild();
            ret.add(zone);
        } else { // obj.getRightChild() instanceof BxZoneGroup
            ret.addAll(convertToList((BxZoneGroup) obj.getRightChild()));
        }
        return ret;
    }

}
