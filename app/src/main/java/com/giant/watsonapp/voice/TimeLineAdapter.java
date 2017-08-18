package com.giant.watsonapp.voice;

import android.content.Context;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.Const;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.OrderStatus;
import com.giant.watsonapp.models.Orientation;
import com.giant.watsonapp.models.TimeLineModel;
import com.giant.watsonapp.utils.DateTimeUtils;
import com.giant.watsonapp.utils.L;
import com.giant.watsonapp.utils.T;
import com.giant.watsonapp.utils.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding) {
        mFeedList = feedList;
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

        TimeLineModel timeLineModel = mFeedList.get(position);

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
            holder.play.setImageResource(R.mipmap.icon_voice_pause);
            holder.message.setExpand(true);//展开文字
        }else {
            holder.play.setImageResource(R.mipmap.icon_voice_play);
            holder.message.setExpand(false);//折叠文字
        }
        holder.message.refreshText();

        Glide
                .with(mContext)
                .load(timeLineModel.getImg())
                .placeholder(R.mipmap.refresh_loading01)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(holder.img);

        //点击事件
        holder.play.setOnClickListener((View view)->{
            //停止播放中的item
            for(int i=0;i<mFeedList.size();i++){
                if(mFeedList.get(i).isPlaying() && i != position){
                    mFeedList.get(i).setPlaying(false);
                    notifyItemChanged(i);
                }
            }

            //播放当前item
            if(timeLineModel.isPlaying()){
                L.i("点击了暂停");
                timeLineModel.setPlaying(false);
                Message message=new Message();
                message.what = Const.BUS_VOICE_STOP;
                message.obj = timeLineModel;
                EventBus.getDefault().post(message);
            }else {
                L.i("点击了播放");
                timeLineModel.setPlaying(true);
                Message message=new Message();
                message.what = Const.BUS_VOICE_PLAY;
                message.obj = timeLineModel;
                EventBus.getDefault().post(message);
            }

            //刷新界面
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}
