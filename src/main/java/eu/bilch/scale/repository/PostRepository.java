package eu.bilch.scale.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import eu.bilch.scale.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
}