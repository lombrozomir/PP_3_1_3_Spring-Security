package ru.kata.spring.boot_security.demo.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserDao;


import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, @Lazy UserService userService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    @Transactional
    public void update(Long id, User user) {
        Optional<User> oldUser = userDao.findById(id);
        if (oldUser.isPresent()) {
            if (user.getPassword().isEmpty()) {
                user.setPassword(oldUser.get().getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user.setId(id);
            userDao.save(user);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return new User(user.get().getFirstName(), user.get().getLastName(), user.get().getAge(),
                user.get().getUsername(), user.get().getPassword(), user.get().getRoles());
    }
}
