package com.giant.watsonapp.setting;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.models.GreenDaoManager;
import com.giant.watsonapp.models.VrClassifier;
import com.giant.watsonapp.utils.T;
import com.giant.watsonapp.views.Divider;
import com.jaeger.library.StatusBarUtil;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class ClassifierEditActivity extends AppCompatActivity implements BGAOnItemChildClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.add_bt)
    Button addBt;
    @BindView(R.id.recylerView)
    RecyclerView recylerView;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.id_et)
    EditText idEt;
    @BindView(R.id.divider_tv)
    TextView dividerTv;
    @BindView(R.id.divider_ll)
    AutoLinearLayout dividerLl;

    private ClassifierRecyclerViewAdapter mAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier_edit);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;
        titleTv.setText("图像识别");
        dividerTv.setText("分类器列表");

        //初始化列表
        initRefreshLayout();
    }

    @OnClick({R.id.back_iv, R.id.add_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.add_bt:
                add();
                break;
        }
    }

    public void initRefreshLayout() {
        refreshLayout.setDelegate(this);
        mAdapter = new ClassifierRecyclerViewAdapter(this, recylerView);
        mAdapter.setOnItemChildClickListener(this);

        BGAMoocStyleRefreshViewHolder moocStyleRefreshViewHolder = new BGAMoocStyleRefreshViewHolder(this, true);
        moocStyleRefreshViewHolder.setOriginalImage(R.mipmap.bga_refresh_water);
        moocStyleRefreshViewHolder.setUltimateColor(R.color.theme_color);
        refreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);

        recylerView.addItemDecoration(new Divider(this));

//        recylerView.setLayoutManager(new GridLayoutManager(mApp, 2, GridLayoutManager.VERTICAL, false));
        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recylerView.setAdapter(mAdapter);

        //预加载一次
        refreshLayout.beginRefreshing();

    }

    /**
     * 添加
     */
    private void add() {
        String name = nameEt.getText().toString();
        String id = idEt.getText().toString();

        if (TextUtils.isEmpty(name)) {
            T.showShort(this, "名称不能为空");
            return;
        }

        if (TextUtils.isEmpty(id)) {
            T.showShort(this, "id不能为空");
            return;
        }

        VrClassifier vrClassifier = new VrClassifier();
        vrClassifier.setName(name);
        vrClassifier.setClassifierId(id);
        GreenDaoManager.getDaoInstant().getVrClassifierDao().insertOrReplaceInTx(vrClassifier);

        mAdapter.addFirstItem(vrClassifier);
        T.showShort(this, "添加成功");
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout mrefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //数据库加载数据
                List<VrClassifier> list = GreenDaoManager.getDaoInstant().getVrClassifierDao().loadAll();
                if (list.size() > 0) {
                    mAdapter.setData(list);
                } else {
                    T.showShort(context, "暂无数据,请添加");
                }
                refreshLayout.endRefreshing();
            }
        }, 1000);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout mrefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.endLoadingMore();
            }
        }, 1000);
        return true;
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (childView.getId() == R.id.remove_bt) {
            //数据库中移除
            GreenDaoManager.getDaoInstant().getVrClassifierDao().delete(mAdapter.getItem(position));
            //列表中移除
            mAdapter.removeItem(position);
            T.showShort(this, "删除成功");
        }
    }
}
