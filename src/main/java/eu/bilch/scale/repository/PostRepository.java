package eu.bilch.scale.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import eu.bilch.scale.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"author"})
    List<Post> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"author"})
    List<Post> findByAuthorId(Long authorId);

    @EntityGraph(attributePaths = {"author"})
    List<Post> findByTitleContainingIgnoreCase(String title);

    @EntityGraph(attributePaths = {"author"})
    Optional<Post> findByIdWithAuthor(Long id);
}