package ru.itlearn.MyUiRestDbService.controller;
import ru.itlearn.MyUiRestDbService.dto.UserDto;
import ru.itlearn.MyUiRestDbService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SecurityController {
 private final UserService service;
 public SecurityController(UserService service){this.service=service;}
 @GetMapping("/index") public String home(){return "index";}
 @GetMapping("/login") public String login(){return "login";}
 @GetMapping("/register") public String registration(Model model){model.addAttribute("user",new UserDto());return "register";}
 @PostMapping("/register/save")
 public String register(@Valid @ModelAttribute("user")UserDto dto,BindingResult errors){
  if(!errors.hasFieldErrors("email")&&service.findUserByEmail(dto.getEmail())!=null)
   errors.rejectValue("email","duplicate","На этот email уже зарегистрирована учётная запись");
  if(errors.hasErrors()){dto.setPassword(null);return "register";}
  try{service.saveUser(dto);}catch(DataIntegrityViolationException e){errors.rejectValue("email","duplicate","На этот email уже зарегистрирована учётная запись");}
  catch(IllegalArgumentException e){errors.rejectValue("password","invalid",e.getMessage());}
  if(errors.hasErrors()){dto.setPassword(null);return "register";}
  return "redirect:/register?success";
 }
 @GetMapping("/users")public String users(Model model){model.addAttribute("users",service.findAllUsers());return "users";}
}