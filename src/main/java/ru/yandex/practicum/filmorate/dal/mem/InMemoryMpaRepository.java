package ru.yandex.practicum.filmorate.dal.mem;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Repository
public class InMemoryMpaRepository implements MpaRepository {
    private Long count = 1L;
    private final Map<Long, Mpa> mpaMap = new HashMap<>();

    public Optional<Mpa> getById(Long id) {
        return Optional.ofNullable(mpaMap.get(id));
    }

    public List<Mpa> getAll() {
        return new ArrayList<>(mpaMap.values());
    }

    public void addMpa(String name) {
        Mpa mpa = new Mpa();
        mpa.setName(name);
        mpa.setId(count);
        mpaMap.put(count, mpa);
        count++;
    }
}
