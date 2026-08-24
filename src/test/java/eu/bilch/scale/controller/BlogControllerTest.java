package eu.bilch.scale.controller;

import eu.bilch.scale.model.Post;
import eu.bilch.scale.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlogControllerTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private Model model;

    @InjectMocks
    private BlogController blogController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListPosts() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        String viewName = blogController.listPosts(model);

        verify(model).addAttribute(eq("posts"), any());
        assert "blog/posts".equals(viewName);
    }

    @Test
    public void testShowPost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));

        String viewName = blogController.showPost(1L, model);

        verify(model).addAttribute(eq("post"), any());
        assert "blog/post".equals(viewName);
    }

    @Test
    public void testShowPostNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            blogController.showPost(1L, model);
        } catch (ResourceNotFoundException e) {
            assert "Post not found".equals(e.getMessage());
        }
    }
}