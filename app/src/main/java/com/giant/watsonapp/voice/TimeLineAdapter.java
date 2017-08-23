package com.giant.watsonapp.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.Const;
import com.giant.watsonapp.R;
import com.giant.watsonapp.chat.ChatActivity;
import com.giant.watsonapp.models.OrderStatus;
import com.giant.watsonapp.models.Orientation;
import com.giant.watsonapp.models.TimeLineModel;
import com.giant.watsonapp.utils.DateTimeUtils;
import com.giant.watsonapp.utils.L;
import com.giant.watsonapp.utils.T;
import com.giant.watsonapp.utils.UiUtils;
import com.giant.watsonapp.utils.VectorDrawableUtils;
import com.giant.watsonapp.web.WebActivity;
import com.github.vipulasri.timelineview.TimelineView;
import com.iflytek.cloud.thirdparty.M;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.R.id.message;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mDatas;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding) {
        mDatas = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        if(mOrientation == Orientation.HORIZONTAL) {
            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_horizontal_line_padding : R.layout.item_timeline_horizontal, parent, false);
        } else {
            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_line_padding : R.layout.item_timeline, parent, false);
        }

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        TimeLineModel timeLineModel = mDatas.get(position);

        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, R.color.theme_color));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.theme_color));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.theme_color));
        }

        holder.title.setText(timeLineModel.getTitle());
        holder.message.setText(timeLineModel.getMessage());

        if(timeLineModel.isPlaying()){
            holder.play.setImageResource(R.mipmap.icon_voice_stop);
            holder.message.setExpand(true);//展开文字
        }else {
            holder.play.setImageResource(R.mipmap.icon_voice_play);
            holder.message.setExpand(false);//折叠文字
        }
        holder.message.refreshText();

        Glide
                .with(mContext)
                .load(timeLineModel.getImg())
                .placeholder(R.mipmap.pic_spot_default)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(holder.img);

        //点击事件
        holder.img.setOnClickListener((View view)->{
            if(TextUtils.isEmpty(timeLineModel.getUrl())){
                T.showShort(mContext,"没有更多详情了");
                return;
            }
            Intent intent = new Intent(mContext, WebActivity.class);
            intent.putExtra("url", timeLineModel.getUrl());
            mContext.startActivity(intent);
        });

        holder.play.setOnClickListener((View view)->{
            //停止播放中的item
            for(int i=0;i<mDatas.size();i++){
                if(mDatas.get(i).isPlaying() && i != position){
                    mDatas.get(i).setPlaying(false);
                    notifyItemChanged(i);
                }
            }

            //播放当前item
            if(timeLineModel.isPlaying()){
                L.i("点击了停止");
                timeLineModel.setPlaying(false);
                Message message=new Message();
                message.what = Const.BUS_VOICE_STOP;
                message.obj = timeLineModel;
                message.arg1 = position;
                EventBus.getDefault().post(message);
            }else {
                L.i("点击了播放");
                timeLineModel.setStatus(OrderStatus.ACTIVE);
                timeLineModel.setPlaying(true);
                Message message=new Message();
                message.what = Const.BUS_VOICE_PLAY;
                message.obj = timeLineModel;
                message.arg1 = position;
                EventBus.getDefault().post(message);
            }

            //刷新界面
            notifyItemChanged(position);
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
    public void setItem(int position, TimeLineModel newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(TimeLineModel oldModel, TimeLineModel newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
