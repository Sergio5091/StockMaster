package com.backend.stockmaster.user.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.user.application.dto.PasswordResetRequest;
import com.backend.stockmaster.user.application.dto.RegisterRequest;
import com.backend.stockmaster.user.application.dto.UserResponse;
import com.backend.stockmaster.user.application.dto.UserUpdateRequest;
import com.backend.stockmaster.user.application.mapper.UserMapper;
import com.backend.stockmaster.user.domain.User;
import com.backend.stockmaster.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserApplicationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public void changePassword(Long userId, PasswordResetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userMapper.updateFromDto(request, user);
        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(false);
        userRepository.save(user);
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }
}
