package ru.itlearn.MyUiRestDbService.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Entity @NoArgsConstructor @AllArgsConstructor @Table(name="STUDENTS")
public class Student {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Integer id;
 @NotBlank(message="Введите имя") @Size(max=15,message="Не более 15 символов") @Column(nullable=false,length=15)
 private String name;
 @NotBlank(message="Введите фамилию") @Size(max=25,message="Не более 25 символов") @Column(nullable=false,length=25)
 private String surname;
 @NotBlank(message="Введите факультет") @Size(max=20,message="Не более 20 символов") @Column(nullable=false,length=20)
 private String faculty;
 @NotNull(message="Введите возраст") @Min(value=0,message="Возраст не может быть отрицательным") @Max(value=150,message="Возраст не более 150") @Column(nullable=false)
 private Integer age;
}