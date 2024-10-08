package com.banking.banking_app.controller;

import com.banking.banking_app.dto.LoginDto;
import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.exception.UserAlreadyExistsException;
import com.banking.banking_app.exception.UserNotFoundException;
import com.banking.banking_app.service.UserService;
import com.banking.banking_app.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper){

        this.userService = userService;
        this.objectMapper = objectMapper;
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
        LoginDto responseDto = userService.login(loginDto);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid username or password");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestPart("userDto") String userDtoJson,
            @RequestPart(value = "document", required = false) MultipartFile document,
            @RequestPart(value = "profilePicFile", required = false) MultipartFile profilePicFile) {

        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto;
        try {
            userDto = objectMapper.readValue(userDtoJson, UserDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }

        userDto.setDocument(document);
        userDto.setProfilePicFile(profilePicFile);

        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No JWT token found in request headers");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getLoggedInUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                String username = jwtUtil.extractUsername(jwt);
                UserDto userDto = userService.getUserByUsername(username);
                return ResponseEntity.ok(userDto);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
