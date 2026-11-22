package com.example.androidmessageapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity (
        tableName = "conversation_members",
        primaryKeys = {"conversation_id", "user_id"},
        foreignKeys = {
                @ForeignKey(
                        entity = ConversationEntity.class,
                        parentColumns = "id",
                        childColumns = "conversation_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("conversation_id"),
                @Index("user_id")
        }
)
public class ConversationMemberEntity {
    @NonNull
    @ColumnInfo(name = "conversation_id")
    private String conversationId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @Ignore
    public ConversationMemberEntity(@NonNull String conversationId, @NonNull String userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }
}
