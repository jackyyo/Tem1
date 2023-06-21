package com.example.tem;

import static com.example.tem.ParamSaveClass.Location;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/*
 *a
 */   class WifiThread extends Thread{

    private MainActivity mainActivity;
    private Handler handler;
    private InputStream inputStream;
    public WifiThread(Handler handler,MainActivity mainActivity){
        this.handler = handler;
        this.mainActivity=mainActivity;
    }


    @Override
    public void run() {
        String cmd="";
        Socket socket=null;
        ParamSaveClass.num1=0;
        ParamSaveClass.num2=0;
        float max1=0,max2=0;
        int index = 0;
        String filePath = Constants.DATA_DIRECTORY+"/"+Location+"/预采集";
        File file = new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
        String filename = "("+ParamSaveClass.LineNum+"-"+ParamSaveClass.PointNum+")";
        BufferedOutputStream bs = null;
        File saveDataFile ;

        while(index<4){
            try {
                socket = new Socket("192.168.4.1", Constants.PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (socket == null) {
                handler.obtainMessage(Constants.SET_LOG_MESSAGE, "WiFi连接失败\r\n").sendToTarget();
                break;
            } else {
                handler.obtainMessage(Constants.SET_LOG_MESSAGE, "仪器连接成功\r\n").sendToTarget();
            }
            try{
                socket.setSoTimeout(3000);
                if(index==0){
                    saveDataFile = new File(filePath+"/"+filename+ "1.dat");
                    try {
                        bs = new BufferedOutputStream(new FileOutputStream(saveDataFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //"configR/adc输出速率/第2通道放大倍数/采集次数/同步设置/叠加使能/电流设置/延迟采集/采集点数"
                    cmd="configR/3/1/1/1/0/0/4096/4096";//弱信号通道一直接地不采集（最多16384点）
                }else if(index==1){
                    cmd="configR/3/1/1/1/0/1/4096/4096";
                }else if(index==2){
                    saveDataFile = new File(filePath+"/"+filename+ "2.dat");
                    try {
                        bs = new BufferedOutputStream(new FileOutputStream(saveDataFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    cmd="configR/3/1/1/1/0/0/4096/4096";
                }else{
                    cmd="configR/3/1/1/1/0/1/4096/4096";
                }
                sendToBle(cmd);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sendToBle(Constants.CMD_RESET);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(index==0){
                    sendToBle(Constants.CMD_START2);
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送开始预采集命令：start2\r\n").sendToTarget();//嵌入式解析，6/6-1=0
                }
                if(index==1){
                    sendToBle(Constants.CMD_START3);
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送开始预采集命令：start3\r\n").sendToTarget();//嵌入式解析，6/6-1=0
                }
                if(index==2){
                    sendToBle(Constants.CMD_START0);
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送开始预采集命令：start0\r\n").sendToTarget();//嵌入式解析，6/6-1=0
                }
                if(index==3){
                    sendToBle(Constants.CMD_START1);
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE,"发送开始预采集命令：start1\r\n").sendToTarget();//嵌入式解析，6/6-1=0
                }
                socket.shutdownOutput();
                int total = 0;//总的数据量
                int singleLength;
                int progress=1024;
                byte[] buffer = new byte[1020];
                InputStream inputStream = socket.getInputStream();
                while ((singleLength = inputStream.read(buffer)) != -1) {
                    if(index==0)Log.e("11111111111","while ((singleLength = inputStream.read(buffer)) != -1) ");
                    if(index==1)Log.e("111122222","while ((singleLength = inputStream.read(buffer)) != -1) ");
                    for(int i = 0; i < singleLength; i+=4) {
                        int seq=buffer[i]&0xff;
                    }
                    bs.write(buffer, 0, singleLength);
                    bs.flush();
                    total+=singleLength;
                    if(total>progress){
                        String s=progress/1024+"kb";
                        progress+=1024;
                        handler.obtainMessage(Constants.GET_FILE_SIZE,s).sendToTarget();
                    }
                }
                if(index==1){
                    ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(filePath+"/"+filename+ "1.dat");
                    for(int i=0;i<ChartDataAnalysis.lists2.get(1).size();i++){
                        max1=Math.max(max1,ChartDataAnalysis.lists2.get(1).get(i));
                    }
                    for(int i=0;i<ChartDataAnalysis.lists2.get(2).size();i++){
                        max2=Math.max(max2,ChartDataAnalysis.lists2.get(2).get(i));
                    }
                }
                if(index==3){
                    ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(filePath+"/"+filename+ "2.dat");
                    for(int i=0;i<ChartDataAnalysis.lists2.get(0).size();i++){
                        ParamSaveClass.num1++;
                        if(Math.abs(ChartDataAnalysis.lists2.get(0).get(i))<max1){
                            ParamSaveClass.num1=ParamSaveClass.num1/4;
                            break;
                        }
                    }
                    for(int i=0;i<ChartDataAnalysis.lists2.get(3).size();i++){
                        ParamSaveClass.num2++;
                        if(Math.abs(ChartDataAnalysis.lists2.get(3).get(i))<max2){
                            ParamSaveClass.num2=ParamSaveClass.num2/4;
                            break;
                        }
                    }
                }
                index++;
                Log.e("11111111111",ParamSaveClass.num1+"   "+ParamSaveClass.num2);
            }catch (SocketTimeoutException e) {
                if(index==1){
                    ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(filePath+"/"+filename+ "1.dat");
                    for(int i=0;i<ChartDataAnalysis.lists2.get(1).size();i++){
                        max1=Math.max(max1,ChartDataAnalysis.lists2.get(1).get(i));
                    }
                    for(int i=0;i<ChartDataAnalysis.lists2.get(2).size();i++){
                        max2=Math.max(max2,ChartDataAnalysis.lists2.get(2).get(i));
                    }
                }
                if(index==3){
                    ChartDataAnalysis.lists2 = ChartDataAnalysis.binaryToDecimalTest(filePath+"/"+filename+ "2.dat");
                    for(int i=0;i<ChartDataAnalysis.lists2.get(0).size();i++){
                        ParamSaveClass.num1++;
                        if(Math.abs(ChartDataAnalysis.lists2.get(0).get(i))<max1){
                            ParamSaveClass.num1=ParamSaveClass.num1/4;
                            break;
                        }
                    }
                    for(int i=0;i<ChartDataAnalysis.lists2.get(3).size();i++){
                        ParamSaveClass.num2++;
                        if(Math.abs(ChartDataAnalysis.lists2.get(3).get(i))<max2){
                            ParamSaveClass.num2=ParamSaveClass.num2/4;
                            break;
                        }
                    }
                    handler.obtainMessage(Constants.SET_LOG_MESSAGE, "发射机大小电流弱信号开始采集点num1:"+ParamSaveClass.num1+"  num2:"+ParamSaveClass.num2).sendToTarget();
                }
                index++;

            } catch (IOException e) {
                handler.obtainMessage(Constants.SET_LOG_MESSAGE, "预采集错误，请重新采集\r\n").sendToTarget();
                throw new RuntimeException(e);
            }finally {
                if (socket != null || inputStream != null) {
                    try {
                        if(inputStream != null)inputStream.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        handler.obtainMessage(Constants.ParpareFinish).sendToTarget();
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
}
