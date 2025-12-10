package tfi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tfi.application.dto.ErrorResponse;
import tfi.exception.ObraSocialException;

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
     * Maneja excepciones de obras sociales.
     * Puede ser por verificación de afiliación fallida, API no disponible, etc.
     * 
     * @param ex La excepción de obra social
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(ObraSocialException.class)
    public ResponseEntity<ErrorResponse> handleObraSocialException(ObraSocialException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones de atenciones médicas.
     * Puede contener mensajes específicos sobre validaciones, duplicados o estados inválidos.
     * 
     * @param ex La excepción de atención
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(AtencionException.class)
    public ResponseEntity<ErrorResponse> handleAtencionException(AtencionException ex) {
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
     * Maneja errores de parsing de JSON.
     * Se lanza cuando:
     * - El JSON está malformado (sintaxis incorrecta)
     * - Un campo no puede ser deserializado (ej: enum con valor inválido)
     * - Falta el body cuando es requerido
     * 
     * @param ex La excepción de lectura de mensaje HTTP
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String mensaje;


        if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            mensaje = "El cuerpo de la petición es obligatorio";
        } else if (ex.getMessage() != null && ex.getMessage().contains("JSON parse error")) {

            String originalMsg = ex.getMessage();
            if (originalMsg.contains("Cannot deserialize")) {

                if (originalMsg.contains("Autoridad")) {
                    mensaje = "Autoridad inválida. Valores permitidos: MEDICO, ENFERMERO";
                } else {
                    mensaje = "Valor inválido en el JSON para uno de los campos";
                }
            } else {
                mensaje = "El formato del JSON es inválido";
            }
        } else {
            mensaje = "Error al procesar el cuerpo de la petición. Verifica el formato JSON";
        }

        ErrorResponse error = new ErrorResponse(mensaje, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de tipo de argumento incorrecto.
     * Se lanza cuando un parámetro de URL no puede ser convertido al tipo esperado.
     * 
     * @param ex La excepción
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String mensaje = String.format(
            "El parámetro '%s' debe ser de tipo %s", 
            ex.getName(), 
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "válido"
        );
        ErrorResponse error = new ErrorResponse(mensaje, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de parámetros faltantes.
     * Se lanza cuando falta un parámetro requerido en la URL.
     * 
     * @param ex La excepción
     * @return 400 Bad Request con mensaje descriptivo
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String mensaje = String.format("Falta el parámetro requerido: '%s'", ex.getParameterName());
        ErrorResponse error = new ErrorResponse(mensaje, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de método HTTP no soportado.
     * Se lanza cuando se intenta usar un método HTTP (POST, GET, etc.) no permitido en un endpoint.
     * 
     * @param ex La excepción
     * @param request La solicitud HTTP
     * @return 405 Method Not Allowed con mensaje descriptivo
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {


        System.err.println("ERROR: Método HTTP no soportado");
        System.err.println("URL: " + request.getRequestURI());
        System.err.println("Método usado: " + request.getMethod());
        System.err.println("Métodos soportados: " + String.join(", ", ex.getSupportedMethods() != null ? ex.getSupportedMethods() : new String[]{"ninguno"}));

        String mensaje = String.format(
            "Método %s no soportado para %s. Métodos permitidos: %s",
            ex.getMethod(),
            request.getRequestURI(),
            ex.getSupportedHttpMethods() != null ? 
                String.join(", ", ex.getSupportedHttpMethods().stream().map(Object::toString).toArray(String[]::new)) : 
                "ninguno"
        );

        ErrorResponse error = new ErrorResponse(mensaje, HttpStatus.METHOD_NOT_ALLOWED.value());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /**
     * Maneja excepciones RuntimeException.
     * Generalmente contienen mensajes descriptivos sobre validaciones de negocio.
     * 
     * @param ex La excepción RuntimeException
     * @return 400 Bad Request con el mensaje específico de la excepción
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {

        String mensaje = ex.getMessage() != null && !ex.getMessage().trim().isEmpty()
            ? ex.getMessage()
            : "Error de validación: " + ex.getClass().getSimpleName();

        ErrorResponse error = new ErrorResponse(mensaje, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja cualquier otra excepción no capturada específicamente.
     * Último recurso para errores inesperados del servidor.
     * 
     * NOTA: Este handler solo debería activarse para errores REALES del servidor
     * (NullPointer, errores de BD, etc.), no para errores de validación del cliente.
     * 
     * @param ex La excepción
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        System.err.println("ERROR NO MANEJADO: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
            "Error interno del servidor. Por favor contacta al administrador.", 
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
