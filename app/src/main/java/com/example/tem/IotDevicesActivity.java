package com.example.tem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tem.WifiTest.WifiUtil;
import com.example.tem.iot.mycommunication.huaweiIOT;
import com.example.tem.iot.responseObject.Devices;

import java.util.ArrayList;
import java.util.List;

public class IotDevicesActivity extends AppCompatActivity {

    private static final String TAG ="IOTActivity" ;
    private ListView listView;
    List<Devices.DevicesBean> fileList=new ArrayList<>();
    private huaweiIOT HuaweiIOT ;
    private String ssid="";

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.IOT_DEVICES:
                    fileList=HuaweiIOT.getDevices();
                    if (fileList==null){
                        Toast.makeText(IotDevicesActivity.this, "获取设备列表失败，请检查是否可正常上网", Toast.LENGTH_SHORT).show();
                    }
                    else if (fileList.isEmpty()) {
                        Toast.makeText(IotDevicesActivity.this, "没有设备", Toast.LENGTH_SHORT).show();
                    } else {
                        IotDevicesActivity.FileAdapter adapter = new IotDevicesActivity.FileAdapter(
                                IotDevicesActivity.this, R.layout.file_list_item, fileList);//将文件列表fileList添加到listView中
                        listView.setAdapter(adapter);
                    }
                    break;
                case Constants.IOT_INTERNET:
                    Toast.makeText(IotDevicesActivity.this, "获取设备列表失败，请检查是否可正常上网", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_devices);
        listView = (ListView) findViewById(R.id.iot_devices_list_view);
        HuaweiIOT = new huaweiIOT(handler);
        Intent data = new Intent();
        setResult(Constants.IOT_DEVICES_ACTIVITY_CODE, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HuaweiIOT.getdevices();
                    Log.e(TAG, "run: aaaaaaaaaaaaaa"+fileList.size());
                    //handler.obtainMessage(Constants.IOT_DEVICES).sendToTarget();
                } catch (Exception e) {
                    handler.obtainMessage(Constants.IOT_INTERNET).sendToTarget();
                    e.printStackTrace();
                    finish();
                }
            }
        }).start();
        Toast.makeText(IotDevicesActivity.this, "获取设备列表中...", Toast.LENGTH_SHORT).show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ParamSaveClass.iotDevice= fileList.get(i);
                Log.e(TAG, "onItemClick: +"+i+"   "+ParamSaveClass.iotDevice.getDevice_id());
                ssid=ParamSaveClass.iotDevice.getDevice_id();

                finish();
            }
        });
    }





    public class FileAdapter extends ArrayAdapter<Devices.DevicesBean> {

        private int resourceId;
        public FileAdapter(Context context, int resource, List<Devices.DevicesBean> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @NonNull
        @Override
        @TargetApi(24)
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.e(TAG, "getView: ++++++++++++++++++++++++++++++++++" );
            Devices.DevicesBean device = getItem(position);
            String fileName = device.getDevice_name();
            // long fileSize=file.length()/1024;
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.file_info);
            textView.setText(fileName);
            ((ImageView) view.findViewById(R.id.file_image)).setImageResource(R.drawable.iot_device);
            return view;
        }
    }
}
