package s_a_rb01_its6.postservice.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadWordsException extends RuntimeException {
  public BadWordsException(String message) {
    super(message);
  }
}