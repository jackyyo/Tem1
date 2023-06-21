package com.example.tem.iot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.tem.Constants;
import com.example.tem.MainActivity;
import com.example.tem.ParamSaveClass;
import com.example.tem.R;
import com.example.tem.iot.responseObject.IotMessage;
import com.google.gson.Gson;

import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.apache.qpid.jms.transports.TransportOptions;
import org.apache.qpid.jms.transports.TransportSupport;

import java.net.URI;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

/*import javax.naming.Context;
import javax.naming.InitialContext;*/

public class IotMonitorService extends Service {

    private static final String TAG ="IotMonitorService" ;
    private static final int NOTIFICATION_ID = 250;
    private Intent intent=new Intent(Constants.MY_BROAD);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            createNotificationChannel("请等待数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    beginAmqpClient();
                    Log.e(TAG, "run: "+"重新运行amqp客户端" );
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public  void beginAmqpClient(){
        try {
            //连接凭证接入键值。
            String accessKey = "o2ClME1J";
            long timeStamp = System.currentTimeMillis();
            //UserName组装方法，请参见文档：AMQP客户端接入说明。
            String userName = "accessKey=" + accessKey + "|timestamp=" + timeStamp;
            //连接凭证接入码。
            String password = "es3xufK44T0EMFZGdglrsKZWxREKSitt";
            String connectionUrl = "amqps://015f686c71.iot-amqps.cn-north-4.myhuaweicloud.com:5671?amqp.vhost=default&amqp.idleTimeout=8000&amqp.saslMechanisms=PLAIN";

            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("connectionfactory.HwConnectionURL", connectionUrl);
            //队列名，可以使用默认队列DefaultQueue
            String queueName = "DefaultQueue";
            hashtable.put("queue.HwQueueName", queueName);
            hashtable.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
            javax.naming.Context context = new InitialContext(hashtable);
            JmsConnectionFactory cf = (JmsConnectionFactory) context.lookup("HwConnectionURL");
            //同一个链接可创建多个queue,与前面queue.HwQueueName作好配对就行
            Destination queue = (Destination) context.lookup("HwQueueName");

            //信任服务端
            TransportOptions to = new TransportOptions();
            to.setTrustAll(true);
            cf.setSslContext(TransportSupport.createJdkSslContext(to));

            // 创建连接
            Connection connection = cf.createConnection(userName, password);
            ((JmsConnection) connection).addConnectionListener(myJmsConnectionListener);
            // 创建 Session
            // Session.CLIENT_ACKNOWLEDGE: 收到消息后，需要手动调用message.acknowledge()。
            // Session.AUTO_ACKNOWLEDGE: SDK自动ACK（推荐）。
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            // 创建 Receiver Link
            MessageConsumer consumer = session.createConsumer(queue);
            //处理消息有两种方式
            // 1，主动拉数据（推荐），参照receiveMessage(consumer)
            // 2, 添加监听，参照consumer.setMessageListener(messageListener), 服务端主动推数据给客户端，但得考虑接受的数据速率是客户能力能够承受住的
            receiveMessage(consumer);
            // consumer.setMessageListener(messageListener);
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "beginAmqpClient: "+e.getMessage());
        }
    }

    private  void receiveMessage(MessageConsumer consumer) throws JMSException {
        while (true){
            try{
                // 建议异步处理收到的消息，确保receiveMessage函数里没有耗时逻辑。
                Message message = consumer.receive();processMessage(message);
            } catch (Exception e) {
                Log.e(TAG, "receiveMessage hand an exception: " + e.getMessage());
                //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                //   createNotificationChannel("AMQP抛出异常，不能接收数据");
                e.printStackTrace();
                break;////////////////////******************
            }
        }
    }

    /**
     * 在这里处理您收到消息后的具体业务逻辑。
     */
    private  void processMessage(Message message) {
        try {
            String body = message.getBody(String.class); String content = new String(body);
            //如果API在26以上即版本为O则调用startForefround()方法启动服务
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Gson gson=new Gson();
                IotMessage mes=gson.fromJson(content,IotMessage.class);//提供两个参数，分别是json字符串以及需要转换对象的类型。
                IotMessage.NotifyDataBean.BodyBean.ServicesBean.PropertiesBean b=mes.getNotify_data().getBody().getServices().get(0).getProperties();
                createNotificationChannel("收到第"+b.getCheckBit()+"组数据,请及时查看！！！\r\n");
                ParamSaveClass.voltage_group=b.getVoltage();
                ParamSaveClass.current_group=b.getCurrent();
                intent.putExtra("TEMData",content);
                sendBroadcast(intent);
            }
            Log.e(TAG,"receive an message, the content is " + content);
        } catch (Exception e){
            Log.e(TAG,"processMessage occurs error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //Channel ID 必须保证唯一
    //todo
    private static final String CHANNEL_ID = "com.example.a63431.tem1";

    //todo
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String s) {
        //设定的通知渠道名称
        String channelName = "IOT";
        //设置通知的重要程度
        int importance = NotificationManager.IMPORTANCE_LOW;
        //构建通知渠道
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        channel.setDescription("csu_AIoT");
        //点击通知进入app 不太行，页面切换很卡,因为会重新创建活动实例，之前的资源来不及释放
        //解决方法，将活动启动模式换成singleTop,当发现要启动的活动在栈顶时就会直接使用它，不会创建新的活动实例。
        Intent intent =new Intent(this, MainActivity.class);
        PendingIntent pi= PendingIntent.getActivities(this,0, new Intent[]{intent},0);
        //在创建的通知渠道上发送通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.fdmert) //设置通知图标
                .setContentTitle("获取到云平台数据")//设置通知标题
                .setContentText(s)//设置通知内容
                .setContentIntent(pi)
                //.setAutoCancel(true) //用户触摸时，自动关闭
                .setOngoing(true);//设置处于运行状态
        //向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        //将服务置于启动状态 NOTIFICATION_ID指的是创建的通知的ID
        startForeground(NOTIFICATION_ID, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "in onDestroy");
    }
    private static JmsConnectionListener myJmsConnectionListener = new JmsConnectionListener(){
        /**
         * 连接成功建立。
         */
        @Override
        public void onConnectionEstablished(URI remoteURI){
            Log.e(TAG,"onConnectionEstablished, remoteUri:" + remoteURI);
        }

        /**
         * 尝试过最大重试次数之后，最终连接失败。
         */
        @Override
        public void onConnectionFailure(Throwable error){
            Log.e(TAG,"onConnectionFailure, " + error.getMessage());
        }

        /**
         * 连接中断。
         */
        @Override
        public void onConnectionInterrupted(URI remoteURI){
            Log.e(TAG,"onConnectionInterrupted, remoteUri:" + remoteURI);
        }

        /**
         * 连接中断后又自动重连上。
         */
        @Override
        public void onConnectionRestored(URI remoteURI){
            Log.e(TAG,"onConnectionRestored, remoteUri:" + remoteURI);
        }

        @Override
        public void onInboundMessage(JmsInboundMessageDispatch envelope){
            Log.e(TAG,"onInboundMessage, " + envelope);
        }

        @Override
        public void onSessionClosed(Session session, Throwable cause){
            Log.e(TAG,"onSessionClosed, session=" + session + ", cause =" + cause);
        }

        @Override
        public void onConsumerClosed(MessageConsumer consumer, Throwable cause){
            Log.e(TAG,"MessageConsumer, consumer=" + consumer + ", cause =" + cause);
        }

        @Override
        public void onProducerClosed(MessageProducer producer, Throwable cause){
            Log.e(TAG,"MessageProducer, producer=" + producer + ", cause =" + cause);
        }
    };
}
