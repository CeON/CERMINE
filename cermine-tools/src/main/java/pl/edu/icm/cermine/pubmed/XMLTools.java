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
package pl.edu.icm.cermine.pubmed;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Pawel Szostek
 */
public class XMLTools {

    public static String extractTextFromNode(Node node) {

        StringBuilder ret = new StringBuilder();

        if (node == null) {
            return "";
        }
        if (node.getChildNodes().getLength() == 0) {
            if (node.getNodeValue() != null) {
                return node.getNodeValue() + " ";
            } else {
                return "";
            }
        } else {
            for (int childIdx = 0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
                ret.append(extractTextFromNode(node.getChildNodes().item(childIdx)));
            }
        }
        return ret.toString().replaceAll("\n", " ").replaceAll("\\s+", " ");
    }

    public static String extractTextFromNodes(NodeList nodes) {
        StringBuilder ret = new StringBuilder();
        for (int nodeIdx = 0; nodeIdx < nodes.getLength(); ++nodeIdx) {
            Node node = nodes.item(nodeIdx);
            ret.append(extractTextFromNode(node));
        }
        return ret.toString();
    }

    public static List<String> extractTextAsList(NodeList nodes) {
        List<String> ret = new ArrayList<String>();
        for (int nodeIdx = 0; nodeIdx < nodes.getLength(); ++nodeIdx) {
            String extractedText = extractTextFromNode(nodes.item(nodeIdx));
            extractedText = extractedText.trim();
            if (!extractedText.isEmpty()) {
                ret.add(extractedText);
            }
        }
        return ret;
    }

    public static List<String> extractChildrenAsTextList(Node node) {
        List<String> ret = new ArrayList<String>();

        if (node == null) {
            return ret;
        }
        if (node.getChildNodes().getLength() == 0 && node.getNodeValue() != null) {
            ret.add(node.getNodeValue());
        } else {
            for (int childIdx = 0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
                ret.addAll(extractChildrenAsTextList(node.getChildNodes().item(childIdx)));
            }
        }
        return ret;
    }

}
