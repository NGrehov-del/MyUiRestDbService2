package ru.itlearn.MyUiRestDbService;
import ru.itlearn.MyUiRestDbService.entity.*;
import ru.itlearn.MyUiRestDbService.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
 properties="spring.datasource.url=jdbc:h2:mem:security8;DB_CLOSE_DELAY=-1")
class SecurityIntegrationTest {
 @LocalServerPort int port;
 @Autowired UserRepository users;
 @Autowired RoleRepository roles;
 @Autowired StudentRepository students;
 @Autowired StudyGroupRepository groups;
 @Autowired PasswordEncoder encoder;
 private HttpClient client;
 private static final String PASSWORD="Lab8-Test-482!";
 @BeforeEach void browser(){client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();}
 private String enc(String text){return URLEncoder.encode(text,StandardCharsets.UTF_8);}
 private HttpResponse<String> request(String method,String path,String body,int status)throws Exception {
  var req=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
   .header("Content-Type","application/x-www-form-urlencoded")
   .method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build();
  var response=client.send(req,HttpResponse.BodyHandlers.ofString());
  assertEquals(status,response.statusCode(),response.body());return response;
 }
 private String token(String path)throws Exception {
  var html=request("GET",path,null,200).body();
  var match=Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"").matcher(html);
  assertTrue(match.find(),html);return match.group(1);
 }
 private String registration(String email){return "firstName="+enc("Иван")+"&lastName="+enc("Иванов")+"&email="+enc(email)+"&password="+enc(PASSWORD);}
 private String register()throws Exception {
  String email="lab8-"+UUID.randomUUID()+"@example.test";
  var response=request("POST","/register/save",registration(email)+"&_csrf="+enc(token("/register")),302);
  assertTrue(response.headers().firstValue("Location").orElse("").contains("/register?success"));return email;
 }
 private void login(String email)throws Exception {
  var response=request("POST","/login","email="+enc(email)+"&password="+enc(PASSWORD)+"&_csrf="+enc(token("/login")),302);
  assertTrue(response.headers().firstValue("Location").orElse("").endsWith("/users"));
 }
 @Test void anonymousCannotOpenProtectedPages()throws Exception {
  request("GET","/index",null,200);request("GET","/",null,302);
  for(String path:List.of("/list","/groups","/users","/actuator/info")){
   assertTrue(request("GET",path,null,302).headers().firstValue("Location").orElse("").endsWith("/login"));
  }
 }
 @Test void registrationStoresHashAndRole()throws Exception {
  String email=register();User saved=users.findByEmail(email);
  assertNotNull(saved);assertNotEquals(PASSWORD,saved.getPassword());assertTrue(encoder.matches(PASSWORD,saved.getPassword()));
  assertTrue(saved.getRoles().stream().anyMatch(role->role.getName().equals("ROLE_ADMIN")));
 }
 @Test void duplicateEmailIsRejectedCaseInsensitively()throws Exception {
  String email=register();long count=users.count();
  String html=request("POST","/register/save",registration(email.toUpperCase(Locale.ROOT))+"&_csrf="+enc(token("/register")),200).body();
  assertTrue(html.contains("уже зарегистрирована"));assertEquals(count,users.count());assertFalse(html.contains(PASSWORD));
 }
 @Test void invalidRegistrationIsNotSaved()throws Exception {
  long count=users.count();String html=request("POST","/register/save","firstName=&lastName=&email=bad&password=1&_csrf="+enc(token("/register")),200).body();
  assertTrue(html.contains("Введите имя"));assertTrue(html.contains("корректный email"));assertEquals(count,users.count());
 }
 @Test void registrationRequiresCsrf()throws Exception {
  request("POST","/register/save",registration("csrf@example.test"),403);assertNull(users.findByEmail("csrf@example.test"));
 }
 @Test void wrongPasswordIsRejected()throws Exception {
  String email=register();var result=request("POST","/login","email="+enc(email)+"&password=wrong&_csrf="+enc(token("/login")),302);
  assertTrue(result.headers().firstValue("Location").orElse("").endsWith("/login?error"));request("GET","/list",null,302);
 }
 @Test void userListNeverContainsPasswords()throws Exception {
  String email=register();login(email);String html=request("GET","/users",null,200).body();
  assertTrue(html.contains(email));assertFalse(html.contains(PASSWORD));assertFalse(html.contains(users.findByEmail(email).getPassword()));
 }
 @Test void logoutEndsSession()throws Exception {
  login(register());request("POST","/logout","_csrf="+enc(token("/users")),302);request("GET","/list",null,302);
  assertTrue(request("GET","/login?logout",null,200).body().contains("Вы вышли"));
 }
 @Test void nonAdminCannotListUsers()throws Exception {
  Role role=roles.findByName("ROLE_USER");if(role==null){role=new Role();role.setName("ROLE_USER");role=roles.save(role);}
  User basic=new User();basic.setName("Basic User");basic.setEmail("basic-"+UUID.randomUUID()+"@example.test");basic.setPassword(encoder.encode(PASSWORD));basic.setRoles(new ArrayList<>(List.of(role)));users.save(basic);
  login(basic.getEmail());request("GET","/users",null,403);request("GET","/list",null,200);
 }
 @Test void studentCrudWithSessionAndCsrf()throws Exception {
  login(register());String name="Тест";String values="name="+enc(name)+"&surname="+enc("Студент")+"&faculty=IT&age=25";
  request("POST","/saveStudent",values,403);
  request("POST","/saveStudent",values+"&_csrf="+enc(token("/addStudentForm")),302);
  var student=students.findAll().stream().filter(s->s.getName().equals(name)).findFirst().orElseThrow();int id=student.getId();
  request("POST","/saveStudent","id="+id+"&"+values.replace("age=25","age=26")+"&_csrf="+enc(token("/showUpdateForm?studentId="+id)),302);
  assertEquals(26,students.findById(id).orElseThrow().getAge());
  request("POST","/deleteStudent","studentId="+id+"&_csrf="+enc(token("/list")),302);assertFalse(students.existsById(id));
 }
 @Test void groupCrudRemainsAvailable()throws Exception {
  login(register());String values="name=RI-888&faculty=IT&curator=Test&course=1";
  request("POST","/saveGroup",values+"&_csrf="+enc(token("/addGroupForm")),302);
  var group=groups.findAll().stream().filter(g->g.getName().equals("RI-888")).findFirst().orElseThrow();int id=group.getId();
  request("POST","/saveGroup","id="+id+"&"+values.replace("course=1","course=2")+"&_csrf="+enc(token("/showGroupUpdateForm?groupId="+id)),302);
  assertEquals(2,groups.findById(id).orElseThrow().getCourse());
  request("POST","/deleteGroup","groupId="+id+"&_csrf="+enc(token("/groups")),302);assertFalse(groups.existsById(id));
 }
 @Test void invalidStudentIsNotSaved()throws Exception {
  login(register());long count=students.count();String html=request("POST","/saveStudent","name=&surname=Test&faculty=IT&age=-1&_csrf="+enc(token("/addStudentForm")),200).body();
  assertTrue(html.contains("Введите имя"));assertEquals(count,students.count());
 }
 @ParameterizedTest @ValueSource(strings={"health","info","metrics","mappings","beans"})
 void actuatorRequiresAuthenticatedAdmin(String endpoint)throws Exception {
  request("GET","/actuator/"+endpoint,null,302);login(register());request("GET","/actuator/"+endpoint,null,200);
 }
}
