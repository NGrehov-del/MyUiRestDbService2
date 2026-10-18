package ru.itlearn.MyUiRestDbService.service;
import ru.itlearn.MyUiRestDbService.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
 private final UserRepository repository;
 public CustomUserDetailsService(UserRepository repository){this.repository=repository;}
 @Override @Transactional(readOnly=true)
 public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
  var user=repository.findByEmail(UserServiceImpl.normalize(email));
  if(user==null)throw new UsernameNotFoundException("Неверный email или пароль");
  return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),
    user.getRoles().stream().map(role->new SimpleGrantedAuthority(role.getName())).toList());
 }
}