package com.example.androidmessageapp.data.mapper;

import com.example.androidmessageapp.common.TimeFormatter;
import com.example.androidmessageapp.data.entities.MessageEntity;
import com.example.androidmessageapp.models.MessageModel;

import java.util.ArrayList;
import java.util.List;

public final class MessageMapper {
    private MessageMapper() {}

    public static MessageModel toModel(MessageEntity entity) {
        if (entity == null) return null;
        MessageModel model = new MessageModel();
        model.setId(entity.getId());
        model.setSenderId(entity.getSenderId());
        model.setContent(entity.getContent());
        model.setFormatTime(TimeFormatter.format(entity.getSentAt()));
        return model;
    }

    public static List<MessageModel> toModelList(List<MessageEntity> entities) {
        List<MessageModel> models = new ArrayList<>();
        if (entities != null) {
            for (MessageEntity entity : entities) {
                models.add(toModel(entity));
            }
        }
        return models;
    }
}
