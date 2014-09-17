package pl.edu.icm.cermine.parsing.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.model.ParsableString;

/**
 * Generic extractor, which transform NLM XML's into the parsable string representation.
 * 
 * @author Dominika Tkaczyk
 * @author Bartosz Tarnawski
 *
 * @param <L> label type
 * @param <T> token type
 * @param <PS> parsable string type
 */
public abstract class NLMParsableStringExtractor<L, T extends Token<L>, PS extends ParsableString<T>> {
	

    protected abstract List<String> getTags();
    protected abstract String getKeyText();
	protected abstract Map<String, L> getTagLabelMap();
	protected abstract PS createParsableString();
	protected abstract PS createParsableString(String text);
	
	/**
	 * @param source the InputSoruce representing the XML document
	 * @return parsable string representing the source
	 * @throws JDOMException
	 * @throws IOException
	 */
	public List<PS> extractStrings(InputSource source) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = builder.build(source);

        @SuppressWarnings("serial")
		Iterator<?> mixedStrings = doc.getDescendants(new Filter() {
			@Override
            public boolean matches(Object object) {
                return object instanceof Element && getTags().contains(((Element) object).getName());
            }
        });

        List<PS> stringSet = new ArrayList<PS>();

        while (mixedStrings.hasNext()) {
        	PS instance = createParsableString();
            readElement((Element) mixedStrings.next(), instance);
            stringSet.add(instance);
        }
        return stringSet;
    }

    private void readElement(Element element, PS instance) {
        for (Object content : element.getContent()) {
            if (content instanceof Text) {
                String contentText = ((Text) content).getText();
                if (!contentText.matches("^[\\s]*$")) {
                    for (T token : createParsableString(contentText).getTokens()) {
                        token.setStartIndex(token.getStartIndex() + instance.getRawText().length());
                        token.setEndIndex(token.getEndIndex() + instance.getRawText().length());
                        token.setLabel(getTagLabelMap().get(getKeyText()));
                        instance.addToken(token);
                    }
                    instance.appendText(contentText);
                } else {
                    instance.appendText(" ");
                }
            } else if (content instanceof Element) {
                Element contentElement = (Element) content;
                String contentElementName = contentElement.getName();
                if (getTagLabelMap().containsKey(contentElementName)) {
                    for (T token : createParsableString(contentElement.getValue()).getTokens()) {
                        token.setStartIndex(token.getStartIndex() + instance.getRawText().length());
                        token.setEndIndex(token.getEndIndex() + instance.getRawText().length());
                        token.setLabel(getTagLabelMap().get(contentElementName));
                        instance.addToken(token);
                    }
                    instance.appendText(contentElement.getValue());
                } else {
                    readElement(contentElement, instance);
                }
            }
        }
        instance.clean();
    }
}
