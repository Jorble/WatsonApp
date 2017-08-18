package com.giant.watsonapp.voice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.giant.watsonapp.MainActivity;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.OrderStatus;
import com.giant.watsonapp.models.Orientation;
import com.giant.watsonapp.models.TimeLineModel;
import com.giant.watsonapp.setting.TtsSettings;
import com.giant.watsonapp.utils.L;
import com.giant.watsonapp.utils.T;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.thirdparty.M;
import com.iflytek.sunflower.FlowerCollector;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.giant.watsonapp.Const.BUS_VOICE_PLAY;
import static com.giant.watsonapp.Const.BUS_VOICE_STOP;

public class VoiceActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TimeLineAdapter mTimeLineAdapter;
    //数据源
    private List<TimeLineModel> mDataList = new ArrayList<>();
    //垂直或水平排列
    private Orientation mOrientation = Orientation.VERTICAL;
    //时间轴之间是否隔开
    private boolean mWithLinePadding = false;

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

    //当前操作的model
    TimeLineModel currentModel;
    //当前操作的model的位置
    int positon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        titleTv.setText("语音导游");

        //设置排列方式
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setHasFixedSize(true);

        initRecyclerView();

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

        mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, MODE_PRIVATE);
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
                if (currentModel != null) {
                    currentModel.setStatus(OrderStatus.COMPLETED);
                    currentModel.setPlaying(false);
                    mTimeLineAdapter.setItem(positon, currentModel);
                }
            } else if (error != null) {
                L.i(error.getPlainDescription(true));
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
     * 根据recyclerview的排列方式决定布局排列方式
     *
     * @return
     */
    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
    }

    /**
     * 初始化
     */
    private void initRecyclerView() {
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
        recyclerView.setAdapter(mTimeLineAdapter);
    }

    /**
     * 设置数据
     */
    private void setDataListItems() {
        TimeLineModel model = new TimeLineModel();
        model.setTitle("亚龙湾热带天堂森林公园");
        model.setUrl("https://baike.baidu.com/item/"+model.getTitle());
        model.setImg("https://dimg06.c-ctrip.com/images/fd/tg/g1/M02/7D/34/CghzfFWwz_uAa1DFABwYw-O18Fg870_D_180_180.jpg");
        model.setMessage("亚龙湾热带天堂森林公园是海南省第一座滨海山地生态观光兼生态度假型森林公园，整个环境呈现热带风格，树木繁多茂密，是很原始的热带自然风格。爬到最高峰，可以从上而下俯瞰亚龙湾。");
        model.setPlaying(false);
        model.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model);

        TimeLineModel model2 = new TimeLineModel();
        model2.setTitle("亚龙湾");
        model2.setUrl("https://baike.baidu.com/item/"+model2.getTitle());
        model2.setImg("https://dimg01.c-ctrip.com/images/fd/tg/g1/M03/7D/32/CghzfFWwz9eAFdICABJIucYkXDg976_D_180_180.jpg");
        model2.setMessage("亚龙湾沙质细腻，海水干净，椰树海风蓝天白云，景色非常优美，不论是看海景还是体验水上运动，都非常不错。");
        model2.setPlaying(false);
        model2.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model2);

        TimeLineModel model3 = new TimeLineModel();
        model3.setTitle("南山文化旅游区");
        model3.setUrl("https://baike.baidu.com/item/"+model3.getTitle());
        model3.setImg("https://dimg02.c-ctrip.com/images/100g050000000fs9o223C_D_180_180.jpg");
        model3.setMessage("南山文化旅游区是著名的佛教圣地，这里108米高的海上观音圣像是标志性景观，南山寺的素食也是非常有名。");
        model3.setPlaying(false);
        model3.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model3);

        TimeLineModel model4 = new TimeLineModel();
        model4.setTitle("天涯海角");
        model4.setUrl("https://baike.baidu.com/item/"+model4.getTitle());
        model4.setImg("https://dimg03.c-ctrip.com/images/fd/tg/g1/M02/7A/7D/CghzfFWwra2AUWzeACOF4nciR4E872_D_180_180.jpg");
        model4.setMessage("从古至今，都流传着许多关于天涯海角的浪漫故事，与“天涯”、“海角”、“南天一柱”等地标石刻合影留念，是许多游客来这里必做的事情。");
        model4.setPlaying(false);
        model4.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model4);

        TimeLineModel model5 = new TimeLineModel();
        model5.setTitle("椰梦长廊");
        model5.setUrl("https://baike.baidu.com/item/"+model5.getTitle());
        model5.setImg("https://dimg05.c-ctrip.com/images/fd/tg/g2/M03/8E/90/CghzgVWxFkKAdOeMACHAZkL2Ob0476_D_180_180.jpg");
        model5.setMessage("椰梦长廊位于三亚湾的海岸线上，葱郁的椰林、洁白的沙滩、一望无际的大海，构成了这里的美景。慢悠悠地走着，或者租辆单车骑行，都无比惬意。");
        model5.setPlaying(false);
        model5.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model5);

        TimeLineModel model6 = new TimeLineModel();
        model6.setTitle("蜈支洲岛");
        model6.setUrl("https://baike.baidu.com/item/"+model6.getTitle());
        model6.setImg("https://dimg05.c-ctrip.com/images/fd/tg/g1/M0B/7F/99/CghzfFWw9WKAbGoyABJRTZ3Fifw127_D_180_180.jpg");
        model6.setMessage("蜈支洲岛的海水清澈，能见度非常高，其中的观日岩是绝佳的观景点。岛上的娱乐设施也很齐全，项目种类丰富。");
        model6.setPlaying(false);
        model6.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model6);

        TimeLineModel model7 = new TimeLineModel();
        model7.setTitle("第一市场");
        model7.setUrl("https://baike.baidu.com/item/"+model7.getTitle());
        model7.setImg("https://dimg08.c-ctrip.com/images/fd/tg/g1/M07/7A/DC/CghzfVWwuHeACFZQABD-1Z8NBvI018_D_180_180.jpg");
        model7.setMessage("第一市场是许多游客吃海鲜的首选之地，这里的海鲜新鲜又便宜，在集贸市场里买了海鲜之后就近找加工店加工，比去大饭店吃要便宜很多。");
        model7.setPlaying(false);
        model7.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model7);

        TimeLineModel model8 = new TimeLineModel();
        model8.setTitle("珠江南田温泉");
        model8.setUrl("https://baike.baidu.com/item/"+model8.getTitle());
        model8.setImg("https://dimg03.c-ctrip.com/images/tg/643/495/089/70fcb8ca31ad4e50b02f658da12aed07_D_180_180.jpg");
        model8.setMessage("珠江南田温泉是三亚知名度非常高的温泉区，汤池的种类丰富，环境也很有特色，给人一种在热带雨林中泡温泉的感觉。");
        model8.setPlaying(false);
        model8.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model8);

        TimeLineModel model9 = new TimeLineModel();
        model9.setTitle("大东海");
        model9.setUrl("https://baike.baidu.com/item/"+model9.getTitle());
        model9.setImg("https://dimg03.c-ctrip.com/images/fd/tg/g3/M08/0B/4B/CggYGVXAOoOAU3K1ACd6fZ_ann4456_D_180_180.jpg");
        model9.setMessage("大东海是一片三面环山的月牙形海湾，这里水清沙软，也比较适合游泳和潜水。大东海一带有许多价格实惠的海景公寓，很适合来此小住些日子。");
        model9.setPlaying(false);
        model9.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model9);

        TimeLineModel model10 = new TimeLineModel();
        model10.setTitle("三亚千古情景区");
        model10.setUrl("https://baike.baidu.com/item/"+model10.getTitle());
        model10.setImg("https://dimg09.c-ctrip.com/images/100a0700000020xmg4C78_D_180_180.jpg");
        model10.setMessage("三亚千古情景区内有许多有意思的小表演，但最值得一看的是大型歌舞秀《三亚千古情》，每一个场景都编排得十分震撼。");
        model10.setPlaying(false);
        model10.setStatus(OrderStatus.INACTIVE);
        mDataList.add(model10);
    }

    @OnClick({R.id.back_iv, R.id.title_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.title_tv:
                break;
        }
    }

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
     * 接收播放或停止音频信息
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message message) {
        if (message.obj == null) return;
        currentModel = (TimeLineModel) message.obj;
        positon = message.arg1;

        switch (message.what) {
            case BUS_VOICE_PLAY:
                playAudio(currentModel.getMessage());
                break;
            case BUS_VOICE_STOP:
                mTts.stopSpeaking();
                break;
        }
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
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
}
