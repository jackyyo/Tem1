package com.example.tem;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;

public class ChartPlay {
    private static final String TAG = "ChartPlay";

    public static void init1(LineChart chart, int countX, int countY, String unitX, String unitY, String description) {//图表对象，标签数量，x轴单位,y轴单位
        chart.setDrawBorders(true);//显示边界
        chart.setDrawGridBackground(false);//不显示图表网格线
        chart.getDescription().setText(description);
        (chart.getLegend()).setTextSize(7f);//设置图例文字大小
        XAxis xaxis=chart.getXAxis();//设置x轴
        xaxis.setLabelCount(countX,true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴底端显示
        xaxis.setDrawGridLines(true);//不显示x轴网格线
        xaxis.setDrawLabels(true);
        //xaxis.setAxisMinimum(1);
        //xaxis.setAxisMaximum(4);

        xaxis.enableGridDashedLine(10.4f, 10.4f, 0f);
        xaxis.setValueFormatter(new IAxisValueFormatter() {//设置x轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ("1E"+String.format("%.0f",value)).concat(unitX);
            }
        });
        chart.getAxisRight().setEnabled(false);//右侧y轴不显示
        YAxis leftYAxis = chart.getAxisLeft();//设置y轴
        leftYAxis.setLabelCount(countY,true);
        leftYAxis.setDrawGridLines(true);//y轴网格线不显示
        //leftYAxis.setAxisMinimum(-6);
        //leftYAxis.setAxisMaximum(1);

        //leftYAxis.setAxisMinimum(-7);
        //leftYAxis.setAxisMaximum(1);
       /* xAxis.setDrawGridLines(true);   					// 设置是否画网格线
        xAxis.setGridLineWidth(2);       					// 线宽
        xAxis.setGridColor(Color.RED);            			// 颜色
        xAxis.setGridDashedLine(DashPathEffect effect); */    // 虚线
        leftYAxis.enableGridDashedLine(11f, 11f, 0f);
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {//设置y轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value<=-5){
                    if(value==-5)return ("0").concat(unitX);
                    return ("-1E"+String.format("%.0f",-value-10)).concat(unitX);
                }else{
                    return ("1E"+String.format("%.0f",value)).concat(unitX);
                }
            }
        });
    }

    public static void init(LineChart chart, int countX, int countY,boolean b, String unitX, String unitY, String description) {//图表对象，标签数量，x轴单位,y轴单位
        chart.setDrawBorders(true);//显示边界
        chart.setDrawGridBackground(false);//不显示图表网格线
        chart.getDescription().setText(description);
        (chart.getLegend()).setTextSize(7f);//设置图例文字大小
        XAxis xaxis=chart.getXAxis();//设置x轴
        xaxis.setLabelCount(countX,true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴底端显示
        xaxis.setDrawGridLines(true);//不显示x轴网格线
        xaxis.setDrawLabels(true);
        if(b){
            xaxis.setAxisMinimum(0);//0
            xaxis.setAxisMaximum(15);//4
        }else{
            xaxis.setAxisMinimum(0);//0
            xaxis.setAxisMaximum(4);//4
        }


        xaxis.enableGridDashedLine(10.4f, 10.4f, 0f);
        xaxis.setValueFormatter(new IAxisValueFormatter() {//设置x轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (String.format("%.0f",value)).concat(unitX);
            }
        });
        chart.getAxisRight().setEnabled(false);//右侧y轴不显示
        YAxis leftYAxis = chart.getAxisLeft();//设置y轴
        leftYAxis.setLabelCount(countY,true);
        leftYAxis.setDrawGridLines(true);//y轴网格线不显示
        leftYAxis.setAxisMinimum(-7);//-7
        leftYAxis.setAxisMaximum(2);//2
       /* xAxis.setDrawGridLines(true);   					// 设置是否画网格线
        xAxis.setGridLineWidth(2);       					// 线宽
        xAxis.setGridColor(Color.RED);            			// 颜色
        xAxis.setGridDashedLine(DashPathEffect effect); */    // 虚线
        leftYAxis.enableGridDashedLine(11f, 11f, 0f);
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {//设置y轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value<=-5){
                    if(value==-5)return ("0").concat(unitX);
                    return ("-1E"+String.format("%.0f",-value-10)).concat(unitX);
                }else{
                    return ("1E"+String.format("%.0f",value)).concat(unitX);
                }
            }
        });
    }
    public static void show(LineChart lineChart,double arr[], List<Float> dataList, String name, int color, float t,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = dataList.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    Log.e("CHART","a:"+a);
                    data=-Math.abs(10+a);
                }
                entry= new Entry( ((float)Math.log10((arr[i])*t)),(float)data);
            }else{
                //entry=new Entry(i,(float)Math.log10(data));

                entry=new Entry(((float)Math.log10((arr[i])*t)),(float)Math.log10(data));
            }
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    public static void add(LineChart lineChart, double arr[],ArrayList<Float> dataList, String name, int color, float t,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = dataList.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    Log.e("CHART","a:"+a);
                    data=-Math.abs(10+a);
                }
                entry= new Entry( ((float)Math.log10((arr[i])*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((arr[i])*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    public static void show(LineChart lineChart, List<Float> dataList, String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = dataList.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    Log.e("CHART","a:"+a);
                    data=-Math.abs(10+a);
                }
                /*if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-9f;
                    else if(a>=-2)data=-8f+a+1;
                    else if(a>=-3)data=-7f+a+2;
                    else if(a>=-4)data=-6f+a+3;
                    else if(a>=-5)data=-5f+a+4;
                    else{
                        data=-5f;
                    }
                }*/
                /*if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }*/
                //entry= new Entry( i,(float)data);

                entry= new Entry( ((float)Math.log10((i+p)*t)),(float)data);
            }else{
                //entry=new Entry(i,(float)Math.log10(data));

                entry=new Entry(((float)Math.log10((i+p)*t)),(float)Math.log10(data));
            }
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    public static void add(LineChart lineChart, ArrayList<Float> dataList, String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = dataList.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    Log.e("CHART","a:"+a);
                    data=-Math.abs(10+a);
                }
                /*if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-9f;
                    else if(a>=-2)data=-8f+a+1;
                    else if(a>=-3)data=-7f+a+2;
                    else if(a>=-4)data=-6f+a+3;
                    else if(a>=-5)data=-5f+a+4;
                    else{
                        data=-5f;
                    }
                }*/
                /*if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }*/
                entry= new Entry( ((float)Math.log10((i+p)*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((i+p)*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }


    public static void show1(LineChart lineChart,  String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < ControllerFragment.ChouDaoX0.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            if(ControllerFragment.ChouDaoX0.get(i)<9)continue;
            Float data = ControllerFragment.ChouDaoY0.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-9f;
                    else if(a>=-2)data=-8f+a+1;
                    else if(a>=-3)data=-7f+a+2;
                    else if(a>=-4)data=-6f+a+3;
                    else if(a>=-5)data=-5f+a+4;
                    else{
                        data=-5f;
                    }
                }
                /*if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }*/
                //entry= new Entry( i,(float)data);

                entry= new Entry( ((float)Math.log10((ControllerFragment.ChouDaoX0.get(i)+p)*t)),(float)data);
            }else{
                //entry=new Entry(i,(float)Math.log10(data));

                entry=new Entry(((float)Math.log10((ControllerFragment.ChouDaoX0.get(i)+p)*t)),(float)Math.log10(data));
            }
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    public static void show2(LineChart lineChart,  String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < ControllerFragment.ChouDaoX1.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = ControllerFragment.RongHe0.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                entry= new Entry( ((float)Math.log10((ControllerFragment.ChouDaoX1.get(i)+p)*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((ControllerFragment.ChouDaoX1.get(i)+p)*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    public static void add1(LineChart lineChart,  String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < ControllerFragment.ChouDaoX1.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            if(ControllerFragment.ChouDaoX0.get(i)<9)continue;

            Float data = ControllerFragment.ChouDaoY1.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                entry= new Entry( ((float)Math.log10((ControllerFragment.ChouDaoX1.get(i)+p)*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((ControllerFragment.ChouDaoX1.get(i)+p)*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    public static void add2(LineChart lineChart,  String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < ControllerFragment.ChouDaoX1.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            if(ControllerFragment.ChouDaoX0.get(i)<9)continue;

            Float data = ControllerFragment.RongHe0.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                entry= new Entry( ((float)Math.log10((ControllerFragment.ChouDaoX1.get(i)+p)*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((ControllerFragment.ChouDaoX1.get(i)+p)*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public static void sh1(LineChart lineChart,  String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < ControllerFragment.ChouDaoX2.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            if(ControllerFragment.ChouDaoX2.get(i)<88)continue;

            Float data = ControllerFragment.ChouDaoY2.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                //entry= new Entry( i,(float)data);

                entry= new Entry( ((float)Math.log10((ControllerFragment.ChouDaoX2.get(i)+p)*t)),(float)data);
            }else{
                //entry=new Entry(i,(float)Math.log10(data));

                entry=new Entry(((float)Math.log10((ControllerFragment.ChouDaoX2.get(i)+p)*t)),(float)Math.log10(data));
            }
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    public static void ad1(LineChart lineChart,  String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < ControllerFragment.ChouDaoX3.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            if(ControllerFragment.ChouDaoX2.get(i)<88)continue;

            Float data = ControllerFragment.ChouDaoY3.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                entry= new Entry( ((float)Math.log10((ControllerFragment.ChouDaoX3.get(i)+p)*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((ControllerFragment.ChouDaoX3.get(i)+p)*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    public static void ad2(LineChart lineChart,  String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < ControllerFragment.ChouDaoX3.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            if(ControllerFragment.ChouDaoX2.get(i)<88)continue;

            Float data = ControllerFragment.RongHe1.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                entry= new Entry( ((float)Math.log10((ControllerFragment.ChouDaoX3.get(i)+p)*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((ControllerFragment.ChouDaoX3.get(i)+p)*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    private static void initLineDataSet1(LineDataSet lineDataSet, String color, LineDataSet.Mode mode) {
        lineDataSet.setColor(Color.parseColor(color));//Color.parseColor("#1123123")
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircles(true);//不显示点
        lineDataSet.setCircleColors(Color.parseColor(color));
        lineDataSet.setCircleRadius(2f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(false);//不显示值
        if (mode == null) {
            //设置曲线展示为圆滑曲线
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }


    private static void initLineDataSet1(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);//Color.parseColor("#1123123")
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircles(true);//不显示点
        lineDataSet.setCircleColors(color);
        lineDataSet.setCircleRadius(2f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(false);//不显示值
        if (mode == null) {
            //设置曲线展示为圆滑曲线
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }



    public static void addRight(LineChart lineChart, ArrayList<Float> dataList, String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = dataList.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                entry= new Entry( ((float)Math.log10(i+p)*t),(float)data);
            }else{
                entry=new Entry(((float)Math.log10(i+p)*t),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }




    /**
     *
     * @param chart     图表
     * @param countX    x轴标签
     * @param countY    y轴标签
     * @param bX        x轴对数
     * @param bY        y轴对数
     * @param unitX     x轴单位
     * @param unitY     y轴单位
     * @param description    图表描述
     * @createtime 2023/3/7 11:03
     **/
    public static void initChartView(LineChart chart,int countX,int countY, boolean bX,boolean bY,String unitX, String unitY, String description) {//图表对象，标签数量，x轴单位,y轴单位
        chart.setDrawBorders(true);//显示边界
        chart.setDrawGridBackground(false);//不显示图表网格线
        chart.getDescription().setText(description);
        (chart.getLegend()).setTextSize(7f);//设置图例文字大小
        XAxis xaxis=chart.getXAxis();//设置x轴
        xaxis.setLabelCount(countX,true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴底端显示
        xaxis.setDrawGridLines(true);//不显示x轴网格线
        xaxis.enableGridDashedLine(10.4f, 10.4f, 0f);
        xaxis.setValueFormatter(new IAxisValueFormatter() {//设置x轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(bX){
                    return "1E"+String.format("%.0f",value).concat(unitX);
                }
                return String.valueOf(((Math.round(value*10))/10)).concat(unitX);

            }
        });
        chart.getAxisRight().setEnabled(false);//右侧y轴不显示
        YAxis leftYAxis = chart.getAxisLeft();//设置y轴
        leftYAxis.setLabelCount(countY,true);//设置标签个数以及是否精确（false为模糊，true为精确）
        leftYAxis.setDrawGridLines(true);//y轴网格线不显示
        leftYAxis.enableGridDashedLine(10.4f, 10.4f, 0f);

        leftYAxis.setValueFormatter(new IAxisValueFormatter() {//设置y轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(bY){
                    return "1E"+String.format("%.0f",value).concat(unitY);
                }
                return String.format("%.0f",value).concat(unitY);
            }
        });

    }

    public static void initChartView(LineChart chart,int countX,int countY, boolean bX,boolean bY1,boolean bY2,String unitX, String unitY, String description) {//图表对象，标签数量，x轴单位,y轴单位
        chart.setDrawBorders(true);//显示边界
        chart.setDrawGridBackground(false);//不显示图表网格线
        chart.getDescription().setText(description);
        (chart.getLegend()).setTextSize(7f);//设置图例文字大小
        XAxis xaxis=chart.getXAxis();//设置x轴
        xaxis.setLabelCount(countX,true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴底端显示
        xaxis.setDrawGridLines(false);//不显示x轴网格线
        xaxis.setValueFormatter(new IAxisValueFormatter() {//设置x轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(bX){
                    return "1E"+String.format("%.1f",value).concat(unitX);
                }
                return String.valueOf(((Math.round(value*10))/10)).concat(unitX);

            }
        });
        chart.getAxisRight().setEnabled(false);//右侧y轴不显示
        YAxis leftYAxis = chart.getAxisLeft();//设置y轴
        leftYAxis.setLabelCount(countY,true);
        leftYAxis.setDrawGridLines(false);//y轴网格线不显示
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {//设置y轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(bY1){
                    return "1E"+String.format("%.1f",value).concat(unitY);
                }
                return String.valueOf(((Math.round(value*10))/10)).concat(unitY);
            }
        });
        YAxis rightYAxis = chart.getAxisRight();//设置y轴
        rightYAxis.setDrawGridLines(false);//y轴网格线不显示
        rightYAxis.setValueFormatter(new IAxisValueFormatter() {//设置y轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(bY2)String.valueOf((float)((Math.round(value*100))/100.00)).concat("");
                return String.valueOf("");
            }
        });
    }



    public static void showLineChart(LineChart lineChart, List<Float> dataList, boolean bX,boolean bY,String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        Log.e("showLineChart", "开始"+dataList.size());
        int m=1;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = dataList.get(i);
            Entry entry;
            if(data==0&&i>0){
                dataList.set(i,dataList.get(i-1));
            }
            data=dataList.get(i);
            if(data>0){
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }else{
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(-data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(-data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        Log.e("showLineChart", "结束"+dataList.size());
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    public static void addLine(LineChart lineChart, ArrayList<Float> dataList,boolean bX,boolean bY, String name, int color, float t,int p,boolean b){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Float data = dataList.get(i);
            Entry entry;
            if(data==0&&i>0){
                dataList.set(i,dataList.get(i-1));
            }
            data=dataList.get(i);
            if(data>0){
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }else{
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(-data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(-data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    /**
     * * 展示曲线
     *
     * @param lineChart  图表
     * @param dataList   曲线数据
     * @param name       曲线名
     * @param color      曲线颜色
     */
    public static void showLineChart(LineChart lineChart, List<Float> dataList, boolean bX,boolean bY,String name, int color, int t,int p){
        List<Entry> entries = new ArrayList<>();
        Log.e("showLineChart", "开始"+dataList.size());
        for (int i = 0; i < dataList.size(); i++) {
            Float data = dataList.get(i);
            Entry entry;
            if(data==0&&i>0){
                dataList.set(i,dataList.get(i-1));
            }
            data=Math.abs(dataList.get(i));
            if(data>0){
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }else{
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(-data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(-data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        Log.e("showLineChart", "结束"+dataList.size());
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }

    public static void addLine(LineChart lineChart, ArrayList<Float> dataList,boolean bX,boolean bY, String name, int color, int t,int p){
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Float data = dataList.get(i);
            Entry entry;
            if(data==0&&i>0){
                dataList.set(i,dataList.get(i-1));
            }
            data=Math.abs(dataList.get(i));
            if(data>0){
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }else{
                if(bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(-data)));
                }else if(bY&&!bX){
                    entry = new Entry((i+p)*t,((float)Math.log10(-data)));
                }else if(!bY&&bX){
                    entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
                }else{
                    entry = new Entry((i+p)*t,data);
                }
            }
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public static void addRightYLine(LineChart lineChart,List<Float> dataList, boolean bX,boolean bY, String name, int color,float t,int p,boolean b) {
        List<Entry> entries = new ArrayList<>();
        int m=0;
        for (int i = 0; i < dataList.size(); i=i+m) {
            if(b){
                if(i<10)m=1;
                else if(i<100)m=2;
                else if(i<1000)m=25;
                else m=100;
            }
            Entry entry;
            Float data = dataList.get(i);
            if(bY&&bX){
                entry = new Entry(((float)Math.log10(i+p)*t),((float)Math.log10(data)));
            }else if(bY&&!bX){
                entry = new Entry((i+p)*t,((float)Math.log10(data)));
            }else if(!bY&&bX){
                entry = new Entry(((float)Math.log10(i+p)*t),((float)data));
            }else{
                entry = new Entry((i+p)*t,data);
            }
            entries.add(entry);
            /*Float data = dataList.get(i*b);
            Entry entry = new Entry(a+i*b, data);
            entries.add(entry);*/
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private static void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircles(false);//不显示点
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(false);//不显示值
        if (mode == null) {
            //设置曲线展示为圆滑曲线
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }


    public static void firstChartView(LineChart chart, int countX, int countY, String unitX, String unitY, String description) {
        chart.setDrawBorders(true);//显示边界
        chart.setDrawGridBackground(false);//不显示图表网格线
        chart.getDescription().setText(description);
        (chart.getLegend()).setTextSize(7f);//设置图例文字大小
        XAxis xaxis=chart.getXAxis();//设置x轴
        xaxis.setLabelCount(countX,true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴底端显示
        xaxis.setDrawGridLines(true);//不显示x轴网格线
        xaxis.enableGridDashedLine(10.4f, 10.4f, 0f);

        xaxis.setValueFormatter(new IAxisValueFormatter() {//设置x轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "1E"+String.format("%.0f",value).concat(unitX);
            }
        });
        //chart.getAxisRight().setEnabled(false);//右侧y轴不显示
        YAxis leftYAxis = chart.getAxisLeft();//设置y轴
        leftYAxis.setLabelCount(countY,true);
        leftYAxis.setDrawGridLines(true);//y轴网格线不显示
        leftYAxis.enableGridDashedLine(10.4f, 10.4f, 0f);
        leftYAxis.setAxisMaximum(7);
        leftYAxis.setAxisMinimum(-2);

        leftYAxis.setValueFormatter(new IAxisValueFormatter() {//设置y轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "1E"+String.format("%.0f",value).concat(unitY);
            }
        });
        YAxis rightYAxis = chart.getAxisRight();//设置y轴
        rightYAxis.setDrawGridLines(false);//y轴网格线不显示
        rightYAxis.setValueFormatter(new IAxisValueFormatter() {//设置y轴轴标签
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //return String.valueOf((float)((Math.round(value*100))/100.00)).concat("");
                return "";

            }
        });
    }
    /**
     * @param lineChart
     * @param dataList
     * @param name
     * @param color
     * @param t
     * @param p      * 偏移值
     * @createtime 2023/3/15 11:06
     **/
    public static void firstChart(LineChart lineChart, List<Float> dataList,String name, int color, double t,int p){
        List<Entry> entries = new ArrayList<>();
        Log.e("showLineChart", "开始"+dataList.size());
        for (int i = 0; i < dataList.size(); i++) {
            Float data = dataList.get(i);
            Entry entry;
            entry=new Entry((float) ((i)*t+p),dataList.get(i));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    /**
     *
     * @param lineChart
     * @param dataList
     * @param name
     * @param color
     * @param t
     * @param p 偏移值
     * @createtime 2023/3/15 11:08
     **/
    public static void firstaddline(LineChart lineChart, ArrayList<Float> dataList, String name, int color, double t,int p) {

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Float data = dataList.get(i);
            Entry entry;
            entry=new Entry((float) (i*t+p),dataList.get(i));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    public static void firstaddRightYLine(LineChart lineChart, ArrayList<Float> dataList, String name, int color, double t,int p) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Float data = dataList.get(i);
            Entry entry;
            entry=new Entry((float) (i*t+p),dataList.get(i));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }




//num为关断时间
    public static void show(LineChart lineChart, ArrayList<Float> X,ArrayList<Float> Y,int num, String name, int color, float t,int p){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < X.size(); i=i+m) {
            if(X.get(i)<num)continue;
            Float data = Y.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                //entry= new Entry( i,(float)data);

                entry= new Entry( ((float)Math.log10((X.get(i)+p)*t)),(float)data);
            }else{
                //entry=new Entry(i,(float)Math.log10(data));

                entry=new Entry(((float)Math.log10((X.get(i)+p)*t)),(float)Math.log10(data));
            }
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        //initLineDataSet1(lineDataSet, "#FF8C00", LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    public static void add(LineChart lineChart,  ArrayList<Float> X,ArrayList<Float> Y,int num,  String name, int color, float t,int p){
        List<Entry> entries = new ArrayList<>();
        int m=1;
        for (int i = 0; i < X.size(); i=i+m) {
            if(X.get(i)<num)continue;
            Float data = Y.get(i);
            Entry entry=new Entry();
            if(data<=0.00001){
                if(data>=-0.00001){
                    data=-5f;
                }else{
                    float a=(float)Math.log10(-data);
                    if(a>=-1)data=-8f;
                    else if(a>=-2)data=-6f+a;
                    else if(a>=-3)data=-4f+a;
                    else if(a>=-4)data=-2f+a;
                    else if(a>=-5)data=a;
                    else{
                        data=-5f;
                    }
                }
                entry= new Entry( ((float)Math.log10((X.get(i)+p)*t)),(float)data);
            }else{
                entry=new Entry(((float)Math.log10((X.get(i)+p)*t)),(float)Math.log10(data));
            }
            //Log.e("showLineChart", i+"((float)Math.log10(i))   "+((float)Math.log10(i+0.001))+"  data   "+data+"   (float)Math.log10(data)     "+(float)Math.log10(data));
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet1(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        //initLineDataSet1(lineDataSet, "#663300", LineDataSet.Mode.CUBIC_BEZIER);

        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }




}
