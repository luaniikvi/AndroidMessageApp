package com.example.androidmessageapp.view.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.androidmessageapp.databinding.ActivityMainBinding;
import com.example.androidmessageapp.view.activities.addfriend.AddFriendActivity;
import com.example.androidmessageapp.view.activities.BaseActivity;



public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));

        setContentView(binding.getRoot());

        binding.btnAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);

                startActivity(intent);
            }
        });
    }
}