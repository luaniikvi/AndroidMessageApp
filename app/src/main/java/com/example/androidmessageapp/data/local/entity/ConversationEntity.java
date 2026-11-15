package com.example.androidmessageapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversations")
public class ConversationEntity {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;
    @ColumnInfo(name = "last_message")
    private String lastMessage; // Nội dung tin nhắn cuối cùng để preview bên ngoài
    @ColumnInfo(name = "updated_at")
    private long updatedAt;
    @ColumnInfo(name = "unread_count")
    private int unreadCount;
    @ColumnInfo(name = "is_online")
    private boolean isOnline;

    public ConversationEntity(@NonNull String id,
                              String title,
                              String avatarUrl,
                              String lastMessage,
                              long updatedAt,
                              int unreadCount,
                              boolean isOnline)
    {
        this.id = id;
        this.title = title;
        this.avatarUrl = avatarUrl;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.unreadCount = unreadCount;
        this.isOnline = isOnline;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }
}
