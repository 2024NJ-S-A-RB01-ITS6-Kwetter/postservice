package s_a_rb01_its6.postservice.event;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMentionedEvent implements Serializable {
    //who got mentioned in the post
    private String id;
    private String username;
    //who mentioned you
    private String mentioned_id;
    private String mentioned_username;
    //post id
    private String post_id;
    //post content
    private String content;
    //post created time
    private String created_at;

}