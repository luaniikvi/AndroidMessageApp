package com.example.androidmessageapp.data.repository;

import android.content.Context;

import com.example.androidmessageapp.data.AppDatabase;
import com.example.androidmessageapp.data.dao.ConversationDao;
import com.example.androidmessageapp.data.entities.ConversationEntity;
import com.example.androidmessageapp.data.entities.ConversationMemberEntity;
import com.example.androidmessageapp.data.mapper.ConversationMapper;
import com.example.androidmessageapp.models.ConversationModel;

import java.util.List;

public class ConversationRepository {
    private final ConversationDao conversationDao;

    public ConversationRepository(Context context) {
        this.conversationDao = AppDatabase.getInstance(context).conversationDao();
    }

    public void insertConversation(ConversationEntity conversationEntity) {
        conversationDao.insertConversation(conversationEntity);
    }

    public void insertMember(ConversationMemberEntity conversationMember){
        conversationDao.insertMember(conversationMember);
    }
    public void insertMembers(List<ConversationMemberEntity> members) {
        conversationDao.insertMembers(members);
    }

    public List<ConversationModel> getConversationsByUserId(String userId) {
        return ConversationMapper.toModelList(conversationDao.getConversationsByUserId(userId));
    }

    public List<ConversationModel> getConversationsByEmail(String email) {
        return ConversationMapper.toModelList(conversationDao.getConversationsByEmail(email));
    }


}
