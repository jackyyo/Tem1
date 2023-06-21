package com.example.tem;

import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;

import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChartDataAnalysis {
        //Arraylist的最大长度为2147483647(32位最多接收8G数据)2147483647*4/1024/1024/1024
        public static ArrayList<ArrayList<Float>> lists1=null;//多测道图表数据
        public static int CurNum0 = 1;
        public static int CurNum1 = 1;
        public static ArrayList<ArrayList<Float>> lists2=null;//四道电压衰减曲线
        public static ArrayList<Float> lists3=null;//融合电压衰减曲线数据
        public static ArrayList<ArrayList<Double>> CurrentList=new ArrayList<>(2);//两道电流曲线

        public static ArrayList<Float> ErrorList=new ArrayList<>();//误差曲线
        public static  ArrayList<Float> OddList=new ArrayList<>();//奇数
        public static  ArrayList<Float> EvenList=new ArrayList<>();//偶数

        public static  ArrayList<ArrayList<Float>> Channel = new ArrayList<>();//抽道数据存储
        public static  float [] time0={(float)0.02,(float)0.01,(float)0.002,(float)0.001,(float)1/1800};//ms


    private static final String LINE_SEPARATOR = System.getProperty("line.separator");


    private static final String TAG ="chartDataAnalysis" ;
    private static float time=0;
    /**
     * 多测道初始图
 * @param a
 * @param x
 * @param y
 * @param z
 * @param dot
 * @return java.util.ArrayList<java.util.ArrayList<java.lang.Float>>
     * @createtime 2023/3/15 11:55
    **/
    public static ArrayList<ArrayList<Float>> getSinData(float a,float x,float y,float z,int dot){
        ArrayList<ArrayList<Float>> lists=new ArrayList<>(9);
        for(int i=0;i<9;i++){
            lists.add(getTestData(a,x,y,z+a*i/10,dot));
        }
        return lists;
    }
    public static ArrayList<Float> getTestData(float a,float x,float y,float z,int dot){
        ArrayList<Float> lists=new ArrayList<>();
            for(double i=0;i<dot;i++){
                float data=(float) (a*Math.sin(x*i*Math.PI/125+y)+z )+(float) (a*Math.sin(2*x*i*Math.PI/125+y)+z)+(float) (a*Math.sin(4*x*i*Math.PI/125+y)+z);
                lists.add((float) Math.pow(10,data));
            }
        return lists;
    }




    public static ArrayList<ArrayList<Float>> getInit(){
        ArrayList<ArrayList<Float>> res=new ArrayList<>(6);
        for(int i=0;i<6;i++){
            res.add(new ArrayList<>());
        }
        for(double i=-1.5;i<1;){
            res.get(0).add((float) (Math.pow(Math.E,-1.15*i)-0.1));
            res.get(2).add((float) (Math.pow(Math.E,-0.9*i)));
            i=i+0.01;
        }
        for(double i=-1;i<1;){
            boolean b=Math.random()>0.5?true:false;
            if(b){
                res.get(1).add((float) (Math.pow(Math.E,-1.2*(i-0.1)) + Math.random()*0.01-0.1));
                res.get(3).add((float) (Math.pow(Math.E,-1*(i-0.1)) + Math.random()*0.01));
            }else{
                res.get(1).add((float) (Math.pow(Math.E,-1.2*(i-0.1)) - Math.random()*0.01-0.1));
                res.get(3).add((float) (Math.pow(Math.E,-1*(i-0.1)) - Math.random()*0.01));
            }

            i=i+0.01;
        }
        return res;
    }

        public static ArrayList<Float> getDesc(float x,float y,int dot){
            ArrayList<Float> res=new ArrayList<>(6);
            for(int i=0;i<dot;i++){
                res.add((float)(1/(y*i+0.001)));
            }
            return res;
        }

        public static ArrayList<ArrayList<Float>> getSin(float x,float y,int dot){
            ArrayList<ArrayList<Float>> res=new ArrayList<>(10);
            for(int i=0;i<9;i++){
                res.add(getSinOne(x,y+(float)i/4,dot));
            }
            return res;
        }


        public static ArrayList<Float> getSinOne(float x,float y,int dot){
            ArrayList<Float> res=new ArrayList<>();
            for(int i=0;i<dot;i++){
                res.add((float)Math.pow(10,(double)((float) (1.1*Math.sin(x*i*Math.PI/125))+(float) (1.1*Math.sin(2*x*i*Math.PI/125))+(float) (1.1*Math.sin(4*x*i*Math.PI/125)))+y));
            }
            return res;
        }


    public static ArrayList<Float> getAverageLists(ArrayList<Float> per,ArrayList<Float> cur){
        ArrayList<Float> res=new ArrayList<>();
        if(per.size()==0)return cur;
        if(cur.size()==0)return per;
        int len=Math.min(per.size(),cur.size());
        for(int i=0;i<len;i++){
            res.add((per.get(i)+cur.get(i))/2);
        }
        return res;
    }

    /**
     *
     * @param per
     * @param cur
     * @return     返回两个图表数据的相对误差值
     */
    public static ArrayList<Float> getErrorLists(ArrayList<Float> per,ArrayList<Float> cur){
        ArrayList<Float> res=new ArrayList<Float>();
        if(per.size()==0|cur.size()==0)return res;
        for (int j=0;j<per.size();j++){
            float data=Math.abs(per.get(j)-cur.get(j))*100/(per.get(j)+cur.get(j));//相对误差百分比    （A-B）*100/（A+B）
            res.add((float)((Math.round(data*10))/10.0));
        }
        return res;
    }


    /**
     *
     * @param dataFilePathname 文件名
     * @return           返回原始数据集合数组，解析二进制数据
     */
    public static ArrayList<ArrayList<Float>> binaryToDecimal1(String dataFilePathname){
        Log.e(TAG,"Time:");


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        String modfiedTime = formatter.format(date);

        ArrayList<ArrayList<Float>> res=new ArrayList<ArrayList<Float>>();

        for(int i=0;i<4;i++){
            res.add(new ArrayList<>());
        }
        if(CurrentList.size()<2){
            for(int i=0;i<2;i++){
                CurrentList.add(new ArrayList<>());
            }
        }
        Log.e(TAG, "CurrentList.size():" +CurrentList.size());
        ChartDataAnalysis.CurNum0=1;
        ChartDataAnalysis.CurNum1=1;
        if (dataFilePathname!=null){
            //todo
            File file = new File(dataFilePathname);
            Log.e(TAG, "file=" +file);
            Log.e(TAG, "dataFilePathname=" +dataFilePathname);
            long fileSize= file.length();//返回文件长度，字节为单位
            int DotNumber=(int)fileSize/4;//每个数据32位(4字节）,前8位为校验位
            RandomAccessFile readFile = null;
            byte[] buffer =new byte[1024];
            int len = 0;
            int sum=0;
            Log.e(TAG, "fileSize=" +fileSize);
            Log.e(TAG, "DotNumber=" +DotNumber);
            try {
                readFile=new RandomAccessFile(file, "r");
                //while(DotNumber--!=0) {
                while(sum<fileSize) {

                    len=readFile.read(buffer);
                    for(int index=0;index<1024;index=index+4){
                        int seq1=(buffer[index]&0xf0)>>4;//高四位
                        //Log.e(TAG, "seq1=" +seq1);
                        if (seq1 == 8) {
                            int seq2=buffer[index]&0x0f;//低四位
                            if(seq2==1){
                                time+=time0[0];
                            }else if(seq2==2){
                                time+=time0[1];
                            }else if(seq2==3){
                                time+=time0[2];
                            }else if(seq2==4){
                                time+=time0[3];
                            }else{
                                time+=time0[4];
                            }
                            int num = (buffer[index+1]&0xff)<<16
                                    |(buffer[index+2]&0xff)<<8
                                    | buffer[index+3]&0xff;
                            if (num>524288){//接收到的数据大于524287为负数补码,数据最高位为符号位。(2^19=524288)
                                num=0xff<<24|((buffer[index+1]&0xff)|0xf0)<<16
                                        |(buffer[index+2]&0xff)<<8
                                        | buffer[index+3]&0xff;
                            }
                            double data=  num/Math.pow(2,19)*2.5;
                            CurrentList.get(0).add(data);
                            SetCurrentRecord(modfiedTime,"小电流",""+time,""+data);
                        }else if(seq1==12){
                            int seq2=buffer[index]&0x0f;//低四位
                            if(seq2==1){
                                time+=time0[0];
                            }else if(seq2==2){
                                time+=time0[1];
                            }else if(seq2==3){
                                time+=time0[2];
                            }else if(seq2==4){
                                time+=time0[3];
                            }else{
                                time+=time0[4];
                            }
                            int num = (buffer[index+1]&0xff)<<16
                                    |(buffer[index+2]&0xff)<<8
                                    | buffer[index+3]&0xff;
                            if (num>524288){//接收到的数据大于524287为负数补码,数据最高位为符号位。(2^19=524288)
                                num=0xff<<24|((buffer[index+1]&0xff)|0xf0)<<16
                                        |(buffer[index+2]&0xff)<<8
                                        | buffer[index+3]&0xff;
                                Log.e(TAG, "111111111111111111");
                            }

                            double data=  num/Math.pow(2,19)*2.5;
                            CurrentList.get(1).add(data);
                            SetCurrentRecord(modfiedTime,"大电流",""+time,""+data);
                            Log.e(TAG, "data:" +data);
                        }
                    }
                    //Log.e(TAG, "sum=" +sum);
                    sum+=len;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "CurrentList:"+CurrentList);
            Log.e(TAG, "ParamSaveClass.CurrentOffTime:"+ParamSaveClass.CurrentOffTime+"   "+ParamSaveClass.CurrentOffTime1);
            Log.e(TAG, "CurrentList.size():"+CurrentList.get(0).size());
        }
        time=0;
        return res;
    }

    /**
     *  返回绝对值最大
    	 * @param list 数据集合
    	 * @return float
     * @createtime 2023/3/5 17:09
    **/
    public static float getMax(ArrayList<ArrayList<Float>> list){
        float res=0;
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).size();j++){
                res=Math.max(Math.abs(list.get(i).get(j)),res);
            }
        }
        return res;
    }


    /**
     *解析数据
     * 发射机电流（小）高四位1000，发射机电流（大）高四位1100，小电流关断时间1001，大电流关断时间1101，关断时间24位二进制数据，直接解析
     * 接收机小电流强信号前期00100000，后期00101111，弱信号后期00111111
     * 接收机大电流强信号前期00000000，后期00001111，弱信号后期00011111
     * 接收机共32为，其中前八位校验位，第九位符号位，后23位数据，计算方法num/Math.pow(2,23)*5
     * 发送机电流共32为，其中前八位校验位，第十三位符号位，后19位数据，计算方法num/Math.pow(2,19)*5
     *
     * @param dataFilePathname 文件名
     * @return           返回原始数据集合数组，解析二进制数据
     */
        public static ArrayList<ArrayList<Float>> binaryToDecimalTest(String dataFilePathname){
            Log.e(TAG,"Time:");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date(System.currentTimeMillis());
            String modfiedTime = formatter.format(date);
            ArrayList<ArrayList<Float>> res=new ArrayList<ArrayList<Float>>();
            for(int i=0;i<4;i++){
                res.add(new ArrayList<>());
            }
            if(CurrentList.size()<2){
                for(int i=0;i<2;i++){
                    CurrentList.add(new ArrayList<>());
                }
            }
            ChartDataAnalysis.CurNum0=1;
            ChartDataAnalysis.CurNum1=1;
            if (dataFilePathname!=null){
                //todo
                File file = new File(dataFilePathname);
                Log.e(TAG, "file=" +file);
                Log.e(TAG, "dataFilePathname=" +dataFilePathname);
                long fileSize= file.length();//返回文件长度，字节为单位
                int DotNumber=(int)fileSize/4;//每个数据32位(4字节）,前8位为校验位
                RandomAccessFile readFile = null;
                byte[] buffer =new byte[4];
                int len = 0;
                int sum=0;
                Log.e(TAG, "fileSize=" +fileSize);
                Log.e(TAG, "DotNumber=" +DotNumber);
                try {
                    readFile=new RandomAccessFile(file, "r");
                    while(DotNumber--!=0) {

                        len=readFile.read(buffer);
                        int index = 0;
                        int seq1=(buffer[index]&0xf0)>>4;//高四位
                        //Log.e(TAG, "seq1=" +seq1);
                        if(seq1>=8){//发送机
                            int seq2=buffer[index]&0x0f;//低四位
                            if(seq2==1){
                                time+=time0[0];
                            }else if(seq2==2){
                                time+=time0[1];
                            }else if(seq2==3){
                                time+=time0[2];
                            }else if(seq2==4){
                                time+=time0[3];
                            }else{
                                time+=time0[4];
                            }
                            if(seq1==8){//1000小电流数据
                                int num = (buffer[index+1]&0xff)<<16
                                        |(buffer[index+2]&0xff)<<8
                                        | buffer[index+3]&0xff;
                                if (num>524288){//接收到的数据大于524287为负数补码,数据最高位为符号位。(2^19=524288)
                                    num=0xff<<24|((buffer[index+1]&0xff)|0xf0)<<16
                                            |(buffer[index+2]&0xff)<<8
                                            | buffer[index+3]&0xff;
                                }
                                float data= (float) (num/Math.pow(2,19)*2.5);
                                //CurrentList.get(0).add(data);
                                SetCurrentRecord(modfiedTime,"小电流",""+time,""+data);
                            }else if(seq1==12){//1100大电流数据
                                int num =((buffer[index+1]&0xff))<<16
                                        |(buffer[index+2]&0xff)<<8
                                        | buffer[index+3]&0xff;
                                if (num>524288){
                                    num=0xff<<24|((buffer[index+1]&0xff)|0xf0)<<16
                                            |(buffer[index+2]&0xff)<<8
                                            | buffer[index+3]&0xff;
                                }
                                float data= (float) (num/Math.pow(2,19)*2.5);
                                //CurrentList.get(1).add(data);
                                SetCurrentRecord(modfiedTime,"大电流",""+time,""+data);
                            }else if(seq1==9){//小电流关断时间
                                int num =(buffer[index+1]&0xff)<<16
                                        |(buffer[index+2]&0xff)<<8
                                        | buffer[index+3]&0xff;
                                ParamSaveClass.CurrentOffTime = num;
                            }else if(seq1==13){//大电流关断时间
                                int num =(buffer[index+1]&0xff)<<16
                                        |(buffer[index+2]&0xff)<<8
                                        | buffer[index+3]&0xff;
                                ParamSaveClass.CurrentOffTime1 = num;
                            }
                        }else{//接收机
                            int seq2=buffer[index]&0x0f;//低四位
                            int num =(buffer[index+1]&0xff)<<16
                                    |(buffer[index+2]&0xff)<<8
                                    | buffer[index+3]&0xff;
                            if (num>8388608){//接收到的数据大于8388608为负数补码,数据最高位为符号位。(2^23=8388608)
                                num=0xff<<24|(buffer[index+1]&0xff)<<16
                                        |(buffer[index+2]&0xff)<<8
                                        | buffer[index+3]&0xff;
                            }
                            //float data= (float) (num/Math.pow(2,23)*1000);//20*1000(test的1000为max)
                            float data= (float) (num/Math.pow(2,23)*(float)5);//20*1000(test的1000为max)
                            if(seq1==0){  //大强
                                data= (float) (data);//强信号衰减1.5/11.5*100倍(float)1.5/(float)11.5*100       //*15
                                res.get(3).add(data*7);
                                if(seq2==0){
                                    CurNum1++;
                                }
                            }else if(seq1==1){  //大弱
                                data= ((float) data/32);//32
                                res.get(2).add(data);
                            }else if(seq1==2){  //小强
                                data= (float) (data*7);//(float)1.5/(float)11.5*100
                                if(seq2==0){
                                    CurNum0++;
                                }
                                res.get(0).add(data);
                            }else if(seq1==3){  //小弱
                                data= ((float) data/32);
                                res.get(1).add(data);
                            }
                        }
                        sum+=4;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SetCurrentRecord(modfiedTime+"强信号",res.get(0));
                SetCurrentRecord(modfiedTime+"弱信号",res.get(1));

                //SetCurrentRecord("000000",res.get(0));
                //SetCurrentRecord("111111",res.get(1));
                Channel=getChannel(res);

                Log.e(TAG, "res.size():"+res.size());
                Log.e(TAG, "res.get(0).size():"+res.get(0).size());
                Log.e(TAG, "res.get(1).size():"+res.get(1).size());
                Log.e(TAG, "res.get(0):"+res.get(0));
                Log.e(TAG, "res.get(1):"+res.get(1));
                Log.e(TAG, "res.get(2):"+res.get(2));
                Log.e(TAG, "res.get(3):"+res.get(3));
                Log.e(TAG, "CurrentList:"+CurrentList);
                Log.e(TAG, "ParamSaveClass.CurrentOffTime:"+ParamSaveClass.CurrentOffTime+"   "+ParamSaveClass.CurrentOffTime1);
                Log.e(TAG, "CurrentList.size():"+CurrentList.get(0).size());
            }
            time=0;
            return res;
        }

        /**
         *
        	 * @param list 抽道集合
        	 * @return java.util.ArrayList<java.util.ArrayList<java.lang.Float>>
         * @createtime 2023/5/18 11:46
        **/
    public static ArrayList<ArrayList<Float>> getChannel(ArrayList<ArrayList<Float>> list){
        ArrayList<ArrayList<Float>> res=new ArrayList<>();
        int arr1[]={5,10,20,40,80,160,320,640};//抽道窗口
        int arr2[]={20,60,140,300,620,1260,2540,5100};//抽道起始点
        for(int i=0;i<4;i++){
            res.add(new ArrayList<>());
        }
        //float a=0,b=0;
        int index=0;
        for(int j=0;j<8;j++){
            while(index<arr2[j]){
                float a=0,b=0;
                for(int i=index;i<index+arr1[j];i++){
                    a=a+list.get(0).get(i);
                    b=b+list.get(1).get(i);
                }
                res.get(0).add(a/arr1[j]);
                res.get(1).add(b/arr1[j]);
                index=index+5;
            }
        }
        index=0;
        for(int j=0;j<8;j++){
            while(index<arr2[j]){
                float a=0,b=0;
                for(int i=index;i<index+arr1[j];i++){
                    a=a+list.get(2).get(i);
                    b=b+list.get(3).get(i);
                }
                res.get(2).add(a/arr1[j]);
                res.get(3).add(b/arr1[j]);
                index=index+5;
            }
        }
        return res;
    }

    private static void SetCurrentRecord(String modfiedTime,ArrayList<Float> res){
        File f = new File(Constants.DATA_DIRECTORY+"/"+ParamSaveClass.Location+"/结果数据/" +modfiedTime+".txt");
        if(f.exists()){
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(f, true), "utf-8");
            for(int i=0;i<res.size();i++){
                writer.write(res.get(i)+"\n");
            }
            writer.write(LINE_SEPARATOR);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void SetCurrentRecord(String modfiedTime,String type, String a, String b){
        File f = new File(Constants.DATA_DIRECTORY+"/"+ParamSaveClass.Location +"/"+modfiedTime+".txt");
        if(!f.exists()){
            f.mkdirs();
        }
        try {
            OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(f, true), "utf-8");
            writer.write(type+"   "+a+"   "+b);
            writer.write(LINE_SEPARATOR);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void ResultRecord(){
        File ResultFile = new File(Constants.DATA_DIRECTORY+"/Result.txt");
        if(ResultFile.exists()){
            ResultFile.delete();
            try {
                ResultFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                ResultFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //WriteData(ResultFile,CurrentList);
    }
    private void WriteData(File file,ArrayList<ArrayList<Float>> lists){
        try {
            OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(file, true),"GBK");
            for(int i=0;i<lists.size();i++){
                for(int j=0;j<lists.get(i).size();j++){
                    if(j!=lists.get(i).size()-1){
                        writer.write(lists.get(i).get(j)+",");
                    }else{
                        writer.write(lists.get(i).get(j)+"");
                    }
                }
                writer.write(LINE_SEPARATOR);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        /**
         * 四组数据做融合
	 * @param list  四组数据
	 * @param num0  小电流开始采集弱电流点数
	 * @param num1  小电流开始采集弱电流点数
	 * @return java.util.ArrayList<java.lang.Float>
         * @createtime 2023/3/3 16:09
        **/
        public static ArrayList<Float> getRongHe(ArrayList<ArrayList<Float>> list,int num0,int num1){
            ArrayList<Float> res=new ArrayList<>();
            int a=list.get(0).size(),b=list.get(1).size(),c=list.get(2).size();//记录交点位置
            if(list==null||list.size()==0){
            }else{
                //融合
                if(list.get(0).size()!=0&&list.get(1).size()!=0&&list.get(2).size()!=0&&list.get(3).size()!=0){
                    int index=0;
                    Log.e(TAG,"size:"+(list.get(0).size()+" "+list.get(1).size()+" "+list.get(2).size()+" "+list.get(3).size()));
                    Log.e(TAG,"size:"+(list.get(0).size()-list.get(1).size()));
                    Log.e(TAG,list.get(1)+"");
                    boolean aa=false,bb=false,cc=false;//记录是否已找到交点
                    for(int i=0;i<list.get(0).size()&&i-num0<list.get(1).size()&&i-num1<list.get(2).size()&&i<list.get(3).size();i++){
                        /*Log.e(TAG,i+"");
                        if(i>=num0)Log.e(TAG,i+" "+list.get(0).get(i)+"  "+list.get(1).get(i-num0)+"  "+list.get(2).get(i-num1)+"  "+list.get(3).get(i));*/
                        if(!aa&&i>num0&&(list.get(0).get(i).equals(list.get(1).get(i-num0))||
                                (i>num0&&list.get(0).get(i-1)>list.get(1).get(i-num0-1)&&list.get(0).get(i)<list.get(1).get(i-num0))||
                                (i>num0&&list.get(0).get(i-1)<list.get(1).get(i-num0-1)&&list.get(0).get(i)>list.get(1).get(i-num0)))){
                            a=i;
                            aa=true;
                        }
                        if(!bb&&i>num1&&(list.get(1).get(i-num0).equals(list.get(3).get(i))||
                                (i>num1&&list.get(1).get(i-num0-1)>list.get(3).get(i-1)&&list.get(1).get(i-num0)<list.get(3).get(i))||
                                (i>num1&&list.get(1).get(i-num0-1)<list.get(3).get(i-1)&&list.get(1).get(i-num0)>list.get(3).get(i)))){
                            b=i;
                            bb=true;
                        }

                        if(!cc&&i>num1&&(list.get(3).get(i).equals(list.get(2).get(i-num1))||
                                (i>num1&&list.get(3).get(i-1)>list.get(2).get(i-num1-1)&&list.get(3).get(i)<list.get(2).get(i-num1))||
                                (i>num1&&list.get(3).get(i-1)<list.get(2).get(i-num1-1)&&list.get(3).get(i)>list.get(2).get(i-num1)))){
                            Log.e(TAG,i+"");
                            c=i;
                            cc=true;
                        }
                    }
                }
                Log.e(TAG,"a:"+a+" b:"+b+" c:"+c);
                for(int i=0;i<list.get(0).size()&&i-num0<list.get(1).size()&&i-num1<list.get(2).size()&&i<list.get(3).size();i++){
                    if(i<a){
                        res.add(list.get(0).get(i));
                    }else if(i<b){
                        res.add(list.get(1).get(i-num0));
                    }else if(i<c){
                        res.add(list.get(3).get(i));
                    }else{
                        res.add(list.get(2).get(i-num1));
                        if(!(i+1<list.get(0).size()&&i+1-num0<list.get(1).size()&&i+1-num1<list.get(2).size()&&i+1<list.get(3).size())){
                            while(i-num1<list.get(2).size()){
                                res.add(list.get(2).get(i-num1));
                                i++;
                            }
                        }
                    }
                }
            }
            return res;
        }

        //测试版
    public static ArrayList<Float> getRongHe1(ArrayList<ArrayList<Float>> list,int num0,int num1){
        ArrayList<Float> res=new ArrayList<>();
        int a=list.get(0).size(),b=list.get(1).size(),c=list.get(2).size();//记录交点位置
        if(list==null||list.size()==0){
        }else{
            //融合
            int index1=0;
            while(list.get(0).get(index1)>0.001){
                index1++;
            }
            if(list.get(0).size()!=0&&list.get(1).size()!=0&&list.get(2).size()!=0&&list.get(3).size()!=0){
                int index=0;
                Log.e(TAG,"size:"+(list.get(0).size()+" "+list.get(1).size()+" "+list.get(2).size()+" "+list.get(3).size()));
                Log.e(TAG,"size:"+(list.get(0).size()-list.get(1).size()));
                Log.e(TAG,list.get(1)+"");
                boolean aa=false,bb=false,cc=false;//记录是否已找到交点
                for(int i=index1;i<list.get(0).size()&&i-num0<list.get(1).size()&&i-num1<list.get(2).size()&&i<list.get(3).size();i++){
                        /*Log.e(TAG,i+"");
                        if(i>=num0)Log.e(TAG,i+" "+list.get(0).get(i)+"  "+list.get(1).get(i-num0)+"  "+list.get(2).get(i-num1)+"  "+list.get(3).get(i));*/

                    if(!aa&&i>num0&&(list.get(0).get(i).equals(list.get(1).get(i-num0))||
                            (i>num0&&list.get(0).get(i-1)>list.get(1).get(i-num0-1)&&list.get(0).get(i)<list.get(1).get(i-num0))||
                            (i>num0&&list.get(0).get(i-1)<list.get(1).get(i-num0-1)&&list.get(0).get(i)>list.get(1).get(i-num0)))){
                        a=i;
                        aa=true;
                    }
                    if(!bb&&i>num0&&(list.get(1).get(i-num0).equals(list.get(3).get(i))||
                            (i>num0&&list.get(1).get(i-num0-1)>list.get(3).get(i-1)&&list.get(1).get(i-num0)<list.get(3).get(i))||
                            (i>num0&&list.get(1).get(i-num0-1)<list.get(3).get(i-1)&&list.get(1).get(i-num0)>list.get(3).get(i)))){
                        b=i;
                        bb=true;
                    }

                    if(!cc&&i>num1&&(list.get(3).get(i).equals(list.get(2).get(i-num1))||
                            (i>num1&&list.get(3).get(i-1)>list.get(2).get(i-num1-1)&&list.get(3).get(i)<list.get(2).get(i-num1))||
                            (i>num1&&list.get(3).get(i-1)<list.get(2).get(i-num1-1)&&list.get(3).get(i)>list.get(2).get(i-num1)))){
                        Log.e(TAG,i+"");
                        c=i;
                        cc=true;
                    }
                }
            }
            Log.e(TAG,"a:"+a+" b:"+b+" c:"+c);
            for(int i=0;i<list.get(0).size()&&i-num0<list.get(1).size()&&i-num1<list.get(2).size()&&i<list.get(3).size();i++){
                if(i<a){
                    res.add(list.get(0).get(i));
                }else if(i<b){
                    res.add(list.get(1).get(i-num0));
                }else if(i<c){
                    res.add(list.get(3).get(i));
                }else{
                    res.add(list.get(2).get(i-num1));
                    if(!(i+1<list.get(0).size()&&i+1-num0<list.get(1).size()&&i+1-num1<list.get(2).size()&&i+1<list.get(3).size())){
                        while(i-num1<list.get(2).size()){
                            res.add(list.get(2).get(i-num1));
                            i++;
                        }
                    }
                }
            }
        }
        return res;
    }

    //四组数据全采集版本
    public static ArrayList<Float> getRongHe2(ArrayList<ArrayList<Float>> list){
        ArrayList<Float> res=new ArrayList<>();
        int a=list.get(0).size(),b=list.get(1).size(),c=list.get(2).size();//记录交点位置
        int arr[]={0,0,0};
        int index=0;
        int i=0;
        while(i<2500){
            if(index<3){
                if(list.get(index).get(i)==list.get(index+1).get(i)
                        ||(i>1&&i<2499&&list.get(index).get(i-1)>list.get(index+1).get(i-1)&&list.get(index).get(i+1)<list.get(index+1).get(i+1))
                        ||(i>1&&i<2499&&list.get(index).get(i-1)<list.get(index+1).get(i-1)&&list.get(index).get(i+1)>list.get(index+1).get(i+1))){
                   arr[index]=i;
                   index++;
                }
            }
            i++;
        }
        Log.e(TAG, Arrays.toString(arr));
        for(int j=0;j<arr[0];j++){
            res.add(list.get(0).get(j));
        }
        for(int j=arr[0];j<arr[1];j++){
            res.add(list.get(1).get(j));
        }
        for(int j=arr[1];j<arr[2];j++){
            res.add(list.get(2).get(j));
        }
        for(int j=arr[2];j<2500;j++){
            res.add(list.get(3).get(j));
        }
        return res;
    }


    /**
         *
         * @param dataFilePathname 文件名
         * @return           返回原始数据集合数组，解析二进制数据
         */
        public static ArrayList<Float> binaryToDecimal(String dataFilePathname){
            if (dataFilePathname!=null){
                //todo
                File file = new File(dataFilePathname);
                /*if(!dataFilePathname.equals(Constants.DATA_DIRECTORY + "/" + "textData.dat")){
                    if(!file.exists()||file.length()<1){
                        return ChartDataAnalysis .getSinData(0.2f,1.0f,100).get(0);
                    }
                }*/
                long fileSize= file.length();//返回文件长度，字节为单位
                int DotNumber=(int)fileSize/400;//每个数据32位(4字节）,前四位为校验位（1->...->8)
                ArrayList<Float> lists=new ArrayList<>();
                RandomAccessFile readFile = null;
                byte[] buffer =new byte[400];
                int len = 0;
                Log.e(TAG, "fileSize=" +fileSize);
                Log.e(TAG, "DotNumber=" +DotNumber);
                try {
                    int count=0;
                    while(count<DotNumber){
                        count++;
                        readFile=new RandomAccessFile(file, "r");
                        len=readFile.read(buffer);
                        Log.e(TAG, "count=" +count);
                        for(int i = 0; i < 100; i++){
                            //Log.e(TAG, "i=" +i);
                            if (len < 400) {
                                break;
                            }
                            int begin=i*4;
                            //Log.e(TAG, "begin=" +begin);
                            int mark=(buffer[begin]&0xf0)>>4;
                            int data =0;
                            float data_f=0;
                            float vref= (float) 3.3;
                            if((buffer[begin]&0x08)>>>3==(byte)1){
                                data =((buffer[begin]&0x0f)|0xf0)<<24
                                        |(buffer[begin+1]&0xff)<<16
                                        |(buffer[begin+2]&0xff)<<8
                                        |(buffer[begin+3]&0xff);
                            }else{
                                data =(buffer[begin]&0x0f)<<24
                                        |(buffer[begin+1]&0xff)<<16
                                        |(buffer[begin+2]&0xff)<<8
                                        |(buffer[begin+3]&0xff);
                            }
                            Log.e(TAG, "data=" +data);
                            data_f= (float) ((float) data*(vref/((Math.pow(2,23)-1)))/5);
                            Log.e(TAG, "data_f=" +data_f);
                            lists.add(data_f);
                        }
                    }

                    /*readFile=new RandomAccessFile(file, "r");
                    int count=DotNumber/100;//400位读一次，fileSize/400=DotNumber/100
                    while(count--!=0){
                        len=readFile.read(buffer);
                        if (len < 400) {
                            break;
                        }
                        for(int i = 0; i < DotNumber; i++){
                            int begin=i*4;
                            int mark=(buffer[begin]&0xf0)>>4;
                            int data =0;
                            float data_f=0;
                            if((((buffer[begin])>>3)&1)==1){
                                //负数
                                data=(buffer[begin]&0x07)<<24
                                        |(buffer[begin+1]&0xff)<<16
                                        |(buffer[begin+2]&0xff)<<8
                                        |(buffer[begin+3]&0xff);
                                data=data-1;
                                int dataTemp=0;
                                for(int m=26;m>=0;m--){
                                    if(((data>>m)&1)==0){
                                        dataTemp|=1<<m;
                                    }
                                }
                                data=-dataTemp;
                                Log.e(TAG, "负数data=" +data);
                            }else{
                                data =(buffer[begin]&0x0f)<<24
                                        |(buffer[begin+1]&0xff)<<16
                                        |(buffer[begin+2]&0xff)<<8
                                        |(buffer[begin+3]&0xff);
                                Log.e(TAG, "data=" +data);
                            }
                            data_f= (float) (data/((Math.pow(2,23)-1)))/5;
                            lists.add(data_f);
                        }
                    }*/
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "list.size()0"+lists.size());
                return lists;
            }else{
                return ChartDataAnalysis.getSin(4f,1.0f,1000).get(0);
            }
        }


    public static ArrayList<Complex> getSingleFFTarray(ArrayList<Float> a,double signalFrequency,int sampleRate,int sampleDots){

            ArrayList<Complex> res=new ArrayList<>();
            double[] arr=new double[a.size()];
            for(int j=0;j<a.size();j++){
                arr[j]=a.get(j);
            }
            Complex ui=executeSingleFFT(arr,a.size(),signalFrequency,sampleRate);
            //Complex ui=executeSingleFFT(arr,sampleDots,signalFrequency,sampleRate);
        res.add(ui);
        return res;
    }
    /**
     * 进行傅里叶变换，并获得傅里叶变换之后的复数值
     * @param firstChannelFFT  进行fft的数据
     * @param sampleDots       采样点数
     * @param signalFrequency  信号频率数组
     * @param sampleRate       采样率
     * @return                 相应信号频率下对应的正弦波的幅度值
     */
    public static Complex executeSingleFFT(double[] firstChannelFFT, int sampleDots, double signalFrequency, int sampleRate) {
        Log.e("FFT","sampleDots"+sampleDots+"  "+firstChannelFFT.length);
        DoubleFFT_1D DoubleFFT_1D = new DoubleFFT_1D(sampleDots);
        DoubleFFT_1D.realForward(firstChannelFFT);
        //信号分辨率:采样率除以采样点数
        double signalResolution = (double) sampleRate / sampleDots;
        //用信号频率除以信号分辨率就可以得到角标，当信号频率存在小数的时候要四舍五入
        int count = (int) Math.round(signalFrequency/ signalResolution);
        double im = firstChannelFFT[count * 2 + 1];
        double re = firstChannelFFT[count * 2];

        Log.e("FFT","count:"+count+"  signalResolution"+signalResolution);

        for(int i=0;i * 2 + 1<20;i++){//firstChannelFFT.length
            double im1 = firstChannelFFT[i * 2 + 1];
            double re1 = firstChannelFFT[i * 2];
            Float max= (float)Math.sqrt(re1 * re1 + im1 * im1) / (firstChannelFFT.length / 2);
            Log.e("FFT","i:"+i+"   max:"+max);
        }

        Complex Ui = new Complex(re,im);
        Log.e("FFT",signalFrequency+"Hz信号幅值为："+Math.sqrt(re * re + im * im) / (firstChannelFFT.length / 2));

        // Log.d("实部虚部", "GETrun: "+"im:"+im+"re:"+re);
        //Log.d("幅度值", "GETrun: "+(float) Math.sqrt(re * re + im * im)/(firstChannelFFT.length / 2));
        //Log.d("相位", "GETrun: "+Math.atan2(im,re)/Math.PI*180);
        return Ui;
    }


}
