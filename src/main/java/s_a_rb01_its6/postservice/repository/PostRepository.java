package s_a_rb01_its6.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s_a_rb01_its6.postservice.repository.entities.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    //TODO database decision.

    void deleteAllByAuthorId(Long userId);

}
