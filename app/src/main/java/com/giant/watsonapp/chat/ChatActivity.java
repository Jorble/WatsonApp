package com.giant.watsonapp.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.giant.watsonapp.App;
import com.giant.watsonapp.Const;
import com.giant.watsonapp.R;
import com.giant.watsonapp.hotel.HotelDetailActivity;
import com.giant.watsonapp.models.Conversation;
import com.giant.watsonapp.models.ConversationDao;
import com.giant.watsonapp.models.DefaultUser;
import com.giant.watsonapp.models.MyMessage;
import com.giant.watsonapp.photo.CustomHelper;
import com.giant.watsonapp.photo.PhotoViewPop;
import com.giant.watsonapp.utils.JdbcHelper;
import com.giant.watsonapp.utils.KeyBoardUtils;
import com.giant.watsonapp.utils.L;
import com.giant.watsonapp.utils.T;
import com.giant.watsonapp.views.ChatView;
import com.giant.watsonapp.views.CircularRippleButton;
import com.giant.watsonapp.watson.WatsonServiceManager;
import com.giant.watsonapp.web.WebActivity;
import com.github.florent37.viewanimator.ViewAnimator;
import com.ibm.watson.developer_cloud.android.library.audio.AmplitudeListener;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.iflytek.cloud.thirdparty.C;
import com.jaeger.library.StatusBarUtil;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.R.id.list;
import static android.media.CamcorderProfile.get;
import static com.giant.watsonapp.watson.WatsonServiceManager.getSttRecognizeOptions;
import static com.giant.watsonapp.watson.WatsonServiceManager.getVisualRecognitionOptions;
import static rx.schedulers.Schedulers.start;

public class ChatActivity extends TakePhotoActivity implements EasyPermissions.PermissionCallbacks {


    @BindView(R.id.chat_view)
    ChatView chatView;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.msg_list)
    MessageList msgList;
    @BindView(R.id.menu_input_text)
    ImageView menuInputText;
    @BindView(R.id.menu_input_audio)
    ImageView menuInputAudio;
    @BindView(R.id.menu_input_photo)
    ImageView menuInputPhoto;
    @BindView(R.id.chat_input)
    RelativeLayout chatInput;
    @BindView(R.id.text_input_close)
    ImageView textInputClose;
    @BindView(R.id.text_input_content)
    EditText textInputContent;
    @BindView(R.id.text_input_send)
    ImageView textInputSend;
    @BindView(R.id.text_input_ll)
    LinearLayout textInputLl;
    @BindView(R.id.photo_input_close)
    ImageView photoInputClose;
    @BindView(R.id.photo_input_gallery)
    RelativeLayout photoInputGallery;
    @BindView(R.id.photo_input_takephoto)
    RelativeLayout photoInputTakephoto;
    @BindView(R.id.photo_input_ll)
    LinearLayout photoInputLl;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.audio_input_ripple)
    CircularRippleButton audioInputRipple;

    private Context context;

    private final int RC_RECORD_VOICE = 0x0001;
    private final int RC_CAMERA = 0x0002;
    private final int RC_PHOTO = 0x0003;

    private final String ROLE_MYSELF = "1";
    private final String ROLE_ROBOT = "0";

    private MsgListAdapter<MyMessage> mAdapter;
    private List<MyMessage> mData;

    private InputMethodManager mImm;
    private Window mWindow;

    ConversationService conversationService;
    Map conversationContext = new HashMap();

    SpeechToText speechToTextService;
    private boolean listening = false;
    private MicrophoneInputStream capture;
    MicrophoneRecognizeDelegate microphoneRecognizeDelegate;

    VisualRecognition visualRecognitionService;

    //拍照配置
    private CustomHelper customHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        //初始化服务，填写username和password和url
        conversationService = WatsonServiceManager.initConversationService();
        speechToTextService = WatsonServiceManager.initSpeechToTextService();
        visualRecognitionService = WatsonServiceManager.initVisualRecognitionService();
        conversationAPI("你好");//先发送一次进行场景初始化
        microphoneRecognizeDelegate = new MicrophoneRecognizeDelegate();

        this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindow = getWindow();

        chatView.initModule();
        chatView.setTitle("旅行助手");
        mData = new ArrayList<>();
        initMsgAdapter();

        customHelper = CustomHelper.newInstant();

        //文本输入
        /**
         * //因为系统没有直接监听软键盘API，所以就用以下方法
         * //获取屏幕高度
         */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int screenHeight = metrics.heightPixels;
        chatView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() { //当界面大小变化时，系统就会调用该方法
                        Rect r = new Rect(); //该对象代表一个矩形（rectangle）
                        chatView.getWindowVisibleDisplayFrame(r); //将当前界面的尺寸传给Rect矩形
                        int deltaHeight = screenHeight - r.bottom;  //弹起键盘时的变化高度，在该场景下其实就是键盘高度。
                        if (deltaHeight > 150) {  //该值是随便写的，认为界面高度变化超过150px就视为键盘弹起。
                            mAdapter.getLayoutManager().scrollToPosition(0);
                        }
                    }
                });

        //语音输入
        menuInputAudio.setOnTouchListener((View v, MotionEvent event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    openMicrop();
                    return true;
                case MotionEvent.ACTION_UP:
                    closeMicrop();
                    return true;
                default:
                    break;
            }
            return false;
        });

    }

    /**
     * 打开麦克风
     */
    private void openMicrop() {
        if (!listening) {
            startBlowUpAnim(menuInputAudio);
            new Thread(() -> {
                try {
                    //打开麦克风
                    capture = new MicrophoneInputStream(true);
                    speechToTextService.recognizeUsingWebSocket(capture, getSttRecognizeOptions(), microphoneRecognizeDelegate);
                    L.i("Listening....");
                } catch (Exception e) {
                    L.e(e.getMessage());
                }
            }).start();
        }
    }

    /**
     * 关闭麦克风
     */
    private void closeMicrop() {
        startDrawBackAnim(menuInputAudio);
        new Thread(() -> {
            try {
                if (capture != null) {
                    capture.close();
                    L.i("Stopped Listening!");
                }
            } catch (Exception e) {
                L.e(e.getMessage());
            }
        }).start();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestPermissions();//请求权限
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 请求权限
     */
    private void requestPermissions() {
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO

        };

        if (!EasyPermissions.hasPermissions(ChatActivity.this, perms)) {
            EasyPermissions.requestPermissions(ChatActivity.this,
                    getResources().getString(R.string.rationale_record_voice)
                            + getResources().getString(R.string.rationale_photo)
                            + getResources().getString(R.string.rationale_camera),
                    RC_RECORD_VOICE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /**
     * 创建文本类消息
     *
     * @param role
     * @param text
     * @return
     */
    private void creatTextMessages(String role, String text, String url) {
        final MyMessage message;
        if (TextUtils.isEmpty(text)) return;//如果消息为空，直接返回
        switch (role) {
            case ROLE_ROBOT:
                message = new MyMessage(text, IMessage.MessageType.RECEIVE_TEXT);
                message.setUserInfo(new DefaultUser(role, "robot", "R.mipmap.avatar_robot"));
                //返回含有convId
                if (!TextUtils.isEmpty(url)) message.setConvId(url);
                break;
            case ROLE_MYSELF:
                message = new MyMessage(text, IMessage.MessageType.SEND_TEXT);
                message.setUserInfo(new DefaultUser(role, "myself", "R.mipmap.avatar_myself"));
                //发送对话请求
                conversationAPI(text);
                break;
            default:
                message = new MyMessage(text, IMessage.MessageType.SEND_TEXT);
                message.setUserInfo(new DefaultUser(role, "myself", "R.mipmap.avatar_myself"));
                break;
        }
        message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        //显示到对话中
        ChatActivity.this.runOnUiThread(() -> {
            mAdapter.addToStart(message, true);
        });
    }

    /**
     * 创建图片类消息
     *
     * @param role
     * @param photoPath
     */
    private void creatPhotoMessages(String role, String photoPath) {
        final MyMessage message;
        switch (role) {
            case ROLE_ROBOT:
                message = new MyMessage(null, IMessage.MessageType.RECEIVE_IMAGE);
                message.setUserInfo(new DefaultUser(role, "robot", "R.mipmap.avatar_robot"));
                break;
            case ROLE_MYSELF:
                message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE);
                message.setUserInfo(new DefaultUser(role, "myself", "R.mipmap.avatar_myself"));
                //发送图像识别请求
                visualRecognitionAPI(photoPath);
                break;
            default:
                message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE);
                message.setUserInfo(new DefaultUser(role, "myself", "R.mipmap.avatar_myself"));
                break;
        }
        message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        message.setMediaFilePath(photoPath);
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addToStart(message, true);
            }
        });
    }

    /**
     * 初始化消息适配器
     */
    private void initMsgAdapter() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                // You can use other image load libraries.
                if (string.contains("R.mipmap")) {
                    Integer resId = getResources().getIdentifier(string.replace("R.mipmap.", ""),
                            "mipmap", getPackageName());

                    avatarImageView.setImageResource(resId);
                } else {
                    Glide.with(getApplicationContext())
                            .load(string)
                            .placeholder(R.drawable.aurora_headicon_default)
                            .into(avatarImageView);
                }
            }

            @Override
            public void loadImage(ImageView imageView, String string) {
                // You can use other image load libraries.
                Glide.with(getApplicationContext())
                        .load(string)
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .override(800, Target.SIZE_ORIGINAL)
                        .into(imageView);
            }
        };

        // Use default layout
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>("0", holdersConfig, imageLoader);

        // If you want to customise your layout, try to create custom ViewHolder:
        // holdersConfig.setSenderTxtMsg(CustomViewHolder.class, layoutRes);
        // holdersConfig.setReceiverTxtMsg(CustomViewHolder.class, layoutRes);
        // CustomViewHolder must extends ViewHolders defined in MsgListAdapter.
        // Current ViewHolders are TxtViewHolder, VoiceViewHolder.

        //点击事件
        mAdapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<MyMessage>() {
            @Override
            public void onMessageClick(MyMessage message) {
                if (message.getType() == IMessage.MessageType.SEND_IMAGE
                        || message.getType() == IMessage.MessageType.RECEIVE_IMAGE) {//查看大图
                    PhotoViewPop photoViewPop = new PhotoViewPop((Activity) context, message.getMediaFilePath());
                    photoViewPop.showPopupWindow();
                } else if (message.getType() == IMessage.MessageType.RECEIVE_TEXT
                        && !TextUtils.isEmpty(message.getConvId())) {//文本跳转到DetailActivity

                    ConversationDao.queryById(message.getConvId(), new ConversationDao.DbCallBack() {
                        @Override
                        public void onSuccess(List<Conversation> datas) {
                            if (datas != null && datas.size() > 0) {
                                Intent intent = new Intent();
                                intent.setClass(context, DetailActivity.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("model", datas.get(0));
                                intent.putExtras(mBundle);
                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });
                }
            }
        });

        mAdapter.setMsgResendListener(new MsgListAdapter.OnMsgResendListener<MyMessage>() {
            @Override
            public void onMessageResend(MyMessage message) {
                // resend message here
                L.i("onMessageResend");
            }
        });

        mAdapter.addToEnd(mData);
        mAdapter.setOnLoadMoreListener(new MsgListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalCount) {
                if (totalCount <= mData.size()) {
                    L.i("Loading next page");
                    loadNextPage();
                }
            }
        });

        chatView.setAdapter(mAdapter);
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addToEnd(mData);
            }
        }, 1000);
    }

    /**
     * 对话
     *
     * @param input
     * @return
     */
    public void conversationAPI(String input) {
        new Thread(() -> {
            MessageRequest newMessage = new MessageRequest.Builder()
                    .inputText(input).context(conversationContext).build();
            String workspaceId = App.getCacheString(Const.WATSON_CONV_WSID_KEY, Const.WATSON_CONV_WSID_DEFAULT);

            // async
            conversationService.message(workspaceId, newMessage).enqueue(new ServiceCallback<MessageResponse>() {
                @Override
                public void onResponse(MessageResponse response) {
                    L.i(response.getOutput().get("text").toString());
                    conversationContext = response.getContext();//保持上下文
                    String answer = response.getOutput().get("text").toString().trim();
                    final String answerFinal = answer.substring(1, answer.length() - 1);//"[你好]",去掉首尾出现的"["和"]"

                    //"你好//id=123",如果返回是带分隔符
                    if (answerFinal.contains("//")) {
                        //"你好//id=123"转为"你好"和"123"
                        String[] array = answerFinal.split("//");
                        //遍历数组
                        for (int i = 0; i < array.length; i++) {
                            if (array[i].contains("id=")) {//如果含有"id="的字符串，输出图文
                                String convId = array[i].substring(3);//"id=123",剪切掉"id="
                                queryConvById(convId);
                            } else {//如果不含有"id="的字符串，直接输出文本
                                creatTextMessages(ROLE_ROBOT, array[i], null);
                            }
                        }
                    } else {//如果返回不是id，则显示原话
                        creatTextMessages(ROLE_ROBOT, answerFinal, null);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    String answer = "网络不好，我听不见了你说的话...";
                    creatTextMessages(ROLE_ROBOT, answer, null);
                    L.e(e.toString());
                }
            });
        }).start();
    }

    /**
     * 图像识别
     *
     * @param imgPath
     * @return
     */
    public void visualRecognitionAPI(String imgPath) {
        visualRecognitionService.classify(getVisualRecognitionOptions(imgPath))
                .enqueue(new ServiceCallback<VisualClassification>() {
                    @Override
                    public void onResponse(VisualClassification response) {
                        L.i(response.toString());

                        //如果识别不了
                        if (response.getImages().size() == 0
                                || response.getImages().get(0).getClassifiers().size() == 0
                                || response.getImages().get(0).getClassifiers().get(0).getClasses().size() == 0) {
                            creatTextMessages(ROLE_ROBOT, "我还没见过这图，不过我会慢慢研究", null);
                            return;
                        }

                        //最高分的识别类,默认取第一个为标准
                        VisualClassifier.VisualClass highScoreClass
                                = response.getImages().get(0).getClassifiers().get(0).getClasses().get(0);

                        for (ImageClassification imageClassification : response.getImages()) {
                            for (VisualClassifier classifiers : imageClassification.getClassifiers()) {
                                for (VisualClassifier.VisualClass visualClass : classifiers.getClasses()) {
//                                    answer.append(visualClass.getName()+"  "+visualClass.getScore()+"|");
                                    //只有分数大于0.5并且大于标准，方认为它是最高分
                                    if (visualClass.getScore() > 0.5 && visualClass.getScore() > highScoreClass.getScore()) {
                                        highScoreClass = visualClass;
                                    }
                                }
                            }
                        }

                        final String convId = highScoreClass.getName();//分类名对应mysql类型
                        if (highScoreClass.getScore() > 0.5) {
                            queryConvById(convId);
                        } else {
                            creatTextMessages(ROLE_ROBOT, "这图好眼熟啊，可是我不知道怎么解释", null);
                        }

                    }

                    @Override
                    public void onFailure(Exception e) {
                        String answer = "网络不好，我听不见了你说的话...";
                        creatTextMessages(ROLE_ROBOT, answer, null);
                        L.e(e.toString());
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 查询数据库，并显示图文描述
     */
    private void queryConvById(final String query) {
        L.i("查询conversationId=" + query);

        ConversationDao.queryById(query, new ConversationDao.DbCallBack() {
            @Override
            public void onSuccess(List<Conversation> datas) {
                if (datas != null && datas.size() > 0) {
                    /**
                     * conversation转message
                     * 默认取第一个值
                     */
                    Conversation model = datas.get(0);
                    //图片描述
                    if (!TextUtils.isEmpty(model.getImg())) {
                        creatPhotoMessages(ROLE_ROBOT, model.getImg());
                    }
                    //文本描述
                    if (!TextUtils.isEmpty(model.getContent())) {
                        if (!TextUtils.isEmpty(model.getDetail())) {
                            creatTextMessages(ROLE_ROBOT, model.getContent(), model.getId());
                        } else {
                            creatTextMessages(ROLE_ROBOT, model.getContent(), null);
                        }
                    }
                } else {
                    //如果查询不到相应对话，则显示未找到的值
                    creatTextMessages(ROLE_ROBOT, query, null);
                }
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                creatTextMessages(ROLE_ROBOT, "连不上服务，我也不知该如何回答", null);
            }
        });
    }

    @OnClick({R.id.menu_input_text, R.id.menu_input_photo, R.id.back_iv
            , R.id.text_input_close, R.id.text_input_send, R.id.photo_input_takephoto
            , R.id.photo_input_close, R.id.photo_input_gallery, R.id.title_tv
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_input_text:
                showTextInput();
                break;
            case R.id.menu_input_photo:
                showPhotoInput();
                break;
            case R.id.text_input_close:
                showChatInput();
                break;
            case R.id.text_input_send:
                sendInputText();
                break;
            case R.id.photo_input_close:
                showChatInput();
                break;
            case R.id.photo_input_gallery:
                customHelper.goToPickBySelect(getTakePhoto());
                break;
            case R.id.photo_input_takephoto:
                customHelper.goToPickByTake(getTakePhoto());
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.title_tv:
                break;
        }
    }

    /**
     * 显示文本输入
     */
    private void showTextInput() {
        chatInput.setVisibility(View.GONE);
        textInputLl.setVisibility(View.VISIBLE);
        photoInputLl.setVisibility(View.GONE);
        startShowAnim(textInputLl);
        mAdapter.getLayoutManager().scrollToPosition(0);
        KeyBoardUtils.openKeyboard(this, textInputContent);
    }

    /**
     * 显示图片输入
     */
    private void showPhotoInput() {
        chatInput.setVisibility(View.GONE);
        textInputLl.setVisibility(View.GONE);
        photoInputLl.setVisibility(View.VISIBLE);
        startShowAnim(photoInputLl);
    }

    /**
     * 显示聊天菜单
     */
    private void showChatInput() {
        chatInput.setVisibility(View.VISIBLE);
        textInputLl.setVisibility(View.GONE);
        photoInputLl.setVisibility(View.GONE);
        startShowAnim(chatInput);
        KeyBoardUtils.closeKeyboard(this);
    }

    /**
     * 显示动画
     */
    private void startShowAnim(View view) {
        //增加动画
        ViewAnimator
                .animate(view)
                .translationY(view.getY(), 0)
                .duration(700)
                .start();
    }

    /**
     * 放大动画
     */
    private void startBlowUpAnim(View view) {
        runOnUiThread(() -> {
            ViewAnimator
                    .animate(view)
                    .scale(1f, 1.2f)
                    .interpolator(new AccelerateInterpolator())
                    .duration(200)
                    .onStart(() -> {
                    })
                    .onStop(() -> audioInputRipple.startRipple())
                    .start();
        });
    }

    /**
     * 缩小动画
     */
    private void startDrawBackAnim(View view) {
        runOnUiThread(() -> {
            ViewAnimator
                    .animate(view)
                    .scale(1.2f, 1f)
                    .interpolator(new AccelerateInterpolator())
                    .duration(200)
                    .onStart(() -> {
                    })
                    .onStop(() -> audioInputRipple.stopRipple())
                    .start();
        });
    }

    /**
     * 发送输入文本
     */
    private void sendInputText() {
        String inputText = textInputContent.getText().toString();
        if (!TextUtils.isEmpty(inputText)) {
            L.i("send:" + inputText);
            textInputContent.setText("");
            creatTextMessages(ROLE_MYSELF, inputText, null);
        } else {
            T.showShort(this, "发送内容不能为空");
        }
    }

    /**
     * 语音转文本回调
     */
    //Watson Speech to Text Methods.
    private class MicrophoneRecognizeDelegate implements RecognizeCallback {

        @Override
        public void onTranscription(SpeechResults speechResults) {
            L.i("onTranscription");
            L.i(speechResults.toString());
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                if(text.contains(" ")){
                    text = text.trim().replaceAll(" ","");
                }
                creatTextMessages(ROLE_MYSELF, text, null);
            } else {
                creatTextMessages(ROLE_ROBOT, "你有说话吗，我没听清", null);
            }
        }

        @Override
        public void onConnected() {
            L.i("onConnected");
            listening = true;
        }

        @Override
        public void onError(Exception e) {
            L.i("onError");
            L.e(e.getMessage());
            closeMicrop();
            listening = false;
            creatTextMessages(ROLE_ROBOT, "网络不好，无法进行语音识别", null);
        }

        @Override
        public void onDisconnected() {
            L.i("onDisconnected");
            listening = false;
        }
    }


    /**
     * 拍照或相册
     */
    @Override
    public void takeCancel() {
        super.takeCancel();
        L.i("takeCancel");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        L.i("takeFail=" + msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        L.i("takeSuccess");
        showChatInput();
        showImg(result.getImages());
    }

    /**
     * 拍照或选择完图片成功后的处理
     *
     * @param images
     */
    private void showImg(ArrayList<TImage> images) {
        for (TImage img : images) {
//            L.i("getCompressPath="+img.getCompressPath());//压缩后的路径
            L.i("getOriginalPath=" + img.getOriginalPath());//源文件路径
//            L.i("getFromType="+img.getFromType());
            creatPhotoMessages(ROLE_MYSELF, img.getOriginalPath());
        }
    }

}
