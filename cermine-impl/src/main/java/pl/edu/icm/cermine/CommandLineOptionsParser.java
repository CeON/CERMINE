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

package pl.edu.icm.cermine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.SVMAlternativeMetadataZoneClassifier;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CommandLineOptionsParser {
    
    private Options options;
    
    private CommandLine commandLine;
    
    
    public CommandLineOptionsParser() {
        options = new Options();
        options.addOption("path", true, "file or directory path");
        options.addOption("ext", true, "metadata file extension");
        options.addOption("str", false, "store structure (TrueViz) files as well");
        options.addOption("strext", true, "structure file extension");
        options.addOption("modelmeta", true, "path to metadata classifier model");
        options.addOption("modelinit", true, "path to initial classifier model");
        options.addOption("threads", true, "number of threads used");
        options.addOption("output", true, "output path");
        options.addOption("timeout", true, "time in seconds");
    }
    
    public boolean parse(String[] args) throws ParseException {
        CommandLineParser clParser = new GnuParser();
        commandLine = clParser.parse(options, args);
                
        return commandLine.getOptionValue("path") != null;
    }

    public String getPath() {
        return commandLine.getOptionValue("path");
    }

    public String getOutput() {
        return commandLine.getOptionValue("output");
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
    public Long getTimeout(){
    	if (!commandLine.hasOption("timeout")) {
    		return null;
    	} else {
    		return Long.parseLong(commandLine.getOptionValue("timeout"));
    	}
    }
    
    public void updateMetadataModel(ComponentConfiguration conf) throws AnalysisException, IOException {
        String model = getStringOptionValue(null, "modelmeta");
        String modelRange = model == null ? null : model + ".range";
        if ("alt-humanities".equals(model)) {
            conf.setMetadataZoneClassifier(SVMAlternativeMetadataZoneClassifier.getDefaultInstance());
        } else if (model != null) {
            InputStream modelIS = null;
            InputStream modelRangeIS = null;
            try {
                modelIS = new FileInputStream(model);
                try {
                    modelRangeIS = new FileInputStream(modelRange);
                    conf.setMetadataZoneClassifier(modelIS, modelRangeIS);
                } finally {
                    if (modelRangeIS != null) {
                       modelRangeIS.close();
                    }
                }
            } finally {
                if (modelIS != null) {
                    modelIS.close();
                }
            }
        }
    }
    
    public void updateInitialModel(ComponentConfiguration conf) throws AnalysisException, IOException {
        String model = getStringOptionValue(null, "modelinit");
        String modelRange = model == null ? null : model + ".range";
        if (model != null) {
            InputStream modelIS = null;
            InputStream modelRangeIS = null;
            try {
                modelIS = new FileInputStream(model);
                try {
                    modelRangeIS = new FileInputStream(modelRange);
                    conf.setInitialZoneClassifier(modelIS, modelRangeIS);
                } finally {
                    if (modelRangeIS != null) {
                       modelRangeIS.close();
                    }
                }
            } finally {
                if (modelIS != null) {
                    modelIS.close();
                }
            }
        }
    }
    
    public int getThreadsNumber() {
        if (commandLine.hasOption("threads")) {
            return Integer.valueOf(commandLine.getOptionValue("threads"));
        }
        return ContentExtractor.THREADS_NUMBER;
    }

    private String getStringOptionValue(String defaultValue, String name) {
        String value = defaultValue;
        if (commandLine.hasOption(name)) {
            value = commandLine.getOptionValue(name);
        }
        return value;
    }
    
}
