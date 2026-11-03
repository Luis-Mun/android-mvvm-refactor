package com.example.firstapp_luismunoz.data.repository;

import com.example.firstapp_luismunoz.data.dao.UserDao;
import com.example.firstapp_luismunoz.model.User;
import com.example.firstapp_luismunoz.utils.PasswordHasher;

import java.util.concurrent.Executor;

public class RoomUserRepository implements UserRepository {

    private final UserDao dao;
    private final Executor ioExecutor;

    public RoomUserRepository(UserDao dao, Executor ioExecutor) {
        this.dao = dao;
        this.ioExecutor = ioExecutor;
    }

    @Override
    public void register(String username, String password, RepositoryCallback<Boolean> callback) {
        ioExecutor.execute(() -> {
            try {
                long rowId = dao.insert(new User(username, PasswordHasher.hash(password)));
                callback.onSuccess(rowId != -1);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void login(String username, String password, RepositoryCallback<Boolean> callback) {
        ioExecutor.execute(() -> {
            try {
                User user = dao.findByUsername(username);
                callback.onSuccess(user != null && PasswordHasher.verify(password, user.getPasswordHash()));
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}
