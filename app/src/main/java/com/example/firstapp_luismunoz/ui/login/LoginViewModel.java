package com.example.firstapp_luismunoz.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firstapp_luismunoz.data.repository.RepositoryCallback;
import com.example.firstapp_luismunoz.data.repository.UserRepository;
import com.example.firstapp_luismunoz.utils.Event;
import com.example.firstapp_luismunoz.utils.Validator;

public class LoginViewModel extends ViewModel {

    private final UserRepository repository;
    private final MutableLiveData<Boolean> busy = new MutableLiveData<>(false);
    private final MutableLiveData<Event<String>> messages = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> loginSuccess = new MutableLiveData<>();

    public LoginViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> isBusy() { return busy; }

    public LiveData<Event<String>> getMessages() { return messages; }

    /** Fires once with the username after a successful login. */
    public LiveData<Event<String>> getLoginSuccess() { return loginSuccess; }

    public void login(String rawUsername, String password) {
        String username = rawUsername == null ? "" : rawUsername.trim();
        if (username.isEmpty() || password == null || password.isEmpty()) {
            messages.setValue(new Event<>("Please enter all fields"));
            return;
        }
        busy.setValue(true);
        repository.login(username, password, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean valid) {
                busy.postValue(false);
                if (valid) {
                    loginSuccess.postValue(new Event<>(username));
                } else {
                    messages.postValue(new Event<>("Invalid credentials"));
                }
            }

            @Override
            public void onError(Exception e) {
                busy.postValue(false);
                messages.postValue(new Event<>("Login failed. Please try again."));
            }
        });
    }

    public void register(String rawUsername, String password) {
        String username = rawUsername == null ? "" : rawUsername.trim();
        if (username.isEmpty() || password == null || password.isEmpty()) {
            messages.setValue(new Event<>("Please enter all fields"));
            return;
        }
        String error = Validator.validateUsername(username);
        if (error == null) error = Validator.validatePassword(password);
        if (error != null) {
            messages.setValue(new Event<>(error));
            return;
        }
        busy.setValue(true);
        repository.register(username, password, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean created) {
                busy.postValue(false);
                messages.postValue(new Event<>(created ? "Registration successful" : "User already exists"));
            }

            @Override
            public void onError(Exception e) {
                busy.postValue(false);
                messages.postValue(new Event<>("Registration failed. Please try again."));
            }
        });
    }
}
