package carlsberg.com.hungtp.wifimanager;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public abstract class ConnectWifiDialog extends Dialog implements View.OnClickListener {
    private EditText mEditTextPassword;

    public abstract void connect(String password);

    private TextView mTextViewSsid;

    public ConnectWifiDialog(Context context) {
        super(context, R.style.ShareDialog);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext().getApplicationContext(), R.layout.dialoag_layout, null);
        mTextViewSsid = (TextView) view.findViewById(R.id.tv_ssid);
        mEditTextPassword = (EditText) view.findViewById(R.id.et_pwd);
        Button mButtonCancel = (Button) view.findViewById(R.id.btn_cancel);
        mButtonCancel.setOnClickListener(this);
        Button mButtonConnect = (Button) view.findViewById(R.id.btn_connect);
        mButtonConnect.setOnClickListener(this);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public ConnectWifiDialog setSsid(String ssid) {
        mTextViewSsid.setText(ssid);
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:

                dismiss();
                break;
            case R.id.btn_connect:
                String pwd = mEditTextPassword.getText().toString();
                if (TextUtils.isEmpty(pwd)) {

                } else {
                    connect(pwd);
                    dismiss();
                }
                break;
        }
    }
}
