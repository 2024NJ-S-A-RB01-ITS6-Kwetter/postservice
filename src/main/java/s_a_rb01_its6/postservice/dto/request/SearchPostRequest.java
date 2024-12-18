package s_a_rb01_its6.postservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchPostRequest {
    @NotBlank
    private int page;
    @NotBlank
    private int size;
    @NotBlank
    private String query;
}
