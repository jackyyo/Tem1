package com.example.tem.iot.mycommunication;

import android.os.Handler;
import android.util.Log;

import com.example.tem.Constants;
import com.example.tem.iot.command.AsynchronousCommand;
import com.example.tem.iot.command.Paras;
import com.example.tem.iot.responseObject.Devices;
import com.example.tem.iot.util.JsonUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class huaweiIOT {

    //放大倍数/叠加次数/小电流(0)/发送频率控制字/放大倍数/采样率/大电流(1)/发送频率控制字/放大倍数/采样率/当前时间
    public static String configCommand="config/自动/1/0/1/1/1/1////\r\n";
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/json");
    private static final OkHttpClient client = new OkHttpClient();
    public static String TAG="huaweiIOT";

    private List<Devices.DevicesBean> devices;
    private Handler handler;
    public huaweiIOT(Handler handler){
        this.handler=handler;
    }
    public huaweiIOT(){
    }

    public List<Devices.DevicesBean> getDevices() {
        return devices;
    }

    public void setDevices(List<Devices.DevicesBean> devices) {
        this.devices = devices;
    }

    //下发指令
    public int sendCommand(String url,String postBody) throws IOException{
        String token= null;
        try {
            token = gettoken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .addHeader("X-Auth-Token", token)
                .post(RequestBody.create(postBody,MEDIA_TYPE_MARKDOWN))
                .build();

        Response response = null;
        response = client.newCall(request).execute();

        if (!response.isSuccessful()) try {
            throw new IOException("Unexpected code " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String body=null;
        int code=0;
        try {
            code=response.code();
            body=response.body().string();
            Log.e("code", ""+code );
            Log.e("body", ""+body );
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(response);
        //return response.header("X-Subject-Token");
        return code;
    }


    //获取设备列表
    public void getdevices() throws Exception{
        OkHttpPost okHttpPost=new OkHttpPost();
        //String token=okHttpPost.run();
        String token=gettoken();

        //Log.e(TAG, "String token:"+token);
        //String url = "https://iotda.cn-north-4.myhuaweicloud.com/v5/iot/5ecde13941f4fc02c747971c/devices?product_id=5ecde13941f4fc02c747971c";
        String url = "https://iotda.cn-north-4.myhuaweicloud.com/v5/iot/640fdc6192edbc7ee93a3cf0/devices?product_id=640fdc6192edbc7ee93a3cf0";
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .addHeader("X-Auth-Token", token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.e(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                String s=response.body().string();
                Log.e(TAG, "devices:"+s);
                Gson gson =new Gson();
                List<Devices.DevicesBean> devices= gson.fromJson(s,Devices.class).getDevices();
                setDevices(devices);
                handler.obtainMessage(Constants.IOT_DEVICES).sendToTarget();
                devices=getDevices();
                for(int i=0;i<devices.size();i++){
                    Log.e(TAG, "devices.id:"+devices.get(i).getDevice_id());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("失败啦");
                e.printStackTrace();
            }
        });
    }

    //获取token
    public static String gettoken() throws Exception {

        //String postbody="{"+"\""+"auth"+"\""+": {"+"\""+"identity"+"\""+": {"+"\""+"methods"+"\""+": ["+"\""+"password"+"\""+"],"+"\""+"password"+"\""+": {"+"\""+"user"+"\""+":{"+"\""+"domain\": {\"name\": \"********\"},\"name\": \"********\",\"password\": \"********\"}}},\"scope\": {\"project\": {\"name\": \"cn-north-4\"}}}}";

        //刘志同账号
        //String postbody="{"+"\""+"auth"+"\""+": {"+"\""+"identity"+"\""+": {"+"\""+"methods"+"\""+": ["+"\""+"password"+"\""+"],"+"\""+"password"+"\""+": {"+"\""+"user"+"\""+":{"+"\""+"domain\": {\"name\": \"hw_008615073149308_01\"},\"name\": \"hw_008615073149308_01\",\"password\": \"liuzhi0811\"}}},\"scope\": {\"project\": {\"name\": \"cn-north-4\"}}}}";

        //test账号
       String postbody="{"+"\""+"auth"+"\""+": {"+"\""+"identity"+"\""+": {"+"\""+"methods"+"\""+": ["+"\""+"password"+"\""+"],"+"\""+"password"+"\""+": {"+"\""+"user"+"\""+":{"+"\""+"domain\": {\"name\": \"hw95008139\"},\"name\": \"hw95008139\",\"password\": \"csu85321\"}}},\"scope\": {\"project\": {\"name\": \"cn-north-4\"}}}}";
        //String postbody="{"+"\""+"auth"+"\""+": {"+"\""+"identity"+"\""+": {"+"\""+"methods"+"\""+": ["+"\""+"password"+"\""+"],"+"\""+"password"+"\""+": {"+"\""+"user"+"\""+":{"+"\""+"domain\": {\"name\": \" hid_ox3hrvszio-whwr\"},\"name\": \"hid_ox3hrvszio-whwr\",\"password\": \"hsl18791664995\"}}},\"scope\": {\"project\": {\"name\": \"cn-north-4\"}}}}";


       /* String postbody="{"+"\""+"auth"+"\""+": {"+
                                          "\""+"identity"+"\""+": {"+
                                              "\""+"methods"+"\""+": ["+
                                                  "\""+"password"+"\""+
                                                  "],"+
                                                  "\""+"password"+"\""+": {"+
                                                      "\""+"user"+"\""+":{"+
                                                            "\""+"domain\": {\"name\": \"********\"},
                                                            \"name\": \"********\",
                                                            \"password\": \"********\"}
                                                        }
                                                   },
                                                   \"scope\": {
                                                       \"project\": {
                                                            \"name\": \"cn-north-4\"
                                                        }
                                                   }
                                             }
                                      }";

        */

        String strurl="https://iam.cn-north-4.myhuaweicloud.com"+"/v3/auth/tokens?nocatalog=false";

        URL url = new URL(strurl);
        HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
        urlCon.addRequestProperty("Content-Type", "application/json;charset=utf8");

        urlCon.setDoOutput(true);
        urlCon.setRequestMethod("POST");
        urlCon.setUseCaches(false);
        urlCon.setInstanceFollowRedirects(true);
        urlCon.connect();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlCon.getOutputStream(),"UTF-8"));
        writer.write(postbody);
        writer.flush();
        writer.close();
        Map headers = urlCon.getHeaderFields();
        Set<String> keys = headers.keySet();
        for( String key : keys ){
            String val = urlCon.getHeaderField(key);
            System.out.println(key+"    "+val);
            Log.e("Token",key+"    "+val);
        }
        String token = urlCon.getHeaderField("X-Subject-Token");
        Log.e(TAG, "获取Token为:"+token);
        return token;
    }


    public static String getshadow() throws Exception
    {
        String strurl="https://iotda.cn-north-4.myhuaweicloud.com"+"/v5/iot/%s/devices/%s/shadow";
        String project_id="6000079d4f04b00309b07052";
        String device_id="5eed76e4da222a02eac70a5f_1647508162747";
        strurl = String.format(strurl, project_id,device_id);
        String  token=gettoken();
        URL url = new URL(strurl);
        HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
        urlCon.addRequestProperty("Content-Type", "application/json");
        urlCon.addRequestProperty("X-Auth-Token",token);
        urlCon.connect();
        InputStreamReader is = new InputStreamReader(urlCon.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(is);
        StringBuffer strBuffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            strBuffer.append(line);
        }
        is.close();
        urlCon.disconnect();
        String result = strBuffer.toString();
        System.out.println(result);
        return null;
    }


    /*
    调用此函数返回这个{"service_id":null,"command_name":null,"paras":{"cofig":"start","order":null},"expire_time":null,"send_strategy":"immediately"}
     */
    /**
     *
    	 * @param s  指令
    	 * @param str   所属命令名
    	 * @return java.lang.String
     * @createtime 2023/3/14 19:56
    **/
    public String setConfig(String s,String str){
        AsynchronousCommand asynchronousCommand=new AsynchronousCommand();
        asynchronousCommand.setService_id("远程采控");
        asynchronousCommand.setSend_strategy("immediately");
        asynchronousCommand.setCommand_name(str);
        asynchronousCommand.setExpire_time(0);
        Paras paras =new Paras();
        paras.setState(s);
        Log.e("","paras"+paras);
        asynchronousCommand.setParas(paras);
        return JsonUtils.Obj2String(asynchronousCommand);
    }

}
