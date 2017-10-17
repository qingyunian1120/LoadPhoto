package android.com.shakephone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Context context;
    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_SHRESHOLD = 1700;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 50;
    // 传感器管理器
    private SensorManager sensorManager;
    // 传感器
    private Sensor sensor;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;

    private List<Integer> shakeCounts;
    private List<Integer> twistCounts;
    TextView textView;
    TextView textView2;
    private List<Sensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        textView2 = (TextView) findViewById(R.id.text2);
        Button button = (Button) findViewById(R.id.buttonPanel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("Hello World!");
                textView2.setText("nihao");
                textView.setTextColor(Color.BLACK);
            }
        });
        shakeCounts = new ArrayList<>();
        twistCounts = new ArrayList<>();
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensorx : sensorList) {
        }
        if (sensorManager != null) {
            // 获得重力传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // 注册
            if (sensor != null) {
                sensorManager.registerListener(sensorlistener, sensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }



    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 要做的事情
            if(msg.what == 2) {
                twistCounts.clear();
            }else {
                shakeCounts.clear();
            }
            super.handleMessage(msg);
        }
    };
    public class ShakeThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
                try {
                    Thread.sleep(2000);// 线程暂停2秒，单位毫秒
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);// 发送消息
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
    public class TwistThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(2000);// 线程暂停2秒，单位毫秒
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);// 发送消息
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    SensorEventListener sensorlistener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // 现在检测时间
            long currentUpdateTime = System.currentTimeMillis();
            // 两次检测的时间间隔
            long timeInterval = currentUpdateTime - lastUpdateTime;
            // 判断是否达到了检测时间间隔
            if (timeInterval < UPTATE_INTERVAL_TIME)
                return;
            // 现在的时间变成last时间
            lastUpdateTime = currentUpdateTime;
            // 获得x,y,z坐标
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // 获得x,y,z的变化值
            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z- lastZ;
            // 将现在的坐标变成last坐
            lastX = x;
            lastY = y;
            lastZ = z;
            //向左翻转熄屏
            if(x > 0.2 && x <4.5 && z < -8){
                filpPhone();
            }

            double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                    * deltaZ)
                    / timeInterval * 10000;
            // 摇晃手机打开手电筒
            // 达到速度阀值，发出提示
            if (speed >= SPEED_SHRESHOLD && Math.sqrt(deltaX * deltaX)/ timeInterval * 10000 >600
                    && Math.sqrt(deltaY * deltaY)/ timeInterval * 10000 >600 && Math.abs(deltaX) >20 && Math.abs(deltaY) > 17
                    && Math.abs(deltaZ) < 20) {
                twistCounts.clear();
                new Thread(new ShakeThread()).start();
                if (shakeIsDone()) {
                    shakePhone();
                }
            } else {
                //new Thread(new ShakeThread()).start();
            }
            // 翻转两次手机打开相机
            // 达到速度阀值，发出提示
            if (speed >= 1600 && Math.sqrt(deltaX * deltaX)/ timeInterval * 10000 >600
                    && Math.sqrt(deltaZ * deltaZ)/ timeInterval * 10000 >600 && Math.abs(deltaX) > 10
                    && Math.abs(deltaY) < 10  ) {
                new Thread(new TwistThread()).start();
                if (twistIsDone()) {
                    twistWrist();
                }
            } else {
                //new Thread(new ShakeThread()).start();
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    /**
     * 摇晃次数到达三次
     *
     * @return
     */
    private boolean shakeIsDone() {
        int shakecurrent = shakeCounts.size();
        if (2 == shakecurrent) {
            shakeCounts.clear();
            return true;
        } else {
            shakeCounts.add(1);
            return false;
        }
    }
    /*
    * 翻转次数达到三次
    *
    **/
    private boolean twistIsDone() {
        int flipcurrent = twistCounts.size();
        if (3 == flipcurrent) {
            twistCounts.clear();
            return true;
        } else {
            twistCounts.add(1);
            return false;
        }
    }

    public void shakePhone(){
        textView.setText("摇晃：");
        textView.setTextColor(Color.RED);
    }

    public void twistWrist(){
        textView.setText("翻转：");
    }
    public void filpPhone(){
        textView.setText("熄屏：");
    }

}
