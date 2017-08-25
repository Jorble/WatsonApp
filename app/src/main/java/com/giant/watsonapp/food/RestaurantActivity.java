package com.giant.watsonapp.food;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Restaurant;
import com.giant.watsonapp.models.RestaurantDao;
import com.jaeger.library.StatusBarUtil;
import com.race604.drawable.wave.WaveDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestaurantActivity extends AppCompatActivity {

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

    private List<Restaurant> mDatas = new ArrayList<>();
    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;
        titleTv.setText("美食");

        initRv();

        beginRefreshing();
    }

    /**
     * 初始化数据
     */
    private void beginRefreshing() {
        showLoading();
        new Handler().postDelayed(() -> {
            RestaurantDao.queryAll(new RestaurantDao.DbCallBack() {
                @Override
                public void onSuccess(List<Restaurant> datas) {
                    if (recyclerView == null) return;
                    showContent();

                    mDatas = datas;

                    if (mDatas == null || mDatas.size() == 0) {
                        //空数据
                        showEmpty();
                    } else {
                        adapter = new RestaurantAdapter(mDatas);
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
    }

    /**
     * 显示空布局
     */
    private void showEmpty() {
        emptyRl.setVisibility(View.VISIBLE);
        errorRl.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * 显示错误布局
     */
    private void showError() {
        emptyRl.setVisibility(View.GONE);
        errorRl.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
