package com.example.androidmessageapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(
        tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class UserEntity {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String name;

    @Ignore
    public UserEntity(@NonNull String email, @NonNull String password, @NonNull String name) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.name = name.trim();
    }
}
