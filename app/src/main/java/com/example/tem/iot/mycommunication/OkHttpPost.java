package com.example.tem.iot.mycommunication;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpPost {
    public static final MediaType mediaType = MediaType.parse("application/json");//用于描述请求/响应body的内容类型
    private final OkHttpClient client = new OkHttpClient();

    public String PostDatasync(){
        Log.e("kwwl","getDatasync()");


        String postBody = setConfig();
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
        Request request = new Request.Builder()
                .url("https://iam.cn-north-4.myhuaweicloud.com/v3/auth/tokens")
                .header("Content-Type", "application/json")//请求消息头——消息体的类型（格式），必选，默认取值为“application/json”
                .post(RequestBody.create(postBody,mediaType))
                .build();
        Response response = null;
        try{
            response = client.newCall(request).execute();//得到Response 对象
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.isSuccessful()) {
            Log.e("kwwl","response.code()=="+response.code());
        }else{
            Log.e("kwwl","Unexpected code =="+response.code());
        }
        Log.e("token", "run: "+response.header("X-Subject-Token") );
        return response.header("X-Subject-Token");
    }

    public String setConfig(){

        return "";
    }
}
