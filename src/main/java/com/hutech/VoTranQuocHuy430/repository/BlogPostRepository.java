package com.hutech.VoTranQuocHuy430.repository;

import com.hutech.VoTranQuocHuy430.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
}
