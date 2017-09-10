package com.giant.watsonapp.route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.hotel.HotelDetailActivity;
import com.giant.watsonapp.models.Route;
import com.giant.watsonapp.voice.VoiceActivity;

import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private List<Route> mDatas;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public RouteAdapter(List<Route> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_route, parent, false);

        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        Route model = mDatas.get(position);

        holder.price.setText(model.getPrice());
        holder.title.setText(model.getTitle());

        Glide
                .with(mContext)
                .load(model.getImg())
                .placeholder(R.mipmap.pic_spot_default)
                .into(holder.img);

        holder.cardView.setOnClickListener((View view)->{
            Intent intent=new Intent();
            intent.setClass(mContext,VoiceActivity.class);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (mDatas!=null? mDatas.size():0);
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param position
     * @param newModel
     */
    public void setItem(int position, Route newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(Route oldModel, Route newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
