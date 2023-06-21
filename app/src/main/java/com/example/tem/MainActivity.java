package com.example.tem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tem.WifiTest.ConnectThread;
import com.example.tem.WifiTest.ListenerThread;
import com.example.tem.iot.IotMonitorService;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import sun.applet.Main;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public View logLayout;
    private ImageView logImage;
    public View controllerLayout;
    private ImageView controllerImage;
    public View settingLayout;
    private ImageView settingImage;
    public View measurementLayout;
    private ImageView measurementImage;
    public String logContent="";
    private TextView logTextView;

    private MenuItem itemConnect;

    private LogFragment logFragment;
    private ControllerFragment controllerFragment;
    private MeasurementFragment measurementFragment;
    private SettingFragment settingFragment;
    private NoneFileFragment noneFileFragment;

    private FragmentManager fragmentManager;

    public static final String TAG = "MainActivity";
    public static final int REQUEST_CONNECT_DEVICE = 1;
    private BluetoothChatService mChatService;

    private String currentDeviceName;

    public static boolean getip=true;
    public static int wifiClientThreadState=0;

    public static int fragmentState=0;//用来判断当前页面是否为controllerframent，防止更新文件接收进度信息时程序崩溃
    private BluetoothAdapter mBluetoothAdapter;


    //test
    public static ConnectThread connectThread;
    private ListenerThread listenerThread;
    private static final int  PORT = 4321;
    private static final int  PORT1 = 54321;


    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothChatService.MESSAGE_BT_READ:
                    Bundle data = msg.getData();
                    String reply = data.getString("BTdata");
                    Log.e("Length",String.valueOf(reply.length()) + "  " + reply);
                    String[] infoToDispaly = reply.split("\r\n");
                    if (reply.contains("filesize")){
                        int firstIndex, lastIndex;
                        firstIndex = reply.indexOf("filesize");
                        lastIndex = reply.indexOf("\r\n", firstIndex);
                        String filesize = reply.substring(firstIndex+8, lastIndex);
                        Log.e(TAG, "handleMessage: bt_read: "+filesize);
                        //networkFragment.setFileSize(filesize);
                        break;
                    }
                    for(int i = 0; i<infoToDispaly.length; i++){
                        logAppend(currentDeviceName+": "+infoToDispaly[i]+"\r\n");//显示相应信息
                        Log.e(TAG, "handleMessage: bt_read: "+ Arrays.toString(infoToDispaly));
                    }
                    break;
                case BluetoothChatService.MESSAGE_DEVICE_NAME:
                    itemConnect.setEnabled(true);
                    itemConnect.setTitle("断开");
                    break;
                case BluetoothChatService.MESSAGE_STATE_CHANGE:
                    //更新连接状态显示

                    switch (msg.arg1){

                        case BluetoothChatService.STATE_CONNECTING:
                            setSubtitle("正在连接...");
                            break;
                        case BluetoothChatService.STATE_NOT_CONNECTED:
                            itemConnect.setEnabled(true);
                            itemConnect.setTitle("连接");
                            setSubtitle("未连接");
                            /*if (networkFragment != null) {
                                networkFragment.disableAllView();
                                if (networkFragment.getRecieveState())
                                    networkFragment.setViewEnabled(R.id.network_button_get_data, true);
                            }*/
                            break;
                        case BluetoothChatService.STATE_CONNECTED:
                            setSubtitle("已连接："+ currentDeviceName);
                            /*if (networkFragment != null) {
                                networkFragment.setViewEnabled(R.id.network_checkbox, true);
                                //networkFragment.setViewEnabled(R.id.network_button_start_server, true);
                                networkFragment.setViewEnabled(R.id.network_button_get_data, true);
                                networkFragment.setViewEnabled(R.id.network_button_edit, true);
                                if (networkFragment.getCheckBoxState())
                                    networkFragment.setViewEnabled(R.id.network_edit_server_port, true);
                            }*/
                    }
                    break;
                case BluetoothChatService.MESSAGE_TOAST:
                    String string = msg.getData().getString(BluetoothChatService.TOAST);
                    displayToast(string);
                    break;
                case Constants.DEVICE_CONNECTING:
                    setTabDisplay(0);
                    break;
                    //test
                case Constants.DEVICE_CONNECTING1:
                    Toast.makeText(MainActivity.this,"阻塞",Toast.LENGTH_SHORT).show();
                    connectThread = new ConnectThread(MainActivity.this, listenerThread.getSocket(),mHandler);
                    connectThread.start();
                    break;
                case Constants.DEVICE_CONNECTED1:
                    logAppend("设备连接成功\r\n");
                    break;
                case Constants.SEND_MSG_SUCCSEE1:
                    logAppend("发送消息成功:" + msg.getData().getString("MSG")+"\r\n");
                    break;
                case Constants.SEND_MSG_ERROR1:
                    logAppend("发送消息失败:" + msg.getData().getString("MSG"));
                    break;
                case Constants.GET_MSG1:
                    logAppend("收到消息:" + msg.getData().getString("MSG"));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getpermissions();
        setContentView(R.layout.activity_main);
        //开启IOT数据接收监听服务
        // todo
        Intent startIntent=new Intent(this, IotMonitorService.class);
          startService(startIntent);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//得到蓝牙适配器
        fragmentManager = getSupportFragmentManager();//获取FragmentManager，在活动中而已使用此方法获取
        initView();
        setTabDisplay(0);

    }



    private void getpermissions()//运行时权限授权
    {
        String[] permission=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION};
        for (int i=0;i<permission.length;i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 6.0) {
                    ActivityCompat.requestPermissions(MainActivity.this, permission, 12);
                }
            }
        }
    }

    private void initView(){
        controllerLayout=findViewById(R.id.controller_layout);
        controllerImage=findViewById(R.id.controller_image);

        settingLayout=findViewById(R.id.setting_layout);
        settingImage=findViewById(R.id.setting_image);

        logLayout=findViewById(R.id.log_layout);
        logImage=findViewById(R.id.log_image);

        measurementLayout=findViewById(R.id.measurement_layout);
        measurementImage=findViewById(R.id.measurement_image);

        controllerLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        logLayout.setOnClickListener(this);
        measurementLayout.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.controller_layout:
                setTabDisplay(0);
                break;
            case R.id.setting_layout:
                setTabDisplay(1);
                break;
            case R.id.log_layout:
                setTabDisplay(2);
                break;
            case R.id.none_file_fragment_text_view:
            case R.id.measurement_layout:
                setTabDisplay(3);
                break;
            default:
                break;
        }
    }

    protected void setTabDisplay(int index){
        clearSelection();
        FragmentTransaction transaction = fragmentManager.beginTransaction();//开启一个事务，通过beginTransaction（）开启
        switch (index){
            case 0:
                controllerImage.setImageResource(R.drawable.ic_tabber_control_pressed);//更改碎片切换按钮颜色
                if(controllerFragment == null)
                    controllerFragment = new ControllerFragment();
                transaction.replace(R.id.fragment_layout, controllerFragment);//向容器内调价或替换碎片，一般使用replace（）方法实现，需要传入容器的id和待添加的碎片实例
                Log.e(TAG, "onClick:controllerfragment ");
                break;
            case 1:
                settingImage.setImageResource(R.drawable.ic_tabbar_measure_pressed);
                if(settingFragment == null)
                    settingFragment = new SettingFragment();
                transaction.replace(R.id.fragment_layout, settingFragment);
                break;
            case 2:
                logImage.setImageResource(R.drawable.ic_tabber_data_pressed);
                if(logFragment == null)
                    logFragment = new LogFragment();
                transaction.replace(R.id.fragment_layout, logFragment);
                break;
            case 3:
                measurementImage.setImageResource(R.drawable.ic_tabber_result_pressed);
                if(measurementFragment == null)
                    measurementFragment = new MeasurementFragment();
                transaction.replace(R.id.fragment_layout, measurementFragment);
                Log.e(TAG, "setTabDisplay: 3 -------");
                break;
            case 4:
                measurementImage.setImageResource(R.drawable.ic_tabber_result_pressed);
                if (noneFileFragment == null)
                    noneFileFragment = new NoneFileFragment();
                Log.e(TAG, "setTabDisplay: 4 -------"+noneFileFragment.toString());
                transaction.replace(R.id.fragment_layout, noneFileFragment);
                break;
            default:
                break;
        }
        transaction.commit();//提交事务，调用commit（）方法来完成
    }

    private void clearSelection(){
        controllerImage.setImageResource(R.drawable.ic_tabber_control);
        settingImage.setImageResource(R.drawable.ic_tabbar_measure);
        logImage.setImageResource(R.drawable.ic_tabber_data);
        measurementImage.setImageResource(R.drawable.ic_tabber_result);
    }

    public void displayToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mChatService == null)
            mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatService != null)
            mChatService.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);//第一个参数为菜单布局文件路径
        itemConnect = menu.findItem(R.id.menu_connect_item);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) //判断弹出式菜单，菜单的响应事件，为处理菜单被选中运行后的事件处理。
    {
        if(item.getTitle().toString().equals("连接")) {
            item.setEnabled(false);
            Intent intent = new Intent(this, ChooseDeviceActivity.class);
            startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
        }
        else {
            mChatService.stop();
            logAppend("->bluetooth disconnected!\n");
            item.setTitle("连接");
        }
        return true;
    }

    public void logAppend(String string){
        logContent += string;
        WifiClientThread.SetResetFile(string);
        if(controllerFragment == null)
            controllerFragment = new ControllerFragment();
        logTextView =(TextView) findViewById(R.id.log_text_view);
        if(logTextView != null){
            if (logContent.length()>1024*5){
                //writeLog(logContent,ParamSaveClass.logText);
                logTextView.setText("");
                logContent = string;
            }
            logTextView.append(string);
        }
        ScrollView scrollView=findViewById(R.id.log_scroll_view);
        if(fragmentState==0)
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
    }

    public void setSubtitle(String subtitle)//显示蓝牙连接状态
    {
        ActionBar actionBar = MainActivity.this.getSupportActionBar();
        //Log.e(TAG, "setSubtitle: actionBar = "+actionBar);
        if(actionBar != null)
            actionBar.setSubtitle(subtitle);
    }

    public void sendCommand(String cmd)//发送命令
    {
        if(mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
            mChatService.write(cmd.getBytes());//调用BluetoothChatService类中的write方法
        else
            displayToast("蓝牙未连接");//调用Toast提醒
    }

    public int getBluetoothState()//得到状态指示常量
    {
        return mChatService.getState();
    }


   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) //在被startActivityForResult（）方法启动的活动销毁后调用
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK) {
                    String deviceAddress = data.getExtras().getString(ChooseDeviceActivity.DEVICE_ADDRESS);
                    currentDeviceName = data.getExtras().getString(ChooseDeviceActivity.DEVICE_NAME);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                    mChatService.connect(device);
                    currentDeviceName = device.getName();
                }
                if(resultCode == Activity.RESULT_CANCELED)
                    itemConnect.setEnabled(true);
                break;
        }
    }
}
