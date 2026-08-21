package com.garagehub.mapper;

import com.garagehub.dto.UserRequestDto;
import com.garagehub.dto.UserResponseDto;
import com.garagehub.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public User toEntity(UserRequestDto dto) {
        return new User(dto.getFirstName(), dto.getLastName(), dto.getEmail());
    }
}

