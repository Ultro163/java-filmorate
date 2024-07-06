package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
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
    @DirtiesContext
    void testForGetUsers() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(creatJsonUser()));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("Skuf@maila.net"))
                .andExpect(jsonPath("$.name").value("MegaPusher"));
    }

    @Test
    @DirtiesContext
    void testForCreateUser() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(creatJsonUser()))
                .andExpect(status().isCreated());
    }

    @Test
    @DirtiesContext
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