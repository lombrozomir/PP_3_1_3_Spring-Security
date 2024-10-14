package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Optional;

@Controller
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/user")
    public String getUserPage(@AuthenticationPrincipal UserDetails currentUser, ModelMap model) {
        Optional<User> user = userService.findByUsername(currentUser.getUsername());
        model.addAttribute("user", user.orElse(null));
        model.addAttribute("currentUser", currentUser);
        return "user";
    }
}
