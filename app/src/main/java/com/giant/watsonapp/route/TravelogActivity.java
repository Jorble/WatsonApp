package com.giant.watsonapp.route;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Travelog;
import com.giant.watsonapp.models.TravelogDao;
import com.giant.watsonapp.utils.T;
import com.jaeger.library.StatusBarUtil;
import com.race604.drawable.wave.WaveDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.giant.watsonapp.R.id.webView;

public class TravelogActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.share_bt)
    LinearLayout shareBt;
    @BindView(R.id.loadView)
    ImageView loadView;
    @BindView(R.id.loading_rl)
    RelativeLayout loadingRl;
    @BindView(R.id.empty_iv)
    ImageView emptyIv;
    @BindView(R.id.empty_rl)
    RelativeLayout emptyRl;
    @BindView(R.id.error_iv)
    ImageView errorIv;
    @BindView(R.id.error_rl)
    RelativeLayout errorRl;
    @BindView(R.id.content_ll)
    LinearLayout contentLl;


    String id;
    Travelog travelog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelog);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;
        titleTv.setText("游记");

        id = (String) getIntent().getCharSequenceExtra("id");

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示

        webView.setBackgroundColor(Color.TRANSPARENT);

        beginRefreshing();
    }

    /**
     * 初始化数据
     */
    private void beginRefreshing() {
        if(TextUtils.isEmpty(id))return;

        showLoading();
        new Handler().postDelayed(() -> {
            TravelogDao.queryById(id,new TravelogDao.DbCallBack() {
                @Override
                public void onSuccess(List<Travelog> datas) {
                    if (webView == null) return;
                    showContent();

                    if (datas == null || datas.size() == 0) {
                        //空数据
                        showEmpty();
                    } else {
                        //正文处理
                        travelog = datas.get(0);
                        webView.loadDataWithBaseURL(null, travelog.getHtml(), "text/html", "UTF-8", null);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    if (webView == null) return;
                    if (travelog == null) {
                        showError();
                    }
                }
            });
        }, 1000);
    }
    
    @OnClick({R.id.back_iv, R.id.title_tv, R.id.empty_rl, R.id.error_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.title_tv:
                break;
            case R.id.empty_rl:
                beginRefreshing();
                break;
            case R.id.error_rl:
                beginRefreshing();
                break;
        }
    }

    /**
     * 显示加载
     */
    private void showLoading() {
        emptyRl.setVisibility(View.GONE);
        errorRl.setVisibility(View.GONE);
        contentLl.setVisibility(View.GONE);
        loadingRl.setVisibility(View.VISIBLE);

        WaveDrawable mWaveDrawable = new WaveDrawable(this, R.mipmap.pic_loading);
        mWaveDrawable.setWaveAmplitude(5);//振幅,max=100
        mWaveDrawable.setWaveLength(100);//波长,max=600
        mWaveDrawable.setWaveSpeed(5);//速度,max=50
//        mWaveDrawable.setLevel(4000);//进度,max=10000
        mWaveDrawable.setIndeterminate(true);//是否自增
        loadView.setImageDrawable(mWaveDrawable);
    }

    /**
     * 显示内容
     */
    private void showContent() {
        emptyRl.setVisibility(View.GONE);
        errorRl.setVisibility(View.GONE);
        contentLl.setVisibility(View.VISIBLE);
        loadingRl.setVisibility(View.GONE);
    }

    /**
     * 显示空布局
     */
    private void showEmpty() {
        emptyRl.setVisibility(View.VISIBLE);
        errorRl.setVisibility(View.GONE);
        contentLl.setVisibility(View.GONE);
        loadingRl.setVisibility(View.GONE);
    }

    /**
     * 显示错误布局
     */
    private void showError() {
        emptyRl.setVisibility(View.GONE);
        errorRl.setVisibility(View.VISIBLE);
        contentLl.setVisibility(View.GONE);
        loadingRl.setVisibility(View.GONE);
    }

    /**
     * 根据传入id并启动自身
     */
    public static void startMyself(Context context, String id) {
        if (id == null) return;

        Intent intent = new Intent();
        intent.setClass(context, TravelogActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putCharSequence("id", id);
        intent.putExtras(mBundle);
        context.startActivity(intent);
    }

}
