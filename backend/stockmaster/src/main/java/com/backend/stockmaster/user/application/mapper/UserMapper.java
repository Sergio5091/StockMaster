package com.backend.stockmaster.user.application.mapper;

import com.backend.stockmaster.user.application.dto.RegisterRequest;
import com.backend.stockmaster.user.application.dto.UserResponse;
import com.backend.stockmaster.user.application.dto.UserUpdateRequest;
import com.backend.stockmaster.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User toEntity(RegisterRequest request);

    @Mapping(target = "password", ignore = true)
    void updateFromDto(UserUpdateRequest request, @MappingTarget User user);
}
