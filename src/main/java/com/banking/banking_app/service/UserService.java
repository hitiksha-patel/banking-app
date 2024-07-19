package com.banking.banking_app.service;

import com.banking.banking_app.dto.LoginDto;
import com.banking.banking_app.dto.UserDto;

public interface UserService {

    UserDto registerUser(UserDto userDto);
    UserDto login(LoginDto loginDto);
    UserDto updateUser(Long userId, UserDto userDto);
}
