package com.example.tem;

import static com.example.tem.ParamSaveClass.Location;
import static java.lang.Character.LINE_SEPARATOR;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tem.Measurement.DBHelper;
import com.example.tem.Measurement.TemData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SettingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "SettingFragment";
    private MainActivity mainActivity;
    private TextView coilLength1;
    private EditText coilLengthEdit1;
    private TextView coilLength2;
    private EditText coilLengthEdit2;
    private Spinner[] s=new Spinner[12];

    @BindView(R.id.location)
    EditText Location;//测区信息
    @BindView(R.id.off_time_edit)
    EditText OffTime;//小电流关断时间
    @BindView(R.id.up_time_edit)
    EditText UpTime;//上升时间
    @BindView(R.id.off_time_edit1)
    EditText OffTime1;//大电流关断时间
    @BindView(R.id.up_time_edit1)
    EditText UpTime1;//上升时间

    Unbinder unbinder;

    private EditText PointNumber;
    private EditText LineNumber;
    private EditText DotPitch;
    private EditText LinePitch;
    private EditText PointIncrement;
    private EditText LineIncrement;

    private Button ReceiverMore;
    private Button StopMore;
    private Button Save;
    private Spinner spinner1 ;
    private Spinner spinner2 ;
    private Spinner spinner3 ;
    private Spinner spinner4 ;
    private Spinner spinner5 ;
    private Spinner spinner6 ;
    private Spinner spinner7 ;
    private Spinner spinner8 ;
    private Spinner spinner9 ;
    private Spinner spinner10 ;
    private Spinner spinner11 ;
    private Spinner spinner12 ;
    private Spinner spinner13 ;
    private Spinner spinner14 ;
    private Spinner spinner15 ;


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settingLayout= inflater.inflate(R.layout.fragment_setting, container, false);
        //unbinder_c = ButterKnife.bind(this, settingLayout);
        unbinder = ButterKnife.bind(this, settingLayout);//快速绑定Android视图中字段和方法的注解框架

        coilLength1=settingLayout.findViewById(R.id.text_coil_1);
        coilLengthEdit1=settingLayout.findViewById(R.id.edit_coil_1);
        coilLength2=settingLayout.findViewById(R.id.text_coil_6);
        coilLengthEdit2=settingLayout.findViewById(R.id.edit_coil_6);
        return settingLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        PointNumber=mainActivity.findViewById(R.id.Point_number_edit);
        LineNumber=mainActivity.findViewById(R.id.Line_number_edit);
        DotPitch=mainActivity.findViewById(R.id.Dot_pitch_edit);
        LinePitch=mainActivity.findViewById(R.id.line_pitch_edit);
        PointIncrement=mainActivity.findViewById(R.id.Point_increment_edit);
        LineIncrement=mainActivity.findViewById(R.id.line_increment_edit);

        Log.e(TAG,"ParamSaveClass1:"+Location.getText());

        Location.setText(ParamSaveClass.Location);//测区信息
        OffTime.setText(ParamSaveClass.CurrentOffTime+"");//小电流关断时间
        UpTime.setText(ParamSaveClass.CurrentUpTime+"");//上升时间
        OffTime1.setText(ParamSaveClass.CurrentOffTime1+"");//大电流关断时间
        UpTime1.setText(ParamSaveClass.CurrentUpTime1+"");//上升时间
        PointNumber.setText(ParamSaveClass.PointNum+"");
        LineNumber.setText(ParamSaveClass.LineNum+"");
        DotPitch.setText(ParamSaveClass.DotPit+"");
        LinePitch.setText(ParamSaveClass.LinePit+"");
        PointIncrement.setText(ParamSaveClass.PointIncrement+"");
        LineIncrement.setText(ParamSaveClass.LineIncrement+"");


        spinner1 = (Spinner)mainActivity.findViewById(R.id.spinner_increase);//小电流放大倍数
        spinner2 = (Spinner)mainActivity.findViewById(R.id.spinner_coil_accept);//接收线圈
        spinner3 = (Spinner)mainActivity.findViewById(R.id.spinner_coil_send);//发送线圈
        spinner4 = (Spinner)mainActivity.findViewById(R.id.spinner_curr_mol);//小电流
        spinner5 = (Spinner)mainActivity.findViewById(R.id.spinner_curr_mol1);//大电流
        spinner6 = (Spinner)mainActivity.findViewById(R.id.spinner_frequency);//小电流发送频率
        spinner7 = (Spinner)mainActivity.findViewById(R.id.spinner_frequency1);//大电流发送频率
        spinner8 = (Spinner)mainActivity.findViewById(R.id.spinner_magnification);//小电流放大倍数
        spinner9 = (Spinner)mainActivity.findViewById(R.id.spinner_magnification1);//大电流放大倍数
        spinner10 = (Spinner)mainActivity.findViewById(R.id.spinner_Sampling_rate);//小电流采样率
        spinner11 = (Spinner)mainActivity.findViewById(R.id.spinner_Sampling_rate1);//大电流采样率
        spinner12 = (Spinner)mainActivity.findViewById(R.id.spinner_Stacking_number);//叠加次数
        spinner13 = (Spinner)mainActivity.findViewById(R.id.spinner_Significant_number);//有效次数
        spinner14 = (Spinner)mainActivity.findViewById(R.id.spinner_increase1);//大电流放大倍数
        spinner15 = (Spinner)mainActivity.findViewById(R.id.spinner_Sampling);//接收机采样率

        s= new Spinner[]{ spinner1, spinner2, spinner3, spinner4,spinner5,spinner6,spinner7,spinner8,spinner9,spinner10,spinner11,spinner12,spinner13,spinner14,spinner15};
        setSpinnerTextSize(s);
        //setSpinnerListener();
        ReceiverMore=mainActivity.findViewById(R.id.coil_receive_more);
        StopMore=mainActivity.findViewById(R.id.coil_send_more);
        Save=mainActivity.findViewById(R.id.setting_save);
        ReceiverMore.setOnClickListener(this);
        StopMore.setOnClickListener(this);
        Save.setOnClickListener(this);
    }

    //spinner设置监听
    private void setSpinnerListener() {
        SpinnerListener spinnerListener = new SpinnerListener();
        spinner1.setOnItemSelectedListener(spinnerListener);
        spinner2.setOnItemSelectedListener(spinnerListener);
        spinner3.setOnItemSelectedListener(spinnerListener);
        spinner4.setOnItemSelectedListener(spinnerListener);
        spinner5.setOnItemSelectedListener(spinnerListener);
        spinner6.setOnItemSelectedListener(spinnerListener);
        spinner7.setOnItemSelectedListener(spinnerListener);
        spinner8.setOnItemSelectedListener(spinnerListener);
        spinner9.setOnItemSelectedListener(spinnerListener);
        spinner10.setOnItemSelectedListener(spinnerListener);
        spinner11.setOnItemSelectedListener(spinnerListener);
        spinner12.setOnItemSelectedListener(spinnerListener);
        spinner13.setOnItemSelectedListener(spinnerListener);
        spinner14.setOnItemSelectedListener(spinnerListener);
        spinner15.setOnItemSelectedListener(spinnerListener);
    }

    //实现监听spinner的类，获取到spinner选择的值。
    class SpinnerListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    //设置Spinner字体格式
    public void setSpinnerTextSize(Spinner[] s){
        ArrayList<String[]> arraySpinner = new ArrayList<>();
        arraySpinner.add(getResources().getStringArray(R.array.spinner_increase));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_coil_accept));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_coil_send));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_curr_mol));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_curr_mol1));

        arraySpinner.add(getResources().getStringArray(R.array.spinner_frequency));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_frequency));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_magnification));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_magnification));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_Sampling_rate));

        arraySpinner.add(getResources().getStringArray(R.array.spinner_Sampling_rate));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_Stacking_number));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_Significant_number));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_increase1));
        arraySpinner.add(getResources().getStringArray(R.array.spinner_Sampling));


        for(int i=0;i<arraySpinner.size();i++){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.personal_spinner,
                    arraySpinner.get(i));
            s[i].setAdapter(adapter);
            s[i].setOnItemSelectedListener(this);
               /* String a=(String)s[i].getSelectedItem();
                mainActivity.displayToast("1111111111");
                if(a=="自制圆形"){
                    textLength.setText("直径：");
                    mainActivity.displayToast("cool");
                }*/

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.setting_save:
                /*if (mainActivity.getBluetoothState() != BluetoothChatService.STATE_CONNECTED){
                    mainActivity.displayToast("蓝牙未连接");
                    return;
                }*/
                butSettingSaveClicked();
                break;
            case R.id.coil_receive_more:
                butReceiveMore();
                break;
            case R.id.coil_send_more:
                butSendMore();
                break;
            default:
                break;
        }
    }

    private void butReceiveMore() {

        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.show();  //注意：必须在window.setContentView之前show
        Window window = dialog.getWindow();
        window.setContentView(R.layout.butreceivemore);
        TextView yesButton = (TextView) window.findViewById(R.id.tv_alert_dialog_yes);
        //点击确定按钮让对话框消失
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void butSendMore(){
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.show();  //注意：必须在window.setContentView之前show
        Window window = dialog.getWindow();
        window.setContentView(R.layout.butsendmore);
        TextView yesButton = (TextView) window.findViewById(R.id.tv_alert_dialog_yes);
        //点击确定按钮让对话框消失
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void butSettingSaveClicked(){
        //todo
        // 保存数据
        //String cmd="";
        Log.e(TAG,"ParamSaveClass.Current:"+ParamSaveClass.CurrentOffTime);
        ParamSaveClass.Location=Location.getText().toString();
        //ParamSaveClass.CurrentSendFre=Integer.parseInt(SendFre.getText().toString());
        ParamSaveClass.CurrentOffTime=Integer.parseInt(OffTime.getText().toString());
        ParamSaveClass.CurrentUpTime=Integer.parseInt(UpTime.getText().toString());
        //ParamSaveClass.CurrentSendFre1=Integer.parseInt(SendFre1.getText().toString());
        ParamSaveClass.CurrentOffTime1=Integer.parseInt(OffTime1.getText().toString());
        ParamSaveClass.CurrentUpTime1=Integer.parseInt(UpTime1.getText().toString());
        ParamSaveClass.PointNum=Integer.parseInt(PointNumber.getText().toString());
        ParamSaveClass.LineNum=Integer.parseInt(LineNumber.getText().toString());
        ParamSaveClass.DotPit=Integer.parseInt(DotPitch.getText().toString());
        ParamSaveClass.LinePit=Integer.parseInt(LinePitch.getText().toString());
        ParamSaveClass.PointIncrement=Integer.parseInt(PointIncrement.getText().toString());
        ParamSaveClass.LineIncrement=Integer.parseInt(PointIncrement.getText().toString());
/*
        ParamSaveClass.Current=Integer.parseInt(s[0].getSelectedItem().toString().equals("自动")?"1":s[0].getSelectedItem().toString());
        ParamSaveClass.Current1=Integer.parseInt(s[3].getSelectedItem().toString().substring(0,s[3].getSelectedItem().toString().length()-1));
        ParamSaveClass.Amplification=Integer.parseInt(s[4].getSelectedItem().toString().substring(0,s[4].getSelectedItem().toString().length()-1));*/
        Log.e(TAG,"ParamSaveClass.Current:"+ParamSaveClass.Current);

        //记录到数据库
        /*DBHelper dbHelper = new DBHelper(getContext(),ParamSaveClass.Location);
        TemData td=new TemData(ParamSaveClass.PointNum,ParamSaveClass.LineNum,
                ParamSaveClass.DotPit,ParamSaveClass.LinePit,
                );
        dbHelper.InsertData(td);*/

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-HH-mm");
        //获取当前日期，在配置命令中发送到下面去，让嵌入式建立文件
        String dateNowStr = sdf.format(d);
        Log.e(TAG,"dateNowStr:"+dateNowStr);
        long result = Math.round (5*Math.pow(10,6)/ParamSaveClass.CurrentSendFre);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!p频率控制字
        long result1 = Math.round (5*Math.pow(10,6)/ParamSaveClass.CurrentSendFre1);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!p频率控制字
        //用2进制表示出来的
        String freControlID = Long.toBinaryString(result/4*4);
        String freControlID1 = Long.toBinaryString(result1/4*4);
        double [] arr={625,237.5,62.5,25,6.25,2.5,0.625,0.25};
        for(int i=0;i<8;i++){
            long r = Math.round (5*Math.pow(10,6)/arr[i]);
            String ID = Long.toBinaryString(r/4*4);
            Log.e(TAG,"ID:"+ID);
        }
        Log.e(TAG,"ParamSaveClass.Current:"+ParamSaveClass.Current);
        //叠加次数
        int time=(int)(ParamSaveClass.Overlay*ParamSaveClass.CurrentSendFre);
        int time1=(int)(ParamSaveClass.Overlay*ParamSaveClass.CurrentSendFre1);
        int a=1;
        int b=1;
        if(!ParamSaveClass.Amplification.equals("自动")&&!ParamSaveClass.Amplification1.equals("自动")){
            a=Integer.parseInt(ParamSaveClass.Amplification);
            b=Integer.parseInt(ParamSaveClass.Amplification);
            if(a<25&&b<25){
                a=1;
                b=1;
                ParamSaveClass.WeakSignal=25;
                ParamSaveClass.WeakSignal1=25;
            }else{
                if(a>=25){
                    a=a/25;
                    ParamSaveClass.WeakSignal=25*a;
                }
                if(b>=25){
                    b=b/25;
                    ParamSaveClass.WeakSignal1=25*b;
                }
            }
            ParamSaveClass.a=a;
            ParamSaveClass.b=b;
        }
        //接收机采集点数除以4后下发
        //接收机小电流放大倍数/接收机大电流放大倍数/小电流(0)/叠加次数/发送频率控制字/放大倍数/采样率/大电流(1)/叠加次数/发送频率控制字/放大倍数/采样率/TEMI/TEMU/当前时间
        //小电流(0)/叠加次数/发送频率控制字/放大倍数/采样率/大电流(1)/叠加次数/发送频率控制字/放大倍数/采样率/TEMI/TEMU/当前时间

        //Amplification/Overlay/Current/CurrentSendFre/CurrentOffTime/CurrentUpTime/Current1/CurrentSendFre1/CurrentOffTime1/CurrentUpTime1/当前时间
        //cmd="configT/小电流(0)/叠加次数/发送频率控制字/放大倍数/采样率/大电流(1)/叠加次数/发送频率控制字/放大倍数/采样率/TEMI/TEMU/当前时间";
        String cmd="configT/" + 0 +"/"+time+"/"+freControlID
                +"/" +ParamSaveClass.CurrentMagnification+"/"+ ParamSaveClass.CurrentSampling+ "/"+1 +"/"+time1+"/"+"/"+freControlID1
                +"/" +ParamSaveClass.CurrentMagnification1+"/"+ ParamSaveClass.CurrentSampling1+ "/"+"TEMI"+"/"+"TEMU"+"/"+ dateNowStr+"\r\n";
        Log.e(TAG,"配置："+cmd);
        mainActivity.sendCommand(cmd);
        String log ="配置命令已发送!\r\n";
        mainActivity.logAppend(log);
        mainActivity.displayToast("配置命令已更新");
        SetConfigFile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spinner_increase://接收小电流放大倍数
                String [] s1=getResources().getStringArray(R.array.spinner_increase);
                ParamSaveClass.Amplification = s1[position];
                Log.e(TAG,"spinner_increase:"+s1[position]);
                break;
            case R.id.spinner_increase1://接收大电流放大倍数
                String [] s14=getResources().getStringArray(R.array.spinner_increase1);
                ParamSaveClass.Amplification1 = s14[position];
                Log.e(TAG,"spinner_increase1:"+s14[position]);
                break;
            case R.id.spinner_coil_accept://接收线圈
                if(position==3){
                    coilLength1.setText("线圈直径(m)：");
                    coilLengthEdit1.setText("0.8");
                }
                if(position==4){
                    coilLength1.setText("线圈边长(m)：");
                    coilLengthEdit1.setText("0.8");
                }
                break;
            case R.id.spinner_coil_send://发送线圈
                if(position==3){
                    coilLength2.setText("线圈直径(m)：");
                    coilLengthEdit2.setText("0.8");
                }
                if(position==4){
                    coilLength2.setText("线圈边长(m)：");
                    coilLengthEdit2.setText("0.8");
                }
                break;
            case R.id.spinner_curr_mol://小电流
                String [] s2=getResources().getStringArray(R.array.spinner_curr_mol);
                ParamSaveClass.Current = Integer.parseInt(s2[position].substring(0,s2[position].length()-1));
                break;
            case R.id.spinner_curr_mol1://大电流
                String [] s3=getResources().getStringArray(R.array.spinner_curr_mol);
                ParamSaveClass.Current1 = Integer.parseInt(s3[position].substring(0,s3[position].length()-1));
                break;
            case R.id.spinner_frequency://小电流发送频率
                String [] s4=getResources().getStringArray(R.array.spinner_frequency);
                ParamSaveClass.CurrentSendFre = Double.parseDouble(s4[position].substring(0,s4[position].length()-2));
                break;
            case R.id.spinner_frequency1://大电流发送频率
                String [] s5=getResources().getStringArray(R.array.spinner_frequency);
                ParamSaveClass.CurrentSendFre1 = Double.parseDouble(s5[position].substring(0,s5[position].length()-2));
                break;
            case R.id.spinner_magnification://小电流放大倍数
                String [] s6=getResources().getStringArray(R.array.spinner_magnification);
                Log.e(TAG,"position:"+position+"  "+ Arrays.toString(s6));
                ParamSaveClass.CurrentMagnification = Double.parseDouble(s6[position]);
                break;
            case R.id.spinner_magnification1://大电流放大倍数
                String [] s7=getResources().getStringArray(R.array.spinner_magnification);
                ParamSaveClass.CurrentMagnification1 =  Double.parseDouble(s7[position]);
                break;
            case R.id.spinner_Sampling_rate://小电流采样率
                String [] s8=getResources().getStringArray(R.array.spinner_Sampling_rate);
                ParamSaveClass.CurrentSampling = s8[position];
                break;
            case R.id.spinner_Sampling_rate1://大电流采样率
                String [] s9=getResources().getStringArray(R.array.spinner_Sampling_rate);
                ParamSaveClass.CurrentSampling1 = s9[position];
                break;
            case R.id.spinner_Stacking_number://叠加次数
                String [] s10=getResources().getStringArray(R.array.spinner_Stacking_number);
                ParamSaveClass.Overlay = Integer.parseInt(s10[position]);
                break;
            case R.id.spinner_Significant_number://有效次数
                String [] s11=getResources().getStringArray(R.array.spinner_Significant_number);
                ParamSaveClass.Effective = Integer.parseInt(s11[position]);
                break;
            case R.id.spinner_Sampling://接收采样率
                String [] s12=getResources().getStringArray(R.array.spinner_Sampling);
                ParamSaveClass.sampleIndex=position;
                ParamSaveClass.Sample = Double.parseDouble(s12[position].substring(0,s12[position].length()-1));
                break;
            default:
                break;
        }
    }

    //
    private void SetConfigFile(){
        String filePath = Constants.DATA_DIRECTORY+"/"+Location+"/配置信息.txt";
        File f = new File(filePath);
        if(f.exists()){
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(f, true));
            //更新配置信息
            String str="接收设置:\r\n"+
                    "小电流放大倍数："+ParamSaveClass.Amplification+"\n"+
                    "大电流放大倍数："+ParamSaveClass.Amplification+"\n"+
                    "一级叠加周期（s）："+ParamSaveClass.Overlay+"\n"+
                    "二级叠加次数:"+ParamSaveClass.Effective+"\n"+
                    /*"接收线圈:\r\n"+
                    "接收线圈:"+ParamSaveClass.Effective+"\n"+
                    "线圈边长:"+ParamSaveClass.Effective+"\n"+
                    "线圈带宽(kHz):"+ParamSaveClass.Effective+"\n"+
                    "线圈匝数:"+ParamSaveClass.Effective+"\n"+*/
                    "发送设置:\r\n"+
                    "小电流:"+ParamSaveClass.Current+"\n"+
                    "发送频率:"+ParamSaveClass.CurrentSendFre+"\n"+
                    "放大倍数:"+ParamSaveClass.CurrentMagnification+"\n"+
                    "采样率:"+ParamSaveClass.CurrentSampling+"\n"+
                    "关断时间(µs):"+ParamSaveClass.CurrentOffTime+"\n"+
                    "上升时间(µs):"+ParamSaveClass.CurrentUpTime+"\n"+
                    "大电流:"+ParamSaveClass.Current1+"\n"+
                    "发送频率:"+ParamSaveClass.CurrentSendFre1+"\n"+
                    "放大倍数:"+ParamSaveClass.CurrentMagnification1+"\n"+
                    "采样率:"+ParamSaveClass.CurrentSampling1+"\n"+
                    "关断时间(µs):"+ParamSaveClass.CurrentOffTime1+"\n"+
                    "上升时间(µs):"+ParamSaveClass.CurrentUpTime1+"\n"+
                  /*  "发送线圈:\r\n"+
                    "发送线圈:"+ParamSaveClass.Effective+"\n"+
                    "线圈边长:"+ParamSaveClass.Effective+"\n"+
                    "线圈带宽(kHz):"+ParamSaveClass.Effective+"\n"+
                    "线圈匝数:"+ParamSaveClass.Effective+"\n"+*/
                    "测点设置:\r\n"+
                    "测区信息:"+ParamSaveClass.Location+"\n"+
                    "点号:"+ParamSaveClass.PointNum+"\n"+
                    "线号:"+ParamSaveClass.LineNum+"\n"+
                    "点距:"+ParamSaveClass.DotPit+"\n"+
                    "线距:"+ParamSaveClass.LinePit+"\n"+
                    "点号增量:"+ParamSaveClass.PointIncrement+"\n"+
                    "线号增量:"+ParamSaveClass.LineIncrement+"\n";

            writer.write(str);
            writer.write(LINE_SEPARATOR);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
