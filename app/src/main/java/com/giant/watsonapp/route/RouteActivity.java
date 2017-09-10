package com.giant.watsonapp.route;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Route;
import com.giant.watsonapp.models.RouteDao;
import com.jaeger.library.StatusBarUtil;
import com.race604.drawable.wave.WaveDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RouteActivity extends AppCompatActivity {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    Context context;
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
    @BindView(R.id.search_bt)
    LinearLayout searchBt;
    @BindView(R.id.setting_ll)
    LinearLayout settingLl;
    @BindView(R.id.content_rl)
    RelativeLayout contentRl;

    private List<Route> mDatas = new ArrayList<>();
    private RouteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;
        titleTv.setText("路线");

        initRv();

        beginRefreshing();
    }

    /**
     * 初始化数据
     */
    private void beginRefreshing() {
        showLoading();
        new Handler().postDelayed(() -> {
            RouteDao.queryAll(new RouteDao.DbCallBack() {
                @Override
                public void onSuccess(List<Route> datas) {
                    if (recyclerView == null) return;
                    showContent();

                    mDatas = datas;

                    if (mDatas == null || mDatas.size() == 0) {
                        //空数据
                        showEmpty();
                    } else {
                        adapter = new RouteAdapter(mDatas);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    if (recyclerView == null) return;
                    if (mDatas.size() == 0) {
                        showError();
                    }
                }
            });
        }, 1000);
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }

    @OnClick({R.id.back_iv, R.id.title_tv, R.id.empty_rl, R.id.error_rl, R.id.search_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                back();
                break;
            case R.id.title_tv:
                break;
            case R.id.search_bt:
                settingLl.setVisibility(View.GONE);
                contentRl.setVisibility(View.VISIBLE);
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
     * 返回
     */
    private void back(){
        if(settingLl.getVisibility()==View.GONE){
            settingLl.setVisibility(View.VISIBLE);
            contentRl.setVisibility(View.GONE);
        }else {
            finish();
        }
    }

    /**
     * 监听返回
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return flag;
    }

    /**
     * 显示加载
     */
    private void showLoading() {
        emptyRl.setVisibility(View.GONE);
        errorRl.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
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
        recyclerView.setVisibility(View.VISIBLE);
        loadingRl.setVisibility(View.GONE);
    }

    /**
     * 显示空布局
     */
    private void showEmpty() {
        emptyRl.setVisibility(View.VISIBLE);
        errorRl.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        loadingRl.setVisibility(View.GONE);
    }

    /**
     * 显示错误布局
     */
    private void showError() {
        emptyRl.setVisibility(View.GONE);
        errorRl.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        loadingRl.setVisibility(View.GONE);
    }
}
