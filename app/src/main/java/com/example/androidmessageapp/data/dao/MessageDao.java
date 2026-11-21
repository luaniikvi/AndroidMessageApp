package com.example.androidmessageapp.data.dao;

import androidx.room.Insert;
import androidx.room.Query;

import com.example.androidmessageapp.data.entities.MessageEntity;
import com.example.androidmessageapp.models.MessageModel;

import java.util.List;

public interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId")
    public List<MessageEntity> getAll(String conversationId);

    @Insert
    public void insert(MessageEntity message);
}
