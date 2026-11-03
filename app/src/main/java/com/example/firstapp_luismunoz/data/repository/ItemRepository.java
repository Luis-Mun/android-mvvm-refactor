package com.example.firstapp_luismunoz.data.repository;

import androidx.lifecycle.LiveData;

import com.example.firstapp_luismunoz.model.Item;

import java.util.List;

/** The single source of truth for inventory data. ViewModels depend on this interface only. */
public interface ItemRepository {

    LiveData<List<Item>> getItems();

    void addItem(String name, int quantity, RepositoryCallback<Item> callback);

    void updateQuantity(long id, int newQuantity, RepositoryCallback<Item> callback);

    void deleteItem(long id, RepositoryCallback<Void> callback);
}
