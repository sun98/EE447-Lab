package cn.nibius.drawline.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class PaintView extends View  {

    private Canvas canvas;
    private Path    mpath;
    private Paint   mBitmapPaint;
    private Bitmap bitmap;
    private Paint mpaint;

    class Draw{
        Path path;
        Paint paint;
    }
    private ArrayList<Draw> undopaths;
    private ArrayList<Draw> redopaths;
    private Draw currentDraw;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private int width;
    private int height;

    public void initCanvas(){
        mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setColor(0xFF00FF00);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeJoin(Paint.Join.ROUND);
        mpaint.setStrokeCap(Paint.Cap.ROUND);
        mpaint.setStrokeWidth(10);
        mpath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }
    public PaintView(Context c) {
        super(c);
        undopaths = new ArrayList<Draw>();
        redopaths = new ArrayList<Draw>();
        initCanvas();
    }
    public PaintView(Context c, AttributeSet attrs) {
        super(c,attrs);
        undopaths = new ArrayList<Draw>();
        redopaths = new ArrayList<Draw>();
        initCanvas();
    }
    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        undopaths = new ArrayList<Draw>();
        redopaths = new ArrayList<Draw>();
        initCanvas();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        setMeasuredDimension(width,height);
        bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        canvas = new Canvas(bitmap);  //所有mCanvas画的东西都被保存在了mBitmap中

        canvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);     //显示旧的画布
        if (mpath != null) {
            // 实时的显示
            canvas.drawPath(mpath, mpaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mpath = new Path();
                currentDraw = new Draw();
                currentDraw.path = mpath;
                currentDraw.paint = mpaint;

                mpath.reset();//清空path
                mpath.moveTo(x, y);
                mX = x;
                mY = y;

                invalidate(); //清屏
                break;
            case MotionEvent.ACTION_MOVE:
                mpath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mpath.lineTo(mX, mY);
                canvas.drawPath(mpath, mpaint);
                undopaths.add(currentDraw);
                mpath = null;

                invalidate();
                break;
        }
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

    public void clearbitmap(){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
        undopaths.clear();
        redopaths.clear();
    }

    public void undo(){
        if(undopaths != null && undopaths.size() > 0){
            clearbitmap();

            Draw drawPath = undopaths.get(undopaths.size() - 1);
            redopaths.add(drawPath);
            undopaths.remove(undopaths.size() - 1);

            Iterator<Draw> iterator = undopaths.iterator();
            while (iterator.hasNext()) {
                Draw draw = iterator.next();
                canvas.drawPath(draw.path, draw.paint);
            }
            invalidate();
        }
    }

    public void redo(){
        if(redopaths.size() > 0){
            Draw draw = redopaths.get(redopaths.size() - 1);
            undopaths.add(draw);
            canvas.drawPath(draw.path, draw.paint);
            redopaths.remove(redopaths.size() - 1);
            invalidate();
        }
    }

}