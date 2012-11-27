package pl.edu.icm.cermine.web.controller;

import com.google.common.io.CountingOutputStream;
import com.google.common.io.NullOutputStream;
import java.io.ByteArrayInputStream;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.icm.cermine.service.CermineExtractorService;
import pl.edu.icm.cermine.service.ExtractionResult;

/**
 *
 * @author bart
 */
@org.springframework.stereotype.Controller
public class UserConsoleController {
    @Autowired
    CermineExtractorService extractorService;

    Logger logger = LoggerFactory.getLogger(UserConsoleController.class);
    public static final String VIEW_USER_CONSOLE = "userConsole";
//
//    @RequestMapping(value = "/userConsole", method = RequestMethod.GET)
//    public String showForm(Model model) {
//        model.addAttribute("hi", "hello!");
//
//        return VIEW_USER_CONSOLE;
//    }
//
//    @RequestMapping(value = "/uploadPdf", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<List<Map<String, Object>>> uploadFile(@RequestParam("files[]") MultipartFile file, HttpServletRequest request) {
//
//        byte[] content;
//        try {
//            content = file.getBytes();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        logger.info("uploaded file: originalFilename={}, size={}B, type={}", new Object[]{file.getOriginalFilename(), content.length, file.getContentType()});
//
//        return uploadResponseOK(file, content.length);
//    }

    @RequestMapping(value = "/index.html")
    public String showForm2(Model model) {
        model.addAttribute("hi", "hello!");

        return "home";
    }

    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView uploadFileStream(@RequestParam("files") MultipartFile file, HttpServletRequest request) {
        logger.info("Got an upload request.");
        try {
//            
//            file.getInputStream();
//            CountingOutputStream cos = new CountingOutputStream(new NullOutputStream());
//            IOUtils.copy(file.getInputStream(), cos);
//            logger.info("Got {} bytes", cos.getCount());
            byte[] content = file.getBytes();
            ExtractionResult eres = extractorService.extractNLM(new ByteArrayInputStream(content));
            String nlmHtml = StringEscapeUtils.escapeHtml(eres.getNlm());
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put("message", "Aqqq");
            model.put("nlm", nlmHtml);
            model.put("result", eres);
            return new ModelAndView("result", model);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static ResponseEntity<List<Map<String, Object>>> uploadResponseOK(MultipartFile file, int size) {
        return wrapResponse(fileDetails(file, size));
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
}
