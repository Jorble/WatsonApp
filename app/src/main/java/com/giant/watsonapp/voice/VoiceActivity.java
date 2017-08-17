package com.giant.watsonapp.voice;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.MainActivity;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.OrderStatus;
import com.giant.watsonapp.models.Orientation;
import com.giant.watsonapp.models.TimeLineModel;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TimeLineAdapter mTimeLineAdapter;
    //数据源
    private List<TimeLineModel> mDataList = new ArrayList<>();
    //垂直或水平排列
    private Orientation mOrientation = Orientation.VERTICAL;
    //时间轴之间是否隔开
    private boolean mWithLinePadding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        titleTv.setText("语音导游");

        //设置排列方式
        recyclerView.setLayoutManager(getLinearLayoutManager());
        recyclerView.setHasFixedSize(true);

        initRecyclerView();
    }

    /**
     * 根据recyclerview的排列方式决定布局排列方式
     * @return
     */
    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
    }

    /**
     * 初始化
     */
    private void initRecyclerView() {
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList,mOrientation,mWithLinePadding);
        recyclerView.setAdapter(mTimeLineAdapter);
    }

    /**
     * 设置数据
     */
    private void setDataListItems(){
        for(int i=0;i<10;i++){
            TimeLineModel model=new TimeLineModel();
            model.setImg("http://ubmcmm.baidustatic.com/media/v1/0f0005yWcIBA48LujiH7h0.jpg");
            model.setMessage("多于自己指定的行数的textView多于自己指定的行数的textView" +
                    "多于自己指定的行数的textView多于自己指定的行数的textView多于自己指定的行数的textView" +
                    "多于自己指定的行数的textView多于自己指定的行数的textView多于自己指定的行数的textView" +
                    "多于自己指定的行数的textView多于自己指定的行数的textView多于自己指定的行数的textView" +
                    "多于自己指定的行数的textViewv多于自己指定的行数的textView多于自己指定的行数的textView");
            model.setPlaying(false);
            model.setStatus(OrderStatus.INACTIVE);
            model.setTitle("三亚风景区");
            mDataList.add(model);
        }
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
