package com.example.userservice.service;

import com.example.userservice.model.Users;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Users user1;
    private Users user2;

    @BeforeEach
    public void setUp() {
        user1 = new Users();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setRole("ROLE_USER");

        user2 = new Users();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setRole("ROLE_ADMIN");
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(any(Users.class))).thenReturn(user1);

        Users createdUser = userService.createUser(user1);

        assertNotNull(createdUser);
        assertEquals(user1.getId(), createdUser.getId());
        assertEquals(user1.getUsername(), createdUser.getUsername());
        assertEquals(user1.getPassword(), createdUser.getPassword()); // Ensure password is not encrypted

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    public void testGetUserByUsernameFound() {
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));

        Optional<Users> foundUser = userService.getUserByUsername(user1.getUsername());

        assertTrue(foundUser.isPresent());
        assertEquals(user1.getId(), foundUser.get().getId());
        assertEquals(user1.getUsername(), foundUser.get().getUsername());

        verify(userRepository, times(1)).findByUsername(user1.getUsername());
    }

    @Test
    public void testGetUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<Users> foundUser = userService.getUserByUsername("nonexistent");

        assertFalse(foundUser.isPresent());

        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    public void testGetAllUsers() {
        List<Users> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<Users> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        assertEquals(user1.getId(), allUsers.get(0).getId());
        assertEquals(user2.getId(), allUsers.get(1).getId());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserByIdFound() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        Optional<Users> foundUser = userService.getUserById(user1.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user1.getId(), foundUser.get().getId());
        assertEquals(user1.getUsername(), foundUser.get().getUsername());

        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Users> foundUser = userService.getUserById(999L);

        assertFalse(foundUser.isPresent());

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userRepository).deleteById(user1.getId());

        userService.deleteUser(user1.getId());

        verify(userRepository, times(1)).deleteById(user1.getId());
    }
}



