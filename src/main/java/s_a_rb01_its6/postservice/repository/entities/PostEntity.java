package s_a_rb01_its6.postservice.repository.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id") // Map to the correct column name
    private String authorId;

    @Column(name = "author_username") // Map to the correct column name
    private String authorUsername;

    @Column(name = "created_at") // Ensure this matches the database column
    private Date createdAt;

    @Column(name = "content") // Ensure this matches the database column
    private String content;
}
