package ru.itlearn.MyUiRestDbService.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Getter @Setter @NoArgsConstructor @Entity @Table(name="USERS")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=201) private String name;
 @Column(nullable=false,unique=true,length=254) private String email;
 @Column(nullable=false,length=100) private String password;
 @ManyToMany(fetch=FetchType.EAGER)
 @JoinTable(name="USERS_ROLES",joinColumns=@JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="role_id"))
 private List<Role> roles=new ArrayList<>();
}