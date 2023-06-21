package com.example.tem.iot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

//Json转换String

public class JsonUtils {

    private static ObjectMapper objectMapper = null;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String Obj2String(Object object) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);//将传入的对象序列化为json，返回给调用者
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static <T> T jsonStr2Object(String content, Class<T> tClass) {
        T obj = null;
        try {
            obj = objectMapper.readValue(content, tClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
