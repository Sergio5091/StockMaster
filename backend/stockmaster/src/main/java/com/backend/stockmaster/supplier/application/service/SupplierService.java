package com.backend.stockmaster.supplier.application.service;

import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.supplier.application.dto.SupplierCreateDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierUpdateDTO;
import com.backend.stockmaster.supplier.application.mapper.SupplierMapper;
import com.backend.stockmaster.supplier.domain.Supplier;
import com.backend.stockmaster.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    public SupplierDTO createSupplier(SupplierCreateDTO dto) {
        String code = generateCode();
        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setCode(code);
        supplier.setActif(true);
        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierUpdateDTO dto) {
        Supplier supplier = findOrThrow(id);
        supplierMapper.updateFromDTO(dto, supplier);
        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }

    @Transactional
    public void deactivateSupplier(Long id) {
        Supplier supplier = findOrThrow(id);
        supplier.setActif(false);
        supplierRepository.save(supplier);
    }

    public Page<SupplierDTO> findAll(Pageable pageable) {
        return supplierRepository.findByActifTrue(pageable)
                .map(supplierMapper::toDTO);
    }

    public SupplierDTO findById(Long id) {
        return supplierMapper.toDTO(findOrThrow(id));
    }

    private Supplier findOrThrow(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable avec l'id : " + id));
    }

    private String generateCode() {
        long count = supplierRepository.count() + 1;
        return String.format("SUP-%03d", count);
    }
}
