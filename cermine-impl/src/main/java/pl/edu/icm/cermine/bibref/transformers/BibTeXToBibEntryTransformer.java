package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Reader of BibTeX format to BibEntry model.
 * @autor estocka
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class BibTeXToBibEntryTransformer {


    public List<BibEntry> read(String string, Object... hints) throws TransformationException {
        return read(new StringReader(string), hints);
    }

    public List<BibEntry> read(Reader reader, Object... hints) throws TransformationException {

        List<BibEntry> bibEntryList = new ArrayList<BibEntry>();
        String bibteXString = readerToString(reader);
        //format? replace  }\\s*@ with }\n\n@
        String[] split = bibteXString.split("\n\n");
        for (String s : split) {
       
            bibEntryList.add(processBibteX(s.substring(s.indexOf("@"))));

        }
        return bibEntryList;
    }

    protected String readerToString(Reader reader) {

        int i;
        StringBuilder sb = new StringBuilder();
        try {
            while ((i = reader.read()) != -1) {
                sb.append((char) i);

            }
        } catch (IOException ex) {
            LoggerFactory.getLogger(BibTeXToBibEntryTransformer.class).error("Exception caught", ex);
        }
        return sb.toString();
    }

    protected BibEntry processBibteX(String bibteX) {
        BibEntry bibEntry = new BibEntry();

        String[] lines = bibteX.split("\n");

        //type
        int indexOfAt = lines[0].indexOf('@');
        int indexOfBrace = lines[0].indexOf('{');
        if (indexOfBrace > indexOfAt) {
            String type = lines[0].substring(indexOfAt + 1, indexOfBrace).toLowerCase();
            //list??
            bibEntry.setType(type);
        }

        //fields
        for (int i = 1; i < lines.length; i++) {

            if (lines[i].matches("\\s\\w*\\s*=\\s*[{].*[},]")) {
          
                String[] field = lines[i].split("\\s*=\\s*[{]");
                String key = field[0].trim().toLowerCase();
                String value = field[1].substring(0, field[1].length() - 2);
                //list??
                bibEntry.setField(key, revertEscape(value));

            }
        }

        return bibEntry;
    }

    protected String revertEscape(String text) {
        text = text.replace("\\{", "{");
        text = text.replace("\\}", "}");
        text = text.replace("\\_", "_");
        return text;
    }
}
