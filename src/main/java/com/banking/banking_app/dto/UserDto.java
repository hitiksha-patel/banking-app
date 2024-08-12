package com.banking.banking_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID  = 1L;

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String password;
    private String token;
    private String role;
    private List<AccountDto> accounts;
}
