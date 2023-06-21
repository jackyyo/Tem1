package com.example.tem.WifiTest;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.tem.Constants;
import com.example.tem.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 *a
 */public class ListenerThread extends Thread {

    private ServerSocket serverSocket = null;
    private Handler handler;
    private int     port;
    private Socket socket;

    public ListenerThread(int port, Handler handler) {
        setName("ListenerThread");
        this.port = port;
        this.handler = handler;
        try {
            serverSocket = new ServerSocket(port);//监听本机的12345端口
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                Log.i("ListennerThread", "阻塞");
                //阻塞，等待设备连接
                if (serverSocket != null)
                    socket = serverSocket.accept();
                Message message = Message.obtain();
                message.what = Constants.DEVICE_CONNECTING1;
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.i("ListennerThread", "error:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
