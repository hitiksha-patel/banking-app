package com.banking.banking_app.controller;

import com.banking.banking_app.dto.LoginDto;
import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.exception.UserAlreadyExistsException;
import com.banking.banking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto){
        try {
            UserDto registeredUser = userService.registerUser(userDto);
            return new ResponseEntity<>(registeredUser, HttpStatus.OK);
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        UserDto userDto = userService.login(loginDto);
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid username or password");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        System.out.println("Received update request for user ID: " + id);
        System.out.println("Request body: " + userDto);
        UserDto updatedUser = userService.updateUser(id, userDto);
        System.out.println("Update successful for user ID: " + id);
        return ResponseEntity.ok(updatedUser);
    }


}
