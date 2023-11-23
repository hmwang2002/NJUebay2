package com.ebaynju.ebay_backend.util;

import java.util.Random;

public class CodeUtil {
    public static String generateCode(int num) {
        Random codeNum = new Random();
        String chars = "1234567890abcdefghijklmnopqrstuvwxwzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < num; i ++) {
            code.append(chars.charAt(codeNum.nextInt(58)));
        }

        return code.toString();
    }
}
