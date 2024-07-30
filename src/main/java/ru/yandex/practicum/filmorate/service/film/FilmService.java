package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAllFilms();

    Film getFilmById(long id);

    List<Film> getPopularFilms(Integer count);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLikeFilm(long filmId, long userId);

    void deleteLikeFromFilm(long filmId, long userId);
}