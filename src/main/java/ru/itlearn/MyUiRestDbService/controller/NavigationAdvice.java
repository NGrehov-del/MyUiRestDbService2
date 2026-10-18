package ru.itlearn.MyUiRestDbService.controller;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;
@ControllerAdvice
public class NavigationAdvice {
 @ModelAttribute("signedIn") public boolean signedIn(Principal principal){return principal!=null;}
 @ModelAttribute("loginEmail") public String email(Principal principal){return principal==null?"":principal.getName();}
}