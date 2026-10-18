package ru.itlearn.MyUiRestDbService.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Entity @NoArgsConstructor @AllArgsConstructor @Table(name="STUDY_GROUPS")
public class StudyGroup {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Integer id;
 @NotBlank(message="Введите название группы") @Size(max=30,message="Не более 30 символов") @Column(nullable=false,length=30)
 private String name;
 @NotBlank(message="Введите факультет") @Size(max=50,message="Не более 50 символов") @Column(nullable=false,length=50)
 private String faculty;
 @NotBlank(message="Введите ФИО куратора") @Size(max=100,message="Не более 100 символов") @Column(nullable=false,length=100)
 private String curator;
 @NotNull(message="Введите курс") @Min(value=1,message="Курс от 1 до 6") @Max(value=6,message="Курс от 1 до 6") @Column(nullable=false)
 private Integer course;
}