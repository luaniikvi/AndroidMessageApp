package com.example.androidmessageapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.androidmessageapp.data.entities.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    public List<UserEntity> getAll();

    @Query("SELECT * FROM users WHERE id=:userId")
    public UserEntity getById(String userId);

    @Insert
    public void insert(UserEntity userEntity);

    @Query("SELECT * FROM users WHERE email=:email")
    public UserEntity getByEmail(String email);

    @Query("SELECT * FROM users WHERE email=:email AND password=:pass")
    public UserEntity getByEmailAndPass(String email, String pass);
}
