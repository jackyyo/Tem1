package com.example.tem.Measurement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tem.ChartDataAnalysis;
import com.example.tem.ChartPlay;
import com.example.tem.Constants;
import com.example.tem.ParamSaveClass;
import com.example.tem.R;
import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.tem.ChartDataAnalysis.CurNum0;
import static com.example.tem.ChartDataAnalysis.CurNum1;
import static com.example.tem.ParamSaveClass.ColorArr;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private LineChart chartone;
    private LineChart charttwo;

    @BindView(R.id.point_show1)
    TextView PointShow1;
    @BindView(R.id.line_show1)
    TextView LineShow1;
    @BindView(R.id.point_distence_show1)
    TextView PointDistence;
    @BindView(R.id.line_distence_show1)
    TextView LineDistence;
    @BindView(R.id.Current_show1)
    TextView CurrentShow1;
    @BindView(R.id.Voltage_show1)
    TextView VoltageShow1;
    @BindView(R.id.time_show1)
    TextView TimeShow1;
    @BindView(R.id.fre_show1)
    TextView FreShow1;
    @BindView(R.id.pre_dot)
    Button PreDot;
    @BindView(R.id.next_dot)
    Button NexDot;

    Unbinder unbinder_c;

    private DBHelper db;
    private HashMap allInformationMap;
    private int PointIndex=1;
    private int LineIndex=1;


    private static final String TAG ="ResultActivity" ;
    private ListView listView;
    private String filePath=null;
    private String path=Constants.DATA_DIRECTORY+"/"+ParamSaveClass.Location;
    private ArrayList<ArrayList<Float>> chart1=null;

    String location;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder_c.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        unbinder_c = ButterKnife.bind(this);
        initView();
        initChart();
    }

    //
    protected void initView(){
        chartone=(LineChart) findViewById(R.id.first_chart);
        charttwo=(LineChart) findViewById(R.id.second_chart);
        PreDot.setOnClickListener(this);
        NexDot.setOnClickListener(this);
    }
    private void initChart() {
        Intent intent = getIntent();
        location= intent.getStringExtra("location");
        db= new DBHelper(this,location);

        //更新图一
        if(chart1==null){
            chart1=new ArrayList<>();
            for(int i=0;i<9;i++) {
                chart1.add(new ArrayList<>());
            }
            int index = PointIndex;
            allInformationMap = db.getAllInformation(index + "", LineIndex + "");
            while (allInformationMap.size() != 0) {
                String data = (String) allInformationMap.get("data");
                Log.e(TAG,"data:"+data+"   data"+data.length());
                if(data.length()==0||data.length()==2)break;
                String[] str = data.split(",");
                Log.e(TAG,"str:"+ Arrays.toString(str));
                Log.e(TAG,"str:"+str.length);
                for (int j = 0; j < str.length; j++) {
                    chart1.get(j).add(Float.valueOf(str[j]));
                }
                index++;
                allInformationMap = db.getAllInformation(index + "", LineIndex + "");
            }
        }
        ChartPlay.initChartView(chartone,5,6,false,true,"","","V/I（µV/A）/点号(号)");//初始化图表(1V=10^6µV)
        if(chart1.get(0).size()!=0){
            ChartPlay.showLineChart(chartone,chart1.get(0),false,true,"", ColorArr[0],1,1);
            for(int i=1;i<chart1.size();i++){
                ChartPlay.addLine(chartone,chart1.get(i),false,true,"", ColorArr[i],1,1);
            }
        }

       /* String path=Constants.DATA_DIRECTORY+"/"+location+"/Result.txt";
        File file=new File(path);
        ArrayList<ArrayList<Float>> List1= ReadTxt(file);
        ChartPlay.initChartView(chartone,5,6,false,true,"","","V/I（µV/A）/点号(号)");//初始化图表(1V=10^6µV)
        ChartPlay.showLineChart(chartone,List1.get(0),false,true,"", ColorArr[0],1,1);
        for(int i=1;i<List1.size();i++){
            ChartPlay.addLine(chartone,List1.get(i),false,true,"", ColorArr[i],1,1);
        }
        chartone.invalidate();*/
        //更新图二
        Log.e(TAG, "location："+location);
        allInformationMap = db.getAllInformation(PointIndex+"",LineIndex+"");
        String file_name = (String)allInformationMap.get("file_name");
        ArrayList<ArrayList<Float>> list2=new ArrayList<>();
        if(allInformationMap.size()==0){
            Log.e(TAG, "数据库为空");
            list2.add(ChartDataAnalysis.getDesc(4f,0.1f,400));
            list2.add(ChartDataAnalysis.getDesc(4f,0.01f,400));
            list2.add(ChartDataAnalysis.getDesc(4f,0.001f,400));
            list2.add(ChartDataAnalysis.getDesc(4f,0.0001f,400));
        }else{
            queryData(location);
            list2=ChartDataAnalysis.binaryToDecimalTest(file_name);
        }
        Log.e(TAG, "file_name:"+file_name);
        Log.e(TAG, "list2.get(0)"+list2.get(0));
        Log.e(TAG, "list2.get(1)"+list2.get(1));
        Log.e(TAG, "list2.get(2)"+list2.get(2));
        Log.e(TAG, "list2.get(3)"+list2.get(3));
        ChartPlay.initChartView(charttwo,5,4,true,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
        if(list2.get(0)!=null||list2.get(0).size()!=0)ChartPlay.showLineChart(charttwo,list2.get(0),true,true,"小电流强信号", Color.CYAN,1,1);
        if(list2.get(1)!=null||list2.get(1).size()!=0)ChartPlay.addLine(charttwo,list2.get(1),true,true,"小电流弱信号", Color.LTGRAY,1,CurNum0);
        if(list2.get(2)!=null||list2.get(2).size()!=0)ChartPlay.addLine(charttwo,list2.get(2),true,true,"大电流强信号", Color.RED,1,1);
        if(list2.get(3)!=null||list2.get(3).size()!=0)ChartPlay.addLine(charttwo,list2.get(3),true,true,"大电流弱信号", Color.BLUE,1,CurNum1);
    }


    /**
     *  多测到数据读取
    	 * @param file 读取数据文件名
    **/
    protected  ArrayList<ArrayList<Float>> ReadTxt(File file){
        ArrayList<ArrayList<Float>> res=new ArrayList<>();
        Log.e(TAG, "111111111111:"+file.length());
        if(file.length()<90||!file.exists()){//一个图最少两个点，初定九条线 2*9*5
            Log.e(TAG, "1111111111112222222222:"+file.length());
            res=ChartDataAnalysis.getSin(4f,1.0f,24);
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
                Log.e("ReadTxT","strTmp"+strTmp);
                String [] array=strTmp.split(",");
                res.add(new ArrayList<>());
                for(int i=0;i<array.length;i++){
                    Float a=Float.parseFloat(array[i]);
                    res.get(index).add(a);
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
        return res;
    }

    /**
     *
    	 * @param workSpace 存储文件名
    **/

    private void queryData(String workSpace) {
        //20191205基本信息从数据库中获取
        db = new DBHelper(this,workSpace);
        //通过file_name获取到基本信息
        allInformationMap = db.getAllInformation(PointIndex+"",LineIndex+"");
        //将获取到的信息全部取出来，用变量存起来
        Log.e(TAG, "allInformationMap.get(point)"+allInformationMap.get("point"));
        Log.e(TAG, "allInformationMap.size()"+allInformationMap.size());

        PointShow1.setText(allInformationMap.get("point")+"");
        LineShow1.setText(allInformationMap.get("line")+"");
        PointDistence.setText(allInformationMap.get("point_distence")+"");
        LineDistence.setText(allInformationMap.get("line_distence")+"");
        CurrentShow1.setText(allInformationMap.get("current")+"");
        VoltageShow1.setText(allInformationMap.get("voltage")+"");
        TimeShow1.setText(allInformationMap.get("time")+"");
        FreShow1.setText(allInformationMap.get("fre")+"");
    }

    //上一个测点
    private void PreDotToDo(){
        PointIndex--;
        if(PointIndex<=0){
            Toast.makeText(getApplicationContext(), "已经是第一个测点", Toast.LENGTH_SHORT).show();
            PointIndex++;
        }else{
            db= new DBHelper(this,location);
            allInformationMap = db.getAllInformation(PointIndex+"",LineIndex+"");
            String file_name = (String)allInformationMap.get("file_name");
            Log.e(TAG, "file_name1:"+file_name);
            ArrayList<ArrayList<Float>> res=ChartDataAnalysis.binaryToDecimalTest(file_name);
            Log.e(TAG, "res:"+res);
            ChartPlay.initChartView(charttwo,5,4,true,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
            if(res.get(0)!=null||res.get(0).size()!=0)ChartPlay.showLineChart(charttwo,res.get(0),true,true,"小电流强信号", Color.CYAN,1,1);
            if(res.get(1)!=null||res.get(1).size()!=0)ChartPlay.addLine(charttwo,res.get(1),true,true,"小电流弱信号", Color.LTGRAY,1,CurNum0);
            if(res.get(2)!=null||res.get(2).size()!=0)ChartPlay.addLine(charttwo,res.get(2),true,true,"大电流强信号", Color.RED,1,1);
            if(res.get(3)!=null||res.get(3).size()!=0)ChartPlay.addLine(charttwo,res.get(3),true,true,"大电流弱信号", Color.BLUE,1,CurNum1);
            queryData(location);
        }
    }

    //下一个测点
    private void NextDotToDo(){
        PointIndex++;
        queryData(location);
        allInformationMap = db.getAllInformation(PointIndex+"",LineIndex+"");
        if(allInformationMap.size()==0){
            Toast.makeText(getApplicationContext(), "已经是最后一个测点", Toast.LENGTH_SHORT).show();
            PointIndex--;
        }
        db= new DBHelper(this,location);
        allInformationMap = db.getAllInformation(PointIndex+"",LineIndex+"");
        String file_name = (String)allInformationMap.get("file_name");
        Log.e(TAG, "file_name2:"+file_name);
        ArrayList<ArrayList<Float>> res=ChartDataAnalysis.binaryToDecimalTest(file_name);
        ChartPlay.initChartView(charttwo,5,4,true,true,"","","V/I（µV/A）/时间（µs）");//初始化图表
        if(res.get(0)!=null||res.get(0).size()!=0)ChartPlay.showLineChart(charttwo,res.get(0),true,true,"小电流强信号", Color.CYAN,1,1);
        if(res.get(1)!=null||res.get(1).size()!=0)ChartPlay.addLine(charttwo,res.get(1),true,true,"小电流弱信号", Color.LTGRAY,1,CurNum0);
        if(res.get(2)!=null||res.get(2).size()!=0)ChartPlay.addLine(charttwo,res.get(2),true,true,"大电流强信号", Color.RED,1,1);
        if(res.get(3)!=null||res.get(3).size()!=0)ChartPlay.addLine(charttwo,res.get(3),true,true,"大电流弱信号", Color.BLUE,1,CurNum1);
        queryData(location);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pre_dot:
                PreDotToDo();
                break;
            case R.id.next_dot:
                NextDotToDo();
                break;
            default:
                break;
        }
    }
}
