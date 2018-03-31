package cn.nibius.drawline.util;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.zyyoona7.lib.EasyPopup;

import static cn.nibius.drawline.activity.MainActivity.paintView;

/**
 * Created by Nibius at 2018/3/31 23:16.
 */

public class BgColorClickListener implements View.OnClickListener {
    private int color;
    private EasyPopup easyPopup;
    private Button button;

    public BgColorClickListener(int color, EasyPopup easyPopup, Button button) {
        this.color = color;
        this.easyPopup = easyPopup;
        this.button = button;
    }

    @Override
    public void onClick(View view) {
        GradientDrawable drawable = (GradientDrawable) button.getBackground();
        drawable.setColor(color);
        paintView.setBitmapBackgroundColor(color);
        easyPopup.dismiss();
    }
}
