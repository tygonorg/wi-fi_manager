package carlsberg.com.hungtp.wifimanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseWiFiManager {
    static WifiManager mWifiManager;

    private static ConnectivityManager mConnectivityManager;

    BaseWiFiManager(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    int setOpenNetwork(@NonNull String ssid) {
        if (TextUtils.isEmpty(ssid)) {
            return -1;
        }
        WifiConfiguration wifiConfiguration = getConfigFromConfiguredNetworksBySsid(ssid);
        if (null == wifiConfiguration) {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = addDoubleQuotation(ssid);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.clear();
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            return addNetwork(wifiConfig);
        } else {
            return wifiConfiguration.networkId;
        }
    }

    int setWEPNetwork(@NonNull String ssid, @NonNull String password) {
        if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(password)) {
            return -1;
        }
        WifiConfiguration wifiConfiguration = getConfigFromConfiguredNetworksBySsid(ssid);
        if (null == wifiConfiguration) {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = addDoubleQuotation(ssid);
            wifiConfig.wepKeys[0] = "\"" + password + "\"";
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            return addNetwork(wifiConfig);
        } else {
            wifiConfiguration.wepKeys[0] = "\"" + password + "\"";
            return updateNetwork(wifiConfiguration);
        }
    }
    int setWPA2Network(@NonNull String ssid, @NonNull String password) {
        if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(password)) {
            return -1;
        }
        WifiConfiguration wifiConfiguration = getConfigFromConfiguredNetworksBySsid(ssid);
        if (null == wifiConfiguration) {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = addDoubleQuotation(ssid);
            wifiConfig.preSharedKey = "\"" + password + "\"";
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.status = WifiConfiguration.Status.ENABLED;
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            return addNetwork(wifiConfig);
        } else {
            wifiConfiguration.preSharedKey = "\"" + password + "\"";
            return updateNetwork(wifiConfiguration);
        }
    }

    public WifiConfiguration getConfigFromConfiguredNetworksBySsid(@NonNull String ssid) {
        ssid = addDoubleQuotation(ssid);
        List<WifiConfiguration> existingConfigs = getConfiguredNetworks();
        if (null != existingConfigs) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals(ssid)) {
                    return existingConfig;
                }
            }
        }
        return null;
    }
    public boolean isWifiEnabled() {
        return null != mWifiManager && mWifiManager.isWifiEnabled();
    }
    boolean isWifiConnected() {
        if (null != mConnectivityManager) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            return null != networkInfo && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
    boolean hasNetwork() {
        if (null != mConnectivityManager) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable();
        }
        return false;
    }
    public WifiInfo getConnectionInfo() {
        if (null != mWifiManager) {
            return mWifiManager.getConnectionInfo();
        }
        return null;
    }

    public void startScan() {
        if (null != mWifiManager) {
            mWifiManager.startScan();
        }
    }

    public List<ScanResult> getScanResults() {
        if (null != mWifiManager) {
            return mWifiManager.getScanResults();
        }
        return null;
    }
    public static ArrayList<ScanResult> excludeRepetition(List<ScanResult> scanResults) {
        HashMap<String, ScanResult> hashMap = new HashMap<>();

        for (ScanResult scanResult : scanResults) {
            String ssid = scanResult.SSID;

            if (TextUtils.isEmpty(ssid)) {
                continue;
            }

            ScanResult tempResult = hashMap.get(ssid);
            if (null == tempResult) {
                hashMap.put(ssid, scanResult);
                continue;
            }

            if (WifiManager.calculateSignalLevel(tempResult.level, 100) < WifiManager.calculateSignalLevel(scanResult.level, 100)) {
                hashMap.put(ssid, scanResult);
            }
        }

        ArrayList<ScanResult> results = new ArrayList<>();
        for (Map.Entry<String, ScanResult> entry : hashMap.entrySet()) {
            results.add(entry.getValue());
        }

        return results;
    }

    private List<WifiConfiguration> getConfiguredNetworks() {
        if (null != mWifiManager) {
            return mWifiManager.getConfiguredNetworks();
        }
        return null;
    }

    boolean saveConfiguration() {
        return null != mWifiManager && mWifiManager.saveConfiguration();
    }

    boolean enableNetwork(int networkId) {
        if (null != mWifiManager) {
            boolean isDisconnect = disconnectCurrentWifi();
            boolean isEnableNetwork = mWifiManager.enableNetwork(networkId, true);
            boolean isSave = mWifiManager.saveConfiguration();
            boolean isReconnect = mWifiManager.reconnect();
            return isDisconnect && isEnableNetwork && isSave && isReconnect;
        }
        return false;
    }

    private int addNetwork(WifiConfiguration wifiConfig) {
        if (null != mWifiManager) {
            int networkId = mWifiManager.addNetwork(wifiConfig);
            if (-1 != networkId) {
                boolean isSave = mWifiManager.saveConfiguration();
                if (isSave) {
                    return networkId;
                }
            }
        }
        return -1;
    }

    private int updateNetwork(WifiConfiguration wifiConfig) {
        if (null != mWifiManager) {
            int networkId = mWifiManager.updateNetwork(wifiConfig);
            if (-1 != networkId) {
                boolean isSave = mWifiManager.saveConfiguration();
                if (isSave) {
                    return networkId;
                }
            }
        }
        return -1;
    }


    public boolean disconnectWifi(int netId) {
        if (null != mWifiManager) {
            boolean isDisable = mWifiManager.disableNetwork(netId);
            boolean isDisconnect = mWifiManager.disconnect();
            return isDisable && isDisconnect;
        }
        return false;
    }


    public boolean disconnectCurrentWifi() {
        WifiInfo wifiInfo = getConnectionInfo();
        if (null != wifiInfo) {
            int networkId = wifiInfo.getNetworkId();
            return disconnectWifi(networkId);
        } else {
            return true;
        }
    }
    public boolean deleteConfig(int netId) {
        if (null != mWifiManager) {
            boolean isDisable = mWifiManager.disableNetwork(netId);
            boolean isRemove = mWifiManager.removeNetwork(netId);
            boolean isSave = mWifiManager.saveConfiguration();
            return isDisable && isRemove && isSave;
        }
        return false;
    }
    public int calculateSignalLevel(int rssi) {
        return WifiManager.calculateSignalLevel(rssi, 5);
    }

    
    public SecurityModeEnum getSecurityMode(@NonNull ScanResult scanResult) {
        String capabilities = scanResult.capabilities;

        if (capabilities.contains("WPA")) {
            return SecurityModeEnum.WPA;
        } else if (capabilities.contains("WEP")) {
            return SecurityModeEnum.WEP;
        } else {
            return SecurityModeEnum.OPEN;
        }
    }
    
    public String addDoubleQuotation(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return "\"" + text + "\"";
    }
}
