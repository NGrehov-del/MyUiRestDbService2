package ru.itlearn.MyUiRestDbService.service;
import ru.itlearn.MyUiRestDbService.dto.UserDto;
import ru.itlearn.MyUiRestDbService.entity.User;
import java.util.List;
public interface UserService {
 void saveUser(UserDto user);
 User findUserByEmail(String email);
 List<UserDto> findAllUsers();
}