package ru.itlearn.MyUiRestDbService.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itlearn.MyUiRestDbService.entity.Student;
public interface StudentRepository extends JpaRepository<Student,Integer> {}