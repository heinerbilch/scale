package eu.bilch.scale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import eu.bilch.scale.controller.ResourceNotFoundException;
import eu.bilch.scale.model.Post;
import eu.bilch.scale.repository.PostRepository;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Cacheable(value = "posts", key = "#id")
    public Post findById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    @CacheEvict(value = "posts", key = "#post.id")
    public Post save(Post post) {
        return postRepository.save(post);
    }
}