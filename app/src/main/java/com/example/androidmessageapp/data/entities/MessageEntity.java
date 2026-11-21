package com.example.androidmessageapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.androidmessageapp.common.TimeFormatter;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // Bắt buộc có để Room khởi tạo đối tượng khi đọc từ DB
@Entity(
        tableName = "messages",
        foreignKeys = {
                @ForeignKey(
                        entity = ConversationEntity.class,
                        parentColumns = "id",
                        childColumns = "conversationId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = UserEntitiy.class,
                        parentColumns = "id",
                        childColumns = "senderId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("senderId"),
                @Index("conversationId")
        }

)
public class MessageEntity {

    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String senderId;
    @NonNull
    private String conversationId;
    @NonNull
    private String content;
    private long sentAt;

    @Ignore
    public MessageEntity(@NonNull String senderId,
                         @NonNull String conversationId,
                         @NonNull String content,
                         long sentAt) {
        this.senderId = senderId;
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.sentAt = sentAt;
        this.conversationId = conversationId;
    }

    @Ignore
    public MessageEntity(@NonNull String senderId, @NonNull String conversationId, @NonNull String content) {
        this(
                senderId,
                conversationId,
                content,
                TimeFormatter.getCurrentLongTimeStamp()
        );
    }
}