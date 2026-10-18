package ru.itlearn.MyUiRestDbService.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Getter @Setter @NoArgsConstructor @Entity @Table(name="ROLES")
public class Role {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=50) private String name;
 @ManyToMany(mappedBy="roles") private List<User> users=new ArrayList<>();
}