package com.remia.lab3_pedometer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import static android.content.ContentValues.TAG;
import com.github.mikephil.charting.charts.LineChart;
import com.remia.lab3_pedometer.util.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements SensorEventListener,ChartListener {
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.btn_start)
    Button btnStart;

    private boolean isCount = true;
    private SensorManager sm;
    private TextView AT;

    private AccelChart mAccelChart;
    private StepDetector simpleStepDetector;
    private ProgressBar mProgress;

    int n = 0;
    int numOfPoint = 50;

    private Context context;
    private LayoutInflater inflater;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        /*context = getApplicationContext();
        inflater = LayoutInflater.from(context);
        final LinearLayout circleLayout = findViewById(R.id.circle_progress);
        circleProgress = (CircleProgress) inflater.inflate(R.layout.circle_view, null);
        circleLayout.addView(circleProgress);*/

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        simpleStepDetector = new StepDetector();
        AT = findViewById(R.id.AT);
        mAccelChart = new AccelChart();
        mAccelChart.registerListener(this);
        mAccelChart.initLineChart();

        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isCount = true;
            }
        });
        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isCount = false;
            }
        });
    }

    public void onPause() {
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isCount) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
            n++;
            if (n == numOfPoint){
                n=0;
                mAccelChart.plotupdate(simpleStepDetector.getaccl());
                AT.setText(""+simpleStepDetector.getStepNums());
            }
        }
    }

    public LineChart showChart(){
        return findViewById(R.id.btn_chart);
    }
}

