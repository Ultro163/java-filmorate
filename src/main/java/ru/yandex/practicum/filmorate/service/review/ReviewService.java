package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewDbStorage;
    private final UserService userDbServiceImpl;
    private final FilmService filmDbServiceImpl;

    public Review getReview(Long id) {
        return reviewDbStorage.getReview(id);
    }

    public List<Review> getReviewsForFilm(Long filmId, Integer count) {
        return reviewDbStorage.getReviewsForFilm(filmId, count);
    }

    public Review addReview(Review review) {
        if (review.getUserId() <= 0 || review.getFilmId() <= 0) {
            throw new EntityNotFoundException("ID must be positive");
        }
        filmDbServiceImpl.getFilmById(review.getFilmId());
        userDbServiceImpl.getUserById(review.getUserId());
        return reviewDbStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        if (review.getUserId() <= 0 || review.getFilmId() <= 0) {
            throw new EntityNotFoundException("ID must be positive");
        }
        reviewDbStorage.getReview(review.getReviewId());
        return reviewDbStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        reviewDbStorage.getReview(id);
        reviewDbStorage.deleteReview(id);
    }

    public void addLikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.addLikeInReview(reviewId, userId);
    }

    public void addDislikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.addDislikeInReview(reviewId, userId);
    }

    public void deleteLikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.deleteLikeInReview(reviewId, userId);
    }

    public void deleteDislikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.deleteDislikeInReview(reviewId, userId);
    }

    private void checkId(Long reviewId, Long userId) {
        reviewDbStorage.getReview(reviewId);
        userDbServiceImpl.getUserById(userId);
    }
}