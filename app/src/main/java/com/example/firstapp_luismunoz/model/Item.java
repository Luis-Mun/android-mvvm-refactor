package com.example.firstapp_luismunoz.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/** An inventory item. Replaces the old TEXT-typed "items" row; quantity is now a real int. */
@Entity(tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private final long id;

    @NonNull
    private final String name;

    private final int quantity;

    /** Used by Room. */
    public Item(long id, @NonNull String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    /** Convenience constructor for new, not-yet-saved items. */
    @Ignore
    public Item(@NonNull String name, int quantity) {
        this(0, name, quantity);
    }

    public long getId() { return id; }

    @NonNull
    public String getName() { return name; }

    public int getQuantity() { return quantity; }

    public Item withQuantity(int newQuantity) {
        return new Item(id, name, newQuantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item other = (Item) o;
        return id == other.id && quantity == other.quantity && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity);
    }
}
