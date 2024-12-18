package s_a_rb01_its6.postservice.service.impl.DTOConverter;

import s_a_rb01_its6.postservice.dto.response.IndividualPost;
import s_a_rb01_its6.postservice.repository.entities.PostEntity;

public final class PostDTOConverter {
    private PostDTOConverter() {
    }

    public static IndividualPost convertToIndividualPost(PostEntity postEntity) {
        return IndividualPost.builder().
                id(postEntity.getId()).
                AuthorId(postEntity.getAuthorId()).
                AuthorUsername(postEntity.getAuthorUsername()).
                createdAt(postEntity.getCreatedAt()).
                content(postEntity.getContent()).
                build();
    }
}