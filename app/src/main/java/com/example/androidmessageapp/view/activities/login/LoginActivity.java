package com.example.androidmessageapp.view.activities.login;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidmessageapp.databinding.LoginActivityBinding;
import com.example.androidmessageapp.view.activities.BaseActivity;

public class LoginActivity extends BaseActivity {
    LoginActivityBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(
                LayoutInflater.from(this)
        );
        setContentView(binding.getRoot());
    }
}
