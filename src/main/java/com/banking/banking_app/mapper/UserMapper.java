package com.banking.banking_app.mapper;

import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.Account;
import com.banking.banking_app.entity.User;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .accountIds(user.getAccounts() == null ? new ArrayList<>() :
                        user.getAccounts().stream().map(Account::getId).collect(Collectors.toList()))
                .build();
        System.out.println("Mapped User to UserDto: " + userDto);
        return userDto;
    }

    public static User mapToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        User user = User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .accounts(userDto.getAccountIds() == null ? new ArrayList<>() :
                        userDto.getAccountIds().stream().map(id -> {
                            Account account = new Account();
                            account.setId(id);
                            return account;
                        }).collect(Collectors.toList()))
                .build();
        System.out.println("Mapped UserDto to User: " + user);
        return user;
    }
}
