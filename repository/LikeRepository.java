package com.grad.akemha.repository;

import com.grad.akemha.entity.Like;
import com.grad.akemha.entity.Post;
import com.grad.akemha.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>,
      /* this is for finding the record when wanting to match more than one attribute */  QueryByExampleExecutor<Like> {
    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
}
