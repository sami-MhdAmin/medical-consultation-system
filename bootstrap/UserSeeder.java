package com.grad.akemha.bootstrap;

import com.grad.akemha.entity.Specialization;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.Gender;
import com.grad.akemha.entity.enums.Role;
import com.grad.akemha.repository.SpecializationRepository;
import com.grad.akemha.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

// this class works on application run
// it fills the table Role with roles/
@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;


    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, SpecializationRepository specializationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.specializationRepository = specializationRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadUsers();
    }

    private void loadUsers() {
        Specialization spec = specializationRepository.findBySpecializationType("صدرية");
        if (spec == null) {
            Specialization specialization = specializationRepository.save(Specialization.builder().specializationType("صدرية").isPublic(true).imageUrl("url").build());

            User[] userNames = new User[]{
                    User.builder()
                            .dob(LocalDate.of(2001, 2, 2))
                            .phoneNumber("123")
                            .name("user")
                            .email("user@temp.com")
                            .gender(Gender.MALE)
                            .password(passwordEncoder.encode("123"))
                            .role(Role.USER)
                            .isActive(true)
                            .isVerified(true)
                            .creationDate(LocalDateTime.now())
                            .build(),
                    User.builder()
                            .dob(LocalDate.of(2001, 2, 2))
                            .phoneNumber("321")
                            .name("doctor")
                            .email("doctor@temp.com")
                            .gender(Gender.MALE)
                            .password(passwordEncoder.encode("123"))
                            .role(Role.DOCTOR)
                            .isActive(true)
                            .isVerified(true)
                            .creationDate(LocalDateTime.now())
                            .specialization(specialization)
                            .build(),
                    User.builder()
                            .dob(LocalDate.of(2001, 2, 2))
                            .phoneNumber("1234")
                            .name("owner")
                            .email("owner@temp.com")
                            .gender(Gender.MALE)
                            .password(passwordEncoder.encode("123"))
                            .role(Role.OWNER)
                            .isActive(true)
                            .isVerified(true)
                            .creationDate(LocalDateTime.now())
                            .build()
            };

            Optional<User> optionalEmailUser = userRepository.findByEmail("user@temp.com");
            if (optionalEmailUser.isPresent()) {
                return;
            }

            for (int i = 1; i <= userNames.length; i++) {
                System.out.println("entered loop for the " + i + " time");
                Optional<User> optionalUser = userRepository.findById((long) i);
                if (optionalUser.isEmpty()) {
                    userRepository.save(userNames[i - 1]);
                    System.out.println("User with name " + userNames[i - 1].getName() + " have been saved");
                    System.out.println("User with id " + userNames[i - 1].getId() + " have been saved");
                } else {
                    System.out.println("already have been added");
                }
            }
        }
    }
}
