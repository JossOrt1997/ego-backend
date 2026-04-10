package com.analiticasoft.ida.exception;

import com.analiticasoft.ida.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest; // Importar
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // Importar
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importar
import org.springframework.web.bind.MethodArgumentNotValidException; // Importar
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException; // <-- Importante
import jakarta.persistence.EntityNotFoundException; // <-- Importante si no está ya

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Esta anotación activa el manejo global de excepciones para @RestControllers
public class GlobalExceptionHandler {

    // Maneja errores de credenciales inválidas (Login)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(), // 401
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), // "Unauthorized"
                "Credenciales inválidas.", // Mensaje amigable
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Maneja errores cuando no se encuentra un usuario (Login o Filtro JWT)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // 404
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found"
                ex.getMessage(), // Usamos el mensaje de la excepción ("Usuario no encontrado...")
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AccessDeniedException.class, SecurityException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(), // 403
                HttpStatus.FORBIDDEN.getReasonPhrase(), // "Forbidden"
                ex.getMessage(), // "No tiene permisos para modificar este rol"
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Maneja errores de EntityNotFoundException (404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // 404
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found"
                ex.getMessage(), // "Rol no encontrado" o "Cliente no encontrado"
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Maneja errores de validación de DTOs (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // Para errores de validación, es común devolver un mapa de errores por campo
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        body.put("error", "Validation Error");
        body.put("messages", errors); // El mapa de errores específicos
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Maneja cualquier otra excepción no capturada específicamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        // Loggear el stack trace para depuración interna (¡importante!)
        // log.error("Error inesperado:", ex); // Necesitarías añadir un logger

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), // "Internal Server Error"
                "Ocurrió un error inesperado en el servidor.", // Mensaje genérico para el usuario
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}