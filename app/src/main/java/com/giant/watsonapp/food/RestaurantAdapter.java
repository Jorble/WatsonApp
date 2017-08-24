package com.giant.watsonapp.food;

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
import com.giant.watsonapp.models.Restaurant;

import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<Restaurant> mDatas;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public RestaurantAdapter(List<Restaurant> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_restaurant, parent, false);

        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Restaurant model = mDatas.get(position);

        holder.price.setText(model.getPrice());
        holder.name.setText(model.getName());
        holder.star.setRating(Float.parseFloat(model.getStar()));
        holder.starTv.setText(model.getStar());

        String img=model.getImgs();
        if(img.contains(";")) {
            String[] imgarr = model.getImgs().split(";");
            img = imgarr[0];
        }

        Glide
                .with(mContext)
                .load(img)
                .placeholder(R.mipmap.pic_spot_default)
                .into(holder.img);

        holder.img.setOnClickListener((View view)->{
            Intent intent=new Intent();
            intent.setClass(mContext,RestaurantDetailActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("model",model);
            intent.putExtras(mBundle);
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
    public void setItem(int position, Restaurant newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(Restaurant oldModel, Restaurant newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
