package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
<<<<<<< HEAD
=======
import java.util.HashMap;
import java.util.Map;
>>>>>>> 6d84347 (Use FriendshipStatus map in User; adjust UserService logic for friend requests and confirmations and add DB diagram, DDL and SQL examples)

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
<<<<<<< HEAD
=======
    private final Map<Integer, FriendshipStatus> friends = new HashMap<>();
>>>>>>> 6d84347 (Use FriendshipStatus map in User; adjust UserService logic for friend requests and confirmations and add DB diagram, DDL and SQL examples)
}