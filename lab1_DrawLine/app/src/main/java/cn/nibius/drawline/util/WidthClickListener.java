package cn.nibius.drawline.util;

import android.view.View;

import com.zyyoona7.lib.EasyPopup;

import static cn.nibius.drawline.activity.MainActivity.paintView;

/**
 * Created by Nibius at 2018/3/31 19:10.
 */

public class WidthClickListener implements View.OnClickListener {
    private int width;
    private EasyPopup easyPopup;

    public WidthClickListener(int width, EasyPopup easyPopup) {
        this.width = width;
        this.easyPopup = easyPopup;
    }

    @Override
    public void onClick(View view) {
        paintView.setPaintStrokeWidth((float) width);
        easyPopup.dismiss();
    }
}
