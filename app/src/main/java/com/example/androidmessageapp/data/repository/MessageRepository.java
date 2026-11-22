package com.example.androidmessageapp.data.repository;

import android.content.Context;

import com.example.androidmessageapp.data.dao.MessageDao;
import com.example.androidmessageapp.data.AppDatabase;
import com.example.androidmessageapp.data.entities.MessageEntity;
import com.example.androidmessageapp.data.mapper.MessageMapper;
import com.example.androidmessageapp.models.MessageModel;

import java.util.List;

public class MessageRepository {
    private final MessageDao messageDao;

    public MessageRepository(Context context) {
        this.messageDao = AppDatabase.getInstance(context).messageDao();
    }

    public void insertMessage(MessageEntity entity) {
        messageDao.insert(entity);
    }

    public List<MessageModel> getMessages(String conversationId) {
        List<MessageEntity> entities = messageDao.getAllById(conversationId);
        return MessageMapper.toModelList(entities);
    }

    public List<MessageModel> getMessages() {
        return MessageMapper.toModelList(messageDao.getAll());
    }
}
