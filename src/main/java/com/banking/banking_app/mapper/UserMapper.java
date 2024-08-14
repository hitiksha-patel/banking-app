package com.banking.banking_app.mapper;

import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.Document;
import com.banking.banking_app.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .profilePic(user.getProfilePic())  // Ensure this is correctly set
                .accounts(user.getAccounts() == null ? new ArrayList<>() :
                        user.getAccounts().stream().map(AccountMapper::mapToAccountDto).collect(Collectors.toList()))
                .documents(user.getDocuments() == null ? new ArrayList<>() :
                        user.getDocuments().stream().map(DocumentMapper::mapToDocumentDto).collect(Collectors.toList()))
                .build();
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
                .profilePic(userDto.getProfilePic())
                .accounts(userDto.getAccounts() == null ? new ArrayList<>() :
                        userDto.getAccounts().stream().map(AccountMapper::mapToAccount).collect(Collectors.toList()))
                .build();

        // Optionally map documents back to entity
        if (userDto.getDocuments() != null) {
            List<Document> documents = userDto.getDocuments().stream()
                    .map(DocumentMapper::mapToDocument)  // Ensure this line is correct
                    .collect(Collectors.toList());
            user.setDocuments(documents);
        }

        return user;
    }
}
