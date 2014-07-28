/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.structure;

import java.io.IOException;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Dominika Tkaczyk
 */
public class SVMAlternativeMetadataZoneClassifier extends SVMMetadataZoneClassifier {
	private static final String MODEL_FILE_PATH = "/pl/edu/icm/cermine/structure/meta_humanities_model";
	private static final String RANGE_FILE_PATH = "/pl/edu/icm/cermine/structure/meta_humanities_model.range";
	
    private static SVMAlternativeMetadataZoneClassifier defaultInstance;
    
	public SVMAlternativeMetadataZoneClassifier() throws AnalysisException {
		super(MODEL_FILE_PATH, RANGE_FILE_PATH, true);
	}
	
    public static SVMAlternativeMetadataZoneClassifier getDefaultInstance() throws AnalysisException, IOException {
        if (defaultInstance == null) {
            defaultInstance = new SVMAlternativeMetadataZoneClassifier();
        }
        return defaultInstance;
    }

}
