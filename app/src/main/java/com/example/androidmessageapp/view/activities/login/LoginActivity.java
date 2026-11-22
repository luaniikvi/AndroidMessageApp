package com.example.androidmessageapp.view.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.androidmessageapp.data.entities.UserEntity;
import com.example.androidmessageapp.data.repository.UserRepository;
import com.example.androidmessageapp.databinding.LoginActivityBinding;
import com.example.androidmessageapp.models.UserModel;
import com.example.androidmessageapp.view.activities.BaseActivity;
import com.example.androidmessageapp.view.activities.main.MainActivity;

import java.util.Objects;

public class LoginActivity extends BaseActivity {
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
                        LoginActivity.this,
                        "Login successfully",
                        Toast.LENGTH_SHORT
                ).show();
                // TODO: send to main act
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userId", user.getId());
                bundle.putString("email", email);
                bundle.putString("name", user.getName());

                intent.putExtras(bundle);
                startActivity(intent);
            }
            else {
                Toast.makeText(
                        LoginActivity.this,
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
                Toast.makeText(LoginActivity.this, "Account has been existed", Toast.LENGTH_SHORT).show();
            }

            else {
                userRepository.insert(new UserEntity(email, pass, email));
                Toast.makeText(LoginActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
