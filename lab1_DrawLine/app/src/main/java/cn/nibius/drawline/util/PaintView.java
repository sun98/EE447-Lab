package cn.nibius.drawline.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class PaintView extends View {

    private static String TAG = "PaintView";
    private Canvas canvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Bitmap bitmap;
    private Paint mPaint, savePaint;

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
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
        mPath = new Path();

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
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        if (eraserPath != null) {
            canvas.drawPath(eraserPath, eraserPaint);
        }
    }

    private void drawMove(float x, float y) {
        mPath.reset();
        switch (model) {
            case 1:
                float radius = (float) Math.sqrt((x - mX) * (x - mX) + (y - mY) * (y - mY));
                mPath.addCircle(mX, mY, radius, Path.Direction.CW);
                break;
            case 2:
                mPath.addOval(mX, mY, x, y, Path.Direction.CW);
                break;
            case 3:
                if (x >= mX && y >= mY) mPath.addRect(mX, mY, x, y, Path.Direction.CW);
                else if (x >= mX && y < mY) mPath.addRect(mX, y, x, mY, Path.Direction.CW);
                else if (x < mX && y >= mY) mPath.addRect(x, mY, mX, y, Path.Direction.CW);
                else mPath.addRect(x, y, mX, mY, Path.Direction.CW);
                break;
            default:
                break;
        }
    }

    private void drawUp(float x, float y) {
        drawMove(x, y);

        Paint tmp = new Paint(mPaint);
        canvas.drawPath(mPath, tmp);
        undoPaths.add(currentDraw);
        mPath = null;
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
                mPath = new Path();
                savePaint = new Paint(mPaint);
                currentDraw = new Draw();
                currentDraw.path = mPath;
                currentDraw.paint = savePaint;

                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isEraser) {
                    eraserPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                    invalidate();
                    break;
                }
                if (modelToPaintSpecialShape) {
                    drawMove(x, y);
                    invalidate();
                    break;
                }
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (isEraser) {
                    eraserPath.lineTo(mX, mY);
                    canvas.drawPath(eraserPath, eraserPaint);
                    undoPaths.add(currentDraw);
                    eraserPath = null;
                    invalidate();
                    break;
                }
                if (modelToPaintSpecialShape) {
                    drawUp(x, y);
                    invalidate();
                    break;
                }
                mPath.lineTo(mX, mY);
                canvas.drawPath(mPath, mPaint);
                undoPaths.add(currentDraw);
                mPath = null;

                invalidate();
                break;
        }
        return true;
    }

    //设置画笔颜色
    public void setPaintColor(int Color) {
        mPaint.setColor(Color);
    }

    //设置画笔线的类型
    public void setPaintStrokeCap(Paint.Cap a) {
        mPaint.setStrokeCap(a);
    }

    //设置线的宽度
    public void setPaintStrokeWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    //设置抗锯齿
    public void setPaintAntiAlias(boolean flag) {
        mPaint.setAntiAlias(flag);
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
        eraserPaint.setColor(backgroundColor);

        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        invalidate();
    }

    //设置画板背景颜色
    public void setBitmapBackgroundColor(int color) {
        setBackgroundColor(color);
        undoPaths.clear();
        redoPaths.clear();
    }

    //保存bitmap图片
    public Bitmap saveBitmap() {
        return bitmap;
    }

    public void clearBitmap() {
        setBitmapBackgroundColor(backgroundColor);
    }

    public void undo() {
        if (undoPaths != null && undoPaths.size() > 0) {
            setBackgroundColor(backgroundColor);

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

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.i(TAG, "onFocusChanged: " + gainFocus);
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        Log.i(TAG, "onStartTemporaryDetach: ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.i(TAG, "onWindowFocusChanged: " + hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.i(TAG, "onVisibilityChanged: " + visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.i(TAG, "onWindowVisibilityChanged: " + visibility);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow: ");
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        Log.i(TAG, "onScreenStateChanged: " + screenState);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        Log.i(TAG, "onRestoreInstanceState: ");
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        super.onWindowSystemUiVisibilityChanged(visible);
        Log.i(TAG, "onWindowSystemUiVisibilityChanged: " + visible);
    }
}