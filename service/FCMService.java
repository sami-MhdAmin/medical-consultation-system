package com.grad.akemha.service;

import com.google.firebase.messaging.*;
import com.grad.akemha.dto.notification.NotificationRequestToken;
import com.grad.akemha.dto.notification.NotificationRequestTopic;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {
    //    private Logger logger = LoggerFactory.getLogger(FCMService.class);

    // token (a specific User)
    public void sendMessageToToken(NotificationRequestToken request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageToToken(request);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String jsonOutput = gson.toJson(message);
        sendAndGetResponse(message);
    }

    // global
    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }


    private AndroidConfig getAndroidConfigToken() {
        return AndroidConfig.builder()
                // This sets the time-to-live (TTL) for the message, which determines how long the
                // message should be kept in the FCM system's storage if the device is offline.
                //  it sets the collapse key, which is an identifier for a group of messages that can be collapsed
                // so that only the last message is sent when the device comes back online.
//      OLD          .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)

                // I want it to live 24 hours if the user is offline
                // and receive all messages
                .setTtl(Duration.ofDays(1).toMillis())
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .build()).build();
    }

    // APPLe configurations
    private ApnsConfig getApnsConfigToken() {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .build()).build();
    }

    private Message getPreconfiguredMessageToToken(NotificationRequestToken request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getDeviceToken())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(NotificationRequestToken request) {
        AndroidConfig androidConfig = getAndroidConfigToken();
        ApnsConfig apnsConfig = getApnsConfigToken();
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();
        return Message.builder()
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .setNotification(notification);
    }

    // ***************************************************************************************
    // topic
    public void sendMessageToTopic(NotificationRequestTopic requestTopic) throws ExecutionException, InterruptedException {
        Notification notification = Notification.builder()
                .setTitle(requestTopic.getTitle())
                .setBody(requestTopic.getBody())
                .build();
        AndroidConfig androidConfig = getAndroidConfigTopic(requestTopic.getTopic());
        ApnsConfig apnsConfig = getApnsConfigTopic(requestTopic.getTopic());
        Message message = Message.builder()
                .setTopic(requestTopic.getTopic())
                .setNotification(notification)
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .build();

        sendAndGetResponse(message);
    }

    private AndroidConfig getAndroidConfigTopic(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofDays(1).toMillis())
                .setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setTag(topic).build()).build();
    }

    // APPLe configurations
    private ApnsConfig getApnsConfigTopic(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setCategory(topic)
                        .setThreadId(topic)
                        .build()).build();
    }


    // **************************
    // to Subscribe to Topic so u can get notification each time a notification is sent to that topic
    public void subscribeToTopic(String deviceToken, String topic) {
        FirebaseMessaging.getInstance()
                .subscribeToTopicAsync(List.of(deviceToken), topic);
        System.out.println("Subscribed successfully");
    }

    // to UnSubscribe from Topics so u don't get notification each time a notification is sent to that topic
    public void unSubscribeFromTopic(String deviceToken, String topic) {
        FirebaseMessaging.getInstance()
                .unsubscribeFromTopicAsync(List.of(deviceToken), topic);
        System.out.println("UnSubscribed successfully");
    }
}
