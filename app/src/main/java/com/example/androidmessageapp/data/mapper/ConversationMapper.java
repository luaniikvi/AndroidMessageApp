package com.example.androidmessageapp.data.mapper;

import com.example.androidmessageapp.data.entities.ConversationEntity;
import com.example.androidmessageapp.models.ConversationModel;

import java.util.ArrayList;
import java.util.List;

public final class ConversationMapper {
    private ConversationMapper(){}

    public static ConversationModel toModel(ConversationEntity entity) {
        if (entity == null) return null;
        ConversationModel model = new ConversationModel();
        model.setId(entity.getId());
        model.setConversationName(entity.getConversationName());
        return model;
    }

    public static List<ConversationModel> toModelList(List<ConversationEntity> entities) {
        List<ConversationModel> models = new ArrayList<>();
        if (entities != null) {
            for (ConversationEntity entity : entities) {
                models.add(toModel(entity));
            }
        }
        return models;
    }
}
