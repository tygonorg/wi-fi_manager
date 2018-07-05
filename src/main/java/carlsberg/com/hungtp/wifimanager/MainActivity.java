package carlsberg.com.hungtp.wifimanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnWifiEnabledListener, OnWifiScanResultsListener, OnWifiConnectListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private WiFiManager mWiFiManager;
    private SwitchCompat aSwitch;
    private SwitchCompat bSwitch;
    private ListView lstwifi;
    private WifiListAdapter mWifiListAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private PermissionsManager mPermissionsManager;
    private final int GET_WIFI_LIST_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWiFiManager = WiFiManager.getInstance(getApplicationContext());

        lstwifi = (ListView)findViewById(R.id.wifi_list) ;
        lstwifi.setOnItemClickListener(this);
        lstwifi.setOnItemLongClickListener(this);
        aSwitch = (SwitchCompat) findViewById(R.id.swwifi_on_off);
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWiFiManager.openWiFi();
                    List<ScanResult> scanResults = mWiFiManager.getScanResults();
                    refreshData(scanResults);

                } else {
                    mWiFiManager.closeWiFi();
                }

               // boolean checkwifienable = mWiFiManager.isWifiEnabled();

                aSwitch.setChecked(isChecked);
                mSwipeLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        bSwitch = (SwitchCompat)findViewById(R.id.swtg_on_off);
        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //check 3g
            }
        });
        mWifiListAdapter = new WifiListAdapter(getApplicationContext());
        lstwifi.setAdapter(mWifiListAdapter);
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                if (GET_WIFI_LIST_REQUEST_CODE == requestCode) {
                    List<ScanResult> scanResults = mWiFiManager.getScanResults();
                    refreshData(scanResults);
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                List<ScanResult> scanResults = mWiFiManager.getScanResults();
                refreshData(scanResults);
            }

            @Override
            public void ignore(int requestCode) {
                List<ScanResult> scanResults = mWiFiManager.getScanResults();
                refreshData(scanResults);
            }
        };
        mPermissionsManager.checkPermissions(GET_WIFI_LIST_REQUEST_CODE, PERMISSIONS);

    }
    @Override
    protected void onResume() {
        super.onResume();
        // 添加监听
        mWiFiManager.setOnWifiEnabledListener(this);
        mWiFiManager.setOnWifiScanResultsListener(this);
        mWiFiManager.setOnWifiConnectListener(this);
        boolean checkwifienable = mWiFiManager.isWifiEnabled();

        aSwitch.setChecked(checkwifienable);
        mSwipeLayout.setVisibility(checkwifienable ? View.VISIBLE : View.GONE);

    }
    private boolean check3g(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);


        NetworkInfo mobileInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;
        return mobileConnected;
    }
    private void setMobileDataEnabled(Context context, boolean enabled) {
        final ConnectivityManager conman =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWifiEnabled(boolean enabled) {
        if(enabled) {
            Toast.makeText(getApplicationContext(), "Đã bật Wifi", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(), "Đã tắt Wifi", Toast.LENGTH_LONG).show();
        }
        aSwitch.setChecked(enabled);
        mSwipeLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onScanResults(List<ScanResult> scanResults) {
        refreshData(scanResults);
    }

    @Override
    public void onWiFiConnectLog(String log) {
       // Toast.makeText(getApplicationContext(), "Đang kết nối : "+log , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWiFiConnectSuccess(String SSID) {
        Toast.makeText(getApplicationContext(), "Đã nối tới "+SSID , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWiFiConnectFailure(String SSID) {
        Toast.makeText(getApplicationContext(), "Có lỗi khi nối tới "+SSID , Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mWiFiManager.removeOnWifiEnabledListener();
        mWiFiManager.removeOnWifiScanResultsListener();
        mWiFiManager.removeOnWifiConnectListener();
    }
    public void refreshData(List<ScanResult> scanResults) {
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
        if(aSwitch.isChecked()) {
            //Toast.makeText(getApplicationContext(), "Danh sách wifi đã được cập nhật", Toast.LENGTH_SHORT).show();
            mWifiListAdapter.refreshData(scanResults);
        }
    }

    @Override
    public void onRefresh() {

        mWiFiManager.startScan();
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
        switch (mWiFiManager.getSecurityMode(scanResult)) {
            case WPA:
            case WPA2:
                new ConnectWifiDialog(this) {
                    @Override
                    public void connect(String password) {
                        mWiFiManager.connectWPA2Network(scanResult.SSID, password);
                    }
                }.setSsid(scanResult.SSID).show();
                break;
            case WEP:
                new ConnectWifiDialog(this) {

                    @Override
                    public void connect(String password) {
                        mWiFiManager.connectWEPNetwork(scanResult.SSID, password);
                    }
                }.setSsid(scanResult.SSID).show();
                break;
            case OPEN:
                mWiFiManager.connectOpenNetwork(scanResult.SSID);
                break;
        }

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
        final String ssid = scanResult.SSID;
        new AlertDialog.Builder(this)
                .setTitle(ssid)
                .setItems(new String[]{ "Quên mạng này"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                          //  case 0:
//                                WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();
//                                if (mWiFiManager.addDoubleQuotation(ssid).equals(connectionInfo.getSSID())) {
//                                    mWiFiManager.disconnectWifi(connectionInfo.getNetworkId());
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Hiện không có kết nối nào [ " + ssid + " ]", Toast.LENGTH_SHORT).show();
//                                }
                          //      break;
                            case 0:
                                WifiConfiguration wifiConfiguration = mWiFiManager.getConfigFromConfiguredNetworksBySsid(ssid);
                                if (null != wifiConfiguration) {
                                    boolean isDelete = mWiFiManager.deleteConfig(wifiConfiguration.networkId);
                                    Toast.makeText(getApplicationContext(), isDelete ? "Đã xóa thành công！" : "Đã xảy ra lỗi không mong muốn!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Mạng này không lưu！", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
        return true;
    }
}
