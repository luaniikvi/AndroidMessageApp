package com.example.androidmessageapp.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmessageapp.databinding.MessageItemBinding;
import com.example.androidmessageapp.models.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<MessageModel> msgList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setMsgList(List<MessageModel> newMsgList) {
        this.msgList.clear();
        if (newMsgList != null) {
            this.msgList.addAll(newMsgList);
        }
        notifyDataSetChanged();
    }

    public void addMsg(MessageModel newMsg) {
        if(newMsg == null) return;
        this.msgList.add(newMsg);
        notifyItemInserted(msgList.size()-1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageItemBinding binding = MessageItemBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );

        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel currentMsg = msgList.get(position);
        holder.binding.tvMsgContent.setText(currentMsg.getContent());
        holder.binding.tvMsgTime.setText(currentMsg.getFormatTime());
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageItemBinding binding;

        public MessageViewHolder(@NonNull MessageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
