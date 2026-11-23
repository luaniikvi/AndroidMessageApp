package com.example.androidmessageapp.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidmessageapp.databinding.ConversationItemBinding;
import com.example.androidmessageapp.models.ConversationModel;
import com.example.androidmessageapp.view.activities.message.MessageActivity;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>{
    private final List<ConversationModel> conversations = new ArrayList<>();
    private String currentUserId;

    @SuppressLint("NotifyDataSetChanged")
    public void setConversationsList(List<ConversationModel> newList, String currentUserId) {
        this.conversations.clear();
        if (newList != null){
            conversations.addAll(newList);
        }
        notifyDataSetChanged();
        this.currentUserId = currentUserId;
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

        holder.binding.tvConversationName.setText(currConversation.getConversationName());
        holder.binding.tvLastMessage.setText(currConversation.getLastMessage());

        holder.binding.conversationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.binding.getRoot().getContext();
                Intent intent = new Intent(context, MessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userId", currentUserId);
                bundle.putString("conversationId",currConversation.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
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
