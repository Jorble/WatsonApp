package com.giant.watsonapp.hotel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Hotel;

import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomViewHolder> {

    private List<Hotel.HotelListBean.RoomListBean> mDatas;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public RoomAdapter(List<Hotel.HotelListBean.RoomListBean> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_room, parent, false);

        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Hotel.HotelListBean.RoomListBean model = mDatas.get(position);

        holder.price.setText(model.getPrice());
        holder.name.setText(model.getName());
        holder.desc.setText(model.getDesc());
        holder.impression.setText(model.getImpression());

        Glide
                .with(mContext)
                .load(model.getImg())
                .placeholder(R.mipmap.pic_spot_default)
                .into(holder.img);

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
    public void setItem(int position, Hotel.HotelListBean.RoomListBean newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(Hotel.HotelListBean.RoomListBean oldModel, Hotel.HotelListBean.RoomListBean newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
