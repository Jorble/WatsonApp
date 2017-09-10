package com.giant.watsonapp.route;

import android.support.v7.widget.CardView;
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
public class RouteViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cardview)
    CardView cardView;
    @BindView(R.id.img_iv)
    ImageView img;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.price_tv)
    TextView price;

    public RouteViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
