package co.edu.sena.productsreact.service;

import co.edu.sena.productsreact.dto.auth.AuthResponse;
import co.edu.sena.productsreact.dto.auth.ForgotPasswordRequest;
import co.edu.sena.productsreact.dto.auth.ForgotPasswordResponse;
import co.edu.sena.productsreact.dto.auth.LoginRequest;
import co.edu.sena.productsreact.dto.auth.MessageResponse;
import co.edu.sena.productsreact.dto.auth.RegisterRequest;
import co.edu.sena.productsreact.dto.auth.ResetPasswordRequest;
import co.edu.sena.productsreact.dto.auth.UserDto;
import co.edu.sena.productsreact.entity.User;
import co.edu.sena.productsreact.exception.DuplicateResourceException;
import co.edu.sena.productsreact.exception.ResourceNotFoundException;
import co.edu.sena.productsreact.repository.UserRepository;
import co.edu.sena.productsreact.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(
                    "El nombre '" + request.name() + "' ya esta registrado");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "El email '" + request.email() + "' ya esta registrado");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role("ROLE_USER")
                .build();

        User saved = userRepository.save(user);

        UserDetails userDetails = buildUserDetails(saved);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, toDto(saved));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
                .or(() -> userRepository.findByName(userDetails.getUsername()))
                .orElseThrow();

        return new AuthResponse(token, toDto(user));
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un usuario registrado con ese correo"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiresAt(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        String resetLink = frontendUrl + "/reset-password?token=" + token;
        boolean mailSent = emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return new ForgotPasswordResponse(
                mailSent
                        ? "Te enviamos el enlace de recuperacion al correo"
                        : "Se genero el enlace para restablecer la contrasena",
                resetLink
        );
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.token())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El enlace de recuperacion no es valido"));

        if (user.getResetTokenExpiresAt() == null
                || user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            user.setResetToken(null);
            user.setResetTokenExpiresAt(null);
            userRepository.save(user);
            throw new ResourceNotFoundException("El enlace de recuperacion ya expiro");
        }

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        userRepository.save(user);

        return new MessageResponse("Contrasena actualizada correctamente");
    }

    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getSpringRole()))
        );
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getName(), user.getEmail(), user.getRole());
    }
}
