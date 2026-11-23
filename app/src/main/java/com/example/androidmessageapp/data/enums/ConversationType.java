package com.example.androidmessageapp.data.enums;

import lombok.Getter;

@Getter
public enum ConversationType {
    DIRECT("DIRECT"),
    GROUP("GROUP");

    private final String value;

    ConversationType(String value) {
        this.value = value;
    }

}
