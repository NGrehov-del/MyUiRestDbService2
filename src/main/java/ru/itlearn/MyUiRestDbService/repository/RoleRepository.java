package ru.itlearn.MyUiRestDbService.repository;
import ru.itlearn.MyUiRestDbService.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoleRepository extends JpaRepository<Role,Long> { Role findByName(String name); }