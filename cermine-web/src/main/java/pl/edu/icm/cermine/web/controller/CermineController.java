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

package pl.edu.icm.cermine.web.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.util.Collections.singletonList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.transformers.BibEntryToNLMConverter;
import pl.edu.icm.cermine.metadata.affiliation.CRFAffiliationParser;
import pl.edu.icm.cermine.service.*;

/**
 * @author bart
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
 */
@org.springframework.stereotype.Controller
public class CermineController {

    @Autowired
    CermineExtractorService extractorService;
    @Autowired
    TaskManager taskManager;
    Logger logger = LoggerFactory.getLogger(CermineController.class);

    @RequestMapping(value = "/index.html")
    public String showHome(Model model) {
        return "home";
    }

    @RequestMapping(value = "/about.html")
    public String showAbout(Model model) {
        return "about";
    }

    @RequestMapping(value = "/download.html")
    public ResponseEntity<String> downloadXML(@RequestParam("task") long taskId,
            @RequestParam("type") String resultType, Model model) throws NoSuchTaskException {
        ExtractionTask task = taskManager.getTask(taskId);
        if ("nlm".equals(resultType)) {
            String nlm = task.getResult().getNlm();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "application/xml;charset=utf-8");
            return new ResponseEntity<String>(nlm, responseHeaders, HttpStatus.OK);
        } else {
            throw new RuntimeException("Unknown request type: " + resultType);
        }
    }
    
    @RequestMapping(value = "/examplepdf.html", method = RequestMethod.GET)
    public void getExamplePDF(@RequestParam("file") String filename, HttpServletRequest request, HttpServletResponse response) { 
        InputStream in = null;
        OutputStream out = null;
        try {
            if (!filename.matches("^example\\d+\\.pdf$")) {
                throw new RuntimeException("No such example file!");
            }
            response.setContentType("application/pdf");
            in = CermineController.class.getResourceAsStream("/examples/"+filename);
            if (in == null) {
                throw new RuntimeException("No such example file!");
            }
            
            out = response.getOutputStream();
        
            byte[] buf = new byte[1024]; 
            int len; 
            while ((len = in.read(buf)) > 0) { 
                out.write(buf, 0, len); 
            } 
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
            }
        }
    } 

    @RequestMapping(value = "/uploadexample.do", method = RequestMethod.GET)
    public String uploadExampleFileStream(@RequestParam("file") String filename, HttpServletRequest request, Model model) {
        if (!filename.matches("^example\\d+\\.pdf$")) {
            throw new RuntimeException("No such example file!");
        }
        logger.info("Got an upload request.");
        try {
            InputStream in = CermineController.class.getResourceAsStream("/examples/"+filename);
            if (in == null) {
                throw new RuntimeException("No such example file!");
            }
        
            byte[] content = IOUtils.toByteArray(in);
            if (content.length == 0) {
                model.addAttribute("warning", "An empty or no file sent.");
                return "home";
            }
            logger.debug("Original filename is: " + filename);
            filename = taskManager.getProperFilename(filename);
            logger.debug("Created filename: " + filename);
            long taskId = extractorService.initExtractionTask(content, filename);
            logger.debug("Task manager is: " + taskManager);
            return "redirect:/task.html?task=" + taskId;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public String uploadFileStream(@RequestParam("files") MultipartFile file, HttpServletRequest request, Model model) {
        logger.info("Got an upload request.");
        try {
            byte[] content = file.getBytes();
            if (content.length == 0) {
                model.addAttribute("warning", "An empty or no file sent.");
                return "home";
            }
            String filename = file.getOriginalFilename();
            logger.debug("Original filename is: " + filename);
            filename = taskManager.getProperFilename(filename);
            logger.debug("Created filename: " + filename);
            long taskId = extractorService.initExtractionTask(content, filename);
            logger.debug("Task manager is: " + taskManager);
            return "redirect:/task.html?task=" + taskId;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @RequestMapping(value = "/extract.do", method = RequestMethod.POST)
    public ResponseEntity<String> extractSync(@RequestBody byte[] content,
            HttpServletRequest request,
            Model model) {
        try {
            logger.debug("content length: {}", content.length);
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_XML);
            ExtractionResult result = extractorService.extractNLM(new ByteArrayInputStream(content));
            String nlm = result.getNlm();
            return new ResponseEntity<String>(nlm, responseHeaders, HttpStatus.OK);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(CermineController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<String>("Exception: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "/parse.do", method = RequestMethod.POST)
    public ResponseEntity<String> parseSync(HttpServletRequest request, Model model) { 
        try {
            String refText = request.getParameter("reference");
            if (refText == null) {
                refText = request.getParameter("ref");
            }
            String affText = request.getParameter("affiliation");
            if (affText == null) {
                affText = request.getParameter("aff");
            }
            
            if (refText == null && affText == null) {
                return new ResponseEntity<String>(
                        "Exception: \"reference\" or \"affiliation\" parameter has to be passed!\n", null, 
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            HttpHeaders responseHeaders = new HttpHeaders();
            String response;

            if (refText != null) {
            
                String format = request.getParameter("format");
                if (format == null) {
                    format = "bibtex";
                }
                format = format.toLowerCase();
                if (!format.equals("nlm") && !format.equals("bibtex")) {
                    return new ResponseEntity<String>(
                            "Exception: format must be \"bibtex\" or \"nlm\"!\n", null, 
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            
                CRFBibReferenceParser parser = CRFBibReferenceParser.getInstance();
                BibEntry reference = parser.parseBibReference(refText);
                if (format.equals("bibtex")) {
                    responseHeaders.setContentType(MediaType.TEXT_PLAIN);
                    response = reference.toBibTeX();
                } else {
                    responseHeaders.setContentType(MediaType.APPLICATION_XML);
                    BibEntryToNLMConverter converter = new BibEntryToNLMConverter();
                    Element element = converter.convert(reference);
                    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                    response = outputter.outputString(element);
                }
            } else {
                responseHeaders.setContentType(MediaType.APPLICATION_XML);
                CRFAffiliationParser parser = new CRFAffiliationParser();
                Element parsedAff = parser.parse(affText);
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                response = outputter.outputString(parsedAff);
            }
            
            return new ResponseEntity<String>(response+"\n", responseHeaders, HttpStatus.OK);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(CermineController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<String>("Exception: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 

    @ExceptionHandler(value = NoSuchTaskException.class)
    public ModelAndView taskNotFoundHandler(NoSuchTaskException nste) {
        return new ModelAndView("error", "errorMessage", nste.getMessage());
    }

    @RequestMapping(value = "/task.html", method = RequestMethod.GET)
    public ModelAndView showTask(@RequestParam("task") long id) throws NoSuchTaskException {
        ExtractionTask task = taskManager.getTask(id);

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("task", task);
        if (task.isFinished()) {
            model.put("result", task.getResult());
            String nlmHtml = StringEscapeUtils.escapeHtml(task.getResult().getNlm());
            model.put("nlm", nlmHtml);
            model.put("meta", task.getResult().getMeta());
            model.put("html", task.getResult().getHtml());
        }
        return new ModelAndView("task", model);
    }

    @RequestMapping(value = "/tasks.html")
    public ModelAndView showTasks() {
        return new ModelAndView("tasks", "tasks", taskManager.taskList());
    }

    private static ResponseEntity<List<Map<String, Object>>> wrapResponse(Map<String, Object> rBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<List<Map<String, Object>>>(singletonList(rBody), headers, HttpStatus.OK);
    }

    private static Map<String, Object> fileDetails(MultipartFile file, int size) {
        Map<String, Object> rBody = new HashMap<String, Object>();
        rBody.put("name", file.getOriginalFilename());
        rBody.put("size", size);
        return rBody;
    }

    public CermineExtractorService getExtractorService() {
        return extractorService;
    }

    public void setExtractorService(CermineExtractorService extractorService) {
        this.extractorService = extractorService;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
}
