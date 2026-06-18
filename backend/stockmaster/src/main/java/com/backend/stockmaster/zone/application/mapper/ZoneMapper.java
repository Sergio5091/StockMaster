package com.backend.stockmaster.zone.application.mapper;

import com.backend.stockmaster.zone.application.dto.ZoneCreateDTO;
import com.backend.stockmaster.zone.application.dto.ZoneDTO;
import com.backend.stockmaster.zone.domain.Zone;
import org.springframework.stereotype.Component;

@Component
public class ZoneMapper {
    
    public Zone toEntity(ZoneCreateDTO dto) {
        throw new UnsupportedOperationException("À implémenter");
    }
    
    public ZoneDTO toDTO(Zone zone) {
        throw new UnsupportedOperationException("À implémenter");
    }
}