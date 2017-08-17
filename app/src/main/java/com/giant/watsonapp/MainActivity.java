package com.giant.watsonapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.chat.ChatActivity;
import com.giant.watsonapp.setting.SettingActivity;
import com.giant.watsonapp.utils.GlideRoundTransform;
import com.giant.watsonapp.utils.UiUtils;
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

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.spot_title)
    TextView spotTitle;
    @BindView(R.id.spot_desc)
    TextView spotDesc;
    @BindView(R.id.menu_basic)
    RelativeLayout menuBasic;
    @BindView(R.id.menu_traffic)
    RelativeLayout menuTraffic;
    @BindView(R.id.menu_hotel)
    RelativeLayout menuHotel;
    @BindView(R.id.menu_food)
    RelativeLayout menuFood;
    @BindView(R.id.menu_specialty)
    RelativeLayout menuSpecialty;
    @BindView(R.id.menu_robot)
    RelativeLayout menuRobot;
    @BindView(R.id.banner)
    Banner banner;

    private int countTitleClick=0;//点击标题3下进入设置页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);

        initBanner();
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

    @OnClick({R.id.menu_basic, R.id.menu_traffic, R.id.menu_hotel, R.id.menu_food
            , R.id.menu_specialty, R.id.menu_robot,R.id.spot_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_basic:
                break;
            case R.id.menu_traffic:
                break;
            case R.id.menu_hotel:
                break;
            case R.id.menu_food:
                break;
            case R.id.menu_specialty:
                break;
            case R.id.menu_robot:
                UiUtils.startActivity(this, ChatActivity.class);
                break;
            case R.id.spot_title:
                countTitleClick++;
                if(countTitleClick>=3){
                    countTitleClick=0;
                    UiUtils.startActivity(this, SettingActivity.class);
                }
                break;
        }
    }

    /**
     * 初始化轮播
     */
    private void initBanner(){
        //本地图片数据（资源文件）
        List<Integer> images=new ArrayList<>();
        images.add(R.mipmap.home_banner1);
        images.add(R.mipmap.home_banner2);
        images.add(R.mipmap.home_banner3);

        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
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
                    .transform(new GlideRoundTransform(context,10))//圆角10dp
                    .into(imageView);

        }
    }
}
