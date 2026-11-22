package com.example.androidmessageapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity (
        tableName = "conversation_members",
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
}
