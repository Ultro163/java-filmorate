package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@SpringBootTest
class FilmControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

    }

    private String createJsonFilm() throws JsonProcessingException {
        Film film = Film.builder()
                .id(1L)
                .name("Fox")
                .description("Wild fox in snow forest")
                .releaseDate(LocalDate.of(2005, 4, 23))
                .duration(120)
                .build();
        return objectMapper.writeValueAsString(film);
    }

    @Test
    @DirtiesContext
    void testForGetFilms() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(createJsonFilm()));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fox"));
    }

    @Test
    @DirtiesContext
    void testForCreateFilm() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(createJsonFilm()))
                .andExpect(status().isCreated());
    }

    @Test
    @DirtiesContext
    void testForUpdateFilm() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(createJsonFilm()));

        Film film = Film.builder()
                .id(1L)
                .name("Frog")
                .description("Crazy frog in snow forest")
                .releaseDate(LocalDate.of(2005, 4, 23))
                .duration(1)
                .build();

        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Frog"));
    }
}