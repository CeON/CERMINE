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

package pl.edu.icm.cermine.metadata;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.extraction.enhancers.*;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Extracting metadata from labelled zones. 
 *
 * @author Krzysztof Rusek
 */
public class EnhancerMetadataExtractor implements MetadataExtractor<DocumentMetadata> {

    private final List<Enhancer> enhancers = Arrays.<Enhancer>asList(
                new TitleAuthorSplitterEnhancer(),
                new AuthorTitleSplitterEnhancer(),
                new AuthorAffiliationSplitterEnhancer(),
                new AffiliationAuthorSplitterEnhancer(),
                new AuthorAffiliationGeometricEnhancer(),
                new HindawiCornerInfoEnhancer(),
                new TitleMergedWithTypeEnhancer(),
                new TitleEnhancer(),
                new AuthorEnhancer(),
                new EmailEnhancer(),
                new DoiEnhancer(),
                new UrnEnhancer(),
                new DescriptionEnhancer(),
                new KeywordsEnhancer(),
                new ArticleIdEnhancer(),
                new VolumeEnhancer(),
                new IssueEnhancer(),
                new JournalVolumeIssueWithAuthorEnhancer(),
                new JournalVolumeIssueEnhancer(),
                new JournalWithoutVolumeIssueEnhancer(),
                new JournalYearVolumeEnhancer(),
                new JournalVolumePagesYearEnhancer(),
                new JournalVolumePagesEnhancer(),
                new JournalVolumeIssueExtendedEnhancer(),
                new JournalIssnEnhancer(),
                new JournalEnhancer(),
                new IssnEnhancer(),
                new EditorEnhancer(),
                new AffiliationGeometricEnhancer(),
                new ReceivedDateEnhancer(),
                new AcceptedDateEnhancer(),
                new PublishedDateEnhancer(),
                new RevisedDateEnhancer(),
                new RevisedFormDateEnhancer(),
                new PagesEnhancer(),
                new PagesPartialEnhancer(),
                new PagesNumbersEnhancer(),
                new PagesLastEnhancer(),
                new CiteAsEnhancer(),
                new YearEnhancer()
                );

    public List<Enhancer> getEnhancers() {
        return enhancers;
    }

    public EnhancerMetadataExtractor addEnhancer(Enhancer enhancer) {
        enhancers.add(enhancer);
        return this;
    }

    public EnhancerMetadataExtractor setEnhancers(List<Enhancer> value) {
        enhancers.clear();
        enhancers.addAll(value);
        return this;
    }

    @Override
    public DocumentMetadata extractMetadata(BxDocument document) throws AnalysisException {
        Set<EnhancedField> enhancedFields = EnumSet.noneOf(EnhancedField.class);
        
        DocumentMetadata metadata = new DocumentMetadata();
        
        for (Enhancer enhancer : enhancers) {
            enhancer.enhanceMetadata(document, metadata, enhancedFields);
            TimeoutRegister.get().check();
        }
        metadata.clean();
        return metadata;
    }

}
