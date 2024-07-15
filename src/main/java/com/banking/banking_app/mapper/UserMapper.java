package com.banking.banking_app.mapper;

import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.User;

import java.util.stream.Collectors;

public class UserMapper {
    public static User mapToUser(UserDto userDto){  // converts userdto to user
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .password(userDto.getPassword())
                .build();
    }
    public static UserDto mapToUserDto(User user){  //converts user to userdto
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(user.getPassword())
                .accountIds(user.getAccounts() != null ? user.getAccounts().stream().map(account -> account.getId()).collect(Collectors.toList()) : null)
                .build();
    }
}
