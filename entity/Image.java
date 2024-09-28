package com.grad.akemha.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO check the erd why we have user_id in image?
//    private Long user_id;
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "name_image")
    private String name;
}
