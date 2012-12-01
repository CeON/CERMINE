package pl.edu.icm.cermine.web.controller;

import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringEscapeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import pl.edu.icm.cermine.service.CermineExtractorService;
import pl.edu.icm.cermine.service.ExtractionTask;
import pl.edu.icm.cermine.service.NoSuchTaskException;
import pl.edu.icm.cermine.service.TaskManager;

/**
 *
 * @author bart
 * @author axnow
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
        //you may use 'warning' to show problem.
        return "home";
    }
    @RequestMapping(value = "/about.html")
    public String showAbout(Model model) {
        return "about";
    }

    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public String uploadFileStream(@RequestParam("files") MultipartFile file, HttpServletRequest request, Model model) {
        logger.info("Got an upload request.");
        try {
            byte[] content = file.getBytes();
            if(content.length==0) {
                model.addAttribute("warning", "An empty or no file sent.");
                return "home";
            }
            String filename = file.getOriginalFilename();
            logger.debug("Original filename is: " + filename);
            filename = taskManager.getProperFilename(filename);
            logger.debug("Created filename: "+filename);
            long taskId = extractorService.initExtractionTask(content, filename);
            logger.debug("Task manager is: " + taskManager);
            return "redirect:/task.html?task=" + taskId;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
        }
        return new ModelAndView("task", model);
    }

    @RequestMapping(value = "/tasks.html")
    public ModelAndView showTasks() {
        return new ModelAndView("tasks", "tasks", taskManager.taskList());
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
