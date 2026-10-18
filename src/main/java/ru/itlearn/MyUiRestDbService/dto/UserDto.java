package ru.itlearn.MyUiRestDbService.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class UserDto {
 private Long id;
 @NotBlank(message="Введите имя") @Size(max=100,message="Не более 100 символов") private String firstName;
 @NotBlank(message="Введите фамилию") @Size(max=100,message="Не более 100 символов") private String lastName;
 @NotBlank(message="Введите email") @Email(message="Введите корректный email") @Size(max=254) private String email;
 @NotBlank(message="Введите пароль") @Size(min=6,max=72,message="Пароль от 6 до 72 символов") private String password;
}