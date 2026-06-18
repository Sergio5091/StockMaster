package com.backend.stockmaster.supplier.application.mapper;

import com.backend.stockmaster.supplier.application.dto.SupplierCreateDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierUpdateDTO;
import com.backend.stockmaster.supplier.domain.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    
    public Supplier toEntity(SupplierCreateDTO dto) {
        throw new UnsupportedOperationException("À implémenter");
    }
    
    public SupplierDTO toDTO(Supplier supplier) {
        throw new UnsupportedOperationException("À implémenter");
    }
    
    public void updateFromDTO(SupplierUpdateDTO dto, Supplier supplier) {
        throw new UnsupportedOperationException("À implémenter");
    }
}