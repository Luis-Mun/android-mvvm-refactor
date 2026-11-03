package com.example.firstapp_luismunoz.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.firstapp_luismunoz.data.dao.ItemDao;
import com.example.firstapp_luismunoz.data.dao.UserDao;
import com.example.firstapp_luismunoz.model.Item;
import com.example.firstapp_luismunoz.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room database. Uses a new file name ("inventory.db") so it never collides with the
 * legacy hand-written SQLite file ("AppDatabase.db") from the original app.
 */
@Database(entities = {Item.class, User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "inventory.db";

    /** Single thread keeps writes ordered and off the main thread. */
    public static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();

    private static volatile AppDatabase instance;

    public abstract ItemDao itemDao();

    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }
}
