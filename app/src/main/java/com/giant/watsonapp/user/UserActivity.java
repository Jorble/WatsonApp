package com.giant.watsonapp.user;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.models.User;
import com.giant.watsonapp.models.UserDao;
import com.giant.watsonapp.views.TextItem;
import com.giant.watsonapp.views.TextWall;
import com.jaeger.library.StatusBarUtil;
import com.race604.drawable.wave.WaveDrawable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.userImg_iv)
    ImageView userImgIv;
    @BindView(R.id.userName_tv)
    TextView userNameTv;
    @BindView(R.id.user_tw)
    TextWall userTw;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.score_tv)
    TextView scoreTv;
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

    private User user;

    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        titleTv.setText("个人中心");

        beginRefreshing();
    }

    /**
     * 初始化数据
     */
    private void beginRefreshing() {
        showLoading();
        new Handler().postDelayed(() -> {
            UserDao.queryAll(new UserDao.DbCallBack() {
                @Override
                public void onSuccess(List<User> datas) {
                    if (recyclerView == null) return;
                    showContent();

                    if (datas == null || datas.size() == 0) {
                        //空数据
                        showEmpty();
                    } else {
                        user = datas.get(0);
                        userNameTv.setText(user.getName());
                        scoreTv.setText(user.getScore());
                        //文字墙
                        String[] texts=user.getTag().split(";");
                        initTextWall(texts);
                        //行为轨迹
                        List<String> history = Arrays.asList(user.getHistory().split(";"));
                        List<String> highlight = Arrays.asList(user.getHighlight().split(";"));
                        initRv(history,highlight);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    if (user == null) {
                        showError();
                    }
                }
            });
        }, 1000);
    }
    
    /**
     * 初始化列表
     */
    private void initRv(List<String> mDatas,List<String> highLightList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        if (mDatas != null && mDatas.size() > 0) {
            adapter = new UserAdapter(mDatas, highLightList);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * 初始化文字墙
     */
    private void initTextWall(String[] texts) {
        userTw.post(() -> {
            List<TextItem> textItems = new ArrayList<>();
            for (int i = 0; i < texts.length; i++) {
                TextItem item = new TextItem();
                item.setIndex(10);
                item.setValue(texts[i]);
                textItems.add(item);
            }
            userTw.setData(textItems, UserActivity.this);
        });
    }

    @OnClick({R.id.back_iv, R.id.title_tv})
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

}
