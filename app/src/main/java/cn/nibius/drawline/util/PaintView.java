package cn.nibius.drawline.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class PaintView extends View {
    private Canvas canvas;
    private Paint mpaint;
    private Path mpath;
    private Bitmap bitmap;
    private int width,height;
    float x, y;

    private ArrayList<Pair<Path, Paint>> paths = new ArrayList<>();
    private ArrayList<Pair<Path, Paint>> undonePaths = new ArrayList<>();

    public PaintView(Context context) {
        super(context);

        mpaint = new Paint(Paint.DITHER_FLAG);
        mpaint.setAntiAlias(true);                //设置抗锯齿，一般设为true
        mpaint.setColor(Color.RED);              //设置线的颜色
        mpaint.setStrokeCap(Paint.Cap.ROUND);     //设置线的类型
        mpaint.setStrokeWidth(8);                //设置线的宽度
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mpaint = new Paint(Paint.DITHER_FLAG);
        mpaint.setAntiAlias(true);                //设置抗锯齿，一般设为true
        mpaint.setColor(Color.RED);              //设置线的颜色
        mpaint.setStrokeCap(Paint.Cap.ROUND);     //设置线的类型
        mpaint.setStrokeWidth(8);                //设置线的宽度
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mpaint = new Paint(Paint.DITHER_FLAG);
        mpaint.setAntiAlias(true);                //设置抗锯齿，一般设为true
        mpaint.setColor(Color.RED);              //设置线的颜色
        mpaint.setStrokeCap(Paint.Cap.ROUND);     //设置线的类型
        mpaint.setStrokeWidth(8);                //设置线的宽度
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        setMeasuredDimension(width,height);
        bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
    }

    //触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE) {    //拖动屏幕
            canvas.drawLine(x, y, event.getX(), event.getY(), mpaint);   //画线，x，y是上次的坐标，event.getX(), event.getY()是当前坐标
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {    //按下屏幕
            x = event.getX();
            y = event.getY();
            canvas.drawPoint(x, y, mpaint);                //画点
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {    //松开屏幕

        }
        x = event.getX();   //记录坐标
        y = event.getY();
        return true;
    }

    //设置画笔颜色
    public void setPaintColor(int Color){
        mpaint.setColor(Color);
    }

    //设置画笔线的类型
    public void setPaintStrokeCap(Paint.Cap a){
        mpaint.setStrokeCap(a);
    }

    //设置线的宽度
    public void setPaintStrokeWidth(float width){
        mpaint.setStrokeWidth(width);
    }

    //设置抗锯齿
    public void setPaintAntiAlias(boolean flag){
        mpaint.setAntiAlias(flag);
    }

    //设置画板背景颜色
    public void setBitmapBackgroundColor(int Color){
        Bitmap tmp=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
        canvas = new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        bitmap = tmp;
    }

    //保存bitmap图片
    public Bitmap saveBitmap(){
        return bitmap;
    }



    @Override
    public void onDraw(Canvas c) {
        c.drawBitmap(bitmap, 0, 0, null);
        //if (mpath != null) c.drawPath(mpath,mpaint);
    }
}