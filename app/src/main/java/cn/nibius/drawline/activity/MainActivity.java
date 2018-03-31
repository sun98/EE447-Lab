package cn.nibius.drawline.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.BindView;
import cn.nibius.drawline.R;
import cn.nibius.drawline.util.PaintView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_black)
    Button btnBlack;
    @BindView(R.id.btn_red)
    Button btnRed;
    @BindView(R.id.btn_yellow)
    Button btnYellow;

    private String TAG = "draw";
    private Context context;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        inflater = LayoutInflater.from(context);
        initView();
    }

    private void initView() {
        Log.i(TAG, "initView: 1");
        LinearLayout paintLayout = findViewById(R.id.paint_layout);
        Log.i(TAG, "initView: 2");
        PaintView paintView = (PaintView) inflater.inflate(R.layout.paint_view, null);
        Log.i(TAG, "initView: 3");
        paintLayout.addView(paintView);
    }
}
