package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film getFilmById(long id);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getRecommendationsFilmsForUser(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLikeFilm(long filmId, long userId);

    void deleteLikeFromFilm(long filmId, long userId);

    void deleteFilm(long filmId);
}