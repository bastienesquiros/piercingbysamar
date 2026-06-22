package com.besquiros.piercingbysamar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faq_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FaqItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(nullable = false)
    private Integer position = 0;

    @Column(nullable = false)
    private Boolean active = true;
}
