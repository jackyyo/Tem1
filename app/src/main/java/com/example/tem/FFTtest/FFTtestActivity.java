package com.example.tem.FFTtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.tem.ChartDataAnalysis;
import com.example.tem.ChartPlay;
import com.example.tem.R;
import com.github.mikephil.charting.charts.LineChart;

public class FFTtestActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffttest);
        lineChart=findViewById(R.id.chart);
        ChartPlay.initChartView(lineChart,5,4,false,false,"","","V/HZ");//初始化图表
        //if(ChartDataAnalysis.FFTlists.size()!=0)ChartPlay.showLineChart(lineChart,ChartDataAnalysis.FFTlists,false,false,"fft", Color.CYAN,50,0,false);//ParamSaveClass.time
    }
}