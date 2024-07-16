package com.banking.banking_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private List<Long> accountIds;
    private String password;
    private String token;
}
