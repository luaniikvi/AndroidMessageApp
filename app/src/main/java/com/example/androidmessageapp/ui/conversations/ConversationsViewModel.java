package com.example.androidmessageapp.ui.conversations;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidmessageapp.data.local.entity.ConversationEntity;
import com.example.androidmessageapp.data.repository.ChatRepository;

import java.util.List;

public class ConversationsViewModel extends AndroidViewModel {

    private final ChatRepository repository;
    private final LiveData<List<ConversationEntity>> conversations;

    public ConversationsViewModel(@NonNull Application application) {
        super(application);
        repository = new ChatRepository(application);
        conversations = repository.getAllConversations();
    }

    public LiveData<List<ConversationEntity>> getConversations() {
        return conversations;
    }

    public void seedSampleDataIfNeeded() {
        repository.seedSampleData();
    }
}