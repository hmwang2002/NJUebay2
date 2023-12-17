package com.njuebay2.backend.domain.vo;

public enum MyErrorCode {
    NULL_USERNAME(900), NULL_PWD(901);

    private final int status;

    MyErrorCode(int num){
        status = num;
    }
};
