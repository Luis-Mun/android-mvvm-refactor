package com.example.firstapp_luismunoz.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.firstapp_luismunoz.data.dao.ItemDao;
import com.example.firstapp_luismunoz.data.database.AppDatabase;
import com.example.firstapp_luismunoz.model.Item;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Verifies Room persistence against a real (in-memory) SQLite database. */
@RunWith(AndroidJUnit4.class)
public class ItemDaoTest {

    private AppDatabase db;
    private ItemDao dao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        dao = db.itemDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndFind() {
        long id = dao.insert(new Item("Apples", 4));
        Item found = dao.findById(id);
        assertNotNull(found);
        assertEquals("Apples", found.getName());
        assertEquals(4, found.getQuantity());
    }

    @Test
    public void updateQuantity() {
        long id = dao.insert(new Item("Apples", 4));
        assertEquals(1, dao.updateQuantity(id, 9));
        assertEquals(9, dao.findById(id).getQuantity());
    }

    @Test
    public void deleteById() {
        long id = dao.insert(new Item("Apples", 4));
        assertEquals(1, dao.deleteById(id));
        assertNull(dao.findById(id));
    }
}
