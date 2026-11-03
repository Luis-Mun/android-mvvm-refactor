package com.example.firstapp_luismunoz.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.firstapp_luismunoz.model.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM items ORDER BY name COLLATE NOCASE ASC, id ASC")
    LiveData<List<Item>> observeAll();

    @Insert
    long insert(Item item);

    @Query("UPDATE items SET quantity = :quantity WHERE id = :id")
    int updateQuantity(long id, int quantity);

    @Query("DELETE FROM items WHERE id = :id")
    int deleteById(long id);

    @Query("SELECT * FROM items WHERE id = :id")
    Item findById(long id);
}
