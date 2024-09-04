package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();

    Film getFilmById(long id);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getFilmsWithSortedDirector(Long directorId, String sortBy);

    List<Film> searchFilms(String query, String by);

    List<Film> getCommonFilms(Long userId, Long friendId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLikeFilm(long filmId, long userId);

    void deleteLikeFromFilm(long filmId, long userId);

    void deleteFilm(long filmId);
}