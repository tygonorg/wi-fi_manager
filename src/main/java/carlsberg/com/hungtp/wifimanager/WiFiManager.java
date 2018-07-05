package carlsberg.com.hungtp.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

public class WiFiManager extends BaseWiFiManager {
    private static final String TAG = "WiFiManager";
    private static WiFiManager mWiFiManager;
    private static CallBackHandler mCallBackHandler = new CallBackHandler();
    private static final int WIFI_STATE_ENABLED = 0;
    private static final int WIFI_STATE_DISABLED = 1;
    private static final int SCAN_RESULTS_UPDATED = 3;
    private static final int WIFI_CONNECT_LOG = 4;
    private static final int WIFI_CONNECT_SUCCESS = 5;
    private static final int WIFI_CONNECT_FAILURE = 6;

    private WiFiManager(Context context) {
        super(context);
    }

    public static WiFiManager getInstance(Context context) {
        if (null == mWiFiManager) {
            synchronized (WiFiManager.class) {
                if (null == mWiFiManager) {
                    mWiFiManager = new WiFiManager(context);
                }
            }
        }
        return mWiFiManager;
    }
    public void openWiFi() {
        if (!isWifiEnabled() && null != mWifiManager) {
            mWifiManager.setWifiEnabled(true);
        }
    }
    public void closeWiFi() {
        if (isWifiEnabled() && null != mWifiManager) {
            mWifiManager.setWifiEnabled(false);
        }
    }
    public boolean connectOpenNetwork(@NonNull String ssid) {
        int networkId = setOpenNetwork(ssid);
        if (-1 != networkId) {
            boolean isSave = saveConfiguration();
            boolean isEnable = enableNetwork(networkId);

            return isSave && isEnable;
        }
        return false;
    }
    public boolean connectWEPNetwork(@NonNull String ssid, @NonNull String password) {
        int networkId = setWEPNetwork(ssid, password);
        if (-1 != networkId) {
            boolean isSave = saveConfiguration();
            boolean isEnable = enableNetwork(networkId);
            return isSave && isEnable;
        }
        return false;
    }
    public boolean connectWPA2Network(@NonNull String ssid, @NonNull String password) {
        int networkId = setWPA2Network(ssid, password);
        if (-1 != networkId) {
            boolean isSave = saveConfiguration();
            boolean isEnable = enableNetwork(networkId);
            return isSave && isEnable;
        }
        return false;
    }
    public static class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                        case WifiManager.WIFI_STATE_ENABLING:
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            mCallBackHandler.sendEmptyMessage(WIFI_STATE_ENABLED);
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            mCallBackHandler.sendEmptyMessage(WIFI_STATE_DISABLED);
                            break;
                        case WifiManager.WIFI_STATE_UNKNOWN:
                        default:
                            break;
                    }
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        boolean isUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    } else {
                    }

                    Message scanResultsMessage = Message.obtain();
                    scanResultsMessage.what = SCAN_RESULTS_UPDATED;
                    scanResultsMessage.obj = wifiManager.getScanResults();
                    mCallBackHandler.sendMessage(scanResultsMessage);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    if (null != wifiInfo && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                        String ssid = wifiInfo.getSSID();
                    }
                    break;
                case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION:
                    boolean isConnected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                    SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                    Message logMessage = Message.obtain();
                    logMessage.what = WIFI_CONNECT_LOG;
                    logMessage.obj = supplicantState.toString();
                    logMessage.obj = supplicantState.toString();
                    mCallBackHandler.sendMessage(logMessage);

                    switch (supplicantState) {
                        case INTERFACE_DISABLED:
                            break;
                        case DISCONNECTED:
                        case INACTIVE:
                            WifiInfo connectFailureInfo = wifiManager.getConnectionInfo();
                            if (null != connectFailureInfo) {
                                Message wifiConnectFailureMessage = Message.obtain();
                                wifiConnectFailureMessage.what = WIFI_CONNECT_FAILURE;
                                wifiConnectFailureMessage.obj = connectFailureInfo.getSSID();
                                mCallBackHandler.sendMessage(wifiConnectFailureMessage);
                                int networkId = connectFailureInfo.getNetworkId();
                                boolean isDisable = wifiManager.disableNetwork(networkId);
                                boolean isDisconnect = wifiManager.disconnect();
                            }
                            break;
                        case SCANNING:

                            break;
                        case AUTHENTICATING:
                            break;
                        case ASSOCIATING:
                            break;
                        case ASSOCIATED:
                            break;
                        case FOUR_WAY_HANDSHAKE:
                            break;
                        case GROUP_HANDSHAKE:
                            break;
                        case COMPLETED:
                            WifiInfo connectSuccessInfo = wifiManager.getConnectionInfo();
                            if (null != connectSuccessInfo) {
                                Message wifiConnectSuccessMessage = Message.obtain();
                                wifiConnectSuccessMessage.what = WIFI_CONNECT_SUCCESS;
                                wifiConnectSuccessMessage.obj = connectSuccessInfo.getSSID();
                                mCallBackHandler.sendMessage(wifiConnectSuccessMessage);
                            }
                            break;
                        case DORMANT:
                            break;
                        case UNINITIALIZED:
                            break;
                        case INVALID:
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private static class CallBackHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WIFI_STATE_ENABLED:
                    if (null != mOnWifiEnabledListener) {
                        mOnWifiEnabledListener.onWifiEnabled(true);
                    }
                    break;
                case WIFI_STATE_DISABLED:
                    if (null != mOnWifiEnabledListener) {
                        mOnWifiEnabledListener.onWifiEnabled(false);
                    }
                    break;
                case SCAN_RESULTS_UPDATED:
                    if (null != mOnWifiScanResultsListener) {
                        List<ScanResult> scanResults = (List<ScanResult>) msg.obj;
                        mOnWifiScanResultsListener.onScanResults(scanResults);
                    }
                    break;
                case WIFI_CONNECT_LOG:
                    if (null != mOnWifiConnectListener) {
                        String log = (String) msg.obj;
                        mOnWifiConnectListener.onWiFiConnectLog(log);
                    }
                    break;
                case WIFI_CONNECT_SUCCESS:
                    if (null != mOnWifiConnectListener) {
                        String ssid = (String) msg.obj;
                        mOnWifiConnectListener.onWiFiConnectSuccess(ssid);
                    }
                    break;
                case WIFI_CONNECT_FAILURE:
                    if (null != mOnWifiConnectListener) {
                        String ssid = (String) msg.obj;
                        mOnWifiConnectListener.onWiFiConnectFailure(ssid);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static OnWifiEnabledListener mOnWifiEnabledListener;

    private static OnWifiScanResultsListener mOnWifiScanResultsListener;

    private static OnWifiConnectListener mOnWifiConnectListener;

    public void setOnWifiEnabledListener(OnWifiEnabledListener listener) {
        mOnWifiEnabledListener = listener;
    }

    public void removeOnWifiEnabledListener() {
        mOnWifiEnabledListener = null;
    }

    public void setOnWifiScanResultsListener(OnWifiScanResultsListener listener) {
        mOnWifiScanResultsListener = listener;
    }

    public void removeOnWifiScanResultsListener() {
        mOnWifiScanResultsListener = null;
    }

    public void setOnWifiConnectListener(OnWifiConnectListener listener) {
        mOnWifiConnectListener = listener;
    }

    public void removeOnWifiConnectListener() {
        mOnWifiConnectListener = null;
    }
}
