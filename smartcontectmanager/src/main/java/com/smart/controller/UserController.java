package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contect;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.loggerutlity.GlobleResources;
import com.razorpay.*;


@Controller
@RequestMapping("/user")
public class UserController {

	private Logger logger=GlobleResources.getLogger(UserController.class);//configure the logger
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private MyOrderRepository myOrderRepository;
	//method for adding common data to response
	@ModelAttribute
	public String addCommonData(Model model,Principal principal) {

		String userName = principal.getName();
		System.out.println("User="+userName);
		
User userByUserName = this.userRepository.getUserByUserName(userName);
		
		System.out.println("User="+userByUserName);
		model.addAttribute("user", userByUserName);
		
		return " ";
	}
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		String userName = principal.getName();
		System.out.println("User="+userName); 
		User userByUserName = this.userRepository.getUserByUserName(userName);
		
		System.out.println("UserByUserName="+userByUserName);
		model.addAttribute("user", userByUserName);
		model.addAttribute("title","This is user Dasboard");
		
		return "normal/user_dashboard";
	}
	
	//open add contact handler
	@GetMapping("/add-contact")
	public String openAddContectForm(Model model) {
		
		model.addAttribute("title","This is our add contact");
		model.addAttribute("contact",new Contect());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contect contact,
			Principal p,
			@RequestParam("profileImage") MultipartFile file,
			HttpSession session) {
		
		try {
		String name=p.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		
		//processing of uploading file
		if(file.isEmpty()) {
			
			//if the file is empty then try our message
			System.out.println("File is empty");
			contact.setImage("contact.png");
		}
		
		else {
			//
			contact.setImage(file.getOriginalFilename());
			
			File file2 = new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("image is uploaded");
		}
		contact.setUser(user);
		user.getContects().add(contact);
		this.userRepository.save(user);
		System.out.println("Data"+contact);
		
		//success message
		session.setAttribute("message", new Message("Your contact is added!!", "success"));
		
		}catch(Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			//error message
			session.setAttribute("message", new Message("Something went to worng please try again!!", "danger"));
		}
		return "normal/add_contact_form";
	}
	
	//show one time only 5 data
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal p) {
		model.addAttribute("title","show contacts page");
		
		//send to contact list
//		String userName = p.getName();
//		User user2 = this.userRepository.getUserByUserName(userName);
//		List<Contect> contacts = user2.getContects();
		
		String userName = p.getName();
		User userName2 = this.userRepository.getUserByUserName(userName);
		
		Pageable page1 = PageRequest.of(page, 10);
		Page<Contect> userContacts = this.contactRepository.findContactsByUser(userName2.getId(),page1);
		
		//send data to frontend
		model.addAttribute("contacts",userContacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",userContacts.getTotalPages());
		
		
		
		return "normal/show_contacts";
		
	}
	
	//showing specific contact detail
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		System.out.println("cid"+cId);
		
		Optional<Contect> contactOptional = this.contactRepository.findById(cId);
		Contect contactDetail = contactOptional.get();
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		
		if(user.getId()==contactDetail.getUser().getId())
			
			model.addAttribute("contact",contactDetail);
		model.addAttribute("title",contactDetail.getName());
		
		return "normal/contact_detail";
	}
	
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,HttpSession httpSession) {
		
		try {
		Optional<Contect> optional = this.contactRepository.findById(cId);
		
		Contect conatct=this.contactRepository.findById(cId).get();
		
		System.out.println("contact"+conatct.getcId());
		
		conatct.setUser(null);
		
		this.contactRepository.delete(conatct);
		 //for send message use HttpSesion interface
		httpSession.setAttribute("message", new Message("contact deleted successfully !", "success"));
		
		
		
		
		}
		catch(Exception e){
			e.printStackTrace();
			
			
		}
		return "redirect:/user/show-contacts/0";
	}
	
	//open update form handler
	
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid,Model model) {
		
		model.addAttribute("title","update contact");
		Contect contect = this.contactRepository.findById(cid).get();
		model.addAttribute("contact",contect);
		return "normal/update_form";
		
		
	}
	
	//update contact handler
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contect contact,
			@RequestParam("profileImage") MultipartFile file,
			Model m,HttpSession session,Principal principal) {
		try {
			//image 
			
			//old contact detail
			Contect oldDetail = this.contactRepository.findById(contact.getcId()).get();
			if(!file.isEmpty()) {
				
				//file work
				//rewrite
				
				//delete old photo
				File deletefile = new ClassPathResource("static/img").getFile();
				File file1=new File(deletefile,oldDetail.getImage());
				file1.delete();
				
				//update new photo
				File file2 = new ClassPathResource("static/img").getFile();
				
				
				Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
			}
			
			else {
				
				contact.setImage(oldDetail.getImage());
			}
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			
			System.out.println("user"+user);
		
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated", "success"));
			
			
		} catch (Exception e) {
			
		}
	System.out.println("contact"+contact.getName());
	System.out.println("id" +contact.getcId());
		return "redirect:/user/"+contact.getcId()+"/contact";
				
	}
	
	//your profile detail
	
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title","Your profile");
		return "normal/profile";
	}
	
	//creating order for payment
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createorder(@RequestBody Map<String,Object> data,Principal principal) throws Exception {
		
		String methodName="createorder()";
		logger.info(methodName+ " called");
		//use Principal interface for get current user
		//System.out.println("Hey order function is executed....");
		
		System.out.println(data);
		
		//accept data from client payment request by using request body
		//if we have not entity class then we can accept data in map 
		//extract the data from data variable
		int amt = Integer.parseInt(data.get("amount").toString());
		
		var client = new RazorpayClient("rzp_test_gt30kVDiLL2Cu6","oDa3dRZ3XsQAp85yQAmKDgue");
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100);  //convert amount in paisa
		ob.put("currency","INR");
		ob.put("receipt", "txn_2345");
		
		
		//creating new order
		Order order = client.orders.create(ob);
		System.out.println(order);
		
		
		//save data in database which is received from client by implementing JPA repository
		MyOrder myOrder=new MyOrder();
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));//get login user from database by using userRepository object
		myOrder.setReceipt(order.get("receipt"));
		this.myOrderRepository.save(myOrder);// save all data in database using myOrderRepository save method
		
		
		return order.toString();
	}
	
	
	//update payment details 
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String,Object> data){
		
		MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString()); //convert data into String form because data is receive in jason format 
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myOrder);
		
		System.out.println(data);
		
		return ResponseEntity.ok(Map.of("msg","updated"));
				
	}
}
