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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Representation of a token, an atomic part of a string. Used for string
 * parsing.
 *
 * @author Bartosz Tarnawski
 * @param <L> type of label used for token classifying
 */
public class Token<L> {

    protected String text;
    protected int startIndex;
    protected int endIndex;
    protected L label;
    protected List<String> features;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public L getLabel() {
        return label;
    }

    public void setLabel(L label) {
        this.label = label;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public void addFeature(String feature) {
        features.add(feature);
    }

    /**
     * @param text the normalized string corresponding to the
     * substring(startIndex, endIndex) of the parsable string the token belongs
     * to
     * @param startIndex start index
     * @param endIndex end index
     * @param label may be null if the token is not classified yet
     */
    public Token(String text, int startIndex, int endIndex, L label) {
        this.text = text;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.label = label;
        this.features = new ArrayList<String>();
    }

    /**
     * @param text the normalized string corresponding to the
     * substring(startIndex, endIndex) of the parsable string the token belongs
     * to
     * @param startIndex start index
     * @param endIndex end index
     */
    public Token(String text, int startIndex, int endIndex) {
        this(text, startIndex, endIndex, null);
    }

    public Token(String text) {
        this(text, 0, 0);
    }

    public Token() {
        this("");
    }

    // For testing purposes only
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Token)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        @SuppressWarnings("rawtypes")
        Token rhs = (Token) obj;
        return new EqualsBuilder().
                append(text, rhs.text).
                append(startIndex, rhs.startIndex).
                append(endIndex, rhs.endIndex).
                append(label, rhs.label).
                isEquals();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 23 * hash + this.startIndex;
        hash = 23 * hash + this.endIndex;
        hash = 23 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }

    /**
     * Compares text strings represented by sequences of tokens
     *
     * @param <T> token type
     * @param lhs token sequence
     * @param rhs token sequence
     * @param caseSensitive whether case sensitive
     * @return whether the corresponding strings are equal
     */
    @SuppressWarnings("rawtypes")
    public static <T extends Token> boolean sequenceTextEquals(List<T> lhs, List<T> rhs,
            boolean caseSensitive) {
        if (lhs.size() != rhs.size()) {
            return false;
        }

        for (int i = 0; i < lhs.size(); i++) {
            String lhsString = lhs.get(i).getText();
            String rhsString = rhs.get(i).getText();
            if (!caseSensitive) {
                lhsString = lhsString.toLowerCase();
                rhsString = rhsString.toLowerCase();
            }
            if (!lhsString.equals(rhsString)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "Token{" + "text=" + text + ", startIndex=" + startIndex + ", endIndex=" + endIndex + ", label=" + label + '}';
    }

}
