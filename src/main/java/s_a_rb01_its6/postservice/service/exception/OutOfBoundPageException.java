package s_a_rb01_its6.postservice.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OutOfBoundPageException extends ResponseStatusException {
    public OutOfBoundPageException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
