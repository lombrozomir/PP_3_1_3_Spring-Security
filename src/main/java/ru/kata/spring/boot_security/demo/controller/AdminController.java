package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Optional;


@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/admin")
    public String printAllUsers(@AuthenticationPrincipal UserDetails currentUser, ModelMap model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("newUser", new User());
        model.addAttribute("currentUser", currentUser);
        return "admin";
    }

    @PostMapping("/admin/add")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin";
        }
        Optional<User> userFromDB = userService.findByUsername(user.getUsername());
        if (userFromDB.isPresent()) {
            return "redirect:/admin/error";
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/admin/update")
    public String addUser(@ModelAttribute("user") User user, @RequestParam("id") Long id) {
        Optional<User> userFromDB = userService.findByUsername(user.getUsername());
        System.out.println(user);
        if (userFromDB.isPresent() && !(userFromDB.get().getId().equals(id))) {
            return "redirect:/admin/error";
        }
        userService.update(id, user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/error")
    public String error() {
        return "/admin/error";
    }
}
