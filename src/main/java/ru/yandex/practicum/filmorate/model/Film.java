package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
<<<<<<< HEAD
=======

    private Set<Integer> genreIds = new HashSet<>();

    private Integer mpaRatingId;

    private final Set<Integer> likes = new HashSet<>();

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }

    public int getLikes() {
        return likes.size();
    }
>>>>>>> 6d84347 (Use FriendshipStatus map in User; adjust UserService logic for friend requests and confirmations and add DB diagram, DDL and SQL examples)
}