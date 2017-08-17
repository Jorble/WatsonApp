package com.giant.watsonapp.photo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.giant.watsonapp.R;
import com.giant.watsonapp.utils.L;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import razerdp.basepopup.BasePopupWindow;

import static android.R.attr.data;

/**
 * Created by Jorble on 2017/7/19.
 */

public class PhotoViewPop extends BasePopupWindow implements View.OnClickListener {

    private LinearLayout popwindow;
    private PhotoView photo_view;

    public PhotoViewPop(Activity context, String imgPath) {
        super(context);

        //popup是否可以覆盖状态栏（全屏）
        setPopupWindowFullScreen(true);

        popwindow = (LinearLayout) findViewById(R.id.popwindow);
        photo_view = (PhotoView) findViewById(R.id.photo_view);

        //加载图片
        loadImg(context, imgPath);

        //设置点击监听
        setViewClickListener(this, popwindow);
        photo_view.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                dismiss();
            }
        });
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imgPath
     */
    private void loadImg(Context context, String imgPath) {
        //本地图片
//            FileInputStream fis = new FileInputStream(imgPath);
//            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
//            //设置图片
//            photo_view.setImageBitmap(bitmap);

        //网络图片
        Glide.with(context)
                .load(imgPath)
                .fitCenter()
                .placeholder(R.drawable.aurora_picture_not_found)
                .override(400, Target.SIZE_ORIGINAL)
                .into(photo_view);
    }

    @Override
    protected Animation initShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected Animation initExitAnimation() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, 1, 0.5F, 1, 0.5F);
        scaleAnimation.setDuration(300L);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }

    @Override
    public View getClickToDismissView() {
        return getPopupWindowView();
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.pop_photoview);
    }

    //展示动画的这个view必须是popupview的子view）
    @Override
    public View initAnimaView() {
        return findViewById(R.id.photo_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popwindow:
                dismiss();
                break;
            default:
                break;
        }

    }
}
