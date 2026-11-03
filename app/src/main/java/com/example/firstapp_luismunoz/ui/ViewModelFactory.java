package com.example.firstapp_luismunoz.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.firstapp_luismunoz.data.database.AppDatabase;
import com.example.firstapp_luismunoz.data.repository.RoomItemRepository;
import com.example.firstapp_luismunoz.data.repository.RoomUserRepository;
import com.example.firstapp_luismunoz.ui.login.LoginViewModel;
import com.example.firstapp_luismunoz.ui.main.MainViewModel;

/** Wires ViewModels to their repositories (manual dependency injection). */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public ViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        AppDatabase db = AppDatabase.getInstance(application);
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(new RoomItemRepository(db.itemDao(), AppDatabase.IO_EXECUTOR));
        }
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(new RoomUserRepository(db.userDao(), AppDatabase.IO_EXECUTOR));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
