package br.com.nobre.api.controller;

import br.com.nobre.api.dto.AuthDtos.*;
import br.com.nobre.api.model.User;
import br.com.nobre.api.repository.UserRepository;
import br.com.nobre.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final PasswordEncoder passwords; private final AuthService auth; private final String adminEmail;
 public AuthController(UserRepository users, PasswordEncoder passwords, AuthService auth, @Value("${app.admin-email}") String adminEmail) {
   this.users=users; this.passwords=passwords; this.auth=auth; this.adminEmail=normalize(adminEmail);
 }
 @PostMapping("/register") ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
   var email=normalize(req.email());
   if(users.findByEmailIgnoreCase(email).isPresent()) return ResponseEntity.status(409).body(Map.of("error","Já existe uma conta com este e-mail."));
   var user=new User(); user.name=req.name().trim(); user.email=email; user.phone=blankToNull(req.phone());
   user.passwordHash=passwords.encode(req.password()); user.role=email.equals(adminEmail)?User.Role.ADMIN:User.Role.CUSTOMER;
   try { users.saveAndFlush(user); }
   catch(DataIntegrityViolationException e) { return ResponseEntity.status(409).body(Map.of("error","Já existe uma conta com este e-mail.")); }
   return ResponseEntity.status(201).header(HttpHeaders.SET_COOKIE, auth.sessionCookie(user).toString()).body(UserResponse.from(user));
 }
 @PostMapping("/login") ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
   var user=users.findByEmailIgnoreCase(normalize(req.email())).orElse(null);
   if(user==null || !passwords.matches(req.password(),user.passwordHash)) return ResponseEntity.status(401).body(Map.of("error","E-mail ou senha inválidos."));
   return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,auth.sessionCookie(user).toString()).body(UserResponse.from(user));
 }
 @GetMapping("/me") ResponseEntity<?> me(HttpServletRequest req) { return auth.currentUser(req).<ResponseEntity<?>>map(u->ResponseEntity.ok(UserResponse.from(u))).orElseGet(()->ResponseEntity.status(401).body(Map.of("error","Não autenticado."))); }
 @PostMapping("/logout") ResponseEntity<?> logout() { return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,auth.clearCookie().toString()).body(Map.of("ok",true)); }
 private static String normalize(String s){return s.trim().toLowerCase(Locale.ROOT);} private static String blankToNull(String s){return s==null||s.isBlank()?null:s.trim();}
}
