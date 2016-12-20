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
package pl.edu.icm.cermine.parsing.model;

import java.util.List;

/**
 * Representation of a parsable string. Such a string may be tokenized i.e.
 * split into smaller parts (for example words). Each token may have a label
 * corresponding to the type of its text content. The parsing process consists
 * of tokenization followed by token classification.
 *
 * @author Bartosz Tarnawski
 * @param <T> type of the tokens
 */
public interface ParsableString<T extends Token<?>> {

    /**
     * @return tokens corresponding to the text content
     */
    List<T> getTokens();

    /**
     * @param tokens the tokens corresponding to the text content of the
     * parsable string
     */
    void setTokens(List<T> tokens);

    /**
     * @return the text content
     */
    String getRawText();

    /**
     * @param token token to the end of the token list
     */
    void addToken(T token);

    /**
     * Appends the text to the text content
     *
     * @param text text
     */
    void appendText(String text);

    /**
     * Cleans the text content
     */
    void clean();
}
