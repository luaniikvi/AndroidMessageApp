package com.example.androidmessageapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.androidmessageapp.data.local.entity.MessageEntity;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    LiveData<List<MessageEntity>> getMessagesByConversation(String conversationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MessageEntity> messages);

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    void updateMessageStatus(String messageId, String status);

    @Query("SELECT * FROM messages WHERE status = 'SENDING' ORDER BY createdAt ASC")
    List<MessageEntity> getPendingMessages();
}