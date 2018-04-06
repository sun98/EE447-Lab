package cn.nibius.drawline.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zyyoona7.lib.EasyPopup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nibius.drawline.R;
import cn.nibius.drawline.util.BgColorClickListener;
import cn.nibius.drawline.util.ColorClickListener;
import cn.nibius.drawline.util.PaintView;
import cn.nibius.drawline.util.ToastUtil;
import cn.nibius.drawline.util.WidthClickListener;
import me.shaohui.bottomdialog.BottomDialog;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_color)
    Button btnColor;
    @BindView(R.id.btn_line)
    ImageButton btnLine;
    @BindView(R.id.btn_shape)
    ImageButton btnShape;
    @BindView(R.id.btn_bg_color)
    Button btnBgColor;
    @BindView(R.id.btn_clear)
    ImageButton btnClear;
    @BindView(R.id.btn_eraser)
    ImageButton btnEraser;
    @BindView(R.id.btn_undo)
    ImageButton btnUndo;
    @BindView(R.id.btn_redo)
    ImageButton btnRedo;

    private String TAG = "MainActivity";
    private Context context;
    private LayoutInflater inflater;
    private EasyPopup popColor, popLineWidth, popBgColor;
    public static PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();
        inflater = LayoutInflater.from(context);
        initView();
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        final LinearLayout paintLayout = findViewById(R.id.paint_layout);
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
        popColor.getView(R.id.btn_color_c).setOnClickListener(view -> {
            BottomDialog colorDialog = BottomDialog.create(getSupportFragmentManager())
                    .setLayoutRes(R.layout.dialog_color)
                    .setCancelOutside(true);
            colorDialog.setViewListener(v -> {
                EditText colorText = v.findViewById(R.id.edit_color);
                v.findViewById(R.id.btn_color_cancel).setOnClickListener((View v1) -> {
                    colorDialog.dismiss();
                    hideKeyBoard(colorText);
                    popColor.dismiss();
                });
                v.findViewById(R.id.btn_color_confirm).setOnClickListener((View v1) -> {
                    String colorStr = colorText.getText().toString();
                    try {
                        int colorInt = Color.parseColor(colorStr);
                        GradientDrawable drawable = (GradientDrawable) btnColor.getBackground();
                        drawable.setColor(colorInt);
                        paintView.setPaintColor(colorInt);
                        colorDialog.dismiss();
                        hideKeyBoard(colorText);
                        popColor.dismiss();
                    } catch (IllegalArgumentException e) {
                        ToastUtil.showShort(context, R.string.unknown_color);
                    }catch (Exception e){
                        ToastUtil.showShort(context, R.string.unknown_error);
                    }
                });
                showKeyBoard(colorText);
            });
            colorDialog.show();
            ToastUtil.showLong(context, getString(R.string.bug_notation));
        });

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
        popLineWidth.getView(R.id.width_custom).setOnClickListener(view -> {
            BottomDialog lineDialog = BottomDialog.create(getSupportFragmentManager())
                    .setLayoutRes(R.layout.dialog_line)
                    .setCancelOutside(true);
            lineDialog.setViewListener(v -> {
                EditText lineText = v.findViewById(R.id.edit_line);
                v.findViewById(R.id.btn_color_cancel).setOnClickListener((View v1) -> {
                    lineDialog.dismiss();
                    hideKeyBoard(lineText);
                    popLineWidth.dismiss();
                });
                v.findViewById(R.id.btn_color_confirm).setOnClickListener((View v1) -> {
                    try{
                    String lineStr = lineText.getText().toString();
                    float lineInt = Float.parseFloat(lineStr);
                    paintView.setPaintStrokeWidth(lineInt);
                    hideKeyBoard(lineText);
                    lineDialog.dismiss();
                    popLineWidth.dismiss();} catch (Exception e){
                        ToastUtil.showShort(context,R.string.unknown_error);
                    }
                });
                showKeyBoard(lineText);
            });
            lineDialog.show();
            ToastUtil.showLong(context, getString(R.string.bug_notation));
        });

        GradientDrawable drawable = (GradientDrawable) btnColor.getBackground();
        drawable.setColor(Color.BLACK);
        btnColor.setOnClickListener(view -> {
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
        });

        popBgColor = new EasyPopup(this)
                .setContentView(R.layout.pop_color)
                .setFocusAndOutsideEnable(true)
                .createPopup();
        for (int i = 0; i < 5; i++) {
            popBgColor.getView(buttons[i])
                    .setOnClickListener(new BgColorClickListener(definedColors[i], popBgColor, btnBgColor));
        }
        popBgColor.getView(R.id.btn_color_c).setOnClickListener(view -> {
            BottomDialog bgColorDialog = BottomDialog.create(getSupportFragmentManager())
                    .setLayoutRes(R.layout.dialog_color)
                    .setCancelOutside(true);
            bgColorDialog.setViewListener(v -> {
                EditText colorText = v.findViewById(R.id.edit_color);
                v.findViewById(R.id.btn_color_cancel).setOnClickListener((View v1) -> {
                    hideKeyBoard(colorText);
                    bgColorDialog.dismiss();
                    popBgColor.dismiss();
                });
                v.findViewById(R.id.btn_color_confirm).setOnClickListener((View v1) -> {
                    String colorStr = colorText.getText().toString();
                    try {
                        int colorInt = Color.parseColor(colorStr);
                        Log.i(TAG, "initView: " + colorInt);
                        GradientDrawable drawable1 = (GradientDrawable) btnBgColor.getBackground();
                        drawable1.setColor(colorInt);
                        paintView.setBitmapBackgroundColor(colorInt);
                        bgColorDialog.dismiss();
                        hideKeyBoard(colorText);
                        popBgColor.dismiss();
                    } catch (IllegalArgumentException e) {
                        ToastUtil.showShort(context, getString(R.string.unknown_color));
                    }catch (Exception e){
                        ToastUtil.showShort(context, R.string.unknown_error);
                    }
                });
                showKeyBoard(colorText);
            });
            bgColorDialog.show();
            ToastUtil.showLong(context, getString(R.string.bug_notation));
        });

        GradientDrawable drawable1 = (GradientDrawable) btnBgColor.getBackground();
        drawable1.setColor(Color.WHITE);
        btnBgColor.setOnClickListener(view -> popBgColor.showAsDropDown(view));

        btnLine.setOnClickListener(view -> popLineWidth.showAsDropDown(view));

        final View.OnClickListener[] shapeListeners = new View.OnClickListener[4];
        final int[] shapeRes = {
                R.drawable.brush,
                R.drawable.ic_circle_outline_black_36dp,
                R.drawable.ellipse_stroked,
                R.drawable.crop_landscape
        };
        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            shapeListeners[i] = view -> {
                view.setOnClickListener(shapeListeners[(finalI + 1) % 4]);
                view.setBackgroundResource(shapeRes[(finalI + 1) % 4]);
                paintView.setPaintMode((finalI + 1) % 4);
            };
        }
        btnShape.setOnClickListener(shapeListeners[0]);

        btnEraser.setOnClickListener(view -> {
            paintView.setEraserOn();
            // TODO change background
        });
        btnClear.setOnClickListener(view -> paintView.clearBitmap());
        btnUndo.setOnClickListener(view -> paintView.undo());
        btnRedo.setOnClickListener(view -> paintView.redo());
    }

    private void showKeyBoard(EditText et) {
        // TODO: 弹出输入框不起作用
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, 0);
    }

    private void hideKeyBoard(EditText et) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
