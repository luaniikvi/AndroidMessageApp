package com.example.androidmessageapp.data.local.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity (
        tableName = "messages",
        foreignKeys = @ForeignKey(
                entity = ConversationEntity.class,
                parentColumns = "id",
                childColumns = "conversationId",
                onDelete = ForeignKey.CASCADE
        ),
        // Đánh index cho conversationId để truy vấn nhanh
        indices = {@Index("conversationId")}
)
public class MessageEntity {
    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String conversationId;

    @NonNull
    private String senderId;

    private String type;           // Loại: "TEXT", "IMAGE", "AUDIO", "VIDEO_CALL", "VOICE_CALL"
    private String content;
    private String status;         // SENDING, SENT, DELIVERED, READ
    private long createdAt;

    public MessageEntity(@NonNull String id,
                         @NonNull String conversationId,
                         @NonNull String senderId,
                         String type, String content,
                         String status,
                         long createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.type = type;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getConversationId() { return conversationId; }
    public void setConversationId(@NonNull String conversationId) { this.conversationId = conversationId; }

    @NonNull
    public String getSenderId() { return senderId; }
    public void setSenderId(@NonNull String senderId) { this.senderId = senderId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
