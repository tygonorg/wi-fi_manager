package carlsberg.com.hungtp.wifimanager;

public interface OnWifiConnectListener {
    void onWiFiConnectLog(String log);
    void onWiFiConnectSuccess(String SSID);
    void onWiFiConnectFailure(String SSID);
}

