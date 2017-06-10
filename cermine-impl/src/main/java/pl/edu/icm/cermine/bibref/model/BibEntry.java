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

package pl.edu.icm.cermine.bibref.model;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * Bibliographic reference entry, modelled after BibTeX format.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 */
public class BibEntry {

    private BibEntryType type;
    private String key;
    private String text;
    private final Map<BibEntryFieldType, List<BibEntryField>> fields = 
            new EnumMap<BibEntryFieldType, List<BibEntryField>>(BibEntryFieldType.class);

    public BibEntry() {
    }

    public BibEntry(BibEntryType type) {
        this.type = type;
    }

    public BibEntry(BibEntryType type, String key) {
        this.type = type;
        this.key = key;
    }

    public BibEntryType getType() {
        return type;
    }

    public BibEntry setType(BibEntryType type) {
        this.type = type;
        return this;
    }

    public String getKey() {
        return key;
    }

    public BibEntry setKey(String key) {
        this.key = key;
        return this;
    }

    public String getText() {
        return text;
    }

    public BibEntry setText(String text) {
        this.text = text;
        return this;
    }

    public Set<BibEntryFieldType> getFieldKeys() {
        return fields.keySet();
    }

    public BibEntryField getFirstField(BibEntryFieldType key) {
        List<BibEntryField> fieldList = fields.get(key);
        if (fieldList == null) {
            return null;
        }
        return (fieldList.isEmpty()) ? null : fieldList.get(0);
    }

    public String getFirstFieldValue(BibEntryFieldType key) {
        BibEntryField field = getFirstField(key);
        if (field == null) {
            return null;
        }
        return field.getText();
    }

    public List<BibEntryField> getAllFields(BibEntryFieldType key) {
        return (fields.get(key) == null) ? new ArrayList<BibEntryField>() : 
                fields.get(key);
    }

    public List<String> getAllFieldValues(BibEntryFieldType key) {
        List<BibEntryField> beFields = getAllFields(key);
        List<String> values = new ArrayList<String>();

        for (BibEntryField field : beFields) {
            values.add(field.getText());
        }
        return values;
    }

    public BibEntry addField(BibEntryFieldType key, String value) {
        if (key == null || value == null) {
            return this;
        }
        return addField(key, new BibEntryField(value));
    }

    public BibEntry setField(BibEntryFieldType key, String value) {
        if (key == null || value == null) {
            return this;
        }
        return setField(key, new BibEntryField(value));
    }

    public BibEntry addField(BibEntryFieldType key, String value, int startIndex, int endIndex) {
        if (key == null || value == null) {
            return this;
        }
        return addField(key, new BibEntryField(value, startIndex, endIndex));
    }

    public BibEntry setField(BibEntryFieldType key, String value, int startIndex, int endIndex) {
        if (key == null || value == null) {
            return this;
        }
        return setField(key, new BibEntryField(value, startIndex, endIndex));
    }

    public BibEntry addField(BibEntryFieldType key, BibEntryField field) {
        if (key == null || field == null) {
            return this;
        }
        if (fields.get(key) == null) {
            fields.put(key, new ArrayList<BibEntryField>());
        }
        fields.get(key).add(field);

        return this;
    }

    public BibEntry setField(BibEntryFieldType key, BibEntryField field) {
        if (key == null || field == null) {
            return this;
        }
        if (fields.get(key) != null) {
            fields.get(key).clear();
        }
        return addField(key, field);
    }

    public BibEntry removeField(BibEntryFieldType key) {
        fields.remove(key);
        return this;
    }
    
    public String generateKey() {
        String result = "Unknown";
        if (fields.get(BibEntryFieldType.AUTHOR) != null && 
                fields.get(BibEntryFieldType.AUTHOR).size() > 0) {
            result = fields.get(BibEntryFieldType.AUTHOR).get(0).getText();
            Pattern pattern = Pattern.compile("^[\\p{L}' ]+");
            Matcher matcher = pattern.matcher(result);
            result = matcher.find() ? matcher.group().replaceAll("[' ]", "") : "Unknown";
        }
        if (fields.get(BibEntryFieldType.YEAR) != null) {
            result += fields.get(BibEntryFieldType.YEAR).get(0).getText();
        }
        return result;
    }

    protected String escape(String text) {
        return text.replace("{", "\\{").replace("}", "\\}").replace("_", "\\_");
    }

    public String toBibTeX() {
        StringBuilder sb = new StringBuilder();
        sb.append('@').append(type == null ? BibEntryType.MISC.getType() : type.getType());
        sb.append('{').append(key == null ? generateKey() : key).append(",\n");
        for (BibEntryFieldType field : fields.keySet()) {
            sb.append('\t').append(field.getType()).append(" = {");
            List<String> values = new ArrayList<String>();
            for (BibEntryField bef : fields.get(field)) {
                values.add(bef.getText());
            }
            sb.append(escape(StringUtils.join(values, ", "))).append("},\n");
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BibEntry other = (BibEntry) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        if (this.fields == other.fields) {
            return true;
        }
        return this.fields != null && this.fields.equals(other.fields);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 23 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 23 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 23 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }
    
}
