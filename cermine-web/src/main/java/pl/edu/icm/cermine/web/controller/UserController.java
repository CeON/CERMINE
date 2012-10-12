package pl.edu.icm.cermine.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author bart
 */
@org.springframework.stereotype.Controller
@RequestMapping("/userConsole")
public class UserController {
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	public static final String VIEW_USER_CONSOLE = "userConsole";
	
    @RequestMapping(method = RequestMethod.GET)
    public String showForm(Model model) {
    	model.addAttribute("hi","hello!");
    	
    	return VIEW_USER_CONSOLE;
    }
}
