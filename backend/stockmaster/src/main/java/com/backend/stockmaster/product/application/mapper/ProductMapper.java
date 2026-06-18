package com.backend.stockmaster.product.application.mapper;

import com.backend.stockmaster.product.application.dto.ProductCreateDTO;
import com.backend.stockmaster.product.application.dto.ProductDTO;
import com.backend.stockmaster.product.application.dto.ProductUpdateDTO;
import com.backend.stockmaster.product.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    
    public Product toEntity(ProductCreateDTO dto) {
        throw new UnsupportedOperationException("À implémenter");
    }
    
    public ProductDTO toDTO(Product product) {
        throw new UnsupportedOperationException("À implémenter");
    }
    
    public void updateFromDTO(ProductUpdateDTO dto, Product product) {
        throw new UnsupportedOperationException("À implémenter");
    }
}