package com.example.androidmessageapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(
        tableName = "users"
)

public class UserEntitiy {
    @PrimaryKey
    @NonNull
    private String id;

    private String password;
    private String name;

    @Ignore
    public UserEntitiy (String password, String name) {
        this.password = password;
        this.name = name.trim();
    }
}
