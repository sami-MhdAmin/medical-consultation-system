package com.grad.akemha.entity;

import com.grad.akemha.entity.enums.ConsultationType;
import com.grad.akemha.entity.enums.SupervisionStatus;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supervision")
public class Supervision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @ManyToOne
    @JoinColumn(name = "supervised_id")
    private User supervised;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupervisionStatus supervisionStatus;
}
