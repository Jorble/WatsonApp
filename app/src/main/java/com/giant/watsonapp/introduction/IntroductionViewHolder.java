package com.giant.watsonapp.introduction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.giant.watsonapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class IntroductionViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.img_iv)
    ImageView img;
    @BindView(R.id.name_tv)
    TextView name;
    @BindView(R.id.text_tv)
    TextView text;
    @BindView(R.id.imgName_tv)
    TextView imgName;
    @BindView(R.id.section_ll)
    LinearLayout section_ll;

    public IntroductionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
