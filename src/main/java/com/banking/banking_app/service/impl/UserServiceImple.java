package com.banking.banking_app.service.impl;

import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.mapper.UserMapper;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.UserService;
//import com.banking.banking_app.util.PasswordUtil;
import com.banking.banking_app.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImple implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    public UserServiceImple(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto){
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto registerUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }
}
