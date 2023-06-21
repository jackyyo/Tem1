package com.example.tem;

import android.os.Environment;

public class Constants {
    //蓝牙发送指令
    public static final String CMD_STOP = "stop\r\n";
    public static final String CMD_START0 = "start0\r\n";//采集小电流
    public static final String CMD_START1 = "start1\r\n";//采集大电流
    public static final String CMD_START2 = "start2\r\n";//预采集小电流
    public static final String CMD_START3 = "start3\r\n";//预采集大电流

    public static final String CMD_DELETE = "delete\r\n";
    public static final String CMD_CALIBRATION = "calibration\r\n";
    public static final String CMD_RESET = "reset\r\n";

    public static final int fre=1;

    //消息
    public static final int DEVICE_CONNECTING = 11;//有设备正在连接热点
    public static final int DEVICE_CONNECTED = 12;//有设备连上热点
    public static final int SEND_MSG_SUCCSEE = 13;//发送消息成功
    public static final int SEND_MSG_ERROR = 14;//发送消息失败
    public static final int FINISH_THREAD=15;//采集完成
    public static final int ChartTwoCur0=16;//小电流采集完成
    public static final int ChartTwoCur1=17;//大电流采集完成
    public static final int ChartOne=18;//多测道图更新
    public static final int ChartTwoCur0test=19;//测试
    public static final int Calibration = 20;//校准完成
    public static final int ChartTwoTest=21;//测试
    public static final int ChartTwoTest4=27;//测试

    public static final int ParpareFinish = 22;//校准完成

    public static final int ChartTwoTest1 = 23;//校准完成
    public static final int ChartTwoTest2 = 26;//校准完成

    public static final int ChartOne1=24;//多测道图更新

    public static final int ChartALLRongHe=28;//小电流采集完成


    //test
    public static final int DEVICE_CONNECTING1 = 221;//有设备正在连接热点
    public static final int DEVICE_CONNECTED1  = 222;//有设备连上热点
    public static final int SEND_MSG_SUCCSEE1 = 223;//发送消息成功
    public static final int SEND_MSG_ERROR1    = 224;//发送消息失败
    public static final int GET_MSG1           = 226;//获取新消息

    //wifi
    public static final int PORT = 4321;

    public static final int GET_FILE_NAME=23;
    public static final int GET_FILE_SIZE=31;//数据文件大小
    public static final int SET_LOG_MESSAGE=32;
    public static final int STOP_ERROR=42;
    public static final int Transmission_Missing=52;//数据传输出错

    //intent
    public static final int PARAM_ACTIVITY_CODE=110;
    public static final int RECEIVE_1_ACTIVITY_CODE=111;
    public static final int RECEIVE_2_ACTIVITY_CODE=112;
    public static final int IOT_DEVICES_ACTIVITY_CODE=113;

    //云平台
    public static final int IOT_DEVICES=150;
    public static final int IOT_INTERNET=151;
    public static final int IOT_START=152;
    public static final int IOT_STOP=153;
    public static final int IOT_MESSAGE=154;
    public static final int IOT_GET_R_DATA=155;


    //其他
    public static final String DEVICE_ONLINE="ONLINE";
    public static final String MY_BROAD="com.example.TEM.iot.IOT_RECEIVER";
    public static final String DATA_DIRECTORY = Environment.getExternalStorageDirectory().getPath()+"/TEM";
}
