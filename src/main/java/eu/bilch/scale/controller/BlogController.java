package eu.bilch.scale.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.bilch.scale.repository.PostRepository;

@Controller
@RequestMapping("/blog/posts")
public class BlogController {
    private final PostRepository postRepository;

    public BlogController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepository.findAllByOrderByCreatedAtDesc());
        return "blog/posts";
    }

    @GetMapping("/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found")));
        return "blog/post";
    }
}