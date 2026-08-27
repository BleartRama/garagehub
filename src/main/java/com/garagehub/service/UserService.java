package com.garagehub.service;

import com.garagehub.dto.UserRequestDto;
import com.garagehub.dto.UserResponseDto;
import com.garagehub.entity.User;
import com.garagehub.exception.ResourceNotFoundException;
import com.garagehub.mapper.UserMapper;
import com.garagehub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Användare hittades inte"));
        return userMapper.toResponseDto(user);
    }

    public UserResponseDto create(UserRequestDto dto) {
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toResponseDto(saved);
    }

    public UserResponseDto update(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Användare hittades inte"));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        return userMapper.toResponseDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
