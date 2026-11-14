package com.example.androidmessageapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.androidmessageapp.data.local.entity.ConversationEntity;

import java.util.List;

@Dao
public interface ConversationDao {

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    LiveData<List<ConversationEntity>> getAllConversations();

    @Query("SELECT * FROM conversations WHERE id = :conversationId LIMIT 1")
    LiveData<ConversationEntity> getConversationById(String conversationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ConversationEntity> conversations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ConversationEntity conversation);

    @Query("UPDATE conversations SET lastMessage = :lastMessage, updatedAt = :updatedAt WHERE id = :conversationId")
    void updateLastMessage(String conversationId, String lastMessage, long updatedAt);

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :conversationId")
    void markAsRead(String conversationId);
}