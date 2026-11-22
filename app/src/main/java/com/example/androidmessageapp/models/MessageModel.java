package com.example.androidmessageapp.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageModel {
    private String id;
    private String senderId;
    private String content;
    private String formatTime;
}
