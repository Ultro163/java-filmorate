package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userController.clearUsers();
    }

    private String creatJsonUser() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .build();
        return objectMapper.writeValueAsString(user);
    }

    @Test
    void testForGetUsers() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(creatJsonUser()));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("Skuf@maila.net"))
                .andExpect(jsonPath("$[0].name").value("MegaPusher"));
    }

    @Test
    void testForCreateUser() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(creatJsonUser()))
                .andExpect(status().isOk());
    }

    @Test
    void testForUpdateUser() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(creatJsonUser()));

        User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("Mol")
                .birthday(LocalDate.of(1987, 7, 12))
                .build();
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("Skuf@maila.net"))
                .andExpect(jsonPath("$.name").value("Mol"));
    }
}