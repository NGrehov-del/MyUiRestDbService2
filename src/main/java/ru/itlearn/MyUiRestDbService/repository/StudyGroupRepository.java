package ru.itlearn.MyUiRestDbService.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itlearn.MyUiRestDbService.entity.StudyGroup;
public interface StudyGroupRepository extends JpaRepository<StudyGroup,Integer> {}