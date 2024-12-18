package s_a_rb01_its6.postservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualPost {

    private Long id;
    private String AuthorId;
    private String AuthorUsername;
    private Date createdAt;
    private String content;
}
