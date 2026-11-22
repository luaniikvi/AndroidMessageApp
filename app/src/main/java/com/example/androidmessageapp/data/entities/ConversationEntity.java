package com.example.androidmessageapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity (
        tableName = "conversations"
)
public class ConversationEntity {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "conversation_name")
    private String conversationName;

    @Ignore
    public ConversationEntity(String conversationName) {
        this.id = UUID.randomUUID().toString();
        this.conversationName = conversationName;
    }
}
