package ru.yandex.practicum.filmorate.dal.mem;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class InMemoryGenreRepository implements GenreRepository {
    private Long count = 1L;
    private final Map<Long, Genre> genres = new HashMap<>();

    public Optional<Genre> getById(Long id) {
        return Optional.ofNullable(genres.get(id));
    }

    public List<Genre> getAll() {
        return new ArrayList<>(genres.values());
    }

    public void addGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        genre.setId(count);

        genres.put(genre.getId(), genre);
        count++;
    }
}