package ru.itlearn.MyUiRestDbService.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class MvcConfig implements WebMvcConfigurer {
 @Override public void addViewControllers(ViewControllerRegistry registry){
  registry.addRedirectViewController("/","/index");
 }
}
