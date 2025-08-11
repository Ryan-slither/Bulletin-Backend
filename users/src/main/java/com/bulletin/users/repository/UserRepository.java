package com.bulletin.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulletin.users.model.FullUserDTO;
import com.bulletin.users.model.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    FullUserDTO findByEmail(String email);

    Users findByVerificationCode(Long verificationCode);

}
