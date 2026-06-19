package com.backend.stockmaster.inventory.application.dto;

import com.backend.stockmaster.inventory.domain.InventoryStatus;
import com.backend.stockmaster.inventory.domain.InventoryType;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryDTO {
    private Long id;
    private String numero;
    private Long entrepotId;
    private String entrepotNom;
    private Long zoneId;
    private String zoneNom;
    private Long categorieId;
    private String categorieNom;
    private InventoryType type;
    private InventoryStatus statut;
    private LocalDate datePlanifiee;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String note;
    private String creePar;
    private String valideePar;
    private int nbLignes;
    private int nbEcarts;
    private List<InventoryLineDTO> lignes;
}
