package com.banking.banking_app.service.impl;

import com.banking.banking_app.dto.LoginDto;
import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.Document;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.exception.InvalidPasswordException;
import com.banking.banking_app.exception.UserAlreadyExistsException;
import com.banking.banking_app.exception.UserNotFoundException;
import com.banking.banking_app.mapper.UserMapper;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.UserService;
//import com.banking.banking_app.util.PasswordUtil;
import com.banking.banking_app.util.JwtUtil;
import com.banking.banking_app.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UserServiceImple implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${user.profile-pic.upload-dir}")
    private String documentUploadDir;


    @Autowired
    public UserServiceImple(UserRepository userRepository, JwtUtil jwtUtil, DocumentService documentService){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.documentService = documentService;
    }

    @Override
    public UserDto registerUser(UserDto userDto) {
        try {
            if (userRepository.findByUsername(userDto.getUsername()) != null) {
                throw new UserAlreadyExistsException("User with username " + userDto.getUsername() + " already exists");
            }

            if (userRepository.findByEmail(userDto.getEmail()) != null) {
                throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
            }

            User user = UserMapper.mapToUser(userDto);
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            user.setRole("ROLE_USER");

            User savedUser = userRepository.save(user);
            return UserMapper.mapToUserDto(savedUser);

        } catch (UserAlreadyExistsException e) {
            throw new UserAlreadyExistsException("This user is already exists");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred during user registration: " + e.getMessage());
        }
    }



    @Override
    public LoginDto login(LoginDto loginDto) {
        try {
            User user = userRepository.findByUsername(loginDto.getUsername());
            if (user == null) {
                throw new UserNotFoundException("User not found: " + loginDto.getUsername());
            }

            if (PasswordUtil.checkPassword(loginDto.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user);
                return new LoginDto(user.getId(), user.getUsername(), user.getEmail(), token);
            } else {
                throw new InvalidPasswordException("Invalid Password for username: " + loginDto.getUsername());
            }
        }
        catch (UserNotFoundException e) {
            throw new UserNotFoundException("User not found: " + loginDto.getUsername());
        }
    }


    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        updateUserDetails(existingUser, userDto);

        return UserMapper.mapToUserDto(userRepository.save(existingUser));
    }

    private void updateUserDetails(User existingUser, UserDto userDto) {
        existingUser.setFirstname(userDto.getFirstname());
        existingUser.setLastname(userDto.getLastname());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setUsername(userDto.getUsername());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(PasswordUtil.hashPassword(userDto.getPassword()));
        }

        if (userDto.getRole() != null && !userDto.getRole().isEmpty()) {
            existingUser.setRole(userDto.getRole());
        }

        if (userDto.getProfilePicFile() != null && !userDto.getProfilePicFile().isEmpty()) {
            String profilePicPath = saveProfilePicture(userDto.getProfilePicFile());
            existingUser.setProfilePic(profilePicPath);
        }
    }

    private String saveProfilePicture(MultipartFile profilePicFile) {
        return fileStorageService.saveProfilePicture(profilePicFile, documentUploadDir);
    }



    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDto getUserByUsername(String username) {
        System.out.println("Fetching user from DB for username: " + username);
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UserNotFoundException("User not found with username: " + username);
            }
            UserDto userDto = UserMapper.mapToUserDto(user);
            System.out.println("Retrieved UserDto: " + userDto);
            return userDto;
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            throw e; // Re-throw the exception to be handled by the controller
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while retrieving user by username", e); // Re-throw as a generic runtime exception
        }
    }

}
