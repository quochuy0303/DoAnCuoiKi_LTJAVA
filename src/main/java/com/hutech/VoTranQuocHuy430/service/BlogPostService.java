package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.model.BlogPost;
import com.hutech.VoTranQuocHuy430.model.User;
import com.hutech.VoTranQuocHuy430.repository.BlogPostRepository;
import com.hutech.VoTranQuocHuy430.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<BlogPost> findAll() {
        return blogPostRepository.findAll();
    }

    public Optional<BlogPost> findById(Long id) {
        return blogPostRepository.findById(id);
    }

    public BlogPost save(BlogPost blogPost, String username, MultipartFile imageFile) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username: " + username));
        blogPost.setAuthor(author);
        blogPost.setCreatedAt(LocalDateTime.now());
        blogPost.setViews(0); // Initialize views to 0

        // Save image file
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(imageFile);
            blogPost.setImageUrl(imageUrl);
        } else {
            blogPost.setImageUrl("path/to/default/image.jpg"); // Set a default image URL if not provided
        }

        blogPost.setExcerpt(blogPost.getContent().substring(0, Math.min(blogPost.getContent().length(), 150))); // Create an excerpt
        return blogPostRepository.save(blogPost);
    }

    public BlogPost update(BlogPost blogPost) {
        blogPost.setUpdatedAt(LocalDateTime.now());
        return blogPostRepository.save(blogPost);
    }

    public void deleteById(Long id) {
        blogPostRepository.deleteById(id);
    }
}
