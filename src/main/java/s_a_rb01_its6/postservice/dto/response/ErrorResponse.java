package s_a_rb01_its6.postservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private final int status;
    private final String error;
    private final String message;
    private final List<String> details; // Specific field errors
}
