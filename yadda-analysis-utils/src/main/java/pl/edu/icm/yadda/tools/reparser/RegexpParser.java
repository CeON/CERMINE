/**
 * (C)2007 ICM Warsaw University
 *
 * package:	pl.edu.icm.yadda.tools.reparser
 * file:	RegexpParser.java
 * date:    2007-06-15
 * svnid:   $Id$
 */
package pl.edu.icm.yadda.tools.reparser;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.AnalysisException;

/**
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class RegexpParser {
	private static final Logger log = LoggerFactory.getLogger(RegexpParser.class);

	private NodeCategory mainNodeCategory;
	
	private static final String NAME = "name";
	private static final String NODE = "node";
	private static final String REGEXP = "regexp";
	private static final String TEMPLATE = "template";

	@SuppressWarnings("unchecked")
	private void loadConfiguration(String resource, String mainNode) throws AnalysisException {
		Iterator<String> iter = null;
		URL url = this.getClass().getClassLoader().getResource(resource);
        Configuration cfg;
		try {
			cfg = new PropertiesConfiguration(url);
		} catch (ConfigurationException e) {
			throw new AnalysisException(e);
		}

        /* Collect all nodes */
        Map<String, NodeCategory> nodes = new HashMap<String, NodeCategory>();
        iter = (Iterator<String>)cfg.getKeys(NODE);
		while (iter.hasNext()) {
			String nodeId = iter.next();
			nodeId = nodeId.replaceFirst("^" + NODE + "\\.", "").replaceFirst("\\..*$", "");
			if (!nodes.containsKey(nodeId))
				nodes.put(nodeId, new NodeCategory());
		}
		log.debug("Found nodes: " + nodes.keySet());
		if (!nodes.containsKey(mainNode))
			throw new AnalysisException("Main node not found in the configuration file.  The required node is: " + mainNode);
		
		for (Map.Entry<String, NodeCategory> entry : nodes.entrySet()) {
			NodeCategory node = entry.getValue();
			String id = entry.getKey();
			node.setId(id);

			String regexp = cfg.getString(NODE + "." + id + "." + REGEXP);
			if (regexp != null)
				node.setRegexp(regexp);

			String name = cfg.getString(NODE + "." + id + "." + NAME);
			if (name != null)
				node.setName(name);
			
	        iter = (Iterator<String>)cfg.getKeys(NODE + "." + id + "." + TEMPLATE);
			while (iter.hasNext()) {
				String type = iter.next();
				type = type.replaceFirst("^" + NODE + "\\." + id + "\\." + TEMPLATE + "\\.?+", "");
				List<String> templates = (List<String>)cfg.getList(NODE + "." + id + "." + TEMPLATE + "." + type);
				
				for (String template : templates) {
					Template t = new Template();
					t.setType(type);

					String[] fields = template.split("\\s+");
					for (String field : fields) {
						NodeCategory n = nodes.get(field);
						if (n == null)
							log.warn("Node '" + field + "' does not exist.  Referenced in template: " + template);
						t.addField(n);
					}
					node.addTemplate(t);
				}
			}
		}
		mainNodeCategory = nodes.get(mainNode);
	}

	public RegexpParser(String resource, String mainNode) throws AnalysisException {
		loadConfiguration(resource, mainNode);
	}
	
	public Node parse(String text) {
		return mainNodeCategory.match(text);
	}
}
