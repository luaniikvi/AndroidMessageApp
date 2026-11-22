package com.example.androidmessageapp.view.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidmessageapp.data.repository.ConversationRepository;
import com.example.androidmessageapp.data.repository.UserRepository;
import com.example.androidmessageapp.databinding.ActivityMainBinding;
import com.example.androidmessageapp.models.ConversationModel;
import com.example.androidmessageapp.view.activities.addfriend.AddFriendActivity;
import com.example.androidmessageapp.view.activities.BaseActivity;
import com.example.androidmessageapp.view.adapter.ConversationAdapter;
import com.example.androidmessageapp.view.adapter.MessageAdapter;

import java.util.List;


public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private String userId;
    ConversationAdapter conversationAdapter;
    ConversationRepository conversationRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));

        setContentView(binding.getRoot());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
        }
        conversationAdapter = new ConversationAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvConversation.setAdapter(conversationAdapter);
        binding.rvConversation.setLayoutManager(linearLayoutManager);
        conversationRepository = new ConversationRepository(this);


        binding.btnAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                if (bundle != null)
                    intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId != null && !userId.isEmpty()) {
            loadData();
        }
    }

    public void loadData(){
        List<ConversationModel> conversationsList = conversationRepository.getConversationsByUserId(userId);
        conversationAdapter.setConversationsList(conversationsList);
    }
}