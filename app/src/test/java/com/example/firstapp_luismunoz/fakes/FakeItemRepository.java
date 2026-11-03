package com.example.firstapp_luismunoz.fakes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.firstapp_luismunoz.data.repository.ItemRepository;
import com.example.firstapp_luismunoz.data.repository.RepositoryCallback;
import com.example.firstapp_luismunoz.model.Item;

import java.util.ArrayList;
import java.util.List;

/** In-memory repository that runs synchronously, for ViewModel tests. */
public class FakeItemRepository implements ItemRepository {

    private final List<Item> store = new ArrayList<>();
    private final MutableLiveData<List<Item>> liveItems = new MutableLiveData<>(new ArrayList<>());
    private long nextId = 1;
    public boolean failNextCall = false;

    @Override
    public LiveData<List<Item>> getItems() { return liveItems; }

    @Override
    public void addItem(String name, int quantity, RepositoryCallback<Item> callback) {
        if (consumeFailure()) { callback.onError(new RuntimeException("boom")); return; }
        Item item = new Item(nextId++, name, quantity);
        store.add(item);
        publish();
        callback.onSuccess(item);
    }

    @Override
    public void updateQuantity(long id, int newQuantity, RepositoryCallback<Item> callback) {
        if (consumeFailure()) { callback.onError(new RuntimeException("boom")); return; }
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId() == id) {
                Item updated = store.get(i).withQuantity(newQuantity);
                store.set(i, updated);
                publish();
                callback.onSuccess(updated);
                return;
            }
        }
        callback.onError(new IllegalStateException("missing"));
    }

    @Override
    public void deleteItem(long id, RepositoryCallback<Void> callback) {
        if (consumeFailure()) { callback.onError(new RuntimeException("boom")); return; }
        store.removeIf(i -> i.getId() == id);
        publish();
        callback.onSuccess(null);
    }

    public List<Item> snapshot() { return new ArrayList<>(store); }

    private boolean consumeFailure() {
        boolean fail = failNextCall;
        failNextCall = false;
        return fail;
    }

    private void publish() { liveItems.setValue(new ArrayList<>(store)); }
}
