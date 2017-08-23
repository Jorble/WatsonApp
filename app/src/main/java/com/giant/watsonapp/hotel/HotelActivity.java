package com.giant.watsonapp.hotel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Hotel;
import com.giant.watsonapp.utils.L;
import com.giant.watsonapp.voice.TimeLineAdapter;
import com.jaeger.library.StatusBarUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

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

    private List<Hotel.HotelListBean> mDatas = new ArrayList<>();
    private HotelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;
        titleTv.setText("住宿");

        initData();

        initRv();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        L.i("initData");
        for(int i=0;i<10;i++){
            Hotel.HotelListBean bean=new Hotel.HotelListBean();
            bean.setName("三亚海韵度假酒店");
            bean.setPrice("¥760");
            bean.setStar(4.38f);
            List<String> imgList=new ArrayList<>();
            imgList.add("https://dimg04.c-ctrip.com/images/20070d0000006tk9o3BD0_C_550_412_Q50.jpg");
            bean.setImgList(imgList);
            mDatas.add(bean);
            L.i("add"+i);
        }
    }

    /**
     * 初始化列表
     */
    private void initRv(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        adapter = new HotelAdapter(mDatas);
        recyclerView.setAdapter(adapter);
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
