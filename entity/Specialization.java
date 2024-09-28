package com.grad.akemha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "specialization")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "specialization_type", unique = true, nullable = false)
    private String specializationType;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL)
    private List<Consultation> consultations;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "specialization")
    private List<User> users;

}


