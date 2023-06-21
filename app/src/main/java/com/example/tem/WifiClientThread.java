package com.example.tem;

import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.tem.Measurement.DBHelper;
import com.example.tem.Measurement.TemData;
import com.sun.media.sound.FFT;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static com.example.tem.ParamSaveClass.Location;

import org.apache.commons.math3.complex.Complex;

public class WifiClientThread extends Thread{
    private static final String TAG ="WifiClientThread" ;
    //public static boolean threadTextDataState=false;
    private  Socket socket=null;
    private String ip;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;
    private MainActivity mainActivity;
    public int Interval = 100;//多测道间隔点数
    public  boolean stopThreadFlag = false;//检查采集过程中是否终止采集
    //public  boolean stopReceiveFlag = true;//停止接收
    public static boolean chartFlag = false;//图表更新完成

    private int CurrentFlag=0;//电流类型标志 0--小电流，1--大电流
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String path=Constants.DATA_DIRECTORY+"/"+ Location;
    private int StartNum = 0;

    public  int [] samArr = {3,5,7,9,11,13};//下发采样率标识


    String cmd="";

    public WifiClientThread(String ip, Handler handler,MainActivity mainActivity){
        Log.e(TAG,"ClientThread开启");
        this.ip = ip;
        this.handler = handler;
        this.mainActivity=mainActivity;
    }

    //开始采集模板
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        String modfiedTime = formatter.format(date);
        String filePath = Constants.DATA_DIRECTORY+"/"+Location+"/原始数据";
        File file = new File(filePath);
        Log.e(TAG, "1file.exists()" +file.exists());
        if (!file.exists()){
            file.mkdirs();
        }
        String filePath1 = Constants.DATA_DIRECTORY+"/"+Location+"/结果数据";
        File file1 = new File(filePath1);
        Log.e(TAG, "2file.exists()" +file1.exists());
        if (!file1.exists()){
            file1.mkdirs();
        }
        //原始数据文件名： 时间+线号+点号
        String filename = modfiedTime+"("+ParamSaveClass.LineNum+"-"+ParamSaveClass.PointNum+")";
        BufferedOutputStream bs = null;
        StartNum=1;
        handler.obtainMessage(Constants.SET_LOG_MESSAGE,"当前WiFi信号强度"+ControllerFragment.Rssi+"\r\n").sendToTarget();
        while(chartFlag){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

       //中途不停止
        while((!stopThreadFlag)&&!(CurrentFlag==2)&&(StartNum<=ParamSaveClass.Effective)) {
            handler.obtainMessage(Constants.SET_LOG_MESSAGE,"开始第"+StartNum+"次采集！\r\n").sendToTarget();//嵌入式解析，6/6-1=0
            Log.e(TAG,"StartNum;"+StartNum);
            try {
                socket = new Socket("192.168.4.1", Constants.PORT);
                Log.e(TAG,"socket!=null:"+(socket!=null));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (socket == null) {
                handler.obtainMessage(Constants.SET_LOG_MESSAGE, "WiFi连接失败\r\n").sendToTarget();
                break;
            } else {
                handler.obtainMessage(Constants.SET_LOG_MESSAGE, "仪器连接成功\r\n").sendToTarget();
            }

            ChartDataAnalysis.CurrentList.clear();
            ChartDataAnalysis.OddList.clear();
            ChartDataAnalysis.EvenList.clear();

            try {
                socket.setSoTimeout(8000);
                outputStream = socket.getOutputStream();
                if(CurrentFlag==0){
                    //"configR/adc输出速率/第2通道放大倍数(ParamSaveClass.a)/采集次数/同步设置/叠加使能/电流设置/延迟采集/采集点数"
                    cmd="configR/"+samArr[ParamSaveClass.sampleIndex]+"/"+1+"/25/1/1/0/"+88+"/4096\r\n";//ParamSaveClass.num1
                    //cmd="configR/3/"+1+"/25/1/1/0/"+ParamSaveClass.num1+"/4096";
                    sendToBle(cmd);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送开始命令：start0，开始采集第"+ParamSaveClass.LineNum+"测线"+ParamSaveClass.PointNum+"测点小电流\r\n").sendToTarget();//嵌入式解析，6/6-1=0
                    sendToBle(Constants.CMD_RESET);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendToBle(Constants.CMD_START0);
                    File saveDataFile = new File(filePath+"/"+filename+"-"+StartNum+ ".dat");
                    try {
                        bs = new BufferedOutputStream(new FileOutputStream(saveDataFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG,"outputStream.write(start0.getBytes());");
                    Log.e(TAG,"bs="+bs);
                }else if(CurrentFlag==1){
                    //"configR/adc输出速率/第2通道放大倍数(ParamSaveClass.b)/采集次数/同步设置/叠加使能/电流设置/延迟采集/采集点数";
                    cmd="configR/"+samArr[ParamSaveClass.sampleIndex]+"/"+1+"/25/1/1/1/"+4096+"/4096\r\n";//ParamSaveClass.num2
                    //cmd="configR/3/"+1+"/25/1/1/1/"+ParamSaveClass.num2+"/4096";
                    sendToBle(cmd);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送开始命令：start1，开始采集第"+ParamSaveClass.LineNum+"测线"+ParamSaveClass.PointNum+"测点大电流\r\n").sendToTarget();//嵌入式解析，6/6-1=0
                    sendToBle(Constants.CMD_RESET);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendToBle(Constants.CMD_START1);
                    /*File saveDataFile = new File(filePath+"/"+filename+"-"+StartNum+ "B.dat");
                    try {
                        bs = new BufferedOutputStream(new FileOutputStream(saveDataFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }*/
                    Log.e(TAG,"outputStream.write(start1.getBytes());");
                    Log.e(TAG,"bs1="+bs);
                }
                //socket.shutdownOutput();
                inputStream = socket.getInputStream();
                byte[] buffer = new byte[1020];
                int total = 0;//总的数据量
                int singleLength;
                int progress=1024;//进度显示阈值
                Log.e(TAG,"sockettttttttttttt;开始");
                while ((singleLength = inputStream.read(buffer)) != -1) {
                    Log.e(TAG,"111111111111111111111111" );
                    if(!stopThreadFlag)bs.write(buffer, 0, singleLength);
                    //解析
                    for(int i = 0; i < singleLength; i+=4) {
                        int seq=buffer[i]&0xff;
                        Log.e(TAG,"seq："+seq);
                        /*if(seq==0){
                            handler.obtainMessage(Constants.SET_LOG_MESSAGE,seq+"校验位为0，请重新采集！！！\r\n").sendToTarget();
                        }*/
                        /*if(seq!=0&&seq!=15&&seq!=31&&seq!=32&&seq!=47&&seq!=63&&seq!=144&&seq!=208&&seq!=129&&seq!=130&&seq!=131&&seq!=132&&seq!=133&&seq!=193&&seq!=194&&seq!=195&&seq!=196&&seq!=197){
                            handler.obtainMessage(Constants.SET_LOG_MESSAGE,seq+"校验位出错，请重新采集！！！\r\n").sendToTarget();
                            sendToBle(Constants.CMD_STOP);
                            stopThreadFlag=true;
                            break;
                        }*/
                    }

                    bs.flush();
                    total+=singleLength;
                    if(total>progress){
                        String s=progress/1024+"kb";
                        progress+=1024;
                        handler.obtainMessage(Constants.GET_FILE_SIZE,s).sendToTarget();
                    }
                    Log.e(TAG,"total:"+total +"  stopThreadFlag:"+stopThreadFlag);
                    if(stopThreadFlag){
                        //stopReceiveFlag=false;
                        break;
                    }
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Log.e(TAG,"SocketTimeoutException");
                if(CurrentFlag==0){
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"小电流接收数据完成\n").sendToTarget();
                    ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(filePath+"/"+filename+"-"+StartNum+ "S.dat");
                    Log.e(TAG,"小电流数据解析完成"+ChartDataAnalysis.lists2 );
                    chartFlag=true;
                    handler.obtainMessage(Constants.ChartTwoCur0).sendToTarget();
                    //mainActivity.sendCommand(Constants.CMD_STOP);
                    CurrentFlag=2;
                }else if(CurrentFlag==1){
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"大电流接收数据完成\n").sendToTarget();
                    ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(filePath+"/"+filename+"-"+StartNum+ "D.dat");
                    Log.e(TAG,"大电流数据解析完成"+ChartDataAnalysis.lists2);
                    //曲线融合
                    ChartDataAnalysis.lists3 = ChartDataAnalysis.getRongHe(ChartDataAnalysis.lists2,ChartDataAnalysis.CurNum0,ChartDataAnalysis.CurNum1);
                    if(StartNum%2==0){
                        ChartDataAnalysis.EvenList=ChartDataAnalysis.getAverageLists(ChartDataAnalysis.EvenList,ChartDataAnalysis.lists3);
                    }else{
                        ChartDataAnalysis.OddList=ChartDataAnalysis.getAverageLists(ChartDataAnalysis.OddList,ChartDataAnalysis.lists3);
                    }
                    ChartDataAnalysis.ErrorList = ChartDataAnalysis.getErrorLists(ChartDataAnalysis.OddList,ChartDataAnalysis.EvenList);

                    Log.e("FFT",ChartDataAnalysis.lists2.get(1).size()+"");

                    //todo
                    //ArrayList<Complex> temp=ChartDataAnalysis.getSingleFFTarray(ChartDataAnalysis.lists2.get(1),200,625000,12500);
                    
                    
                    handler.obtainMessage(Constants.ChartTwoCur1).sendToTarget();
                    //多测道数据选点
                    ArrayList<Float> DotList = getChartOneData();
                    //数据库记录
                    ResultRecordDBHelper(filePath+"/"+filename+"-"+StartNum+ ".dat",DotList.toString());
                    handler.obtainMessage(Constants.ChartOne).sendToTarget();
                    //mainActivity.sendCommand(Constants.CMD_STOP);
                    CurrentFlag=2;
                }else{
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送机电流数据接收完成\n").sendToTarget();
                    CurrentFlag=0;
                    //mainActivity.sendCommand(Constants.CMD_STOP);
                    StartNum++;
                    break;
                }
            } catch (IOException e) {
                handler.obtainMessage(Constants.SET_LOG_MESSAGE,"IOException异常，网络连接异常\n").sendToTarget();
                Log.e(TAG,"IOException e");
                e.printStackTrace();
            } finally {
                Log.e(TAG, "11111finally" );
                if (socket != null || inputStream != null) {
                    try {
                        if(inputStream != null)inputStream.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.e(TAG, "111111" );
        }

        //接收机等待用户停止
        while(!stopThreadFlag){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        handler.obtainMessage(Constants.FINISH_THREAD).sendToTarget();
    }

    //为多测道选点缓存
    public static ArrayList<Float> getChartOneData() {//2.5M,每个点0.0000004s
        ArrayList<Float> res=new ArrayList<>();
        if(ChartDataAnalysis.lists3==null||ChartDataAnalysis.lists3.size()==0)return res;
        int dot=ChartDataAnalysis.lists3.size()/9;
        Log.e(TAG, "ChartDataAnalysis.lists3:"+ChartDataAnalysis.lists3+"   dot:"+dot);
        dot=10;
        int [] arr={0,20,25,30,35,40,45,50,55};
        for(int i=0;i<9;i++){
            res.add(ChartDataAnalysis.lists3.get(arr[i]));
            Log.e(TAG, " dot/9*i:"+(dot*i));
            //res.add(ChartDataAnalysis.lists3.get(dot*i));
            Log.e(TAG, i+"  ChartDataAnalysis.lists1.get(dot/9*i):"+ChartDataAnalysis.lists3.get(dot*i));
        }
        return res;
    }

    /*public static ArrayList<ArrayList<Float>> getChartOneData() {
        ArrayList<ArrayList<Float>> res = new ArrayList<>();
        //多测道结果缓存
        if(ChartDataAnalysis.lists1==null){
            ChartDataAnalysis.lists1=new ArrayList<>();
        }
        if(ChartDataAnalysis.lists1.size()==0){
            for(int i=0;i<9;i++){
                ChartDataAnalysis.lists1.add(new ArrayList<>());
            }
        }
        if(ChartDataAnalysis.lists3==null||ChartDataAnalysis.lists3.size()==0){
            ChartDataAnalysis.lists3=ChartDataAnalysis.getSin(4f,1.0f,24).get(0);
            res=ChartDataAnalysis.getSin(4f,1.0f,24);
        }else{
            int dot=ChartDataAnalysis.lists3.size()/9;
            Log.e(TAG, "ChartDataAnalysis.lists3:"+ChartDataAnalysis.lists3+"   dot:"+dot);
            if(ChartDataAnalysis.lists1.get(0).size()==ParamSaveClass.PointNum-1){
                for(int i=0;i<9;i++){
                    Log.e(TAG, " dot/9*i:"+(dot*i));
                    ChartDataAnalysis.lists1.get(i).add(ChartDataAnalysis.lists3.get(dot*i));
                    Log.e(TAG, i+"  ChartDataAnalysis.lists1.get(dot/9*i):"+ChartDataAnalysis.lists3.get(dot*i));
                }
            }else if(ChartDataAnalysis.lists1.get(0).size()>ParamSaveClass.PointNum-1){
                for(int i=0;i<9;i++){
                    ChartDataAnalysis.lists1.get(i).remove(ChartDataAnalysis.lists1.get(i).size()-1);
                    ChartDataAnalysis.lists1.get(i).add(ChartDataAnalysis.lists3.get(dot*i));
                    Log.e(TAG, i+"  ChartDataAnalysis.lists1.get(dot/9*i):"+ChartDataAnalysis.lists3.get(dot*i));
                }
            }
            res=ChartDataAnalysis.lists1;
        }
        return res;
    }*/

    //    String file_name,int point,int line,int point_distence,int line_distence,int current_show,int voltage_show,int time_show,int fre_show
    private void ResultRecordDBHelper(String filename,String DotList) {
        Log.e(TAG,"ParamSaveClass.PointNum:"+ParamSaveClass.PointNum);
        Log.e(TAG,"DotList:"+DotList);
        String data=DotList.substring(1,DotList.length()-1);
        Log.e(TAG,"data:"+data);

        TemData td=new TemData(filename,ParamSaveClass.PointNum,ParamSaveClass.LineNum,ParamSaveClass.DotPit,ParamSaveClass.LinePit,ParamSaveClass.Current,ParamSaveClass.Current1,ParamSaveClass.CurrentOffTime,ParamSaveClass.CurrentOffTime1,data);
        DBHelper dbHelper = new DBHelper(mainActivity,Location);
        Log.e(TAG,"dbHelper11111111111111:"+(dbHelper.getAllInformation(ParamSaveClass.PointNum+"",ParamSaveClass.LineNum+"")));
        Log.e(TAG,"dbHelper:"+(dbHelper.getAllInformation(ParamSaveClass.PointNum+"",ParamSaveClass.LineNum+"").size()));
        if(dbHelper.getAllInformation(ParamSaveClass.PointNum+"",ParamSaveClass.LineNum+"").size()==0){
            Log.e(TAG,"dbHelper:InsertData");
            dbHelper.InsertData(td);
        }else{
            Log.e(TAG,"dbHelper:updateData");
            dbHelper.updateData(filename,DotList,ParamSaveClass.PointNum+"",ParamSaveClass.LineNum+"");
        }
    }

        //处理采集数据，存储和计算
    //注释快捷键：/+<abbreviation>+enter
    /**
     *
    	 * @param filename   文件名
    	 * @param a  大/小电流
    **/
    private void DataTreating(String filename,int a){

    }


    //将采集结果以txt形式记录到文件中，方便测量结果界面查询
    //多测道数据记录"Result.txt"，lists1写入
    //衰减曲线记录“Damping.txt”,damping写入
    // 每次记录前判断是否存在文件
    private void ResultRecord(){
        File ResultFile = new File(path+"/Result.txt");
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
        //写入数据
        if(ChartDataAnalysis.lists1==null||ChartDataAnalysis.lists1.size()==0||ChartDataAnalysis.lists1.get(0).size()==0){
            ChartDataAnalysis.lists1=ChartDataAnalysis.getSin(4f,1.0f,24);
        }
        WriteData(ResultFile,ChartDataAnalysis.lists1);
    }

    //结果数据写入txt文件
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


    //发送指令
    private void  sendToBle(String command){
        if (mainActivity.getBluetoothState() != BluetoothChatService.STATE_CONNECTED) {
            //stopThreadFlag=true;
            handler.obtainMessage(Constants.SET_LOG_MESSAGE,"命令发送失败，蓝牙连接断开\r\n").sendToTarget();
        }else {
            mainActivity.sendCommand(command);
        }
    }

    /**
     *
    	 * @param s  记录值
    	 * @param filepath  文件位置（.usf文件）
     * @createtime 2023/3/13 18:29
    **/
    private void SetUSFRecord(String s,String filepath){
        File f = new File(filepath+"/result.usf");
        Log.e(TAG,f.exists()+"");
        try {
            OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(f, true));
            String str="//USF: Universal Sounding Format\n" +
                    "//SOUNDINGS:            1\n" +
                    "//END\n" +
                    " \n" +
                    "/ARRAY:Central Loop TEM    \n" +
                    "/AZIMUTH:       0.0000000\n" +
                    "/DATE:     20230501\n" +
                    "/DAYTIME:       0.0000000\n" +
                    "/COIL_LOCATION:       0.0000000,     0.0000000\n" +
                    "/LOOP_SIZE:    1500.0000000,       0.0000000\n" +
                    "/SWEEPS:            1\n" +
                    "/COIL_SIZE:     225.0000000\n" +
                    "/CURRENT:       3.0286000\n" +
                    "/LOOP_TURNS:            1\n" +
                    "/RAMP_TIME:       0.0000024\n" +
                    "/FREQUENCY:       0.6250000\n" +
                    "/TIME_DELAY:       0.0000000\n" +
                    "/LOCATION:     1000.0000000,       0.0000000,       0.0000000\n" +
                    "/POINTS:           20\n" +
                    "/SOUNDING_NAME: \"200\"\n" +
                    "/LENGTH_UNITS: M\n" +
                    "/VOLTAGE_UNITS: V/AMP\n" +
                    "/END";
            writer.write(str);
            writer.write("INDEX, TIME, VOLTAGE, ERROR_BAR, MASK\r\n" );
            for(int i=0;i<ChartDataAnalysis.lists3.size();i++){
                writer.write(i+"   "+i*ParamSaveClass.time+"    "+ChartDataAnalysis.lists3.get(i)+"   "+ChartDataAnalysis.ErrorList.get(i)+"     "+ParamSaveClass.PointNum+"\r\n" );
            }
            writer.write(LINE_SEPARATOR);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //新建文件记录复位情况
    public static void SetResetFile(String s){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-HH-mm");
        Date date=new Date(System.currentTimeMillis());
        String modfiedTime = formatter.format(date);
        File f = new File(Environment.getExternalStorageDirectory().getPath() +"/log.txt");
        try {
            OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(f, true));
            writer.write(LINE_SEPARATOR);
            writer.append(modfiedTime+":"+s);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化文件大小
     *
     * @param length
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getFormatFileSize(long length) {
        DecimalFormat df = new DecimalFormat("#0.0");
        double size = ((double) length) / (1 << 30);
        if (size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if (size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if (size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }



}
