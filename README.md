# java-filmorate
Template repository for Filmorate project.

# Database schema
![text](db_diagram.png)

# Requests
+ Getting all movies: ```SELECT * FROM films;```
+ Getting all users: ```SELECT * FROM users;```
+ Getting all genres: ```"SELECT * FROM genres ORDER BY genre_id"```
+ Getting all mpa: ```"SELECT * FROM rating_mpa ORDER BY rating_id"```
+ Getting friends list: ```"SELECT user2_id as user_id, email, login, name, birthday
  FROM friendship
  INNER JOIN users ON friendship.user2_id = users.user_id
  WHERE friendship.user1_id = ?"```