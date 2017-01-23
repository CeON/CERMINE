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

import java.io.IOException;
import java.util.Locale;
import org.apache.commons.cli.ParseException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxBibZonesCorrector extends BxDocRewriter {

    public static void main(String[] args) throws ParseException, TransformationException, IOException, AnalysisException {
        BxBibZonesCorrector corrector = new BxBibZonesCorrector();
        corrector.run(args);
    }

    @Override
    protected BxDocument transform(BxDocument document) {
        for (BxZone z : document.asZones()) {
            if (!BxZoneLabel.MET_BIB_INFO.equals(z.getLabel())
                    && !BxZoneLabel.REFERENCES.equals(z.getLabel())
                    && z.childrenCount() <= 2
                    && (z.toText().toLowerCase(Locale.ENGLISH).contains("journal ")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("vol.")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("vol ")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("pp.")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("volume ")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("pp ")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("issn")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("doi:")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("doi ")
                    || z.toText().toLowerCase(Locale.ENGLISH).contains("citation:"))) {
                System.out.println("DETECTED BIBINFO: ");
                System.out.println(z.getLabel() + " " + z.toText());
                System.out.println("");
                z.setLabel(BxZoneLabel.MET_BIB_INFO);
            } else {
                if (!BxZoneLabel.OTH_UNKNOWN.equals(z.getLabel())
                        && !BxZoneLabel.MET_BIB_INFO.equals(z.getLabel())
                        && z.childrenCount() <= 2
                        && (z.toText().toLowerCase(Locale.ENGLISH).contains("page "))) {
                    System.out.println("DETECTED PAGE: ");
                    System.out.println(z.getLabel() + " " + z.toText());
                    System.out.println("");
                    z.setLabel(BxZoneLabel.OTH_UNKNOWN);
                }
            }
        }
        return document;
    }
}
