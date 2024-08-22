package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final UserService userService;

    private final Map<Long, Post> posts = new HashMap<>();

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

//    public Collection<Post> findAll() {
//        return posts.values();
//    }

    public Collection<Post> findAll(Integer size, Integer from, SortOrder sort) {
        return posts.values()
                .stream()
                .sorted((p0, p1) -> {
                    int comp = 1;
                    if (sort.equals(SortOrder.ASCENDING)) {
                        comp = p0.getPostDate().compareTo(p1.getPostDate());
                    } //прямой порядок сортировки
                    if (sort.equals(SortOrder.DESCENDING)) {
                        comp = -1 * comp; //обратный порядок сортировки
                    }
                    return comp;
                }).skip(from).limit(size).collect(Collectors.toList());
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Long authorId = post.getAuthorId();
        if (userService.findAuthorById(authorId).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + authorId + " не найден");
        }
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Optional<Post> findPostById(Long id) {
        return Optional.ofNullable(posts.values()
                .stream()
                .filter(x -> x.getId().equals(id))
                .findFirst().orElseThrow(() -> new ConditionsNotMetException("Автор с id = " + id + " не найден")));
    }
}