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
package pl.edu.icm.cermine.tools;

import org.jdom.Element;
import org.jdom.Text;
import org.jdom.Verifier;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class XMLTools {
    
    public static String getTextContent(Element element) {
        StringBuilder ret = new StringBuilder();
        if (element == null) {
            return "";
        }
        for (Object cont: element.getContent()) {
            if (cont instanceof Text) {
                ret.append(((Text) cont).getText());
                ret.append(" ");
            } else if (cont instanceof Element) {
                ret.append(getTextContent((Element) cont));
                ret.append(" ");
            }
        }
        return ret.toString().replaceAll("\\s+", " ").trim();
    }

    public static String removeInvalidXMLChars(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Verifier.isXMLCharacter(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

}
