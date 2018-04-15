package cn.nibius.visualwifi;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.bottomdialog.BottomDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    @BindView(R.id.wifi_recycler_view)
    RecyclerView wifiRecycler;
    @BindView(R.id.text_ssid)
    TextView textSSID;
    @BindView(R.id.text_bssid)
    TextView textBSSID;
    @BindView(R.id.text_signal)
    TextView textSignal;
    @BindView(R.id.signal_layout)
    RelativeLayout signalLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_ok)
    Button btnOK;

    private static String TAG = "MainActivity";
    private Context context;
    private List<ScanResult> scanResults;
    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private WifiRecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private int receiveTime = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();
        setSupportActionBar(toolbar);

        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                scanResults = wifiManager.getScanResults();
                Collections.sort(scanResults, (o1, o2) -> Integer.compare(o2.level, o1.level));
                recyclerAdapter.updateResults(scanResults);
                receiveTime = 0 - receiveTime;
                Log.i(TAG, "onReceive: " + receiveTime);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiver, filter);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        refreshLayout.setOnRefreshListener((RefreshLayout refreshLayout) -> {
            scan();
            refreshLayout.finishRefresh(true);
        });
        wifiRecycler.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(context);
        recyclerAdapter = new WifiRecyclerAdapter(context, scanResults);
        recyclerAdapter.setOnItemClickListener((view, position) -> {
            wifiRecycler.setClickable(false);
            btnOK.setVisibility(View.INVISIBLE);
            textSSID.setText(getString(R.string.ssid_));
            textBSSID.setText(getString(R.string.bssid_));
            textSignal.setText(getString(R.string.signal_strength));
            signalLayout.setVisibility(View.VISIBLE);
            int levels[] = new int[10];
            String bssid = scanResults.get(position).BSSID;
            levels[0] = scanResults.get(position).level;
            textSSID.setText(String.format("%s %s", textSSID.getText(), scanResults.get(position).SSID));
            textBSSID.setText(String.format("%s %s", textBSSID.getText(), bssid));
            textSignal.setText(String.format(Locale.getDefault(), "%s\n0: %d%s\n", textSignal.getText(), levels[0], getString(R.string.dbm)));
            new Thread(() -> {
                int oldReceive, total = 0;
                for (int i = 1; i < 10; i++) {
                    oldReceive = receiveTime;
                    scan();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (oldReceive == receiveTime) {
                    }
                    for (ScanResult s : scanResults) {
                        if (bssid.equals(s.BSSID)) {
                            levels[i] = s.level;
                            total += s.level;
                            break;
                        }
                    }
                    int finalI = i;
                    runOnUiThread(() -> textSignal.setText(String.format(Locale.getDefault(), "%s%d: %d%s\n", textSignal.getText(), finalI, levels[finalI], getString(R.string.dbm))));
                }
                int finalTotal = total;
                runOnUiThread(() -> {
                    btnOK.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    textSignal.setText(String.format(Locale.getDefault(), "%sAverage Signal Strength = %d dBm", textSignal.getText(), finalTotal / 10));
                });
            }).start();

        });
        wifiRecycler.setLayoutManager(recyclerLayoutManager);
        wifiRecycler.setAdapter(recyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        scan();
        refreshLayout.autoRefresh();
    }

    @OnClick(R.id.btn_ok)
    public void onOKClick(View view) {
        signalLayout.setVisibility(View.INVISIBLE);
        btnOK.setVisibility(View.INVISIBLE);
        textSSID.setText(getString(R.string.ssid_));
        textBSSID.setText(getString(R.string.bssid_));
        textSignal.setText(getString(R.string.signal_strength));
        wifiRecycler.setClickable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private String getScanString() {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < scanResults.size(); i++) {
            localStringBuilder.append(Integer.toString(i + 1)).append(": ");
            localStringBuilder.append((scanResults.get(i)).toString()).append("\n");
        }
        return localStringBuilder.toString();
    }

    private void scan() {
        if (wifiManager == null) return;
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

}
