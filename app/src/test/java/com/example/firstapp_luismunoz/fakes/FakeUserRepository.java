package com.example.firstapp_luismunoz.fakes;

import com.example.firstapp_luismunoz.data.repository.RepositoryCallback;
import com.example.firstapp_luismunoz.data.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class FakeUserRepository implements UserRepository {

    private final Map<String, String> users = new HashMap<>();
    public boolean failNextCall = false;

    @Override
    public void register(String username, String password, RepositoryCallback<Boolean> callback) {
        if (failNextCall) { failNextCall = false; callback.onError(new RuntimeException("boom")); return; }
        boolean created = users.putIfAbsent(username, password) == null;
        callback.onSuccess(created);
    }

    @Override
    public void login(String username, String password, RepositoryCallback<Boolean> callback) {
        if (failNextCall) { failNextCall = false; callback.onError(new RuntimeException("boom")); return; }
        callback.onSuccess(password.equals(users.get(username)));
    }
}
