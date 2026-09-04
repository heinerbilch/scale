package eu.bilch.scale.controller;

import eu.bilch.scale.model.Post;
import eu.bilch.scale.model.User;
import eu.bilch.scale.repository.PostRepository;
import eu.bilch.scale.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BlogControllerTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private BlogController blogController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListPosts() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        String viewName = blogController.listPosts(model, principal);

        verify(model).addAttribute(eq("posts"), any());
        assertEquals("blog/posts", viewName);
    }

    @Test
    public void testListPostsWithPrincipal() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());
        when(principal.getAttribute("email")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        String viewName = blogController.listPosts(model, principal);

        verify(model).addAttribute(eq("posts"), any());
        verify(model).addAttribute(eq("currentUser"), any());
        assertEquals("blog/posts", viewName);
    }

    @Test
    public void testShowPost() {
        Post post = new Post();
        post.setId(1L);
        when(postRepository.findByAuthor(anyLong())).thenReturn(Optional.of(post));

        String viewName = blogController.showPost(1L, model, principal);

        verify(model).addAttribute(eq("post"), any());
        assertEquals("blog/post", viewName);
    }

    @Test
    public void testShowPostWithPrincipal() {
        Post post = new Post();
        post.setId(1L);
        when(postRepository.findByAuthor(anyLong())).thenReturn(Optional.of(post));
        when(principal.getAttribute("email")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        String viewName = blogController.showPost(1L, model, principal);

        verify(model).addAttribute(eq("post"), any());
        verify(model).addAttribute(eq("currentUser"), any());
        assertEquals("blog/post", viewName);
    }

    @Test
    public void testShowPostNotFound() {
        when(postRepository.findByAuthor(anyLong())).thenReturn(Optional.empty());

        assertThrows(
            NoSuchElementException.class,
            () -> blogController.showPost(1L, model, principal)
        );
    }

    @Test
    public void testShowLandingPage() {
        Post post = new Post();
        post.setId(1L);
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(java.util.List.of(post));

        String viewName = blogController.showLandingPage(model);

        verify(model).addAttribute(eq("featuredPost"), any());
        verify(model).addAttribute(eq("recentPosts"), any());
        assertEquals("index", viewName);
    }

    @Test
    public void testShowLandingPageWithEmptyPosts() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        String viewName = blogController.showLandingPage(model);

        verify(model, never()).addAttribute(eq("featuredPost"), any());
        verify(model).addAttribute(eq("recentPosts"), any());
        assertEquals("index", viewName);
    }

    @Test
    public void testShowCreatePostFormWithPrincipal() {
        when(principal.getAttribute("email")).thenReturn("test@example.com");
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String viewName = blogController.showCreatePostForm(model, principal);

        verify(model).addAttribute(eq("post"), any());
        verify(model).addAttribute(eq("currentUser"), any());
        assertEquals("blog/create-post", viewName);
    }

    @Test
    public void testShowCreatePostFormWithoutPrincipal() {
        String viewName = blogController.showCreatePostForm(model, null);

        assertEquals("redirect:/login", viewName);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    public void testCreatePostWithPrincipal() {
        when(principal.getAttribute("email")).thenReturn("test@example.com");
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");

        String viewName = blogController.createPost(post, principal);

        verify(postRepository).save(any(Post.class));
        assertEquals("redirect:/blog/posts", viewName);
    }

    @Test
    public void testCreatePostWithoutPrincipal() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");

        String viewName = blogController.createPost(post, null);

        verify(postRepository, never()).save(any());
        assertEquals("redirect:/login", viewName);
    }
}