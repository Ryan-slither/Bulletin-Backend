package com.bulletin.users.service;

import java.util.Optional;

import com.bulletin.users.model.FullUserDTO;
import com.bulletin.users.model.SignInDTO;
import com.bulletin.users.model.SignedInDTO;
import com.bulletin.users.model.UsersDTO;

public interface UserService {

    UsersDTO createUser(UsersDTO usersDTO);

    Optional<FullUserDTO> getUserById(Long id);

    SignedInDTO signIn(SignInDTO signInDTO);

    SignedInDTO reauthenticate(Long id, String token);

    String verifyToken(String token);

    void deleteUserById(Long id);

    boolean verify(Long verificationCode);

}
