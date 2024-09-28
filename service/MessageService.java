package com.grad.akemha.service;

import com.grad.akemha.entity.Consultation;
import com.grad.akemha.entity.Message;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.ConsultationRepository;
import com.grad.akemha.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConsultationRepository consultationRepository;
//    public List<Message> getMessagesByConsultationId(Long consultationId) {
//        List<Message> messageList = messageRepository.findByConsultationId(consultationId);
//        return messageList;
//    }

public List<Message> getMessagesByConsultationId(Long consultationId) {
    List<Message> messages = messageRepository.findByConsultationId(consultationId);
    return messages;
}

}
