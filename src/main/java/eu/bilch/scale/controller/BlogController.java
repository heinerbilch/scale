package eu.bilch.scale.controller;

import java.time.LocalDateTime;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import eu.bilch.scale.model.Post;
import eu.bilch.scale.model.User;
import eu.bilch.scale.repository.PostRepository;
import eu.bilch.scale.repository.UserRepository;

@Controller
public class BlogController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public BlogController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
    public String listPosts(Model model, @AuthenticationPrincipal OAuth2User principal) {
        var allPosts = postRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("posts", allPosts);
        
        // Aktuellen User hinzufügen, falls eingeloggt
        if (principal != null) {
            String email = principal.getAttribute("email");
            userRepository.findByEmail(email).ifPresent(user -> {
                model.addAttribute("currentUser", user);
            });
        }
        return "blog/posts";
    }

    @GetMapping("/blog/posts/{id}")
    public String showPost(@PathVariable Long id, Model model, @AuthenticationPrincipal OAuth2User principal) {
        postRepository.findByIdWithAuthor(id).ifPresent(post -> {
            model.addAttribute("post", post);
        });
        
        // Aktuellen User hinzufügen, falls eingeloggt
        if (principal != null) {
            String email = principal.getAttribute("email");
            userRepository.findByEmail(email).ifPresent(user -> {
                model.addAttribute("currentUser", user);
            });
        }
        return "blog/post";
    }

    @GetMapping("/blog/posts/new")
    public String showCreatePostForm(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = new Post();
        post.setAuthor(user);
        model.addAttribute("post", post);
        model.addAttribute("currentUser", user);
        return "blog/create-post";
    }

    @PostMapping("/blog/posts")
    public String createPost(@ModelAttribute Post post, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        post.setAuthor(user);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
        return "redirect:/blog/posts";
    }
}
