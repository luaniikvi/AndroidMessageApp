package com.example.androidmessageapp.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmessageapp.data.repository.MessageRepository;
import com.example.androidmessageapp.databinding.ActivityMainBinding;
import com.example.androidmessageapp.databinding.ConversationItemBinding;
import com.example.androidmessageapp.models.ConversationModel;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>{
    private final List<ConversationModel> conversations = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setConversationsList(List<ConversationModel> newList) {
        this.conversations.clear();
        if (newList != null){
            conversations.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConversationItemBinding binding = ConversationItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ConversationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        ConversationModel currConversation = conversations.get(position);
        MessageRepository messageRepository = new MessageRepository(holder.binding.getRoot().getContext());
/*        String lastMsg = messageRepository.getLastMessageByConversationId(currConversation.get);
        holder.binding.tvConversationName.setText(currConversation.getConversationName());
        holder.binding.tvLastMessage.setText(currConversation);*/
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder{
        private final ConversationItemBinding binding;
        public ConversationViewHolder(@NonNull ConversationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
