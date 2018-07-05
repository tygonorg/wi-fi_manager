package carlsberg.com.hungtp.wifimanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public abstract class PermissionsManager {
    private static final String PACKAGE_URL_SCHEME = "package:";
    private Activity mTargetActivity;
    public abstract void authorized(int requestCode);
    public abstract void noAuthorization(int requestCode, String[] lacksPermissions);
    public abstract void ignore(int requestCode);
    public PermissionsManager(Activity targetActivity) {
        mTargetActivity = targetActivity;
    }
    public void checkPermissions(int requestCode, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> lacks = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(mTargetActivity.getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                    lacks.add(permission);
                }
            }

            if (!lacks.isEmpty()) {
                String[] lacksPermissions = new String[lacks.size()];
                lacksPermissions = lacks.toArray(lacksPermissions);
                ActivityCompat.requestPermissions(mTargetActivity, lacksPermissions, requestCode);
            } else {
                authorized(requestCode);
            }
        } else {
            ignore(requestCode);
        }
    }
    public void recheckPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                noAuthorization(requestCode, permissions);
                return;
            }
        }
        authorized(requestCode);
    }
    public static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
        context.startActivity(intent);
    }
}
