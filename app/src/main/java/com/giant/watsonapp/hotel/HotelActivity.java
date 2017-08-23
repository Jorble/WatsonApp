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

import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Hotel;
import com.giant.watsonapp.utils.L;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        for(int i=0;i<10;i++){
            Hotel.HotelListBean bean=new Hotel.HotelListBean();
            bean.setName("三亚海韵度假酒店");
            bean.setPrice("¥760");
            bean.setStar(4.8f);
            List<String> imgList=new ArrayList<>();
            imgList.add("https://dimg04.c-ctrip.com/images/20070d0000006tk9o3BD0_C_550_412_Q50.jpg");
            imgList.add("https://dimg04.c-ctrip.com/images/200d0800000037bf26045_C_300_225_Q50.jpg_.webp");
            imgList.add("https://dimg04.c-ctrip.com/images/fd/hotel/g3/M02/C4/0E/CggYGlX8tdSAWvcGAAJfb6Rsqao013_C_300_225_Q50.jpg_.webp");
            bean.setImgList(imgList);
            bean.setLocation("三亚湾路168号（近海虹路）");

            List<Hotel.HotelListBean.RoomListBean> roomList=new ArrayList<>();
            for(int j=0;j<5;j++){
                Hotel.HotelListBean.RoomListBean roomListBean=new Hotel.HotelListBean.RoomListBean();
                roomListBean.setName("豪华海景套房");
                roomListBean.setPrice("¥792");
                roomListBean.setDesc("85-90m² 大/双床 6-19层");
                roomListBean.setImg("http://dimg04.c-ctrip.com/images/200p04000000017v61559_C_130_130_Q50.jpg");
                roomListBean.setImpression("视野开阔");
                roomList.add(roomListBean);
            }
            bean.setRoomList(roomList);

            mDatas.add(bean);
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
