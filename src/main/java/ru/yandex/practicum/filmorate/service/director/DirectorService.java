package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;


    public List<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }

    public Director getDirector(Long id) {
        return directorDbStorage.getDirector(id);
    }

    public Director createDirector(Director director) {
        return directorDbStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        getDirector(director.getId());
        return directorDbStorage.updateDirector(director);
    }

    public void deleteDirector(Long id) {
        getDirector(id);
        directorDbStorage.deleteDirector(id);
    }

    public Set<Director> getDirectorsForFilm(Long fimId) {
        return directorDbStorage.getDirectorsForFilm(fimId).stream()
                .sorted(Comparator.comparingLong(Director::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}