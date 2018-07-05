package carlsberg.com.hungtp.wifimanager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WifiListAdapter extends BaseAdapter {
    private static final String TAG = "WifiListAdapter";
    private List<ScanResult> scanResults;
    private Context mContext;

    public WifiListAdapter(Context context) {
        mContext = context.getApplicationContext();
        this.scanResults = new ArrayList<>();
    }

    public void refreshData(List<ScanResult> scanResults) {
        if (null != scanResults) {
            scanResults = WiFiManager.excludeRepetition(scanResults);
            this.scanResults.clear();
            this.scanResults.addAll(scanResults);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return scanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.wifi_items, null);
            holder = new ViewHolder();
            holder.ssid = (TextView) (convertView).findViewById(R.id.ssid);
            holder.imageView = convertView.findViewById(R.id.img_wifi_items);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = scanResults.get(position);
        int level = WifiManager.calculateSignalLevel(scanResult.level, 4);
        String capabilities = scanResult.capabilities;

        if (capabilities.contains("WPA")||capabilities.contains("WEP")) {
            //co mat khau
            if(level==0){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi1_key);
            }
            if(level==1){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi2_key);
            }
            if(level==2){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi3_key);
            }
            if(level==3){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi4_key);
            }

        }  else {
           //ko pass
            if(level==0){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi1_pub);
            }
            if(level==1){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi2_pub);
            }
            if(level==2){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi3_pub);
            }
            if(level==3){
                holder.imageView.setBackgroundResource(R.drawable.img_wifi4_pub);
            }
        }

       holder.ssid.setText(scanResult.SSID);
       return convertView;
    }

    private class ViewHolder {
        private TextView ssid;
        private ImageView imageView;
    }
}
