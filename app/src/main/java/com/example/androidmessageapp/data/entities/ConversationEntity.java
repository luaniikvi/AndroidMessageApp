package com.example.androidmessageapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.androidmessageapp.data.enums.ConversationType;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
/*@NoArgsConstructor*/
@Entity (
        tableName = "conversations"
)
public class ConversationEntity {

    private static final String TYPE_DIRECT = ConversationType.DIRECT.getValue();
    private static final String TYPE_GROUP = ConversationType.GROUP.getValue();

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "conversation_name")
    private String conversationName;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    public ConversationEntity(){
        this.id = UUID.randomUUID().toString();
        this.conversationName = "";
        this.type = TYPE_DIRECT;
    }

    @Ignore
    public ConversationEntity(String conversationName) {
        this.id = UUID.randomUUID().toString();
        this.conversationName = conversationName;
        this.type = TYPE_DIRECT;
    }

    @Ignore
    public ConversationEntity(String conversationName,@NonNull String type) {
        this.id = UUID.randomUUID().toString();
        this.conversationName = conversationName;
        this.type = type;
    }

    @Ignore
    public ConversationEntity(String conversationName,ConversationType type) {
        this.id = UUID.randomUUID().toString();
        this.conversationName = conversationName;
        this.type = type  == null ? TYPE_DIRECT : type.getValue();
    }
}
