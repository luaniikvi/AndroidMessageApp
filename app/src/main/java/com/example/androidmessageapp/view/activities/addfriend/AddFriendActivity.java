package com.example.androidmessageapp.view.activities.addfriend;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.example.androidmessageapp.databinding.AddfriendAcitivityBinding;
import com.example.androidmessageapp.view.activities.BaseActivity;

public class AddFriendActivity extends BaseActivity {
    AddfriendAcitivityBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddfriendAcitivityBinding.inflate(LayoutInflater.from(this));

        setContentView(binding.getRoot());
    }
}
