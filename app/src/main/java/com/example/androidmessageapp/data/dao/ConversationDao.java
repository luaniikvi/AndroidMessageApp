package com.example.androidmessageapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.androidmessageapp.data.entities.ConversationEntity;
import com.example.androidmessageapp.data.entities.ConversationMemberEntity;
import com.example.androidmessageapp.data.entities.MessageEntity;

import java.util.List;

@Dao
public interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertConversation(ConversationEntity conversationEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertMember(ConversationMemberEntity conversationMember);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertMembers(List<ConversationMemberEntity> members);

    @Query("SELECT c.* FROM conversations c " +
            "INNER JOIN conversation_members cm ON c.id = cm.conversation_id " +
            "WHERE cm.user_id = :userId")
    public List<ConversationEntity> getConversationsByUserId(String userId);

    @Query("SELECT conversations.* FROM conversations " +
            "INNER JOIN conversation_members ON conversations.id=conversation_members.conversation_id " +
            "INNER JOIN users ON conversation_members.user_id=users.id "+
            "WHERE users.email=:email")
    public List<ConversationEntity> getConversationsByEmail(String email);


}
