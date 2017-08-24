package com.giant.watsonapp.food;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.Food;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {

    private List<Food> mDatas;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public FoodAdapter(List<Food> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_food, parent, false);

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        Food model = mDatas.get(position);

        holder.name.setText(model.getName());

        Glide
                .with(mContext)
                .load(model.getImg())
                .placeholder(R.mipmap.pic_spot_default)
                .bitmapTransform(new RoundedCornersTransformation(mContext,10,0))
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
    public void setItem(int position, Food newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(Food oldModel, Food newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
