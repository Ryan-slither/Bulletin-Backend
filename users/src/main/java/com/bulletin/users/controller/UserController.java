package com.bulletin.users.controller;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bulletin.users.model.FullUserDTO;
import com.bulletin.users.model.SignInDTO;
import com.bulletin.users.model.SignedInDTO;
import com.bulletin.users.model.TokenDTO;
import com.bulletin.users.model.UsersDTO;
import com.bulletin.users.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public UsersDTO createUser(@RequestBody UsersDTO usersDTO)
            throws UnsupportedEncodingException {
        return userService.createUser(usersDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        if (id != userId) {
            return new ResponseEntity<String>("Accessing User Not Your Own", HttpStatus.UNAUTHORIZED);
        }
        Optional<FullUserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDTO signInDTO) {
        SignedInDTO signedInDTO = userService.signIn(signInDTO);
        if (signedInDTO.token() != null) {
            return new ResponseEntity<SignedInDTO>(signedInDTO, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Error: Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/reauthenticate")
    public ResponseEntity<?> reauthenticate(@RequestHeader("X-User-Id") Long userId,
            @RequestHeader("Authorization") String token) {

        SignedInDTO verifiedUser = userService.reauthenticate(userId, token);
        if (verifiedUser.id() != null) {

            return new ResponseEntity<SignedInDTO>(verifiedUser, HttpStatus.OK);

        }

        return new ResponseEntity<String>("Token Could Not Be Used To Sign In", HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestBody TokenDTO tokenDTO) {
        String verifiedToken = userService.verifyToken(tokenDTO.token());
        if (!verifiedToken.isEmpty()) {
            return new ResponseEntity<String>(verifiedToken, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Error: Verification Failed", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        if (id != userId) {
            return new ResponseEntity<String>("Deleting User Not Your Own", HttpStatus.UNAUTHORIZED);
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam Long code) {
        if (userService.verify(code)) {
            return "verify-success";
        } else {
            return "verify-fail";
        }
    }

}
