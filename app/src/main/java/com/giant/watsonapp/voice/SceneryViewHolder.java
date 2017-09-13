package com.giant.watsonapp.voice;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giant.watsonapp.R;
import com.giant.watsonapp.views.MoreTextView;
import com.github.vipulasri.timelineview.TimelineView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class SceneryViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.head_cv)
    CardView head;
    @BindView(R.id.img_iv)
    ImageView img;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.day_tv)
    TextView day;
    @BindView(R.id.plan_tv)
    TextView plan;
    @BindView(R.id.message_tv)
    MoreTextView message;
    @BindView(R.id.play_iv)
    ImageView play;
    @BindView(R.id.time_marker)
    TimelineView mTimelineView;
    @BindView(R.id.readMap_tv)
    TextView readMap;
    @BindView(R.id.takePic_tv)
    TextView takePic;

    public SceneryViewHolder(View itemView, int viewType) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        mTimelineView.initLine(viewType);
    }
}
