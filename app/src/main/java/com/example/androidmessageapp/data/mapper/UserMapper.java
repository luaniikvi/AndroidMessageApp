package com.example.androidmessageapp.data.mapper;

import com.example.androidmessageapp.data.entities.UserEntity;
import com.example.androidmessageapp.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public final class UserMapper {
    private UserMapper() {}

    public static UserModel toModel(UserEntity userEntity) {
        if(userEntity == null) return null;
        UserModel userModel = new UserModel();

        userModel.setEmail(userEntity.getEmail());
        userModel.setName(userEntity.getName());
        return userModel;
    }

    public static List<UserModel> toModel(List<UserEntity> userEntities){
        List<UserModel> userModels = new ArrayList<>();
        if (userEntities != null) {
            for (UserEntity userEntity : userEntities)
                userModels.add(toModel(userEntity));
        }
        return  userModels;
    }
}
