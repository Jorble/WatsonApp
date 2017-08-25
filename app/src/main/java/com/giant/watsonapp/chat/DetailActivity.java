package com.giant.watsonapp.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Conversation;
import com.giant.watsonapp.models.Hotel;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.System.load;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.img_iv)
    ImageView imgIv;
    @BindView(R.id.text_tv)
    TextView textTv;
    @BindView(R.id.section_ll)
    LinearLayout sectionLl;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;

    Context context;
    Conversation model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;

        model = (Conversation) getIntent().getSerializableExtra("model");
        titleTv.setText("详情");

        initUI();

    }

    /**
     * 初始化界面
     */
    private void initUI(){
        if(model==null)return;
        nameTv.setText(model.getName());

        Glide
                .with(this)
                .load(model.getImg())
                .placeholder(R.mipmap.pic_spot_default)
                .into(imgIv);

        String html=model.getDetail();
        Spanned spanned = Html.fromHtml(html, null, null);
        textTv.setText(spanned);
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
