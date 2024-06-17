package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.BlogPost;
import com.hutech.VoTranQuocHuy430.service.BlogPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/blog")
public class BlogPostController {

    private final BlogPostService blogPostService;

    @Autowired
    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping
    public String getAllBlogPosts(Model model) {
        List<BlogPost> blogPosts = blogPostService.findAll();
        model.addAttribute("blogPosts", blogPosts);
        return "blog/list";
    }

    @GetMapping("/new")
    public String createBlogPostForm(Model model) {
        model.addAttribute("blogPost", new BlogPost());
        return "blog/create";
    }

    @PostMapping("/create")
    public String createBlogPost(@RequestParam("title") String title,
                                 @RequestParam("content") String content,
                                 @RequestParam("category") String category,
                                 @RequestParam("file") MultipartFile file,
                                 Principal principal) {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(title);
        blogPost.setContent(content);
        blogPost.setCategory(category);

        blogPostService.save(blogPost, principal.getName(), file);
        return "redirect:/blog";
    }

    @GetMapping("/{id}")
    public String getBlogPostById(@PathVariable Long id, Model model) {
        BlogPost blogPost = blogPostService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid blog post Id:" + id));
        model.addAttribute("blogPost", blogPost);
        return "blog/view";
    }

    @GetMapping("/edit/{id}")
    public String editBlogPostForm(@PathVariable Long id, Model model) {
        BlogPost blogPost = blogPostService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid blog post Id:" + id));
        model.addAttribute("blogPost", blogPost);
        return "blog/edit";
    }

    @PostMapping("/update/{id}")
    public String updateBlogPost(@PathVariable Long id, @Valid @ModelAttribute BlogPost blogPost, BindingResult result) {
        if (result.hasErrors()) {
            blogPost.setId(id);
            return "blog/edit";
        }
        blogPostService.update(blogPost);
        return "redirect:/blog";
    }

    @GetMapping("/delete/{id}")
    public String deleteBlogPost(@PathVariable Long id) {
        blogPostService.deleteById(id);
        return "redirect:/blog";
    }
}
