package com.banking.banking_app.service.impl;

import com.banking.banking_app.dto.LoginDto;
import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.mapper.UserMapper;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.UserService;
//import com.banking.banking_app.util.PasswordUtil;
import com.banking.banking_app.util.JwtUtil;
import com.banking.banking_app.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImple implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    public UserServiceImple(UserRepository userRepository, JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
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
        user.setPassword(PasswordUtil.hashPassword(user.getPassword())); // Ensuring password is hashed using BCrypt
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }


    @Override
    public UserDto login(LoginDto loginDto) {
        System.out.println("Attempting to login user: " + loginDto.getUsername());
        User user = userRepository.findByUsername(loginDto.getUsername());
        if (user == null) {
            System.out.println("User not found: " + loginDto.getUsername());
            return null;
        }

        if (PasswordUtil.checkPassword(loginDto.getPassword(), user.getPassword())) {
            // Generate JWT token
            String token = jwtUtil.generateToken(user);
            UserDto userDto = UserMapper.mapToUserDto(user);
            userDto.setToken(token); // Include the token in the UserDto
            System.out.println("Login successful for user: " + loginDto.getUsername());
            return userDto;
        } else {
            System.out.println("Invalid password for user: " + loginDto.getUsername());
        }
        return null;
    }
}
