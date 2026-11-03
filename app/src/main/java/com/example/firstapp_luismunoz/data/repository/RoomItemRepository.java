package com.example.firstapp_luismunoz.data.repository;

import androidx.lifecycle.LiveData;

import com.example.firstapp_luismunoz.data.dao.ItemDao;
import com.example.firstapp_luismunoz.model.Item;

import java.util.List;
import java.util.concurrent.Executor;

public class RoomItemRepository implements ItemRepository {

    private final ItemDao dao;
    private final Executor ioExecutor;

    public RoomItemRepository(ItemDao dao, Executor ioExecutor) {
        this.dao = dao;
        this.ioExecutor = ioExecutor;
    }

    @Override
    public LiveData<List<Item>> getItems() {
        return dao.observeAll();
    }

    @Override
    public void addItem(String name, int quantity, RepositoryCallback<Item> callback) {
        ioExecutor.execute(() -> {
            try {
                long id = dao.insert(new Item(name, quantity));
                callback.onSuccess(new Item(id, name, quantity));
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void updateQuantity(long id, int newQuantity, RepositoryCallback<Item> callback) {
        ioExecutor.execute(() -> {
            try {
                if (dao.updateQuantity(id, newQuantity) == 0) {
                    callback.onError(new IllegalStateException("Item no longer exists"));
                    return;
                }
                callback.onSuccess(dao.findById(id));
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void deleteItem(long id, RepositoryCallback<Void> callback) {
        ioExecutor.execute(() -> {
            try {
                dao.deleteById(id);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}
