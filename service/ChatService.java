package com.grad.akemha.service;

import com.grad.akemha.dto.message.request.MessageRequest;
import com.grad.akemha.dto.message.response.MessageResponse;
import com.grad.akemha.entity.Consultation;
import com.grad.akemha.entity.Message;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.ConsultationStatus;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.ConsultationRepository;
import com.grad.akemha.repository.MessageRepository;
import com.grad.akemha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private UserRepository userRepository;

    public MessageResponse sendMessageWithWebsocket(MessageRequest message, String consultationId) {
        Consultation consultation = consultationRepository.findById(Long.valueOf(consultationId)).orElseThrow(() -> new NotFoundException("not found"));
        if(!consultation.getConsultationStatus().equals(ConsultationStatus.ACTIVE)){
            System.out.println("inside if");
            consultation.setConsultationStatus(ConsultationStatus.ACTIVE);
            consultationRepository.save(consultation);
        }
        Message savedMessage = Message.builder()
                .textMsg(message.getTextMsg())
                .consultation(consultation)
                .userId(message.getUserId())
                .build();
        messageRepository.save(savedMessage);

        User user = userRepository.findById(message.getUserId()).orElseThrow();
        MessageResponse messageResponse = new MessageResponse(savedMessage, user);
        return messageResponse;
    }
}
