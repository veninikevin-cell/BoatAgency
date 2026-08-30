package com.example.lago.service;

import com.example.lago.dto.RegisterDTO;
import com.example.lago.exception.EmailAlreadyExistsException;
import com.example.lago.exception.UserNotFoundException;
import com.example.lago.model.User;
import com.example.lago.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// registrazione utenti + recupero utente autenticato

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passEnc;

    public UserService(UserRepository userRepo,
                       PasswordEncoder passEnc) {
        this.userRepo = userRepo;
        this.passEnc = passEnc;
    }

    public User register(RegisterDTO dto) {
        if (!dto.passwordsMatch()) {
            throw new IllegalArgumentException("Le password non coincidono");
        }
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User.Role role = "PROVIDER".equalsIgnoreCase(dto.getAccountType())
            ? User.Role.PROVIDER
            : User.Role.USER;

        User user = new User(
            dto.getFirstName(),
            dto.getLastName(),
            dto.getEmail().toLowerCase().trim(),
            passEnc.encode(dto.getPassword()),
            role
        );
        return userRepo.save(user);
    }

    // centralizzato qui: prima era duplicato identico in ogni controller
    public User getCurrentUser(UserDetails userDetails) {
        return findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }
}