package com.backend.stockmaster.scheduler;

import com.backend.stockmaster.inventory.repository.InventoryRepository;
import com.backend.stockmaster.transfer.repository.TransferRepository;
import com.backend.stockmaster.receipt.repository.GoodsReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperationScheduler {

    private final InventoryRepository inventoryRepository;
    private final TransferRepository transferRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;

    /**
     * Vérifier les inventaires en cours et alerter si délai dépassé
     */
    @Scheduled(cron = "0 0 9 * * *") // Every day at 9 AM
    public void checkPendingInventories() {
        try {
            log.info("Checking pending inventories...");
            var pendingInventories = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getStatut() == null || !inv.getStatut().toString().equals("VALIDE"))
                    .toList();
            
            if (!pendingInventories.isEmpty()) {
                log.warn("Found {} pending inventories", pendingInventories.size());
            }
            log.info("Pending inventory check completed");
        } catch (Exception e) {
            log.error("Error checking pending inventories", e);
        }
    }

    /**
     * Vérifier les transferts en attente
     */
    @Scheduled(cron = "0 0 10 * * *") // Every day at 10 AM
    public void checkPendingTransfers() {
        try {
            log.info("Checking pending transfers...");
            var pendingTransfers = transferRepository.findAll().stream()
                    .filter(t -> t.getStatut() == null || !t.getStatut().toString().equals("RECU"))
                    .toList();
            
            if (!pendingTransfers.isEmpty()) {
                log.warn("Found {} pending transfers", pendingTransfers.size());
            }
            log.info("Pending transfer check completed");
        } catch (Exception e) {
            log.error("Error checking pending transfers", e);
        }
    }

    /**
     * Vérifier les réceptions en attente
     */
    @Scheduled(cron = "0 0 11 * * *") // Every day at 11 AM
    public void checkPendingReceipts() {
        try {
            log.info("Checking pending receipts...");
            var pendingReceipts = goodsReceiptRepository.findAll().stream()
                    .filter(r -> r.getStatut() == null || !r.getStatut().toString().equals("VALIDEE"))
                    .toList();
            
            if (!pendingReceipts.isEmpty()) {
                log.warn("Found {} pending receipts", pendingReceipts.size());
            }
            log.info("Pending receipt check completed");
        } catch (Exception e) {
            log.error("Error checking pending receipts", e);
        }
    }
}
