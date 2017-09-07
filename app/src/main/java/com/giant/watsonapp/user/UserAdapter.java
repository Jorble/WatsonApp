package com.giant.watsonapp.user;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giant.watsonapp.R;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<String> mDatas;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<String> highLightList;
    List<Link> linkList = new ArrayList<>();

    public UserAdapter(List<String> mDatas,List<String> highLightList) {
        this.mDatas = mDatas;
        this.highLightList = highLightList;

        initLinks();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_history, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        String model = mDatas.get(position);

        holder.title_tv.setText(model);

        // 高亮
        if(linkList!=null && linkList.size()>0) {
            LinkBuilder.on(holder.title_tv)
                    .addLinks(linkList)
                    .build();
        }
    }

    /**
     * 关键字list转换位linklist
     * @return
     */
    private void initLinks(){
        // not underlined
        if(highLightList==null || highLightList.size()==0)return;
        for (String s:highLightList){
            Link link = new Link(s);
            link.setUnderlined(false);
            link.setTextColor(Color.parseColor("#137FF0"));
            linkList.add(link);
        }
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
    public void setItem(int position, String newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(String oldModel, String newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
