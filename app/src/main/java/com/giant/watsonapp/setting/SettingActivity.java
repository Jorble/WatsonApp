package com.giant.watsonapp.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.App;
import com.giant.watsonapp.Const;
import com.giant.watsonapp.R;
import com.giant.watsonapp.utils.T;
import com.giant.watsonapp.utils.UiUtils;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.mysql_url_et)
    EditText mysqlUrlEt;
    @BindView(R.id.mysql_user_et)
    EditText mysqlUserEt;
    @BindView(R.id.mysql_pwd_et)
    EditText mysqlPwdEt;
    @BindView(R.id.mysql_driver_et)
    EditText mysqlDriverEt;
    @BindView(R.id.stt_user_et)
    EditText sttUserEt;
    @BindView(R.id.stt_pwd_et)
    EditText sttPwdEt;
    @BindView(R.id.conv_user_et)
    EditText convUserEt;
    @BindView(R.id.conv_pwd_et)
    EditText convPwdEt;
    @BindView(R.id.conv_wsid_et)
    EditText convWsidEt;
    @BindView(R.id.vr_apikey_et)
    EditText vrApikeyEt;
    @BindView(R.id.save_bt)
    Button saveBt;
    @BindView(R.id.add_classifier_bt)
    Button addClassifierBt;
    @BindView(R.id.reset_bt)
    Button resetBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);

        initUI();
    }

    @OnClick({R.id.back_iv, R.id.save_bt, R.id.add_classifier_bt, R.id.reset_bt,R.id.iflytek_tts_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.save_bt:
                save();
                break;
            case R.id.add_classifier_bt:
                UiUtils.startActivity(this,ClassifierEditActivity.class);
                break;
            case R.id.reset_bt:
                reset();
                break;
            case R.id.iflytek_tts_bt:
                UiUtils.startActivity(this,TtsSettings.class);
                break;
        }
    }

    /**
     * 初始化界面
     */
    private void initUI(){
        titleTv.setText("设置");

        //数据库
        mysqlUrlEt.setText(App.getCacheString(Const.MYSQL_URL_KEY,Const.MYSQL_URL_DEFAULT));
        mysqlUserEt.setText(App.getCacheString(Const.MYSQL_USER_KEY,Const.MYSQL_USER_DEFAULT));
        mysqlPwdEt.setText(App.getCacheString(Const.MYSQL_PWD_KEY,Const.MYSQL_PWD_DEFAULT));
        mysqlDriverEt.setText(App.getCacheString(Const.MYSQL_DRIVER_KEY,Const.MYSQL_DRIVER_DEFAULT));

        //语音转文本
        sttUserEt.setText(App.getCacheString(Const.WATSON_STT_USER_KEY,Const.WATSON_STT_USER_DEFAULT));
        sttPwdEt.setText(App.getCacheString(Const.WATSON_STT_PWD_KEY,Const.WATSON_STT_PWD_DEFAULT));

        //对话
        convUserEt.setText(App.getCacheString(Const.WATSON_CONV_USER_KEY,Const.WATSON_CONV_USER_DEFAULT));
        convPwdEt.setText(App.getCacheString(Const.WATSON_CONV_PWD_KEY,Const.WATSON_CONV_PWD_DEFAULT));
        convWsidEt.setText(App.getCacheString(Const.WATSON_CONV_WSID_KEY,Const.WATSON_CONV_WSID_DEFAULT));

        //图像识别
        vrApikeyEt.setText(App.getCacheString(Const.WATSON_VR_API_KEY,Const.WATSON_VR_API_DEFAULT));

    }

    /**
     * 保存
     */
    private void save(){
        //数据库
        App.getAcache().put(Const.MYSQL_URL_KEY,mysqlUrlEt.getText().toString());
        App.getAcache().put(Const.MYSQL_USER_KEY,mysqlUserEt.getText().toString());
        App.getAcache().put(Const.MYSQL_PWD_KEY,mysqlPwdEt.getText().toString());
        App.getAcache().put(Const.MYSQL_DRIVER_KEY,mysqlDriverEt.getText().toString());

        //语音转文本
        App.getAcache().put(Const.WATSON_STT_USER_KEY,sttUserEt.getText().toString());
        App.getAcache().put(Const.WATSON_STT_PWD_KEY,sttPwdEt.getText().toString());

        //对话
        App.getAcache().put(Const.WATSON_CONV_USER_KEY,convUserEt.getText().toString());
        App.getAcache().put(Const.WATSON_CONV_PWD_KEY,convPwdEt.getText().toString());
        App.getAcache().put(Const.WATSON_CONV_WSID_KEY,convWsidEt.getText().toString());

        //图像识别
        App.getAcache().put(Const.WATSON_VR_API_KEY,vrApikeyEt.getText().toString());

        T.showShort(this,"保存成功");
        finish();
    }

    /**
     * 恢复默认
     */
    private void reset(){
        //数据库
        mysqlUrlEt.setText(Const.MYSQL_URL_DEFAULT);
        mysqlUserEt.setText(Const.MYSQL_USER_DEFAULT);
        mysqlPwdEt.setText(Const.MYSQL_PWD_DEFAULT);
        mysqlDriverEt.setText(Const.MYSQL_DRIVER_DEFAULT);

        //语音转文本
        sttUserEt.setText(Const.WATSON_STT_USER_DEFAULT);
        sttPwdEt.setText(Const.WATSON_STT_PWD_DEFAULT);

        //对话
        convUserEt.setText(Const.WATSON_CONV_USER_DEFAULT);
        convPwdEt.setText(Const.WATSON_CONV_PWD_DEFAULT);
        convWsidEt.setText(Const.WATSON_CONV_WSID_DEFAULT);

        //图像识别
        vrApikeyEt.setText(Const.WATSON_VR_API_DEFAULT);

        T.showShort(this,"已恢复默认值,请点击[保存]");
    }
}
