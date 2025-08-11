package com.bulletin.users.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import com.bulletin.users.model.FullUserDTO;
import com.bulletin.users.model.SignInDTO;
import com.bulletin.users.model.SignedInDTO;
import com.bulletin.users.model.Users;
import com.bulletin.users.model.UsersDTO;
import com.bulletin.users.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JavaMailSender mailSender;

    private final String NUMBERS = "0123456789";
    private final SecureRandom RANDOM = new SecureRandom();

    @Value("${EMAIL}")
    private String email;

    @Value("${SITEURL}")
    private String siteURL;

    @Override
    public UsersDTO createUser(UsersDTO usersDTO) {
        Users user = convertToCreateEntity(usersDTO);
        user.setTimeCreated(Instant.now().getEpochSecond());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationCode(generateVerificationCode());

        try {
            sendVerificationEmail(user);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        Users savedUser = userRepository.save(user);
        return convertToCreateDTO(savedUser);
    }

    @Override
    public Optional<FullUserDTO> getUserById(Long id) {
        Optional<Users> user = userRepository.findById(id);
        return user.map(this::convertToFullDTO);
    }

    @Override
    public SignedInDTO signIn(SignInDTO signInDTO) {

        FullUserDTO user = userRepository.findByEmail(signInDTO.email());

        if (user != null) {

            if (passwordEncoder.matches(signInDTO.password(), user.password()) && user.enabled()) {

                Instant now = Instant.now();
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer("bulletin")
                        .issuedAt(now)
                        .expiresAt(now.plus(30, ChronoUnit.DAYS))
                        .subject(user.id().toString())
                        .build();

                return new SignedInDTO(jwtEncoder
                        .encode(JwtEncoderParameters.from(claims))
                        .getTokenValue(), user.id(), user.timeCreated());

            }

        }

        return new SignedInDTO(null, null, null);

    }

    @Override
    public SignedInDTO reauthenticate(Long id, String token) {

        Optional<FullUserDTO> user = getUserById(id);

        if (user.isEmpty()) {

            return new SignedInDTO(null, null, null);

        }

        token = token.substring(7);

        return new SignedInDTO(token, user.get().id(), user.get().timeCreated());

    }

    @Override
    public String verifyToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            return "";
        }
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean verify(Long verificationCode) {
        Users user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
    }

    private Long generateVerificationCode() {
        Long i = 0L;
        while (i < 1000) {
            StringBuilder sb = new StringBuilder(10);

            for (int j = 0; j < 10; j++) {
                int idx = RANDOM.nextInt(NUMBERS.length());
                sb.append(NUMBERS.charAt(idx));
            }

            Long verificationCode = Long.parseLong(sb.toString());
            Users user = userRepository.findByVerificationCode(verificationCode);

            if (user != null) {
                continue;
            }

            return verificationCode;
        }
        throw new IllegalStateException("Valid Code Could Not Be Generated");
    }

    private Users convertToCreateEntity(UsersDTO usersDTO) {
        Users user = new Users();
        user.setEmail(usersDTO.email());
        user.setPassword(usersDTO.password());
        user.setTimeCreated(null);
        return user;
    }

    private UsersDTO convertToCreateDTO(Users user) {
        return new UsersDTO(user.getId(), user.getEmail(), user.getPassword(), user.getTimeCreated());
    }

    private FullUserDTO convertToFullDTO(Users user) {
        return new FullUserDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getTimeCreated(),
                user.getVerificationCode(),
                user.isEnabled(),
                user.getPasswordCode());
    }

    private void sendVerificationEmail(Users user)
            throws UnsupportedEncodingException, MessagingException {
        String toAddress = user.getEmail();
        String fromAddress = this.email;
        String senderName = "Bulletin";
        String subject = "Please verify your registration";
        String content = "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">Verify Here</a></h3>"
                + "Thank you,<br>"
                + senderName;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

}
