package com.giant.watsonapp.route;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.chat.ChatActivity;
import com.giant.watsonapp.models.OrderStatus;
import com.giant.watsonapp.models.Orientation;
import com.giant.watsonapp.models.Scenery;
import com.giant.watsonapp.models.SceneryDao;
import com.giant.watsonapp.setting.TtsSettings;
import com.giant.watsonapp.utils.L;
import com.giant.watsonapp.utils.UiUtils;
import com.giant.watsonapp.voice.SceneryAdapter;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.jaeger.library.StatusBarUtil;
import com.race604.drawable.wave.WaveDrawable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.id.message;
import static com.giant.watsonapp.Const.BUS_VOICE_PLAY;
import static com.giant.watsonapp.Const.BUS_VOICE_STOP;
import static com.giant.watsonapp.R.id.loadView;
import static com.giant.watsonapp.R.id.recyclerView;

public class SceneNavActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.img_iv)
    ImageView imgIv;
    @BindView(R.id.name_tv)
    AppCompatTextView nameTv;
    @BindView(R.id.play_iv)
    ImageView playIv;
    @BindView(R.id.message_tv)
    TextView messageTv;
    @BindView(R.id.takePic_tv)
    TextView takePicTv;
    @BindView(R.id.generate_tv)
    TextView generateTv;

    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人 0-"xiaoyan"
    private String voicer = "0";

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private SharedPreferences mSharedPreferences;

    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_nav);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        titleTv.setText("在线导游");

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

        mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, MODE_PRIVATE);

        showRouteChoose();
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            L.i("InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                L.e("初始化tts失败,错误码：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
                L.i("初始化tts成功");
                // 设置参数
                setParam();
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            L.i("开始播放");
            isPlaying=true;
        }

        @Override
        public void onSpeakPaused() {
            L.i("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            L.i("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
//            L.i(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
//            L.i(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                L.i("播放完成");
                //播放完成设置完成标志
                isPlaying=false;
                playIv.setImageResource(R.mipmap.icon_voice_play);
            } else if (error != null) {
                L.i(error.getPlainDescription(true));
                isPlaying=false;
                playIv.setImageResource(R.mipmap.icon_voice_play);
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


    /**
     * 播放语音
     */
    private void playAudio(String source) {
        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            L.e("创建单例失败，与 21001 错误为同样原因，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        // 开始合成
        // 收到onCompleted 回调时，合成结束、生成合成音频
        // 合成的音频格式：只支持pcm格式

        int code = mTts.startSpeaking(source, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //未安装则跳转到提示安装页面
//                mInstaller.install();
                L.i("本地合成需要安装语记app");
            } else {
                L.e("语音合成失败,错误码: " + code);
            }
        }
    }

    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, mSharedPreferences.getString("voice_preference", voicer));
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    /**
     * 选择路线
     */
    private void showRouteChoose(){
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_scene_nav, null);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);


        // 实例化一个ColorDrawable颜色为半透明0xb00000
        ColorDrawable dw = new ColorDrawable(0xb00000);
        window.setBackgroundDrawable(dw);


        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.popwindow_bottom_anim_style);
        // 在底部显示,第一个参数是parent，不是在其中显示，而是通过子控件找到主窗体，随便一个子控件都行
        titleTv.post(()->window.showAtLocation(titleTv, Gravity.BOTTOM, 0, 0));

        // 自然风光线
        TextView followMe1 = (TextView) view.findViewById(R.id.followMe_tv1);
        followMe1.setOnClickListener((View v) ->{
            imgIv.setImageResource(R.mipmap.sanya11);
            nameTv.setText("金龟探海");
            messageTv.setText("在蜈支洲岛东南的观日岩下，有一天然形成的巨石如一只巨大的海龟，头、甲等都清晰可辨尤值得称道的是，在整块巨石的左前方有一露出海面的条状长型岩石，当海水袭来，犹如海龟用脚在划水，动态逼真，仿佛一只巨大的海龟期望回到自己的故乡，正缓缓爬向大海。");
            window.dismiss();
        });

        //休闲徒步线
        TextView followMe2 = (TextView) view.findViewById(R.id.followMe_tv2);
        followMe2.setOnClickListener((View v) ->{
            imgIv.setImageResource(R.mipmap.sanya12);
            nameTv.setText("观海长廊");
            messageTv.setText("在蜈支洲岛西侧，沿海边地形修建了木质走廊和平台，可以沿着走廊观看蜈支洲岛清澈的海水，这一带因为礁石比较多，所以可以看到很螃蟹在礁石上“行走”，如果运气好，在平台上还可以看到成群的热带鱼 。");
            window.dismiss();
        });
    }

    /**
     * 停止播放音频
     *
     *
     */
    private void stopAudio(){
        mTts.stopSpeaking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.back_iv, R.id.title_tv, R.id.play_iv, R.id.takePic_tv, R.id.generate_tv
            ,R.id.chooseRoute_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.title_tv:
                break;
            case R.id.play_iv:
                isPlaying=!isPlaying;
                if(isPlaying){
                    playIv.setImageResource(R.mipmap.icon_voice_stop);
                    playAudio(messageTv.getText().toString());
                }else {
                    playIv.setImageResource(R.mipmap.icon_voice_play);
                    stopAudio();
                }
                break;
            case R.id.takePic_tv:
                ChatActivity.startMyself(context,true);
                break;
            case R.id.generate_tv:
                TravelogActivity.startMyself(this,"wzzd");
                break;
            case R.id.chooseRoute_ll:
                showRouteChoose();
                break;
        }
    }
}
