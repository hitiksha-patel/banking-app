package com.banking.banking_app.service;

import com.banking.banking_app.dto.LoginDto;
import com.banking.banking_app.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);
    UserDto registerUser(UserDto userDto);
    UserDto login(LoginDto loginDto);
}
