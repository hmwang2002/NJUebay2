package com.ebaynju.ebay_backend.util;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cardigan
 * @version 1.0
 * Create by 2022/11/26
 */
public class JsonUtil {
    // code 0 正常 1 不正常

    /**
     * 生成只含 code 的JSON字符串
     * note: 0代表正常 1代表异常
     * @param code 表示状态
     * @return
     * @throws JSONException
     */
    public static String getJSONString(int code) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toString();
    }

    /**
     * 生成带有多字段的JSON字符串
     * @param code 表示状态
     * @param map
     * @return
     * @throws JSONException
     */
    public static String getJSONString(int code, Map<String ,Object> map) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String ,Object> entry:map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toString();
    }

    /**
     * 生成含 code 和信息的JSON字符串
     * @param code 表示状态
     * @param msg
     * @return
     * @throws JSONException
     */
    public static String getJSONString(int code, String msg) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        return json.toString();
    }

    public static String getJSONString(int code, List<Map<String ,Object>> mapSets) throws JSONException {
        int id = 0;
        JSONObject jsonAns = new JSONObject();
        //JSONObject jsonData = new JSONObject();
        List<JSONObject> jsonData = new ArrayList<>();
        for (Map<String, Object> map:mapSets) {

            JSONObject json = new JSONObject();
            json.put("id", id);
            for (Map.Entry<String ,Object> entry:map.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }

            jsonData.add(json);
        }
        jsonAns.put("code", code);
        jsonAns.put("data", jsonData);
        return jsonAns.toString();
    }
}
