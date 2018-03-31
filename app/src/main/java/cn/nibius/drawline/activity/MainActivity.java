package cn.nibius.drawline.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zyyoona7.lib.EasyPopup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nibius.drawline.R;
import cn.nibius.drawline.util.ColorClickListener;
import cn.nibius.drawline.util.PaintView;
import cn.nibius.drawline.util.WidthClickListener;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_color)
    Button btnColor;
    @BindView(R.id.btn_line)
    ImageButton btnLine;
    @BindView(R.id.btn_shape)
    ImageButton btnShape;

    private String TAG = "draw";
    private Context context;
    private LayoutInflater inflater;
    private EasyPopup popColor, popLineWidth;
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
        int[] buttons = {R.id.btn_color_b, R.id.btn_color_r,
                R.id.btn_color_y, R.id.btn_color_bu, R.id.btn_color_g};
        int[] definedColors = {Color.BLACK, Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN};
        for (int i = 0; i < 5; i++) {
            popColor.getView(buttons[i])
                    .setOnClickListener(new ColorClickListener(definedColors[i], popColor, btnColor));
        }
        popLineWidth = new EasyPopup(this)
                .setContentView(R.layout.pop_line_width)
                .setFocusAndOutsideEnable(true)
                .createPopup();
        int[] widths = {1, 3, 5, 10, 20};
        int layouts[] = {R.id.width_1, R.id.width_3, R.id.width_5, R.id.width_10, R.id.width_20};
        for (int i = 0; i < 5; i++) {
            popLineWidth.getView(layouts[i])
                    .setOnClickListener(new WidthClickListener(widths[i], popLineWidth));
        }

        GradientDrawable drawable = (GradientDrawable) btnColor.getBackground();
        drawable.setColor(Color.BLACK);
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
        btnLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popLineWidth.showAsDropDown(view);
            }
        });

        final View.OnClickListener[] shapeListeners = new View.OnClickListener[4];
        final int[] shapeRes = {
                R.drawable.brush,
                R.drawable.ic_circle_outline_black_36dp,
                R.drawable.ellipse_stroked,
                R.drawable.crop_landscape
        };
        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            shapeListeners[i] = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setOnClickListener(shapeListeners[(finalI + 1) % 4]);
                    view.setBackgroundResource(shapeRes[(finalI + 1) % 4]);
                    paintView.setPaintMode((finalI + 1) % 4);
                }
            };
        }
        btnShape.setOnClickListener(shapeListeners[0]);
    }
}
