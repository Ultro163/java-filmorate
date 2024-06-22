package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        filmController.clear();
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
    void testForGetFilms() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(createJsonFilm()));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fox"));
    }

    @Test
    void testForCreateFilm() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(createJsonFilm()))
                .andExpect(status().isOk());
    }

    @Test
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