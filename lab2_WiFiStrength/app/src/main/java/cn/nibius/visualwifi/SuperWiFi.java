package cn.nibius.visualwifi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Nibius at 2018/4/10 10:50.
 */


public class SuperWiFi extends MainActivity {//The class of the parameter of WiFi
    static final String TAG = "SuperWiFi";
    static SuperWiFi wifi = null;
    static Object sync = new Object();
    static int TESTTIME = 25;//Number of measurement
    WifiManager wm = null;
    private Vector<String> scanned = null;
    boolean isScanning = false;
    private int[] APRSS = new int[10];
    private FileOutputStream out;
    private int p;

    public SuperWiFi(Context context) {
        this.wm = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);
        this.scanned = new Vector<String>();
    }

    public void ScanRss() {
        startScan();
    }

    public boolean isscan() {
        return isScanning;
    }

    public Vector<String> getRSSlist() {
        return scanned;
    }

    private void startScan() {//The start of scanning

        this.isScanning = true;
        Thread scanThread = new Thread(() -> {
            scanned.clear();//Clear last result
            for (int j = 1; j <= 10; j++) {
                APRSS[j - 1] = 0;
            }
            p = 1;
            //Record the test time and write into the SD card
            SimpleDateFormat formatter = new SimpleDateFormat
                    ("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
            Date curDate = new Date(System.currentTimeMillis());
            //Get the current time
            String str = formatter.format(curDate);
            for (int k = 1; k <= 10; k++) {
                write2file("RSS-IWCTAP" + k + ".txt", "testID: " + testID + "TestTime:" + str + " BEGIN\n ");
            }
            while (p <= TESTTIME) {//Scan for a certain times

                performScan();
                p = p + 1;
            }
            for (int i = 1; i <= 10; i++) {//Record the average of the result
                scanned.add("IWCTAP" + i + "= "
                        + APRSS[i - 1] / TESTTIME + "\n");
            }
            for (int k = 1; k <= 10; k++) {//Mark the end of the test in the file
                write2file("RSS-IWCTAP" + k + ".txt", "testID:" + testID + "END\n");
            }
            isScanning = false;
        });
        scanThread.start();
    }

    private void performScan()//The realization of the test
    {
        if (wm == null)
            return;
        try {
            if (!wm.isWifiEnabled()) {
                wm.setWifiEnabled(true);
            }
            wm.startScan();//Start to scan
            try {
                Thread.sleep(3000);//Wait for 3000ms
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.scanned.clear();
            List<ScanResult> sr = wm.getScanResults();
            Iterator<ScanResult> it = sr.iterator();
            while (it.hasNext()) {
                ScanResult ap = it.next();
                for (int k = 1; k <= 10; k++) {
                    if (ap.SSID.equals("IWCTAP" + k)) {//Write the result to the file
                        APRSS[k - 1] = APRSS[k - 1] + ap.level;
                        write2file("RSS-IWCTAP" + k + ".txt", ap.level + "\n");
                    }
                }
            }
//this.isScanning=false;
        } catch (Exception e) {
            this.isScanning = false;
            this.scanned.clear();
            Log.d(TAG, e.toString());
        }
    }

    private void write2file(String filename, String a) {//Write to the SD card
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            // Open a random filestream by Read&Write
            RandomAccessFile randomFile = new
                    RandomAccessFile(Environment.getExternalStorageDirectory().getPath() + filename, "rw");
            // The length of the file(byte)
            long fileLength = randomFile.length();
            // Put the writebyte to the end of the file
            randomFile.seek(fileLength);
            randomFile.writeBytes(a);
            //Log.e("!","!!");
            randomFile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}