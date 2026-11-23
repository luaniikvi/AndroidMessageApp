package com.example.androidmessageapp.data.repository;

import android.content.Context;

import com.example.androidmessageapp.data.AppDatabase;
import com.example.androidmessageapp.data.dao.ConversationDao;
import com.example.androidmessageapp.data.dao.MessageDao;
import com.example.androidmessageapp.data.entities.ConversationEntity;
import com.example.androidmessageapp.data.entities.ConversationMemberEntity;
import com.example.androidmessageapp.data.enums.ConversationType;
import com.example.androidmessageapp.data.mapper.ConversationMapper;
import com.example.androidmessageapp.models.ConversationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationRepository {
    private final ConversationDao conversationDao;
    private final MessageDao messageDao;

    public ConversationRepository(Context context) {
        this.conversationDao = AppDatabase.getInstance(context).conversationDao();
        this.messageDao = AppDatabase.getInstance(context).messageDao();
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
        if(userId == null || userId.isEmpty()) return new ArrayList<>();
        List<ConversationModel> conversationModels = ConversationMapper.toModelList(conversationDao.getConversationsByUserId(userId));
        for (ConversationModel conversationModel : conversationModels) {
            if(Objects.equals(conversationModel.getType(), ConversationType.DIRECT.getValue()))
            {
                String otherName = conversationDao.getOtherMemberName(conversationModel.getId(), userId);
                String fallbackName = conversationModel.getConversationName();
                String name = (otherName != null && !otherName.trim().isEmpty())
                        ? otherName
                        : (fallbackName != null ? fallbackName : "");
                conversationModel.setConversationName(name);
            }
            else conversationModel.setConversationName("GROUP");

            String lastMessage = messageDao.getLastMessageByConversationId(conversationModel.getId());
            conversationModel.setLastMessage(lastMessage != null ? lastMessage : "");
        }

        return conversationModels;
    }

    public List<ConversationModel> getConversationsByEmail(String email) {
        return ConversationMapper.toModelList(conversationDao.getConversationsByEmail(email));
    }


}
