package clear.solution.test.handler;

import clear.solution.test.exception.InvalidAgeException;
import clear.solution.test.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MESSAGE = "message";
    private static final String RESPONSE_CODE = "response_code";
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            InvalidAgeException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception exception) {
        Map<String, String> error = new HashMap<>();
        error.put(RESPONSE_CODE, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        if (exception.getClass().equals(MethodArgumentNotValidException.class)) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError fieldError : ((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors()) {
                errorMessage.append(fieldError.getDefaultMessage()).append(". ");
            }
            error.put(MESSAGE, errorMessage.toString());
        } else {
            error.put(MESSAGE, exception.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(Exception exception) {
        Map<String, String> error = new HashMap<>();
        error.put(RESPONSE_CODE, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.put(MESSAGE, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
