package com.example.firstapp_luismunoz.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firstapp_luismunoz.data.repository.ItemRepository;
import com.example.firstapp_luismunoz.data.repository.RepositoryCallback;
import com.example.firstapp_luismunoz.model.Item;
import com.example.firstapp_luismunoz.utils.AppConfig;
import com.example.firstapp_luismunoz.utils.Event;
import com.example.firstapp_luismunoz.utils.Validator;

import java.util.List;

/**
 * UI state and application logic for the inventory grid. Holds no Android framework references,
 * so it survives rotation and can be unit-tested on the JVM.
 */
public class MainViewModel extends ViewModel {

    private final ItemRepository repository;
    private final LiveData<List<Item>> items;
    private final MutableLiveData<Boolean> busy = new MutableLiveData<>(false);
    private final MutableLiveData<Event<String>> messages = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errors = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> itemAdded = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> lowStockAlerts = new MutableLiveData<>();

    public MainViewModel(ItemRepository repository) {
        this.repository = repository;
        this.items = repository.getItems();
    }

    public LiveData<List<Item>> getItems() { return items; }

    /** True while a write is in flight. */
    public LiveData<Boolean> isBusy() { return busy; }

    /** One-shot success messages for the View to show. */
    public LiveData<Event<String>> getMessages() { return messages; }

    /** One-shot error messages (validation or storage failures). */
    public LiveData<Event<String>> getErrors() { return errors; }

    /** Fires once after an item is saved, so the View can clear its input fields. */
    public LiveData<Event<Boolean>> getItemAdded() { return itemAdded; }

    /** One-shot request to notify the owner; payload is the SMS text. The View sends it. */
    public LiveData<Event<String>> getLowStockAlerts() { return lowStockAlerts; }

    public void addItem(String rawName, String rawQuantity) {
        String error = firstNonNull(Validator.validateItemName(rawName), Validator.validateQuantity(rawQuantity));
        if (error != null) {
            errors.setValue(new Event<>(error));
            return;
        }
        String name = rawName.trim();
        int quantity = Integer.parseInt(rawQuantity.trim());

        busy.setValue(true);
        repository.addItem(name, quantity, new RepositoryCallback<Item>() {
            @Override
            public void onSuccess(Item saved) {
                busy.postValue(false);
                messages.postValue(new Event<>("Item added"));
                itemAdded.postValue(new Event<>(true));
                if (quantity <= AppConfig.LOW_STOCK_THRESHOLD) postLowStock(name);
            }

            @Override
            public void onError(Exception e) {
                busy.postValue(false);
                errors.postValue(new Event<>("Could not add item"));
            }
        });
    }

    /** Changes quantity by {@code delta}, never below zero. */
    public void adjustQuantity(Item item, int delta) {
        int newQuantity = Math.max(0, item.getQuantity() + delta);
        if (newQuantity == item.getQuantity()) return;
        int oldQuantity = item.getQuantity();

        busy.setValue(true);
        repository.updateQuantity(item.getId(), newQuantity, new RepositoryCallback<Item>() {
            @Override
            public void onSuccess(Item updated) {
                busy.postValue(false);
                boolean crossedThreshold = newQuantity <= AppConfig.LOW_STOCK_THRESHOLD
                        && oldQuantity > AppConfig.LOW_STOCK_THRESHOLD;
                if (crossedThreshold) postLowStock(item.getName());
            }

            @Override
            public void onError(Exception e) {
                busy.postValue(false);
                errors.postValue(new Event<>("Could not update item"));
            }
        });
    }

    public void deleteItem(Item item) {
        busy.setValue(true);
        repository.deleteItem(item.getId(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                busy.postValue(false);
                messages.postValue(new Event<>("Item deleted"));
            }

            @Override
            public void onError(Exception e) {
                busy.postValue(false);
                errors.postValue(new Event<>("Could not delete item"));
            }
        });
    }

    private void postLowStock(String itemName) {
        lowStockAlerts.postValue(new Event<>("Alert: " + itemName + " is out of stock!"));
    }

    private static String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }
}
