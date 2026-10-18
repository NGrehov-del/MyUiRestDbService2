package ru.itlearn.MyUiRestDbService.config;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableWebSecurity
public class WebSecurityConfig {
 @Bean public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean public SecurityFilterChain filterChain(HttpSecurity http)throws Exception {
  http.authorizeHttpRequests(auth->auth
    .requestMatchers("/","/index","/login","/register","/register/save","/style.css","/error").permitAll()
    .requestMatchers("/users","/actuator/**","/h2-console/**").hasRole("ADMIN")
    .anyRequest().authenticated())
   .formLogin(form->form.loginPage("/login").loginProcessingUrl("/login").usernameParameter("email")
       .defaultSuccessUrl("/users",true).failureUrl("/login?error").permitAll())
   .logout(logout->logout.logoutSuccessUrl("/login?logout").permitAll())
   .csrf(csrf->csrf.ignoringRequestMatchers("/h2-console/**"))
   .headers(headers->headers.frameOptions(frame->frame.sameOrigin()));
  return http.build();
 }
}