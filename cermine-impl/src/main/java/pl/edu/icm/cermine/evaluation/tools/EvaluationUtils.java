/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.evaluation.tools;

import com.google.common.collect.Lists;
import java.io.*;
import java.util.*;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.general.DirExtractor;
import pl.edu.icm.cermine.tools.classification.general.DocumentsExtractor;

public class EvaluationUtils {
    public static List<BxDocument> getDocumentsFromPath(String inputDirPath) throws TransformationException
	{
		if (inputDirPath == null) {
			throw new NullPointerException("Input directory must not be null.");
		}

		if (!inputDirPath.endsWith(File.separator)) {
			inputDirPath += File.separator;
		}
		DocumentsExtractor extractor = new DirExtractor(inputDirPath);
		
		List<BxDocument> evaluationDocuments;
		evaluationDocuments = extractor.getDocuments();
		return evaluationDocuments;
	}
    
    public static BxDocument getDocument(File file) throws IOException, TransformationException {
    	TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
        BxDocument newDoc = new BxDocument();
        InputStream is = new FileInputStream(file);
        try {
            List<BxPage> pages = tvReader.read(new InputStreamReader(is));
            for(BxPage page: pages) {
                page.setParent(newDoc);
            }
            newDoc.setFilename(file.getName());
            newDoc.setPages(pages);
            return newDoc;
        } finally {
            if (is != null) {
            	is.close();
            }
        }
    }
    
    public static class DocumentsIterator implements Iterable<BxDocument> {

       	private File dir;
    	private int curIdx;
    	private File[] files;
        
        public DocumentsIterator(String dirPath) {
            this(dirPath, "cxml");
        }
        
        public DocumentsIterator(String dirPath, String extension) {
			if(!dirPath.endsWith(File.separator)) {
				dirPath += File.separator;
			}
    		this.dir = new File(dirPath);
    		this.curIdx = -1;
    		
            List<File> list = Lists.newArrayList(FileUtils.listFiles(dir, new String[]{extension}, true));
            this.files = list.toArray(new File[]{});
    	}
    	
		@Override
		public Iterator<BxDocument> iterator() {

	        return new Iterator<BxDocument>() {

	            @Override
	            public boolean hasNext() {
	                return curIdx + 1 < files.length;
	            }

	            @Override
	            public BxDocument next() {
	                ++curIdx;
        			try {
						return getDocument(files[curIdx]);
					} catch (IOException e) {
						return null;
					} catch (TransformationException e) {
						return null;
					}
	            }

	            @Override
	            public void remove() {
	                ++curIdx;
	            }
	        };
		}
    	
    }
  
    public static double compareStringsSW(String expectedText, String extractedText) {
        List<String> expectedTokens = StringTools.tokenize(expectedText.trim());
        List<String> extractedTokens = StringTools.tokenize(extractedText.trim());
        SmithWatermanDistance distanceFunc = new SmithWatermanDistance(.0, .0);
        double distance = distanceFunc.compare(expectedTokens, extractedTokens);
        return 2*distance / (double) (expectedTokens.size()+extractedTokens.size());
    }

    public static List<String> removeLeadingZerosFromDate(List<String> strings) {
        List<String> ret = new ArrayList<String>();
        for (String string : strings) {
            String[] parts = string.split("\\s");
            if (parts.length > 1) {
                List<String> newDate = new ArrayList<String>();
                for (String part : parts) {
                    newDate.add(part.replaceFirst("^0+(?!$)", ""));
                }
                ret.add(StringUtils.join(newDate, " "));
            } else {
                ret.add(string);
            }
        }
        return ret;
    }

    public static boolean isSubsequence(String str, String sub) {
        if (sub.isEmpty()) {
            return true;
        }
        if (str.isEmpty()) {
            return false;
        }
        if (str.charAt(0) == sub.charAt(0)) {
            return isSubsequence(str.substring(1), sub.substring(1));
        }
        return isSubsequence(str.substring(1), sub);
    }
    
    public static Comparator<String> defaultComparator =
        new Comparator<String>() {

        @Override
        public int compare(String t1, String t2) {
            return t1.trim().replaceAll(" +", " ").compareToIgnoreCase(t2.trim().replaceAll(" +", " "));
        }
    };
    
    public static Comparator<String> cosineComparator() {
        return cosineComparator(0.7);
    }
    
    public static Comparator<String> cosineComparator(final double threshold) {
        return new Comparator<String>() {

        @Override
        public int compare(String t1, String t2) {
            if (new CosineDistance().compare(StringTools.tokenize(t1), StringTools.tokenize(t2)) > threshold) {
                return 0;
            }
            return t1.compareToIgnoreCase(t2);
        }
    };
    }
    
    public static Comparator<String> swComparator =
        new Comparator<String>() {

        @Override
        public int compare(String t1, String t2) {
            String t1Norm = t1.replaceAll("[^a-zA-Z]", "");
            String t2Norm = t2.replaceAll("[^a-zA-Z]", "");
            if (compareStringsSW(t1, t2) >= .9 ||
                    (!t1Norm.isEmpty() && t1Norm.equals(t2Norm))) {
                return 0;
            }
            return t1.compareToIgnoreCase(t2);
        }
    };
    
    public static Comparator<String> authorComparator =
        new Comparator<String>() {

        @Override
        public int compare(String t1, String t2) {
            if (t1.toLowerCase().replaceAll("[^a-z]", "").equals(t2.toLowerCase().replaceAll("[^a-z]", ""))) {
                return 0;
            }
            return t1.trim().compareToIgnoreCase(t2.trim());
        }
    };
    
    public static Comparator<String> emailComparator =
        new Comparator<String>() {

        @Override
        public int compare(String t1, String t2) {
            String t1Norm = t1.toLowerCase().replaceAll("[^a-z0-9@]", "").replaceFirst("^e.?mail:? *", "");
            String t2Norm = t1.toLowerCase().replaceAll("[^a-z0-9@]", "").replaceFirst("^e.?mail:? *", "");
            
            if (t1Norm.equals(t2Norm)) {
                return 0;
            }
            return t1.trim().compareToIgnoreCase(t2.trim());
        }
    };
        
    public static Comparator<String> journalComparator =
        new Comparator<String>() {

        @Override
        public int compare(String t1, String t2) {
            if (EvaluationUtils.isSubsequence(t1.toLowerCase().replaceAll("[^a-z]", ""), t2.toLowerCase().replaceAll("[^a-z]", ""))) {
                return 0;
            }
            return t1.trim().compareToIgnoreCase(t2.trim());
        }
    };

    public static Comparator<String> yearComparator =
        new Comparator<String>() {

        @Override
        public int compare(String t1, String t2) {
            List<String> expected = Arrays.asList(t1.split("---"));
            List<String> extracted = Arrays.asList(t2.split("---"));
            Boolean match = DateComparator.yearsMatch(expected, extracted);
            if (match != null && match) {
                return 0;
            }
            return t1.trim().compareToIgnoreCase(t2.trim());
        }
    };
    
    public static Comparator<String> headerComparator(final Comparator<String> comp) {
        return new Comparator<String>() {

            @Override
            public int compare(String t1, String t2) {
                List<String> t1Lines = Lists.newArrayList(t1.split("\n"));
                List<String> t2Lines = Lists.newArrayList(t2.split("\n"));
                if (t1Lines.size() != t2Lines.size()) {
                    return -1;
                }
                for (int i = 0; i < t1Lines.size(); i++) {
                    if (t1Lines.get(i).equals(t2Lines.get(i))) {
                        continue;
                    }
                    String trimmed1 = t1Lines.get(i).trim();
                    String trimmed2 = t2Lines.get(i).trim();
                    if (t1Lines.get(i).length() - trimmed1.length() != t2Lines.get(i).length() - trimmed2.length()) {
                        return -1;
                    }
                    if (comp.compare(trimmed1, trimmed2) != 0) {
                        return -1;
                    }
                }
                return 0;
            }
        };
    }
    
 
    public static org.w3c.dom.Document elementToW3CDocument(org.jdom.Element elem) throws JDOMException {
        org.jdom.Document metaDoc = new org.jdom.Document();
        metaDoc.setRootElement(elem);
        org.jdom.output.DOMOutputter domOutputter = new DOMOutputter();
        return domOutputter.output(metaDoc);
    }

    public static String outputDoc(Document document) throws IOException, TransformerException {
        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);
        return out.toString();
    }
    
}
