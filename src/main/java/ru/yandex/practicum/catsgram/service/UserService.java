package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (checkDuplicatedEmail(user)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (checkDuplicatedEmail(newUser)) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            // если user найдена и все условия соблюдены, обновляем её содержимое
            User oldUser = users.get(newUser.getId());

            return oldUser;
        }
        throw new NotFoundException("Пост с id = " + newUser.getId() + " не найден");
    }


    private boolean checkDuplicatedEmail(User user) {
        return users.values().stream().anyMatch(list -> list.getEmail().equals(user.getEmail()));
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User findUserById(Long id) {
        Optional<User> op = users.values()
                .stream()
                .filter(x -> x.getId().equals(id))
                .findFirst();
        if (op.isEmpty()) {
            throw new ConditionsNotMetException("Id " + id + " не в списке");
        }
        return op.get();
    }

    public Optional<User> findAuthorById(Long authorId) {
        return Optional.ofNullable(users.get(authorId));
    }
}