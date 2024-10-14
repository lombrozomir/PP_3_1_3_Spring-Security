package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;

@Component
public class DefaultDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadyLoaded = false;

    private final UserService userService;

    private final RoleService roleService;

    public DefaultDataLoader(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (alreadyLoaded) {
            return;
        }

        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(roleService.findByName("ROLE_ADMIN").orElse(null));
        adminRoles.add(roleService.findByName("ROLE_USER").orElse(null));

        User user = new User();
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setAge((short) 35);
        user.setUsername("admin@mail.ru");
        user.setPassword("admin");
        user.setRoles(adminRoles);
        userService.save(user);

        User user1 = new User();
        user1.setFirstName("user");
        user1.setLastName("user");
        user1.setAge((short) 30);
        user1.setUsername("user@mail.ru");
        user1.setPassword("user");
        Set<Role> userRole = new HashSet<>();
        userRole.add(roleService.findByName("ROLE_USER").orElse(null));
        user1.setRoles(userRole);
        userService.save(user1);


        alreadyLoaded = true;
    }

    @Transactional
    void createRoleIfNotFound(String roleName) {
        Optional<Role> optionalRole = roleService.findByName(roleName);
        if (optionalRole.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleService.save(role);
        }
    }
}
