package com.example.firstapp_luismunoz.data.repository;

public interface UserRepository {

    /** Callback result is true if the account was created, false if the username is taken. */
    void register(String username, String password, RepositoryCallback<Boolean> callback);

    /** Callback result is true if the credentials are valid. */
    void login(String username, String password, RepositoryCallback<Boolean> callback);
}
