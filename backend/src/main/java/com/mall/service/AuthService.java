package com.mall.service;

import com.mall.common.BusinessException;
import com.mall.domain.User;
import com.mall.domain.enums.Role;
import com.mall.dto.Dtos;
import com.mall.repository.UserRepository;
import com.mall.security.JwtService;
import com.mall.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public Dtos.AuthResponse register(Dtos.RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname() == null || request.nickname().isBlank()
                        ? request.username() : request.nickname())
                .email(request.email())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return createAuthResponse(user);
    }

    public Dtos.AuthResponse login(Dtos.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (BadCredentialsException exception) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        return createAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public Dtos.UserView currentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        return toView(user);
    }

    private Dtos.AuthResponse createAuthResponse(User user) {
        UserPrincipal principal = UserPrincipal.from(user);
        return new Dtos.AuthResponse(jwtService.generateToken(principal), toView(user));
    }

    private Dtos.UserView toView(User user) {
        return new Dtos.UserView(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getAvatar(),
                user.getRole().name()
        );
    }
}
