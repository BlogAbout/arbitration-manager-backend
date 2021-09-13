package ru.blogabout.arbitrationmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.blogabout.arbitrationmanager.config.jwt.JwtUtils;
import ru.blogabout.arbitrationmanager.entity.User;
import ru.blogabout.arbitrationmanager.pojo.JwtResponse;
import ru.blogabout.arbitrationmanager.pojo.SigninRequest;
import ru.blogabout.arbitrationmanager.pojo.SignupRequest;
import ru.blogabout.arbitrationmanager.service.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (signupRequest.getCode() == 0) {
            if (userService.existsByUsername(signupRequest.getUsername()))
                return new ResponseEntity<String>("Пользователь с таким именем пользователя уже существует.", HttpStatus.ACCEPTED);

            if (userService.existsByEmail(signupRequest.getEmail()))
                return new ResponseEntity<String>("Пользователь с таким e-mail уже существует.", HttpStatus.ACCEPTED);

            if (userService.existsByPhone(signupRequest.getPhone()))
                return new ResponseEntity<String>("Пользователь с таким номером телефона уже существует.", HttpStatus.ACCEPTED);

            User user = userService.create(signupRequest);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } else {
            User user = (User) userService.loadUserByUsername(signupRequest.getUsername());
            if (user.getValidationCode() != signupRequest.getCode())
                return new ResponseEntity<String>("Проверочный код неверный.", HttpStatus.ACCEPTED);

            userService.activate(user);
            return new ResponseEntity<String>("Вы успешно зарегистрировались.", HttpStatus.OK);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authUser(@RequestBody SigninRequest signinRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signinRequest.getUsername(),
                    signinRequest.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User user = (User) authentication.getPrincipal();

            Set<String> roles = user.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toSet());

            JwtResponse jwtResponse = new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles);
            return new ResponseEntity<JwtResponse>(jwtResponse, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<String>("Логин или пароль указаны неверно.", HttpStatus.ACCEPTED);
        }
    }
}