package tfi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tfi.model.dto.ErrorResponse;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura excepciones y las convierte en respuestas HTTP apropiadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Maneja excepciones de autenticación.
     * IMPORTANTE: Siempre retorna el mismo mensaje genérico para prevenir
     * enumeración de usuarios.
     * 
     * @param ex La excepción de autenticación
     * @return 401 Unauthorized con mensaje genérico
     */
    @ExceptionHandler(AutenticacionException.class)
    public ResponseEntity<ErrorResponse> handleAutenticacionException(AutenticacionException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Maneja excepciones de registro.
     * Puede contener mensajes específicos sobre validaciones o duplicados.
     * 
     * @param ex La excepción de registro
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(RegistroException.class)
    public ResponseEntity<ErrorResponse> handleRegistroException(RegistroException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja excepciones de pacientes.
     * Puede contener mensajes específicos sobre validaciones o duplicados.
     * 
     * @param ex La excepción de paciente
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(PacienteException.class)
    public ResponseEntity<ErrorResponse> handlePacienteException(PacienteException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja excepciones de permisos insuficientes.
     * Se lanza cuando un usuario autenticado intenta acceder a un recurso
     * para el cual no tiene autoridad.
     * 
     * @param ex La excepción de permisos
     * @return 403 Forbidden con mensaje descriptivo
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Maneja errores de validación de Bean Validation.
     * Se lanza cuando los DTOs no cumplen con las anotaciones de validación.
     * 
     * @param ex La excepción de validación
     * @return 400 Bad Request con mensajes de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(mensaje, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja excepciones de argumentos ilegales.
     * Generalmente causadas por validaciones fallidas.
     * 
     * @param ex La excepción
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja cualquier otra excepción no capturada específicamente.
     * Último recurso para errores inesperados.
     * 
     * @param ex La excepción
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "Error interno del servidor: " + ex.getMessage(), 
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
