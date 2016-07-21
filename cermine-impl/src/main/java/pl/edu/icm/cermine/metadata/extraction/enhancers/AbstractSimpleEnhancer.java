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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * Abstract base class for enhancers that can only succeed or fail - if
 * an enhancer extracts, for example, both volume and issue (from text
 * "Vol. 1/2"), it can only extract both pieces of information or none of them.
 *
 * @author Krzysztof Rusek
 */
public abstract class AbstractSimpleEnhancer extends AbstractFilterEnhancer {

    protected AbstractSimpleEnhancer() {}
    
    protected AbstractSimpleEnhancer(Collection<BxZoneLabel> zoneLabels) {
        setSearchedZoneLabels(zoneLabels);
    }

    protected boolean enhanceMetadata(BxZone zone, DocumentMetadata metadata) {
        return false;
    }

    protected boolean enhanceMetadata(BxPage page, DocumentMetadata metadata) {
        for (BxZone zone : filterZones(page)) {
            if (enhanceMetadata(zone, metadata)) {
                return true;
            }
        }
        return false;
    }

    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        for (BxPage page : filterPages(document)) {
            if (enhanceMetadata(page, metadata)) {
                return true;
            }
        }
        return false;
    }

    protected abstract Set<EnhancedField> getEnhancedFields();

    @Override
    public void enhanceMetadata(BxDocument document, DocumentMetadata metadata, Set<EnhancedField> enhancedFields) {
        Set<EnhancedField> fieldsToEnhance = getEnhancedFields();
        if (!CollectionUtils.containsAny(enhancedFields, fieldsToEnhance) 
                && enhanceMetadata(document, metadata)) {
            enhancedFields.addAll(fieldsToEnhance);
        }
    }
}
