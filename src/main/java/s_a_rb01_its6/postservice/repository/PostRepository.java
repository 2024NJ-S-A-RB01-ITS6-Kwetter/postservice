package s_a_rb01_its6.postservice.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import s_a_rb01_its6.postservice.repository.entities.PostEntity;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long>, PagingAndSortingRepository<PostEntity, Long> {

    void deleteAllByAuthorId(String userId);

    @Modifying
    @Query("UPDATE PostEntity p SET p.authorUsername = :newUsername WHERE p.authorId = :authorId")
    void updateAllUsernamesByAuthorId(@Param("newUsername") String newUsername, @Param("authorId") String authorId);

    Page<PostEntity> findByContentContainingIgnoreCase(String query, Pageable pageable);

    @Query("SELECT DISTINCT p.authorId FROM PostEntity p WHERE p.authorUsername = :authorUsername")
    Optional<String> findDistinctAuthorIdByAuthorUsername(@Param("authorUsername") String authorUsername);

    Page<PostEntity> getAllByAuthorId(String userId, Pageable pageable);

}
