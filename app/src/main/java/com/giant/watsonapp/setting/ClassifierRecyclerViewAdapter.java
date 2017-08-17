package com.giant.watsonapp.setting;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.VrClassifier;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * 作者:Jorble 邮件:lijiebu@gzjtit.com
 * 创建时间:17/7/3 13:43
 * 描述:
 */
public class ClassifierRecyclerViewAdapter extends BGARecyclerViewAdapter<VrClassifier> {
    Context context;

    public ClassifierRecyclerViewAdapter(Context context, RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_classifier_list);
        this.context=context;
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper, int viewType) {
        viewHolderHelper.setItemChildClickListener(R.id.remove_bt);
    }

    @Override
    public void fillData(BGAViewHolderHelper viewHolderHelper, int position, VrClassifier model) {
        viewHolderHelper
                .setText(R.id.remove_bt, "删除")
                .setText(R.id.value_tv, model.getClassifierId())
                .setText(R.id.name_tv, model.getName());

    }
}