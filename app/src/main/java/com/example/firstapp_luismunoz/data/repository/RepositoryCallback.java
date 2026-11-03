package com.example.firstapp_luismunoz.data.repository;

/** Result callback for repository operations. May be invoked on a background thread. */
public interface RepositoryCallback<T> {
    void onSuccess(T result);

    void onError(Exception error);
}
