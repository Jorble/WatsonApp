package com.giant.watsonapp.user;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.klinker.android.link_builder.LinkConsumableTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title_tv)
    LinkConsumableTextView title_tv;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
