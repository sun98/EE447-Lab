package com.remia.lab4_qrcode;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;
import com.remia.lab4_qrcode.R;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.*;
import static com.dou361.dialogui.DialogUIUtils.appContext;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends Activity {
    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Decoding result")
                        .setMessage(scanResult)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);


            Button encoder = (Button)findViewById(R.id.btn_encoder);
            Button decoder = (Button)findViewById(R.id.btn_decoder);
            View.OnClickListener myListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    switch (v.getId()){
                        case R.id.btn_encoder:
                            intent = new Intent(MainActivity.this, TestEncoder.class);
                            startActivity(intent);
                            break;
                        case R.id.btn_decoder:
                            intent = new Intent(MainActivity.this, TestDecoder.class);
                            startActivityForResult(intent, 0);
                            break;
                        default:
                            break;
                    }
                }
            };
            encoder.setOnClickListener(myListener);
            decoder.setOnClickListener(myListener);
        }*/
    /*@BindView(R.id.et_qr_string)
    EditText qrStrEditText;*/
    @BindView(R.id.btn_scan_barcode)
    Button scanBarCodeButton;
    @BindView(R.id.btn_add_qrcode)
    Button generateQRCodeButton;

    /*private TextView resultTextView;
    private EditText qrStrEditText;
    private ImageView qrImgImageView;
    private CheckBox mCheckBox;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DialogUIUtils.init(appContext);

        /*resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
        mCheckBox = (CheckBox) findViewById(R.id.logo);*/

        //Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });
        generateQRCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText inputServer = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Encoder").setView(inputServer)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String contentString = inputServer.getText().toString();
                        if (!contentString.equals("")) {
                            Intent showImageIntent = new Intent(MainActivity.this,QRCodePicture.class);
                            showImageIntent.putExtra(String.valueOf(R.string.ReturnInfo),contentString);
                            startActivity(showImageIntent);
                        } else {
                            makeText(MainActivity.this, "Text can not be empty", LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanResult = bundle.getString("result");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(scanResult);
            builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(scanResult);
                    Toast.makeText(MainActivity.this, "Copy Success",Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog ad = builder.create();
            ad.show();
        }
    }
}
