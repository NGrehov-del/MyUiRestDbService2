package ru.itlearn.MyUiRestDbService.controller;
import ru.itlearn.MyUiRestDbService.entity.Student;
import ru.itlearn.MyUiRestDbService.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j @Controller
public class StudentController {
 private final StudentRepository repository;
 public StudentController(StudentRepository repository) { this.repository=repository; }
 @GetMapping("/list")
 public String list(Model model) {
  model.addAttribute("students",repository.findAll(Sort.by("id")));
  return "list-students";
 }
 @GetMapping("/addStudentForm")
 public String add(Model model) { model.addAttribute("student",new Student()); return "add-student-form"; }
 @GetMapping("/showUpdateForm")
 public String edit(@RequestParam int studentId,Model model) {
  model.addAttribute("student",find(studentId));return "add-student-form";
 }
 @PostMapping("/saveStudent")
 public String save(@Valid @ModelAttribute("student") Student entity,BindingResult errors,RedirectAttributes redirect) {
  if(errors.hasErrors()) return "add-student-form";
  if(entity.getId()!=null) find(entity.getId());
  repository.save(entity);
  log.info("Сохранён Student: {}",entity);
  redirect.addFlashAttribute("message","Запись сохранена");
  return "redirect:/list";
 }
 @PostMapping("/deleteStudent")
 public String delete(@RequestParam int studentId,RedirectAttributes redirect) {
  repository.delete(find(studentId));
  log.info("Удалён Student с id={}",studentId);
  redirect.addFlashAttribute("message","Запись удалена");
  return "redirect:/list";
 }
 private Student find(int id) {
  return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Запись не найдена"));
 }
}