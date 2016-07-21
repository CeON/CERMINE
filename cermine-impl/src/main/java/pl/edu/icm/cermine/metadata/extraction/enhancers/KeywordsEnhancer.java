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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Krzysztof Rusek
 */
public class KeywordsEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX = Pattern.compile("^key\\s?words[:-—]?|^index terms[:-—]?", Pattern.CASE_INSENSITIVE);

    public KeywordsEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_KEYWORDS));
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.KEYWORDS);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                String text = zone.toText().replace("\n", "<eol>");
                text = PREFIX.matcher(text).replaceFirst("");
                
                if (text.matches(".*[:;,.·—].*")) {
                    String separator = "[:;,.·—]";
                    for (String keyword : text.split(separator)) {
                        metadata.addKeyword(keyword.trim().replaceFirst("\\.$", "").replace("-<eol>", "").replace("<eol>", " "));
                    }
                
                    return true;
                }
                
                List<String> keywords = new ArrayList<String>();
                for (BxLine line : zone) {
                    List<BxWord> words = new ArrayList<BxWord>();
                    for (BxWord word : line) {
                        words.add(word);
                    }
                    if (PREFIX.matcher(words.get(0).toText()).matches()) {
                        words.remove(0);
                    } else if (words.size() > 1 && PREFIX.matcher(words.get(0).toText()+" "+words.get(1).toText()).matches()) {
                        words.remove(0);
                        words.remove(0);
                    }
                    if (words.isEmpty()) {
                        continue;
                    }
                    
                    if (words.get(0).toText().charAt(0) >= 'a' && words.get(0).toText().charAt(0) <= 'z' && !keywords.isEmpty()) {
                        String concat = keywords.get(keywords.size()-1)+" "+words.get(0).toText();
                        keywords.remove(keywords.size()-1);
                        keywords.add(concat);
                    } else {
                        keywords.add(words.get(0).toText());
                    }
                    for (BxWord word : words) {
                        if (words.indexOf(word) < words.size() - 1) {
                            double space = word.getNext().getX()-word.getX()-word.getWidth();
                            if (space > 6) {
                                keywords.add(word.getNext().toText());
                            } else {
                                String concat = keywords.get(keywords.size()-1)+" "+word.getNext().toText();
                                keywords.remove(keywords.size()-1);
                                keywords.add(concat);
                            }
                        }
                    }
                }
                for (String keyword : keywords) {
                    metadata.addKeyword(keyword.trim().replaceFirst("\\.$", ""));
                }
            }
        }
        return false;
    }
}
