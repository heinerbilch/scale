package eu.bilch.scale.controller;

import eu.bilch.scale.model.Post;
import eu.bilch.scale.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class BlogControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostRepository postRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        postRepository.deleteAll();
    }

    @Test
    public void testListPosts() throws Exception {
        // Given
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        // When & Then
        mockMvc.perform(get("/blog/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/posts"))
                .andExpect(model().attributeExists("posts"));
//                .andExpect(model().attribute("posts", List.of(post)));
    }

    @Test
    public void testShowPost() throws Exception {
        // Given
        Post post = new Post();
        post.setTitle("Test Post 2");
        post.setContent("Test Content");
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        // When & Then
        mockMvc.perform(get("/blog/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", post));
    }

    @Test
    public void testShowPostNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/blog/posts/{id}", 999L))
                .andExpect(status().isInternalServerError());
    }
}