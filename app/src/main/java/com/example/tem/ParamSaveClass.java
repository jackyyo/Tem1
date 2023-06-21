package com.example.tem;

import android.graphics.Color;

import com.example.tem.iot.responseObject.Devices;

public class ParamSaveClass {

    //
    public static int num1=0;
    public static int num2=0;

    //云平台设备
    public static Devices.DevicesBean iotDevice=null;


    //配置
    public static String configCommand="config/250/1/1/1/1/1/1/FrequencyEle250HzData/FrequencyEle-Data\r\n";



    public static int WeakSignal = 25;//小电流弱信号放大倍数
    public static int WeakSignal1 = 25;//大电流弱信号放大倍数
    public static int a=1;
    public static int b=1;

    public static int StrongSignal = 13;//强信号已经衰弱了13倍

    //接收
    public static int Overlay = 1;//叠加次数
    public static int Effective = 2;//有效次数
    public static float time= (float) ((float)1/2.5);//一次采样间隔（2Msps=1/2500000）微秒(us)
    public static double Sample=2.5;
    public static int sampleIndex=0;
    //字符串
    public static String Location = "中南大学";  //测区信息

    //发射
    public static String Amplification = "1";//接收小电流放大倍数
    public static String Amplification1 = "1";//接收大电流放大倍数

    public static int Current = 1;//小电流
    public static double CurrentSendFre = 25;//小电流发送频率
    public static int CurrentOffTime=50;//小电流关断时间
    public static int CurrentUpTime=50;//小电流上升时间
    public static double CurrentMagnification=10;//小电流放大倍数
    public static String CurrentSampling="1.8Mksps";//小电流采样率
    public static int Current1 = 15;//大电流
    public static double CurrentSendFre1 = 25;//大电流发送频率
    public static int CurrentOffTime1=50;//大电流关断时间
    public static int CurrentUpTime1=50;//大电流上升时间
    public static double CurrentMagnification1=80;//大电流放大倍数
    public static String CurrentSampling1="1.8Mksps";//大电流采样率
    public static int PointNum=1;//点号
    public static int LineNum=1;//线号
    public static int DotPit=20;//点距
    public static int LinePit=40;//线距
    public static int PointIncrement=1;//点好增量
    public static int LineIncrement=1;//线号增量

    public static int SPS=1;//采样频率

    //大小电流接收机采集幅值
    public static double currentI = 1;
    public static double currentI2 = 15;


    public static String voltage_group = "";
    public static String current_group = "";

    public static final int [] ColorArr={-16777216,-16776961,-16711681,-12303292,-7829368,-16711936,-3355444,-65281,-65536,0,-1,-256};


}
