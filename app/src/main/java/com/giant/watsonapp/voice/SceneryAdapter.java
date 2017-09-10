package com.giant.watsonapp.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.giant.watsonapp.Const;
import com.giant.watsonapp.R;
import com.giant.watsonapp.map.MapActivity;
import com.giant.watsonapp.models.MyMarker;
import com.giant.watsonapp.models.OrderStatus;
import com.giant.watsonapp.models.Orientation;
import com.giant.watsonapp.models.Scenery;
import com.giant.watsonapp.utils.L;
import com.giant.watsonapp.utils.T;
import com.giant.watsonapp.utils.VectorDrawableUtils;
import com.giant.watsonapp.web.WebActivity;
import com.github.vipulasri.timelineview.TimelineView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.giant.watsonapp.map.MapActivity.TYPE_SCENERY;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class SceneryAdapter extends RecyclerView.Adapter<SceneryViewHolder> {

    private List<Scenery> mDatas;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public SceneryAdapter(List<Scenery> feedList, Orientation orientation, boolean withLinePadding) {
        mDatas = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public SceneryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_scenery, parent, false);

        return new SceneryViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(SceneryViewHolder holder, int position) {

        Scenery timeLineModel = mDatas.get(position);

        if (TextUtils.isEmpty(timeLineModel.getDay()) && TextUtils.isEmpty(timeLineModel.getPlan())) {
            holder.head.setVisibility(View.GONE);
        }

        if (timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, R.color.theme_color));
        } else if (timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.theme_color));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.theme_color));
        }

        if (TextUtils.isEmpty(timeLineModel.getDay())) {
            holder.day.setVisibility(View.GONE);
        } else {
            holder.day.setText(timeLineModel.getDay());
        }

        String plan = timeLineModel.getPlan();

        if (TextUtils.isEmpty(plan)) {
            holder.plan.setVisibility(View.GONE);
        } else {
            Spanned spanned = Html.fromHtml(plan, null, null);
            holder.plan.setText(spanned);
        }

        holder.title.setText(timeLineModel.getTitle());
        holder.message.setText(timeLineModel.getMessage());

        if (timeLineModel.isPlaying()) {
            holder.play.setImageResource(R.mipmap.icon_voice_stop);
            holder.message.setExpand(true);//展开文字
        } else {
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
        holder.img.setOnClickListener((View view) -> {
            if (TextUtils.isEmpty(timeLineModel.getUrl())) {
                T.showShort(mContext, "没有更多详情了");
                return;
            }
            Intent intent = new Intent(mContext, WebActivity.class);
            intent.putExtra("url", timeLineModel.getUrl());
            mContext.startActivity(intent);
        });

        holder.readMap.setOnClickListener((View view) -> {
            if (TextUtils.isEmpty(timeLineModel.getLat()) || TextUtils.isEmpty(timeLineModel.getLon())) {
                T.showShort(mContext, "暂无地图信息");
                return;
            }
            MyMarker myMarker = new MyMarker();
            myMarker.setType(TYPE_SCENERY);
            myMarker.setImg(timeLineModel.getImg());
            myMarker.setName(timeLineModel.getTitle());
            myMarker.setLat(timeLineModel.getLat());
            myMarker.setLon(timeLineModel.getLon());
            MapActivity.startMyself(mContext, myMarker);
        });

        holder.play.setOnClickListener((View view) -> {
            //停止播放中的item
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i).isPlaying() && i != position) {
                    mDatas.get(i).setPlaying(false);
                    notifyItemChanged(i);
                }
            }

            //播放当前item
            if (timeLineModel.isPlaying()) {
                L.i("点击了停止");
                timeLineModel.setPlaying(false);
                Message message = new Message();
                message.what = Const.BUS_VOICE_STOP;
                message.obj = timeLineModel;
                message.arg1 = position;
                EventBus.getDefault().post(message);
            } else {
                L.i("点击了播放");
                timeLineModel.setStatus(OrderStatus.ACTIVE);
                timeLineModel.setPlaying(true);
                Message message = new Message();
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
        return (mDatas != null ? mDatas.size() : 0);
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param position
     * @param newModel
     */
    public void setItem(int position, Scenery newModel) {
        mDatas.set(position, newModel);
        notifyItemChanged(position);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(Scenery oldModel, Scenery newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }
}
