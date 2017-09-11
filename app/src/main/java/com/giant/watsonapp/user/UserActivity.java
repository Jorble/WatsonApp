package com.giant.watsonapp.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.views.TextItem;
import com.giant.watsonapp.views.TextWall;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
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

    //用户行为历史数据
    private List<String> mDatas = new ArrayList<>();
    //高亮名词
    private List<String> highLightList = new ArrayList<>();
    //用户画像文字墙
    String[] texts = {
            "三亚", "旅游", "导航", "美食", "酒店", "亚龙湾", "经济型", "自由", "海边", "探索", "蜈支洲岛",
            "广州", "自驾游", "椰子鸡", "海鲜", "西岛", "玳瑁岛", "海景", "地图", "个性", "淡季", "优惠"};

    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        titleTv.setText("个人中心");

        initData();

        initRv();

        initTextWall();
    }

    private void initData(){
        mDatas.add("2017年9月15日查看了三亚优惠活动");
        mDatas.add("2017年9月11日分享了《三亚游记》到朋友圈");
        mDatas.add("2017年9月7日在三亚游览了亚龙湾景点");
        mDatas.add("2017年9月5日使用了地图导航从广州到三亚");
        mDatas.add("2017年9月4日预订了海韵度假酒店海景房");
        mDatas.add("2017年9月1日机器人捕捉到了三亚游玩意向");
        mDatas.add("2017年8月17日对海南椰子鸡很感兴趣");
        mDatas.add("2017年8月13日浏览了酒店、美食和地图模块");
        mDatas.add("2017年8月9日对推荐景点中的三亚特别关心");
        mDatas.add("2017年8月3日询问了机器人关于海边游玩景点");

        highLightList.add("三亚");
        highLightList.add("游记");
        highLightList.add("优惠");
        highLightList.add("亚龙湾");
        highLightList.add("广州");
        highLightList.add("海韵度假酒店");
        highLightList.add("海南椰子鸡");
        highLightList.add("酒店");
        highLightList.add("美食");
        highLightList.add("地图");
        highLightList.add("景点");
        highLightList.add("海边");
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        if (mDatas != null && mDatas.size() > 0) {
            adapter = new UserAdapter(mDatas,highLightList);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * 初始化文字墙
     */
    private void initTextWall() {
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
        }
    }
}
