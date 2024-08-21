package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

//    @GetMapping
//    public Collection<Post> findAll() {
//        return postService.findAll();
//    }

    @GetMapping
    public Collection<Post> findAll(
        @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
        @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
        @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort) {

            if (!(sort.equals("asc") || sort.equals("desc"))) {
                throw new IllegalArgumentException();
            }
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException();
            }
            Integer from = page * size;
            return postService.findAll(size, from, sort);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @GetMapping("/post/{postId}")
    @ResponseBody
    public Post findById(@PathVariable Long postId) {
        return postService.findPostById(postId);
    }

}