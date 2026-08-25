package com.garagehub.service;

import com.garagehub.dto.RegisterRequestDto;
import com.garagehub.dto.UserResponseDto;
import com.garagehub.entity.User;
import com.garagehub.enums.Role;
import com.garagehub.exception.EmailAlreadyExistsException;
import com.garagehub.mapper.UserMapper;
import com.garagehub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponseDto register(RegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("E-postadressen används redan");
        }

        User user = new User(dto.getFirstName(), dto.getLastName(), dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);

        return userMapper.toResponseDto(saved);
    }
}
