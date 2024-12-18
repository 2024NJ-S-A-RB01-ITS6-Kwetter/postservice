package s_a_rb01_its6.postservice.event;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdatedEvent implements Serializable {
    private String id;
    private String username;
}