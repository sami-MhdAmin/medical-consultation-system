package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.message.request.MessageRequest;
import com.grad.akemha.dto.message.response.MessageResponse;
import com.grad.akemha.entity.Message;
import com.grad.akemha.repository.UserRepository;
import com.grad.akemha.service.ChatService;
import com.grad.akemha.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
public class ChatController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatService chatService;

//    two methods.
//    the first one is to add user. when new user connect to our chat app, we need to hit that endpoint and inform ALL the users that we have a new user joined the chat
//i think it is the same as when the doctor start the conversion
//
//    second method is to send the msg
//
//    payload == object

//        var messages = this.chats.getOrDefault(chatId);
//        messages.add(message.getPayload());
//        chats.put(chatId, messages);
//        return messages;

//    @MessageMapping("/chat.addUser") //note: messageMapping is the endPoint.  the end point is /app/chat.addUser   app from prefix config
//    @SendTo("/topic/public")
//    public ChatMessageModel addUser(
//            @Payload ChatMessageModel chatMessage,
//            SimpMessageHeaderAccessor headerAccessor
//    ) {
//        System.out.println("hello I'm sami in addUser");
//        // Add username in WebSocket session
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        return chatMessage;
//        // the return Model will return to /topic/  so the client should subscribe on topic to read the msg
//    }


    @MessageMapping("/consultation/{consultationId}/chat")
    @SendTo("/topic/consultation/{consultationId}/messages")
    @MessageExceptionHandler()
    public MessageResponse sendMessageWithWebsocket(@DestinationVariable String consultationId,
                                                    @Payload MessageRequest message) {
        log.info("new message arrived in chat with id {}", consultationId);
        log.info(String.valueOf(message));

        MessageResponse messageResponse = chatService.sendMessageWithWebsocket(message, consultationId);
        return messageResponse;
    }

    //get list of messages by consultationId.
    @GetMapping("/consultation/messages/{consultationId}")
    public ResponseEntity<BaseResponse<List<MessageResponse>>> getMessagesByConsultationId(
            @PathVariable Long consultationId
    ) {
        List<Message> messageList = messageService.getMessagesByConsultationId(consultationId);
        List<MessageResponse> messageResponseList = messageList.stream().map(message -> new MessageResponse(message, userRepository.findById(message.getUserId()).orElseThrow())).toList();
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "messages", messageResponseList));
    }
}
