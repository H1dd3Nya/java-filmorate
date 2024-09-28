package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    @Qualifier("JdbcMpaRepository")
    private final MpaRepository mpaRepository;

    @GetMapping
    public List<Mpa> getAll() {
        return mpaRepository.getAll();
    }

    @GetMapping("/{id}")
    public Mpa get(@PathVariable Long id) {
        return mpaRepository.getById(id).orElseThrow(() -> new NotFoundException("MPA not found"));
    }
}
