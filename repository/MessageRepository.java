package com.grad.akemha.repository;

import com.grad.akemha.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
List<Message> findByConsultationId(Long consultationId);

}
