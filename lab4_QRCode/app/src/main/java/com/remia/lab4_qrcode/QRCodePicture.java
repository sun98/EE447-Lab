package com.remia.lab4_qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.makeText;


/**
 * Created by 23533 on 2018/4/28.
 */

public class QRCodePicture extends Activity {
    @BindView(R.id.qr_image)
    ImageView qrImageView;
    @BindView(R.id.save_picture)
    Button savePicture;

    Bitmap qrCodeBitmap;
    public static int MAX_PIC_MEMORY_SIZE = 1500;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.encoder);
        ButterKnife.bind(this);
        int width  = getWindowManager().getDefaultDisplay().getWidth();


        String contentString = getIntent().getStringExtra(String.valueOf(R.string.ReturnInfo));
        qrCodeBitmap = EncodingUtils.createQRCode(contentString, width, width,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        qrImageView.setImageBitmap(qrCodeBitmap);

        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addBitmapToFile(qrCodeBitmap)){
                    makeText(QRCodePicture.this,"Save Success",Toast.LENGTH_LONG);
                }
            }
        });

    }

    public File getBasePath(Context context) {
        return getStoragePath("qrcode");
    }

    public File getStoragePath(String baseName) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        File file = new File(path + "/" + baseName+"/");
        if (!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    public String createImageFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File f = getBasePath(context);
        String path = f.getAbsolutePath() + "/"+timeStamp + ".jpg";
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    public boolean compressBitmapToFile(Bitmap bmp, String outFile) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bmp.compress(Bitmap.CompressFormat.PNG, options, baos);
            while (baos.toByteArray().length / 1024 > MAX_PIC_MEMORY_SIZE && options > 0) {
                baos.reset();
                options -= 10;
                bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return true;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean addBitmapToFile(Bitmap bitmap) {
        String path = createImageFile(this);
        return compressBitmapToFile(bitmap,path);
    }
}