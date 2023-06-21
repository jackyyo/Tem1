package com.example.tem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tem.FFTtest.FFTtestActivity;
import com.example.tem.Measurement.DBHelper;
import com.example.tem.Measurement.TemData;
import com.example.tem.WifiTest.ConnectThread;
import com.example.tem.WifiTest.ListenerThread;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.tem.ChartDataAnalysis.CurNum0;
import static com.example.tem.ChartDataAnalysis.CurNum1;
import static com.example.tem.ChartDataAnalysis.lists2;
import static com.example.tem.Constants.PORT;
import static com.example.tem.ParamSaveClass.ColorArr;
import static com.example.tem.ParamSaveClass.Location;
import static com.example.tem.ParamSaveClass.StrongSignal;
import static java.lang.Math.floor;
import static java.lang.Math.pow;


public class ControllerFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;
    private TextView logTextView;
    private TextView fileSize;
    private Button butResistivity;
    private Button butClearLog;
    private Button butCarlibration;
    private Button butSelfCheck;
    private Button butAquisition;
    private Button preDot;
    private Button nextDot;
    private Button chartChange;
    private WifiClientThread wifiClientThread;

    @BindView(R.id.point_show1)
    TextView PointShow;
    @BindView(R.id.line_show1)
    TextView LineShow;
    @BindView(R.id.point_distence_show1)
    TextView PointDistenceShow;
    @BindView(R.id.line_distence_show1)
    TextView LineDistenceShow;
    @BindView(R.id.Current_show1)
    TextView CurrentShow;
    @BindView(R.id.Voltage_show1)
    TextView VoltageShow;
    @BindView(R.id.time_show1)
    TextView TimeShow;
    @BindView(R.id.fre_show1)
    TextView TimeShow1;
    Unbinder unbinder_c;

    private LineChart chartone;
    private LineChart charttwo;
    private List<Entry> entrie_one = new ArrayList<>();
    private List<Entry> entrie_two = new ArrayList<>();

    private ArrayList<Integer> line = new ArrayList<>();

    private ArrayList<String> connectIP;

    public String logContent="";
    private static final String TAG = "ControllerFragment";

    ArrayList<ArrayList<Float>> list=new ArrayList<>();

    private WifiManager wifiManager;
    private String ip="";
    public static int Rssi=-50;//WiFi信号强度(得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线)

    public static ArrayList<Float> ChouDaoX0=new ArrayList<>();
    public static ArrayList<Float> ChouDaoY0=new ArrayList<>();
    public static ArrayList<Float> ChouDaoX1=new ArrayList<>();
    public static ArrayList<Float> ChouDaoY1=new ArrayList<>();
    public static ArrayList<Float> RongHe0=new ArrayList<>();
//小电流采集16384点，延迟80点
public static ArrayList<Float> ChouDaoX2=new ArrayList<>();
    public static ArrayList<Float> ChouDaoY2=new ArrayList<>();
    public static ArrayList<Float> ChouDaoX3=new ArrayList<>();
    public static ArrayList<Float> ChouDaoY3=new ArrayList<>();
    public static ArrayList<Float> RongHe1=new ArrayList<>();
    public static double [] ChouDaoX={2.5, 7.5, 12.5, 17.5, 25.0, 35.0, 45.0, 55.0, 70.0, 90.0, 110.0, 130.0, 160.0, 200.0, 240.0, 280.0, 340.0, 420.0, 500.0, 580.0, 700.0, 860.0, 1020.0, 1180.0, 1420.0, 1740.0, 2060.0, 2380.0, 2840.0, 3480.0, 4120.0, 4760.0};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ChartOne:
                    chartone.clear();
                    ChartPlay.init(chartone,5,4,true,"","","V/I（V/A）/点号(号)");//初始化图表(1V=10^6µV)
                    ChartPlay.showLineChart(chartone,ChartDataAnalysis.lists1.get(0),false,true,"5µs", ColorArr[0],1,1);
                    ChartPlay.addLine(chartone,ChartDataAnalysis.lists1.get(1),false,true,"22µs", ColorArr[1],1,1);
                    ChartPlay.addLine(chartone,ChartDataAnalysis.lists1.get(2),false,true,"64µs", ColorArr[2],1,1);
                    ChartPlay.addLine(chartone,ChartDataAnalysis.lists1.get(3),false,true,"168µs", ColorArr[3],1,1);
                    ChartPlay.addLine(chartone,ChartDataAnalysis.lists1.get(4),false,true,"408µs", ColorArr[4],1,1);
                    ChartPlay.addLine(chartone,ChartDataAnalysis.lists1.get(5),false,true,"952µs", ColorArr[5],1,1);
                    chartone.invalidate();
                    break;
                case Constants.ChartTwoCur0:
                    charttwo.clear();
                    ChartPlay.init(chartone,5,4,false,"","","V/I（V/A）/时间（µs）");//初始化图表
                    if(ChartDataAnalysis.Channel.get(0).size()!=0)ChartPlay.show(chartone,ChouDaoX,ChartDataAnalysis.Channel.get(0),"小电流强信号",  Color.CYAN,ParamSaveClass.time,false);//ParamSaveClass.time
                    if(ChartDataAnalysis.Channel.get(1).size()!=0)ChartPlay.add(chartone,ChouDaoX,ChartDataAnalysis.Channel.get(1),"小电流弱信号",Color.MAGENTA,ParamSaveClass.time,false);
                    if(TimeShow!=null)TimeShow.setText(""+ParamSaveClass.CurrentOffTime);
                    charttwo.invalidate();
                    WifiClientThread.chartFlag=false;
                    break;
                case Constants.ChartTwoCur1:
                    charttwo.clear();
                    ChartPlay.init(chartone,5,4,false,"","","V/I（V/A）/时间（µs）");//初始化图表
                    if(ChartDataAnalysis.Channel.get(0).size()!=0)ChartPlay.show(chartone,ChouDaoX,ChartDataAnalysis.Channel.get(0),"小电流强信号",  Color.CYAN,ParamSaveClass.time,false);//ParamSaveClass.time
                    if(ChartDataAnalysis.Channel.get(1).size()!=0)ChartPlay.add(chartone,ChouDaoX,ChartDataAnalysis.Channel.get(1),"小电流弱信号",Color.MAGENTA,ParamSaveClass.time,false);
                    if(ChartDataAnalysis.Channel.get(2).size()!=0)ChartPlay.add(chartone,ChouDaoX,ChartDataAnalysis.Channel.get(2),"大电流强信号", Color.BLUE,ParamSaveClass.time,false);//ParamSaveClass.time
                    if(ChartDataAnalysis.Channel.get(3).size()!=0)ChartPlay.add(chartone,ChouDaoX,ChartDataAnalysis.Channel.get(3),"大电流弱信号", Color.RED,ParamSaveClass.time,false);
                    charttwo.invalidate();
                    break;
                case Constants.FINISH_THREAD:
                    mainActivity.logAppend("->"+"采集结束！！！"+"\n");
                    butAquisition.setText(R.string.button_start_aquisition);
                    mainActivity.controllerLayout.setEnabled(true);
                    mainActivity.settingLayout.setEnabled(true);
                    mainActivity.logLayout.setEnabled(true);
                    mainActivity.measurementLayout.setEnabled(true);
                    butCarlibration.setEnabled(true);
                    butSelfCheck.setEnabled(true);
                    chartone.setTouchEnabled(true);
                    charttwo.setTouchEnabled(true);
                    break;
                case Constants.GET_FILE_SIZE:
                    String s=(String)msg.obj;
                    fileSize.setText(s);
                    break;
                case Constants.SET_LOG_MESSAGE:
                    mainActivity.logAppend("->"+msg.obj);
                    break;
                case Constants.Transmission_Missing:
                    mainActivity.logAppend("->"+msg.obj);
                    butAquisition.setText(R.string.button_start_aquisition);
                    butCarlibration.setEnabled(true);
                    butSelfCheck.setEnabled(true);
                    chartone.setTouchEnabled(true);
                    charttwo.setTouchEnabled(true);
                    break;
                case Constants.Calibration:
                    mainActivity.logAppend("->校准完成\r\n");
                    butCarlibration.setEnabled(true);
                    butAquisition.setEnabled(true);
                    butSelfCheck.setEnabled(true);
                    chartone.setTouchEnabled(true);
                    charttwo.setTouchEnabled(true);
                    break;
                case Constants.ParpareFinish:
                    butSelfCheck.setText("预采集");
                    mainActivity.logAppend("->预采集完成\r\n");
                    butCarlibration.setEnabled(true);
                    butAquisition.setEnabled(true);
                    butSelfCheck.setEnabled(true);
                    chartone.setTouchEnabled(true);
                    charttwo.setTouchEnabled(true);
                    break;
                case Constants.ChartTwoTest:
                    charttwo.clear();
                    ChartPlay.init(chartone,5,4,false,"","","V/I（V/A）/时间（µs）");//初始化图表
                    if(list.get(0).size()!=0)ChartPlay.show(chartone,list.get(0),"小电流强信号",  Color.CYAN,ParamSaveClass.time,1,false);//ParamSaveClass.time
                    if(list.get(1).size()!=0)ChartPlay.add(chartone,list.get(1),"小电流弱信号",Color.MAGENTA,ParamSaveClass.time,1,false);
                    TimeShow.setText(""+ParamSaveClass.CurrentOffTime);
                    charttwo.invalidate();
                    break;
                case Constants.ChartTwoTest4:
                    charttwo.clear();
                    ChartPlay.init(chartone,5,4,false,"","","V/I（V/A）/时间（µs）");//初始化图表
                    if(list.get(0).size()!=0)ChartPlay.show(chartone,list.get(0),"大电流强信号", Color.BLUE,ParamSaveClass.time,1,false);//ParamSaveClass.time
                    if(list.get(1).size()!=0)ChartPlay.add(chartone,list.get(1),"大电流弱信号", Color.RED,ParamSaveClass.time,1,false);
                    TimeShow.setText(""+ParamSaveClass.CurrentOffTime);
                    charttwo.invalidate();
                    break;
                case Constants.ChartTwoTest1:
                    charttwo.clear();
                    ChartPlay.init(charttwo,5,4,false,"","","V/I（V/A）/时间（µs）");//初始化图表
                    ChartPlay.show(charttwo,ChouDaoX0,ChouDaoY0,9,"小电流强信号",Color.CYAN,ParamSaveClass.time,1);//ParamSaveClass.time
                    ChartPlay.add(charttwo,ChouDaoX1,ChouDaoY1,9,"小电流弱信号", Color.MAGENTA,ParamSaveClass.time,1);
                    ChartPlay.add(charttwo,ChouDaoX1,RongHe0,9,"融合信号", Color.GREEN,ParamSaveClass.time,1);
                    break;
                case Constants.ChartTwoTest2:
                    charttwo.clear();
                    ChartPlay.init1(charttwo,4,8,"","","V/I（V/A）/时间（µs）");//初始化图表
                    //if(list.get(0).size()!=0)ChartPlay.show1(charttwo,"小电流强信号", Color.CYAN,ParamSaveClass.time,1,false);//ParamSaveClass.time
                    //if(list.get(1).size()!=0)ChartPlay.add1(charttwo,"小电流弱信号", Color.MAGENTA,ParamSaveClass.time,1,false);
                    ChartPlay.show(charttwo,ChouDaoX2,ChouDaoY2,88,"大电流强信号", Color.BLUE,ParamSaveClass.time,1);//ParamSaveClass.time
                    ChartPlay.add(charttwo,ChouDaoX3,ChouDaoY3,88,"大电流弱信号", Color.RED,ParamSaveClass.time,1);
                    ChartPlay.add(charttwo,ChouDaoX2,RongHe1,88,"融合信号", Color.GREEN,ParamSaveClass.time,1);
                    break;
                case Constants.ChartTwoCur0test:
                    charttwo.clear();
                    Log.e(TAG,"ChartDataAnalysis.lists20:"+ChartDataAnalysis.lists2);
                    ChartPlay.initChartView(charttwo,5,4,false,false,false,"","","V/I（V/A）/时间（µs）");//初始化图表
                    if(ChartDataAnalysis.lists2.get(0).size()!=0)ChartPlay.showLineChart(charttwo,ChartDataAnalysis.lists2.get(0),false,false,"小电流强信号", Color.CYAN,1,1,false);
                    //if(ChartDataAnalysis.lists2.get(1).size()!=0)ChartPlay.addLine(charttwo,ChartDataAnalysis.lists2.get(1),false,false,"小电流弱信号", Color.LTGRAY,1,CurNum0,false);
                    //TimeShow.setText(""+ParamSaveClass.CurrentOffTime);
                    charttwo.invalidate();
                    break;
                case Constants.ChartALLRongHe:
                    ChartPlay.init(charttwo,5,4,false,"","","V/I（V/A）/时间（µs）");//初始化图表
                    //ChartPlay.show(charttwo,ChouDaoX0,ChouDaoY0,9,"小电流强信号",Color.CYAN,ParamSaveClass.time,1);//ParamSaveClass.time
                    //ChartPlay.add(charttwo,ChouDaoX1,ChouDaoY1,9,"小电流弱信号", Color.MAGENTA,ParamSaveClass.time,1);
                    ChartPlay.show(charttwo,ChouDaoX1,RongHe0,9,"小电流融合信号", Color.DKGRAY,ParamSaveClass.time,1);
                    //ChartPlay.add(charttwo,ChouDaoX2,ChouDaoY2,88,"大电流强信号", Color.BLUE,ParamSaveClass.time,1);//ParamSaveClass.time
                    //ChartPlay.add(charttwo,ChouDaoX3,ChouDaoY3,88,"大电流弱信号", Color.RED,ParamSaveClass.time,1);
                    ChartPlay.add(charttwo,ChouDaoX2,RongHe1,88,"大电流融合信号", Color.YELLOW,ParamSaveClass.time,1);
                    break;
                case Constants.ChartOne1:
                    chartone.clear();
                    break;

            }
        }
    };

   /* public ControllerFragment() {
        // Required empty public constructor
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View controllerfLayout=inflater.inflate(R.layout.fragment_controller, container, false);
        unbinder_c = ButterKnife.bind(this, controllerfLayout);
        return controllerfLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder_c.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.fragmentState=0;
        mainActivity = (MainActivity) getActivity();
        Log.e(TAG, "开始onActivityCreated");
        wifiManager = (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initView();
        initChart();
        initText();
        //connectIP=getConnectedIP();
        setHotsspotReceiver();//wifi广播

        /*String s="TEM002:USART SENT:AT+NNMI=1\r\n" +
                "TEM002:@@Rev:\n"+
                "TEM002:云平台下发命令：\n"+
                "TEM002:+NNMI：9,020032737461727430\n"+
                "TEM002:start code:\n"+
                "TEM002:开始解析命令：start0\n"+
                "TEM002:命令解析成功!\n";
        mainActivity.logAppend(s);*/

        //wifi测试版本
        //WifiSend();
    }

    public void WifiSend(){
        /**
         * 先开启监听线程，在开启连接
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        ServerSocket server = new ServerSocket(PORT);
                        while(true){
                            ArrayList<ArrayList<Float>> res = new ArrayList<>();
                            res.add(ChartDataAnalysis.getDesc(1f,0.1f,10000));
                            res.add(ChartDataAnalysis.getDesc(2f,0.01f,10000));
                            res.add(ChartDataAnalysis.getDesc(3f,0.001f,10000));
                            res.add(ChartDataAnalysis.getDesc(4f,0.0001f,10000));
                            float max = ChartDataAnalysis.getMax(res);
                            Log.e(TAG,"max="+max);
                            Socket socket = server.accept();
                            InputStream inputStream = socket.getInputStream();
                            byte[] bytes = new byte[1024];
                            int len;
                            StringBuilder sb = new StringBuilder();
                            //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
                            while ((len = inputStream.read(bytes)) != -1) {
                                // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                                sb.append(new String(bytes, 0, len, "UTF-8"));
                            }
                            OutputStream outputStream = socket.getOutputStream();
                            ArrayList<Float> list = new ArrayList<>();
                            if(sb.toString().equals("start0\r\n")){
                                //Log.e("wifitest","outputStream.write(0)");
                                //list=ChartDataAnalysis.getDesc(4f,0.001f,400);
                                list=ChartDataAnalysis.getDesc(1f,0.01f,10000);
                                Log.e("chartDataAnalysis","0"+ChartDataAnalysis.getDesc(1f,0.01f,10000));

                                Log.e("WifiClientThread","CurrentData0:list:"+list);
                                for (int i = 1; i < list.size(); i++) {
                                    //int data = (int)((float)list.get(i)/1000*(Math.pow(2, 23)));//手动乘max
                                    int data = (int)((float)i*0.01/1000*(Math.pow(2, 23)));//手动乘max
                                    Log.e(TAG,"data:"+data);
                                    //int data=(int)((float)list.get(i));
                                    byte [] b;//小电流

                                    if(i>=100){//小强
                                        b=IntToBytes2(47,data);
                                    }else{
                                        b=IntToBytes2(32,data);
                                    }

                                    outputStream.write(b);
                                }
                                list=ChartDataAnalysis.getDesc(2f,0.001f,9900);
                                Log.e("chartDataAnalysis","1"+ChartDataAnalysis.getDesc(2f,0.001f,9900));

                                for (int i = 1; i < list.size(); i++) {
                                    //int data = (int)((float)list.get(i)/1000*(Math.pow(2, 23)));
                                    //int data=(int)((float)list.get(i));
                                    int data = (int)((float)(i*0.01+5)/1000*(Math.pow(2, 23)));
                                    byte [] b=IntToBytes2(63,data);//大弱
                                    outputStream.write(b);
                                }
                                /*for(int i=0;i<20;i++){
                                    int data = (int)((float)1/5*(Math.pow(2, 19)));
                                    byte [] b=IntToBytes(128,data);//小电流
                                    outputStream.write(b);
                                }
                                int data = (int)(9);
                                byte [] b=IntToBytes(144,data);//关断时间
                                outputStream.write(b);*/
                            }
                            if(sb.toString().equals("start1\r\n")){
                                Log.e("wifitest","1"+ChartDataAnalysis.getSin(4f, 1.0f, 400).size());
                                Log.e("wifitest","2"+ChartDataAnalysis.getSin(4f, 1.0f, 400));
                                //outputStream.write("Hello Client,I get the message.2".getBytes("UTF-8"));
                                list=ChartDataAnalysis.getDesc(3f,0.0001f,10000);
                                Log.e("chartDataAnalysis","3"+ChartDataAnalysis.getSin(4f, 1.0f, 400));
                                float[] ints = new float[list.size()];
                                for (int i = 1; i < list.size(); i++) {
                                    //int data = (int)((float)list.get(i)/1000*(Math.pow(2, 23)));
                                    //int data=(int)((float)list.get(i));
                                    int data = (int)((float)(i*0.01+10)/1000*(Math.pow(2, 23)));

                                    byte [] b;
                                    if(i>=100){
                                        b=IntToBytes2(15,data);
                                    }else{
                                        b=IntToBytes2(0,data);
                                    }
                                    outputStream.write(b);
                                }
                                list=ChartDataAnalysis.getDesc(4f,0.00001f,9900);
                                Log.e("chartDataAnalysis","2"+ChartDataAnalysis.getDesc(4f,0.00001f,9900));

                                for (int i = 1; i < list.size(); i++) {
                                    //int data = (int)((float)list.get(i)/1000*(Math.pow(2, 23)));
                                    //int data=(int)((float)list.get(i));
                                    int data = (int)((float)(i*0.01+15)/1000*(Math.pow(2, 23)));
                                    byte [] b=IntToBytes2(31,data);//da弱
                                    outputStream.write(b);
                                }
                                /*for(int i=0;i<20;i++){
                                    int data = (int)((float)1/5*(Math.pow(2, 19)));
                                    byte [] b=IntToBytes(192,data);//大电流
                                    outputStream.write(b);
                                }
                                int data = (int)(13);
                                byte [] b=IntToBytes(208,data);//关断时间
                                outputStream.write(b);*/
                            }
                            if(sb.toString().equals("start2\r\n")){
                                Log.e("wifitest","start2");
                                list=ChartDataAnalysis.getDesc(3f,0.0001f,100);

                                float[] ints = new float[list.size()];
                                for (int i = 0; i < list.size(); i++) {
                                    int data = (int)(list.get(i)/1000*(Math.pow(2, 23)));
                                    //int data=(int)((float)list.get(i));
                                    byte [] b;
                                    b=IntToBytes2(128,data);
                                    outputStream.write(b);
                                }
                            }
                            //socket.shutdownOutput();
                            inputStream.close();
                            outputStream.close();
                            socket.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"5555555555555");
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.logAppend("客户端连接失败！！！\n");
                        }
                    });
                }
            }
        }).start();
    }

    /**
     *
    	 * @param type  电流类型1-小强，2-小弱，3-大强，4-大弱
    	 * @param i 数据
    	 * @return byte[]
    **/
    //23位数据位，高位为符号位
    public static byte[] IntToBytes2(int type,int i) {
        byte [] result = new byte[4];
        result[0] = (byte)(type&0xFF);
        result[1] = (byte)((i>>16)&0x7F);
        result[2] = (byte)((i>>8)&0xFF);
        result[3] = (byte)(i&0xFF);
        if(i<0) {
            result[1] = (byte) (result[0]|0x80);
        }
        return result;
    }
    public static byte[] IntToBytes(int type,int i) {
        byte [] result = new byte[4];
        result[0] = (byte)(type&0xFF);
        result[1] = (byte)((i>>16)&0xFF);
        result[2] = (byte)(i>>8&0xFF);
        result[3] = (byte)(i&0xFF);
        return result;
    }
    public static byte[] IntToBytes(int i) {
        byte [] result = new byte[4];
        result[0] = (byte)((i>>24)&0xFF);
        result[1] = (byte)((i>>16)&0xFF);
        result[2] = (byte)((i>>8)&0xFF);
        result[3] = (byte)(i&0xFF);
        return result;
    }
    private void initText(){
        PointShow.setText(ParamSaveClass.PointNum+"");
        LineShow.setText(ParamSaveClass.LineNum+"");
        PointDistenceShow.setText(ParamSaveClass.DotPit+"");
        LineDistenceShow.setText(ParamSaveClass.LinePit+"");
        //todo
    }
    private void initView(){
        logTextView = (TextView) mainActivity.findViewById(R.id.log_text_view);
        butResistivity=(Button) mainActivity.findViewById(R.id.log_button_chart);
        butClearLog=(Button) mainActivity.findViewById(R.id.log_button_clear);
        butCarlibration=(Button) mainActivity.findViewById(R.id.controller_button_calibration);
        butSelfCheck=(Button) mainActivity.findViewById(R.id.controller_button_selfcheck);
        butAquisition=(Button) mainActivity.findViewById(R.id.controller_button_aquisition);
        preDot=(Button) mainActivity.findViewById(R.id.pre_dot);
        nextDot=(Button) mainActivity.findViewById(R.id.next_dot);
        chartChange=(Button)mainActivity.findViewById(R.id.chart_change) ;
        fileSize=mainActivity.findViewById(R.id.time_data_siza);
        butResistivity.setOnClickListener(this);
        butClearLog.setOnClickListener(this);
        butAquisition.setOnClickListener(this);
        butSelfCheck.setOnClickListener(this);
        butCarlibration.setOnClickListener(this);
        preDot.setOnClickListener(this);
        nextDot.setOnClickListener(this);
        chartChange.setOnClickListener(this);
        chartone=(LineChart) mainActivity.findViewById(R.id.first_chart);
        charttwo=(LineChart) mainActivity.findViewById(R.id.second_chart);
    }
    private void initChart(){

       ArrayList<ArrayList<Float>> List1=null;
        if(ChartDataAnalysis.lists1==null||ChartDataAnalysis.lists1.get(0).size()<=1){
            List1 = ChartDataAnalysis.getSinData(1.25f,4f,0f, 0,24);
        }else{
            List1=new ArrayList<>(ChartDataAnalysis.lists1);
        }
        Log.e(TAG,"ChartDataAnalysis.lists1:"+ChartDataAnalysis.lists1);
        ChartPlay.initChartView(chartone,5,4,false,true,"","","V/I（µV/A）/点号(号)");//初始化图表(1V=10^6µV)
        ChartPlay.showLineChart(chartone,List1.get(0),false,true,"5 µs", ColorArr[0],1,1);
        ChartPlay.addLine(chartone,List1.get(1),false,true,"22 µs", ColorArr[1],1,1);
        ChartPlay.addLine(chartone,List1.get(2),false,true,"64 µs", ColorArr[2],1,1);
        ChartPlay.addLine(chartone,List1.get(3),false,true,"168 µs", ColorArr[3],1,1);
        ChartPlay.addLine(chartone,List1.get(4),false,true,"408 µs", ColorArr[4],1,1);
        ChartPlay.addLine(chartone,List1.get(5),false,true,"952 µs", ColorArr[5],1,1);

        /*ArrayList<ArrayList<Float>> List1 = ChartDataAnalysis.binaryToDecimalTest(".dat");
        ChartPlay.initChartView(chartone,5,6,true,true,"","","V/I（µV/A）/点号(号)");//初始化图表(1V=10^6µV)
        ChartPlay.showLineChart(chartone,List1.get(0),true,true,String.format("%.1f",1*0.8), Color.GRAY,1,1);
        ChartPlay.addLine(chartone,List1.get(1),true,true,String.format("%.1f",2*0.8), Color.LTGRAY,1,CurNum0);
        ChartPlay.addLine(chartone,List1.get(2),true,true,String.format("%.1f",3*0.8), Color.RED,1,1);
        ChartPlay.addLine(chartone,List1.get(3),true,true,String.format("%.1f",4*0.8), Color.BLUE,1,CurNum1);*/
        chartone.invalidate();
        charttwo.clear();

        ArrayList<ArrayList<Float>> List2=new ArrayList<>();
        Log.e(TAG,"ChartDataAnalysis.lists2:"+ChartDataAnalysis.lists2);
        Log.e(TAG,"ChartDataAnalysis.CurrentList:"+ChartDataAnalysis.CurrentList);
        if(ChartDataAnalysis.lists2==null||ChartDataAnalysis.lists2.size()!=4||ChartDataAnalysis.lists2.get(0).size()==0){
            List2.add(ChartDataAnalysis.getInit().get(0));
            List2.add(ChartDataAnalysis.getInit().get(3));
            List2.add(ChartDataAnalysis.getInit().get(1));
            List2.add(ChartDataAnalysis.getInit().get(2));
            Log.e(TAG,"list2:"+List2);
            ArrayList<Float> list3 = ChartDataAnalysis.getRongHe(List2,50,50);//50=0.02*1(p*t)
            Log.e(TAG,"list3:"+list3);
            ChartPlay.firstChartView(charttwo,6,4,"","","V/I（µV/A）/时间（µs）/相对误差百分比（%）");
            ChartPlay.firstChart(charttwo,List2.get(0),"小电流强信号",Color.CYAN,0.02,0);
            ChartPlay.firstaddline(charttwo,List2.get(1),"小电流弱信号",Color.MAGENTA,0.02,1);
            ChartPlay.firstaddline(charttwo,List2.get(2),"大电流弱信号",Color.RED,0.02,1);
            ChartPlay.firstaddline(charttwo,List2.get(3),"大电流强信号",Color.BLUE,0.02,0);
            ChartPlay.firstaddline(charttwo,list3,"融合信号",Color.GREEN,0.02,0);
             ChartDataAnalysis.ErrorList.clear();
            for(int i=0;i<6;i++){
                ChartDataAnalysis.ErrorList.add((float)Math.random());
            }
            //ChartPlay.firstaddRightYLine(charttwo,ChartDataAnalysis.ErrorList,"误差",Color.YELLOW,1,0);

        }else{
            Log.e(TAG,"ChartDataAnalysis.lists2:"+ChartDataAnalysis.lists2);
            if(ChartDataAnalysis.ErrorList!=null&&ChartDataAnalysis.ErrorList.size()!=0){
                ChartPlay.initChartView(charttwo,5,4,false,false,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
            }else{
                ChartPlay.initChartView(charttwo,5,4,false,false,"","","V/I（µV/A）/时间（µs）");//初始化图表
            }

            if(ChartDataAnalysis.lists2.get(0).size()!=0)ChartPlay.showLineChart(charttwo,ChartDataAnalysis.lists2.get(0),false,false,"小电流强信号", Color.CYAN,1,1,false);//ParamSaveClass.time
            if(ChartDataAnalysis.lists2.get(1).size()!=0)ChartPlay.addLine(charttwo,ChartDataAnalysis.lists2.get(1),false,false,"小电流弱信号", Color.LTGRAY,1,1,false);
            if(ChartDataAnalysis.lists2.get(2).size()!=0)ChartPlay.addLine(charttwo,ChartDataAnalysis.lists2.get(2),false,false,"大电流弱信号", Color.BLUE,1,1,false);
            if(ChartDataAnalysis.lists2.get(3).size()!=0)ChartPlay.addLine(charttwo,ChartDataAnalysis.lists2.get(3),false,false,"大电流强信号", Color.RED,1,1,false);

            /*int a=Math.min(ChartDataAnalysis.CurrentList.get(0).size(),10000);
            ArrayList<Float> temp=new ArrayList<>();
            for(int i=0;i<a;i++){
                temp.add(ChartDataAnalysis.CurrentList.get(0).get(i));
            }*/
            //if(ChartDataAnalysis.CurrentList.size()!=0&&ChartDataAnalysis.CurrentList.get(0)!=null&&ChartDataAnalysis.CurrentList.get(0).size()!=0)ChartPlay.addLine(charttwo,temp,false,false,"小电流", Color.BLACK,1,CurNum0);
            //if(ChartDataAnalysis.CurrentList.size()!=0&&ChartDataAnalysis.CurrentList.get(0)!=null&&ChartDataAnalysis.CurrentList.get(0).size()!=0)ChartPlay.addLine(charttwo,ChartDataAnalysis.CurrentList.get(0),false,false,"小电流", Color.BLACK,1,CurNum0);
            //if(ChartDataAnalysis.CurrentList.size()!=0&&ChartDataAnalysis.CurrentList.get(1)!=null&&ChartDataAnalysis.CurrentList.get(1).size()!=0)ChartPlay.addLine(charttwo,ChartDataAnalysis.CurrentList.get(1),true,true,"大电流", Color.YELLOW,1,CurNum1);
            if(ChartDataAnalysis.ErrorList!=null&&ChartDataAnalysis.ErrorList.size()!=0)
                ChartPlay.addRightYLine(charttwo,ChartDataAnalysis.ErrorList,true,false, "误差",Color.YELLOW,1,1,true);
            Log.e(TAG,"ChartDataAnalysis.ErrorList:"+ChartDataAnalysis.ErrorList);
            Log.e(TAG,"ChartDataAnalysis.lists2:"+ChartDataAnalysis.lists2.get(0));
            Log.e(TAG,"ChartDataAnalysis.ErrorList:"+ChartDataAnalysis.ErrorList.size());
            Log.e(TAG,"ChartDataAnalysis.lists2:"+ChartDataAnalysis.lists2.get(0).size());
        }
        charttwo.invalidate();
        Log.e(TAG, "电压衰减曲线");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //Activity必需离开Activity栈的栈顶
    @Override
    public void onPause() {
        MainActivity.fragmentState=1;
        super.onPause();
        SharedPreferences.Editor pre = mainActivity.getSharedPreferences("butState", 0).edit();
        pre.putString("carliText", butCarlibration.getText().toString());
        pre.putString("butCheckText", butSelfCheck.getText().toString());
        pre.putString("butAquiText", butAquisition.getText().toString());
        pre.putBoolean("carliState", butCarlibration.isEnabled());
        pre.putBoolean("checkState", butSelfCheck.isEnabled());
        pre.putBoolean("aquisitionState", butAquisition.isEnabled());
        pre.apply();
    }

    //当碎片和活动解除关联的时候调用
    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences.Editor pre = mainActivity.getSharedPreferences("butState", 0).edit();
        pre.putString("carliText", butCarlibration.getText().toString());
        pre.putString("butCheckText", butSelfCheck.getText().toString());
        pre.putString("butAquiText", butAquisition.getText().toString());
        pre.putBoolean("carliState", butCarlibration.isEnabled());
        pre.putBoolean("checkState", butSelfCheck.isEnabled());
        pre.putBoolean("aquisitionState", butAquisition.isEnabled());
        pre.apply();
        super.onDetach();
    }

    //碎片重新激活时调用
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pre = mainActivity.getSharedPreferences("butState", 0);
        String butCheckText = pre.getString("butCheckText", "预采集");
        String butAquiText = pre.getString("butAquiText", "开始采集");

        butSelfCheck.setText(butCheckText);
        butAquisition.setText(butAquiText);
        //controllerFragment.butUpdateConfig.setEnabled(pre.getBoolean("configState", true));
        butCarlibration.setEnabled(pre.getBoolean("carliState", true));
        butSelfCheck.setEnabled(pre.getBoolean("checkState", true));
        butAquisition.setEnabled(pre.getBoolean("aquisitionState", true));

        if (!butAquisition.isEnabled()){
            butAquisition.setEnabled(true);
            butSelfCheck.setEnabled(true);
            butCarlibration.setEnabled(true);
        }
        super.onResume();
    }

    @Override
    public void onStart() {//碎片重新激活时调用
        super.onStart();
        //todo
        logTextView.setText(mainActivity.logContent);
    }

    //校准
    private void butStartCarliClicked() {
        handler.obtainMessage(Constants.ChartTwoCur0test).sendToTarget();

        /*mainActivity.logAppend("->正在校准\r\n");
        butCarlibration.setEnabled(false);
        butAquisition.setEnabled(false);
        butSelfCheck.setEnabled(false);
        chartone.setTouchEnabled(false);
        charttwo.setTouchEnabled(false);
        //progressBar.setVisibility(View.VISIBLE);
        mainActivity.sendCommand(Constants.CMD_CALIBRATION);
        handler.obtainMessage(Constants.ChartTwoCur1).sendToTarget();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    handler.obtainMessage(Constants.Calibration).sendToTarget();
                }
            }
        }).start();*/
    }

    //自检
    private void butStartSelfCheckClicked() {
        mainActivity.logAppend("->正在预采集\r\n");
        butCarlibration.setEnabled(false);
        butAquisition.setEnabled(false);
        chartone.setTouchEnabled(false);
        charttwo.setTouchEnabled(false);
        WifiThread wifiThread=new WifiThread(handler,mainActivity);
        wifiThread.start();
    }

    //停止自检
    private void butStopSelfCheckClicked(){
        mainActivity.logAppend("->停止自检\r\n");
    }

    //开始采集
    private void butStartAquisitionClicked() {
        mainActivity.logAppend("->" + "采集中...\r\n");
        /*wifiClientThread = new WifiClientThread(ip, handler,mainActivity);
        wifiClientThread.start();*/
        String s="";
        if(Rssi>=-50){
            s="dBm,信号强";
        }else if(Rssi>=-70){
            s="dBm,信号较差";
        }else{
            s="dBm,信号差，请对网络或地理位置做出调整";
        }
        mainActivity.logAppend("当前WiFi信号强度为："+Rssi+s+"\r\n");
        if(ip.equals("192.168.4.1")) {//192.168.4.1
            wifiClientThread = new WifiClientThread(ip, handler,mainActivity);
            wifiClientThread.start();
        }else{
            mainActivity.logAppend("->"+"仪器连接失败，请检查是否连接了仪器热点"+"\n");
        }
        butAquisition.setText(R.string.button_stop_aquisition);
        butCarlibration.setEnabled(false);
        butSelfCheck.setEnabled(false);
        chartone.setTouchEnabled(false);
        charttwo.setTouchEnabled(false);
        mainActivity.controllerLayout.setEnabled(false);
        mainActivity.settingLayout.setEnabled(false);
        mainActivity.logLayout.setEnabled(false);
        mainActivity.measurementLayout.setEnabled(false);
    }

    //停止采集
    private void butStopAquisitionClicked() {
        if (wifiClientThread!=null){
            wifiClientThread.stopThreadFlag=true;
        }
        mainActivity.sendCommand(Constants.CMD_STOP);
        handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送停止命令："+Constants.CMD_STOP).sendToTarget();

        butAquisition.setText(R.string.button_start_aquisition);
        //buttonUpdateConfigenableState=true;
        butCarlibration.setEnabled(true);
        butSelfCheck.setEnabled(true);
        chartone.setTouchEnabled(true);
        charttwo.setTouchEnabled(true);
        mainActivity.controllerLayout.setEnabled(true);
        mainActivity.settingLayout.setEnabled(true);
        mainActivity.logLayout.setEnabled(true);
        mainActivity.measurementLayout.setEnabled(true);
    }

    //曲线融合
    private void ChangeChart(){
        //调用list成图，不做计算。
        if(chartChange.getText().equals("融合曲线")){
            chartChange.setText("四曲线");
            ArrayList<ArrayList<Float>> List2=new ArrayList<>();
            if(ChartDataAnalysis.lists2==null|| ChartDataAnalysis.lists2.size()!=4) {
                List2.add(ChartDataAnalysis.getInit().get(0));
                List2.add(ChartDataAnalysis.getInit().get(3));
                List2.add(ChartDataAnalysis.getInit().get(1));
                List2.add(ChartDataAnalysis.getInit().get(2));
                Log.e(TAG, "list2:" + List2);
                ArrayList<Float> list3 = ChartDataAnalysis.getRongHe(List2, 50, 50);//50=0.02*1(p*t)
                Log.e(TAG, "list3:" + list3);
                ChartPlay.firstaddline(charttwo, list3, "融合", Color.GREEN, 0.02, 0);
                charttwo.invalidate();
            }else{
                ArrayList<Float> list3 = ChartDataAnalysis.getRongHe(ChartDataAnalysis.lists2, CurNum0,  CurNum1);//50=0.02*1(p*t)
                Log.e(TAG, "list3:" + list3);
                ChartPlay.addLine(charttwo, list3, true,true,"融合", Color.GREEN, 1, 1);
                charttwo.invalidate();
            }
        }else{
            chartChange.setText("融合曲线");
            if(ChartDataAnalysis.lists2==null|| ChartDataAnalysis.lists2.size()!=4){
                ArrayList<ArrayList<Float>> List2=new ArrayList<>();
                List2.add(ChartDataAnalysis.getInit().get(0));
                List2.add(ChartDataAnalysis.getInit().get(3));
                List2.add(ChartDataAnalysis.getInit().get(1));
                List2.add(ChartDataAnalysis.getInit().get(2));
                Log.e(TAG,"list2:"+List2);
                ArrayList<Float> list3 = ChartDataAnalysis.getRongHe(List2,50,50);//50=0.02*1(p*t)
                Log.e(TAG,"list3:"+list3);
                ChartPlay.firstChartView(charttwo,6,6,"","","衰减曲线");
                ChartPlay.firstChart(charttwo,List2.get(0),"小强",Color.CYAN,0.02,0);
                ChartPlay.firstaddline(charttwo,List2.get(1),"小弱",Color.LTGRAY,0.02,1);
                ChartPlay.firstaddline(charttwo,List2.get(2),"大弱",Color.RED,0.02,1);
                ChartPlay.firstaddline(charttwo,List2.get(3),"大强",Color.BLUE,0.02,0);
            }else{
                ChartPlay.initChartView(charttwo,5,4,true,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
                ChartPlay.showLineChart(charttwo,ChartDataAnalysis.lists2.get(0),true,true,"小电流强信号", Color.CYAN,1,1);
                ChartPlay.addLine(charttwo,ChartDataAnalysis.lists2.get(1),true,true,"小电流弱信号", Color.LTGRAY,1,CurNum0);
                ChartPlay.addLine(charttwo,ChartDataAnalysis.lists2.get(2),true,true,"大电流弱信号", Color.RED,1,CurNum1);
                ChartPlay.addLine(charttwo,ChartDataAnalysis.lists2.get(3),true,true,"大电流强信号", Color.BLUE,1,1);
            }

        }

    }

    private ArrayList<Float> ReadTxt(File file,int begin){
        boolean b=false;//begin为正
        if(begin<0){
            begin=-begin;
            b=true;
        }
        int end=begin+8000;
        ArrayList<Float> res=new ArrayList<>();
        if(file.length()<90||!file.exists()){//一个图最少两个点，初定九条线 2*9*5
            return res;
        }
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffReader = new BufferedReader(reader);
        String strTmp = "";
        try{

            while((strTmp = buffReader.readLine())!=null&&strTmp!=null&&strTmp.length()!=0){
                //Log.e(TAG,"strTmp"+strTmp);
                strTmp=strTmp.trim();
                Float a=Float.parseFloat(strTmp);
                res.add(a);
                /*strTmp=strTmp.trim();
                String [] array=strTmp.split("\\s+");//按空格分隔
                for(int i=0;i<array.length;i++){
                    //Log.e(TAG,i+"array[i]:"+array[i]);
                    Float a=Float.parseFloat(array[i]);
                    if(a==0&&i>0)a=Float.parseFloat(array[i-1]);
                    res.add(a);
                }*/
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buffReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Float> res1=new ArrayList<>();
        if(b){
            for(int i=begin;i<end;i++){
                res1.add(-res.get(i));
            }
        }else{
            for(int i=begin;i<end;i++){
                res1.add(res.get(i));
            }
        }
        return res1;
    }
    private ArrayList<Float> ReadTxt(File file){
        ArrayList<Float> res=new ArrayList<>();
        Log.e(TAG, "111111111111:"+file.length());
        if(file.length()<90||!file.exists()){//一个图最少两个点，初定九条线 2*9*5
            Log.e(TAG, "1111111111112222222222:"+file.length());
            return res;
        }
        Log.e(TAG, "2222222222");
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffReader = new BufferedReader(reader);
        String strTmp = "";
        try{
            int index=0;
            while((strTmp = buffReader.readLine())!=null){
                Log.e(TAG,"strTmp"+strTmp);
                strTmp=strTmp.trim();
                String [] array=strTmp.split("\\s+");//按空格分隔
                for(int i=0;i<array.length;i++){
                    //Log.e(TAG,i+"array[i]:"+array[i]);
                    Float a=Float.parseFloat(array[i]);
                    if(a==0&&i>0)a=Float.parseFloat(array[i-1]);
                    res.add(a);
                }
                index++;
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buffReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG,"res.size():"+res.size());
        return res;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_button_chart:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File f = new File(Constants.DATA_DIRECTORY+"/aaaaaaa.txt");


                        OutputStreamWriter writer= null;
                        try {
                            writer = new OutputStreamWriter(new FileOutputStream(f, true), "utf-8");
                            writer.write("11111111111111111\n");
                            writer.flush();
                            writer.close();
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                        list=new ArrayList<>(4);
                        if(list.size()<4){
                            for(int i=0;i<4;i++){
                                list.add(new ArrayList<Float>());
                            }
                        }
                        String FileName= Environment.getExternalStorageDirectory().getPath()+"/TEM/大1/0.txt";
                        File file=new File(FileName);
                        ArrayList<Float> l1 = ReadTxt(file,0);
                        FileName= Environment.getExternalStorageDirectory().getPath()+"/TEM/大1/1.txt";
                        file=new File(FileName);
                        ArrayList<Float> l2 = ReadTxt(file,0);
                        int FuHao[]={0,-1,1};
                        for(int i=14;i<5114;i++){
                            list.get(0).add(-l1.get(i)/16f);
                            if(i<83){
                                list.get(1).add(0f);
                            }else{
                                list.get(1).add(-l2.get(i)/16f);
                            }
                        }
                        handler.obtainMessage(Constants.ChartTwoTest4).sendToTarget();
                        float a=0,b=0;
                        int index=0,time=0;
                        while(index<20){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+5;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(time*5f-2.5f);ChouDaoY2.add(a/5);
                            ChouDaoX3.add(time*5f-2.5f);ChouDaoY3.add(b/5);
                            index=index+5;
                        }
                        index=20;time=0;
                        while(index<60){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+10;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(20-5f+time*10);ChouDaoY2.add(a/10);
                            ChouDaoX3.add(20-5f+time*10);ChouDaoY3.add(b/10);
                            index=index+10;
                        }
                        index=60;time=0;
                        while(index<140){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+20;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(60-10f+time*20);ChouDaoY2.add(a/20);
                            ChouDaoX3.add(60-10f+time*20);ChouDaoY3.add(b/20);
                            index=index+20;
                        }
                        index=140;time=0;
                        while(index<300){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+40;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(140-20f+time*40);ChouDaoY2.add(a/40);
                            ChouDaoX3.add(140-20f+time*40);ChouDaoY3.add(b/40);
                            index=index+40;
                        }
                        index=300;time=0;
                        while(index<620){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+80;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(300-40f+time*80);ChouDaoY2.add(a/80);
                            ChouDaoX3.add(300-40f+time*80);ChouDaoY3.add(b/80);
                            index=index+80;
                        }
                        index=620;time=0;
                        while(index<1260){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+160;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(620-80f+time*160);ChouDaoY2.add(a/160);
                            ChouDaoX3.add(620-80f+time*160);ChouDaoY3.add(b/160);
                            index=index+160;
                        }
                        index=1260;time=0;
                        while(index<2540){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+320;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(1260-160f+time*320);ChouDaoY2.add(a/320);
                            ChouDaoX3.add(1260-160f+time*320);ChouDaoY3.add(b/320);
                            index=index+320;
                        }
                        index=2540;time=0;
                        while(index<5100){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+640;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX2.add(2540-340f+time*640);ChouDaoY2.add(a/640);
                            ChouDaoX3.add(2540-340f+time*640);ChouDaoY3.add(b/640);
                            index=index+640;
                        }
                        for(int i=0;i<ChouDaoY2.size();i++){
                            Log.e(TAG,"ChouDaoY0:"+ChouDaoY2.get(i));
                            Log.e(TAG,"ChouDaoY1:"+ChouDaoY3.get(i));
                            if(i<17){
                                RongHe1.add(ChouDaoY2.get(i));
                            }else{
                                RongHe1.add(ChouDaoY3.get(i));
                            }
                        }
                        for(int i=0;i<ChouDaoY2.size();i++){
                            Log.e(TAG,"ChouDaoX0:"+ChouDaoX2.get(i));
                            Log.e(TAG,"ChouDaoY0:"+ChouDaoY2.get(i));
                            Log.e(TAG,"ChouDaoX1:"+ChouDaoX3.get(i));
                            Log.e(TAG,"ChouDaoY1:"+ChouDaoY3.get(i));
                        }
                        handler.obtainMessage(Constants.ChartTwoTest2).sendToTarget();
                    }
                }).start();



                        /*int index = ParamSaveClass.PointNum;
                        list=new ArrayList<>(4);
                        if(list.size()<4){
                            for(int i=0;i<4;i++){
                                list.add(new ArrayList<Float>());
                            }
                        }
                        Log.e(TAG,"ParamSaveClass.PointNum:"+ParamSaveClass.PointNum);
                        *//*String FileName= Constants.DATA_DIRECTORY+"/第一次采集"+"/"+index+"/"+"/000000.txt";
                        File file=new File(FileName);
                        ArrayList<Float> l1=ReadTxt(file,SmallIndex1[index]);//2500个点
                        FileName= Constants.DATA_DIRECTORY+"/第一次采集"+"/"+index+"/"+"/111111.txt";
                        file=new File(FileName);
                        ArrayList<Float> l2=ReadTxt(file,SmallIndex1[index]);//2500个点*//*
                        String FileName= Constants.DATA_DIRECTORY+"/大1"+"/000000.txt";
                        File file=new File(FileName);
                        ArrayList<Float> l1=ReadTxt(file,-20887);//2500个点
                        FileName= Constants.DATA_DIRECTORY+"/大1"+"/111111.txt";
                        file=new File(FileName);
                        ArrayList<Float> l2=ReadTxt(file,-20887);//2500个点
                        FileName= Constants.DATA_DIRECTORY+"/第一次采集/小"+index+"/"+"/111111.txt";
                        file=new File(FileName);
                        ArrayList<Float> l3=ReadTxt(file,BigIndex1[index]);
                        FileName= Constants.DATA_DIRECTORY+"/第一次采集/小"+index+"/"+"/000000.txt";
                        file=new File(FileName);
                        ArrayList<Float> l4=ReadTxt(file,BigIndex1[index]);
                        Log.e(TAG,"list:"+list.size());
                        Log.e(TAG,"l1:"+l1.size()+"  l2:"+l2.size()+"   l3:"+l3.size()+"   l4:"+l4.size());
                        int listIndex1=0;
                        while(listIndex1<l1.size()&&l1.get(listIndex1)>0.1){
                            listIndex1++;
                        }
                        int listIndex2=0;
                        while(listIndex2<l3.size()&&l4.get(listIndex2)>0.1){
                            listIndex2++;
                        }
                        listIndex1=488;
                        listIndex2=0;
                        Log.e(TAG,"listIndex1:"+listIndex1+"   listIndex2:"+listIndex2);
                        for(int i=0;i<2500;i++){
                            list.get(0).add(l1.get(i));//1.11145975f
                            //list.get(1).add(l2.get(i));
                            //list.get(2).add(l3.get(i));//归一化处理/16
                            if(listIndex1<i){//+450
                                list.get(1).add(l2.get(i));///1.11145975f
                            }else{
                                list.get(1).add(0f);
                            }
                            if(listIndex2<i){
                                list.get(2).add(l3.get(i));//归一化处理/16
                            }else{
                                list.get(2).add(0f);
                            }
                            list.get(3).add(l4.get(i));//16/16
                        }
                        //ChartDataAnalysis.lists3 = ChartDataAnalysis.getRongHe1(list,1,1);
                        ChartDataAnalysis.lists3 = ChartDataAnalysis.getRongHe2(list);
                        //ChartDataAnalysis.lists3 = list.get(0);
                        if(CopyList.size()!=0){
                            ChartDataAnalysis.ErrorList = ChartDataAnalysis.getErrorLists(ChartDataAnalysis.lists3,CopyList);
                            Log.e(TAG,"ChartDataAnalysis.ErrorList.size()1:"+ChartDataAnalysis.ErrorList.size());
                        }
                        CopyList=new ArrayList<>(ChartDataAnalysis.lists3);
                        Log.e(TAG,"list:"+list.size());
                        handler.obtainMessage(Constants.ChartTwoTest).sendToTarget();
                        ArrayList<Float> DotList = WifiClientThread.getChartOneData();
                        if(ChartDataAnalysis.lists1==null){
                            ChartDataAnalysis.lists1=new ArrayList<>(9);
                        }
                        if(ChartDataAnalysis.lists1.size()<9){
                            for(int i=0;i<9;i++){
                                ChartDataAnalysis.lists1.add(new ArrayList<>());
                            }
                        }
                        for(int i=0;i<DotList.size();i++){
                            ChartDataAnalysis.lists1.get(i).add(DotList.get(i));
                        }
                        handler.obtainMessage(Constants.ChartOne).sendToTarget();
                        //handler.obtainMessage(Constants.ChartTwoTest1).sendToTarget();
                    }
                }).start();*/


                //清空SD卡
                mainActivity.sendCommand(Constants.CMD_DELETE);
                /*if(butResistivity.getText().equals("视电阻率图")){
                    butResistivity.setText("电压衰减曲线");
                    logTextView.setText("更换为视电阻率衰减曲线");
                    mainActivity.logContent = "";
                }else{
                    butResistivity.setText("视电阻率图");
                    logTextView.setText("更换为电压衰减曲线");
                    mainActivity.logContent = "";
                }*/
                break;
            case R.id.log_button_clear:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        list=new ArrayList<>(4);
                        if(list.size()<4){
                            for(int i=0;i<4;i++){
                                list.add(new ArrayList<Float>());
                            }
                        }
                        if(ParamSaveClass.PointNum==1&&ChartDataAnalysis.lists1!=null) ChartDataAnalysis.lists1.clear();
                        if(ParamSaveClass.PointNum==8)ParamSaveClass.PointNum=9;
                        if(ParamSaveClass.PointNum>=20)ParamSaveClass.PointNum=20;
                        String FileName= Environment.getExternalStorageDirectory().getPath()+"/TEM/小电流叠加/小"+ParamSaveClass.PointNum+"/0.txt";
                        File file=new File(FileName);
                        ArrayList<Float> l1 = ReadTxt(file,0);
                        FileName= Environment.getExternalStorageDirectory().getPath()+"/TEM/小电流叠加/小"+ParamSaveClass.PointNum+"/1.txt";
                        file=new File(FileName);
                        ArrayList<Float> l2 = ReadTxt(file,0);
                        int FuHao[]={0,-1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
                        int KaiShi[]={0,1,2,1,1,0,1,2,3,2,1,2,0,2,1,2,2,1,1,2,0};
                        if(FuHao[ParamSaveClass.PointNum]==-1){
                            for(int i=KaiShi[ParamSaveClass.PointNum];i<KaiShi[ParamSaveClass.PointNum]+5100;i++){
                                list.get(0).add(-l1.get(i)/1.11145975f);
                                list.get(1).add(-l2.get(i)/1.11145975f);
                            }
                        }else{
                            for(int i=KaiShi[ParamSaveClass.PointNum];i<KaiShi[ParamSaveClass.PointNum]+5100;i++){
                                list.get(0).add(l1.get(i)/1.11145975f);
                                list.get(1).add(l2.get(i)/1.11145975f);
                            }
                        }
                        /*if(FuHao[ParamSaveClass.PointNum]==-1){
                            for(int i=0;i<5100;i++){
                                list.get(0).add(-l1.get(i)/1.11145975f);
                                list.get(1).add(-l2.get(i)/1.11145975f);
                            }
                        }else{
                            for(int i=0;i<5100;i++){
                                list.get(0).add(l1.get(i)/1.11145975f);
                                list.get(1).add(l2.get(i)/1.11145975f);
                            }
                        }*/

                        handler.obtainMessage(Constants.ChartTwoTest).sendToTarget();
                        ChouDaoX0.clear();ChouDaoY0.clear();ChouDaoX1.clear();ChouDaoY1.clear();RongHe0.clear();
                        float a=0,b=0;
                        int index=0,time=0;
                        while(index<20){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+5;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(time*5f-2.5f);ChouDaoY0.add(a/5);
                            ChouDaoX1.add(time*5f-2.5f);ChouDaoY1.add(b/5);
                            index=index+5;
                        }
                        index=20;time=0;
                        while(index<60){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+10;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(20-5f+time*10);ChouDaoY0.add(a/10);
                            ChouDaoX1.add(20-5f+time*10);ChouDaoY1.add(b/10);
                            index=index+10;
                        }
                        index=60;time=0;
                        while(index<140){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+20;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(60-10f+time*20);ChouDaoY0.add(a/20);
                            ChouDaoX1.add(60-10f+time*20);ChouDaoY1.add(b/20);
                            index=index+20;
                        }
                        index=140;time=0;
                        while(index<300){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+40;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(140-20f+time*40);ChouDaoY0.add(a/40);
                            ChouDaoX1.add(140-20f+time*40);ChouDaoY1.add(b/40);
                            index=index+40;
                        }
                        index=300;time=0;
                        while(index<620){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+80;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(300-40f+time*80);ChouDaoY0.add(a/80);
                            ChouDaoX1.add(300-40f+time*80);ChouDaoY1.add(b/80);
                            index=index+80;
                        }
                        index=620;time=0;
                        while(index<1260){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+160;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(620-80f+time*160);ChouDaoY0.add(a/160);
                            ChouDaoX1.add(620-80f+time*160);ChouDaoY1.add(b/160);
                            index=index+160;
                        }
                        index=1260;time=0;
                        while(index<2540){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+320;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(1260-160f+time*320);ChouDaoY0.add(a/320);
                            ChouDaoX1.add(1260-160f+time*320);ChouDaoY1.add(b/320);
                            index=index+320;
                        }
                        index=2540;time=0;
                        while(index<5100){
                            time++;
                            a=0;b=0;
                            for(int i=index;i<index+640;i++){
                                a=a+list.get(0).get(i);
                                b=b+list.get(1).get(i);
                            }
                            ChouDaoX0.add(2540-340f+time*640);ChouDaoY0.add(a/640);
                            ChouDaoX1.add(2540-340f+time*640);ChouDaoY1.add(b/640);
                            index=index+640;
                        }
                        for(int i=0;i<ChouDaoY0.size();i++){
                            Log.e(TAG,"ChouDaoY0:"+ChouDaoY0.get(i));
                            Log.e(TAG,"ChouDaoY1:"+ChouDaoY1.get(i));
                            if(i<11){
                                RongHe0.add(ChouDaoY0.get(i));
                            }else{
                                RongHe0.add(ChouDaoY1.get(i));
                            }
                        }
                        for(int i=0;i<ChouDaoY0.size();i++){
                            Log.e(TAG,"ChouDaoX0:"+ChouDaoX0.get(i));
                            Log.e(TAG,"ChouDaoY0:"+ChouDaoY0.get(i));
                            Log.e(TAG,"ChouDaoX1:"+ChouDaoX1.get(i));
                            Log.e(TAG,"ChouDaoY1:"+ChouDaoY1.get(i));
                        }
                        Log.e(TAG,"ChouDaoX0:"+ChouDaoX0);
                        Log.e(TAG,"ChouDaoY0:"+ChouDaoY0);
                        Log.e(TAG,"ChouDaoX1:"+ChouDaoX1);
                        Log.e(TAG,"ChouDaoY1:"+ChouDaoY1);
                        Log.e(TAG,"ChouDaoX1:"+ChouDaoX1.size());
                        Log.e(TAG,"ChouDaoY1:"+ChouDaoY1.size());
                        Log.e(TAG,"RongHe0:"+RongHe0);

                        handler.obtainMessage(Constants.ChartTwoTest1).sendToTarget();
                        if(ChartDataAnalysis.lists1==null){
                            ChartDataAnalysis.lists1=new ArrayList<>();
                        }
                        if(ChartDataAnalysis.lists1.size()<9){
                            for(int i=0;i<9;i++){
                                ChartDataAnalysis.lists1.add(new ArrayList<>());
                            }
                        }
                        /*ChartDataAnalysis.lists1.get(0).add((float) (RongHe0.get(0)+RongHe0.get(0)*Math.random()));
                        ChartDataAnalysis.lists1.get(1).add((float) (RongHe0.get(1)+RongHe0.get(3)*Math.random()));
                        ChartDataAnalysis.lists1.get(2).add((float) (RongHe0.get(2)+RongHe0.get(4)*Math.random()));
                        ChartDataAnalysis.lists1.get(3).add((float) (RongHe0.get(3)+RongHe0.get(5)*Math.random()));
                        ChartDataAnalysis.lists1.get(4).add((float) (RongHe0.get(4)+RongHe0.get(6)*Math.random()));
                        ChartDataAnalysis.lists1.get(5).add((float) (RongHe0.get(5)+RongHe0.get(7)*Math.random()));
                        ChartDataAnalysis.lists1.get(6).add((float) (RongHe0.get(6)+RongHe0.get(9)*Math.random()));
                        ChartDataAnalysis.lists1.get(7).add((float) (RongHe0.get(7)+RongHe0.get(11)*Math.random()));
                        ChartDataAnalysis.lists1.get(8).add((float) (RongHe0.get(8)+RongHe0.get(14)*Math.random()));*/

                        ChartDataAnalysis.lists1.get(0).add(RongHe0.get(2));
                        ChartDataAnalysis.lists1.get(1).add(RongHe0.get(7));
                        ChartDataAnalysis.lists1.get(2).add(RongHe0.get(12));
                        ChartDataAnalysis.lists1.get(3).add(RongHe0.get(17));
                        ChartDataAnalysis.lists1.get(4).add(RongHe0.get(22));
                        ChartDataAnalysis.lists1.get(5).add(RongHe0.get(27));
                        //ChartDataAnalysis.lists1.get(6).add(RongHe0.get(21));
                        //ChartDataAnalysis.lists1.get(7).add(RongHe0.get(24));
                        //ChartDataAnalysis.lists1.get(8).add(RongHe0.get(27));


                        Log.e(TAG,"ChartDataAnalysis.lists1:"+ChartDataAnalysis.lists1);
                        handler.obtainMessage(Constants.ChartOne).sendToTarget();


                        /*FileName= Environment.getExternalStorageDirectory().getPath()+"/TEMDATA/TEMdata3.txt";
                        file=new File(FileName);
                        ArrayList<Float> l3 = ReadTxt(file);
                        FileName= Environment.getExternalStorageDirectory().getPath()+"/TEMDATA/TEMdata4.txt";
                        file=new File(FileName);
                        ArrayList<Float> l4 = ReadTxt(file);*/
                        /*int min=Math.min(l1.size(),l2.size());
                        for(int i=0;i<min;i++){
                            list.get(0).add(l1.get(i));
                            list.get(1).add(l2.get(i));
                            //list.get(2).add(l3.get(i));//归一化处理
                            //list.get(3).add(l4.get(i));
                        }
                        handler.obtainMessage(Constants.ChartTwoTest).sendToTarget();*/

                        //ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(Constants.DATA_DIRECTORY+"/大1/原始数据/"+"2023-05-12-11-31-23(1-1)-1S.dat");
                        //handler.obtainMessage(Constants.ChartTwoCur0test).sendToTarget();

                        /*String FileName= Constants.DATA_DIRECTORY+"/中南大学/"+"/2023-05-10-15-54-23强信号.txt";
                        File file=new File(FileName);
                        ArrayList<Float> l1=ReadTxt(file,-11408);//2500个点
                        FileName= Constants.DATA_DIRECTORY+"/中南大学/"+"/2023-05-10-15-54-23弱信号.txt";
                        file=new File(FileName);
                        ArrayList<Float> l2=ReadTxt(file,-11408);//2500个点
                        list=new ArrayList<>(4);
                        if(list.size()<4){
                            for(int i=0;i<4;i++){
                                list.add(new ArrayList<Float>());
                            }
                        }
                        for(int i=0;i<2500;i++){
                            list.get(0).add(l1.get(i));
                            //if(listIndex1<i){
                            list.get(1).add(l2.get(i));
                            //}
                        }
                        handler.obtainMessage(Constants.ChartTwoTest1).sendToTarget();*/
                    }
                }).start();

                /*logTextView.setText("");
                mainActivity.logContent = "";
                String FileName= Constants.DATA_DIRECTORY+"/大1"+"/000000.txt";
                File file=new File(FileName);
                ArrayList<Float> l=ReadTxt(file,-20887);
                ChartDataAnalysis.getSingleFFTarray(l,1000,2500000,l.size());//ArrayList<Float> a,double signalFrequency,int sampleRate,int sampleDots
                Intent intent = new Intent(getContext(), FFTtestActivity.class);
                // 进行跳转
                startActivity(intent);*/
                break;
            case R.id.controller_button_calibration:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.obtainMessage(Constants.ChartALLRongHe).sendToTarget();
                        //ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(Constants.DATA_DIRECTORY+"/大1/原始数据/"+"2023-05-15-14-47-01(1-1)-1S.dat");//
                        //handler.obtainMessage(Constants.ChartTwoCur0).sendToTarget();
                    }
                }).start();

                //butStartCarliClicked();
                break;
            case R.id.controller_button_selfcheck:
                if (butSelfCheck.getText().toString().equals(mainActivity.getString(R.string.button_start_selfcheck))){
                    butSelfCheck.setText("停止预采集");
                    butStartSelfCheckClicked();
                }
                else{
                    butSelfCheck.setText("预采集");
                      butStopSelfCheckClicked();
                }
                break;
            case R.id.controller_button_aquisition:
                if (butAquisition.getText().toString()
                        .equals(mainActivity.getString(R.string.button_start_aquisition)))
                    butStartAquisitionClicked();
                else
                    butStopAquisitionClicked();
                break;
            case R.id.pre_dot:
                //todo
                int a=ParamSaveClass.PointNum;
                int b=ParamSaveClass.PointIncrement;
                if(a-b<=0){
                    Toast.makeText(getContext(), "已经是第一个测点了！！！", Toast.LENGTH_SHORT).show();
                }else{
                    ParamSaveClass.PointNum=a-b;
                    PointShow.setText(ParamSaveClass.PointNum+"");
                    Toast.makeText(getContext(), "已切换测点，请开始采集", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.next_dot:
                //todo
                int c=ParamSaveClass.PointNum;
                int d=ParamSaveClass.PointIncrement;
                ParamSaveClass.PointNum=c+d;
                PointShow.setText(ParamSaveClass.PointNum+"");
                Toast.makeText(getContext(), "已切换测点，请开始采集", Toast.LENGTH_SHORT).show();
                break;
            case R.id.chart_change:
                ChangeChart();
                //ResultRecordDBHelper("123","1,2,3,4");
                break;
            default:
                break;
        }
    }

    private void ResultRecordDBHelper(String filename,String DotList) {
        Log.e(TAG,"ParamSaveClass.PointNum:"+ParamSaveClass.PointNum);
        Log.e(TAG,"DotList:"+DotList);
        String data=DotList.substring(1,DotList.length()-1);
        Log.e(TAG,"data:"+data);
        TemData td=new TemData(filename,ParamSaveClass.PointNum,ParamSaveClass.LineNum,ParamSaveClass.DotPit,ParamSaveClass.LinePit,ParamSaveClass.Current,ParamSaveClass.Current1,ParamSaveClass.CurrentOffTime,ParamSaveClass.CurrentOffTime1,data);
        DBHelper dbHelper = new DBHelper(mainActivity,Location);
        dbHelper.InsertData(td);
    }


    /**
     * 获取连接到热点上的手机ip
     *
     * @return
     */
    private ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }
    private void setHotsspotReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mainActivity.registerReceiver(new HotsspotReceiver(), intentFilter);
    }
    //广播监听WiFi
    class HotsspotReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {//便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra("wifi_state", 0);
                if (state == 13) {
                    //Log.e(TAG, "onReceive: "+connectIP );
                    //mainActivity.logAppend("->"+"热点已开启"+"\n");
                    //new TCPConnectThread().start();
                }
                else if (state==11){
                    //mainActivity.logAppend("->"+"热点已关闭"+"\n");
                }
            }else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                Log.e("BBB", "WifiManager.NETWORK_STATE_CHANGED_ACTION");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    mainActivity.logAppend("->"+"wifi连接断开"+"\n");
                    MainActivity.getip=true;
                    MainActivity.wifiClientThreadState=0;
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Log.w("AAA","wifiInfo.getSSID():"+wifiInfo.getSSID()+"  WIFI_HOTSPOT_SSID:");
                    Rssi=wifiInfo.getRssi();//wifi信号强度
                    if (wifiInfo.getSSID()!=null&&MainActivity.wifiClientThreadState==0) {
                        //如果当前连接到的wifi是热点,则开启连接线程
                        if(MainActivity.getip)
                            mainActivity.logAppend("->"+"已连接到网络:" + wifiInfo.getSSID()+"\n");
                        DhcpInfo dhcpInfo=wifiManager.getDhcpInfo();
                        int i=dhcpInfo.serverAddress;
                        ip=(i&0xFF)+"."+((i>>8)&0xFF)+ "."+((i>>16)&0xFF)+ "."+((i>>24)&0xFF);
                        if(MainActivity.getip)
                            mainActivity.logAppend("->"+"获取到服务端IP："+ip+"\n");
                        MainActivity.getip=false;
                        Log.e(TAG, "onClick: socket.......555" +ip);
                       /* if(!(ip.equals("192.168.4.1"))){
                            mainActivity.logAppend("->"+"仪器连接失败，请检查是否连接了仪器热点"+"\n");
                        }*/
                    }
                }
                else {
                    NetworkInfo.DetailedState state = info.getDetailedState();
                    if (state == state.CONNECTING) {
                        Log.e(TAG, "onReceive: 连接中");
                    } else if (state == state.AUTHENTICATING) {
                        Log.e(TAG, "onReceive: 正在验证身份信息..." );
                    } else if (state == state.OBTAINING_IPADDR) {
                        Log.e(TAG, "onReceive: 正在获取IP地址..." );
                    } else if (state == state.FAILED) {
                        Log.e(TAG, "onReceive: 连接失败" );
                    }
                }
            }
        }
    }
    private void setIotBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.a63431.sip1.IOT_RECEIVER");
        mainActivity.registerReceiver(new IotBroadcastReceiver(), intentFilter);
    }
    //广播监听云平台
    public class IotBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
        //todo
        }
    }
    class CommendSend extends Thread{
        @Override
        public void run() {
            super.run();
        }
    }

}
