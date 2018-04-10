package cn.nibius.drawline.util;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;

import com.zyyoona7.lib.EasyPopup;

import cn.nibius.drawline.R;

import static cn.nibius.drawline.activity.MainActivity.paintView;

/**
 * Created by Nibius at 2018/3/31 15:31.
 */

public class ColorClickListener implements View.OnClickListener {
    private int color;
    private EasyPopup easyPopup;
    private Button button;

    public ColorClickListener(int color, EasyPopup easyPopup, Button button) {
        this.color = color;
        this.easyPopup = easyPopup;
        this.button = button;
    }

    @Override
    public void onClick(View view) {
        GradientDrawable drawable = (GradientDrawable) button.getBackground();
        drawable.setColor(color);
        paintView.setPaintColor(color);
        easyPopup.dismiss();
    }
}
