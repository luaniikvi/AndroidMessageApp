package com.example.androidmessageapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidmessageapp.data.entities.MessageEntity;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId")
    public List<MessageEntity> getAllById(String conversationId);

    @Query("SELECT * FROM messages")
    public List<MessageEntity> getAll();

    @Insert
    public void insert(MessageEntity messageEntity);

    @Update
    public void update(MessageEntity messageEntity);
}
