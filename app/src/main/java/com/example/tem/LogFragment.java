package com.example.tem;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.tem.iot.IotMonitorService;
import com.example.tem.iot.mycommunication.OkHttpPost;
import com.example.tem.iot.mycommunication.huaweiIOT;
import com.example.tem.iot.responseObject.IotMessage;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class LogFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;
    private LineChart lineChart_iot;
    private IotBroadcastReceiver iotBroadcastReceiver;
    private static final String TAG ="LogFragment" ;


    @BindView(R.id.device_state_iot)
    TextView deviceStateIot;
    @BindView(R.id.device_name_iot)
    TextView deviceNameIot;
    @BindView(R.id.device_id_iot)
    TextView deviceIdIot;
    @BindView(R.id.button_devices_iot)
    Button GetDevices;
    @BindView(R.id.button_save_iot)
    Button SaveData;
    @BindView(R.id.button_aquisition_iot)
    Button StartAqui;
    @BindView(R.id.log_button_clear_iot)
    Button ClearLog;
    @BindView(R.id.log_scroll_view_iot)
    ScrollView logScrollViewIot;
    Unbinder unbinder_c;

    private TextView iotMessage;
    private StringBuilder logText = new StringBuilder();
    private huaweiIOT huaweiIot = new huaweiIOT();
    ArrayList<Float> lists = new ArrayList<>();//缓存衰减曲线


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.IOT_START:
                    StartAqui.setText("停止采集");
                    GetDevices.setEnabled(false);
                    SaveData.setEnabled(false);
                    break;
                case Constants.IOT_STOP:
                    StartAqui.setText("开始采集");
                    GetDevices.setEnabled(true);
                    SaveData.setEnabled(true);
                    break;
                case Constants.IOT_MESSAGE:
                    appendText(msg.obj.toString());
                    break;
                case Constants.IOT_GET_R_DATA:
                    lists = ChartDataAnalysis.getDesc(1f,0.001f,100);
                    ChartPlay.initChartView(lineChart_iot,5,4,true,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
                    ChartPlay.showLineChart(lineChart_iot,lists,true,true,"电压衰减曲线", Color.CYAN,1,1);
                    lineChart_iot.invalidate();
                    break;
            }
        }
    };

    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_log, container, false);
        unbinder_c = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder_c.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.fragmentState=2;
        mainActivity = (MainActivity) getActivity();
        iotMessage = getActivity().findViewById(R.id.log_text_view_iot);
        lineChart_iot=mainActivity.findViewById(R.id.chart_iot);
        //GetDevices = getActivity().findViewById(R.id.button_devices_iot);
        GetDevices.setOnClickListener(this);
        initChart();
        setBroadcastReceiver();
    }

    private void setBroadcastReceiver() {
        iotBroadcastReceiver = new IotBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.MY_BROAD);
        getActivity().registerReceiver(iotBroadcastReceiver, intentFilter);
    }

    private void initChart(){
        if(lists==null||lists.size()==0){
            lists=ChartDataAnalysis.getDesc(4f,0.001f,400);
            ChartPlay.initChartView(lineChart_iot,4,4,true,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
            ChartPlay.showLineChart(lineChart_iot,lists,true,true,"电压衰减曲线", Color.CYAN,1,1);
        }else{
            ChartPlay.initChartView(lineChart_iot,5,4,true,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
            ChartPlay.showLineChart(lineChart_iot,lists,true,true,"电压衰减曲线", Color.CYAN,1,1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_devices_iot:
                Intent intent = new Intent(getActivity(), IotDevicesActivity.class);
                Log.e(TAG,"startActivityForResult");
                startActivityForResult(intent, Constants.IOT_DEVICES_ACTIVITY_CODE);
                break;
            case R.id.button_aquisition_iot:
                if (StartAqui.getText().toString()
                        .equals("开始采集"))
                    butStartAquisitionClicked();
                else
                    butStopAquisitionClicked();
                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.IOT_DEVICES_ACTIVITY_CODE:
                Log.e(TAG,"1111选择设备成功，设备名称为");
                if (ParamSaveClass.iotDevice==null){
                    break;
                }
                Log.e(TAG,"选择设备成功，设备名称为");
                iotMessage.append("->选择设备成功，设备名称为"+ParamSaveClass.iotDevice.getDevice_name()+"\n");
                deviceIdIot.setText(ParamSaveClass.iotDevice.getDevice_id());
                deviceNameIot.setText(ParamSaveClass.iotDevice.getDevice_name());
                if (ParamSaveClass.iotDevice.getStatus().equals(Constants.DEVICE_ONLINE)) {
                    deviceStateIot.setTextColor(Color.GREEN);
                    deviceStateIot.setText(ParamSaveClass.iotDevice.getStatus());
                } else {
                    deviceStateIot.setTextColor(Color.RED);
                    deviceStateIot.setText(ParamSaveClass.iotDevice.getStatus());
                }
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.log_button_clear_iot, R.id.button_save_iot, R.id.button_aquisition_iot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.log_button_clear_iot:
                iotMessage.setText("");
                break;
            case R.id.button_save_iot:
                //todo
                appendText("lfhsdhfksdh");
                String url="https://iotda.cn-north-4.myhuaweicloud.com/v5/iot/5ecde13941f4fc02c747971c/devices/5ecde13941f4fc02c747971c_865057045123894/async-commands";
                break;
            case R.id.button_aquisition_iot:
                if (StartAqui.getText().toString()
                        .equals("开始采集"))
                    butStartAquisitionClicked();
                else
                    butStopAquisitionClicked();
                break;
        }
    }

    //开始采集
    private void butStartAquisitionClicked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String config=huaweiIot.setConfig(ParamSaveClass.configCommand,"TEM_Config");
                //"POST https://{endpoint}/v5/iot/{project_id}/devices/{device_id}/async-commands"下发异步设备命令
                //String url="https://iotda.cn-north-4.myhuaweicloud.com/v5/iot/5ecde13941f4fc02c747971c/devices/"+ParamSaveClass.iotDevice.getDevice_id()+"/async-commands";
                String url="https://iotda.cn-north-4.myhuaweicloud.com/v5/iot/640fdc6192edbc7ee93a3cf0/devices/"+ParamSaveClass.iotDevice.getDevice_id()+"/async-commands";
                int code= 0;
                Log.e(TAG, "config:"+config );
                try {
                    code = huaweiIot.sendCommand(url,config);
                    if(code==200){
                        Log.e(TAG, "run: send success!");
                        handler.obtainMessage(Constants.IOT_STOP).sendToTarget();
                        handler.obtainMessage(Constants.IOT_MESSAGE,"配置命令成功发送到云平台").sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: IOException" );
                    handler.obtainMessage(Constants.IOT_INTERNET).sendToTarget();
                    e.printStackTrace();
                }
                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SendCommand sendCommand=new SendCommand();

                //发送开始采集指令
                String command=huaweiIot.setConfig("start/0","TEM_Start0");
                //String url="https://iotda.cn-north-4.myhuaweicloud.com/v5/iot/5eed76e4da222a02eac70a5f/devices/"+ParamSaveClass.iotDevice.getDevice_id()+"/async-commands";
                code= 0;
                try {
                    code = huaweiIot.sendCommand(url,command);
                    if(code==200){
                        Log.e(TAG, "run: send success!");
                        handler.obtainMessage(Constants.IOT_START).sendToTarget();
                        handler.obtainMessage(Constants.IOT_MESSAGE,"开始采集命令成功发送到云平台").sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: IOException" );
                    handler.obtainMessage(Constants.IOT_INTERNET).sendToTarget();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //停止采集
    private void butStopAquisitionClicked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String command=huaweiIot.setConfig("stop","TEM_Stop");
                String url="https://iotda.cn-north-4.myhuaweicloud.com/v5/iot/5ecde13941f4fc02c747971c/devices/"+ParamSaveClass.iotDevice.getDevice_id()+"/async-commands";
                int code= 0;
                try {
                    code = huaweiIot.sendCommand(url,command);
                    if(code==200){
                        Log.e(TAG, "run: send success!");
                        handler.obtainMessage(Constants.IOT_STOP).sendToTarget();
                        handler.obtainMessage(Constants.IOT_MESSAGE,"停止采集命令成功发送到云平台").sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: IOException" );
                    handler.obtainMessage(Constants.IOT_INTERNET).sendToTarget();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void appendText(String s) {
        if(logScrollViewIot==null){
            return;
        }
        if (logText.length() > 5 * 1024) {
            logText = new StringBuilder();
            iotMessage.setText("");
        }
        mainActivity.logAppend("->" + s + "\n");
        logText.append("->").append(s).append("\n");
        iotMessage.append("->" + s + "\n");
        logScrollViewIot.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public  ArrayList<Float> stringToFloat(String[] str) throws NumberFormatException{
        ArrayList<Float> res=new ArrayList<>();
        for(int i=0;i<str.length;i++){
            if(str[i].length()>0){
                res.add(Float.parseFloat(str[i]));
            }
        }
        return res;
    }

    public class IotBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*
            显示当前接收的数据
             */
            //todo
            Log.e(TAG, "onReceive: -------------" );
            String s = intent.getStringExtra("TEMData");
            appendText(s);
            Gson gson=new Gson();
            IotMessage mes=gson.fromJson(s,IotMessage.class);//提供两个参数，分别是json字符串以及需要转换对象的类型。
            IotMessage.NotifyDataBean.BodyBean.ServicesBean.PropertiesBean b=mes.getNotify_data().getBody().getServices().get(0).getProperties();
            appendText("收到第"+b.getCheckBit()+"组数据,请及时查看！！！\r\n");
            appendText("衰减电压："+b.getVoltage());
            ParamSaveClass.voltage_group=b.getVoltage();
            ParamSaveClass.current_group=b.getCurrent();
            Log.e(TAG, "onReceive: "+ ParamSaveClass.voltage_group);
            Log.e(TAG, "onReceive: "+ParamSaveClass.current_group);
            String[] VoltageData=ParamSaveClass.voltage_group.split(",");
            //String[] CurrentData=ParamSaveClass.current_group.split(",");

            Log.e(TAG, "onReceive: "+ Arrays.toString(VoltageData));
            //Log.e(TAG, "onReceive: "+Arrays.toString(CurrentData));

            ArrayList<Float> Voltagetemp=stringToFloat(VoltageData);
            //ArrayList<Float> Currenttemp=stringToFloat(CurrentData);

            for(int i=0;i<Voltagetemp.size()-1;i++){
                lists.add(Voltagetemp.get(i)/4);
            }
            Log.e(TAG, "onReceive: "+lists);
            handler.obtainMessage(Constants.IOT_GET_R_DATA).sendToTarget();
        }
    }
}
