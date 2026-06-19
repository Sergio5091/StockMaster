package com.backend.stockmaster.alert.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity @Table(name = "alerts")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private AlertType type;
    @Column(nullable = false) private String titre;
    @Column(columnDefinition = "TEXT") private String message;
    @Column(name = "entrepot_id") private Long entrepotId;
    private String entrepotNom;
    @Builder.Default private boolean traitee = false;
    private String traiteeParUsername;
    private LocalDateTime traiteeAt;
    @CreatedDate @Column(updatable = false) private LocalDateTime createdAt;
}
