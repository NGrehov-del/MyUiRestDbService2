package ru.itlearn.MyUiRestDbService.controller;
import ru.itlearn.MyUiRestDbService.entity.StudyGroup;
import ru.itlearn.MyUiRestDbService.repository.StudyGroupRepository;
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
public class StudyGroupController {
 private final StudyGroupRepository repository;
 public StudyGroupController(StudyGroupRepository repository) { this.repository=repository; }
 @GetMapping("/groups")
 public String list(Model model) {
  model.addAttribute("groups",repository.findAll(Sort.by("id")));
  return "list-groups";
 }
 @GetMapping("/addGroupForm")
 public String add(Model model) { model.addAttribute("group",new StudyGroup()); return "add-group-form"; }
 @GetMapping("/showGroupUpdateForm")
 public String edit(@RequestParam int groupId,Model model) {
  model.addAttribute("group",find(groupId));return "add-group-form";
 }
 @PostMapping("/saveGroup")
 public String save(@Valid @ModelAttribute("group") StudyGroup entity,BindingResult errors,RedirectAttributes redirect) {
  if(errors.hasErrors()) return "add-group-form";
  if(entity.getId()!=null) find(entity.getId());
  repository.save(entity);
  log.info("Сохранён StudyGroup: {}",entity);
  redirect.addFlashAttribute("message","Запись сохранена");
  return "redirect:/groups";
 }
 @PostMapping("/deleteGroup")
 public String delete(@RequestParam int groupId,RedirectAttributes redirect) {
  repository.delete(find(groupId));
  log.info("Удалён StudyGroup с id={}",groupId);
  redirect.addFlashAttribute("message","Запись удалена");
  return "redirect:/groups";
 }
 private StudyGroup find(int id) {
  return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Запись не найдена"));
 }
}