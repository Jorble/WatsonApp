package com.giant.watsonapp.hotel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giant.watsonapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class RoomViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.img_iv)
    ImageView img;
    @BindView(R.id.name_tv)
    TextView name;
    @BindView(R.id.price_tv)
    TextView price;
    @BindView(R.id.desc_tv)
    TextView desc;
    @BindView(R.id.impression_tv)
    TextView impression;

    public RoomViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
