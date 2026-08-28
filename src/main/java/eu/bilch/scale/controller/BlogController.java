package eu.bilch.scale.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import eu.bilch.scale.model.Post;
import eu.bilch.scale.repository.PostRepository;

@Controller
public class BlogController {
    private final PostRepository postRepository;

    public BlogController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String showLandingPage(Model model) {
        var allPosts = postRepository.findAllByOrderByCreatedAtDesc();
        if (!allPosts.isEmpty()) {
            model.addAttribute("featuredPost", allPosts.get(0));
        }
        var recentPosts = allPosts.stream().limit(3).toList();
        model.addAttribute("recentPosts", recentPosts);
        return "index";
    }

    @GetMapping("/blog/posts")
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepository.findAllByOrderByCreatedAtDesc());
        return "blog/posts";
    }

    @GetMapping("/blog/posts/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postRepository.findById(id).get());
        return "blog/post";
    }

    @GetMapping("/blog/posts/new")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new Post());
        return "blog/create-post";
    }

    @PostMapping("/blog/posts")
    public String createPost(@ModelAttribute Post post) {
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
        return "redirect:/blog/posts";
    }
}
