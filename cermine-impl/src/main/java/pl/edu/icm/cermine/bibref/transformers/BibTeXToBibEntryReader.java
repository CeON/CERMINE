package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.FormatToModelReader;

/**
 * Reader of BibTeX format to BibEntry model.
 * @author estocka
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * @author Dominika Tkaczyk
 */
public class BibTeXToBibEntryReader implements FormatToModelReader<BibEntry> {

    @Override
    public BibEntry read(String string, Object... hints) throws TransformationException {
        return processBibteX(string);
    }

    @Override
    public BibEntry read(Reader reader, Object... hints) throws TransformationException {
        try {
            return processBibteX(IOUtils.toString(reader));
        } catch (IOException ex) {
            throw new TransformationException(ex);
        }
    }

    @Override
    public List<BibEntry> readAll(String string, Object... hints) throws TransformationException {
        List<BibEntry> entries = new ArrayList<BibEntry>();
        String[] split = string.split("\n\n");
        for (String s : split) {
            entries.add(processBibteX(s.substring(s.indexOf("@"))));
        }
        return entries;
    }

    @Override
    public List<BibEntry> readAll(Reader reader, Object... hints) throws TransformationException {
        try {
            return readAll(IOUtils.toString(reader));
        } catch (IOException ex) {
            throw new TransformationException(ex);
        }
    }

    protected BibEntry processBibteX(String bibteX) throws TransformationException {
        BibEntry bibEntry = new BibEntry();

        String[] lines = bibteX.split("\n");

        //type
        int indexOfAt = lines[0].indexOf('@');
        if (indexOfAt < 0) {
            throw new TransformationException("Cannot parse string as BibTeX!");
        }
        int indexOfBrace = lines[0].indexOf('{');
        if (indexOfBrace < 0) {
            throw new TransformationException("Cannot parse string as BibTeX!");
        }
        if (indexOfBrace > indexOfAt) {
            String type = lines[0].substring(indexOfAt + 1, indexOfBrace).toLowerCase();
            //list??
            bibEntry.setType(type);
        } else {
            throw new TransformationException("Cannot parse string as BibTeX!");
        }

        //fields
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].matches("\\s\\w*\\s*=\\s*[{].*[},]")) {
          
                String[] field = lines[i].split("\\s*=\\s*[{]");
                String key = field[0].trim().toLowerCase();
                String value = field[1].substring(0, field[1].length() - 2);
                String[] values = value.split(",");
                if (key.equals(BibEntry.FIELD_AUTHOR)) {
                    for (int j = 0; j < values.length; j+=2) {
                        String val = values[j];
                        if (j + 1 < values.length) {
                            val += ",";
                            val += values[j+1];
                        }
                        bibEntry.addField(key, revertEscape(val));
                    }
                } else {
                    for (String val : values) {
                        bibEntry.addField(key, revertEscape(val));
                    }
                }
            }
        }

        return bibEntry;
    }

    protected String revertEscape(String text) {
        return text.trim().replace("\\{", "{").replace("\\}", "}").replace("\\_", "_");
    }

}
