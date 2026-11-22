package com.example.androidmessageapp.data.repository;

import android.content.Context;

import com.example.androidmessageapp.data.AppDatabase;
import com.example.androidmessageapp.data.dao.UserDao;
import com.example.androidmessageapp.data.entities.UserEntity;
import com.example.androidmessageapp.data.mapper.UserMapper;
import com.example.androidmessageapp.models.UserModel;

import java.util.List;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository (Context context) {
        this.userDao = AppDatabase.getInstance(context).userDao();
    }

    public List<UserModel> getAll() {
        return UserMapper.toModel(userDao.getAll());
    }
    public UserModel getById(String userId) {
        return UserMapper.toModel(userDao.getById(userId));
    }
    public void insert(UserEntity userEntity) {
        userDao.insert(userEntity);
    }

    public UserModel getByEmail(String email) {
        return UserMapper.toModel(userDao.getByEmail(email));
    }

    public UserModel getByEmailAndPass(String email, String pass) {
        return UserMapper.toModel(userDao.getByEmailAndPass(email, pass));
    }
}
