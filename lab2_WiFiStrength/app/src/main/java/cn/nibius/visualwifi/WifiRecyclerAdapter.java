package cn.nibius.visualwifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;

/**
 * Created by Nibius at 2018/4/12 14:41.
 */
public class WifiRecyclerAdapter extends RecyclerView.Adapter<WifiRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    private List<ScanResult> wifiScanResults;
    private Context context;

    WifiRecyclerAdapter(Context context, List<ScanResult> wifiScanResults) {
        this.context = context;
        this.wifiScanResults = wifiScanResults;
    }

    public void updateResults(List<ScanResult> results) {
        this.wifiScanResults = null;
        this.wifiScanResults = results;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_info_item, parent, false);
        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.wifiSSID.setText(wifiScanResults.get(position).SSID);
        int level = WifiUtil.dbm2Int(wifiScanResults.get(position).level);
        int levelImages[] = {
                R.drawable.ic_signal_wifi_0_bar_black_24dp,
                R.drawable.ic_signal_wifi_1_bar_black_24dp,
                R.drawable.ic_signal_wifi_2_bar_black_24dp,
                R.drawable.ic_signal_wifi_3_bar_black_24dp,
                R.drawable.ic_signal_wifi_4_bar_black_24dp
        };
        holder.wifiLevelImage.setImageResource(levelImages[level]);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return wifiScanResults == null ? 0 : wifiScanResults.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    private OnItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView wifiLevelImage;
        TextView wifiSSID;

        ViewHolder(View itemView) {
            super(itemView);
            wifiLevelImage = itemView.findViewById(R.id.wifi_item_image);
            wifiSSID = itemView.findViewById(R.id.wifi_item_ssid);
        }
    }


}
