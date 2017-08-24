package com.giant.watsonapp.food;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giant.watsonapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class FoodViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.img_iv)
    ImageView img;
    @BindView(R.id.name_tv)
    TextView name;

    public FoodViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
