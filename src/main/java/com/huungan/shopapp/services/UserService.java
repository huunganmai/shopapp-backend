package com.huungan.shopapp.services;

import com.huungan.shopapp.components.JwtTokenUtils;
import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.dtos.UpdateUserDTO;
import com.huungan.shopapp.dtos.UserDTO;
import com.huungan.shopapp.exceptions.DataNotFoundException;
import com.huungan.shopapp.exceptions.PermissionDenyException;
import com.huungan.shopapp.models.Role;
import com.huungan.shopapp.models.User;
import com.huungan.shopapp.repositories.RoleRepository;
import com.huungan.shopapp.repositories.UserRepository;
import com.huungan.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        // Check if the phone number already exists or not
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PHONE_EXISTING));
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        if(role.getName().equalsIgnoreCase("ADMIN")) {
            throw new PermissionDenyException("Cannot register admin account");
        }
        // Convert from userDTO -> user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);
        if(userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        User existingUser = optionalUser.get();
        // check password
        if(existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException(
                        localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }
        // check role
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }

        // Authenticate with Java Sping Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password, existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if(userOptional.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }
        return userOptional.get();
    }

    @Override
    public User updateUser(long id ,UpdateUserDTO updatedUserDTO) throws Exception {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, id)));

        // Check if phone number existing
        String newPhoneNumber = updatedUserDTO.getPhoneNumber();
        if(!existingUser.getPhoneNumber().equals(updatedUserDTO.getPhoneNumber()) &&
            userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PHONE_EXISTING));
        }

        if(updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (newPhoneNumber != null) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }
//        if (updatedUserDTO.getFacebookAccountId() > 0) {
//            existingUser.setFacebookAccountId(updatedUserDTO.getFacebookAccountId());
//        }
//        if (updatedUserDTO.getGoogleAccountId() > 0) {
//            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
//        }

        // check passwor and encode password
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            if(!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }
}
