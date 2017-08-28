package com.giant.watsonapp.introduction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.chat.ChatActivity;
import com.giant.watsonapp.food.RestaurantActivity;
import com.giant.watsonapp.hotel.HotelActivity;
import com.giant.watsonapp.hotel.HotelDetailActivity;
import com.giant.watsonapp.map.MapActivity;
import com.giant.watsonapp.models.Introduction;
import com.giant.watsonapp.utils.UiUtils;
import com.giant.watsonapp.voice.VoiceActivity;

import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class IntroductionAdapter extends RecyclerView.Adapter<IntroductionViewHolder> {

    private List<Introduction> mDatas;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public IntroductionAdapter(List<Introduction> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public IntroductionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_introd, parent, false);

        return new IntroductionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IntroductionViewHolder holder, int position) {
        Introduction model = mDatas.get(position);

        holder.text.setText(model.getText());
        holder.name.setText(model.getSection());
        holder.imgName.setText(model.getImgName());

        String img=model.getImg();

        Glide
                .with(mContext)
                .load(img)
                .placeholder(R.mipmap.pic_spot_default)
                .into(holder.img);

        holder.section_ll.setOnClickListener((View view)->{
            switch (model.getId()){
                case "2":
                    MapActivity.startMyself(mContext,null);
                    break;
                case "3":
                    mContext.startActivity(new Intent(mContext,HotelActivity.class));
                    break;
                case "4":
                    mContext.startActivity(new Intent(mContext,RestaurantActivity.class));
                    break;
                case "5":
                    mContext.startActivity(new Intent(mContext,VoiceActivity.class));
                    break;
                case "6":
                    mContext.startActivity(new Intent(mContext,ChatActivity.class));
                    break;
            }
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
    public void setItem(int position, Introduction newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(Introduction oldModel, Introduction newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
