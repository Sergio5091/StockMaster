package com.backend.stockmaster.issue.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "goods_issues")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GoodsIssue extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String numero;
    @Column(name = "entrepot_id", nullable = false) private Long entrepotId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private IssueMotif motif;
    private String clientNom;
    private String clientReference;
    @Enumerated(EnumType.STRING) @Builder.Default private IssueStatus statut = IssueStatus.BROUILLON;
    private LocalDate dateSortie;
    @Column(columnDefinition = "TEXT") private String note;
    private String creePar;
    private String valideePar;
    @Builder.Default
    @OneToMany(mappedBy = "bonSortie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsIssueLine> lignes = new ArrayList<>();
}
