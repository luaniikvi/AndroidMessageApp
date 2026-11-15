package com.example.androidmessageapp.ui.conversations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidmessageapp.R;
import com.example.androidmessageapp.databinding.FragmentConversationsBinding;

public class ConversationsFragment extends Fragment {

    private FragmentConversationsBinding binding;
    private ConversationsViewModel viewModel;
    private ConversationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConversationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ConversationsViewModel.class);

        // Khởi tạo Adapter và xử lý sự kiện khi bấm vào 1 đoạn chat
        adapter = new ConversationAdapter(conversation -> {
            Bundle args = new Bundle();
            args.putString("conversationId", conversation.getId());
            Navigation.findNavController(view).navigate(R.id.action_conversations_to_chat, args);
        });
        binding.recyclerConversations.setAdapter(adapter);

        // Quan sát dữ liệu từ Room thông qua ViewModel (dùng getViewLifecycleOwner())
        viewModel.getConversations().observe(getViewLifecycleOwner(), conversations -> {
            if (conversations == null || conversations.isEmpty()) {
                // Nếu database chưa có gì thì tự động bơm dữ liệu mẫu 1 lần
                viewModel.seedSampleDataIfNeeded();
            } else {
                adapter.submitList(conversations);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng View Binding để tránh memory leak
    }
}