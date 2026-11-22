package com.example.androidmessageapp.view.activities.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidmessageapp.data.entities.MessageEntity;
import com.example.androidmessageapp.data.repository.MessageRepository;
import com.example.androidmessageapp.databinding.MessageActivityBinding;
import com.example.androidmessageapp.view.activities.BaseActivity;
import com.example.androidmessageapp.view.adapter.MessageAdapter;

import java.util.UUID;

public class MessageActivity extends BaseActivity {
    MessageActivityBinding binding;
    MessageAdapter messageAdapter;
    MessageRepository messageRepository = new MessageRepository(this);

    // TODO : change to real uuid

    String myUUID = UUID.randomUUID().toString();
    String id = myUUID;
    String senderId = myUUID;
    String conversationId = myUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = MessageActivityBinding.inflate(
                LayoutInflater.from(this)
        );

        setContentView(binding.getRoot());

        initRcv();
        bindingFunction();
        loadMessages();
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


            // TODO: send to database

            MessageEntity newMessage = new MessageEntity(
                    senderId,
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

    // TODO:change to get by conversation_id
    private void loadMessages() {
        messageAdapter.setMsgList(messageRepository.getMessages());
    }
}
