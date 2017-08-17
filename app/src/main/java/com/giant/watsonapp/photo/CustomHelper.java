package com.giant.watsonapp.photo;

import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.giant.watsonapp.R;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;

import static android.R.attr.width;
import static com.darsh.multipleimageselect.helpers.Constants.limit;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class CustomHelper {

    //是否剪切
    boolean isCrop = false;
    //剪切高度，单位px
    int cropHeight = 800;
    //剪切宽度，单位px
    int cropWidth = 800;
    //是否使用TakePhoto自带剪切工具，或者第三方
    boolean withWonCrop = true;
    //尺寸/比例是否使用：宽/高，或者宽x高
    boolean isAspect = false;

    //是否压缩
    boolean isCompress = true;
    //是否使用自带压缩工具,或者luban
    boolean isUseCompressToolWithOwn = true;
    //是否显示压缩进度条
    boolean isShowProgressBar = true;
    //拍照压缩后是否保存原图
    boolean enableRawFile = false;
    //大小不超过，单位B
    int maxSize = 5120*100;
    //宽度，单位px
    int compressWidth = 800;
    //高度，单位px
    int compressHeight = 800;

    //是否使用TakePhoto自带相册,或者其他(提示：选择多张图片时会自动切换到TakePhoto自带相册)
    boolean isPickToolWithOwn = true;
    //是否从相册选择,或者文件选择
    boolean isFromFile = true;
    //是否纠正拍照的照片旋转角度
    boolean isUseCorrectTool = false;
    //最多选择张数
    int pickLimit = 1;


    public static CustomHelper newInstant() {
        return new CustomHelper();
    }

    private CustomHelper() {
        init();
    }

    private void init() {

    }

    /**
     * 到相册选择照片
     *
     * @param takePhoto
     */
    public void goToPickBySelect(TakePhoto takePhoto) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);

        if (pickLimit > 1) {
            if (isCrop) {
                takePhoto.onPickMultipleWithCrop(pickLimit, getCropOptions());
            } else {
                takePhoto.onPickMultiple(pickLimit);
            }
            return;
        }
        if (isFromFile) {
            if (isCrop) {
                takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions());
            } else {
                takePhoto.onPickFromDocuments();
            }
            return;
        } else {
            if (isCrop) {
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
            } else {
                takePhoto.onPickFromGallery();
            }
        }
    }

    /**
     * 选择拍照
     *
     * @param takePhoto
     */
    public void goToPickByTake(TakePhoto takePhoto) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);

        if (isCrop) {
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        } else {
            takePhoto.onPickFromCapture(imageUri);
        }
    }

    /**
     * 拍照配置
     *
     * @param takePhoto
     */
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        if (isPickToolWithOwn) {
            builder.setWithOwnGallery(true);
        }
        if (isUseCorrectTool) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());

    }

    /**
     * 压缩配置
     *
     * @param takePhoto
     */
    private void configCompress(TakePhoto takePhoto) {
        if (!isCompress) {
            takePhoto.onEnableCompress(null, false);
            return;
        }

        CompressConfig config;
        if (isUseCompressToolWithOwn) {
            config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(compressWidth >= compressHeight ? compressWidth : compressHeight)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder()
                    .setMaxHeight(compressHeight)
                    .setMaxWidth(compressWidth)
                    .setMaxSize(maxSize)
                    .create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, isShowProgressBar);


    }

    /**
     * 剪切配置
     *
     * @return
     */
    private CropOptions getCropOptions() {
        if (!isCrop) return null;

        CropOptions.Builder builder = new CropOptions.Builder();

        if (isAspect) {
            builder.setAspectX(cropWidth).setAspectY(cropHeight);
        } else {
            builder.setOutputX(cropWidth).setOutputY(cropHeight);
        }
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

}
