package ru.itlearn.MyUiRestDbService.repository;
import ru.itlearn.MyUiRestDbService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,Long> { User findByEmail(String email); }