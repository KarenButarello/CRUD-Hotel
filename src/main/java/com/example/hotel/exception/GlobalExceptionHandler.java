package com.example.hotel.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Para erros de validação de Bean Validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Erro de validação");
        response.put("details", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Para erros de integridade de dados (violações de restrições de BD)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        String mensagem = ex.getMostSpecificCause().getMessage();

        // Verificando mensagens específicas para personalizar a resposta
        if (mensagem.contains("data_nascimento") && mensagem.contains("cannot be null")) {
            response.put("message", "A data de nascimento é obrigatória");
        } else if (mensagem.toLowerCase().contains("duplicate") || mensagem.toLowerCase().contains("unique")) {
            response.put("message", "Já existe um registro com os dados informados");
        } else {
            response.put("message", "Erro de validação de dados");
        }

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Para ValidacaoException personalizada
    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Object> handleValidacaoException(ValidacaoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Para recursos não encontrados
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFound(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Para erros de formato JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Formato de dados inválido");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Para parâmetros obrigatórios faltando
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Parâmetro obrigatório ausente: " + ex.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Para exceções genéricas - captura qualquer exceção não tratada especificamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ocorreu um erro inesperado no servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DisponibilidadeException.class)
    public ResponseEntity<Object> handleDisponibilidadeException(DisponibilidadeException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}