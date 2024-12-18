package s_a_rb01_its6.postservice.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedDataAccessException extends ResponseStatusException {
    public UnauthorizedDataAccessException(String errorCause) {
        super(HttpStatus.FORBIDDEN, errorCause);
    }
}