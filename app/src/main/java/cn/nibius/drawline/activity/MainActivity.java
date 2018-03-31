package cn.nibius.drawline.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nibius.drawline.R;
import cn.nibius.drawline.util.PaintView;
import cn.nibius.drawline.util.ToastUtil;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_color)
    Button btnColor;
    @BindView(R.id.btn_red)
    Button btnRed;
    @BindView(R.id.btn_yellow)
    Button btnYellow;

    private String TAG = "draw";
    private Context context;
    private LayoutInflater inflater;
    private EasyPopup popColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();
        inflater = LayoutInflater.from(context);
        initView();
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        LinearLayout paintLayout = findViewById(R.id.paint_layout);
        PaintView paintView = (PaintView) inflater.inflate(R.layout.paint_view, null);
        paintLayout.addView(paintView);

        popColor=new EasyPopup(this)
                .setContentView(R.layout.pop_color)
                .setFocusAndOutsideEnable(true)
                .createPopup();

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: ");
                popColor.showAsDropDown(view);
            }
        });
    }
}
