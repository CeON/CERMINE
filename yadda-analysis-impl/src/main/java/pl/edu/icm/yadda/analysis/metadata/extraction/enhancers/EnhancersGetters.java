package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jdom.Element;

/**
 *
 * @author krusek
 */
public class EnhancersGetters {
    
    //getters
    
    //author names
    public static List<String> getAuthorNames(Element metadata) {
        List<String> authors = new ArrayList<String>();
        String[] path = {"front", "article-meta", "contrib-group", "contrib"};
        
        for (Element element : Enhancers.getElements(metadata, path)) {
            if ("author".equals(element.getAttributeValue("contrib-type"))) {
                authors.add(element.getChildText("string-name"));
            }
        }
        
        return authors;
    }
    
    public static boolean isAffiliationIndex(String text) {
        Pattern pattern = Pattern.compile("\\d{1,2}");
        return pattern.matcher(text).matches();
    }

    public static String getTitle(Element metadata) {
        String[] path = {"front", "article-meta", "title-group", "article-title"};
        return Enhancers.getElement(metadata, path).getText();
    }

}
