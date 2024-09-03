package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
public class User {
    @NotNull(groups = {Update.class})
    private Long id;
    @Email
    private String email;
    @NotBlank
    @NotNull
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
