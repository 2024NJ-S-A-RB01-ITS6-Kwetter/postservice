package s_a_rb01_its6.postservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadWordResponse {
    @JsonProperty("containsBadWords")
    private Boolean denied;
}