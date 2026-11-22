package com.example.androidmessageapp.view.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.androidmessageapp.data.entities.UserEntity;
import com.example.androidmessageapp.data.repository.UserRepository;
import com.example.androidmessageapp.databinding.LoginActivityBinding;
import com.example.androidmessageapp.models.UserModel;

import java.util.Objects;


public class MainActivity extends BaseActivity {
    LoginActivityBinding binding;
    UserRepository userRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LoginActivityBinding.inflate(
                LayoutInflater.from(this)
        );

        setContentView(binding.getRoot());

        userRepository = new UserRepository(this);
        bindKey();
    }
    public void bindKey() {

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String pass = Objects.requireNonNull(binding.etPassword.getText()).toString();
            if(email.isEmpty() || pass.isEmpty()) return;

            UserModel user = userRepository.getByEmailAndPass(email, pass);
            if (user != null) {
                Toast.makeText(
                        MainActivity.this,
                        "Login successfully",
                        Toast.LENGTH_SHORT
                ).show();
            }
            else {
                Toast.makeText(
                        MainActivity.this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        binding.btnSignup.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String pass = Objects.requireNonNull(binding.etPassword.getText()).toString();
            if(email.isEmpty() || pass.isEmpty()) return;

            // checking exist
            UserModel user = userRepository.getByEmail(email);
            if(user != null) {
                Toast.makeText(MainActivity.this, "Account has been existed", Toast.LENGTH_SHORT).show();
            }

            else {
                userRepository.insert(new UserEntity(email, pass, email));
                Toast.makeText(MainActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}