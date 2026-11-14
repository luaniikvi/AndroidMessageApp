package com.example.androidmessageapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.androidmessageapp.data.local.dao.ConversationDao;
import com.example.androidmessageapp.data.local.dao.MessageDao;
import com.example.androidmessageapp.data.local.entity.ConversationEntity;
import com.example.androidmessageapp.data.local.entity.MessageEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                ConversationEntity.class,
                MessageEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ConversationDao conversationDao();
    public abstract MessageDao messageDao();

    private static volatile AppDatabase INSTANCE;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "chat_app_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}