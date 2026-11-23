package com.example.androidmessageapp.view.activities.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidmessageapp.data.entities.MessageEntity;
import com.example.androidmessageapp.data.repository.MessageRepository;
import com.example.androidmessageapp.databinding.MessageActivityBinding;
import com.example.androidmessageapp.models.MessageModel;
import com.example.androidmessageapp.view.activities.BaseActivity;
import com.example.androidmessageapp.view.adapter.MessageAdapter;

import java.util.List;

public class MessageActivity extends BaseActivity {
    MessageActivityBinding binding;
    MessageAdapter messageAdapter;
    MessageRepository messageRepository = new MessageRepository(this);

    // TODO : change to real uuid

    String currentUserId;
    String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = MessageActivityBinding.inflate(
                LayoutInflater.from(this)
        );

        setContentView(binding.getRoot());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            currentUserId = bundle.getString("userId");
            conversationId = bundle.getString("conversationId");
        }
        else {
            Toast.makeText(this, "Missing bundle", Toast.LENGTH_SHORT).show();
        }


        initRcv();
        bindingFunction();
    }
    protected void onResume() {
        super.onResume();
        if (currentUserId != null && !currentUserId.isEmpty()) {
            loadMessages();
        }
    }


    public void initRcv() {
        messageAdapter = new MessageAdapter();
        binding.rvMessage.setAdapter(messageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvMessage.setLayoutManager(linearLayoutManager);
    }

    public void bindingFunction() {
        binding.btnSend.setOnClickListener(v -> {
            String msg = binding.etMessage.getText().toString().trim();
            if (msg.isEmpty()) return;
            binding.etMessage.setText("");


            MessageEntity newMessage = new MessageEntity(
                    currentUserId,
                    conversationId,
                    msg
            );
            messageRepository.insertMessage(newMessage);

            Toast.makeText(
                    MessageActivity.this,
                    "Them message",
                    Toast.LENGTH_SHORT
            ).show();
            //
            loadMessages();
        });
    }


    private void loadMessages() {
        List<MessageModel> messageModelList = messageRepository.getMessages();
        messageAdapter.setMsgList(messageModelList, currentUserId);
    }
}
