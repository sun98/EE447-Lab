package cn.nibius.drawline.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.zyyoona7.lib.EasyPopup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nibius.drawline.R;
import cn.nibius.drawline.util.ColorClickListener;
import cn.nibius.drawline.util.PaintView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_color)
    Button btnColor;
    @BindView(R.id.btn_line)
    Button btnLine;
    @BindView(R.id.btn_yellow)
    Button btnYellow;

    private String TAG = "draw";
    private Context context;
    private LayoutInflater inflater;
    private EasyPopup popColor;
    public static PaintView paintView;

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
        paintView = (PaintView) inflater.inflate(R.layout.paint_view, null);
        paintLayout.addView(paintView);

        popColor = new EasyPopup(this)
                .setContentView(R.layout.pop_color)
                .setFocusAndOutsideEnable(true)
                .createPopup();
        Button[] btnColors = new Button[5];
        int[] buttons = {R.id.btn_color_b, R.id.btn_color_r, R.id.btn_color_y, R.id.btn_color_bu, R.id.btn_color_g};
        int[] definedColors = {Color.BLACK, Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN};
        for (int i = 0; i < 5; i++) {
            btnColors[i] = popColor.getView(buttons[i]);
            btnColors[i].setOnClickListener(new ColorClickListener(definedColors[i], popColor, btnColor));
        }

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popColor.showAsDropDown(view);
//                动画，暂时会报错，先搁着
//                GridLayout gridColorLayout = findViewById(R.id.grid_color_layout);
//                gridColorLayout.setVisibility(View.INVISIBLE);
//                int cx = (gridColorLayout.getLeft() + gridColorLayout.getRight()) / 2;
//                int cy = (gridColorLayout.getTop() + gridColorLayout.getBottom()) / 2;
//                int finalRadius = Math.max(gridColorLayout.getWidth(), gridColorLayout.getHeight());
//                Animator anim = ViewAnimationUtils.createCircularReveal(gridColorLayout, cx, cy, 0, finalRadius);
//                gridColorLayout.setVisibility(View.VISIBLE);
//                anim.start();
            }
        });
    }
}
