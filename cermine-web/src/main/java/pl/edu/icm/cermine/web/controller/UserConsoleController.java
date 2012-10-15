package pl.edu.icm.cermine.web.controller;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * 
 * @author bart
 */
@org.springframework.stereotype.Controller
public class UserConsoleController {
	Logger logger = LoggerFactory.getLogger(UserConsoleController.class);
	
	public static final String VIEW_USER_CONSOLE = "userConsole";
	
    @RequestMapping(value = "/userConsole", method = RequestMethod.GET)
    public String showForm(Model model) {
    	model.addAttribute("hi","hello!");
    	
    	return VIEW_USER_CONSOLE;
    }
    
    @RequestMapping(value = "/uploadPdf", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> uploadFile(@RequestParam("files[]") MultipartFile file, HttpServletRequest request) {
    
        byte[] content;
		try {
			content = file.getBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        logger.info("uploaded file: originalFilename={}, size={}B, type={}", new Object[]{file.getOriginalFilename(), content.length, file.getContentType()});
             
    	return uploadResponseOK(file, content.length);
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
