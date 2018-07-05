package carlsberg.com.hungtp.wifimanager;

import android.net.wifi.ScanResult;

import java.util.List;

public interface OnWifiScanResultsListener {
    void onScanResults(List<ScanResult> scanResults);

}
