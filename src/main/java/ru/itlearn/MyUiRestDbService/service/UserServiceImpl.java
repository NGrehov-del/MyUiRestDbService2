package ru.itlearn.MyUiRestDbService.service;
import ru.itlearn.MyUiRestDbService.dto.UserDto;
import ru.itlearn.MyUiRestDbService.entity.*;
import ru.itlearn.MyUiRestDbService.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
 private final UserRepository users;
 private final RoleRepository roles;
 private final PasswordEncoder encoder;
 public UserServiceImpl(UserRepository users,RoleRepository roles,PasswordEncoder encoder){this.users=users;this.roles=roles;this.encoder=encoder;}
 public static String normalize(String email){return email==null?null:email.strip().toLowerCase(Locale.ROOT);}
 @Override @Transactional
 public void saveUser(UserDto dto){
  String email=normalize(dto.getEmail());
  if(users.findByEmail(email)!=null)throw new IllegalArgumentException("Email уже зарегистрирован");
  if(dto.getPassword().getBytes(StandardCharsets.UTF_8).length>72)throw new IllegalArgumentException("Пароль должен занимать не более 72 байт UTF-8");
  User user=new User();user.setName(dto.getFirstName().strip()+" "+dto.getLastName().strip());user.setEmail(email);
  user.setPassword(encoder.encode(dto.getPassword()));
  // Учебная модель из методички: зарегистрированным пользователям назначается ROLE_ADMIN.
  Role role=roles.findByName("ROLE_ADMIN");
  if(role==null){role=new Role();role.setName("ROLE_ADMIN");role=roles.save(role);}
  user.setRoles(new ArrayList<>(List.of(role)));users.save(user);
 }
 @Override @Transactional(readOnly=true)
 public User findUserByEmail(String email){return users.findByEmail(normalize(email));}
 @Override @Transactional(readOnly=true)
 public List<UserDto> findAllUsers(){return users.findAll().stream().map(user->{
  UserDto dto=new UserDto();dto.setId(user.getId());dto.setEmail(user.getEmail());
  String[] names=user.getName().split(" ",2);dto.setFirstName(names[0]);dto.setLastName(names.length>1?names[1]:"");
  return dto;
 }).toList();}
}