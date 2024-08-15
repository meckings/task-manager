package com.example.task_management.auth.service;

import com.example.task_management.auth.dto.UserDto;
import com.example.task_management.auth.entity.Role;
import com.example.task_management.auth.entity.User;
import com.example.task_management.auth.repository.UserRepository;
import com.example.task_management.common.enums.Roles;
import com.example.task_management.common.exceptions.DuplicateResourceException;
import com.example.task_management.common.util.EntityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);

        Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Transactional
    public User save(UserDto dto) {
        Role role = roleService.findByName(Roles.ROLE_USER.name());
        User user = EntityUtil.getForCreate(User.class, dto);
        user.setRoles(List.of(role));
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new DuplicateResourceException("User already exists");
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void assignRole(long userId, long roleId) {
        Role role = roleService.findById(roleId);
        User user = findById(userId);
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
