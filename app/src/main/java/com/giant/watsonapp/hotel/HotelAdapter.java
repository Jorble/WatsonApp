package com.giant.watsonapp.hotel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Hotel;
import com.giant.watsonapp.models.TimeLineModel;
import com.giant.watsonapp.utils.T;
import com.giant.watsonapp.utils.UiUtils;

import java.util.List;

import static com.iflytek.cloud.resource.Resource.setText;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class HotelAdapter extends RecyclerView.Adapter<HotelViewHolder> {

    private List<Hotel> mDatas;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public HotelAdapter(List<Hotel> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public HotelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_hotel, parent, false);

        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotelViewHolder holder, int position) {
        Hotel model = mDatas.get(position);

        holder.price.setText(model.getPrice());
        holder.name.setText(model.getName());
        holder.star.setRating(Float.parseFloat(model.getStar()));
        holder.starTv.setText(String.valueOf(model.getStar()));

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
            intent.setClass(mContext,HotelDetailActivity.class);
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
    public void setItem(int position, Hotel newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(Hotel oldModel, Hotel newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
