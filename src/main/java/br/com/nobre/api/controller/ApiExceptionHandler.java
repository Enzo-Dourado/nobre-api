package br.com.nobre.api.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){var message=e.getBindingResult().getFieldErrors().stream().findFirst().map(f->"Campo '"+f.getField()+"' inválido.").orElse("Dados inválidos.");return ResponseEntity.badRequest().body(Map.of("error",message));}
}
