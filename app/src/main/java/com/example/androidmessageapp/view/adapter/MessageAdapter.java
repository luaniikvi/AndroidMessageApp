package com.example.androidmessageapp.view.adapter;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmessageapp.databinding.MessageItemBinding;
import com.example.androidmessageapp.models.MessageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<MessageModel> msgList = new ArrayList<>();
    private String currenUserId;

    @SuppressLint("NotifyDataSetChanged")
    public void setMsgList(List<MessageModel> newMsgList, String currenUserId) {
        this.msgList.clear();
        if (newMsgList != null) {
            this.msgList.addAll(newMsgList);
        }
        notifyDataSetChanged();
        this.currenUserId = currenUserId;
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
        if (Objects.equals(currentMsg.getSenderId(), currenUserId)){
            holder.binding.msgItem.setGravity(Gravity.END);
        }
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
