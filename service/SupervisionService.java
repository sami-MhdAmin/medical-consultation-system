package com.grad.akemha.service;

import com.grad.akemha.dto.notification.NotificationRequestToken;
import com.grad.akemha.dto.supervision.request.SendNotificationToSupervisedRequest;
import com.grad.akemha.dto.supervision.response.SupervisionResponse;
import com.grad.akemha.dto.user.response.UserLessResponse;
import com.grad.akemha.entity.Supervision;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.SupervisionStatus;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.SupervisionRepository;
import com.grad.akemha.repository.UserRepository;
import com.grad.akemha.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SupervisionService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupervisionRepository supervisionRepository;

    @Autowired
    private FCMService fcmService;

    // sending request in first tab floating action button
    public void sendSupervisionRequest(Long supervisedId, HttpHeaders httpHeaders) {
        if (supervisedId == null) {
            throw new ForbiddenException("supervisedId is Null");
        }
        User supervisor = jwtService.extractUserFromToken(httpHeaders);
        User supervised = userRepository.findById(supervisedId).orElseThrow(() -> new NotFoundException("user Id: " + supervisedId + " is not found"));
        Supervision supervision = Supervision.builder()
                .supervisor(supervisor)
                .supervised(supervised)
                .supervisionStatus(SupervisionStatus.PENDING)
                .build();
        supervisionRepository.save(supervision);
    }

    // inside bell
    public List<SupervisionResponse> viewSupervisionRequest(HttpHeaders httpHeaders) {
        User supervised = jwtService.extractUserFromToken(httpHeaders);
        List<Supervision> supervisionList = supervisionRepository.findBySupervisionStatusAndSupervised(SupervisionStatus.PENDING, supervised);
        List<SupervisionResponse> supervisionResponseList = supervisionList.stream().map(supervision -> new SupervisionResponse(supervision)).toList();

        return supervisionResponseList;
    }

    // inside bell by pressing check
    public void replyToSupervisionRequest(Long supervisionId, HttpHeaders httpHeaders) {

        Supervision supervision = supervisionRepository.findById(supervisionId).orElseThrow(() -> new NotFoundException("supervisionId: " + supervisionId + " is not found"));
        supervision.setSupervisionStatus(SupervisionStatus.APPROVED);
        supervisionRepository.save(supervision);
    }

    // second tab
    public List<SupervisionResponse> getApprovedSupervisionBySupervised(HttpHeaders httpHeaders) {
        User supervised = jwtService.extractUserFromToken(httpHeaders);
        List<Supervision> supervisionList = supervisionRepository.findBySupervisionStatusAndSupervised(SupervisionStatus.APPROVED, supervised);
        List<SupervisionResponse> supervisionResponseList = supervisionList.stream().map(supervision -> new SupervisionResponse(supervision)).toList();
        return supervisionResponseList;
    }

    // can apply to three things
    public void deleteApprovedSupervision(Long supervisionId) {
        supervisionRepository.deleteById(supervisionId);
    }

    // first tab
    public List<SupervisionResponse> getApprovedSupervisionBySupervisor(HttpHeaders httpHeaders) {
        User supervisor = jwtService.extractUserFromToken(httpHeaders);
        List<Supervision> supervisionList = supervisionRepository.findBySupervisionStatusAndSupervisor(SupervisionStatus.APPROVED, supervisor);
        List<SupervisionResponse> supervisionResponseList = supervisionList.stream().map(supervision -> new SupervisionResponse(supervision)).toList();
        return supervisionResponseList;
    }

    // retrieve random 10 Max users++++++++++++
    public List<UserLessResponse> returnRandomTenUser(HttpHeaders httpHeaders) {
        User user = jwtService.extractUserFromToken(httpHeaders);
        Long userId = user.getId();
        List<User> userList = userRepository.findRandomUsers(userId);
        return userList.stream().map(UserLessResponse::new).toList();
    }

    // retrieve 10 users searched by supervised to add supervisor
    public List<UserLessResponse> returnTenUserByKeyword(String keyword, HttpHeaders httpHeaders) {
        User user = jwtService.extractUserFromToken(httpHeaders);
        Long userId = user.getId();
        List<User> userList = userRepository.findByNameContaining(keyword, userId, PageRequest.of(0, 10));
        return userList.stream().map(UserLessResponse::new).toList();
    }

    // send notification
    public String sendNotificationToSupervisedUser(SendNotificationToSupervisedRequest sendNotificationToSupervisedRequest, Long supervisedId, HttpHeaders httpHeaders) throws ExecutionException, InterruptedException {
        // the one who will send the notification
        User supervisor = jwtService.extractUserFromToken(httpHeaders);
        // the one who will receive the notification
        User supervised = userRepository.findById(supervisedId).orElseThrow();

        NotificationRequestToken tokenRequest = new NotificationRequestToken();
        tokenRequest.setTitle("تذكير بالدواء");
        tokenRequest.setBody(
                "الوصي\n" +
                supervisor.getName() +
                "\nيقوم بتذكيرك بتناول دواء\n" +
                sendNotificationToSupervisedRequest.getName() +
                "\nالذي يجب أخذه عند\n" +
                sendNotificationToSupervisedRequest.getTime());
        tokenRequest.setDeviceToken(supervised.getDeviceToken());
        fcmService.sendMessageToToken(tokenRequest);
        return "تم إرسال الاشعار بنجاح";
    }
}