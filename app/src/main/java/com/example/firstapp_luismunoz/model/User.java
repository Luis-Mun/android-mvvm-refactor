package com.example.firstapp_luismunoz.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** An app user. Only a salted password hash is stored, never the password itself. */
@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    private final String username;

    @NonNull
    private final String passwordHash;

    public User(@NonNull String username, @NonNull String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @NonNull
    public String getUsername() { return username; }

    @NonNull
    public String getPasswordHash() { return passwordHash; }
}
