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

package pl.edu.icm.cermine;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.*;
import org.apache.commons.lang.ArrayUtils;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CommandLineOptionsParser {
    
    private final Options options;
    
    private CommandLine commandLine;
    
    
    public CommandLineOptionsParser() {
        options = new Options();
        options.addOption("path", true, "file or directory path");
        options.addOption("outputs", true, "types of the output");
        options.addOption("exts", true, "extensions of the output files");
        options.addOption("ext", true, "metadata file extension");
        options.addOption("override", false, "override existing files");
        options.addOption("str", false, "store structure (TrueViz) files as well");
        options.addOption("strext", true, "structure file extension");
        options.addOption("configuration", true, "path to configuration file");
        options.addOption("threads", true, "number of threads used");
        options.addOption("timeout", true, "time in seconds");
    }
    
    public String parse(String[] args) throws ParseException {
        CommandLineParser clParser = new DefaultParser();
        commandLine = clParser.parse(options, args);
                
        if (commandLine.getOptionValue("path") == null) {
            return "\"path\" parameter not specified";
        }
        
        String output = commandLine.getOptionValue("outputs");
        String exts = commandLine.getOptionValue("exts");
        if (output != null) {
            List<String> outputs = Lists.newArrayList(output.split(","));
            outputs.removeAll(Lists.newArrayList("jats", "text", "zones", "trueviz"));
            if (!outputs.isEmpty()) {
                return "Unknown output types: " + outputs;
            }
            if (exts != null && output.split(",").length != exts.split(",").length) {
                return "\"output\" and \"exts\" lists have different lengths";
            }
        }
        
        return null;
    }

    public String getPath() {
        return commandLine.getOptionValue("path");
    }
    
    public Map<String, String> getTypesAndExtensions() {
        Map<String, String> typesAndExts = new HashMap<String, String>();
        typesAndExts.put("jats", "cermxml");
        typesAndExts.put("text", "cermtxt");
        typesAndExts.put("zones", "cermzones");
        typesAndExts.put("trueviz", "cermstr");

        String[] types = this.getStringOptionValue("jats", "outputs").split(",");
        for (String type: Lists.newArrayList(typesAndExts.keySet())) {
            if (!ArrayUtils.contains(types, type)) {
                typesAndExts.remove(type);
            }
        }
        
        String exts = commandLine.getOptionValue("exts");
        if (exts != null) {
            String[] extArr = exts.split(",");
            if (types.length == extArr.length) {
                for (int i = 0; i < types.length; i++) {
                    typesAndExts.put(types[i], extArr[i]);
                }
            }
        }
        
        return typesAndExts;
    }
    
    public boolean override() {
        return commandLine.hasOption("override");
    }
    
    public String getNLMExtension() {
        return this.getStringOptionValue("cermxml", "ext");
    }
    
    public String getTextExtension() {
        return this.getStringOptionValue("cermtxt", "ext");
    }
    
    public boolean extractStructure() {
        return commandLine.hasOption("str");
    }
    
    public String getBxExtension() {
        return this.getStringOptionValue("cxml", "strext");
    }
    
    /**
     * @return timeout in seconds; Null if no timeout is set.
     */
    public Long getTimeout() {
        if (!commandLine.hasOption("timeout")) {
            return null;
        } else {
            Long value = Long.parseLong(commandLine.getOptionValue("timeout"));
            if (value < 0) {
                throw new RuntimeException("The 'timeout' value given as a " 
                        + "command line parameter has to be nonnegative.");
            }
            return value;
        }
    }
    
    public int getThreadsNumber() {
        if (commandLine.hasOption("threads")) {
            return Integer.valueOf(commandLine.getOptionValue("threads"));
        }
        return InternalContentExtractor.THREADS_NUMBER;
    }
    
    public String getConfigurationPath() {
        return getStringOptionValue(null, "configuration");
    }

    private String getStringOptionValue(String defaultValue, String name) {
        String value = defaultValue;
        if (commandLine.hasOption(name)) {
            value = commandLine.getOptionValue(name);
        }
        return value;
    }
    
}
