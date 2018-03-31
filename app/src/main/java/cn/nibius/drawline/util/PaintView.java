package cn.nibius.drawline.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class PaintView extends View {

    private Canvas canvas;
    private Path mpath;
    private Paint mBitmapPaint;
    private Bitmap bitmap;
    private Paint mpaint;

    class Draw {
        Path path;
        Paint paint;
    }

    private ArrayList<Draw> undoPaths, redoPaths;
    private Draw currentDraw;

    private float mX, mY;
    private int width, height;

    private boolean modelToPaintSpecialShape = false;
    private int model;

    private boolean isEraser = false;
    int backgroundColor = Color.WHITE;
    private Paint eraserPaint;
    private Path eraserPath;

    public void initCanvas() {
        undoPaths = new ArrayList<>();
        redoPaths = new ArrayList<>();
        mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setColor(Color.BLACK);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeJoin(Paint.Join.ROUND);
        mpaint.setStrokeCap(Paint.Cap.ROUND);
        mpaint.setStrokeWidth(10);
        mpath = new Path();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        eraserPaint = new Paint(Paint.DITHER_FLAG);
        eraserPaint.setColor(backgroundColor);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setStrokeWidth(20);
    }

    public PaintView(Context c) {
        super(c);
        initCanvas();
    }

    public PaintView(Context c, AttributeSet attrs) {
        super(c, attrs);
        initCanvas();
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCanvas();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(bitmap);

        canvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
        if (mpath != null) {
            canvas.drawPath(mpath, mpaint);
        }
        if (eraserPath != null) {
            canvas.drawPath(eraserPath, eraserPaint);
        }
    }

    private void drawMove(float x, float y) {
        mpath.reset();
        switch (model) {
            case 1:
                float radius = (float) Math.sqrt((x - mX) * (x - mX) + (y - mY) * (y - mY));
                mpath.addCircle(mX, mY, radius, Path.Direction.CW);
                break;
            case 2:
                mpath.addOval(x, y, mX, mY, Path.Direction.CW);
                break;
            case 3:
                mpath.addRect(mX, mY, x, y, Path.Direction.CW);
                break;
            default:
                break;
        }
    }

    private void drawUp(float x, float y) {
        drawMove(x, y);
        canvas.drawPath(mpath, mpaint);
        undoPaths.add(currentDraw);
        mpath = null;
//        modelToPaintSpecialShape = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEraser) {
                    eraserPath = new Path();
                    eraserPath.moveTo(x, y);
                    currentDraw = new Draw();
                    currentDraw.path = eraserPath;
                    currentDraw.paint = eraserPaint;
                    mX = x;
                    mY = y;
                    invalidate();
                    break;
                }
                mpath = new Path();
                currentDraw = new Draw();
                currentDraw.path = mpath;
                currentDraw.paint = mpaint;

                mpath.reset();
                mpath.moveTo(x, y);
                mX = x;
                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (modelToPaintSpecialShape) {
                    drawMove(x, y);
                    invalidate();
                    break;
                }
                if (isEraser) {
                    eraserPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                    invalidate();
                    break;
                }
                mpath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (modelToPaintSpecialShape) {
                    drawUp(x, y);
                    invalidate();
                    break;
                }
                if (isEraser) {
                    eraserPath.lineTo(mX, mY);
                    canvas.drawPath(eraserPath, eraserPaint);
                    undoPaths.add(currentDraw);
                    eraserPath = null;
                    invalidate();
                    break;
                }
                mpath.lineTo(mX, mY);
                canvas.drawPath(mpath, mpaint);
                undoPaths.add(currentDraw);
                mpath = null;

                invalidate();
                break;
        }
        return true;
    }

    //设置画笔颜色
    public void setPaintColor(int Color) {
        mpaint.setColor(Color);
    }

    //设置画笔线的类型
    public void setPaintStrokeCap(Paint.Cap a) {
        mpaint.setStrokeCap(a);
    }

    //设置线的宽度
    public void setPaintStrokeWidth(float width) {
        mpaint.setStrokeWidth(width);
    }

    //设置抗锯齿
    public void setPaintAntiAlias(boolean flag) {
        mpaint.setAntiAlias(flag);
    }

    //设置画板背景颜色
    public void setBitmapBackgroundColor(int color) {
        backgroundColor = color;
        eraserPaint.setColor(backgroundColor);

        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        invalidate();
    }

    //保存bitmap图片
    public Bitmap saveBitmap() {
        return bitmap;
    }

    public void clearBitmap() {
        setBitmapBackgroundColor(Color.WHITE);
        undoPaths.clear();
        redoPaths.clear();
    }

    public void undo() {
        if (undoPaths != null && undoPaths.size() > 0) {
            setBitmapBackgroundColor(Color.WHITE);

            Draw drawPath = undoPaths.get(undoPaths.size() - 1);
            redoPaths.add(drawPath);
            undoPaths.remove(undoPaths.size() - 1);

            Iterator<Draw> iterator = undoPaths.iterator();
            while (iterator.hasNext()) {
                Draw draw = iterator.next();
                canvas.drawPath(draw.path, draw.paint);
            }
            invalidate();
        }
    }

    public void redo() {
        if (redoPaths.size() > 0) {
            Draw draw = redoPaths.get(redoPaths.size() - 1);
            undoPaths.add(draw);
            canvas.drawPath(draw.path, draw.paint);
            redoPaths.remove(redoPaths.size() - 1);
            invalidate();
        }
    }

    public void setPaintMode(int m) {
        if (m == 0) modelToPaintSpecialShape = false;
        else {
            modelToPaintSpecialShape = true;
            model = m;
        }
    }

    public void setEraserOn() {
        if (isEraser) isEraser = false;
        else isEraser = true;
    }


}