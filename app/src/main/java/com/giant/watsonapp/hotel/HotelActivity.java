package com.giant.watsonapp.hotel;

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
import com.giant.watsonapp.models.Hotel;
import com.giant.watsonapp.models.HotelDao;
import com.giant.watsonapp.utils.T;
import com.jaeger.library.StatusBarUtil;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.giant.watsonapp.R.id.recyclerView;
import static com.giant.watsonapp.R.id.refreshLayout;

public class HotelActivity extends AppCompatActivity {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    Context context;
    @BindView(R.id.empty_iv)
    ImageView emptyIv;
    @BindView(R.id.empty_rl)
    AutoRelativeLayout emptyRl;
    @BindView(R.id.error_iv)
    ImageView errorIv;
    @BindView(R.id.error_rl)
    AutoRelativeLayout errorRl;

    private List<Hotel> mDatas = new ArrayList<>();
    private HotelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;
        titleTv.setText("住宿");

        initRv();

        beginRefreshing();
    }

    /**
     * 初始化数据
     */
    private void beginRefreshing() {
        showContent();
        new Handler().postDelayed(() -> {
            HotelDao.queryAll(new HotelDao.DbCallBack() {
                @Override
                public void onSuccess(List<Hotel> datas) {
                    if(recyclerView == null) return;
                    mDatas = datas;

                    if(mDatas==null || mDatas.size()==0){
                        //空数据
                        showEmpty();
                    }else {
                        adapter = new HotelAdapter(mDatas);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    if(recyclerView == null) return;
                    if(mDatas.size()==0) {
                        showError();
                    }
                }
            });
        }, 500);
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }

    @OnClick({R.id.back_iv, R.id.title_tv,R.id.empty_rl, R.id.error_rl})
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
