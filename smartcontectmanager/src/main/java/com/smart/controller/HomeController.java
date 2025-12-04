package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home -smart contect manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","about -smart contect manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title"," signUp smart contect manager");
		model.addAttribute("user",new User());
		return "signUp";
	}
	
	@PostMapping("/do_register")
	//@Valid annotation active the hibernate validation
	//use binding result for hibernate validation to binding result
	public String registerUser(@Valid @ModelAttribute ("user") User user,BindingResult result1,
			@RequestParam(value="agreement",defaultValue="false") boolean agreement,Model model, HttpSession session) {
		
		try {
			
			if(!agreement) {
				
				System.out.println("You have not agree term and condition");
				throw new Exception("You have not agree term and condition");
			}
			
			if(result1.hasErrors()) {
				
				System.out.println("Error"+result1.toString());
				model.addAttribute("user", user);
				return "signUp";
			}
			//set field database
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			
			System.out.println("agreement"+agreement);
			System.out.println("User"+user);
			
			//save data in database by user repository
			User result = this.userRepository.save(user);
			
			
			
			//return result on our form
			model.addAttribute("user",new User());
			//if register is success then it throw this message
			  session.setAttribute("message",new Message("Successfully register","alert-success"));
				 
				return "signUp";
				
		} catch (Exception e) {
		
			e.printStackTrace();
			model.addAttribute("user",user);
			
			
			  session.setAttribute("message",new Message("Something went to worng","alert-danger"));
			 
			return "signUp";
		}

	}
	
	@GetMapping("/signin")
	public String signin(Model model) {
		model.addAttribute("title","This is signin page");
		return "login";
	}
}
