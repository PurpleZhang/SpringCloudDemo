package com.example.userservice.controller;

import com.example.userservice.model.Users;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private Users user1;
    private Users user2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

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
    public void testGetAllUsers() throws Exception {
        List<Users> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("user2")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserByIdFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user1")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    public void testCreateUser() throws Exception {
        String newUserJson = "{\"id\":3,\"username\":\"newuser\",\"password\":\"newpassword\",\"role\":\"ROLE_USER\"}";

        when(userService.createUser(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setId(3L); // Set ID to simulate save operation
            return user;
        });

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.username", is("newuser")));

        verify(userService, times(1)).createUser(any(Users.class));
    }

    @Test
    public void testUpdateUserFound() throws Exception {
        String updatedUserJson = "{\"id\":1,\"username\":\"updateduser\",\"password\":\"updatedpassword\",\"role\":\"ROLE_USER\"}";

        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.createUser(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            return user;
        });

        mockMvc.perform(put("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("updateduser")));

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).createUser(any(Users.class));
    }

    @Test
    public void testUpdateUserNotFound() throws Exception {
        String updatedUserJson = "{\"id\":999,\"username\":\"updateduser\",\"password\":\"updatedpassword\",\"role\":\"ROLE_USER\"}";

        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/user/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
        verify(userService, never()).createUser(any(Users.class));
    }

    @Test
    public void testLogin( ) throws Exception {
        when(userService.login("user2", "password2")).thenReturn(user2);
        mockMvc.perform(post("/api/user/login?username=user2&password=password2"))
                .andExpect(status().isOk());
    }
}



