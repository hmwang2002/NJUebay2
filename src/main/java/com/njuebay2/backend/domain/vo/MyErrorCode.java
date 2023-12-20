package com.njuebay2.backend.domain.vo;

public enum MyErrorCode {
    GOOD_NULL(900), STATUS_NOT_DEALING(901);

    public final int status;

    MyErrorCode(int num){
        status = num;
    }


};
