package com.example.firstapp_luismunoz.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.firstapp_luismunoz.model.User;

@Dao
public interface UserDao {

    /** @return the new row id, or -1 if the username is already taken. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);
}
