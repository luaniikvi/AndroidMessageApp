package com.example.androidmessageapp.view.activities.addfriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.androidmessageapp.data.entities.ConversationEntity;
import com.example.androidmessageapp.data.entities.ConversationMemberEntity;
import com.example.androidmessageapp.data.repository.ConversationRepository;
import com.example.androidmessageapp.data.repository.UserRepository;
import com.example.androidmessageapp.databinding.AddfriendAcitivityBinding;
import com.example.androidmessageapp.models.ConversationModel;
import com.example.androidmessageapp.models.UserModel;
import com.example.androidmessageapp.view.activities.BaseActivity;

import java.util.Objects;

public class AddFriendActivity extends BaseActivity {
    UserRepository userRepository;
    ConversationRepository conversationRepository;

    AddfriendAcitivityBinding binding;
    String senderId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddfriendAcitivityBinding.inflate(LayoutInflater.from(this));

        setContentView(binding.getRoot());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            senderId = bundle.getString("userId");
        }
        userRepository = new UserRepository(this);
        conversationRepository = new ConversationRepository(this);

        binding.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etAddFriendEmail.getText().toString().trim();
                if (email.isEmpty()) return;
                if (senderId == null || senderId.isEmpty()) {
                    Toast.makeText(AddFriendActivity.this, "Missing current user", Toast.LENGTH_SHORT).show();
                    return;
                }

                // CHECKING existed or send to themselves
                UserModel usr = userRepository.getByEmail(email);
                if (usr == null) {
                    Toast.makeText(AddFriendActivity.this, "User not existed", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (usr.getId() != null && usr.getId().equals(senderId)) {
                    Toast.makeText(AddFriendActivity.this, "Cannot add yourself", Toast.LENGTH_SHORT).show();
                    return;
                }

                //
                ConversationEntity newConversation = new ConversationEntity();
                conversationRepository.insertConversation(newConversation);
                ConversationMemberEntity senderMember = new ConversationMemberEntity(
                        newConversation.getId(),
                        senderId
                );
                ConversationMemberEntity receiveMember = new ConversationMemberEntity(
                        newConversation.getId(),
                        usr.getId()
                );

                conversationRepository.insertMember(senderMember);
                conversationRepository.insertMember(receiveMember);

                Toast.makeText(AddFriendActivity.this, "Friend added", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
