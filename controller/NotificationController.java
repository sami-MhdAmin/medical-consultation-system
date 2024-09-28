package com.grad.akemha.controller;


import com.grad.akemha.dto.notification.NotificationRequestToken;
import com.grad.akemha.dto.notification.NotificationRequestTopic;
import com.grad.akemha.dto.notification.NotificationResponse;
import com.grad.akemha.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final FCMService fcmService;

    @PostMapping("/token")
    public ResponseEntity<NotificationResponse> sendNotificationToToken(@RequestBody NotificationRequestToken requestToken)
            throws ExecutionException, InterruptedException {
        fcmService.sendMessageToToken(requestToken);
        return new ResponseEntity<>(new NotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/topic")
    public ResponseEntity<NotificationResponse> sendNotificationToTopic(@RequestBody NotificationRequestTopic requestTopic)
            throws ExecutionException, InterruptedException {
        fcmService.sendMessageToTopic(requestTopic);
        return new ResponseEntity<>(new NotificationResponse(HttpStatus.OK.value(), "تم إرسال الإشعار إلى " + requestTopic.getTopic()), HttpStatus.OK);
    }

}

