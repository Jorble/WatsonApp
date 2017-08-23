package com.giant.watsonapp.hotel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Hotel;
import com.giant.watsonapp.utils.GlideRoundTransform;
import com.giant.watsonapp.views.Divider;
import com.jaeger.library.StatusBarUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.giant.watsonapp.R.id.recylerView;

public class HotelDetailActivity extends AppCompatActivity {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.star_mrb)
    MaterialRatingBar starMrb;
    @BindView(R.id.star_tv)
    TextView starTv;
    @BindView(R.id.location_tv)
    TextView locationTv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    Context context;
    Hotel.HotelListBean model;
    RoomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        model = (Hotel.HotelListBean)getIntent().getSerializableExtra("model");
        if(TextUtils.isEmpty(model.getName())){
            titleTv.setText("酒店详情");
        }else {
            titleTv.setText(model.getName());
        }

        initBanner();

        initStar();

        initLocation();

        initRoomRv();
    }

    /**
     * 初始化轮播
     */
    private void initBanner(){
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());

        //设置图片集合
        if(model.getImgList().size()==0){
            //如何无图,显示默认
            List<Integer> images=new ArrayList<>();
            images.add(R.mipmap.pic_spot_default);
            banner.setImages(images);
        }else {
            banner.setImages(model.getImgList());
        }

        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    /**
     * 轮播图片加载器
     */
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */

            //Glide 加载图片简单用法
            Glide
                    .with(context)
                    .load(path)
                    .into(imageView);

        }
    }

    /**
     * 初始化评分
     */
    private void initStar(){
        if(TextUtils.isEmpty(String.valueOf(model.getStar())))return;

        starMrb.setRating(model.getStar());
        starTv.setText(String.valueOf(model.getStar()));
    }

    /**
     * 初始化位置
     */
    private void initLocation(){
        if(TextUtils.isEmpty(model.getLocation()))return;

        locationTv.setText(model.getLocation());
    }

    /**
     * 初始化房间列表
     */
    private void initRoomRv(){
        recyclerView.addItemDecoration(new Divider(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }});
        recyclerView.setHasFixedSize(true);
        adapter = new RoomAdapter(model.getRoomList());
        recyclerView.setAdapter(adapter);
    }

    @OnClick({R.id.back_iv, R.id.title_tv, R.id.location_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.title_tv:
                break;
            case R.id.location_tv:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }
}
