package com.example.androidmessageapp.ui.conversations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmessageapp.data.local.entity.ConversationEntity;

import com.example.androidmessageapp.databinding.ItemConversationBinding;
import com.example.androidmessageapp.utils.DateTimeUtils;

import java.util.Objects;

public class ConversationAdapter extends ListAdapter<ConversationEntity, ConversationAdapter.ViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(ConversationEntity conversation);
    }

    private final OnConversationClickListener listener;

    public ConversationAdapter(OnConversationClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ConversationEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ConversationEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull ConversationEntity oldItem, @NonNull ConversationEntity newItem) {
                    return Objects.equals(oldItem.getId(), newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ConversationEntity oldItem, @NonNull ConversationEntity newItem) {
                    return Objects.equals(oldItem.getTitle(), newItem.getTitle())
                            && Objects.equals(oldItem.getLastMessage(), newItem.getLastMessage())
                            && oldItem.getUpdatedAt() == newItem.getUpdatedAt()
                            && oldItem.getUnreadCount() == newItem.getUnreadCount();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConversationBinding binding = ItemConversationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemConversationBinding binding;

        ViewHolder(ItemConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ConversationEntity item, OnConversationClickListener listener) {
            binding.textTitle.setText(item.getTitle());
            binding.textLastMessage.setText(item.getLastMessage());
            binding.textTime.setText(DateTimeUtils.formatTime(item.getUpdatedAt()));

            if (item.getUnreadCount() > 0) {
                binding.textUnreadBadge.setVisibility(View.VISIBLE);
                binding.textUnreadBadge.setText(String.valueOf(item.getUnreadCount()));
            } else {
                binding.textUnreadBadge.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> listener.onConversationClick(item));
        }
    }
}